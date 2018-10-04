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
package com.tmobile.pacman.api.compliance.domain;
/**
 * The Class SevInfo.
 */
public class SevInfo {

    /** The severity. */
    private String severity;
    
    /** The count. */
    private int count;

    /**
     * Instantiates a new sev info.
     *
     * @param severity the severity
     * @param count the count
     */
    public SevInfo(String severity, int count) {
        this.severity = severity;
        this.count = count;
    }

    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity the new severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count.
     *
     * @param count the new count
     */
    public void setCount(int count) {
        this.count = count;
    }

}
