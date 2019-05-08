/*******************************************************************************
 * Copyright 2019 T Mobile, Inc. or its affiliates. All Rights Reserved.
<<<<<<< HEAD
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
=======
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
  Copyright (C) 2019 T Mobile Inc - All Rights Reserve
  Purpose: Rule for checking whether access keys been rotated after a particular duration of days
  Author : Avinash
  Modified Date: Jan 25, 2019
<<<<<<< HEAD
  
=======

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
 **/
package com.tmobile.cloud.awsrules.federated;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
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

@PacmanRule(key = "check-for-accesskeys-iamuser-federated", desc = "checks for accesskeys for IAM user from current day", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class IAMUserAccessKeyFed extends BaseRule {
<<<<<<< HEAD
	
	private static final Logger logger = LoggerFactory.getLogger(IAMUserAccessKeyFed.class);
	
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
	 * accessKeyInactivityDuration : No. of days of last used access key <br><br>
	 * 
	 * accessKeyAge : No. of days from create date of access key <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */
	
=======

	private static final Logger logger = LoggerFactory.getLogger(IAMUserAccessKeyFed.class);

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
	 * accessKeyInactivityDuration : No. of days of last used access key <br><br>
	 *
	 * accessKeyAge : No. of days from create date of access key <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========AccessKeyRotatedRule started=========");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String userName = resourceAttributes.get(PacmanSdkConstants.RESOURCE_ID);
		int accessKeyInactivityDuration = Integer.parseInt(ruleParam.get("accessKeyInactivityDuration"));
		int accessKeyAge = Integer.parseInt(ruleParam.get("accessKeyAge"));
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String accessKeyLastUsedDateString = resourceAttributes.get("lastuseddate");
<<<<<<< HEAD
		MDC.put("executionId", ruleParam.get("executionId")); 
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
=======
		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
		if (!PacmanUtils.doesAllHaveValue(severity,category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
<<<<<<< HEAD
		Annotation annotation = null;		
		String message =  null;
		boolean keyNotRotatedForLastUsed = false;
		boolean keyNotRotatedForCreateDate = false;
		
		try {
			DateTime accessKeyLastUsedDate = null; 
=======
		Annotation annotation = null;
		String message =  null;
		boolean keyNotRotatedForLastUsed = false;
		boolean keyNotRotatedForCreateDate = false;

		try {
			DateTime accessKeyLastUsedDate = null;
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
			DateTime accessKeyCreateDate = null;
			DateTime currentDate = null;
			Date lastUsedDate = null;
			try {
				Date createDate = dateFormat.parse(resourceAttributes.get("createdate"));
				if (accessKeyLastUsedDateString != null) {
					lastUsedDate = dateFormat.parse(accessKeyLastUsedDateString);
					accessKeyLastUsedDate = new DateTime(lastUsedDate);
				}
				currentDate = new DateTime();
				accessKeyCreateDate = new DateTime(createDate);
			} catch (Exception e) {
				logger.info("Exception in IAM accesskey" + e.getMessage());
			}
			//Checking for Access key last used date should not be used more than 180 days
			if (accessKeyLastUsedDate != null) {
				if (Days.daysBetween(accessKeyLastUsedDate, currentDate).getDays() > accessKeyInactivityDuration) {
					keyNotRotatedForLastUsed = true;
				}
			}
			//Checking for Access key crate date should not be used more than 360 days
			if (Days.daysBetween(accessKeyCreateDate, currentDate).getDays() > accessKeyAge) {
				keyNotRotatedForCreateDate = true;
			}
			if(keyNotRotatedForCreateDate || keyNotRotatedForLastUsed){
                message = "Iam access keys for " + userName + " are NOT ROTATED from either create date or last used date";
<<<<<<< HEAD
                logger.info(message);       
                
=======
                logger.info(message);

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
                annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,"access keys not rotated from either create date or last used date");
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.CATEGORY, category);
<<<<<<< HEAD
                
=======

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
                issue.put(PacmanRuleConstants.VIOLATION_REASON, "access keys not rotated from either create date or last used date");
                issueList.add(issue);
                annotation.put("issueDetails",issueList.toString());
                logger.debug("========AccessKeyRotatedRule ended with annotation {} :=========",annotation);
                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
            }else{
                logger.info(userName,"Access key is already rotated for username ");
            }
		} catch (Exception e) {
			logger.error("unable to get access key details", e.getMessage());
			throw new InvalidInputException(e.toString());
<<<<<<< HEAD
		}	
		
		logger.debug("========AccessKeyRotatedRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}
	
    @Override
	public String getHelpText() {		
=======
		}

		logger.debug("========AccessKeyRotatedRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

    @Override
	public String getHelpText() {
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
		return "This rule checks for accesskeys which are not rotated in past 90 days from current day";
	}

}
