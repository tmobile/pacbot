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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class, Util.class })
public class AssetCountControllerTest {

    @Mock
    AssetService service;

    AssetCountController controller = new AssetCountController();

    @Test
    public void testgeAssetCount() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();

        when(service.getAssetCountByAssetGroup(anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj3 = controller.geAssetCount("ag", "type", "domain", null, null);

        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("count", "100");
        tTypeMap.put("type", "ec2");
        tTypeList.add(tTypeMap);

        when(service.getAssetCountByAssetGroup(anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.geAssetCount("ag", "type", "domain", null, null);

        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);

        ResponseEntity<Object> responseObj2 = controller.geAssetCount("ag", null, "domain", null, null);
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);

    }

    @Test
    public void testgeAssetCountByTypeAndApplication() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("count", "100");
        tTypeMap.put("type", "ec2");
        tTypeList.add(tTypeMap);

        mockStatic(Util.class);
        when(Util.isValidTargetType(anyString(), anyString())).thenReturn(true, true, false);
        when(service.getAssetCountByApplication(anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.geAssetCountByTypeAndApplication("ag", "type");

        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);

        doThrow(new DataException()).when(service).getAssetCountByApplication(anyObject(), anyObject());
        responseObj = controller.geAssetCountByTypeAndApplication("ag", "type");

        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        responseObj = controller.geAssetCountByTypeAndApplication("ag", "type");

        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

    }

    @Test
    public void testgeAssetCountByTypeEnvironment() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("count", "100");
        tTypeMap.put("type", "ec2");
        tTypeList.add(tTypeMap);

        mockStatic(Util.class);
        when(Util.isValidTargetType(anyString(), anyString())).thenReturn(true, true, false);
        when(service.getAssetCountByEnvironment(anyObject(), anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.geAssetCountByTypeEnvironment("ag", "app", "type");

        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);

        doThrow(new NullPointerException()).when(service).getAssetCountByEnvironment(anyObject(), anyObject(), anyObject());
        responseObj = controller.geAssetCountByTypeEnvironment("ag", "app", "type");

        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        responseObj = controller.geAssetCountByTypeEnvironment("ag", "app", "type");

        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

    }
}
