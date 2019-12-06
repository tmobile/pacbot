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
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacbot.azure.inventory.vo.VaultVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class VaultInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(VaultInventoryCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.KeyVault/vaults?api-version=2018-02-14";

	public List<VaultVH> fetchVaultDetails(SubscriptionVH subscription) throws Exception {

		List<VaultVH> vaultList = new ArrayList<VaultVH>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {

			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray vaultObjects = responseObj.getAsJsonArray("value");
			if (vaultObjects != null) {
				for (JsonElement vaultElement : vaultObjects) {
					VaultVH vaultVH = new VaultVH();
					JsonObject vaultObject = vaultElement.getAsJsonObject();
					vaultVH.setSubscription(subscription.getSubscriptionId());
					vaultVH.setSubscriptionName(subscription.getSubscriptionName());
					vaultVH.setId(vaultObject.get("id").getAsString());
					vaultVH.setLocation(vaultObject.get("location").getAsString());
					vaultVH.setName(vaultObject.get("name").getAsString());
					vaultVH.setType(vaultObject.get("type").getAsString());
					JsonObject properties = vaultObject.getAsJsonObject("properties");
					JsonObject tags = vaultObject.getAsJsonObject("tags");
					if (properties != null) {
						HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(),
								HashMap.class);
						vaultVH.setEnabledForDeployment((boolean) propertiesMap.get("enabledForDeployment"));
						vaultVH.setEnabledForDiskEncryption((boolean) propertiesMap.get("enabledForDiskEncryption"));
						vaultVH.setEnabledForTemplateDeployment(
								(boolean) propertiesMap.get("enabledForTemplateDeployment"));
						vaultVH.setTenantId(propertiesMap.get("tenantId").toString());
						vaultVH.setProvisioningState(propertiesMap.get("provisioningState").toString());
						vaultVH.setSku((Map<String, Object>) propertiesMap.get("sku"));
						vaultVH.setVaultUri(propertiesMap.get("vaultUri").toString());

					}
					if (tags != null) {
						HashMap<String, Object> tagsMap = new Gson().fromJson(tags.toString(), HashMap.class);
						vaultVH.setTags(tagsMap);
					}

					vaultList.add(vaultVH);
				}
			}
		} catch (Exception e) {
			log.error("Error Colectting vaults ",e);
		}

		log.info("Target Type : {}  Total: {} ","Vault",vaultList.size());
		return vaultList;
	}

}
