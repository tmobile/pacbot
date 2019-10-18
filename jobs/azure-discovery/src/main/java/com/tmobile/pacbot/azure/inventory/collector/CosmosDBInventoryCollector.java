package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.cosmosdb.VirtualNetworkRule;
import com.tmobile.pacbot.azure.inventory.vo.CosmosDBVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacbot.azure.inventory.vo.VirtualNetworkRuleVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class CosmosDBInventoryCollector {

	public List<CosmosDBVH> fetchCosmosDBDetails(SubscriptionVH subscription, Map<String, Map<String, String>> tagMap) {
		List<CosmosDBVH> cosmosDBList = new ArrayList<CosmosDBVH>();
		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<CosmosDBAccount> CosmosDB = azure.cosmosDBAccounts().list();
		System.out.println(CosmosDB.size());
		for (CosmosDBAccount cosmosDB : CosmosDB) {
			CosmosDBVH cosmosDBVH = new CosmosDBVH();
			cosmosDBVH.setSubscription(subscription.getSubscriptionId());
			cosmosDBVH.setSubscriptionName(subscription.getSubscriptionName());
			cosmosDBVH.setId(cosmosDB.id());
			cosmosDBVH.setKey(cosmosDB.key());
			cosmosDBVH.setName(cosmosDB.name());
			cosmosDBVH.setResourceGroupName(cosmosDB.resourceGroupName());
			cosmosDBVH.setRegion(cosmosDB.regionName());
			cosmosDBVH.setTags(Util.tagsList(tagMap, cosmosDB.resourceGroupName(), cosmosDB.tags()));
			cosmosDBVH.setType(cosmosDB.type());
			cosmosDBVH.setIpRangeFilter(cosmosDB.ipRangeFilter());
			cosmosDBVH.setMultipleWriteLocationsEnabled(cosmosDB.multipleWriteLocationsEnabled());
			cosmosDBVH.setVirtualNetworkRuleList(getVirtualNetworkRule(cosmosDB.virtualNetworkRules()));
			cosmosDBList.add(cosmosDBVH);
			/*
			 * boolean flag = false; Map<String, String> tagsFinal = new HashMap<String,
			 * String>();
			 * 
			 * for (Map.Entry<String, Map<String, String>> resourceGroupTag :
			 * tagMap.entrySet()) {
			 * 
			 * if (resourceGroupTag.getKey().equalsIgnoreCase(cosmosDB.resourceGroupName()))
			 * { flag = true; tagsFinal.putAll(resourceGroupTag.getValue());
			 * tagsFinal.putAll(cosmosDB.tags()); break; }
			 * 
			 * } if (flag == true) { cosmosDBVH.setTags(tagsFinal); } else {
			 * cosmosDBVH.setTags(cosmosDB.tags()); }
			 */

		}
		return cosmosDBList;
	}

	private List<VirtualNetworkRuleVH> getVirtualNetworkRule(List<VirtualNetworkRule> virtualNetworkRuleList) {
		List<VirtualNetworkRuleVH> virtualNetworkRuleVHlist = new ArrayList<>();
		for (VirtualNetworkRule virtualNetworkRule : virtualNetworkRuleList) {
			VirtualNetworkRuleVH virtualNetworkRuleVH = new VirtualNetworkRuleVH();
			virtualNetworkRuleVH.setId(virtualNetworkRule.id());
			virtualNetworkRuleVH
					.setIgnoreMissingVNetServiceEndpoint(virtualNetworkRule.ignoreMissingVNetServiceEndpoint());
			virtualNetworkRuleVHlist.add(virtualNetworkRuleVH);

		}
		return virtualNetworkRuleVHlist;

	}

}
