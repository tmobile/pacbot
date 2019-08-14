package com.tmobile.cso.pacbot.recommendation.entity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.tmobile.cso.pacbot.recommendation.dao.RDSDBManager;
import com.tmobile.cso.pacbot.recommendation.es.ESManager;
import com.tmobile.cso.pacbot.recommendation.util.Constants;
import com.tmobile.cso.pacbot.recommendation.util.Util;

/**
 * The Class RecommendationCollector.
 */
public class RecommendationCollector implements Constants {
	
	/** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationCollector.class);
    
    /** The Constant DATE_FORMAT. */
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    
    /** The Constant CURR_DATE. */
    private static final String CURR_DATE = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

    /**
     * Upload recommendation data.
     *
     * @return the list
     */
    public List<Map<String,String>> uploadRecommendationData() {
    	List<Map<String,String>> errorList = new ArrayList<>();
    	
    	Map<String, Map<String, String>> recommendationMappings = RDSDBManager.executeQuery("select * FROM Recommendation_Mappings")
                .stream().collect(Collectors.toMap(map -> (String) map.get("checkId"), map -> map));
    	
    	List<Map<String, String>> resources = ESManager.getResourcesInfo("aws_checks", "checks_resources");
    	Map<String, Map<String, String>> checks = ESManager.getChecksInfo("aws_checks","checks",Arrays.asList(Constants.CHECKNAME,Constants.CHECKID,Constants.ACCOUNTID,Constants.CHECKCATEGORY));
    	
    	ObjectMapper mapper = new ObjectMapper();
    	List<String> errors = new ArrayList<>();
    	Map<String,List<String>> parentTypes = new HashMap<>();
    	List<Map<String,Object>> recommendationsWithResourceId = new ArrayList<>();
    	List<Map<String,Object>> recommendationsWithoutResourceId = new ArrayList<>();
    	for(Map<String, String> resource : resources) {
			Map<String,String> check = checks.get(resource.get(Constants.CHECKID)+"_"+resource.get(Constants.ACCOUNTID));
    		Map<String, Object> resourceInfo = new HashMap<>();
    		try {
				resourceInfo = mapper.readValue(resource.get(Constants.RESOURCE_INFO), new TypeReference<Map<String, Object>>() {});
			} catch (IOException e) {
				LOGGER.error("Error in fetching resource info "+e);
			}
    		
    		if(!resourceInfo.isEmpty()) {
    			Map<String,String> recommendationMapping = recommendationMappings.get(resource.get(Constants.CHECKID));
        		if(null !=recommendationMapping && !recommendationMapping.isEmpty()) {
        			formRecommendationsWithResourceId(recommendationsWithResourceId, recommendationMapping, check, 
        						resourceInfo, resource, parentTypes);
        		} else {
    				formRecommendationsWithoutResourceId(recommendationsWithoutResourceId, check, resourceInfo, resource);
        		}
    		}
    	}
    	Map<String, String> parentInfo = new HashMap<>(); 
    	for(Entry<String, List<String>> parentType :parentTypes.entrySet()) {
    		String query = "SELECT DISTINCT _resourceId FROM Recommendation_Mappings WHERE TYPE = '"+ parentType.getKey()+ "'";
			parentInfo.putAll(ESManager.getParentInfo(parentType.getKey(), parentType.getValue(),RDSDBManager.executeQuery(query).get(0).get("_resourceId")));
    	}
    	uploadRecommendationsWithResourceIds(recommendationsWithResourceId, parentInfo, errors);
    	uploadRecommendationsWithoutResourceIds(recommendationsWithoutResourceId, errors);
    	
    	for(String parentType : parentTypes.keySet()) {
    		ESManager.deleteOldDocuments("aws_"+parentType, Constants.RECOMMENDATION, "_loaddate.keyword", CURR_DATE);
    		ESManager.updateLatestStatus("aws_"+parentType, Constants.RECOMMENDATION, "_loaddate.keyword", CURR_DATE);
    	}
    	ESManager.deleteOldDocuments(Constants.GLOBAL_RECOMMENDATIONS,Constants.RECOMMENDATION, Constants.LOAD_DATE,CURR_DATE);
    	ESManager.updateLatestStatus(Constants.GLOBAL_RECOMMENDATIONS,Constants.RECOMMENDATION, Constants.LOAD_DATE,CURR_DATE);
    	return errorList;
    }
    
