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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.HeimdallElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.client.AssetServiceClient;
import com.tmobile.pacman.api.compliance.domain.AssetApi;
import com.tmobile.pacman.api.compliance.domain.AssetApiData;
import com.tmobile.pacman.api.compliance.domain.AssetCount;
import com.tmobile.pacman.api.compliance.domain.AssetCountByAppEnvDTO;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;
import com.tmobile.pacman.api.compliance.domain.AssetCountData;
import com.tmobile.pacman.api.compliance.domain.AssetCountEnvCount;
import com.tmobile.pacman.api.compliance.domain.Compare;
import com.tmobile.pacman.api.compliance.domain.ExemptedAssetByPolicy;
import com.tmobile.pacman.api.compliance.domain.ExemptedAssetByPolicyData;
import com.tmobile.pacman.api.compliance.domain.IssueExceptionResponse;
import com.tmobile.pacman.api.compliance.domain.IssueResponse;
import com.tmobile.pacman.api.compliance.domain.IssuesException;
import com.tmobile.pacman.api.compliance.domain.KernelVersion;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithOrder;
import com.tmobile.pacman.api.compliance.domain.RuleDetails;
import com.tmobile.pacman.api.compliance.repository.model.RhnSystemDetails;

/**
 * The Class ComplianceRepositoryImpl.
 */
@Repository
public class ComplianceRepositoryImpl implements ComplianceRepository, Constants {

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

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;

    /** The asset service client. */
    @Autowired
    private AssetServiceClient assetServiceClient;

    /** The filter repository. */
    @Autowired
    private FilterRepository filterRepository;

    /** The rhn system details repository. */
    @Autowired
    private RhnSystemDetailsRepository rhnSystemDetailsRepository;

    /** The patching repository. */
    @Autowired
    private PatchingRepository patchingRepository;

    /** The heimdall elastic search repository. */
    @Autowired
    private HeimdallElasticSearchRepository heimdallElasticSearchRepository;

