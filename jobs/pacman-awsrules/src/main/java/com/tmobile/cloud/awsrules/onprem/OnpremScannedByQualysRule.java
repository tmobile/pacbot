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
package com.tmobile.cloud.awsrules.onprem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-onprem-scanned-by-qualys", desc = "checks for onprem scanned by qualys,if not found then its an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class OnpremScannedByQualysRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(OnpremScannedByQualysRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-onprem-scanned-by-qualys <br><br>
	 *
	 * severity : Enter the value of severity <br><br>
	 * 
	 * esQualysUrl : Enter the Qualys URL esQualysUrl
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 *
	 *threadsafe : if true , rule will be executed on multiple threads <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========OnpremScannedByQualysRule started=========");
		Annotation annotation = null;
		String resourceid = null;
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String target = ruleParam.get(PacmanRuleConstants.TARGET);
		
		String qualysApi =  null;
		
		String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_QUALYS_URL);
        
        if(!StringUtils.isNullOrEmpty(formattedUrl)){
            qualysApi =  formattedUrl;
        }
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex	
		
		List<LinkedHashMap<String,Object>> issueList = new ArrayList<>();
		LinkedHashMap<String,Object> issue = new LinkedHashMap<>();
		Gson gson = new Gson();
		
		if (!PacmanUtils.doesAllHaveValue(severity,category,qualysApi,target)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (resourceAttributes != null) {
		    resourceid = StringUtils.trim(resourceAttributes.get("_resourceid"));
				Map<String,Object> onpremScannesByQualysMap = new HashMap<>();
				try {
					onpremScannesByQualysMap = PacmanUtils.checkInstanceIdFromElasticSearchForQualys(resourceid, qualysApi, "_resourceid",target);
				} catch (Exception e) {
					logger.error("unable to determine" , e);
					throw new RuleExecutionFailedExeption("unable to determine" + e);
				}
				if (!onpremScannesByQualysMap.isEmpty()) {
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"Onprem not scanned  by qualys found!!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "Onprem asset not scanned by qualys found");
					
					issue.put(PacmanRuleConstants.SOURCE_VERIFIED, "_resourceid,"+PacmanRuleConstants.LAST_VULN_SCAN);
					issue.put(PacmanRuleConstants.FAILED_REASON_QUALYS, gson.toJson(onpremScannesByQualysMap) );
					issueList.add(issue);
					annotation.put("issueDetails", issueList.toString());
					logger.debug("========OnpremScannedByQualysRule ended with annotation {} : =========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
				}
		}
		
		logger.debug("========OnpremScannedByQualysRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks for Onprem scanned by qualys,if not found then its an issue";
	}
}
