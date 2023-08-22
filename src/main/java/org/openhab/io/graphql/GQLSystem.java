package org.openhab.io.graphql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.openhab.io.graphql.datafetcher.ItemsDataFetcher;
import org.openhab.io.graphql.datafetcher.QueriesDataFetcher;
import org.openhab.io.graphql.datafetcher.SubscriptionDataFetcher;
import org.openhab.io.graphql.datafetcher.ThingsDataFetcher;
import org.openhab.io.graphql.mapping.DynamicNameTypeResolver;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.openhab.io.graphql.scalar.Base64Scalar;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

@Component(immediate = true, service = GQLSystem.class)
public class GQLSystem {
    private final Logger logger = LoggerFactory.getLogger(GQLSystem.class);
    protected final ItemsDataFetcher idf;
    private final ThingsDataFetcher thingsDataFetcher;

    private final EventPublisher eventPublisher;

    private final SubscriptionDataFetcher subscriptionDataFetcher;

    private GraphQL graphql;

    @Activate
    public GQLSystem(@Reference ItemsDataFetcher idf, @Reference ThingsDataFetcher thingsDataFetcher, @Reference EventPublisher eventPublisher, @Reference SubscriptionDataFetcher subscriptionDataFetcher)
            throws IOException {
        this.idf = idf;
        this.thingsDataFetcher = thingsDataFetcher;
        this.eventPublisher = eventPublisher;
        this.subscriptionDataFetcher = subscriptionDataFetcher;
        logger.info("GQLServer()");
        init();
    }

    public void init() throws IOException {
        var reg = loadAndParseSchema();
        var runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();

        var wiringBuilder = TypeRuntimeWiring.newTypeWiring("Query");
        wiringBuilder.dataFetcher("items", idf);
        wiringBuilder.dataFetcher("things", thingsDataFetcher);
        wiringBuilder.dataFetcher("hello", new QueriesDataFetcher());

        runtimeWiringBuilder.type(wiringBuilder.build());
        runtimeWiringBuilder.type(DynamicNameTypeResolver.THING_INTERFACE);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.ITEM);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.STATE);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.ITEM_METADATA);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.EVENT_WIRING);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.ITEM_EVENT_WIRING);

        runtimeWiringBuilder.scalar(ExtendedScalars.DateTime);
        runtimeWiringBuilder.scalar(ExtendedScalars.Json);
        runtimeWiringBuilder.scalar(Base64Scalar.INSTANCE);

        var subscriptionWiring  =TypeRuntimeWiring.newTypeWiring("Subscription");
    //    subscriptionWiring.dataFetcher("stockQuotes", subscriptionDataFetcher());
        subscriptionWiring.dataFetcher("events", subscriptionDataFetcher);
        runtimeWiringBuilder.type(subscriptionWiring.build());

        var runtimeWiring = runtimeWiringBuilder.build();

        var schema = new SchemaGenerator().makeExecutableSchema(reg, runtimeWiring);

        graphql = GraphQL.newGraphQL(schema).build();
    }

    //private final EventPublisher STOCK_TICKER_PUBLISHER = new EventPublisher();

    /*private DataFetcher subscriptionDataFetcher() {
        return environment -> {
            List<String> arg = environment.getArgument("stockCodes");
            List<String> stockCodesFilter = arg == null ? Collections.emptyList() : arg;
            if (stockCodesFilter.isEmpty()) {
                return eventPublisher.getPublisher();
            } else {
                return eventPublisher.getPublisher(stockCodesFilter);
            }
        };
    }*/

    public GraphQL getGraphQL() {
        return this.graphql;
    }

    public TypeDefinitionRegistry loadAndParseSchema() {
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        Stream.of("/graphql/actions.graphqls", "/graphql/addons.graphqls", "/graphql/config.graphqls","/graphql/events.graphqls",
                "/graphql/items.graphqls", "/graphql/query.graphqls", "/graphql/scalars.graphqls",
                "/graphql/subscription.graphqls", "/graphql/things.graphqls", "/graphql/type.graphqls")
                .forEach(path -> typeRegistry.merge(loadAndParseQueriesSchema(path)));
        return typeRegistry;
    }

    private TypeDefinitionRegistry loadAndParseQueriesSchema(String path) {
        InputStream schema = getClass().getResourceAsStream(path);

        SchemaParser schemaParser = new SchemaParser();

        return schemaParser.parse(new BufferedReader(new InputStreamReader(schema)));
    }

    /*
     * private TypeDefinitionRegistry () throws IOException {
     * SchemaParser schemaParser = new SchemaParser();
     * return schemaParser
     * .parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/graphql/schema.graphqls"))));
     * }
     */
}
