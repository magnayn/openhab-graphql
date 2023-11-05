package org.openhab.io.graphql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

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
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

@Component(immediate = true, service = GQLSystem.class)
public class GQLSystem {
    private final Logger logger = LoggerFactory.getLogger(GQLSystem.class);
    protected final ItemsDataFetcher itemsDataFetcher;
    private final ThingsDataFetcher thingsDataFetcher;

    protected final ItemDataFetcher itemDataFetcher;
    private final ThingDataFetcher thingDataFetcher;

    private final SubscriptionDataFetcher subscriptionDataFetcher;

    private final SubscriptionGroupDataFetcher subscriptionGroupDataFetcher;
    private GraphQL graphql;
    private final SubscriptionItemDataFetcher subscriptionItemDataFetcher;
    private final SubscriptionItemsDataFetcher subscriptionItemsDataFetcher;
    private final ItemMutationResolver itemMutationResolver;
    private final ItemCommandMutationResolver itemCommandResolver;

    @Activate
    public GQLSystem(@Reference ItemsDataFetcher idf, @Reference ThingsDataFetcher thingsDataFetcher,
                     @Reference ItemDataFetcher itemDataFetcher, @Reference ThingDataFetcher thingDataFetcher,
                     @Reference EventPublisher eventPublisher, @Reference SubscriptionDataFetcher subscriptionDataFetcher,
                     @Reference SubscriptionGroupDataFetcher subscriptionGroupDataFetcher, @Reference SubscriptionItemDataFetcher subscriptionItemDataFetcher,
                     @Reference SubscriptionItemsDataFetcher subscriptionItemsDataFetcher,
                     @Reference ItemMutationResolver itemMutationResolver,
                     @Reference ItemCommandMutationResolver itemCommandResolver) throws IOException {
        this.itemsDataFetcher = idf;
        this.thingsDataFetcher = thingsDataFetcher;
        this.itemDataFetcher = itemDataFetcher;
        this.thingDataFetcher = thingDataFetcher;
        this.subscriptionDataFetcher = subscriptionDataFetcher;
        this.subscriptionGroupDataFetcher = subscriptionGroupDataFetcher;
        this.subscriptionItemDataFetcher = subscriptionItemDataFetcher;
        this.subscriptionItemsDataFetcher = subscriptionItemsDataFetcher;
        this.itemMutationResolver = itemMutationResolver;
        this.itemCommandResolver = itemCommandResolver;
        init();
    }

    public void init() throws IOException {
        var reg = loadAndParseSchema();
        var runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();

        var wiringBuilder = TypeRuntimeWiring.newTypeWiring("Query");
        wiringBuilder.dataFetcher("item", itemDataFetcher);
        wiringBuilder.dataFetcher("items", itemsDataFetcher);
        wiringBuilder.dataFetcher("group", itemDataFetcher);
        wiringBuilder.dataFetcher("groups", itemsDataFetcher);
        wiringBuilder.dataFetcher("thing", thingDataFetcher);
        wiringBuilder.dataFetcher("things", thingsDataFetcher);

        runtimeWiringBuilder.type(wiringBuilder.build());
        runtimeWiringBuilder.type(DynamicNameTypeResolver.THING_INTERFACE);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.ITEM);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.STATE);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.ITEM_METADATA);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.EVENT_WIRING);
        runtimeWiringBuilder.type(DynamicNameTypeResolver.ITEM_EVENT_WIRING);

        runtimeWiringBuilder.type(UnionTypes.EXECUTE_COMMAND_RESULT);

        runtimeWiringBuilder.scalar(ExtendedScalars.DateTime);
        runtimeWiringBuilder.scalar(ExtendedScalars.Json);
        runtimeWiringBuilder.scalar(Base64Scalar.INSTANCE);

        var subscriptionWiring = TypeRuntimeWiring.newTypeWiring("Subscription");
        subscriptionWiring.dataFetcher("events", subscriptionDataFetcher);
        subscriptionWiring.dataFetcher("item", subscriptionItemDataFetcher);
        subscriptionWiring.dataFetcher("items", subscriptionItemsDataFetcher);
        subscriptionWiring.dataFetcher("group", subscriptionGroupDataFetcher);
        runtimeWiringBuilder.type(subscriptionWiring.build());

        var mutationWiring = TypeRuntimeWiring.newTypeWiring("Mutation");
        mutationWiring.dataFetcher("item", itemMutationResolver).dataFetcher("executeCommand", itemCommandResolver);
        runtimeWiringBuilder.type(mutationWiring.build());

        var runtimeWiring = runtimeWiringBuilder.build();

        var schema = new SchemaGenerator().makeExecutableSchema(reg, runtimeWiring);

        graphql = GraphQL.newGraphQL(schema).build();
    }

    // private final EventPublisher STOCK_TICKER_PUBLISHER = new EventPublisher();

    /*
     * private DataFetcher subscriptionDataFetcher() {
     * return environment -> {
     * List<String> arg = environment.getArgument("stockCodes");
     * List<String> stockCodesFilter = arg == null ? Collections.emptyList() : arg;
     * if (stockCodesFilter.isEmpty()) {
     * return eventPublisher.getPublisher();
     * } else {
     * return eventPublisher.getPublisher(stockCodesFilter);
     * }
     * };
     * }
     */

    public GraphQL getGraphQL() {
        return this.graphql;
    }

    public TypeDefinitionRegistry loadAndParseSchema() {
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        Stream.of("/graphql/actions.graphqls", "/graphql/addons.graphqls", "/graphql/config.graphqls",
                "/graphql/events.graphqls", "/graphql/items.graphqls", "/graphql/mutation.graphqls",
                "/graphql/query.graphqls", "/graphql/scalars.graphqls", "/graphql/subscription.graphqls",
                "/graphql/things.graphqls", "/graphql/type.graphqls")
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
