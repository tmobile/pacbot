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
  Purpose: Rule for checking the EC2 instances which are stopped for more than 30 days
  Author : U26405
  Modified Date: Jul 24, 2017
  
 **/
package com.tmobile.cloud.awsrules.ec2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.google.common.base.Strings;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-stopped-instance-for-long", desc = "checks entirely for stopped instance for more than 30 days", severity = PacmanSdkConstants.SEV_MEDIUM, category = PacmanSdkConstants.GOVERNANCE)
public class EC2StoppedInstanceForLongRule extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(EC2StoppedInstanceForLongRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-stopped-instance-for-long <br>
     * <br>
     * 
     *            threadsafe : if true , rule will be executed on multiple
     *            threads <br>
     * <br>
     * 
     *            targetstoppedDuration: enter stopped duration, rule creates an
     *            issue if it exceeds this duration.<br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

    public RuleResult execute(final Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========EC2StoppedInstanceForLongRule started=========");
        String targetstoppedDurationInString = ruleParam
                .get(PacmanRuleConstants.STOPPED_DURATION);
        String state = null;
        String reasonLogged = null;
        long stoppedDuration = 0L;
        long targetStoppedDuration = 0L;
        Annotation annotation = null;

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

        MDC.put("executionId", ruleParam.get("executionId")); 
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
        
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        if (!PacmanUtils.doesAllHaveValue(targetstoppedDurationInString,
                severity, category)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(
                    PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (null != resourceAttributes  && resourceAttributes.containsKey(PacmanRuleConstants.STATE_NAME)
                && resourceAttributes.get(PacmanRuleConstants.STATE_NAME)
                        .equalsIgnoreCase(PacmanRuleConstants.STOPPED_INSTANCE)) {
            String instanceId = resourceAttributes
                    .get(PacmanRuleConstants.INSTANCEID);
            if (!StringUtils.isNullOrEmpty(resourceAttributes
                    .get(PacmanRuleConstants.STATE_TRANSITION_REASON))) {
                reasonLogged = resourceAttributes
                        .get(PacmanRuleConstants.STATE_TRANSITION_REASON);
                state = resourceAttributes.get(PacmanRuleConstants.STATE_NAME);
                if (!StringUtils.isNullOrEmpty(state)
                        && state.equalsIgnoreCase(PacmanRuleConstants.STOPPED_INSTANCE)
                        && !StringUtils.isNullOrEmpty(reasonLogged)) {
                    String extractedDateString = getDate(reasonLogged);
                    stoppedDuration = calculateStoppedDuration(getFomatedDate(extractedDateString));
                    targetStoppedDuration = Long
                            .parseLong((targetstoppedDurationInString));
                    logger.debug(" Instance been stopped for {} days ",
                            stoppedDuration);
                }
            } else {
                logger.error("Unable to determine as statetransitionreason is empty ");
                throw new RuleExecutionFailedExeption("Unable to determine");
            }

            if (stoppedDuration > targetStoppedDuration) {
                annotation = Annotation.buildAnnotation(ruleParam,
                        Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,
                        "Instance been stopped for " + stoppedDuration
                                + " days"); 
                issue.put(PacmanRuleConstants.VIOLATION_REASON, "ResourceId "
                        + instanceId + "been stopped for " + stoppedDuration
                        + " days");
                issueList.add(issue);
                annotation.put("issueDetails", issueList.toString());
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.SUBTYPE,
                        Annotation.Type.RECOMMENDATION.toString());
                annotation.put(PacmanRuleConstants.CATEGORY, category);
                logger.debug(
                        "========EC2StoppedInstanceForLongRule ended with an annotation {}=========",
                        annotation);
                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                        PacmanRuleConstants.FAILURE_MESSAGE, annotation);
            }
        } else {
            logger.debug("Instance is running state : {} ",
                    ruleParam.get(PacmanSdkConstants.RESOURCE_ID));
        }

        logger.debug("========EC2StoppedInstanceForLongRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);

    }

    public String getHelpText() {
        return "This rule checks id for stopped Ec2 instance";
    }

    /**
     * The 'desc' string will be the message logged by the AWS when user stops
     * the EC2 instance. This method will search for the date string
     * (yyyy-mm-dd) in the provided input string and returns that date in the
     * String format
     * 
     * @param desc
     *            - String
     * @return match - String
     */
    private static String getDate(String desc) {
        String match = null;
        Matcher m = Pattern.compile(
                "(19|20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])")
                .matcher(desc);
        while (m.find()) {
            match = m.group();
        }
        return match;
    }

    /**
     * This method will convert the input string {yyyy-mm-dd} -> {mm/dd/yyyy}
     * 
     * @param str
     *            - String
     * @return dateString - String
     * @throws Exception
     */
    private static String formatDate(String str) {
        if (!Strings.isNullOrEmpty(str)) {
            String[] splitString = str.split("-");
            String dateString = "";
            dateString = splitString[1] + "/" + splitString[2] + "/"
                    + splitString[0];
            return dateString;
        }
        return "";
    }

    /**
     * This method calculates the difference between the current date and the
     * logged date It uses the TimeUnit utility for conversion purpose.
     * 
     * @param formattedDateString
     *            - String
     * @return stoppedDuration - Long
     * @throws ParseException
     */

    private Long calculateStoppedDuration(String formattedDateString) {
        Date stoppedDate = null;
        try {
            stoppedDate = new SimpleDateFormat("MM/dd/yyyy")
                    .parse(formattedDateString);
        } catch (ParseException e) {
            logger.error("Parse exception occured : ", e);
            throw new RuleExecutionFailedExeption(e.getMessage());
        }
        Date currentDate = new Date();

        Long diffInLongFormat = 0l;
        if (null != stoppedDate) {
            diffInLongFormat = Math.abs(currentDate.getTime()
                    - stoppedDate.getTime());
        }
        TimeUnit timeunit = TimeUnit.DAYS;
        return timeunit.convert(diffInLongFormat, TimeUnit.MILLISECONDS);
    }
    
    /**
     * This method gets the date in string format.
     * 
     * @param formattedDateString
     *            - String
     * @throws NullPointerException
     */
    
    private String getFomatedDate(String extractedDateString){
    String formattedDateString = null;
    try {
        formattedDateString = formatDate(extractedDateString);
    } catch (Exception e) {
        logger.error(e.getMessage());
        throw new RuleExecutionFailedExeption(e.getMessage());
    }
    return formattedDateString;
    }
}
