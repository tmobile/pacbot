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

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.tmobile.pacman.api.admin.domain.JobDetails;
import com.tmobile.pacman.api.admin.domain.JobExecutionManagerListProjections;
import com.tmobile.pacman.api.admin.exceptions.PacManException;

/**
 * JobExecution Manager Service Functionalities
 */
public interface JobExecutionManagerService {

	/**
     * Service to get all job execution managers details
     *
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All JobExecution Manager details list
     */
	public Page<JobExecutionManagerListProjections> getAllJobExecutionManagers(final Integer page, final Integer size, final String searchTerm);

	/**
     * Service to create new Job
     *
     * @author Nidhish
     * @param fileToUpload - valid executable rule jar file
     * @param jobDetails - details for creating new job
     * @return Success or Failure response
     * @throws PacManException
     */
	public String createJob(final MultipartFile fileToUpload, final JobDetails jobDetails) throws PacManException;

	/**
     * Service to update new Job
     *
     * @author Nidhish
     * @param fileToUpload - valid executable rule jar file
     * @param jobDetails - details for updating existing job
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateJob(final MultipartFile fileToUpload, final JobDetails jobDetails) throws PacManException;
}
