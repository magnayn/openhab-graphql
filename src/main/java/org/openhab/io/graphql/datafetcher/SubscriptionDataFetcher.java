package org.openhab.io.graphql.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.Flowable;
import org.openhab.io.graphql.model.GraphqlEvent;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;
import java.util.List;

@Component(service = SubscriptionDataFetcher.class)
public class SubscriptionDataFetcher implements DataFetcher<Flowable<GraphqlEvent>> {

    private final EventPublisher eventPublisher;

    @Activate
    public SubscriptionDataFetcher(@Reference  EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Flowable<GraphqlEvent> get(DataFetchingEnvironment environment) throws Exception {

        return eventPublisher.getEvents();

    }
}
