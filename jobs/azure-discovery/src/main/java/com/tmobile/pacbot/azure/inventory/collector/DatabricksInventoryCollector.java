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
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class DatabricksInventoryCollector {

	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Databricks/workspaces?api-version=2018-04-01";

	public List<DatabricksVH> fetchDatabricksDetails(SubscriptionVH subscription) {

		List<DatabricksVH> databricksList = new ArrayList<DatabricksVH>();
		String accessToken;
		try {
			accessToken = AzureCredentialManager.getAuthToken();
		} catch (Exception e1) {
			return databricksList;
		}
		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {
			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray databricksObjects = responseObj.getAsJsonArray("value");
			for (JsonElement databricksElement : databricksObjects) {
				DatabricksVH databricksVH = new DatabricksVH();
				JsonObject databricksObject = databricksElement.getAsJsonObject();
				JsonObject properties = databricksObject.getAsJsonObject("properties");
				JsonObject sku = databricksObject.getAsJsonObject("sku");
				databricksVH.setId(databricksObject.get("id").getAsString());
				databricksVH.setLocation(databricksObject.get("location").getAsString());
				databricksVH.setName(databricksObject.get("name").getAsString());
				databricksVH.setType(databricksObject.get("type").getAsString());
				databricksVH.setSubscription(subscription.getSubscriptionId());
				databricksVH.setSubscriptionName(subscription.getSubscriptionName());
				if (sku!=null) {
					HashMap<String, Object> skuMap = new Gson().fromJson(sku.toString(), HashMap.class);
					databricksVH.setSkuMap(skuMap);
				}
				if (properties!=null) {
					HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(), HashMap.class);
					databricksVH.setPropertiesMap(propertiesMap);
				}
				databricksList.add(databricksVH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(databricksList.size());
		return databricksList;
	}

}
