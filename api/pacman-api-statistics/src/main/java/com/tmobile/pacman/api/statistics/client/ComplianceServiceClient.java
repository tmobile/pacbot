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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * The Feign client for  compliance service.
 */
@FeignClient(name = "complianceClient", url = "${service.url.compliance}")
public interface ComplianceServiceClient {

   

    /**
     * Gets the patching compliance.
     *
     * @param assetGroup the asset group
     * @return the patching compliance
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/patching")
    Map<String, Object> getPatchingCompliance(@RequestParam("ag") String assetGroup);

    /**
     * Gets the tagging compliance.
     *
     * @param assetGroup the asset group
     * @return the tagging compliance
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/tagging")
    Map<String, Object> getTaggingCompliance(@RequestParam("ag") String assetGroup);

    /**
     * Gets the vuln compliance.
     *
     * @param assetGroup the asset group
     * @return the vuln compliance
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/vulnerabilites")
    Map<String, Object> getVulnCompliance(@RequestParam("ag") String assetGroup);

    /**
     * Gets the cert compliance.
     *
     * @param assetGroup the asset group
     * @return the cert compliance
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/certificates")
    Map<String, Object> getCertCompliance(@RequestParam("ag") String assetGroup);

    /**
     * Gets the open issues.
     *
     * @param payLoad the pay load
     * @return the open issues
     */
    @RequestMapping(method = RequestMethod.POST, value = "/v1/noncompliancepolicy", consumes = "application/json")
    Map<String, Object> getOpenIssues(@RequestBody String payLoad);

    /**
     * Gets the distribution.
     *
     * @param assetGroup the asset group
     * @return the distribution
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/issues/distribution")
    Map<String, Object> getDistribution(@RequestParam("ag") String assetGroup);

    /**
     * Gets the distribution as json.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return the distribution as json
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/issues/distribution")
    String getDistributionAsJson(@RequestParam("ag") String assetGroup, @RequestParam(name = "domain") String domain);
    
    /**
     * Gets the overall compliance.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return the overall compliance
     */
    @RequestMapping(method = RequestMethod.GET, value = "/v1/overallcompliance")
    Map<String, Object> getOverallCompliance(@RequestParam("ag") String assetGroup,
            @RequestParam(name = "domain") String domain);
}
