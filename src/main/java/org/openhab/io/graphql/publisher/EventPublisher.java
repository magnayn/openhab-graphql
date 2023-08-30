package org.openhab.io.graphql.publisher;

import org.openhab.core.events.Event;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.EventWrapper;
import org.openhab.io.graphql.model.GraphqlEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

@Component(service = EventPublisher.class)
public class EventPublisher {

    private final Mapper mapper;

    // private final Flowable<LazyEventWrapper> events;
    private final PublishSubject<LazyEventWrapper> ps;

    @Activate
    public EventPublisher(@Reference Mapper mapper) {
        this.mapper = mapper;

        ps = PublishSubject.create();

        // events = ps.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void broadcast(Event event) {

        MappingSession ms = new MappingSession(mapper, null);

        var e = EventWrapper.create(ms, event);

        ps.onNext(new LazyEventWrapper(mapper, event));
    }

    public Flowable<GraphqlEvent> subscribe(Predicate<LazyEventWrapper> predicate) {
        return ps.filter(predicate).map(it -> it.getEvent()).toFlowable(BackpressureStrategy.BUFFER);
    }
}
