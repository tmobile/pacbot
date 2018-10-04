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
  Modified Date: Sep 8, 2017

 **/
package com.tmobile.cloud.awsrules.lambda;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.util.StringUtils;
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

@PacmanRule(key = "check-for-lambda-throttle-invocation-count", desc = "This Rule check for Lambda Throttle Invocation for the given interval, if the count exceeds target size then creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class LambdaFunctionThrottleInvocationsRule extends BaseRule {
    private static final Logger logger = LoggerFactory
            .getLogger(LambdaFunctionThrottleInvocationsRule.class);

    private static final long TWENTYFOURHOURS = 1000 * 60 * 60 * 24l;

    /**
     * The method will get triggered from Rule Engine with following parameters
     *
     * @param ruleParam
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            threshold : Value of the threshold in digits <br>
     * <br>
     *
     *            ruleKey : check-for-lambda-throttle-invocation-count <br>
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
     *            is provided by execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========LambdaFunctionThrottleInvocationsRule started=========");
        if (resourceAttributes != null
                && !StringUtils.isNullOrEmpty(resourceAttributes
                        .get(PacmanRuleConstants.FUNCTION_NAME_RES_ATTR))) {
            String resourceId = resourceAttributes
                    .get(PacmanRuleConstants.FUNCTION_NAME_RES_ATTR);
            String roleIdentifyingString = ruleParam
                    .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
            AmazonCloudWatchClient cloudWatchClient = null;
            Annotation annotation = null;
            int invocationSum = 0;
            int targetThreshold = Integer.parseInt(ruleParam
                    .get(PacmanRuleConstants.THRESHOLD));

            String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
            String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

            MDC.put("executionId", ruleParam.get("executionId"));
            MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

            List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
            LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

            if (!PacmanUtils.doesAllHaveValue(String.valueOf(targetThreshold),
                    severity, category)) {
                logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
                throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
            }

            try {
                // Create Cloud watch client
                cloudWatchClient = getClient(roleIdentifyingString, ruleParam);
                // Create GetMetricStatisticsRequest
                final GetMetricStatisticsRequest request = request(resourceId);

                final GetMetricStatisticsResult result = cloudWatchClient
                        .getMetricStatistics(request);

                for (final Datapoint dataPoint : result.getDatapoints()) {

                    invocationSum = invocationSum
                            + dataPoint.getSum().intValue();

                }
                if (invocationSum > targetThreshold) {
                    annotation = Annotation.buildAnnotation(ruleParam,
                            Annotation.Type.ISSUE);
                    annotation
                            .put(PacmanSdkConstants.DESCRIPTION,
                                    "Lambda function throttle invocation count exceeded target threshold");
                    annotation.put(PacmanRuleConstants.LAMBDA_INVOCATION_COUNT,
                            ":" + invocationSum);
                    annotation.put(PacmanRuleConstants.SEVERITY, severity);
                    annotation.put(PacmanRuleConstants.CATEGORY, category);

                    issue.put(PacmanRuleConstants.VIOLATION_REASON,
                            "Lambda function throttle invocation count exceeded target threshold");
                    issue.put(PacmanRuleConstants.THRESHOLD, targetThreshold);
                    issue.put("invocation_sum", invocationSum);
                    issue.put(PacmanRuleConstants.TIME_PERIOD_HRS,
                            TWENTYFOURHOURS);
                    issueList.add(issue);
                    annotation.put("issueDetails", issueList.toString());
                    logger.debug("========LambdaFunctionThrottleInvocationsRule ended with an annotation {} :=========",annotation);
                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                            PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                }

            } catch (InvalidInputException e) {
                logger.error(e.toString());
                throw new InvalidInputException(e.getMessage());
            }
        }
        logger.debug("========LambdaFunctionThrottleInvocationsRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    private static GetMetricStatisticsRequest request(
            final String funcationValue) {
        final int fiveMin = 60 * 60 * 24;
        return new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - TWENTYFOURHOURS))
                .withNamespace(PacmanRuleConstants.AWS_LAMBDA)
                .withPeriod(fiveMin)
                .withDimensions(
                        new Dimension().withName(
                                PacmanRuleConstants.FUNCTION_NAME).withValue(
                                funcationValue))
                .withMetricName(PacmanRuleConstants.THROTTLES)
                .withStatistics(PacmanRuleConstants.SUM)
                .withEndTime(new Date());
    }

    @Override
    public String getHelpText() {
        return "This Rule check for Lambda throttle Invocation for the given interval, if the count exceeds target size then creates an issue";
    }

    private AmazonCloudWatchClient getClient(String roleIdentifyingString,
            Map<String, String> ruleParam) {
        Map<String, Object> map = null;
        AmazonCloudWatchClient cloudWatchClient = null;
        try {
            map = getClientFor(AWSService.CLOUDWATCH, roleIdentifyingString,
                    ruleParam);
            cloudWatchClient = (AmazonCloudWatchClient) map
                    .get(PacmanSdkConstants.CLIENT);
        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.getMessage());
        }
        return cloudWatchClient;

    }
}
