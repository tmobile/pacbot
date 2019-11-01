/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/*
 * 
 */
package com.tmobile.pacman.api.statistics.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.statistics.client.AssetServiceClient;
import com.tmobile.pacman.api.statistics.domain.AssetApi;
import com.tmobile.pacman.api.statistics.domain.AssetApiData;
import com.tmobile.pacman.api.statistics.domain.AssetApiName;


/**
 * The Class StatisticsRepositoryImpl.
 */
@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository, Constants {

    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;

    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;

    /** The asset service client. */
    @Autowired
    private AssetServiceClient assetServiceClient;

    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;

    /** The es url. */
    private String esUrl;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsRepositoryImpl.class);
    
    /** The Constant PROTOCOL. */
    private static final String PROTOCOL = "http";
    
    /** The Constant AGGS. */
    private static final String AGGS = "aggregations";
    
    /** The Constant BUCKETS. */
    private static final String BUCKETS = "buckets";
    
    /** The Constant SEARCH. */
    private static final String SEARCH = "_search";
    
    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /**
     * Inits the.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.statistics.repository.StatisticsRepository#getTargetTypeForAG(java.lang.String, java.lang.String)
     */
    public String getTargetTypeForAG(String assetGroup, String domain) {

        String ttypesTemp;
        String ttypes = null;
        try{
        LOGGER.info("before the client call {}",assetServiceClient.toString());
        LOGGER.info("before the client call "+assetServiceClient.toString());
        AssetApi assetApi = assetServiceClient.getTargetTypeList(assetGroup, domain);
        LOGGER.info("after the client call {}",assetServiceClient.toString());
        AssetApiData data = assetApi.getData();
        AssetApiName[] targetTypes = data.getTargettypes();
        for (AssetApiName name : targetTypes) {
            ttypesTemp = new StringBuilder().append('\'').append(name.getType()).append('\'').toString();
            if (Strings.isNullOrEmpty(ttypes)) {
                ttypes = ttypesTemp;
            } else {
                ttypes = new StringBuilder(ttypes).append(",").append(ttypesTemp).toString();
            }
        }
        }catch(Exception e){
        	LOGGER.error("error proccessing fiegnclien assetServiceClient",e.getMessage());
        	LOGGER.error("error proccessing fiegnclien assetServiceClient",e);
        	return "";
        }
        return ttypes;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.statistics.repository.StatisticsRepository#getRuleIdWithTargetTypeQuery(java.lang.String)
     */
    public List<Map<String, Object>> getRuleIdWithTargetTypeQuery(String targetType) throws DataException {
        try {
            String ruleIdWithTargetTypeQuery = "SELECT ruleId, targetType FROM cf_RuleInstance WHERE STATUS = 'ENABLED'AND targetType IN ("
                    + targetType + ")";
            return rdsepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
        } catch (Exception e) {
            LOGGER.error("Error @ StatisticsRepositoryImpl/getRuleIdWithTargetTypeQuery ", e);
            return new ArrayList<>();
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.statistics.repository.StatisticsRepository#getNumberOfAccounts()
     */
    @Override
    public JsonArray getNumberOfAccounts() throws DataException {
        try {
            JsonParser parser = new JsonParser();
            StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(AWS).append("/")
                    .append(SEARCH);
            StringBuilder requestBody = new StringBuilder(
                    "{\"query\":{\"bool\":{}},\"aggs\":{\"accounts\":{\"terms\":{\"field\":\"accountname.keyword\",\"size\":10000}}}}");
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
            JsonObject paramObj = parser.parse(responseDetails).getAsJsonObject();
            JsonObject aggsJson = (JsonObject) parser.parse(paramObj.get(AGGS).toString());
            return aggsJson.getAsJsonObject("accounts").getAsJsonArray(BUCKETS);
        } catch (Exception e) {
        	LOGGER.error("Error while processing the aws accounts",e.getMessage());
        	return new JsonArray();
        }

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.statistics.repository.StatisticsRepository#getNumberOfPoliciesEvaluated()
     */
    @Override
    public String getNumberOfPoliciesEvaluated() throws DataException {
        try {
            JsonParser parser = new JsonParser();
            StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append("fre-stats").append("/")
                    .append(SEARCH);
            StringBuilder requestBody = new StringBuilder(
                    "{\"size\":0,\"query\":{\"range\":{\"endTime\":{\"gte\":\"now-1d/d\"}}},\"aggs\":{\"total_evals\":{\"sum\":{\"field\":\"totalResourcesForThisExecutionCycle\"}}}}");
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
            JsonObject paramObj = parser.parse(responseDetails).getAsJsonObject();
            JsonObject aggregations = (JsonObject) paramObj.get(AGGS);
            JsonObject totalEvals = (JsonObject) aggregations.get("total_evals");
            return totalEvals.get("value").toString();
        } catch (Exception e) {
        	LOGGER.error("Error while processing the number of policies evaluated",e.getMessage());
        	return "0";
        }

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.statistics.repository.StatisticsRepository#getTotalViolations()
     */
    @Override
    public JsonArray getTotalViolations() throws DataException {
        try {
            JsonParser parser = new JsonParser();
            StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(MASTER_ALIAS).append("/")
                    .append(SEARCH);
            StringBuilder requestBody = new StringBuilder(
                    "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}},{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}}]}},\"aggs\":{\"severity\":{\"terms\":{\"field\":\"severity.keyword\",\"size\":10000}}}}");
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
            JsonObject paramObj = parser.parse(responseDetails).getAsJsonObject();
            JsonObject aggsJson = (JsonObject) parser.parse(paramObj.get(AGGS).toString());
            return aggsJson.getAsJsonObject("severity").getAsJsonArray(BUCKETS);
        } catch (Exception e) {
        	LOGGER.error("Error while processing the getTotalViolations",e.getMessage());
        	return new JsonArray();
        }
    }

    @Override
    public List<Map<String, Object>> getAutofixRulesFromDb() throws DataException{
        try {
                    
            String query="SELECT * FROM cf_RuleInstance WHERE `status`='ENABLED' AND ruleParams LIKE '%\"autofix\":true%'";
            return rdsepository.getDataFromPacman(query);
        } catch (Exception e) {
            LOGGER.error("Error @ StatisticsRepositoryImpl/ getAutofixRulesFromDb ", e);
            return new ArrayList<>();
        }
    }
    
    private Long getAutoFixActionCount() throws DataException{

        long totalAutoFixActionCount;
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ACTION), "AUTOFIX_ACTION_FIX");
        try {
            totalAutoFixActionCount = elasticSearchRepository.getTotalDocumentCountForIndexAndType("fre-auto-fix-tran-log", null,
                    mustFilter, mustNotFilter, shouldFilter, null, mustTermsFilter);

        } catch (Exception e) {
        	LOGGER.error("Error while processing the fre auto fix",e.getMessage());
            return 0l;
        }
        return totalAutoFixActionCount;
    }
    
    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.statistics.repository.StatisticsRepository#getAutofixActionCountByRule()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getAutofixActionCountByRule() throws DataException {
        try {
            JsonParser parser = new JsonParser();
            StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/fre-auto-fix-tran-log/_search");
            StringBuilder requestBody = new StringBuilder("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\""+ACTION+"\":\"AUTOFIX_ACTION_FIX\"}}]}},\"aggs\":{\"RULEID\":{\"terms\":{\"field\":\"ruleId.keyword\",\"size\":10000},\"aggs\":{\"RESOURCEID\":{\"terms\":{\"field\":\"resourceId.keyword\",\"size\":"+getAutoFixActionCount()+"}}}}}}");
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
            JsonObject paramObj = parser.parse(responseDetails).getAsJsonObject();
            JsonObject aggsJson = (JsonObject) parser.parse(paramObj.get(AGGS).toString());
            JsonArray outerBuckets = aggsJson.getAsJsonObject("RULEID").getAsJsonArray(BUCKETS);
            Arrays.asList(outerBuckets);
            Gson googleJson = new Gson();
           return googleJson.fromJson(outerBuckets, ArrayList.class); 
        } catch (Exception e) {
        	LOGGER.error("Error while processing the fre auto fix",e.getMessage());
        	return new ArrayList<>();
        }
    }

}