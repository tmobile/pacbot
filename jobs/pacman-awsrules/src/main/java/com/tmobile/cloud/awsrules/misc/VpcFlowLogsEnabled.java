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
  Author :U55262
  Modified Date: Jun 20, 2017
  
 **/
package com.tmobile.cloud.awsrules.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeFlowLogsRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.FlowLog;
import com.amazonaws.util.CollectionUtils;
import com.tmobile.cloud.awsrules.utils.PacmanEc2Utils;
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

@PacmanRule(key = "check-for-vpc-flowlog-enabled", desc = "checks VPC flow log enabled for a given VPC id,account & region ", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.GOVERNANCE)
public class VpcFlowLogsEnabled extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(VpcFlowLogsEnabled.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-vpc-flowlog-enabled <br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     *            roleIdentifyingString : Configure it as role/pacbot_ro <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    public RuleResult execute(final Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========VpcFlowLogsEnabled started=========");
        Map<String, Object> map = null;
        AmazonEC2 ec2Client = null;
        String roleIdentifyingString = ruleParam
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String entityId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);

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

        Annotation annotation = null;
        try {
            map = getClientFor(AWSService.EC2, roleIdentifyingString, ruleParam);
            ec2Client = (AmazonEC2) map.get(PacmanSdkConstants.CLIENT);
        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.toString());
        }
        DescribeFlowLogsRequest describeFlowLogsRequest = new DescribeFlowLogsRequest();
        Filter filter = new Filter();
        filter.setName(PacmanRuleConstants.RESOURCE_NAME);
        filter.setValues(Arrays.asList(entityId));
        describeFlowLogsRequest.setFilter(Arrays.asList(filter));
        List<FlowLog> flowLogs = PacmanEc2Utils.getFlowLogs(ec2Client,
                describeFlowLogsRequest);

        if (CollectionUtils.isNullOrEmpty(flowLogs)) {
            annotation = Annotation.buildAnnotation(ruleParam,
                    Annotation.Type.ISSUE);

            annotation.put(PacmanSdkConstants.DESCRIPTION,
                    "VPC Flow log not enabled");
            annotation.put(PacmanRuleConstants.SEVERITY, severity);
            annotation.put(PacmanRuleConstants.CATEGORY, category);

            issue.put(PacmanRuleConstants.VIOLATION_REASON,
                    "VPC Flow log not enabled");
            issueList.add(issue);
            annotation.put("issueDetails", issueList.toString());
            logger.debug("========VpcFlowLogsEnabled ended with annotation {} : =========",annotation);
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                    PacmanRuleConstants.FAILURE_MESSAGE, annotation);

        } else {
            logger.info(entityId,"Flowlog enabled for the VPC");
        }
        logger.debug("========VpcFlowLogsEnabled ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    public String getHelpText() {
        return "This rules checks entirely for VPC flowlog enabled for a given region & account ";
    }
}
