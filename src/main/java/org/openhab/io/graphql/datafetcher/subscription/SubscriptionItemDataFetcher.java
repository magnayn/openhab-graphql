package org.openhab.io.graphql.datafetcher.subscription;

import org.openhab.io.graphql.model.GraphqlEvent;
import org.openhab.io.graphql.model.GraphqlItemEvent;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.openhab.io.graphql.publisher.LazyEventWrapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Flowable;

@Component(service = SubscriptionItemDataFetcher.class)
public class SubscriptionItemDataFetcher implements DataFetcher<Flowable<GraphqlEvent>> {

    private final EventPublisher eventPublisher;

    @Activate
    public SubscriptionItemDataFetcher(@Reference EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Flowable<GraphqlEvent> get(DataFetchingEnvironment environment) throws Exception {
        return eventPublisher.subscribe(object -> matches(object, environment));
    }

    protected boolean matches(LazyEventWrapper eventWrapper, DataFetchingEnvironment environment) {
        var event = eventWrapper.getEvent();
        if (!(event instanceof GraphqlItemEvent))
            return false;

        return ((GraphqlItemEvent) event).getItem().getName().equals(environment.getArgument("id"));
    }
}
