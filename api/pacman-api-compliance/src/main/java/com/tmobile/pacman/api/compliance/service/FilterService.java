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

import java.util.List;
import java.util.Map;

import com.tmobile.pacman.api.commons.exception.ServiceException;

/**
 * The Interface FilterService.
 */
public interface FilterService {

    /**
     * Gets the details of that filterId and the domain which configured
     *              in Db
     *
     * @param filterId the filter id
     * @param domain the domain
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getFilters(int filterId, String domain)
            throws ServiceException;
    
    /**
     * Gets the policyId and name     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getPolicies(String assetGroup,
            String domain) throws ServiceException;

    /**
     * Gets the regions.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getRegions(String assetGroup)
            throws ServiceException;

    /**
     * Gets the accounts.
     *
     * @param assetGroup the asset group
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getAccounts(String assetGroup)
            throws ServiceException;

    /**
     * Gets the rules.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getRules(String assetGroup, String domain)
            throws ServiceException;
    
    /**
     * Gets the applications.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getApplications(String assetGroup,
            String domain) throws ServiceException;

    /**
     * Gets the environments by asset group.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param domain the domain
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getEnvironmentsByAssetGroup(
            String assetGroup, String application, String domain)
            throws ServiceException;

    /**
     * Gets the target types for asset group.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getTargetTypesForAssetGroup(
            String assetGroup, String domain) throws ServiceException;

}
