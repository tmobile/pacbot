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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * The Interface TrendRepository.
 */
public interface TrendRepository {

    /**
     * Gets the compliance trend progress.
     *
     * @param assetGroup the asset group
     * @param fromDate the from date
     * @param domain the domain
     * @param ruleSev the rule sev
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    List<Map<String, Object>> getComplianceTrendProgress(String assetGroup,
            LocalDate fromDate, String domain, Set<String> ruleSev)
            throws DataException;

    /**
     * Gets the trend progress.
     *
     * @param assetGroup the asset group
     * @param ruleId the rule id
     * @param startDate the start date
     * @param endDate the end date
     * @param trendCategory the trend category
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    List<Map<String, Object>> getTrendProgress(String assetGroup,
            String ruleId, LocalDate startDate, LocalDate endDate,
            String trendCategory) throws DataException;

    /**
     * Gets the trend issues.
     *
     * @param assetGroup the asset group
     * @param startDate the start date
     * @param endDate the end date
     * @param filter the filter
     * @param ruleSev the rule sev
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    List<Map<String, Object>> getTrendIssues(String assetGroup,
            LocalDate startDate, LocalDate endDate, Map<String, String> filter,
            Set<String> ruleSev) throws DataException;

}
