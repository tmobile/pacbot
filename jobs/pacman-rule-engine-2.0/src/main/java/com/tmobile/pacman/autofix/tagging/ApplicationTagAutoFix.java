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

package com.tmobile.pacman.autofix.tagging;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.commons.autofix.manager.ResourceTaggingManager;
import com.tmobile.pacman.dto.AutoFixTransaction;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationTagAutoFix.
 */
@PacmanFix(key = "app-tag-fix", desc = "fixes the application tags")
public class ApplicationTagAutoFix extends BaseFix {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationTagAutoFix.class);

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#executeFix(java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
            Map<String, String> ruleParams) {

        ResourceTaggingManager taggingManager = new ResourceTaggingManager();
        try {
            if(taggingManager.tagResource(issue.get(PacmanSdkConstants.RESOURCE_ID), clientMap,AWSService.valueOf(issue.get("targetType").toUpperCase()),
                     ImmutableMap.of(PacmanSdkConstants.APPLICATION_TAG_NAME, issue.get(PacmanSdkConstants.CORRECT_APP_TAG_KEY))))
             {
                 return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE,"tag name "+ PacmanSdkConstants.APPLICATION_TAG_NAME + "changed to " + issue.get(PacmanSdkConstants.CORRECT_APP_TAG_KEY));
             }else{
                 throw new Exception();
             }

        } catch (Exception e) {
            LOGGER.error(String.format("unable to tag resource  %s", issue.get(PacmanSdkConstants.RESOURCE_ID)));
            return new FixResult(PacmanSdkConstants.STATUS_FAILURE_CODE, "unable to tag resource");
        }

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#backupExistingConfigForResource(java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public boolean backupExistingConfigForResource(final String resourceId, final String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws AutoFixException {
        LOGGER.debug(String.format("backing up config for %s " , resourceId));
        JsonObject appTag =  new JsonObject();
        appTag.addProperty(PacmanSdkConstants.APPLICATION_TAG_NAME, issue.get(PacmanSdkConstants.CURRENT_APP_TAG_KEY));

        if (!Strings.isNullOrEmpty(appTag.toString())) {
          backupOldConfig(resourceId, "originalApplicationTag", appTag.toString());
        }
        LOGGER.debug(String.format("backup complete for %s " , resourceId));
        return true;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#isFixCandidate(java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public boolean isFixCandidate(String resourceId, String resourceType, Map<String, Object> clientMap,
            Map<String, String> ruleParams, Map<String, String> issue) throws AutoFixException {

        String current_application_tag=issue.get(PacmanSdkConstants.CURRENT_APP_TAG_KEY);
        String correct_application_tag=issue.get(PacmanSdkConstants.CORRECT_APP_TAG_KEY);
        LOGGER.debug(String.format("isFixCandidate -- can fix applied -- > %s %s %s",current_application_tag,correct_application_tag, (Strings.isNullOrEmpty(current_application_tag)  && !Strings.isNullOrEmpty(correct_application_tag))));
        return Strings.isNullOrEmpty(current_application_tag)  && !Strings.isNullOrEmpty(correct_application_tag) ;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#addDetailsToTransactionLog()
     */
    @Override
    public AutoFixTransaction addDetailsToTransactionLog(Map<String, String> annotation) {
       return new AutoFixTransaction(annotation.get("_resourceid"), annotation.get("ruleId"), annotation.get("accountid"), annotation.get("region"), annotation.get(PacmanSdkConstants.CORRECT_APP_TAG_KEY));
    }


}
