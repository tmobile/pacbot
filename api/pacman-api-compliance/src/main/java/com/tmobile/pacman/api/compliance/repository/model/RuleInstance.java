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
package com.tmobile.pacman.api.compliance.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
/**
 * The Class RuleInstance.
 */
@Entity
@Table(name = "cf_RuleInstance", uniqueConstraints = @UniqueConstraint(columnNames = "ruleId"))
public class RuleInstance {

    /** The rule id. */
    @Id
    @Column(name = "ruleId", unique = true, nullable = false)
    private String ruleId;
    
    /** The rule UUID. */
    private String ruleUUID;
    
    /** The policy id. */
    private String policyId;
    
    /** The rule name. */
    private String ruleName;
    
    /** The target type. */
    private String targetType;
    
    /** The asset group. */
    private String assetGroup;
    
    /** The alexa keyword. */
    private String alexaKeyword;
    
    /** The rule params. */
    private String ruleParams;
    
    /** The rule frequency. */
    private String ruleFrequency;
    
    /** The rule executable. */
    private String ruleExecutable;
    
    /** The rule rest url. */
    private String ruleRestUrl;
    
    /** The rule type. */
    private String ruleType;
    
    /** The rule arn. */
    private String ruleArn;
    
    /** The status. */
    private String status;
    
    /** The user id. */
    private Integer userId;
    
    /** The display name. */
    private String displayName;
    
    /** The created date. */
    private Date createdDate;
    
    /** The modified date. */
    private Date modifiedDate;

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

    /**
     * Gets the rule UUID.
     *
     * @return the rule UUID
     */
    public String getRuleUUID() {
        return ruleUUID;
    }

    /**
     * Sets the rule UUID.
     *
     * @param ruleUUID the new rule UUID
     */
    public void setRuleUUID(String ruleUUID) {
        this.ruleUUID = ruleUUID;
    }

    /**
     * Gets the policy id.
     *
     * @return the policy id
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Sets the policy id.
     *
     * @param policyId the new policy id
     */
    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    /**
     * Gets the rule name.
     *
     * @return the rule name
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Sets the rule name.
     *
     * @param ruleName the new rule name
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * Gets the target type.
     *
     * @return the target type
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * Sets the target type.
     *
     * @param targetType the new target type
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * Gets the asset group.
     *
     * @return the asset group
     */
    public String getAssetGroup() {
        return assetGroup;
    }

    /**
     * Sets the asset group.
     *
     * @param assetGroup the new asset group
     */
    public void setAssetGroup(String assetGroup) {
        this.assetGroup = assetGroup;
    }

    /**
     * Gets the alexa keyword.
     *
     * @return the alexa keyword
     */
    public String getAlexaKeyword() {
        return alexaKeyword;
    }

    /**
     * Sets the alexa keyword.
     *
     * @param alexaKeyword the new alexa keyword
     */
    public void setAlexaKeyword(String alexaKeyword) {
        this.alexaKeyword = alexaKeyword;
    }

    /**
     * Gets the rule params.
     *
     * @return the rule params
     */
    public String getRuleParams() {
        return ruleParams;
    }

    /**
     * Sets the rule params.
     *
     * @param ruleParams the new rule params
     */
    public void setRuleParams(String ruleParams) {
        this.ruleParams = ruleParams;
    }

    /**
     * Gets the rule frequency.
     *
     * @return the rule frequency
     */
    public String getRuleFrequency() {
        return ruleFrequency;
    }

    /**
     * Sets the rule frequency.
     *
     * @param ruleFrequency the new rule frequency
     */
    public void setRuleFrequency(String ruleFrequency) {
        this.ruleFrequency = ruleFrequency;
    }

    /**
     * Gets the rule executable.
     *
     * @return the rule executable
     */
    public String getRuleExecutable() {
        return ruleExecutable;
    }

    /**
     * Sets the rule executable.
     *
     * @param ruleExecutable the new rule executable
     */
    public void setRuleExecutable(String ruleExecutable) {
        this.ruleExecutable = ruleExecutable;
    }

    /**
     * Gets the rule rest url.
     *
     * @return the rule rest url
     */
    public String getRuleRestUrl() {
        return ruleRestUrl;
    }

    /**
     * Sets the rule rest url.
     *
     * @param ruleRestUrl the new rule rest url
     */
    public void setRuleRestUrl(String ruleRestUrl) {
        this.ruleRestUrl = ruleRestUrl;
    }

    /**
     * Gets the rule type.
     *
     * @return the rule type
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * Sets the rule type.
     *
     * @param ruleType the new rule type
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * Gets the rule arn.
     *
     * @return the rule arn
     */
    public String getRuleArn() {
        return ruleArn;
    }

    /**
     * Sets the rule arn.
     *
     * @param ruleArn the new rule arn
     */
    public void setRuleArn(String ruleArn) {
        this.ruleArn = ruleArn;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the created date.
     *
     * @param createdDate the new created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets the modified date.
     *
     * @return the modified date
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the modified date.
     *
     * @param modifiedDate the new modified date
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
