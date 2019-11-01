package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.resources.PolicyDefinition;
import com.tmobile.pacbot.azure.inventory.vo.PolicyDefinitionVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class PolicyDefinitionInventoryCollector {
	public List<PolicyDefinitionVH> fetchPolicyDefinitionDetails(SubscriptionVH subscription) {
		List<PolicyDefinitionVH> policyDefinitionList = new ArrayList<PolicyDefinitionVH>();
		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<PolicyDefinition> policyDefinitions = azure.policyDefinitions().list();
		System.out.println(policyDefinitions.size());
		for (PolicyDefinition policyDefinition : policyDefinitions) {
			PolicyDefinitionVH policyDefinitionVH = new PolicyDefinitionVH();
			policyDefinitionVH.setId(policyDefinition.id());
			policyDefinitionVH.setName(policyDefinition.name());
			policyDefinitionVH.setDescription(policyDefinition.description());
			policyDefinitionVH.setDisplayName(policyDefinition.displayName());
			policyDefinitionVH.setPolicyType(policyDefinition.policyType().toString());
			policyDefinitionVH.setPolicyRule(policyDefinition.policyRule().toString());
			policyDefinitionVH.setSubscription(subscription.getSubscriptionId());
			policyDefinitionVH.setSubscriptionName(subscription.getSubscriptionName());
			policyDefinitionList.add(policyDefinitionVH);
		}
		return policyDefinitionList;
	}
}
