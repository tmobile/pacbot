package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.tmobile.pacbot.azure.inventory.vo.NetworkVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class NetworkInventoryCollector {

	public List<NetworkVH> fetchNetworkDetails(SubscriptionVH subscription, Map<String, Map<String, String>> tagMap) {
		List<NetworkVH> networkList = new ArrayList<NetworkVH>();

		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
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

		return networkList;
	}
}
