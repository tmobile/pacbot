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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.Medal;
import com.tmobile.pacman.api.compliance.domain.PatchingRequest;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseData;
import com.tmobile.pacman.api.compliance.domain.ResponseWithCount;
import com.tmobile.pacman.api.compliance.service.ComplianceService;
import com.tmobile.pacman.api.compliance.service.PatchingService;

/**
 * The Class PatchingController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@ConditionalOnProperty(name="features.patching.enabled")
public class PatchingController implements Constants {
    
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PatchingController.class);

    /** The patching service. */
    @Autowired
    private PatchingService patchingService;
    
    /** The compliance service. */
    @Autowired
    private ComplianceService complianceService;
    
    /**
     * API to get top non-compliant applications along with director details.
     *
     * @param assetGroup name of the asset group
     * @return ResponseEntity<Object> list of non compliant number
     */
    
    @RequestMapping(path = "/v1/patching/topnoncompliantapps", method = RequestMethod.GET)
    public ResponseEntity<Object> getTopNonCompliantApps(@RequestParam(name = "ag", required = true) String assetGroup) {
        ResponseData response = null;
        try {
            response = new ResponseData(patchingService.getNonCompliantNumberForAG(assetGroup));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the top non compliant applications along with executive sponsor details.
     *
     * @param assetGroup name of the asset group
     * @return ResponseEntity<Object> list of top non compliant
     */
    
    @RequestMapping(path = "/v1/patching/topnoncompliantexecs", method = RequestMethod.GET)
    public ResponseEntity<Object> getTopNonCompliantExecs(@RequestParam(name = "ag", required = true) String assetGroup) {
        ResponseData response = null;
        try {
            response = new ResponseData(patchingService.getNonCompliantExecsForAG(assetGroup));
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the compliance details of all instances on all
     *         applications.
     *
     * @param request the request
     * @return ResponseEntity<Object> list of patching details
     */
    
    @RequestMapping(path = "/v1/patching/detail", method = RequestMethod.POST)
    public ResponseEntity<Object> getPatchingDetails(@RequestBody(required = true) Request request) {

        ResponseWithCount response = null;
        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception("From should not be a negative number"));

        }

        String searchText = request.getSearchtext();

        Map<String, String> filter = request.getFilter();
        try {

            List<Map<String, Object>> masterDetailList = patchingService.getPatchingDetails(assetGroup, filter);

            masterDetailList = (List<Map<String, Object>>) patchingService.filterMatchingCollectionElements(
                    masterDetailList, searchText, true);

            if (masterDetailList.isEmpty()) {
                return ResponseUtils.buildSucessResponse(new ArrayList<Map<String, Object>>());
            }

            if (from >= masterDetailList.size()) {
                return ResponseUtils.buildFailureResponse(new ServiceException("From exceeds the size of list"));
            }

            int endIndex = 0;

            if ((from + size) > masterDetailList.size()) {
                endIndex = masterDetailList.size();
            } else {
                endIndex = from + size;
            }

            if (endIndex == 0) {
                endIndex = masterDetailList.size();
            }
            // from - inclusive, endIndex - exclusive
            List<Map<String, Object>> subDetailList = masterDetailList.subList(from, endIndex);

            response = new ResponseWithCount(subDetailList, masterDetailList.size());

        } catch (ServiceException e) {
            LOGGER.error(PATCHING_EXCEPTION, e.getMessage());
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the patching progress for entered quarter and year.
     *
     * @param request the request
     * @return ResponseEntity<Object> list of patching progress
     */
    
    @RequestMapping(path = "/v1/patching/progress", method = RequestMethod.POST)
    public ResponseEntity<Object> getPatchingProgress(@RequestBody(required = true) PatchingRequest request) {

        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();
        int quarter = request.getQuarter();
        int year = request.getYear();

        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(ASSET_MANDATORY));
        }

        if (year == 0) {
            year = patchingService.getOngoingYear();
        }
        if (quarter == 0) {
            quarter = patchingService.getOngoingQuarter();
        }

        if (!(quarter >= 1 && quarter <= FOUR)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(
                    "Quarter number should be one out of 1,2,3,4"));
        }

        if (!(year >= START_YEAR && year <= LAST_YEAR)) {
            return ResponseUtils.buildFailureResponse(new ServiceException("Year should be in between 1900 and 2100"));
        }

        try {
            Map<String, Object> patchProgressList = patchingService.getPatchingProgress(assetGroup, year, quarter);
            response.put("response", patchProgressList);
        } catch (ServiceException e) {
            LOGGER.error("ServiceException in getPatchingProgress ",e);
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the quarters(and the containing year) for which
     *         patching data is available.
     *
     * @param assetGroup name of the asset group
     * @return ResponseEntity<Object> list of quarters with patching data
     */
    
    @RequestMapping(path = "/v1/patching/quarters", method = RequestMethod.POST)
    public ResponseEntity<Object> getQuartersWithPatchingData(String assetGroup) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> quarterList = patchingService.getQuartersWithPatchingData(assetGroup);
            response.put("response", quarterList);
        } catch (ServiceException e) {
            return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }
    
    /**
     * Gets the patching percentage for given ag for given quarter
     *
     * @param ag name of the asset group
     * @return ResponseEntity<Object> rating info
     */
    
    @RequestMapping(path = "/v1/patching/rating", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatchingDataForAgForQuarter(String ag, int quarter, int year) {
        Map<String, Object> response = new HashMap<>();

        try {
            Medal medal = patchingService.getStarRatingForAgPatching(ag,quarter,year);
            response.put("response", medal);
        } catch (Exception e) {
            return ResponseUtils.buildSucessResponse(response);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

}
