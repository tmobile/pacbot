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
package com.tmobile.pacman.api.compliance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.PatchingProgressResponse;
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;
import com.tmobile.pacman.api.compliance.domain.ProjectionResponse;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.service.ProjectionService;

import io.swagger.annotations.ApiOperation;

/**
 * The Class ProjectionController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class ProjectionController implements Constants {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectionController.class);

    /** The projection service. */
    @Autowired
    private ProjectionService projectionService;


    /**
     * Gets the projection data.targetType,year and quarter are mandatory.If API
     * receives targetType,year and quarter as request parameter, it gives
     * details of projection by week
     *
     * @param targetType the target type
     * @param year the year
     * @param quarter the quarter
     * @return the projection data
     */
    
    @RequestMapping(path = "/v1/getprojection", method = RequestMethod.GET)
    public ResponseEntity<Object> getProjectionData(
            @RequestParam(name = "targettype", required = true) String targetType,
            @RequestParam(name = "year", required = true) int year,
            @RequestParam(name = "quarter", required = true) int quarter) {
        if (Strings.isNullOrEmpty(targetType) || year <= 0 || quarter <= 0) {
            return ResponseUtils.buildFailureResponse(new ServiceException("targetType/year/quarter is Mandatory"));
        }
        if (quarter > FOUR) {
            return ResponseUtils.buildFailureResponse(new ServiceException("Invalid quarter"));
        }

        ProjectionResponse response;
        try {
            response = projectionService.getProjection(targetType, year, quarter);
        } catch (ServiceException e) {
            LOGGER.error(e.toString());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }



    /**
     * Update projection data.projectionRequest expects resourceType, year,
     * quarter as mandatory, projectionByWeek is optional.If API receives
     * resourceType,year and quarter as request parameter, it will save the db
     * with week and projection as zero for those weeks in that given quarter
     * and year. If API receives resourceType,year quarter and projectionByWeek
     * as request parameter, it will update the db with projection as mentioned
     * value for those weeks in that given quarter and year
     *
     * @param projectionRequest the projection request
     * @return the response entity
     * @throws ServiceException the service exception
     */
    
    @ApiOperation(httpMethod = "POST", value = "Save or Update Projection Details")
    @PostMapping(value = "/v1/updateprojection")
    public ResponseEntity<Object> updateProjectionData(@RequestBody ProjectionRequest projectionRequest)
            throws ServiceException {
        if (Strings.isNullOrEmpty(projectionRequest.getResourceType()) || projectionRequest.getYear() <= 0
                || projectionRequest.getQuarter() <= 0) {
            return ResponseUtils.buildFailureResponse(new ServiceException("targetType/year/quarter is mandatory"));
        }
        if (projectionRequest.getQuarter() > FOUR) {
            return ResponseUtils.buildFailureResponse(new ServiceException("Invalid quarter"));
        }
        boolean isDataupdatedtoDB = false;
        try {
            isDataupdatedtoDB = projectionService.updateProjection(projectionRequest);
        } catch (ServiceException e) {
            LOGGER.error(e.toString());
            return ResponseUtils.buildFailureResponse(e);
        }
        if (isDataupdatedtoDB) {
            return ResponseUtils.buildSucessResponse(HttpStatus.OK);
        } else {
            return ResponseUtils.buildFailureResponse(new ServiceException("Projection Details Saving Failed"));
        }
    }

   

    /**
     * Gets the patching and projection progress. assetGroup is mandatory.If API
     * receives assetGroup as request parameter, it will give the list of
     * projection by week data like
     * date/projected/week/totalPtached/patched/totalProjected along with
     * totalAssets,resourceType,quarter and year
     *
     * @param assetGroup the asset group
     * @return the patching and projection progress
     */
    
    @RequestMapping(path = "/v1/getPatchingAndProjectionProgress", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatchingAndProjectionProgress(
            @RequestParam(name = "ag", required = true) String assetGroup) {
        //
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ProjectionResponse response;
        try {
            response = projectionService.getPatchingAndProjectionByWeek(assetGroup);
        } catch (ServiceException e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }



    /**
     * Patching progress by director.request expects assetGroup as mandatory.If
     * API receives assetGroup as request parameter, it will give the list of
     * projection by directors data like director/patched/unpatched/%patched/q2
     * scope along with total assets,resourceType,year and quarter
     * 
     * @param request the request
     * @return the response entity
     */
    
    @RequestMapping(path = "/v1/getPatchingProgressByDirector", method = RequestMethod.POST)
    public ResponseEntity<Object> patchingProgressByDirector(@RequestBody(required = false) Request request) {
        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        PatchingProgressResponse response;
        try {
            response = projectionService.getPatchingProgressByDirector(assetGroup);
        } catch (ServiceException e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    

    /**
     * Patching progress by executive sponsor and assets.
     *
     * @param request the request
     * @return the response entity
     */
    
    @RequestMapping(path = "/v1/getPatchingProgressByExecutiveSponsor", method = RequestMethod.POST)
    public ResponseEntity<Object> patchingProgressByExecutiveSponser(@RequestBody(required = false) Request request) {
        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        PatchingProgressResponse response;
        try {
            response = projectionService.patchProgByExSponsor(assetGroup);
        } catch (ServiceException e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

}
