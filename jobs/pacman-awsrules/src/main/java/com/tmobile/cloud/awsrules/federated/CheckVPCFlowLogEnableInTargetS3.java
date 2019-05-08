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
  Author :Anukriti
  Modified Date: Feb 21, 2019

 **/
package com.tmobile.cloud.awsrules.federated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@PacmanRule(key = "check-VPCFlowLogEnable-in-targetS3", desc = "This rule checks for AWS CloudTrail multi region enabled", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.SECURITY)
public class CheckVPCFlowLogEnableInTargetS3 extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(CheckVPCFlowLogEnableInTargetS3.class);

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
     *            targetTypeARN : ARN of the bucket <br>
     *
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {
        logger.debug("========CheckVPCFlowLogEnableInTargetS3 started=========");
        Map<String, Object> map = null;
        boolean compliance=false;
        Annotation annotation=null;
        AmazonEC2 ec2Client = null;
        String roleIdentifyingString = ruleParam
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String targetTypeARN = ruleParam.get("targetTypeARN");
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String entityId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
        String loggingTags = resourceAttributes.get("tags.logging");
        List<String> sourcesverified = new ArrayList<>();
        LinkedHashMap<String, Object> accessLevels = new LinkedHashMap<>();
        sourcesverified.add("HTTP Get-From Public IP");
        accessLevels.put("HTTP Get-From Public IP", PacmanRuleConstants.PUBLIC);
        if (!PacmanUtils.doesAllHaveValue(severity, category)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }
		if (loggingTags == null || loggingTags.equalsIgnoreCase("true")) 
		{
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
			List<FlowLog> flowLogs = PacmanEc2Utils.getFlowLogs(ec2Client, describeFlowLogsRequest);
			
			List<LinkedHashMap<String,Object>>issueList = new ArrayList<>();
			LinkedHashMap<String,Object>issue = new LinkedHashMap<>();
			annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
			
			logger.debug("========CheckMFAforPowerUserGroup ended=========");
			if (CollectionUtils.isNullOrEmpty(flowLogs)) {
				logger.info("flow logs empty");
				annotation.put(PacmanSdkConstants.DESCRIPTION,"flowlog is empty");
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				
				issue.put(PacmanRuleConstants.VIOLATION_REASON, "flowlog is empty ");
				issueList.add(issue);
				annotation.put("issueDetails",issueList.toString());
				
				logger.debug("========VPC_flow_log_enabled_in_S3_vpc ended with an annotation : {}=========",annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
				
			} else {
				for (FlowLog flowlog : flowLogs) {
					String logDest = flowlog.getLogDestination();
					String logDestType = flowlog.getLogDestinationType();
					logger.info("Dest:::" + logDest);
					logger.info("Dest Type:::" + logDestType);

					if (logDestType.equals("s3") && logDest.equalsIgnoreCase(targetTypeARN)) {
						return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
					}
				}
				
				if (!compliance) {
					logger.info("destination or type mismatch");
					
					annotation.put(PacmanSdkConstants.DESCRIPTION,"flowlog is not enabled for centralized s3 bucket");
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					
					issue.put(PacmanRuleConstants.VIOLATION_REASON, "flowlog is not enabled for centralized s3 bucket ");
					issueList.add(issue);
					annotation.put("issueDetails",issueList.toString());
					
					logger.debug("========VPC_flow_log_enabled_in_S3_vpc ended with an annotation : {}=========",annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
					
				}

			}
		} 				
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
    }
       
	@Override
	public String getHelpText() {
		// TODO Auto-generated method stub
		return null;
	}
        
	


}
