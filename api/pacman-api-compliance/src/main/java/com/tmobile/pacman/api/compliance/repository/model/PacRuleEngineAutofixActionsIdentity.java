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
import java.util.Objects;

import javax.persistence.Column;
/**
 * The Class PacRuleEngineAutofixActionsIdentity.
 */
public class PacRuleEngineAutofixActionsIdentity implements
        java.io.Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4130050412427088441L;

    /** The resource id. */
    @Column(name = "resourceId", nullable = false)
    private String resourceId;

    /** The last action time. */
    @Column(name = "lastActionTime", nullable = false)
    private Date lastActionTime;

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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(getResourceId(), getLastActionTime());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PacRuleEngineAutofixActionsIdentity)) {
            return false;
        }
        PacRuleEngineAutofixActionsIdentity that = (PacRuleEngineAutofixActionsIdentity) obj;
        return Objects.equals(getResourceId(), that.getResourceId())
                && Objects
                        .equals(getLastActionTime(), that.getLastActionTime());
    }
}
