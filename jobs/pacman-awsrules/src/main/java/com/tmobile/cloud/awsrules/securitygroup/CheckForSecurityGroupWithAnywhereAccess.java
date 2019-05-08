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
  Author :U55262,Sgorle
  Modified Date: Jun 20, 2017
  
 **/
package com.tmobile.cloud.awsrules.securitygroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-security-group-global-access", desc = "checks entirely for security group's global access", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckForSecurityGroupWithAnywhereAccess extends BaseRule {
    private static final Logger logger = LoggerFactory.getLogger(CheckForSecurityGroupWithAnywhereAccess.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     ************** Following are the Rule Parameters********* <br><br>
     * 
     * portToCheck : Value of the port<br><br>
     * 
     * ruleKey : check-for-security-group-global-access <br><br>
     * 
     * severity : Enter the value of severity <br><br>
     * 
     * ruleCategory : Enter the value of category <br><br>
     * 
     * esSgRulesUrl : Enter the SG rules ES URL <br><br>
     * 
     * cidrIp : Enter the ip as 0.0.0.0/0 <br><br>
     * 
     * cidripv6 : Enter the ip as ::/0 <br><br>
     * 
     * threadsafe : if true , rule will be executed on multiple threads <br><br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

    @SuppressWarnings("deprecation")
    public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {

        logger.debug("========CheckForSecurityGroupWithAnywhereAccess started=========");
        Annotation annotation = null;
        Set<GroupIdentifier> securityGroupsSet = new HashSet<>();
        GroupIdentifier groupIdentifier = new GroupIdentifier();
        List<GroupIdentifier> list = new ArrayList<>();
        String securityGroupId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
        String portToCheck = ruleParam.get(PacmanRuleConstants.PORT_TO_CHECK);
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String sgRulesUrl = null;
        String cidrIp = ruleParam.get(PacmanRuleConstants.CIDR_IP);
        String cidrIpv6 = ruleParam.get(PacmanRuleConstants.CIDRIPV6);
        String description = null;
        
        String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_SG_RULES_URL);
        
        if(!StringUtils.isEmpty(formattedUrl)){
            sgRulesUrl =  formattedUrl;
        }

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(cidrIpv6,portToCheck, severity, category, sgRulesUrl, cidrIp)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (!StringUtils.isEmpty(securityGroupId)) {
            groupIdentifier.setGroupId(securityGroupId);
            list.add(groupIdentifier);
            securityGroupsSet.addAll(list);

            try {
                Map<String, Boolean> sgOpen = PacmanUtils.checkAccessibleToAll(securityGroupsSet, portToCheck, sgRulesUrl, cidrIp,cidrIpv6,"");
                if (!sgOpen.isEmpty()) {
                    annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
                   
                    annotation.put(PacmanRuleConstants.SEVERITY, severity);
                    annotation.put(PacmanRuleConstants.CATEGORY, category);

					if (!portToCheck.equalsIgnoreCase("any")) {
						description = "Security Group has port : " + portToCheck + " publicly open";
						annotation.put(PacmanSdkConstants.DESCRIPTION, description);
						issue.put(PacmanRuleConstants.VIOLATION_REASON, description);
					} else {
						description = "One of the inbound rule is open to internet for this sg";
						annotation.put(PacmanSdkConstants.DESCRIPTION, description);
						issue.put(PacmanRuleConstants.VIOLATION_REASON, description);
					}
                    issue.put("cidr_ip", cidrIp);
                    issue.put("cidr_ip_v6", cidrIpv6);
                    issueList.add(issue);
                    annotation.put("issueDetails", issueList.toString());

                    logger.debug("========CheckForSecurityGroupWithAnywhereAccess ended with an annotation {} :=========", annotation);
                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                } else {
                    logger.info("Security group doesn't have any port with global access : {} ",securityGroupId);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuleExecutionFailedExeption(e.getMessage());
            }
        } else {
            logger.error("Resource Id not found!!");
            throw new RuleExecutionFailedExeption("Resource Id not found!!");
        }
        logger.debug("========CheckForSecurityGroupWithAnywhereAccess ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);

    }

    public String getHelpText() {
        return "This rule checks security group with anywhere access";
    }
}
