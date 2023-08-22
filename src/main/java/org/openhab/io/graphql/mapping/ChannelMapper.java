package org.openhab.io.graphql.mapping;

import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.ChannelDefinition;
import org.openhab.core.thing.type.ThingType;
import org.openhab.core.thing.type.ThingTypeRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ChannelMapper.class)
public class ChannelMapper {

    private final ThingTypeRegistry thingTypeRegistry;

    @Activate
    public ChannelMapper(@Reference ThingTypeRegistry thingTypeRegistry) {
        this.thingTypeRegistry = thingTypeRegistry;
    }

    public ThingType getThingType(ThingTypeUID uid) {
        return thingTypeRegistry.getThingType(uid);
    }

    public Object getChannelDefinition(ChannelDefinition it) {
        return null;
    }
}
