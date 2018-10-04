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
import java.util.List;
import java.util.Map;

import com.tmobile.pacman.api.commons.exception.ServiceException;
/**
 * The Interface IssueTrendService.
 */
public interface IssueTrendService {

    /**
     * Gets the trend for issues.
     *
     * @param assetGroup the asset group
     * @param fromDate the from date
     * @param toDate the to date
     * @param severity the severity
     * @param ruleId the rule id
     * @param policyId the policy id
     * @param app the app
     * @param env the env
     * @return the trend for issues
     * @throws ServiceException the service exception
     */
    public Map<String, Long> getTrendForIssues(String assetGroup,
            String fromDate, String toDate, String severity, String ruleId,
            String policyId, String app, String env) throws ServiceException;

    /**
     * Gets the compliance trend progress.
     *
     * @param assetGroup the asset group
     * @param fromDate the from date
     * @param domain the domain
     * @return the compliance trend progress
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getComplianceTrendProgress(String assetGroup,
            LocalDate fromDate, String domain) throws ServiceException;

    /**
     * Gets the trend progress.
     *
     * @param assetGroup the asset group
     * @param ruleId the rule id
     * @param startDate the start date
     * @param endDate the end date
     * @param ruleCategory the rule category
     * @return the trend progress
     * @throws ServiceException the service exception
     */
    Map<String, Object> getTrendProgress(String assetGroup, String ruleId,
            LocalDate startDate, LocalDate endDate, String ruleCategory)
            throws ServiceException;

    /**
     * Gets the trend issues.
     *
     * @param assetGroup the asset group
     * @param from the from
     * @param to the to
     * @param filter the filter
     * @param domain the domain
     * @return the trend issues
     * @throws ServiceException the service exception
     */
    Map<String, Object> getTrendIssues(String assetGroup, LocalDate from,
            LocalDate to, Map<String, String> filter, String domain)
            throws ServiceException;

    /**
     * Use real time data for latest date.
     *
     * @param trendList the trend list
     * @param ag the ag
     * @param trendCategory the trend category
     * @param ruleId the rule id
     * @param domain the domain
     * @throws ServiceException the service exception
     */
    void useRealTimeDataForLatestDate(List<Map<String, Object>> trendList,
            String ag, String trendCategory, String ruleId, String domain)
            throws ServiceException;

}
