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
 * LambdaProperty Domain Class
 */
public class LambdaProperty {

	private String region;
	
	private String targetId;

	private String functionName;

	private String functionArn;

	private String principal;

	private String actionEnabled;

	private String actionDisabled;

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionArn() {
		return functionArn;
	}

	public void setFunctionArn(String functionArn) {
		this.functionArn = functionArn;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getActionEnabled() {
		return actionEnabled;
	}

	public void setActionEnabled(String actionEnabled) {
		this.actionEnabled = actionEnabled;
	}

	public String getActionDisabled() {
		return actionDisabled;
	}

	public void setActionDisabled(String actionDisabled) {
		this.actionDisabled = actionDisabled;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}
