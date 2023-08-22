package org.openhab.io.graphql.mapping.wrapper;

import java.util.List;
import java.util.stream.Collectors;

import org.openhab.core.thing.Channel;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.model.GraphqlChannel;
import org.openhab.io.graphql.model.GraphqlChannelKind;
import org.openhab.io.graphql.model.GraphqlChannelType;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlThingInterface;

public class ChannelWrapper extends Wrapper<Channel> implements GraphqlChannel, IClassName {

    public ChannelWrapper(MappingSession session, Channel item) {
        super(session, item);
    }

    public String get__typename() {
        return "Channel";
    }

    @Override
    public String getUid() {
        return item.getUID().toString();
    }

    @Override
    public String getId() {
        // TODO
        return item.getDescription();
    }

    @Override
    public GraphqlChannelType getChannelType() {
        return null;
    }

    @Override
    public String getItemType() {
        return null;
    }

    @Override
    public GraphqlChannelKind getKind() {
        return null;
    }

    @Override
    public String getLabel() {
        return item.getLabel();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public GraphqlThingInterface getThing() {
        return new ThingWrapper(session, session.getMapper().getThingOwningChannel(item.getUID()));
    }

    @Override
    public String getProperties() {
        return null;
    }

    @Override
    public String getConfiguration() {
        return item.getConfiguration().toString();
    }

    @Override
    public String getAutoUpdatePolicy() {
        return item.getAutoUpdatePolicy().toString();
    }

    @Override
    public List<GraphqlItem> getLinkedItems() {
        var items = session.getMapper().getChannelLinkedItems(item.getUID());
        return items.stream().map(it -> ItemWrapper.create(session, it)).collect(Collectors.toList());
    }
}
