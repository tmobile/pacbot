package com.tmobile.pacman.api.admin.domain;

/**
 * The Class ConfigPropertyRollBackItem.
 */
public class ConfigPropertyRollBackItem {
	
	/** The config key. */
	private String configKey;
	
	/** The application. */
	private String application;
	
	/** The present config value. */
	private String presentConfigValue;
	
	/** The future config value. */
	private String futureConfigValue;

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
	 * Gets the present config value.
	 *
	 * @return the present config value
	 */
	public String getPresentConfigValue() {
		return presentConfigValue;
	}

	/**
	 * Sets the present config value.
	 *
	 * @param presentConfigValue the new present config value
	 */
	public void setPresentConfigValue(String presentConfigValue) {
		this.presentConfigValue = presentConfigValue;
	}

	/**
	 * Gets the future config value.
	 *
	 * @return the future config value
	 */
	public String getFutureConfigValue() {
		return futureConfigValue;
	}

	/**
	 * Sets the future config value.
	 *
	 * @param futureConfigValue the new future config value
	 */
	public void setFutureConfigValue(String futureConfigValue) {
		this.futureConfigValue = futureConfigValue;
	}
}
