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
  Modified Date: Oct 24, 2017
  
 **/
package com.tmobile.cloud.awsrules.iam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

@PacmanRule(key = "check-for-inactive-iam-users", desc = "This rule should look for inactive IAM users", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckInactiveIamUsersRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(CheckInactiveIamUsersRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 **************Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-inactive-iam-users <br><br>
	 *
	 * targetExpireDuration : specify the expiry duration in numbers <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========CheckInactiveIamUsersRule started=========");
		Annotation annotation = null;
		String passwordlastused = null;
		long inActiveDuration;
		long pwdInactiveDuration;
		String pwdInactiveDurationInString = ruleParam.get(PacmanRuleConstants.PD_INACTIVE_DURATION);
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>> issueList = new ArrayList<>();
		LinkedHashMap<String,Object> issue = new LinkedHashMap<>();

		if (!PacmanUtils.doesAllHaveValue(pwdInactiveDurationInString,severity,category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (resourceAttributes != null) {
			passwordlastused = resourceAttributes.get(PacmanRuleConstants.PD_LAST_USED);
			if(!StringUtils.isEmpty(passwordlastused)){
			inActiveDuration = calculateInactiveDuration(passwordlastused);
			pwdInactiveDuration = Long.parseLong(pwdInactiveDurationInString);
			if (inActiveDuration > 0) {
				if (inActiveDuration >= pwdInactiveDuration) {
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"Iam user is inactive since "+ inActiveDuration+ " days!!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "Iam user is inactive since "+ inActiveDuration+ " days!!");
					issue.put(passwordlastused, passwordlastused);
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					logger.debug("========CheckInactiveIamUsersRule ended with annotation {} :=========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
				}
			} else {
				logger.info("Iam user is active");
			}
			}
		}
		logger.debug("========CheckInactiveIamUsersRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule should look for inactive IAM users";
	}

	/**
	 * This method calculates the difference between the current date and the
	 * passwordlastused date It uses the TimeUnit utility for conversion purpose.
	 * 
	 * @param passwordlastused - String
	 * @return inactiveDuration - Long
	 * @throws ParseException
	 */

	private Long calculateInactiveDuration(String passwordlastused) {
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat dateFormat1= new SimpleDateFormat("M/d/yyyy H:m");
		Date passwordlastUsedDate = null;
		String passwordlastusedStr = null;
		try {
			passwordlastUsedDate = dateFormat.parse(passwordlastused);
			passwordlastusedStr= dateFormat1.format(passwordlastUsedDate);
		} catch (ParseException e) {
			logger.error("Parse exception occured : ", e);
			throw new RuleExecutionFailedExeption("Parse exception occured : " + e);
		}
		LocalDate expiryDate = LocalDateTime.parse(passwordlastusedStr,  DateTimeFormatter.ofPattern("M/d/yyyy H:m")).toLocalDate();
		LocalDate today = LocalDateTime.now().toLocalDate();
		return java.time.temporal.ChronoUnit.DAYS.between(expiryDate, today);

		
	}
}
