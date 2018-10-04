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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;

/**
 * The Interface FilterRepository.
 */
@Repository
public interface FilterRepository {

    /**
     * Gets the filters from db where the given
     *         filterId is should be configured already in DB.
     *
     * @param filterId the filter id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getFiltersFromDb(int filterId)
            throws DataException;

    /**
     * Gets the policies from DB. If method receives comma separated or single
     *         targetType, it gives the policyId/policyName from the DB
     *
     * @param tartgetType the tartget type
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getPoliciesFromDB(String tartgetType)
            throws DataException;

    /**
     * Gets all the policyId and its issue count from the ES.
     *
     * @param assetGroup the asset group
     * @return Map<String, Long>
     * @throws DataException the data exception
     */
    public Map<String, Long> getPoliciesFromES(String assetGroup)
            throws DataException;

    /**
     * Gets the list of all the accountname and its accountid from the ES.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getAccountsFromES(String assetGroup)
            throws DataException;

    /**
     * Gets the map of all the region and its count from the ES.
     *
     * @param assetGroup the asset group
     * @return Map<String, Long>.
     * @throws DataException the data exception
     */
    public Map<String, Long> getRegionsFromES(String assetGroup)
            throws DataException;

    /**
     * Gets the rules from ES.
     *
     * @param asseGroup the asse group
     * @return Map<String, Long>
     * @throws DataException the data exception
     */
    public Map<String, Long> getRulesFromES(String asseGroup) throws DataException;

    /**
     * Gets the list of applications.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return AssetCountDTO[]
     * @throws DataException the data exception
     */
    public AssetCountDTO[] getListOfApplications(String assetGroup,
            String domain) throws DataException;

    /**
     * Gets the list of environments.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param domain the domain
     * @return AssetCountDTO[]
     * @throws DataException the data exception
     */
    public AssetCountDTO[] getListOfEnvironments(String assetGroup,
            String application, String domain) throws DataException;

    /**
     * Gets the list of target types.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return AssetCountDTO[]
     * @throws DataException the data exception
     */
    public AssetCountDTO[] getListOfTargetTypes(String assetGroup, String domain)
            throws DataException;

}
