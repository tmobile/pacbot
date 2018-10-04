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
  Purpose: Checks for kernel compliance in on-premise servers
  Author :u26405
  Modified Date: Aug 16, 2017
  
 **/
package com.tmobile.cloud.awsrules.onprem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tmobile.cloud.awsrules.compliance.DefaultTargetCriteriaDataProvider;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-kernel-compliance-onprem", desc = "Checks for kernel compliance in on-premise servers", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class OnPremiseServerKernelComplianceRule extends BaseRule {
    private static final Logger logger = LoggerFactory
            .getLogger(OnPremiseServerKernelComplianceRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-kernel-compliance-onprem <br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     *            defaultKernelCriteriaUrl : Enter the API which gets the
     *            default kernel criteria <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    public RuleResult execute(final Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========OnPremiseServerKernelComplianceRule started=========");
        Gson gson = new Gson();
        Set<String> keySet;
        boolean isCompliant = false;
        Annotation annotation = null;

        String lastPatched = null;
        String finalKernelRelease = null;
        int patchedQuarter = 0;
        int currentQuarter = 0;
        int patchedYear = 0;
        int currentYear = 0;
        LocalDate lastPatchedDate = null;
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String defaultKernelCriteriaUrl = ruleParam
                .get(PacmanRuleConstants.DEFAULT_KERNEL_CRITERIA_URL);
        String description = null;
        List<String> sourcesChecked = new ArrayList<>();
        LinkedHashMap<String, Object> onpremSource = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(severity, category,
                defaultKernelCriteriaUrl)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }
        if ("true".equals(resourceAttributes.get(PacmanRuleConstants.IN_SCOPE))) {
            lastPatched = resourceAttributes
                    .get(PacmanRuleConstants.FINAL_U_LAST_PATCHED);
            finalKernelRelease = resourceAttributes
                    .get(PacmanRuleConstants.FINAL_KERNEL_RELEASE);
            JsonObject kernelVersionFromPacmanTable = DefaultTargetCriteriaDataProvider
                    .getInstance(defaultKernelCriteriaUrl)
                    .getTargetCriterianData();

            if (kernelVersionFromPacmanTable != null
                    && !kernelVersionFromPacmanTable.entrySet().isEmpty()) {
                sourcesChecked.add(PacmanRuleConstants.FINAL_U_LAST_PATCHED);
                sourcesChecked.add(PacmanRuleConstants.FINAL_KERNEL_RELEASE);
                HashMap<String, String> mapOfQuaterlyVersions = gson.fromJson(
                        kernelVersionFromPacmanTable.toString(), HashMap.class);
                keySet = mapOfQuaterlyVersions.keySet();
                if (!StringUtils.isNullOrEmpty(lastPatched)) {
                    onpremSource.put(PacmanRuleConstants.FINAL_U_LAST_PATCHED,
                            lastPatched);
                } else {
                    onpremSource.put(PacmanRuleConstants.FINAL_U_LAST_PATCHED,
                            "Not found");
                }

                if (!StringUtils.isNullOrEmpty(finalKernelRelease)) {
                    onpremSource.put(PacmanRuleConstants.FINAL_KERNEL_RELEASE,
                            finalKernelRelease);
                } else {
                    onpremSource.put(PacmanRuleConstants.FINAL_KERNEL_RELEASE,
                            "Not found");
                } 
                // convert String to LocalDate
                if (!StringUtils.isNullOrEmpty(lastPatched)) {
                    lastPatchedDate = LocalDate.parse(lastPatched, formatter);

                    patchedYear = lastPatchedDate.getYear();
                    currentYear = LocalDate.now().getYear();

                    patchedQuarter = lastPatchedDate
                            .get(IsoFields.QUARTER_OF_YEAR);
                    currentQuarter = LocalDate.now().get(
                            IsoFields.QUARTER_OF_YEAR);

                    if ((patchedYear == currentYear)
                            && (currentQuarter == patchedQuarter)) {
                        description = "resource got compliant through final_u_last_patched value "
                                + lastPatchedDate;
                        return new RuleResult(
                                PacmanSdkConstants.STATUS_SUCCESS, description);
                    } else if (!StringUtils.isNullOrEmpty(finalKernelRelease)) {

                        isCompliant = PacmanUtils.checkIsCompliant(
                                finalKernelRelease, keySet,
                                mapOfQuaterlyVersions);

                        if (isCompliant) {
                            description = "resource got compliant through final_kernel_release value "
                                    + finalKernelRelease;
                            return new RuleResult(
                                    PacmanSdkConstants.STATUS_SUCCESS,
                                    description);
                        }

                    }
                } else if (!StringUtils.isNullOrEmpty(finalKernelRelease)) {
                    isCompliant = PacmanUtils.checkIsCompliant(
                            finalKernelRelease, keySet, mapOfQuaterlyVersions);

                    if (isCompliant) {
                        description = "resource got compliant through final_kernel_release value "
                                + finalKernelRelease;
                        return new RuleResult(
                                PacmanSdkConstants.STATUS_SUCCESS, description);
                    }
                }

                if (!isCompliant) {
                    annotation = Annotation.buildAnnotation(ruleParam,
                            Annotation.Type.ISSUE);
                    annotation.put(PacmanSdkConstants.DESCRIPTION,
                            "Onprem kernel version is not compliant");
                    annotation.put(PacmanRuleConstants.SEVERITY, severity);
                    annotation.put(PacmanRuleConstants.CATEGORY, category);
                    annotation.put(PacmanRuleConstants.FAILED_TYPES,
                            sourcesChecked.toString());
                    issue.put(PacmanRuleConstants.VIOLATION_REASON,
                            "Onprem kernel version is not compliant");
                    issue.put(PacmanRuleConstants.SOURCE_VERIFIED,
                            String.join(",", sourcesChecked));
                    issue.put("onprem_sources", gson.toJson(onpremSource));

                    issueList.add(issue);
                    annotation.put("issueDetails", issueList.toString());
                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                            PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                }

            } else {
                logger.info("target kernel criteria not maintained");

                // If Target Kernel Version not maintained create an issue
                annotation = Annotation.buildAnnotation(ruleParam,
                        Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,
                        "Target Kernerl Criteria not maintained");
                annotation.put(PacmanRuleConstants.SEVERITY, severity);
                annotation.put(PacmanRuleConstants.CATEGORY, category);
                issue.put(PacmanRuleConstants.VIOLATION_REASON,
                        PacmanRuleConstants.NO_DEFAULT_TARGET);
                issueList.add(issue);

                annotation.put("issueDetails", issueList.toString());

                return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                        PacmanRuleConstants.FAILURE_MESSAGE, annotation);
            }

        }
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "This rule checks for kernel compliance in on-premise servers";
    }

}
