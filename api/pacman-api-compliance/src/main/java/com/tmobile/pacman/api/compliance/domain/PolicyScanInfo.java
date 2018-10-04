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
 * The Class PolicyScanInfo.
 */
public class PolicyScanInfo {
    
    /** The policy name. */
    private String policyName;
    
    /** The frequency. */
    private String frequency;
    
    /** The severity. */
    private String severity;
    
    /** The last scan. */
    private String lastScan;
    
    /** The issue id. */
    private String issueId;
    
    /** The scan history. */
    private List<ScanResult> scanHistory;
    
    /** The rule id. */
    private String ruleId;
    
    /** The policy id. */
    private String policyId;

    /**
     * Gets the policy name.
     *
     * @return the policy name
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Sets the policy name.
     *
     * @param policyName the new policy name
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * Gets the frequency.
     *
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency.
     *
     * @param frequency the new frequency
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
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
     * Gets the last scan.
     *
     * @return the last scan
     */
    public String getLastScan() {
        return lastScan;
    }

    /**
     * Sets the last scan.
     *
     * @param lastScan the new last scan
     */
    public void setLastScan(String lastScan) {
        this.lastScan = lastScan;
    }

    /**
     * Gets the scan history.
     *
     * @return the scan history
     */
    public List<ScanResult> getScanHistory() {
        return scanHistory;
    }

    /**
     * Sets the scan history.
     *
     * @param scanHistory the new scan history
     */
    public void setScanHistory(List<ScanResult> scanHistory) {
        this.scanHistory = scanHistory;
    }

    /**
     * Gets the rule id.
     *
     * @return the rule id
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Sets the rule id.
     *
     * @param ruleId the new rule id
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * Gets the issue id.
     *
     * @return the issue id
     */
    public String getIssueId() {
        return issueId;
    }

    /**
     * Sets the issue id.
     *
     * @param issueId the new issue id
     */
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    /**
     * Gets the policy id.
     *
     * @return the policy id
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Sets the policy id.
     *
     * @param policyId the new policy id
     */
    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

}

class ScanResult {
    private String result;

    private String date;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
