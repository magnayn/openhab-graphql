package org.openhab.io.graphql.mapping.wrapper;

import java.util.List;

import org.openhab.core.thing.type.ChannelDefinition;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.model.GraphqlChannelDefinition;
import org.openhab.io.graphql.model.GraphqlChannelType;
import org.openhab.io.graphql.model.GraphqlStateDescription;

public class ChannelDefinitionWrapper extends Wrapper<ChannelDefinition>
        implements GraphqlChannelDefinition, IClassName {

    public ChannelDefinitionWrapper(MappingSession session, ChannelDefinition item) {
        super(session, item);
    }

    public static GraphqlChannelDefinition create(MappingSession session, ChannelDefinition item) {
        return new ChannelDefinitionWrapper(session, item);
    }

    @Override
    public String getUid() {
        return item.getChannelTypeUID().toString();
    }

    @Override
    public String getId() {
        return item.getId();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public String getLabel() {
        return item.getLabel();
    }

    @Override
    public List<String> getTags() {

        return List.of();
    }

    @Override
    public String getProperties() {
        return json(item.getProperties());
    }

    @Override
    public String getCategory() {
        return "";
    }

    @Override
    public GraphqlStateDescription getStateDescription() {
        return null;
    }

    @Override
    public boolean getAdvanced() {
        return false;
    }

    @Override
    public GraphqlChannelType getChannelType() {
        return null;
    }
}
