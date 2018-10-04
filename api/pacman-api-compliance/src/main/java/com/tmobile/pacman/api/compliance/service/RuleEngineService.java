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

import java.util.Map;

import com.tmobile.pacman.api.commons.exception.ServiceException;

/**
 * The Interface RuleEngineService.
 */
public interface RuleEngineService {

    /**
     * Run rule.
     *
     * @param ruleId the rule id
     * @param runTimeParams the run time params
     * @throws ServiceException the service exception
     */
    public void runRule(String ruleId, Map<String, String> runTimeParams)
            throws ServiceException;

    /**
     * Gets the last action.
     *
     * @param resourceId the resource id
     * @return the last action
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getLastAction(final String resourceId)
            throws ServiceException;

    /**
     * Post action.
     *
     * @param resourceId the resource id
     * @param action the action
     * @throws ServiceException the service exception
     */
    public void postAction(final String resourceId, final String action)
            throws ServiceException;
}
