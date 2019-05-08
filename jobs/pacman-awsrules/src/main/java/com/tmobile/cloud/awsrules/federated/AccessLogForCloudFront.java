/*******************************************************************************
 * Copyright 2019 T Mobile, Inc. or its affiliates. All Rights Reserved.
<<<<<<< HEAD
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
=======
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
  Copyright (C) 2019 T Mobile Inc - All Rights Reserve
  Purpose:
<<<<<<< HEAD
  Author :Avinash 
  Date: Jan 30, 2019
  
=======
  Author :Avinash
  Date: Jan 30, 2019

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
 **/
package com.tmobile.cloud.awsrules.federated;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-access-log-for-cloudfront", desc = "checks for access log for cloudfront and s3 bucket name ", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class AccessLogForCloudFront extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(AccessLogForCloudFront.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
<<<<<<< HEAD
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 *ruleKey : check-for-access-log-for-cloudfront <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 * 
	 *ruleCategory : Enter the value of category <br><br> 
=======
	 *
	 * @param ruleParam
	 *
	 ************** Following are the Rule Parameters********* <br><br>
	 *
	 *ruleKey : check-for-access-log-for-cloudfront <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 *
	 *ruleCategory : Enter the value of category <br><br>
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
	 *
	 *accessLogBucketName : ARN of the access log bucket name <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========AccessLogForCloudfront started=========");
		String accessLogBucketName = resourceAttributes.get("bucketname");
		String accessLogEnabled = resourceAttributes.get("accesslogenabled");
		String ruleParamBucketKey = ruleParam.get("accessLogBucketName");
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String loggingTags = resourceAttributes.get("tags.logging");
		Annotation annotation = null;
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		if (resourceAttributes != null) {
			if (loggingTags == null || loggingTags.equalsIgnoreCase("true")) {
				if (accessLogBucketName != null && accessLogBucketName.equalsIgnoreCase(ruleParamBucketKey)
						&& accessLogEnabled.equalsIgnoreCase("true")) {
					logger.info("Access log for Cloud front is available in bucket " + accessLogBucketName);
					return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
				} else {
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"Access log is not enabled!!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
					annotation.put(PacmanRuleConstants.CATEGORY, category);
<<<<<<< HEAD
					
=======

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "Access log is not enabled and not attached to any bucket ");
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
				}
			} else {
				return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
			}
		}
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks unused application elb which are not associated with any instance";
	}
}
