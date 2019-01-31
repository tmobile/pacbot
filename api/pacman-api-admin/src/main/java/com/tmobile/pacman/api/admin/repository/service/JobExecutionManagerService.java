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
package com.tmobile.pacman.api.admin.repository.service;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.tmobile.pacman.api.admin.domain.JobDetails;
import com.tmobile.pacman.api.admin.domain.JobExecutionManagerListProjections;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.JobExecutionManager;

/**
 * JobExecution Manager Service Functionalities
 */
public interface JobExecutionManagerService {

	/**
     * Service to get all job execution managers details
     * 
     * @author NidhishKrishnan
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All JobExecution Manager details list
     */
	public Page<JobExecutionManagerListProjections> getAllJobExecutionManagers(final Integer page, final Integer size, final String searchTerm);

	/**
     * Service to create new Job
     * 
     * @author NidhishKrishnan
     * @param fileToUpload - valid executable rule jar file
     * @param jobDetails - details for creating new job
     * @param userId - valid user id
     * @return Success or Failure response
     * @throws PacManException
     */
	public String createJob(final MultipartFile fileToUpload, final JobDetails jobDetails, final String userId) throws PacManException;
	
	/**
     * Service to update new Job
     * 
     * @author NidhishKrishnan
     * @param fileToUpload - valid executable rule jar file
     * @param jobDetails - details for updating existing job
     * @param userId - valid user id
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateJob(final MultipartFile fileToUpload, final JobDetails jobDetails, final String userId) throws PacManException;

	/**
     * Service to get all list of job id's
     * 
     * @author NidhishKrishnan
     * @return All Job Id list
     */
	public Collection<String> getAllJobIds();
	
	/**
     * Service to get rule by rule Id
     *
     * @author NidhishKrishnan
     * @param jobId - valid job Id
     * @return The job details
     * @throws PacManException
     */
	public JobExecutionManager getByJobId(String jobId) throws PacManException;
	
	/**
     * Service to enable disable job
     * 
     * @author NidhishKrishnan
     * @param ruleId - valid job Id
     * @param action - valid action (disable/ enable)
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String enableDisableJob(final String jobId, final String action, final String userId) throws PacManException;
}
