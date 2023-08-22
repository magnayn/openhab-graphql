package org.openhab.io.graphql.mapping.wrapper;

import java.util.List;
import java.util.stream.Collectors;

import org.openhab.core.items.Item;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.model.GraphqlChannel;
import org.openhab.io.graphql.model.GraphqlCommandDescription;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlItemMetadata;
import org.openhab.io.graphql.model.GraphqlState;

public class ItemWrapper implements GraphqlItem, IClassName {
    protected final Item item;
    protected final MappingSession session;

    public static GraphqlItem create(MappingSession session, Item item) {
        return new ItemWrapper(session, item);
    }

    private ItemWrapper(MappingSession session, Item item) {
        this.session = session;
        this.item = item;
    }

    public String get__typename() {
        return "GenericItem";
    }

    @Override
    public String getUid() {
        return item.getUID();
    }

    @Override
    public String getName() {
        return item.getName();
    }

    @Override
    public String getType() {
        return item.getType();
    }

    @Override
    public String getLabel() {
        return item.getLabel();
    }

    @Override
    public String getCategory() {
        return item.getCategory();
    }

    @Override
    public List<String> getTags() {
        return item.getTags().stream().toList();
    }

    @Override
    public List<String> getGroupNames() {
        return item.getGroupNames();
    }

    @Override
    public GraphqlCommandDescription getCommandDescription() {
        return null;
    }

    @Override
    public GraphqlItemMetadata getMetadata() {
        return null;
    }

    @Override
    public Boolean getEditable() {
        return null; // TODO
    }

    @Override
    public String getNamespaces() {
        return null; /// TOOD
    }

    @Override
    public GraphqlState getState() {
        return session.map(item.getState());
    }

    @Override
    public List<GraphqlChannel> getChannels() {
        var channels = session.getMapper().getChannelsForItem(item.getUID());

        return channels.stream().map(it -> new ChannelWrapper(session, it)).collect(Collectors.toList());
    }
}
