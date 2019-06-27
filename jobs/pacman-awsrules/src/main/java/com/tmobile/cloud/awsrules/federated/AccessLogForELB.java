/*******************************************************************************
 * Copyright 2019 T Mobile, Inc. or its affiliates. All Rights Reserved.
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
  Copyright (C) 2019 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :Avinash
  Date: Jan 21, 2019

 **/
package com.tmobile.cloud.awsrules.federated;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-access-log-for-elb", desc = "checks for access log for application/classic elb and s3 bucket name for access log", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class AccessLogForELB extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(AccessLogForELB.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 *
	 * @param ruleParam
	 *
	 ************** Following are the Rule Parameters********* <br><br>
	 *
	 *ruleKey : check-for-access-log-for-classic-elb <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 *
	 *ruleCategory : Enter the value of category <br><br>
	 *
	 *accessLogBucketName : Name of the access log bucket name <br><br>
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {

		logger.debug("========AccessLogForELB started=========");
		String accessLog = resourceAttributes.get("accesslog");
		String accessLogBucketName = resourceAttributes.get("accesslogbucketname");
		String ruleParamBucketKey = ruleParam.get("accessLogBucketName");
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String entityType = ruleParam.get(PacmanRuleConstants.ENTITY_TYPE);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String loggingTags = resourceAttributes.get("tags.logging");
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		if (!PacmanUtils.doesAllHaveValue(severity, category,ruleParamBucketKey)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		String description = "Access log for "+entityType;
			if (loggingTags == null || "true".equalsIgnoreCase(loggingTags)) {
				if (accessLogBucketName != null && accessLogBucketName.equalsIgnoreCase(ruleParamBucketKey)
						&& "true".equalsIgnoreCase(accessLog)) {
					logger.info("Access log for {} is available in bucket {}", entityType,accessLogBucketName);
					return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
				} else {
					description += "is not available in S3 bucket";
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
							PacmanUtils.createELBAnnotation(entityType, ruleParam, description, severity, category));
				}
			} 
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks unused application elb which are not associated with any instance";
	}
}
