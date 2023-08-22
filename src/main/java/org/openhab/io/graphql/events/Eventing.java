package org.openhab.io.graphql.events;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventSubscriber;
import org.openhab.io.graphql.publisher.EventPublisher;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Component
@NonNullByDefault
public class Eventing implements EventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(Eventing.class);
    private final Set<String> subscribedEventTypes = Set.of(EventSubscriber.ALL_EVENT_TYPES);

    private final EventPublisher ssePublisher;

    @Activate
    public Eventing(final @Reference EventPublisher ssePublisher) {
        this.ssePublisher = ssePublisher;
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        return subscribedEventTypes;
    }

    @Override
    public void receive(Event event) {
        logger.info("Event {}", event.getTopic());
        ssePublisher.broadcast(event);
    }
}
