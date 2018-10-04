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

/**
 * The Class SearchCriteria.
 */
public class SearchCriteria {
    
    String ag;
    
    private String domain;
    
    String searchText;
    
    SearchFilter filter;
    
    long from;
    
    long size;
    
    private boolean doNotReturnFilter;
    
    private boolean includeAllAssets;

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

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public SearchFilter getFilter() {
        return filter;
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
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public void setFilter(SearchFilter filter) {
        this.filter = filter;
    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public long getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the new from
     */
    public void setFrom(long from) {
        this.from = from;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size the new size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Checks if is do not return filter.
     *
     * @return true, if is do not return filter
     */
    public boolean isDoNotReturnFilter() {
        return doNotReturnFilter;
    }

    /**
     * Sets the do not return filter.
     *
     * @param doNotReturnFilter the new do not return filter
     */
    public void setDoNotReturnFilter(boolean doNotReturnFilter) {
        this.doNotReturnFilter = doNotReturnFilter;
    }

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain.
     *
     * @param domain the new domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Checks if is include all assets.
     *
     * @return true, if is include all assets
     */
    public boolean isIncludeAllAssets() {
        return includeAllAssets;
    }

    /**
     * Sets the include all assets.
     *
     * @param includeAllAssets the new include all assets
     */
    public void setIncludeAllAssets(boolean includeAllAssets) {
        this.includeAllAssets = includeAllAssets;
    }
}
