package com.tmobile.cloud.azurerules.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;

public class CommonUtils {
	
	/**
	 * create annotation for a given resource
	 * @param ruleParam
	 * @param resourceAttributes
	 * @param publicRanges
	 * @param description, LinkedHashMap<String, Object> issue 
	 * @return
	 */
	public static Annotation buildAnnotationForAzure(Map<String, String> ruleParam, Map<String, String> resourceAttributes,
			String description, LinkedHashMap<String, Object> issue, Map<String, String> additionalParams) {
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        
		String subscription = resourceAttributes.get(PacmanRuleConstants.SUBSCRIPTION);
    	String subscriptionName = resourceAttributes.get(PacmanRuleConstants.AZURE_SUBSCRIPTION_NAME);
		String tenant = resourceAttributes.get(PacmanRuleConstants.TENANT);
		String resourceGroup = resourceAttributes.get(PacmanRuleConstants.RESOURCE_GROUP_NAME);
		
		Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
		annotation.put(PacmanSdkConstants.DESCRIPTION, description);
		annotation.put(PacmanRuleConstants.SEVERITY, severity);
		annotation.put(PacmanRuleConstants.CATEGORY, category);
		annotation.put(PacmanSdkConstants.TENANT, tenant);
		annotation.put(PacmanSdkConstants.SUBSCRIPTION, subscription);
		annotation.put(PacmanSdkConstants.SUBSCRIPTION_NAME, subscriptionName);
		annotation.put(PacmanSdkConstants.RESOURCE_GROUP_NAME, resourceGroup);
		if(additionalParams!=null) {
			annotation.putAll(additionalParams);
		}
		
		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		issueList.add(issue);
		annotation.put("issueDetails", issueList.toString());
		return annotation;
	}

}
