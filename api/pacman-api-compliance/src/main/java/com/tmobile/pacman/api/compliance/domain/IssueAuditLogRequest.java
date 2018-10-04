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
  Modified Date: Nov 20, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;

/**
 * The Class IssueAuditLogRequest.
 */
public class IssueAuditLogRequest {

    /** The issue id. */
    String issueId;

    /** The target type. */
    String targetType;

    /** The from. */
    int from;

    /** The size. */
    int size;

    /** The search text. */
    String searchText;

    /**
     * Instantiates a new issue audit log request.
     *
     * @param issueId the issue id
     * @param targetType the target type
     * @param from the from
     * @param size the size
     */
    public IssueAuditLogRequest(String issueId, String targetType, int from,
            int size) {
        super();
        this.issueId = issueId;
        this.targetType = targetType;
        this.from = from;
        this.size = size;

    }

    /**
     * Instantiates a new issue audit log request.
     *
     * @param searchText the search text
     */
    public IssueAuditLogRequest(String searchText) {
        super();
        this.searchText = searchText;
    }

    /**
     * Gets the search text.
     *
     * @return the search text
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the search text.
     *
     * @param searchText the new search text
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * Instantiates a new issue audit log request.
     */
    public IssueAuditLogRequest() {
        super();
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
     * Gets the from.
     *
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the new from
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size the new size
     */
    public void setSize(int size) {
        this.size = size;
    }
}
