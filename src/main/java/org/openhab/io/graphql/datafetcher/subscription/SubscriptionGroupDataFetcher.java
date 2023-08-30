package org.openhab.io.graphql.datafetcher.subscription;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Flowable;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.model.GraphqlEvent;
import org.openhab.io.graphql.model.GraphqlItemEvent;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.openhab.io.graphql.publisher.LazyEventWrapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SubscriptionGroupDataFetcher.class)
public class SubscriptionGroupDataFetcher implements DataFetcher<Flowable<GraphqlEvent>> {

    private final EventPublisher eventPublisher;
    private final Mapper mapper;
    @Activate
    public SubscriptionGroupDataFetcher(@Reference EventPublisher eventPublisher, @Reference Mapper mapper) {
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Override
    public Flowable<GraphqlEvent> get(DataFetchingEnvironment environment) throws Exception {
        return eventPublisher.subscribe(object -> matches(object, environment));
    }

    protected boolean matches(LazyEventWrapper eventWrapper, DataFetchingEnvironment environment) throws ItemNotFoundException {
        var event = eventWrapper.getEvent();
        if (!(event instanceof GraphqlItemEvent))
            return false;

        var item = eventWrapper.getReferencedItem();

        if (item == null)
            return false;


        var idParam = environment.getArgument("id").toString();

        return matches(item, idParam);
    }

    private boolean matches(Item item, String idParam) throws ItemNotFoundException {
        if( item.getName().equals(idParam))
            return true;
        for(String parentName : item.getGroupNames())
        {
            try {
                var parent = mapper.getItemMapper().getItemByName(parentName);
                if (matches(parent, idParam))
                    return true;
            } catch(ItemNotFoundException ex) {
                return false;
            }
        }
        return false;
    }
}
