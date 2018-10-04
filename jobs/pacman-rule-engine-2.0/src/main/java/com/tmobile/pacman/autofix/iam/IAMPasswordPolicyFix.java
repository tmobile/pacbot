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
package com.tmobile.pacman.autofix.iam;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetAccountPasswordPolicyResult;
import com.amazonaws.services.identitymanagement.model.PasswordPolicy;
import com.amazonaws.services.identitymanagement.model.UpdateAccountPasswordPolicyRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class IAMPasswordPolicyFix.
 */
@PacmanFix(key = "iam-password-policy-fix", desc = "fixes the password policy")
public class IAMPasswordPolicyFix extends BaseFix {

    /** The Constant PASSWORD_POLICY. */
    private static final String PASSWORD_POLICY = "passwordPolicy";
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(IAMPasswordPolicyFix.class);

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.commons.autofix.BaseFix#executeFix(java.util.Map,
     * java.util.Map)
     */
    @Override
    public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
            Map<String, String> ruleParams) {

        AmazonIdentityManagementClient client = (AmazonIdentityManagementClient) clientMap
                .get(PacmanSdkConstants.CLIENT);

        UpdateAccountPasswordPolicyRequest updatePasswordPolicy = new UpdateAccountPasswordPolicyRequest();
        updatePasswordPolicy.setMinimumPasswordLength(Integer.parseInt(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_MIN_PWD_LENGTH + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setRequireSymbols(Boolean.parseBoolean(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_REQ_SYMBLS + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setRequireNumbers(Boolean.parseBoolean(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_REQ_NUMBERS + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setRequireUppercaseCharacters(Boolean.parseBoolean(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_REQ_UPPERCASE + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setRequireLowercaseCharacters(Boolean.parseBoolean(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_REQ_LWRCASE + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setAllowUsersToChangePassword(Boolean.parseBoolean(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_CHNG_PWD_ALLOW + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setMaxPasswordAge(Integer.parseInt(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_MAX_PWD_AGE + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setPasswordReusePrevention(Integer.parseInt(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_PWD_REUSE_PREVENT + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        updatePasswordPolicy.setHardExpiry(Boolean.parseBoolean(CommonUtils.getPropValue(
                PacmanSdkConstants.PAC_AUTO_FIX_PWD_HARD_EXPIRY + ruleParams.get(PacmanSdkConstants.RULE_ID))));
        client.updateAccountPasswordPolicy(updatePasswordPolicy);
        LOGGER.info("password policy fixed");
        return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE, "the IAM password policy is now fixed");

    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.commons.autofix.BaseFix#
     * backupExistingConfigForResource(java.lang.String, java.lang.String,
     * java.util.Map)
     */
    @Override
    public boolean backupExistingConfigForResource(String resourceId, String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws AutoFixException {
        LOGGER.debug(String.format("backing up the config for %s" , resourceId));
        AmazonIdentityManagementClient client = (AmazonIdentityManagementClient) clientMap
                .get(PacmanSdkConstants.CLIENT);
        GetAccountPasswordPolicyResult accountPasswordPolicyResult = client.getAccountPasswordPolicy();
        PasswordPolicy passwordPolicy = accountPasswordPolicyResult.getPasswordPolicy();
        Gson gson = new GsonBuilder().create();
        return backupOldConfig(resourceId, PASSWORD_POLICY, gson.toJson(passwordPolicy));
    }

}
