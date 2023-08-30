package org.openhab.io.graphql.mapping.wrapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.model.GraphqlChannel;
import org.openhab.io.graphql.model.GraphqlCommandDescription;
import org.openhab.io.graphql.model.GraphqlGenericItem;
import org.openhab.io.graphql.model.GraphqlGroupItem;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlItemMetadata;
import org.openhab.io.graphql.model.GraphqlState;

public class ItemWrapper implements GraphqlItem, GraphqlGenericItem, GraphqlGroupItem, IClassName {
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

        if (item instanceof GroupItem)
            return "GroupItem";

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
        return session.map(item, item.getState());
    }

    @Override
    public List<GraphqlChannel> getChannels() {
        var channels = session.getMapper().getChannelsForItem(item.getUID());

        return channels.stream()
                .filter(Objects::nonNull)
                .map(it -> new ChannelWrapper(session, it))
                .collect(Collectors.toList());
    }

    @Override
    public GraphqlItem getBaseItem() {
        var baseItem = ((GroupItem) item).getBaseItem();
        return session.map(baseItem);
    }

    @Override
    public List<GraphqlItem> getMembers() {
        var members = ((GroupItem) item).getMembers();
        return members.stream().map(
                it -> session.map(it)
        ).toList();
    }

    @Override
    public List<GraphqlGroupItem> getGroups() {
        return item.getGroupNames().stream().map(
                        it -> {
                            try {
                                return session.getMapper().getItemMapper().getItemByName(it);
                            } catch (ItemNotFoundException e) {
                                return null;
                            }
                        }
                ).filter(Objects::nonNull)
                .map(it -> (GraphqlGroupItem) session.map(it)).toList();
    }
}
