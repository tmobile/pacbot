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

package com.tmobile.pacman.commons.autofix.manager;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class NextStepManager.
 *
 * @author kkumar
 */
public class NextStepManager {

    /** The tagging manager. */
    ResourceTaggingManager taggingManager;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(NextStepManager.class);

    /**
     * Instantiates a new next step manager.
     */
    public NextStepManager() {

        taggingManager = new ResourceTaggingManager();
    }

    /**
     * Gets the next step.
     *
     * @param ruleId the rule id
     * @param resourceId the resource id
     * @param clientMap the client map
     * @param serviceType the service type
     * @return the next step
     */
    @SuppressWarnings("unchecked")
    public AutoFixAction getNextStep(String ruleId , String resourceId,  Map<String, Object> clientMap, AWSService serviceType) {

        try {

           //silent fix can only be aplied to tagging rules , where exception does not makes much sense
           if(isSilentFixEnabledForRule(ruleId)){
                return AutoFixAction.AUTOFIX_ACTION_FIX;
            }

            // if the resource was ever exempted we will send mail to CSR and
            // Exception Owner
            if (isServiceTaggable(serviceType) && null != wasResourceEverExempted(resourceId, clientMap, serviceType)) {
                return AutoFixAction.AUTOFIX_ACTION_EMAIL_REMIND_EXCEPTION_EXPIRY;
            }



            String url = CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_GET_LASTACTION);
            url = url.concat("?resourceId=").concat(resourceId);
            String response = CommonUtils.doHttpGet(url);
            Map<String, Object> resourceDetailsMap = (Map<String, Object>) CommonUtils.deSerializeToObject(response);

            Double responseCode = Double.valueOf((resourceDetailsMap.get("responseCode").toString()));
            if (responseCode == 1) {
                List<Double> lastActions = (List<Double>) resourceDetailsMap.get("lastActions");
                int maxEmails = Integer.parseInt(
                        CommonUtils.getPropValue(PacmanSdkConstants.AUTOFIX_MAX_EMAILS));
                if (lastActions.size() >= maxEmails) {
                    Collections.sort(lastActions);
                    LocalDateTime currentTime = LocalDateTime.now();
                    LocalDateTime lastActionTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(lastActions.get(lastActions.size() - 1).longValue()),
                            TimeZone.getDefault().toZoneId());
                    long hours = ChronoUnit.HOURS.between(lastActionTime, currentTime);
                    if (hours >= 24) {
                        return AutoFixAction.AUTOFIX_ACTION_FIX;
                    } else {
                        return AutoFixAction.DO_NOTHING;
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("Exception in getNextStep:" + exception.getMessage());
            return AutoFixAction.UNABLE_TO_DETERMINE;
        }
        return AutoFixAction.AUTOFIX_ACTION_EMAIL;
    }

    /**
     * Checks if is silent fix enabled for rule.
     *
     * @param ruleId the rule id
     * @return true, if is silent fix enabled for rule
     */
    public boolean isSilentFixEnabledForRule(String ruleId) {
        String fixType = CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_TYPE + "." +ruleId );
        return !Strings.isNullOrEmpty(fixType) && PacmanSdkConstants.AUTO_FIX_TYPE_SILENT.equals(fixType);
    }

    /**
     * Checks if is service taggable.
     *
     * @param serviceType the service type
     * @return true, if is service taggable
     */
    private boolean isServiceTaggable(AWSService serviceType) {
        try {
            List<String> nonTaggableServices = Arrays.asList(
                    CommonUtils.getPropValue(PacmanSdkConstants.PAC_AUTO_TAG_NON_TAGGABLE_SERVICES).split("\\s*,\\s*"));
            return !nonTaggableServices.contains(serviceType.toString());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Was resource ever exempted.
     *
     * @param resourceId the resource id
     * @param clientMap the client map
     * @param serviceType the service type
     * @return the string
     */
    private String wasResourceEverExempted(String resourceId, Map<String, Object> clientMap, AWSService serviceType) {

        String exceptionExpiry = taggingManager.getPacmanTagValue(resourceId, clientMap, serviceType);
        if (!Strings.isNullOrEmpty(exceptionExpiry)) {
            // dcrypt tag value
            try {
                exceptionExpiry = CommonUtils.decryptB64(exceptionExpiry);
                return exceptionExpiry;
            } catch (IOException e) {
                logger.error("error decrypting pacman tag value", e);
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * Post fix action.
     *
     * @param resourceId the resource id
     * @param action the action
     * @throws Exception the exception
     */
    public void postFixAction(final String resourceId, final AutoFixAction action) throws Exception {
        try {
            String url = CommonUtils
                    .getPropValue(PacmanSdkConstants.RESOURCE_POST_LASTACTION);
            url = url.concat("?resourceId=").concat(resourceId).concat("&action=").concat(action.toString());
            CommonUtils.doHttpPost(url, "", new HashMap<>());
        } catch (Exception exception) {
            logger.error("Exception in getNextStep:" + exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Sets the tagging manager.
     *
     * @param taggingManager the new tagging manager
     */
    public void setTaggingManager(ResourceTaggingManager taggingManager) {
        this.taggingManager = taggingManager;
    }

}
