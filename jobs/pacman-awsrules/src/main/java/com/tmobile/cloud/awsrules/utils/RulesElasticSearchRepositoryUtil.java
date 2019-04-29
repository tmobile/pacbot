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
/**
 * Utility functions for ASGC Rules
 */
package com.tmobile.cloud.awsrules.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;

public class RulesElasticSearchRepositoryUtil {
    private static final Logger logger = LoggerFactory
            .getLogger(RulesElasticSearchRepositoryUtil.class);

    private RulesElasticSearchRepositoryUtil() {

    }

    public static List<Map<Object, Map<String, Object>>> getInvalidRegions(
            String esUrl, String accountId) throws Exception {

        List<Map<Object, Map<String, Object>>> invalidRegionList = new ArrayList<>();
        Map<String, Object> mustFilterRegion = new HashMap<>();
        Map<String, Object> mustNotFilterRegion = new HashMap<>();
        HashMultimap<String, Object> shouldFilterRegion = HashMultimap.create();
        Map<String, Object> mustTermsFilterRegion = new HashMap<>();
        mustFilterRegion.put(
                PacmanUtils.convertAttributetoKeyword("accountid"), accountId);
        String aggsFilterRegion = PacmanUtils
                .convertAttributetoKeyword(PacmanRuleConstants.REGION);

        JsonArray buckets = getQueryDetails(esUrl, mustFilterRegion, mustNotFilterRegion,
                shouldFilterRegion, aggsFilterRegion, 0,
                mustTermsFilterRegion, null);

        if (buckets.size() > 0) {
            for (int i = 0; i < buckets.size(); i++) {
                Map<Object, Map<String, Object>> map = new HashMap<>();
                Map<String, Object> regionMap = new HashMap<>();
                JsonObject bucket = (JsonObject) buckets.get(i);
                String region = bucket.get("key").getAsString();
                if (region.startsWith("us")) {
                    logger.debug("Valid region : {}" , region);
                } else {
                    Long issueCount = bucket.get(PacmanRuleConstants.DOC_COUNT).getAsLong();
                    if (issueCount > 5) {
                        mustFilterRegion
                                .put(PacmanUtils
                                        .convertAttributetoKeyword(PacmanRuleConstants.REGION),
                                        region);
                        String aggsFilterTargetType = PacmanUtils
                                .convertAttributetoKeyword("targetType");
                        JsonArray targetTypeBuckets = getQueryDetails(esUrl,
                                mustFilterRegion, mustNotFilterRegion,
                                shouldFilterRegion, aggsFilterTargetType, 0,
                                mustTermsFilterRegion, null);

                       getTargetTypes(targetTypeBuckets, map);

                    } else {
                        logger.debug("Valid region : {}" ,region);
                    }
                    regionMap.put("issueCount", issueCount);
                    regionMap.put(PacmanRuleConstants.REGION, region);
                    map.put("regionAndCount", regionMap);
                    invalidRegionList.add(map);
                }

            }

        }
        return invalidRegionList;
    }
    
    private static Map<Object, Map<String, Object>> getTargetTypes(JsonArray targetTypeBuckets,Map<Object, Map<String, Object>> map){
        if (targetTypeBuckets.size() > 0) {
            Map<String, Object> targetMap = new HashMap<>();
            for (int j = 0; j < targetTypeBuckets.size(); j++) {

                JsonObject targetBucket = (JsonObject) targetTypeBuckets
                        .get(j);
                String targetType = targetBucket.get("key")
                        .getAsString();
                Long targetTypeCount = targetBucket.get(
                        PacmanRuleConstants.DOC_COUNT).getAsLong();
                targetMap.put(targetType, targetTypeCount);
                map.put("targetTypes", targetMap);
            }
        }
        return map;
    }

    public static JsonArray getAggregationsResponse(String urlToQueryBuffer,
            String requestBody) throws Exception {
        String responseJson = null;
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson;
        JsonObject nameJson;
        try {
            responseJson = PacmanUtils
                    .doHttpPost(urlToQueryBuffer, requestBody);
        } catch (Exception e) {
            throw new Exception(e);
        }
        resultJson = (JsonObject) jsonParser.parse(responseJson);

        JsonObject aggregationsJson = (JsonObject) jsonParser.parse(resultJson
                .get("aggregations").toString());
        nameJson = aggregationsJson.get("name").getAsJsonObject();
        return nameJson.get("buckets").getAsJsonArray();
    }

