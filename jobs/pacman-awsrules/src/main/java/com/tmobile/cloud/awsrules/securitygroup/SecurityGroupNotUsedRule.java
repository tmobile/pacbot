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
package com.tmobile.cloud.awsrules.securitygroup;

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

@PacmanRule(key = "check-for-unused-security-group", desc = "checks for unused security groups", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class SecurityGroupNotUsedRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(SecurityGroupNotUsedRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * esServiceWithSgUrl   : Comma separated list of services with sg ES url's <br><br>
	 * 
	 * splitterChar : The splitter character used to split the mandatory tags <br><br>
	 * 
	 * ruleKey : check-for-unused-security-group <br><br>
	 * 
	 * esUrl : Enter the ES url <br><br>
	 * 
	 * threadsafe : if true , rule will be executed on multiple threads <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {

		logger.debug("========SecurityGroupNotUsedRule started=========");
		String groupId = null;
		Annotation annotation = null;
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);
		String groupName = resourceAttributes.get(PacmanRuleConstants.GROUP_NAME);
		String serviceWithSgUrls = null;
		String esUrl = ruleParam.get(PacmanRuleConstants.ES_URL_PARAM);
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);

        if (!StringUtils.isNullOrEmpty(pacmanHost)) {
            serviceWithSgUrls = ruleParam.get(PacmanRuleConstants.ES_SERVICES_WITH_SG_URL);
            esUrl = pacmanHost;
        }
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(severity,category,serviceWithSgUrls,tagsSplitter,esUrl)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		List<String> serviceWithSgUrlsList = PacmanUtils.splitStringToAList(serviceWithSgUrls, tagsSplitter);
		
		if (!resourceAttributes.isEmpty()) {
			groupId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.GROUP_ID));
			String resource;
			try {
				resource = PacmanUtils.getQueryFromElasticSearch(groupId,serviceWithSgUrlsList,esUrl,ruleParam);
			
			
			if(StringUtils.isNullOrEmpty(resource)){
				annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION,"Unused security group found!!");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				annotation.put(PacmanRuleConstants.GROUP_NAME, groupName);
				
				issue.put(PacmanRuleConstants.VIOLATION_REASON, "Security group not associated to any of EC2/ApplicationElb/ClassicElb/RDSDB/RDSCluster/RedShift/Lambda/Elasticsearch");
				issueList.add(issue);
				annotation.put("issueDetails",issueList.toString());
				
				logger.debug("========SecurityGroupNotUsedRule ended with an annotation : {}=========",annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
			}
			} catch (Exception e) {
				logger.error("unable to determine",e);
				throw new RuleExecutionFailedExeption("unable to determine"+e);
			}
		}
		
		logger.debug("========SecurityGroupNotUsedRule ended=========");

		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks unused security groups which are not associated to any of EC2/ApplicationElb/ClassicElb/RDS";
	}
}
