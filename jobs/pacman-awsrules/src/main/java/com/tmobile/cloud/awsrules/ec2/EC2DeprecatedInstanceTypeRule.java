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
  Purpose: Checks for the deprecated instance type which are passed as rule params
  Author :kkumar
  Modified Date: Jun 20, 2017

 **/
package com.tmobile.cloud.awsrules.ec2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-deprecated-instance-type", desc = "checks entirely for deprecated EC2 instance types", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class EC2DeprecatedInstanceTypeRule extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(EC2DeprecatedInstanceTypeRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     *
     * @param ruleParam
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            deprecatedInstanceType : Comma separated list of deprecated
     *            AWS instance types <br>
     * <br>
     *
     *            splitterChar : The splitter character used to split the
     *            deprecatedInstanceType <br>
     * <br>
     *
     *            ruleKey : check-for-deprecated-instance-type <br>
     * <br>
     *
     *            severity : Enter the value of severity <br>
     * <br>
     *
     *            ruleCategory : Enter the value of category <br>
     * <br>
     *
     *            threadsafe : if true , rule will be executed on multiple
     *            threads <br>
     * <br>
     *
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */
    public RuleResult execute(final Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {
        logger.debug("========EC2DeprecatedInstanceTypeRule started=========");
        if (resourceAttributes.get("statename") != null
                && "running".equals(resourceAttributes.get("statename"))) {
            Annotation annotation = null;

            String entityId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
            String deprecatedInstanceType = ruleParam
                    .get(PacmanRuleConstants.DEPRECATED_INSTANCE_TYPE);
            String tagsSplitter = ruleParam
                    .get(PacmanSdkConstants.SPLITTER_CHAR);

            String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
            String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

            MDC.put("executionId", ruleParam.get("executionId"));
            MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

            List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
            LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

            if (!PacmanUtils.doesAllHaveValue(deprecatedInstanceType,
                    tagsSplitter, severity, category)) {
                logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
                throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
            }
            String instanceType = resourceAttributes
                    .get(PacmanRuleConstants.INSTANCE_TYPE);
            List<String> deprecatedInstanceTypeList = PacmanUtils
                    .splitStringToAList(deprecatedInstanceType, tagsSplitter);

            if (isInstanceDepricated(instanceType, deprecatedInstanceTypeList)) {
                annotation = Annotation.buildAnnotation(ruleParam,
                        Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,
                        "Deprecated instance type " + instanceType
                                + " found !!");
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.CATEGORY, category);

                issue.put(PacmanRuleConstants.VIOLATION_REASON,
                        "Deprecated instance type " + instanceType
                                + " found !!");
                issue.put(PacmanRuleConstants.DEPRECATED_INSTANCE_TYPES,
                        String.join(",", deprecatedInstanceTypeList));
                issueList.add(issue);
                annotation.put("issueDetails", issueList.toString());

                logger.debug("========EC2DeprecatedInstanceTypeRule ended with annotation {} :=========",annotation);
                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                        PacmanRuleConstants.FAILURE_MESSAGE, annotation);
            } else {
                logger.debug(entityId ,":not a deprecated instance");
            }

        }
        logger.debug("========EC2DeprecatedInstanceTypeRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    /**
	 *
	 */
    public String getHelpText() {

        return "This rule checks if the instance is deprecated";
    }

    /**
     *
     * @param dbInstanceType
     * @param deprecatedInstanceType
     * @return
     */
    private Boolean isInstanceDepricated(String dbInstanceType,
            List<String> deprecatedInstanceType) {
        for (String iType : deprecatedInstanceType) {
            if (dbInstanceType.startsWith(iType)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


}
