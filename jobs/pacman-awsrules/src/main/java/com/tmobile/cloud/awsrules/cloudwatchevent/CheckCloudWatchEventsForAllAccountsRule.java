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
 * 
 */

package com.tmobile.cloud.awsrules.cloudwatchevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClient;
import com.amazonaws.services.cloudwatchevents.model.ListRulesRequest;
import com.amazonaws.services.cloudwatchevents.model.ListRulesResult;
import com.amazonaws.services.cloudwatchevents.model.Rule;
import com.google.gson.Gson;
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

@PacmanRule(key = "check-cloudwatch-event-rule", desc = "All Cloud watch events from all accounts should be sent to designated event bus", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckCloudWatchEventsForAllAccountsRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckCloudWatchEventsForAllAccountsRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-cloudwatch-event-rule <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * roleIdentifyingString : Configure it as role/pacbot_ro <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided y execution engine
	 *
	 */
	@Override
	public RuleResult execute(Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========CheckCloudWatchEventsForAllAccountsRule started=========");
		Map<String, String> temp = new HashMap<>();
		temp.putAll(ruleParam);
		temp.put("region", "us-west-2");

		Map<String, Object> map = null;
		Annotation annotation = null;
		AmazonCloudWatchEventsClient cloudWatchEventsClient = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String accountName = resourceAttributes.get("accountname");
		String ruleName = ruleParam.get(PacmanRuleConstants.RULE_NAME);
		logger.info(resourceAttributes.get("accountid"));
		logger.info(resourceAttributes.get("accountname"));

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

		Gson gson = new Gson();
		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		Map<String, Object> failedType = new HashMap<>();

		if (!PacmanUtils.doesAllHaveValue(severity, category, ruleName)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		try {
			map = getClientFor(AWSService.CLOUDWATCH_EVENTS,roleIdentifyingString, temp);
			cloudWatchEventsClient = (AmazonCloudWatchEventsClient) map.get(PacmanSdkConstants.CLIENT);

			ListRulesRequest listRulesRequest = new ListRulesRequest();
			listRulesRequest.setNamePrefix(ruleName);
			ListRulesResult listRulesResult = cloudWatchEventsClient.listRules(listRulesRequest);
			String state = null;
			boolean isRuleEmpty = false;
			boolean isDisabled = false;
			if (listRulesResult.getRules().isEmpty()) {
				isRuleEmpty = true;
				failedType.put("ruleList", "Empty");
			}

			if (!listRulesResult.getRules().isEmpty()) {
				for (Rule result : listRulesResult.getRules()) {
					state = result.getState();
					logger.info(state);
					if (!"ENABLED".equals(state)) {
						isDisabled = true;
						failedType.put("ruleState", "Disabled");
					}
				}
			}
			if (isRuleEmpty || isDisabled) {
				annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION,"Cloud watch events from "+ accountName+ " is not been sent to designated default event bus");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);

				issue.put(PacmanRuleConstants.VIOLATION_REASON,"Cloud watch events from "+ accountName+ " is not been sent to designated default event bus!!");
				issue.put("failed_reason", gson.toJson(failedType));
				issueList.add(issue);
				annotation.put("issueDetails", issueList.toString());
				logger.debug("========CheckCloudWatchEventsForAllAccountsRule ended with annotation : {}=========",annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
		} catch (UnableToCreateClientException e) {
			logger.error("unable to get client for following input", e);
			throw new InvalidInputException(e.toString());
		}

		logger.debug("========CheckCloudWatchEventsForAllAccountsRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "All Cloud watch events from all accounts should be sent to designated event bus";
	}
}
