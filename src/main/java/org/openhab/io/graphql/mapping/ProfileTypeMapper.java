package org.openhab.io.graphql.mapping;

import java.util.HashSet;
import java.util.Set;

import org.openhab.core.thing.profiles.ProfileType;
import org.openhab.core.thing.profiles.ProfileTypeRegistry;
import org.openhab.core.thing.profiles.TriggerProfileType;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ProfileTypeMapper.class)
public class ProfileTypeMapper {

    private final ProfileTypeRegistry profileTypeRegistry;

    @Activate
    public ProfileTypeMapper(@Reference ProfileTypeRegistry profileTypeRegistry) {
        this.profileTypeRegistry = profileTypeRegistry;
    }

    public Set<String> getLinkableItemTypes(ChannelTypeUID ctUID) {
        Set<String> result = new HashSet<>();
        for (ProfileType profileType : profileTypeRegistry.getProfileTypes()) {
            if (profileType instanceof TriggerProfileType type) {
                if (type.getSupportedChannelTypeUIDs().contains(ctUID)) {
                    for (String itemType : profileType.getSupportedItemTypes()) {
                        result.add(itemType);
                    }
                }
            }
        }
        return result;
    }
}
