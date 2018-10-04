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
package com.tmobile.pacman.api.compliance.domain;

import java.util.Date;
import java.util.List;

/**
 * The Class IssuesException.
 */
public class IssuesException {

    /** The issue ids. */
    private List<String> issueIds;
    
    /** The exception granted date. */
    private Date exceptionGrantedDate;
    
    /** The exception end date. */
    private Date exceptionEndDate;
    
    /** The exception reason. */
    private String exceptionReason;
    
    /** The created by. */
    private String createdBy;
    
    /**
     * Gets the issue ids.
     *
     * @return the issue ids
     */
    public List<String> getIssueIds() {
        return issueIds;
    }

    /**
     * Sets the issue ids.
     *
     * @param issueIds the new issue ids
     */
    public void setIssueIds(List<String> issueIds) {
        this.issueIds = issueIds;
    }

    /**
     * Gets the exception granted date.
     *
     * @return the exception granted date
     */
    public Date getExceptionGrantedDate() {
        return exceptionGrantedDate;
    }

    /**
     * Sets the exception granted date.
     *
     * @param exceptionGrantedDate the new exception granted date
     */
    public void setExceptionGrantedDate(Date exceptionGrantedDate) {
        this.exceptionGrantedDate = exceptionGrantedDate;
    }

    /**
     * Gets the exception end date.
     *
     * @return the exception end date
     */
    public Date getExceptionEndDate() {
        return exceptionEndDate;
    }

    /**
     * Sets the exception end date.
     *
     * @param exceptionEndDate the new exception end date
     */
    public void setExceptionEndDate(Date exceptionEndDate) {
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
    
    
}
