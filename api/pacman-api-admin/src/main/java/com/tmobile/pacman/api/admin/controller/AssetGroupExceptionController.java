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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.CreateAssetGroupExceptionDetailsRequest;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupExceptionRequest;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.AssetGroupExceptionService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * AssetGroupException API Controller
 */
@Api(value = "/asset-group-exception", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/asset-group-exception")
public class AssetGroupExceptionController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(AssetGroupExceptionController.class);

	@Autowired
    private AssetGroupExceptionService assetGroupExceptionService;

	/**
     * API to get all asset group exception details
     *
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All AssetGroupException Details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all asset group exception details", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllAssetGroupExceptionDetails(
		@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
		@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
		@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue  =StringUtils.EMPTY, name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupExceptionService.getAllAssetGroupExceptions(searchTerm.trim(), page, size));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all asset group exception details by exception name and dataSource
     *
     * @author Nidhish
     * @param exceptionName - exception name of the target type
     * @param dataSource - dataSource name of the target type
     * @return All AssetGroup Exception Details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all asset group exception details by exception name and datasource", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-name-and-datasource", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllTargetTypesByExceptionNameAndDataSource(
		@ApiParam(value = "provide valid exception name", required = true) @RequestParam(defaultValue="", name = "exceptionName", required = true) String exceptionName,
		@ApiParam(value = "provide valid datasource", required = true) @RequestParam(defaultValue="", name = "dataSource", required = true) String dataSource) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupExceptionService.getAllTargetTypesByExceptionNameAndDataSource(exceptionName, dataSource));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to configure asset group
     *
     * @author Nidhish
     * @param user - userId who performs the action
     * @param assetGroupDetails - assetGroup details for configuring
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to configure asset group exception", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/configure", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAssetGroupDetails(@AuthenticationPrincipal Principal user, @RequestBody CreateAssetGroupExceptionDetailsRequest assetGroupDetails) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupExceptionService.createAssetGroupExceptions(assetGroupDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to delete asset group
     *
     * @author Nidhish
     * @param user - userId who performs the action
     * @param assetGroupDetails - assetGroup details for deleting
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to delete asset group exception", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/delete", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAssetGroupDetails(@AuthenticationPrincipal Principal user, @RequestBody DeleteAssetGroupExceptionRequest assetGroupDetails) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupExceptionService.deleteAssetGroupExceptions(assetGroupDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all exception names
     *
     * @author Nidhish
     * @return All Exception Names
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all asset group exception names", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/exception-names", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllExceptionNames() {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupExceptionService.getAllExceptionNames());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

