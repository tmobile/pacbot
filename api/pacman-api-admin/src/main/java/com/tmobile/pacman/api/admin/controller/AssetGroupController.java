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

import com.tmobile.pacman.api.admin.domain.CreateUpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupRequest;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.AssetGroupService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * AssetGroup API Controller
 */
@Api(value = "/asset-group", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/asset-group")
public class AssetGroupController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(AssetGroupController.class);

	@Autowired
	private AssetGroupService assetGroupService;

	/**
     * API to get all asset group names
     *
     * @author Nidhish
     * @return The asset group names
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all asset group names", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	
	@RequestMapping(path = "/list-names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllAssetGroupNames() {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupService.getAllAssetGroupNames());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to update existing asset group
     *
     * @author Nidhish
     * @param user - userId who performs the action
     * @param assetGroupDetails - details for updating existing AssetGroup
     * @return Success or failure message
     */
	@ApiOperation(httpMethod = "POST", value = "API to update new asset group", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateAssetGroupDetails(@AuthenticationPrincipal Principal user,
			@RequestBody CreateUpdateAssetGroupDetails assetGroupDetails) {
		try {
			return ResponseUtils
					.buildSucessResponse(assetGroupService.updateAssetGroupDetails(assetGroupDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to create asset group
     *
     * @author Nidhish
     * @param user - userId who performs the action
     * @param assetGroupDetails - details for creating new AssetGroup
     * @return Success or failure message
     */
	@ApiOperation(httpMethod = "POST", value = "API to create asset group", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAssetGroupDetails(@AuthenticationPrincipal Principal user,
			@RequestBody CreateUpdateAssetGroupDetails assetGroupDetails) {
		try {
			return ResponseUtils
					.buildSucessResponse(assetGroupService.createAssetGroupDetails(assetGroupDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all asset group details
     *
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All asset group details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all asset group details", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllAssetGroupDetails(
			@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
			@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
			@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue = "", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupService.getAllAssetGroupDetails(searchTerm.trim(), page, size));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all asset group details by id and dataSource
     *
     * @author Nidhish
     * @param assetGroupId - valid assetGroup Id
     * @param dataSource - valid dataSource name
     * @return All asset group details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all asset group details by id and datasource", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-id-and-datasource", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAssetGroupDetailsByNameAndDataSource(
			@ApiParam(value = "provide valid id", required = true) @RequestParam(defaultValue = "", name = "assetGroupId", required = true) String assetGroupId,
			@ApiParam(value = "provide valid datasource", required = true) @RequestParam(defaultValue = "", name = "dataSource", required = true) String dataSource) {
		try {
			return ResponseUtils.buildSucessResponse(
					assetGroupService.getAssetGroupDetailsByIdAndDataSource(assetGroupId, dataSource));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get asset group details by name
     *
     * @author Nidhish
     * @param assetGroupName - valid assetGroup name
     * @return Asset group details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get asset group details by name", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list-by-name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> findByGroupName(
			@ApiParam(value = "provide valid name", required = true) @RequestParam(defaultValue = "", name = "assetGroupName", required = true) String assetGroupName) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupService.findByGroupName(assetGroupName));
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
     * @param assetGroupDetails - details for deleting existing assetGroup
     * @return Success or failure message
     */
	@ApiOperation(httpMethod = "POST", value = "API to delete asset group", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deleteAssetGroup(@AuthenticationPrincipal Principal user,
			@RequestBody DeleteAssetGroupRequest assetGroupDetails) {
		try {
			return ResponseUtils.buildSucessResponse(assetGroupService.deleteAssetGroup(assetGroupDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}
