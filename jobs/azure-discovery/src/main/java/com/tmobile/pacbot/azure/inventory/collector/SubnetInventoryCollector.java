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
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.SubnetVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class SubnetInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/virtualNetworks/%s/subnets?api-version=2019-07-01";
	private static Logger log = LoggerFactory.getLogger(SubnetInventoryCollector.class);

	public List<SubnetVH> fetchSubnetDetails(SubscriptionVH subscription) {

		List<SubnetVH> subnetList = new ArrayList<>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());
		Azure azure = azureCredentialProvider.authenticate(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<Network> networks = azure.networks().list();
		for (Network network : networks) {
			String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()),
					URLEncoder.encode(network.resourceGroupName()), URLEncoder.encode(network.name()));
			try {
				String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
				JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
				JsonArray subnetObjects = responseObj.getAsJsonArray("value");
				for (JsonElement subnetElement : subnetObjects) {
					SubnetVH subnetVH = new SubnetVH();
					subnetVH.setSubscription(subscription.getSubscriptionId());
					subnetVH.setSubscriptionName(subscription.getSubscriptionName());
					JsonObject subnetObject = subnetElement.getAsJsonObject();
					JsonObject properties = subnetObject.getAsJsonObject("properties");
					subnetVH.setId(subnetObject.get("id").getAsString());
					subnetVH.setName(subnetObject.get("name").getAsString());
					subnetVH.setType(subnetObject.get("type").getAsString());
					subnetVH.setEtag(subnetObject.get("etag").getAsString());
					if (properties != null) {
						HashMap<String, Object> propertiesMap = new Gson().fromJson(properties.toString(),
								HashMap.class);
						subnetVH.setIpConfigurations((List<Map<String, Object>>) propertiesMap.get("ipConfigurations"));
						subnetVH.setAddressPrefix(propertiesMap.get("addressPrefix").toString());
						subnetVH.setPrivateLinkServiceNetworkPolicies(
								propertiesMap.get("privateLinkServiceNetworkPolicies").toString());
						subnetVH.setProvisioningState(propertiesMap.get("provisioningState").toString());
						subnetVH.setPrivateEndpointNetworkPolicies(
								propertiesMap.get("privateEndpointNetworkPolicies").toString());

					}
					subnetList.add(subnetVH);
				}
			} catch (Exception e) {
				log.error(" Error fetching subnets for network inventory  {} Cause : {}", network.name(),
						e.getMessage());

			}
		}
		log.info("Target Type : {}  Total: {} ","Subnet",subnetList.size());
		return subnetList;
	}

}
