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
package com.tmobile.pacman.api.statistics.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.tmobile.pacman.api.commons.exception.DataException;


// TODO: Auto-generated Javadoc
/**
 * The Interface StatisticsRepository.
 */
@Repository
public interface StatisticsRepository {

    /**
     * Gets the target type for AG.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return the target type for AG
     */
    public String getTargetTypeForAG(String assetGroup, String domain);

    /**
     * Gets the rule id with target type query.
     *
     * @param targetTypes the target types
     * @return the rule id with target type query
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleIdWithTargetTypeQuery(String targetTypes) throws DataException;

    /**
     * Gets the number of accounts.
     *
     * @return the number of accounts
     * @throws DataException the data exception
     */
    public JsonArray getNumberOfAccounts() throws DataException;

    /**
     * Gets the number of policies evaluated.
     *
     * @return the number of policies evaluated
     * @throws DataException the data exception
     */
    public String getNumberOfPoliciesEvaluated() throws DataException;

    /**
     * Gets the total violations.
     *
     * @return the total violations
     * @throws DataException the data exception
     */
    public JsonArray getTotalViolations() throws DataException;

    /**
     * Gets the rules with auto fix enabled from Database.
     * @author santoshi
     * @return the autofix rules from db
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getAutofixRulesFromDb() throws DataException;

    /**
     * Gets the autofix action count by rule.
     *
     * @return the autofix action count by rule
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getAutofixActionCountByRule() throws DataException;
}
