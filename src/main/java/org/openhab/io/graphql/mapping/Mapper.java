package org.openhab.io.graphql.mapping;

import java.util.Set;
import java.util.stream.Collectors;

import org.openhab.core.config.core.Configuration;
import org.openhab.core.io.rest.LocaleService;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingRegistry;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.link.ItemChannelLinkRegistry;
import org.openhab.io.graphql.model.GraphqlThingConfiguration;
import org.openhab.io.graphql.model.GraphqlThingStatus;
import org.openhab.io.graphql.model.GraphqlThingStatusDetail;
import org.openhab.io.graphql.model.GraphqlThingStatusInfo;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.DataFetchingEnvironment;

@Component(service = Mapper.class)
public class Mapper {

    public ThingMapper getThingMapper() {
        return thingMapper;
    }

    private ThingMapper thingMapper;

    protected ItemChannelLinkRegistry linkRegistry;

    protected ThingRegistry thingRegistry;

    private final LocaleService localeService;

    private final ItemMapper itemMapper;

    public ItemMapper getItemMapper() {
        return itemMapper;
    }

    private final ThingTypeMapper thingTypeMapper;

    private final ConfigDefinitionMapper configDefinitionMapper;

    private final ProfileTypeMapper profileTypeMapper;

    private final FirmwareMapper firmwareMapper;

    public ChannelMapper getChannelMapper() {
        return channelMapper;
    }

    private final ChannelMapper channelMapper;

    @Activate
    public Mapper(@Reference ItemChannelLinkRegistry linkRegistry, @Reference ThingRegistry thingRegistry,
            @Reference LocaleService localeService, @Reference ThingMapper thingMapper,
            @Reference ItemMapper itemMapper, @Reference ThingTypeMapper thingTypeMapper,
            @Reference ConfigDefinitionMapper configDefinitionMapper, @Reference ProfileTypeMapper profileTypeMapper,
            @Reference FirmwareMapper firmwareMapper, @Reference ChannelMapper channelMapper) {

        this.thingRegistry = thingRegistry;
        this.linkRegistry = linkRegistry;
        this.localeService = localeService;
        this.thingMapper = thingMapper;
        this.itemMapper = itemMapper;
        this.thingTypeMapper = thingTypeMapper;
        this.configDefinitionMapper = configDefinitionMapper;
        this.profileTypeMapper = profileTypeMapper;
        this.firmwareMapper = firmwareMapper;
        this.channelMapper = channelMapper;
    }

    public ThingTypeMapper getThingTypeMapper() {
        return thingTypeMapper;
    }

    public ProfileTypeMapper getProfileTypeMapper() {
        return profileTypeMapper;
    }

    public ConfigDefinitionMapper getConfigDefinitionMapper() {
        return configDefinitionMapper;
    }

    public MappingSession newMappingSession(DataFetchingEnvironment dfe) {
        return new MappingSession(this, dfe);
    }

    public Set<Channel> getChannelsForItem(String uid) {
        Set<Channel> o = null;
        var channels = linkRegistry.getBoundChannels(uid);

        return channels.stream().map(it -> thingRegistry.getChannel(it)).collect(Collectors.toSet());
    }

    public Set<Item> getChannelLinkedItems(ChannelUID uid) {
        return linkRegistry.getLinkedItems(uid);
    }

    public Thing getThingOwningChannel(ChannelUID uid) {
        return thingRegistry.get(uid.getThingUID());
    }

    public Thing getThing(ThingUID thingUID) {
        return thingRegistry.get(thingUID);
    }

    public GraphqlThingStatusInfo mapThingStatusInfo(ThingStatusInfo statusInfo) {
        return GraphqlThingStatusInfo.builder().setDescription(statusInfo.getDescription())
                .setStatus(GraphqlThingStatus.valueOf(statusInfo.getStatus().toString()))
                .setStatusDetail(GraphqlThingStatusDetail.valueOf(statusInfo.getStatusDetail().toString())).build();
    }

    public GraphqlThingConfiguration mapConfiguration(Configuration configuration) {
        return null;
    }

    public FirmwareMapper getFirmwareMapper() {
        return this.firmwareMapper;
    }

    public Item getItemForTopic(String topic) {
        var name = topic.substring(14);
        name = name.substring(0, name.indexOf('/'));

        try {
            var theItem = getItemMapper().getItemByName(name);
            return theItem;
        } catch (ItemNotFoundException e) {
            return null;
        }
    }
}
