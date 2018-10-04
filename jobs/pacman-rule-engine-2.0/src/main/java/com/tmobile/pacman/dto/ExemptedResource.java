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
 * The Class ExemptedResource.
 *
 * @author kkumar
 */
public class ExemptedResource {

    /** The resource id. */
    String resourceId;

    /** The exemption expiry date. */
    String exemptionExpiryDate;

    /** The exemption reason. */
    String exemptionReason;

    /**
     * Instantiates a new exempted resource.
     *
     * @param resourceAttributes the resource attributes
     */
    public ExemptedResource(Map<String, String> resourceAttributes) {
        super();
        this.resourceId = resourceAttributes.get("");
        this.exemptionExpiryDate = resourceAttributes.get("expiryDate");
        ;
        this.exemptionReason = resourceAttributes.get("");
        ;
    }

    /**
     * Instantiates a new exempted resource.
     *
     * @param resourceId the resource id
     * @param exemptionExpiryDate the exemption expiry date
     * @param exemptionReason the exemption reason
     */
    public ExemptedResource(String resourceId, String exemptionExpiryDate, String exemptionReason) {
        super();
        this.resourceId = resourceId;
        this.exemptionExpiryDate = exemptionExpiryDate;
        this.exemptionReason = exemptionReason;
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
     * Gets the exemption expiry date.
     *
     * @return the exemption expiry date
     */
    public String getExemptionExpiryDate() {
        return exemptionExpiryDate;
    }

    /**
     * Sets the exemption expiry date.
     *
     * @param exemptionExpiryDate the new exemption expiry date
     */
    public void setExemptionExpiryDate(String exemptionExpiryDate) {
        this.exemptionExpiryDate = exemptionExpiryDate;
    }

    /**
     * Gets the exemption reason.
     *
     * @return the exemption reason
     */
    public String getExemptionReason() {
        return exemptionReason;
    }

    /**
     * Sets the exemption reason.
     *
     * @param exemptionReason the new exemption reason
     */
    public void setExemptionReason(String exemptionReason) {
        this.exemptionReason = exemptionReason;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return resourceId + "" + exemptionExpiryDate + "" + exemptionReason;
    }

}
