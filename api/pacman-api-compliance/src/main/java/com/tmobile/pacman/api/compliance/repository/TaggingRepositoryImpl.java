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
package com.tmobile.pacman.api.compliance.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.client.AssetServiceClient;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;
import com.tmobile.pacman.api.compliance.service.ComplianceService;

/**
 * The Class TaggingRepositoryImpl.
 */
@Repository
public class TaggingRepositoryImpl implements TaggingRepository, Constants {

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;
    
    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;
    
    /** The asset service client. */
    @Autowired
    private AssetServiceClient assetServiceClient;
    
    /** The compliance service client. */
    @Autowired
    private ComplianceService complianceServiceClient;
    
    /** The complaince repository. */
    @Autowired
    private ComplianceRepository complainceRepository;

    /** The mandatory tags. */
    @Value("${tagging.mandatoryTags}")
    private String mandatoryTags;
    
    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;
    
    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;
    
    /** The Constant PROTOCOL. */
    static final String PROTOCOL = "http";
    
    /** The es url. */
    private String esUrl;

    /** The page size. */
    private Integer pageSize = TEN_THOUSAND;

    /**
     * Inits the.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TaggingRepository#getUntaggedIssuesByapplicationFromES(java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public JsonArray getUntaggedIssuesByapplicationFromES(String assetGroup,
            String mandatoryTags, String searchText, int from, int size)
            throws DataException {
    	List<String> mandatoryTagsList  = new ArrayList<>();
    	if(!com.amazonaws.util.StringUtils.isNullOrEmpty(mandatoryTags)){
       mandatoryTagsList = Arrays
                .asList(mandatoryTags.split(","));
    	}
        String responseJson = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/")
                .append(assetGroup).append("/").append(SEARCH);
        StringBuilder requestBody = new StringBuilder(
                "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"policyId.keyword\":{\"value\":\"PacMan_TaggingRule_version-1\"}}}");
        if (!Strings.isNullOrEmpty(searchText)) {
            requestBody.append("," + "{\"match_phrase_prefix\":{\"_all\":\""
                    + searchText + "\"" + "}}");
        }

        requestBody
                .append("],\"should\":[{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}],\"minimum_should_match\":1}},\"aggs\":{\"apps\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":"
                        + pageSize
                        + "},\"aggs\":{\"tags\":{\"filters\":{\"filters\":{");

        for (String tags : mandatoryTagsList) {
            if (!"Application".equalsIgnoreCase(tags)) {
                requestBody.append("\"" + tags + "\""
                        + ":{\"bool\":{\"must\":[{\"match\":{\"missingTags\":"
                        + "\"" + tags + "\"" + "}}]}},");

            }
        }
        requestBody.setLength(requestBody.length() - 1);
        requestBody.append("}}}}}}}");
        try{
        responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(),
                requestBody.toString());
        } catch (Exception e) {
            throw new DataException(e);
        }
        jsonParser = new JsonParser();
        resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get(
                AGGREGATIONS).toString());
        return aggsJson.getAsJsonObject("apps").getAsJsonArray(BUCKETS);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TaggingRepository#getRuleParamsFromDbByPolicyId(java.lang.String)
     */
    public List<Map<String, Object>> getRuleParamsFromDbByPolicyId(
            String policyId) throws DataException {
        String ruleIdQuery = "SELECT rule.ruleParams FROM cf_RuleInstance rule LEFT JOIN cf_Policy policy ON rule.policyId = policy.policyId WHERE rule.status = 'ENABLED' AND policy.policyId ='"
                + policyId + "' GROUP BY rule.policyId";
        return rdsepository.getDataFromPacman(ruleIdQuery);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TaggingRepository#getUntaggedIssues(java.lang.String, java.lang.String)
     */
    public Long getUntaggedIssues(String assetGroup, String mandatoryTag)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, List<String>> matchPhrasePrefix = new HashMap<>();
        List<String> mandatoryTagsList = new ArrayList<>();
        if (mandatoryTag != null) {
            mandatoryTagsList.add(mandatoryTag);
        }
        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(POLICYID),
                TAGGIG_POLICY);
        matchPhrasePrefix.put(MISSING_TAGS, mandatoryTagsList);
        shouldFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS),
                OPEN);
        try{
        return elasticSearchRepository
                .getTotalDistributionForIndexAndTypeWithMatchPhrase(assetGroup,
                        null, mustFilter, mustNotFilter, shouldFilter, null,
                        mustTermsFilter, matchPhrasePrefix);
        } catch (Exception e) {
            throw new DataException(e);
        }
    }


    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TaggingRepository#getRuleTargetTypesFromDbByPolicyId(java.lang.String)
     */
    public List<Map<String, Object>> getRuleTargetTypesFromDbByPolicyId(
            String policyId) throws DataException {
        String ruleIdQuery = "SELECT rule.targetType FROM cf_RuleInstance rule LEFT JOIN cf_Policy policy ON rule.policyId = policy.policyId WHERE rule.status = 'ENABLED' AND policy.policyId ='"
                + policyId + "'";
        return rdsepository.getDataFromPacman(ruleIdQuery);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TaggingRepository#getUntaggedTargetTypeIssues(com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest, java.util.List)
     */
    public String getUntaggedTargetTypeIssues(
            UntaggedTargetTypeRequest request, List<String> tagsList)
            throws DataException {
        String assetGroup = request.getAg();
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/")
                .append(assetGroup).append("/").append(SEARCH);
        StringBuilder requestBody = null;
        String body = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"policyId.keyword\":{\"value\":\""
                + TAGGIG_POLICY
                + "\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}]";

        if (!tagsList.isEmpty()) {
            body = body + ",\"should\":[";
            /* filtering by tags */
            for (String tag : tagsList) {
                body = body + "{\"match_phrase_prefix\":{\"missingTags\":\""
                        + tag + "\"}},";
            }
            body = body.substring(0, body.length() - 1);
            body = body + "]";
            body = body + ",\"minimum_should_match\":1";
        }
        body = body
                + "}},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"targetType.keyword\",\"size\":1000}}}}";
        requestBody = new StringBuilder(body);
     try{   return PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(),
                requestBody.toString());
     } catch (Exception e) {
         logger.error(e.getMessage());
         throw new DataException(e);
     }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TaggingRepository#getTaggingByApplication(java.lang.String, java.lang.String)
     */
    @Override
    public String getTaggingByApplication(String ag, String targetType)
            throws DataException {
        StringBuilder requestBody = new StringBuilder();
        StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(
                ag);
        if (!StringUtils.isEmpty(targetType)) {
            urlToQuery.append("/").append(targetType);
            urlToQuery.append("/").append(UNDERSCORE_COUNT);
            requestBody
                    .append("{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}}],\"must_not\":[{\"exists\":{\"field\":\"tags.Application\"}}]}}}");
        } else {
            urlToQuery.append("/").append(SEARCH);
            requestBody
                    .append("{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}}],\"must_not\":[{\"exists\":{\"field\":\"tags.Application\"}}]}},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"_entitytype.keyword\",\"size\":"
                            + TEN_THOUSAND + "}}}}");
        }

        String responseJson = "";
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQuery.toString(),
                    requestBody.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataException(e);
        }
        return responseJson;
    }
}
