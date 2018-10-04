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

import static com.google.common.collect.Lists.newArrayList;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "/secure", consumes = "application/json", produces = "application/json")
@RestController
@RequestMapping("/secure")
public class AdminController {

	@ApiOperation(httpMethod = "GET", value = "API to get secure names for admin user", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
	@RequestMapping(path = "/admin-names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllAdminNames() {
		try {
			return ResponseUtils.buildSucessResponse(newArrayList("manu", "danny"));
		} catch (Exception exception) {
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	@ApiOperation(httpMethod = "GET", value = "API to get secure names for normal user", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
	@RequestMapping(path = "/normal-names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllNormalNames() {
		try {
			return ResponseUtils.buildSucessResponse(newArrayList("nidhish", "kiran"));
		} catch (Exception exception) {
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}
