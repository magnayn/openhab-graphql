package org.openhab.io.graphql.mapping;

import java.util.Collection;
import java.util.Locale;

import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.firmware.Firmware;
import org.openhab.core.thing.firmware.FirmwareRegistry;
import org.openhab.core.thing.firmware.FirmwareStatusInfo;
import org.openhab.core.thing.firmware.FirmwareUpdateService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = FirmwareMapper.class)
public class FirmwareMapper {

    private final FirmwareRegistry firmwareRegistry;
    private final FirmwareUpdateService firmwareUpdateService;

    @Activate
    public FirmwareMapper(@Reference FirmwareRegistry firmwareRegistry,
            @Reference FirmwareUpdateService firmwareUpdateService) {
        this.firmwareRegistry = firmwareRegistry;
        this.firmwareUpdateService = firmwareUpdateService;
    }

    public FirmwareStatusInfo getFirmwareStatusInfo(ThingUID uid) {
        return firmwareUpdateService.getFirmwareStatusInfo(uid);
    }

    public Collection<Firmware> getFirmwares(Thing thing, Locale locale) {
        return firmwareRegistry.getFirmwares(thing, locale);
    }
}
