package org.openhab.io.graphql;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.openhab.io.graphql.datafetcher.ItemDataFetcher;
import org.openhab.io.graphql.datafetcher.ItemsDataFetcher;
import org.openhab.io.graphql.datafetcher.ThingDataFetcher;
import org.openhab.io.graphql.datafetcher.ThingsDataFetcher;
import org.openhab.io.graphql.datafetcher.mutation.ItemCommandMutationResolver;
import org.openhab.io.graphql.datafetcher.mutation.ItemMutationResolver;
import org.openhab.io.graphql.datafetcher.subscription.SubscriptionDataFetcher;
import org.openhab.io.graphql.datafetcher.subscription.SubscriptionGroupDataFetcher;
import org.openhab.io.graphql.datafetcher.subscription.SubscriptionItemDataFetcher;
import org.openhab.io.graphql.datafetcher.subscription.SubscriptionItemsDataFetcher;

public class TestGQLSystem {

    @Test
    void testGQLBuilds() throws IOException {
        var idf = new ItemsDataFetcher(null, null);
        var tdf = new ThingsDataFetcher(null, null);
        var sdf = new SubscriptionDataFetcher(null);

        ItemDataFetcher itemDataFetcher = new ItemDataFetcher(null, null);
        ThingDataFetcher thingDataFetcher = new ThingDataFetcher(null, null);

        SubscriptionItemDataFetcher subscriptionItemDataFetcher = new SubscriptionItemDataFetcher(null);
        SubscriptionItemsDataFetcher subscriptionItemsDataFetcher = new SubscriptionItemsDataFetcher(null);
        ItemMutationResolver itemMutationResolver = new ItemMutationResolver();

        ItemCommandMutationResolver itemCommandResolver = new ItemCommandMutationResolver(null, null);
        SubscriptionGroupDataFetcher subscriptionGroupDataFetcher = new SubscriptionGroupDataFetcher(null, null);
        GQLSystem system = new GQLSystem(idf, tdf, itemDataFetcher, thingDataFetcher, null, sdf,
                subscriptionGroupDataFetcher, subscriptionItemDataFetcher, subscriptionItemsDataFetcher, itemMutationResolver, itemCommandResolver);
    }
}
