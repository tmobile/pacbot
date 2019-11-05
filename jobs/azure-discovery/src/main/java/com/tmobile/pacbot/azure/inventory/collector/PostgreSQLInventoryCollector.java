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
import com.tmobile.pacbot.azure.inventory.vo.PostgreSQLServerVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class PostgreSQLInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(PostgreSQLInventoryCollector.class);
	
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.DBforPostgreSQL/servers?api-version=2017-12-01";

	public List<PostgreSQLServerVH> fetchPostgreSQLServerDetails(SubscriptionVH subscription) {

		List<PostgreSQLServerVH> postgreSQLServerList = new ArrayList<PostgreSQLServerVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {
			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray postgreSQLServerObjects = responseObj.getAsJsonArray("value");
			for (JsonElement postgreSQLServerObjectElement : postgreSQLServerObjects) {
				PostgreSQLServerVH postgreSQLServerVH = new PostgreSQLServerVH();
				postgreSQLServerVH.setSubscription(subscription.getSubscriptionId());
				postgreSQLServerVH.setSubscriptionName(subscription.getSubscriptionName());
				JsonObject postgreSQLServerObject = postgreSQLServerObjectElement.getAsJsonObject();
				JsonObject properties = postgreSQLServerObject.getAsJsonObject("properties");
				JsonObject sku = postgreSQLServerObject.getAsJsonObject("sku");
				postgreSQLServerVH.setId(postgreSQLServerObject.get("id").getAsString());
				postgreSQLServerVH.setLocation(postgreSQLServerObject.get("location").getAsString());
				postgreSQLServerVH.setName(postgreSQLServerObject.get("name").getAsString());
				postgreSQLServerVH.setType(postgreSQLServerObject.get("type").getAsString());
				if (sku!=null) {
					HashMap<String, Object> skuMap = new Gson().fromJson(sku.toString(), HashMap.class);
					postgreSQLServerVH.setSkuMap(skuMap);
				}
				if (properties!=null) {
					HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(), HashMap.class);
					postgreSQLServerVH.setPropertiesMap(propertiesMap);
				}
				postgreSQLServerList.add(postgreSQLServerVH);
			}
		} catch (Exception e) {
			log.error("Error collectig PostGresDB",e);
		}

		log.info("Target Type : {}  Total: {} ","Postgres DB",postgreSQLServerList.size());
		return postgreSQLServerList;
	}

}
