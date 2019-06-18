package com.tmobile.cloud.awsrules.utils;

import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cloud.awsrules.config.ConfigManager;

public class ConfigUtils {
	
	/** The Constant LOGGER. */
	static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

	/** The prop. */
	static Properties prop;
	static {
		prop = new Properties();
		Hashtable<String, Object> configMap = ConfigManager.getConfigurationsMap();
		if (configMap != null && !configMap.isEmpty()) {
			prop.putAll(configMap);
			LOGGER.info(String.format("loaded the configuration successfully, config has %d keys",
							prop.keySet().size()));
		} else {
			LOGGER.info("unable to load configuration, exiting now");
			throw new RuntimeException("unable to load configuration");
		}
	}

	public static String getPropValue(final String keyname) {
		return prop.getProperty(keyname);
	}
}
