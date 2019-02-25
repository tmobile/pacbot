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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tmobile.pacman.api.admin.domain.CreateUpdateRuleDetails;
import com.tmobile.pacman.api.admin.domain.Response;
import com.tmobile.pacman.api.admin.repository.service.RuleService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Rule API Controller
 */
@Api(value = "/rule")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/rule")
public class RuleController {

	/** The Constant logger. */
	private static final Logger log = LoggerFactory.getLogger(RuleController.class);

	@Autowired
    private RuleService ruleService;

	/**
     * API to get all rules
     *
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All Rules details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all rules", response = Page.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/list", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getRules(
			@ApiParam(value = "provide valid page number", required = true) @RequestParam("page") Integer page,
			@ApiParam(value = "provide valid page size", required = true) @RequestParam("size") Integer size,
			@ApiParam(value = "provide valid search term", required = false) @RequestParam(defaultValue="", name = "searchTerm", required = false) String searchTerm) {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.getRules(searchTerm.trim(), page, size));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get rule by id
     *
     * @author Nidhish
     * @param ruleId - valid rule Id
     * @return Rules details
     */
	@ApiOperation(httpMethod = "GET", value = "API to get rule by id", response = Response.class, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/details-by-id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getRulesById(
			@ApiParam(value = "provide valid rule id", required = true) @RequestParam(defaultValue = "", name = "ruleId", required = true) String ruleId) {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.getByRuleId(ruleId));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get AlexaKeywords
     *
     * @author Nidhish
     * @return All AlexaKeywords
     */
	@ApiOperation(httpMethod = "GET", value = "API to get alexa keywords", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/alexa-keywords", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllAlexaKeywords() {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.getAllAlexaKeywords());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to get all Rule Id's
     *
     * @author Nidhish
     * @return All Rule Id's
     */
	@ApiOperation(httpMethod = "GET", value = "API to get all Rule Id's", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/rule-ids", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllRuleIds() {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.getAllRuleIds());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to create new rule
     *
     * @author Nidhish
     * @param fileToUpload - valid executable rule jar file
     * @param createRuleDetails - details for creating new rule
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to create new rule", response = Response.class, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> createRule(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid rule details", required = false) @RequestParam(defaultValue="", value = "file", required = false) MultipartFile fileToUpload, CreateUpdateRuleDetails createRuleDetails) {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.createRule(fileToUpload, createRuleDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to update new rule
     *
     * @author Nidhish
     * @param fileToUpload - valid executable rule jar file
     * @param updateRuleDetails - details for updating existing rule
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to update new rule", response = Response.class, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> updateRule(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid rule details", required = false) @RequestParam(value = "file", required = false) MultipartFile fileToUpload, CreateUpdateRuleDetails updateRuleDetails) {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.updateRule(fileToUpload, updateRuleDetails, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to invoke rule
     *
     * @author Nidhish
     * @param ruleId - valid rule Id
     * @param ruleOptionalParams - valid rule optional parameters which need to be passed while invoking rule
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to invoke rule", response = Response.class, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/invoke", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> invokeRule(
			@ApiParam(value = "provide valid rule id", required = true) @RequestParam("ruleId") String ruleId,
			@ApiParam(value = "provide rule optional params", required = false) @RequestBody(required = false) List<Map<String, Object>> ruleOptionalParams) {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.invokeRule(ruleId, ruleOptionalParams));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}

	/**
     * API to enable disable rule
     *
     * @author Nidhish
     * @param ruleId - valid rule Id
     * @param user - userId who performs the action
     * @param action - valid action (disable/ enable)
     * @return Success or Failure response
     */
	@ApiOperation(httpMethod = "POST", value = "API to enable disable rule", response = Response.class, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/enable-disable", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> enableDisableRule(@AuthenticationPrincipal Principal user,
			@ApiParam(value = "provide valid rule id", required = true) @RequestParam("ruleId") String ruleId,
			@ApiParam(value = "provide valid action", required = true) @RequestParam("action") String action) {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.enableDisableRule(ruleId, action, user.getName()));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
	
	/**
	 * Gets the all rule category.
	 *
	 * @return the all rule category
	 */
	@ApiOperation(httpMethod = "GET", value = "API to get all Rule Category's", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(path = "/categories", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllRuleCategory() {
		try {
			return ResponseUtils.buildSucessResponse(ruleService.getAllRuleCategories());
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return ResponseUtils.buildFailureResponse(new Exception(UNEXPECTED_ERROR_OCCURRED), exception.getMessage());
		}
	}
}

