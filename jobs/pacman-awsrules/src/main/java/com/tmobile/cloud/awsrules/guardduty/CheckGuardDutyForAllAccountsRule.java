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
 * 
 */

package com.tmobile.cloud.awsrules.guardduty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.guardduty.AmazonGuardDutyClient;
import com.amazonaws.services.guardduty.model.ListDetectorsRequest;
import com.amazonaws.services.guardduty.model.ListDetectorsResult;
import com.google.gson.Gson;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-guard-duty-enabled-for-all-accounts", desc = "AWS Guard Duty service should be enabled on all regions of all AWS accounts", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckGuardDutyForAllAccountsRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckGuardDutyForAllAccountsRule.class);
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 *ruleKey : check-guard-duty-enabled-for-all-accounts <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 * 
	 *ruleCategory : Enter the value of category <br><br>
	 *
	 *roleIdentifyingString : Configure it as role/pac_ro <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */
	@Override
	public RuleResult execute(Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========CheckGuardDutyForAllAccountsRule started=========");
		Map<String, String> temp = new HashMap<>();
		temp.putAll(ruleParam);
		temp.put("region", "us-west-2");

		Map<String, Object> map = null; 
		Annotation annotation = null;
		AmazonGuardDutyClient dutyClient = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String accountName= resourceAttributes.get(PacmanRuleConstants.ACCOUNT_NAME);
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		Gson gson = new Gson();
		List<LinkedHashMap<String,Object>>issueList = new ArrayList();
		LinkedHashMap<String,Object>issue = new LinkedHashMap();
		Map<String,Object> failedType =new HashMap();

		if (!PacmanUtils.doesAllHaveValue(severity, category,roleIdentifyingString)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		
		
		try {
			map = getClientFor(AWSService.GUARD_DUTY, roleIdentifyingString, temp);
			dutyClient = (AmazonGuardDutyClient) map.get(PacmanSdkConstants.CLIENT);
			
			ListDetectorsRequest detectorsRequest = new ListDetectorsRequest();
            ListDetectorsResult detectorsResult = dutyClient.listDetectors(detectorsRequest);
            boolean isDisabled = true;
            
            if (!detectorsResult.getDetectorIds().isEmpty()) {
                isDisabled = false;
            }

            
            if (isDisabled) {
                failedType.put("detectors", "Not found");
                annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
                // AWS Guard Duty service should be enabled on all regions of
                // all AWS accounts
                annotation.put(PacmanSdkConstants.DESCRIPTION,"AWS Guard Duty service is not enabled to " + accountName + " account");
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.CATEGORY, category);
                
                issue.put(PacmanRuleConstants.VIOLATION_REASON, "AWS Guard Duty service is not enabled to " + accountName + " account");
                issue.put("failed_reason", gson.toJson(failedType) );
                issueList.add(issue);
                annotation.put("issueDetails",issueList.toString());
                logger.debug("========CheckGuardDutyForAllAccountsRule ended with annotaion {} : =========",annotation);
                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
            }
			
		} catch (UnableToCreateClientException e) {
			logger.error("unable to get client for following input", e);
			throw new InvalidInputException(e.toString());
		}
		logger.debug("========CheckGuardDutyForAllAccountsRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}


	@Override
	public String getHelpText() {
		return "AWS Guard Duty service should be enabled on all regions of all AWS accounts";
	}
}
