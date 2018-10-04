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
/*
 * 
 */
package com.tmobile.pacman.api.statistics.service;

import java.util.List;
import java.util.Map;

import com.tmobile.pacman.api.commons.exception.ServiceException;


/**
 * The Interface StatisticsService.
 */
public interface StatisticsService {

    /**
     * Gets the CPU utilization.
     *
     * @param assetGroup the asset group
     * @return the CPU utilization
     * @throws ServiceException the service exception
     */

    public List<Map<String, Object>> getCPUUtilization(String assetGroup) throws ServiceException;

    /**
     * Gets the network utilization.
     *
     * @param assetGroup the asset group
     * @return the network utilization
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getNetworkUtilization(String assetGroup) throws ServiceException;

    /**
     * Gets the disk utilization.
     *
     * @param assetGroup the asset group
     * @return the disk utilization
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getDiskUtilization(String assetGroup) throws ServiceException;

    /**
     * Gets the stats.
     *
     * @return the stats
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getStats() throws Exception;

    /**
     * Gets the autofix stats.
     *
     * @return the autofix stats
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getAutofixStats() throws ServiceException;

}
