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
  Author :santoshi
  Modified Date: Jul 21, 2017

 **/
package com.tmobile.cloud.awsrules.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.cloudtrail.AWSCloudTrailClient;
import com.amazonaws.services.cloudtrail.model.DescribeTrailsResult;
import com.amazonaws.services.cloudtrail.model.Trail;
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

@PacmanRule(key = "check-for-aws-cloudtrail-config", desc = "This rule checks for AWS CloudTrail Config is enabled for given account & region", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.SECURITY)
public class CheckAWSCloudTrailConfig extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(CheckAWSCloudTrailConfig.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     *
     * @param ruleParam
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            ruleKey : check-for-aws-cloudtrail-config <br>
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
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {
        logger.debug("========CheckAWSCloudTrailConfig started=========");
        Map<String, Object> map = null;
        AWSCloudTrailClient cloudTrailClient = null;
        Annotation annotation = null;
        String cloudtrailFlg = null;
        String roleIdentifyingString = ruleParam
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(severity, category,roleIdentifyingString)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        try {
            map = getClientFor(AWSService.CLOUDTRL, roleIdentifyingString,
                    ruleParam);
            cloudTrailClient = (AWSCloudTrailClient) map
                    .get(PacmanSdkConstants.CLIENT);

        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.toString());
        }

        DescribeTrailsResult result = cloudTrailClient.describeTrails();
        List<Trail> trailList = result.getTrailList();

        for (Trail trail : trailList) {

            if (trail.getIsMultiRegionTrail()) {
                cloudtrailFlg = "X";// set flag when CloudTrail is enabled all
                                    // regions
                break;
            }
        }

        if (cloudtrailFlg == null) {
            annotation = Annotation.buildAnnotation(ruleParam,
                    Annotation.Type.ISSUE);
            annotation.put(PacmanSdkConstants.DESCRIPTION,
                    "AWS CloudTrail Config not enabled");
            annotation.put(PacmanRuleConstants.SEVERITY, severity);
            annotation.put(PacmanRuleConstants.CATEGORY, category);

            issue.put(PacmanRuleConstants.VIOLATION_REASON,
                    "AWS CloudTrail Config not enabled");
            issueList.add(issue);
            annotation.put("issueDetails", issueList.toString());
            logger.debug("========CheckAWSCloudTrailConfig ended with annotation {} : =========",annotation);
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                    PacmanRuleConstants.FAILURE_MESSAGE, annotation);
        }
        logger.debug("========CheckAWSCloudTrailConfig ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);

    }

    @Override
    public String getHelpText() {
        return "This rule checks for AWS CloudTrail Config is enabled for given account & region";
    }

}
