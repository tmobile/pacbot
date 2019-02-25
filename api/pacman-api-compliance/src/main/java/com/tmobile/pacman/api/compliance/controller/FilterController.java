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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.ResponseData;
import com.tmobile.pacman.api.compliance.service.ComplianceService;
import com.tmobile.pacman.api.compliance.service.FilterService;

import io.swagger.annotations.ApiParam;

/**
 * The Class FilterController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class FilterController implements Constants {

    /** The filter service. */
    @Autowired
    private FilterService filterService;

    /** The compliance service. */
    @Autowired
    private ComplianceService complianceService;

    /**
     * Gets the filterName and filterValues based on filter id passed.filterId
     * is mandatory & domain is optional If API receives filterId as request
     * parameter, it gives the details of that filterId which configured in DB
     *
     * @param filterId the filter id
     * @param domain the domain
     * @return ResponseEntity.
     */
    
    @RequestMapping(path = "/v1/filters", method = RequestMethod.GET)
    public ResponseEntity<Object> getFilters(
            @ApiParam(value = "Provide filter 1-issue,2-vulnerability,3-asset,4-compliance", required = false) @RequestParam("filterId") int filterId,
            @RequestParam(name = "domain", required = false) String domain) {
        if (filterId <= 0) {
            return ResponseUtils.buildFailureResponse(new ServiceException("Object filterId is Mandatory"));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getFilters(filterId, domain));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets all policy Id's based on the asset group and domain passed.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return ResponseEntity<Object>.
     */
    
    @RequestMapping(path = "/v1/filters/policies", method = RequestMethod.GET)
    public ResponseEntity<Object> getPolicies(@RequestParam("ag") String assetGroup,
            @RequestParam("domain") String domain) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getPolicies(assetGroup, domain));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the list of regions based on the asset group passed.
     *
     * @param assetGroup the asset group
     * @return ResponseEntity<Object>
     */
    
    @RequestMapping(path = "/v1/filters/regions", method = RequestMethod.GET)
    public ResponseEntity<Object> getRegions(@RequestParam("ag") String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getRegions(assetGroup));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets all account numbers mapped to the asset group.
     *
     * @param assetGroup the asset group
     * @return ResponseEntity<Object>
     */
    
    @RequestMapping(path = "/v1/filters/accounts", method = RequestMethod.GET)
    public ResponseEntity<Object> getAccounts(@RequestParam("ag") String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getAccounts(assetGroup));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the list of rule id and display name.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return ResponseEntity<Object>
     */
    
    @RequestMapping(path = "/v1/filters/rules", method = RequestMethod.GET)
    public ResponseEntity<Object> getRules(@RequestParam("ag") String assetGroup, @RequestParam("domain") String domain) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getRules(assetGroup, domain));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the list of applications.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return  ResponseEntity<Object>
     */

    
    @RequestMapping(path = "/v1/filters/application", method = RequestMethod.GET)
    public ResponseEntity<Object> getListOfApplications(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "domain", required = false) String domain) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getApplications(assetGroup, domain));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

   

    /**
     * Gets the list of environments.AssetGroup is mandatory.application and
     * domain are optional.
     * 
     * @param assetGroup the asset group
     * @param application the application
     * @param domain the domain
     * @return the list of environments
     */
    
    @RequestMapping(path = "/v1/filters/environment", method = RequestMethod.GET)
    public ResponseEntity<Object> getListOfEnvironments(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "domain", required = false) String domain) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getEnvironmentsByAssetGroup(assetGroup, application, domain));

        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the list of target types.assetGroup is mandatory, domain is
     * optional.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return ResponseEntity<Object>
     */

    
    @GetMapping(value = "/v1/filters/targettype")
    public ResponseEntity<Object> getListOfTargetTypes(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "domain", required = false) String domain) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }
        ResponseData response = null;
        try {
            response = new ResponseData(filterService.getTargetTypesForAssetGroup(assetGroup, domain));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

}
