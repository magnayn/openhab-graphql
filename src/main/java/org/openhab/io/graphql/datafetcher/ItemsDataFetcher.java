package org.openhab.io.graphql.datafetcher;

import java.util.List;
import java.util.stream.Collectors;

import org.openhab.core.items.ItemRegistry;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.wrapper.ItemWrapper;
import org.openhab.io.graphql.model.GraphqlItem;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ItemsDataFetcher.class)
// @QueryMapping(fieldName = "items")
public class ItemsDataFetcher implements DataFetcher<List<GraphqlItem>> {

    private final Mapper mapper;
    protected ItemRegistry itemRegistry;

    @Activate
    public ItemsDataFetcher(@Reference ItemRegistry itemRegistry, @Reference Mapper mapper) {
        this.itemRegistry = itemRegistry;
        this.mapper = mapper;
    }

    @Override
    public List<GraphqlItem> get(DataFetchingEnvironment environment) throws Exception {
        var items = itemRegistry.getItems();

        var mappingSession = mapper.newMappingSession(environment);

        return items.stream().map(it -> {
            return ItemWrapper.create(mappingSession, it);
        }).collect(Collectors.toList());
    }
}
