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

package com.tmobile.pacman.commons.autofix;

import java.util.Map;
import java.util.concurrent.Callable;

import com.tmobile.pacman.dto.AutoFixTransaction;

// TODO: Auto-generated Javadoc
/**
 * The Interface AutoFix.
 */
public interface AutoFix extends Callable<FixResult> {

    /**
     * Execute fix.
     *
     * @param issue the issue
     * @param clientMap the client map
     * @param ruleParams the rule params
     * @return the fix result
     * @throws Exception the exception
     */
    public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
            Map<String, String> ruleParams) throws Exception;

    /**
     * Backup existing config for resource.
     *
     * @param resourceId the resource id
     * @param resourceType the resource type
     * @param clientMap the client map
     * @param ruleParams the rule params
     * @param issue the issue
     * @return true, if successful
     * @throws Exception the exception
     */
    public boolean backupExistingConfigForResource(String resourceId, String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws Exception;


    /**
     * determines if resourceId is a fix candidate.
     *
     * @param resourceId the resource id
     * @param resourceType the resource type
     * @param clientMap the client map
     * @param ruleParams the rule params
     * @param issue the issue
     * @return true, if is fix candidate
     * @throws Exception the exception
     */
    public boolean isFixCandidate(String resourceId, String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws Exception;



    /**
     *
     * @return
     */
    public AutoFixTransaction addDetailsToTransactionLog(Map<String, String> annotation);

}
