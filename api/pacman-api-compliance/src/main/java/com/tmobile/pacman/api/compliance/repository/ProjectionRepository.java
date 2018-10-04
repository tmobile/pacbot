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
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;

// TODO: Auto-generated Javadoc
/**
 * The Interface ProjectionRepository.
 */
@Repository
public interface ProjectionRepository {

    /**
     * Update projection by target type.
     *
     * @param projectionRequest the projection request
     * @return the boolean
     * @throws DataException the data exception
     */
    public Boolean updateProjectionByTargetType(
            ProjectionRequest projectionRequest)throws DataException;

    /**
     * If method receives targetType,year and
     *         quarter, it gives the week and projection number from the DB.
     *
     * @param targetType the target type
     * @param year the year
     * @param quarter the quarter
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getProjectionDetailsFromDb(
            String targetType, int year, int quarter) throws DataException;

    /**
     * Gets the total asset count by target type.
     *
     * @param targetType the target type
     * @return Long
     * @throws DataException the data exception
     */
    public Long getTotalAssetCountByTargetType(String targetType)
            throws DataException;

    /**
     * Gets the patching snapshot based on week of that quarter.
     *
     * @param assetGroup the asset group
     * @return Map<Integer, Long>
     * @throws DataException the data exception
     */
    public Map<Integer, Long> getPatchingSnapshot(String assetGroup)
            throws DataException;

    /**
     * Gets the list of last week date of quarter.
     *
     * @return List<LocalDate>
     */
    public List<LocalDate> getListOfLastWeekDateOfQuarter();


    /**
     * Gets the week no by date.
     *
     * @param date the date
     * @return int
     */
    public int getWeekNoByDate(LocalDate date);

    /**
     * Gets the asset details by application.
     *
     * @param assetGroup the asset group
     * @param resourceType the resource type
     * @return Map<String, Long>
     * @throws DataException the data exception
     */
    public Map<String, Long> getAssetDetailsByApplication(String assetGroup,
            String resourceType) throws DataException;

    /**
     * Gets the un patched details by application.
     *
     * @param assetGroup the asset group
     * @param resourceType the resource type
     * @return Map<String, Long>
     * @throws DataException the data exception
     */
    public Map<String, Long> getUnPatchedDetailsByApplication(
            String assetGroup, String resourceType) throws DataException;

    /**
     * Gets the asset count by ag.
     *
     * @param assetGroup the asset group
     * @param resourceType the resource type
     * @return Long
     * @throws DataException the data exception
     */
    public Long getAssetCountByAg(String assetGroup, String resourceType)
            throws DataException;

    /**
     * Gets the applications details.
     *
     * @param appType the app type
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getAppsDetails(String appType)
            throws DataException;
}
