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
package com.tmobile.pacman.api.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.admin.domain.PluginDetails;
import com.tmobile.pacman.api.admin.repository.service.PluginService;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

import io.swagger.annotations.Api;

@Api(value = "/plugin", consumes = "application/json", produces = "application/json")
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_ADMIN')")
@RequestMapping("/plugin")
public class PluginController {
    PluginService pluginService;
    
    @RequestMapping(path = "/v1/plugins", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getplugins( @RequestParam(name = "pluginId", required = false) String pluginId){
        PluginDetails response = null;
        try {
            response = pluginService.pluginList(pluginId);
        } catch (ServiceException e) {
         //  return complianceService.formatException(e);
            return null;
        }

        return ResponseUtils.buildSucessResponse(response);
    }
    
    @RequestMapping(path = "/v1/updateplugins", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> updateplugins(@RequestBody PluginDetails request){
        return  ResponseUtils.buildSucessResponse("success");
        
    }
}
