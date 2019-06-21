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
  Author :Anukriti
  Modified Date: Feb 27, 2019

 **/
package com.tmobile.cloud.awsrules.federated;

import java.util.ArrayList;
import java.util.Arrays;
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

@PacmanRule(key = "check-cloudtrail-multiRegion-enabled", desc = "This rule checks for AWS CloudTrail multi region enabled", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.SECURITY)
public class CheckCloudTrailMultiRegionEnabled extends BaseRule {

    private static final Logger logger = LoggerFactory.getLogger(CheckCloudTrailMultiRegionEnabled.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     *
     * @param ruleParam
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            ruleKey : check-cloudtrail-multiRegion-enabled <br>
     * <br>
     *
     *            severity : Enter the value of severity <br>
     * <br>
     *
     *            ruleCategory : Enter the value of category <br>
     * <br>
     *
     *            inputCloudTrailName : Enter the cloud trail input  <br>
     * <br>
     *
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
        logger.debug("========CheckAWSCloudTrailConfig started=========");
        Annotation annotation = null;
        String cloudTrailInput = ruleParam.get("inputCloudTrailName");
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
        
        List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
        if (!PacmanUtils.doesAllHaveValue(severity, category,cloudTrailInput)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }
        String cloudTrail = resourceAttributes.get("cloudtrailname");
        List<String>cloudtrail = new ArrayList(Arrays.asList(cloudTrail.split(",")));
        	if(!cloudtrail.contains(cloudTrailInput)){
        		annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
        		annotation.put(PacmanSdkConstants.DESCRIPTION,"Cloudtrail multiregion is not enabled!!");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				issue.put(PacmanRuleConstants.VIOLATION_REASON,	"Cloudtrail multiregion is not enabled!!");
				issueList.add(issue);
				annotation.put("issueDetails", issueList.toString());
	    		return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,annotation);
        	}

        logger.debug("========CheckAWSCloudTrailConfig ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    public String getHelpText() {
		return "This rule checks for AWS CloudTrail multi region enabled";
	}

}
