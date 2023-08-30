package org.openhab.io.graphql.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.automation.handler.ModuleHandlerFactory;
import org.openhab.core.automation.type.ActionType;
import org.openhab.core.automation.type.ModuleTypeRegistry;
import org.openhab.core.thing.ManagedThingProvider;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.io.graphql.model.GraphqlThingAction;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = ThingMapper.class)
public class ThingMapper {
    private final ManagedThingProvider managedThingProvider;
    private final ModuleTypeRegistry moduleTypeRegistry;
    Map<ThingUID, Map<String, ThingActions>> thingActionsMap = new ConcurrentHashMap<>();
    private List<ModuleHandlerFactory> moduleHandlerFactories = new ArrayList<>();

    @Activate
    public ThingMapper(@Reference ManagedThingProvider managedThingProvider,
            @Reference ModuleTypeRegistry moduleTypeRegistry) {
        this.managedThingProvider = managedThingProvider;
        this.moduleTypeRegistry = moduleTypeRegistry;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected void addModuleHandlerFactory(ModuleHandlerFactory moduleHandlerFactory) {
        moduleHandlerFactories.add(moduleHandlerFactory);
    }

    protected void removeModuleHandlerFactory(ModuleHandlerFactory moduleHandlerFactory) {
        moduleHandlerFactories.remove(moduleHandlerFactory);
    }

    public void removeThingActions(ThingActions thingActions) {
        ThingHandler handler = thingActions.getThingHandler();
        String scope = getScope(thingActions);
        if (handler != null && scope != null) {
            ThingUID thingUID = handler.getThing().getUID();
            Map<String, ThingActions> actionMap = thingActionsMap.get(thingUID);
            if (actionMap != null) {
                actionMap.remove(scope);
                if (actionMap.isEmpty()) {
                    thingActionsMap.remove(thingUID);
                }
            }
        }
    }

    public boolean isManagedThing(ThingUID id) {
        return managedThingProvider.get(id) != null;
    }

    public List<ThingActions> getThingActionsFor(Thing thing) {
        var entries = thingActionsMap.get(thing.getUID());
        if (entries == null)
            return List.of();

        return entries.entrySet().stream().map(entry -> entry.getValue()).toList();
    }

    public List<GraphqlThingAction> mapThingAction(ThingActions thingActions, Locale locale) {

        var actions = new ArrayList<GraphqlThingAction>();

        Method[] methods = thingActions.getClass().getDeclaredMethods();
        for (Method method : methods) {
            RuleAction ruleAction = method.getAnnotation(RuleAction.class);

            if (ruleAction == null) {
                continue;
            }

            String actionUid = getScope(thingActions) + "." + method.getName();
            ActionType actionType = (ActionType) moduleTypeRegistry.get(actionUid, locale);
            if (actionType == null) {
                continue;
            }

            GraphqlThingAction actionDTO = new GraphqlThingAction();
            actionDTO.setUid(actionType.getUID());
            actionDTO.setDescription(actionType.getDescription());
            actionDTO.setLabel(actionType.getLabel());
            // actionDTO.setInputs(); = actionType.getInputs();
            // actionDTO.setOutputs(); = actionType.getOutputs();
            actions.add(actionDTO);
        }
        return actions;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addThingActions(ThingActions thingActions) {
        ThingHandler handler = thingActions.getThingHandler();
        String scope = getScope(thingActions);
        if (handler != null && scope != null) {
            ThingUID thingUID = handler.getThing().getUID();
            thingActionsMap.computeIfAbsent(thingUID, thingUid -> new ConcurrentHashMap<>()).put(scope, thingActions);
        }
    }

    private @Nullable String getScope(ThingActions actions) {
        ThingActionsScope scopeAnnotation = actions.getClass().getAnnotation(ThingActionsScope.class);
        if (scopeAnnotation == null) {
            return null;
        }
        return scopeAnnotation.name();
    }
}
