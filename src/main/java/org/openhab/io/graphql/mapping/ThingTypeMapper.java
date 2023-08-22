package org.openhab.io.graphql.mapping;

import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.ThingType;
import org.openhab.core.thing.type.ThingTypeRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ThingTypeMapper.class)
public class ThingTypeMapper {

    private final ThingTypeRegistry thingTypeRegistry;

    @Activate
    public ThingTypeMapper(@Reference ThingTypeRegistry thingTypeRegistry) {
        this.thingTypeRegistry = thingTypeRegistry;
    }

    public ThingType getThingType(ThingTypeUID uid) {
        return thingTypeRegistry.getThingType(uid);
    }
}
