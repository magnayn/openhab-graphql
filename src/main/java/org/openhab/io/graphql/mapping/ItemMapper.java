package org.openhab.io.graphql.mapping;

import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
