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

import static com.tmobile.pacman.api.admin.common.AdminConstants.JOBID_OR_RULEID_NOT_EMPTY;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.security.Principal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.AdminService;
import com.tmobile.pacman.api.admin.repository.service.JobExecutionManagerService;
import com.tmobile.pacman.api.admin.repository.service.RuleService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "/", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/")
public class AdminController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(RuleController.class);
	
	@Autowired
    private RuleService ruleService;
	
	@Autowired
    private JobExecutionManagerService jobService;
	
	@Autowired
	private AdminService adminService;

	/**
     * API to enable disable rule or job
     * 
     * @author NKrishn3
     * @param ruleId - valid rule or job Id
     * @param user - userId who performs the action
     * @param action - valid action (disable/ enable)
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to enable disable rule or job", response = Response.class, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/enable-disable", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> enableDisableRuleOrJob(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid rule id", required = false) @RequestParam(name = "ruleId", required = false) String ruleId,
			@ApiParam(value = "provide valid job id", required = false) @RequestParam(name = "jobId", required = false) String jobId,
			@ApiParam(value = "provide valid action", required = true) @RequestParam(name = "action", required = true) String action) {
		try {
			
			if (!StringUtils.isBlank(ruleId)) {
				return ResponseUtils.buildSucessResponse(ruleService.enableDisableRule(ruleId, action, user.getName()));
			} else if (!StringUtils.isBlank(jobId)) {
				return ResponseUtils.buildSucessResponse(jobService.enableDisableJob(jobId, action, user.getName()));
			} else {
				return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), JOBID_OR_RULEID_NOT_EMPTY);
			}
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	@ApiOperation(httpMethod = "POST", value = "API to shutdown all operations", response = Response.class, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/operations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> shutDownAllOperations(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "select operation ", required = true) @RequestParam("operation") Operation operation,
			@ApiParam(value = "select job to perform operation ", required = true) @RequestParam("job") Job job) {
		try {
			return ResponseUtils.buildSucessResponse(adminService.shutDownAlloperations(operation.toString(),job.toString()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	@ApiOperation(httpMethod = "GET", value = "API to get status of all jobs", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/system/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> statusOfSystem() {
		try {
			return ResponseUtils.buildSucessResponse(adminService.statusOfSystem());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

enum Job {
	all,job,rule;
}

enum Operation {
	enable,disable;
}