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
  Author :kkumar28
  Modified Date: Jun 20, 2017
  
 **/
package com.tmobile.cloud.awsrules.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-non-standard-region-rule", desc = "checks for the resource which has non standard region", severity = PacmanSdkConstants.SEV_HIGH,category=PacmanSdkConstants.GOVERNANCE)
public class NonStandardRegionsRule extends BaseRule {

    private static final Logger logger = LoggerFactory.getLogger(NonStandardRegionsRule.class);

    /**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam 
	 * 
	 * ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * standardRegions   : Comma separated list of standard regions <br><br>
	 * 
	 * splitterChar : The splitter character used to split the mandatory tags <br><br>
	 * 
	 * ruleKey : check-for-non-standard-region-rule <br><br>
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

        logger.debug("========NonStandardRegionsRule started=========");
        
        boolean isNonStandardRegion = false;
        String standardRegions = ruleParam.get(PacmanRuleConstants.STANDARD_REGIONS);
        String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);
        String region = ruleParam.get(PacmanSdkConstants.REGION);
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String targetType = ruleParam.get(PacmanRuleConstants.TARGET_TYPE);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 

        if (!PacmanUtils.doesAllHaveValue(standardRegions, tagsSplitter, severity, category)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        List<String> standardRegionsList = PacmanUtils.splitStringToAList(standardRegions, tagsSplitter);

        if (resourceAttributes != null) {
            if (targetType.equalsIgnoreCase(PacmanRuleConstants.TARGET_TYPE_EC2)) {
                if (resourceAttributes.get(PacmanRuleConstants.STATE_NAME).equalsIgnoreCase(PacmanRuleConstants.RUNNING_STATE) || resourceAttributes.get(PacmanRuleConstants.STATE_NAME).equalsIgnoreCase(PacmanRuleConstants.STOPPED_STATE)) {
                    isNonStandardRegion = PacmanUtils.isNonStandardRegion(standardRegionsList, region);
                }
            } else {
                isNonStandardRegion = PacmanUtils.isNonStandardRegion(standardRegionsList, region);
            }
        }

        if (!isNonStandardRegion) {
            String description = "Non standard Region for " + targetType + " is " + region;
            List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
            LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
            Annotation annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
            annotation.put(PacmanSdkConstants.DESCRIPTION,description);
            annotation.put(PacmanRuleConstants.SEVERITY, severity);
            annotation.put(PacmanRuleConstants.CATEGORY, category);
            issue.put(PacmanRuleConstants.VIOLATION_REASON,description);
            issueList.add(issue);
            annotation.put(PacmanRuleConstants.ISSUE_DETAILS,issueList.toString());
            logger.debug("========NonStandardRegionsRule ended with an annotation {} : =========",annotation);
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
        } else {
            logger.info(targetType, " has standard region");
        }
        logger.debug("========NonStandardRegionsRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    public String getHelpText() {
        return "This rule checks for the resource which has non standard region";
    }
}
