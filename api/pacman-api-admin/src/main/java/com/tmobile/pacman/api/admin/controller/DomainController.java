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

import com.tmobile.pacman.api.admin.domain.CreateUpdateDomain;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.DomainService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Domain API Controller
 */
@Api(value = "/domains", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/domains")
public class DomainController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(DomainController.class);
	
	@Autowired
	private DomainService domainService;

	/**
     * API to get all domains
     * 
     * @author Nidhish
     * @return All Domains in List
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all domains", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllDomains() {
		try {
			return ResponseUtils.buildSucessResponse(domainService.getAllDomains());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get all domains details
     * 
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All Domains Details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all domain details", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-details", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllDomainDetails(
		@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
		@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
		@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue="", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(domainService.getAllDomainDetails(searchTerm.trim(), page, size));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get domain details by name
     * 
     * @author Nidhish
     * @param domainName - name of the domain
     * @return Domains Details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get domain details by name", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-domain-name", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getDomainByName(
		@ApiParam(value = "provide valid domain name", required = true) @RequestParam(name = "domainName", required = true) String domainName) {
		try {
			return ResponseUtils.buildSucessResponse(domainService.getDomainByName(domainName));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to create new domain
     * 
     * @author Nidhish
     * @param createUpdateDomainDetails - details for creating new domain
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to create new domain", response = Response.class, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createDomain(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid domain details", required = true) @RequestBody(required = true) CreateUpdateDomain createUpdateDomainDetails) {
		try {
			return ResponseUtils.buildSucessResponse(domainService.createDomain(createUpdateDomainDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}	
	
	
	/**
     * API to update domain
     * 
     * @author Nidhish
     * @param createUpdateDomainDetails - details for updating existing domain
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to update domain", response = Response.class, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateDomain(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid domain details", required = true) @RequestBody(required = true) CreateUpdateDomain createUpdateDomainDetails) {
		try {
			return ResponseUtils.buildSucessResponse(domainService.updateDomain(createUpdateDomainDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}	
	
	/**
     * API to get all domain names
     * 
     * @author Nidhish
     * @return All Domain Names
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all domain names", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/domain-names", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllDomainNames() {
		try {
			return ResponseUtils.buildSucessResponse(domainService.getAllDomainNames());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}
