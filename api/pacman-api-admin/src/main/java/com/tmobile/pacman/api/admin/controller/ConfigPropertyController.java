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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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

import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyItem;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest;
import com.tmobile.pacman.api.admin.domain.ConfigTreeNode;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService;
import com.tmobile.pacman.api.admin.util.AdminUtils;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

/**
 * Config Properties API Controller.
 */
@Api(value = "/config-properties", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/config-properties")
public class ConfigPropertyController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(ConfigPropertyController.class);
	
	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

	/** The config property service. */
	@Autowired
	private ConfigPropertyService configPropertyService;

	/**
	 * List all config properties.
	 *
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "GET", value = "API to get all config property values", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @HystrixCommand
	@RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> listAllConfigProperties() {
		try {

			Map<String, List<ConfigTreeNode>> appMap = new HashMap<>();
			List<ConfigTreeNode> appList = new ArrayList<>();
			appList.add(configPropertyService.listProperties());
			appMap.put("applications", appList);
			return ResponseUtils.buildSucessResponse(appMap);

		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}
	
	/**
	 * List config properties.
	 *
	 * @param cfkey the cfkey
	 * @param application the application
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "GET", value = "API to get value(s) for a particular config property key", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
    // @HystrixCommand
    @RequestMapping(path = "/property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listConfigProperties(
            @ApiParam(value = "provide a config key", required = true) @RequestParam(name = "cfkey", required = true) String cfkey,
            @ApiParam(value = "provide level. For e.g. application, api, batch, asset-service etc..", required = false) @RequestParam(name = "application", required = false) String application) {
        try {
            if (StringUtils.isEmpty(cfkey)) {
                return ResponseUtils
                        .buildFailureResponse(new Exception("Config key is mandatory"));
            }
    
            return ResponseUtils.buildSucessResponse(configPropertyService.listProperty(cfkey,application));

        } catch (Exception exception) {
            log.error(UNEXPECTED_ERROR_OCCURRED, exception);
            return ResponseUtils.buildFailureResponse(exception, null, null);
        }
    }

	/**
	 * Adds the config properties.
	 *
	 * @param user the user
	 * @param configPropertyRequest the config property request
	 * @param userMessage the user message
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "POST", value = "API to add a particular config property value", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> addConfigProperties(@AuthenticationPrincipal Principal user,
			@RequestBody(required = true) ConfigPropertyRequest configPropertyRequest,
			@ApiParam(value = "provide a message for this create", required = false) @RequestParam(defaultValue = "", name = "userMessage", required = false) String userMessage) {
		try {

			if (!configPropertyRequest.isRequestComplete()) {
				return ResponseUtils
						.buildFailureResponse(new Exception(AdminConstants.ERROR_CONFIG_MANDATORY));
			}

			if (configPropertyService.isAnyPropertyExisting(configPropertyRequest)) {
				return ResponseUtils.buildFailureResponse(new Exception(
						"One or more of the Config Property already exists in current environment for the given combination of config key and application. Use PUT method to update value."));
			}

			if (!configPropertyService.isAllApplicationsExisting(configPropertyRequest)) {
				return ResponseUtils.buildFailureResponse(new Exception(
						"One or more of the provided application is not supported. This needs to be added to the config property relation first."));
			}
			if (!configPropertyService.isAllCfkeysExisting(configPropertyRequest)) {
				return ResponseUtils.buildFailureResponse(new Exception(
						"One or more of the provided Config key is not supported. This needs to be added to the config property metadata first."));

			}

			return ResponseUtils.buildSucessResponse(
					configPropertyService.addUpdateProperties(configPropertyRequest, user.getName(), userMessage,
							AdminUtils.getFormatedStringDate(DATE_FORMAT, new Date()), false));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}

	/**
	 * Update config properties.
	 *
	 * @param user the user
	 * @param configPropertyRequest the config property request
	 * @param userMessage the user message
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "PUT", value = "API to update a particular config property value", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateConfigProperties(@AuthenticationPrincipal Principal user,
			@RequestBody(required = true) ConfigPropertyRequest configPropertyRequest,
			@ApiParam(value = "provide a message for this update", required = false) @RequestParam(defaultValue = "", name = "userMessage", required = false) String userMessage) {
		try {

			if (!configPropertyRequest.isRequestComplete()) {
				return ResponseUtils
						.buildFailureResponse(new Exception(AdminConstants.ERROR_CONFIG_MANDATORY));
			}

			if (!configPropertyService.isAllPropertiesExisting(configPropertyRequest)) {
				return ResponseUtils.buildFailureResponse(new Exception(
						"One or more of the provided Config Properties does not exist in current environment for the combination of config key and application. Use POST method to 'add' new values."));
			}

			return ResponseUtils.buildSucessResponse(
					configPropertyService.addUpdateProperties(configPropertyRequest, user.getName(), userMessage,
							AdminUtils.getFormatedStringDate(DATE_FORMAT, new Date()), false));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}

	}

	/**
	 * Delete config property.
	 *
	 * @param user the user
	 * @param configPropertyItem the config property item
	 * @param userMessage the user message
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "DELETE", value = "API to delete a particular config property value", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deleteConfigProperty(@AuthenticationPrincipal Principal user,
			@RequestBody(required = true) ConfigPropertyItem configPropertyItem,
			@ApiParam(value = "provide a message for this delete", required = false) @RequestParam(defaultValue = "", name = "userMessage", required = false) String userMessage) {
		try {
			if (!configPropertyItem.isRequestComplete()) {
				return ResponseUtils
						.buildFailureResponse(new Exception(AdminConstants.ERROR_CONFIG_MANDATORY));
			}

			if (!configPropertyService.isPropertyExisting(configPropertyItem)) {
				return ResponseUtils.buildFailureResponse(new Exception(
						"Config Property does not exist in current environment for the given combination of config key and application."));
			}

			return ResponseUtils
					.buildSucessResponse(configPropertyService.deleteProperty(configPropertyItem, user.getName(),
							userMessage, AdminUtils.getFormatedStringDate(DATE_FORMAT, new Date()), false));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}

	/**
	 * List all config property keys.
	 *
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "GET", value = "API to get all config property keys", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "/keys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> listAllConfigPropertyKeys() {
		try {
			return ResponseUtils.buildSucessResponse(configPropertyService.listAllKeys());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}

	/**
	 * List all config property audits.
	 *
	 * @param timestamp the timestamp
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "GET", value = "API to get the audit trail of config property changes", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "/audittrail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> listAllConfigPropertyAudits(
			@ApiParam(value = "provide timestamp in yyyy-MM-dd HH:mm:ss", required = false) @RequestParam(defaultValue = "", name = "timestamp", required = false) String timestamp) {
		try {
			return ResponseUtils.buildSucessResponse(configPropertyService.listAllConfigPropertyAudits(timestamp));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}

	/**
	 * Gets the rollback preview.
	 *
	 * @param timestamp the timestamp
	 * @return the rollback preview
	 */
	@ApiOperation(httpMethod = "GET", value = "API to get a preview of config property rollback to a particular timestamp", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "/rollbackpreview", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getRollbackPreview(
			@ApiParam(value = "provide timestamp in yyyy-MM-dd HH:mm:ss", required = true) @RequestParam(defaultValue = "", name = "timestamp", required = true) String timestamp) {
		try {
			return ResponseUtils.buildSucessResponse(configPropertyService.getRollbackPreview(timestamp));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}

	/**
	 * Do config property rollback to timestamp.
	 *
	 * @param user the user
	 * @param timestamp the timestamp
	 * @param userMessage the user message
	 * @return the response entity
	 */
	@ApiOperation(httpMethod = "PUT", value = "API to rollback config properties to a particular timestamp", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("@securityService.hasPermission(authentication)")
	// @HystrixCommand
	@RequestMapping(path = "/rollback", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> doConfigPropertyRollbackToTimestamp(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide timestamp in yyyy-MM-dd HH:mm:ss", required = true) @RequestParam(defaultValue = "", name = "timestamp", required = true) String timestamp,
			@ApiParam(value = "provide a message for this rollback", required = false) @RequestParam(defaultValue = "", name = "userMessage", required = false) String userMessage) {
		try {
			return ResponseUtils.buildSucessResponse(
					configPropertyService.doConfigPropertyRollbackToTimestamp(timestamp, user.getName(), userMessage));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(exception, null, null);
		}
	}

}
