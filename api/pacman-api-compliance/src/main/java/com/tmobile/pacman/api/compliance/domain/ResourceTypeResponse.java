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
  Modified Date: Mar 30, 2018

 **/
package com.tmobile.pacman.api.compliance.domain;

import java.util.List;


/**
 * The Class ResourceTypeResponse.
 */
public class ResourceTypeResponse {

    /** The resource type. */
    List<String> resourceType;

    /**
     * Instantiates a new resource type response.
     */
    public ResourceTypeResponse() {
        super();
    }

    /**
     * Instantiates a new resource type response.
     *
     * @param resourceType the resource type
     */
    public ResourceTypeResponse(List<String> resourceType) {
        super();
        this.resourceType = resourceType;
    }

    /**
     * Gets the resource type.
     *
     * @return the resource type
     */
    public java.util.List<String> getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resource type.
     *
     * @param resourceType the new resource type
     */
    public void setResourceType(java.util.List<String> resourceType) {
        this.resourceType = resourceType;
    }

}
