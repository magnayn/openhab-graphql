package org.openhab.io.graphql.datafetcher.subscription;

import org.openhab.io.graphql.datafetcher.ItemsDataFetcher;
import org.openhab.io.graphql.model.GraphqlEvent;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.openhab.io.graphql.publisher.LazyEventWrapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Flowable;

@Component(service = SubscriptionItemsDataFetcher.class)
public class SubscriptionItemsDataFetcher implements DataFetcher<Flowable<GraphqlEvent>> {

    private final EventPublisher eventPublisher;

    @Activate
    public SubscriptionItemsDataFetcher(@Reference EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Flowable<GraphqlEvent> get(DataFetchingEnvironment environment) throws Exception {
        return eventPublisher.subscribe(object -> matches(object, environment));
    }

    protected boolean matches(LazyEventWrapper eventWrapper, DataFetchingEnvironment environment) {

        var item = eventWrapper.getReferencedItem();

        if (item == null)
            return false;

        return ItemsDataFetcher.filter(item, environment.getArguments());
    }
}
