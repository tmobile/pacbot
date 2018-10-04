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
  Modified Date: Oct 26, 2017

**/
package com.tmobile.pacman.api.statistics.domain;


/**
 * The Class AssetApiData.
 */
public class AssetApiData {

    /** The ag. */
    private String ag;

    /** The applications. */
    private AssetApiName[] applications;

    /** The environments. */
    private AssetApiName[] environments;

    /** The targettypes. */
    private AssetApiName[] targettypes;

    /**
     * Gets the targettypes.
     *
     * @return the targettypes
     */
    public AssetApiName[] getTargettypes() {
        return targettypes;
    }

    /**
     * Sets the targettypes.
     *
     * @param targettypes the new targettypes
     */
    public void setTargettypes(AssetApiName[] targettypes) {
        this.targettypes = targettypes;
    }

    /**
     * Gets the environments.
     *
     * @return the environments
     */
    public AssetApiName[] getEnvironments() {
        return environments;
    }

    /**
     * Sets the environments.
     *
     * @param environments the new environments
     */
    public void setEnvironments(AssetApiName[] environments) {
        this.environments = environments;
    }

    /**
     * Gets the ag.
     *
     * @return the ag
     */
    public String getAg() {
        return ag;
    }

    /**
     * Sets the ag.
     *
     * @param ag the new ag
     */
    public void setAg(String ag) {
        this.ag = ag;
    }

    /**
     * Gets the applications.
     *
     * @return the applications
     */
    public AssetApiName[] getApplications() {
        return applications;
    }

    /**
     * Sets the applications.
     *
     * @param applications the new applications
     */
    public void setApplications(AssetApiName[] applications) {
        this.applications = applications;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClassPojo [applications = " + applications + ", ag = " + ag + ", environments = " + environments
                + ", targettypes = " + targettypes + "]";
    }
}
