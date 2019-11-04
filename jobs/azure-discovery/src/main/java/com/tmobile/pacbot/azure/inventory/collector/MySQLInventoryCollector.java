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
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.storage.StorageAccount;
import com.tmobile.pacbot.azure.inventory.vo.BlobContainerVH;
import com.tmobile.pacbot.azure.inventory.vo.MySQLServerVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class MySQLInventoryCollector {
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.DBforMySQL/servers?api-version=2017-12-01";

	public List<MySQLServerVH> fetchMySQLServerDetails(SubscriptionVH subscription) {

		List<MySQLServerVH> mySqlServerList = new ArrayList<MySQLServerVH>();
		String accessToken;
		try {
			accessToken = AzureCredentialManager.getAuthToken();
		} catch (Exception e1) {
			return mySqlServerList;
		}
		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {
			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray sqlServerObjects = responseObj.getAsJsonArray("value");
			for (JsonElement sqlServerObjectElement : sqlServerObjects) {
				MySQLServerVH mySQLServerVH = new MySQLServerVH();
				mySQLServerVH.setSubscription(subscription.getSubscriptionId());
				mySQLServerVH.setSubscriptionName(subscription.getSubscriptionName());
				JsonObject sqlServerObject = sqlServerObjectElement.getAsJsonObject();
				JsonObject properties = sqlServerObject.getAsJsonObject("properties");
				JsonObject sku = sqlServerObject.getAsJsonObject("sku");
				mySQLServerVH.setId(sqlServerObject.get("id").getAsString());
				mySQLServerVH.setLocation(sqlServerObject.get("location").getAsString());
				mySQLServerVH.setName(sqlServerObject.get("name").getAsString());
				mySQLServerVH.setType(sqlServerObject.get("type").getAsString());
				if (sku!=null) {
					HashMap<String, Object> skuMap = new Gson().fromJson(sku.toString(), HashMap.class);
					mySQLServerVH.setSkuMap(skuMap);
				}
				if (properties!=null) {
					HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(), HashMap.class);
					mySQLServerVH.setPropertiesMap(propertiesMap);
				}

				mySqlServerList.add(mySQLServerVH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(mySqlServerList.size());
		return mySqlServerList;
	}

}
