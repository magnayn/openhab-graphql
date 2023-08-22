package org.openhab.io.graphql.mapping.wrapper.config;

import java.util.List;

import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.openhab.io.graphql.mapping.MappingSession;
import org.openhab.io.graphql.mapping.wrapper.IClassName;
import org.openhab.io.graphql.mapping.wrapper.Wrapper;
import org.openhab.io.graphql.model.GraphqlConfigDescriptionParameter;
import org.openhab.io.graphql.model.GraphqlConfigType;
import org.openhab.io.graphql.model.GraphqlFilterCriteria;
import org.openhab.io.graphql.model.GraphqlParameterOption;

public class ConfigDescriptionParameterWrapper extends Wrapper<ConfigDescriptionParameter>
        implements GraphqlConfigDescriptionParameter, IClassName {
    public ConfigDescriptionParameterWrapper(MappingSession session, ConfigDescriptionParameter item) {
        super(session, item);
    }

    public static GraphqlConfigDescriptionParameter create(MappingSession session, ConfigDescriptionParameter item) {
        return new ConfigDescriptionParameterWrapper(session, item);
    }

    @Override
    public String getContext() {
        return item.getContext();
    }

    @Override
    public String getDefaultValue() {
        return item.getDefault();
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
    public String getName() {
        return item.getName();
    }

    @Override
    public boolean getRequired() {
        return item.isRequired();
    }

    @Override
    public GraphqlConfigType getType() {
        return GraphqlConfigType.valueOf(item.getType().toString());
    }

    @Override
    public Double getMin() {
        return item.getMinimum().doubleValue();
    }

    @Override
    public Double getMax() {
        return item.getMaximum().doubleValue();
    }

    @Override
    public Double getStepsize() {
        return item.getStepSize().doubleValue();
    }

    @Override
    public String getPattern() {
        return item.getPattern();
    }

    @Override
    public Boolean getReadOnly() {
        return item.isReadOnly();
    }

    @Override
    public Boolean getMultiple() {
        return item.isMultiple();
    }

    @Override
    public Integer getMultipleLimit() {
        return item.getMultipleLimit();
    }

    @Override
    public String getGroupName() {
        return item.getGroupName();
    }

    @Override
    public boolean getAdanced() {
        return item.isAdvanced();
    }

    @Override
    public Boolean getVerify() {
        return item.isVerifyable();
    }

    @Override
    public boolean getLimitToOptions() {
        return item.getLimitToOptions();
    }

    @Override
    public String getUnit() {
        return item.getUnit();
    }

    @Override
    public String getUnitLabel() {
        return item.getUnitLabel();
    }

    @Override
    public List<GraphqlParameterOption> getOptions() {
        return item.getOptions().stream().map(it -> new GraphqlParameterOption(it.getLabel(), it.getValue())).toList();
    }

    @Override
    public List<GraphqlFilterCriteria> getFilterCriteria() {
        return item.getFilterCriteria().stream().map(it -> new GraphqlFilterCriteria(it.getValue(), it.getName()))
                .toList();
    }
}
