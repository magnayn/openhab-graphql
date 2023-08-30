package org.openhab.io.graphql.datafetcher.mutation;

import java.util.List;
import java.util.Map;

import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Command;
import org.openhab.core.types.TypeParser;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.model.GraphqlExecuteCommandResult;
import org.openhab.io.graphql.model.GraphqlExecuteCommandResultFailure;
import org.openhab.io.graphql.model.GraphqlExecuteCommandResultSuccess;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ItemCommandMutationResolver.class)
public class ItemCommandMutationResolver implements DataFetcher<GraphqlExecuteCommandResult> {

    private final Mapper mapper;

    private final EventPublisher eventPublisher;

    @Activate
    public ItemCommandMutationResolver(@Reference Mapper mapper, @Reference EventPublisher eventPublisher) {
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public GraphqlExecuteCommandResult get(DataFetchingEnvironment environment) throws Exception {

        Map<String, String> input = environment.getArgument("input");

        var item = mapper.getItemMapper().getItemByName(input.get("id"));
        String value = input.get("command");

        Command command = null;

        if (item == null || value == null) {
            return new GraphqlExecuteCommandResultFailure();
        } else {
            if ("toggle".equalsIgnoreCase(value) && (item instanceof SwitchItem || item instanceof RollershutterItem)) {
                if (OnOffType.ON.equals(item.getStateAs(OnOffType.class))) {
                    command = OnOffType.OFF;
                }

                if (OnOffType.OFF.equals(item.getStateAs(OnOffType.class))) {
                    command = OnOffType.ON;
                }

                if (UpDownType.UP.equals(item.getStateAs(UpDownType.class))) {
                    command = UpDownType.DOWN;
                }

                if (UpDownType.DOWN.equals(item.getStateAs(UpDownType.class))) {
                    command = UpDownType.UP;
                }
            } else {
                var types = item.getAcceptedCommandTypes();
                command = TypeParser.parseCommand((types == null) ? List.of() : types, value);
            }

            if (command != null) {
                this.eventPublisher.post(ItemEventFactory.createCommandEvent(item.getName(), command));
                return new GraphqlExecuteCommandResultSuccess();
            } else {
                return new GraphqlExecuteCommandResultFailure();
            }
        }
    }
}
