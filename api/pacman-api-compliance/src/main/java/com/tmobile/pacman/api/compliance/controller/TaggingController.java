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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :santoshi
  Modified Date: Dec 4, 2017

 **/
package com.tmobile.pacman.api.compliance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseDTO;
import com.tmobile.pacman.api.compliance.domain.ResponseData;
import com.tmobile.pacman.api.compliance.domain.ResponseWithCount;
import com.tmobile.pacman.api.compliance.domain.TaggingResponse;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;
import com.tmobile.pacman.api.compliance.service.ComplianceService;
import com.tmobile.pacman.api.compliance.service.TaggingService;

/**
 * The Class TaggingController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class TaggingController implements Constants {

    /** The tagging service. */
    @Autowired
    TaggingService taggingService;

    /** The compliance service. */
    @Autowired
    ComplianceService complianceService;

    /**
     * Get all applications un-tagged assets by application/environment/role/stack tags.
     *
     * @param request the request
     * @return ResponseEntity
     */

    @RequestMapping(path = "/v1/tagging/summarybyapplication", method = RequestMethod.POST)
    @ResponseBody
    
    public ResponseEntity<Object> getUntaggedAssetsByApp(@RequestBody(required = false) Request request) {
        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }
        ResponseWithCount response = null;
        try {
            response = taggingService.getUnTaggedAssetsByApplication(request);

        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets tagging compliance summary.assetGroup as mandatory.If API receives assetGroup as
     * request parameter, it gives the list of tags with
     * un-tagged/tagged/complaincePercentage/name and over all compliance
     *
     * @param assetGroup
     *            the asset group
     * @return ResponseEntity
     */
    
    @RequestMapping(path = "/v1/tagging/compliance", method = RequestMethod.GET)
    public ResponseEntity<Object> taggingSummary(@RequestParam("ag") String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }
        ResponseDTO response = null;

        try {
            response = new ResponseDTO(taggingService.getTaggingSummary(assetGroup));

        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Get un-tagged assets compliance  by target type.Request expects assetGroup as
     * mandatory.If API receives assetGroup as request parameter, it gives the
     * list of target types with
     * un-tagged/tagged/complaincePercentage/name/assetCount. If API receives
     * assetGroup and targetType as request parameter, it gives
     * un-tagged/tagged/complaincePercentage/name/assetCount for that target
     * type
     *
     * @param request
     *            the request
     * @return ResponseEntity .
     */
    
    @RequestMapping(path = "/v1/tagging/summarybytargettype", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> untaggingByTargetTypes(@RequestBody UntaggedTargetTypeRequest request) {
        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }
        ResponseData response = null;

        try {
            response = new ResponseData(taggingService.getUntaggingByTargetTypes(request));

        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets asset count for un-tagged assets by target type.ssetGroup is mandatory & targetType is optional
     *         If API receives assetGroup as request parameter, it gives the
     *         list of target types of un-tagged
     *         applications. If API receives assetGroup and targetType as
     *         request parameter, it gives un-tagged
     *         applications for that target type.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return ResponseEntity
     */

    
    @RequestMapping(path = "/v1/tagging/taggingByApplication", method = RequestMethod.GET)
    public ResponseEntity<Object> taggingByApplication(@RequestParam("ag") String assetGroup,
            @RequestParam(name = "targetType", required = false) String targetType) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }
        TaggingResponse response = null;

        try {
            response = new TaggingResponse(taggingService.taggingByApplication(assetGroup, targetType));

        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }
}
