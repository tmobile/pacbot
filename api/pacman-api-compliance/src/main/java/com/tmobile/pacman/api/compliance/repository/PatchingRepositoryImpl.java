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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;

/**
 * The Class PatchingRepositoryImpl.
 */
@Repository
public class PatchingRepositoryImpl implements PatchingRepository, Constants {
    
    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;
    
    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;
    
    /** The Constant PROTOCOL. */
    static final  String PROTOCOL = "http";
    
    /** The es url. */
    private String esUrl;
    
    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;
    
    /** The rdsrepository. */
    @Autowired
    private PacmanRdsRepository rdsrepository;

    /** The filter repository. */
    @Autowired
    private FilterRepository filterRepository;

    /**
     * Inits the.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getPatchingProgress(java.lang.String, java.time.LocalDate, java.time.LocalDate)
     */
    @Override
    public List<Map<String, Object>> getPatchingProgress(String assetGroup,
            LocalDate startDate, LocalDate endDate) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("ag"), assetGroup);

        Map<String, Object> rangeMap = new HashMap<>();
        rangeMap.put("gte", startDate.format(DateTimeFormatter.ISO_DATE));
        rangeMap.put("lte", endDate.format(DateTimeFormatter.ISO_DATE));

        Map<String, Object> dateRangeMap = new HashMap<>();
        dateRangeMap.put("date", rangeMap);

        mustFilter.put("range", dateRangeMap);
      try{  return elasticSearchRepository.getSortedDataFromES("assetgroup_stats",
                "patching", mustFilter, null, null, Arrays.asList("date",
                        "unpatched_instances", "patched_instances",
                        "total_instances", "patching_percentage"), null, null);
      } catch(Exception e){
          throw new DataException(e);
      }
    }

    @Override
    public List<Map<String, Object>> getIssueInfo(String assetGroup)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID),
                EC2_KERNEL_COMPLIANCE_RULE);

        Map<String, Object> mustNotFilter = new HashMap<>();
        mustNotFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS),
                CLOSED);
        try{
        return elasticSearchRepository.getSortedDataFromES(assetGroup,
                "issue_ec2", mustFilter, mustNotFilter, null,
                Arrays.asList(RESOURCEID, "issueStatus"), null, null);
        } catch(Exception e){
            throw new DataException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getClosedIssueInfo(java.lang.String, int)
     */
    public List<Map<String, Object>> getClosedIssueInfo(String assetGroup,
            int size) throws DataException {
        /* query has exist field genric ES queries will not support */
        String responseJson = null;
        List<Map<String, Object>> issueDetails = null;
        List<Map<String, Object>> closedLissues = new ArrayList<>();

        Gson serializer = new GsonBuilder().create();
        StringBuilder requestBody = new StringBuilder(
                "{\"size\":\""
                        + size
                        + "\",\"_source\":[\"_resourceid\",\"reason-to-close\"],\"sort\":[{\"closeddate\":{\"order\":\"desc\"}}],\"query\":{\"bool\":{\"must\":[{\"match\":{\"issueStatus.keyword\":\"closed\"}},{\"match\":{\"ruleId.keyword\":\""
                        + EC2_KERNEL_COMPLIANCE_RULE
                        + "\"}},{\"exists\":{\"field\":\"reason-to-close\"}}],\"must_not\":[{\"match\":{\"reason-to-close.keyword\":\"Rule evaluation sucessfull\"}},{\"match\":{\"reason-to-close.keyword\":\"resource not found\"}}]}}}");
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/")
                .append(assetGroup).append("/").append(SEARCH);
        try{
        responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(),
                requestBody.toString());
        } catch(Exception e){
            throw new DataException(e);
        }
        Map<String, Object> responseMap = (Map<String, Object>) serializer
                .fromJson(responseJson, Object.class);
        if (responseMap.containsKey("hits")) {
            Map<String, Object> hits = (Map<String, Object>) responseMap
                    .get("hits");
            if (hits.containsKey("hits")) {
                issueDetails = (List<Map<String, Object>>) hits.get("hits");
                for (Map<String, Object> issueDetail : issueDetails) {
                    Map<String, Object> sourceMap = (Map<String, Object>) issueDetail
                            .get("_source");
                    closedLissues.add(sourceMap);
                }
            }
        }
        return closedLissues;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getInstanceInfo(java.lang.String, java.util.Map)
     */
    @Override
    public List<Map<String, Object>> getInstanceInfo(String assetGroup,
            Map<String, String> filters) throws DataException {

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(LATEST, true);
        mustFilter.put(STATE_NAME, RUNNING);
        Map<String, Object> mustNotFilter = new HashMap<>();
        mustNotFilter.put(PLATFORM, WINDOWS);

        if (null != filters && filters.size() > 0) {
            filters.forEach((key, value) -> mustFilter.put(key, value));
        }
try{
        return elasticSearchRepository
                .getSortedDataFromES(assetGroup, "ec2", mustFilter,
                        mustNotFilter, null, Arrays.asList("tags.Name",
                                TAGS_APPLICATION, RESOURCEID,
                                "tags.Environment", "accountname", "vpcid",
                                "privateipaddress"), null, null);
} catch(Exception e){
    throw new DataException(e);
}
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getExecAndDirectorInfo()
     */
    @Override
    public List<Map<String, Object>> getExecAndDirectorInfo() throws DataException {
try{
        return elasticSearchRepository
                .getSortedDataFromES("aws_apps", "apps", null, null, null,
                        Arrays.asList("appTag", DIRECTOR, EXCUTIVE_SPONSOR),
                        null, null);
} catch(Exception e){
    throw new DataException(e);
}
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getInstanceInfoCount(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public long getInstanceInfoCount(String assetGroup,
            Map<String, String> filters, String searchText) throws DataException {

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(LATEST, true);
        mustFilter.put(STATE_NAME, RUNNING);
        Map<String, Object> mustNotFilter = new HashMap<>();
        mustNotFilter.put(PLATFORM, WINDOWS);

        if (null != filters && filters.size() > 0) {
            filters.forEach((key, value) -> mustFilter.put(key, value));
        }
        try{
        return elasticSearchRepository.getTotalDocumentCountForIndexAndType(
                assetGroup, "ec2", mustFilter, mustNotFilter, null, searchText,
                null);
        } catch(Exception e){
            throw new DataException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getAmilAvailDate(int, int)
     */
    @Override
    public String getAmilAvailDate(int year, int quarter) {
        try {
            String query = "select Date(createdDate) from cf_SystemConfiguration where keyname = 'pacman.kernel.compliance.map."
                    + year + ".q" + quarter + "'";
            return rdsrepository.queryForString(query);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getPatchingWindow()
     */
    @Override
    public int getPatchingWindow() {
        try {
            String query = "select  value from  cf_SystemConfiguration where keyname = 'pacman.patching.compliance.window'";
            return rdsrepository.count(query);
        } catch (Exception e) {
            logger.error(e);
            return -1;
        }

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getQuartersWithPatchingData(java.lang.String)
     */
    @Override
    public Map<String, Long> getQuartersWithPatchingData(String assetGroup)
            throws DataException {
        Map<String, Object> mustFilter = null;
        if (StringUtils.isNotBlank(assetGroup)) {
            mustFilter = new HashMap<>();
            mustFilter.put(CommonUtils.convertAttributetoKeyword("ag"),
                    assetGroup);

        }
        try{
        return elasticSearchRepository
                .getDateHistogramForIndexAndTypeByInterval("assetgroup_stats",
                        "patching", mustFilter, null, null, "date", "quarter");

        } catch(Exception e){
            throw new DataException(e);
        } }

    
    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#addParentConditionPatching(java.lang.String)
     */
    public Map<String, Object> addParentConditionPatching(String targetType) {
        Map<String, Object> parentBool = new HashMap<>();
        List<Map<String, Object>> mustList = new ArrayList<>();
        Map<String, String> match = new HashMap<>();
        match.put(LATEST, "true");
        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put(MATCH, match);
        mustList.add(matchMap);
        Map<String, Object> parentEntryMap = new LinkedHashMap<>();
        if ("ec2".equals(targetType)) {

            match = new HashMap<>();
            match.put(STATE_NAME, RUNNING);
            matchMap = new HashMap<>();
            matchMap.put(MATCH, match);
            mustList.add(matchMap);

            parentBool.put(MUST, mustList);

            match = new HashMap<>();
            match.put(PLATFORM, WINDOWS);
            matchMap = new HashMap<>();
            matchMap.put(MATCH, match);

            parentBool.put("must_not", matchMap);

            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("bool", parentBool);

            parentEntryMap.put("type", "ec2");
            parentEntryMap.put("query", queryMap);

        }
        return parentEntryMap;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getOnpremIssueInfo(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getOnpremIssueInfo(String assetGroup)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID),
                ONPREM_KERNEL_COMPLIANCE_RULE);
        Map<String, Object> mustNotFilter = new HashMap<>();
        mustNotFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS),
                CLOSED);
      try{
        return elasticSearchRepository.getSortedDataFromES(assetGroup,
                "issue_onpremserver", mustFilter, mustNotFilter, null,
                Arrays.asList(RESOURCEID, "issueStatus"), null, null);
      } catch(Exception e){
          throw new DataException(e);
      }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getOnpremResourceInfo(java.lang.String, java.util.Map)
     */
    @Override
    public List<Map<String, Object>> getOnpremResourceInfo(String assetGroup,
            Map<String, String> filters) throws DataException {

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(LATEST, true);
        mustFilter.put("inScope.keyword", true);

        Map<String, Object> mustTermsFilter = new HashMap<>();

        Map<String, Object> mustNotTermsFilter = new HashMap<>();

        Map<String, Object> mustNotFilter = new HashMap<>();

        Map<String, Object> mustNotWildCardFilter = new HashMap<>();

        if (null != filters && filters.size() > 0) {
            filters.forEach((key, value) -> mustFilter.put(key, value));
        }
        try{
        return elasticSearchRepository
                .getSortedDataFromESWithMustNotTermsFilter(assetGroup,
                        ONPREMSERVER, mustFilter, mustNotFilter, null, Arrays
                                .asList(RESOURCEID, "ip_address", "host_name",
                                        "kernel_release", TAGS_APPLICATION,
                                        "tags.Environment"), mustTermsFilter,
                        mustNotTermsFilter, mustNotWildCardFilter, null);
        } catch(Exception e){
            throw new DataException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getDirectorsAndExcutiveSponsers(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Object> getDirectorsAndExcutiveSponsers(String appTag,
            String appType) throws DataException {

        String responseJson = null;
        String director = null;
        String executiveSponsor = null;
        String ipprotocol = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        Map<String, Object> dirAndExecMap = new HashMap<>();
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/")
                .append("aws_apps").append("/").append("apps").append("/")
                .append(SEARCH);
        StringBuilder requestBody = new StringBuilder(
                "{\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"match\":{\"_appType\":\""
                        + appType + "\"}},{\"match\":{\"appTag.keyword\":\""
                        + appTag + "\"}}]}}}");
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(),
                    requestBody.toString());
        } catch(Exception e){
            throw new DataException(e);
        }
        jsonParser = new JsonParser();
        resultJson = (JsonObject) jsonParser.parse(responseJson);

        JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(
                "hits").toString());
        JsonArray hitsArray = hitsJson.getAsJsonArray("hits");
        for (int i = 0; i < hitsArray.size(); i++) {
            JsonObject source = hitsArray.get(i).getAsJsonObject()
                    .get("_source").getAsJsonObject();
            if (null!=source.get(DIRECTOR)&&!source.get(DIRECTOR).isJsonNull()) {
                director = source.get(DIRECTOR).getAsString();
            } else {
                director = "";
            }
            if (null!=source.get(EXCUTIVE_SPONSOR)&&!source.get(EXCUTIVE_SPONSOR).isJsonNull()) {
                executiveSponsor = source.get(EXCUTIVE_SPONSOR).getAsString();
            } else {
                executiveSponsor = "";
            }
            logger.info(ipprotocol);
            dirAndExecMap.put(DIRECTOR, director);
            dirAndExecMap.put(EXCUTIVE_SPONSOR, executiveSponsor);
        }
        return dirAndExecMap;

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PatchingRepository#getNonCompliantNumberForAgAndResourceType(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Long> getNonCompliantNumberForAgAndResourceType(
            String assetGroup, String resourceType) throws DataException {
        Map<String, Long> map = new HashMap<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS),
                OPEN);
        String aggsFilterFieldName = CommonUtils
                .convertAttributetoKeyword(TAGS_APPLICATION);
        AssetCountDTO[] targetTypes = filterRepository.getListOfTargetTypes(
                assetGroup, null);
        for (AssetCountDTO targettype : targetTypes) {
            if (StringUtils.isNotBlank(targettype.getType())) {
                try{
                if (EC2.equals(resourceType) && resourceType.equals(targettype.getType())) {
                    mustFilter.put(
                            CommonUtils.convertAttributetoKeyword(RULEID),
                            EC2_KERNEL_COMPLIANCE_RULE);
                    mustFilter.put(HAS_PARENT,
                            addParentConditionPatching(EC2));
                    map = elasticSearchRepository
                            .getTotalDistributionForIndexAndType(assetGroup
                                    + "/issue_ec2", null, mustFilter,
                                    mustNotFilter, null, aggsFilterFieldName,
                                    FIVE_THOUSAND, null);
                }
                } catch(Exception e){
                    throw new DataException(e);
                }
            }
        }
        return map;
    }

	@Override
	public List<Map<String, Object>> getPatchingPercentForDateRange(String assetGroup, LocalDate fromDate,
			LocalDate toDate){
		
		List<String> fieldList = new ArrayList<>();
		
		Map<String, Object> mustFilter = new HashMap<>();
		
		mustFilter.put(CommonUtils.convertAttributetoKeyword("ag"),assetGroup);

		Map<String, Object> rangeMap = new HashMap<>();
		rangeMap.put("gte", fromDate.format(DateTimeFormatter.ISO_DATE));
		rangeMap.put("lte", toDate.format(DateTimeFormatter.ISO_DATE));
		
		Map<String, Object> dateRangeMap = new HashMap<>();
		dateRangeMap.put("date", rangeMap);
		
		mustFilter.put(RANGE, dateRangeMap);
		
		fieldList.add("patching_percentage");
		fieldList.add("date");
		fieldList.add("ag");
		
		try {
			return elasticSearchRepository.getSortedDataFromES(AG_STATS, "patching", mustFilter, null, null,
					fieldList, null, null);
		} catch (Exception e) {
			return null;
		}
	}
}
