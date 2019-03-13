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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.client.ComplianceServiceClient;
import com.tmobile.pacman.api.asset.domain.PageFilterRequest;
import com.tmobile.pacman.api.asset.domain.PolicyViolationApi;
import com.tmobile.pacman.api.asset.domain.Response;
import com.tmobile.pacman.api.asset.domain.ResponseWithCount;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.NoDataFoundException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.ApiOperation;

/**
 * The controller layer for Asset Details which has methods to return asset details.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class AssetDetailController {

    @Autowired
    AssetService assetService;

    @Autowired
    ComplianceServiceClient complianceServiceClient;
    
    @Value("${features.vulnerability.enabled:false}")
    private boolean qualysEnabled;

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetDetailController.class);

    /**
     * Fetches the CPU utilization for the given instanceid.
     *
     * @param assetGroup name of the assetgroup
     * @param instanceId id of the instance
     * asssetGroup and instanceId is mandatory
     * 
     * @return list of date and its CPU utilization of the instance id.
     */
    
    @GetMapping(value = "/v1/{assetGroup}/ec2/{resourceId}/cpu-utilization")
    public ResponseEntity<Object> getCPUUtilizationByInstanceId(
            @PathVariable(name = "resourceId", required = true) String instanceId) {
        if (Strings.isNullOrEmpty(instanceId)) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_INSTANCEID));
        }
        Response response;
        try {
            response = new Response(assetService.getInstanceCPUUtilization(instanceId));
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the Disk utilization for the given instanceid.
     *
     * @param assetGroup name of the asset group
     * @param instanceId id of the instance
     * 
     * @return list of disk name, size and free space of the instance id.
     */
    @RequestMapping(path = "/v1/{assetGroup}/ec2/{resourceId}/disk-utilization", method = RequestMethod.GET)
    public ResponseEntity<Object> getDiskUtilizationByInstanceId(
            @PathVariable(name = "resourceId", required = true) String instanceId) {
    	if (!qualysEnabled) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_QUALYS_NOT_ENABLED));
    	}
    	
        if (Strings.isNullOrEmpty(instanceId)) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_INSTANCEID));
        }
        Response response;
        try {
            response = new Response(assetService.getInstanceDiskUtilization(instanceId));
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the Softwares installed for the given instanceid.
     *
     * @param instanceId id of the instance
     * @param from for pagination
     * @param size for pagination
     * @param searchText searchText is used to match any text you are looking for
     * 
     * @return list of software name and its version installed on the instance id.
     */
    @RequestMapping(path = "/v1/{assetGroup}/ec2/{resourceId}/installed-softwares", method = RequestMethod.GET)
    public ResponseEntity<Object> getInstalledSoftwareDetailsByInstanceId(
            @PathVariable(name = "resourceId", required = true) String instanceId,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "searchtext", required = false) String searchText) {
    	if (!qualysEnabled) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_QUALYS_NOT_ENABLED));
    	}
        List<Map<String, Object>> subDetailList ;
        if (Strings.isNullOrEmpty(instanceId)) {
            return ResponseUtils.buildFailureResponse(new Exception("Instance Id is Mandatory"));
        }
        ResponseWithCount response;
        try {
            List<Map<String, Object>> softWareList = assetService.getInstanceSoftwareInstallDetails(instanceId, from,
                    size, searchText);

            if (softWareList.isEmpty()) {
                throw new NoDataFoundException(" No data found");
            }

            if (from != null && size != null) {

                if (from >= softWareList.size()) {
                    throw new DataException(AssetConstants.ERROR_FROM_EXCEEDS);
                }

                int endIndex = 0;

                if ((from + size) > softWareList.size()) {
                    endIndex = softWareList.size();
                } else {
                    endIndex = from + size;
                }

                if (from == 0 && size == 0) {
                    subDetailList = softWareList;
                } else {
                    subDetailList = softWareList.subList(from, endIndex);
                }
            } else {
                subDetailList = softWareList;
            }
            response = new ResponseWithCount(subDetailList, softWareList.size());
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the port which are in open status for the given instanceId. 
     *
     * @param assetGroup name of the asset group
     * @param instanceId id of the instance
     * @param from for pagination
     * @param size for pagination
     * @param searchText searchText is used to match any text you are looking for
     * 
     * @return list of open ports.
     */
    @RequestMapping(path = "/v1/{assetGroup}/ec2/{resourceId}/open-ports", method = RequestMethod.GET)
    public ResponseEntity<Object> getOpenPortsByInstanceId(
            @PathVariable(name = "resourceId", required = true) String instanceId,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "searchtext", required = false) String searchText) {
    	if (!qualysEnabled) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_QUALYS_NOT_ENABLED));
    	}
        List<Map<String, Object>> subDetailList ;
        if (Strings.isNullOrEmpty(instanceId)) {
            return ResponseUtils.buildFailureResponse(new Exception("Instance Id is Mandatory"));
        }
        ResponseWithCount response;
        try {
            List<Map<String, Object>> openPortList = assetService
                    .getOpenPortDetails(instanceId, from, size, searchText);

            if (openPortList.isEmpty()) {
                throw new NoDataFoundException(" No data found");
            }
            if (from != null && size != null) {
                if (from >= openPortList.size()) {
                    throw new DataException(AssetConstants.ERROR_FROM_EXCEEDS);
                }

                int endIndex = 0;

                if ((from + size) > openPortList.size()) {
                    endIndex = openPortList.size();
                } else {
                    endIndex = from + size;
                }

                if (from == 0 && size == 0) {
                    subDetailList = openPortList;
                } else {
                    subDetailList = openPortList.subList(from, endIndex);
                }
            } else {
                subDetailList = openPortList;
            }
            response = new ResponseWithCount(subDetailList, openPortList.size());
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the open,closed and upcoming notification count for the given instance.
     *
     * @param instanceId id of the instance
     * 
     * @return list of assets with open,closed and upcoming count.
     */
    
    @RequestMapping(path = "/v1/{assetGroup}/ec2/{resourceId}/aws-notifications/summary", method = RequestMethod.GET)
    public ResponseEntity<Object> getAwsNotificationSummary(
            @PathVariable(name = "resourceId", required = true) String instanceId) {
        if (Strings.isNullOrEmpty(instanceId)) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_INSTANCEID));
        }

        Map<String, Object> summaryInfo = new LinkedHashMap<>();

        List<Map<String, Object>> sevList;
        try {
            sevList = assetService.getNotificationSummary(instanceId);

            summaryInfo.put("distribution", sevList);

            String total = assetService.getNotificationSummaryTotal(sevList);
            summaryInfo.put("total", total);

        } catch (Exception e) {
            LOGGER.error("Exception in getAwsNotificationSummary ",e);
            summaryInfo = new HashMap<>();
        }

        return ResponseUtils.buildSucessResponse(summaryInfo);
    }

    /**
    * Fetches the notification details of the instanceId.
    *
    * @param instanceId id of the instance
    * @param request the PageFilterRequest which has from,size, searchText and filters
    * 
    * @return list of notification details.
    */
    
    @RequestMapping(path = "/v1/{assetGroup}/ec2/{resourceId}/aws-notifications/details", method = RequestMethod.POST)
    public ResponseEntity<Object> getAwsNotificationDetails(@RequestBody(required = true) PageFilterRequest request,
            @PathVariable(name = "resourceId", required = true) String instanceId) {

        Map<String, Object> response = new HashMap<>();

        if (Strings.isNullOrEmpty(instanceId)) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_INSTANCEID));
        }
        int from = request.getFrom();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));

        }

        int size = request.getSize();

        Map<String, String> filter = request.getFilter();

        List<Map<String, Object>> detailInfo = new ArrayList<>();

        try {
            detailInfo = assetService.getNotificationDetails(instanceId, filter, request.getSearchText());
            response.put("response", detailInfo);

            if (detailInfo.isEmpty()) {
                throw new NoDataFoundException("No data found");
            }

            if (from >= detailInfo.size()) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_EXCEEDS));
            }

            int endIndex = 0;

            if ((from + size) > detailInfo.size()) {
                endIndex = detailInfo.size();
            } else {
                endIndex = from + size;
            }

            if (endIndex == 0) {
                endIndex = detailInfo.size();
            }
            List<Map<String, Object>> subDetailList = detailInfo.subList(from, endIndex);
            return ResponseUtils.buildSucessResponse(subDetailList);

        } catch (Exception e) {
            LOGGER.error("Error in getAwsNotificationDetails ",e);
            List<Map<String, Object>> subDetailList = new ArrayList<>();
            return ResponseUtils.buildSucessResponse(subDetailList);
        }

    }

    /**
     * Fetches the creator details for the given resourceId.
     *
     * @param assetGroup name of the asset group
     * @param resourceId id of the resource
     * @param resourceType type of the resource
     * 
     * @return created by, creation date and email.
     */
    @ApiOperation(httpMethod = "GET", value = "Get the creator details for a particular resource")
    @GetMapping(value = "/v1/{assetGroup}/{resourceType}/{resourceId}/creatordetails")
    public ResponseEntity<Object> getEc2CreatorDetail(
            @PathVariable(name = "assetGroup", required = true) String assetGroup,
            @PathVariable(name = "resourceType", required = true) String resourceType,
            @PathVariable(name = "resourceId", required = true) String resourceId) {
        Map<String, Object> creatorDetail;

        try {
            creatorDetail = assetService.getEc2CreatorDetail(resourceId);

        } catch (Exception e) {
            LOGGER.error("Error in getEc2CreatorDetail ",e);
            creatorDetail = new HashMap<>();
        }
        return ResponseUtils.buildSucessResponse(creatorDetail);
    }

    /**
     * Fetches the AD group details of the resourceId for the given asset group.
     *
     * @param ag  name of the asset group
     * @param resourceId id of the resource
     * 
     * @return list of AD group details.
     */
    
    @ApiOperation(httpMethod = "GET", value = "Get the ad group details for a ec2 instance")
    @GetMapping(value = "/v1/{assetGroup}/ec2/{resourceId}/ad-groups")
    public ResponseEntity<Object> getAdGroupsDetail(@PathVariable(name = "assetGroup", required = true) String ag,
            @PathVariable(name = "resourceId", required = true) String resourceId) {
        List<Map<String, String>> adGroupsDetail;

        try {
            adGroupsDetail = assetService.getAdGroupsDetail(ag, resourceId);

        } catch (Exception e) {
            LOGGER.error("Error in getAdGroupsDetail ",e);
            adGroupsDetail = new ArrayList<>();
        }
        return ResponseUtils.buildSucessResponse(adGroupsDetail);
    }

    /**
     * Fetches the details from a particular data source for a particular resource for given assetGroup
     * 
     * @param ag name of the asset group
     * @param resourceType type of the resource
     * @param resourceId id of the resource
     * 
     * @return details of ec2 resource
     */
    
    @ApiOperation(httpMethod = "GET", value = "Get the details from a particular data source for a particular  resource")
    @GetMapping(value = "v1/{assetGroup}/{resourceType}/{resourceId}/details")
    public ResponseEntity<Object> getEc2ResourceDetail(@PathVariable(name = "assetGroup", required = true) String ag,
            @PathVariable(name = "resourceType", required = true) String resourceType,
            @PathVariable(name = "resourceId", required = true) String resourceId) {
        Map<String, Object> assetDetail;

        try {
            if ("ec2".equals(resourceType)) {
                assetDetail = assetService.getEc2ResourceDetail(ag, resourceId);
            } else {
                assetDetail = assetService.getGenericResourceDetail(ag, resourceType, resourceId);
            }
        } catch (Exception e) {
            LOGGER.error("Error in getEc2ResourceDetail ",e);
            assetDetail = new HashMap<>();
        }
        return ResponseUtils.buildSucessResponse(assetDetail);
    }

    /**
     * Fetches the average last week cost and total cost of the ec2 instance.
     *
     * @param assetGroup name of the asset group
     * @param resourceId id of the resource
     * 
     * @return average last week cost and total cost of ec2.
     * @throws DataException when fetching data from ES.
     */
    
    @ApiOperation(httpMethod = "GET", value = "Get the average cost and total cost for an EC2 resource")
    @GetMapping(value = "v1/{assetGroup}/ec2/{resourceId}/cost")
    public ResponseEntity<Object> getEc2ResourceSummary(
            @PathVariable(name = "resourceId", required = true) String resourceId) throws DataException {
        Map<String, Object> dataMap = null;
        try {
            dataMap = assetService.getEC2AvgAndTotalCost(resourceId);

        } catch (Exception e) {
            LOGGER.error("Error in getEc2ResourceSummary ",e);
            dataMap = new HashMap<>();
        }
        return ResponseUtils.buildSucessResponse(dataMap);
    }

    /**
     * Fetches the summary from AWS for a particular resource for given assetGroup
     * 
     * @param ag name of the asset group
     * @param resourceType type of the resource
     * @param resourceId id of the resource
     * 
     * @return compliance, statename and attributes
     */
    
    @ApiOperation(httpMethod = "GET", value = "Get the summary from AWS for a particular  resource")
    @GetMapping(value = "v1/{assetGroup}/{resourceType}/{resourceId}/summary")
    public ResponseEntity<Object> getEc2ResourceSummary(@PathVariable(name = "assetGroup", required = true) String ag,
            @PathVariable(name = "resourceType", required = true) String resourceType,
            @PathVariable(name = "resourceId", required = true) String resourceId) {

        PolicyViolationApi violationSummary = complianceServiceClient.getPolicyViolationSummary(
                Util.encodeUrl(resourceId), ag, resourceType);

        Map<String, Object> assetSummary = new HashMap<>();

        try {

            List<Map<String, Object>> attributesList = new ArrayList<>();

            Map<String, Object> attribute = new LinkedHashMap<>();
            attribute.put(Constants.NAME, "resourceId");
            attribute.put(Constants.VALUE, resourceId);
            attributesList.add(attribute);

            attribute = new LinkedHashMap<>();
            attribute.put(Constants.NAME, "Overall Compliance");
            attribute.put(Constants.VALUE, violationSummary.getData().getCompliance());
            attributesList.add(attribute);

            if (Constants.EC2.equals(resourceType)) {
                attribute = new LinkedHashMap<>();
                attribute.put(Constants.NAME, "statename");
                attribute.put(Constants.VALUE, assetService.getEc2StateDetail(ag, resourceId));
                attributesList.add(attribute);

                attributesList.add(getAverageCPUUtilization(resourceId));
            }

            assetSummary.put("resourceId", resourceId);
            assetSummary.put("attributes", attributesList);

        } catch (Exception e) {
            LOGGER.error("Error in getEc2ResourceSummary",e);
            assetSummary = new HashMap<>();
        }
        return ResponseUtils.buildSucessResponse(assetSummary);
    }

    /**
     * Fetches the CPU utilization for the given instanceid.
     *
     * @param resourceId id of the resource
     * 
     * @return list of date and its CPU utilization of the instance id.
     */
    private Map<String, Object> getAverageCPUUtilization(String resourceId) {

        Map<String, Object> attribute = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> cpuUtilisation = assetService.getInstanceCPUUtilization(resourceId);
            if(null==cpuUtilisation) {
            	return attribute;
            }
            double avergeUtilisation = cpuUtilisation.stream()
                    .mapToDouble(obj -> Double.valueOf(obj.get("cpu-utilization").toString())).average().getAsDouble();
            attribute.put(Constants.NAME, "Utilization Score");
            attribute.put(Constants.VALUE, Util.getUtilisationScore(avergeUtilisation));

        } catch (Exception e) {
            LOGGER.error("Error in getEc2ResourceSummary",e);
        }
        return attribute;
    }
}
