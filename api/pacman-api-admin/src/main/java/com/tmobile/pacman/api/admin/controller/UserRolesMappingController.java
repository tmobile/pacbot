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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.domain.UserRoleConfigRequest;
import com.tmobile.pacman.api.admin.repository.service.UserRolesMappingService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * User Roles Mapping API Controller
 */
@Api(value = "/users-roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/users-roles")
public class UserRolesMappingController {
	
	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(UserRolesMappingController.class);
	
	@Autowired
    private UserRolesMappingService userRolesMappingService;
	
	/**
     * API to get all user roles mappings details
     * 
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All UserRolesMapping Details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all user roles mappings details", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllUserRolesMapping(
		@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
		@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
		@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue="", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(userRolesMappingService.getAllUserRolesMapping(searchTerm.trim(), page, size));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to allocate user roles
     * 
     * @author Nidhish
     * @param user - userId who performs the action
     * @param roleAllocateDetailsRequest - details request for role allocation
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to allocate user roles", response = Response.class,  consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/allocate", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> allocateUserRole(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid role details", required = true)
			@RequestBody(required = true) UserRoleConfigRequest userRoleConfigRequest) {
		try {
			return ResponseUtils.buildSucessResponse(userRolesMappingService.allocateUserRole(userRoleConfigRequest, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

