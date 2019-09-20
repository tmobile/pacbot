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
  Purpose: Checks for mandatory tags on the EC2 instances
  Author :kkumar
  Modified Date: Jun 20, 2017

 **/
package com.tmobile.cloud.awsrules.ec2;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.base.Joiner;
import com.tmobile.cloud.awsrules.utils.ConfigUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-missing-mandatory-tags", desc = "checks services for missing mandatory tags", severity = PacmanSdkConstants.SEV_HIGH,category=PacmanSdkConstants.GOVERNANCE)
public class TaggingRule extends BaseRule {

    private static final Logger logger = LoggerFactory.getLogger(TaggingRule.class);

    /**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam
	 *
	 * ************* Following are the Rule Parameters********* <br><br>
	 *
	 * mandatoryTags   : Comma separated list of AWS Ec2 mandatory tags <br><br>
	 *
	 * splitterChar : The splitter character used to split the mandatory tags <br><br>
	 *
	 * ruleKey : check-for-missing-mandatory-tags <br><br>
	 *
	 * threadsafe : if true , rule will be executed on multiple threads <br><br>
	 *
	 * severity : Enter the value of severity <br><br>
	 *
	 * ruleCategory : Enter the value of category <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */
	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {

		logger.debug("========TaggingRule started=========");
		Set<String> missingTags = new HashSet<>();
		String mandatoryTags = ConfigUtils.getPropValue(PacmanSdkConstants.TAGGING_MANDATORY_TAGS);
		String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);
		String entityId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String targetType = ruleParam.get(PacmanRuleConstants.TARGET_TYPE);

		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex


		if (!PacmanUtils.doesAllHaveValue(mandatoryTags,tagsSplitter,severity,category,targetType)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		String missingTagsStr = null;


		List<String> mandatoryTagsList = PacmanUtils.splitStringToAList(mandatoryTags, tagsSplitter);

		if (resourceAttributes!=null) {
			if(targetType.equalsIgnoreCase(PacmanRuleConstants.TARGET_TYPE_EC2)){
				if(resourceAttributes.get(PacmanRuleConstants.STATE_NAME).equalsIgnoreCase(PacmanRuleConstants.RUNNING_STATE)||resourceAttributes.get(PacmanRuleConstants.STATE_NAME).equalsIgnoreCase(PacmanRuleConstants.STOPPED_STATE)){
				missingTags = PacmanUtils.getMissingTagsfromResourceAttribute(mandatoryTagsList,resourceAttributes);
				}
			}else{
				missingTags = PacmanUtils.getMissingTagsfromResourceAttribute(mandatoryTagsList,resourceAttributes);
			}
		}
		missingTagsStr = Joiner.on(", ").join(missingTags);
		if (!missingTags.isEmpty()) {
			String description = "Missed tags for "+targetType+" are "+ missingTagsStr;
			return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, PacmanUtils.createAnnotaion(ruleParam, missingTagsStr, mandatoryTagsList, description,severity,category,targetType));

		} else {
			logger.info(targetType ," ", entityId , " has all manadatory tags");
		}
		logger.debug("========TaggingRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks for the missing tags of services";
	}
}
