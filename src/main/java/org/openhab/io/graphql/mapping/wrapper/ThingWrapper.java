package org.openhab.io.graphql.mapping.wrapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.jetty.util.ajax.JSON;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.type.ThingTypeWrapper;
import org.openhab.io.graphql.model.GraphqlBridge;
import org.openhab.io.graphql.model.GraphqlChannel;
import org.openhab.io.graphql.model.GraphqlThing;
import org.openhab.io.graphql.model.GraphqlThingAction;
import org.openhab.io.graphql.model.GraphqlThingConfiguration;
import org.openhab.io.graphql.model.GraphqlThingFirmware;
import org.openhab.io.graphql.model.GraphqlThingInterface;
import org.openhab.io.graphql.model.GraphqlThingStatusInfo;
import org.openhab.io.graphql.model.GraphqlThingType;

public class ThingWrapper implements GraphqlThing, GraphqlBridge, IClassName {
    private final MappingSession session;
    private final Thing thing;

    public static GraphqlThingInterface create(MappingSession session, Thing item) {
        return new ThingWrapper(session, item);
    }

    public ThingWrapper(MappingSession session, Thing thing) {
        this.session = session;
        this.thing = thing;
    }

    public String get__typename() {
        return thing instanceof Bridge ? "Bridge" : "Thing";
    }

    @Override
    public String getUid() {
        return thing.getUID().getId();
    }

    @Override
    public GraphqlThingType getType() {

        return ThingTypeWrapper.create(session,
                session.getMapper().getThingTypeMapper().getThingType(thing.getThingTypeUID()));
    }

    @Override
    public String getLabel() {
        return thing.getLabel();
    }

    @Override
    public @NotNull GraphqlThingConfiguration getConfiguration() {
        return session.getMapper().mapConfiguration(thing.getConfiguration());
    }

    @Override
    public String getProperties() {
        return JSON.toString(thing.getProperties());
    }

    @Override
    public String getLocation() {
        return thing.getLocation();
    }

    @Override
    public List<GraphqlChannel> getChannels() {
        return thing.getChannels().stream().map(it -> new ChannelWrapper(session, it)).collect(Collectors.toList());
    }

    @Override
    public GraphqlThingStatusInfo getStatus() {
        return session.map(thing.getStatusInfo());
    }

    @Override
    public GraphqlThingFirmware getFirmware() {

        var firmwareInfo = session.getMapper().getFirmwareMapper().getFirmwareStatusInfo(thing.getUID());

        var firmwares = session.getMapper().getFirmwareMapper().getFirmwares(thing, session.getLocale()).stream()
                .map(it -> session.map(it)).toList();

        return new GraphqlThingFirmware(session.map(firmwareInfo), firmwares);
    }

    @Override
    public boolean getEnabled() {
        return thing.isEnabled();
    }

    @Override
    public boolean getEditable() {
        return session.getMapper().getThingMapper().isManagedThing(thing.getUID());
    }

    @Override
    public GraphqlBridge getBridge() {
        var bridge = session.getMapper().getThing(thing.getBridgeUID());
        return (GraphqlBridge) create(session, bridge);
    }

    @Override
    public List<GraphqlThingAction> getActions() {
        var thingActions = session.getMapper().getThingMapper().getThingActionsFor(thing);

        return thingActions.stream().map(it -> session.map(it)).flatMap(List::stream).toList();
    }

    @Override
    public List<GraphqlThing> getThings() {

        return ((Bridge) thing).getThings().stream().map(it -> session.map(it)).toList();
    }
}
