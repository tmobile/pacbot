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

import java.util.List;
import java.util.Map;
/**
 * The Class ProjectionRequest.
 */
public class ProjectionRequest {

    /** The year. */
    int year;

    /** The resource type. */
    String resourceType;

    /** The quarter. */
    int quarter;

    /** The projection by week. */
    List<Map<String, Object>> projectionByWeek;

    /**
     * Instantiates a new projection request.
     */
    public ProjectionRequest() {
        super();
    }

    /**
     * Instantiates a new projection request.
     *
     * @param year the year
     * @param resourceType the resource type
     * @param quarter the quarter
     * @param projectionByWeek the projection by week
     */
    public ProjectionRequest(int year, String resourceType, int quarter,
            List<Map<String, Object>> projectionByWeek) {
        super();
        this.year = year;
        this.resourceType = resourceType;
        this.quarter = quarter;
        this.projectionByWeek = projectionByWeek;
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
     * Gets the year.
     *
     * @return the year
     */
    public int getYear() {
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
     * Gets the projection by week.
     *
     * @return the projection by week
     */
    public List<Map<String, Object>> getProjectionByWeek() {
        return projectionByWeek;
    }

    /**
     * Sets the projection by week.
     *
     * @param projectionByWeek the projection by week
     */
    public void setProjectionByWeek(List<Map<String, Object>> projectionByWeek) {
        this.projectionByWeek = projectionByWeek;
    }

}
