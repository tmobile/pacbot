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
  Purpose: Rule for checking whether non-admin accounts have IAM full access
  Author : U26405
  Modified Date: Aug 3, 2017
  
 **/
package com.tmobile.cloud.awsrules.iam;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.GetPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetPolicyResult;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionRequest;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.Policy;
import com.amazonaws.services.identitymanagement.model.PolicyVersion;
import com.amazonaws.util.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

@PacmanRule(key = "check-non-admin-accounts-for-iamfullccess", desc = "Checks for non-admin accounts with IAM full access", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class IAMAccessGrantForNonAdminAccountRule extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(IAMAccessGrantForNonAdminAccountRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            adminRolesToCompare : String as Admin to compare the roles <br>
     * <br>
     * 
     *            ruleKey : check-non-admin-accounts-for-iamfullccess <br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     * roleIdentifyingString : Configure it as role/pacbot_ro <br><br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

    public RuleResult execute(final Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {

        logger.debug("========IAMAccessGrantForNonAdminAccountRule started=========");
        Map<String, String> ruleParamforIAM = new HashMap<>();
        ruleParamforIAM.putAll(ruleParam);
        ruleParamforIAM.put("region", "us-west-2");

        Map<String, Object> map = null;
        AmazonIdentityManagementClient iamClient = null;
        String roleIdentifyingString = ruleParamforIAM
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String roleName = resourceAttributes.get(PacmanRuleConstants.ROLE_NAME);
        String adminRolesToCompare = ruleParamforIAM
                .get(PacmanRuleConstants.ADMIN_ROLES_TO_COMPARE);

        String severity = ruleParamforIAM.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParamforIAM.get(PacmanRuleConstants.CATEGORY);

        MDC.put("executionId", ruleParam.get("executionId")); 
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(adminRolesToCompare, severity,
                category)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        Annotation annotation = null;

        List<AttachedPolicy> attachedRolePolicies = new ArrayList<>();
        String message = null;
        List<String> adminRoleListToCompare = PacmanUtils.splitStringToAList(
                adminRolesToCompare, ",");

        try {
            map = getClientFor(AWSService.IAM, roleIdentifyingString,
                    ruleParamforIAM);
            iamClient = (AmazonIdentityManagementClient) map
                    .get(PacmanSdkConstants.CLIENT);
        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.toString());
        }

        if (!adminRoleListToCompare.contains(roleName)) {
            ListAttachedRolePoliciesRequest request = new ListAttachedRolePoliciesRequest();
            ListAttachedRolePoliciesResult result = null;
            request.setRoleName(roleName);

            // Disabling pagination. Otherwise it fetches only the first 100
            // records.
            do {
                result = iamClient.listAttachedRolePolicies(request);
                attachedRolePolicies.addAll(result.getAttachedPolicies());
                request.setMarker(result.getMarker());
            } while (result.isTruncated());

            if (!CollectionUtils.isNullOrEmpty(attachedRolePolicies)) {
                if (isValidPoliciesForNonAdminRole(attachedRolePolicies)
                        || isIAMFullAccessFoundAfterThoroughCheck(iamClient,
                                attachedRolePolicies)) {
                    message = "Role " + roleName + " has IAM full access";
                    logger.warn(message);
                    annotation = Annotation.buildAnnotation(ruleParam,
                            Annotation.Type.ISSUE);
                    annotation.put(PacmanSdkConstants.DESCRIPTION,
                            "Non-admin roles with IAMFullAccess found");
                    annotation.put(PacmanRuleConstants.SEVERITY, severity);
                    annotation.put(PacmanRuleConstants.CATEGORY, category);

                    issue.put(PacmanRuleConstants.VIOLATION_REASON,
                            "Non-admin roles with IAMFullAccess found");
                    issueList.add(issue);
                    annotation.put("issueDetails", issueList.toString());

                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                            PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                }
            } else {
                logger.info(roleName , "This Role has no policies attached");
            }
        } else {
            logger.info("Admin role found");
        }

        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    /**
     * This method is for extracting the policy document and finding out whether
     * iam:* is present in the policy document JSON
     * 
     * @param iamClient
     * @param attachedRolePolicies
     * @return
     */
    private boolean isIAMFullAccessFoundAfterThoroughCheck(
            AmazonIdentityManagementClient iamClient,
            List<AttachedPolicy> attachedRolePolicies) {
        boolean isIAMFullAccess = false;
        for (AttachedPolicy policy : attachedRolePolicies) {

            GetPolicyRequest policyRequest = new GetPolicyRequest();
            policyRequest.setPolicyArn(policy.getPolicyArn());
            GetPolicyResult policyResult = iamClient.getPolicy(policyRequest);
            Policy p = policyResult.getPolicy();

            GetPolicyVersionRequest versionRequest = new GetPolicyVersionRequest();
            versionRequest.setPolicyArn(policy.getPolicyArn());
            versionRequest.setVersionId(p.getDefaultVersionId());
            GetPolicyVersionResult versionResult = iamClient
                    .getPolicyVersion(versionRequest);

            String decode = null;
            PolicyVersion pv = versionResult.getPolicyVersion();
            try {
                decode = URLDecoder.decode(pv.getDocument(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
                throw new InvalidInputException(e.getMessage());
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(decode, JsonObject.class);
            if (json.get(PacmanRuleConstants.STATEMENT) instanceof JsonArray) {
                JsonArray statementArray = (JsonArray) json
                        .get(PacmanRuleConstants.STATEMENT);
                for (int i = 0; i < statementArray.size(); i++) {
                    Gson innerGson = new Gson();
                    JsonObject innerJson = innerGson.fromJson(
                            statementArray.get(i), JsonObject.class);
                    isIAMFullAccess = isActionTagContainsIAMFullAccess(innerJson);
                }
            } else {
                JsonObject statementArray = (JsonObject) json
                        .get(PacmanRuleConstants.STATEMENT);
                Gson innerGson = new Gson();
                JsonObject innerJson = innerGson.fromJson(statementArray,
                        JsonObject.class);
                isIAMFullAccess = isActionTagContainsIAMFullAccess(innerJson);
            }

        }
        return isIAMFullAccess;
    }

    private boolean isActionTagContainsIAMFullAccess(JsonObject innerJson) {
        boolean flag = false;
        if (innerJson.get(PacmanRuleConstants.EFFECT).getAsString()
                .equals(PacmanRuleConstants.ALLOW)) { 
            if (innerJson.get(PacmanRuleConstants.ACTION) instanceof JsonArray) {
                JsonArray innerArray = (JsonArray) innerJson
                        .get(PacmanRuleConstants.ACTION);
                for (int j = 0; j < innerArray.size(); j++) {
                    if (innerArray.get(j).getAsString()
                            .equals(PacmanRuleConstants.IAM_COLON_STAR)) {
                        flag = true;
                    }
                }
            } else {
                if (null!=innerJson.get(PacmanRuleConstants.ACTION) && innerJson.get(PacmanRuleConstants.ACTION).getAsString()
                        .equals(PacmanRuleConstants.IAM_COLON_STAR)) {
                        flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * This method checks for the property "AdministratorAccess" and
     * "IAMFullAccess" in the list of attached policies for a Role
     * 
     * @param attachedPolicies
     * @param isAdmin
     * @return true if the attached policy contains "AdministratorAccess"
     *         property false otherwise.
     */
    private boolean isValidPoliciesForNonAdminRole(
            List<AttachedPolicy> attachedPolicies) {
        for (AttachedPolicy policy : attachedPolicies) {
            String policyName = policy.getPolicyName();
            if(!StringUtils.isEmpty(policyName)){
            if (policyName
                    .equalsIgnoreCase(PacmanRuleConstants.ADMINISTRATOR_ACCESS)) {
                return true;
            }
            if (policyName
                    .equalsIgnoreCase(PacmanRuleConstants.IAM_FULL_ACCESS)) {
                return true;
            }
        }
        }
        return false;
    }

    @Override
    public String getHelpText() {
        return "Checks for non-admin accounts with IAM full access";
    }

}
