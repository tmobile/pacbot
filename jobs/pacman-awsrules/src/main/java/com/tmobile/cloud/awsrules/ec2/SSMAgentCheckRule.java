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
package com.tmobile.cloud.awsrules.ec2;

import java.util.ArrayList;
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

@PacmanRule(key = "check-for-SSM-agent-rule", desc = "checks for SSM agent rule", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class SSMAgentCheckRule extends BaseRule {

	private static final Logger logger = LoggerFactory
			.getLogger(SSMAgentCheckRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 **************            Following are the Rule Parameters********* <br>
	 * <br>
	 * 
	 *            ruleKey : check-for-SSM-agent-rule <br>
	 * <br>
	 *
	 *            esSsmWithInstanceUrl : Enter the ssm with instance api <br>
	 * <br>
	 *  discoveredDaysRange : Enter the discovered days Range <br><br>
	 *            threadsafe : if true , rule will be executed on multiple
	 *            threads <br>
	 * <br>
	 *
	 *            severity : Enter the value of severity <br>
	 * <br>
	 * 
	 *            ruleCategory : Enter the value of category <br>
	 * <br>
	 *
	 * @param resourceAttributes
	 *            this is a resource in context which needs to be scanned this
	 *            is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,
			Map<String, String> resourceAttributes) {

		logger.debug("========SSMAgentCheckRule started=========");
		Annotation annotation = null;
		String resourceId = null;
		String accountId = null;
		String region = null;
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String online = ruleParam.get(PacmanRuleConstants.ONLINE);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String ssmAgentWithInstanceUrl = null;
		
		String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_SSM_WITH_INSTANCE_URL);

		String firstDiscoveredOn = resourceAttributes.get(PacmanRuleConstants.FIRST_DISCOVERED_ON);
        String discoveredDaysRange = ruleParam.get(PacmanRuleConstants.DISCOVERED_DAYS_RANGE);
        if (!StringUtils.isNullOrEmpty(firstDiscoveredOn)) {
            firstDiscoveredOn = firstDiscoveredOn.substring(0,firstDiscoveredOn.length() - 3);
        }

        
        if(!StringUtils.isNullOrEmpty(formattedUrl)){
            ssmAgentWithInstanceUrl =  formattedUrl;
        }
		
		String desc = null;
		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		if (!PacmanUtils.doesAllHaveValue(severity, category,
				ssmAgentWithInstanceUrl,discoveredDaysRange)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		if(null != resourceAttributes && PacmanRuleConstants.RUNNING_STATE.equalsIgnoreCase(resourceAttributes.get(PacmanRuleConstants.STATE_NAME))) {
		    if(PacmanUtils.calculateLaunchedDuration(firstDiscoveredOn)>Long.parseLong(discoveredDaysRange)){
			resourceId = StringUtils.trim(resourceAttributes
					.get(PacmanSdkConstants.RESOURCE_ID));
			accountId = StringUtils.trim(resourceAttributes
					.get(PacmanRuleConstants.ACCOUNTID));
			region = StringUtils.trim(resourceAttributes
					.get(PacmanRuleConstants.REGION_ATTR));
			boolean isSSMAgentOnline = false;
			try {
				isSSMAgentOnline = PacmanUtils.checkSSMAgent(resourceId,
						ssmAgentWithInstanceUrl,
						PacmanRuleConstants.INSTANCEID, region, accountId,
						online);
			} catch (Exception e) {
				logger.error("unable to determine" , e);
				throw new RuleExecutionFailedExeption("unable to determine" + e);
			}
			if (!isSSMAgentOnline) {
				desc = "On EC2 Instance " + "<b>" + resourceId + "</b>"
						+ " SSM Agent is not installed/online";
				annotation = Annotation.buildAnnotation(ruleParam,
						Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION, desc);
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				issue.put(PacmanRuleConstants.VIOLATION_REASON, desc);
				issueList.add(issue);
				annotation.put("issueDetails", issueList.toString());
				logger.debug("========SSMAgentCheckRule ended with an annotation {} : =========", annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
						PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
		}
		}
		logger.debug("========SSMAgentCheckRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
				PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks for SSM agent rule";
	}
	
}
