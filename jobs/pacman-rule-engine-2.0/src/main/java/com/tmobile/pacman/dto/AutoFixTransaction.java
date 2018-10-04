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
package com.tmobile.pacman.dto;

import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.util.CommonUtils;

public class AutoFixTransaction {

    /** The transation time. */
    private String transationTime;

    /** The action. */
    private AutoFixAction action;

    /** The resource id. */
    private String resourceId;

    /** The execution id. */
    private String executionId;

    /** The transaction id. */
    private String transactionId;

    /** The desc. */
    private String desc;

    /**  ruleId. */
    private String ruleId;

    /** The account id. */
    private String accountId;

    /** The region. */
    private String region;

    /** The application tag. */
    private String applicationTag;

    /**
     * Instantiates a new auto fix transaction.
     *
     * @param resourceId the resource id
     * @param ruleId the rule id
     * @param accountId the account id
     * @param region the region
     * @param applicationTag the application tag
     */
    public AutoFixTransaction(String resourceId,
            String ruleId, String accountId, String region,String applicationTag) {
        super();
        this.resourceId = resourceId;
        this.ruleId = ruleId;
        this.accountId = accountId;
        this.region = region;
        this.applicationTag = applicationTag;
    }

    /**
     * Gets the account id.
     *
     * @return the account id
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the account id.
     *
     * @param accountId the new account id
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the region.
     *
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the region.
     *
     * @param region the new region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Gets the application tag.
     *
     * @return the application tag
     */
    public String getApplicationTag() {
        return applicationTag;
    }

    /**
     * Sets the application tag.
     *
     * @param applicationTag the new application tag
     */
    public void setApplicationTag(String applicationTag) {
        this.applicationTag = applicationTag;
    }

    /**
     * Instantiates a new auto fix transaction.
     */
    public AutoFixTransaction() {
    }

    /**
     * Instantiates a new auto fix transaction.
     *
     * @param action the action
     * @param resourceId the resource id
     * @param ruleId the rule id
     * @param executionId the execution id
     * @param transactionId the transaction id
     * @param desc the desc
     */
    public AutoFixTransaction(AutoFixAction action, String resourceId, String ruleId, String executionId, String transactionId,
            String desc) {
        super();
        this.transationTime = CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT);
        this.action = action;
        this.resourceId = resourceId;
        this.ruleId=ruleId;
        this.executionId = executionId;
        this.transactionId = transactionId;
        this.desc = desc;
    }

    /**
     * Gets the transation time.
     *
     * @return the transation time
     */
    public String getTransationTime() {
        return transationTime;
    }

    /**
     * Sets the transation time.
     *
     * @param transationTime the new transation time
     */
    public void setTransationTime(String transationTime) {
        this.transationTime = transationTime;
    }

    /**
     * Gets the action.
     *
     * @return the action
     */
    public AutoFixAction getAction() {
        return action;
    }

    /**
     * Sets the action.
     *
     * @param action the new action
     */
    public void setAction(AutoFixAction action) {
        this.action = action;
    }

    /**
     * Gets the resource id.
     *
     * @return the resource id
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the resource id.
     *
     * @param resourceId the new resource id
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Gets the execution id.
     *
     * @return the execution id
     */
    public String getExecutionId() {
        return executionId;
    }

    /**
     * Sets the execution id.
     *
     * @param executionId the new execution id
     */
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    /**
     * Gets the transaction id.
     *
     * @return the transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction id.
     *
     * @param transactionId the new transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the desc.
     *
     * @param desc the new desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets the rule id.
     *
     * @return the rule id
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Sets the rule id.
     *
     * @param ruleId the new rule id
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

}
