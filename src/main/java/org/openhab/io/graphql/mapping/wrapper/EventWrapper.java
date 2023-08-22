package org.openhab.io.graphql.mapping.wrapper;

import org.openhab.core.events.Event;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.GroupItemStateChangedEvent;
import org.openhab.core.items.events.GroupStateUpdatedEvent;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemStateChangedEvent;
import org.openhab.core.items.events.ItemStateEvent;
import org.openhab.core.items.events.ItemStatePredictedEvent;
import org.openhab.core.items.events.ItemStateUpdatedEvent;
import org.openhab.core.thing.type.ChannelDefinition;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.model.GraphqlChannelDefinition;
import org.openhab.io.graphql.model.GraphqlChannelType;
import org.openhab.io.graphql.model.GraphqlEvent;
import org.openhab.io.graphql.model.GraphqlGroupItemStateChangedEvent;
import org.openhab.io.graphql.model.GraphqlGroupItemStateUpdatedEvent;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlItemCommandEvent;
import org.openhab.io.graphql.model.GraphqlItemEvent;
import org.openhab.io.graphql.model.GraphqlItemStateChangedEvent;
import org.openhab.io.graphql.model.GraphqlItemStateEvent;
import org.openhab.io.graphql.model.GraphqlItemStatePredictedEvent;
import org.openhab.io.graphql.model.GraphqlItemStateUpdatedEvent;
import org.openhab.io.graphql.model.GraphqlState;
import org.openhab.io.graphql.model.GraphqlStateDescription;

import java.util.List;

public class EventWrapper extends Wrapper<Event>
        implements GraphqlEvent, GraphqlItemEvent, GraphqlItemStateChangedEvent,
        GraphqlGroupItemStateChangedEvent, GraphqlItemCommandEvent, GraphqlItemStateEvent, GraphqlItemStateUpdatedEvent,
        GraphqlItemStatePredictedEvent, GraphqlGroupItemStateUpdatedEvent, IClassName {

    public EventWrapper(MappingSession session, Event item) {
        super(session, item);
    }

    public static GraphqlEvent create(MappingSession session, Event item) {
        return new EventWrapper(session, item);
    }

    @Override
    public String getTopic() {
        return item.getTopic();
    }

    @Override
    public String getPayload() {
        return item.getPayload();
    }

    @Override
    public String getSource() {
        return item.getSource();
    }

    @Override
    public GraphqlItem getItem() {
        var name = item.getTopic().substring(14);
        name = name.substring(0, name.indexOf('/'));

        try {
            var theItem = session.getMapper().getItemMapper().getItemByName(name);
            return session.map(theItem);
        } catch (ItemNotFoundException e) {
            return null;
        }
    }

    @Override
    public String getMemberName() {
        if( item instanceof GroupItemStateChangedEvent)
            return ((GroupItemStateChangedEvent)item).getMemberName();
        else if( item instanceof GroupStateUpdatedEvent)
            return ((GroupStateUpdatedEvent)item).getMemberName();
        return null;
    }

    @Override
    public GraphqlState getPredictedState() {
        return session.map(((ItemStatePredictedEvent)item).getPredictedState());
    }

    @Override
    public boolean getIsConfirmation() {
        return ((ItemStatePredictedEvent)item).isConfirmation();
    }

    @Override
    public GraphqlState getState() {
        if( item instanceof ItemStateEvent) {
            return session.map(((ItemStateEvent)item).getItemState());
        } else if( item instanceof ItemStateUpdatedEvent) {
            return session.map(((ItemStateUpdatedEvent)item).getItemState());
        }
        return null;
    }

    @Override
    public GraphqlState getOldState() {
        return session.map(((ItemStateChangedEvent)item).getOldItemState());
    }

    @Override
    public GraphqlState getNewState() {
        return session.map(((ItemStateChangedEvent)item).getItemState());
    }

    @Override
    public String getCommand() {
        return session.map(((ItemCommandEvent)item).getItemCommand());
    }
}
