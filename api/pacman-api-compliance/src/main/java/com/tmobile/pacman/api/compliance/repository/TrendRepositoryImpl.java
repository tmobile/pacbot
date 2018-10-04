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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;

/**
 * The Class TrendRepositoryImpl.
 *
 * @author kkumar
 */
@Repository
public class TrendRepositoryImpl implements TrendRepository, Constants {

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TrendRepository#getComplianceTrendProgress(java.lang.String, java.time.LocalDate, java.lang.String, java.util.Set)
     */
    @Override
    public List<Map<String, Object>> getComplianceTrendProgress(
            String assetGroup, LocalDate fromDate, String domain,
            Set<String> ruleCat) throws DataException {
        List<String> categoryList = new ArrayList<>(ruleCat);
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("ag"), assetGroup);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(DOMAIN), domain);
        Map<String, Object> rangeMap = new HashMap<>();
        rangeMap.put("gte", fromDate.format(DateTimeFormatter.ISO_DATE));

        Map<String, Object> dateRangeMap = new HashMap<>();
        dateRangeMap.put("date", rangeMap);

        mustFilter.put(RANGE, dateRangeMap);
        categoryList.add("date");
        categoryList.add("overall");
try{
        return elasticSearchRepository.getSortedDataFromES(AG_STATS, "compliance",
                mustFilter, null, null, categoryList, null,null);
}catch(Exception e){
    throw new DataException(e);
}
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TrendRepository#getTrendProgress(java.lang.String, java.lang.String, java.time.LocalDate, java.time.LocalDate, java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getTrendProgress(String assetGroup,
            String ruleId, LocalDate startDate, LocalDate endDate,
            String trendCategory) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("ag"), assetGroup);
        if ("issuecompliance".equals(trendCategory)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword("ruleId"),
                    ruleId);
        }

        Map<String, Object> rangeMap = new HashMap<>();
        rangeMap.put("gte", startDate.format(DateTimeFormatter.ISO_DATE));
        rangeMap.put("lte", endDate.format(DateTimeFormatter.ISO_DATE));

        Map<String, Object> dateRangeMap = new HashMap<>();
        dateRangeMap.put("date", rangeMap);

        mustFilter.put(RANGE, dateRangeMap);
try{
        return elasticSearchRepository.getSortedDataFromES(AG_STATS, trendCategory,
                mustFilter, null, null, Arrays.asList("date", "total",
                        "compliant", "noncompliant", "compliance_percent"),
                null,null);
}catch(Exception e){
    throw new DataException(e);
}
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.TrendRepository#getTrendIssues(java.lang.String, java.time.LocalDate, java.time.LocalDate, java.util.Map, java.util.Set)
     */
    @Override
    public List<Map<String, Object>> getTrendIssues(String assetGroup,
            LocalDate startDate, LocalDate endDate, Map<String, String> filter,
            Set<String> ruleSev) throws DataException {
        String domain = filter.get(DOMAIN);
        List<String> severityList = new ArrayList<>(ruleSev);
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("ag"), assetGroup);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(DOMAIN), domain);
        Map<String, Object> rangeMap = new HashMap<>();
        rangeMap.put("gte", startDate.format(DateTimeFormatter.ISO_DATE));
        rangeMap.put("lte", endDate.format(DateTimeFormatter.ISO_DATE));

        Map<String, Object> dateRangeMap = new HashMap<>();
        dateRangeMap.put("date", rangeMap);

        mustFilter.put(RANGE, dateRangeMap);
        severityList.add("date");
        severityList.add("total");
try{
        return elasticSearchRepository.getSortedDataFromES(AG_STATS, "issues",
                mustFilter, null, null, severityList, null,null);
}catch(Exception e){
    throw new DataException(e);
}
    }

}
