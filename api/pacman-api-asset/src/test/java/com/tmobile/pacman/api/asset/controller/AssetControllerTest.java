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

import com.tmobile.pacman.api.asset.domain.AssetUpdateRequest;
import com.tmobile.pacman.api.asset.model.DefaultUserAssetGroup;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class AssetControllerTest {

    @Mock
    AssetService service;

    AssetController controller = new AssetController();

    @Test
    public void testgetListOfTargetTypes() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();

        when(service.getTargetTypesForAssetGroup(anyObject(), anyObject(), anyObject()	)).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.getListOfTargetTypes("ag", "domain", "provider");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("type", "ec2");
        tTypeMap.put("category", "Compute");
        tTypeMap.put("domain", "Infra & Platforms");
        tTypeList.add(tTypeMap);

        ResponseEntity<Object> responseObj = controller.getListOfTargetTypes("ag", "domain", "provider");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
    }

    @Test
    public void testgetListOfApplications() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("name", "pacman");
        tTypeList.add(tTypeMap);

        when(service.getApplicationsByAssetGroup(anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.getListOfApplications("ag", "domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);

        doThrow(new DataException()).when(service).getApplicationsByAssetGroup(anyObject(), anyObject());
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getListOfApplications("ag", "domain");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testgetListOfEnvironments() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        
        when(service.getEnvironmentsByAssetGroup(anyObject(), anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.getListOfEnvironments("ag", "application", "domain");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("name", "pacman");
        tTypeList.add(tTypeMap);

        when(service.getEnvironmentsByAssetGroup(anyObject(), anyObject(), anyObject())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.getListOfEnvironments("ag", "application", "domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
    }

    @Test
    public void testgetAllAssetGroups() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("name", "pacman");
        tTypeList.add(tTypeMap);

        when(service.getAllAssetGroups()).thenReturn(tTypeList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.getAllAssetGroups();
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        
        doThrow(new NumberFormatException()).when(service).getAllAssetGroups();
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getAllAssetGroups();
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testgetAssetGroupInfo() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        
        when(service.getAssetGroupInfo("ag")).thenReturn(tTypeMap);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.getAssetGroupInfo("ag");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        tTypeMap.put("name", "aws-all");
        tTypeList.add(tTypeMap);

        when(service.getAssetGroupInfo("ag")).thenReturn(tTypeMap);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.getAssetGroupInfo("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
    }

    @Test
    public void testgetUserDefaultAssetGroup() throws Exception {

        when(service.getUserDefaultAssetGroup(anyString())).thenReturn("aws-all");
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.getUserDefaultAssetGroup("userid");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        
        when(service.getUserDefaultAssetGroup(anyString())).thenReturn("");
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.getUserDefaultAssetGroup("userid");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testsaveOrUpdateAssetGroup() throws Exception {
        DefaultUserAssetGroup defaultUserAssetGroup = new DefaultUserAssetGroup();
        defaultUserAssetGroup.setDefaultAssetGroup("aws-all");
        defaultUserAssetGroup.setUserId("userId");
        defaultUserAssetGroup.getUserId();
        defaultUserAssetGroup.getDefaultAssetGroup();
        
        when(service.saveOrUpdateAssetGroup(anyObject())).thenReturn(true);
        ReflectionTestUtils.setField(controller, "assetService", service);
       
 
        ResponseEntity<Object> responseObj = controller.saveOrUpdateAssetGroup(defaultUserAssetGroup);
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        
        when(service.saveOrUpdateAssetGroup(anyObject())).thenReturn(false);
        ReflectionTestUtils.setField(controller, "assetService", service);
       
        ResponseEntity<Object> responseObj0 = controller.saveOrUpdateAssetGroup(defaultUserAssetGroup);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testappendToRecentlyViewedAG() throws Exception {
        List<Map<String, Object>> agList = new ArrayList<>();
        Map<String, Object> agMap = new HashMap<>();
        agMap.put("name", "aws-all");
        agList.add(agMap);
        when(service.saveAndAppendToRecentlyViewedAG(anyObject(), anyObject())).thenReturn(agList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.appendToRecentlyViewedAG("userId", "ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        ResponseEntity<Object> responseObj0 = controller.appendToRecentlyViewedAG("userId", "");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        doThrow(new DataException()).when(service).saveAndAppendToRecentlyViewedAG(anyString(),anyString());
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.appendToRecentlyViewedAG("userId", "ag");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

    }

    @Test
    public void testretrieveAssetConfig() throws Exception {

        when(service.retrieveAssetConfig(anyObject(), anyObject())).thenReturn("{}");
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.retrieveAssetConfig("a1", "passwordPolicy");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        
        when(service.retrieveAssetConfig(anyObject(), anyObject())).thenReturn("");
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.retrieveAssetConfig("a1", "passwordPolicy");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    

    @Test
    public void testsaveAssetConfig() throws Exception {

        when(service.saveAssetConfig(anyObject(), anyObject(), anyObject())).thenReturn(1);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.saveAssetConfig("a1", "passwordPolicy", "{}");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        
        when(service.saveAssetConfig(anyObject(), anyObject(), anyObject())).thenReturn(0);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.saveAssetConfig("a1", "passwordPolicy", "{}");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testupdateAsset() throws Exception {

        AssetUpdateRequest assetUpdateRequest = new AssetUpdateRequest();
        assetUpdateRequest.setAg("ag");
        assetUpdateRequest.setTargettype("targetType");
        assetUpdateRequest.setUpdateBy("update_by");
        assetUpdateRequest.setUpdates(new ArrayList<>());
        assetUpdateRequest.setResources(new HashMap<>());
        
        when(service.updateAsset(anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(0);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj4 = controller.updateAsset(assetUpdateRequest);
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        when(service.updateAsset(anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(1);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.updateAsset(assetUpdateRequest);

        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data") != null);
        
      
        
        assetUpdateRequest.setUpdateBy(null);
        ResponseEntity<Object> responseObj0 = controller.updateAsset(assetUpdateRequest);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        assetUpdateRequest.setAg("");
        ResponseEntity<Object> responseObj2 = controller.updateAsset(assetUpdateRequest);
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        assetUpdateRequest.setAg("aws-all");
        assetUpdateRequest.setTargettype("");
        ResponseEntity<Object> responseObj3 = controller.updateAsset(assetUpdateRequest);
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
      

    }

    @Test
    public void testgetResourceCreatedDate() throws Exception {

        AssetUpdateRequest assetUpdateRequest = new AssetUpdateRequest();
        assetUpdateRequest.setAg("ag");
        assetUpdateRequest.setTargettype("targetType");
        assetUpdateRequest.setUpdateBy("update_by");
        assetUpdateRequest.setUpdates(new ArrayList<>());
        assetUpdateRequest.setResources(new HashMap<>());

        when(service.getResourceCreatedDate(anyObject(), anyObject())).thenReturn("01-01-2018");
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj = controller.getResourceCreatedDate("a1", "ec2");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj.getBody()).get("data").toString().equals("01-01-2018"));
        
        when(service.getResourceCreatedDate(anyObject(), anyObject())).thenReturn("");
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj1 = controller.getResourceCreatedDate("a1", "ec2");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        when(service.getResourceCreatedDate(anyObject(), anyObject())).thenReturn(null);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj2 = controller.getResourceCreatedDate("a1", "ec2");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
}
