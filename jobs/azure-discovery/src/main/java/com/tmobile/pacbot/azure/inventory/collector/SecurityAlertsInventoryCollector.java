package com.tmobile.pacbot.azure.inventory.collector;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.SecurityAlertsVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class SecurityAlertsInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Security/alerts?api-version=2019-01-01";
	private static Logger log = LoggerFactory.getLogger(SecurityAlertsInventoryCollector.class);
	
	public List<SecurityAlertsVH> fetchSecurityAlertsDetails(SubscriptionVH subscription) throws Exception {

		List<SecurityAlertsVH> securityAlertsList = new ArrayList<SecurityAlertsVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {
			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray securityAlertsObjects = responseObj.getAsJsonArray("value");
			for (JsonElement securityAlertsElement : securityAlertsObjects) {
				SecurityAlertsVH securityAlertsVH = new SecurityAlertsVH();
				JsonObject databricksObject = securityAlertsElement.getAsJsonObject();
				JsonObject properties = databricksObject.getAsJsonObject("properties");
				securityAlertsVH.setId(databricksObject.get("id").getAsString());
				securityAlertsVH.setName(databricksObject.get("name").getAsString());
				securityAlertsVH.setType(databricksObject.get("type").getAsString());
				securityAlertsVH.setSubscription(subscription.getSubscriptionId());
				securityAlertsVH.setSubscriptionName(subscription.getSubscriptionName());

				if (properties != null) {
					HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(), HashMap.class);
					securityAlertsVH.setPropertiesMap(propertiesMap);
				}
				securityAlertsList.add(securityAlertsVH);
			}
		} catch (Exception e) {
			log.error("Error collecting Security Alerts",e);
		}

		log.info("Target Type : {}  Total: {} ","Security Alerts",securityAlertsList.size());
		return securityAlertsList;
	}

}
