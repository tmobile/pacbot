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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

/**
 * The controller layer for Assets which has methods to return count of assets based on different conditions.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class AssetCountController {

    @Autowired
    AssetService assetService;

    /**
     * Fetches the total count of assets for the particular asset group. If no
     * type is passed, all the assets of valid target type for the asset group
     * is considered.
     *
     * @param assetGroup name of the asset group
     * @param type target type
     * @param domain the domain of asset group
     * assetGroup is mandatory type and domain are optional
     * 
     * @return list of type and its asset count.
     */
    @GetMapping(value = "/v1/count")
    public ResponseEntity<Object> geAssetCount(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "domain", required = false) String domain,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "provider", required = false) String provider) {
        if (type == null) {
            type = "all";
        }
        List<Map<String, Object>> countMap = assetService.getAssetCountAndEnvDistributionByAssetGroup(assetGroup, type, domain, application, provider);
        LongSummaryStatistics totalCount = countMap.stream().collect(Collectors.summarizingLong(map -> (Long) map.get(Constants.COUNT)));
        Map<String, Object> response = new HashMap<>();
        response.put("ag", assetGroup);
        response.put(AssetConstants.ASSET_COUNT, countMap);
        response.put(AssetConstants.ASSET_TYPE, totalCount.getCount());
        response.put(AssetConstants.TOTAL_ASSETS, totalCount.getSum());
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the total asset count for each application for the given target
     * type and asset group.
     *
     * @param assetGroup name of the asset group
     * @param type target type of the asset group
     * assetGroup and type is mandatory
     * 
     * @return list of applications and its asset count.
     */
    @GetMapping(value = "/v1/count/byapplication")
    public ResponseEntity<Object> geAssetCountByTypeAndApplication(
            @RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "type", required = true) String type) {

        if (Util.isValidTargetType(assetGroup, type)) {
            List<Map<String, Object>> countMap = null;
            try {
                countMap = assetService.getAssetCountByApplication(assetGroup, type);
            } catch (Exception e) {
                return ResponseUtils.buildFailureResponse(e);
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("ag", assetGroup);
            response.put("type", type);
            response.put(AssetConstants.ASSET_COUNT, countMap);
            return ResponseUtils.buildSucessResponse(response);
        } else {
            return ResponseUtils.buildFailureResponse(new Exception("Invalid Target type for the assetgroup "));
        }
    }

    /**
     * Fetches the total asset count for each environment for the given target
     * type and asset group.
     *
     * @param assetGroup  name of the asset group
     * @param type target type of the asset group
     * @param application application needed for the count
     * assetGroup and type is mandatory and application is optional
     * 
     * @return list of environment and its asset count.
     */
    @GetMapping(value = "/v1/count/byenvironment")
    public ResponseEntity<Object> geAssetCountByTypeEnvironment(
            @RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "type", required = true) String type) {

        if (Util.isValidTargetType(assetGroup, type)) {
            List<Map<String, Object>> countMap = null;
            try {
                countMap = assetService.getAssetCountByEnvironment(assetGroup, application, type);
            } catch (Exception e) {
                return ResponseUtils.buildFailureResponse(e);
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("ag", assetGroup);
            response.put("type", type);
            response.put(AssetConstants.ASSET_COUNT, countMap);
            return ResponseUtils.buildSucessResponse(response);
        } else {
            return ResponseUtils.buildFailureResponse(new Exception("Invalid Target type for the assetgroup "));
        }
    }
}