    /**
     * Group resource id by type.
     *
     * @param parentTypes the parent types
     * @param type the type
     * @param resourceId the resource id
     */
    private void groupResourceIdByType(Map<String,List<String>> parentTypes, String type, String resourceId) {
    	if(parentTypes.containsKey(type)) {
    		if(!parentTypes.get(type).contains(resourceId)) {
    			parentTypes.get(type).add(resourceId);
    		}
    	} else {
    		List<String> resourceIds = new ArrayList<>();
    		resourceIds.add(resourceId);
    		parentTypes.put(type, resourceIds);
    	}
    }
    
    /**
     * Form recommendations with resource id.
     *
     * @param recommendationsWithResourceId the recommendations with resource id
     * @param recommendationMapping the recommendation mapping
     * @param check the check
     * @param resourceInfo the resource info
     * @param resource the resource
     * @param parentTypes the parent types
     */
    private void formRecommendationsWithResourceId(List<Map<String,Object>> recommendationsWithResourceId,Map<String,String> recommendationMapping,
    		Map<String,String> check, Map<String, Object> resourceInfo,Map<String, String> resource,Map<String,List<String>> parentTypes) {
    	
    	Map<String,Object> recommendationObj = new HashMap<>();
    	String type = recommendationMapping.get("type");
		resourceInfo.put(recommendationMapping.get("_resourceId"), resourceInfo.get(recommendationMapping.get("resourceInfo")));
		resourceInfo.remove(recommendationMapping.get("resourceInfo"));
		try {
			String resourceId = resourceInfo.get(recommendationMapping.get("_resourceId")).toString().split(" ")[0];
    		recommendationObj.putAll(resource);
    		recommendationObj.remove(Constants.CHECKID);
    		recommendationObj.put(Constants.RESOURCE_INFO,resourceInfo);
    		recommendationObj.put("_resourceid", resourceId);
    		recommendationObj.put(Constants.LOAD_DATE,CURR_DATE);
    		recommendationObj.put("latest",false);
    		if(!Strings.isNullOrEmpty(recommendationMapping.get(Constants.MONTHLY_SAVINGS_FIELD)) && resourceInfo.containsKey(recommendationMapping.get(Constants.MONTHLY_SAVINGS_FIELD))) {
        		recommendationObj.put("monthlysavings", Double.valueOf(resourceInfo.get(recommendationMapping.get(Constants.MONTHLY_SAVINGS_FIELD)).toString().replace("-", "").replace("$", "").replace(",", "")));
    		}
    		recommendationObj.put(Constants.RECOMMENDATION_ID,check.get(Constants.CHECKID));
    		recommendationObj.put(Constants.RECOMMENDATION,check.get(Constants.CHECKNAME));
    		recommendationObj.put("category",check.get(Constants.CHECKCATEGORY));
    		recommendationObj.put(Constants.ACCOUNTID,check.get(Constants.ACCOUNTID));
    		recommendationObj.put("type",type);
    		groupResourceIdByType(parentTypes, type, resourceId);
    		recommendationsWithResourceId.add(recommendationObj);
		} catch(Exception e) {
			LOGGER.error("Error in form recommendation info with resource id "+e);
		}
    }
    
    /**
     * Form recommendations without resource id.
     *
     * @param recommendationsWithoutResourceId the recommendations without resource id
     * @param check the check
     * @param resourceInfo the resource info
     * @param resource the resource
     */
    private void formRecommendationsWithoutResourceId(List<Map<String,Object>> recommendationsWithoutResourceId,Map<String,String> check, 
    		Map<String, Object> resourceInfo,Map<String, String> resource) {
    	
    	Map<String,Object> recommendationObj = new HashMap<>();
		try {
    		recommendationObj.putAll(resource);
    		recommendationObj.remove(Constants.CHECKID);
    		recommendationObj.put(Constants.RESOURCE_INFO,resourceInfo);
    		recommendationObj.put(Constants.LOAD_DATE,CURR_DATE);
    		recommendationObj.put("latest",false);
    		recommendationObj.put(Constants.RECOMMENDATION_ID,check.get(Constants.CHECKID));
    		recommendationObj.put(Constants.RECOMMENDATION,check.get(Constants.CHECKNAME));
    		recommendationObj.put("category",check.get(Constants.CHECKCATEGORY));
    		recommendationObj.put(Constants.ACCOUNTID,check.get(Constants.ACCOUNTID));
    		recommendationsWithoutResourceId.add(recommendationObj);
		} catch(Exception e) {
			LOGGER.error("Error in form recommendation info without resource id "+e);
		}
    }
    
