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
import com.microsoft.azure.management.network.LoadBalancer;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.LoadBalancerVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class LoadBalancerInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(LoadBalancerInventoryCollector.class);
	
	public List<LoadBalancerVH> fetchLoadBalancerDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {
		List<LoadBalancerVH> loadBalancerList = new ArrayList<>();

		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<LoadBalancer> loadBalancers = azure.loadBalancers().list();
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
		log.info("Target Type : {}  Total: {} ","LoadBalancer",loadBalancerList.size());
		return loadBalancerList;
	}

}
