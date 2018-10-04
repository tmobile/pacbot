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

package com.tmobile.pacman.commons.autofix;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.aws.clients.AWSClientManager;
import com.tmobile.pacman.commons.aws.clients.impl.AWSClientManagerImpl;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseFix.
 */
public abstract class BaseFix implements AutoFix {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFix.class);

    /** The issue. */
    Annotation issue;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.AutoFix#executeFix(java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public abstract FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
            Map<String, String> ruleParams);

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public FixResult call() throws Exception {
        if (issue != null) {
            FixResult result = executeFix(issue, null, null);
            return result;
        } else {
            throw new Exception("rule parameters or resource attributes cannot be null, exiting now");
        }
    }

    /**
     * Gets the client for.
     *
     * @param service the service
     * @param roleIdentifierString the role identifier string
     * @param issue the issue
     * @return the client for
     * @throws UnableToCreateClientException the unable to create client exception
     */
    public Map<String, Object> getClientFor(AWSService service, String roleIdentifierString, Map<String, String> issue)
            throws UnableToCreateClientException {
        AWSClientManager awsClientManager = new AWSClientManagerImpl();
        StringBuilder roleArn = new StringBuilder();
        String accountId = issue.get(PacmanSdkConstants.ACCOUNT_ID);
        String roleIdentifier = issue.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        try {
            if (Strings.isNullOrEmpty(accountId) || Strings.isNullOrEmpty(roleIdentifier)) {
                throw new UnableToCreateClientException("missing account id or role arn identifier");
            }
            roleArn.append(PacmanSdkConstants.ROLE_ARN_PREFIX).append(issue.get(PacmanSdkConstants.ACCOUNT_ID))
                    .append(":").append(roleIdentifierString);
            if (null != issue.get(PacmanSdkConstants.REGION)) {
                return awsClientManager.getClient("", roleArn.toString(), service,
                        Regions.fromName(issue.get(PacmanSdkConstants.REGION)), "");
            } else {
                return awsClientManager.getClient("", roleArn.toString(), service, null, "");
            }
        } catch (UnableToCreateClientException e) {
            throw e;
        }
    }

    /**
     * Gets the issue.
     *
     * @return the issue
     */
    public Annotation getIssue() {
        return issue;
    }

    /**
     * Sets the issue.
     *
     * @param issue the new issue
     */
    public void setIssue(Annotation issue) {
        this.issue = issue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.commons.autofix.AutoFix#
     * backupExistingConfigForResource(java.lang.String, java.lang.String)
     */
    @Override
    public abstract boolean backupExistingConfigForResource(final String resourceId, final String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws Exception;



    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.AutoFix#isFixCandidate(java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public  boolean isFixCandidate(String resourceId, String resourceType, Map<String, Object> clientMap,
            Map<String, String> ruleParams, Map<String, String> issue) throws AutoFixException {
        return true;
    }

    /**
     * Backup old config.
     *
     * @param resourceId the resource id
     * @param configType the config type
     * @param oldConfig the old config
     * @return true, if successful
     * @throws AutoFixException the auto fix exception
     */
    public boolean backupOldConfig(String resourceId, String configType, String oldConfig) throws AutoFixException {
        String url = CommonUtils.getPropValue(com.tmobile.pacman.common.PacmanSdkConstants.BACKUP_ASSET_CONFIG);
        url = url.concat("?resourceId=").concat(resourceId).concat("&configType=").concat(configType);
        try {
            CommonUtils.doHttpPost(url, oldConfig, Maps.newHashMap());
            return true;
        } catch (Exception exception) {
            LOGGER.error(String.format("Exception in backuping Old Config: %s" , exception.getMessage()));
            throw new AutoFixException(exception);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.AutoFix#addDetailsToTransactionLog()
     */
    @Override
    public AutoFixTransaction addDetailsToTransactionLog(Map<String, String> annotation) {
        return new AutoFixTransaction();

    }

}
