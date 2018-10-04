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
import com.tmobile.pacman.api.compliance.domain.Medal;

/**
 * Service layer for all patching related calls.
 */
public interface PatchingService {

    /**
     * Gets the top non-compliant applications along with director details for
     * AG.This method is applicable for ec2,onprem server targettypes.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getNonCompliantNumberForAG(
            String assetGroup) throws ServiceException;

    /**
     * Gets the top non-compliant applications along with executive sponsers
     * details for AG.This method is applicable for ec2,onpremserver
     * targettypes.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getNonCompliantExecsForAG(String assetGroup)
            throws ServiceException;

    /**
     * Gets the patching details.This method is applicable for ec2,onprem server
     * targettypes.
     *
     * @param assetGroup the asset group
     * @param filter the filter
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getPatchingDetails(String assetGroup,
            Map<String, String> filter) throws ServiceException;

    /**
     * Gets the ongoing year.
     *
     * @return int
     */
    int getOngoingYear();

    /**
     * Gets the ongoing quarter.
     *
     * @return int
     */
    int getOngoingQuarter();

    /**
     * Gets the first day of quarter.
     *
     * @param quarterNumber the quarter number
     * @param year the year
     * @return LocalDate
     */
    LocalDate getFirstDayOfQuarter(int quarterNumber, int year);

    /**
     * Gets the last day of quarter.
     *
     * @param quarterNumber the quarter number
     * @param year the year
     * @return LocalDate
     */
    LocalDate getLastDayOfQuarter(int quarterNumber, int year);

    /**
     * Gets the patching progress.
     *
     * @param assetGroup the asset group
     * @param year the year
     * @param quarter the quarter
     * @return Map<String, Object>
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getPatchingProgress(String assetGroup, int year,
            int quarter) throws ServiceException;

    /**
     * Gets the quarters with patching data.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getQuartersWithPatchingData(
            String assetGroup) throws ServiceException;

    /**
     * Filter matching collection elements.
     *
     * @param masterDetailList the master detail list
     * @param searchText the search text
     * @param b the b
     * @return Object
     * @throws ServiceException the service exception
     */
    public Object filterMatchingCollectionElements(
            List<Map<String, Object>> masterDetailList, String searchText,
            boolean b) throws ServiceException;

    /**
     * Get rating for patching performance of an ag
     * 
     * @param ag asset group
     * @param quarter quarter
     * @param year year
     * @return Medal object
     */
	public Medal getStarRatingForAgPatching(String ag, int quarter, int year);

}
