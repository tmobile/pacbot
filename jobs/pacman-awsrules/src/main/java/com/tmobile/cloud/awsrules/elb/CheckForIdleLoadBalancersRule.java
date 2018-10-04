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
package com.tmobile.cloud.awsrules.elb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-idle-load-balancers", desc = "Checks for Elastic Load Balancing configuration for load balancers that are not actively used. Any load balancer that is configured accrues charges. If a load balancer has no associated back-end instances or if network traffic is severely limited, the load balancer is not being used effectively.", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckForIdleLoadBalancersRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckForIdleLoadBalancersRule.class);
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam 
	 * 
	 * ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * checkId   : Mention the checkId value <br><br>
	 * 
	 * ruleKey : check-for-idle-load-balancers <br><br>
	 * 
	 * esServiceURL : Enter the Es url <br><br>
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
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		
		logger.debug("========CheckForIdleLoadBalancersRule started=========");
		Annotation annotation = null;
		String loadBalancerId = null;
		String region = null;
		String accountId = null;
		String checkId = StringUtils.trim(ruleParam.get(PacmanRuleConstants.CHECK_ID));
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		String serviceEsURL = null;
		
		String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_CHECK_SERVICE_SEARCH_URL_PARAM);
        
        if(!StringUtils.isNullOrEmpty(formattedUrl)){
            serviceEsURL =  formattedUrl;
        }

        MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(checkId,severity,category,serviceEsURL)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		
		
		if (resourceAttributes != null) {
			region = resourceAttributes.get(PacmanRuleConstants.REGION);
			accountId = resourceAttributes.get(PacmanSdkConstants.ACCOUNT_ID);
			loadBalancerId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.LOAD_BALANCER_ID_ATTRIBUTE));
			Map<String, String> idleLoadBalancerMap = new HashMap<>();
			try {
				idleLoadBalancerMap = PacmanUtils.getIdleLoadBalancerDetails(checkId,loadBalancerId,serviceEsURL,region,accountId);
			} catch (Exception e) {
				logger.error("unable to determine",e);
				throw new RuleExecutionFailedExeption("unable to determine"+e);
			}
			if (!idleLoadBalancerMap.isEmpty()) {
				annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION,"Idle load balancer found !");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				annotation.put(PacmanRuleConstants.EST_MONTHLY_SAVINGS, idleLoadBalancerMap.get(PacmanRuleConstants.EST_MONTHLY_SAVINGS));
				annotation.put(PacmanRuleConstants.REASON, idleLoadBalancerMap.get(PacmanRuleConstants.REASON));
				
				issue.put(PacmanRuleConstants.VIOLATION_REASON,	"Idle load balancer found!!");
				issue.put(PacmanRuleConstants.CHECKID, checkId);
				issue.put(PacmanRuleConstants.SOURCE_VERIFIED, "trusted advisor");
				issueList.add(issue);
				annotation.put("issueDetails", issueList.toString());
				logger.debug("========CheckForIdleLoadBalancersRule ended with annotaion {} :=========",annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
			}
		logger.debug("========CheckForIdleLoadBalancersRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "This rule checks for Elastic Load Balancing configuration for load balancers that are not actively used. Any load balancer that is configured accrues charges. If a load balancer has no associated back-end instances or if network traffic is severely limited, the load balancer is not being used effectively.";
	}
}
