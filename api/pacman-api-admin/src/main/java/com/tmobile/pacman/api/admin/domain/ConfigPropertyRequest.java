package com.tmobile.pacman.api.admin.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class ConfigPropertyRequest.
 */
public class ConfigPropertyRequest {
    
    /** The config properties. */
    private List<ConfigPropertyItem> configProperties;
    

    /**
     * Gets the config properties.
     *
     * @return the config properties
     */
    public List<ConfigPropertyItem> getConfigProperties() {
        return configProperties;
    }

    /**
     * Sets the config properties.
     *
     * @param configProperties the new config properties
     */
    public void setConfigProperties(List<ConfigPropertyItem> configProperties) {
        this.configProperties = configProperties;
    }

    /**
     * Checks if is request complete.
     *
     * @return true, if is request complete
     */
    @JsonIgnore
    public boolean isRequestComplete() {
        List<String> invalidList = new ArrayList<>();
        configProperties.forEach(configProperty -> {
            if (!(StringUtils.isNotBlank(configProperty.getConfigKey())
                    && StringUtils.isNotBlank(configProperty.getConfigValue())
                    && StringUtils.isNotBlank(configProperty.getApplication()))) {
                invalidList.add(configProperty.toString());
            }
        });
        return invalidList.isEmpty();
    }

	
  
}
