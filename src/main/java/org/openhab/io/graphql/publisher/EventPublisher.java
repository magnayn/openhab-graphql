package org.openhab.io.graphql.publisher;

import io.reactivex.subjects.PublishSubject;
import org.openhab.core.events.Event;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.EventWrapper;
import org.openhab.io.graphql.model.GraphqlEvent;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = EventPublisher.class)
public class EventPublisher {


    private final Mapper mapper;

    private final Flowable<GraphqlEvent> events;
    private final PublishSubject<GraphqlEvent> ps;

    @Activate
    public EventPublisher(@Reference Mapper mapper) {
        this.mapper = mapper;

        ps = PublishSubject.create();
        events = ps.toFlowable(BackpressureStrategy.BUFFER);

    }

    public void broadcast(Event event) {

        MappingSession ms = new MappingSession(mapper, null);

        var e = EventWrapper.create(ms, event);

        ps.onNext(e);
    }

    public Flowable<GraphqlEvent> getEvents() {
        return this.events;
    }


    private void emitEvents(ObservableEmitter<GraphqlEvent> emitter, GraphqlEvent event) {
        emitter.onNext(event);
    }



}
