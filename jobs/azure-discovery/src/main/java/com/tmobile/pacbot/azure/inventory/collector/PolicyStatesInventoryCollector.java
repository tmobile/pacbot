package com.tmobile.pacbot.azure.inventory.collector;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.PolicyDefinitionVH;
import com.tmobile.pacbot.azure.inventory.vo.PolicyStatesVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class PolicyStatesInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(PolicyStatesInventoryCollector.class);
	
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.PolicyInsights/policyStates/latest/queryResults?api-version=2018-04-04";

	public List<PolicyStatesVH> fetchPolicyStatesDetails(SubscriptionVH subscription,
			List<PolicyDefinitionVH> policyDefinitionList) throws Exception {

		List<PolicyStatesVH> policyStatesList = new ArrayList<>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());

		String url = String.format(apiUrlTemplate, URLEncoder.encode(subscription.getSubscriptionId()));
		try {

			String response = CommonUtils.doHttpPost(url, "Bearer", accessToken);
			JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
			JsonArray policyStatesObjects = responseObj.getAsJsonArray("value");
			for (JsonElement policyStatesElement : policyStatesObjects) {
				PolicyStatesVH policyStatesVH = new PolicyStatesVH();
				JsonObject policyStatesObject = policyStatesElement.getAsJsonObject();
				PolicyDefinitionVH PolicyDefinitionVH = policyDefinitionList.stream()
						.filter(policyDefinitionObj -> policyDefinitionObj.getName()
								.equals(policyStatesObject.get("policyDefinitionName").getAsString()))
						.findFirst().get();
				policyStatesVH.setPolicyDescription(PolicyDefinitionVH.getDescription());
				policyStatesVH.setPolicyName(PolicyDefinitionVH.getDisplayName());
				policyStatesVH.setPolicyType(PolicyDefinitionVH.getPolicyType());
				policyStatesVH.setPolicyRule(PolicyDefinitionVH.getPolicyRule());
				policyStatesVH.setTimestamp(policyStatesObject.get("timestamp").getAsString());
				policyStatesVH.setId(policyStatesObject.get("policyDefinitionName").getAsString()+"_"+policyStatesObject.get("resourceId").getAsString().toLowerCase());
				policyStatesVH.setResourceId(Util.removeFirstSlash(policyStatesObject.get("resourceId").getAsString()));
				policyStatesVH.setResourceIdLower(Util.removeFirstSlash(policyStatesObject.get("resourceId").getAsString().toLowerCase()));

				policyStatesVH.setPolicyAssignmentId(policyStatesObject.get("policyAssignmentId").getAsString());
				policyStatesVH.setPolicyDefinitionId(policyStatesObject.get("policyDefinitionId").getAsString());
				policyStatesVH.setEffectiveParameters(policyStatesObject.get("effectiveParameters").getAsString());
				policyStatesVH.setIsCompliant(policyStatesObject.get("isCompliant").getAsBoolean());
				policyStatesVH.setSubscriptionId(policyStatesObject.get("subscriptionId").getAsString());
				policyStatesVH.setResourceType(policyStatesObject.get("resourceType").getAsString());
				policyStatesVH.setResourceLocation(policyStatesObject.get("resourceLocation").getAsString());
				policyStatesVH.setResourceGroup(policyStatesObject.get("resourceGroup").getAsString());
				policyStatesVH.setResourceTags(policyStatesObject.get("resourceTags").getAsString());
				policyStatesVH.setPolicyAssignmentName(policyStatesObject.get("policyAssignmentName").getAsString());
				policyStatesVH.setPolicyAssignmentOwner(policyStatesObject.get("policyAssignmentOwner").getAsString());
				policyStatesVH.setPolicyAssignmentParameters(
						policyStatesObject.get("policyAssignmentParameters").getAsString());
				policyStatesVH.setPolicyAssignmentScope(policyStatesObject.get("policyAssignmentScope").getAsString());
				policyStatesVH.setPolicyDefinitionName(policyStatesObject.get("policyDefinitionName").getAsString());
				policyStatesVH
						.setPolicyDefinitionAction(policyStatesObject.get("policyDefinitionAction").getAsString());
				policyStatesVH
						.setPolicyDefinitionCategory(policyStatesObject.get("policyDefinitionCategory").getAsString());
				policyStatesVH.setPolicySetDefinitionId(policyStatesObject.get("policySetDefinitionId").getAsString());
				policyStatesVH
						.setPolicySetDefinitionName(policyStatesObject.get("policySetDefinitionName").getAsString());
				policyStatesVH
						.setPolicySetDefinitionOwner(policyStatesObject.get("policySetDefinitionOwner").getAsString());
				policyStatesVH.setPolicySetDefinitionCategory(
						policyStatesObject.get("policySetDefinitionCategory").getAsString());
				policyStatesVH.setPolicySetDefinitionParameters(
						policyStatesObject.get("policySetDefinitionParameters").getAsString());
				policyStatesVH.setManagementGroupIds(policyStatesObject.get("managementGroupIds").getAsString());
				policyStatesVH.setPolicyDefinitionReferenceId(
						policyStatesObject.get("policyDefinitionReferenceId").getAsString());

				policyStatesVH.setSubscription(subscription.getSubscriptionId());
				policyStatesVH.setSubscriptionName(subscription.getSubscriptionName());

				policyStatesList.add(policyStatesVH);
			}
		} catch (Exception e) {
			log.error("Error collleting Policy States",e);
		}

		log.info("Target Type : {}  Total: {} ","Policy States",policyStatesList.size());
		return policyStatesList;
	}

}
