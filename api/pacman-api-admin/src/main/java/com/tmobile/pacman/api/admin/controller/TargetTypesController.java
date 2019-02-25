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
import java.util.List;

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

import com.tmobile.pacman.api.admin.domain.AttributeValuesRequest;
import com.tmobile.pacman.api.admin.domain.CreateUpdateTargetTypeDetailsRequest;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.model.TargetTypes;
import com.tmobile.pacman.api.admin.repository.service.AssetGroupTargetDetailsService;
import com.tmobile.pacman.api.admin.repository.service.TargetTypesService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * TargetTypes API Controller
 */
@Api(value = "/target-types", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/target-types")
public class TargetTypesController {
	
	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(TargetTypesController.class);

	@Autowired
    private TargetTypesService targetTypesService;
	
	@Autowired
    private AssetGroupTargetDetailsService assetGroupTargetDetailsService;
	
	/**
     * API to get all target types details
     * 
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All TargetType Details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all target types details", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllTargetTypesDetails(
		@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
		@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
		@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue="", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getAllTargetTypeDetails(searchTerm.trim(), page, size));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get all target types categories
     * 
     * @author Nidhish
     * @return All TargetTypes Categories
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all target types categories", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-categories", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllTargetTypesCategories() {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getAllTargetTypesCategories());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all target types by asset group name
     * 
     * @author Nidhish
     * @param assetGroupName - valid assetGroup name
     * @return TargetTypes details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all target types by asset group name", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-asset-group-name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTargetTypesByAssetGroupName(
			@ApiParam(value = "provide valid asset group name", required = true) @RequestParam(defaultValue="", name = "assetGroupName", required = true) String assetGroupName) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupTargetDetailsService.getTargetTypesByAssetGroupName(assetGroupName));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get target types details by name
     * 
     * @author Nidhish
     * @param targetTypeName - valid targetType name
     * @return TargetTypes details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get target types details by name", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-target-type-name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTargetTypesByName(
			@ApiParam(value = "provide valid target type name", required = true) @RequestParam(defaultValue="", name = "targetTypeName", required = true) String targetTypeName) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getTargetTypesByName(targetTypeName));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to create new target type
     * 
     * @author Nidhish
     * @param targetTypesDetails - details for creating new targetType
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to create new target type", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/create", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createTargetType(@AuthenticationPrincipal Principal user, @RequestBody CreateUpdateTargetTypeDetailsRequest targetTypesDetails) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.addTargetTypeDetails(targetTypesDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to update target type
     * 
     * @author Nidhish
     * @param targetTypesDetails - details for updating existing targetType
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to update target type", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/update", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateTargetType(@AuthenticationPrincipal Principal user, @RequestBody CreateUpdateTargetTypeDetailsRequest targetTypesDetails) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.updateTargetTypeDetails(targetTypesDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get all target types by domain
     * 
     * @author Nidhish
     * @param domains - list of domain names
     * @return All TargetTypes details
     */
	@ApiOperation(httpMethod = "POST", value = "API to get all target types by domain", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-domains", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllTargetTypes(
		@ApiParam(value = "provide valid domain list", required = true) @RequestBody(required = true) List<String> domains) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getAllTargetTypesByDomainList(domains));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get all target type names by dataSource name
     * 
     * @author Nidhish
     * @param dataSourceName - valid dataSourceName
     * @return TargetTypes names
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all target type names by datasource name", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-names-by-datasource", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTargetTypesNamesByDataSourceName(
			@ApiParam(value = "provide valid dataSource name", required = true) @RequestParam(defaultValue="", name = "dataSourceName", required = true) String dataSourceName) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getTargetTypesNamesByDataSourceName(dataSourceName));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
     * API to get all target type attributes
     * 
     * @author Nidhish
     * @param targetTypes - list of target type details
     * @return TargetType Attributes
     */
	@ApiOperation(httpMethod = "POST", value = "API to get all target type attributes", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-target-type-attributes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTargetTypeAttributes(
			@ApiParam(value = "provide valid target types details", required = true) @RequestBody(required = true) List<TargetTypes> targetTypes) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getTargetTypeAttributes(targetTypes));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	} 
	
	/**
     * API to get attribute values
     * 
     * @author Nidhish
     * @param attributeValuesRequest - valid attribute value request details
     * @return TargetType Attribute Values
     */
	@ApiOperation(httpMethod = "POST", value = "API to get attribute values", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-target-type-attributes-values", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAttributeValues(
			@ApiParam(value = "provide valid attribute request details", required = true) @RequestBody(required = true) AttributeValuesRequest attributeValuesRequest) {
		try {
			return ResponseUtils.buildSucessResponse(targetTypesService.getAttributeValues(attributeValuesRequest));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

