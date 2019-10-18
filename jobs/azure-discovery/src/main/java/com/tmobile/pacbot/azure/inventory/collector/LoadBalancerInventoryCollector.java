package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.LoadBalancer;
import com.tmobile.pacbot.azure.inventory.vo.LoadBalancerVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class LoadBalancerInventoryCollector {

	public List<LoadBalancerVH> fetchLoadBalancerDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {
		List<LoadBalancerVH> loadBalancerList = new ArrayList<LoadBalancerVH>();

		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<LoadBalancer> loadBalancers = azure.loadBalancers().list();
		System.out.println(loadBalancers.size());
		for (LoadBalancer loadBalancer : loadBalancers) {
			LoadBalancerVH loadBalancerVH = new LoadBalancerVH();
			loadBalancerVH.setHashCode(loadBalancer.hashCode());
			loadBalancerVH.setId(loadBalancer.id());
			loadBalancerVH.setKey(loadBalancer.key());
			loadBalancerVH.setPublicIPAddressIds(loadBalancer.publicIPAddressIds());
			loadBalancerVH.setName(loadBalancer.name());
			loadBalancerVH.setRegionName(loadBalancer.regionName());
			loadBalancerVH.setResourceGroupName(loadBalancer.resourceGroupName());
			loadBalancerVH.setTags(Util.tagsList(tagMap, loadBalancer.resourceGroupName(), loadBalancer.tags()));
			loadBalancerVH.setType(loadBalancer.type());
			loadBalancerVH.setSubscription(subscription.getSubscriptionId());
			loadBalancerVH.setSubscriptionName(subscription.getSubscriptionName());
			loadBalancerList.add(loadBalancerVH);

		}

		return loadBalancerList;
	}

}
