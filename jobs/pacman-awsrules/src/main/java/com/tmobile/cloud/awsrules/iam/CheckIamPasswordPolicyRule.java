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

package com.tmobile.cloud.awsrules.iam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetAccountPasswordPolicyResult;
import com.amazonaws.services.identitymanagement.model.PasswordPolicy;
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

@PacmanRule(key = "check-iam-password-policy", desc = "checks for Password Policy not compliant", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckIamPasswordPolicyRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckIamPasswordPolicyRule.class);
	private StringBuilder policyIssues;
	
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam 
	 * 
	 * ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-iam-password-policy <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * roleIdentifyingString : Configure it as role/pacbot_ro <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	@Override
	public RuleResult execute(Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========CheckIamPasswordPolicyRule started=========");
		Map<String, String> temp = new HashMap<>();
		temp.putAll(ruleParam);
		temp.put("region", "us-west-2");

		Map<String, Object> map = null;
		Annotation annotation = null;
		AmazonIdentityManagementClient iamClient = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		
		logger.info(resourceAttributes.get("accountid"));
		logger.info(resourceAttributes.get("accountname"));

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();

		if (!PacmanUtils.doesAllHaveValue(severity, category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		try {
			map = getClientFor(AWSService.IAM, roleIdentifyingString, temp);
			iamClient = (AmazonIdentityManagementClient) map.get(PacmanSdkConstants.CLIENT);
		} catch (UnableToCreateClientException e) {
			logger.error("unable to get client for following input", e);
			throw new InvalidInputException(e.toString());
		}

		GetAccountPasswordPolicyResult result = iamClient.getAccountPasswordPolicy();
		PasswordPolicy passwordPolicy = result.getPasswordPolicy();

		if (null != passwordPolicy) {
			if (!isPasswordPolicyCompliant(passwordPolicy, ruleParam)) {

				logger.warn("Password Policy not compliant");
				annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION,policyIssues.toString().trim());
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				
				issue.put(PacmanRuleConstants.VIOLATION_REASON, policyIssues.toString().trim());
				issueList.add(issue);
				annotation.put("issueDetails",issueList.toString());
				logger.debug("========CheckIamPasswordPolicyRule ended with an annotation {} :=========",annotation); 
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);

			} else {
				logger.info("Password policy ok");
			}
		} else {
			logger.warn("No password policy defined for the account");
		}
		logger.debug("========CheckIamPasswordPolicyRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	private boolean isPasswordPolicyCompliant(PasswordPolicy passwordPolicy,Map<String, String> ruleParam) {
		String requireSymbols = ruleParam.get("requireSymbols");
		Boolean isRequireSymbols = Boolean.parseBoolean(requireSymbols);
		String requireNumbers = ruleParam.get("requireNumbers");
		Boolean isRequireNumbers = Boolean.parseBoolean(requireNumbers);
		String requireUppercaseCharacters = ruleParam.get("requireUppercaseCharacters");
		Boolean isRequireUppercaseCharacters = Boolean.parseBoolean(requireUppercaseCharacters);
		String requireLowercaseCharacters = ruleParam.get("requireLowercaseCharacters");
		Boolean isRequireLowercaseCharacters = Boolean.parseBoolean(requireLowercaseCharacters);
		String allowUsersToChangePassword = ruleParam.get("allowUsersToChangePassword");
		Boolean isAllowUsersToChangePassword = Boolean.parseBoolean(allowUsersToChangePassword);
		String expirePasswords = ruleParam.get("expirePasswords");
		Boolean isExpirePasswords = Boolean.parseBoolean(expirePasswords);
		String hardExpiry = ruleParam.get("hardExpiry"); 
		Boolean isHardExpiry = Boolean.parseBoolean(hardExpiry);

		policyIssues = new StringBuilder();
		Boolean complianceStatus = Boolean.TRUE;
		if (passwordPolicy.getMaxPasswordAge()==null || passwordPolicy.getMaxPasswordAge() > Integer.parseInt(ruleParam.get("maxPasswordAge"))) {
			policyIssues.append("The standard max password age is "+ruleParam.get("maxPasswordAge")+" days but the found password age is "+passwordPolicy.getMaxPasswordAge()+" days\n");
			complianceStatus = Boolean.FALSE;
		}
		
		if (passwordPolicy.getMinimumPasswordLength()==null || passwordPolicy.getMinimumPasswordLength() != Integer.parseInt(ruleParam.get("minPasswordLength"))) {
			policyIssues.append("Min password length do not matched the standards \n");
			complianceStatus = Boolean.FALSE;
		}

		
		if (passwordPolicy.getPasswordReusePrevention()==null || passwordPolicy.getPasswordReusePrevention() != Integer.parseInt(ruleParam.get("lastPasswordsToRemember"))) {
			policyIssues.append("Password reuse prevention number do not matched the standards \n");
			complianceStatus = Boolean.FALSE;
		}

		if (passwordPolicy.getAllowUsersToChangePassword()==null || !passwordPolicy.getAllowUsersToChangePassword().equals(isAllowUsersToChangePassword)) {
			policyIssues.append("Allow users to change passwords do not matched the standards \n");
			complianceStatus = Boolean.FALSE;
		}

		if (passwordPolicy.getHardExpiry()==null || !passwordPolicy.getHardExpiry().equals(isHardExpiry)) {
			policyIssues.append("Hard expiry do not matched the standards \n");
			complianceStatus = Boolean.FALSE;
		}

		if (passwordPolicy.getRequireLowercaseCharacters()==null || !passwordPolicy.getRequireLowercaseCharacters().equals(isRequireLowercaseCharacters)) {
			policyIssues.append("Require at least one lowercase letter \n");
			complianceStatus = Boolean.FALSE;
		}
		
		if (passwordPolicy.getRequireUppercaseCharacters()==null || !passwordPolicy.getRequireUppercaseCharacters().equals(isRequireUppercaseCharacters)) {
			policyIssues.append("Require at least one uppercase letter \n");
			complianceStatus = Boolean.FALSE;
		}

		if (passwordPolicy.getExpirePasswords()==null || !passwordPolicy.getExpirePasswords().equals(isExpirePasswords)) {
			policyIssues.append("Expire passwords do not matched the standards \n");
			complianceStatus = Boolean.FALSE;
		}

		if (passwordPolicy.getRequireSymbols()==null || !passwordPolicy.getRequireSymbols().equals(isRequireSymbols)) {
			policyIssues.append("Require at least one non-alpanumeric character \n");
			complianceStatus = Boolean.FALSE;
		}
		
		if (passwordPolicy.getRequireNumbers()==null || !passwordPolicy.getRequireNumbers().equals(isRequireNumbers)) {
			policyIssues.append("Require at least one number \n");
			complianceStatus = Boolean.FALSE;
		}

		return complianceStatus;
	}

	@Override
	public String getHelpText() {
		return "Checks for Password Policy not compliant";
	}

}
