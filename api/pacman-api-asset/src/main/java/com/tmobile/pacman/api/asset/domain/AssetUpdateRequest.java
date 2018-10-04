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
 * The Class AssetUpdateRequest.
 */
public class AssetUpdateRequest {

    private String ag;
    
    private String targettype;
    
    private String update_by;
    
    private Map<String, Object> resources;
    
    private List<Map<String, Object>> updates;

    /**
     * Gets the resources.
     *
     * @return the resources
     */
    public Map<String, Object> getResources() {
        return resources;
    }

    /**
     * Sets the resources.
     *
     * @param resources the resources
     */
    public void setResources(Map<String, Object> resources) {
        this.resources = resources;
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
     * Gets the targettype.
     *
     * @return the targettype
     */
    public String getTargettype() {
        return targettype;
    }

    /**
     * Sets the targettype.
     *
     * @param targettype the new targettype
     */
    public void setTargettype(String targettype) {
        this.targettype = targettype;
    }

    /**
     * Gets the update by.
     *
     * @return the update by
     */
    public String getUpdateBy() {
        return update_by;
    }

    /**
     * Sets the update by.
     *
     * @param update_by the new update by
     */
    public void setUpdateBy(String update_by) {
        this.update_by = update_by;
    }

    /**
     * Gets the updates.
     *
     * @return the updates
     */
    public List<Map<String, Object>> getUpdates() {
        return updates;
    }

    /**
     * Sets the updates.
     *
     * @param updates the updates
     */
    public void setUpdates(List<Map<String, Object>> updates) {
        this.updates = updates;
    }
}
