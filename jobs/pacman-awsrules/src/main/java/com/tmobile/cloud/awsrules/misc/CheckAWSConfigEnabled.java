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
  Modified Date: Jul 20, 2017

 **/
package com.tmobile.cloud.awsrules.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.config.AmazonConfigClient;
import com.amazonaws.services.config.model.ConfigurationRecorder;
import com.amazonaws.services.config.model.DescribeConfigurationRecordersResult;
import com.amazonaws.util.CollectionUtils;
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

@PacmanRule(key = "check-aws-config-enabled", desc = "Checks for AWS Config enabled for given AWS account and region", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.SECURITY)
public class CheckAWSConfigEnabled extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckAWSConfigEnabled.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam
	 *
	 * ************* Following are the Rule Parameters********* <br><br>
	 *
	 * ruleKey : check-aws-config-enabled <br><br>
	 *
	 * severity : Enter the value of severity <br><br>
	 *
	 * ruleCategory : Enter the value of category <br><br>
	 *
	 * roleIdentifyingString : Configure it as role/pac_ro <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided y execution engine
	 *
	 */


	public RuleResult execute(Map<String, String> ruleParam,Map<String, String> resourceAttributes) {

		logger.debug("========CheckAWSConfigEnabled started=========");
		Map<String, Object> map = null;
		AmazonConfigClient awsConfigClient = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex

		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();

		if (!PacmanUtils.doesAllHaveValue(severity,category,roleIdentifyingString)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		Annotation annotation = null;
		try {
			map = getClientFor(AWSService.CONFIG, roleIdentifyingString, ruleParam);
			awsConfigClient = (AmazonConfigClient) map.get(PacmanSdkConstants.CLIENT);
			// Check AWS Config Enabled
            DescribeConfigurationRecordersResult describeConfigurationRecordersResult = awsConfigClient.describeConfigurationRecorders();
            List<ConfigurationRecorder> configurationRecorders = describeConfigurationRecordersResult.getConfigurationRecorders();
            if (CollectionUtils.isNullOrEmpty(configurationRecorders)) {
                // Create an annotation if config is not enabled
                annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,"AWS Config not enabled");
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.CATEGORY, category);
                issue.put(PacmanRuleConstants.VIOLATION_REASON, "AWS Config not enabled");
                issueList.add(issue);
                annotation.put("issueDetails",issueList.toString());
                logger.debug("========CheckAWSConfigEnabled ended with annotation {} :=========",annotation);
                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
            } else {
                logger.info("AWS Config enabled");
            }
		} catch (UnableToCreateClientException e) {
		    logger.error("unable to get client for following input", e);
			throw new InvalidInputException(e.getMessage());
		}

		logger.debug("========CheckAWSConfigEnabled ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);

	}

	public String getHelpText() {
		return "This rule checks for AWS Config is enabled for given account & region";
	}

}
