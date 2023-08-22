package org.openhab.io.graphql;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.openhab.io.graphql.datafetcher.ItemsDataFetcher;
import org.openhab.io.graphql.datafetcher.SubscriptionDataFetcher;
import org.openhab.io.graphql.datafetcher.ThingsDataFetcher;

public class TestGQLSystem {

    @Test
    void testGQLBuilds() throws IOException {
        var idf = new ItemsDataFetcher(null, null);
        var tdf = new ThingsDataFetcher(null, null);
        var sdf = new SubscriptionDataFetcher(null);

        GQLSystem system = new GQLSystem(idf, tdf, null, sdf);
    }
}
