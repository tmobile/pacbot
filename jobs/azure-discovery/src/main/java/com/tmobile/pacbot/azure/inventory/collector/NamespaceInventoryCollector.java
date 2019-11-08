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
import com.tmobile.pacbot.azure.inventory.vo.NamespaceVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class NamespaceInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(NamespaceInventoryCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.EventHub/namespaces?api-version=2017-04-01";

	public List<NamespaceVH> fetchNamespaceDetails(SubscriptionVH subscription) throws Exception {

		List<NamespaceVH> namespaceList = new ArrayList<NamespaceVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {

			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray namespaceObjects = responseObj.getAsJsonArray("value");
			if (namespaceObjects != null) {
				for (JsonElement namespaceElement : namespaceObjects) {
					NamespaceVH namespaceVH = new NamespaceVH();
					JsonObject namespaceObject = namespaceElement.getAsJsonObject();
					namespaceVH.setSubscription(subscription.getSubscriptionId());
					namespaceVH.setSubscriptionName(subscription.getSubscriptionName());
					namespaceVH.setId(namespaceObject.get("id").getAsString());
					namespaceVH.setLocation(namespaceObject.get("location").getAsString());
					namespaceVH.setName(namespaceObject.get("name").getAsString());
					namespaceVH.setType(namespaceObject.get("type").getAsString());
					JsonObject properties = namespaceObject.getAsJsonObject("properties");
					JsonObject tags = namespaceObject.getAsJsonObject("tags");
					JsonObject sku = namespaceObject.getAsJsonObject("sku");
					if (properties != null) {
						HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(),
								HashMap.class);
						namespaceVH.setProperties(propertiesMap);
					}
					if (tags != null) {
						HashMap<String, Object> tagsMap = new Gson().fromJson(tags.toString(), HashMap.class);
						namespaceVH.setTags(tagsMap);
					}
					if (sku != null) {
						HashMap<String, Object> skuMap = new Gson().fromJson(sku.toString(), HashMap.class);
						namespaceVH.setSku(skuMap);
					}


					namespaceList.add(namespaceVH);
				}
			}
		} catch (Exception e) {
			log.error("Error collecting namespace",e);
		}

		log.info("Target Type : {}  Total: {} ","Namespace",namespaceList.size());
		return namespaceList;
	}


}
