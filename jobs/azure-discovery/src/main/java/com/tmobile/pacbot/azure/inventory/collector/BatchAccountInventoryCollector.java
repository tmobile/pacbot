package com.tmobile.pacbot.azure.inventory.collector;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tmobile.pacbot.azure.inventory.vo.BatchAccountVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class BatchAccountInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger LOGGER = LoggerFactory.getLogger(BatchAccountInventoryCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Batch/batchAccounts?api-version=2019-08-01";

	public List<BatchAccountVH> fetchBatchAccountDetails(SubscriptionVH subscription) throws Exception {

		List<BatchAccountVH> batchAccountList = new ArrayList<BatchAccountVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {

			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray batchAccountObjects = responseObj.getAsJsonArray("value");
			if (batchAccountObjects != null) {
				for (JsonElement batchAccountElement : batchAccountObjects) {
					BatchAccountVH batchAccountVH = new BatchAccountVH();
					JsonObject batchAccountObject = batchAccountElement.getAsJsonObject();
					batchAccountVH.setSubscription(subscription.getSubscriptionId());
					batchAccountVH.setSubscriptionName(subscription.getSubscriptionName());
					batchAccountVH.setId(batchAccountObject.get("id").getAsString());
					batchAccountVH.setLocation(batchAccountObject.get("location").getAsString());
					batchAccountVH.setName(batchAccountObject.get("name").getAsString());
					batchAccountVH.setType(batchAccountObject.get("type").getAsString());
					JsonObject properties = batchAccountObject.getAsJsonObject("properties");
					JsonObject tags = batchAccountObject.getAsJsonObject("tags");
					if (properties != null) {
						HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(),
								HashMap.class);
						batchAccountVH.setProvisioningState(propertiesMap.get("provisioningState").toString());
						batchAccountVH.setAccountEndpoint(propertiesMap.get("accountEndpoint").toString());
						batchAccountVH.setPoolQuota(propertiesMap.get("poolQuota").toString());
						batchAccountVH.setPoolAllocationMode(propertiesMap.get("poolAllocationMode").toString());
						batchAccountVH.setDedicatedCoreQuotaPerVMFamily(propertiesMap.get("dedicatedCoreQuotaPerVMFamilyEnforced").toString());
						batchAccountVH.setDedicatedCoreQuota(propertiesMap.get("dedicatedCoreQuota").toString());
						batchAccountVH.setLowPriorityCoreQuota(propertiesMap.get("lowPriorityCoreQuota").toString());
						batchAccountVH.setActiveJobAndJobScheduleQuota(propertiesMap.get("activeJobAndJobScheduleQuota").toString());
						batchAccountVH.setAutoStorage((Map<String, Object>) propertiesMap.get("autoStorage"));
					}
					if (tags != null) {
						HashMap<String, Object> tagsMap = new Gson().fromJson(tags.toString(), HashMap.class);
						batchAccountVH.setTags(tagsMap);
						
						
						
					}

					batchAccountList.add(batchAccountVH);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching BatchAccount",e);
		}

		LOGGER.info("Target Type : {}  Total: {} ","Batch Account",batchAccountList.size());
		return batchAccountList;
	}


}
