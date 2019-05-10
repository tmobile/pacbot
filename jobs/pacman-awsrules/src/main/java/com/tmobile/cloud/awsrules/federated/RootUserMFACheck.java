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
  Purpose: Rule for checking whether access keys been rotated after a particular duration of dayss
 **/
package com.tmobile.cloud.awsrules.federated;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetAccountSummaryRequest;
import com.amazonaws.services.identitymanagement.model.GetAccountSummaryResult;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-MFA-RootUser", desc = "checks for MFA for Root User", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class RootUserMFACheck extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(RootUserMFACheck.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam
	 *
	 * ************* Following are the Rule Parameters********* <br><br>
	 *
	 * ruleKey : check-for-accesskeys-iamuser-federated-for-180-and-360-days <br><br>
	 *
	 * severity : Enter the value of severity <br><br>
	 *
	 * ruleCategory : Enter the value of category <br><br>
	 *
	 * roleIdentifyingString : Configure it as role/pac_ro <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========CheckMFAforRootUser started=========");
		Annotation annotation = null;
		Map<String, String> temp = new HashMap<>();
	        temp.putAll(ruleParam);
	        temp.put("region", "us-west-2");
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String roleIdentifyingString = ruleParam
	                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);

		Map<String, Object> map = null;
        AmazonIdentityManagementClient identityManagementClient = null;

        try {
            map = getClientFor(AWSService.IAM, roleIdentifyingString, temp);
            identityManagementClient = (AmazonIdentityManagementClient) map
                    .get(PacmanSdkConstants.CLIENT);
        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.toString());
        }
        GetAccountSummaryRequest request = new GetAccountSummaryRequest();
        GetAccountSummaryResult response = identityManagementClient.getAccountSummary(request);
        Map<String, Integer> summaryMap = response.getSummaryMap();
        for(Map.Entry<String, Integer> sumMap : summaryMap.entrySet()){
        	if(sumMap.getKey().equalsIgnoreCase("AccountMFAEnabled") && sumMap.getValue() == 0){
				annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
        		return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,annotation);
        	}
        }
		logger.debug("========CheckMFAforRootUser ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);

	}

    @Override
	public String getHelpText() {
		return "This rule checks for accesskeys which are not rotated in past 90 days from current day";
	}

}