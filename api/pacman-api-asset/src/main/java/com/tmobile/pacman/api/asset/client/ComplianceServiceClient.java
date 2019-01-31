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
package com.tmobile.pacman.api.asset.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tmobile.pacman.api.asset.domain.PolicyViolationApi;

/**
 * The Interface ComplianceServiceClient.
 */
@FeignClient(name = "compliance", url = "${service.url.compliance}")
public interface ComplianceServiceClient {

    /**
     * Gets the total issues.
     *
     * @param assetGroup the asset group
     * @return the total issues
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/issues/count")
    String getTotalIssues(@RequestParam("ag") String assetGroup);

    /**
     * Gets the policy violation summary.
     *
     * @param resourceId the resource id
     * @param dataSource the data source
     * @param resourceType the resource type
     * @return the policy violation summary
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/policyviolations/summary/{dataSource}/{resourceType}/{resourceId}")
    PolicyViolationApi getPolicyViolationSummary(@PathVariable("resourceId") String resourceId,
            @PathVariable("dataSource") String dataSource, @PathVariable("resourceType") String resourceType);

}
