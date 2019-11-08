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
  Modified Date: Oct 26, 2017

 **/
package com.tmobile.pacman.api.compliance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tmobile.pacman.api.compliance.domain.AssetApi;
import com.tmobile.pacman.api.compliance.domain.AssetCount;
import com.tmobile.pacman.api.compliance.domain.ExemptedAssetByPolicy;

/**
 * The Interface AssetServiceClient.
 */
@FeignClient(name = "assetclient", url = "${service.url.asset}")
public interface AssetServiceClient {

	/**
     * Gets the total assets count.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @param domain the domain
     * @param application the application
     * @return AssetCount
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/count")
    AssetCount getTotalAssetsCount(@RequestParam("ag") String assetGroup,
            @RequestParam("type") String targetType,
            @RequestParam("domain") String domain,
            @RequestParam("application") String application,
            @RequestParam("provider") String provider);

    /**
     * Gets the applications list.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return AssetApi
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/list/application")
    AssetApi getApplicationsList(@RequestParam("ag") String assetGroup,
            @RequestParam("domain") String domain);

    /**
     * Gets the environment list.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param domain the domain
     * @return AssetApi
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/list/environment")
    AssetApi getEnvironmentList(@RequestParam("ag") String assetGroup,
            @RequestParam("application") String application,
            @RequestParam("domain") String domain);

    /**
     * Gets the target type list.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return AssetApi
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/list/targettype")
    AssetApi getTargetTypeList(@RequestParam("ag") String assetGroup,
            @RequestParam("domain") String domain);

    /**
     * Gets the total assets count by application.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return AssetCount
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/count/byapplication")
    AssetCount getTotalAssetsCountByApplication(
            @RequestParam("ag") String assetGroup,
            @RequestParam("type") String targetType);

    /**
     * Gets the total assets count by environment.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param targetType the target type
     * @return AssetCount
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/count/byenvironment")
    AssetCount getTotalAssetsCountByEnvironment(
            @RequestParam("ag") String assetGroup,
            @RequestParam("application") String application,
            @RequestParam("type") String targetType);

    /**
     * Gets the asset group info.
     *
     * @param assetGroup the asset group
     * @return AssetApi
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/assetgroup")
    AssetApi getAssetGroupInfo(@RequestParam("ag") String assetGroup);

    /**
     * Gets the target type list by domain.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return AssetApi
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/list/targettype")
    AssetApi getTargetTypeListByDomain(@RequestParam("ag") String assetGroup,
            @RequestParam("domain") String domain);
    
    /**
     * Gets the total assets exempted by policy.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param targetType the target type
     * @param domain the domain
     * @return the total assets exempted by policy
     */
    @RequestMapping(method = RequestMethod.GET, value = "v1/count/exempted/bypolicy")
    ExemptedAssetByPolicy getTotalAssetsExemptedByPolicy(
            @RequestParam("ag") String assetGroup,
            @RequestParam("application") String application,
            @RequestParam("type") String targetType,
            @RequestParam("domain") String domain);
}
