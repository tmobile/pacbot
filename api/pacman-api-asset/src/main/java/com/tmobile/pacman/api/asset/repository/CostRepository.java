package com.tmobile.pacman.api.asset.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@Repository
public class CostRepository {
	
	@Value("${elastic-search.host}")
    private String esHost;
    @Value("${elastic-search.port}")
    private int esPort;
    private static final String PROTOCOL = "http";
    private String esUrl;
    
    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;
    
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }
    
    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(CostRepository.class);

	public List<Map<String,Object>> getCostAggs(List<String> appNameList) throws DataException {
		
		List<Map<String,Object>> costTrend = new ArrayList<>();
		Map<String, Integer> latestCostFinalised = findLatestCostFinalisedMonth();
		
		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append("aws-cost/monthly-cost").append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"terms\":{\"application.keyword\":");
		requestBody.append((new Gson().toJson(appNameList)));
		requestBody.append("}}]}},\"aggs\":{\"YEAR\":{\"terms\":{\"field\":\"year\",\"size\":10000},\"aggs\":{\"MONTH\":{\"terms\":{\"field\":\"month\",\"size\":12},"
				+ "\"aggs\":{\"COST\":{\"sum\":{\"field\":\"totalCost\"}}}}}}}}");
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			throw new DataException(e);
		}
		int currentYear = LocalDate.now().getYear();
		int currentMonth = LocalDate.now().getMonthValue();
		 
        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        JsonArray yearBuckets = aggregations.get("YEAR").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
        if (yearBuckets.size() > 0) {
            for (int i=0; i<yearBuckets.size();i++) {
                JsonObject yearObj = (JsonObject) yearBuckets.get(i);
                if (yearObj != null) {
                	int year = yearObj.get("key").getAsInt();
                	JsonArray monthBucket = yearObj.get("MONTH").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
                	for(int j=0;j< monthBucket.size();j++) {
                		Map<String,Object> trendMap = new HashMap<>();
                    	trendMap.put("year",year);
                		JsonObject monthObj = (JsonObject) monthBucket.get(j);
                		trendMap.put("month",monthObj.get("key").getAsInt());
                		trendMap.put("cost",Math.round(monthObj.get("COST").getAsJsonObject().get("value").getAsDouble()));
                		if(year==currentYear && currentMonth== monthObj.get("key").getAsInt()) {
                			trendMap.put("costStatus","accumulated");
                		}else {
                			trendMap.put("costStatus",checkForFinalised(latestCostFinalised.get("year"),latestCostFinalised.get("month"),year,monthObj.get("key").getAsInt())?"finalised":"estimated");
                		}
                		costTrend.add(trendMap);
                	}
                }
            }
        }
		return costTrend;
	}
	
	public List<Map<String,Object>> getCostAggsWithTT(List<String> appNameList, List<String> tTypeList) throws DataException {
		
		List<Map<String,Object>> costTrend = new ArrayList<>();
		Map<String, Integer> latestCostFinalised = findLatestCostFinalisedMonth();
		
		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append("aws-cost/monthly-cost").append("/").append(Constants.SEARCH);
		StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"terms\":{\"application.keyword\":");
		requestBody.append((new Gson().toJson(appNameList)));
		requestBody.append("}},\"aggs\":{\"YEAR\":{\"terms\":{\"field\":\"year\",\"size\":10000},\"aggs\":{\"MONTH\":{\"terms\":{\"field\":\"month\",\"size\":12},");
		requestBody.append( "\"aggs\":{\"COST\":{\"sum\":{\"script\":{\"inline\":\" double cost =0.0 ; for(int i=0; i<params._source.costInfo.length; i++){ if(params._source.costInfo[i].type=='");
		requestBody.append(tTypeList.get(0));
		requestBody.append("' ){ cost = params._source.costInfo[i].cost ; break;}} return cost;\"}}}}}}}}}");
		
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
		} catch (Exception e) {
			throw new DataException(e);
		}

        JsonParser parser = new JsonParser();
        JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggregations = responseDetailsjson.get(Constants.AGGREGATIONS).getAsJsonObject();
        JsonArray yearBuckets = aggregations.get("YEAR").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
        
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        
        if (yearBuckets.size() > 0) {
            for (int i=0; i<yearBuckets.size();i++) {
                JsonObject yearObj = (JsonObject) yearBuckets.get(i);
                if (yearObj != null) {
                	int year = yearObj.get("key").getAsInt();
                	JsonArray monthBucket = yearObj.get("MONTH").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
                	for(int j=0;j< monthBucket.size();j++) {
                		Map<String,Object> trendMap = new HashMap<>();
                    	trendMap.put("year",year);
                		JsonObject monthObj = (JsonObject) monthBucket.get(j);
                		trendMap.put("month",monthObj.get("key").getAsInt());
                		trendMap.put("cost",Math.round(monthObj.get("COST").getAsJsonObject().get("value").getAsDouble()));		
                		if(year==currentYear && currentMonth== monthObj.get("key").getAsInt()) {
                			trendMap.put("costStatus","accumulated");
                		}else {
                			trendMap.put("costStatus",checkForFinalised(latestCostFinalised.get("year"),latestCostFinalised.get("month"),year,monthObj.get("key").getAsInt())?"finalised":"estimated");
                		}
                		costTrend.add(trendMap);
                	}
                }
            }
        }
		return costTrend;
	}

	public Map<String,Integer> findLatestCostFinalisedMonth(){
		Map<String,Integer> yearMonthMap = new HashMap<>();
		String requestJson = "{\"size\":0,\"query\":{\"match\":{\"finalised\":true}},\"aggs\":{\"year-month\":{\"max\":{\"script\":\"Integer.parseInt((doc['year'].value+''+String.format('%02d',new def[] {doc['month'].value})))\"}}}}";
		String responseJson = "";
	    try {
	        responseJson = PacHttpUtils.doHttpPost(esUrl+"/aws-cost/monthly-cost/_search",requestJson);
	        String yearMonth = ""+new JsonParser().parse(responseJson).getAsJsonObject().getAsJsonObject("aggregations").getAsJsonObject("year-month").get("value").getAsInt();
	        
	    	yearMonthMap.put("year", Integer.valueOf(yearMonth.substring(0,4)));
	    	yearMonthMap.put("month", Integer.valueOf(yearMonth.substring(4)));
	    } catch (Exception e) {
	        LOGGER.error("Error fetching latest finalied cost year and month Info", e);
	    }
	   
		return yearMonthMap;
	}
	
	public List<String> fetchApplicationMasterList() {
		List<String> cloudApps = new ArrayList<>();
		Map<String, Object> mustFilter = new HashMap<>();
		mustFilter.put("_appType.keyword","Cloud");
		mustFilter.put("latest",true);
	
		List<String> sourceFields = new ArrayList<>();
		sourceFields.add("appTag");
		try {
			List<Map<String, Object>> result = elasticSearchRepository.getDataFromES("aws_apps", "apps", mustFilter,
	         null, null, sourceFields, null);
			cloudApps = result.stream().filter(app-> app.get("appTag")!=null).map(app-> app.get("appTag").toString()).collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error("Error fetching applications",e);
		}
		return cloudApps;
	}
	
	private boolean checkForFinalised(int finalisedYear, int finalisedMonth, int year, int month) {
		
		if(finalisedYear<year) {
			return false;
		} else if(finalisedYear==year) {
			if(finalisedMonth>=month) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}