    public static JsonArray getQueryDetails(String esUrl,
            Map<String, Object> mustFilter, Map<String, Object> mustNotFilter,
            HashMultimap<String, Object> shouldFilter, String aggsFilter,
            int size, Map<String, Object> mustTermsFilter,
            Map<String, List<String>> matchPhrasePrefix) throws Exception {
        String requestJson = null;
        String urlToQuery = esUrl;
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> matchFilters = Maps.newHashMap();
        if (mustFilter == null) {
            matchFilters.put(PacmanRuleConstants.MATCH_ALL, new HashMap<String, String>());
        } else {
            matchFilters.putAll(mustFilter);
        }
        if (null != mustFilter) {
            requestBody.put(
                    PacmanRuleConstants.QUERY,
                    buildQuery(matchFilters, mustNotFilter, shouldFilter, null,
                            mustTermsFilter, matchPhrasePrefix,null));
            requestBody.put("aggs", buildAggs(aggsFilter, size));

            if (!Strings.isNullOrEmpty(aggsFilter)) {
                requestBody.put("size", "0");
            }

        } else {
            requestBody.put(PacmanRuleConstants.QUERY, matchFilters);
        }
        Gson gson = new GsonBuilder().create();

        try {
            requestJson = gson.toJson(requestBody, Object.class);

        } catch (Exception e) {
            logger.error(PacmanRuleConstants.ERROR_MESSAGE, e);
            throw e;
        }
        return getAggregationsResponse(urlToQuery, requestJson);
    }

    /**
     * 
     * @param filter
     * @return elastic search query details
     */
    private static Map<String, Object> buildQuery(
            final Map<String, Object> mustFilter,
            final Map<String, Object> mustNotFilter,
            final HashMultimap<String, Object> shouldFilter,
            final String searchText, final Map<String, Object> mustTermsFilter,
            Map<String, List<String>> matchPhrasePrefix,Map<String, List<String>> matchPhrase) {
        Map<String, Object> queryFilters = Maps.newHashMap();
        Map<String, Object> boolFilters = Maps.newHashMap();

        Map<String, Object> hasChildObject = null;
        Map<String, Object> hasParentObject = null;

        if (isNotNullOrEmpty(mustFilter)) {
            if (mustFilter.containsKey(PacmanRuleConstants.HAS_CHILD)) {
                hasChildObject = (Map<String, Object>) mustFilter
                        .get(PacmanRuleConstants.HAS_CHILD);
                mustFilter.remove(PacmanRuleConstants.HAS_CHILD);
            }
            if (mustFilter.containsKey(PacmanRuleConstants.HAS_PARENT)) {
                hasParentObject = (Map<String, Object>) mustFilter
                        .get(PacmanRuleConstants.HAS_PARENT);
                mustFilter.remove(PacmanRuleConstants.HAS_PARENT);
            }
        }

        if (isNotNullOrEmpty(mustFilter)
                && (!Strings.isNullOrEmpty(searchText))) {
            List<Map<String, Object>> must = getFilter(mustFilter,
                    mustTermsFilter, matchPhrasePrefix,matchPhrase);
            Map<String, Object> match = Maps.newHashMap();
            Map<String, Object> all = Maps.newHashMap();
            all.put("_all", searchText);
            match.put("match", all);
            must.add(match);
            boolFilters.put("must", must);
        } else if (isNotNullOrEmpty(mustFilter) || isNotNullOrEmpty(mustTermsFilter)) {
            boolFilters.put("must",
                    getFilter(mustFilter, mustTermsFilter, matchPhrasePrefix,matchPhrase));
        }

        if (isNotNullOrEmpty(mustFilter)) {

            Map<String, Object> hasChildMap = Maps.newHashMap();
            Map<String, Object> hasParentMap = Maps.newHashMap();

            List<Map<String, Object>> must = (List<Map<String, Object>>) boolFilters
                    .get("must");

            if (null != hasChildObject) {
                hasChildMap.put(PacmanRuleConstants.HAS_CHILD, hasChildObject);
                must.add(hasChildMap);
            }
            if (null != hasParentObject) {
                hasParentMap.put(PacmanRuleConstants.HAS_PARENT, hasParentObject);
                must.add(hasParentMap);
            }

        }

        if (isNotNullOrEmpty(mustNotFilter)) {

            boolFilters.put("must_not",
                    getFilter(mustNotFilter, null, matchPhrasePrefix,null));
        }
        
        if (isNotNullOrEmpty(shouldFilter)) {
            boolFilters.put("should", getFilter(shouldFilter));
            boolFilters.put("minimum_should_match", 1);
        }
        queryFilters.put("bool", boolFilters);
        return queryFilters;
    }
    /**
     * 
     * @param collection
     * @return
     */
    private static boolean isNotNullOrEmpty(Map<String, Object> collection) {

        return collection != null && collection.size() > 0;
    }

