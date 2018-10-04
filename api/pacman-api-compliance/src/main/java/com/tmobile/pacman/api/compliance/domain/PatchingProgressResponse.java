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
  Modified Date: Mar 20, 2018

 **/
package com.tmobile.pacman.api.compliance.domain;

import java.util.List;
import java.util.Map;
/**
 * The Class PatchingProgressResponse.
 */
public class PatchingProgressResponse {

    /** The ag. */
    String ag;

    /** The resource type. */
    String resourceType;

    /** The total assets. */
    long totalAssets;

    /** The year. */
    int year;

    /** The quarter. */
    int quarter;

    /** The patching progress. */
    List<Map<String, Object>> patchingProgress;

    /**
     * Instantiates a new patching progress response.
     */
    public PatchingProgressResponse() {
        super();
    }

    /**
     * Instantiates a new patching progress response.
     *
     * @param ag the ag
     * @param resourceType the resource type
     * @param totalAssets the total assets
     * @param year the year
     * @param quarter the quarter
     * @param patchingProgress the patching progress
     */
    public PatchingProgressResponse(String ag, String resourceType,
            long totalAssets, int year, int quarter,
            List<Map<String, Object>> patchingProgress) {
        super();
        this.ag = ag;
        this.resourceType = resourceType;
        this.totalAssets = totalAssets;
        this.year = year;
        this.quarter = quarter;
        this.patchingProgress = patchingProgress;
    }

    /**
     * Gets the quarter.
     *
     * @return the quarter
     */
    public int getQuarter() {
        return quarter;
    }

    /**
     * Sets the quarter.
     *
     * @param quarter the new quarter
     */
    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    /**
     * Gets the patching progress.
     *
     * @return the patching progress
     */
    public List<Map<String, Object>> getPatchingProgress() {
        return patchingProgress;
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
     * Gets the total assets.
     *
     * @return the total assets
     */
    public long getTotalAssets() {
        return totalAssets;
    }

    /**
     * Sets the total assets.
     *
     * @param totalAssets the new total assets
     */
    public void setTotalAssets(long totalAssets) {
        this.totalAssets = totalAssets;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public Number getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the new year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Sets the patching progress.
     *
     * @param patchingProgress the patching progress
     */
    public void setPatchingProgress(List<Map<String, Object>> patchingProgress) {
        this.patchingProgress = patchingProgress;
    }

}
