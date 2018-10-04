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
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
/**
 * The Class PacRuleEngineAutofixActions.
 */
@Entity
@Table(name = "pac_rule_engine_autofix_actions", uniqueConstraints = @UniqueConstraint(columnNames = {
        "resourceId", "lastActionTime" }))
@IdClass(PacRuleEngineAutofixActionsIdentity.class)
public class PacRuleEngineAutofixActions {

    /** The resource id. */
    @Id
    private String resourceId;

    /** The last action time. */
    @Id
    private Date lastActionTime;

    /** The action. */
    @Column(name = "action")
    private String action;

    /**
     * Instantiates a new pac rule engine autofix actions.
     */
    public PacRuleEngineAutofixActions() {
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
     * Gets the last action time.
     *
     * @return the last action time
     */
    public Date getLastActionTime() {
        return lastActionTime;
    }

    /**
     * Sets the last action time.
     *
     * @param lastActionTime the new last action time
     */
    public void setLastActionTime(Date lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    /**
     * Gets the action.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action.
     *
     * @param action the new action
     */
    public void setAction(String action) {
        this.action = action;
    }
}
