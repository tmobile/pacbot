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
package com.tmobile.pacman.api.asset.model;

/**
 * The Class DefaultUserAssetGroup.
 */
public class DefaultUserAssetGroup {

    /** The user id. */
    public String userId;
    
    /** The default asset group. */
    public String defaultAssetGroup;

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the default asset group.
     *
     * @return the default asset group
     */
    public String getDefaultAssetGroup() {
        return defaultAssetGroup;
    }

    /**
     * Sets the default asset group.
     *
     * @param defaultAssetGroup the new default asset group
     */
    public void setDefaultAssetGroup(String defaultAssetGroup) {
        this.defaultAssetGroup = defaultAssetGroup;
    }
}
