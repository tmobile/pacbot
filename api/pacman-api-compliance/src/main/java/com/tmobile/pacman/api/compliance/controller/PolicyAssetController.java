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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.PolicyScanInfo;
import com.tmobile.pacman.api.compliance.domain.PolicyVialationSummary;
import com.tmobile.pacman.api.compliance.service.PolicyAssetService;
import com.tmobile.pacman.api.compliance.util.CommonUtil;

/**
 * The Class PolicyAssetController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class PolicyAssetController {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAssetController.class);
    
    /** The service. */
    @Autowired
    PolicyAssetService service;

    /**
     * Gets the policy violations.
     *
     * @param resourceId the resource id
     * @param ag the ag
     * @param resourceType the resource type
     * @param searchText the search text
     * @param from the from
     * @param size the size
     * @return the policy violations
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/v1/policyevaluations/{assetGroup}/{resourceType}/{resourceId}")
    @ResponseBody
    
    public ResponseEntity<Object> getPolicyViolations(
            @PathVariable("resourceId") String resourceId,
            @PathVariable("assetGroup") String ag,
            @PathVariable("resourceType") String resourceType,
            @RequestParam(name = "searchtext", required = false) String searchText,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) {
        try {
            Integer iFrom = from == null ? 0 : from;
            Integer iSize = size == null ? 0 : size;

            Map<String, Object> response = new HashMap<>();

            List<PolicyScanInfo> masterDetailList = service
                    .getPolicyExecutionDetails(ag, resourceType, resourceId);

            masterDetailList = (List<PolicyScanInfo>) CommonUtils
                    .filterMatchingCollectionElements(masterDetailList,
                            searchText, true);
            if (masterDetailList.isEmpty()) {
                response.put("response", masterDetailList);
                response.put("total", masterDetailList.size());
                return ResponseUtils.buildSucessResponse(response);
            }

            if (iFrom >= masterDetailList.size()) {
                return ResponseUtils.buildFailureResponse(new Exception(
                        "From exceeds the size of list"));
            }

            int endIndex = 0;

            if (iSize == 0) {
                iSize = masterDetailList.size();
            }

            if ((iFrom + iSize) > masterDetailList.size()) {
                endIndex = masterDetailList.size();
            } else {
                endIndex = iFrom + iSize;
            }

            List<PolicyScanInfo> subDetailList = masterDetailList.subList(
                    iFrom, endIndex);

            response.put("response", subDetailList);
            response.put("total", masterDetailList.size());

            return ResponseUtils.buildSucessResponse(response);
        } catch (Exception e) {
            LOGGER.error("Error fetching issue Details ",e);
            return ResponseUtils.buildFailureResponse(e);
        }

    }

    /**
     * Gets the policy violation summary.
     *
     * @param resourceId the resource id
     * @param ag the ag
     * @param resourceType the resource type
     * @return the policy violation summary
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @GetMapping(path = "/v1/policyviolations/summary/{assetGroup}/{resourceType}/{resourceId}")
    @ResponseBody
    
    public ResponseEntity<Object> getPolicyViolationSummary(
            @PathVariable("resourceId") String resourceId,
            @PathVariable("assetGroup") String ag,
            @PathVariable("resourceType") String resourceType)
            throws UnsupportedEncodingException {

        String decodedId = CommonUtil.decodeUrl(resourceId);
        try {
            PolicyVialationSummary vialationSummary = service
                    .getPolicyViolationSummary(ag, resourceType, decodedId);
            return ResponseUtils.buildSucessResponse(vialationSummary);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return ResponseUtils.buildFailureResponse(e);
        }

    }
}
