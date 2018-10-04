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

import java.util.List;
/**
 * The Class PolicyVialationSummary.
 */
public class PolicyVialationSummary {

    /** The total. */
    private int total;
    
    /** The compliance. */
    private double compliance;
    
    /** The severity info. */
    private List<SevInfo> severityInfo;

    /**
     * Gets the total.
     *
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total the new total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the severity info.
     *
     * @return the severity info
     */
    public List<SevInfo> getSeverityInfo() {
        return severityInfo;
    }

    /**
     * Gets the compliance.
     *
     * @return the compliance
     */
    public double getCompliance() {
        return compliance;
    }

    /**
     * Sets the compliance.
     *
     * @param compliance the new compliance
     */
    public void setCompliance(double compliance) {
        this.compliance = compliance;
    }

    /**
     * Sets the severity info.
     *
     * @param severityInfo the new severity info
     */
    public void setSeverityInfo(List<SevInfo> severityInfo) {
        this.severityInfo = severityInfo;
    }

}
