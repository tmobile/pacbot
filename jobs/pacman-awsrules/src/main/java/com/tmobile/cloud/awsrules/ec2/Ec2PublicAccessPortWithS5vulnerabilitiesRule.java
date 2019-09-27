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
package com.tmobile.cloud.awsrules.ec2;

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
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-ec2-public-access-port-with-s5-vulnerabilities", desc = "An Ec2 instance with remotely exploitable vulnerability (S5) should not be open to internet", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class Ec2PublicAccessPortWithS5vulnerabilitiesRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(Ec2PublicAccessPortWithS5vulnerabilitiesRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 * 
	 *ruleKey : check-for-ec2-public-access-port-with-s5-vulnerabilities <br><br>
	 *
	 *threadsafe : if true , rule will be executed on multiple threads <br><br>
	 *
	 *severity : Enter the value of severity <br><br>
	 * 
	 *ruleCategory : Enter the value of category <br><br>
	 *
	 *esEc2PubAccessPortUrl : Enter the EC2 Public Access for any port ES API <br><br>
	 *
	 *esEc2WithVulnInfoForS5Url : Enter the EC2 with Vuln info ES URL <br><br>
	 *
	 *severityVulnValue : Enter the severity level such as S5 <br><br>
	 *
	 *ec2PortRuleId : Enter the ruleId which is ec2 with public access for any port <br><br>
	 *
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {
		logger.debug("========Ec2PublicAccessPortWithS5vulnerabilitiesRule started=========");
		Annotation annotation = null;
		String instanceId = null;
		
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String ec2PubAccessPortUrl = null;
		String ec2WithVulnInfoForS5Url = null;
		String ec2PortRuleId = ruleParam.get(PacmanRuleConstants.EC2_PORT_RULE_ID);
		String severityVulnValue = ruleParam.get(PacmanRuleConstants.SEVERITY_VULN);
		String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
        
        if(!StringUtils.isNullOrEmpty(pacmanHost)){
            ec2WithVulnInfoForS5Url =  ruleParam.get(PacmanRuleConstants.ES_EC2_WITH_VULN_INFO_S5_URL);
            ec2PubAccessPortUrl = ruleParam.get(PacmanRuleConstants.ES_EC2_PUB_ACC_PORT_URL);
            ec2WithVulnInfoForS5Url = pacmanHost+ec2WithVulnInfoForS5Url;
            ec2PubAccessPortUrl = pacmanHost+ec2PubAccessPortUrl;
        }
		
		String publicIp=null;
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex		
		List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
		LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
		if (!PacmanUtils.doesAllHaveValue(severity,category,ec2PubAccessPortUrl,ec2WithVulnInfoForS5Url,ec2PortRuleId,severityVulnValue)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (resourceAttributes != null) {
			instanceId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.INSTANCEID));
			publicIp   = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.PUBLIC_IP_ADDR));
			boolean isInstanceExists = false;
			try {
				isInstanceExists = PacmanUtils.checkInstanceIdForPortRuleInES(instanceId,ec2PubAccessPortUrl,ec2PortRuleId,"");
				if (isInstanceExists) {
					List<String> severityList = PacmanUtils.getSeverityVulnerabilitiesByInstanceId(instanceId,ec2WithVulnInfoForS5Url,severityVulnValue);
					if(!severityList.isEmpty()){
					annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,"An Ec2 instance with remotely exploitable vulnerability (S5) is open to internet found!");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "ResourceId " + instanceId + " remotely exploitable vulnerability (S5) is open to internet found");
					if(!org.apache.commons.lang.StringUtils.isEmpty(publicIp)){
						issue.put("public_ip", publicIp);
					}else{
						issue.put("public_ip", "Not found");	
					}
					issue.put("voilation_title", String.join(",", severityList));
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					logger.debug("========Ec2PublicAccessPortWithS5vulnerabilitiesRule ended with an annotation {} : =========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
					
				}
			}
			} catch (Exception e) {
				logger.error("error", e);
				throw new RuleExecutionFailedExeption(e.getMessage());
			}
			
		}
		logger.debug("========Ec2PublicAccessPortWithS5vulnerabilitiesRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	public String getHelpText() {
		return "This rule checks for an Ec2 instance with remotely exploitable vulnerability (S5) should not be open to internet";
	}
}
