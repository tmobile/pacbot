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
package com.tmobile.cloud.awsrules.lambda;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-lambda-fun-with-admin-or-IAM-privileges", desc = "Lambda functions should not have administrative permissions (Managed Policy : AdministratorAccess). Least privileges should be granted to lambda functions. Also IAM privileges should never be granted to lambda functions. (Exceptional cases has to be reviewed and prior whitelisting would be required.)", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckLambdaFunWithAdminOrIamPrivileges extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckLambdaFunWithAdminOrIamPrivileges.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 *ruleKey : check-for-lambda-fun-with-admin-or-IAM-privileges <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 *
	 *esNonAdminAccntsWithIAMFullAccessUrl : Enter the Non admin accounts with Iam Full access IAM issue's ES URL  <br><br>
	 *
	 *nonAdminAccntsWithIAMFullAccessRuleId : Enter the non admin accounts with IAM full access rule Id <br><br>
	 * 
	 *ruleCategory : Enter the value of category <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========CheckLambdaFunWithAdminOrIAMPrivileges started=========");
		Annotation annotation = null;
		String lambdaRole = null;
		boolean isRoleExists = false;
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String accountid = resourceAttributes.get(PacmanSdkConstants.ACCOUNT_ID);
		String nonAdminAccntsWithIAMFullAccessEsUrl = null;
		String nonAdminAccntsWithIAMFullAccessRuleId = ruleParam.get(PacmanRuleConstants.NON_ADMIN_ACCNT_WITH_IAM_FULL_ACC_RULE_ID);
		
        String formattedUrl = PacmanUtils.formatUrl(ruleParam,
                PacmanRuleConstants.ES_NON_ADMIN_ACCNT_WITH_IAM_FULL_ACC_URL);

        if (!StringUtils.isEmpty(formattedUrl)) {
            nonAdminAccntsWithIAMFullAccessEsUrl = formattedUrl;
        }
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(severity,category,nonAdminAccntsWithIAMFullAccessEsUrl)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (resourceAttributes.containsKey(PacmanRuleConstants.LAMBDA_ROLE)) {
			lambdaRole = resourceAttributes.get(PacmanRuleConstants.LAMBDA_ROLE);
			String role = StringUtils.substringAfterLast(lambdaRole, "/");
			
			try {
				isRoleExists = PacmanUtils.checkResourceIdForRuleInES(role,nonAdminAccntsWithIAMFullAccessEsUrl,nonAdminAccntsWithIAMFullAccessRuleId,accountid);
				if (isRoleExists) {
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"Lambda function with admin or IAM privileges found!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "Lambda function with AdministratorAccess/IAMFullAccess privileges found");
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					logger.debug("========CheckLambdaFunWithAdminOrIAMPrivileges ended with annotation {} : =========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
			} catch (Exception e) {
				logger.error("error", e);
				throw new RuleExecutionFailedExeption(e.getMessage());
			}
		}
		logger.debug("========CheckLambdaFunWithAdminOrIAMPrivileges ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks for a lambda function with admin or IAM privileges";
	}
}
