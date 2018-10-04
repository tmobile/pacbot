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
  Purpose: checks any AWS activity in blacklisted region
  Author :kanchana
  Modified Date: Feb 14, 2018

 **/
package com.tmobile.cloud.awsrules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.awsrules.utils.RulesElasticSearchRepositoryUtil;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-aws-activity-in-blacklisted-region", desc = "checks any AWS activity in blacklisted region", severity = PacmanSdkConstants.SEV_HIGH,category=PacmanSdkConstants.GOVERNANCE)
public class CheckAwsActivityInBlacklistedRegionRule extends BaseRule {

    private static final Logger logger = LoggerFactory.getLogger(CheckAwsActivityInBlacklistedRegionRule.class);

    /**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam
	 *
	 * ************* Following are the Rule Parameters********* <br><br>
	 *
	 * ruleKey : check-for-aws-activity-in-blacklisted-region <br><br>
	 *
	 * url : Configure the Es url,  <br><br>
	 *
	 * threadsafe : if true , rule will be executed on multiple threads <br><br>
	 *
	 * severity : Enter the value of severity <br><br>
	 *
	 * ruleCategory : Enter the value of category <br><br>
	 *
	 * awsSearch : Enter aws index for search <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */
	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {

		logger.debug("========CheckAwsActivityInBlacklistedRegionRule started=========");
		Annotation annotation = null;
		String accountId = resourceAttributes.get(PacmanSdkConstants.ACCOUNT_ID);
		String url = ruleParam.get(PacmanRuleConstants.URL);

        String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.AWS_SEARCH);

        if(!StringUtils.isEmpty(formattedUrl)){
            url =  formattedUrl;
        }

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex

		Gson gson = new Gson();
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		if (!PacmanUtils.doesAllHaveValue(severity,category,url)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}


		Map<String,Object> regionMap = new HashMap<>();
		if (!resourceAttributes.isEmpty()) {
			try {
			    List<Map<Object,Map<String,Object>>> inValidRegionMap = RulesElasticSearchRepositoryUtil.getInvalidRegions(url, accountId);
				for(Map<Object,Map<String,Object>> invalidRegion:inValidRegionMap){
				    if(invalidRegion.containsKey(PacmanRuleConstants.REGION_AND_COUNT) && invalidRegion.get(PacmanRuleConstants.REGION_AND_COUNT).containsKey("region") && invalidRegion.get(PacmanRuleConstants.REGION_AND_COUNT).containsKey("issueCount")){
					regionMap.put(invalidRegion.get(PacmanRuleConstants.REGION_AND_COUNT).get("region").toString(),invalidRegion.get(PacmanRuleConstants.REGION_AND_COUNT).get("issueCount").toString());
				    }
				}

				if (!inValidRegionMap.isEmpty()) {
					annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION, "Aws activity in blacklisted region found!!");
					annotation.put(PacmanRuleConstants.BLACKLISTED_REGIONS, inValidRegionMap.toString());
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);

					issue.put(PacmanRuleConstants.VIOLATION_REASON,	"Aws activity in blacklisted region found");
					issue.put("invalid_regions", gson.toJson(regionMap) );
					issueList.add(issue);
					annotation.put("issueDetails", issueList.toString());
					logger.debug("========CheckAwsActivityInBlacklistedRegionRule ended with an annotation {} :=========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
				}
			} catch (Exception e) {
				logger.error("unable to determine",e);
				throw new RuleExecutionFailedExeption("unable to determine"+e);
			}
		}
		logger.debug("========CheckAwsActivityInBlacklistedRegionRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks any AWS activity in blacklisted region";
	}
}
