package org.openhab.io.graphql.mapping.wrapper;

import java.util.List;

import org.openhab.core.thing.type.ChannelGroupDefinition;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.model.GraphqlChannelDefinition;
import org.openhab.io.graphql.model.GraphqlChannelGroupDefinition;

public class ChannelGroupDefinitionWrapper extends Wrapper<ChannelGroupDefinition>
        implements GraphqlChannelGroupDefinition, IClassName {

    public ChannelGroupDefinitionWrapper(MappingSession session, ChannelGroupDefinition item) {
        super(session, item);
    }

    public static GraphqlChannelGroupDefinition create(MappingSession session, ChannelGroupDefinition item) {
        return new ChannelGroupDefinitionWrapper(session, item);
    }

    @Override
    public String getUid() {
        return null;
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
    public List<GraphqlChannelDefinition> getChannels() {
        return List.of();
    }
}
