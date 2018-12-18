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

import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * The Interface PolicyAssetRepository.
 */
public interface PolicyAssetRepository {

    /**
     * Fetch rule details.
     *
     * @param targetType the target type
     * @return the list
     */
    public List<Map<String, Object>> fetchRuleDetails(String targetType);

    /**
     * Fetch open issues.
     *
     * @param ag the ag
     * @param resourceType the resource type
     * @param resourceId the resource id
     * @return the list
     */
    public List<Map<String, Object>> fetchOpenIssues(String ag,
            String resourceType, String resourceId, boolean includeExempted) throws DataException;;

    /**
     * Fetch doc id.
     *
     * @param ag the ag
     * @param resourceType the resource type
     * @param resourceId the resource id
     * @return the string
     */
    public String fetchDocId(String ag, String resourceType, String resourceId);
}