    /**
     * Upload recommendations with resource ids.
     *
     * @param recommendationsWithResourceId the recommendations with resource id
     * @param parentInfo the parent info
     * @param errors the errors
     */
    private void uploadRecommendationsWithResourceIds(List<Map<String,Object>> recommendationsWithResourceId, Map<String, String> parentInfo, List<String> errors) {
    	
    	LOGGER.info("Started Uploading for recommendations with resource id {} ",recommendationsWithResourceId.size());
    	String createTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\", \"_parent\" : \"%s\" } }%n";
    	StringBuilder bulkRequest = new StringBuilder();
    	int i=0;
    	for(Map<String,Object> recommendationObj : recommendationsWithResourceId) {
    		String type = recommendationObj.get("type").toString();
    		String parentInfoId = recommendationObj.get("_resourceid")+"_"+recommendationObj.get(Constants.ACCOUNTID);
    		StringBuilder doc = new StringBuilder(ESManager.createESDoc(recommendationObj));
    		if (doc != null) {
    			if(!StringUtils.isNullOrEmpty(parentInfo.get(parentInfoId))) {
    				ESManager.createType("aws_" + type, Constants.RECOMMENDATION, type);
                    bulkRequest.append(String.format(createTemplate,"aws_"+type,Constants.RECOMMENDATION,parentInfo.get(parentInfoId)+"_"+recommendationObj.get(Constants.RECOMMENDATION_ID)+"_"+Math.random(),parentInfo.get(parentInfoId)));
                    bulkRequest.append(doc + "\n");
    			}
            }
    		i++;
    		if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
    			if (bulkRequest.length() > 0) {
	    			LOGGER.info("Uploading {}", i);
	                ESManager.bulkUpload(errors,bulkRequest.toString());
	                bulkRequest = new StringBuilder();
    			}
            }
    	}
    	if (bulkRequest.length() > 0) {
    		LOGGER.info("Uploading {}", i);
            ESManager.bulkUpload(errors,bulkRequest.toString());
        }
    	LOGGER.info("Completed Uploading for recommendations with resource id");
    }
    
	/**
	 * Upload recommendations without resource ids.
	 *
	 * @param recommendationsWithoutResourceId the recommendations without resource id
	 * @param errors the errors
	 */
	private void uploadRecommendationsWithoutResourceIds(List<Map<String,Object>> recommendationsWithoutResourceId, List<String> errors) {
    	
		LOGGER.info("Started Uploading for recommendations without resource id {} ",recommendationsWithoutResourceId.size());
    	List<Map<String, String>> errorList = new ArrayList<>();
    	String createTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\"} }%n";
    	ESManager.createIndex(Constants.GLOBAL_RECOMMENDATIONS, errorList);
    	ESManager.createType(Constants.GLOBAL_RECOMMENDATIONS, Constants.RECOMMENDATION, errorList);
    	StringBuilder bulkRequest = new StringBuilder();
    	int i=0;
    	for(Map<String,Object> recommendationObj : recommendationsWithoutResourceId) {
    		String id = recommendationObj.get(Constants.ACCOUNTID).toString()+recommendationObj.get(Constants.RECOMMENDATION_ID)+Math.random();
    		StringBuilder doc = new StringBuilder(ESManager.createESDoc(recommendationObj));
    		if (doc != null) {
                bulkRequest.append(String.format(createTemplate,Constants.GLOBAL_RECOMMENDATIONS,Constants.RECOMMENDATION,Util.getUniqueID(id)));
                bulkRequest.append(doc + "\n");
            }
    		i++;
    		if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
    			if (bulkRequest.length() > 0) {
    				LOGGER.info("Uploading {}", i);
                    ESManager.bulkUpload(errors,bulkRequest.toString());
                    bulkRequest = new StringBuilder();
    			}
            }
    	}
    	if (bulkRequest.length() > 0) {
    		LOGGER.info("Uploading {}", i);
            ESManager.bulkUpload(errors,bulkRequest.toString());
        }
    	LOGGER.info("Completed Uploading for recommendations without resource id");
    }
}
