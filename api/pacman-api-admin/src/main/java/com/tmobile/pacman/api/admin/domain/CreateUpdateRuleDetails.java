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
package com.tmobile.pacman.api.admin.domain;

/**
 * CreateUpdateRuleDetails Domain Class
 */
public class CreateUpdateRuleDetails {

	private String ruleId;
	private String policyId;
	private String ruleName;
	private String targetType;
	private String assetGroup;
	private String alexaKeyword;
	private String ruleParams;
	private String ruleFrequency;
	private String ruleExecutable;
	private String ruleRestUrl;
	private String ruleType;
	private String status;
	private String displayName;
	private String dataSource;
	private Boolean isFileChanged;
	private Boolean  isAutofixEnabled;
	private String severity;
	private String category;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(String assetGroup) {
		this.assetGroup = assetGroup;
	}

	public String getAlexaKeyword() {
		return alexaKeyword;
	}

	public void setAlexaKeyword(String alexaKeyword) {
		this.alexaKeyword = alexaKeyword;
	}

	public String getRuleParams() {
		return ruleParams;
	}

	public void setRuleParams(String ruleParams) {
		this.ruleParams = ruleParams;
	}

	public String getRuleFrequency() {
		return ruleFrequency;
	}

	public void setRuleFrequency(String ruleFrequency) {
		this.ruleFrequency = ruleFrequency;
	}

	public String getRuleExecutable() {
		return ruleExecutable;
	}

	public void setRuleExecutable(String ruleExecutable) {
		this.ruleExecutable = ruleExecutable;
	}

	public String getRuleRestUrl() {
		return ruleRestUrl;
	}

	public void setRuleRestUrl(String ruleRestUrl) {
		this.ruleRestUrl = ruleRestUrl;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean isFileExitsOrChanged() {
		return isFileChanged;
	}

	public void setIsFileChanged(Boolean isFileChanged) {
		this.isFileChanged = isFileChanged;
	}

	public Boolean getIsAutofixEnabled() {
		return isAutofixEnabled;
	}
	
	public Boolean isAutofixEnabled() {
		return isAutofixEnabled;
	}

	public void setIsAutofixEnabled(Boolean isAutofixEnabled) {
		this.isAutofixEnabled = isAutofixEnabled;
	}

	public Boolean getIsFileChanged() {
		return isFileChanged;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
