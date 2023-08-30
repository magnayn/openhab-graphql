package org.openhab.io.graphql.publisher;

import org.openhab.core.events.Event;
import org.openhab.core.items.Item;
import org.openhab.io.graphql.mapping.Mapper;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.EventWrapper;
import org.openhab.io.graphql.model.GraphqlEvent;

public class LazyEventWrapper {

    private final Mapper mapper;
    private final Event openhabEvent;

    private GraphqlEvent event;

    public LazyEventWrapper(Mapper mapper, Event openhabEvent) {
        this.mapper = mapper;
        this.openhabEvent = openhabEvent;
    }

    public GraphqlEvent getEvent() {
        if (event == null) {
            MappingSession ms = new MappingSession(mapper, null);

            event = EventWrapper.create(ms, openhabEvent);
        }

        return event;
    }

    public Event getOpenHABEvent() {
        return this.openhabEvent;
    }

    public Item getReferencedItem() {
        return mapper.getItemForTopic(event.getTopic());
    }
}
