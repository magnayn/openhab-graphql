package org.openhab.io.graphql.datafetcher.subscription;

import org.openhab.io.graphql.model.GraphqlEvent;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;

@Component(service = SubscriptionDataFetcher.class)
public class SubscriptionDataFetcher implements DataFetcher<Flowable<GraphqlEvent>> {

    private final EventPublisher eventPublisher;

    @Activate
    public SubscriptionDataFetcher(@Reference EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Flowable<GraphqlEvent> get(DataFetchingEnvironment environment) throws Exception {
        return eventPublisher.subscribe((Predicate) object -> true);
    }
}
