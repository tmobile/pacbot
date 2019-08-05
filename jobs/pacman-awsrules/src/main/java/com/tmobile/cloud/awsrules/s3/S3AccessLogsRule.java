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
 Purpose: This rule check for the elastic search exposed to public
 Author : Kkambal1
 Modified Date: Jun 6, 2019

 **/
package com.tmobile.cloud.awsrules.s3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.google.common.collect.HashMultimap;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-s3-access-logs", desc = "This rule checks for private s3 has server access logs enabled or not", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class S3AccessLogsRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(S3AccessLogsRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 *
	 *ruleKey : check-for-s3-access-logs<br><br>
	 *
	 *esS3PubAccessIssueUrl : Enter the S3 Public Access ES issue URL <br><br>
	 *
	 *s3PublicAccessRuleId : Enter the ruleId which is s3 with public access with read\write<br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========S3AccessLogsRule started=========");
		Annotation annotation = null;
		String esS3PubAccessIssueUrl = null;
		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		String s3Bucket = ruleParam.get(PacmanRuleConstants.RESOURCE_ID);
		String destinationBucketForAutoFix = ruleParam.get(PacmanRuleConstants.DESTINATION_BUCKET_AUTOFIX);
		String accessLogsEnabledRegions = ruleParam.get(PacmanRuleConstants.ACCESSLOGS_ENABLED_REGIONS);
		String splitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);
		String region = resourceAttributes.get(PacmanRuleConstants.REGION_ATTR);
		String accountId = resourceAttributes.get(PacmanRuleConstants.ACCOUNTID);
		String targetType = resourceAttributes.get(PacmanRuleConstants.ENTITY_TYPE);
		String isLoggingEnabled = resourceAttributes.get(PacmanRuleConstants.IS_S3_ACCESS_LOGS_ENABLED);

		String description = targetType+ " has not enabled the server access logs for " + s3Bucket;
		String s3PublicAccessRuleId = ruleParam.get(PacmanRuleConstants.S3_PUBLIC_ACCESS_RULE_ID);
        Map<String, String> data = new HashMap<>();
        data.put("ACCOUNT_ID", accountId);
        data.put("REGION", region);
        destinationBucketForAutoFix = StrSubstitutor.replace(destinationBucketForAutoFix, data);

		String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
		logger.debug("========pacmanHost {}  =========", pacmanHost);

		if (!StringUtils.isNullOrEmpty(pacmanHost)) {
			esS3PubAccessIssueUrl = ruleParam.get(PacmanRuleConstants.ES_S3_PUBLIC_ACCESS_ISSUE_URL);
			esS3PubAccessIssueUrl = pacmanHost + esS3PubAccessIssueUrl;
		}

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

		if (!PacmanUtils.doesAllHaveValue(esS3PubAccessIssueUrl,s3PublicAccessRuleId,destinationBucketForAutoFix,accessLogsEnabledRegions,splitter)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		try {

			Map<String, Object> mustFilter = new HashMap<>();
			mustFilter.put(PacmanRuleConstants.RESOURCE_ID, s3Bucket);
			mustFilter.put(PacmanRuleConstants.RULE_ID, s3PublicAccessRuleId);
			mustFilter.put(PacmanRuleConstants.ACCOUNTID, accountId);
			mustFilter.put(PacmanRuleConstants.REGION_ATTR, region);
			HashMultimap<String, Object> shouldFilter = HashMultimap.create();
			Map<String, Object> mustTermsFilter = new HashMap<>();
			shouldFilter.put(PacmanSdkConstants.ISSUE_STATUS_KEY,PacmanSdkConstants.STATUS_OPEN);
			shouldFilter.put(PacmanSdkConstants.ISSUE_STATUS_KEY,PacmanRuleConstants.STATUS_EXEMPTED);

			Set<String> resourceSet = PacmanUtils.getValueFromElasticSearchAsSet(esS3PubAccessIssueUrl,mustFilter, shouldFilter, mustTermsFilter,"_resourceid", null);
				logger.debug("======issueDetails : {}", resourceSet);	
			
				logger.debug("======isLoggingEnabled : {}", Boolean.parseBoolean(isLoggingEnabled));	
			
			if (resourceSet.isEmpty() && !Boolean.parseBoolean(isLoggingEnabled)) {
				String destinationBucketName = resourceAttributes.get(PacmanRuleConstants.DESTINATION_BUCKET_NAME);
				String logFilePrefix = resourceAttributes.get(PacmanRuleConstants.LOG_FILE_PREFIX);
				annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION, description);
				annotation.put(PacmanRuleConstants.SEVERITY,ruleParam.get(PacmanRuleConstants.SEVERITY));
				annotation.put(PacmanRuleConstants.CATEGORY,ruleParam.get(PacmanRuleConstants.CATEGORY));
				destinationBucketForAutoFix = destinationBucketForAutoFix.replace("ACCOUNT_ID", accountId);
				destinationBucketForAutoFix = destinationBucketForAutoFix.replace("REGION", region);
				annotation.put(PacmanRuleConstants.DESTINATION_BUCKET_AUTOFIX,destinationBucketForAutoFix);
				annotation.put(PacmanRuleConstants.ACCESSLOGS_ENABLED_REGIONS,accessLogsEnabledRegions);
				annotation.put(PacmanSdkConstants.SPLITTER_CHAR,splitter);
				issue.put(PacmanRuleConstants.VIOLATION_REASON, description);
				issue.put(PacmanRuleConstants.IS_S3_ACCESS_LOGS_ENABLED, isLoggingEnabled);
				issue.put(PacmanRuleConstants.DESTINATION_BUCKET_NAME, destinationBucketName);
				issue.put(PacmanRuleConstants.LOG_FILE_PREFIX, logFilePrefix);
				issueList.add(issue);
				annotation.put("issueDetails", issueList.toString());
				logger.debug("========S3AccessLogsRule ended with an annotation {} : =========",annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuleExecutionFailedExeption(e.getMessage());
		}
		logger.debug("========S3AccessLogsRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "This rule checks for private s3 has server access logs enabled or not";
	}
	
}
