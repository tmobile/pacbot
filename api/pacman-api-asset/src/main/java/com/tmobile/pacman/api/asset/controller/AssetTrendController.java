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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.ApiOperation;

/**
 * The controller layer which has methods to return trend of assets.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class AssetTrendController {

    @Autowired
    AssetService assetService;

    /**
     * Fetches the asset trends(daily min/max) over the period of last 1 month
     * for the given asset group. From and to can be passed to fetch the asset
     * trends for particular days.
     *
     * @param assetGroup name of the asset group
     * @param type target type of the asset group
     * @param fromDate starting date of the asset trend
     * @param toDate end date of the asset trend needed
     * @param domain domain of the group
     * 
     * @return list of days with its min/max asset count.
     */
    
    @ApiOperation(value = "View the asset trends(daily min/max) over the period of last 1 month", response = Iterable.class)
    @GetMapping(path = "/v1/trend/minmax")
    public ResponseEntity<Object> getMinMaxAssetCount(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "type", required = true) String type,
            @RequestParam(name = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(name = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(name = "domain", required = false) String domain) {
        try {
            Date from = fromDate; 
            Date to = toDate;
            if (from == null && to == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                to = cal.getTime();
                cal.add(Calendar.DATE, Constants.NEG_THIRTY);
                from = cal.getTime();
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("ag", assetGroup);
            response.put("type", type);
            List<Map<String, Object>> trendList = assetService.getAssetMinMax(assetGroup, type, from, to);
            response.put("trend", trendList);
            return ResponseUtils.buildSucessResponse(response);
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
    }
}
