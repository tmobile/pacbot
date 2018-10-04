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

import java.util.Map;

/**
 * The Class UntaggedTargetTypeRequest.
 */
public class UntaggedTargetTypeRequest {

    /** The filter. */
    private Map<String, String> filter;

    /** The ag. */
    private String ag;

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public Map<String, String> getFilter() {
        return filter;
    }

    /**
     * Sets the filter.
     *
     * @param filter the filter
     */
    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
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
