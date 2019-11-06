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
import com.tmobile.pacbot.azure.inventory.vo.SitesVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class SitesInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(SitesInventoryCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Network/vpnSites?api-version=2019-06-01";

	public List<SitesVH> fetchSitesDetails(SubscriptionVH subscription) throws Exception {

		List<SitesVH> sitesList = new ArrayList<SitesVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {

			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray sitesObjects = responseObj.getAsJsonArray("value");
			if (sitesObjects != null) {
				for (JsonElement sitesElement : sitesObjects) {
					SitesVH sitesVH = new SitesVH();
					JsonObject sitesObject = sitesElement.getAsJsonObject();
					sitesVH.setSubscription(subscription.getSubscriptionId());
					sitesVH.setSubscriptionName(subscription.getSubscriptionName());
					sitesVH.setId(sitesObject.get("id").getAsString());
					sitesVH.setEtag(sitesObject.get("etag").getAsString());
					sitesVH.setLocation(sitesObject.get("location").getAsString());
					sitesVH.setName(sitesObject.get("name").getAsString());
					sitesVH.setType(sitesObject.get("type").getAsString());
					JsonObject properties = sitesObject.getAsJsonObject("properties");
					JsonObject tags = sitesObject.getAsJsonObject("tags");
					if (properties!=null) {
						HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(), HashMap.class);
						sitesVH.setProperties(propertiesMap);
					}
					if (tags!=null) {
						HashMap<String, Object> tagsMap = new Gson().fromJson(tags.toString(), HashMap.class);
						sitesVH.setTags(tagsMap);
					}
					

					sitesList.add(sitesVH);
				}
			}
		} catch (Exception e) {
			log.error("Error Collecting sites",e);
		}

		log.info("Target Type : {}  Total: {} ","Site",sitesList.size());
		return sitesList;
	}

}
