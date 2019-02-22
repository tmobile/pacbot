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
import com.tmobile.pacman.api.compliance.domain.DitributionDTO;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithCount;
import com.tmobile.pacman.api.compliance.service.CertificateService;

/**
 * The Controller layer for methods related to certificates
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@ConditionalOnProperty(name="features.certificate.enabled")
public class CertificateController implements Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateController.class);

    @Autowired
    private CertificateService certificateService;

    /**
     * Gets the certicates expiry by application.
     *
     * @param assetGroup name of the asset group
     * @return certicates expiry by application
     * @throws ServiceException the service exception
     */
    @RequestMapping(path = "/v1/certificates/expirybyapplication", method = RequestMethod.GET)
    
    public ResponseEntity<Object> getCerticatesExpiryByApplication(
            @RequestParam(name = "ag", required = true) String assetGroup)
            throws ServiceException {

        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(
                    ASSET_MANDATORY));
        }
        DitributionDTO response;
        try {
            response = new DitributionDTO(
                    certificateService
                            .getCerticatesExpiryByApplication(assetGroup));
        } catch (ServiceException e) {
            LOGGER.error(EXE_CERT_EXPIRY,e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the certificates details.
     *
     * @param request the request
     * @return the certificates details
     */
    @RequestMapping(path = "/v1/certificates/detail", method = RequestMethod.POST)
    
    public ResponseEntity<Object> getCertificatesDetails(
            @RequestBody(required = true) Request request) {

        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(
                    ASSET_MANDATORY));
        }
        int from = request.getFrom();
        int size = request.getSize();
        String searchText = request.getSearchtext();
        Map<String, String> filter = request.getFilter();
        if (filter == null) {
            filter = new HashMap<>();
        }

        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new ServiceException(
                    "From should not be a negative number"));
        }

        ResponseWithCount response;
        try {

            List<Map<String, Object>> masterDetailList = certificateService
                    .getCerticatesDetails(assetGroup, searchText, filter);
            if (masterDetailList.isEmpty()) {
                return ResponseUtils.buildSucessResponse(new ResponseWithCount(
                        new ArrayList<Map<String, Object>>(), 0));
            }

            if (from >= masterDetailList.size()) {
                return ResponseUtils.buildFailureResponse(new ServiceException(
                        "From exceeds the size of list"));
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
            List<Map<String, Object>> subDetailList = masterDetailList.subList(
                    from, endIndex);
            response = new ResponseWithCount(subDetailList,
                    masterDetailList.size());

        } catch (Exception e) {
            LOGGER.error(EXE_CERT_EXPIRY , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the certificates summary.
     *
     * @param assetGroup the asset group
     * @return the certificates summary
     * @throws ServiceException the service exception
     */
    @RequestMapping(path = "/v1/certificates/summary", method = RequestMethod.GET)
    
    public ResponseEntity<Object> getCertificatesSummary(
            @RequestParam(name = "ag", required = true) String assetGroup)
            throws ServiceException {

        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new ServiceException(
                    ASSET_MANDATORY));
        }
        DitributionDTO response;
        try {
            response = new DitributionDTO(
                    certificateService.getCerticatesSummary(assetGroup));
        } catch (ServiceException e) {
            LOGGER.error(EXE_CERT_EXPIRY , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }
}
