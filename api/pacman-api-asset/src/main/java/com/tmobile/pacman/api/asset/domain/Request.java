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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

/**
 * The Class Request.
 */
public class Request {
    
    private String searchtext = null;
    
    private int from;
    
    private int size;
    
    private Map<String, String> filter;
    
    private String ag;

    /**
     * this is used to cache the response.
     *
     * @return the key
     */
    public String getKey() {
        return ag
                + searchtext
                + Joiner.on("_").withKeyValueSeparator("-")
                        .join(filter == null ? new HashMap<String, String>() : filter) + from + "" + size;
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
     * Gets the searchtext.
     *
     * @return the searchtext
     */
    public String getSearchtext() {
        return searchtext;
    }

    /**
     * Sets the searchtext.
     *
     * @param searchtext the new searchtext
     */
    public void setSearchtext(String searchtext) {
        this.searchtext = searchtext;
    }

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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClassPojo [searchtext = " + searchtext + ", from = " + from + ", filter = " + filter + ", size = "
                + size + "]";
    }
}
