package com.tmobile.pacman.commons.azure.clients;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.tmobile.pacman.commons.utils.CommonUtils;

public class AzureCredentialManager {

	 /** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(AzureCredentialManager.class);
	
	public static Azure authenticate(String subscription) {
		return Azure.authenticate(getCredentials()).withSubscription(subscription);
	}
	
	public static String getAuthToken() throws Exception {
		String url = "https://login.microsoftonline.com/%s/oauth2/token";
		
		String clientId = System.getProperty("azure.clientId");
		String domain = System.getProperty("azure.domain");
		String secret = System.getProperty("azure.secret");
		
		
		Map<String,String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("client_secret", secret);
		params.put("resource", "https://management.azure.com");
		params.put("grant_type", "client_credentials");
		url = String.format(url, domain);
	
		try {
			String jsonResponse = CommonUtils.doHttpPost(url, params);
			Map<String,String> respMap = new Gson().fromJson(jsonResponse, new TypeToken<Map<String, String>>() {}.getType() );
			return respMap.get("access_token");
		} catch (Exception e) {
			logger.error("Error getting mangement API token from Azure",e);
			throw e;
		}
	}

	public static String getGraphApiAuthToken() throws Exception {
		String url = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";
		
		String clientId = System.getProperty("azure.clientId");
		String domain = System.getProperty("azure.domain");
		String secret = System.getProperty("azure.secret");
		
		Map<String,String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("client_secret", secret);
		params.put("scope", "https://graph.microsoft.com/.default");
		params.put("grant_type", "client_credentials");
		url = String.format(url, domain);
	
		try {
			String jsonResponse = CommonUtils.doHttpPost(url, params);
			Map<String,String> respMap = new Gson().fromJson(jsonResponse, new TypeToken<Map<String, String>>() {}.getType() );
			return respMap.get("access_token");
		} catch (Exception e) {
			logger.error("Error getting Grpah API token from Azure",e);
			throw e;
		}
	
	}
	
	
	private static ApplicationTokenCredentials getCredentials(){
		String clientId = System.getProperty("azure.clientId");
		String domain = System.getProperty("azure.domain");
		String secret = System.getProperty("azure.secret");
		return new ApplicationTokenCredentials(clientId, 
				domain, secret, AzureEnvironment.AZURE);
	}

	
}
