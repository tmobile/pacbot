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

import java.util.List;

/**
 * The Class SearchFilterAttributeGroup.
 */
public class SearchFilterAttributeGroup extends SearchFilterItem {

    private String type = "searchFilterAttributeGroup";

    List<? extends SearchFilterItem> values;

    /** 
     * Gets the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public List<? extends SearchFilterItem> getValues() {
        return values;
    }

    /**
     * Sets the values.
     *
     * @param values the new values
     */
    public void setValues(List<? extends SearchFilterItem> values) {
        this.values = values;
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
