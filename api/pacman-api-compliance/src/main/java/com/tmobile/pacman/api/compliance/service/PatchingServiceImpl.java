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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.compliance.client.AssetServiceClient;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;
import com.tmobile.pacman.api.compliance.domain.Medal;
import com.tmobile.pacman.api.compliance.repository.FilterRepository;
import com.tmobile.pacman.api.compliance.repository.PatchingRepository;
import com.tmobile.pacman.api.compliance.repository.ProjectionRepository;

/**
 * Stub for unimplemented APIs. Can be removed after all APIs are completed
 * 
 */
@Service
public class PatchingServiceImpl implements PatchingService, Constants {

    /** The repository. */
    @Autowired
    private PatchingRepository repository;

    /** The issue trend service. */
    @Autowired
    private IssueTrendService issueTrendService;

    /** The exec and director info. */
    private List<Map<String, Object>> execAndDirectorInfo;

    /** The filter repository. */
    @Autowired
    private FilterRepository filterRepository;

    /** The asset service client. */
    @Autowired
    private AssetServiceClient assetServiceClient;

    /** The projection repository. */
    @Autowired
    private ProjectionRepository projectionRepository;

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.PatchingService#
     * getNonCompliantNumberForAG(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getNonCompliantNumberForAG(
            String assetGroup) throws ServiceException {

        List<Map<String, Long>> appComplianceMapList = new ArrayList<>();

        List<Map<String, Object>> appComplianceList = new ArrayList<>();

        // Invoking ES
      try{
        AssetCountDTO[] targetTypes = filterRepository.getListOfTargetTypes(
                assetGroup, null);
        for (AssetCountDTO targettype : targetTypes) {
            if (StringUtils.isNotBlank(targettype.getType())) {
                appComplianceMapList.add(repository.getNonCompliantNumberForAgAndResourceType(assetGroup, targettype.getType()));
            }
            }
      } catch (DataException e) {
          throw new ServiceException(e);
      }
        for (Map<String, Long> appComplianceMap : appComplianceMapList) {

            for (Map.Entry<String, Long> entry : appComplianceMap.entrySet()) {
                Map<String, Object> createdMap = new HashMap<>();
                if (StringUtils.isNotBlank(entry.getKey())) {

                    createdMap.put("app", entry.getKey());
                    createdMap.put(NON_COMPLIANT_NUMBER, entry.getValue());
                    createdMap
                            .put("Director", fetchAppDirector(entry.getKey()));
                    appComplianceList.add(createdMap);
                }
            }
        }

        // Sort the list by the number of nonCompliantNumber in descending order
        Comparator<Map<String, Object>> comp = (m1, m2) -> Integer.compare(
                new Integer(m2.get(NON_COMPLIANT_NUMBER).toString()),
                new Integer(m1.get(NON_COMPLIANT_NUMBER).toString()));
        Collections.sort(appComplianceList, comp);
        if (appComplianceList.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return appComplianceList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.PatchingService#
     * getNonCompliantExecsForAG(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getNonCompliantExecsForAG(String assetGroup)
            throws ServiceException {
        List<Map<String, Long>> appComplianceMapList =new ArrayList<>();
        List<Map<String, Object>> appComplianceList = new ArrayList<>();

        // Invoking ES

        try{
            AssetCountDTO[] targetTypes = filterRepository.getListOfTargetTypes(
                    assetGroup, null);
            for (AssetCountDTO targettype : targetTypes) {
                if (StringUtils.isNotBlank(targettype.getType())) {
                    appComplianceMapList.add(repository.getNonCompliantNumberForAgAndResourceType(assetGroup, targettype.getType()));
                }
                }
          } catch (DataException e) {
              throw new ServiceException(e);
          }
        for (Map<String, Long> appComplianceMap : appComplianceMapList) {
            for (Map.Entry<String, Long> entry : appComplianceMap.entrySet()) {
                Map<String, Object> createdMap = new HashMap<>();
                if (StringUtils.isNotBlank(entry.getKey())) {
                    long currentNumber = entry.getValue();
                    long previousNumber = 0;
                    String execName = fetchAppExec(entry.getKey());
                    boolean sponsorDataOverwritten = false;
                    // One exec sponsor can have more than one application. If
                    // so,
                    // the number for each applications are to be summed up and
                    // the
                    // total number should be shown against the sponsors name
                    for (Map<String, Object> existingSponsorData : appComplianceList) {
                        if (execName.equals(existingSponsorData
                                .get("Executive Sponsor"))) {
                            previousNumber = (long) existingSponsorData
                                    .get(NON_COMPLIANT_NUMBER);
                            // Add the current number to the existing total of
                            // the
                            // sponsor, to get the updated total for the sponsor
                            long updatedNumber = currentNumber + previousNumber;
                            existingSponsorData.put(NON_COMPLIANT_NUMBER,
                                    updatedNumber);
                            sponsorDataOverwritten = true;
                            break;
                        }
                    }

                    // If this is a fresh sponsor, add it to the map. If
                    // existing
                    // one, we would have already updated the numbers above, so
                    // no
                    // need to add fresh
                    if (!sponsorDataOverwritten) {
                        createdMap.put(NON_COMPLIANT_NUMBER, currentNumber);
                        createdMap.put("Executive Sponsor", execName);
                        appComplianceList.add(createdMap);
                    }

                }
            }
        }

        // Sort the list by the number of nonCompliantNumber in descending order
        Comparator<Map<String, Object>> comp = (m1, m2) -> Integer.compare(
                new Integer(m2.get(NON_COMPLIANT_NUMBER).toString()),
                new Integer(m1.get(NON_COMPLIANT_NUMBER).toString()));
        Collections.sort(appComplianceList, comp);
        if (appComplianceList.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return appComplianceList;
    }

    /**
     * Fetch app exec.
     *
     * @param app
     *            the app
     * @return the string
     * @throws ServiceException
     *             the service exception
     */
    private String fetchAppExec(String app) throws ServiceException {
        if (null == execAndDirectorInfo) {
            try {
                execAndDirectorInfo = repository.getExecAndDirectorInfo();
            } catch (DataException e) {
                throw new ServiceException(e);
            }
        }
        String executiveSponsor = "Unknown";
        for (Map<String, Object> execMap : execAndDirectorInfo) {
            if (app.equals(execMap.get(APP_TAG))) {
                if (null != execMap.get(EXCUTIVE_SPONSOR)) {
                    executiveSponsor = execMap.get(EXCUTIVE_SPONSOR).toString();
                }

                return executiveSponsor;
            }
        }

        return executiveSponsor;

    }

    /**
     * Fetch app director.
     *
     * @param app
     *            the app
     * @return the object
     * @throws ServiceException
     *             the service exception
     */
    private Object fetchAppDirector(String app) throws ServiceException {
        if (null == execAndDirectorInfo) {
            try {
                execAndDirectorInfo = repository.getExecAndDirectorInfo();

            } catch (DataException e) {
                throw new ServiceException(e);
            }
        }

        String director = "Unknown";
        for (Map<String, Object> execMap : execAndDirectorInfo) {
            if (app.equals(execMap.get(APP_TAG))) {
                if (null != execMap.get(DIRECTOR)) {
                    director = execMap.get(DIRECTOR).toString();
                }

                return director;
            }
        }

        return director;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.tmobile.pacman.api.compliance.service.PatchingService#getPatchingDetails
     * (java.lang.String, java.util.Map)
     */
    @Override
    public List<Map<String, Object>> getPatchingDetails(String assetGroup,
            Map<String, String> filter) throws ServiceException {
        List<Map<String, Object>> patchingDetailsList;
        try {
            patchingDetailsList = new ArrayList<>();
            AssetCountDTO[] targetTypes = filterRepository
                    .getListOfTargetTypes(assetGroup, null);
            for (AssetCountDTO targettype : targetTypes) {
                if (StringUtils.isNotBlank(targettype.getType()) && ("ec2".equals(targettype.getType()))) {
                        // get apps info
                        List<Map<String, Object>> cloudAppsList = getCloudAppList(CLOUD);
                        Map<String, Object> cloudappstodirectorMap = getCloudAppstodirectorMap(
                                cloudAppsList, DIRECTOR);
                        Map<String, Object> cloudappstoExecutiveMap = getCloudAppstodirectorMap(
                                cloudAppsList, EXCUTIVE_SPONSOR);
                        List<Map<String, Object>> issueInfo = repository
                                .getIssueInfo(assetGroup);
                        Map<String, String> instanceIssueStatusLookup = prepareInstanceToIssueStatusLookup(issueInfo);
                        List<Map<String, Object>> instanceInfo = repository
                                .getInstanceInfo(assetGroup, filter);
                        List<Map<String, Object>> closedInfo = repository
                                .getClosedIssueInfo(assetGroup,
                                        instanceInfo.size());
                        Map<String, String> closedInfoLookup = prepareInstanceToClosedIssueStatusLookup(closedInfo);
                        patchingDetailsListForEc2(patchingDetailsList,
                                instanceInfo, instanceIssueStatusLookup,
                                cloudappstodirectorMap, 
                                cloudappstoExecutiveMap, closedInfoLookup,
                                targettype.getType());

                }
            }
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        return patchingDetailsList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.tmobile.pacman.api.compliance.service.PatchingService#getPatchingProgress
     * (java.lang.String, int, int)
     */
    @Override
    public Map<String, Object> getPatchingProgress(String assetGroup, int year,
            int quarter) throws ServiceException {
        List<Map<String, Object>> patchingProgressList;
        try {
            patchingProgressList = repository.getPatchingProgress(assetGroup,
                    getFirstDayOfQuarter(quarter, year),
                    getLastDayOfQuarter(quarter, year));

            // Sort the list by the date in ascending order
            Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate.parse(
                    m1.get("date").toString(), DateTimeFormatter.ISO_DATE)
                    .compareTo(
                            LocalDate.parse(m2.get("date").toString(),
                                    DateTimeFormatter.ISO_DATE));
            Collections.sort(patchingProgressList, comp);
            // Elastic Search might not have data for some dates. But we want to
            // send consistent data to the consumers of this service, so we will
            // populate zeros where data is unavailable
            fillNoDataDatesWithPrevious(patchingProgressList, year, quarter);
            issueTrendService.useRealTimeDataForLatestDate(
                    patchingProgressList, assetGroup, "patching", null, null);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        return segregatePatchingProgressByWeek(assetGroup,
                patchingProgressList, year, quarter);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.PatchingService#
     * getQuartersWithPatchingData(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getQuartersWithPatchingData(
            String assetGroup) throws ServiceException {
        Map<String, Long> patchingCountByQuarter;
        try {
            patchingCountByQuarter = repository
                    .getQuartersWithPatchingData(assetGroup);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        Map<Long, List<Long>> yearQuarterMap = new HashMap<>();

        patchingCountByQuarter.forEach((key, value) -> {
            LocalDateTime dateTime = LocalDateTime.parse(key,
                    DateTimeFormatter.ISO_DATE_TIME);

            LocalDate date = dateTime.toLocalDate();
            Long year = (long) date.getYear();
            List<Long> existingQuartersForCurrentYear = yearQuarterMap
                    .get(year);
            if (null == existingQuartersForCurrentYear) {
                existingQuartersForCurrentYear = new ArrayList<>();
                yearQuarterMap.put(year, existingQuartersForCurrentYear);
            }

            Long quarter = (long) (((date.getMonthValue() - 1) / THREE) + 1);
            existingQuartersForCurrentYear.add(quarter);

        });

        List<Map<String, Object>> listOfYears = new ArrayList<>();

        yearQuarterMap.forEach((key, value) -> {
            Map<String, Object> mapForCurrentYear = new HashMap<>();
            mapForCurrentYear.put("year", key);
            mapForCurrentYear.put("quarters", value);
            listOfYears.add(mapForCurrentYear);
        });
        if (listOfYears.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return listOfYears;
    }

    /**
     * Fill no data dates with previous.
     *
     * @param patchingProgressList
     *            the patching progress list
     * @param year
     *            the year
     * @param quarter
     *            the quarter
     */
    private void fillNoDataDatesWithPrevious(
            List<Map<String, Object>> patchingProgressList, int year,
            int quarter) {
        LocalDate firstDay = getFirstDayOfQuarter(quarter, year);
        LocalDate lastDay = getLastDayOfQuarter(quarter, year);

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
        currentData.put(TOTAL_INSTANCES, 0);
        currentData.put(PATCHED_INSTANCE, 0);
        currentData.put(UNPATCHED_INSTANCE, 0);

        for (int i = 0; i < listOfAllDates.size(); i++) {
            LocalDate date = listOfAllDates.get(i);
            Map<String, Object> patchInfo = getPatchingDataForDate(
                    patchingProgressList, date);
            if (patchInfo == null) {
                patchInfo = new LinkedHashMap<>();
                patchInfo.put("date", date.format(DateTimeFormatter.ISO_DATE));
                patchInfo
                        .put(TOTAL_INSTANCES, currentData.get(TOTAL_INSTANCES));
                patchInfo.put(PATCHED_INSTANCE,
                        currentData.get(PATCHED_INSTANCE));
                patchInfo.put(UNPATCHED_INSTANCE,
                        currentData.get(UNPATCHED_INSTANCE));
                patchingProgressList.add(i, patchInfo);
            } else {
                currentData = patchInfo;
            }
        }

    }

    /**
     * Gets the patching data for date.
     *
     * @param patchingProgressList
     *            the patching progress list
     * @param date
     *            the date
     * @return the patching data for date
     */
    private Map<String, Object> getPatchingDataForDate(
            List<Map<String, Object>> patchingProgressList, LocalDate date) {

        List<Map<String, Object>> match = patchingProgressList
                .stream()
                .filter(patchMap -> {
                    LocalDate dateInThisIteration = LocalDate
                            .parse(patchMap.get("date").toString(),
                                    DateTimeFormatter.ISO_DATE);
                    return dateInThisIteration.isEqual(date);
                }).collect(Collectors.toList());
        if (match != null && !match.isEmpty())
            return match.get(0);
        return null;
    }

    /**
     * Segregate patching progress by week.
     *
     * @param assetGroup
     *            the asset group
     * @param patchingProgressList
     *            the patching progress list
     * @param year
     *            the year
     * @param quarter
     *            the quarter
     * @return the map
     * @throws ServiceException
     *             the service exception
     */
    private Map<String, Object> segregatePatchingProgressByWeek(
            String assetGroup, List<Map<String, Object>> patchingProgressList,
            int year, int quarter) throws ServiceException {
        long maxInstancesForTheQuarter = 0;

        long totalInstancesRunningValue = 0;
        long patchedInstancesRunningValue = 0;
        long unpatchedInstancesRunningValue = 0;
        double complianceRunningValue = 0;

        List<Map<String, Object>> allWeeksDataList = new ArrayList<>();

        // The first day of quarter is taken as the first day of week 1 of the
        // quarter. This
        // could be a Monday, Thursday or ANY day.
        LocalDate startingDayOfWeek = getFirstDayOfQuarter(quarter, year);

        // Add 6 days to get the end date. If we start on a Thursday, the week
        // ends on next Wednesday
        LocalDate endingDayOfWeek = startingDayOfWeek.plusDays(SIX);

        List<Map<String, Object>> patchingProgressListForTheWeek = new ArrayList<>();

        // Any quarter will have 14 weeks at the most(Q1 and Q2 will have 13
        // weeks. Q3 and Q4 will have 14 weeks, the 14th week being just a 1 day
        // week. Start with week 1(There
        // is no week zero!)
        for (int weekNumber = 1; weekNumber <= FOURTEEN; weekNumber++) {

            LocalDate startingDayOfWeekLocalCopy = startingDayOfWeek;
            LocalDate endingDayOfWeekLocalCopy = endingDayOfWeek;

            patchingProgressList
                    .forEach(patchingProgressMap -> patchingProgressMap.forEach((
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
                                patchingProgressListForTheWeek
                                        .add(patchingProgressMap);
                            }

                        }

                    }));

            Map<String, Object> mapForTheWeek = new LinkedHashMap<>();

            // First some k-v pairs for week number,week start date, week end
            // date
            mapForTheWeek.put("week", weekNumber);
            mapForTheWeek.put(START_DATE,
                    startingDayOfWeek.format(DateTimeFormatter.ISO_DATE));
            mapForTheWeek.put(END_DATE,
                    endingDayOfWeek.format(DateTimeFormatter.ISO_DATE));

            // Lets calculate the compliance for the week. We simply get the
            // compliance for the last day of the week
            complianceRunningValue = calculateWeeklyCompliance(patchingProgressListForTheWeek);
            mapForTheWeek.put("compliance", complianceRunningValue);

            patchingProgressListForTheWeek.forEach(patchingProgressMap -> {

                // We don't need _id in the response
                    patchingProgressMap.remove("_id");

                    /*
                     * We only need this to see the compliance percentage level
                     * on the last day of each week and on current day. We have
                     * calculated this above already. Lets remove it from the
                     * response, else it will show up on all 'days'
                     */
                    patchingProgressMap.remove("patching_percentage");
                });

            // Store a 'copy' of the weeks array list instead of the original,
            // as we will clear the original and reuse it for the next
            // iteration. Lets call this by the key 'patching_info'
            mapForTheWeek.put("patching_info",
                    new ArrayList<Map<String, Object>>(
                            patchingProgressListForTheWeek));

            if (!patchingProgressListForTheWeek.isEmpty()) {
                allWeeksDataList.add(mapForTheWeek);

                totalInstancesRunningValue = (long) getLatestDaysNumericDataFromAWeeklyDataList(
                        TOTAL_INSTANCES, patchingProgressListForTheWeek);
                patchedInstancesRunningValue = (long) getLatestDaysNumericDataFromAWeeklyDataList(
                        PATCHED_INSTANCE, patchingProgressListForTheWeek);
                unpatchedInstancesRunningValue = (long) getLatestDaysNumericDataFromAWeeklyDataList(
                        UNPATCHED_INSTANCE, patchingProgressListForTheWeek);

                // Maintain a max instance number for the quarter that is being
                // processed.
                long maxInstancesRunningValue = (long) getMaxValueNumericDataFromAWeeklyDataList(
                        TOTAL_INSTANCES, patchingProgressListForTheWeek);
                if (maxInstancesRunningValue > maxInstancesForTheQuarter) {
                    maxInstancesForTheQuarter = maxInstancesRunningValue;
                }

            }

            // Now, lets get ready for the iteration for next week
            patchingProgressListForTheWeek.clear();
            startingDayOfWeek = startingDayOfWeek.plusDays(SEVEN);
            endingDayOfWeek = endingDayOfWeek.plusDays(SEVEN);

            // If week ending date bypasses the quarter end date, lets rewind
            // back to quarter end date. The quarter end date will be set as the
            // week ending date.
            if (endingDayOfWeek.isAfter(getLastDayOfQuarter(quarter, year))) {
                endingDayOfWeek = getLastDayOfQuarter(quarter, year);
            }

        }

        Map<String, Object> quarterlyDataMap = new LinkedHashMap<>();
        quarterlyDataMap.put("ag", assetGroup);
        quarterlyDataMap.put("year", year);
        quarterlyDataMap.put("quarter", quarter);

        quarterlyDataMap.put(START_DATE, "");
        quarterlyDataMap.put(END_DATE, "");
        quarterlyDataMap.put("amiavail_date", "");
        quarterlyDataMap.put("internal_target", "");

        boolean agContainsOnPrem = false;
        boolean agContainsEc2 = false;
        AssetCountDTO[] targetTypes = null;
        try {
            targetTypes = filterRepository.getListOfTargetTypes(assetGroup,
                    null);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        for (AssetCountDTO targettype : targetTypes) {
            if (StringUtils.isNotBlank(targettype.getType())) {
                if ("onpremserver".equals(targettype.getType())) {
                    agContainsOnPrem = true;
                }
                if ("ec2".equals(targettype.getType())) {
                    agContainsEc2 = true;
                }
            }
        }

        if (agContainsEc2 && (!agContainsOnPrem)) {

            quarterlyDataMap.put(
                    START_DATE,
                    getFirstDayOfQuarter(quarter, year).format(
                            DateTimeFormatter.ISO_DATE));
            quarterlyDataMap.put(END_DATE, getLastDayOfQuarter(quarter, year)
                    .format(DateTimeFormatter.ISO_DATE));

            String amilAvailDate = repository.getAmilAvailDate(year, quarter);

            if (null != amilAvailDate) {
                int patchingWindow = repository.getPatchingWindow();
                quarterlyDataMap.put("amiavail_date", amilAvailDate);
                quarterlyDataMap.put("internal_target", (LocalDate
                        .parse(amilAvailDate).plusDays(patchingWindow))
                        .toString());
            }
        }

        quarterlyDataMap.put("max_instances", maxInstancesForTheQuarter);

        quarterlyDataMap.put(TOTAL_INSTANCES, totalInstancesRunningValue);
        quarterlyDataMap.put(PATCHED_INSTANCE, patchedInstancesRunningValue);
        quarterlyDataMap
                .put(UNPATCHED_INSTANCE, unpatchedInstancesRunningValue);
        quarterlyDataMap.put("compliance", complianceRunningValue);
        quarterlyDataMap.put("patching_progress", allWeeksDataList);

        /* commenting this as we implemted projection api seprately */
        List<Map<String, Object>> emptyList = new ArrayList<>();
        quarterlyDataMap.put("projection_info", emptyList);

        return quarterlyDataMap;

    }

    /**
     * Calculate weekly compliance.
     *
     * @param patchingProgressListForTheWeek
     *            the patching progress list for the week
     * @return the double
     */
    private double calculateWeeklyCompliance(
            List<Map<String, Object>> patchingProgressListForTheWeek) {

        int index = patchingProgressListForTheWeek.size() - 1;
        while (index >= 0) {
            Object patchingPercentObject = patchingProgressListForTheWeek.get(
                    index).get("patching_percentage");
            if (null != patchingPercentObject
                    && Double.valueOf(patchingPercentObject.toString()) != 0) {
                return Double.valueOf(patchingPercentObject.toString());
            }
            index--;
        }
        return 0;

    }

    /**
     * Gets the latest days numeric data from A weekly data list.
     *
     * @param dataKeyName
     *            the data key name
     * @param patchingProgressListForTheWeek
     *            the patching progress list for the week
     * @return the latest days numeric data from A weekly data list
     */
    private double getLatestDaysNumericDataFromAWeeklyDataList(
            String dataKeyName,
            List<Map<String, Object>> patchingProgressListForTheWeek) {

        int index = patchingProgressListForTheWeek.size() - 1;

        // We take the latest days data, provided its a non-zero value
        while (index >= 0) {
            Object obj = patchingProgressListForTheWeek.get(index).get(
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
     * @param dataKeyName
     *            the data key name
     * @param patchingProgressListForTheWeek
     *            the patching progress list for the week
     * @return the max value numeric data from A weekly data list
     */
    private double getMaxValueNumericDataFromAWeeklyDataList(
            String dataKeyName,
            List<Map<String, Object>> patchingProgressListForTheWeek) {

        double maxValue = 0;
        int index = patchingProgressListForTheWeek.size() - 1;

        while (index >= 0) {
            Object obj = patchingProgressListForTheWeek.get(index).get(
                    dataKeyName);
            if (null != obj && Double.valueOf(obj.toString()) != 0
                    && Double.valueOf(obj.toString()) > maxValue) {
                maxValue = Double.valueOf(obj.toString());
            }
            index--;
        }

        return maxValue;
    }

    /**
     * Prepare instance to issue status lookup.
     *
     * @param issueInfo
     *            the issue info
     * @return the map
     */
    private Map<String, String> prepareInstanceToIssueStatusLookup(
            List<Map<String, Object>> issueInfo) {

        Map<String, String> instanceIssueStatusLookup = new HashMap<>();

        issueInfo.forEach(issueMap -> instanceIssueStatusLookup.put(issueMap
                .get(RESOURCEID).toString(), issueMap.get("issueStatus")
                .toString()));
        return instanceIssueStatusLookup;
    }

    /**
     * Prepare instance to closed issue status lookup.
     *
     * @param closedIssueInfo
     *            the closed issue info
     * @return the map
     */
    private Map<String, String> prepareInstanceToClosedIssueStatusLookup(
            List<Map<String, Object>> closedIssueInfo) {

        Map<String, String> closedIssueReasonsLookup = new HashMap<>();

        closedIssueInfo.forEach(closedIssueMap -> closedIssueReasonsLookup.put(
                closedIssueMap.get(RESOURCEID).toString(),
                closedIssueMap.get("reason-to-close").toString()));
        return closedIssueReasonsLookup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.PatchingService#
     * getFirstDayOfQuarter(int, int)
     */
    @Override
    public LocalDate getFirstDayOfQuarter(int quarterNumber, int year) {
        int startingMonth = (quarterNumber * THREE) - TWO;

        return LocalDate.of(year, startingMonth, 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.tmobile.pacman.api.compliance.service.PatchingService#getLastDayOfQuarter
     * (int, int)
     */
    @Override
    public LocalDate getLastDayOfQuarter(int quarterNumber, int year) {

        int endingMonth = (quarterNumber * THREE);

        // Find last month first. Start with day 1, then find out last day.
        LocalDate endDate = LocalDate.of(year, endingMonth, 1);
        endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());

        return endDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.tmobile.pacman.api.compliance.service.PatchingService#getOngoingQuarter
     * ()
     */
    @Override
    public int getOngoingQuarter() {
        LocalDate date = LocalDate.now();
        return ((date.getMonthValue() - 1) / THREE) + 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.tmobile.pacman.api.compliance.service.PatchingService#getOngoingYear
     * ()
     */
    @Override
    public int getOngoingYear() {
        LocalDate date = LocalDate.now();
        return date.getYear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.PatchingService#
     * filterMatchingCollectionElements(java.util.List, java.lang.String,
     * boolean)
     */
    @Override
    public Object filterMatchingCollectionElements(
            List<Map<String, Object>> masterDetailList, String searchText,
            boolean b) throws ServiceException {
        return CommonUtils.filterMatchingCollectionElements(masterDetailList,
                searchText, true);
    }

    /**
     * Prepare onprem to issue status lookup.
     *
     * @param onpremIssueInfo
     *            the onprem issue info
     * @return the map
     */
    private Map<String, String> prepareOnpremToIssueStatusLookup(
            List<Map<String, Object>> onpremIssueInfo) {

        Map<String, String> resourceIssueStatusLookup = new HashMap<>();

        onpremIssueInfo.forEach(issueMap -> resourceIssueStatusLookup.put(
                issueMap.get(RESOURCEID).toString(), issueMap
                        .get("issueStatus").toString()));
        return resourceIssueStatusLookup;
    }

    /**
     * Gets the patching details list.
     *
     * @param resourceId
     *            the resource id
     * @param name
     *            the name
     * @param status
     *            the status
     * @param kernelVersion
     *            the kernel version
     * @param source
     *            the source
     * @param assetType
     *            the asset type
     * @param ipAddress
     *            the ip address
     * @param accountName
     *            the account name
     * @param application
     *            the application
     * @param environment
     *            the environment
     * @param vpcid
     *            the vpcid
     * @param patchingDetailsList
     *            the patching details list
     * @param directors
     *            the directors
     * @param executiveSponser
     *            the executive sponser
     * @param targetType
     *            the target type
     * @return the patching details list
     */
    private List<Map<String, Object>> getPatchingDetailsList(Object resourceId,
            Object name, String status, String kernelVersion, String source,
            String assetType, Object ipAddress, Object accountName,
            Object application, Object environment, Object vpcid,
            List<Map<String, Object>> patchingDetailsList, Object directors,
            Object executiveSponser, String targetType) {
        Map<String, Object> patchingDetail = new LinkedHashMap<>();
        patchingDetail.put(RESOURCEID, resourceId);
        patchingDetail.put("name", name);
        patchingDetail.put("status", status);
        patchingDetail.put(KERNEL_RELEASE, kernelVersion);
        patchingDetail.put(SOURCE, source);
        patchingDetail.put("assetType", assetType);
        patchingDetail.put(IP_ADDRESS, ipAddress);
        patchingDetail.put(ACCOUNT_NAME, accountName);
        patchingDetail.put("application", application);
        patchingDetail.put("environment", environment);
        patchingDetail.put(VPC_ID, vpcid);
        patchingDetail.put("directors", directors);
        patchingDetail.put(EXCUTIVE_SPONSOR, executiveSponser);
        patchingDetail.put("targetType", targetType);
        patchingDetailsList.add(patchingDetail);
        return patchingDetailsList;
    }

    private List<Map<String, Object>> getCloudAppList(String type)
            throws DataException {
            return projectionRepository.getAppsDetails(type);
    }

    private Map<String, Object> getCloudAppstodirectorMap(
            List<Map<String, Object>> appsList, String type)
            {
        return appsList
                .parallelStream()
                .filter(apps -> apps.get(type) != null)
                .collect(
                        Collectors.toMap(apps -> apps.get(APP_TAG).toString(),
                                apps -> apps.get(type),
                                (oldValue, newValue) -> newValue));
    }

    private List<Map<String, Object>> patchingDetailsListForEc2(
            List<Map<String, Object>> patchingDetailsList,
            List<Map<String, Object>> resourceInfo,
            Map<String, String> issueStatusLookup,
            Map<String, Object> ec2AppstoDirectorMap,
            Map<String, Object> ec2AppstoExecutiveMap,
            Map<String, String> closedInfoLookup, String targetType) {

        resourceInfo.forEach(instanceInfoMap -> {

            String kernelVersion = "";
            String source = "";
            String status = "";
            String director = "";
            String executive = "";
            String application = "";
            String name = "";
            String environment = "";
            String type = "";
            String ipAddress = "";
            String accountName = "";
            String vpcId = "";
            if (null != issueStatusLookup && !issueStatusLookup.isEmpty()) { 
                if (!Strings.isNullOrEmpty(issueStatusLookup.get(instanceInfoMap.get(RESOURCEID)))) {
                    status = returnStatus(issueStatusLookup.get(instanceInfoMap.get(RESOURCEID)));
                } else {
                    status = "Compliant";
                    source = getKernelVersionForEc2(closedInfoLookup, instanceInfoMap,targetType).get(SOURCE);
                    kernelVersion = getKernelVersionForEc2(closedInfoLookup, instanceInfoMap,targetType).get(KERNEL_VERSION); 
                }
                if (EC2.equals(targetType)) {
                    type = "cloud";
                    name = getAttributesValue("tags.Name",instanceInfoMap);
                    ipAddress = getAttributesValue("privateipaddress",instanceInfoMap);
                    accountName = getAttributesValue(ACCOUNT_NAME,instanceInfoMap);
                    vpcId = getAttributesValue(VPC_ID,instanceInfoMap);

                }
            }
            if (null != instanceInfoMap.get(TAGS_APPLICATION)) {
                director = getDirectorsAndExecutiveSponserName(ec2AppstoDirectorMap, instanceInfoMap, TAGS_APPLICATION);
                executive = getDirectorsAndExecutiveSponserName(ec2AppstoExecutiveMap, instanceInfoMap, TAGS_APPLICATION);
                application = getAttributesValue(TAGS_APPLICATION,instanceInfoMap);
            }
            environment = getAttributesValue(TAGS_ENVIRONMENT,instanceInfoMap);

            getPatchingDetailsList(instanceInfoMap.get(RESOURCEID), name,
                    status, kernelVersion, source, type, ipAddress,
                    accountName, application, environment, vpcId,
                    patchingDetailsList, director, executive, targetType);

        });
        return patchingDetailsList;
    }

    private String getAttributesValue(String attribute,
            Map<String, Object> instanceInfoMap) {
        if (instanceInfoMap.containsKey(attribute)) {
            return instanceInfoMap.get(attribute).toString();
        }
        return "";
    }
    private String getDirectorsAndExecutiveSponserName(Map<String, Object> ec2AppstoDirectorOrExecutiveSponsorsMap,Map<String, Object> instanceInfoMap,String attribute){
        if (null != ec2AppstoDirectorOrExecutiveSponsorsMap.get(instanceInfoMap
                .get(attribute))) {
            return ec2AppstoDirectorOrExecutiveSponsorsMap.get(
                    instanceInfoMap.get(attribute)).toString();
        }
        return "";
    }
    
    private Map<String,String> getKernelVersionForEc2(Map<String, String> closedInfoLookup,Map<String, Object> instanceInfoMap,String targetType){
        String value = null;
        String source = null;
        String kernelVersion = null;
        Map<String,String> kvAndSourceMap = new HashMap<>();
        if (EC2.equals(targetType) && (null != closedInfoLookup 
                && !closedInfoLookup.isEmpty() && (!Strings.isNullOrEmpty(closedInfoLookup.get(instanceInfoMap
                        .get(RESOURCEID)))))) {
                value = closedInfoLookup.get(instanceInfoMap
                        .get(RESOURCEID)).substring(1, closedInfoLookup.get(instanceInfoMap
                        .get(RESOURCEID)).length() - 1);
                Map<String, String> resontoclose=null;
                if(!value.isEmpty()){
                    resontoclose = Arrays
                           .stream(value.split(", "))
                           .map(s -> s.split("="))
                           .collect(Collectors.toMap(a -> a[0], // key
                                   a -> a[1] // value
                                   ));}
            
                if (resontoclose!=null&&null != resontoclose.get("sourceType")) {
                    source = resontoclose.get("sourceType");
                }
                if (resontoclose!=null&&null != resontoclose.get(KERNEL_VERSION)) {
                    kernelVersion = resontoclose
                            .get(KERNEL_VERSION);
                }
    }
        kvAndSourceMap.put(SOURCE, source);
        kvAndSourceMap.put(KERNEL_VERSION, kernelVersion);
        return kvAndSourceMap;
    }
    
    private String returnStatus(String value){
        if (value.equalsIgnoreCase(EXEMPTED)) {
            return "Exempted";
        } else {
            return "Non Compliant";
        }
    }
    
	@Override
	public Medal getStarRatingForAgPatching(String ag, int quarter, int year) {
		
	    LocalDate firstDay =  LocalDate.parse(repository.getAmilAvailDate(year, quarter));
		LocalDate lastDay = getLastDayOfQuarter(quarter, year);

		boolean incompleteQuarter = false;

		// We don't want data for future weeks. If the quarter being
		// requested is the ongoing quarter, the max we we are interested
		// is data up to and including the ongoing day in the ongoing week.
		if (lastDay.isAfter(LocalDate.now())) {
			lastDay = LocalDate.now();
			incompleteQuarter = true;
		}

		List<Map<String, Object>> ratingList = repository.getPatchingPercentForDateRange(ag, firstDay, lastDay);
		List<Map<String, Object>> filteredRatingList = new ArrayList<Map<String, Object>>();

		ratingList.forEach(rateMap -> {
			if (ag.equals(rateMap.get("ag"))) {
				filteredRatingList.add(rateMap);
			}
		});
		
		

      

		// Sort the list by the date in descending order
		Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate
				.parse(m2.get("date").toString(), DateTimeFormatter.ISO_DATE)
				.compareTo(LocalDate.parse(m1.get("date").toString(), DateTimeFormatter.ISO_DATE));
		Collections.sort(filteredRatingList, comp);
		
		List<String> patchingPercentFor45Days = new ArrayList<>(); 
		filteredRatingList.forEach(rateMap->{
			LocalDate d = LocalDate.parse(rateMap.get("date").toString(),
					DateTimeFormatter.ISO_DATE);
			if((patchingPercentFor45Days.isEmpty()) && (ChronoUnit.DAYS.between(firstDay,d)<45)) {
				patchingPercentFor45Days.add(rateMap.get("patching_percentage").toString());
			}
		});
		
		List<String> patchingPercentFor60Days = new ArrayList<>(); 
		filteredRatingList.forEach(rateMap->{
			LocalDate d = LocalDate.parse(rateMap.get("date").toString(),
					DateTimeFormatter.ISO_DATE);
			if((patchingPercentFor60Days.isEmpty()) && (ChronoUnit.DAYS.between(firstDay,d)<60)) {
				patchingPercentFor60Days.add(rateMap.get("patching_percentage").toString());
			}
		});

		Medal patchingMedal = new Medal();
		patchingMedal.setMedalType("");
		patchingMedal.setMedalStatus("");
		
		double patchingPercentValueFor45Days = patchingPercentFor45Days.isEmpty()?0:Double.parseDouble(patchingPercentFor45Days.get(0));
		double patchingPercentValueFor60Days = patchingPercentFor60Days.isEmpty()?0:Double.parseDouble(patchingPercentFor60Days.get(0));
		double lastDatePatchingPercentValue = filteredRatingList.isEmpty()?0:Double.parseDouble(filteredRatingList.get(0).get("patching_percentage").toString());
		
		if(incompleteQuarter) {
			patchingMedal.setMedalStatus("Pending");
			if(patchingPercentValueFor45Days>=100) {
				patchingMedal.setMedalType("GOLD");
			}else if(patchingPercentValueFor60Days>=100) {
				patchingMedal.setMedalType("SILVER");
			}
		}else {
			//If we reach here, this means quarter end data is available
			patchingMedal.setMedalStatus("Confirmed");
			if(patchingPercentValueFor45Days>=100 && lastDatePatchingPercentValue>=100) {
				patchingMedal.setMedalType("GOLD");
			}else if(patchingPercentValueFor45Days>=100 && lastDatePatchingPercentValue>=90) {
				patchingMedal.setMedalType("SILVER");
			}else if(patchingPercentValueFor60Days>=100 && lastDatePatchingPercentValue>=100) {
				patchingMedal.setMedalType("SILVER");
			}else if(patchingPercentValueFor60Days>=100 && lastDatePatchingPercentValue>=90) {
				patchingMedal.setMedalType("BRONZE");
			}else if(lastDatePatchingPercentValue>=100) {
				patchingMedal.setMedalType("BRONZE");
			}else {
				patchingMedal.setMedalStatus("");
			}
			
		}

		return patchingMedal;
	}

}
