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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.DateUtils;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.CompliantTrendRequest;
import com.tmobile.pacman.api.compliance.domain.RuleTrendRequest;
import com.tmobile.pacman.api.compliance.service.IssueTrendService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class TrendController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CacheConfig(cacheNames = { "trends" })
public class TrendController implements Constants {

    /** The LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TrendController.class);

    /** The trend service. */
    @Autowired
    private IssueTrendService trendService;

    /**
     * Gets the trend.
     *
     * @return the trend
     */
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved trend"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 417, message = "Expectation Failed") })
    
    @ApiOperation(value = "view the issue trends over the period of last 3 months", response = Iterable.class)
    @RequestMapping(path = "/v1/trend", method = RequestMethod.GET)
    public String getTrend() {
        try {
            // simulate slowness
            Thread.sleep(FIVE_THOUSAND);
            return "working....!!!";
        } catch (Exception e) {
            return CommonUtils.buildErrorResponse(e);
        }
    }

    /**
     * Gets the trend for issues.
     *
     * @param assetGroup
     *            the asset group
     * @param fromDate
     *            the from date
     * @param toDate
     *            the to date
     * @param severity
     *            the severity
     * @param ruleId
     *            the rule id
     * @param policyId
     *            the policy id
     * @param app
     *            the app
     * @param env
     *            the env
     * @return ResponseEntity<Object> 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved trend"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 417, message = "Expectation Failed") })
    
    @ApiOperation(value = "view the issue trends over the period of last 3 months", response = Iterable.class)
    // @Cacheable(cacheNames="trends",unless="#result.status==200")
    @Cacheable(cacheNames = "trends", key = "T(java.util.Objects).hash(#p0,#p1, #p2, #p3, #p4, #p5, #p6, #p7)")
    @RequestMapping(path = "/v1/trend/issueTrend", method = RequestMethod.GET)
    public ResponseEntity<Object> getTrendForIssues(@RequestParam("ag") String assetGroup,
            @RequestParam(name = "frdt", required = false) String fromDate,
            @RequestParam(name = "todt", required = false) String toDate,
            @RequestParam(name = "severity", required = false) String severity,
            @RequestParam(name = "ruleId", required = false) String ruleId,
            @RequestParam(name = "policyId", required = false) String policyId,
            @RequestParam(name = "app", required = false) String app,
            @RequestParam(name = "env", required = false) String env) {
        try {
            return ResponseUtils.buildSucessResponse(trendService.getTrendForIssues(assetGroup, fromDate, toDate,
                    severity, ruleId, policyId, app, env));
        } catch (ServiceException e) {
            if (null!=e.getCause() && e.getCause().toString().contains(NO_DATA_FOUND)) {
                LOGGER.error("Exception in getPatchingDetails" , e.getMessage());
                Date endDate = new Date(System.currentTimeMillis());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, NEG_FIFTEEN);
                Date startDate = cal.getTime();
                List dates = DateUtils.getAllDatesBetweenDates(startDate, endDate, "yyyy-MM-dd'T'HH:mm:ssZ");
                Map<String, Long> data = new HashMap<>();
                dates.stream().forEach(obj -> data.put((String) obj, 0L));
                return ResponseUtils.buildSucessResponse(data);
            } else {
                return ResponseUtils.buildFailureResponse(e);
            }
        }

    }

    /**
     * Gets the trend from cache.
     *
     * @return the trend from cache
     */
    
    public String getTrendFromCache() {
        return "{\"message\":\"retrieving from cache..! when I will implement caching it will get picked up :-)))\"}";
    }

    /**
     * Gets the trend from cache.
     *
     * @param assetGroup
     *            the asset group
     * @param fromDate
     *            the from date
     * @param toDate
     *            the to date
     * @param severity
     *            the severity
     * @param ruleId
     *            the rule id
     * @return ResponseEntity<Object> 
     */
    
    public ResponseEntity<Object> getTrendFromCache(String assetGroup, String fromDate, String toDate, String severity,
            String ruleId) {
        return ResponseUtils.buildFailureResponse(new ServiceException(
                "retrieving from cache..! when I will implement caching it will get picked up :-)))"));
    }

    /**
     * Gets the compliant trend.This request expects asset group,domain and from
     * as mandatory.If API receives asset group,domain and from as request
     * parameters, it gives weekly based details of compliance info from
     * mentioned date to till date by rule category
     *
     * @param request
     *            the request
     * @return ResponseEntity
     * @RequestBody request
     */

    @RequestMapping(path = "/v1/trend/compliance", method = RequestMethod.POST)
    
    public ResponseEntity<Object> getCompliantTrend(@RequestBody(required = true) CompliantTrendRequest request) {
        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();

        Date input = request.getFrom();

        if (input == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, NEG_THIRTY);
            input = cal.getTime();
        }

        Instant instant = input.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate fromDate = zdt.toLocalDate();
        Map<String, String> filter = request.getFilters();

