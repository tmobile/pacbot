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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;

/**
 * The Class ProjectionRepositoryImpl.
 */
@Repository
public class ProjectionRepositoryImpl implements ProjectionRepository, Constants {

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

    /**
     * Inits the.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /** The es repository. */
    @Autowired
    ElasticSearchRepository esRepository;

    /** The rds repository. */
    @Autowired
    PacmanRdsRepository rdsRepository;

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * updateProjectionByTargetType
     * (com.tmobile.pacman.api.compliance.domain.ProjectionRequest)
     */
    @Override
    public Boolean updateProjectionByTargetType(ProjectionRequest projectionRequest) throws DataException {
        String resourceType = projectionRequest.getResourceType();
        int year = projectionRequest.getYear();
        int quarter = projectionRequest.getQuarter();
        List<Map<String, Object>> projectionByweeksList = projectionRequest.getProjectionByWeek();
        boolean isUpdated = false;
        List<String> insertValuesList = new ArrayList<>();
        List<String> deleteValuesList = new ArrayList<>();
        List<LocalDate> lastDayOfEachWeek = getListOfLastWeekDateOfQuarter();

        if (lastDayOfEachWeek.size() == projectionByweeksList.size()) {
            projectionByweeksList.parallelStream().forEach(
                    projectionByWeek -> {
                        int week = 0;
                        long projection = 0;
                        if (projectionByWeek.containsKey("week")) {
                            week = Integer.parseInt(projectionByWeek.get("week").toString());

                        }
                        if (projectionByWeek.containsKey("projection")) {
                            projection = Long.parseLong(projectionByWeek.get("projection").toString());
                        }
                        StringBuilder deleteValue = new StringBuilder("(").append("'" + year + "'")
                                .append("," + "'" + quarter + "'").append("," + "'" + week + "'").append(")");
                        StringBuilder insertValue = new StringBuilder("(").append("'" + resourceType + "'")
                                .append("," + "'" + year + "'").append("," + "'" + quarter + "'")
                                .append("," + "'" + week + "'").append("," + "'" + projection + "'").append(")");

                        synchronized (deleteValuesList) {
                            deleteValuesList.add(deleteValue.toString());
                        }
                        synchronized (insertValuesList) {
                            insertValuesList.add(insertValue.toString());
                        }

                    });
            StringBuilder insertQuery = new StringBuilder(
                    "INSERT INTO pac_v2_projections ( resourcetype,year,quarter,week,projection) VALUES ");
            insertQuery.append(String.join(",", insertValuesList));
            StringBuilder deleteQuery = new StringBuilder("DELETE FROM pac_v2_projections WHERE resourceType = '"
                    + resourceType + "' AND " + "( year,quarter,week) IN");
            deleteQuery.append("(").append(String.join(",", deleteValuesList)).append(")");
            rdsRepository.executeQuery(deleteQuery.toString());
            int[] count = rdsRepository.executeQuery(insertQuery.toString());
            if (count[0] > 0) {
                isUpdated = true;
            }
        } else {
            throw new DataException("Please enter correct no of weeks for the quarter");
        }

        return isUpdated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getProjectionDetailsFromDb(java.lang.String, int, int)
     */
    @Override
    public List<Map<String, Object>> getProjectionDetailsFromDb(String targetType, int year, int quarter)
            throws DataException {
        String query = "select week,projection from pac_v2_projections where resourceType = '" + targetType
                + "' AND year = '" + year + "' AND quarter = '" + quarter + "' ";
        return rdsRepository.getDataFromPacman(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getTotalAssetCountByTargetType(java.lang.String)
     */
    @Override
    public Long getTotalAssetCountByTargetType(String targetType) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put("latest", true);
        mustFilter.put(CommonUtils.convertAttributetoKeyword("inScope"), true);
        try {
            return esRepository.getTotalDocumentCountForIndexAndType("aws" + "_" + targetType, targetType, mustFilter,
                    null, null, null, mustTermsFilter);
        } catch (Exception e) {
            logger.error(e);
            throw new DataException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getListOfLastWeekDateOfQuarter()
     */
    public List<LocalDate> getListOfLastWeekDateOfQuarter() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.of(today.getYear(), today.getMonth().firstMonthOfQuarter(), 1);
        Month firstMonthOftheQuarter = startDate.getMonth();
        Month secMonthOftheQuarter = startDate.getMonth().plus(1);
        Month thirdMonthOftheQuarter = secMonthOftheQuarter.plus(1);
        List<Month> monthList = new ArrayList<>();
        monthList.add(firstMonthOftheQuarter);
        monthList.add(secMonthOftheQuarter);
        monthList.add(thirdMonthOftheQuarter);
        int year = today.getYear();
        List<LocalDate> lastWeeksOfQuarterList = new ArrayList<>();
        for (Month month : monthList) {
            IntStream.rangeClosed(1, YearMonth.of(year, month).lengthOfMonth())
                    .mapToObj(day -> LocalDate.of(year, month, day))
                    .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                    .forEach(date -> lastWeeksOfQuarterList.add(date));
        }
        return lastWeeksOfQuarterList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * updateCurrentWeekDate(java.time.LocalDate, java.util.List)
     */
    private List<LocalDate> updateCurrentWeekDate(LocalDate today, List<LocalDate> lastWeeksOfQuarterList) {
        List<LocalDate> weeksDetailsByDate = new ArrayList<>();
        for (LocalDate lastdayofweek : lastWeeksOfQuarterList) {

            if (getWeekNoByDate(lastdayofweek.minusDays(1)) == getWeekNoByDate(today.minusDays(1))) {
                weeksDetailsByDate.add(today);
            } else {
                weeksDetailsByDate.add(lastdayofweek);
            }
        }
        return weeksDetailsByDate;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getWeekNoByDate(java.time.LocalDate)
     */
    public int getWeekNoByDate(LocalDate date) {
        LocalDate lastDayInPreviousQuarter = null;
        int quarter = date.get(IsoFields.QUARTER_OF_YEAR);
        int weekNumber;
        if (quarter > 1) {
            LocalDate previousQuarter = LocalDate.now().minus(1, IsoFields.QUARTER_YEARS);
            // get last day in previous quarter
            long lastDayOfQuarter = IsoFields.DAY_OF_QUARTER.rangeRefinedBy(previousQuarter).getMaximum();
            // get the date corresponding to the last day of quarter
            lastDayInPreviousQuarter = previousQuarter.with(IsoFields.DAY_OF_QUARTER, lastDayOfQuarter);
        }
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        if (null != lastDayInPreviousQuarter) {
            weekNumber = ((date.get(weekFields.weekOfWeekBasedYear())) - (lastDayInPreviousQuarter.get(weekFields
                    .weekOfWeekBasedYear())));
        } else {
            weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        }
        return weekNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getPatchingSnapshot(java.lang.String)
     */
    @Override
    public Map<Integer, Long> getPatchingSnapshot(String assetGroup) throws DataException {
        String responseJson = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        Map<Integer, Long> patchedInstancesMap = new HashMap<>();
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = updateCurrentWeekDate(today, getListOfLastWeekDateOfQuarter());
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append("assetgroup_stats").append("/")
                .append("patching").append("/").append("_search").append("?").append("size").append("=")
                .append(ES_PAGE_SIZE);
        StringBuilder requestBody = new StringBuilder(
                "{\"sort\":[{\"date\":{\"order\":\"asc\"}}],\"query\":{\"bool\":{\"must\":[{\"terms\":{\"date\":[");
        for (LocalDate date : dates) {
            requestBody.append("\"" + date + "\",");
        }
        requestBody.deleteCharAt(requestBody.lastIndexOf(","));
        requestBody.append("]}},{\"match\":{\"ag.keyword\":\"" + assetGroup + "\"}}]}}}");

        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
        } catch (Exception e) {
            logger.error(e);
            throw new DataException(e);
        }
        jsonParser = new JsonParser();
        resultJson = (JsonObject) jsonParser.parse(responseJson);

        if (resultJson != null) {
            patchedInstancesMap = processJsonData(resultJson);
           
        }
        return patchedInstancesMap;
    }

    private Map<Integer, Long> processJsonData(JsonObject resultJson) {
        Map<Integer, Long> patchedInstancesMap = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        LocalDate today = LocalDate.now();
        int quarter = today.get(IsoFields.QUARTER_OF_YEAR);
        JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get("hits").toString());
        JsonArray hitsArray = hitsJson.getAsJsonArray("hits");
        DateTimeFormatter formatter;
        Boolean isPreviousQuarter = false;
        for (int i = 0; i < hitsArray.size(); i++) {
            LocalDate lastdayofWeekMinusDay = null;
            JsonObject source = hitsArray.get(i).getAsJsonObject().get("_source").getAsJsonObject();
            if (!source.get("patched_instances").isJsonNull() && !source.get("date").isJsonNull()) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                formatter = formatter.withLocale(Locale.US);
                LocalDate date = LocalDate.parse(source.get("date").getAsString(), formatter);
                lastdayofWeekMinusDay = date;
                if (!date.equals(LocalDate.now())) {
                    lastdayofWeekMinusDay = date.minusDays(1);

                    int lastdayofWeekQuarter = lastdayofWeekMinusDay.get(IsoFields.QUARTER_OF_YEAR);
                    if (!isPreviousQuarter && lastdayofWeekQuarter != quarter) {

                        lastdayofWeekMinusDay = date;
                        isPreviousQuarter = true;
                    }
                }
                patchedInstancesMap.put(getWeekNoByDate(lastdayofWeekMinusDay), source.get("patched_instances")
                        .getAsLong());
            }
        }
        return patchedInstancesMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getUnPatchedDetailsByApplication(java.lang.String, java.lang.String)
     */
    public Map<String, Long> getUnPatchedDetailsByApplication(String assetGroup, String resourceType)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword(TYPE), ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(ISSUE_STATUS), OPEN);
        if (resourceType.equals(ONPREMSERVER)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), ONPREM_KERNEL_COMPLIANCE_RULE);
        } else if (resourceType.equals(EC2)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(RULEID), EC2_KERNEL_COMPLIANCE_RULE);
        }

