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
  Modified Date: Nov 15, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;

import java.util.List;
import java.util.Map;


/**
 * The Class TaggingResponse.
 */
public class TaggingResponse {

    /** The response. */
    List<Map<String, Long>> response;

    /**
     * Instantiates a new tagging response.
     */
    public TaggingResponse() {
        super();
    }

    /**
     * Instantiates a new tagging response.
     *
     * @param response the response
     */
    public TaggingResponse(List<Map<String, Long>> response) {
        super();
        this.response = response;
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    public List<Map<String, Long>> getResponse() {
        return response;
    }

    /**
     * Sets the response.
     *
     * @param response the response
     */
    public void setResponse(List<Map<String, Long>> response) {
        this.response = response;
    }

}
