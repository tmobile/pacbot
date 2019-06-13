package com.tmobile.pacman.api.admin.domain;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class ConfigPropertyItem.
 */
public class ConfigPropertyItem {
	
	/** The config key. */
	private String configKey;
	
	/** The config value. */
	private String configValue;
	
	/** The application. */
	private String application;

	/**
	 * Gets the config key.
	 *
	 * @return the config key
	 */
	public String getConfigKey() {
		return configKey;
	}

	/**
	 * Sets the config key.
	 *
	 * @param configKey the new config key
	 */
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	/**
	 * Gets the config value.
	 *
	 * @return the config value
	 */
	public String getConfigValue() {
		return configValue;
	}

	/**
	 * Sets the config value.
	 *
	 * @param configValue the new config value
	 */
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	/**
	 * Gets the application.
	 *
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 *
	 * @param application the new application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Checks if is request complete.
	 *
	 * @return true, if is request complete
	 */
	@JsonIgnore
	public boolean isRequestComplete() {
		return (StringUtils.isNotBlank(configKey) && StringUtils.isNotBlank(configValue)
				&& StringUtils.isNotBlank(application));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return application + "|" + configKey;
	}
}
