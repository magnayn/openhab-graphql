package org.openhab.io.graphql.mapping;

import java.net.URI;
import java.util.Locale;

import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.config.core.ConfigDescriptionRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ConfigDefinitionMapper.class)
public class ConfigDefinitionMapper {

    private final ConfigDescriptionRegistry configDescriptionRegistry;

    @Activate
    public ConfigDefinitionMapper(@Reference ConfigDescriptionRegistry configDescriptionRegistry) {
        this.configDescriptionRegistry = configDescriptionRegistry;
    }

    public ConfigDescription getConfigDescription(URI descURI, Locale locale) {
        return descURI == null ? null : configDescriptionRegistry.getConfigDescription(descURI, locale);
    }
}