    /**
     * 
     * @param shouldFilter
     * @return
     */
    private static boolean isNotNullOrEmpty(
            HashMultimap<String, Object> shouldFilter) {

        return shouldFilter != null && shouldFilter.size() > 0;
    }

    /**
     * 
     * @param mustfilter
     * @return
     */
    private static List<Map<String, Object>> getFilter(
            final Map<String, Object> mustfilter,
            final Map<String, Object> mustTerms,
            Map<String, List<String>> matchPhrasePrefix,Map<String, List<String>> matchPhrase) {
        List<Map<String, Object>> finalFilter = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : mustfilter.entrySet()) {
            Map<String, Object> term = Maps.newHashMap();
            Map<String, Object> termDetails = Maps.newHashMap();
            termDetails.put(entry.getKey(), entry.getValue());
            if (PacmanRuleConstants.RANGE.equals(entry.getKey())) {
                term.put(PacmanRuleConstants.RANGE, entry.getValue());
            } else {
                term.put("term", termDetails);
            }
            finalFilter.add(term);
        }
        if (mustTerms != null && !mustTerms.isEmpty()) {
            Map<String, Object> term = Maps.newHashMap();
            term.put(PacmanRuleConstants.TERMS, mustTerms);
            finalFilter.add(term);
        }

        if (matchPhrasePrefix != null && !matchPhrasePrefix.isEmpty()) {

            for (Map.Entry<String, List<String>> entry : matchPhrasePrefix
                    .entrySet()) {
                List<Object> infoList = new ArrayList<>();
                infoList.add(entry.getValue());

                for (Object val : entry.getValue()) {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> matchPhrasePrefixMap = Maps
                            .newHashMap();
                    map.put(entry.getKey(), val);
                    matchPhrasePrefixMap.put("match_phrase_prefix", map);
                    finalFilter.add(matchPhrasePrefixMap);
                }

            }
        }
        
