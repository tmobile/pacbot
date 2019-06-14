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
  Author :u55262
  Modified Date: feb 02, 2018
  
 **/
package com.tmobile.cloud.awsrules.elasticip;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-unused-elastic-ip", desc = "checks for unused elastic ip which are not associated with any instance", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class UnusedElasticIpRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(UnusedElasticIpRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-unused-elastic-ip <br><br>
	 * 
	 * threadsafe   : if true , rule will be executed on multiple threads <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {

		logger.debug("========UnusedElasticIpRule started=========");
		Annotation annotation = null;
		String associationId = null;
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(severity,category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (resourceAttributes != null) {
			associationId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.ASSOCIATION_ID));
           
			if (StringUtils.isNullOrEmpty(associationId)) {
				annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION,"Unused Elastic Ip found!!");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				annotation.put(PacmanRuleConstants.ALLOCATION_ID, resourceAttributes.get(PacmanRuleConstants.ALLOCATION_ID));
				issue.put(PacmanRuleConstants.VIOLATION_REASON, "ElasticIp doesn't associated to an EC2 instance!!");
				issueList.add(issue);
				annotation.put("issueDetails",issueList.toString());
				logger.debug("========UnusedElasticIpRule ended with annotation {} :=========",annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
		}
		logger.debug("========UnusedElasticIpRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks unused elastic ip which are not associated with any instance";
	}
	
}
