package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.ResourceGroupVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class ResourceGroupInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(ResourceGroupInventoryCollector.class);
	
	public List<ResourceGroupVH> fetchResourceGroupDetails(SubscriptionVH subscription) {
		List<ResourceGroupVH> resourceGroupList = new ArrayList<>();
		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<ResourceGroup> resourceGroups = azure.resourceGroups().list();
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
		log.info("Target Type : {}  Total: {} ","ResourceGroup",resourceGroupList.size());
		return resourceGroupList;
	}

}
