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
package com.tmobile.cloud.awsrules.iam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.util.CollectionUtils;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
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

@PacmanRule(key = "check-for-aws-iam-account-with-permanent-access-keys", desc = "checks entirely for No AWS IAM accounts except service accounts should have permanent access keys", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class AwsIamAccountWithPermanentAccessKeysRule extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(AwsIamAccountWithPermanentAccessKeysRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-aws-iam-account-with-permanent-access-keys <br>
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
        logger.debug("========AwsIamAccountWithPermanentAccessKeysRule started=========");
        Map<String, String> ruleParamIam = new HashMap<>();
        ruleParamIam.putAll(ruleParam);
        ruleParamIam.put("region", "us-west-2");

        Map<String, Object> map = null;
        Annotation annotation = null;
        AmazonIdentityManagementClient identityManagementClient = null;

        String roleIdentifyingString = ruleParam
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String userId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(severity, category,
                roleIdentifyingString)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(
                    PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        try {
            map = getClientFor(AWSService.IAM, roleIdentifyingString,
                    ruleParamIam);
            identityManagementClient = (AmazonIdentityManagementClient) map
                    .get(PacmanSdkConstants.CLIENT);
            logger.debug("No AWS IAM accounts except service accounts should have permanent access keys rule starts");

            if (!userId.startsWith(PacmanRuleConstants.SERVICE_ACCOUNTS)) {
                List<AccessKeyMetadata> accessKeyMetadatas = IAMUtils
                        .getAccessKeyInformationForUser(userId,
                                identityManagementClient);
                if (!CollectionUtils.isNullOrEmpty(accessKeyMetadatas)) {
                    Map<String, String> accessMap = getAccessMap(
                            accessKeyMetadatas, userId);
                    if (!accessMap.isEmpty()) {
                        annotation = Annotation.buildAnnotation(ruleParam,
                                Annotation.Type.ISSUE);
                        annotation.put(PacmanSdkConstants.DESCRIPTION,
                                "IAM user has access keys");
                        annotation.put(PacmanRuleConstants.SEVERITY, severity);
                        annotation.put(PacmanRuleConstants.CATEGORY, category);
                        annotation.put(PacmanRuleConstants.ACCOUNT_DETAILS,
                                accessMap.toString());

                        issue.put(PacmanRuleConstants.VIOLATION_REASON,
                                "IAM user which are non service account has access keys");
                        issueList.add(issue);
                        annotation.put("issueDetails", issueList.toString());

                        logger.debug(
                                "========AwsIamAccountWithPermanentAccessKeysRule ended with annotation {} :=========",
                                annotation);
                        return new RuleResult(
                                PacmanSdkConstants.STATUS_FAILURE,
                                PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                    } else {
                        logger.info(userId,
                                "This userId,AWS IAM Account has no permanent access keys");
                    }
                } else {
                    logger.info(userId,
                            "Access key metadata is empty for username ");
                }

            } else {
                logger.info(userId, " : is a service account");
            }

        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.toString());
        }
        logger.debug("========AwsIamAccountWithPermanentAccessKeysRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);

    }

    private Map<String, String> getAccessMap(
            List<AccessKeyMetadata> accessKeyMetadatas, String userId) {
        Map<String, String> accessMap = new HashMap<>();
        for (AccessKeyMetadata keyMetadata : accessKeyMetadatas) {
            if (keyMetadata.getAccessKeyId() != null) {
                accessMap.put(keyMetadata.getAccessKeyId(), userId);
            }
        }
        return accessMap;
    }

    @Override
    public String getHelpText() {
        return "This rule checks No AWS IAM accounts except service accounts should have permanent access keys";
    }
}
