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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.client.AuthServiceClient;
import com.tmobile.pacman.api.compliance.domain.Asset;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithOrder;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.TrendRepository;
/**
 * The Class IssueTrendServiceImpl.
 */
@Service
public class IssueTrendServiceImpl implements IssueTrendService, Constants {

    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;
    
    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;
    
    /** The es cluster name. */
    @Value("${elastic-search.clusterName}")
    private String esClusterName;
    
    /** The date format. */
    @Value("${formats.date}")
    private String dateFormat;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The statistics client. */

    /** The auth client. */
    @Autowired
    private AuthServiceClient authClient;

    /** The repository. */
    @Autowired
    private TrendRepository repository;

    /** The compliance service. */
    @Autowired
    private ComplianceService complianceService;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;
    
    /** The compliance repository. */
    @Autowired
    private ComplianceRepository complianceRepository;

    /**
     * {@inheritDoc}
     */
    public Asset finfindByName(String accountName) {
        Assert.hasLength(accountName, "accountName cannot be null or empty");
        return null;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.IssueTrendService#getTrendForIssues(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Long> getTrendForIssues(String assetGroup,
            String fromDate, String toDate, String severity, String ruleId,
            String policyId, String app, String env) throws ServiceException {
        Assert.hasLength(assetGroup, "assetGroup cannot be null or empty");

        RangeGenerator generator = new RangeGenerator();
        Map<String, String> mustNotFilter = new HashMap<>();
        Map<String, String> mustFilter = new HashMap<>();
        if (!Strings.isNullOrEmpty(severity)) {
            mustFilter.put("severity.keyword", severity);
        }

        if (!Strings.isNullOrEmpty(policyId)) {
            mustFilter.put("policyId.keyword", policyId);
        }

        if (!Strings.isNullOrEmpty(ruleId)) {
            mustFilter.put("ruleId.keyword", ruleId);
        }
        if (!Strings.isNullOrEmpty(app)) {
            mustFilter.put("tags.Application.keyword", app);
        }

        if (!Strings.isNullOrEmpty(env)) {
            mustFilter.put("tags.Environment.keyword", env);
        }

        List<String> hosts = Arrays.asList(esHost,
                "");
        // this has to move to config, this is just an additional end point,
        // even if not provided default end point from config will work

        try {
            return generator.generateTrend(esClusterName, hosts,
                    NINE_THOUSAND_THREE_HUNDRED, assetGroup, "issue",
                    "createdDate", "modifiedDate", mustNotFilter, mustFilter,
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        } catch (DataException e) {
          
          throw new ServiceException(e);
        }

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.IssueTrendService#getComplianceTrendProgress(java.lang.String, java.time.LocalDate, java.lang.String)
     */
    @Override
    public Map<String, Object> getComplianceTrendProgress(String assetGroup,
            LocalDate fromDate, String domain) throws ServiceException {
        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("ag", assetGroup);
        // get list of targetypes mapped
        String ttypes = complianceRepository.getTargetTypeForAG(assetGroup,
                domain);
        List<Map<String, Object>> ruleDetails = null;
        List<Map<String, Object>> inputList;
        List<Map<String, Object>> complianceInfoList;
        
        if (!Strings.isNullOrEmpty(ttypes)) {
            try {
                ruleDetails = complianceRepository
                        .getRuleIdWithDisplayNameQuery(ttypes);
            } catch (DataException e) {
              throw new ServiceException(e);
            }
        }
        // Make map of rule severity,category
        Set<String> ruleCat = new HashSet<>();
        List<Map<String, Object>> ruleSevCatDetails;
    
            ruleSevCatDetails = complianceService
                    .getRuleSevCatDetails(ruleDetails);
       
        
        
        Map<String, Object> ruleCatDetails = ruleSevCatDetails.parallelStream()
                .collect(
                        Collectors.toMap(c -> c.get(RULEID).toString(),
                                c -> c.get(RULE_CATEGORY),
                                (oldvalue, newValue) -> newValue));
        ruleCatDetails.entrySet().parallelStream()
                .forEach(entry -> ruleCat.add(entry.getValue().toString()));
        complianceInfoList = new ArrayList<>();

         try {
            inputList = repository
                    .getComplianceTrendProgress(assetGroup, fromDate, domain,
                            ruleCat);
         } catch (DataException e) {
             throw new ServiceException(e);
           }
       
        if (!inputList.isEmpty()) {
            // Sort the list by the date in ascending order
            Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate.parse(
                    m1.get("date").toString(), DateTimeFormatter.ISO_DATE)
                    .compareTo(
                            LocalDate.parse(m2.get("date").toString(),
                                    DateTimeFormatter.ISO_DATE));

            Collections.sort(inputList, comp);
            useRealTimeDataForLatestDate(inputList, assetGroup,
                    COMPLIANCE_PERCENTAGE, null, domain);
            inputList.forEach(inputMap -> {
                Map<String, Object> outputMap = new HashMap<>();
                inputMap.forEach((key, value) -> {
                    // Other than the specified keys, ignore all other kv pairs
                    if ((!Strings.isNullOrEmpty(key))
                            && !("_id".equalsIgnoreCase(key))) {

                        outputMap.put(key, value);
                    }
                });

                complianceInfoList.add(outputMap);
            });

            Collections.sort(complianceInfoList, comp);
        }
        parentMap.put("compliance_info", complianceInfoList);
        return parentMap;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.IssueTrendService#getTrendProgress(java.lang.String, java.lang.String, java.time.LocalDate, java.time.LocalDate, java.lang.String)
     */
    @Override
    public Map<String, Object> getTrendProgress(String assetGroup,
            String ruleId, LocalDate startDate, LocalDate endDate,
            String trendCategory) throws ServiceException {

        List<Map<String, Object>> trendList;
        try{
        trendList  = repository.getTrendProgress(
                assetGroup, ruleId, startDate, endDate, trendCategory);
        }catch(DataException e){
            throw new ServiceException(e);
        }
        if (!trendList.isEmpty()) {

            // Sort the list by the date in ascending order
            Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate.parse(
                    m1.get("date").toString(), DateTimeFormatter.ISO_DATE)
                    .compareTo(
                            LocalDate.parse(m2.get("date").toString(),
                                    DateTimeFormatter.ISO_DATE));
            Collections.sort(trendList, comp);
            LocalDate trendStartDate = LocalDate.parse(trendList.get(0)
                    .get("date").toString());

            // Elastic Search might not have data for some dates. But we want to
            // send consistent data to the consumers of this service, so we will
            // populate previous where data is unavailable
            fillNoDataDatesWithPrevious(trendList, trendStartDate, endDate);

            useRealTimeDataForLatestDate(trendList, assetGroup, trendCategory,
                    ruleId, null);

            // ADD compliance_percent if not available . This is done
            // temporarily.Will update with compliance_percent at source

            appendWithCompliancePercent(trendList);

            return segregateTrendProgressByWeek(assetGroup, trendList,
                    trendStartDate, endDate);
        } else {
            return new HashMap<>();
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.IssueTrendService#useRealTimeDataForLatestDate(java.util.List, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void useRealTimeDataForLatestDate(
            List<Map<String, Object>> trendList, String ag,
            String trendCategory, String ruleId, String domain)
            throws ServiceException {
        Map<String, Object> latestDaysTrendData = new HashMap<>(
                trendList.get(trendList.size() - 1));
        Map<String, Long> baseApiReturnMap = new HashMap<>();
        Map<String, Object> overallCompliance = new HashMap<>();
        long compliantQuantity = 0;
        long noncompliantQuantity = 0;
        long total = 0;
        double compliance;
        LocalDate today;
        try {
            switch (trendCategory) {
            case "tagcompliance":
                baseApiReturnMap = complianceService.getTagging(ag, null);
                compliantQuantity = baseApiReturnMap.get("tagged");
                noncompliantQuantity = baseApiReturnMap.get("untagged");
                total = baseApiReturnMap.get("assets");
                compliance = baseApiReturnMap.get(COMPLIANCE_PERCENTAGE);

                latestDaysTrendData.put(COMPLAINT, compliantQuantity);
                latestDaysTrendData.put(NON_COMPLIANT, noncompliantQuantity);
                latestDaysTrendData.put(TOTAL, total);
                latestDaysTrendData.put(COMPLIANCE_PERCENT, compliance);
                break;

            case "certcompliance":

                baseApiReturnMap = complianceService.getCertificates(ag);
                total = baseApiReturnMap.get("certificates");
                noncompliantQuantity = baseApiReturnMap
                        .get("certificates_expiring");
                compliantQuantity = total - noncompliantQuantity;

                latestDaysTrendData.put(COMPLAINT, compliantQuantity);
                latestDaysTrendData.put(NON_COMPLIANT, noncompliantQuantity);
                latestDaysTrendData.put(TOTAL, total);
                if (total > 0) {
                    compliance = Math
                            .floor(compliantQuantity * HUNDRED / total);
                } else {
                    compliance = INT_HUNDRED;
                }
                latestDaysTrendData.put(COMPLIANCE_PERCENT, compliance);
                break;

            case "issuecompliance":
                Request request = new Request();
                request.setAg(ag);
                Map<String, String> filters = new HashMap<>();
                filters.put("ruleId.keyword", ruleId);
                filters.put("domain", domain);
                request.setFilter(filters);
                ResponseWithOrder responseWithOrder = complianceService
                        .getRulecompliance(request);
                latestDaysTrendData.put(COMPLAINT, responseWithOrder
                        .getResponse().get(0).get("passed"));
                latestDaysTrendData.put(NON_COMPLIANT, responseWithOrder
                        .getResponse().get(0).get("failed"));
                latestDaysTrendData.put(TOTAL, responseWithOrder.getResponse()
                        .get(0).get("assetsScanned"));
                latestDaysTrendData.put(COMPLIANCE_PERCENT, responseWithOrder
                        .getResponse().get(0).get(COMPLIANCE_PERCENT));

                break;

            case "patching":
                baseApiReturnMap = complianceService.getPatching(ag, null,null);
                compliantQuantity = baseApiReturnMap.get("patched_instances");
                noncompliantQuantity = baseApiReturnMap
                        .get("unpatched_instances");
                total = baseApiReturnMap.get("total_instances");
                compliance = baseApiReturnMap.get("patching_percentage");
                latestDaysTrendData.put("patched_instances", compliantQuantity);
                latestDaysTrendData.put("unpatched_instances",
                        noncompliantQuantity);
                latestDaysTrendData.put("total_instances", total);
                latestDaysTrendData.put("patching_percentage", compliance);
                break;

            case COMPLIANCE_PERCENTAGE:
                overallCompliance  = complianceService
                        .getOverallComplianceByDomain(ag, domain);
                if(!overallCompliance.isEmpty()){
                    for (Map.Entry<String,Object> entry : overallCompliance.entrySet()) {
                        latestDaysTrendData.put(entry.getKey(),entry.getValue());
                    }
                }
                break;

            case "issues":
                Map<String, Object> distroMap =  complianceService
                        .getDistribution(ag, domain);
                Map<String, Object> distroBySev = (Map<String, Object>) distroMap
                        .get("distribution_by_severity");
                latestDaysTrendData.put(TOTAL, distroMap.get("total_issues"));

                for (Map.Entry<String, Object> severity : distroBySev
                        .entrySet()) {
                    latestDaysTrendData.put(severity.getKey(),
                            severity.getValue());
                }
                break;
            default:
                //nothings
            }

            // Check if the trend already has todays data (Compare dates)
            // If yes, overwrite. If not, add at the end.
            LocalDate date = null;
            today = LocalDate.now();
            date = LocalDate.parse(latestDaysTrendData.get("date").toString(),
                    DateTimeFormatter.ISO_LOCAL_DATE);

            if (date.isEqual(today)) {
                logger.info("Latest days data available in trend data, so overwriting");
                trendList.set(trendList.size() - 1, latestDaysTrendData);
            } else if (date.isEqual(today.minusDays(1))) {
                // Ideally we need to consider this case only else, we may
                // unnecessarily append wrong data. FOr eg. In case of patching
                // if any previous/ progress is requested.
                logger.info("Latest days data is NOT available in trend data, so adding at the end");
                latestDaysTrendData.put("date",
                        today.format(DateTimeFormatter.ISO_LOCAL_DATE));
                trendList.add(latestDaysTrendData);
            }

        } catch (ServiceException e) {
            logger.error("Call to Base API to get todays data failed" , e);
            return;
        }

    }

    /**
     * Append with compliance percent.
     *
     * @param trendList the trend list
     */
    private void appendWithCompliancePercent(List<Map<String, Object>> trendList) {

        trendList.parallelStream().forEach(
                trend -> {
                    if (trend.get(COMPLIANCE_PERCENT) == null) {
                        double total = Double.parseDouble(trend.get(TOTAL)
                                .toString());
                        double compliant = Double.parseDouble(trend.get(COMPLAINT)
                                .toString());
                        double compliancePercent = HUNDRED;
                        if (total > 0) {
                            compliancePercent = Math.floor(compliant * HUNDRED
                                    / total);
                        }
                        trend.put(COMPLIANCE_PERCENT, compliancePercent);
                    }
                });
    }

    /**
     * Fill no data dates with previous.
     *
     * @param trendList the trend list
     * @param firstDay the first day
     * @param lastDay the last day
     */
    private void fillNoDataDatesWithPrevious(
            List<Map<String, Object>> trendList, LocalDate firstDay,
            LocalDate lastDay) {

        // We don't want data for future weeks. If the quarter being
        // requested is the ongoing quarter, the max we we are interested
        // is data up to and including the ongoing day in the ongoing week.
        if (lastDay.isAfter(LocalDate.now())) {
            lastDay = LocalDate.now();
        }

        List<LocalDate> listOfAllDates = new ArrayList<>();

        LocalDate iterationDate = firstDay;

        // Have a temp variable called iterationDate. Keep incrementing it by 1,
        // until we reach the end date. In each such iteration, add each date to
        // our list of dates
        while (!iterationDate.isAfter(lastDay)) {
            listOfAllDates.add(iterationDate);
            iterationDate = iterationDate.plusDays(1);
        }

        // Iterate through each date. If the data from ES is missing for any
        // such
        // date, add a dummy map with zero values
        Map<String, Object> currentData = new LinkedHashMap<>();
        currentData.put(TOTAL, 0);
        currentData.put(COMPLAINT, 0);
        currentData.put(NON_COMPLIANT, 0);
        currentData.put(COMPLIANCE_PERCENT, HUNDRED);

        for (int i = 0; i < listOfAllDates.size(); i++) {
            LocalDate date = listOfAllDates.get(i);
            Map<String, Object> trendInfo = getTrendDataForDate(trendList, date);
            if (trendInfo == null) {
                trendInfo = new LinkedHashMap<>();
                trendInfo.put("date", date.format(DateTimeFormatter.ISO_DATE));
                trendInfo.put(NON_COMPLIANT, currentData.get(NON_COMPLIANT));
                trendInfo.put(TOTAL, currentData.get(TOTAL));
                trendInfo.put(COMPLAINT, currentData.get(COMPLAINT));
                if (currentData.get(COMPLIANCE_PERCENT) != null) {
                    trendInfo.put(COMPLIANCE_PERCENT,
                            currentData.get(COMPLIANCE_PERCENT));
                }
                trendList.add(i, trendInfo);
            } else {
                currentData = trendInfo;
            }
        }

    }

    /**
     * Gets the trend data for date.
     *
     * @param trendList the trend list
     * @param date the date
     * @return the trend data for date
     */
    private Map<String, Object> getTrendDataForDate(
            List<Map<String, Object>> trendList, LocalDate date) {

        List<Map<String, Object>> match = trendList
                .stream()
                .filter(trendMap -> {
                    LocalDate dateInThisIteration = LocalDate
                            .parse(trendMap.get("date").toString(),
                                    DateTimeFormatter.ISO_DATE);
                    return dateInThisIteration.isEqual(date);
                }).collect(Collectors.toList());
        if (match != null && !match.isEmpty()) {
            return match.get(0);
        }
        return null;
    }

    /**
     * Segregate trend progress by week.
     *
     * @param assetGroup the asset group
     * @param trendProgressList the trend progress list
     * @param startDate the start date
     * @param endDate the end date
     * @return the map
     */
    private Map<String, Object> segregateTrendProgressByWeek(String assetGroup,
            List<Map<String, Object>> trendProgressList, LocalDate startDate,
            LocalDate endDate) {

        long maxInstancesForTheCompleteDateRange = 0;

        long totalNumberRunningValue = 0;
        long compliantRunningValue = 0;
        long noncompliantRunningValue = 0;
        double complianceRunningValue = 0;

        List<Map<String, Object>> allWeeksDataList = new ArrayList<>();

        // The first day of date range is taken as the first day of week 1 of
        // the
        // quarter. This
        // could be a Monday, Thursday or ANY day.
        LocalDate startingDayOfWeek = startDate;

        // Add 6 days to get the end date. If we start on a Thursday, the week
        // ends on next Wednesday
        LocalDate endingDayOfWeek = startingDayOfWeek.plusDays(SIX);

        List<Map<String, Object>> trendListForTheWeek = new ArrayList<>();

        // We will send 100 weeks at most. Start with week 1(There
        // is no week zero!)
        for (int weekNumber = 1; weekNumber <= HUNDRED; weekNumber++) {

            LocalDate startingDayOfWeekLocalCopy = startingDayOfWeek;
            LocalDate endingDayOfWeekLocalCopy = endingDayOfWeek;

            trendProgressList
                    .forEach(ruleTrendProgressMap -> ruleTrendProgressMap.forEach((
                            key, value) -> {

                        if ("date".equals(key)) {

                            // Check if this date falls in the week that we are
                            // currently interested in
                            LocalDate dateInThisIteration = LocalDate.parse(
                                    value.toString(),
                                    DateTimeFormatter.ISO_DATE);
                            if (dateInThisIteration
                                    .isAfter(startingDayOfWeekLocalCopy
                                            .minusDays(1))
                                    && (dateInThisIteration
                                            .isBefore(endingDayOfWeekLocalCopy
                                                    .plusDays(1)))) {
                                // If the date matches, lets pick the map which
                                // represents this date's patching data and add
                                // it to
                                // the weeks list
                                trendListForTheWeek.add(ruleTrendProgressMap);
                            }

                        }

                    }));

            Map<String, Object> mapForTheWeek = new LinkedHashMap<>();

            // First some k-v pairs for week number,week start date, week end
            // date
            mapForTheWeek.put("week", weekNumber);
            mapForTheWeek.put("start_date",
                    startingDayOfWeek.format(DateTimeFormatter.ISO_DATE));
            mapForTheWeek.put("end_date",
                    endingDayOfWeek.format(DateTimeFormatter.ISO_DATE));

            // Lets calculate the compliance for the week. We simply get the
            // compliance for the last day of the week

            complianceRunningValue = calculateWeeklyCompliance(trendListForTheWeek);
            mapForTheWeek.put(COMPLIANCE_PERCENTAGE, complianceRunningValue);
            trendListForTheWeek.forEach(ruleTrendProgressMap -> {
                // We don't need _id in the response
                    ruleTrendProgressMap.remove("_id");
                });

            // Store a 'copy' of the weeks array list instead of the original,
            // as we will clear the original and reuse it for the next
            // iteration. Lets call this by the key 'compliance_info'
            mapForTheWeek.put("compliance_info",
                    new ArrayList<Map<String, Object>>(trendListForTheWeek));

            if (!trendListForTheWeek.isEmpty()) {
                allWeeksDataList.add(mapForTheWeek);

                totalNumberRunningValue = (long) getLatestDaysNumericDataFromAWeeklyDataList(
                        TOTAL, trendListForTheWeek);
                compliantRunningValue = (long) getLatestDaysNumericDataFromAWeeklyDataList(
                        COMPLAINT, trendListForTheWeek);
                noncompliantRunningValue = (long) getLatestDaysNumericDataFromAWeeklyDataList(
                        NON_COMPLIANT, trendListForTheWeek);

                // Maintain a max instance number for the quarter that is being
                // processed.
                long maxInstancesRunningValue = (long) getMaxValueNumericDataFromAWeeklyDataList(
                        TOTAL, trendListForTheWeek);
                if (maxInstancesRunningValue > maxInstancesForTheCompleteDateRange) {
                    maxInstancesForTheCompleteDateRange = maxInstancesRunningValue;
                }

            }

            // Now, lets get ready for the iteration for next week
            trendListForTheWeek.clear();
            startingDayOfWeek = startingDayOfWeek.plusDays(7);
            endingDayOfWeek = endingDayOfWeek.plusDays(7);

            // If week ending date bypasses the quarter end date, lets rewind
            // back to quarter end date. The quarter end date will be set as the
            // week ending date.
        }

        Map<String, Object> quarterlyDataMap = new LinkedHashMap<>();
        quarterlyDataMap.put("ag", assetGroup);
        quarterlyDataMap.put("start_date",
                startDate.format(DateTimeFormatter.ISO_DATE));
        quarterlyDataMap.put("end_date",
                endDate.format(DateTimeFormatter.ISO_DATE));
        quarterlyDataMap.put("max", maxInstancesForTheCompleteDateRange);
        quarterlyDataMap.put(TOTAL, totalNumberRunningValue);
        quarterlyDataMap.put(COMPLAINT, compliantRunningValue);
        quarterlyDataMap.put(NON_COMPLIANT, noncompliantRunningValue);
        quarterlyDataMap.put(COMPLIANCE_PERCENTAGE, complianceRunningValue);

        quarterlyDataMap.put("compliance_trend", allWeeksDataList);

        return quarterlyDataMap;

    }

    /**
     * Gets the latest days numeric data from A weekly data list.
     *
     * @param dataKeyName the data key name
     * @param ruleTrendProgressListForTheWeek the rule trend progress list for the week
     * @return the latest days numeric data from A weekly data list
     */
    private double getLatestDaysNumericDataFromAWeeklyDataList(
            String dataKeyName,
            List<Map<String, Object>> ruleTrendProgressListForTheWeek) {

        int index = ruleTrendProgressListForTheWeek.size() - 1;

        // We take the latest days data, provided its a non-zero value
        while (index >= 0) {
            Object obj = ruleTrendProgressListForTheWeek.get(index).get(
                    dataKeyName);
            if (null != obj && Double.valueOf(obj.toString()) != 0) {
                return Double.valueOf(obj.toString());
            }
            index--;
        }

        return 0;
    }

    /**
     * Gets the max value numeric data from A weekly data list.
     *
     * @param dataKeyName the data key name
     * @param trendProgressListForTheWeek the trend progress list for the week
     * @return the max value numeric data from A weekly data list
     */
    private double getMaxValueNumericDataFromAWeeklyDataList(
            String dataKeyName,
            List<Map<String, Object>> trendProgressListForTheWeek) {

        double maxValue = 0;
        int index = trendProgressListForTheWeek.size() - 1;

        while (index >= 0) {
            Object obj = trendProgressListForTheWeek.get(index)
                    .get(dataKeyName);
            if (null != obj && Double.valueOf(obj.toString()) != 0
                    && (Double.valueOf(obj.toString()) > maxValue)) {
                maxValue = Double.valueOf(obj.toString());
            }
            index--;
        }

        return maxValue;
    }

    /**
     * Calculate weekly compliance.
     *
     * @param trendProgressListForTheWeek the trend progress list for the week
     * @return the double
     */
    private double calculateWeeklyCompliance(
            List<Map<String, Object>> trendProgressListForTheWeek) {

        int index = trendProgressListForTheWeek.size() - 1;
        while (index >= 0) {
            Object percentObj = trendProgressListForTheWeek.get(index).get(
                    COMPLIANCE_PERCENT);
            if (null != percentObj
                    && Double.valueOf(percentObj.toString()) != 0) {
                return Double.valueOf(percentObj.toString());
            }
            index--;
        }
        return HUNDRED;

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.IssueTrendService#getTrendIssues(java.lang.String, java.time.LocalDate, java.time.LocalDate, java.util.Map, java.lang.String)
     */
    @Override
    public Map<String, Object> getTrendIssues(String assetGroup,
            LocalDate from, LocalDate to, Map<String, String> filter,
            String domain) throws ServiceException {

        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("ag", assetGroup);

        String ttypes = complianceRepository.getTargetTypeForAG(assetGroup,
                filter.get(DOMAIN));
        List<Map<String, Object>> ruleDetails = null;
        if (!Strings.isNullOrEmpty(ttypes)) {
            try{
            ruleDetails = complianceRepository
                    .getRuleIdWithDisplayNameQuery(ttypes);
            }catch(DataException e){
                throw new ServiceException(e);
            }
        }

        Set<String> ruleSev = new HashSet<>();
        List<Map<String, Object>> ruleSevCatDetails;
            ruleSevCatDetails = complianceService
                    .getRuleSevCatDetails(ruleDetails);
        
        Map<String, Object> ruleSevDetails = ruleSevCatDetails.parallelStream()
                .collect(
                        Collectors.toMap(r -> r.get(RULEID).toString(),
                                r -> r.get(SEVERITY),
                                (oldvalue, newValue) -> newValue));
        ruleSevDetails.entrySet().parallelStream()
                .forEach(entry -> ruleSev.add(entry.getValue().toString()));

        List<Map<String, Object>> issueInfoList;
        try {
            issueInfoList = repository.getTrendIssues(
                    assetGroup, from, to, filter, ruleSev);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        if (!issueInfoList.isEmpty()) {
            issueInfoList.parallelStream().forEach(
                    issuemap -> {
                        issuemap.remove("_id");
                        double total = Double.parseDouble(issuemap.get(TOTAL)
                                .toString());
                        if (total == 0) {
                            for (Map.Entry<String, Object> issue : issuemap
                                    .entrySet()) {
                                if (!"date".equals(issue.getKey())) {
                                    issuemap.put(issue.getKey(), 0);
                                }
                            }
                        } else {

                            for (Map.Entry<String, Object> issue : issuemap
                                    .entrySet()) {
                                if (issuemap.get(issue.getKey()) == null)
                                    issuemap.put(issue.getKey(), 0);
                            }
                        }

                    });
            // Sort the list by the date in ascending order
            Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate.parse(
                    m1.get("date").toString(), DateTimeFormatter.ISO_DATE)
                    .compareTo(
                            LocalDate.parse(m2.get("date").toString(),
                                    DateTimeFormatter.ISO_DATE));

            Collections.sort(issueInfoList, comp);

            useRealTimeDataForLatestDate(issueInfoList, assetGroup, "issues",
                    null, domain);

            parentMap.put("issues_info", issueInfoList);
        }
        return parentMap;
    }

}
