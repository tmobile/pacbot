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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :santoshi
  Modified Date: Dec 4, 2017

 **/
package com.tmobile.pacman.api.compliance.repository;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;

/**
 * The Interface TaggingRepository.
 */
public interface TaggingRepository {

    /**
     * Gets the untagged issues by application from ES.
     *
     * @param assetGroup the asset group
     * @param mandatoryTags the mandatory tags
     * @param searchText the search text
     * @param from the from
     * @param size the size
     * @return JsonArray
     * @throws DataException the data exception
     */
    public JsonArray getUntaggedIssuesByapplicationFromES(String assetGroup,
            String mandatoryTags, String searchText, int from, int size)
            throws DataException;

    /**
     * Gets the rule params from DB by policy id.
     *
     * @param policyId the policy id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleParamsFromDbByPolicyId(
            String policyId) throws DataException;

    /**
     * Gets the untagged issues.
     *
     * @param assetGroup the asset group
     * @param mandatoryTags the mandatory tags
     * @return Long
     * @throws DataException the data exception
     */
    public Long getUntaggedIssues(String assetGroup, String mandatoryTags)
            throws DataException;

    /**
     * Gets the rule target types from db by policy id.
     *
     * @param policyId the policy id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleTargetTypesFromDbByPolicyId(
            String policyId) throws DataException;

    /**
     * Gets the un tagged target type issues.
     *
     * @param request the request
     * @param tagsList the tags list
     * @return String
     * @throws DataException the data exception
     */
    public String getUntaggedTargetTypeIssues(
            UntaggedTargetTypeRequest request, List<String> tagsList)
            throws DataException;

    /**
     * Gets the tagging by application.
     *
     * @param ag the ag
     * @param targetType the target type
     * @return String
     * @throws DataException the data exception
     */
    public String getTaggingByApplication(String ag, String targetType)
            throws DataException;
}
