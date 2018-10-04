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
package com.tmobile.pacman.api.asset.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Class SearchFilterAttribute.
 */
public class SearchFilterAttribute extends SearchFilterItem {

    private String type = "searchFilterAttribute";

    @ApiModelProperty(hidden = true)
    long count;
    
    boolean applied;
    
    SearchFilterAttributeGroup groupBy;

    /**
     * Gets the group by.
     *
     * @return the group by
     */
    public SearchFilterAttributeGroup getGroupBy() {
        return groupBy;
    }

    /**
     * Sets the group by.
     *
     * @param groupBy the new group by
     */
    public void setGroupBy(SearchFilterAttributeGroup groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * Sets the count.
     *
     * @param count the new count
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     * Checks if is applied.
     *
     * @return true, if is applied
     */
    public boolean isApplied() {
        return applied;
    }

    /**
     * Sets the applied.
     *
     * @param applied the new applied
     */
    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

}