    /** The rest client. */
    private static RestClient restClient;

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Inits the.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getIssuesCount(java.lang.String, java.lang.String, java.lang.String)
     */
    public long getIssuesCount(String assetGroup, String ruleId, String domain) throws DataException {
        long totalIssueCount;
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        String targetTypes = getTargetTypeForAG(assetGroup, domain);
        List<Object> rules = getRuleIds(targetTypes);
        if (!Strings.isNullOrEmpty(ruleId)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), ruleId);
        }
        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), rules);
        try {
            totalIssueCount = elasticSearchRepository.getTotalDocumentCountForIndexAndType(assetGroup, null,
                    mustFilter, mustNotFilter, shouldFilter, null, mustTermsFilter);

        } catch (Exception e) {
            throw new DataException("" + e);
        }
        return totalIssueCount;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getIssuesFromES(com.tmobile.pacman.api.compliance.domain.Request)
     */
    @SuppressWarnings("rawtypes")
    public ResponseWithOrder getIssuesFromES(Request request) throws DataException {
        // check exempted Issues to be included

        StringBuilder requestBody = null;
        List<String> tagsList = new ArrayList<>(Arrays.asList(mandatoryTags.split(",")));
        List<Map<String, Object>> issueDetails = null;
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map.Entry pair;
        Iterator filterIterator;
        String ruleId = "";
        String ruleIdQuery = "";
        long totalIssueCount = 0;
        ResponseWithOrder response;
        List<Map<String, Object>> ruleIdDetail = null;
        String assetGroup = request.getAg();
        Map<String, String> filters = request.getFilter();
        int size = request.getSize();
        int from = request.getFrom();

        String searchText = request.getSearchtext();
        // _source only these fields from ES
        ArrayList<String> fields = new ArrayList<>();
        fields.add(RESOURCEID);
        fields.add(SEVERITY);
        fields.add(RULE_CATEGORY);
        fields.add(ACCOUNT_NAME);
        fields.add(ACCOUNT_ID);
        fields.add(REGION);
        fields.add(CREATED_DATE);
        fields.add(MODIFIED_DATE);
        fields.add(ISSUE_STATUS);
        fields.add(TARGET_TYPE);
        fields.add(TAGS_APPLICATION);
        fields.add(TAGS_ENVIRONMENT);
        fields.add(DESC);
        fields.add(RULEID);
        fields.add(POLICYID);
        fields.add(_ID);
        fields.add(ENV);
        // for sox domain

        String domain = filters.get(DOMAIN);

        // get all active rules.
        String targetTypes = getTargetTypeForAG(assetGroup, domain);
        List<Map<String, Object>> rulesList = getRuleIdWithDisplayNameQuery(targetTypes);
        List<Object> rules = getRuleIds(targetTypes);
        List<Object> issueStatus = new ArrayList<>();

        Map<String, String> ruleIdwithDisplayNameMap = rulesList.stream().collect(
                Collectors.toMap(s -> (String) s.get(RULEID), s -> (String) s.get(RULE_DISPAY_NAME)));
        if (MapUtils.isNotEmpty(filters)) {
            filterIterator = filters.entrySet().iterator();

            while (filterIterator.hasNext()) {
                pair = (Map.Entry) filterIterator.next();

                if ("ruleId.keyword".equalsIgnoreCase(pair.getKey().toString())) {
                    ruleId = pair.getValue().toString();
                    ruleIdQuery = "SELECT ruleId, displayName FROM cf_RuleInstance WHERE STATUS = 'ENABLED' AND ruleId = '"
                            + ruleId + "'";

                    ruleIdDetail = rdsepository.getDataFromPacman(ruleIdQuery);

                    if (ruleIdDetail.isEmpty()) {
                        throw new DataException(NO_DATA_FOUND);
                    }
                    ruleIdwithDisplayNameMap = ruleIdDetail.stream().collect(
                            Collectors.toMap(s -> (String) s.get(RULEID), s -> (String) s.get(RULE_DISPAY_NAME)));
                }
                if (!DOMAIN.equalsIgnoreCase(pair.getKey().toString())
                        && !(INCLUDE_EXEMPT.equalsIgnoreCase(pair.getKey().toString()))) {
                    mustFilter.put(pair.getKey().toString(), pair.getValue());
                }

            }
        }
        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), Constants.ISSUE);

        issueStatus.add(OPEN);
        if (null != filters.get("include_exempt") && ("yes".equalsIgnoreCase(filters.get(INCLUDE_EXEMPT)))) {
            issueStatus.add(EXEMPTED);
        }
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), issueStatus);
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), rules);

        try {
            if (!Strings.isNullOrEmpty(ruleId) && ruleId.contains(TAGGING_POLICY)) {
                /*
                 * if rule is related tagging rule we need to consider only
                 * Application/environment tags not all open issues .
                 */
                Gson serializer = new GsonBuilder().create();
                String body = null;
                if (size <= 0) {
                    size = getUntaggedCount(esUrl, assetGroup, ruleId, tagsList);
                }
                body = "{\"size\":"
                        + size
                        + ",\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"policyId.keyword\":{\"value\":\""
                        + TAGGIG_POLICY + "\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}";
                body = body + ",{\"term\":{\"ruleId.keyword\":{\"value\":\"" + ruleId + "\"}}}";
                body = body + "]";
                if (!tagsList.isEmpty()) {
                    body = body + ",\"should\":[";
                    for (String tag : tagsList) {
                        body = body + "{\"match_phrase_prefix\":{\"missingTags\":\"" + tag + "\"}},";
                    }
                    body = body.substring(0, body.length() - 1);
                    body = body + "]";
                    body = body + ",\"minimum_should_match\":1";
                }
                body = body + "}}}";
                requestBody = new StringBuilder(body);
                StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
                        .append(SEARCH).append("?").append("from").append("=").append(from);
                String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
                Map<String, Object> responseMap = (Map<String, Object>) serializer.fromJson(responseDetails,
                        Object.class);
                if (responseMap.containsKey(HITS)) {
                    Map<String, Object> hits = (Map<String, Object>) responseMap.get(HITS);
                    if (hits.containsKey(HITS)) {
                        issueDetails = (List<Map<String, Object>>) hits.get(HITS);
                        for (Map<String, Object> issueDetail : issueDetails) {
                            Map<String, Object> sourceMap = (Map<String, Object>) issueDetail.get("_source");
                            sourceMap.put("_id", issueDetail.get("_id"));
                            issueList = getIssueList(issueDetail, sourceMap, ruleIdwithDisplayNameMap, issueList,
                                    domain);
                        }

                    }
                }
            } else {
                if (MapUtils.isNotEmpty(filters) || size > 0 || !Strings.isNullOrEmpty(searchText)) {

                    issueDetails = elasticSearchRepository.getSortedDataFromESBySize(assetGroup, null, mustFilter,
                            mustNotFilter, shouldFilter, fields, from, size, searchText, mustTermsFilter, null);
                    for (Map<String, Object> issueDetail : issueDetails) {
                        issueList = getIssueList(null, issueDetail, ruleIdwithDisplayNameMap, issueList, domain);
                    }

                } else {
                    issueDetails = elasticSearchRepository.getSortedDataFromES(assetGroup, null, mustFilter,
                            mustNotFilter, shouldFilter, fields, mustTermsFilter, null);

                    for (Map<String, Object> issueDetail : issueDetails) {
                        issueList = getIssueList(null, issueDetail, ruleIdwithDisplayNameMap, issueList, domain);
                    }
                }
            }

            if (!issueList.isEmpty()) {
                // sorting by severity
                Collections.sort(issueList, new Compare() {
                    @Override
                    public int compare(Map<String, Object> a, Map<String, Object> b) {
                        return a.get(SEVERITY_DISPALY_NAME).toString()
                                .compareTo(b.get(SEVERITY_DISPALY_NAME).toString());
                    }
                });
            }

            if (issueDetails != null && issueDetails.isEmpty()) {
                throw new DataException(NO_DATA_FOUND);
            } else {
                if (!Strings.isNullOrEmpty(ruleId) && ruleId.contains(TAGGING_POLICY)) {
                    totalIssueCount = getUntaggedCount(esUrl, assetGroup, ruleId, tagsList);
                } else {
                    totalIssueCount = elasticSearchRepository.getTotalDocumentCountForIndexAndType(assetGroup, null,
                            mustFilter, mustNotFilter, null, searchText, mustTermsFilter);
                }
                response = new ResponseWithOrder(issueList, totalIssueCount);

            }
        } catch (Exception e) {
            logger.info(e);
            throw new DataException(e);
        }
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.api.compliance.repository.ComplianceRepository#getTagging
     * (java.lang.String, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Long> getTagging(String assetGroup, String targetType) throws DataException {

        Map<String, Long> tagging = new HashMap<>();
        long totaluntagged;
        long totalAssets = 0l;
        long assets;
        long totalTagged;
        double compliance;
        String type;
        String responseDetails = null;
        JsonParser parser = new JsonParser();
        List<Map<String, Object>> ruleIdwithTargetType;
        String ruleIdWithTargetTypeQuery = null;
        AssetCountData data;
        AssetCount assetCount;
        AssetCountByAppEnvDTO[] assetcountCount;
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
                .append(UNDERSCORE_COUNT);
        StringBuilder requestBody = null;
        List<String> tagsList = new ArrayList<>(Arrays.asList(mandatoryTags.split(",")));

        String body = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"policyId.keyword\":{\"value\":\""
                + TAGGIG_POLICY + "\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}";
        if (!Strings.isNullOrEmpty(targetType)) {
            body = body + ",{\"term\":{\"targetType.keyword\":{\"value\":\"" + targetType + "\"}}}";
        }

        body = body + "]";
        if (!tagsList.isEmpty()) {
            body = body + ",\"should\":[";

            for (String tag : tagsList) {
                body = body + "{\"match_phrase_prefix\":{\"missingTags\":\"" + tag + "\"}},";
            }
            body = body.substring(0, body.length() - 1);
            body = body + "]";
            body = body + ",\"minimum_should_match\":1";
        }
        body = body + "}}}";
        requestBody = new StringBuilder(body);
        try {
            responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
        } catch (Exception e) {
            throw new DataException(e);
        }
        JsonObject responseJson = parser.parse(responseDetails).getAsJsonObject();
        totaluntagged = responseJson.get(COUNT).getAsLong();
        ruleIdWithTargetTypeQuery = "SELECT  A.targetType FROM cf_RuleInstance A, cf_Policy B WHERE A.policyId = B.policyId AND A.status = 'ENABLED' AND B.policyId = 'PacMan_TaggingRule_version-1'";
        ruleIdwithTargetType = rdsepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
        if (Strings.isNullOrEmpty(targetType)) {
            assetCount = assetServiceClient.getTotalAssetsCount(assetGroup, targetType, null,null,"");
            data = assetCount.getData();
            assetcountCount = data.getAssetcount();

            for (Map<String, Object> rulewithtype : ruleIdwithTargetType) {
                type = rulewithtype.get(TARGET_TYPE).toString();
                for (AssetCountByAppEnvDTO count : assetcountCount) {
                    if (count.getType().equalsIgnoreCase(type)) {
                        assets = Long.parseLong(count.getCount());
                        totalAssets = totalAssets + assets;
                    }

                }
            }
        } else {
            totalAssets = getTotalAssetCountForAnytargetType(assetGroup, targetType);
        }

        if (totaluntagged > totalAssets) {
            totaluntagged = totalAssets;
        }
        totalTagged = totalAssets - totaluntagged;
        if (totalAssets > 0) {
            compliance = (totalTagged * HUNDRED / totalAssets);
            compliance = Math.floor(compliance);
        } else {
            compliance = INT_HUNDRED;
        }
        if (compliance > HUNDRED) {
            compliance = INT_HUNDRED;
        }

        tagging.put("untagged", totaluntagged);
        tagging.put("tagged", totalTagged);
        tagging.put("assets", totalAssets);
        tagging.put("compliance", (long) compliance);
        if (tagging.isEmpty()) {
            throw new DataException(NO_DATA_FOUND);
        }
        return tagging;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getCertificates(java.lang.String)
     */
    public Map<String, Long> getCertificates(String assetGroup) throws DataException {
        Map<String, Long> certificates = new HashMap<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Long totalCertificates;
        Long totalCertificatesExpired;
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TYPE), Constants.ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.RULEID), Constants.SSL_EXPIRY_RULE);
        try {
            totalCertificatesExpired = elasticSearchRepository.getTotalDocumentCountForIndexAndType(assetGroup, null,
                    mustFilter, mustNotFilter, shouldFilter, null, null);
            mustFilter = new HashMap<>();
            mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.LATEST), Constants.TRUE);
            totalCertificates = getTotalAssetCountForAnytargetType(assetGroup, "cert");
            certificates.put("certificates_expiring", totalCertificatesExpired);
            certificates.put("certificates", totalCertificates);
        } catch (Exception e) {
            throw new DataException(e);
        }
        if (certificates.isEmpty()) {
            throw new DataException(NO_DATA_FOUND);
        }
        return certificates;
    }

    /**
     * Gets the recommendations.
     *
     * @author santoshi
     * @param assetGroup
     *            the asset group
     * @param targetType
     *            the target type
     * @return the recommendations
     * @throws DataException
     *             the data exception
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> getRecommendations(String assetGroup, String targetType) throws DataException {
        List<Map<String, Object>> recommendationList = new ArrayList<>();
        List<Map<String, Object>> ruleIdwithPolicy;
        Map<String, Long> ruleDistributionByRuleId;
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        String ruleIdWithPolicyQuery = null;
        String ruleId = null;
        Map<String, Object> recommendationByRule = null;
        Iterator ruleIdswithCount;
        Map.Entry pair;
        if (!Strings.isNullOrEmpty(targetType)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(TARGET_TYPE), targetType);
        }
        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(SUBTYPE), RECOMMENDATION);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        ruleId = CommonUtils.convertAttributetoKeyword(RULEID);
        ruleIdWithPolicyQuery = "SELECT A.ruleId, A.displayName, A.targetType, B.policyDesc FROM cf_RuleInstance A, cf_Policy B  WHERE A.policyId = B.policyId AND A.status = 'ENABLED'";
        ruleIdwithPolicy = rdsepository.getDataFromPacman(ruleIdWithPolicyQuery);
        try {
            ruleDistributionByRuleId = elasticSearchRepository.getTotalDistributionForIndexAndType(assetGroup, null,
                    mustFilter, mustNotFilter, null, ruleId, ruleIdwithPolicy.size(), null);
        } catch (Exception e) {
            throw new DataException(e);
        }
        ruleIdswithCount = ruleDistributionByRuleId.entrySet().iterator();
        while (ruleIdswithCount.hasNext()) {
            pair = (Map.Entry) ruleIdswithCount.next();
            recommendationByRule = new HashMap<>();
            recommendationByRule.put(METRIC_VALUE, pair.getValue().toString());
            for (Map<String, Object> ruleDetails : ruleIdwithPolicy) {
                if (ruleDetails.get(RULEID).equals(pair.getKey())) {
                    recommendationByRule.put(DESCRIPTION, ruleDetails.get(POLICY_DESC));
                    recommendationByRule.put(TITLE, ruleDetails.get(DISPLAY_NAME));
                    recommendationByRule.put(METRIC_NAME, ruleDetails.get(DISPLAY_NAME) + " " + COUNT);
                    recommendationByRule.put(TARGET_TYPE, ruleDetails.get(TARGET_TYPE));
                }
            }
            recommendationList.add(recommendationByRule);
        }
        if (recommendationList.isEmpty()) {
            throw new DataException(NO_DATA_FOUND);
        }
        return recommendationList;
    }

    /**
     * Gets the total asset count for anytarget type.
     *
     * @author santoshi
     * @param assetGroup
     *            the asset group
     * @param targetType
     *            the target type
     * @return the total asset count for anytarget type
     */
    public Long getTotalAssetCountForAnytargetType(String assetGroup, String targetType) {

        AssetCount totalAssets = assetServiceClient.getTotalAssetsCount(assetGroup, targetType, null,null,"");
        AssetCountData data = totalAssets.getData();
        AssetCountByAppEnvDTO[] assetcount = data.getAssetcount();
        Long totalAssetsCount = 0l;
        for (AssetCountByAppEnvDTO assetCount_Count : assetcount) {
            if (assetCount_Count.getType().equalsIgnoreCase(targetType)) {
                totalAssetsCount = Long.parseLong(assetCount_Count.getCount());
            }
        }
        return totalAssetsCount;
    }

    /**
     * Gets the resource details from ES.
     *
     * @author u55262
     * @param assetGroup
     *            the asset group
     * @param resourceId
     *            the resource id
     * @return the resource details from ES
     * @throws DataException
     *             the data exception
     */
    public List<Map<String, Object>> getResourceDetailsFromES(String assetGroup, String resourceId)
            throws DataException {
        List<Map<String, Object>> resourceDetList = new ArrayList<>();
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(RESOURCEID), resourceId);
        mustFilter.put(Constants.LATEST, Constants.TRUE);
        try {
            resourceDetList = elasticSearchRepository.getSortedDataFromES(assetGroup, null, mustFilter, null, null,
                    null, null, null);
        } catch (Exception e) {
            throw new DataException(e);
        }
        if (resourceDetList.isEmpty()) {
            throw new DataException(NO_DATA_FOUND);
        }
        return resourceDetList;
    }

    /**
     * Gets the issue audit log.
     *
     * @author u55262
     * @param annotationId
     *            the annotation id
     * @param targetType
     *            the target type
     * @param from
     *            the from
     * @param size
     *            the size
     * @param searchText
     *            the search text
     * @return the issue audit log
     * @throws DataException
     *             the data exception
     */
    public List<LinkedHashMap<String, Object>> getIssueAuditLog(String annotationId, String targetType, int from,
            int size, String searchText) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("annotationid"), annotationId);
        String type = ISSUE_UNDERSCORE + targetType + "_audit";
        ArrayList<String> fields = new ArrayList<>();
        fields.add(STATUS);
        fields.add(AUDIT_DATE);
        fields.add(DATA_SOURCE);
        try {
            return elasticSearchRepository.getDetailsFromESBySize(AWS, type, mustFilter, null, null, fields, from,
                    size, null, null);
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getIssueAuditLogCount(java.lang.String, java.lang.String)
     */
    public Long getIssueAuditLogCount(String annotationId, String targetType) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("annotationid"), annotationId);
        String type = ISSUE_UNDERSCORE + targetType + "_audit";
        try {
            return elasticSearchRepository.getTotalDocumentCountForIndexAndType(AWS, type, mustFilter, null, null,
                    null, null);
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    /**
     * Gets the rules from ES.
     *
     * @param assetGroup
     *            the asset group
     * @return the rules from ES
     * @throws Exception
     *             the exception
     */
    public Map<String, Long> getRulesFromES(String assetGroup) throws Exception {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        String aggsFilterRuleId = CommonUtils.convertAttributetoKeyword(RULEID);
        return elasticSearchRepository.getTotalDistributionForIndexAndType(assetGroup, null, mustFilter, mustNotFilter,
                null, aggsFilterRuleId, 1000, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * exemptAndUpdateIssueDetails
     * (com.tmobile.pacman.api.compliance.domain.IssueResponse)
     */
    @Override
    public Boolean exemptAndUpdateIssueDetails(IssueResponse issueException) throws DataException {
        try {
            List<Map<String, Object>> issueDetails = getOpenIssueDetails(issueException.getIssueId());
            if (!issueDetails.isEmpty()) {
                Map<String, Object> issueDetail = issueDetails.get(0);
                Map<String, Object> issueExceptionDetails = issueException.getIssueExceptionDetails();
                issueExceptionDetails.put(TARGET_TYPE, String.valueOf(issueDetail.get(TARGET_TYPE)));
                issueExceptionDetails.put("source", "issueException");
                issueExceptionDetails.put("resourceId", String.valueOf(issueDetail.get(RESOURCEID)));
                String dataSource = issueDetail.get(PAC_DS) + "_" + issueDetail.get(TARGET_TYPE);
                String targetType = issueDetail.get(TYPE) + "_" + issueDetail.get(TARGET_TYPE);
                String id = String.valueOf(issueDetail.get(ES_DOC_ID_KEY));
                String routing = String.valueOf(issueDetail.get(ES_DOC_ROUTING_KEY));
                String parent = String.valueOf(issueDetail.get(ES_DOC_PARENT_KEY));
                Map<String, Object> partialDocument = Maps.newHashMap();
                partialDocument.put(ISSUE_STATUS, EXEMPTED);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                partialDocument.put(MODIFIED_DATE, sdf.format(new Date()));
                partialDocument.put(STATUS, "enforced");
                Boolean isUpdated = elasticSearchRepository.updatePartialDataToES(dataSource, targetType, id, routing,
                        parent, partialDocument);
                if (isUpdated) {
                    return elasticSearchRepository.saveExceptionDataToES(dataSource, routing, issueExceptionDetails);
                }
            }
            return false;
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    /**
     * Close issues by rule.
     *
     * @author NidhishKrishnan (Nidhish)
     * @param ruleDetails
     *            the rule details
     * @return Boolean
     * @requestBody RuleDetails
     */
    @Override
    public Boolean closeIssuesByRule(RuleDetails ruleDetails) {
        List<Map<String, Object>> issueDetails;
        try {
            issueDetails = getOpenIssueDetailsByRuleId(ruleDetails.getRuleId());
            Boolean status = true;
            for (Map<String, Object> issueDetail : issueDetails) {
                if (status) {
                    String dataSource = String.valueOf(issueDetail.get(PAC_DS)) + "_" + issueDetail.get(TARGET_TYPE);
                    String targetType = String.valueOf(issueDetail.get(TYPE)) + "_" + issueDetail.get(TARGET_TYPE);
                    String id = String.valueOf(issueDetail.get(ES_DOC_ID_KEY));
                    String routing = String.valueOf(issueDetail.get(ES_DOC_ROUTING_KEY));
                    String parent = String.valueOf(issueDetail.get(ES_DOC_PARENT_KEY));
                    Map<String, Object> partialDocument = Maps.newHashMap();
                    partialDocument.put(ISSUE_STATUS, CLOSED);
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    partialDocument.put(MODIFIED_DATE, sdf.format(new Date()));
                    partialDocument.put("reasonForCloser", "Rule disabled by : " + ruleDetails.getUserId());
                    status = elasticSearchRepository.updatePartialDataToES(dataSource, targetType, id, routing, parent,
                            partialDocument);
                }
            }
            return status;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    /**
     * Gets the open issue details by rule id.
     *
     * @author Nidhish Krishnan (Nidhish)
     * @param ruleId
     *            the rule id
     * @return List<Map<String, Object>>
     * @throws Exception
     *             the exception
     * @requestParam ruleId
     */
    private List<Map<String, Object>> getOpenIssueDetailsByRuleId(final String ruleId) throws Exception {
        Map<String, Object> mustFilter = Maps.newHashMap();
        List<Map<String, Object>> resourceDetList = Lists.newArrayList();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), ruleId);
        mustFilter.put(TYPE, ISSUE);
        try {
            resourceDetList = elasticSearchRepository.getSortedDataFromES(AWS, null, mustFilter, null, null, null,
                    null, null);
        } catch (Exception e) {
            throw new DataException(NO_DATA_FOUND);
        }
        return resourceDetList;
    }

    /**
     * Gets the issue details.
     *
     * @author NidhishKrishnan (Nidhish)
     * @param issueId
     *            the issue id
     * @param status
     *            the status
     * @return It return list of map details for the given issueId and status
     * @throws DataException
     *             the data exception
     * @requestParam issueId - String
     * @requestParam status - String
     */
    private List<Map<String, Object>> getIssueDetails(final String issueId, final String status) throws DataException {
        Map<String, Object> mustFilter = Maps.newHashMap();
        List<Map<String, Object>> resourceDetList;
        if (!Strings.isNullOrEmpty(issueId) && !Strings.isNullOrEmpty(status)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), status);
            mustFilter.put(ES_DOC_ID_KEY, issueId);
        } else {
            mustFilter.put(CommonUtils.convertAttributetoKeyword("issueId"), issueId);
        }
        try {
            resourceDetList = elasticSearchRepository.getSortedDataFromES(AWS, null, mustFilter, null, null, null,
                    null, null);
        } catch (Exception e) {
            throw new DataException(e);
        }
        return resourceDetList;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * revokeAndUpdateIssueDetails(java.lang.String)
     */
    @Override
    public Boolean revokeAndUpdateIssueDetails(String issueId) throws DataException {
        try {
            List<Map<String, Object>> issueDetails = getExemptedIssueDetails(issueId);
            Boolean isUpdated = false;
            if (!issueDetails.isEmpty()) {
                Map<String, Object> issueDetail = issueDetails.get(0);
                Map<String, Object> issueExceptionDetails = Maps.newHashMap();
                issueExceptionDetails.put(TARGET_TYPE, String.valueOf(issueDetail.get(TARGET_TYPE)));
                issueExceptionDetails.put("source", "issueException");
                String dataSource = issueDetail.get(PAC_DS) + "_" + issueDetail.get(TARGET_TYPE);
                String targetType = issueDetail.get(TYPE) + "_" + issueDetail.get(TARGET_TYPE);
                String id = String.valueOf(issueDetail.get(ES_DOC_ID_KEY));
                String routing = String.valueOf(issueDetail.get(ES_DOC_ROUTING_KEY));
                String parent = String.valueOf(issueDetail.get(ES_DOC_PARENT_KEY));
                Map<String, Object> partialDocument = Maps.newHashMap();
                partialDocument.put(ISSUE_STATUS, OPEN);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                partialDocument.put(MODIFIED_DATE, sdf.format(new Date()));
                partialDocument.put(STATUS, "revoked");
                isUpdated = elasticSearchRepository.updatePartialDataToES(dataSource, targetType, id, routing, parent,
                        partialDocument);
                if (isUpdated) {
                    try {
                        List<Map<String, Object>> exceptionDetails = getIssueDetails(issueId, null);
                        Map<String, Object> exceptionDetail = exceptionDetails.get(0);
                        String exceptionId = String.valueOf(exceptionDetail.get(ES_DOC_ID_KEY));
                        StringBuilder esQueryUrl = new StringBuilder(esUrl);
                        esQueryUrl.append("/").append(dataSource).append("/" + targetType).append("_exception/")
                                .append(exceptionId).append("?routing=" + routing);
                        HttpClient client = HttpClientBuilder.create().build();
                        HttpDelete httpdelete = new HttpDelete(esQueryUrl.toString());
                        httpdelete.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
                        client.execute(httpdelete);
                    } catch (Exception exception) {
                        logger.info("No Issue Exception found!!!" + exception);
                    }
                    return isUpdated;
                }
            }
            return false;
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getExemptedIssueDetails(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getExemptedIssueDetails(String issueId) throws DataException {
        return getIssueDetails(issueId, EXEMPTED);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getOpenIssueDetails(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getOpenIssueDetails(final String issueId) throws DataException {
        return getIssueDetails(issueId, OPEN);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleIdWithDisplayNameQuery(java.lang.String)
     */
    public List<Map<String, Object>> getRuleIdWithDisplayNameQuery(String targetType) {
        String ruleIdWithDisplayquery = "SELECT ruleId, displayName,targetType,ruleParams FROM cf_RuleInstance WHERE STATUS = 'ENABLED'AND targetType IN ("
                + targetType + ")";
        return rdsepository.getDataFromPacman(ruleIdWithDisplayquery);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleIDsForTargetType(java.lang.String)
     */
    public List<Map<String, Object>> getRuleIDsForTargetType(String targetType) throws DataException {
        String ruleIdWithDisplayquery = "SELECT ruleId, displayName,targetType FROM cf_RuleInstance WHERE STATUS = 'ENABLED'AND targetType ='"
                + targetType + "'";

        return rdsepository.getDataFromPacman(ruleIdWithDisplayquery);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleIdDetails(java.lang.String)
     */
    public List<Map<String, Object>> getRuleIdDetails(String ruleId) throws DataException {
        String ruleIdWithDisplayquery = "SELECT ruleId, displayName,targetType,ruleParams FROM cf_RuleInstance WHERE STATUS = 'ENABLED' AND ruleId IN ("
                + ruleId + ")";
        try {
            return rdsepository.getDataFromPacman(ruleIdWithDisplayquery);
        } catch (Exception e) {
            throw new DataException(e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.api.compliance.repository.ComplianceRepository#getRuleIds
     * (java.lang.String)
     */
    @Override
    public List<Object> getRuleIds(String targetType) {
        List<Object> rules = new ArrayList<>();
        List<Map<String, Object>> rulesList = getRuleIdWithDisplayNameQuery(targetType);
        for (Map<String, Object> rule : rulesList) {
            rules.add(rule.get(RULEID));
        }
        return rules;
    }

    /**
     * Gets the non compliance policy by es with asset group.
     *
     * @author santoshi
     * @param assetGroup
     *            the asset group
     * @param searchText
     *            the search text
     * @param filters
     *            the filters
     * @param from
     *            the from
     * @param size
     *            the size
     * @param targetTypes
     *            the target types
     * @return the non compliance policy by es with asset group
     * @throws DataException
     *             the data exception
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Long> getNonCompliancePolicyByEsWithAssetGroup(String assetGroup, String searchText,
            Map<String, String> filters, int from, int size, String targetTypes) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        List<Object> rules;

        Map.Entry pair;
        Iterator filterIterator;
        String ruleIdAggsFilterName = CommonUtils.convertAttributetoKeyword(RULEID);
        /*--if size is zero it will return all the Rules which has open+exmpted issues --*/

        rules = getRuleIds(targetTypes);
        int esSize = 0;
        if (size == 0) {
            esSize = 1000;
        }

        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), Constants.ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), rules);
        /*--add filters to the Must Filters--*/
        if (MapUtils.isNotEmpty(filters)) {
            filterIterator = filters.entrySet().iterator();
            while (filterIterator.hasNext()) {
                pair = (Map.Entry) filterIterator.next();
                if (!DOMAIN.equalsIgnoreCase((pair.getKey().toString()))) {
                    mustFilter.put(pair.getKey().toString(), pair.getValue());
                }
            }
        }
        try {

            return elasticSearchRepository.getTotalDistributionForIndexAndTypeBySize(assetGroup, null, mustFilter,
                    mustNotFilter, null, ruleIdAggsFilterName, esSize, from, searchText);
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getTargetTypeForAG(java.lang.String, java.lang.String)
     */
    public String getTargetTypeForAG(String assetGroup, String domain) {

        String ttypesTemp;
        String ttypes = null;
        AssetApi assetApi = assetServiceClient.getTargetTypeList(assetGroup, domain);
        AssetApiData data = assetApi.getData();
        AssetCountDTO[] targetTypes = data.getTargettypes();
        for (AssetCountDTO name : targetTypes) {
            if (!Strings.isNullOrEmpty(name.getType())) {
                ttypesTemp = new StringBuilder().append('\'').append(name.getType()).append('\'').toString();
                if (Strings.isNullOrEmpty(ttypes)) {
                    ttypes = ttypesTemp;
                } else {
                    ttypes = new StringBuilder(ttypes).append(",").append(ttypesTemp).toString();
                }
            }
        }
        return ttypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRulesLastScanDate()
     */
    public List<Map<String, Object>> getRulesLastScanDate() throws DataException {
        String responseJson = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        HashMap<String, Object> ruleDetails;
        List<Map<String, Object>> rulesList = new ArrayList<>();
        JsonObject nameObj;
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/fre-stats/_search");
        StringBuilder requestBody = new StringBuilder(
                "{\"size\":0,\"query\":{},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"ruleId.keyword\",\"size\":1000},\"aggs\":{\"NAME\":{\"max\":{\"field\":\"startTime\"}}}}}}");
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
        } catch (Exception e) {
            logger.error(ERROR_IN_US, e);
            throw new DataException(e);
        }
        jsonParser = new JsonParser();
        resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get(AGGREGATIONS).toString());
        JsonArray buckets = aggsJson.getAsJsonObject("NAME").getAsJsonArray(BUCKETS);
        for (int i = 0; i < buckets.size(); i++) {
            ruleDetails = new HashMap<>();
            ruleDetails.put(RULEID, buckets.get(i).getAsJsonObject().get("key").getAsString());
            nameObj = (JsonObject) buckets.get(i).getAsJsonObject().get("NAME");
            ruleDetails.put(MODIFIED_DATE, nameObj.get("value_as_string").getAsString());
            rulesList.add(ruleDetails);
        }
        return rulesList;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.api.compliance.repository.ComplianceRepository#getScanDate
     * (java.lang.String, java.util.Map)
     */
    public String getScanDate(String ruleId, Map<String, String> rulidwithScanDate) {
        return rulidwithScanDate.get(ruleId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleCategory(java.lang.Object, java.util.Map)
     */
    public String getRuleCategory(Object ruleId, Map<String, String> ruleIdwithruleParamsMap) {
        String ruleCategory = null;
        String ruleParams = ruleIdwithruleParamsMap.get(ruleId);
        JsonParser parser = new JsonParser();
        List<Map<String, String>> paramsList;
        JsonObject ruleParamsJson;

        ruleParamsJson = (JsonObject) parser.parse(ruleParams);
        paramsList = new Gson().fromJson(ruleParamsJson.get(PARAMS), new TypeToken<List<Object>>() {
        }.getType());

        for (Map<String, String> param : paramsList) {
            if (param.get(KEY).equalsIgnoreCase(RULE_CATEGORY)) {
                ruleCategory = param.get(VALUE);
                return ruleCategory;
            }
        }

        return ruleCategory;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleDetailsByApplicationFromES(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    public JsonArray getRuleDetailsByApplicationFromES(String assetGroup, String ruleId, String searchText)
            throws DataException {
        String responseJson = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        StringBuilder requestBody = null;
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
                .append(SEARCH);
        requestBody = new StringBuilder(
                "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"ruleId.keyword\":{\"value\":\""
                        + ruleId + "\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}");
        if (!StringUtils.isEmpty(searchText)) {
            requestBody.append(",{\"match_phrase_prefix\":{\"_all\":\"" + searchText + "\"}}");
        }
        // additional filters for kernel compliance rule
        if (EC2_KERNEL_COMPLIANCE_RULE.equalsIgnoreCase(ruleId)) {
            requestBody
                    .append(",{\"has_parent\":{\"parent_type\":\"ec2\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"statename\":\"running\"}}],\"must_not\":[{\"match\":{\"platform\":\"windows\"}}]}}}}");
        }
        requestBody.append("]");
        // additional filters for Tagging compliance rule
        if (ruleId.contains(TAGGING_POLICY)) {
            List<String> tagsList = new ArrayList<>(Arrays.asList(mandatoryTags.split(",")));
            if (!tagsList.isEmpty()) {
                requestBody = requestBody.append(",\"should\":[");
                for (String tag : tagsList) {
                    requestBody = requestBody.append("{\"match_phrase_prefix\":{\"missingTags\":\"" + tag + "\"}},");
                }
                requestBody.setLength(requestBody.length() - 1);
                requestBody.append("]");
                requestBody.append(",\"minimum_should_match\":1");
            }
        }
        requestBody
                .append("}},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":1000}}}}");
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
        } catch (Exception e) {
            logger.error(ERROR_IN_US, e);
            throw new DataException(e);
        }
        jsonParser = new JsonParser();
        resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get(AGGREGATIONS).toString());
        return aggsJson.getAsJsonObject("NAME").getAsJsonArray(BUCKETS);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getAllApplicationsAssetCountForTargetType(java.lang.String,
     * java.lang.String)
     */
    public Map<String, Long> getAllApplicationsAssetCountForTargetType(String assetGroup, String targetType) {
        Map<String, Long> map = new HashMap<>();
        AssetCount totalAssets = assetServiceClient.getTotalAssetsCountByApplication(assetGroup, targetType);
        AssetCountData data = totalAssets.getData();
        AssetCountByAppEnvDTO[] assetcount = data.getAssetcount();
        for (AssetCountByAppEnvDTO assetCount_Count : assetcount) {
            map.put(assetCount_Count.getApplication(), Long.parseLong(assetCount_Count.getCount()));
        }
        return map;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getTargetTypeByRuleId(java.lang.String)
     */
    public List<Map<String, Object>> getTargetTypeByRuleId(String ruleId) {
        String ruleIdWithTargetTypeQuery = "SELECT rule.targetType FROM cf_RuleInstance rule LEFT JOIN cf_Policy policy ON rule.policyId = policy.policyId WHERE rule.status = 'ENABLED' AND rule.ruleId ='"
                + ruleId + "'";
        return rdsepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getTotalAssetCountForEnvironment(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public Long getTotalAssetCountForEnvironment(String assetGroup, String application, String environment,
            String targetType) {
        AssetCount totalAssets = assetServiceClient.getTotalAssetsCountByEnvironment(assetGroup, application,
                targetType);
        AssetCountData data = totalAssets.getData();
        AssetCountByAppEnvDTO[] assetcount = data.getAssetcount();
        Long totalAssetsCount = 0l;
        for (AssetCountByAppEnvDTO assetCount_Count : assetcount) {
            if (assetCount_Count.getApplication().equals(application)) {
                for (AssetCountEnvCount envCount_Count : assetCount_Count.getEnvironments()) {
                    if (envCount_Count.getEnvironment().equals(environment)) {
                        totalAssetsCount = Long.parseLong(envCount_Count.getCount());
                    }
                }
            }
        }
        return totalAssetsCount;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleDetailsByEnvironmentFromES(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public JsonArray getRuleDetailsByEnvironmentFromES(String assetGroup, String ruleId, String application,
            String searchText,String targetType) throws DataException {
        String responseJson = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        StringBuilder requestBody = null;
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
                .append(SEARCH);
        
if (ruleId.contains(TAGGIG_POLICY)) {
        	
        	List<String> tagsList = new ArrayList<>(Arrays.asList(mandatoryTags.split(",")));

            String body = "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"ruleId.keyword\":{\"value\":\""+ruleId+"\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}";

            //Added resourceType to the Query
            char ch='"';
            targetType = ch+targetType+ch;
            
            body = body + ",{\"terms\":{\"targetType.keyword\":["+targetType+"]}}";
            if(application!=null){
            	  body = body + ",{\"match\":{\"tags.Application.keyword\":\""+application+"\"}}";
            }
            body = body + "]";
            if (!tagsList.isEmpty()) {
                body = body + ",\"should\":[";

                for (String tag : tagsList) {
                    body = body + "{\"match_phrase_prefix\":{\"missingTags\":\"" + tag + "\"}},";
                }
                body = body.substring(0, body.length() - 1);
                body = body + "]";
                body = body + ",\"minimum_should_match\":1";
            }
            body = body + "}},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"tags.Environment.keyword\",\"size\":1000000}}}}";
            requestBody = new StringBuilder(body);
           
        }else{

        if (StringUtils.isEmpty(searchText)) {
            requestBody = new StringBuilder(
                    "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"ruleId.keyword\":{\"value\":\""
                            + ruleId
                            + "\"}}},{\"term\":{\"tags.Application.keyword\":{\"value\":\""
                            + application
                            + "\"}}}],\"should\":[{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"exempted\"}}}],\"minimum_should_match\":1}},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"tags.Environment.keyword\",\"size\":1000}}}}");
        } else {
            requestBody = new StringBuilder(
                    "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"ruleId.keyword\":{\"value\":\""
                            + ruleId
                            + "\"}}},{\"term\":{\"tags.Application.keyword\":{\"value\":\""
                            + application
                            + "\"}}},{\"match_phrase_prefix\":{\"_all\":\""
                            + searchText
                            + "\"}}],\"should\":[{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"exempted\"}}}],\"minimum_should_match\":1}},\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"tags.Environment.keyword\",\"size\":1000}}}}");
        }
    }
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
        } catch (Exception e) {
            logger.error(ERROR_IN_US, e);
            throw new DataException(e);
        }
        jsonParser = new JsonParser();
        resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get(AGGREGATIONS).toString());
        return aggsJson.getAsJsonObject("NAME").getAsJsonArray(BUCKETS);
    }

    /**
     * Gets the events processed from es.
     *
     * @return the events processed from es
     * @throws Exception
     *             the exception
     */
    public JsonArray getEventsProcessedFromEs() throws Exception {
        return heimdallElasticSearchRepository.getEventsProcessed();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleDescriptionFromDb(java.lang.String)
     */
    public List<Map<String, Object>> getRuleDescriptionFromDb(String ruleId) throws DataException {
        String policyDescQuery = "SELECT rule.displayName, policy.resolution, policy.policyDesc, policy.policyVersion, rule.ruleParams FROM cf_RuleInstance rule LEFT JOIN cf_Policy policy ON rule.policyId = policy.policyId WHERE rule.status = 'ENABLED' AND rule.ruleId ='"
                + ruleId + "'";
        return rdsepository.getDataFromPacman(policyDescQuery);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getKernelComplianceByInstanceIdFromDb(java.lang.String)
     */
    public List<Map<String, Object>> getKernelComplianceByInstanceIdFromDb(String instanceId) throws DataException {
        String rhnKernelVersionQuery = "SELECT rhn.kernelVersion FROM cf_RhnSystemDetails rhn WHERE rhn.instanceId= '"
                + instanceId + "' AND rhn.userName ='webservice'";
        return rdsepository.getDataFromPacman(rhnKernelVersionQuery);
    }

    /**
     * Gets the issue list.
     *
     * @param issueDetail
     *            the issue detail
     * @param sourceMap
     *            the source map
     * @param ruleIdwithDisplayNameMap
     *            the rule idwith display name map
     * @param issueList
     *            the issue list
     * @param domain
     *            the domain
     * @return the issue list
     * @throws DataException
     *             the data exception
     */
    private List<LinkedHashMap<String, Object>> getIssueList(Map<String, Object> issueDetail,
            Map<String, Object> sourceMap, Map<String, String> ruleIdwithDisplayNameMap,
            List<LinkedHashMap<String, Object>> issueList, String domain){
        Map<String, Object> nonDisplayable = new HashMap<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        if (!Strings.isNullOrEmpty(ruleIdwithDisplayNameMap.get(sourceMap.get(RULEID)))) {
            issue.put(POLICY_DISPLAY_NAME, ruleIdwithDisplayNameMap.get(sourceMap.get(RULEID)));
            issue.put(ISSUE_ID, sourceMap.get(_ID));
            issue.put(RESOURCE_DISPLAY_ID, sourceMap.get(RESOURCEID));
            issue.put(SEVERITY_DISPALY_NAME, sourceMap.get(SEVERITY));
            issue.put(RULECATEGORY_DISPALY_NAME, sourceMap.get(RULE_CATEGORY));

            if (domain.equals(INFRA_AND_PLATFORMS)) {
                issue.put(ACCOUNT_DISPALY_NAME, sourceMap.get(ACCOUNT_NAME));
                issue.put(ACCOUNT_DISPLAYI_D, sourceMap.get(ACCOUNT_ID));
                issue.put(REGION_DISPALY_NAME, sourceMap.get(REGION));
                if (issueDetail != null) {
                    issue.put(APPLICATION, "");
                    issue.put(ENVIRONMENT, "");
                } else {
                    issue.put(APPLICATION, sourceMap.get(TAGS_APPLICATION));
                    issue.put(ENVIRONMENT, sourceMap.get(TAGS_ENVIRONMENT));
                }
            }
            issue.put(CREATED_DISPLAY_DATE, sourceMap.get(CREATED_DATE));
            issue.put(MODIFIED_DISPLAY_DATE, sourceMap.get(MODIFIED_DATE));
            issue.put(STATUS_DISPLAY_NAME, sourceMap.get(ISSUE_STATUS));
            issue.put(RESOURCE_TYPE, sourceMap.get(TARGET_TYPE));
            issue.put(DESCRIPTION, sourceMap.get(DESC));
            if (domain.equals(SOX)) {
                issue.put(ENV, sourceMap.get(ENV));
            }
            nonDisplayable.put(POLICY_DISPLAY_ID, sourceMap.get(POLICYID));
            nonDisplayable.put(RULE_DISPLAY_ID, sourceMap.get(RULEID));
            issue.put("nonDisplayableAttributes", nonDisplayable);
            issueList.add(issue);
        }
        return issueList;
    }

    /**
     * Gets the untagged count.
     *
     * @param esUrl
     *            the es url
     * @param assetGroup
     *            the asset group
     * @param ruleId
     *            the rule id
     * @param tagsList
     *            the tags list
     * @return the untagged count
     * @throws Exception
     *             the exception
     */
    private static int getUntaggedCount(String esUrl, String assetGroup, String ruleId, List<String> tagsList)
            throws Exception {
        int size = 0;
        Gson serializer = new GsonBuilder().create();
        String body = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"policyId.keyword\":{\"value\":\""
                + TAGGIG_POLICY + "\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}";
        body = body + ",{\"term\":{\"ruleId.keyword\":{\"value\":\"" + ruleId + "\"}}}";

        body = body + "]";
        if (!tagsList.isEmpty()) {
            body = body + ",\"should\":[";

            for (String tag : tagsList) {
                body = body + "{\"match_phrase_prefix\":{\"missingTags\":\"" + tag + "\"}},";
            }
            body = body.substring(0, body.length() - 1);
            body = body + "]";
            body = body + ",\"minimum_should_match\":1";
        }
        body = body + "}}}";
        StringBuilder requestBody = new StringBuilder(body);
        StringBuilder urlToQueryCountBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
                .append(UNDERSCORE_COUNT);
        String responseCount = PacHttpUtils.doHttpPost(urlToQueryCountBuffer.toString(), requestBody.toString());
        Map<String, Object> responseCountMap = (Map<String, Object>) serializer.fromJson(responseCount, Object.class);
        if (responseCountMap.containsKey(COUNT)) {
            Double sizeD = (Double) responseCountMap.get(COUNT);
            size = sizeD.intValue();
        }
        return size;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * fetchSystemConfiguration(java.lang.String)
     */
    public String fetchSystemConfiguration(final String keyname) {
        String query = "SELECT value FROM cf_SystemConfiguration WHERE keyname =\"" + keyname + "\"";
        try {
            return rdsepository.queryForString(query);
        } catch (Exception exception) {
            logger.error(exception);
            return StringUtils.EMPTY;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * updateKernelVersion
     * (com.tmobile.pacman.api.compliance.domain.KernelVersion)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> updateKernelVersion(final KernelVersion kernelVersion) {
        Map<String, Object> response = Maps.newHashMap();
        RhnSystemDetails systemDetails = null;
        Map<String, String> kernelCriteriaMapCurrent = buildCriteriaMap(getCurrentQuarterCriteriaKey());
        Set<String> kernelCriteriaMapkeysCurrent = kernelCriteriaMapCurrent.keySet();
        try {
            if ((kernelVersion.getInstanceId() != null && !"".equals(kernelVersion.getInstanceId()))
                    && (kernelVersion.getKernelVersionId() != null && !"".equals(kernelVersion.getKernelVersionId()))) {
                Boolean isKernelComplaint = isCompliant(kernelVersion.getKernelVersionId(),
                        kernelCriteriaMapkeysCurrent, kernelCriteriaMapCurrent);
                systemDetails = rhnSystemDetailsRepository.findRhnSystemDetailsByInstanceId(kernelVersion
                        .getInstanceId());
                if (systemDetails != null) {
                    systemDetails.setKernelVersion(kernelVersion.getKernelVersionId());
                    systemDetails.setIsKernelCompliant(isKernelComplaint);
                    systemDetails.setModifiedDate(new Date());
                    systemDetails.setUserName("webservice");
                    rhnSystemDetailsRepository.save(systemDetails);
                    response.put(MESSAGE_KEY, "Kernel Version Updated Successfully!!!");
                    response.put(STATUS, true);
                    return response;
                } else {
                    String instanceIdSearchQuery = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"latest\":{\"value\":\"true\"}}},{\"term\":{\"instanceid.keyword\":{\"value\":\""
                            + kernelVersion.getInstanceId() + "\"}}}]}}}";
                    StringBuilder urlToQueryinstanceId = new StringBuilder(esUrl).append("/").append("aws_ec2/ec2")
                            .append("/").append(UNDERSCORE_COUNT);
                    String instanceDetails = PacHttpUtils.doHttpPost(urlToQueryinstanceId.toString(),
                            instanceIdSearchQuery);
                    Gson serializer = new GsonBuilder().create();
                    Map<String, Object> instanceDetailsMap = (Map<String, Object>) serializer.fromJson(instanceDetails,
                            Object.class);
                    double count = (double) instanceDetailsMap.get(COUNT);
                    if (count >= 1) {
                        systemDetails = new RhnSystemDetails();
                        systemDetails.setGroupId(0L);
                        systemDetails.setCompanyId(0L);
                        systemDetails.setUserId(0L);
                        systemDetails.setUserName("webservice");
                        systemDetails.setCreateDate(new Date());
                        systemDetails.setModifiedDate(new Date());
                        systemDetails.setSystemId(0);
                        systemDetails.setKernelVersion(kernelVersion.getKernelVersionId());
                        systemDetails.setInstanceId(kernelVersion.getInstanceId());
                        systemDetails.setLastCheckedIn(null);
                        systemDetails.setOs("");
                        systemDetails.setIsKernelCompliant(isKernelComplaint);
                        rhnSystemDetailsRepository.save(systemDetails);
                        response.put(MESSAGE_KEY, "Successfully added new Kernel Version for the InstanceId!!!");
                        response.put(STATUS, true);
                        return response;
                    } else {
                        response.put(MESSAGE_KEY, "InstanceId not Found!!!");
                        response.put(STATUS, false);
                        return response;
                    }
                }
            } else {
                response.put(MESSAGE_KEY, "Kernel Version or InstanceId cannot be null!!!");
                response.put(STATUS, false);

                return response;
            }
        } catch (Exception exception) {
            logger.error(exception);
            response.put(MESSAGE_KEY, "Unexpected Error Occurred!!!");
            response.put(STATUS, false);
            return response;
        }
    }

    /**
     * Builds the criteria map.
     *
     * @param compCriteriaMap
     *            the comp criteria map
     * @return the map
     */
    private Map<String, String> buildCriteriaMap(String compCriteriaMap) {
        Map<String, String> kernelCriteriaMap = new TreeMap<>();
        try {
            String kernelSriteriaString = fetchSystemConfiguration(compCriteriaMap);
            StringTokenizer tokens = new StringTokenizer(kernelSriteriaString, "|");
            StringTokenizer keyValue;
            logger.info("criteria string -- >" + kernelSriteriaString);
            while (tokens.hasMoreTokens()) {
                keyValue = new StringTokenizer(tokens.nextToken(), "#");
                kernelCriteriaMap.put(keyValue.nextToken(), keyValue.nextToken());
            }
            logger.info("criteria map" + kernelCriteriaMap);
            return kernelCriteriaMap;
        } catch (Exception e) {
            logger.error("error parsing 'pacman.kernel.compliance.map' from system configuration : " , e);
            return new TreeMap<>();
        }
    }

    /**
     * Checks if is compliant.
     *
     * @param version
     *            the version
     * @param criteriaMapkeys
     *            the criteria mapkeys
     * @param kernelCriteriaMapDetails
     *            the kernel criteria map details
     * @return the boolean
     */
    private Boolean isCompliant(String version, Set<String> criteriaMapkeys,
            Map<String, String> kernelCriteriaMapDetails) {
        String criteria = getComplianceCriteriaFor(version, criteriaMapkeys, kernelCriteriaMapDetails);
        if (criteria == null || criteria.equals(StringUtils.EMPTY)) {
            return Boolean.FALSE;
        }
        DefaultArtifactVersion minVersion = new DefaultArtifactVersion(criteria);
        return minVersion.compareTo(new DefaultArtifactVersion(version)) <= 0;
    }

    /**
     * Gets the compliance criteria for.
     *
     * @param version
     *            the version
     * @param criteriaMapkeys
     *            the criteria mapkeys
     * @param kernelCriteriaMapDetails
     *            the kernel criteria map details
     * @return the compliance criteria for
     */
    private String getComplianceCriteriaFor(String version, Set<String> criteriaMapkeys,
            Map<String, String> kernelCriteriaMapDetails) {
        for (String key : criteriaMapkeys) {
            if (version.contains(key)) {
                return kernelCriteriaMapDetails.get(key);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Gets the current quarter criteria key.
     *
     * @return the current quarter criteria key
     */
    private static String getCurrentQuarterCriteriaKey() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int currentQuarter = (month / 3) + 1;
        return "pacman.kernel.compliance.map".concat(".").concat(String.valueOf(year)).concat(".q")
                .concat(String.valueOf(currentQuarter));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleCategoryWeightagefromDB(java.lang.String)
     */
    public Map<String, Object> getRuleCategoryWeightagefromDB(String domain) throws DataException {
        Map<String, Object> weightage = null;
        String query = "SELECT * FROM pac_v2_ruleCategory_weightage WHERE domain ='" + domain + "'";
        List<Map<String, Object>> ruleCatWeightageList = rdsepository.getDataFromPacman(query);
        if (!ruleCatWeightageList.isEmpty()) {
            weightage = ruleCatWeightageList.parallelStream().collect(
                    Collectors.toMap(cats -> cats.get(RULE_CATEGORY).toString(), cats -> cats.get("weightage")));
        }
        return weightage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
	 * getTaggingByAG(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getTaggingByAG(String assetGroup, String targetTypes, String application)
			throws DataException {
		List<String> targetTypeList = Arrays.asList(targetTypes.split("\\s*,\\s*"));

		Gson gson = new GsonBuilder().create();
		String responseDetails = null;
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup).append("/")
				.append(SEARCH);
		StringBuilder requestBody = null;
		List<String> tagsList = new ArrayList<>(Arrays.asList(mandatoryTags.split(",")));

		String body = "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.keyword\":{\"value\":\"issue\"}}},{\"term\":{\"policyId.keyword\":{\"value\":\"PacMan_TaggingRule_version-1\"}}},{\"term\":{\"issueStatus.keyword\":{\"value\":\"open\"}}}";

		// Added resourceType to the Query
		String targetTypesTerms = targetTypes.replaceAll("'", "\"");
		body = body + ",{\"terms\":{\"targetType.keyword\":[" + targetTypesTerms + "]}}";
		if (application != null) {
			body = body + ",{\"match\":{\"tags.Application.keyword\":\"" + application + "\"}}";
		}
		body = body + "]";
		if (!tagsList.isEmpty()) {
			body = body + ",\"should\":[";

			for (String tag : tagsList) {
				body = body + "{\"match_phrase_prefix\":{\"missingTags\":\"" + tag + "\"}},";
			}
			body = body.substring(0, body.length() - 1);
			body = body + "]";
			body = body + ",\"minimum_should_match\":1";
		}
		body = body + "}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"targetType.keyword\",\"size\":"
				+ targetTypeList.size() + "}}}}";
		requestBody = new StringBuilder(body);
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
		} catch (Exception e) {
			throw new DataException(e);
		}
		Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Map.class);
		Map<String, Object> aggregations = (Map<String, Object>) response.get(AGGREGATIONS);
		Map<String, Object> name = (Map<String, Object>) aggregations.get("name");
		List<Map<String, Object>> buckets = (List<Map<String, Object>>) name.get(BUCKETS);

		return buckets.parallelStream().filter(buket -> buket.get("doc_count") != null)
				.collect(Collectors.toMap(buket -> buket.get("key").toString(), buket -> buket.get("doc_count"),
						(oldValue, newValue) -> newValue));
	}

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getPolicyViolationDetailsByIssueId(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Object> getPolicyViolationDetailsByIssueId(String assetGroup, String issueId)
            throws DataException {
    	Map<String, Object> issueDetails = new HashMap<>();
		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(assetGroup);
		urlToQuery.append("/").append(SEARCH);

		StringBuilder requestBody = new StringBuilder();
		requestBody.append("{\"query\":{\"bool\":{\"must\":[{\"match\":{\"type\":\"issue\"}},{\"match\":{\"_id\":\"")
				.append(issueId).append("\"}}]}}}");
        String responseJson = "";
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataException("No response found!!" + e);
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);

        if (resultJson != null) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(HITS).toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray(HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get("_source").getAsJsonObject();
                issueDetails.put(RULEID, source.get(RULEID).getAsString());
                issueDetails.put(RESOURCEID, source.get(RESOURCEID).getAsString());
                issueDetails.put(TARGET_TYPE, source.get(TARGET_TYPE).getAsString());
                issueDetails.put(ISSUE_STATUS, source.get(ISSUE_STATUS).getAsString());
                issueDetails.put(RULE_CATEGORY, source.get(RULE_CATEGORY).getAsString());
                issueDetails.put(SEVERITY, source.get(SEVERITY).getAsString());
                issueDetails.put(POLICYID, source.get(POLICYID).getAsString());
                issueDetails.put(CREATED_DATE, source.get(CREATED_DATE).getAsString());
                issueDetails.put(MODIFIED_DATE, source.get(MODIFIED_DATE).getAsString());
                if(source.has("desc")){
                    issueDetails.put(ISSUE_REASON, source.get("desc").getAsString());
                   }else{
                       JsonObject message = (JsonObject) jsonParser.parse(source.get("message").getAsString());
                       issueDetails.put(ISSUE_REASON, message.get("desc").getAsString());
                   }
                if (null != source.get(ISSUE_DETAILS)) {
                    issueDetails.put(ISSUE_DETAILS, source.get(ISSUE_DETAILS).getAsString());
                }
            }
        }
        return issueDetails;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getPatchableAssetsByApplication(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    public Map<String, Long> getPatchableAssetsByApplication(String assetGroup, String application, String resourceType)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        Map<String, Long> patchableassetsByApp = null;
        mustFilter.put(LATEST, true);
        String aggsFilter = "tags.Application.keyword";
        if (!Strings.isNullOrEmpty(application)) {
            mustFilter.put(TAGS_APPLICATION, application);
        }
        try {
            if ("ec2".equals(resourceType)) {
                mustFilter.put(STATE_NAME, RUNNING);
                mustNotFilter.put(PLATFORM, WINDOWS);

                patchableassetsByApp = elasticSearchRepository.getTotalDistributionForIndexAndType(assetGroup, "ec2",
                        mustFilter, mustNotFilter, null, aggsFilter, 1000, null);
            } else if (ONPREMSERVER.equals(resourceType)) {
                mustFilter.put(INSCOPE, true);
                patchableassetsByApp = elasticSearchRepository.getTotalDistributionForIndexAndType(assetGroup,
                        ONPREMSERVER, mustFilter, null, null, aggsFilter, 5000, null);
            }
        } catch (Exception e) {
            throw new DataException(e);
        }
        if (null != patchableassetsByApp && patchableassetsByApp.isEmpty()) {
            throw new DataException(NO_DATA_FOUND);
        }
        return patchableassetsByApp;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getRuleIdWithDisplayNameWithRuleCategoryQuery(java.lang.String,
     * java.lang.String)
     */
    public List<Map<String, Object>> getRuleIdWithDisplayNameWithRuleCategoryQuery(String targetType,
            String ruleCategory) throws DataException {
        String ruleIdWithDisplayquery = "SELECT ruleId, displayName,targetType,ruleParams FROM cf_RuleInstance WHERE STATUS = 'ENABLED'AND targetType IN ("
                + targetType + ") AND `ruleParams` LIKE '%" + ruleCategory + "%'";
        try {
            return rdsepository.getDataFromPacman(ruleIdWithDisplayquery);
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * getPatchabeAssetsCount(java.lang.String, java.lang.String)
     */
    public Long getPatchabeAssetsCount(String assetGroup, String targetType) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = null;
        mustFilter.put(LATEST, true);
        if (EC2.equalsIgnoreCase(targetType)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(STATE_NAME), RUNNING);
            mustNotFilter = new HashMap<>();
            mustNotFilter.put(CommonUtils.convertAttributetoKeyword(PLATFORM), WINDOWS);
        }
        try {
            return elasticSearchRepository.getTotalDocumentCountForIndexAndType(assetGroup, targetType, mustFilter,
                    mustNotFilter, null, null, null);
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
	 * getUnpatchedAssetsCount(java.lang.String, java.lang.String)
	 */
	public Long getUnpatchedAssetsCount(String assetGroup, String targetType, String application) throws DataException {
		String policyId = null;
		if (EC2.equalsIgnoreCase(targetType) || VIRTUALMACHINE.equalsIgnoreCase(targetType)) {
			policyId = CLOUD_KERNEL_COMPLIANCE_POLICY;
		}

		Map<String, Object> mustFilter = formatUnpatchedMustFilter(targetType, policyId);
		if (StringUtils.isNotBlank(application)) {
			mustFilter.put(TAGS_APPS, application);
		}
		String type = ISSUE_UNDERSCORE + targetType;
		try {
			return elasticSearchRepository.getTotalDocumentCountForIndexAndType(assetGroup, type, mustFilter, null,
					null, null, null);
		} catch (Exception e) {
			throw new DataException("" + e);
		}
	}

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
     * formatUnpatchedMustFilter(java.lang.String, java.lang.String)
     */
    public Map<String, Object> formatUnpatchedMustFilter(String targetType, String ruleId) {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TYPE), Constants.ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), ruleId);
        mustFilter.put("has_parent",

        patchingRepository.addParentConditionPatching(targetType));
        return mustFilter;
    }

    public Map<String, Long> getRulesDistribution(String assetGroup, String domain, List<Object> rules,
            String aggsFiltername) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();

        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), rules);
        String aggsFilterSeverity = CommonUtils.convertAttributetoKeyword(aggsFiltername);

        try {
            return elasticSearchRepository.getTotalDistributionForIndexAndType(assetGroup, null, mustFilter,
                    mustNotFilter, shouldFilter, aggsFilterSeverity, 0, mustTermsFilter);
        } catch (Exception e) {
            throw new DataException();
        }

    }

    public Map<String, Object> getRuleCategoryPercentage(Map<String, Long> ruleCategoryDistribution, Long totalIssues) {
        Iterator distributionByCat;
        Map.Entry pair;
        double value;
        int size;
        double totalPpercentage = 0;
        Map<String, Object> ruleCategoryPercentage = new HashMap<>();
        distributionByCat = ruleCategoryDistribution.entrySet().iterator();
        size = ruleCategoryDistribution.keySet().size();

        while (distributionByCat.hasNext()) {
            pair = (Map.Entry) distributionByCat.next();
            if (totalIssues > 0) {
                if (pair.getValue() != null) {
                    if (size == 1) {
                        ruleCategoryPercentage.put(pair.getKey().toString(), HUNDRED - totalPpercentage);
                    } else {
                        value = ((long) pair.getValue()) * HUNDRED / totalIssues;
                        value = Math.floor(value);
                        if (value > HUNDRED) {
                            value = INT_HUNDRED;
                        }
                        totalPpercentage = totalPpercentage + value;
                        ruleCategoryPercentage.put(pair.getKey().toString(), value);
                    }
                }
            } else {
                ruleCategoryPercentage.put(pair.getKey().toString(), 0);
            }
            size = size - 1;
        }
        return ruleCategoryPercentage;
    }

    private JsonObject getResopnse(String assetGroup, String apiType, String application, String environment,
			String resourceType) throws DataException {
		StringBuilder urlToQuery = formatURL(assetGroup, resourceType, apiType);
		String responseJson = "";
		try {
			responseJson = PacHttpUtils.doHttpPost(urlToQuery.toString(),
					getQueryForQualys(apiType, application, environment, resourceType).toString());
		} catch (Exception e) {
			logger.error(e.toString());
			throw new DataException(e.getMessage());
		}
		JsonParser jsonParser = new JsonParser();
		return (JsonObject) jsonParser.parse(responseJson);
	}


    public Long getInstanceCountForQualys(String assetGroup, String apiType, String application, String environment,
			String resourceType) throws DataException {
		return getResopnse(assetGroup, apiType, application, environment, resourceType).get(COUNT).getAsLong();
	}

    public Map<String, Long> getInstanceCountForQualysByAppsOrEnv(String assetGroup, String apiType, String application,
			String environment, String resourceType) throws DataException {
		Map<String, Long> assetWithTagsMap = new HashMap<>();
		JsonObject resultJson = getResopnse(assetGroup, apiType, application, environment, resourceType);

		JsonObject aggs = (JsonObject) resultJson.get(AGGREGATIONS);
		JsonObject name = (JsonObject) aggs.get("NAME");
		JsonArray buckets = name.get(BUCKETS).getAsJsonArray();
		// convert Json Array to Map object
		for (JsonElement bucket : buckets) {
			assetWithTagsMap.put(bucket.getAsJsonObject().get("key").getAsString(),
					bucket.getAsJsonObject().get(DOC_COUNT).getAsLong());
		}

		return assetWithTagsMap;
	}

	private StringBuilder getQueryForQualys(String apiType, String application, String environment,
			String resourceType) {
		StringBuilder requestBody = new StringBuilder();

		if (EC2.equals(resourceType)) {
			requestBody = new StringBuilder(
					"{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"statename.keyword\":\"running\"}}");
		} else if (VIRTUALMACHINE.equals(resourceType)) {
			requestBody = new StringBuilder(
					"{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"status.keyword\":\"running\"}}");
		}
		if (StringUtils.isNotBlank(application)) {
			requestBody.append(",{\"match\":{\"tags.Application.keyword\":\"" + application + "\"}}");
		}
		if (StringUtils.isNotBlank(environment)) {
			requestBody.append(",{\"match\":{\"tags.Environment.keyword\":\"" + environment + "\"}}");
		}
		requestBody.append(
				"],\"should\":[{\"script\":{\"script\":\"LocalDate.parse(doc['firstdiscoveredon.keyword'].value.substring(0,10)).isBefore(LocalDate.from(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault())).minusDays(7))\"}},{\"has_child\":{\"type\":\"qualysinfo\",\"query\":{\"match\":{\"latest\":\"true\"}}}}],\"minimum_should_match\":1}}");
		if ("noncompliancepolicy".equals(apiType)) {

			requestBody.append("}");
		} else if ("policydetailsbyapplication".equals(apiType)) {
			requestBody.append(
					",\"aggs\":{\"NAME\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":10000}}}}");
		} else if ("policydetailsbyenvironment".equals(apiType)) {

			if (EC2.equals(resourceType)) {
				requestBody = new StringBuilder(
						"{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"statename.keyword\":\"running\"}},{\"match\":{\"tags.Application.keyword\":\""
								+ application + "\"}},{\"match\":{\"tags.Environment.keyword\":\"" + environment
								+ "\"}}],\"should\":[{\"script\":{\"script\":\"LocalDate.parse(doc['firstdiscoveredon.keyword'].value.substring(0,10)).isBefore(LocalDate.from(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault())).minusDays(7))\"}},{\"has_child\":{\"type\":\"qualysinfo\",\"query\":{\"match\":{\"latest\":\"true\"}}}}],\"minimum_should_match\":1}}}");
			} else if (VIRTUALMACHINE.equals(resourceType)) {
				requestBody = new StringBuilder(
						"{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"status.keyword\":\"running\"}},{\"match\":{\"tags.Application.keyword\":\""
								+ application + "\"}},{\"match\":{\"tags.Environment.keyword\":\"" + environment
								+ "\"}}],\"should\":[{\"script\":{\"script\":\"LocalDate.parse(doc['firstdiscoveredon.keyword'].value.substring(0,10)).isBefore(LocalDate.from(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault())).minusDays(7))\"}},{\"has_child\":{\"type\":\"qualysinfo\",\"query\":{\"match\":{\"latest\":\"true\"}}}}],\"minimum_should_match\":1}}}");
			}
		}
		return requestBody;
	}

    private StringBuilder formatURL(String assetGroup, String resourcetype,String apiType) {
        StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(
                assetGroup);
        urlToQuery.append("/").append(resourcetype);
        if("noncompliancepolicy".equals(apiType) || "policydetailsbyenvironment".equals(apiType)){
        urlToQuery.append("/").append("_count");
        }else if("policydetailsbyapplication".equals(apiType)){
            urlToQuery.append("/").append("_search");
        }
        return urlToQuery;
    }

    @Override
    public IssueExceptionResponse exemptAndUpdateMultipleIssueDetails(IssuesException issuesException) throws DataException {

        IssueExceptionResponse issueExceptionResponse = new IssueExceptionResponse();
        String actionTemplateIssue = "{ \"update\" : { \"_id\" : \"%s\", \"_index\" : \"%s\", \"_type\" : \"%s\", \"_routing\" : \"%s\", \"_parent\" : \"%s\"} }%n";
        String actionTemplateException = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_routing\" : \"%s\"} }%n";
        List<String> issueIds = issuesException.getIssueIds();

        StringBuilder bulkRequest = new StringBuilder();
        Map<String,String> exceptions = new HashMap<>();
        List<String> failedIssueIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<Map<String, Object>> issueDetails = new ArrayList<>();
        try {
            issueDetails = getMultipleIssueDetails(issueIds, OPEN);
        } catch(DataException e) {
            logger.error("Error while fetching open issue details ", e);
            issueExceptionResponse.setStatus("Failed");
            issueExceptionResponse.setFailedIssueIds(issueIds);
            return issueExceptionResponse;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (!issueDetails.isEmpty()) {
            int i = 0;
            for(Map<String, Object> issueDetail : issueDetails) {
                try {
                    Map<String, Object> issueExceptionDetails = Maps.newHashMap();
                    issueExceptionDetails.put("issueId", issueDetail.get(ES_DOC_ID_KEY));
                    issueExceptionDetails.put("exceptionGrantedDate", sdf.format(issuesException.getExceptionGrantedDate()));
                    issueExceptionDetails.put("exceptionEndDate", sdf.format(issuesException.getExceptionEndDate()));
                    issueExceptionDetails.put("exceptionReason", issuesException.getExceptionReason());
                    issueExceptionDetails.put(TARGET_TYPE, String.valueOf(issueDetail.get(TARGET_TYPE)));
                    issueExceptionDetails.put("source", "issueException");
                    issueExceptionDetails.put("resourceId", String.valueOf(issueDetail.get(RESOURCEID)));
                    issueExceptionDetails.put("createdBy",issuesException.getCreatedBy());
                    String dataSource = issueDetail.get(PAC_DS) + "_" + issueDetail.get(TARGET_TYPE);
                    String targetType = issueDetail.get(TYPE) + "_" + issueDetail.get(TARGET_TYPE);
                    String id = String.valueOf(issueDetail.get(ES_DOC_ID_KEY));
                    String routing = String.valueOf(issueDetail.get(ES_DOC_ROUTING_KEY));
                    String parent = String.valueOf(issueDetail.get(ES_DOC_PARENT_KEY));
                    Map<String, Object> partialDocument = Maps.newHashMap();
                    partialDocument.put(ISSUE_STATUS, EXEMPTED);
                    partialDocument.put(MODIFIED_DATE, sdf.format(new Date()));
                    partialDocument.put(STATUS, "enforced");

                    StringBuilder exceptionDoc = new StringBuilder(createESDoc(issueExceptionDetails));
                    if (exceptionDoc != null) {
                        StringBuilder exceptionTarget = new StringBuilder(String.format(actionTemplateException, dataSource, "issue_"+issueDetail.get(TARGET_TYPE)+"_exception", routing)).append(exceptionDoc + "\n");
                        exceptions.put(id, exceptionTarget.toString());
                    }

                    Map<String, Object> issueDocument = Maps.newHashMap();
                    issueDocument.put("doc", partialDocument);
                    StringBuilder doc = new StringBuilder(createESDoc(issueDocument));
                    if (doc != null) {
                        bulkRequest.append(String.format(actionTemplateIssue, id, dataSource, targetType, routing, parent));
                        bulkRequest.append(doc + "\n");
                    }
                    i++;
                    if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                        logger.info("Uploading {}"+ i);
                        bulkUpload(errors,bulkRequest.toString());
                        bulkRequest = new StringBuilder();
                    }
                } catch (Exception e) {
                    throw new DataException(e);
                }
            }
            if (bulkRequest.length() > 0) {
                logger.info("Uploading {}"+ i);
                bulkUpload(errors,bulkRequest.toString());
            }

            if(!errors.isEmpty()) {
                failedIssueIds.addAll(fetchIdFromErrors(errors));
                issueIds.removeAll(failedIssueIds);
            }
            failedIssueIds.addAll(revokeException(issueIds));

            if(failedIssueIds.isEmpty()) {
                i = 0;
                for(Map.Entry<String,String> entry: exceptions.entrySet()) {
                    bulkRequest.append(entry.getValue());
                    i++;
                    if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                        logger.info("Uploading {}"+ i);
                        bulkUpload(errors,bulkRequest.toString());
                        bulkRequest = new StringBuilder();
                    }
                }
                if (bulkRequest.length() > 0) {
                    logger.info("Uploading {}"+ i);
                    bulkUpload(errors,bulkRequest.toString());
                }
            } else {
                errors = new ArrayList<>();
                i = 0;
                for(Map.Entry<String,String> entry: exceptions.entrySet()) {
                    if(!failedIssueIds.contains(entry.getKey())) {
                        bulkRequest.append(entry.getValue());
                        i++;
                        if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                            logger.info("Uploading {}"+ i);
                            bulkUpload(errors,bulkRequest.toString());
                            bulkRequest = new StringBuilder();
                        }
                    }
                }
                if (bulkRequest.length() > 0) {
                    logger.info("Uploading {}"+ i);
                    bulkUpload(errors,bulkRequest.toString());
                }
            }

            fetchIdFromErrors(errors).parallelStream().forEach(id -> {
                if(!failedIssueIds.contains(id)) {
                    synchronized (failedIssueIds) {
                        failedIssueIds.add(id);
                    }
                }
            });

        } else {
            failedIssueIds.addAll(issueIds);
        }

        if(failedIssueIds.isEmpty()) {
            issueExceptionResponse.setStatus("Success");
        } else if(issuesException.getIssueIds().size() == failedIssueIds.size()) {
            issueExceptionResponse.setStatus("Failed");
        } else {
            issueExceptionResponse.setStatus("Partial Success");
        }
        issueExceptionResponse.setFailedIssueIds(failedIssueIds);
        return issueExceptionResponse;
    }

    @Override
    public IssueExceptionResponse revokeAndUpdateMultipleIssueDetails(List<String> issueIds) throws DataException {

        String actionTemplateIssue = "{ \"update\" : { \"_id\" : \"%s\", \"_index\" : \"%s\", \"_type\" : \"%s\", \"_routing\" : \"%s\", \"_parent\" : \"%s\"} }%n";
        IssueExceptionResponse issueExceptionResponse = new IssueExceptionResponse();

        StringBuilder bulkRequest = new StringBuilder();
        List<String> failedIssueIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        List<Map<String, Object>> issueDetails = new ArrayList<>();
        try {
            issueDetails = getMultipleIssueDetails(issueIds, EXEMPTED);
        } catch(DataException e) {
            logger.error("Error while fetching exempted issue details ", e);
            issueExceptionResponse.setStatus("Failed");
            issueExceptionResponse.setFailedIssueIds(issueIds);
            return issueExceptionResponse;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (!issueDetails.isEmpty()) {
            int i = 0;
            for(Map<String, Object> issueDetail : issueDetails) {
                try {
                    String dataSource = issueDetail.get(PAC_DS) + "_" + issueDetail.get(TARGET_TYPE);
                    String targetType = issueDetail.get(TYPE) + "_" + issueDetail.get(TARGET_TYPE);
                    String id = String.valueOf(issueDetail.get(ES_DOC_ID_KEY));
                    String routing = String.valueOf(issueDetail.get(ES_DOC_ROUTING_KEY));
                    String parent = String.valueOf(issueDetail.get(ES_DOC_PARENT_KEY));
                    Map<String, Object> partialDocument = Maps.newHashMap();
                    partialDocument.put(ISSUE_STATUS, OPEN);
                    partialDocument.put(MODIFIED_DATE, sdf.format(new Date()));
                    partialDocument.put(STATUS, "revoked");

                    Map<String, Object> issueDocument = Maps.newHashMap();
                    issueDocument.put("doc", partialDocument);
                    StringBuilder doc = new StringBuilder(createESDoc(issueDocument));
                    if (doc != null) {
                        bulkRequest.append(String.format(actionTemplateIssue, id, dataSource, targetType, routing, parent));
                        bulkRequest.append(doc + "\n");
                    }
                    i++;
                    if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                        logger.info("Uploading {}"+ i);
                        bulkUpload(errors,bulkRequest.toString());
                        bulkRequest = new StringBuilder();
                    }
                } catch (Exception e) {
                    throw new DataException(e);
                }
            }
            if (bulkRequest.length() > 0) {
                logger.info("Uploading {}"+ i);
                bulkUpload(errors,bulkRequest.toString());
            }

            if(!errors.isEmpty()) {
                failedIssueIds.addAll(fetchIdFromErrors(errors));
                issueIds.removeAll(failedIssueIds);
            }

            failedIssueIds.addAll(revokeException(issueIds));
        } else {
            failedIssueIds.addAll(issueIds);
        }


        if(failedIssueIds.isEmpty()) {
            issueExceptionResponse.setStatus("Success");
        } else if(issueIds.size() == failedIssueIds.size()) {
            issueExceptionResponse.setStatus("Failed");
        } else {
            issueExceptionResponse.setStatus("Partial Success");
        }
        issueExceptionResponse.setFailedIssueIds(failedIssueIds);
        return issueExceptionResponse;
    }

    private List<Map<String, Object>> getMultipleIssueDetails(final List<String> issueIds, final String status) throws DataException {
        Map<String, Object> mustFilter = Maps.newHashMap();
        Map<String, Object> mustFilterTerms = Maps.newHashMap();
        List<Map<String, Object>> resourceDetList =  new ArrayList<>();
        if (!CollectionUtils.isEmpty(issueIds) && !Strings.isNullOrEmpty(status)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), status);
            mustFilterTerms.put(ES_DOC_ID_KEY, issueIds);
        } else {
            mustFilterTerms.put(CommonUtils.convertAttributetoKeyword("issueId"), issueIds);
        }

        try {
            if(mustFilter.isEmpty()) {
                StringBuilder urlToQuery = new StringBuilder(esUrl).append("/aws/_search");
                String responseJson = "";
                try {
                    StringBuilder requestBody = new StringBuilder("{\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"terms\":{\"issueId.keyword\":[");
                      requestBody.append("\""+StringUtils.join(issueIds, "\", \"")+"\"");
                      requestBody.append("]}}]}}}");
                    responseJson = PacHttpUtils.doHttpPost(urlToQuery.toString(),
                            requestBody.toString());
                    elasticSearchRepository.processResponseAndSendTheScrollBack(
                            responseJson, resourceDetList);
                } catch (Exception e) {
                    throw new DataException(e);
                }
            } else {
                resourceDetList = elasticSearchRepository.getSortedDataFromES(AWS, null, mustFilter, null, null, null,
                        mustFilterTerms, null);
            }
        } catch (Exception e) {
            throw new DataException(e);
        }
        return resourceDetList;
    }

    private List<String> revokeException(List<String> issueIds) throws DataException {

        String actionTemplateException = "{ \"index\" : { \"_id\" : \"%s\", \"_index\" : \"%s\", \"_type\" : \"%s\", \"_routing\" : \"%s\"} }%n";
        StringBuilder bulkRequest = new StringBuilder();

        List<String> errors = new ArrayList<>();
        List<String> failedIssueIds = new ArrayList<>();
        List<Map<String, Object>> exceptionDetails =  new ArrayList<>();

        try {
            exceptionDetails = getMultipleIssueDetails(issueIds, null);
        } catch(DataException e) {
            logger.error("Error while fetching exemption details ", e);
            failedIssueIds.addAll(issueIds);
            return failedIssueIds;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Map<String,String> idMapping = new HashMap<>();
        if (!exceptionDetails.isEmpty()) {
            int i = 0;
            for(Map<String, Object> exceptionDetail : exceptionDetails) {
                try {
                    if(sdf.parse(exceptionDetail.get("exceptionEndDate").toString()).after(yesterday())) {
                        String dataSource = "aws_" + exceptionDetail.get(TARGET_TYPE);
                        String targetType = "issue_" + exceptionDetail.get(TARGET_TYPE)+"_exception";
                        String id = String.valueOf(exceptionDetail.get(ES_DOC_ID_KEY));
                        String routing = String.valueOf(exceptionDetail.get(ES_DOC_ROUTING_KEY));
                        exceptionDetail.put("exceptionEndDate", sdf.format(yesterday()));
                        exceptionDetail.remove(ES_DOC_ID_KEY);
                        exceptionDetail.remove(ES_DOC_ROUTING_KEY);
                        idMapping.put(id, exceptionDetail.get("issueId").toString());

                        StringBuilder doc = new StringBuilder(createESDoc(exceptionDetail));
                        if (doc != null) {
                            bulkRequest.append(String.format(actionTemplateException, id, dataSource, targetType, routing));
                            bulkRequest.append(doc + "\n");
                        }
                        i++;
                        if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                            logger.info("Uploading {}"+ i);
                            bulkUpload(errors,bulkRequest.toString());
                            bulkRequest = new StringBuilder();
                        }
                    }
                } catch (Exception e) {
                    throw new DataException(e);
                }
            }
            if (bulkRequest.length() > 0) {
                logger.info("Uploading {}"+ i);
                bulkUpload(errors,bulkRequest.toString());
            }
        }

        fetchIdFromErrors(errors).parallelStream().forEach(id -> {
            synchronized (failedIssueIds) {
                failedIssueIds.add(idMapping.get(id));
            }
        });

        return failedIssueIds;
    }

    private void bulkUpload(List<String> errors, String bulkRequest) {
        try {
            Response resp = invokeAPI("POST", "/_bulk?refresh=true", bulkRequest);
            String responseStr = EntityUtils.toString(resp.getEntity());
            if (responseStr.contains("\"errors\":true")) {
                logger.error(responseStr);
                errors.add(responseStr);
            }
        } catch (Exception e) {
            logger.error("Bulk upload failed",e);
            errors.add(e.getMessage());
        }
    }

    public String createESDoc(Map<String, ?> doc) {
        ObjectMapper objMapper = new ObjectMapper();
        String docJson = "{}";
        try {
            docJson = objMapper.writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            logger.error("Error createESDoc",e);
        }
        return docJson;
    }

    public Response invokeAPI(String method, String endpoint, String payLoad) throws IOException {
        String uri = endpoint;
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        HttpEntity entity = null;
        if (payLoad != null)
            entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);

        return getRestClient().performRequest(method, uri, Collections.<String, String>emptyMap(), entity);
    }

    private RestClient getRestClient() {
        if (restClient == null)
            restClient = RestClient.builder(new HttpHost(esHost, esPort)).build();
        return restClient;

    }

    @SuppressWarnings("unchecked")
    private List<String> fetchIdFromErrors(List<String> errors) {
        List<String> ids = new ArrayList<>();
        for(String error : errors) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorMap = new HashMap<String, Object>();
            try {
                errorMap = mapper.readValue(error, new TypeReference<Map<String, Object>>(){});
            } catch (IOException e) {
            }
            List<LinkedHashMap<String, Object>> errorItems = ((List<LinkedHashMap<String, Object>>) errorMap.get("items"));

            for(LinkedHashMap<String, Object> errorItem : errorItems) {
                Map<String, Object> errorItemMap = (Map<String, Object>) errorItem.get("update");
                ids.add(errorItemMap.get("_id").toString());
            }
        }
        return ids;
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#getTotalAssetCountByEnvironment(java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String,Long> getTotalAssetCountByEnvironment(String assetGroup, String application,String targetType) {
    	Map<String,Long> assetCountByEnv = new HashMap<>();
        AssetCount totalAssets = assetServiceClient.getTotalAssetsCountByEnvironment(assetGroup, application,targetType);
        AssetCountData data = totalAssets.getData();
        AssetCountByAppEnvDTO[] assetcount = data.getAssetcount();
        for (AssetCountByAppEnvDTO assetCount_Count : assetcount) {
            if (assetCount_Count.getApplication().equals(application)) {
                for (AssetCountEnvCount envCount_Count : assetCount_Count.getEnvironments()) {
                	assetCountByEnv.put(envCount_Count.getEnvironment(),Long.parseLong(envCount_Count.getCount()));
                }
            }
        }
        return assetCountByEnv;
    }
    
	/**
	 * Function for getting dataSource and target type of an asset group and domain
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
	 *      getDataSourceForTargetTypeForAG(java.lang.String, java.lang.String)
	 */
	public List<Map<String, String>> getDataSourceForTargetTypeForAG(String assetGroup, String domain,
			String targetType) {

		List<Map<String, String>> dataSourceForTargetType = new ArrayList<Map<String, String>>();
		AssetApi assetApi = assetServiceClient.getTargetTypeList(assetGroup, domain);
		AssetApiData data = assetApi.getData();
		AssetCountDTO[] targetTypes = data.getTargettypes();
		for (AssetCountDTO name : targetTypes) {
			Map<String, String> datasourceTargetType = new HashMap<String, String>();
			if (!Strings.isNullOrEmpty(name.getType())) {
				datasourceTargetType.put(TYPE, name.getType());
				datasourceTargetType.put(PROVIDER, name.getProvider());
				if (targetType == null) {
					dataSourceForTargetType.add(datasourceTargetType);
				} else {
					if (datasourceTargetType.get(TYPE).equals(targetType)) {
						dataSourceForTargetType.add(datasourceTargetType);
					}
				}
			}
		}
		return dataSourceForTargetType;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
	 * getTotalAssetCount(java.lang.String, java.lang.String)
	 */
	public Map<String, Long> getTotalAssetCount(String assetGroup, String domain, String application, String type) {
		AssetCount totalAssets = assetServiceClient.getTotalAssetsCount(assetGroup, type, domain, application,"");   
		AssetCountData data = totalAssets.getData();
		AssetCountByAppEnvDTO[] assetcount = data.getAssetcount();
		Map<String, Long> assetCountByType = new HashMap<>();
		for (AssetCountByAppEnvDTO assetCount_Count : assetcount) {
			assetCountByType.put(assetCount_Count.getType(), Long.parseLong(assetCount_Count.getCount()));
		}
		return assetCountByType;
	}
	
	@Override
	public Map<String, Integer> getExemptedAssetsCountByRule(String assetGroup, String application, String type)
			throws DataException {

		Map<String, Integer> exemptedAssetsCount = new HashMap<>();
		ExemptedAssetByPolicy exemptedAssetByPolicy = assetServiceClient.getTotalAssetsExemptedByPolicy(assetGroup,
				application, type, null);
		ExemptedAssetByPolicyData data = exemptedAssetByPolicy.getData();
		for (Map<String, Object> exempted : data.getExempted()) {
			exemptedAssetsCount.put(exempted.get("ruleid").toString(),
					Integer.parseInt(exempted.get(COUNT).toString()));
		}
		return exemptedAssetsCount;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.ComplianceRepository#
	 * getPatchabeAssetsCount(java.lang.String, java.lang.String)
	 */
	public Long getPatchabeAssetsCount(String assetGroup, String targetType, String application, String environment,
			String searchText) throws DataException {
		Map<String, Object> mustFilter = new HashMap<>();
		Map<String, Object> mustNotFilter = null;

		if (!StringUtils.isEmpty(application)) {
			mustFilter.put(CommonUtils.convertAttributetoKeyword(TAGS_APPLICATION), application);
		}
		if (!StringUtils.isEmpty(environment)) {
			mustFilter.put(CommonUtils.convertAttributetoKeyword(TAGS_ENVIRONMENT), environment);
		}

		mustFilter.put(LATEST, true);
		if (EC2.equalsIgnoreCase(targetType)) {
			mustFilter.put(CommonUtils.convertAttributetoKeyword(STATE_NAME), RUNNING);
			mustNotFilter = new HashMap<>();
			mustNotFilter.put(CommonUtils.convertAttributetoKeyword(PLATFORM), WINDOWS);
		} else if (VIRTUALMACHINE.equalsIgnoreCase(targetType)) {
			mustFilter.put(CommonUtils.convertAttributetoKeyword("status"), RUNNING);
			mustNotFilter = new HashMap<>();
			mustNotFilter.put(CommonUtils.convertAttributetoKeyword("osType"), AZURE_WINDOWS);
		}
		try {
			return elasticSearchRepository.getTotalDocumentCountForIndexAndType(assetGroup, targetType, mustFilter,
					mustNotFilter, null, searchText, null);
		} catch (Exception e) {
			throw new DataException(e);
		}
	}

	
}
