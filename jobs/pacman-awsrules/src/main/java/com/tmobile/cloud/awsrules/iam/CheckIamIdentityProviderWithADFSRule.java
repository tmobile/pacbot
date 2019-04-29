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
 * 
 */

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
import com.amazonaws.services.identitymanagement.model.GetSAMLProviderRequest;
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

@PacmanRule(key = "check-iam-identity-provider-with-ADFS", desc = "At least one CORP ADFS identity provider should be configured on all AWS accounts", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckIamIdentityProviderWithADFSRule extends BaseRule {

    private static final Logger logger = LoggerFactory
            .getLogger(CheckIamIdentityProviderWithADFSRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-iam-identity-provider-with-ADFS <br>
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

        logger.debug("========CheckIamIdentityProviderWithADFSRule started=========");
        Map<String, String> temp = new HashMap<>();
        temp.putAll(ruleParam);
        temp.put("region", "us-west-2");

        Annotation annotation = null;
        String accountId = resourceAttributes.get("accountid");

        logger.info(resourceAttributes.get("accountid"));
        logger.info(resourceAttributes.get("accountname"));

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String roleIdentifyingString = ruleParam
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(severity, category, roleIdentifyingString)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(
                    PacmanRuleConstants.MISSING_CONFIGURATION);
        }
        Map<String, Object> map = null;
        AmazonIdentityManagementClient identityManagementClient = null;

        try {
            map = getClientFor(AWSService.IAM, roleIdentifyingString, temp);
            identityManagementClient = (AmazonIdentityManagementClient) map
                    .get(PacmanSdkConstants.CLIENT);

            GetSAMLProviderRequest request = new GetSAMLProviderRequest();
            request.setSAMLProviderArn("arn:aws:iam::" + accountId
                    + ":saml-provider/ADFS");
             identityManagementClient
                    .getSAMLProvider(request);
            
        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.toString());
        }catch(Exception e){
            logger.error("e", e);
            annotation = Annotation.buildAnnotation(ruleParam,
                    Annotation.Type.ISSUE);
            annotation
                    .put(PacmanSdkConstants.DESCRIPTION,
                            "CORP ADFS identity provider not configured for this AWS account");
            annotation.put(PacmanRuleConstants.SEVERITY, severity);
            annotation.put(PacmanRuleConstants.CATEGORY, category);

            issue.put(PacmanRuleConstants.VIOLATION_REASON,
                    "CORP ADFS identity provider not configured for this AWS account");
            issueList.add(issue);
            annotation.put("issueDetails", issueList.toString());

            logger.debug(
                    "========CheckIamIdentityProviderWithADFSRule ended with an annotation {} : =========",
                    annotation);
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                    PacmanRuleConstants.FAILURE_MESSAGE, annotation);   
        }

        logger.debug("========CheckIamIdentityProviderWithADFSRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "At least one CORP ADFS identity provider should be configured on all AWS accounts";
    }
    
}
