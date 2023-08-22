package org.openhab.io.graphql.mapping.wrapper.config;

import java.util.List;

import org.openhab.core.config.core.ConfigDescription;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.IClassName;
import org.openhab.io.graphql.mapping.wrapper.Wrapper;
import org.openhab.io.graphql.model.GraphqlConfigDescription;
import org.openhab.io.graphql.model.GraphqlConfigDescriptionParameter;
import org.openhab.io.graphql.model.GraphqlConfigDescriptionParameterGroup;

public class ConfigDescriptionWrapper extends Wrapper<ConfigDescription>
        implements GraphqlConfigDescription, IClassName {

    public static GraphqlConfigDescription create(MappingSession session, ConfigDescription item) {
        return new ConfigDescriptionWrapper(session, item);
    }

    protected ConfigDescriptionWrapper(MappingSession session, ConfigDescription item) {
        super(session, item);
    }

    @Override
    public String getUid() {
        return item == null ? "" : item.getUID().toString();
    }

    @Override
    public List<GraphqlConfigDescriptionParameter> getParameters() {
        return item == null ? List.of()
                : item.getParameters().stream().map(it -> ConfigDescriptionParameterWrapper.create(session, it))
                        .toList();
    }

    @Override
    public List<GraphqlConfigDescriptionParameterGroup> getParameterGroups() {
        return item == null ? List.of()
                : item.getParameterGroups().stream()
                        .map(it -> ConfigDescriptionParameterGroupWrapper.create(session, it)).toList();
    }
}
