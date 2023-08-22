package org.openhab.io.graphql.mapping;

import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.thing.profiles.ProfileType;
import org.openhab.core.thing.profiles.ProfileTypeRegistry;
import org.openhab.core.thing.profiles.TriggerProfileType;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashSet;
import java.util.Set;

@Component(service = ItemMapper.class)
public class ItemMapper {

    private final ItemRegistry itemRegistry;

    @Activate
    public ItemMapper(@Reference ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

   public Item getItemByName(String name) throws ItemNotFoundException {
        return itemRegistry.getItem(name);
   }
}
