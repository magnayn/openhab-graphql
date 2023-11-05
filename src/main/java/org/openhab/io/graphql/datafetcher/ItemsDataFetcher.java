package org.openhab.io.graphql.datafetcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.wrapper.ItemWrapper;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlItemCollection;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ItemsDataFetcher.class)
public class ItemsDataFetcher implements DataFetcher<CollectionWrapper<GraphqlItem>> {

    private final Mapper mapper;
    protected ItemRegistry itemRegistry;

    @Activate
    public ItemsDataFetcher(@Reference ItemRegistry itemRegistry, @Reference Mapper mapper) {
        this.itemRegistry = itemRegistry;
        this.mapper = mapper;
    }

    @Override
    public CollectionWrapper<GraphqlItem> get(DataFetchingEnvironment environment) throws Exception {

        var root = environment.getField();
        // This can be called as 'groups' or 'items'
        var isGroup = root.getName().equals("groups");

        var arguments = environment.getArguments();


        var filter = (Map) arguments.get("itemFilter");
        if (filter == null)
            filter = new HashMap<>();

        Collection<Item> items = getItems(filter);

        var mappingSession = mapper.newMappingSession(environment);

        return mappingSession.buildCollection(items.stream()
                .filter(it -> !isGroup || it instanceof GroupItem)
                .map(it -> ItemWrapper.create(mappingSession, it) ));
    }

    private Collection<Item> getItems(Map<String, Object> arguments) {

        var items = itemRegistry.getItems();

        return items.stream().filter(it -> filter(it, arguments)).toList();
    }

    public static boolean filter(Item item, Map arguments) {
        if (arguments.containsKey("type") && !item.getType().equals(arguments.get("type").toString()))
            return false;

        if (arguments.containsKey("tags")) {
            var tags = (Collection<String>) arguments.get("tags");
            if (!itemHasTags(item, tags))
                return false;
        }

        if (arguments.containsKey("ids")) {
            var ids = (Collection<String>) arguments.get("ids");
            return ids.contains(item.getName());
        }

        return true;
    }

    private static boolean itemHasTags(Item item, Collection<String> tags) {
        for (String tag : tags) {
            if (!item.hasTag(tag)) {
                return false;
            }
        }
        return true;
    }
}
