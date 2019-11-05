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
import com.tmobile.pacbot.azure.inventory.vo.MariaDBVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class MariaDBInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(MariaDBInventoryCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.DBforMariaDB/servers?api-version=2018-06-01-preview";

	public List<MariaDBVH> fetchMariaDBDetails(SubscriptionVH subscription) {

		List<MariaDBVH> mariaDBList = new ArrayList<>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());
		
		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {
			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray mariaDBObjects = responseObj.getAsJsonArray("value");
			for (JsonElement mariaDBElement : mariaDBObjects) {
				MariaDBVH mariaDBVH = new MariaDBVH();
				JsonObject mariaDBObject = mariaDBElement.getAsJsonObject();
				JsonObject properties = mariaDBObject.getAsJsonObject("properties");
				JsonObject sku = mariaDBObject.getAsJsonObject("sku");
				mariaDBVH.setId(mariaDBObject.get("id").getAsString());
				mariaDBVH.setLocation(mariaDBObject.get("location").getAsString());
				mariaDBVH.setName(mariaDBObject.get("name").getAsString());
				mariaDBVH.setType(mariaDBObject.get("type").getAsString());
				mariaDBVH.setSubscription(subscription.getSubscriptionId());
				mariaDBVH.setSubscriptionName(subscription.getSubscriptionName());
				if (sku!=null) {
					HashMap<String, Object> skuMap = new Gson().fromJson(sku.toString(), HashMap.class);
					mariaDBVH.setSkuMap(skuMap);
				}
				if (properties!=null) {
					HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(), HashMap.class);
					mariaDBVH.setPropertiesMap(propertiesMap);
				}
				mariaDBList.add(mariaDBVH);
			}
		} catch (Exception e) {
			log.error("Error Collecting MariaDB",e);
		}

		log.info("Target Type : {}  Total: {} ","MariaDB",mariaDBList.size());
		return mariaDBList;
	}

}
