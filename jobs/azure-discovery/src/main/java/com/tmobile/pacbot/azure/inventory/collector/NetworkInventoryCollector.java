package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.NetworkVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class NetworkInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(NetworkInventoryCollector.class);
	
	public List<NetworkVH> fetchNetworkDetails(SubscriptionVH subscription, Map<String, Map<String, String>> tagMap) {
		List<NetworkVH> networkList = new ArrayList<>();

		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<Network> networks = azure.networks().list();

		for (Network network : networks) {
			NetworkVH networkVH = new NetworkVH();
			networkVH.setAddressSpaces(network.addressSpaces());
			networkVH.setDdosProtectionPlanId(network.ddosProtectionPlanId());
			networkVH.setDnsServerIPs(network.dnsServerIPs());
			networkVH.setHashCode(network.hashCode());
			networkVH.setId(network.id());
			networkVH.setDdosProtectionEnabled(network.isDdosProtectionEnabled());
			networkVH.setVmProtectionEnabled(network.isVmProtectionEnabled());
			networkVH.setKey(network.key());
			networkVH.setName(network.name());
			networkVH.setRegion(network.region().name());
			networkVH.setResourceGroupName(network.resourceGroupName());
			networkVH.setTags(Util.tagsList(tagMap, network.resourceGroupName(), network.tags()));
			networkVH.setSubscription(subscription.getSubscriptionId());
			networkVH.setSubscriptionName(subscription.getSubscriptionName());
			networkList.add(networkVH);
		}
		log.info("Target Type : {}  Total: {} ","vnet",networkList.size());
		return networkList;
	}
}
