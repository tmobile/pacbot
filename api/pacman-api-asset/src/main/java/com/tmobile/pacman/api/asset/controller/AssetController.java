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
package com.tmobile.pacman.api.asset.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.asset.domain.AssetUpdateRequest;
import com.tmobile.pacman.api.asset.domain.Response;
import com.tmobile.pacman.api.asset.model.DefaultUserAssetGroup;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * The controller layer for  Assets which has methods to fetch the asset related info.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class AssetController {

    @Autowired
    AssetService assetService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);

    /**
     * Fetches all the target types for the given asset group. 
     *
     * @param assetGroup name of the asset group
     * @param domain the domain of asset group
     * assetGroup is mandatory, domain is optional.
     * 
     * @return list of target types.
     */
    @GetMapping(value = "/v1/list/targettype")
    public ResponseEntity<Object> getListOfTargetTypes(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "domain", required = false) String domain,
    	@RequestParam(name = "provider", required = false) String provider) {
        Map<String, Object> targetTypesResponse = new HashMap<>();
        List<Map<String, Object>> targetTypes = assetService.getTargetTypesForAssetGroup(assetGroup, domain, provider);
        if (targetTypes.isEmpty()) {
            return ResponseUtils.buildFailureResponse(new Exception(
                    "No target types found for the asset group . Please check the asset group configuration"));
        } else {
            targetTypesResponse.put("ag", assetGroup);
            targetTypesResponse.put("targettypes", targetTypes);
            return ResponseUtils.buildSucessResponse(targetTypesResponse);
        }
    }

    /**
     * Fetches all the applications for the particular asset group.
     *
     * @param assetGroup name of the asset group
     * @param domain the domain of asset group
     * asssetGroup is mandatory, domain is optional.
     * 
     * @return list of applications.
     */
    @GetMapping(value = "/v1/list/application")
    public ResponseEntity<Object> getListOfApplications(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "domain", required = false) String domain) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> appNames = assetService.getApplicationsByAssetGroup(assetGroup, domain);
            response.put("ag", assetGroup);
            response.put("applications", appNames);
            return ResponseUtils.buildSucessResponse(response);
        } catch (Exception e) {
            LOGGER.error("Error fetching application for asset group " + assetGroup, e);
            return ResponseUtils.buildFailureResponse(new Exception(
                    "No applications found for the asset group . Please check the asset group configuration"));
        }
    }

    /**
     * Fetches all the environments for the given asset group
     *
     * @param assetGroup name of the asset group
     * @param application name of the application
     * @param domain the domain of asset group
     * asssetGroup is mandatory, application, domain is optional.
     * 
     * @return list of environments.
     */
    @GetMapping(value = "/v1/list/environment")
    public ResponseEntity<Object> getListOfEnvironments(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "domain", required = false) String domain) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> environments = assetService.getEnvironmentsByAssetGroup(assetGroup, application,
                domain);
        if (environments.isEmpty()) {
            return ResponseUtils
                    .buildFailureResponse(new Exception(
                            "No environments found for the asset group /application . Please check the asset group configuration"));
        } else {
            response.put("ag", assetGroup);
            if (application != null) {
                response.put("application", application);
            }
            response.put("environments", environments);
            return ResponseUtils.buildSucessResponse(response);
        }
    }

    /**
     * Fetches all the asset groups and its name, display name, description,
     * type, createdby and domains
     *
     * @return list of asset group details.
     */
    @GetMapping(value = "/v1/list/assetgroup")
    public ResponseEntity<Object> getAllAssetGroups() {
        try {
            List<Map<String, Object>> assetGroupDetails = assetService.getAllAssetGroups();
            return ResponseUtils.buildSucessResponse(assetGroupDetails);
        } catch (Exception exception) {
            return ResponseUtils.buildFailureResponse(exception);
        }
    }

    /**
     * Fetches all the details of the asset group - name, display name,
     * description, type, createdby, appcount, assetcount and domains.
     *
     * @param assetGroup name of the asset group
     * asssetGroup is mandatory
     * 
     * @return asset group info.
     */
    @GetMapping(value = "/v1/assetgroup")
    public ResponseEntity<Object> getAssetGroupInfo(@RequestParam(name = "ag", required = true) String assetGroup) {
        Map<String, Object> assetGroupInfo = assetService.getAssetGroupInfo(assetGroup);
        if (assetGroupInfo.isEmpty()) {
            return ResponseUtils.buildFailureResponse(new Exception("No Asset Group found"));
        } else {
            return ResponseUtils.buildSucessResponse(assetGroupInfo);
        }
    }

    /**
     * Fetches the default asset group the user has saved for the given asset
     * group.
     *
     * @param userId id of the user
     * userId is mandatory
     * 
     * @return asset group name.
     */
    @ApiOperation(httpMethod = "GET", value = "Get User Default Asset Group")
    @GetMapping(value = "/v1/list/user-default-assetgroup")
    public ResponseEntity<Object> getUserDefaultAssetGroup(
            @ApiParam(value = "Provide the User Id for getting the Default Asset Group", required = true) @RequestParam(name = "userId", required = true) String userId) {
        String userDefaultAssetGroup = assetService.getUserDefaultAssetGroup(userId);
        if (userDefaultAssetGroup.isEmpty()) {
            return ResponseUtils.buildFailureResponse(new Exception("No Default Asset Group found"));
        } else {
            return ResponseUtils.buildSucessResponse(userDefaultAssetGroup);
        }
    }

    /**
     * Save/update asset group details in DB.Saves default asset group for the
     * user id.
     *
     * @param defaultUserAssetGroup This request expects userid and assetgroup name as mandatory.
     * 
     * @return boolean as updated status.
     */
    @ApiOperation(httpMethod = "POST", value = "Save or Update User Asset Groups")
    @PostMapping(value = "/v1/save-or-update/assetgroup")
    public ResponseEntity<Object> saveOrUpdateAssetGroup(
            @ApiParam(value = "Provide Default Asset Group and the User Id", required = true) @RequestBody(required = true) DefaultUserAssetGroup defaultUserAssetGroup) {
        boolean isSavedOrUpdated = assetService.saveOrUpdateAssetGroup(defaultUserAssetGroup);
        if (isSavedOrUpdated) {
            return ResponseUtils.buildSucessResponse(HttpStatus.OK);
        } else {
            return ResponseUtils.buildFailureResponse(new Exception("Default User Asset Group Saving Failed"));
        }
    }

    /**
     * Saves the recently viewed asset group for the user id.
     *
     * @param assetGroup  name of the asset group
     * @param userId id of the user
     * assetGroup and UserId is mandatory
     * 
     * @return updated list of asset group for the userId.
     */
    @ApiOperation(httpMethod = "POST", value = "Save or Append User Recently Viewed Asset Groups")
    @PostMapping(value = "/v1/appendToRecentlyViewedAG")
    public ResponseEntity<Object> appendToRecentlyViewedAG(
            @RequestParam(name = "userId", required = true) String userId,
            @RequestParam(name = "ag", required = true) String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup) || Strings.isNullOrEmpty(userId)) {
            return ResponseUtils.buildFailureResponse(new Exception("Asset group/userId is Mandatory"));
        }
        Response response;
        try {
            response = new Response(assetService.saveAndAppendToRecentlyViewedAG(userId, assetGroup));
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the config details for the given resourceId and config type.
     *
     * @param resourceId id of the resource
     * @param configType type of the config
     * resourceId and configType is mandatory
     * 
     * @return config details as string.
     */
    @ApiOperation(httpMethod = "GET", value = "Retrieve Asset Config for the given resourceId and configType")
    @GetMapping(value = "/v1/retrieve-asset-config")
    public ResponseEntity<Object> retrieveAssetConfig(
            @RequestParam(name = "resourceId", required = true) String resourceId,
            @RequestParam(name = "configType", required = true) String configType) {
        String assetConfig = assetService.retrieveAssetConfig(resourceId, configType);
        if (assetConfig.isEmpty()) {
            return ResponseUtils
                    .buildFailureResponse(new Exception(
                            "No Configuration found for the given resourceId and configType. Please check the resourceId and configType"));
        } else {
            return ResponseUtils.buildSucessResponse(assetConfig);
        }
    }

    /**
     * Saves the config details for the given resourceId.
     *
     * @param resourceId id of the resource
     * @param configType type of the config
     * @param config config of the asset
     * resourceId, configType and config is mandatory
     * 
     * @return ResponseEntity.
     */
    @ApiOperation(httpMethod = "POST", value = "Save Asset Configuration")
    @PostMapping(value = "/v1/save-asset-config")
    public ResponseEntity<Object> saveAssetConfig(
            @RequestParam(name = "resourceId", required = true) String resourceId,
            @RequestParam(name = "configType", required = true) String configType,
            @RequestBody(required = true) String config) {
        Integer isAssetConfigSaved = assetService.saveAssetConfig(resourceId, configType, config);
        if (isAssetConfigSaved == 1) {
            return ResponseUtils.buildSucessResponse(HttpStatus.OK);
        } else {
            return ResponseUtils.buildFailureResponse(new Exception("Save Asset Configuration Saving Failed"));
        }
    }

    /**
     * Saves/updates the resource values for given assetgroup and target type.
     * 
     * @param request This AssetUpdateRequest expects asset group and target type as mandatory
     * @return ResponseEntity status of the update
     * 
     * @throws DataException when the update fails. 
     */
    
    @ApiOperation(httpMethod = "POST", value = "Update Asset values")
    @PostMapping(value = "/v1/update-asset")
    public ResponseEntity<Object> updateAsset(@RequestBody(required = true) AssetUpdateRequest request)
            throws DataException {
        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception("Asset group is Mandatory"));
        }
        String targettype = request.getTargettype();
        if (Strings.isNullOrEmpty(targettype)) {
            return ResponseUtils.buildFailureResponse(new Exception("Target type is Mandatory"));
        }
        String updatedBy = request.getUpdateBy();
        if (Strings.isNullOrEmpty(updatedBy)) {
            updatedBy = "";
        }
        Map<String, Object> resources = request.getResources();
        int totalRows = assetService
                .updateAsset(assetGroup, targettype, resources, updatedBy, request.getUpdates());
        if (totalRows > 0) {
            return ResponseUtils.buildSucessResponse(HttpStatus.OK);
        } else {
            return ResponseUtils.buildFailureResponse(new Exception("Update failed"));
        }
    }

    /**
     * Fetches the created date for the give resourceId.
     *
     * @param resourceId id of the resource
     * @param resourceType type of the resource
     * resourceId and resourceType is mandatory
     * 
     * @return created date as string 
     */
    @ApiOperation(httpMethod = "GET", value = "Get Resource Created Date")
    @RequestMapping(path = "/v1/get-resource-created-date", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getResourceCreatedDate(
            @ApiParam(value = "Provide Valid Resource Id", required = true) @RequestParam(name = "resourceId", required = true) String resourceId,
            @ApiParam(value = "Provide Valid Resource Type", required = true) @RequestParam(name = "resourceType", required = false) String resourceType) {
        String response = assetService.getResourceCreatedDate(resourceId, resourceType);
        if (!Strings.isNullOrEmpty(response)) {
            return ResponseUtils.buildSucessResponse(response);
        } else {
            if (response != null) {
                return ResponseUtils.buildFailureResponse(new Exception("failure"), "Resource Creation Date Empty");
            } else {
                return ResponseUtils.buildFailureResponse(new Exception("failure"), "Resource Not Found");
            }
        }
    }
}
