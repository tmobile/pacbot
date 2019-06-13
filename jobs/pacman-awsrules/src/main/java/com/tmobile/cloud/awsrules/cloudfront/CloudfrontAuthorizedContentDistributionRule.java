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
package com.tmobile.cloud.awsrules.cloudfront;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Purpose: This rule checks for cloudfront resources serving content without
 * authorization
 * 
 * Author: pavankumarchaitanya
 * 
 * Reviewers: Kamal, Kanchana
 * 
 * Modified Date: April 15th, 2019
 */
@PacmanRule(key = "check-for-unauthorized-cloudfront-distribution", desc = "checks for unauthorized cloudfront distribution", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.SECURITY)
public class CloudfrontAuthorizedContentDistributionRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(CloudfrontAuthorizedContentDistributionRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * ************* Following are the Rule Parameters********* <br>
	 * 
	 * ruleKey : check-for-unauthorized-cloudfront-distribution <br>
	 * <br>
	 * 
	 * @param resourceAttributes
	 *            this is a resource in context which needs to be scanned this is
	 *            provided by execution engine
	 *
	 */
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========CloudfrontAuthorizedContentDistributionRule started=========");
		String cloudFrontResourceID = resourceAttributes.get(PacmanSdkConstants.RESOURCE_ID);

		String description = "CloudFront instance: " + cloudFrontResourceID
				+ " is unauthorized for content distribution";

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		if (!StringUtils.isEmpty(cloudFrontResourceID)) {
			logger.info("Found Cloudfront instance with Resource Id: [{}] distributing content without authorization.",
					resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID));
			return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
					PacmanUtils.createAnnotation("cloudfront", ruleParam, description, PacmanSdkConstants.SEV_MEDIUM,
							PacmanSdkConstants.SECURITY));

		}

		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);

	}

	@Override
	public String getHelpText() {
		return "This rule checks for unauthorized cloudfront distribution.";
	}

}