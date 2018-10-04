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
  Author :Nidhish
  Modified Date: Nov 22, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * The Class IssueResponse.
 */
public class IssueResponse {

    /** The issue id. */
    private String issueId;

    /** The exception granted date. */
    private String exceptionGrantedDate;

    /** The exception end date. */
    private String exceptionEndDate;

    /** The exception reason. */
    private String exceptionReason;

    /** The created by. */
    private String createdBy;

    /**
     * Gets the issue id.
     *
     * @return the issue id
     */
    public String getIssueId() {
        return issueId;
    }

    /**
     * Sets the issue id.
     *
     * @param issueId the new issue id
     */
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    /**
     * Gets the exception granted date.
     *
     * @return the exception granted date
     */
    public String getExceptionGrantedDate() {
        return exceptionGrantedDate;
    }

    /**
     * Sets the exception granted date.
     *
     * @param exceptionGrantedDate the new exception granted date
     */
    public void setExceptionGrantedDate(String exceptionGrantedDate) {
        this.exceptionGrantedDate = exceptionGrantedDate;
    }

    /**
     * Gets the exception end date.
     *
     * @return the exception end date
     */
    public String getExceptionEndDate() {
        return exceptionEndDate;
    }

    /**
     * Sets the exception end date.
     *
     * @param exceptionEndDate the new exception end date
     */
    public void setExceptionEndDate(String exceptionEndDate) {
        this.exceptionEndDate = exceptionEndDate;
    }

    /**
     * Gets the exception reason.
     *
     * @return the exception reason
     */
    public String getExceptionReason() {
        return exceptionReason;
    }

    /**
     * Sets the exception reason.
     *
     * @param exceptionReason the new exception reason
     */
    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy the new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the issue exception details.
     *
     * @return the issue exception details
     */
    public Map<String, Object> getIssueExceptionDetails() {
        Map<String, Object> issueExceptionDetails = Maps.newHashMap();
        issueExceptionDetails.put("issueId", issueId);
        issueExceptionDetails.put("exceptionGrantedDate", exceptionGrantedDate);
        issueExceptionDetails.put("exceptionEndDate", exceptionEndDate);
        issueExceptionDetails.put("exceptionReason", exceptionReason);
        issueExceptionDetails.put("createdBy",createdBy);
        return issueExceptionDetails;
    }
}