        if (matchPhrase != null && !matchPhrase.isEmpty()) {

            for (Map.Entry<String, List<String>> entry : matchPhrase
                    .entrySet()) {
                List<Object> infoList = new ArrayList<>();
                infoList.add(entry.getValue());

                for (Object val : entry.getValue()) {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> matchPhraseMap = Maps
                            .newHashMap();
                    map.put(entry.getKey(), val);
                    matchPhraseMap.put("match_phrase", map);
                    finalFilter.add(matchPhraseMap);
                }

            }
        }
        return finalFilter;
    }
    /**
     * 
     * @param filter
     * @return
     */
    private static List<Map<String, Object>> getFilter(
            final HashMultimap<String, Object> filter) {
        List<Map<String, Object>> finalFilter = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : filter.entries()) {
            Map<String, Object> term = Maps.newHashMap();
            Map<String, Object> termDetails = Maps.newHashMap();
            termDetails.put(entry.getKey(), entry.getValue());
            term.put("term", termDetails);
            finalFilter.add(term);
        }
        return finalFilter;
    }

    private static Map<String, Object> buildAggs(String aggrigationField,
            int size) {
        Map<String, Object> name = new HashMap<>();
        if (!Strings.isNullOrEmpty(aggrigationField)) {
            Map<String, Object> terms = new HashMap<>();
            Map<String, Object> termDetails = new HashMap<>();
            termDetails.put("field", aggrigationField);
            if (size > 0) {
                termDetails.put("size", size);
            } else {
                termDetails.put("size", 10000);
            }
            terms.put(PacmanRuleConstants.TERMS, termDetails);
            name.put("name", terms);
        }
        return name;
    }

    public static JsonObject getQueryDetailsFromES(String esUrl,
            Map<String, Object> mustFilter, Map<String, Object> mustNotFilter,
            HashMultimap<String, Object> shouldFilter, String aggsFilter,
            int size, Map<String, Object> mustTermsFilter,
            Map<String, List<String>> matchPhrasePrefix,Map<String, List<String>> matchPhrase) throws Exception {
        String requestJson = null;
        String urlToQuery = esUrl;
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> matchFilters = Maps.newHashMap();
        if (mustFilter == null) {
            matchFilters.put(PacmanRuleConstants.MATCH_ALL, new HashMap<String, String>());
        } else {
            matchFilters.putAll(mustFilter);
        }
        if (null != mustFilter) {
            requestBody.put(
                    PacmanRuleConstants.QUERY,
                    buildQuery(matchFilters, mustNotFilter, shouldFilter, null,
                            mustTermsFilter, matchPhrasePrefix,matchPhrase));

            if (!Strings.isNullOrEmpty(aggsFilter)) {
                requestBody.put("size", "0");
                requestBody.put("aggs", buildAggs(aggsFilter, size));
            }

        } else {
            requestBody.put(PacmanRuleConstants.QUERY, matchFilters);
        }
        Gson gson = new GsonBuilder().create();

        try {
            requestJson = gson.toJson(requestBody, Object.class);

        } catch (Exception e) {
            logger.error(PacmanRuleConstants.ERROR_MESSAGE, e);
            throw e;
        }
        return getResponse(urlToQuery, requestJson);
    }

    public static JsonObject getResponse(String urlToQueryBuffer,
            String requestBody) throws Exception {
        String responseJson = null;
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson;
        try {
            responseJson = PacmanUtils
                    .doHttpPost(urlToQueryBuffer, requestBody);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuleExecutionFailedExeption(e.getMessage());
        }
        resultJson = (JsonObject) jsonParser.parse(responseJson);

        return resultJson;
    }

    public static long getTotalDocumentCountForIndexAndTypeWithMustNotTermsFilter(
            String esUrl, Map<String, Object> mustFilter,
            Map<String, Object> mustNotFilter,
            HashMultimap<String, Object> shouldFilter, String searchText,
            Map<String, Object> mustTermsFilter,
            Map<String, Object> mustNotTermsFilter,
            Map<String, Object> mustNotWildCardFilter,
            Map<String, List<String>> matchPhrasePrefix) throws Exception {

        String urlToQuery = esUrl;
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> matchFilters = Maps.newHashMap();
        if (mustFilter == null) {
            matchFilters.put(PacmanRuleConstants.MATCH_ALL, new HashMap<String, String>());
        } else {
            matchFilters.putAll(mustFilter);
        }
        if (null != mustFilter) {
            requestBody.put(
                    PacmanRuleConstants.QUERY,
                    buildQueryForMustTermsFilter(matchFilters, mustNotFilter,
                            shouldFilter, searchText, mustTermsFilter,
                            mustNotTermsFilter, mustNotWildCardFilter,
                            matchPhrasePrefix));
        } else {
            requestBody.put(PacmanRuleConstants.QUERY, matchFilters);
        }
        String responseDetails = null;
        Gson gson = new GsonBuilder().create();
        try {

            String requestJson = gson.toJson(requestBody, Object.class);
            responseDetails = PacmanUtils.doHttpPost(urlToQuery, requestJson);
            Map<String, Object> response = (Map<String, Object>) gson.fromJson(
                    responseDetails, Object.class);
            return (long) (Double.parseDouble(response.get("count").toString()));
        } catch (Exception e) {
            logger.error(PacmanRuleConstants.ERROR_MESSAGE, e);
            throw e;
        }
    }

    /**
     * 
     * @param filter
     * @return elastic search query details
     */
    private static Map<String, Object> buildQueryForMustTermsFilter(
            final Map<String, Object> mustFilter,
            final Map<String, Object> mustNotFilter,
            final HashMultimap<String, Object> shouldFilter,
            final String searchText, final Map<String, Object> mustTermsFilter,
            final Map<String, Object> mustNotTermsFilter,
            final Map<String, Object> mustNotWildCardFilter,
            Map<String, List<String>> matchPhrasePrefix) {
        Map<String, Object> queryFilters = Maps.newHashMap();
        Map<String, Object> boolFilters = Maps.newHashMap();

        Map<String, Object> hasChildObject = null;
        Map<String, Object> hasParentObject = null;

        if (isNotNullOrEmpty(mustFilter)) {
            if (mustFilter.containsKey(PacmanRuleConstants.HAS_CHILD)) {
                hasChildObject = (Map<String, Object>) mustFilter
                        .get(PacmanRuleConstants.HAS_CHILD);
                mustFilter.remove(PacmanRuleConstants.HAS_CHILD);
            }
            if (mustFilter.containsKey(PacmanRuleConstants.HAS_PARENT)) {
                hasParentObject = (Map<String, Object>) mustFilter
                        .get(PacmanRuleConstants.HAS_PARENT);
                mustFilter.remove(PacmanRuleConstants.HAS_PARENT);
            }
        }

        if (isNotNullOrEmpty(mustFilter)
                && (!Strings.isNullOrEmpty(searchText))) {
            List<Map<String, Object>> must = getFilter(mustFilter,
                    mustTermsFilter, matchPhrasePrefix,null);
            Map<String, Object> match = Maps.newHashMap();
            Map<String, Object> all = Maps.newHashMap();
            all.put("_all", searchText);
            match.put("match", all);
            must.add(match);
            boolFilters.put("must", must);
        } else if (isNotNullOrEmpty(mustFilter)) {
            boolFilters.put("must",
                    getFilter(mustFilter, mustTermsFilter, matchPhrasePrefix,null));
        }

        if (isNotNullOrEmpty(mustFilter)) {

            Map<String, Object> hasChildMap = Maps.newHashMap();
            Map<String, Object> hasParentMap = Maps.newHashMap();

            List<Map<String, Object>> must = (List<Map<String, Object>>) boolFilters
                    .get("must");

            if (null != hasChildObject) {
                hasChildMap.put(PacmanRuleConstants.HAS_CHILD, hasChildObject);
                must.add(hasChildMap);
            }
            if (null != hasParentObject) {
                hasParentMap.put(PacmanRuleConstants.HAS_PARENT, hasParentObject);
                must.add(hasParentMap);
            }

        }

        if (isNotNullOrEmpty(mustNotFilter)) {

            boolFilters.put(
                    "must_not",
                    getFilterWithWildCard(mustNotFilter, mustNotTermsFilter,
                            mustNotWildCardFilter));
        }
        if (isNotNullOrEmpty(shouldFilter)) {
            boolFilters.put("should", getFilter(shouldFilter));
            boolFilters.put("minimum_should_match", 1);
        }
        queryFilters.put("bool", boolFilters);
        return queryFilters;
    }

    /**
     * 
     * @param mustfilter
     * @return
     */
    private static List<Map<String, Object>> getFilterWithWildCard(
            final Map<String, Object> mustfilter,
            final Map<String, Object> mustTerms,
            final Map<String, Object> wildCardFilter) {
        List<Map<String, Object>> finalFilter = Lists.newArrayList();
        Map<String, Object> wildCardMap = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : mustfilter.entrySet()) {
            Map<String, Object> term = Maps.newHashMap();
            Map<String, Object> termDetails = Maps.newHashMap();
            termDetails.put(entry.getKey(), entry.getValue());
            if (PacmanRuleConstants.RANGE.equals(entry.getKey())) {
                term.put(PacmanRuleConstants.RANGE, entry.getValue());
            } else {
                term.put("term", termDetails);
            }
            finalFilter.add(term);
        }

        if (isNotNullOrEmpty(wildCardFilter)) {
            wildCardMap.put("wildcard", wildCardFilter);
            finalFilter.add(wildCardMap);
        }

        if (mustTerms != null && !mustTerms.isEmpty()) {
            Map<String, Object> term = Maps.newHashMap();
            term.put(PacmanRuleConstants.TERMS, mustTerms);
            finalFilter.add(term);
        }
        return finalFilter;
    }
}
