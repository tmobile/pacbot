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
/*
 *
 */
package com.tmobile.pacman.api.statistics.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.statistics.StatsConstants;
import com.tmobile.pacman.api.statistics.client.domain.ResponseVO;
import com.tmobile.pacman.api.statistics.service.StatisticsService;

/**
 * The Class StatisticsController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class StatisticsController implements Constants {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);

    /** The stats service. */
    @Autowired
    private StatisticsService statsService;

    /**
     * @author santoshi
     * @param assetGroup
     * @return API description: asssetGroup is mandatory. API returns CPU
     *         utilization for last 7 days for given assetGroup.
     */
    @RequestMapping(path = "/v1/cpu-utilization", method = RequestMethod.GET)
    
    public ResponseEntity<Object> getCPUUtilization(@RequestParam("ag") String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(StatsConstants.ERR_MSG_AG_MANDATORY));
        }
        ResponseVO response;
        try {
            response = new ResponseVO(statsService.getCPUUtilization(assetGroup));
        } catch (Exception e) {
            LOGGER.error("Error fetching CPU Utilisation", e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);

    }

    /**
     * Gets the network utilization.
     *
     * @author santoshi
     * @param assetGroup the asset group
     * @return API description: asssetGroup is mandatory. API returns network
     *         utilization for last 7 days for given assetGroup.
     */
    @RequestMapping(path = "/v1/network-utilization", method = RequestMethod.GET)
    
    public ResponseEntity<Object> getNetworkUtilization(@RequestParam("ag") String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(StatsConstants.ERR_MSG_AG_MANDATORY));
        }
        ResponseVO response;
        try {
            response = new ResponseVO(statsService.getNetworkUtilization(assetGroup));
        } catch (Exception e) {
            LOGGER.error("Error fetching Nework Utilisation", e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);

    }

    /**
     * Gets the disk utilization.
     *
     * @author santoshi
     * @param assetGroup the asset group
     * @return API description: asssetGroup is mandatory. API returns disk
     *         utilization for last 7 days for given assetGroup.
     */
    @RequestMapping(path = "/v1/disk-utilization", method = RequestMethod.GET)
    
    public ResponseEntity<Object> getDiskUtilization(@RequestParam("ag") String assetGroup) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(StatsConstants.ERR_MSG_AG_MANDATORY));
        }
        ResponseVO response;
        try {
            response = new ResponseVO(statsService.getDiskUtilization(assetGroup));
        } catch (Exception e) {
            LOGGER.error("Error fetching Disk Utilisation", e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);

    }

    /**
     * Gets the stats details.
     *
     * @return the stats details
     */
    @RequestMapping(path = "/v1/statsdetails", method = RequestMethod.GET)
    @ResponseBody
    
    public ResponseEntity<Object> getStatsDetails() {
        ResponseVO response;
        try {
            response = new ResponseVO(statsService.getStats());

        } catch (Exception e) {
            LOGGER.error("Error fetching stats details", e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the autofix stats.
     *
     * @return the autofix stats
     */
    @RequestMapping(path = "/v1/autofixstats", method = RequestMethod.GET)
    @ResponseBody
    
    public ResponseEntity<Object> getAutofixStats() {
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        try {
            response.put("summary", statsService.getAutofixStats());

        } catch (Exception e) {
            LOGGER.error("Error fetching Auto fix stats", e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

}
