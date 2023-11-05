package org.openhab.io.graphql.mapping;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.openhab.core.items.Item;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.library.types.PointType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.library.types.RewindFastforwardType;
import org.openhab.core.library.types.StringListType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.firmware.Firmware;
import org.openhab.core.thing.firmware.FirmwareStatusInfo;
import org.openhab.core.types.Command;
import org.openhab.core.types.CommandDescription;
import org.openhab.core.types.State;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.UnDefType;
import org.openhab.io.graphql.datafetcher.CollectionWrapper;
import org.openhab.io.graphql.mapping.wrapper.ItemWrapper;
import org.openhab.io.graphql.mapping.wrapper.ThingWrapper;
import org.openhab.io.graphql.model.GraphqlCommandDescription;
import org.openhab.io.graphql.model.GraphqlCommandOption;
import org.openhab.io.graphql.model.GraphqlDateTimeState;
import org.openhab.io.graphql.model.GraphqlDecimalState;
import org.openhab.io.graphql.model.GraphqlFirmware;
import org.openhab.io.graphql.model.GraphqlFirmwareCurrentStatusEnum;
import org.openhab.io.graphql.model.GraphqlFirmwareStatus;
import org.openhab.io.graphql.model.GraphqlHSBState;
import org.openhab.io.graphql.model.GraphqlItem;
import org.openhab.io.graphql.model.GraphqlLocationState;
import org.openhab.io.graphql.model.GraphqlOnOffEnum;
import org.openhab.io.graphql.model.GraphqlOnOffState;
import org.openhab.io.graphql.model.GraphqlOpenClosedEnum;
import org.openhab.io.graphql.model.GraphqlOpenClosedState;
import org.openhab.io.graphql.model.GraphqlOtherState;
import org.openhab.io.graphql.model.GraphqlPlayerItemState;
import org.openhab.io.graphql.model.GraphqlPlayerItemStateEnum;
import org.openhab.io.graphql.model.GraphqlQuantityState;
import org.openhab.io.graphql.model.GraphqlRawState;
import org.openhab.io.graphql.model.GraphqlState;
import org.openhab.io.graphql.model.GraphqlStateDescription;
import org.openhab.io.graphql.model.GraphqlStringListState;
import org.openhab.io.graphql.model.GraphqlStringState;
import org.openhab.io.graphql.model.GraphqlThing;
import org.openhab.io.graphql.model.GraphqlThingAction;
import org.openhab.io.graphql.model.GraphqlThingStatus;
import org.openhab.io.graphql.model.GraphqlThingStatusDetail;
import org.openhab.io.graphql.model.GraphqlThingStatusInfo;

import graphql.schema.DataFetchingEnvironment;
import org.openhab.io.graphql.model.GraphqlUnDefEnum;
import org.openhab.io.graphql.model.GraphqlUnDefState;
import org.openhab.io.graphql.model.GraphqlUpDownEnum;
import org.openhab.io.graphql.model.GraphqlUpDownState;

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

        if (firmwareInfo == null)
            return null;

        var status = GraphqlFirmwareCurrentStatusEnum.valueOf(firmwareInfo.getFirmwareStatus().toString());

        return new GraphqlFirmwareStatus(status, firmwareInfo.getUpdatableFirmwareVersion());
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public GraphqlFirmware map(Firmware firmware) {
        if (firmware == null)
            return null;

        return GraphqlFirmware.builder().setDescription(firmware.getDescription()).setChangelog(firmware.getChangelog())
                .setModel(firmware.getModel()).setModelRestricted(firmware.isModelRestricted())
                .setVersion(firmware.getVersion()).setPrerequisiteVersion(firmware.getPrerequisiteVersion()).build();
    }

    public List<GraphqlThingAction> map(ThingActions thingActions) {
        return this.getMapper().getThingMapper().mapThingAction(thingActions, getLocale());
    }

    public GraphqlThing map(Thing it) {
        if (it == null)
            return null;
        return new ThingWrapper(this, it);
    }

    public GraphqlItem map(Item it) {
        if (it == null)
            return null;

        return ItemWrapper.create(this, it);
    }

    public GraphqlStateDescription map(StateDescription it) {
        var result = GraphqlStateDescription.builder();

        if( it.getMaximum() != null )
                result.setMaximum(it.getMaximum().doubleValue())
                      .setMinimum(it.getMinimum().doubleValue())
                        .setStep(it.getStep().doubleValue())
                        .setReadOnly(it.isReadOnly())
                        .setPattern(it.getPattern());

        return result.build();
    }

    public GraphqlState map(Item item, State s) {

        var state = s.toFullString();
        // TODO:
        var description = map(item.getStateDescription());

        if (s instanceof QuantityType) {
            return new GraphqlQuantityState(state, description, ((QuantityType<?>) s).doubleValue(), ((QuantityType<?>) s).getUnit().getName());
        }

        if (s instanceof OpenClosedType) {
            return new GraphqlOpenClosedState(state, description, GraphqlOpenClosedEnum.valueOf(s.toString()) );
        }

        if (s instanceof StringType) {
            return new GraphqlStringState(state, description, s.toString());
        }

        if (s instanceof UnDefType) {
            return new GraphqlUnDefState(state, description, GraphqlUnDefEnum.valueOf(s.toString()));
        }

        if (s instanceof UpDownType) {
            return new GraphqlUpDownState(state, description, GraphqlUpDownEnum.valueOf(s.toString()));
        }

        if (s instanceof OnOffType) {
            return new GraphqlOnOffState(state, description, GraphqlOnOffEnum.valueOf(s.toString()));
        }

        if (s instanceof DateTimeType) {
            return new GraphqlDateTimeState(state, description, new Date(((DateTimeType) s).getInstant().toEpochMilli()) );
        }

        if (s instanceof RewindFastforwardType) {
            return new GraphqlPlayerItemState(state, description, GraphqlPlayerItemStateEnum.valueOf(s.toString()));
        }

        if (s instanceof RawType) {
            return new GraphqlRawState(state, description, ((RawType) s).getMimeType(), Base64.getEncoder().encodeToString(((RawType) s).getBytes()));
        }

        if (s instanceof PointType) {
            return new GraphqlLocationState(state, description, ((PointType) s).getLongitude().doubleValue(), ((PointType) s).getLatitude().doubleValue(), ((PointType) s).getAltitude().doubleValue());
        }

        if (s instanceof HSBType) {
            return new GraphqlHSBState(state, description, ((HSBType) s).getHue().doubleValue(), ((HSBType) s).getSaturation().doubleValue(), ((HSBType) s).getBrightness().doubleValue());
        }

        if (s instanceof DecimalType) {
            return new GraphqlDecimalState(state, description, ((DecimalType) s).doubleValue());
        }

        if (s instanceof StringListType) {
            List<String> values = new ArrayList<>();
            int i=0;
            while(true) {
                try {
                    values.add( ((StringListType) s).getValue(i));
                } catch( IllegalArgumentException ex) {
                    // expected
                    return new GraphqlStringListState(state, description, values);
                }
                i++;
            }

        }

        if (s instanceof PlayPauseType) {
            return new GraphqlPlayerItemState(state, description, GraphqlPlayerItemStateEnum.valueOf(s.toString()));
        }

        return new GraphqlOtherState(state, description);
    }

    public String map(Command itemCommand) {
        return itemCommand.toString();
    }

    public <T> CollectionWrapper<T> buildCollection(Stream<T> stream) {
        var args = dataFetchingEnvironment.getArguments();

        var skip = (int)args.get("skip");
        var limit = (int)args.get("limit");

        return new CollectionWrapper<T>(stream, skip, limit);
    }
}
