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
 * UpdatePolicyDetails Domain Class
 */
public class UpdatePolicyDetails {

	private String policyId;
	private String policyDesc;
	private String policyUrl;
	private String policyVersion;
	private String resolution;

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyDesc() {
		return policyDesc;
	}

	public void setPolicyDesc(String policyDesc) {
		this.policyDesc = policyDesc;
	}

	public String getPolicyUrl() {
		return policyUrl;
	}

	public void setPolicyUrl(String policyUrl) {
		this.policyUrl = policyUrl;
	}

	public String getPolicyVersion() {
		return policyVersion;
	}

	public void setPolicyVersion(String policyVersion) {
		this.policyVersion = policyVersion;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
}
