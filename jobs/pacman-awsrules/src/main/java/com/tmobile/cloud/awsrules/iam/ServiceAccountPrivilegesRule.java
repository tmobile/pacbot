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
  Author :Kanchana
  Modified Date: January 30, 2018
  
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
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * The Class ServiceAccountPrivilegesRule.
 */
@PacmanRule(key = "iam-serviceaccount-privileges-rule", desc = "Checks if any service account has certain privileges,if so creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class ServiceAccountPrivilegesRule extends BaseRule {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory.getLogger(ServiceAccountPrivilegesRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 *            ************* Following are the Rule Parameters********* <br>
	 *            <br>
	 * 
	 *            ruleKey : iam-serviceaccount-privileges-rule <br>
	 *            <br>
	 * 
	 *            unApprovedIamActions : Enter the comma separated privileges for
	 *            which you want to create issues<br>
	 *            </br>
	 * 
	 *            splitterChar : The splitter character used to split the
	 *            iamPriviliges
	 * 
	 *            roleIdentifyingString : Configure it as role/pacbot_ro <br>
	 *            <br>
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
		logger.debug("========ServiceAccountPrivilegesRule started=========");
		Map<String, String> ruleParamIam = new HashMap<>();
		ruleParamIam.putAll(ruleParam);
		ruleParamIam.put(PacmanRuleConstants.REGION, Regions.DEFAULT_REGION.getName());

		Map<String, Object> map = null;
		Annotation annotation = null;
		AmazonIdentityManagementClient identityManagementClient = null;

		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String userName = resourceAttributes.get(PacmanRuleConstants.IAM_USER_NAME);
		String unApprovedIamActions = ruleParam.get(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS);
		String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put(PacmanSdkConstants.EXECUTION_ID, ruleParam.get(PacmanSdkConstants.EXECUTION_ID));
		MDC.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));

		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

		List<String> unApprovedPrivileges = new ArrayList<>();

		if (!PacmanUtils.doesAllHaveValue(severity, category, roleIdentifyingString, unApprovedIamActions,
				tagsSplitter)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		try {
			map = getClientFor(AWSService.IAM, roleIdentifyingString, ruleParamIam);
			identityManagementClient = (AmazonIdentityManagementClient) map.get(PacmanSdkConstants.CLIENT);
			List<String> priviligesList = PacmanUtils.splitStringToAList(unApprovedIamActions, tagsSplitter);
			if (userName.startsWith(PacmanRuleConstants.SERVICE_ACCOUNTS)) {
				Set<String> actionSet = IAMUtils.getAllowedActionsByUserPolicy(identityManagementClient, userName);
				if (!actionSet.isEmpty()) {
					for (String privilege : priviligesList) {
						if (actionSet.contains(privilege)) {
							unApprovedPrivileges.add(privilege);
						}
					}
					if (!unApprovedPrivileges.isEmpty()) {
						annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
						annotation.put(PacmanSdkConstants.DESCRIPTION, "IAM service account has unapproved privileges");
						annotation.put(PacmanRuleConstants.SEVERITY, severity);
						annotation.put(PacmanRuleConstants.CATEGORY, category);
						annotation.put(PacmanRuleConstants.IAM_USER_NAME, userName);
						issue.put(PacmanRuleConstants.VIOLATION_REASON,
								"IAM service account has this action privileges");
						issue.put("unapprovedPrivileges", String.join(",", unApprovedPrivileges));
						issueList.add(issue);
						annotation.put("issueDetails", issueList.toString());

						logger.debug("========ServiceAccountPrivilegesRule ended with annotation {} :=========",
								annotation);
						return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
								annotation);
					}
				}
			} else {
				logger.info(userName, " : is not a service account");
			}

		} catch (UnableToCreateClientException e) {
			logger.error(PacmanRuleConstants.UNABLE_TO_GET_CLIENT, e);
			throw new InvalidInputException(PacmanRuleConstants.UNABLE_TO_GET_CLIENT, e);
		}
		logger.debug("========ServiceAccountPrivilegesRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "Checks if any service account has certain privileges,if so creates an issue";
	}

}
