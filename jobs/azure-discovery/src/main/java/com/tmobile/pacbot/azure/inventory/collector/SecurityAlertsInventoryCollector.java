package com.tmobile.pacbot.azure.inventory.collector;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.management.Azure;
import com.tmobile.pacbot.azure.inventory.vo.DatabricksVH;
import com.tmobile.pacbot.azure.inventory.vo.SecurityAlertsVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class SecurityAlertsInventoryCollector {

	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Security/alerts?api-version=2019-01-01";

	public List<SecurityAlertsVH> fetchSecurityAlertsDetails(SubscriptionVH subscription) throws Exception {

		List<SecurityAlertsVH> securityAlertsList = new ArrayList<SecurityAlertsVH>();
		String accessToken;
		try {
			accessToken = AzureCredentialManager.getAuthToken();

		} catch (Exception e1) {
			return securityAlertsList;
		}

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
			e.printStackTrace();
		}

		System.out.println(securityAlertsList.size());
		return securityAlertsList;
	}

}
