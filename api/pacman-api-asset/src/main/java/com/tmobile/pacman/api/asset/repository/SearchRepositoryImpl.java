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
package com.tmobile.pacman.api.asset.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import joptsimple.internal.Strings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * Implemented class for SearchRepository and all its method
 */
@Repository
public class SearchRepositoryImpl implements SearchRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchRepositoryImpl.class);
    private static final String PROTOCOL = "http://";
    @Autowired
    private PacmanRdsRepository rdsRepository;

    @Value("${elastic-search.host}")
    private String esHost;
    @Value("${elastic-search.port}")
    private int esPort;
    @Value("${vulnerability.types}")
    private String configuredVulnTargetTypes;
    @Value("${datasource.types:aws,azure}")
    private String dataSourceTypes;

    @Autowired
    ElasticSearchRepository esRepository;

    @Autowired
    AssetService assetService;

    private static RestClient restClient;

    private static Map<String, Map<String, String>> categoryToRefineByMap = new HashMap<>();
    private static Map<String, Map<String, String>> categoryToReturnFieldsMap = new HashMap<>();

    private synchronized void fetchConfigFromDB() {

        if (categoryToRefineByMap.size() > 0 || categoryToReturnFieldsMap.size() > 0) {
            return;
        }

        String query = "select SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS FROM OmniSearch_Config";
        List<Map<String, Object>> resultsList = rdsRepository.getDataFromPacman(query);

        Iterator<Map<String, Object>> rowIterator = resultsList.iterator();

        while (rowIterator.hasNext()) {
            Map<String, Object> currentRowMap = rowIterator.next();
            String searchCategory = currentRowMap.get("SEARCH_CATEGORY") != null
                    ? currentRowMap.get("SEARCH_CATEGORY").toString().trim() : "";
            String resourceType = currentRowMap.get("RESOURCE_TYPE") != null
                    ? currentRowMap.get("RESOURCE_TYPE").toString().trim() : "";
            String refineByFields = currentRowMap.get("REFINE_BY_FIELDS") != null
                    ? currentRowMap.get("REFINE_BY_FIELDS").toString().trim() : "";
            String returnFields = currentRowMap.get("RETURN_FIELDS") != null
                    ? currentRowMap.get("RETURN_FIELDS").toString().trim() : "";

            Map<String, String> resourceTypeToRefineByMap = categoryToRefineByMap.get(searchCategory);
            if (null == resourceTypeToRefineByMap) {
                resourceTypeToRefineByMap = new HashMap<>();
            }
            resourceTypeToRefineByMap.put(resourceType, refineByFields);

            Map<String, String> resourceTypeToReturnFieldMap = categoryToReturnFieldsMap.get(searchCategory);
            if (null == resourceTypeToReturnFieldMap) {
                resourceTypeToReturnFieldMap = new HashMap<>();
            }
            resourceTypeToReturnFieldMap.put(resourceType, returnFields);

            categoryToRefineByMap.put(searchCategory, resourceTypeToRefineByMap);
            categoryToReturnFieldsMap.put(searchCategory, resourceTypeToReturnFieldMap);

        }
    }

    @Override
    public SearchResult fetchSearchResultsAndSetTotal(String ag, String domain, boolean includeAllAssets,
            String targetType, String searchText, Map<String, List<String>> lowLevelFilters, int from, int size,
            SearchResult result, String searchCategory) throws DataException {

        if (categoryToRefineByMap.size() == 0 || categoryToReturnFieldsMap.size() == 0) {

            fetchConfigFromDB();

        }

        Map<String, Object> mustFilter = new LinkedHashMap<>();
        Map<String, Object> mustTermsFilter = new LinkedHashMap<>();

        lowLevelFilters.forEach((displayName, valueList) -> {
            String esFieldName = getFieldMappingsForSearch(targetType, false, searchCategory).get(displayName)
                    + ".keyword";
            mustTermsFilter.put(esFieldName, valueList);
        });

        String esType = null;

        if (AssetConstants.ASSETS.equals(searchCategory)) {
            if (!includeAllAssets) {
                mustFilter.put(Constants.LATEST, Constants.TRUE);
            }
            mustFilter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
            if (null == targetType) {
                mustTermsFilter.put(AssetConstants.UNDERSCORE_ENTITY_TYPE_KEYWORD, getTypesForDomain(ag, domain));
            }
            esType = targetType;
        }

        if (AssetConstants.POLICY_VIOLATIONS.equals(searchCategory)) {
            mustFilter.put("type.keyword", "issue");
            mustTermsFilter.put("issueStatus.keyword", Arrays.asList("open", "exempted"));
            if (null == targetType) {
                esType = null;
                mustTermsFilter.put("targetType.keyword", getTypesForDomain(ag, domain));

            } else {
                esType = "issue_" + targetType;
            }

        }

        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            mustFilter.put(Constants.LATEST, Constants.TRUE);
            mustTermsFilter.put(Constants.SEVEITY_LEVEL+".keyword",
                    Arrays.asList(Constants.THREE, Constants.FOUR, Constants.FIVE));

            if (null != targetType) {
                mustFilter.put("_index", "aws_" + targetType);
            }
            esType = Constants.VULN_INFO;
        }
        long start = System.currentTimeMillis();

        if (!Strings.isNullOrEmpty(searchText)) {
            searchText = "\"" + searchText + "\"";
        }
        List<Map<String, Object>> results = new ArrayList<>();
        if (!AssetConstants.VULNERABILITIES.equals(searchCategory)) {

            List<Map<String, Object>> sortFieldsMapList = new ArrayList<>();
            Map<String, Object> resourceIdSort = new HashMap<>();
            resourceIdSort.put("_resourceid.keyword", "asc");
            sortFieldsMapList.add(resourceIdSort);

            StringBuilder urlToQuery = new StringBuilder(PROTOCOL + esHost + ":" + esPort + "/" + ag);
            if (!Strings.isNullOrEmpty(esType)) {
                urlToQuery.append("/").append(esType);
            }
            urlToQuery.append("/").append("_search?from=").append(from).append("&size=").append(size);

            Map<String, Object> query = new HashMap<>();
            query.put("query", esRepository.buildQuery(mustFilter, null, null, searchText, mustTermsFilter, null));
            query.put("_source", getReturnFieldsForSearch(targetType, searchCategory));
            query.put("sort", resourceIdSort);

            String resultJson = invokeESCall("GET", urlToQuery.toString(), new Gson().toJson(query));

            Gson serializer = new GsonBuilder().create();
            Map<String, Object> response = (Map<String, Object>) serializer.fromJson(resultJson, Object.class);
            if (response.containsKey("hits")) {
                Map<String, Object> hits = (Map<String, Object>) response.get("hits");
                long total = Double.valueOf(hits.get("total").toString()).longValue();
                result.setTotal(total);
            }
            esRepository.processResponseAndSendTheScrollBack(resultJson, results);

        } else {
            List<String> returnFields = getReturnFieldsForSearch(targetType, searchCategory);
            String docQueryString = "";
            docQueryString = new StringBuilder(docQueryString).append("[").toString();
            int count = 0;
            for (String returnField : returnFields) {
                if (count == 0) {
                    docQueryString = new StringBuilder(docQueryString).append("doc['").append(returnField)
                            .append("'].value").toString();
                } else {
                    docQueryString = new StringBuilder(docQueryString).append("doc['").append(returnField)
                            .append(".keyword'].value").toString();

                }
                count++;

                if (count < returnFields.size()) {
                    docQueryString = new StringBuilder(docQueryString).append(" +'~'+").toString();
                }

            }
            docQueryString = docQueryString + "]";

            String url = PROTOCOL + esHost + ":" + esPort + "/" + ag + "/" + Constants.VULN_INFO + "/"
                    + Constants.SEARCH;
            Map<String, Object> query = new HashMap<>();
            query.put("query", esRepository.buildQuery(mustFilter, null, null, searchText, mustTermsFilter, null));

            String queryString = new Gson().toJson(query);
            String payload = queryString.substring(0, queryString.length() - 1)
                    + ", \"aggs\": {\"qids\": {\"terms\": {\"script\": \"" + docQueryString + "\",\"size\": 10000}}}}";
            String responseJson = "";
            try {
                long startAggs = System.currentTimeMillis();
                LOGGER.debug("To get vuln aggs without dups, url is: {} and payload is: {}", url, payload);
                responseJson = PacHttpUtils.doHttpPost(url, payload);
                LOGGER.debug(AssetConstants.DEBUG_RESPONSEJSON, responseJson);
                long endAggs = System.currentTimeMillis();
                LOGGER.debug("Time taken for ES call(vuln aggs sans dups) is: {}", (endAggs - startAggs));
                results = getDistFromVulnAggsResult(responseJson, returnFields);
            } catch (Exception e) {
                LOGGER.error("Failed to retrieve vuln aggs for omni search ", e);
            }

        }

        results = pruneResults(results, targetType, searchCategory);
        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            result.setTotal(results.size());

            int end = from + size;
            if (end > (results.size())) {
                from = 0;
                end = results.size();
            }
            results = results.subList(from, end);
        }

        result.setResults(results);
        long end = System.currentTimeMillis();
        LOGGER.debug("Time taken to perform search for Search Category {} is: {}", searchCategory, (end - start));

        return result;

    }

    @Override
    public List<Map<String, Object>> fetchTargetTypes(String ag, String searchText, String searchCategory,
            String domain, boolean includeAllAssets) throws DataException {

        if (categoryToRefineByMap.size() == 0 || categoryToReturnFieldsMap.size() == 0) {

            fetchConfigFromDB();

        }

        List<Map<String, Object>> resourceTypeBucketList = new ArrayList<>();

        String aggStringForHighLevelEntities = "";
        if (AssetConstants.ASSETS.equals(searchCategory)) {
            aggStringForHighLevelEntities = "\"targetTypes\":{\"terms\":{\"field\":\"_entitytype.keyword\",\"size\":10000}}";
        }

        if (AssetConstants.POLICY_VIOLATIONS.equals(searchCategory)) {
            aggStringForHighLevelEntities = "\"targetTypes\":{\"terms\":{\"field\":\"targetType.keyword\",\"size\":10000}}";
        }

        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            aggStringForHighLevelEntities = "\"targetTypes\":{\"terms\":{\"field\":\"_index\",\"size\":10000},\"aggs\":{\"unique\":{\"cardinality\":{\"field\":\""
                    + getReturnFieldsForSearch(null, searchCategory).get(0) + "\"}}}}";
        }

        String payLoadStr = createPayLoad(aggStringForHighLevelEntities, searchText, searchCategory, null,
                includeAllAssets);

        String firstUrl = "";

        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            firstUrl = PROTOCOL + esHost + ":" + esPort + "/" + ag + "/" + Constants.VULN_INFO + "/" + Constants.SEARCH;
        } else {
            firstUrl = PROTOCOL + esHost + ":" + esPort + "/" + ag + "/" + Constants.SEARCH;
        }
        String responseJson = "";

        try {
            long start = System.currentTimeMillis();
            LOGGER.debug("To get targetTypes:URL is: {} and payload is : {}", firstUrl, payLoadStr);
            responseJson = PacHttpUtils.doHttpPost(firstUrl, payLoadStr);
            LOGGER.debug(AssetConstants.DEBUG_RESPONSEJSON, responseJson);
            long end = System.currentTimeMillis();
            LOGGER.debug("Time taken for ES call(targetType) for Search Category {} is: {}", searchCategory,
                    (end - start));

        } catch (Exception e) {
            LOGGER.error("Failed to retrieve high level entity types for omni search ", e);
            return resourceTypeBucketList;
        }

        resourceTypeBucketList = getDistributionFromAggResult(responseJson, "targetTypes");

        // Atleast one refinement should be defined for the entity. If not, kick
        // it out
        removeResourceTypeIfNoMappingDefined(resourceTypeBucketList, searchCategory);

        removeResourceTypeIfNotAttachedToDomain(ag, resourceTypeBucketList, domain);

        return resourceTypeBucketList;
    }

    private List<String> getTypesForDomain(String ag, String domain) {
        List<Map<String, Object>> domainData = assetService.getTargetTypesForAssetGroup(ag, domain, null);
        List<String> typesForDomain = new ArrayList<>();
        domainData.forEach(domainMap -> {
            domainMap.forEach((key, value) -> {
                if (key.equals("type")) {
                    typesForDomain.add(value.toString());
                }
            });
        });
        return typesForDomain;
    }

    private synchronized void removeResourceTypeIfNotAttachedToDomain(String ag,
            List<Map<String, Object>> resourceTypeBucketList, String domain) {

        List<String> typesForDomain = getTypesForDomain(ag, domain);

        Iterator<Map<String, Object>> resourceIterator = resourceTypeBucketList.iterator();
        while (resourceIterator.hasNext()) {
            String resourceType = resourceIterator.next().get(AssetConstants.FIELDNAME).toString();
            if (!typesForDomain.contains(resourceType)) {
                resourceIterator.remove();
            }
        }
    }

    private synchronized void removeResourceTypeIfNoMappingDefined(List<Map<String, Object>> resourceTypeBucketList,
            String searchCategory) {
        Iterator<Map<String, Object>> resourceIterator = resourceTypeBucketList.iterator();
        while (resourceIterator.hasNext()) {
            String resourceType = resourceIterator.next().get(AssetConstants.FIELDNAME).toString();
            Map<String, String> fieldMapping = getFieldMappingsForSearch(resourceType, false, searchCategory);
            if (fieldMapping.isEmpty()) {
                resourceIterator.remove();
            }
        }
    }

    @Override
    public Map<String, List<Map<String, Object>>> fetchDistributionForTargetType(String ag, String resourceType,
            String searchText, String searchCategory, boolean includeAllAssets) {

        Map<String, List<Map<String, Object>>> returnBucketMap = new LinkedHashMap<>();

        // Prepare aggregation string based on what resourceType we are
        // dealing with
        StringBuilder aggregationStrBuffer = new StringBuilder();
        Map<String, String> fieldMapping = getFieldMappingsForSearch(resourceType, false, searchCategory);
        if (fieldMapping.isEmpty()) {
            return returnBucketMap;
        }

        fieldMapping.forEach((displayName, esFieldName) -> {
            aggregationStrBuffer.append(
                    "\"" + displayName + "\":{\"terms\":{\"field\":\"" + esFieldName + ".keyword\",\"size\":10000}");

            if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {

                aggregationStrBuffer.append(",\"aggs\":{\"unique\":{\"cardinality\":{\"field\":\""
                        + getReturnFieldsForSearch(resourceType, searchCategory).get(0) + "\"}}}");
            }
            aggregationStrBuffer.append("}");

            // Trailing comma
            aggregationStrBuffer.append(",");
        });

        // Remove the trailing comma, because of the iteration above
        String aggStringForLowLevelMenu = aggregationStrBuffer.toString().substring(0,
                aggregationStrBuffer.toString().length() - 1);

        String lowLevelPayLoadStr = createPayLoad(aggStringForLowLevelMenu, searchText, searchCategory, resourceType,
                includeAllAssets);

        String secondUrl = null;
        if (AssetConstants.ASSETS.equals(searchCategory)) {
            secondUrl = PROTOCOL + esHost + ":" + esPort + "/" + ag + "/" + resourceType + "/" + Constants.SEARCH;
        }
        if (AssetConstants.POLICY_VIOLATIONS.equals(searchCategory)) {
            secondUrl = PROTOCOL + esHost + ":" + esPort + "/" + ag + "/" + "issue_" + resourceType + "/"
                    + Constants.SEARCH;
        }
        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            secondUrl = PROTOCOL + esHost + ":" + esPort + "/" + ag + "/" + Constants.VULN_INFO + "/"
                    + Constants.SEARCH;
        }

        try {
            LOGGER.debug("To get distribution, the URL is: {} and the payload is: {}", secondUrl, lowLevelPayLoadStr);
            long start = System.currentTimeMillis();
            final String lowLevelResponseJson = invokeESCall("GET", secondUrl, lowLevelPayLoadStr);
            LOGGER.debug(AssetConstants.DEBUG_RESPONSEJSON, lowLevelResponseJson);
            long end = System.currentTimeMillis();
            LOGGER.debug("Search Category {}", searchCategory);
            LOGGER.debug("Target type {}", resourceType);
            LOGGER.debug("Time taken for ES call(refineBy) is: {}", (end - start));

            fieldMapping.forEach((displayName, esFieldName) -> {
                List<Map<String, Object>> detailsBucketList = getDistributionFromAggResult(lowLevelResponseJson,
                        displayName);
                if (!detailsBucketList.isEmpty()) {
                    returnBucketMap.put(displayName, detailsBucketList);
                }

            });
        } catch (Exception e) {
            LOGGER.error("Error fetching distributions from ES:", e);
        }

        return returnBucketMap;
    }

    private List<Map<String, Object>> getDistributionFromAggResult(String responseJson, String aggName) {
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = jsonParser.parse(responseJson).getAsJsonObject();
        JsonArray types = resultJson.get("aggregations").getAsJsonObject().get(aggName).getAsJsonObject().get("buckets")
                .getAsJsonArray();
        List<Map<String, Object>> bucketList = new ArrayList<>();
        String dsArray[] = dataSourceTypes.split(",");
        for (JsonElement type : types) {
            JsonObject typeObj = type.getAsJsonObject();
            String fieldName = typeObj.get("key").getAsString();

            // To handle vulnerabilities type
       
            for(String ds : dsArray) {
	            if (fieldName.startsWith(ds+"_")) {
	                fieldName = fieldName.substring(ds.length()+1);
	                break;
	            }
            }

            long count = typeObj.get("doc_count").getAsLong();
            JsonElement uniqueNumberElement = typeObj.get("unique");
            if (null != uniqueNumberElement) {
                count = uniqueNumberElement.getAsJsonObject().get("value").getAsLong();
            }
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put(AssetConstants.FIELDNAME, fieldName);
            typeMap.put("count", count);
            bucketList.add(typeMap);
        }

        return bucketList;
    }

    private List<Map<String, Object>> getDistFromVulnAggsResult(String responseJson, List<String> returnFields) {

        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = jsonParser.parse(responseJson).getAsJsonObject();
        JsonArray types = resultJson.get("aggregations").getAsJsonObject().get("qids").getAsJsonObject().get("buckets")
                .getAsJsonArray();
        List<Map<String, Object>> bucketList = new ArrayList<>();
        for (JsonElement type : types) {
            int count = 0;
            Map<String, Object> map = new HashMap<>();
            JsonObject typeObj = type.getAsJsonObject();
            String key = typeObj.get("key").getAsString();
            StringTokenizer vulnkeyTokenizer = new StringTokenizer(key, "~");
            while (vulnkeyTokenizer.hasMoreTokens()) {
                String token = vulnkeyTokenizer.nextToken();
                map.put(returnFields.get(count), token);
                count++;
            }
            bucketList.add(map);
        }

        return bucketList;
    }

    private String createPayLoad(String aggString, String searchText, String searchCategory, String resourceType,
            boolean includeAllAssets) {
        StringBuilder payLoad = new StringBuilder();
        String matchString = "";
        if (AssetConstants.ASSETS.equals(searchCategory)) {
            matchString = "{\"match\":{\"_entity\":\"true\"}}";
        }
        if (AssetConstants.POLICY_VIOLATIONS.equals(searchCategory)) {
            matchString = "{\"match\":{\"type.keyword\":\"issue\"}},{\"terms\":{\"issueStatus.keyword\":[ \"open\",\"exempted\"]}}";
        }
        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            matchString = "{\"terms\":{\"severitylevel.keyword\":[3,4,5]}}";
            if (resourceType != null) {
                matchString = matchString + ",{\"match\":{\"_index\":\"aws_" + resourceType + "\"}}";
            }
        }

        payLoad.append("{\"size\":0,\"query\":{\"bool\":{\"must\":[");
        payLoad.append(matchString);
        if (AssetConstants.ASSETS.equals(searchCategory) && !includeAllAssets) {
            payLoad.append(",{\"match\":{\"latest\":\"true\"}}");
        }
        if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
            payLoad.append(",{\"match\":{\"latest\":\"true\"}}");
        }

        payLoad.append(",{\"match_phrase_prefix\":{\"_all\":\"");
        payLoad.append(searchText);
        payLoad.append("\"}}]}}");
        payLoad.append(",\"aggs\":{");
        payLoad.append(aggString);
        payLoad.append("}}");
        return payLoad.toString();
    }

    @Override
    public Map<String, String> getFieldMappingsForSearch(String incomingResourceType, boolean flipOrder,
            String searchCategory) {

        Map<String, String> mappingList = new LinkedHashMap<>();

        String commaSepString = categoryToRefineByMap.get(searchCategory).get(incomingResourceType);

        String commaSepStringForAll = categoryToRefineByMap.get(searchCategory).get("All");

        String jointStr = commaSepStringForAll + (commaSepString != null ? (",".concat(commaSepString)) : "");

        String displayName = null;
        String esFieldName = null;

        StringTokenizer commaTokens = new StringTokenizer(jointStr, ",");
        while (commaTokens.hasMoreTokens()) {
            String pipeSeparatedStr = commaTokens.nextToken();
            if (pipeSeparatedStr.contains("|")) {
                int posOfPipe = pipeSeparatedStr.indexOf('|');
                esFieldName = pipeSeparatedStr.substring(0, posOfPipe);
                displayName = pipeSeparatedStr.substring(posOfPipe, pipeSeparatedStr.length());
            } else {
                // Assume display name is same as field name
                esFieldName = pipeSeparatedStr;
                displayName = pipeSeparatedStr;
            }
            if (flipOrder) {
                mappingList.put(esFieldName, displayName);
            } else {
                mappingList.put(displayName, esFieldName);
            }
        }
        return mappingList;
    }

    @Override
    public List<String> getReturnFieldsForSearch(String incomingResourceType, String searchCategory) {

        List<String> returnFieldList = new ArrayList<>();

        String commaSepString = categoryToReturnFieldsMap.get(searchCategory).get(incomingResourceType);
        String commaSepStringForAll = categoryToReturnFieldsMap.get(searchCategory).get("All");

        String jointStr = commaSepStringForAll + (commaSepString != null ? (",".concat(commaSepString)) : "");
        StringTokenizer commaTokens = new StringTokenizer(jointStr, ",");

        while (commaTokens.hasMoreTokens()) {
            returnFieldList.add(commaTokens.nextToken());
        }

        return returnFieldList;
    }

    private List<Map<String, Object>> pruneResults(List<Map<String, Object>> results, String targetType,
            String searchCategory) {
        List<String> returnFields = getReturnFieldsForSearch(targetType, searchCategory);
        List<Map<String, Object>> resultsAfterPruning = new ArrayList<>();
        results.forEach(result -> {
            Map<String, Object> outgoingMap = new LinkedHashMap<>();
            result.forEach((key, value) -> {
                if (returnFields.contains(key) || key.startsWith("tags.")) {
                    // The first item in the return fields from RDS is to be
                    // considered as the id
                    // field
                    if (key.equals(returnFields.get(0))) {
                        key = Constants._ID;
                    }
                    outgoingMap.put(key, value);
                }
            });

            outgoingMap.put("searchCategory", searchCategory);
            boolean removeDups = false;
            if (AssetConstants.VULNERABILITIES.equals(searchCategory)) {
                removeDups = true;
            }
            if (!removeDups || (removeDups
                    && !doesResultAlreadyContainId(resultsAfterPruning, outgoingMap.get(Constants._ID)))) {
                resultsAfterPruning.add(outgoingMap);
            }
        });
        return resultsAfterPruning;
    }

    private boolean doesResultAlreadyContainId(List<Map<String, Object>> resultsAfterPruning,
            Object idValueToBeChecked) {
        List<Object> matchedObjects = new ArrayList<>();
        resultsAfterPruning.forEach(result -> {
            result.forEach((key, value) -> {
                if (key.equals(Constants._ID)) {
                    double lhsLongValue = Double.parseDouble(value.toString());
                    double rhsLongValue = Double.parseDouble(idValueToBeChecked.toString());
                    if (lhsLongValue == rhsLongValue) {

                        LOGGER.debug("Duplicate vuln id found(Won't be adding this..): {}", idValueToBeChecked);
                        matchedObjects.add(idValueToBeChecked);
                    }

                }
            });
        });
        return !matchedObjects.isEmpty();
    }

    private RestClient getRestClient() {
        if (restClient == null) {

            RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, esPort));
            builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder.setConnectionRequestTimeout(0);
                }
            });
            restClient = builder.build();
        }
        return restClient;

    }

    private String invokeESCall(String method, String endpoint, String payLoad) {
        HttpEntity entity = null;
        try {
            if (payLoad != null) {
                entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);
            }
            return EntityUtils.toString(getRestClient()
                    .performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity).getEntity());
        } catch (IOException e) {
            LOGGER.error("Error in invokeESCall ", e);
        }
        return null;
    }

}
