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
import com.tmobile.pacbot.azure.inventory.vo.SearchServiceVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class SearchServiceInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(SearchServiceInventoryCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Search/searchServices?api-version=2015-08-19";

	public List<SearchServiceVH> fetchSearchServiceDetails(SubscriptionVH subscription) throws Exception {

		List<SearchServiceVH> searchServiceList = new ArrayList<SearchServiceVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());
		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {

			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray searchServiceObjects = responseObj.getAsJsonArray("value");
			if (searchServiceObjects != null) {
				for (JsonElement searchServiceElement : searchServiceObjects) {
					SearchServiceVH searchServiceVH = new SearchServiceVH();
					JsonObject searchServiceObject = searchServiceElement.getAsJsonObject();
					searchServiceVH.setSubscription(subscription.getSubscriptionId());
					searchServiceVH.setSubscriptionName(subscription.getSubscriptionName());
					searchServiceVH.setId(searchServiceObject.get("id").getAsString());
					searchServiceVH.setLocation(searchServiceObject.get("location").getAsString());
					searchServiceVH.setName(searchServiceObject.get("name").getAsString());
					searchServiceVH.setType(searchServiceObject.get("type").getAsString());
					JsonObject properties = searchServiceObject.getAsJsonObject("properties");
					JsonObject sku = searchServiceObject.getAsJsonObject("sku");
					if (properties != null) {
						HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(),
								HashMap.class);
						searchServiceVH.setProperties(propertiesMap);
					}

					if (sku != null) {
						HashMap<String, Object> skuMap = new Gson().fromJson(sku.toString(), HashMap.class);
						searchServiceVH.setSku(skuMap);
					}

					searchServiceList.add(searchServiceVH);
				}
			}
		} catch (Exception e) {
			log.error("Error collecting Search Service",e);
		}

		log.info("Target Type : {}  Total: {} ","Search Service",searchServiceList.size());
		return searchServiceList;
	}

}
