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
  Purpose: Rule for checking whether access keys been rotated after a particular duration of days
  Author : U26405
  Modified Date: Jul 28, 2017
  
 **/
package com.tmobile.cloud.awsrules.iam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.StatusType;
import com.amazonaws.util.CollectionUtils;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
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

@PacmanRule(key = "check-for-accesskeys-rotated-in-every-90-days", desc = "checks for accesskeys which are not rotated in past 90 days from current day", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class AccessKeyRotatedRule extends BaseRule {
	
	private static final Logger logger = LoggerFactory.getLogger(AccessKeyRotatedRule.class);
	
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * @param ruleParam 
	 * 
	 * ************* Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-accesskeys-rotated-in-every-90-days <br><br>
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
	
	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========AccessKeyRotatedRule started=========");
		Map<String, String> temp = new HashMap<>();
		temp.putAll(ruleParam);
		temp.put("region", "us-west-2");
		
		Map<String, Object> map = null;
		AmazonIdentityManagementClient iamClient = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String userName = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		
		if (!PacmanUtils.doesAllHaveValue(severity,category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		Annotation annotation = null;		
		List<AccessKeyMetadata> accessKeyMetadatas;
		String message =  null;
		try {
			map = getClientFor(AWSService.IAM, roleIdentifyingString, temp);
			iamClient = (AmazonIdentityManagementClient) map.get(PacmanSdkConstants.CLIENT);
			
			 accessKeyMetadatas = IAMUtils.getAccessKeyInformationForUser(userName, iamClient);
		        if(!CollectionUtils.isNullOrEmpty(accessKeyMetadatas)){
		            if(anyAccessKeysNotRotatedForLong(accessKeyMetadatas)){
		                
		                message = "Iam access keys for " + userName + " are NOT ROTATED for more than "+PacmanRuleConstants.ACCESSKEY_ROTATION_DURATION+" days";
		                logger.info(message);       
		                
		                annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
		                annotation.put(PacmanSdkConstants.DESCRIPTION,"access keys not rotated for more than " +PacmanRuleConstants.ACCESSKEY_ROTATION_DURATION+" days found");
		                annotation.put(PacmanRuleConstants.SEVERITY, severity);
		                annotation.put(PacmanRuleConstants.CATEGORY, category);
		                
		                issue.put(PacmanRuleConstants.VIOLATION_REASON, "access keys not rotated for more than " +PacmanRuleConstants.ACCESSKEY_ROTATION_DURATION+" days found");
		                issueList.add(issue);
		                annotation.put("issueDetails",issueList.toString());
		                logger.debug("========AccessKeyRotatedRule ended with annotation {} :=========",annotation);
		                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
		            }else{
		                logger.info(userName,"Access key is already rotated for username ");
		            }
		        } else{
		            logger.info(userName,"Access key metadata is empty for username ");
		        }
		} catch (UnableToCreateClientException e) {
			logger.error("unable to get client for following input", e);
			throw new InvalidInputException(e.toString());
		}	
		
		logger.debug("========AccessKeyRotatedRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}
	
    @Override
	public String getHelpText() {		
		return "This rule checks for accesskeys which are not rotated in past 90 days from current day";
	}
	
	/**
	 * This utility method is for calculating the duration between last rotation and current date
	 * Returns true, if rotation exceeds 90 days. 
	 * Returns false otherwise.
	 * 
	 * @param accessKeyMetadatas
	 * @return boolean
	 */
	private boolean anyAccessKeysNotRotatedForLong(List<AccessKeyMetadata> accessKeyMetadatas) {
		Boolean keyNotRotated = Boolean.FALSE;
    	for(AccessKeyMetadata accessKeyMetadata : accessKeyMetadatas){
    		//Skip the inactive keys
    		if(accessKeyMetadata.getStatus().equals(StatusType.Inactive.toString())){ 
    			continue;
    		}
    		
    		Date keyCreationDate = accessKeyMetadata.getCreateDate();
    		DateTime creationDate = new DateTime(keyCreationDate);
    		DateTime currentDate = new DateTime();
    		if(Days.daysBetween(creationDate, currentDate).getDays() > PacmanRuleConstants.ACCESSKEY_ROTATION_DURATION){
    			keyNotRotated = Boolean.TRUE;
    		}
		}
		return keyNotRotated;
	}

}
