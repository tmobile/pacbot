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
package com.tmobile.pacman.api.statistics.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esotericsoftware.minlog.Log;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.HeimdallElasticSearchRepository;
import com.tmobile.pacman.api.statistics.client.AssetServiceClient;
import com.tmobile.pacman.api.statistics.client.ComplianceServiceClient;
import com.tmobile.pacman.api.statistics.repository.StatisticsRepository;

/**
 * The Class StatisticsServiceImpl.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService, Constants {

    /** The asset client. */
    @Autowired
    private AssetServiceClient assetClient;

    /** The compliance client. */
    @Autowired
    private ComplianceServiceClient complianceClient;

    /** The repository. */
    @Autowired
    private StatisticsRepository repository;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /** The heimdall elastic search repository. */
    @Autowired(required=false)
    private HeimdallElasticSearchRepository heimdallElasticSearchRepository;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    /** The Constant THOUSAND. */
    private static final int THOUSAND = 1000;

    /** The Constant TWENTYFOUR. */
    private static final int TWENTYFOUR = 24;

    /** The Constant SIXTY. */
    private static final int SIXTY = 60;

    private int numberOfPoliciesEnforced;

    private int numberOfAwsAccounts;

    private String numberOfPolicyEvaluations;

    private int numberOfPolicyWithAutoFixes;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.api.statistics.service.StatisticsService#getCPUUtilization
     * (java.lang.String)
     */
    public List<Map<String, Object>> getCPUUtilization(String assetGroup) throws ServiceException {
        try {
            List<Map<String, Object>> cpuUtilizationList = new ArrayList<>();
            Map<String, Object> cpuUtilization;
            List<Map<String, Object>> utilizationDetails = elasticSearchRepository
                    .getUtilizationByAssetGroup(assetGroup);
            for (Map<String, Object> details : utilizationDetails) {
                cpuUtilization = new HashMap<>();
                cpuUtilization.put(DATE, details.get(DATE));
                cpuUtilization.put(CPU_UTILIZATION, details.get(CPU_UTILIZATION));
                cpuUtilizationList.add(cpuUtilization);
            }
            return cpuUtilizationList;
        } catch (Exception e) {
            LOGGER.error("Error @ StatisticsServiceImpl/getCPUUtilization", e);
            throw new ServiceException(e);
        }

    }

    /**
     * Gets the network utilization.
     *
     * @author santoshi
     * @param assetGroup
     *            the asset group
     * @return the network utilization
     * @throws ServiceException
     *             the service exception
     */
    public List<Map<String, Object>> getNetworkUtilization(String assetGroup) throws ServiceException {
        try {
            List<Map<String, Object>> networkUtilizationList = new ArrayList<>();
            List<Map<String, Object>> networkDetailsList;
            Map<String, Object> networkUtilization = null;

            networkDetailsList = elasticSearchRepository.getUtilizationByAssetGroup(assetGroup);
            for (Map<String, Object> networkDetails : networkDetailsList) {
                networkUtilization = new HashMap<>();
                networkUtilization.put(DATE, networkDetails.get(DATE));
                networkUtilization.put(NETWORK_IN, networkDetails.get(NETWORK_IN));
                networkUtilization.put(NETWORK_OUT, networkDetails.get(NETWORK_OUT));
                networkUtilizationList.add(networkUtilization);
            }

            return networkUtilizationList;
        } catch (Exception e) {
            LOGGER.error("Error @ StatisticsServiceImpl/getNetworkUtilization", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the disk utilization.
     *
     * @author santoshi
     * @param assetGroup
     *            the asset group
     * @return the disk utilization
     * @throws ServiceException
     *             the service exception
     */
    public List<Map<String, Object>> getDiskUtilization(String assetGroup) throws ServiceException {
        try {
            List<Map<String, Object>> diskUtilizationList = new ArrayList<>();
            List<Map<String, Object>> diskDetailsList;
            Map<String, Object> diskUtilization = null;

            diskDetailsList = elasticSearchRepository.getUtilizationByAssetGroup(assetGroup);
            for (Map<String, Object> diskDetails : diskDetailsList) {
                diskUtilization = new HashMap<>();
                diskUtilization.put(DATE, diskDetails.get(DATE));
                diskUtilization.put(DISK_READ_IN_BYTES, diskDetails.get(DISK_READ_IN_BYTES));
                diskUtilization.put(DISK_WRITE_IN_BYTES, diskDetails.get(DISK_WRITE_IN_BYTES));
                diskUtilizationList.add(diskUtilization);
            }
            return diskUtilizationList;
        } catch (Exception e) {
            LOGGER.error("Error @ StatisticsServiceImpl/getDiskUtilization", e);
            throw new ServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.api.statistics.service.StatisticsService#getStats()
     */
    public List<Map<String, Object>> getStats() throws Exception {

        try {
            List<Map<String, Object>> statsList = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put("totalAutoFixesApplied", getAutofixStats().get(0).get(COUNT));
            Long totalAssets = getTotalAssetCount();
            Long eventsProcessed = getTotalEventProcessed();
            Map<String, Long> violationsMap = getIssueDistribution();
            String targettypes = repository.getTargetTypeForAG(MASTER_ALIAS, null);
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(() -> {
             numberOfPoliciesEnforced = getNumberOfPoliciesEnforced(targettypes);
            });
            executor.execute(() -> {
             numberOfAwsAccounts = getNumberOfAwsAccounts();
            });
            executor.execute(() -> {
             numberOfPolicyEvaluations = getNumberOfPolicyEvaluations();
            });
            executor.execute(() -> {
             numberOfPolicyWithAutoFixes = getNumberOfPolicyWithAutoFixes();
            });
            executor.shutdown();
            while (!executor.isTerminated()) {
            }

            // 1. Total number of policies active in AWS Datasources
            data.put("numberOfPoliciesEnforced", numberOfPoliciesEnforced);
            // 2.Total Accounts
            data.put("numberOfAwsAccounts", numberOfAwsAccounts);
            // 3.Total Assets Scanned
            data.put("totalNumberOfAssets", totalAssets);
            // 4.Total Events Processed
            data.put("numberOfEventsProcessed", eventsProcessed);
            // 5. No of polices Evaluated from Fre-stats
            data.put("numberOfPolicyEvaluations", numberOfPolicyEvaluations);
            // 6. Auto Fix
            data.put("numberOfPolicyWithAutoFixes", numberOfPolicyWithAutoFixes);
            //data.put("totalAutoFixesApplied", totalAutofixapplied);
            data.put("totalViolations", violationsMap);
            // 6. Stats
            /*
             * String overAllComplianceData =
             * complianceClient.getOverallCompliance(AWS, "Infra & Platforms")
             * .get("data").toString(); JsonObject responseDetailsjson =
             * parser.parse(overAllComplianceData).getAsJsonObject(); Type type
             * = new TypeToken<Map<String, String>>() { }.getType();
             *
             * data.put("compliance",
             * gson.fromJson(responseDetailsjson.get("distribution"), type));
             */
            statsList.add(data);
            return statsList;
        } catch (ServiceException e) {
            LOGGER.error("Error @ StatisticsServiceImpl/getStats", e);
            throw new ServiceException(e);
        }

    }

    /**
     * Gets the issue distribution.
     *
     * @return the issue distribution
     */
    private Map<String, Long> getIssueDistribution() {
        Long totalViolations = 0l;
        Long critical = 0l;
		Long high = 0l;
		Long low = 0l;
		Long medium = 0l;
        Map<String, Long> violationsMap = new HashMap<>();
        JsonParser parser = new JsonParser();
        try {
        LOGGER.info("before the client call {}",complianceClient.toString());
        String distributionStr = complianceClient.getDistributionAsJson(MASTER_ALIAS, null);
        LOGGER.info("after the client call {}",complianceClient.toString());
        if (!Strings.isNullOrEmpty(distributionStr)) {
            JsonObject responseDetailsjson = parser.parse(distributionStr).getAsJsonObject();
            JsonObject dataJson = responseDetailsjson.get("data").getAsJsonObject();
            JsonObject distributionJson = dataJson.get("distribution").getAsJsonObject();
            totalViolations = distributionJson.get("total_issues").getAsLong();
            JsonObject severityJson = distributionJson.get("distribution_by_severity").getAsJsonObject();

				if (severityJson.has("critical")) {
					critical = severityJson.get("critical").getAsLong();
				}
				if (severityJson.has("high")) {
					high = severityJson.get("high").getAsLong();
				}
				if (severityJson.has("low")) {
					low = severityJson.get("low").getAsLong();
				}
				if (severityJson.has("medium")) {
					medium = severityJson.get("medium").getAsLong();
				}
			violationsMap.put("critical", critical);
            violationsMap.put("high", high);
            violationsMap.put("low", low);
            violationsMap.put("medium", medium);
            violationsMap.put("totalViolations", totalViolations);
        }
    } catch (Exception e) {
		LOGGER.error("error processing compliance fiegnclient", e);
		LOGGER.debug("the client call is having error",e);
		violationsMap.put("critical", critical);
        violationsMap.put("high", high);
        violationsMap.put("low", low);
        violationsMap.put("medium", medium);
        violationsMap.put("totalViolations", totalViolations);
		return violationsMap;
	}
        return violationsMap;
    }

    /**
     * Gets the total event processed.
     *
     * @return the total event processed
     * @throws Exception
     *             the exception
     */
    private Long getTotalEventProcessed() throws Exception {
        Long eventsProcessed = 0l;
        final int MILLIS_IN_DAY = THOUSAND * SIXTY * SIXTY * TWENTYFOUR;
        Date date = new Date();
        Date prevEsDate = null;
        String prevEsStrDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String prevDate = dateFormat.format(date.getTime() - MILLIS_IN_DAY);
        if(null==heimdallElasticSearchRepository) {
        	return eventsProcessed;
        }
        try{
        JsonArray eventsBuckets = heimdallElasticSearchRepository.getEventsProcessed();

        // Get Total Events Processed
        for (int i = 0; i < eventsBuckets.size(); i++) {
            prevEsDate = dateFormat.parse(eventsBuckets.get(i).getAsJsonObject().get("key_as_string").getAsString());
            prevEsStrDate = dateFormat.format(prevEsDate);
            if (prevDate.equalsIgnoreCase(prevEsStrDate)) {
                eventsProcessed = eventsBuckets.get(i).getAsJsonObject().get("doc_count").getAsLong();
            }
        }
        }catch(Exception e){
	    	LOGGER.error("error processing getTotalEventProcessed", e.getMessage());
	    	return eventsProcessed;
	    }
        return eventsProcessed;
    }

    /**
     * Gets the total asset count.
     *
     * @return the total asset count
     */
    private Long getTotalAssetCount() {
        Map<String,Long> totalAssetCountMap = new HashMap<>();
        totalAssetCountMap.put(TOTAL, 0l);
        JsonParser parser = new JsonParser();
        try{
        	LOGGER.debug("before the client call",assetClient.toString());
        Map<String, Object> assetCounts = assetClient.getTypeCounts(MASTER_ALIAS, null, null);
        LOGGER.debug("after the client call",assetClient.toString());
        // Get Total Asset Count
        assetCounts.entrySet().stream().forEach(entry->{

        if ("data".equalsIgnoreCase(entry.getKey())) {
            Long totalAssets = 0l;
            JsonObject responseDetailsjson = parser.parse(entry.getValue().toString()).getAsJsonObject();
            JsonArray assetcounts = responseDetailsjson.get("assetcount").getAsJsonArray();
            for (int i = 0; i < assetcounts.size(); i++) {
                totalAssets += assetcounts.get(i).getAsJsonObject().get("count").getAsLong();
            }
            synchronized (totalAssetCountMap) {
            	totalAssetCountMap.put(TOTAL, totalAssets);
			}
        }

        });
        }catch(Exception e){
	    	LOGGER.error("error processing fiegn assetClienr", e.getMessage());
	    	LOGGER.debug("the client call is having error",e);
	    	return totalAssetCountMap.get(TOTAL);
	    	
	    }

        return totalAssetCountMap.get(TOTAL);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.api.statistics.service.StatisticsService#getAutofixStats
     * ()
     */
    @Override
    public List<Map<String, Object>> getAutofixStats() throws ServiceException {
        try {
            List<Map<String, Object>> autofixStats = new ArrayList<>();
            Map<String, Object> actionInfo = new HashMap<>();
            List<Map<String, Object>> autofixActionsByRuleId =repository.getAutofixActionCountByRule();
            Map<String,Long> autoFixActionUniqueCountMap = new HashMap<>();
            autoFixActionUniqueCountMap.put(TOTAL, 0l);
            autofixActionsByRuleId
            .parallelStream()
            .forEach(
                    autofixActionsByRuleIdMap -> {
            Map<String, Object> resources = (Map<String, Object>) autofixActionsByRuleIdMap
                    .get("RESOURCEID");
            List<Map<String, Object>> resourceIdMap = (List<Map<String, Object>>) resources
                    .get(BUCKETS);
            synchronized (autoFixActionUniqueCountMap) {
            	autoFixActionUniqueCountMap.put(TOTAL, resourceIdMap.size()+autoFixActionUniqueCountMap.get(TOTAL));
			}
                
        });

            actionInfo.put("action", "autoFixed");
            actionInfo.put("count", autoFixActionUniqueCountMap.get(TOTAL));
            autofixStats.add(actionInfo);
            return autofixStats;
        } catch (Exception e) {
            LOGGER.error("Error @ StatisticsServiceImpl/getAutofixStats", e);
            throw new ServiceException(e);
        }
    }

    private int getNumberOfPoliciesEnforced(String targettypes) {
        int numberOfPoliciesEnforced = 0;
        try {
            numberOfPoliciesEnforced = repository.getRuleIdWithTargetTypeQuery(targettypes).size();
        } catch (DataException e) {
            LOGGER.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return numberOfPoliciesEnforced;
    }

    private int getNumberOfAwsAccounts() {
        int numberOfAwsAccounts = 0;
        try {
            numberOfAwsAccounts = repository.getNumberOfAccounts().size();
        } catch (DataException e) {
            LOGGER.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return numberOfAwsAccounts;
    }

    private String getNumberOfPolicyEvaluations() {
        String numberOfPolicyEvaluations = null;
        try {
            numberOfPolicyEvaluations = repository.getNumberOfPoliciesEvaluated();
        } catch (DataException e) {
            LOGGER.error("error",e.getMessage());
            Thread.currentThread().interrupt();
        }
        return numberOfPolicyEvaluations;
    }

    private int getNumberOfPolicyWithAutoFixes() {
        int numberOfPolicyWithAutoFixes = 0;
        try {
            numberOfPolicyWithAutoFixes = repository.getAutofixRulesFromDb().size();
        } catch (DataException e) {
            LOGGER.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return numberOfPolicyWithAutoFixes;
    }

}
