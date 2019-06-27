package com.tmobile.cloud.awsrules.config;


import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
public class ConfigManager {
	


	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	/**
	 * Gets the configurations map.
	 *
	 * @return the configurations map
	 */
	public static Hashtable<String, Object> getConfigurationsMap() {
		
		JsonArray propertySourcesArray = new JsonArray();
		Hashtable<String, Object> appPropsHashtable = new Hashtable<>();
		Hashtable<String, Object> rulePropsHashtable = new Hashtable<>();
		Hashtable<String, Object> configHashtable = new Hashtable<>();
		
		String configServerURL = PacmanUtils.getEnvironmentVariable(PacmanSdkConstants.CONFIG_SERVICE_URL);
		String configCredentials = PacmanUtils.getEnvironmentVariable(PacmanSdkConstants.CONFIG_CREDENTIALS);

		if (StringUtils.isNullOrEmpty(configServerURL) || StringUtils.isNullOrEmpty(configCredentials)) {
			logger.info(PacmanSdkConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanSdkConstants.MISSING_CONFIGURATION);
		}

		Map<String, String> configCreds = PacmanUtils.getHeader(configCredentials);

		JsonObject configurationsFromPacmanTable = PacmanUtils.getConfigurationsFromConfigApi(configServerURL, configCreds);
		logger.info("Configured values {} ",configurationsFromPacmanTable);
		if (configurationsFromPacmanTable != null) {
			propertySourcesArray = configurationsFromPacmanTable.get("propertySources").getAsJsonArray();
		}

		
		if (propertySourcesArray.size() > 0) {
			for (int i = 0; i < propertySourcesArray.size(); i++) {
				JsonObject propertySource = (JsonObject) propertySourcesArray.get(i);
				if(propertySource.has(PacmanSdkConstants.NAME)) {
					if (propertySource.get(PacmanSdkConstants.NAME).toString().contains("application")) {
						JsonObject appProps = propertySource.get(PacmanSdkConstants.SOURCE).getAsJsonObject();
						appPropsHashtable = new Gson().fromJson(appProps,new TypeToken<Hashtable<String, Object>>() {}.getType());
					}
					if (propertySource.get(PacmanSdkConstants.NAME).toString().contains("rule")) {
						JsonObject ruleProps = propertySource.get(PacmanSdkConstants.SOURCE).getAsJsonObject();
						rulePropsHashtable = new Gson().fromJson(ruleProps,new TypeToken<Hashtable<String, Object>>() {}.getType());
					}
				}
			}
		} else {
			logger.info(PacmanSdkConstants.MISSING_DB_CONFIGURATION);
			throw new InvalidInputException(PacmanSdkConstants.MISSING_DB_CONFIGURATION);
		}

		
		configHashtable.putAll(appPropsHashtable);
		configHashtable.putAll(rulePropsHashtable);
		return configHashtable;
	}
	
}
