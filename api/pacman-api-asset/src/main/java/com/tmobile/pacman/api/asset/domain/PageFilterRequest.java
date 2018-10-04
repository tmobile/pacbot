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

import java.util.Map;

/**
 * The Class PageFilterRequest.
 */
public class PageFilterRequest {

    private int from;
    
    private int size;
    
    private String searchText;
    
    private Map<String, String> filter;

    /**
     * Gets the from.
     *
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the new from
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size the new size
     */
    public void setSize(int size) {
        this.size = size;
    }

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
     * Gets the search text.
     *
     * @return the search text
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the search text.
     *
     * @param searchText the new search text
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
