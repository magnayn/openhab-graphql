package org.openhab.io.graphql.datafetcher;

import org.openhab.core.items.GroupItem;
import org.openhab.core.items.ItemRegistry;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.wrapper.ItemWrapper;
import org.openhab.io.graphql.model.GraphqlItem;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ItemDataFetcher.class)
public class ItemDataFetcher implements DataFetcher<GraphqlItem> {

    private final Mapper mapper;
    protected ItemRegistry itemRegistry;

    @Activate
    public ItemDataFetcher(@Reference ItemRegistry itemRegistry, @Reference Mapper mapper) {
        this.itemRegistry = itemRegistry;
        this.mapper = mapper;
    }

    @Override
    public GraphqlItem get(DataFetchingEnvironment environment) throws Exception {
        var root = environment.getField();

        var arguments = environment.getArguments();

        var item = itemRegistry.getItem(arguments.get("id").toString());

        if( root.getName().equals("group") && !(item instanceof GroupItem))
            throw new IllegalArgumentException("Not a group");

        var mappingSession = mapper.newMappingSession(environment);

        return ItemWrapper.create(mappingSession, item);
    }
}
