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
package com.tmobile.pacman.api.admin.controller;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.security.Principal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tmobile.pacman.api.admin.domain.JobDetails;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.JobExecutionManagerService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * JobExecutionManager API Controller
 */
@Api(value = "/job-execution-manager", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/job-execution-manager")
public class JobExecutionManagerController {
	
	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(JobExecutionManagerController.class);
	
	@Autowired
    private JobExecutionManagerService jobExecutionManagerService;

	/**
     * API to get all Job Execution Managers
     * 
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All JobExecutionManagers details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all Job Execution Managers", response = Page.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllJobExecutionManagers(
			@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
			@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
			@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue="", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(jobExecutionManagerService.getAllJobExecutionManagers(page, size, searchTerm.trim()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to create new job
     * 
     * @author Nidhish
     * @param fileToUpload - job executable jar file
     * @param createJobDetails - details for creating new Job
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to create new job", response = Response.class, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> createJob(@AuthenticationPrincipal Principal user,
			 @ApiParam(value = "provide valid job details", required = false) @RequestParam(defaultValue = StringUtils.EMPTY, value = "file", required = false) MultipartFile fileToUpload, JobDetails createJobDetails) {
		try {
			return ResponseUtils.buildSucessResponse(jobExecutionManagerService.createJob(fileToUpload, createJobDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}	
	
	/**
     * API to update existing job
     * 
     * @author Nidhish
     * @param fileToUpload - job executable jar file
     * @param updateJobDetails - details for updating existing Job
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to update existing job", response = Response.class, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> updateJob(@AuthenticationPrincipal Principal user,
			 @ApiParam(value = "provide valid job details", required = false) @RequestParam(value = "file", required = false) MultipartFile fileToUpload, JobDetails updateJobDetails) {
		try {
			return ResponseUtils.buildSucessResponse(jobExecutionManagerService.updateJob(fileToUpload, updateJobDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}	
	
	/**
     * API to get all Job Id list
     * 
     * @author Nidhish
     * @return All Job Id list
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all Job Id's", response = Page.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/job-ids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllJobIds() {
		try {
			return ResponseUtils.buildSucessResponse(jobExecutionManagerService.getAllJobIds());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get job by id
     * 
     * @author Nidhish
     * @param jobId - valid job Id
     * @return Job details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get job by id", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/details-by-id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getRulesById(
			@ApiParam(value = "provide valid job id", required = true) @RequestParam(defaultValue = StringUtils.EMPTY, name = "jobId", required = true) String jobId) {
		try {
			return ResponseUtils.buildSucessResponse(jobExecutionManagerService.getByJobId(jobId));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

