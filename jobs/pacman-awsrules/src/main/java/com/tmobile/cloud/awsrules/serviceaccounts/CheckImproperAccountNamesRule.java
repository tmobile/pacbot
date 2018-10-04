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
package com.tmobile.cloud.awsrules.serviceaccounts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-improper-account-name", desc = "Checks for account names having spaces inbetween", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckImproperAccountNamesRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckImproperAccountNamesRule.class);
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam 
	 * 
	 * ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-improper-account-name <br><br>
	 * 
	 * threadsafe : if true , rule will be executed on multiple threads <br><br>
	 *  
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		
		logger.debug("========CheckImproperAccountNamesRule started=========");
		Annotation annotation = null;
		String resourceId = null;
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		
		MDC.put("executionId", ruleParam.get("executionId")); 
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(severity,category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		if (resourceAttributes != null) {
			resourceId = resourceAttributes.get(PacmanSdkConstants.RESOURCE_ID);
			if(StringUtils.containsWhitespace(resourceId)){
			logger.info(resourceId, "=========_resourceId");
				annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION,"Improper account name found !!");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				
				issue.put(PacmanRuleConstants.VIOLATION_REASON, "Account name with space found");
				issueList.add(issue);
				annotation.put("issueDetails",issueList.toString());
				
				logger.debug("========CheckImproperAccountNamesRule ended with an annotation : {}=========", annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
		}
			}
		logger.debug("========CheckImproperAccountNamesRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "This rule checks for account names having spaces inbetween";
	}
}
