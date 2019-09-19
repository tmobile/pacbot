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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.deps.io.netty.util.internal.StringUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.repository.DownloadRepository;
import com.tmobile.pacman.api.compliance.service.ComplianceService;
import com.tmobile.pacman.api.compliance.service.ComplianceServiceImpl;
import com.tmobile.pacman.api.compliance.service.DownloadFileService;
import com.tmobile.pacman.api.compliance.service.PatchingService;
import com.tmobile.pacman.api.compliance.service.TaggingService;
import com.tmobile.pacman.api.compliance.util.PacHttpUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * The Class DownloadController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class DownloadController implements Constants {

    /** The download file service. */
    @Autowired
    private DownloadFileService downloadFileService;

    /** The compliance service. */
    @Autowired
    private ComplianceService complianceService;

    /** The compliance service impl. */
    @Autowired
    private ComplianceServiceImpl complianceServiceImpl;

    /** The download repository. */
    @Autowired
    private DownloadRepository downloadRepository;

    /** The patching controller. */
    @Autowired(required=false)
    private PatchingController patchingController;

    /** The tagging service. */
    @Autowired
    private TaggingService taggingService;

    /** The patching service. */
    @Autowired
    private PatchingService patchingService;

    /** The certificate controller. */
    @Autowired(required=false)
    private CertificateController certificateController;

    /** The compliance controller. */
    @Autowired
    private ComplianceController complianceController;

    /** The tagging controller. */
    @Autowired
    private TaggingController taggingController;
    
    @Value("${service.dns.name}")
    private String serviceDnsName;
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    /**
     * API to download the data either in CSV or excel format,serviceId is the
     * number which is configured in the database for a particular API. Pass the
     * request params based on the API you want to download for.
     *
     * @param servletResponse
     *            the servlet response
     * @param fileFormat
     *            the file format
     * @param serviceId
     *            the service id
     * @param request
     *            the request
     * @return ResponseEntity<Object>
     * @throws Exception
     *             the exception
     */

    
    @ApiOperation(httpMethod = "POST", value = "Download Service Details in CSV or Excel")
    @RequestMapping(path = "/v1/download/services", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> getIssuesDownload(final HttpServletRequest servletRequest,
            final HttpServletResponse servletResponse,
            @ApiParam(value = "Provide Download File Format (excel or csv). Default is csv", required = false) @RequestParam(name = "fileFormat", required = false) String fileFormat,
            @ApiParam(value = "Provide serviceId 1-policyViolationList,2-policyOverviewList,3-patchingList,4-taggingList,5-certificateList,6-vulnerabilitiesList", required = false) @RequestParam("serviceId") int serviceId,
            @ApiParam(value = "Provide Service Search Filter", required = false) @RequestBody(required = false) Request request)
            throws Exception {
        try {
            String assetGroup = request.getAg();
            JsonArray responseArray = null;

            String serviceName = null;
            String serviceEndpoint = null;
            if (Strings.isNullOrEmpty(assetGroup)) {
                return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
            }
            if (request.getSize() <= 0 || request.getSize() > 100000) {
                return ResponseUtils.buildFailureResponse(new Exception(
                        "enter valid size/ you can max download 100k records"));
            }
          
            List<Map<String, Object>> filterMap = downloadRepository.getFiltersFromDb(serviceId);
            if (!filterMap.isEmpty()) {
                for (Map<String, Object> filterMethodName : filterMap) {
                    if (filterMethodName.get("serviceName") != null && filterMethodName.get("serviceEndpoint") != null) {
                        serviceName = filterMethodName.get("serviceName").toString();
                        serviceEndpoint = filterMethodName.get("serviceEndpoint").toString();
                    }
                }
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                String jsonString = gson.toJson(request);

                if (!StringUtils.isEmpty(serviceEndpoint) && !StringUtils.isEmpty(serviceDnsName) && !StringUtils.isEmpty(jsonString)) {

                    String serviceResponse = PacHttpUtils.doHttpsPost(serviceDnsName+serviceEndpoint, jsonString, getTokenHeader(servletRequest));
                    if(!StringUtil.isNullOrEmpty(serviceResponse)){
                    responseArray = getServiceDetails(serviceResponse);
                    downloadFileService.downloadData(servletResponse, responseArray, fileFormat, serviceName);
                    return new ResponseEntity<>(HttpStatus.OK);
                    }else{
                    	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                } else {
                    return ResponseUtils.buildFailureResponse(new Exception(
                            "Please configure the serviceEndpoint or urlParameters"));
                }
            } else {
                return ResponseUtils.buildFailureResponse(new Exception(
                        "Please configure the serviceName and serviceEndpoint"));
            }
           
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    /**
     * Gets the service details.
     *
     * @param json
     *            the json
     * @return the service details
     */
    
    private JsonArray getServiceDetails(String json) {
        JsonParser jsonParser;
        JsonObject dataJson = null;
        jsonParser = new JsonParser();
        JsonArray resultArray = new JsonArray();
        if (!StringUtils.isEmpty(json)) {
            JsonObject resultJson = (JsonObject) jsonParser.parse(json);
            if (resultJson.get("data").isJsonObject()) {
                dataJson = resultJson.get("data").getAsJsonObject();
                resultArray = dataJson.getAsJsonArray("response");
            }
        }
        return resultArray;

    }
    
    private Map<String,String> getTokenHeader(HttpServletRequest servletRequest){
        String token = PacHttpUtils.getBase64AuthorizationHeader(servletRequest);
        Map<String,String> authToken = new HashMap<>();
        authToken.put(AUTHORIZATION, "Bearer "+token);
        return authToken;
    }

}
