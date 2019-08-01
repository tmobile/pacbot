package com.tmobile.pacman.api.asset.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.Request;
import com.tmobile.pacman.api.asset.domain.ResponseWithCount;
import com.tmobile.pacman.api.asset.service.RecommendationsService;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class RecommendationsController {

	@Autowired
	RecommendationsService recommendationsService;
	
	private static final Log LOGGER = LogFactory.getLog(RecommendationsController.class);
	
	@GetMapping(value = "/v1/recommendations/summary")
    public ResponseEntity<Object> getRecommendationSummary(@RequestParam(name = "ag", required = false) String assetGroup, 
    		@RequestParam(name = "application", required = false) String application, @RequestParam(name = "general", required = true) Boolean general) {
		
		if(!general && StringUtils.isBlank(assetGroup)) {
			return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
		}
		
		try {
			return ResponseUtils.buildSucessResponse(recommendationsService.getRecommendationSummary(assetGroup,application,general));
		} catch (DataException e) {
			LOGGER.error("Error in getRecommendationSummary "+ e);
			return ResponseUtils.buildFailureResponse(e);
		}
    }
	
	@GetMapping(value = "/v1/recommendations/summaryByApplication")
    public ResponseEntity<Object> getSummaryByApplication(@RequestParam(name = "ag", required = true) String assetGroup, 
    		@RequestParam(name = "category", required = false) String category) {
		try {
			return ResponseUtils.buildSucessResponse(recommendationsService.getSummaryByApplication(assetGroup,category));
		} catch (DataException e) {
			LOGGER.error("Error in getSummaryByApplication "+ e);
			return ResponseUtils.buildFailureResponse(e);
		}
    }
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/v1/recommendations")
    public ResponseEntity<Object> getRecommendations(@RequestBody(required = true) Request request) {
		
		Map<String, String> filter = request.getFilter();
		if (filter == null) {
            filter = new HashMap<>();
        }
		
		List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,AssetConstants.FILTER_CATEGORY,AssetConstants.FILTER_GENERAL);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }
        
        if (!filter.containsKey(AssetConstants.FILTER_CATEGORY)) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.FILTER_CATEGORY+" is mandatory in filter"));
        }
		
		if(!filter.containsKey(AssetConstants.FILTER_GENERAL)) {
			return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.FILTER_GENERAL+" is mandatory in filter"));
		}
		
		String general = filter.get(AssetConstants.FILTER_GENERAL);
		String assetGroup = request.getAg();
		if (general.equals(AssetConstants.FALSE) && StringUtils.isBlank(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));
        }

        String searchText = request.getSearchtext();
        String category = filter.get(AssetConstants.FILTER_CATEGORY);
        String application = filter.get(AssetConstants.FILTER_APPLICATION);
        
		Map<String, Object> recommendations;
		try {
			recommendations = recommendationsService.getRecommendations(assetGroup, category, application, general);
		} catch (DataException e) {
			LOGGER.error("Error in getRecommendations "+ e);
			return ResponseUtils.buildFailureResponse(e);
		}
		List<Map<String, Object>> masterList = (List<Map<String, Object>>) recommendations.get("response");
		List<Map<String, Object>> masterDetailList = (List<Map<String, Object>>) CommonUtils
                .filterMatchingCollectionElements(masterList, searchText, true);
		
		if(masterDetailList.isEmpty()) {
			recommendations.put("response", new ArrayList<>());
	        recommendations.put("total", 0);
			
	        return ResponseUtils.buildSucessResponse(recommendations);
		}
		
		if (from >= masterDetailList.size()) {
	        return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_EXCEEDS));
	    }
		
        int endIndex = 0;

        if (size == 0) {
            size = masterDetailList.size();
        }

        if ((from + size) > masterDetailList.size()) {
            endIndex = masterDetailList.size();
        } else {
            endIndex = from + size;
        }

        List<Map<String, Object>> subDetailList = masterDetailList.subList(from, endIndex);
        recommendations.put("response", subDetailList);
        recommendations.put("total", masterDetailList.size());
		
        return ResponseUtils.buildSucessResponse(recommendations);
    }
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/v1/recommendations/detail")
    public ResponseEntity<Object> getRecommendationDetail(@RequestBody(required = true) Request request) {
		
		Map<String, String> filter = request.getFilter();
		if (filter == null) {
            filter = new HashMap<>();
        }
		
		List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,AssetConstants.FILTER_RECOMMENDATION_ID,AssetConstants.FILTER_GENERAL);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }
        
        if (!filter.containsKey(AssetConstants.FILTER_RECOMMENDATION_ID)) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.FILTER_RECOMMENDATION_ID+" is mandatory in filter"));
        }
		
		if(!filter.containsKey(AssetConstants.FILTER_GENERAL)) {
			return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.FILTER_GENERAL+" is mandatory in filter"));
		}
		
		String general = filter.get(AssetConstants.FILTER_GENERAL);
		String assetGroup = request.getAg();
		if (general.equals(AssetConstants.FALSE) && StringUtils.isBlank(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));
        }

        String searchText = request.getSearchtext();
        String recommendationId = filter.get(AssetConstants.FILTER_RECOMMENDATION_ID);
        String application = filter.get(AssetConstants.FILTER_APPLICATION);
		Map<String, Object> recommendationDetails;
		try {
			recommendationDetails = recommendationsService.getRecommendationDetail(assetGroup,recommendationId,application,general);
		} catch (DataException e) {
			LOGGER.error("Error in getRecommendationDetail "+ e);
			return ResponseUtils.buildFailureResponse(e);
		}
		
		List<Map<String, Object>> masterList = (List<Map<String, Object>>) recommendationDetails.get("resources");
        return formResponseWithCount(masterList, from, size, searchText);
    }
	
	@GetMapping(value = "/v1/recommendations/info")
    public ResponseEntity<Object> getRecommendationInfo(@RequestParam(name = "recommendationId", required = true) String recommendationId) {
		try {
			return ResponseUtils.buildSucessResponse(recommendationsService.getRecommendationInfo(recommendationId));
		} catch (DataException e) {
			LOGGER.error("Error in getRecommendationInfo "+ e);
			return ResponseUtils.buildFailureResponse(e);
		}
    }
	
	/**
     * Method returns the list with count based on the from and size.
     * 
     * @param masterList
     * @param from
     * @param size
     * @param searchText
     * 
     * @return ResponseEntity 
     */
    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> formResponseWithCount(List<Map<String, Object>> masterList, int from, int size,
            String searchText) {
        try {
            List<Map<String, Object>> masterDetailList = (List<Map<String, Object>>) CommonUtils
                    .filterMatchingCollectionElements(masterList, searchText, true);
            if (masterDetailList.isEmpty()) {
                return ResponseUtils
                        .buildSucessResponse(new ResponseWithCount(new ArrayList<Map<String, Object>>(), 0));
            }

            if (from >= masterDetailList.size()) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_EXCEEDS));
            }

            int endIndex = 0;

            if (size == 0) {
                size = masterDetailList.size();
            }

            if ((from + size) > masterDetailList.size()) {
                endIndex = masterDetailList.size();
            } else {
                endIndex = from + size;
            }

            List<Map<String, Object>> subDetailList = masterDetailList.subList(from, endIndex);
            return ResponseUtils.buildSucessResponse(new ResponseWithCount(subDetailList, masterDetailList.size()));
        } catch (Exception e) {
            LOGGER.error("Exception in formResponseWithCount ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
    }
}
