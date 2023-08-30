package org.openhab.io.graphql.datafetcher;

import org.openhab.core.thing.ThingRegistry;
import org.openhab.core.thing.ThingUID;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.wrapper.ThingWrapper;
import org.openhab.io.graphql.model.GraphqlThingInterface;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ThingDataFetcher.class)
// @QueryMapping(fieldName = "items")
public class ThingDataFetcher implements DataFetcher<GraphqlThingInterface> {

    protected ThingRegistry thingsRegistry;

    protected Mapper mapper;

    @Activate
    public ThingDataFetcher(@Reference ThingRegistry thingsRegistry, @Reference Mapper mapper) {
        this.thingsRegistry = thingsRegistry;
        this.mapper = mapper;
    }

    @Override
    public GraphqlThingInterface get(DataFetchingEnvironment environment) throws Exception {
        var root = environment.getRoot();

        var arguments = environment.getArguments();

        var item = thingsRegistry.get(new ThingUID(arguments.get("id").toString()));

        var m = mapper.newMappingSession(environment);
        return new ThingWrapper(m, item);
    }
}
