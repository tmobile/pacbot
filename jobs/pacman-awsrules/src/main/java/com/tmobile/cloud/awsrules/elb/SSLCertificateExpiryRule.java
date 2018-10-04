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
package com.tmobile.cloud.awsrules.elb;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-ssl_certificate-expiry", desc = "This Rule should look for the SSL expiry with given Date Range", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class SSLCertificateExpiryRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(SSLCertificateExpiryRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 **************Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-ssl_certificate-expiry <br><br>
	 *
	 * threadsafe : if true , rule will be executed on multiple threads <br><br>
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
		logger.debug("========SSLCertificateExpiryRule started=========");
		Annotation annotation = null;
		String validTo = null;
		long expiredDuration;
		long targetExpiredDuration;
		String targetExpiryDurationInString = ruleParam.get(PacmanRuleConstants.EXPIRED_DURATION);
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex
		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();

		if (!PacmanUtils.doesAllHaveValue(targetExpiryDurationInString,severity,category)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (resourceAttributes != null) {
			validTo = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.VALID_TO));

			expiredDuration = calculateSslExpiredDuration(validTo);
			targetExpiredDuration = Long.parseLong(targetExpiryDurationInString);
			if (expiredDuration > 0) {
				if (expiredDuration <= targetExpiredDuration) {
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"SSL Expiry within "+ targetExpiryDurationInString+ " days found!!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "SSL Expiry within "+ targetExpiryDurationInString+ " days found!!");
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					logger.debug("========SSLCertificateExpiryRule ended with annotation {} : =========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
				}
			} else {
				logger.info("Elb with SSL validity not expired");
			}
		}
		logger.debug("========SSLCertificateExpiryRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This Rule should look for the SSL expiry with given Date Range";
	}

	/**
	 * This method calculates the difference between the current date and the
	 * validto date It uses the TimeUnit utility for conversion purpose.
	 * 
	 * @param formattedDateString - String
	 * @return expiredDuration - Long
	 * @throws ParseException
	 */

	private Long calculateSslExpiredDuration(String formattedDateString) {
	    if(formattedDateString!=null){
		LocalDate expiryDate = LocalDateTime.parse(formattedDateString,  DateTimeFormatter.ofPattern("M/d/yyyy H:m")).toLocalDate();
		LocalDate today = LocalDateTime.now().toLocalDate();
		return java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
	    }else{
	        return 0l;
	    }
	}

}