        String aggsFilterApp = CommonUtils.convertAttributetoKeyword(TAGS_APPLICATION);

        try {
            return esRepository.getTotalDistributionForIndexAndType(assetGroup, "issue_" + resourceType, mustFilter,
                    null, null, aggsFilterApp, TEN_THOUSAND, null);
        } catch (Exception e) {
            logger.error(e);
            throw new DataException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getAssetDetailsByApplication(java.lang.String, java.lang.String)
     */
    public Map<String, Long> getAssetDetailsByApplication(String assetGroup, String resourceType) throws DataException {

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(LATEST, true);

        if (resourceType.equals(ONPREMSERVER)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword(INSCOPE), true);
        }

        String aggsFilterApp = CommonUtils.convertAttributetoKeyword(TAGS_APPLICATION);

        try {

            return esRepository.getTotalDistributionForIndexAndType(assetGroup, resourceType, mustFilter, null, null,
                    aggsFilterApp, TEN_THOUSAND, null);
        } catch (Exception e) {
            logger.error(e);
            throw new DataException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getAssetCountByAg(java.lang.String, java.lang.String)
     */
    public Long getAssetCountByAg(String assetGroup, String resourceType) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(LATEST, true);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(INSCOPE), true);
        try {
            return esRepository.getTotalDocumentCountForIndexAndType(assetGroup, resourceType, mustFilter, null, null,
                    null, null);
        } catch (Exception e) {
            logger.error(e);
            throw new DataException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.repository.ProjectionRepository#
     * getAppsDetails(java.lang.String)
     */
    @SuppressWarnings("deprecation")
    public List<Map<String, Object>> getAppsDetails(String appType) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("_appType.keyword", appType);
        mustFilter.put("latest", true);
        ArrayList<String> fields = new ArrayList<>();
        fields.add("appTag");
        fields.add("director");
        fields.add("executiveSponsor");
        try {
            return esRepository.getSortedDataFromESBySize("aws_apps", "apps", mustFilter, null, null, fields, 0,
                    TEN_THOUSAND, null, null, null);
        } catch (Exception e) {
            logger.error(e);
            throw new DataException(e);
        }
    }

}
