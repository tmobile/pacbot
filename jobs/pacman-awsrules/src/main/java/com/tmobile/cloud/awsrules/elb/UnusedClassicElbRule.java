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
  Purpose:
  Author :u55262
  Modified Date: Sep 19, 2017
  
 **/
package com.tmobile.cloud.awsrules.elb;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-unused-classic-elb", desc = "checks for unused classic elb which are not associated with any instance", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class UnusedClassicElbRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(UnusedClassicElbRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 *ruleKey : check-for-unused-classic-elb <br><br>
	 *
	 *esClassicElbWithInstanceUrl : Enter the classic elb with instance es api <br><br>
	 *
	 *threadsafe : if true , rule will be executed on multiple threads <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 * 
	 *ruleCategory : Enter the value of category <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========UnusedClassicElbRule started=========");
		String classicLoadBalncerId = null;
		String region = null;
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String classicElbUrl = null;
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_CLASSIC_ELB_WITH_INSTANCE_URL);
        
        if(!StringUtils.isNullOrEmpty(formattedUrl)){
            classicElbUrl =  formattedUrl;
        }
		
		if (!PacmanUtils.doesAllHaveValue(severity,category,classicElbUrl)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		if (resourceAttributes != null) {
			classicLoadBalncerId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.LOAD_BALANCER_ID_ATTRIBUTE));
			region = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.REGION_ATTR));
			boolean isClassicElbWithEc2Exists = false;
			try {
				isClassicElbWithEc2Exists = PacmanUtils.checkResourceIdFromElasticSearch(classicLoadBalncerId,classicElbUrl,PacmanRuleConstants.LOAD_BALANCER_ID_ATTRIBUTE,region);
			} catch (Exception e) {
				logger.error("unable to determine",e);
				throw new RuleExecutionFailedExeption("unable to determine"+e);
			}
			if (!isClassicElbWithEc2Exists) {
				String description = "Unused Classic ELB found!!";
	            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, PacmanUtils.createELBAnnotation("Classic",ruleParam, description,severity,category));
			}
		}
		logger.debug("========UnusedClassicElbRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks unused classic elb which are not associated with any instance";
	}
}
