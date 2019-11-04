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
package com.tmobile.pacman.api.compliance.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithCount;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.TaggingRepository;

/**
 * The Class TaggingServiceImpl.
 */
@Service
public class TaggingServiceImpl implements TaggingService, Constants {

    /** The mandatory tags. */
    @Value("${tagging.mandatoryTags}")
    private String mandatoryTags;

    /** The repository. */
    @Autowired
    private TaggingRepository repository;

    /** The complaince repository. */
    @Autowired
    private ComplianceRepository complainceRepository;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.TaggingService#getUnTaggedAssetsByApplication(com.tmobile.pacman.api.compliance.domain.Request)
     */
    @Override
    public ResponseWithCount getUnTaggedAssetsByApplication(Request request) throws ServiceException {
        String assetGroup = request.getAg();
        String searchText = request.getSearchtext();
        int from = request.getFrom();
        int size = request.getSize();

        List<Map<String, Object>> masterDetailList = new ArrayList<>();
        LinkedHashMap<String, Object> app;
        JsonArray buckets;
        try {
            buckets = repository.getUntaggedIssuesByapplicationFromES(assetGroup, mandatoryTags, searchText, from,
                    size);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        JsonObject tagsObj = null;
        JsonObject bucketsTags = null;
        JsonObject tagsJson = null;

        JsonElement tagsDocId = null;
        String tags = null;
        String tagName = null;
        int total = 0;
        for (int i = 0; i < buckets.size(); i++) {
            total = buckets.size();
            app = new LinkedHashMap<>();
            app.put("application", buckets.get(i).getAsJsonObject().get("key").getAsString());
            tagsObj = (JsonObject) buckets.get(i).getAsJsonObject().get("tags");
            bucketsTags = (JsonObject) tagsObj.get("buckets");
            Iterator<String> it = bucketsTags.keySet().iterator();

            while (it.hasNext()) {
                tags = it.next();
                tagsJson = (JsonObject) bucketsTags.get(tags);
                tagsDocId = tagsJson.get("doc_count");
                StringBuilder sb = new StringBuilder(tags);
                sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
                tagName = sb.toString();
                app.put(tagName + "Untagged", tagsDocId.getAsLong());
            }
            masterDetailList.add(app);
        }

        if (masterDetailList.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }

        if (from >= masterDetailList.size()) {
            throw new ServiceException("From exceeds the size of list");
        }

        int endIndex = 0;

        if ((from + size) > masterDetailList.size()) {
            endIndex = masterDetailList.size();
        } else {
            endIndex = from + size;
        }
        List<Map<String, Object>> subDetailList;

        if (from == 0 && size == 0) {
            subDetailList = masterDetailList;
        } else {
            subDetailList = masterDetailList.subList(from, endIndex);
        }
        if (buckets.size() > 0) {
            return new ResponseWithCount(subDetailList, total);
        } else {
            throw new ServiceException("No records found!!");
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.TaggingService#getTaggingSummary(java.lang.String)
     */
    public Map<String, Object> getTaggingSummary(String assetGroup) throws ServiceException {
        List<String> mandatoryTagsList = new ArrayList<>();
        String ruleMandatoryTags = mandatoryTags;
        if (!StringUtils.isEmpty(ruleMandatoryTags)) {
            mandatoryTagsList = Arrays.asList(ruleMandatoryTags.split(","));
        }
        Map<String, Object> totalMap = new HashMap<>();
        List<Map<String, Object>> unTagsList = new ArrayList<>();
        Map<String, Long> tagMap;
        try {
            tagMap = complainceRepository.getTagging(assetGroup, null);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        unTagsList = getUnTaggedListData(unTagsList, assetGroup, mandatoryTagsList, tagMap);
        totalMap.put("untaggedList", unTagsList);
        totalMap.put("overallCompliance", tagMap.get("compliance"));

        return totalMap; 
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.TaggingService#getUntaggingByTargetTypes(com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest)
     */
    public List<Map<String, Object>> getUntaggingByTargetTypes(UntaggedTargetTypeRequest request)
            throws ServiceException {
        long assetCount ;
        long untaggedCount ;
        long taggedCount ;
        double compliancePercentage;
        String type;

        List<String> tagsList = new ArrayList<>(Arrays.asList(mandatoryTags.split(",")));
        List<Map<String, Object>> targetTypes;
        try {
            targetTypes = repository.getRuleTargetTypesFromDbByPolicyId(TAGGIG_POLICY);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        if (targetTypes.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }

        Map<String, String> filterTags = request.getFilter();

        List<Map<String, Object>> unTagsList = new ArrayList<>();
        Map<String, Long> assetCountByTypes = complainceRepository.getTotalAssetCount(request.getAg(), null,null,null);
        Map<String, Long> untaggedCountMap = getUntaggedTargetTypeIssues(request, tagsList);
        // process records to format the response
        for (Map<String, Object> targetType : targetTypes) {
            type = targetType.get(TARGET_TYPE).toString();

            Map<String, Object> data = new HashMap<>();
            if (null != filterTags && !filterTags.isEmpty() && !filterTags.get(TARGET_TYPE).isEmpty()
                    && (!type.equalsIgnoreCase(filterTags.get(TARGET_TYPE)))) {
                continue;
            }
            assetCount = (null != assetCountByTypes.get(type)) ? Long.parseLong(assetCountByTypes.get(type).toString())
                    : 0l;
            if (assetCount > 0) {
                untaggedCount = getUntaggedAsset(untaggedCountMap, type);
                if (untaggedCount > assetCount) {
                    untaggedCount = assetCount;
                }
                taggedCount = assetCount - untaggedCount;
                compliancePercentage = (double) (taggedCount * INT_HUNDRED) / assetCount;
                compliancePercentage = Math.floor(compliancePercentage);

                data.put("name", type);
                data.put("untagged", untaggedCount);
                data.put("tagged", taggedCount);
                data.put("assetCount", assetCount);
                data.put(COMP_PERCENTAGE, compliancePercentage);
                unTagsList.add(data);
            }
        }
        if (unTagsList.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return unTagsList;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.TaggingService#taggingByApplication(java.lang.String, java.lang.String)
     */
    @Override
    public List<Map<String, Long>> taggingByApplication(String assetGroup, String targetType) throws ServiceException {
        List<Map<String, Long>> taggsApplication = new ArrayList<>();
        List<String> tagsList = new ArrayList<>();
        tagsList.add("");
        tagsList.add("unknown");
        Map<String, Long> tagsMap = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        String targetTypes = complainceRepository.getTargetTypeForAG(assetGroup, null);
        List<String> targetTypesList = new ArrayList<>(Arrays.asList(targetTypes.split(",")));
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        String aggsEntityType = "_entitytype.keyword";
        mustTermsFilter.put("tags.Application.keyword", tagsList);
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("latest", true);
        Map<String, Long> assetWithoutTag;
        Map<String, Long> emptyOrUnkownAssets;
        Long emptyOrUnkownAssetsLong;
        Long assetWithoutTagLong = 0l; 
        String type = null;
        if (!StringUtils.isEmpty(targetType)) {
            type = "'" + targetType + "'";
            if (targetTypesList.contains(type)) {
                assetWithoutTag = getAssetCountWithoutApplicationTag(assetGroup, targetType);
                try {
                    emptyOrUnkownAssetsLong = elasticSearchRepository
                            .getTotalDocumentCountForIndexAndTypeWithMustNotTermsFilter(assetGroup, targetType,
                                    mustFilter, null, null, null, mustTermsFilter, null, null);
                } catch (Exception e) {
                    throw new ServiceException(e);
                }
                if (assetWithoutTag.containsKey(targetType)) {
                    assetWithoutTagLong = assetWithoutTag.get(targetType);
                }
                tagsMap.put(targetType, emptyOrUnkownAssetsLong + assetWithoutTagLong);
            }
        } else {
            assetWithoutTag = getAssetCountWithoutApplicationTag(assetGroup, null);
            try {
                emptyOrUnkownAssets = elasticSearchRepository.getTotalDistributionForIndexAndType(assetGroup, null,
                        mustFilter, mustNotFilter, shouldFilter, aggsEntityType, TEN_THOUSAND, mustTermsFilter);
            } catch (Exception e) {
                throw new ServiceException(e);
            }
           getNoTagsData(tagsMap, targetTypesList, assetWithoutTag, emptyOrUnkownAssets);
        }
        taggsApplication.add(tagsMap);
        return taggsApplication;
    }

    /**
     * Gets the untagged asset.
     *
     * @param bucketMap the bucket map
     * @param type the type
     * @return the untagged asset
     */
    private long getUntaggedAsset(Map<String, Long> bucketMap, String type) {
        if (bucketMap.get(type) != null) {
            return bucketMap.get(type);
        } else {
            return 0;
        }
    }

    /**
     * Gets the asset count without application tag.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return the asset count without application tag
     * @throws ServiceException the service exception
     */
    private Map<String, Long> getAssetCountWithoutApplicationTag(String assetGroup, String targetType)
            throws ServiceException {
        Map<String, Long> assetWithoutTagsMap = new HashMap<>();
        String responseJson;
        try {
            responseJson = repository.getTaggingByApplication(assetGroup, targetType);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = jsonParser.parse(responseJson).getAsJsonObject();
        if (StringUtils.isEmpty(targetType)) {
            JsonObject aggs = (JsonObject) resultJson.get(AGGREGATIONS);
            JsonObject name = (JsonObject) aggs.get("NAME");
            JsonArray buckets = name.get(BUCKETS).getAsJsonArray();
            // convert Json Array to Map object
            for (JsonElement bucket : buckets) {
                assetWithoutTagsMap.put(bucket.getAsJsonObject().get("key").getAsString(), bucket.getAsJsonObject()
                        .get(DOC_COUNT).getAsLong());
            }
        } else {
            if(resultJson.has(COUNT)){
            assetWithoutTagsMap.put(targetType, resultJson.get(COUNT).getAsLong());
            }
        }
        return assetWithoutTagsMap;
    }

    /**
     * Gets the untagged target type issues.
     *
     * @param request the request
     * @param tagsList the tags list
     * @return the untagged target type issues
     * @throws ServiceException the service exception
     */
    private Map<String, Long> getUntaggedTargetTypeIssues(UntaggedTargetTypeRequest request, List<String> tagsList)
            throws ServiceException {
        JsonParser parser = new JsonParser();
        Map<String, Long> untaggedCountMap = new HashMap<>();
        String responseDetails;
        try {
            responseDetails = repository.getUntaggedTargetTypeIssues(request, tagsList);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        JsonObject responseJson = parser.parse(responseDetails).getAsJsonObject();
        JsonObject aggs = (JsonObject) responseJson.get(AGGREGATIONS);
        JsonObject name = (JsonObject) aggs.get("NAME");
        JsonArray buckets = name.get(BUCKETS).getAsJsonArray();
        // convert Json Array to Map object
        for (JsonElement bucket : buckets) {
            untaggedCountMap.put(bucket.getAsJsonObject().get("key").getAsString(),
                    bucket.getAsJsonObject().get(DOC_COUNT).getAsLong());
        }
        return untaggedCountMap;
    }
    
    /**
     * Gets the total assets.
     *
     * @param tagMap the tag map
     * @return the total assets
     */
    private long getTotalAssets(Map<String, Long> tagMap){
        Long totalAssets = 0l;
        for (Map.Entry<String, Long> asset : tagMap.entrySet()) {
            if ("assets".equalsIgnoreCase(asset.getKey())) {
                totalAssets = asset.getValue();
            }
        }
        return totalAssets;
    }
    
    /**
     * Gets the un tagged list data.
     *
     * @param unTagsList the un tags list
     * @param assetGroup the asset group
     * @param mandatoryTagsList the mandatory tags list
     * @param tagMap the tag map
     * @return the un tagged list data
     * @throws ServiceException the service exception
     */
    private List<Map<String, Object>> getUnTaggedListData(List<Map<String, Object>> unTagsList,String assetGroup,List<String> mandatoryTagsList, Map<String, Long> tagMap) throws ServiceException{
        Long unTagged;
        Long tagged= 0l;
        for (String mandatoryTag : mandatoryTagsList) {
            Map<String, Object> data = new HashMap<>();
            Long totalAssets = getTotalAssets(tagMap);
            try {
            	unTagged = repository.getUntaggedIssues(assetGroup, mandatoryTag);
            } catch (DataException e) {
                throw new ServiceException(e);
            }
            
            if (unTagged > totalAssets) {
            	unTagged = totalAssets;
            }
            
            tagged = totalAssets - unTagged;
            data.put("name", mandatoryTag);
            data.put("untagged", unTagged);
            data.put("tagged", tagged);

            if (totalAssets < unTagged) {
                totalAssets = unTagged;
            }

            if (unTagged == 0 && totalAssets == 0) {
                data.put(COMP_PERCENTAGE, INT_HUNDRED);
            }

            if (totalAssets > 0) {
                data.put(
                        COMP_PERCENTAGE,
                        Math.floor(((totalAssets - Double.parseDouble(String.valueOf(unTagged))) / totalAssets)
                                * INT_HUNDRED));
            }
            unTagsList.add(data);
        }
        return unTagsList;
    }
    
    /**
     * Gets the no tags data.
     *
     * @param tagsMap the tags map
     * @param targetTypesList the target types list
     * @param assetWithoutTag the asset without tag
     * @param emptyOrUnkownAssets the empty or unkown assets
     * @return the no tags data
     */
    private Map<String, Long> getNoTagsData(Map<String, Long> tagsMap,List<String> targetTypesList, Map<String, Long> assetWithoutTag, Map<String, Long> emptyOrUnkownAssets){
        for (String resourceType : targetTypesList) {
            Long assetWithoutTagLong = 0l;
            Long emptyOrUnkownAssetsLong = 0l;
            resourceType = resourceType.replaceAll("\'", "");
            if (assetWithoutTag.containsKey(resourceType)) {
                assetWithoutTagLong = assetWithoutTag.get(resourceType);
            }
            if (emptyOrUnkownAssets.containsKey(resourceType)) {
                emptyOrUnkownAssetsLong = emptyOrUnkownAssets.get(resourceType);
            }
            tagsMap.put(resourceType, emptyOrUnkownAssetsLong + assetWithoutTagLong);
        } 
        return tagsMap;
    }
}
