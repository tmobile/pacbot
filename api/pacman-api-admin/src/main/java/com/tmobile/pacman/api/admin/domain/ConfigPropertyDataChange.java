package com.tmobile.pacman.api.admin.domain;

/**
 * The Class ConfigPropertyDataChange.
 */
public class ConfigPropertyDataChange {
	
	/** The config key. */
	private String configKey;
	
	/** The application. */
	private String application;
	
	/** The old config value. */
	private String oldConfigValue;
	
	/** The new config value. */
	private String newConfigValue;

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
	 * Gets the old config value.
	 *
	 * @return the old config value
	 */
	public String getOldConfigValue() {
		return oldConfigValue;
	}

	/**
	 * Sets the old config value.
	 *
	 * @param oldConfigValue the new old config value
	 */
	public void setOldConfigValue(String oldConfigValue) {
		this.oldConfigValue = oldConfigValue;
	}

	/**
	 * Gets the new config value.
	 *
	 * @return the new config value
	 */
	public String getNewConfigValue() {
		return newConfigValue;
	}

	/**
	 * Sets the new config value.
	 *
	 * @param newConfigValue the new new config value
	 */
	public void setNewConfigValue(String newConfigValue) {
		this.newConfigValue = newConfigValue;
	}


}
