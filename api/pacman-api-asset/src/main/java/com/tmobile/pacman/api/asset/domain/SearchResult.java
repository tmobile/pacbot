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
import java.util.Map;

/**
 * The Class SearchResult.
 */
public class SearchResult {
    
    long total;
    
    List<Map<String, Object>> results;
    
    SearchFilter filter;

    /**
     * Gets the total.
     *
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total the new total
     */
    public void setTotal(long total) {
        this.total = total;
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
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public void setFilter(SearchFilter filter) {
        this.filter = filter;
    }

    /**
     * Gets the results.
     *
     * @return the results
     */
    public List<Map<String, Object>> getResults() {
        return results;
    }

    /**
     * Sets the results.
     *
     * @param results the results
     */
    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }
}
