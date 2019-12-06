package com.tmobile.pacman.api.asset.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@Repository
public class RecommendationsRepository {

	@Value("${elastic-search.host}")
    private String esHost;
    @Value("${elastic-search.port}")
    private int esPort;
    private static final String PROTOCOL = "http";
    private String esUrl;
    
    @Value("${recommendation.categories}")
    private String recommendationCategories;
    
    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;
    
    @Autowired
    PacmanRdsRepository rdsRepository;
    
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }
    
    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(RecommendationsRepository.class);
    
    public List<Map<String,Object>> getRecommendationSummary(String assetGroup, String application) throws DataException {
    	
    	List<Map<String,Object>> recommendationSummary = new ArrayList<>();
    	StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(assetGroup).append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}}");
		if(StringUtils.isNotBlank(application)) {
			requestBody.append(",{\"match\":{\"tags.Application.keyword\":\"");
			requestBody.append(application);
			requestBody.append("\"}}");
		}
		requestBody.append("]}},\"aggs\":{\"recommendations\":{\"children\":{\"type\":\"recommendation\"},\"aggs\":{\"latest\":{\"filter\":{\"term\":{\"latest\":\"true\"}},"
				+ "\"aggs\":{\"category\":{\"terms\":{\"field\":\"category.keyword\",\"size\":1000},\"aggs\":{\"savings\":{\"sum\":{\"field\":\"monthlysavings\"}}}}}}}}}}");
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error("Error while fetching recommendation summary from ES ", e);
			throw new DataException(e);
		}
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        boolean dataAvailable = false;
        if(aggregations != null) {
        	JsonObject recommendations = aggregations.get("recommendations").getAsJsonObject();
        	if(recommendations.has("latest") && recommendations.get("latest").getAsJsonObject().has("category")) {
				JsonArray categoryBuckets = recommendations.get("latest").getAsJsonObject().get("category")
						.getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
				if (categoryBuckets.size() > 0) {
					dataAvailable = true;
                    for (int i=0; i<categoryBuckets.size();i++) {
                        JsonObject categoryObj = (JsonObject) categoryBuckets.get(i);
                        if (categoryObj != null) {
                        	Map<String,Object> category = new HashMap<>();
                        	category.put("category", categoryObj.get("key").getAsString());
                        	category.put("recommendations", categoryObj.get("doc_count").getAsLong());
                        	JsonObject savingsObj = categoryObj.get("savings").getAsJsonObject();
                        	if(savingsObj.size() != 0) {
                        		long potentialMonthlySavings = Math.round(savingsObj.get("value").getAsDouble());
                        		if(potentialMonthlySavings > 0) {
                        			category.put("potentialMonthlySavings", potentialMonthlySavings);
                        		}
                        	}
                        	recommendationSummary.add(category);
                        }
                    }
                }
        	}
        	
        }
        if(!dataAvailable) {
    		//for azure there are no recommendations currently, so passing 0 values for azure asset group
    		String[] categories = recommendationCategories.split(",");
    		for(int i=0; i < categories.length; i++) {
    			Map<String,Object> category = new HashMap<>();
            	category.put("category", categories[i]);
            	category.put("recommendations", 0L);
            	recommendationSummary.add(category);
    		}
    	}
        
        return recommendationSummary;
    }
    
    public Map<String,Object> getSummaryByApplication(String assetGroup, String category) throws DataException {
    	
    	Map<String,Object> result = new HashMap<>();
		List<Map<String,Object>> summaryByApplication = new ArrayList<>();
		Double totalMonthlySavings = 0.0;
		
    	StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(assetGroup).append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\":{\"_entity.keyword\":true}}]}},"
				+ "\"aggs\":{\"apps\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":100000},\"aggs\":{\"recommendations\":{\"children\":{\"type\":\"recommendation\"},"
				+ "\"aggs\":{\"latest\":{\"filter\":{\"match\":{\"latest\":true}},\"aggs\":{\"category\":{\"filter\":{\"match\":{\"category.keyword\":\"");
		requestBody.append(category);
		requestBody.append("\"}},\"aggs\":{\"savings\":{\"sum\":{\"field\":\"monthlysavings\"}}}}}}}}}}}}");
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error("Error in getSummaryByApplication "+e);
			throw new DataException(e);
		}
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        JsonArray appsBuckets = aggregations.get("apps").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
        if (appsBuckets.size() > 0) {
            for (int i=0; i<appsBuckets.size();i++) {
            	JsonObject appObj = (JsonObject) appsBuckets.get(i);
                if (appObj != null) {
            		JsonObject recommendationObj = appObj.get("recommendations").getAsJsonObject();
            		if(recommendationObj.has("latest") && recommendationObj.get("latest").getAsJsonObject().has("category")) {
            			Map<String,Object> app = new HashMap<>();
                		app.put("application", appObj.get(Constants.KEY).getAsString());
            			JsonObject categoryObj = recommendationObj.get("latest").getAsJsonObject().get("category").getAsJsonObject();
                		app.put("recommendations", categoryObj.get(Constants.DOC_COUNT).getAsLong());
                		if("cost_optimizing".equals(category)) {
                			Double monthlySavings = categoryObj.get("savings").getAsJsonObject().get(Constants.VALUE).getAsDouble();
                    		totalMonthlySavings += monthlySavings;
                    		app.put("monthlySavings", Math.round(monthlySavings));
                		}
                		if( categoryObj.get(Constants.DOC_COUNT).getAsLong() > 0) {
                			summaryByApplication.add(app);
                		}
            		}
                }
            }
        }
		
		result.put("ag", assetGroup);
		result.put("category", category);
		if("cost_optimizing".equals(category)) {
			result.put("totalMonthlySavings", Math.round(totalMonthlySavings));
		}
		result.put("applications", summaryByApplication);
		return result;
    }

	public Map<String, Object> getSummaryByApplication(String assetGroup) throws DataException {
		
		Map<String,Object> result = new HashMap<>();
		List<Map<String,Object>> summaryByApplication = new ArrayList<>();
		Double totalMonthlySavings = 0.0;
		
    	StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(assetGroup).append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\":{\"_entity.keyword\":true}}]}}"
				+ ",\"aggs\":{\"apps\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":100000},\"aggs\":{\"recommendations\":{\"children\":{\"type\":\"recommendation\"}"
				+ ",\"aggs\":{\"latest\":{\"filter\":{\"match\":{\"latest\":true}},\"aggs\":{\"savings\":{\"sum\":{\"field\":\"monthlysavings\"}}}}}}}}}}");
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error("Error in getSummaryByApplication "+e);
			throw new DataException(e);
		}
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        JsonArray appsBuckets = aggregations.get("apps").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
        if (appsBuckets.size() > 0) {
            for (int i=0; i<appsBuckets.size();i++) {
            	JsonObject appObj = (JsonObject) appsBuckets.get(i);
                if (appObj != null) {
            		JsonObject recommendationObj = appObj.get("recommendations").getAsJsonObject();
            		if(recommendationObj.has("latest") && recommendationObj.get("latest").getAsJsonObject().has("savings")) {
            			Map<String,Object> app = new HashMap<>();
                		app.put("application", appObj.get(Constants.KEY).getAsString());
                		app.put("recommendations", recommendationObj.get(Constants.DOC_COUNT).getAsLong());
                		Double monthlySavings = recommendationObj.get("latest").getAsJsonObject().get("savings").getAsJsonObject().get(Constants.VALUE).getAsDouble();
                		totalMonthlySavings += monthlySavings;
                		app.put("monthlySavings", Math.round(monthlySavings));
                		summaryByApplication.add(app);
            		}
                }
            }
        }
		
		result.put("ag", assetGroup);
		result.put("totalMonthlySavings", Math.round(totalMonthlySavings));
		result.put("applications", summaryByApplication);
		return result;
	}
	
	public Map<String,Object> getRecommendations(String assetGroup, String category, String application) throws DataException {
		
		Map<String,Object> result = new HashMap<>();
		List<Map<String,Object>> recommendations = new ArrayList<>();
		Double totalMonthlySavings = 0.0;
		
		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(assetGroup).append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\":{\"_entity.keyword\":true}}");
		if(StringUtils.isNotBlank(application)) {
			requestBody.append(",{\"match\":{\"tags.Application.keyword\":\"");
			requestBody.append(application);
			requestBody.append("\"}}");
		}
		requestBody.append("]}},\"aggs\":{\"type\":{\"terms\":{\"field\":\"_entitytype.keyword\",\"size\":100000},"
				+ "\"aggs\":{\"recommendations\":{\"children\":{\"type\":\"recommendation\"},\"aggs\":{\"latest\":{\"filter\":{\"match\":{\"latest\":true}},\"aggs\":{\"category\":{\"filter\":{\"match\":{\"category.keyword\":\"");
		requestBody.append(category);
		requestBody.append("\"}},\"aggs\":{\"recommendation\":{\"terms\":{\"field\":\"recommendationId.keyword\",\"size\":10000},"
				+ "\"aggs\":{\"savings\":{\"sum\":{\"field\":\"monthlysavings\"}}}}}}}}}}}}}}");
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error("Error in getRecommendations "+e);
			throw new DataException(e);
		}
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        JsonArray typeBuckets = aggregations.get("type").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
        if (typeBuckets.size() > 0) {
            for (int i=0; i<typeBuckets.size();i++) {
            	JsonObject typeObj = (JsonObject) typeBuckets.get(i);
                if (typeObj != null) {
                	JsonObject recommendationsObj = typeObj.get("recommendations").getAsJsonObject();
            		if(recommendationsObj.has("latest") && recommendationsObj.get("latest").getAsJsonObject().has("category")) {
            			JsonObject categoryObj = recommendationsObj.get("latest").getAsJsonObject().get("category").getAsJsonObject();
            			JsonArray recommendationBuckets =  categoryObj.get("recommendation").getAsJsonObject().getAsJsonArray(Constants.BUCKETS);
            			if(recommendationBuckets.size() > 0) {
            				for (int j=0; j<recommendationBuckets.size();j++) {
            					JsonObject recommendationObj = (JsonObject) recommendationBuckets.get(j);
            					String recommendationId = recommendationObj.get(Constants.KEY).getAsString();
            					Map<String,Object> recommendationInfo = getRecommendation(recommendationId);
            					if(!recommendationInfo.isEmpty()) {
            						Map<String,Object> recommendation = new HashMap<>();
                					recommendation.put("recommendationId", recommendationId);
                					recommendation.put("recommendation", recommendationInfo.get("checkname"));
                					recommendation.put("description", recommendationInfo.get("checkdescription").toString().replace("[NL]", "<br />"));
                					recommendation.put("total", typeObj.get(Constants.DOC_COUNT).getAsLong());
                					recommendation.put("recommended", recommendationObj.get(Constants.DOC_COUNT).getAsLong());
                					recommendation.put("targetType", typeObj.get(Constants.KEY).getAsString());
                					if("cost_optimizing".equals(category)) {
	                					Double monthlySavings = recommendationObj.get("savings").getAsJsonObject().get(Constants.VALUE).getAsDouble();
	                            		totalMonthlySavings += monthlySavings;
	                					recommendation.put("monthlySavings", Math.round(monthlySavings));
                					}
                					recommendations.add(recommendation);
            					}
            				}
            			}
            		}
                }
            }
        }
		
		result.put("ag", assetGroup);
		result.put("category", category);
		if("cost_optimizing".equals(category)) {
			result.put("totalMonthlySavings", Math.round(totalMonthlySavings));
		}
		result.put("response", recommendations);
		return result;
	}
	
	public Map<String,Object> getRecommendationDetail(String assetGroup, String recommendationId, String application) throws DataException {
		
		Map<String,Object> recommendationDetail = new HashMap<>();
		List<Map<String,Object>> resources = new ArrayList<>();
		
		String parentType = "";
		if(StringUtils.isNotBlank(application)) {
			String query = "SELECT type FROM Recommendation_Mappings WHERE checkId =\""+recommendationId+"\"";
	        try {
	        	parentType = rdsRepository.queryForString(query);
	        } catch (Exception exception) {
	            LOGGER.error("Error in getRecommendationDetail for getting parent type " , exception);
	        }
		}
		
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
    			.append("recommendation").append("/").append(Constants.SEARCH).append("?scroll=")
                .append(Constants.ES_PAGE_SCROLL_TTL);

        String urlToQuery = urlToQueryBuffer.toString();
        String urlToScroll = new StringBuilder(esUrl).append("/").append(Constants.SEARCH).append("/scroll")
                .toString();
		
		StringBuilder requestBody = new StringBuilder("{\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"recommendationId.keyword\":\"");
		requestBody.append(recommendationId).append("\"}}");
		if(StringUtils.isNotBlank(application)) {
			requestBody.append(",{\"has_parent\":{\"parent_type\":\"");
			requestBody.append(parentType);
			requestBody.append("\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\":{\"_entity.keyword\":true}},{\"match\":{\"tags.Application.keyword\":\"");
			requestBody.append(application);
			requestBody.append("\"}}]}}}}");
		}
		requestBody.append("]}}}");
		long totalDocs = getTotalDocCount(assetGroup, "recommendation", "{" + requestBody.toString().substring(14));
		String request = requestBody.toString();
        String scrollId = null;
        if(totalDocs > 0){
            for (int index = 0; index <= (totalDocs / Constants.ES_PAGE_SIZE); index++) {
                String responseDetails = null;
                try {
                    if (StringUtils.isNotBlank(scrollId)) {
                        request = elasticSearchRepository.buildScrollRequest(scrollId, Constants.ES_PAGE_SCROLL_TTL);
                        urlToQuery = urlToScroll;
                    }
                    responseDetails = PacHttpUtils.doHttpPost(urlToQuery, request);
                    scrollId = processResponseAndSendTheScrollBack(responseDetails, resources);
                } catch (Exception e) {
                }
            }
        }
		
		recommendationDetail.put("resources", resources);
		return recommendationDetail;
	}
	
	@SuppressWarnings("deprecation")
	public Map<String,Object> getRecommendation(String recommendationId) throws DataException {
		
		Map<String, Object> mustFilter = new HashMap<>();
		mustFilter.put("checkid.keyword",recommendationId);
		mustFilter.put("latest",true);
		
		try {
			List<Map<String, Object>> result = elasticSearchRepository.getDataFromES("aws_checks", "checks", mustFilter,
	         null, null, null, null);
			return result.get(0);
		} catch (Exception e) {
			LOGGER.error("Error fetching applications",e);
			return new HashMap<>();
		}
		
	}
	
	@SuppressWarnings("unchecked")
    private long getTotalDocCount(String index, String type, String requestBody) {
    	StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(index);
        if(StringUtils.isNotBlank(type)) {
        	urlToQuery.append("/").append(type);
        }
        urlToQuery.append("/").append("_count");
        String responseDetails = null;
        Gson gson = new GsonBuilder().create();
        try {
            responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody);
            Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
            return (long) (Double.parseDouble(response.get("count").toString()));
        } catch (Exception e) {
            LOGGER.error("Error in getTotalDocCount", e);
            return 0;
        }
    }
	
	@SuppressWarnings("unchecked")
	private String processResponseAndSendTheScrollBack(String responseDetails, List<Map<String, Object>> results) {
		JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonArray hits = responseDetailsjson.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
        for (JsonElement hit : hits) {
            JsonObject source = hit.getAsJsonObject().get(AssetConstants.UNDERSCORE_SOURCE).getAsJsonObject();
            if (source != null) {
                Map<String, Object> doc = new Gson().fromJson(source, new TypeToken<Map<String, Object>>() {
                }.getType());
                Map<String,Object> resourceinfo = (Map<String, Object>) doc.get("resourceinfo");
                doc.remove("resourceinfo");
                doc.remove("_loaddate");
                doc.remove("latest");
                doc.remove("monthlysavings");
                doc.remove("recommendation");
                doc.putAll(resourceinfo);
                results.add(doc);
            }
        }
		return responseDetailsjson.get("_scroll_id").getAsString();
	}
	
	public List<Map<String,Object>> getGeneralRecommendationSummary(List<String> providers) throws DataException {
    	
    	List<Map<String,Object>> recommendationSummary = new ArrayList<>();
    	StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append("global_recommendations").append("/")
    			.append("recommendation").append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\": {\"filter\":[{\"term\":{\"latest\":\"true\"}},{\"terms\":{\"_cloudType\":[\"");
		requestBody.append(String.join("\",\" ", providers.stream().collect(Collectors.toList())));
		requestBody.append("\"]}}]}},\"aggs\":{\"category\":{\"terms\":{\"field\":\"category.keyword\",\"size\":100}}}}");
		
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error("Error in getGlobalRecommendationSummary "+e);
			throw new DataException(e);
		}
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        
		if (aggregations != null) {

			JsonArray categoryBuckets = aggregations.get("category").getAsJsonObject().get(Constants.BUCKETS)
					.getAsJsonArray();
			if (categoryBuckets.size() > 0) {
				for (int i = 0; i < categoryBuckets.size(); i++) {
					JsonObject categoryObj = (JsonObject) categoryBuckets.get(i);
					if (categoryObj != null) {
						Map<String, Object> category = new HashMap<>();
						category.put("category", categoryObj.get("key").getAsString());
						category.put("recommendations", categoryObj.get("doc_count").getAsLong());
						recommendationSummary.add(category);
					}
				}
			} else {
				//   passing 0 values if there are no recommendations
				String[] categories = recommendationCategories.split(",");
				for (int i = 0; i < categories.length; i++) {
					Map<String, Object> category = new HashMap<>();
					category.put("category", categories[i]);
					category.put("recommendations", 0L);
					recommendationSummary.add(category);
				}
			}
		}
        return recommendationSummary;
    }
	
	public Map<String,Object> getGeneralRecommendations(String category, List<String> providers) throws DataException {
		
		Map<String,Object> result = new HashMap<>();
		List<Map<String,Object>> recommendations = new ArrayList<>();
		
		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append("global_recommendations").append("/")
    			.append("recommendation").append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\":{\"category.keyword\":\"");
		requestBody.append(category);
		requestBody.append("\"}}],\"filter\":[{\"terms\":{\"_cloudType\":[\"");
		requestBody.append(String.join("\",\" ", providers.stream().collect(Collectors.toList())));
		requestBody.append("\"]}}]}},\"aggs\":{\"recommendations\":{\"terms\":{\"field\":\"recommendationId.keyword\",\"size\":10000}}}}");
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error("Error in getGeneralRecommendations "+e);
			throw new DataException(e);
		}
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        JsonArray recommendationBuckets = aggregations.get("recommendations").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
        if (recommendationBuckets.size() > 0) {
            for (int i=0; i<recommendationBuckets.size();i++) {
            	JsonObject recommendationObj = (JsonObject) recommendationBuckets.get(i);
                if (recommendationObj != null) {
                	String recommendationId = recommendationObj.get(Constants.KEY).getAsString();
					Map<String,Object> recommendationInfo = getRecommendation(recommendationId);
					if(!recommendationInfo.isEmpty()) {
						Map<String,Object> recommendation = new HashMap<>();
    					recommendation.put("recommendationId", recommendationId);
    					recommendation.put("recommendation", recommendationInfo.get("checkname"));
    					recommendation.put("description", recommendationInfo.get("checkdescription").toString().replace("[NL]", "<br />"));
    					recommendation.put("recommended", recommendationObj.get(Constants.DOC_COUNT).getAsLong());
    					recommendations.add(recommendation);
					}
                }
            }
        }
		
		result.put("category", category);
		result.put("response", recommendations);
		return result;
	}
	
	public Map<String,Object> getGeneralRecommendationDetail(String recommendationId) throws DataException {
		
		Map<String,Object> recommendationDetail = new HashMap<>();
		List<Map<String,Object>> resources = new ArrayList<>();
		
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append("global_recommendations").append("/")
    			.append("recommendation").append("/").append(Constants.SEARCH).append("?scroll=")
                .append(Constants.ES_PAGE_SCROLL_TTL);

        String urlToQuery = urlToQueryBuffer.toString();
        String urlToScroll = new StringBuilder(esUrl).append("/").append(Constants.SEARCH).append("/scroll")
                .toString();
		
		StringBuilder requestBody = new StringBuilder("{\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\":{\"recommendationId.keyword\":\"");
		requestBody.append(recommendationId);
		requestBody.append("\"}}]}}}");
		long totalDocs = getTotalDocCount("global_recommendations", "recommendation", "{" + requestBody.toString().substring(14));
		String request = requestBody.toString();
        String scrollId = null;
        if(totalDocs > 0){
            for (int index = 0; index <= (totalDocs / Constants.ES_PAGE_SIZE); index++) {
                String responseDetails = null;
                try {
                    if (StringUtils.isNotBlank(scrollId)) {
                        request = elasticSearchRepository.buildScrollRequest(scrollId, Constants.ES_PAGE_SCROLL_TTL);
                        urlToQuery = urlToScroll;
                    }
                    responseDetails = PacHttpUtils.doHttpPost(urlToQuery, request);
                    scrollId = processResponseAndSendTheScrollBack(responseDetails, resources);
                } catch (Exception e) {
                }
            }
        }
		
		recommendationDetail.put("resources", resources);
		return recommendationDetail;
	}
}
