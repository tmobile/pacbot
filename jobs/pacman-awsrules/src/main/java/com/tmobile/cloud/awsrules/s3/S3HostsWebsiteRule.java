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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Purpose: This rule checks for s3 bucket containing web-site configuration.
 * Author: pavankumarchaitanya 
 * Reviewers: Kamal, Kanchana 
 * Modified Date: April 11th, 2019
 * 
 */
@PacmanRule(key = "check-for-s3-hosting-website", desc = "checks for S3 Buckets Hosting Website", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class S3HostsWebsiteRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(S3HostsWebsiteRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * ************* Following are the Rule Parameters********* <br>
	 * 
	 * ruleKey : check-for-s3-hosting-website <br><br>

	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========S3HostsWebsiteRule started=========");
		String s3BucketName = resourceAttributes.get(PacmanSdkConstants.RESOURCE_ID);
		String websiteConfiguration = resourceAttributes.get(PacmanRuleConstants.WEB_SITE_CONFIGURATION);
		String description = "S3 bucket " + s3BucketName+ " has website configuration for hosting website or redirecting requests from S3";
		
		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		if(!StringUtils.isEmpty(websiteConfiguration)){
		if ("true".equalsIgnoreCase(websiteConfiguration)) {
			logger.info("Found S3 Bucket With Resource Id: [{}] Which Hosts Website",resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID));
			return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, PacmanUtils.createS3Annotation(ruleParam, description));
		} else {
			logger.info(s3BucketName, "This Bucket Doesn't Host Website.");
			return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
		}
		}else{
			 logger.error("website configuration attribute is not available for {}", s3BucketName);
	         throw new RuleExecutionFailedExeption("website configuration attribute is not available for "+ s3BucketName);
		}

	}


	@Override
	public String getHelpText() {
		return "This rule checks for s3 bucket containing web-site configuration.";
	}

}