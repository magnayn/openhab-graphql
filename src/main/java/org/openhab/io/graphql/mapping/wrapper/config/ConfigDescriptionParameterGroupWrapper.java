package org.openhab.io.graphql.mapping.wrapper.config;

import org.openhab.core.config.core.ConfigDescriptionParameterGroup;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.IClassName;
import org.openhab.io.graphql.mapping.wrapper.Wrapper;
import org.openhab.io.graphql.model.GraphqlConfigDescriptionParameterGroup;

public class ConfigDescriptionParameterGroupWrapper extends Wrapper<ConfigDescriptionParameterGroup>
        implements GraphqlConfigDescriptionParameterGroup, IClassName {
    public ConfigDescriptionParameterGroupWrapper(MappingSession session, ConfigDescriptionParameterGroup item) {
        super(session, item);
    }

    public static GraphqlConfigDescriptionParameterGroup create(MappingSession session,
            ConfigDescriptionParameterGroup item) {
        return new ConfigDescriptionParameterGroupWrapper(session, item);
    }

    @Override
    public String getName() {
        return item.getName();
    }

    @Override
    public String getContext() {
        return item.getContext();
    }

    @Override
    public boolean getAdvanced() {
        return item.isAdvanced();
    }

    @Override
    public String getLabel() {
        return item.getLabel();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }
}
