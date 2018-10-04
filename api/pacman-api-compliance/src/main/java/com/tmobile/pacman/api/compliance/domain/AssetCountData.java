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
  Modified Date: Oct 26, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;
/**
 * The Class AssetCountData.
 */
public class AssetCountData {

    /** The assetcount. */
    private AssetCountByAppEnvDTO[] assetcount;

    /** The ag. */
    private String ag;

    /** The type. */
    private String type;

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

    /**
     * Gets the assetcount.
     *
     * @return the assetcount
     */
    public AssetCountByAppEnvDTO[] getAssetcount() {
        return assetcount;
    }

    /**
     * Sets the assetcount.
     *
     * @param assetcount the new assetcount
     */
    public void setAssetcount(AssetCountByAppEnvDTO[] assetcount) {
        this.assetcount = assetcount;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClassPojo [assetcount = " + assetcount + ", ag = " + ag
                + ", type = " + type + "]";
    }
}
