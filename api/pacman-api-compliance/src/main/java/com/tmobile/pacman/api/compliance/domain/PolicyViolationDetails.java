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
  Purpose:
  Author :santoshi
  Modified Date: May 10, 2018

 **/
package com.tmobile.pacman.api.compliance.domain;

import java.util.List;
import java.util.Map;
/**
 * The Class PolicyViolationDetails.
 */
public class PolicyViolationDetails {

    /** The resource type. */
    String resourceType;

    /** The status. */
    String status;

    /** The severity. */
    String severity;

    /** The rule category. */
    String ruleCategory;

    /** The resouce violated policy. */
    String resouceViolatedPolicy;

    /** The policy violated. */
    String policyViolated;

    /** The policy description. */
    String policyDescription;

    /** The violation reason. */
    String violationReason;

    /** The violation created date. */
    String violationCreatedDate;

    /** The violation modified date. */
    String violationModifiedDate;

    /** The policy id. */
    String policyId;

    /** The rule id. */
    String ruleId;

    /** The violation details. */
    List<Map<String, Object>> violationDetails;

    /**
     * Instantiates a new policy violation details.
     *
     * @param resourceType the resource type
     * @param status the status
     * @param severity the severity
     * @param ruleCategory the rule category
     * @param resouceViolatedPolicy the resouce violated policy
     * @param policyViolated the policy violated
     * @param policyDescription the policy description
     * @param violationReason the violation reason
     * @param violationCreatedDate the violation created date
     * @param violationModifiedDate the violation modified date
     * @param policyId the policy id
     * @param ruleId the rule id
     * @param violationDetails the violation details
     */
    public PolicyViolationDetails(String resourceType, String status,
            String severity, String ruleCategory, String resouceViolatedPolicy,
            String policyViolated, String policyDescription,
            String violationReason, String violationCreatedDate,
            String violationModifiedDate, String policyId, String ruleId,
            List<Map<String, Object>> violationDetails) {
        super();
        this.resourceType = resourceType;
        this.status = status;
        this.severity = severity;
        this.ruleCategory = ruleCategory;
        this.resouceViolatedPolicy = resouceViolatedPolicy;
        this.policyViolated = policyViolated;
        this.policyDescription = policyDescription;
        this.violationReason = violationReason;
        this.violationCreatedDate = violationCreatedDate;
        this.violationModifiedDate = violationModifiedDate;
        this.policyId = policyId;
        this.ruleId = ruleId;
        this.violationDetails = violationDetails;
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

    /**
     * Instantiates a new policy violation details.
     */
    public PolicyViolationDetails() {
        super();
    }

    /**
     * Gets the resource type.
     *
     * @return the resource type
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resource type.
     *
     * @param resourceType the new resource type
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
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
     * Gets the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity the new severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the rule category.
     *
     * @return the rule category
     */
    public String getRuleCategory() {
        return ruleCategory;
    }

    /**
     * Sets the rule category.
     *
     * @param ruleCategory the new rule category
     */
    public void setRuleCategory(String ruleCategory) {
        this.ruleCategory = ruleCategory;
    }

    /**
     * Gets the resouce violated policy.
     *
     * @return the resouce violated policy
     */
    public String getResouceViolatedPolicy() {
        return resouceViolatedPolicy;
    }

    /**
     * Sets the resouce violated policy.
     *
     * @param resouceViolatedPolicy the new resouce violated policy
     */
    public void setResouceViolatedPolicy(String resouceViolatedPolicy) {
        this.resouceViolatedPolicy = resouceViolatedPolicy;
    }

    /**
     * Gets the policy violated.
     *
     * @return the policy violated
     */
    public String getPolicyViolated() {
        return policyViolated;
    }

    /**
     * Sets the policy violated.
     *
     * @param policyViolated the new policy violated
     */
    public void setPolicyViolated(String policyViolated) {
        this.policyViolated = policyViolated;
    }

    /**
     * Gets the policy description.
     *
     * @return the policy description
     */
    public String getPolicyDescription() {
        return policyDescription;
    }

    /**
     * Sets the policy description.
     *
     * @param policyDescription the new policy description
     */
    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    /**
     * Gets the violation reason.
     *
     * @return the violation reason
     */
    public String getViolationReason() {
        return violationReason;
    }

    /**
     * Sets the violation reason.
     *
     * @param violationReason the new violation reason
     */
    public void setViolationReason(String violationReason) {
        this.violationReason = violationReason;
    }

    /**
     * Gets the violation created date.
     *
     * @return the violation created date
     */
    public String getViolationCreatedDate() {
        return violationCreatedDate;
    }

    /**
     * Sets the violation created date.
     *
     * @param violationCreatedDate the new violation created date
     */
    public void setViolationCreatedDate(String violationCreatedDate) {
        this.violationCreatedDate = violationCreatedDate;
    }

    /**
     * Gets the violation modified date.
     *
     * @return the violation modified date
     */
    public String getViolationModifiedDate() {
        return violationModifiedDate;
    }

    /**
     * Sets the violation modified date.
     *
     * @param violationModifiedDate the new violation modified date
     */
    public void setViolationModifiedDate(String violationModifiedDate) {
        this.violationModifiedDate = violationModifiedDate;
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
     * Gets the violation details.
     *
     * @return the violation details
     */
    public List<Map<String, Object>> getViolationDetails() {
        return violationDetails;
    }

    /**
     * Sets the violation details.
     *
     * @param violationDetails the violation details
     */
    public void setViolationDetails(List<Map<String, Object>> violationDetails) {
        this.violationDetails = violationDetails;
    }
}
