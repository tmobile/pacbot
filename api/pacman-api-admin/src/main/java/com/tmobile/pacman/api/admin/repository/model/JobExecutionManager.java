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
package com.tmobile.pacman.api.admin.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * JobExecutionManager Model Class
 */
@Entity
@Table(name = "cf_JobScheduler", uniqueConstraints = @UniqueConstraint(columnNames = "jobId"))
public class JobExecutionManager {

	@Id
	@Column(name = "jobId", unique = true, nullable = false)
	private String jobId;
	private String jobUUID;
	private String jobName;
	private String jobType;
	private String jobParams;
	private String jobFrequency;
	private String jobExecutable;
	private String jobArn;
	private String status;
	private String userId;
	private Date createdDate;
	private Date modifiedDate;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobUUID() {
		return jobUUID;
	}

	public void setJobUUID(String jobUUID) {
		this.jobUUID = jobUUID;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobParams() {
		return jobParams;
	}

	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public String getJobExecutable() {
		return jobExecutable;
	}

	public void setJobExecutable(String jobExecutable) {
		this.jobExecutable = jobExecutable;
	}

	public String getJobArn() {
		return jobArn;
	}

	public void setJobArn(String jobArn) {
		this.jobArn = jobArn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
