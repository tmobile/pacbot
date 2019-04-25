/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Dec 26, 2018
  
**/
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
package com.tmobile.pacman.reactors.dto;

import com.google.gson.JsonObject;

/**
 * @author kkumar28
 *
 */
public class PacEvent {
    
    
    
    private String eventId;
    
    private String messageId;
    
    private String accountId;
    
    private String eventName;
    
    private JsonObject eventData;
    
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return eventName +"--->"+ eventData;
    }
    /**
     * @return
     */
    public String getEventName() {
        return eventName;
    }
    /**
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    /**
     * @return
     */
    public JsonObject getEventData() {
        return eventData;
    }
    /**
     * @param eventData
     */
    public void setEventData(JsonObject eventData) {
        this.eventData = eventData;
    }
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
