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
  Modified Date: Nov 9, 2017

**/
package com.tmobile.pacman.api.statistics.client.domain;

import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class Response.
 */
public class ResponseVO {

    /** The response list. */
    List<Map<String, Object>> response;

    /**
     * Instantiates a new response.
     */
    public ResponseVO() {
        super();
    }

    /**
     * Instantiates a new response.
     *
     * @param responseList the response list
     */
    public ResponseVO(List<Map<String, Object>> responseList) {
        super();
        this.response = responseList;
    }

    /**
     * Gets the response list.
     *
     * @return the response list
     */
    public List<Map<String, Object>> getResponse() {
        return response;
    }

    /**
     * Sets the response list.
     *
     * @param response the response
     */
    public void setResponseList(List<Map<String, Object>> response) {
        this.response = response;
    }

}
