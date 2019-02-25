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
  Author :kkumar
  Modified Date: Dec 19, 2017

 **/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.api.compliance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.service.RecommendedActionService;

import io.swagger.annotations.ApiOperation;

/**
 * The Class RecommendedActionController.
 *
 * @author kkumar
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class RecommendedActionController {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendedActionController.class);

    /** The recommended action service. */
    @Autowired
    private RecommendedActionService recommendedActionService;

    /**
     * Gets the trend for issues.
     *
     * @param targetType the target type
     * @param ruleId the rule id
     * @param dataSource the data source
     * @return the trend for issues
     */
    
    @ApiOperation(value = "get the list of recommended actions for datasource and targetType", response = Iterable.class)
    @RequestMapping(path = "/v1/recommendations/actions", method = RequestMethod.GET)
    public ResponseEntity<Object> getTrendForIssues(
            @RequestParam("targetType") String targetType,
            @RequestParam("ruleId") String ruleId,
            @RequestParam(name = "dataSource", required = false) String dataSource) {
        try {
            return ResponseUtils.buildSucessResponse(recommendedActionService
                    .getRecommendedActions(dataSource, targetType, ruleId));
        } catch (Exception e) {
            LOGGER.error("error fetching recommended actions", e);
            return ResponseUtils.buildFailureResponse(e);
        }
    }

}
