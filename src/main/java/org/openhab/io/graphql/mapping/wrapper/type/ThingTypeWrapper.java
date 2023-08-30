package org.openhab.io.graphql.mapping.wrapper.type;

import java.util.List;

import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.BridgeType;
import org.openhab.core.thing.type.ThingType;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.ChannelDefinitionWrapper;
import org.openhab.io.graphql.mapping.wrapper.ChannelGroupDefinitionWrapper;
import org.openhab.io.graphql.mapping.wrapper.config.ConfigDescriptionWrapper;
import org.openhab.io.graphql.model.GraphqlChannelDefinition;
import org.openhab.io.graphql.model.GraphqlChannelGroupDefinition;
import org.openhab.io.graphql.model.GraphqlConfigDescription;
import org.openhab.io.graphql.model.GraphqlThingType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ThingTypeWrapper implements GraphqlThingType {
    private final MappingSession session;
    private final ThingType thingType;

    public static GraphqlThingType create(MappingSession session, ThingType item) {
        return new ThingTypeWrapper(session, item);
    }

    private ThingTypeWrapper(MappingSession session, ThingType thingType) {
        this.session = session;
        this.thingType = thingType;
    }

    @Override
    public String getUid() {
        return thingType.getUID().toString();
    }

    @Override
    public String getLabel() {
        return thingType.getLabel();
    }

    @Override
    public String getDescription() {
        return thingType.getDescription();
    }

    @Override
    public String getCategory() {
        return thingType.getCategory();
    }

    @Override
    public boolean getListed() {
        return thingType.isListed();
    }

    @Override
    public List<GraphqlThingType> getSupportedBridgeTypes() {
        return thingType.getSupportedBridgeTypeUIDs().stream()
                .map(it -> session.getMapper().getThingTypeMapper().getThingType(new ThingTypeUID(it)))
                .map(it -> ThingTypeWrapper.create(session, it)).toList();
    }

    @Override
    public Boolean getBridge() {
        return thingType instanceof BridgeType;
    }

    @Override
    public List<GraphqlChannelDefinition> getChannels() {
        return thingType.getChannelDefinitions().stream().map(it -> ChannelDefinitionWrapper.create(session, it))
                .toList();
    }

    @Override
    public List<GraphqlChannelGroupDefinition> getChannelGroups() {
        return thingType.getChannelGroupDefinitions().stream()
                .map(it -> ChannelGroupDefinitionWrapper.create(session, it)).toList();
    }

    @Override
    public GraphqlConfigDescription getConfigDescription() {

        var descURI = thingType.getConfigDescriptionURI();

        var configDescription = session.getMapper().getConfigDefinitionMapper().getConfigDescription(descURI,
                session.getLocale());

        return ConfigDescriptionWrapper.create(session, configDescription);
    }

    @Override
    public String getProperties() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(thingType.getProperties());
    }

    @Override
    public List<String> getExtensibleChannelTypeIds() {
        return thingType.getExtensibleChannelTypeIds();
    }
}