        if (Strings.isNullOrEmpty(assetGroup) || MapUtils.isEmpty(filter) || Strings.isNullOrEmpty(filter.get(DOMAIN))) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_GROUP_DOMAIN));
        }

        String domain = filter.get(DOMAIN);
        try {
            Map<String, Object> trendData = trendService.getComplianceTrendProgress(assetGroup, fromDate, domain);
            response.put(RESPONSE, trendData);
        } catch (ServiceException e) {
            LOGGER.error("Exception in getCompliantTrend()" ,e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the rule trend.This request expects asset group,ruleId and from as
     * mandatory.If API receives asset group,ruleId and from as request
     * parameters, it gives weekly based details of compliance trend from
     * mentioned date to till date for ruleId.
     *
     * @param request
     *            the request
     * @return ResponseEntity
     */
    
    @RequestMapping(path = "/v1/trend/compliancebyrule", method = RequestMethod.POST)
    public ResponseEntity<Object> getRuleTrend(@RequestBody(required = true) RuleTrendRequest request) {

        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();
        String ruleId = request.getRuleid();

        Date input = request.getFrom();

        if (input == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, NEG_THIRTY);
            input = cal.getTime();
        }

        Instant instant = input.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate fromDate = zdt.toLocalDate();
        LocalDate toDate = LocalDate.now();

        if (Strings.isNullOrEmpty(assetGroup) || Strings.isNullOrEmpty(ruleId)) {
            return ResponseUtils.buildFailureResponse(new Exception("assetGroup/ruleId is Mandatory"));
        }

        try {
            Map<String, Object> ruleTrendProgressList = trendService.getTrendProgress(assetGroup, ruleId, fromDate,
                    toDate, "issuecompliance");
            response.put(RESPONSE, ruleTrendProgressList);
        } catch (ServiceException e) {
            LOGGER.error("Exception in getRuleTrend" , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the vulnerabilities trend.This request expects asset group and from
     * as mandatory.If API receives asset group and from as request parameters,
     * it gives weekly based details of compliance trend from mentioned date to
     * till date for vulnerabilities
     *
     * @param request
     *            the request
     * @return ResponseEntity
     */
    
    @RequestMapping(path = "/v1/trend/compliance/vulnerabilities", method = RequestMethod.POST)
    public ResponseEntity<Object> getVulnTrend(@RequestBody(required = true) CompliantTrendRequest request) {

        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();

        Date input = request.getFrom();

        if (input == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, NEG_THIRTY);
            input = cal.getTime();
        }

        Instant instant = input.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate fromDate = zdt.toLocalDate();
        LocalDate toDate = LocalDate.now();

        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }

        try {
            Map<String, Object> ruleTrendProgressList = trendService.getTrendProgress(assetGroup, null, fromDate,
                    toDate, "vulncompliance");
            response.put(RESPONSE, ruleTrendProgressList);
        } catch (ServiceException e) {
            LOGGER.error("Exception in getVulnTrend" , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the certificates trend.This request expects asset group and from as
     * mandatory.If API receives asset group and from as request parameters, it
     * gives weekly based details of compliance trend from mentioned date to
     * till date for certificates.
     *
     * @param request
     *            the request
     * @return ResponseEntity<Object> 
     */
    
    @RequestMapping(path = "/v1/trend/compliance/certificates", method = RequestMethod.POST)
    public ResponseEntity<Object> getCertTrend(@RequestBody(required = true) CompliantTrendRequest request) {

        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();

        Date input = request.getFrom();

        if (input == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, NEG_THIRTY);
            input = cal.getTime();
        }

        Instant instant = input.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate fromDate = zdt.toLocalDate();
        LocalDate toDate = LocalDate.now();

        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }

        try {
            Map<String, Object> ruleTrendProgressList = trendService.getTrendProgress(assetGroup, null, fromDate,
                    toDate, "certcompliance");
            response.put(RESPONSE, ruleTrendProgressList);
        } catch (ServiceException e) {
            LOGGER.error("Exception in getCertTrend" , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the Tagging compliance trend.This request expects asset group,domain
     * and from as mandatory.If API receives asset group,domain and from as
     * request parameters, it gives weekly based details of compliance trend
     * from mentioned date to till date for tagging
     *
     * @param request
     *            the request
     * @return ResponseEntity
     */
    
    @RequestMapping(path = "/v1/trend/compliance/tagging", method = RequestMethod.POST)
    public ResponseEntity<Object> getTagTrend(@RequestBody(required = true) CompliantTrendRequest request) {

        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();

        Date input = request.getFrom();

        if (input == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, NEG_THIRTY);
            input = cal.getTime();
        }

        Instant instant = input.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate fromDate = zdt.toLocalDate();
        LocalDate toDate = LocalDate.now();

        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_MANDATORY));
        }

        try {
            Map<String, Object> ruleTrendProgressList = trendService.getTrendProgress(assetGroup, null, fromDate,
                    toDate, "tagcompliance");
            response.put(RESPONSE, ruleTrendProgressList);
        } catch (ServiceException e) {
            LOGGER.error("Exception in getTagTrend" , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Gets the trend issues.request expects asset group,domain and from as
     * mandatory.If API receives asset group,domain and from as request
     * parameters, it gives details of severity from mentioned date to till date
     *
     * @param request
     *            the request
     * @return ResponseEntity
     */

    
    @RequestMapping(path = "/v1/trend/issues", method = RequestMethod.POST)
    public ResponseEntity<Object> getTrendIssues(@RequestBody(required = true) CompliantTrendRequest request) {
        Map<String, Object> response = new HashMap<>();
        String assetGroup = request.getAg();
        Date input = request.getFrom();

        if (input == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, NEG_THIRTY);
            input = cal.getTime();
        }

        Instant instant = input.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate fromDate = zdt.toLocalDate();
        LocalDate toDate = LocalDate.now();
        Map<String, String> filter = request.getFilters();

        if (Strings.isNullOrEmpty(assetGroup) || MapUtils.isEmpty(filter) || Strings.isNullOrEmpty(filter.get(DOMAIN))) {
            return ResponseUtils.buildFailureResponse(new Exception(ASSET_GROUP_DOMAIN));
        }

        String domain = filter.get(DOMAIN);

        Map<String, Object> trendIssues = new HashMap<>();
        try {
            trendIssues = trendService.getTrendIssues(assetGroup, fromDate, toDate, filter, domain);
            response.put(RESPONSE, trendIssues);
        } catch (ServiceException e) {
            LOGGER.error("Exception in getTrendIssues" , e.getMessage());
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

}
