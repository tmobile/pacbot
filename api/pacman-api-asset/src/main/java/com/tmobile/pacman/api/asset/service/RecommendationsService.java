package com.tmobile.pacman.api.asset.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.repository.RecommendationsRepository;
import com.tmobile.pacman.api.commons.exception.DataException;

@Service
public class RecommendationsService {
	
	@Autowired
	RecommendationsRepository recommendationsRepository;
	@Autowired
	AssetService assetService;

	public List<Map<String,Object>> getRecommendationSummary(String assetGroup, String application, Boolean general) throws DataException {
		if(general) {
			List<String> providerList = assetService.getProvidersForAssetGroup(assetGroup);
			return recommendationsRepository.getGeneralRecommendationSummary(providerList);
		} else {
			return recommendationsRepository.getRecommendationSummary(assetGroup,application);
		}
	}

	public Map<String,Object> getSummaryByApplication(String assetGroup, String category) throws DataException {
		
		if(StringUtils.isBlank(category)) {
			return recommendationsRepository.getSummaryByApplication(assetGroup);
		} else {
			return recommendationsRepository.getSummaryByApplication(assetGroup, category);
		}
		
	}

	public Map<String,Object> getRecommendations(String assetGroup, String category, String application, String general) throws DataException {
		if(general.equals(AssetConstants.FALSE)) {
			return recommendationsRepository.getRecommendations(assetGroup, category, application);
		} else {
			List<String> providerList = assetService.getProvidersForAssetGroup(assetGroup);
			return recommendationsRepository.getGeneralRecommendations(category, providerList);
		}
	}

	public Map<String,Object> getRecommendationDetail(String assetGroup, String recommendationId, String application, String general) throws DataException {
		if(general.equals(AssetConstants.FALSE)) {
			return recommendationsRepository.getRecommendationDetail(assetGroup, recommendationId,application);
		} else {
			return recommendationsRepository.getGeneralRecommendationDetail(recommendationId);
		}
	}

	public Map<String,Object> getRecommendationInfo(String recommendationId) throws DataException {
		
		String description = recommendationsRepository.getRecommendation(recommendationId).get("checkdescription").toString();
		String[] description1 = description.split("Alert Criteria");
		String[] description2 = description1[1].split("Recommended Action");
		Map<String,Object> recommendationInfo = new HashMap<>();
		recommendationInfo.put("summary", description1[0].replace("<br>", "").replace("[NL]", "").replace("<b>", "").replace("</b>", ""));
		recommendationInfo.put("alert criteria", description2[0].replace("<br>", "").replace("[NL]", "").replace("<b>", "").replace("</b>", ""));
		recommendationInfo.put("recommended action", description2[1].substring(12,description2[1].length()-1).replace("[NL]", "<br />"));
		return recommendationInfo;
	}
}
