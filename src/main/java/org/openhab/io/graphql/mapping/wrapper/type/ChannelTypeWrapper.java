package org.openhab.io.graphql.mapping.wrapper.type;

import java.util.ArrayList;
import java.util.List;

import org.openhab.core.thing.type.ChannelType;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.IClassName;
import org.openhab.io.graphql.mapping.wrapper.Wrapper;
import org.openhab.io.graphql.mapping.wrapper.config.ConfigDescriptionWrapper;
import org.openhab.io.graphql.model.GraphqlChannelKind;
import org.openhab.io.graphql.model.GraphqlChannelType;
import org.openhab.io.graphql.model.GraphqlCommandDescription;
import org.openhab.io.graphql.model.GraphqlConfigDescription;
import org.openhab.io.graphql.model.GraphqlStateDescription;

public class ChannelTypeWrapper extends Wrapper<ChannelType> implements GraphqlChannelType, IClassName {

    public ChannelTypeWrapper(MappingSession session, ChannelType item) {
        super(session, item);
    }

    @Override
    public String getUid() {
        return item.getUID().toString();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public String getLabel() {
        return item.getLabel();
    }

    @Override
    public String getCategory() {
        return item.getCategory();
    }

    @Override
    public String getItemType() {
        return item.getItemType();
    }

    @Override
    public GraphqlChannelKind getKind() {
        return GraphqlChannelKind.valueOf(item.getKind().toString());
    }

    @Override
    public GraphqlStateDescription getStateDescription() {
        return null;
    }

    @Override
    public List<String> getTags() {
        return new ArrayList<>(item.getTags());
    }

    @Override
    public boolean getAdvanced() {
        return item.isAdvanced();
    }

    @Override
    public GraphqlCommandDescription getCommandDescription() {
        return session.map(item.getCommandDescription());
    }

    @Override
    public List<String> getLinkableItemTypes() {
        return new ArrayList(session.getMapper().getProfileTypeMapper().getLinkableItemTypes(item.getUID()));
    }

    @Override
    public GraphqlConfigDescription getConfigDescription() {
        var uri = item.getConfigDescriptionURI();
        var c = session.getMapper().getConfigDefinitionMapper().getConfigDescription(uri, session.getLocale());
        return ConfigDescriptionWrapper.create(session, c);
    }
}
