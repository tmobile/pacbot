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
package com.tmobile.pacman.api.auth.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.auth.domain.TokenDetails;
import com.tmobile.pacman.api.auth.domain.UserLoginCredentials;
import com.tmobile.pacman.api.auth.services.ApiService;
import com.tmobile.pacman.api.auth.services.AuthService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author 	NidhishKrishnan
 * @purpose Controller for user authorization functionalities
 * @since	November 10, 2018
 * @version	1.0 
**/
@RestController
public class AuthController {
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private AuthService authService;
	
	@ApiOperation(httpMethod = "POST", value = "Authorize and get Access Tokens", response = Map.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/user/authorize", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> authorize(@ApiParam(value = "Provide valid authorization header", required = true) @RequestHeader(name = "Authorization", required = true) final String authorization) {
		if(authorization.startsWith("bearer ") || authorization.startsWith("Bearer ")) {
			Map<String, Object> response = authService.authorizeUser(authorization.substring(7).trim());
			if (response != null) {
				if(Boolean.parseBoolean(String.valueOf(response.get("success")))) {
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
				}
			} else {
				Map<String, Object> errorResponse = unAuthorizeResponse();
				errorResponse.put("message", "Authorization Failed. Please provide a valid token");
				return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
			}
		} else {
			Map<String, Object> errorResponse = unAuthorizeResponse();
			errorResponse.put("message", "Authorization Failed. Authorization header should begin with \"bearer\"");
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
	}

	@ApiOperation(httpMethod = "POST", value = "Refresh and get new Access Tokens", response = Map.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/user/refresh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> refresh(@ApiParam(value = "Provide valid refresh_token", required = true) @RequestBody final TokenDetails tokenDetails) {
		Map<String, Object> response = apiService.refreshToken(tokenDetails.getRefreshToken());
		if(Boolean.parseBoolean(String.valueOf(response.get("success")))) {
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@ApiOperation(httpMethod = "POST", value = "Login to Auth Server", response = Map.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/user/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> login(@ApiParam(value = "provide valid username and password details", required = true) @RequestBody final UserLoginCredentials credentials) {
		Map<String, Object> response = apiService.login(credentials);
		if (response != null) {
			return response;
		} else {
			return unAuthorizeResponse(); 
		}
	}
	
	@ApiOperation(httpMethod = "GET", value = "Logout User from Auth Server")
	@RequestMapping(value = "/user/logout-session", method = RequestMethod.GET)
	public void logout(@AuthenticationPrincipal Principal principal) {
		apiService.logout(principal);
	}

	@ApiOperation(httpMethod = "GET", value = "Get User Details")
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public Principal user(@AuthenticationPrincipal Principal user) {
		return user;
	}
	
	private Map<String, Object> unAuthorizeResponse() {
		Map<String, Object> unauthroizeRespone = Maps.newHashMap();
		unauthroizeRespone.put("message", "Authentication Failed!!");
		unauthroizeRespone.put("success", false);
		return unauthroizeRespone;
	}
}
