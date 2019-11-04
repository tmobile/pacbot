package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.tmobile.pacbot.azure.inventory.vo.PublicIpAddressVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class PublicIpAddressInventoryCollector {

	public List<PublicIpAddressVH> fetchPublicIpAddressDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {

		List<PublicIpAddressVH> publicIpAddressList = new ArrayList<PublicIpAddressVH>();

		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<PublicIPAddress> publicIPAddresses = azure.publicIPAddresses().list();
		System.out.println(publicIPAddresses.size());
		for (PublicIPAddress publicIPAddress : publicIPAddresses) {
			PublicIpAddressVH publicIpAddressVH = new PublicIpAddressVH();
			publicIpAddressVH.setId(publicIPAddress.id());
			publicIpAddressVH.setName(publicIPAddress.name());
			publicIpAddressVH.setResourceGroupName(publicIPAddress.resourceGroupName());
			publicIpAddressVH.setType(publicIPAddress.type());
			publicIpAddressVH
					.setTags(Util.tagsList(tagMap, publicIPAddress.resourceGroupName(), publicIPAddress.tags()));
			publicIpAddressVH.setSubscription(subscription.getSubscriptionId());
			publicIpAddressVH.setSubscriptionName(subscription.getSubscriptionName());
			publicIpAddressVH.setIdleTimeoutInMinutes(publicIPAddress.idleTimeoutInMinutes());
			publicIpAddressVH.setFqdn(publicIPAddress.fqdn());
			publicIpAddressVH.setIpAddress(publicIPAddress.ipAddress());
			publicIpAddressVH.setKey(publicIPAddress.key());
			publicIpAddressVH.setRegionName(publicIPAddress.regionName());
			publicIpAddressVH.setReverseFqdn(publicIPAddress.reverseFqdn());
			publicIpAddressVH.setVersion(publicIPAddress.version().toString());
			publicIpAddressList.add(publicIpAddressVH);

		}

		return publicIpAddressList;
	}

}
