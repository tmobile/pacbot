package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class RecommendationVH extends AzureVH{
	
	private Map<String,Object> recommendation;
	
	public Map<String, Object> getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(Map<String, Object> recommendation) {
		this.recommendation = recommendation;
	}

}
