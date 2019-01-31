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
package com.tmobile.pacman.api.statistics.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tmobile.pacman.api.statistics.domain.AssetApi;


/**
 * The Interface AssetServiceClient.
 */
@FeignClient(name = "assetClient", url = "${service.url.asset}")
public interface AssetServiceClient {
    
    /**
     * Gets the type counts.
     *
     * @param assetGroup the asset group
     * @param type the type
     * @param domain the domain
     * @return the type counts
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/count")
    Map<String, Object> getTypeCounts(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "domain", required = false) String domain);

    /**
     * Gets the target type list.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return the target type list
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/list/targettype")
    AssetApi getTargetTypeList(@RequestParam("ag") String assetGroup, @RequestParam("domain") String domain);
}
