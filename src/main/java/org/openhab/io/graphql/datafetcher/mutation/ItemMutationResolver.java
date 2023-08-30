package org.openhab.io.graphql.datafetcher.mutation;

import org.openhab.io.graphql.model.GraphqlItemMutations;
import org.osgi.service.component.annotations.Component;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ItemMutationResolver.class)
public class ItemMutationResolver implements DataFetcher<GraphqlItemMutations> {
    @Override
    public GraphqlItemMutations get(DataFetchingEnvironment environment) throws Exception {
        return GraphqlItemMutations.builder().build();
    }
}
