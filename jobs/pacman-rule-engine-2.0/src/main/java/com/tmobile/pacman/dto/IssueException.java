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

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class IssueException.
 *
 * @author kkumar
 */
public class IssueException {

    /** The id. */
    private String id;

    /** The exception name. */
    private String exceptionName;

    /** The asset group. */
    private String assetGroup;

    /** The exception reason. */
    private String exceptionReason;

    /** The expiry date. */
    private String expiryDate;

    /** The exception type. */
    // sticky or individual
    private ExceptionType exceptionType;

    /** The issue id. */
    private String issueId;

    /**
     * Instantiates a new issue exception.
     *
     * @param exception the exception
     * @param exceptionType the exception type
     */
    public IssueException(Map<String, String> exception, ExceptionType exceptionType) {
        super();
        this.id = exception.get("_id");
        this.exceptionReason = exception.get("exceptionReason");
        this.exceptionType = exceptionType;
        switch (exceptionType) {
        case STICKY: {
            this.exceptionName = exception.get("exceptionName");
            this.assetGroup = exception.get("assetGroup");
            this.expiryDate = exception.get("expiryDate");
            break;
        }
        case INDIVIDUAL: {
            this.issueId = exception.get("issueId");
            this.expiryDate = exception.get("exceptionEndDate");
        }
        }
    }

    /**
     * Instantiates a new issue exception.
     *
     * @param exceptionName the exception name
     * @param assetGroup the asset group
     * @param exceptionReason the exception reason
     * @param expiryDate the expiry date
     * @param exceptionType the exception type
     */
    public IssueException(String exceptionName, String assetGroup, String exceptionReason, String expiryDate,
            ExceptionType exceptionType) {
        super();
        this.exceptionType = exceptionType;
        this.exceptionName = exceptionName;
        this.assetGroup = assetGroup;
        this.exceptionReason = exceptionReason;
        this.expiryDate = expiryDate;
    }

    /**
     * Gets the exception name.
     *
     * @return the exception name
     */
    public String getExceptionName() {
        return exceptionName;
    }

    /**
     * Sets the exception name.
     *
     * @param exceptionName the new exception name
     */
    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
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
     * Gets the expiry date.
     *
     * @return the expiry date
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the expiry date.
     *
     * @param expiryDate the new expiry date
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof IssueException)) {
            return false;
        }
        IssueException exp = (IssueException) o;
        return exp.id.equals(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Gets the exception type.
     *
     * @return the exception type
     */
    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    /**
     * Sets the exception type.
     *
     * @param exceptionType the new exception type
     */
    public void setExceptionType(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuffer stringBuffer = new StringBuffer();
        final String SEPERATOR = "--";
        return stringBuffer.append(this.id).append(SEPERATOR).append(this.issueId).append(SEPERATOR)
                .append(this.exceptionType).append(SEPERATOR).append(this.exceptionName).append(SEPERATOR)
                .append(this.exceptionReason).append(SEPERATOR).append(this.expiryDate).toString();
    }

}
