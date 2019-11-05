package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.PolicyDefinition;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.PolicyDefinitionVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class PolicyDefinitionInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(PolicyDefinitionInventoryCollector.class);
	
	public List<PolicyDefinitionVH> fetchPolicyDefinitionDetails(SubscriptionVH subscription) {
		List<PolicyDefinitionVH> policyDefinitionList = new ArrayList<>();
		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<PolicyDefinition> policyDefinitions = azure.policyDefinitions().list();
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
		log.info("Target Type : {}  Total: {} ","Policy Defintion",policyDefinitionList.size());
		return policyDefinitionList;
	}
}
