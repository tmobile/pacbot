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
  Modified Date: Jan 31, 2018

 **/
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

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.ResponseDTO;
import com.tmobile.pacman.api.compliance.service.ComplianceService;
import com.tmobile.pacman.api.compliance.service.FAQService;
/**
 * The Class FAQSController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class FAQSController {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FAQSController.class);

    /** The faq service. */
    @Autowired
    private FAQService faqService;

    /** The compliance service. */
    private ComplianceService complianceService;

    /**
     * API to get frequently asked questions and answers for entered widget Id.
     *
     * @param widgetId the widget id
     * @param domainId the domain id
     * @return FAQ Details
     */
    
    @RequestMapping(path = "/v1/faqs", method = RequestMethod.GET)
    public ResponseEntity<Object> getFAQSByWidget(
            @RequestParam("widgetId") String widgetId,
            @RequestParam(name = "domainId", required = false) String domainId) {
        if (Strings.isNullOrEmpty(widgetId)) {
            return ResponseUtils.buildFailureResponse(new Exception(
                    "WidgetId is Mandatory"));
        }
        ResponseDTO response=null;
        try {
            response = new ResponseDTO(faqService.getFAQSByWidget(widgetId,
                    domainId));
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
           return complianceService.formatException(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }
}
