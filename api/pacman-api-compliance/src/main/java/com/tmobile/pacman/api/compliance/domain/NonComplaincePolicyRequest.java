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

/**
 * The Class NonComplaincePolicyRequest.
 */
public class NonComplaincePolicyRequest {

    /** The asset group. */
    String assetGroup;

    /** The from. */
    int from;

    /** The size. */
    int size;

    /**
     * Instantiates a new non complaince policy request.
     */
    public NonComplaincePolicyRequest() {
        super();
    }

    /**
     * Instantiates a new non complaince policy request.
     *
     * @param assetGroup the asset group
     * @param from the from
     * @param size the size
     */
    public NonComplaincePolicyRequest(String assetGroup, int from, int size) {
        super();
        this.assetGroup = assetGroup;
        this.from = from;
        this.size = size;
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
     * Gets the asset group.
     *
     * @return the asset group
     */
    public String getAssetGroup() {
        return assetGroup;
    }

    /**
     * Sets the asset group.
     *
     * @param assetGroup the new asset group
     */
    public void setAssetGroup(String assetGroup) {
        this.assetGroup = assetGroup;
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
}
