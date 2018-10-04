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

import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * The Interface PatchingRepository.
 */
@Repository
public interface PatchingRepository {

    /**
     * Gets the issue info.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Long>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getIssueInfo(String assetGroup)
            throws DataException;

    /**
     * Gets the closed issue info.
     *
     * @param assetGroup the asset group
     * @param size the size
     * @return List<Map<String, Long>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getClosedIssueInfo(String assetGroup,
            int size) throws DataException;

    /**
     * Gets the instance info.
     *
     * @param assetGroup the asset group
     * @param filters the filters
     * @return List<Map<String, Long>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getInstanceInfo(String assetGroup,
            Map<String, String> filters) throws DataException;

    /**
     * Gets the executive sponsors and director info.
     *
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getExecAndDirectorInfo() throws DataException;

    /**
     * Gets the instance info count.
     *
     * @param assetGroup the asset group
     * @param filter the filter
     * @param searchText the search text
     * @return long
     * @throws DataException the data exception
     */
    public long getInstanceInfoCount(String assetGroup,
            Map<String, String> filter, String searchText) throws DataException;

    /**
     * Gets the patching progress.
     *
     * @param assetGroup the asset group
     * @param startDate the start date
     * @param endDate the end date
     * @return List<Map<String, Object>> 
     * @throws DataException the data exception
     */
    List<Map<String, Object>> getPatchingProgress(String assetGroup,
            LocalDate startDate, LocalDate endDate) throws DataException;

    /**
     * Gets the amil avail date.
     *
     * @param year the year
     * @param quarter the quarter
     * @return String
     */
    public String getAmilAvailDate(int year, int quarter);

    /**
     * Gets the patching window.
     *
     * @return int
     */
    public int getPatchingWindow();

    /**
     * Gets the quarters with patching data.
     *
     * @param assetGroup the asset group
     * @return Map<String, Long> 
     * @throws DataException the data exception
     */
    Map<String, Long> getQuartersWithPatchingData(String assetGroup)
            throws DataException;

    /**
     * Gets the onprem issue info.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getOnpremIssueInfo(String assetGroup)
            throws DataException;

    /**
     * Gets the onprem resource info.
     *
     * @param assetGroup the asset group
     * @param filter the filter
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getOnpremResourceInfo(String assetGroup,
            Map<String, String> filter) throws DataException;

    /**
     * Gets the directors and executive sponsors for given application tag and application type.
     *
     * @param appTag the app tag
     * @param appType the app type
     * @return Map<String, Object>
     * @throws DataException the data exception
     */
    public Map<String, Object> getDirectorsAndExcutiveSponsers(String appTag,
            String appType) throws DataException;

    /**
     * Gets the non compliant number for ag and resource type.
     *
     * @param assetGroup the asset group
     * @param resourceType the resource type
     * @return Map<String, Long>
     * @throws DataException the data exception
     */
    public Map<String, Long> getNonCompliantNumberForAgAndResourceType(
            String assetGroup, String resourceType) throws DataException;

    /**
     * Adds the parent condition for onprem or ec2 patching.
     *
     * @param targetType the target type
     * @return Map<String, Object>
     */
    public Map<String, Object> addParentConditionPatching(String targetType);

    /**
     * Gets the patching percent for a given date range
     *
     * @param assetGroup the ag
     * @param fromDate start end inclusive
     * @param toDate end date inclusive
     * 
     * @return List<Map<String, Object>>
     */
	public List<Map<String, Object>> getPatchingPercentForDateRange(String assetGroup, LocalDate fromDate, LocalDate toDate);

}
