package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.tmobile.pacbot.azure.inventory.vo.ResourceGroupVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class ResourceGroupInventoryCollector {
	public List<ResourceGroupVH> fetchResourceGroupDetails(SubscriptionVH subscription) {
		List<ResourceGroupVH> resourceGroupList = new ArrayList<ResourceGroupVH>();
		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<ResourceGroup> resourceGroups = azure.resourceGroups().list();
		System.out.println(resourceGroups.size());
		for (ResourceGroup resourceGroup : resourceGroups) {
			ResourceGroupVH resourceGroupVH = new ResourceGroupVH();
			resourceGroupVH.setSubscription(subscription.getSubscriptionId());
			resourceGroupVH.setSubscriptionName(subscription.getSubscriptionName());
			resourceGroupVH.setId(resourceGroup.id());
			resourceGroupVH.setResourceGroupName(resourceGroup.name());
			resourceGroupVH.setKey(resourceGroup.key());
			resourceGroupVH.setType(resourceGroup.type());
			resourceGroupVH.setProvisioningState(resourceGroup.provisioningState());
			resourceGroupVH.setRegionName(resourceGroup.regionName());
			resourceGroupVH.setTags(resourceGroup.tags());
			resourceGroupList.add(resourceGroupVH);
		}
		return resourceGroupList;
	}

}
