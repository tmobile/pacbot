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
package com.tmobile.cloud.awsrules.serviceaccounts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.JsonArray;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-service-account-admin", desc = "Checks for any service account in a r_win_*_admin or r_rhel_*_admin group must have a valid exception on file (Description must contain “RP” and “FND” strings)", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class ServiceAccountAdministratorsRule extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(ServiceAccountAdministratorsRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-service-account-admin <br>
     * <br>
     * 
     *            threadsafe : if true , rule will be executed on multiple
     *            threads <br>
     * <br>
     * esServiceAccountURL : Enter the service Es url <br><br>
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
    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========ServiceAccountAdministratorsRule started=========");
        Annotation annotation = null;
        String resourceId = null;
        String description = null;
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String serviceAccountEsURL = null;
        Boolean isServiceAccountValidException = false;
        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        
        String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_SERVICE_ACCOUNT_ES_URL);
        
        if(!StringUtils.isEmpty(formattedUrl)){
            serviceAccountEsURL =  formattedUrl;
        }
        
        if (!PacmanUtils.doesAllHaveValue(severity, category,
                serviceAccountEsURL)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (resourceAttributes != null) {
            resourceId = StringUtils.trim(resourceAttributes
                    .get(PacmanSdkConstants.RESOURCE_ID));

            JsonArray memberOfList = new JsonArray();
            try {

                memberOfList = PacmanUtils.getMemberOf(resourceId,
                        serviceAccountEsURL);

            } catch (Exception e) {
                logger.error("unable to determine" ,e);
                throw new RuleExecutionFailedExeption("unable to determine" + e);
            }
            if (memberOfList.size() > 0) {
                for (int j = 0; j < memberOfList.size(); j++) {
                    String memberOfObject = memberOfList.get(j).getAsString();

                    if ((memberOfObject.startsWith(PacmanRuleConstants.R_WIN) && memberOfObject
                            .endsWith(PacmanRuleConstants.ADMIN))
                            || (memberOfObject
                                    .startsWith(PacmanRuleConstants.R_RHEL) && memberOfObject
                                    .endsWith(PacmanRuleConstants.ADMIN))) {
                        description = resourceAttributes
                                .get(PacmanRuleConstants.DESCRIPTION);
                        if (description.contains("RP")
                                && description.contains("FND")) {
                            isServiceAccountValidException = true;
                        }

                        if (!isServiceAccountValidException) {
                            annotation = Annotation.buildAnnotation(ruleParam,
                                    Annotation.Type.ISSUE);
                            annotation
                                    .put(PacmanSdkConstants.DESCRIPTION,
                                            "Service account with an invalid exception on file found !!");
                            annotation.put(PacmanRuleConstants.SEVERITY,
                                    severity);
                            annotation.put(PacmanRuleConstants.CATEGORY,
                                    category);

                            issue.put(PacmanRuleConstants.VIOLATION_REASON,
                                    "Service account's description doesn't contain RP & FND!!");
                            issue.put("member_of_service_account",
                                    memberOfObject);
                            issue.put("service_account_description",
                                    description);
                            issueList.add(issue);
                            annotation
                                    .put("issueDetails", issueList.toString());

                            logger.debug(
                                    "========ServiceAccountAdministratorsRule ended with an annotation : {}=========",
                                    annotation);
                            return new RuleResult(
                                    PacmanSdkConstants.STATUS_FAILURE,
                                    PacmanRuleConstants.FAILURE_MESSAGE,
                                    annotation);
                        }
                    }
                }
            }

        }
        logger.debug("========ServiceAccountAdministratorsRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "This rule Checks for any service account in a r_win_*_admin or r_rhel_*_admin group must have a valid exception on file (Description must contain “RP” and “FND” strings)";
    }
  
}
