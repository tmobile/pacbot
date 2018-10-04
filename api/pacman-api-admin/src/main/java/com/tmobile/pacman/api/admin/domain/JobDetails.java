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

import javax.validation.constraints.NotNull;

/**
 * JobDetails Domain Class
 */
public class JobDetails {

	@NotNull
	private String jobName;

	@NotNull
	private String jobDesc;

	@NotNull
	private String jobFrequency;

	@NotNull
	private String jobType;

	@NotNull
	private String jobParams;

	@NotNull
	private String jobExecutable;

	private Boolean isFileChanged;

	public String getJobName() {
		return jobName;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}

	public String getJobType() {
		return jobType;
	}

	public String getJobParams() {
		return jobParams;
	}

	public String getJobExecutable() {
		return jobExecutable;
	}

	public Boolean getIsFileChanged() {
		return isFileChanged;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}

	public void setJobExecutable(String jobExecutable) {
		this.jobExecutable = jobExecutable;
	}

	public void setIsFileChanged(Boolean isFileChanged) {
		this.isFileChanged = isFileChanged;
	}
}
