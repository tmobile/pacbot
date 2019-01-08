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
package com.tmobile.pacman.api.admin.repository.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.tmobile.pacman.api.admin.domain.CreateUpdateRuleDetails;
import com.tmobile.pacman.api.admin.domain.RuleProjection;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.Rule;
import com.tmobile.pacman.api.admin.repository.model.RuleCategory;

/**
 * Rule Service Functionalities
 */
public interface RuleService {

	/**
     * Service to get rule by rule Id
     *
     * @author Nidhish
     * @param ruleId - valid rule Id
     * @return The rule details
     */
	public Rule getByRuleId(String ruleId);

	/**
     * Service to get all alexaKeywords
     *
     * @author Nidhish
     * @return List of alexaKeywords
     */
	public Collection<String> getAllAlexaKeywords();

	/**
     * Service to get all rules
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return All Rules details
     */
	public Page<Rule> getRules(final String searchTerm, final int page, final int size);

	/**
     * Service to create new rule
     *
     * @author Nidhish
     * @param fileToUpload - valid executable rule jar file
     * @param ruleDetails - details for creating new rule
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String createRule(final MultipartFile fileToUpload, final CreateUpdateRuleDetails ruleDetails, final String userId) throws PacManException;

	/**
     * Service to update existing rule
     *
     * @author Nidhish
     * @param fileToUpload - valid executable rule jar file
     * @param updateRuleDetails - details for creating new rule
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateRule(final MultipartFile fileToUpload, final CreateUpdateRuleDetails updateRuleDetails, final String userId) throws PacManException;

	/**
     * Service to invoke a rule
     *
     * @author Nidhish
     * @param ruleId - valid rule Id
     * @param ruleOptionalParams - valid rule optional parameters which need to be passed while invoking rule
     * @return Success or Failure response
     */
	public String invokeRule(final String ruleId, final List<Map<String, Object>> ruleOptionalParams);

	/**
     * Service to enable disable rule
     *
     * @author Nidhish
     * @param ruleId - valid rule Id
     * @param action - valid action (disable/ enable)
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String enableDisableRule(final String ruleId, final String action, final String userId) throws PacManException;

	/**
     * Service to get all rules by targetType
     *
     * @author Nidhish
     * @param targetType - valid targetType
     * @return List of all rules
     */
	public List<Rule> getAllRulesByTargetType(final String targetType);

	/**
     * Service to get all rules by targetType and not in ruleId list
     *
     * @author Nidhish
     * @param targetType - valid targetType
     * @param ruleIdList - valid rule Id list
     * @return List of rules details
     */
	public List<RuleProjection> getAllRulesByTargetTypeAndNotInRuleIdList(final String targetType, final List<String> ruleIdList);

	/**
     * Service to get all rules by targetType and ruleId list
     *
     * @author Nidhish
     * @param targetType - valid targetType
     * @param ruleIdList - valid rule Id list
     * @return List of rules details
     */
	public List<RuleProjection> getAllRulesByTargetTypeAndRuleIdList(final String targetType, final List<String> ruleIdList);

	/**
     * Service to get all rules by targetType name
     *
     * @author Nidhish
     * @param targetType - valid targetType
     * @return List of rules details
     */
	public List<RuleProjection> getAllRulesByTargetTypeName(String targetType);

	/**
     * Service to invoke all rules
     *
     * @author Nidhish
     * @param ruleIds - valid rule id list
     * @return Success and failure rule id details
     */
	public Map<String, Object> invokeAllRules(List<String> ruleIds);

	/**
     * Service to get all Rule Id's
     *
     * @author Nidhish
     * @return List of Rule Id's
     */
	public Collection<String> getAllRuleIds();
	
	/**
	 * Gets the all rule categories.
	 *
	 * @return the all rule categories
	 */
	public List<RuleCategory> getAllRuleCategories() throws PacManException;
}
