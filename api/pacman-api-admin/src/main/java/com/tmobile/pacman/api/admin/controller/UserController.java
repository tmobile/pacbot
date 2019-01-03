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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.UserService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * User API Controller
 */
@Api(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/users")
public class UserController {
	
	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
    private UserService userService;
	
	/**
     * API to get user details by email id
     * 
     * @author Nidhish
     * @param emailId - valid email Id
     * @return User details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get user details by email id", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
	@RequestMapping(path = "/list", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getUserByEmailId(
		@ApiParam(value = "provide valid user email id", required = true) @RequestParam(name = "emailId", required = true) String emailId) {
		try {
			return ResponseUtils.buildSucessResponse(userService.getUserByEmailId(emailId));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	
	/**
     * API to get all sign-in users
     * 
     * @author Nidhish
     * @param emailId - valid email Id
     * @return User details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all sign-in users", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
	@RequestMapping(path = "/list-users", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllLoginUsers()  {
		try {
			return ResponseUtils.buildSucessResponse(userService.getAllLoginUsers());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

