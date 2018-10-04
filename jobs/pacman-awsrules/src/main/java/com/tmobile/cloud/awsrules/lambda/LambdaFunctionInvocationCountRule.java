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

@PacmanRule(key = "check-for-lambda-invocation-count", desc = "This Rule check for Lambda Invocation for the given interval, if the count exceeds target size then creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class LambdaFunctionInvocationCountRule extends BaseRule {
    private static final Logger logger = LoggerFactory
            .getLogger(LambdaFunctionInvocationCountRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     *
     * @param ruleParam
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            timePeriodInHours : Number of hours <br>
     * <br>
     *
     *            threshold : Value of the threshold in digits <br>
     * <br>
     *
     *            ruleKey : check-for-lambda-invocation-count <br>
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

        logger.debug("========LambdaFunctionInvocationCountRule started=========");

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String timeperiod = ruleParam
                .get(PacmanRuleConstants.TIME_PERIOD);
        String targetThresholdStr = ruleParam
                .get(PacmanRuleConstants.THRESHOLD);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(severity, category,timeperiod,
                targetThresholdStr)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (resourceAttributes != null && !StringUtils.isNullOrEmpty(resourceAttributes
                .get(PacmanRuleConstants.FUNCTION_NAME_RES_ATTR))) {
                String resourceId = resourceAttributes
                        .get(PacmanRuleConstants.FUNCTION_NAME_RES_ATTR);
                String roleIdentifyingString = ruleParam
                        .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);

                int targetThreshold = Integer.parseInt(targetThresholdStr);

                int noOfHours = Integer.parseInt(timeperiod);
                AmazonCloudWatchClient cloudWatchClient = null;
                Annotation annotation = null;
                int invocationSum = 0;

                try {
                    // Create Cloud watch client
                    cloudWatchClient = getClient(roleIdentifyingString, ruleParam);
                    // Create GetMetricStatisticsRequest
                    final GetMetricStatisticsRequest request = request(
                            resourceId, noOfHours);

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
                                        "Lambda function invocation count exceeded target threshold");
                        annotation.put(
                                PacmanRuleConstants.LAMBDA_INVOCATION_COUNT,String.valueOf(invocationSum));
                        annotation.put(
                                PacmanRuleConstants.LAMBDA_FUNCTION_NAME,
                                resourceId);
                        annotation.put(PacmanRuleConstants.SEVERITY, severity);
                        annotation.put(PacmanRuleConstants.CATEGORY, category);

                        issue.put(PacmanRuleConstants.VIOLATION_REASON,
                                "Lambda function invocation count exceeded target threshold");
                        issue.put(PacmanRuleConstants.THRESHOLD,
                                targetThreshold);
                        issue.put("invocation_sum", invocationSum);
                        issue.put(PacmanRuleConstants.TIME_PERIOD_HRS,
                                noOfHours);
                        issueList.add(issue);
                        annotation.put("issueDetails", issueList.toString());
                        logger.debug("========LambdaFunctionInvocationCountRule ended with an annotation {} : =========",annotation);
                        return new RuleResult(
                                PacmanSdkConstants.STATUS_FAILURE,
                                PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                    }

                } catch (InvalidInputException e) {
                    logger.error(e.getMessage());
                    throw new InvalidInputException(e.getMessage());
                }
        }
        logger.debug("========LambdaFunctionInvocationCountRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    private static GetMetricStatisticsRequest request(
            final String funcationValue, int hours) {
        final long twentyFourHrs = 1000 * 60 * 60l * hours;
        final int oneHour = 60 * 60 * hours;
        return new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - twentyFourHrs))
                .withNamespace(PacmanRuleConstants.AWS_LAMBDA)
                .withPeriod(oneHour)
                .withDimensions(
                        new Dimension().withName("FunctionName").withValue(
                                funcationValue))
                .withMetricName(PacmanRuleConstants.INVOCATIONS)
                .withStatistics("Sum").withEndTime(new Date());
    }

    @Override
    public String getHelpText() {
        return "This Rule check for Lambda Invocation for the given interval, if the count exceeds target size then creates an issue";
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
