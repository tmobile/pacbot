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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.CreatePolicyDetails;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.domain.UpdatePolicyDetails;
import com.tmobile.pacman.api.admin.repository.service.PolicyService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Policy API Controller
 */
@Api(value = "/policy", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/policy")
public class PolicyController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(PolicyController.class);

	@Autowired
    private PolicyService policyService;

	/**
     * API to get all policies
     *
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All policies details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all policies", response = Page.class, produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("@securityService.hasPermission(authentication)")
	@RequestMapping(path = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getPolicies(
			@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
			@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
			@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue="", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(policyService.getPolicies(page, size, searchTerm.trim()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get policy by id
     *
     * @author Nidhish
     * @param policyId - valid policy id
     * @return Policy details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get policy by id", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("@securityService.hasPermission(authentication)")
	@RequestMapping(path = "/details-by-id", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getRulesById(
			@ApiParam(value = "provide valid policy id", required = true) @RequestParam("policyId") String policyId) {
		try {
			return ResponseUtils.buildSucessResponse(policyService.getByPolicyId(policyId));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all policy Id's
     *
     * @author Nidhish
     * @return Policy Id's
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all policy ids", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("@securityService.hasPermission(authentication)")
	@RequestMapping(path = "/list-ids", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllPolicyIds() {
		try {
			return ResponseUtils.buildSucessResponse(policyService.getAllPolicyIds());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to update existing policy
     *
     * @author Nidhish
     * @param policyDetails - details for updating existing policy
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to update existing policy", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("@securityService.hasPermission(authentication)")
	@RequestMapping(path = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updatePolicies(
			@ApiParam(value = "provide valid policy details", required = true) @RequestBody(required = true) UpdatePolicyDetails policyDetails) {
		try {
			return ResponseUtils.buildSucessResponse(policyService.updatePolicies(policyDetails));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to create new policy
     *
     * @author Nidhish
     * @param policyDetails - details for creating new policy
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to create new policy", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("@securityService.hasPermission(authentication)")
	@RequestMapping(path = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createPolicies(
			@ApiParam(value = "provide valid policy details", required = true) @RequestBody(required = true) CreatePolicyDetails policyDetails) {
		try {
			return ResponseUtils.buildSucessResponse(policyService.createPolicies(policyDetails));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

