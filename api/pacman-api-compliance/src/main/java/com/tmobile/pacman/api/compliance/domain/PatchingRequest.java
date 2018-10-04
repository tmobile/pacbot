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

import java.util.Map;
/**
 * The Class PatchingRequest.
 */
public class PatchingRequest {

    /** The year. */
    private int year;
    
    /** The quarter. */
    private int quarter;
    
    /** The ag. */
    private String ag;
    
    /** The filters. */
    private Map<String, String> filters;

    /**
     * Gets the filters.
     *
     * @return the filters
     */
    public Map<String, String> getFilters() {
        return filters;
    }

    /**
     * Sets the filters.
     *
     * @param filters the filters
     */
    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
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

}
