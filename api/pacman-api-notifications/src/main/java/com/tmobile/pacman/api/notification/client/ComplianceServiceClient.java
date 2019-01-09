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
  Author : NidhishKrishnan
  Modified Date: Jan 29, 2018
  
**/
package com.tmobile.pacman.api.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tmobile.pacman.api.notification.domain.Response;

@FeignClient(name="compliance-service",url="${service.url.compliance}")
public interface ComplianceServiceClient {

    @RequestMapping(path = "/vulnerabilites", method = RequestMethod.GET)
	public Response getVulnerabilities(@RequestParam("ag") String assetGroup);
    
    @RequestMapping(path = "/tagging", method = RequestMethod.GET)
	public Response getTagging(@RequestParam("ag") String assetGroup);
    
    @RequestMapping(path = "/certificates", method = RequestMethod.GET)
	public Response getCertificates(@RequestParam("ag") String assetGroup);
    
    @RequestMapping(path = "/patching", method = RequestMethod.GET)
	public Response getPatching(@RequestParam("ag") String assetGroup);
    
    @RequestMapping(path = "/patching/topnoncompliantapps", method = RequestMethod.GET)
	public Response getTopNonCompliantApps(@RequestParam("ag") String assetGroup);
    
    @RequestMapping(path = "/vulnerabilities/summarybyapplication", method = RequestMethod.GET)
	public Response getVulnerabilityByApplications(@RequestParam("ag") String assetGroup);
    
    @RequestMapping(path = "/issues/distribution", method = RequestMethod.GET)
	public Response getDistribution(@RequestParam("ag") String assetGroup);
	
}
