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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.compliance.service.AssetGroupService;

/**
 * The Class AssetGroupController.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class AssetGroupController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetGroupController.class);
    @Autowired
    private AssetGroupService assetGroupService;

    /**
     * Gets the all asset group apis.
     *
     * @return the all asset group apis
     */
    @RequestMapping(path = "/v1/get-all-asset-group-apis", method = RequestMethod.GET)
    
    public ResponseEntity<List<Map<String, Object>>> getAllAssetGroupApis() {
        try {
            List<Map<String, Object>> assetGroupDetails = assetGroupService
                    .getAllAssetGroupApis();
            return new ResponseEntity<>(
                    assetGroupDetails, HttpStatus.OK);
        } catch (Exception exception) {
            LOGGER.error("Exception in sendTextMail:", exception);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
