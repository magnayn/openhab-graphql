package org.openhab.io.graphql.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.thing.ThingRegistry;
import org.openhab.core.thing.ThingUID;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.wrapper.ItemWrapper;
import org.openhab.io.graphql.mapping.wrapper.ThingWrapper;
import org.openhab.io.graphql.model.GraphqlTagEntry;
import org.openhab.io.graphql.model.GraphqlThingInterface;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component(service = TagsDataFetcher.class)
public class TagsDataFetcher implements DataFetcher<List<GraphqlTagEntry>> {

    protected ItemRegistry itemRegistry;

    protected Mapper mapper;

    @Activate
    public TagsDataFetcher(@Reference ItemRegistry itemRegistry, @Reference Mapper mapper) {
        this.itemRegistry = itemRegistry;
        this.mapper = mapper;
    }

    @Override
    public List<GraphqlTagEntry> get(DataFetchingEnvironment environment) throws Exception {
        var root = environment.getRoot();

        var arguments = environment.getArguments();

        var items = itemRegistry.getItems();
        Map<String, List<Item>> itemsByTag = new HashMap<>();

        items.forEach(it -> {
            it.getTags().forEach(it2 -> {
                if (!itemsByTag.containsKey(it2)) {
                    itemsByTag.put(it2, new ArrayList<>());
                }

                itemsByTag.get(it2).add(it);
            });
        });

        var m = mapper.newMappingSession(environment);

        return itemsByTag.entrySet().stream().map(it ->
                {
                    return new GraphqlTagEntry(it.getKey(), it.getValue().stream().map(it2 -> {
                        return ItemWrapper.create(m, it2);
                    }).toList());
                }
        ).collect(Collectors.toList());
    }
}
