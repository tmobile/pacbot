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
  Date: Mar 22, 2019

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

@PacmanRule(key = "check-fmb-awssoc-subscribed-in-sns", desc = "checks for FMB AWS-Soc email are subscribed under TSI_Base_Security_Incident topic in N.virginia region only", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class SNSInfoForSubscribedEmails extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(SNSInfoForSubscribedEmails.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 *
	 * @param ruleParam
	 *
	 ************** Following are the Rule Parameters********* <br><br>
	 *
	 *ruleKey : check-FMB_AWSSOC-subscribed-in-SNS <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 *
	 *ruleCategory : Enter the value of category <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========SNSInfoForSubscribedEmails started=========");
		String topicARN = resourceAttributes.get("securitytopicarn");
		String subscriptionEndPoint = resourceAttributes.get("securitytopicendpoint");
		String endPoint = ruleParam.get("endPoint");
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		Annotation annotation = null;
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		if (resourceAttributes != null) {
				if (topicARN != null && topicARN.contains("TSI_Base_Security_Incident") &&  subscriptionEndPoint != null && subscriptionEndPoint.contains(endPoint)) {
					logger.info("Subscription is enabled for " + endPoint);
					return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
				} else {
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"SNS is not subscribed into TSI_Based_Security_Incident topic!!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
					annotation.put(PacmanRuleConstants.CATEGORY, category);

					issue.put(PacmanRuleConstants.VIOLATION_REASON, "SNS is not subscribed into TSI_Based_Security_Incident topic ");
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
				}
		}
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks for FMB AWS-Soc email are subscribed under TSI_Base_Security_Incident topic in N.virginia region only";
	}
}
