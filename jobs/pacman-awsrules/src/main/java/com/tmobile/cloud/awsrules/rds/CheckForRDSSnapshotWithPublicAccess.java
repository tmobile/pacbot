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
  Purpose: Rule for checking whether any AWS RDS accounts have public access
  Author : U26405
  Modified Date: Jul 26, 2017
  
 **/
package com.tmobile.cloud.awsrules.rds;

import java.util.ArrayList;
import java.util.HashMap;
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

@PacmanRule(key = "check-for-rds-snapshot-with-public-access", desc = "checks for rds snapshot with public access", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckForRDSSnapshotWithPublicAccess extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(CheckForRDSSnapshotWithPublicAccess.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-rds-snapshot-with-public-access <br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     *            roleIdentifyingString : Configure it as role/pac_ro <br>
     * <br>
     * 
     * esServiceURL : Enter the Es url <br><br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========CheckForRDSSnapshotWithPublicAccess started=========");
        Annotation annotation = null;
        String resourceId = null;
        String region = null;
        String accountId = null;
        String checkId = StringUtils.trim(ruleParam
                .get(PacmanRuleConstants.CHECK_ID));

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

        String serviceEsURL = null;
        
        String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_CHECK_SERVICE_SEARCH_URL_PARAM);
        
        if(!StringUtils.isEmpty(formattedUrl)){
            serviceEsURL =  formattedUrl;
        }

        MDC.put("executionId", ruleParam.get("executionId")); 
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(checkId, severity, category,
                serviceEsURL)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (resourceAttributes != null) {
            region = resourceAttributes.get(PacmanRuleConstants.REGION);
            accountId = resourceAttributes.get(PacmanSdkConstants.ACCOUNT_ID);
            resourceId = StringUtils.trim(resourceAttributes
                    .get("dbsnapshotarn"));
            Map<String, String> rdsSnapshotMap = new HashMap<>();
            try {
                rdsSnapshotMap = PacmanUtils.getRDSSnapshotWithCheckId(checkId,
                        resourceId, serviceEsURL, region, accountId);
            } catch (Exception e) {
                logger.error("unable to determine" , e);
                throw new RuleExecutionFailedExeption("unable to determine" + e);
            }
            if (!rdsSnapshotMap.isEmpty()) {
                annotation = Annotation.buildAnnotation(ruleParam,
                        Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,
                        "Amazon RDS snapshot with public access found !!");
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.CATEGORY, category);
                annotation.put("status", rdsSnapshotMap.get("status"));
                annotation.put("dbInstanceOrClusterId",
                        rdsSnapshotMap.get("dbInstanceOrClusterId"));

                issue.put(PacmanRuleConstants.VIOLATION_REASON,
                        "Amazon RDS snapshot with public access found");
                issue.put(PacmanRuleConstants.CHECKID, checkId);
                issue.put(PacmanRuleConstants.SOURCE_VERIFIED,
                        "trusted advisor");
                issueList.add(issue);

                annotation.put("issueDetails", issueList.toString());
                logger.debug("========CheckForRDSSnapshotWithPublicAccess ended with annotation {} :=========",annotation);
                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                        PacmanRuleConstants.FAILURE_MESSAGE, annotation);
            }
        }
        logger.debug("========CheckForRDSSnapshotWithPublicAccess ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "This rule checks RDS snapshot with public access ";
    }
}
