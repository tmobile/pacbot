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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.Request;
import com.tmobile.pacman.api.asset.domain.ResponseWithCount;
import com.tmobile.pacman.api.asset.domain.ResponseWithEditableFields;
import com.tmobile.pacman.api.asset.domain.ResponseWithFieldsByTargetType;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.ApiOperation;

/**
 * The controller layer which has methods to return list of assets.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class AssetListController {

    @Autowired
    AssetService assetService;

    private static final Log LOGGER = LogFactory.getLog(AssetListController.class);

    /**
     * Fetches all the asset for the given asset group.
     *
     * @param request This request expects assetGroup as mandatory and
     * application,environment,resourceType as optional filters. API
     * returns all the assets associated with the assetGroup with
     * matching filters. If Domain is also passed it returns the asset
     * associated with assetGroup an Domain. SearchText is used to match
     * any text you are looking for. from and size are for the
     * pagination.
     * @param domain name of the domain
     * 
     * @return list of assets and its some details.
     */
    
    @ApiOperation(httpMethod = "POST", value = "Get the list of assets in an asset group. Optional filters -'application','environment','resourceType' ")
    @PostMapping(value = "/v1/list/assets")
    public ResponseEntity<Object> listAssets(@RequestBody(required = true) Request request,
            @RequestParam(name = "domain", required = false) String domain) {

        String assetGroup = request.getAg();

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));
        }

        String searchText = request.getSearchtext();
        Map<String, String> filter = request.getFilter();
        if (filter == null) {
            filter = new HashMap<>();
        }

        List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,
                AssetConstants.FILTER_ENVIRONMENT, AssetConstants.FILTER_RES_TYPE, AssetConstants.FILTER_DOMAIN);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }

        ResponseWithCount response;
        try {
            long count = assetService.getAssetCount(assetGroup, filter, searchText);
            List<Map<String, Object>> masterDetailList = assetService.getListAssets(assetGroup, filter, from, size,
                    searchText);
            response = new ResponseWithCount(masterDetailList, count);

        } catch (Exception e) {
            LOGGER.error("Error in listAssets ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the taggable assets for the given assetgroup.If tagged filter is false it returns the untagged assets
     * and if tagged is true returns the tagged assets.It also filters the the list based on resource type passed in filter 
     * else returns all the assets of the asset group
     *
     * @param request This request expects assetGroup as mandatory and
     * tagged(true/false),application,environment,resourcetype,
     * tagname(must with tagged) as optional filters. API returns all
     * the taggable assets associated with the assetGroup with matching
     * filters. SearchText is used to match any text you are looking
     * for. from and size are for the pagination.
     * 
     * @return list of assets tagged/untagged.
     */
    
    @ApiOperation(httpMethod = "POST", value = "Get the list of taggble assets in an asset group. Optional filters -'application','environment','resourceType','tagged'{true/false} ,'tagName' {Application/Environment/Stack/Role}")
    @PostMapping(value = "/v1/list/assets/taggable")
    public ResponseEntity<Object> listTaggableAssets(@RequestBody(required = true) Request request) {

        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));
        }

        String searchText = request.getSearchtext();
        Map<String, String> filter = request.getFilter();
        if (filter == null) {
            filter = new HashMap<>();
        }

        List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,
                AssetConstants.FILTER_ENVIRONMENT, AssetConstants.FILTER_RES_TYPE, AssetConstants.FILTER_TAGGED,
                AssetConstants.FILTER_TAGNAME);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }

        if (filter.containsKey(AssetConstants.FILTER_TAGNAME) && !filter.containsKey(AssetConstants.FILTER_TAGGED)) {
                return ResponseUtils.buildFailureResponse(new Exception("tagname should always be passed with tagged"));
        }

        List<Map<String, Object>> masterList;
        try {
            masterList = assetService.getListAssetsTaggable(assetGroup, filter);
        } catch (Exception e) {
            LOGGER.error("Error in listTaggableAssets ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return formResponseWithCount(masterList, from, size, searchText);
    }

    /**
     * Fetches the patchable assets for the given assetgroup.If patched filter is false it returns the unpatched assets
     * and if patched is true returns the patched assets.It also filters the the list based on resource type passed in filter 
     * else returns all the assets of the asset group
     *
     * @param request This request expects assetGroup as mandatory and
     * patched(true/false),application,environment,resourcetype,
     * executivesponsor,director as optional filters. API returns all
     * the patchable assets associated with the assetGroup with matching
     * filters. SearchText is used to match any text you are looking
     * for. from and size are for the pagination.
     * 
     * @return list of assets patched/unpatched.
     */
    
    @ApiOperation(httpMethod = "POST", value = "Get the list of patchable assets in an asset group. Optional filters -'application', 'environment', 'resourceType', 'patched'{true/false}, 'executiveSponsor' and 'director'.")
    @PostMapping(value = "/v1/list/assets/patchable")
    public ResponseEntity<Object> listPatchableAssets(@RequestBody(required = true) Request request) {

        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));

        }

        String searchText = request.getSearchtext();
        Map<String, String> filter = request.getFilter();
        if (filter == null) {
            filter = new HashMap<>();
        }

        List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,
                AssetConstants.FILTER_ENVIRONMENT, AssetConstants.FILTER_RES_TYPE, AssetConstants.FILTER_PATCHED,
                AssetConstants.FILTER_EXEC_SPONSOR, AssetConstants.FILTER_DIRECTOR);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }

        List<Map<String, Object>> masterList;
        try {

            if (filter.containsKey(AssetConstants.FILTER_RES_TYPE) && !isEC2OrOnPremServer(filter)) {
                return ResponseUtils.buildFailureResponse(new Exception(
                        "Value of resourceType must be ec2 or onpremserver"));
            }
            masterList = assetService.getListAssetsPatchable(assetGroup, filter);
        } catch (Exception e) {
            LOGGER.error("Error in listPatchableAssets ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return formResponseWithCount(masterList, from, size, searchText);
    }

    /**
     * Fetches the vulnerable assets for the given assetgroup. It looks for any particular resourceType passed in the filter 
     * else considers ec2 and onpremserver for targetype and fetch it vulnerable asset details.
     *
     * @param request This request expects assetGroup and qid as
     * mandatory and application,environment as optional filters. API
     * returns all the vulnerable assets associated with the assetGroup
     * based on the qid with matching filters.SearchText is used to
     * match any text you are looking for.from and size are for the
     * pagination.
     * 
     * @return list of vulnerable assets.
     */
    
    @ApiOperation(httpMethod = "POST", value = "Get the list of vulnerable assets in an asset group for a particualr qid. Mandatory Filter -'qid'. Optional filters -'application','environment','resourceType' ")
    @PostMapping(value = "/v1/list/assets/vulnerable")
    public ResponseEntity<Object> listVulnerableAssets(@RequestBody(required = true) Request request) {

        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));

        }

        String searchText = request.getSearchtext();
        Map<String, String> filter = request.getFilter();
        if (filter == null) {
            filter = new HashMap<>();
        }

        List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,
                AssetConstants.FILTER_ENVIRONMENT, AssetConstants.FILTER_RES_TYPE, "qid");
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }
        if (!filter.containsKey("qid")) {
            return ResponseUtils.buildFailureResponse(new Exception("qid is mandatory in filter"));
        }

        List<Map<String, Object>> masterList;
        try {
            masterList = assetService.getListAssetsVulnerable(assetGroup, filter);
        } catch (Exception e) {
            LOGGER.error("Error in listVulnerableAssets ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return formResponseWithCount(masterList, from, size, searchText);
    }

    /**
     * Fetches the assets with open issue status for the rule id passed in the filter.
     *
     * @param request This request expects assetGroup and ruleid as mandatory and
     * compliant(true/false),application,environment,resourcetype, as
     * optional filters. API returns all the scanned assets associated
     * with the assetGroup with matching filters.If Domain is also
     * passed it returns the asset associated with assetGroup an
     * Domain.SearchText is used to match any text you are looking for.
     * from and size are for the pagination.
     * @param domain name of the domain
     * 
     * @return list of assets with open issue status.
     */
    
    @ApiOperation(httpMethod = "POST", value = "Get the list of  assets  scanned by a ruleid in an asset group .  Mandatory Filter -'ruleId'. Optional filters -'application','environment','resourceType','compliant'{true/false}")
    @PostMapping(value = "/v1/list/assets/scanned")
    public ResponseEntity<Object> listScannedAssets(@RequestBody(required = true) Request request,
            @RequestParam(name = "domain", required = false) String domain) {

        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));

        }

        String searchText = request.getSearchtext();
        Map<String, String> filter = request.getFilter();
        if (filter == null) {
            filter = new HashMap<>();
        }

        List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,
                AssetConstants.FILTER_ENVIRONMENT, AssetConstants.FILTER_RES_TYPE, AssetConstants.FILTER_RULEID,
                AssetConstants.FILTER_COMPLIANT);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }
        if (!filter.containsKey(AssetConstants.FILTER_RULEID)) {
            return ResponseUtils.buildFailureResponse(new Exception("ruleId is mandatory in filter"));
        }

        List<Map<String, Object>> masterList;
        try {
            masterList = assetService.getListAssetsScanned(assetGroup, filter);
        } catch (Exception e) {
            LOGGER.error("Error in listScannedAssets ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return formResponseWithCount(masterList, from, size, searchText);
    }

    /**
     * Fetches all the asset details for the given asset group.
     *
     * @param request This request expects assetGroup and resourceType
     * as mandatory and application,environment as optional filters. API
     * returns all the assets complete details associated with the
     * assetGroup with matching filters. SearchText is used to match any
     * text you are looking for. from and size are for the pagination.
     * 
     * @return list of complete asset details.
     */
    
    @ApiOperation(httpMethod = "POST", value = "Get the complete details assets")
    @PostMapping(value = "/v1/listing/assets")
    public ResponseEntity<Object> getAssetLists(@RequestBody(required = true) Request request) {

        String assetGroup = request.getAg();
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception(Constants.ASSET_MANDATORY));
        }

        String resourceType = null;
        Map<String, String> filter = request.getFilter();
        if (filter != null && MapUtils.isNotEmpty(filter)) {
            resourceType = filter.get(AssetConstants.FILTER_RES_TYPE);
        }
        
        if (Strings.isNullOrEmpty(resourceType)) {
            return ResponseUtils.buildFailureResponse(new Exception("Resource Type is Mandatory"));
        }

        int from = request.getFrom();
        int size = request.getSize();
        if (from < 0) {
            return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_NEGATIVE));

        }

        String searchText = request.getSearchtext();

        List<String> acceptedFilterKeys = Arrays.asList(AssetConstants.FILTER_APPLICATION,
                AssetConstants.FILTER_ENVIRONMENT, AssetConstants.FILTER_RES_TYPE);
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            if (!acceptedFilterKeys.contains(entry.getKey())) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FILTER_ACCEPTS
                        + StringUtils.join(acceptedFilterKeys, ", ")));
            }
        }

        ResponseWithEditableFields response;
        try {
            Long totalDocCount = assetService.getTotalCountForListingAsset(assetGroup, resourceType);
            if (size <= 0) {
                size = Constants.TEN_THOUSAND;
            }

            List<Map<String, Object>> masterDetailList = assetService.getAssetLists(assetGroup, filter, from, size,
                    searchText);
            Map<String, Object> identifier = new HashMap<>();
            identifier.put("key", "_resourceid");
            response = new ResponseWithEditableFields(masterDetailList, totalDocCount,
                    assetService.getDataTypeInfoByTargetType(resourceType), identifier);
        } catch (Exception e) {
            LOGGER.error("Error in getAssetLists",e);
            return ResponseUtils.buildFailureResponse(e);
        }
        return ResponseUtils.buildSucessResponse(response);
    }

    /**
     * Fetches the list of fields that can be edited for the given resource type.
     *
     * @param assetGroup name of the asset group
     * @param resourceType type of the resource
     * 
     * @return list of editable fields
     */
    
    @RequestMapping(path = "/v1/updateFieldsbyresourceType", method = RequestMethod.GET)
    public ResponseEntity<Object> getEditableFieldsByTargetType(
            @RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "resourceType", required = true) String resourceType) {
        if (Strings.isNullOrEmpty(assetGroup)) {
            return ResponseUtils.buildFailureResponse(new Exception("Asset group/TargetType is Mandatory"));
        }
        boolean isTargetTypePresent = false;
        for (Map<String, Object> targetType : assetService.getTargetTypesForAssetGroup(assetGroup, null, null)) {
            if (targetType.get("type").toString().equals(resourceType)) {
                isTargetTypePresent = true;
                break;
            }
        }

        if (isTargetTypePresent) {
            ResponseWithFieldsByTargetType response = assetService.getEditFieldsByTargetType(resourceType);
            return ResponseUtils.buildSucessResponse(response);
        } else {
            return ResponseUtils.buildFailureResponse(new Exception("TargetType not present in Asset Group"));
        }
    }

    /**
     * Method returns the list with count based on the from and size.
     * 
     * @param masterList
     * @param from
     * @param size
     * @param searchText
     * 
     * @return ResponseEntity 
     */
    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> formResponseWithCount(List<Map<String, Object>> masterList, int from, int size,
            String searchText) {
        try {
            List<Map<String, Object>> masterDetailList = (List<Map<String, Object>>) CommonUtils
                    .filterMatchingCollectionElements(masterList, searchText, true);
            if (masterDetailList.isEmpty()) {
                return ResponseUtils
                        .buildSucessResponse(new ResponseWithCount(new ArrayList<Map<String, Object>>(), 0));
            }

            if (from >= masterDetailList.size()) {
                return ResponseUtils.buildFailureResponse(new Exception(AssetConstants.ERROR_FROM_EXCEEDS));
            }

            int endIndex = 0;

            if (size == 0) {
                size = masterDetailList.size();
            }

            if ((from + size) > masterDetailList.size()) {
                endIndex = masterDetailList.size();
            } else {
                endIndex = from + size;
            }

            List<Map<String, Object>> subDetailList = masterDetailList.subList(from, endIndex);
            return ResponseUtils.buildSucessResponse(new ResponseWithCount(subDetailList, masterDetailList.size()));
        } catch (Exception e) {
            LOGGER.error("Exception in formResponseWithCount ",e);
            return ResponseUtils.buildFailureResponse(e);
        }
    }

    /**
     * Returns boolean on checking the resourceType from filter for EC2 or OnPremServer
     * 
     * @param filter
     * 
     * @return boolean 
     */
    private boolean isEC2OrOnPremServer(Map<String, String> filter) {
        return Constants.EC2.equals(filter.get(AssetConstants.FILTER_RES_TYPE))
                || Constants.ONPREMSERVER.equals(filter.get(AssetConstants.FILTER_RES_TYPE));
    }
}
