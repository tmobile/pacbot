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

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.PolicyScanInfo;
import com.tmobile.pacman.api.compliance.domain.PolicyVialationSummary;
/**
 * The Interface PolicyAssetService.
 */
public interface PolicyAssetService {

    /**
     * Gets the policy execution details.
     *
     * @param ag the ag
     * @param resourceType the resource type
     * @param resourceId the resource id
     * @return the policy execution details
     */
    public List<PolicyScanInfo> getPolicyExecutionDetails(String ag,
            String resourceType, String resourceId) throws ServiceException;

    /**
     * Gets the policy violation summary.
     *
     * @param ag the ag
     * @param resourceType the resource type
     * @param resourceId the resource id
     * @return the policy violation summary
     */
    public PolicyVialationSummary getPolicyViolationSummary(String ag,
            String resourceType, String resourceId) throws ServiceException;

}
