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
package com.tmobile.cloud.awsrules.route53;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
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

@PacmanRule(key = "check-for-aws-route53-DNS-For-accounts", desc = "Route 53 service is allowed to be used only in designated account. No other accounts should be using Route 53 service. Since Route 53 service is critical service for every application, a controlled environment is required for smooth operations. Also in order stop domain proliferation and enforce best practices, this service  is limited only to these two accounts", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckAwsRoute53DNSForAccountsRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(CheckAwsRoute53DNSForAccountsRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 * accountNames : Comma separated list of AWS Auth types <br><br>
	 * 
	 * splitterChar : The splitter character used to split the accountNames <br><br>
	 * 
	 * ruleKey : check-for-aws-route53-DNS-For-accounts <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * roleIdentifyingString : Configure it as role/pac_ro <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided y execution engine
	 *
	 */

	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========CheckAwsRoute53DNSForAccountsRule started=========");
		Map<String, String> temp = new HashMap<>();
		temp.putAll(ruleParam);
		temp.put("region", "us-west-2");
		Map<String, Object> map = null;
		Annotation annotation = null;
		AmazonRoute53Client route53Client = null;

		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String accountName = resourceAttributes.get(PacmanSdkConstants.ACCOUNT_NAME);
		String accNames = ruleParam.get(PacmanRuleConstants.ACCOUNT_NAMES);
		String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		MDC.put("executionId", ruleParam.get("executionId")); 
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
		
		Gson gson = new Gson();
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		Map<String,Object> failedType =new HashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(severity, category, accNames,	tagsSplitter)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		List<String> accountNames = PacmanUtils.splitStringToAList(accNames,tagsSplitter);
		Boolean isAccountExists = false;
		for (String account : accountNames) {
			if (accountName.equalsIgnoreCase(account)) {
				isAccountExists = true;
			} 
		}
		
			if(!isAccountExists) {

				try {
					map = getClientFor(AWSService.ROUTE53, roleIdentifyingString, temp);
					route53Client = (AmazonRoute53Client) map.get(PacmanSdkConstants.CLIENT);
					ListHostedZonesResult result = route53Client.listHostedZones();
                    if (!result.getHostedZones().isEmpty()) {
                        failedType.put("hostedZones", "Found");
                        annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
                        annotation.put(PacmanSdkConstants.DESCRIPTION,"AWS route53 DNS for "+accountName+" account has found!!");
                        annotation.put(PacmanRuleConstants.SEVERITY, severity);
                        annotation.put(PacmanRuleConstants.CATEGORY, category);
                        
                        issue.put(PacmanRuleConstants.VIOLATION_REASON, "AWS route53 DNS for "+accountName+" account has found!!");
                        issue.put("failed_reason", gson.toJson(failedType) );
                        issueList.add(issue);
                        annotation.put("issueDetails",issueList.toString());
                        
                        return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                    }
				} catch (UnableToCreateClientException e) {
					logger.error("unable to get client for following input", e);
					throw new InvalidInputException(e.toString());
				}

			}

		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "Route 53 service is allowed to be used only in designated accounts. No other accounts should be using Route 53 service. Since Route 53 service is critical service for every application, a controlled environment is required for smooth operations. Also in order stop domain proliferation and enforce best practices, this service  is limited only to these two accounts";
	}
}
