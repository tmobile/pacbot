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
package com.tmobile.pacman.api.asset.domain;

import java.util.Map;

/**
 * The Class EventInfoResponse.
 */
public class EventInfoResponse {

    /** The event info. */
    Map<String, Object> eventInfo;

    /**
     * Instantiates a new event info response.
     */
    public EventInfoResponse() {
        super();
    }

    /**
     * Instantiates a new event info response.
     *
     * @param eventInfo the event info
     */
    public EventInfoResponse(Map<String, Object> eventInfo) {
        super();
        this.eventInfo = eventInfo;
    }

    /**
     * Gets the event info.
     *
     * @return the event info
     */
    public Map<String, Object> getEventInfo() {
        return eventInfo;
    }

    /**
     * Sets the event info.
     *
     * @param eventInfo the event info
     */
    public void setEventInfo(Map<String, Object> eventInfo) {
        this.eventInfo = eventInfo;
    }
}
