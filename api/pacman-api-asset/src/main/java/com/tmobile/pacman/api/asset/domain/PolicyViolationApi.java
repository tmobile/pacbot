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

/**
 * The Class PolicyViolationApi.
 */
public class PolicyViolationApi {
    
    private String message;

    private PolicyViolationApiData data;

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public PolicyViolationApiData getData() {
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data the new data
     */
    public void setData(PolicyViolationApiData data) {
        this.data = data;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", data = " + data + "]";
    }
}
