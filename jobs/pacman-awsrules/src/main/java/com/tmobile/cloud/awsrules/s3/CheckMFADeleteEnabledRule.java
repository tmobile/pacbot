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
package com.tmobile.cloud.awsrules.s3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
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

@PacmanRule(key = "check-for-s3-MFA-delete-enabled", desc = "checks s3 bucket has MFA delete enabled or not", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckMFADeleteEnabledRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckMFADeleteEnabledRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-s3-MFA-delete-enabled <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * roleIdentifyingString : Configure it as role/pac_ro <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========CheckMFADeleteEnabledRule started=========");
		Map<String, Object> map = null;
		AmazonS3Client awsS3Client = null;
		Annotation annotation = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String s3BucketName = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put("executionId", ruleParam.get("executionId")); 
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();

		if (!PacmanUtils.doesAllHaveValue(severity, category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		if (!resourceAttributes.isEmpty()) {
			try {
				map = getClientFor(AWSService.S3, roleIdentifyingString, ruleParam);
				awsS3Client = (AmazonS3Client) map.get(PacmanSdkConstants.CLIENT);
			} catch (UnableToCreateClientException e) {
				logger.error("unable to get client for following input", e);
				throw new InvalidInputException(e.toString());
			}

			BucketVersioningConfiguration configuration = awsS3Client.getBucketVersioningConfiguration(s3BucketName);
				if(configuration.isMfaDeleteEnabled()==null || !configuration.isMfaDeleteEnabled()){
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"S3 with no MFA delete enabled found!!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "S3 with no MFA delete enabled found!!");
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					logger.debug("========CheckMFADeleteEnabledRule ended with annotation {} :=========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
				}

		}
		logger.debug("========CheckMFADeleteEnabledRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);

	}

	public String getHelpText() {
		return "This rule checks s3 bucket has MFA delete enabled or not";
	}
}
