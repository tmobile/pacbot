package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.RecommendationVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.utils.CommonUtils;

@Component
public class SCRecommendationsCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	Set<String> policyList = new HashSet<>();
	Set<String> nameList = new HashSet<>();
	Set<String> baseNameList = new HashSet<>();
	private static Logger log = LoggerFactory.getLogger(SCRecommendationsCollector.class);
	private String apiUrlTemplate = "https://management.azure.com/subscriptions/%s/providers/Microsoft.Security/tasks?api-version=2015-06-01-preview";
	public List<RecommendationVH> fetchSecurityCenterRecommendations(SubscriptionVH subscription) {
		List<RecommendationVH> recommendations = new ArrayList<>();
		String accessToken = azureCredentialProvider.getToken(subscription.getTenant());
		String url = String.format(apiUrlTemplate, subscription.getSubscriptionId());
		
		try {
			String response = CommonUtils.doHttpGet(url, "Bearer", accessToken);
			recommendations = filterRecommendationInfo(response,subscription);
		} catch (Exception e) {
			log.error("Error Collecting Security Center Info",e);
		}
		log.info("Target Type : {}  Total: {} ","Security Center",recommendations.size());
		return recommendations;
		
	}
	
	private List<RecommendationVH> filterRecommendationInfo(String response,SubscriptionVH subscription){
		
		List<RecommendationVH> recommendations = new ArrayList<>();
		JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
		JsonArray recommedationObjects = responseObj.getAsJsonArray("value");
		
		for(JsonElement recElmnt : recommedationObjects) {
			JsonObject recommendObject = recElmnt.getAsJsonObject();
			JsonObject properties = recommendObject.getAsJsonObject("properties");
			String id = recommendObject.get("id").getAsString();
			if("Active".equals(properties.get("state").getAsString())){
				JsonObject secTaskParameters = properties.getAsJsonObject("securityTaskParameters");
				//String baseLineName = secTaskParameters.get("baselineName")!=null?secTaskParameters.get("baselineName").getAsString():null;
				String policyName = secTaskParameters.get("policyName")!=null?secTaskParameters.get("policyName").getAsString():null;
				//String name = secTaskParameters.get("name")!=null?secTaskParameters.get("name").getAsString():null;
				String resourceType = secTaskParameters.get("resourceType")!=null?secTaskParameters.get("resourceType").getAsString():"";
		
				if(policyName !=null && "VirtualMachine".equals(resourceType)) {
					
					
					Map<String,Object> recommendationMap = new Gson().fromJson(secTaskParameters, new TypeToken<Map<String, Object>>() {}.getType() );
					Object resourceId = recommendationMap.get("resourceId");
					if(resourceId!=null) {
						RecommendationVH recommendation = new RecommendationVH();
						recommendation.setSubscription(subscription.getSubscriptionId());
						recommendation.setSubscriptionName(subscription.getSubscriptionName());
						recommendationMap.put("resourceId",Util.removeFirstSlash(resourceId.toString()));
						recommendationMap.put("_resourceIdLower",Util.removeFirstSlash(resourceId.toString()).toLowerCase());
						recommendation.setId(id);
						recommendation.setRecommendation(recommendationMap);
						recommendations.add(recommendation);
					}
					
				}
				
			}
		}
	
		return recommendations;
		
	}

}
