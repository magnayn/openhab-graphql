package org.openhab.io.graphql.datafetcher;

import java.util.List;
import java.util.stream.Collectors;

import org.openhab.core.thing.ThingRegistry;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.wrapper.ThingWrapper;
import org.openhab.io.graphql.model.GraphqlThingInterface;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component(service = ThingsDataFetcher.class)
// @QueryMapping(fieldName = "items")
public class ThingsDataFetcher implements DataFetcher<List<GraphqlThingInterface>> {

    protected ThingRegistry thingsRegistry;

    protected Mapper mapper;

    @Activate
    public ThingsDataFetcher(@Reference ThingRegistry thingsRegistry, @Reference Mapper mapper) {
        this.thingsRegistry = thingsRegistry;
        this.mapper = mapper;
    }

    @Override
    public List<GraphqlThingInterface> get(DataFetchingEnvironment environment) throws Exception {
        var items = thingsRegistry.getAll();
        var m = mapper.newMappingSession(environment);
        return items.stream().map(it -> {
            var x = new ThingWrapper(m, it);
            return x;
        }).collect(Collectors.toList());
    }
}
