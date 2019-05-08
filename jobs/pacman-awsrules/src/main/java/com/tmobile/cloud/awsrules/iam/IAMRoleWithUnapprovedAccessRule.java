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
  Author :John, Kanchana, Pavan
  Modified Date: January 27, 2019
**/
package com.tmobile.cloud.awsrules.iam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * The Class IAMRoleWithUnapprovedAccessRule.
 */
@PacmanRule(key = "iam-role-with-unapproved-access", desc = "Checks if any iam role has unapproved access to actions and creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class IAMRoleWithUnapprovedAccessRule extends BaseRule {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory.getLogger(IAMRoleWithUnapprovedAccessRule.class);
	private static final String ROLE_NAME = "rolename";

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 *            ************* Following are the Rule Parameters********* <br>
	 *            <br>
	 * 
	 *            ruleKey : iam-role-with-unapproved-access <br>
	 *            <br>
	 * 
	 *            iamPriviliges : Enter the comma separated privileges for which you
	 *            want to create issues<br>
	 *            </br>
	 * 
	 *            splitterChar : The splitter character used to split the
	 *            iamPriviliges roleIdentifyingString : Configure it as role/pac_ro
	 *            <br>
	 *            <br>
	 *            
	 *            roleIdentifyingString : Configure it as role/pacbot_ro <br><br>
	 * 
	 * @param resourceAttributes
	 *            this is a resource in context which needs to be scanned this is
	 *            provided by execution engine
	 *
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.commons.rule.Rule#execute(java.util.Map,
	 * java.util.Map)
	 */
	@Override
	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========IAMRoleWithUnapprovedAccessRule started=========");
		Map<String, String> ruleParamIam = new HashMap<>();
		ruleParamIam.putAll(ruleParam);
		ruleParamIam.put(PacmanSdkConstants.REGION, Regions.DEFAULT_REGION.getName());

		Map<String, Object> map = null;
		Annotation annotation = null;
		AmazonIdentityManagementClient identityManagementClient = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String roleName = resourceAttributes.get(ROLE_NAME);
		String unapprovedActionsParam = ruleParam.get(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS);
		String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put(PacmanSdkConstants.EXECUTION_ID, ruleParam.get(PacmanSdkConstants.EXECUTION_ID));
		MDC.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));

		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		if (!PacmanUtils.doesAllHaveValue(severity, category, roleIdentifyingString, unapprovedActionsParam,
				tagsSplitter)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		try {
			map = getClientFor(AWSService.IAM, roleIdentifyingString, ruleParamIam);

			identityManagementClient = (AmazonIdentityManagementClient) map.get(PacmanSdkConstants.CLIENT);
			List<String> unApprovedActionList = PacmanUtils.splitStringToAList(unapprovedActionsParam, tagsSplitter);

			Set<String> allowedActionSet = IAMUtils.getAllowedActionsByRolePolicy(identityManagementClient, roleName);
			List<String> unapprovedAttachedAndInlineActionList = new ArrayList<>();

			if (!allowedActionSet.isEmpty()) {
				for (String unapprovedAction : unApprovedActionList) {
					if (allowedActionSet.contains(unapprovedAction)) {
						unapprovedAttachedAndInlineActionList.add(unapprovedAction);
					}
				}
				if (!unapprovedAttachedAndInlineActionList.isEmpty()) {
					annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,
							"Unapproved IAM role has " + unapprovedAttachedAndInlineActionList);
					issue.put(PacmanRuleConstants.VIOLATION_REASON,
							"Unapproved IAM role has " + unapprovedAttachedAndInlineActionList);
					issue.put("privileges", unapprovedActionsParam);
					annotation.put(ROLE_NAME, roleName);
					issueList.add(issue);
					annotation.put("issueDetails", issueList.toString());

					logger.debug("========IAMRoleWithUnapprovedAccessRule ended with annotation {} :=========",
							annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
							annotation);
				}
			}

		} catch (Exception e) {
			logger.error(PacmanRuleConstants.UNABLE_TO_GET_CLIENT, e);
			throw new InvalidInputException(PacmanRuleConstants.UNABLE_TO_GET_CLIENT, e);
		}
		logger.debug("========IAMRoleWithUnapprovedAccessRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "Checks if any iam role has unapproved access to actions and creates an issue";
	}

}
