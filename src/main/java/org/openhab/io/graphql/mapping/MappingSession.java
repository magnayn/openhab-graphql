package org.openhab.io.graphql.mapping;

import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.automation.type.ActionType;
import org.openhab.core.events.Event;
import org.openhab.core.items.Item;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.firmware.Firmware;
import org.openhab.core.thing.firmware.FirmwareStatusInfo;
import org.openhab.core.types.Command;
import org.openhab.core.types.CommandDescription;
import org.openhab.core.types.State;
import org.openhab.io.graphql.mapping.wrapper.ItemWrapper;
import org.openhab.io.graphql.mapping.wrapper.ThingWrapper;
import org.openhab.io.graphql.model.GraphqlCommandDescription;
import org.openhab.io.graphql.model.GraphqlCommandOption;
import org.openhab.io.graphql.model.GraphqlFirmware;
import org.openhab.io.graphql.model.GraphqlFirmwareCurrentStatusEnum;
import org.openhab.io.graphql.model.GraphqlFirmwareStatus;
import org.openhab.io.graphql.model.GraphqlFirmwareStatusEnum;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlState;
import org.openhab.io.graphql.model.GraphqlThing;
import org.openhab.io.graphql.model.GraphqlThingAction;
import org.openhab.io.graphql.model.GraphqlThingFirmware;
import org.openhab.io.graphql.model.GraphqlThingStatus;
import org.openhab.io.graphql.model.GraphqlThingStatusDetail;
import org.openhab.io.graphql.model.GraphqlThingStatusInfo;

import graphql.schema.DataFetchingEnvironment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MappingSession {
    private final Mapper mapper;
    private final DataFetchingEnvironment dataFetchingEnvironment;

    public MappingSession(Mapper mapper, DataFetchingEnvironment dataFetchingEnvironment) {
        this.mapper = mapper;
        this.dataFetchingEnvironment = dataFetchingEnvironment;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public DataFetchingEnvironment getDataFetchingEnvironment() {
        return dataFetchingEnvironment;
    }

    public GraphqlCommandDescription map(CommandDescription commandDescription) {
        if (commandDescription == null)
            return null;
        var cd = new GraphqlCommandDescription();

        cd.setCommandOptions(commandDescription.getCommandOptions().stream().map(it -> {
            return new GraphqlCommandOption(it.getCommand(), it.getLabel());

        }).toList());

        return cd;
    }

    public GraphqlThingStatusInfo map(ThingStatusInfo statusInfo) {
        if (statusInfo == null)
            return null;

        var thingStatus = GraphqlThingStatus.valueOf(statusInfo.getStatus().toString());

        var thingStatusDetail = GraphqlThingStatusDetail.valueOf(statusInfo.getStatusDetail().toString());

        return new GraphqlThingStatusInfo(thingStatus, thingStatusDetail, statusInfo.getDescription());
    }

    public GraphqlFirmwareStatus map(FirmwareStatusInfo firmwareInfo) {

        if( firmwareInfo == null )
            return null;

        var status = GraphqlFirmwareCurrentStatusEnum.valueOf(firmwareInfo.getFirmwareStatus().toString());

        return new GraphqlFirmwareStatus(status, firmwareInfo.getUpdatableFirmwareVersion());
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public GraphqlFirmware map(Firmware firmware) {
        if( firmware == null )
            return null;

        return GraphqlFirmware.builder()
                .setDescription(firmware.getDescription())
                .setChangelog(firmware.getChangelog())
                .setModel(firmware.getModel())
                .setModelRestricted(firmware.isModelRestricted())
                .setVersion(firmware.getVersion())
                .setPrerequisiteVersion(firmware.getPrerequisiteVersion())
                .build();
    }

    public List<GraphqlThingAction> map(ThingActions thingActions) {
        return this.getMapper().getThingMapper().mapThingAction(thingActions, getLocale());
    }

    public GraphqlThing map(Thing it) {
        if( it == null )
            return null;
        return new ThingWrapper(this, it);
    }

    public GraphqlItem map(Item it) {
        if( it == null )
            return null;

        return ItemWrapper.create(this, it);
    }

    public GraphqlState map(State state) {
        return null; // TODO
    }

    public String map(Command itemCommand) {
        return itemCommand.toString();
    }
}
