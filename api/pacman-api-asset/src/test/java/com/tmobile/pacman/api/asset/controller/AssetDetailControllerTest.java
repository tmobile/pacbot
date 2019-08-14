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

import com.tmobile.pacman.api.asset.client.ComplianceServiceClient;
import com.tmobile.pacman.api.asset.domain.PageFilterRequest;
import com.tmobile.pacman.api.asset.domain.PolicyViolationApi;
import com.tmobile.pacman.api.asset.domain.PolicyViolationApiData;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class AssetDetailControllerTest {

    @Mock
    AssetService service;

    @Mock
    ComplianceServiceClient complianceServiceClient;

    AssetDetailController controller = new AssetDetailController();

    @Test
    public void testgetCPUUtilizationByInstanceId() throws Exception {
        List<Map<String, Object>> utilList = new ArrayList<>();
        Map<String, Object> utilMap = new HashMap<>();
        utilMap.put("date", "d1");
        utilMap.put("cpu-utilization", 50);
        utilList.add(utilMap);

        when(service.getInstanceCPUUtilization(anyObject())).thenReturn(utilList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj1 = controller.getCPUUtilizationByInstanceId("");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        ResponseEntity<Object> responseObj2 = controller.getCPUUtilizationByInstanceId("a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj2.getBody()).get("data") != null);

        doThrow(new DataException()).when(service).getInstanceCPUUtilization(anyObject());
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getCPUUtilizationByInstanceId("a1");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testgetDiskUtilizationByInstanceId() throws Exception {
        List<Map<String, Object>> utilList = new ArrayList<>();
        Map<String, Object> utilMap = new HashMap<>();
        utilMap.put("date", "d1");
        utilMap.put("disk-utilization", 50);
        utilList.add(utilMap);

        when(service.getInstanceDiskUtilization(anyObject())).thenReturn(utilList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj1 = controller.getDiskUtilizationByInstanceId("");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        ReflectionTestUtils.setField(controller, "qualysEnabled", true);
        ResponseEntity<Object> responseObj2 = controller.getDiskUtilizationByInstanceId("a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj2.getBody()).get("data") != null);

        doThrow(new DataException()).when(service).getInstanceDiskUtilization(anyObject());
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getDiskUtilizationByInstanceId("a1");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testgetInstalledSoftwareDetailsByInstanceId() throws Exception {
        List<Map<String, Object>> softwareList = new ArrayList<>();

        when(service.getInstanceSoftwareInstallDetails(anyObject(), anyObject(), anyObject(), anyObject()))
                .thenReturn(softwareList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getInstalledSoftwareDetailsByInstanceId("a1", 1, 1, "pacman");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        Map<String, Object> softwareMap1 = new HashMap<>();
        softwareMap1.put("name", "x");
        softwareMap1.put("value", "y");
        Map<String, Object> softwareMap2 = new HashMap<>();
        softwareMap2.put("name", "p");
        softwareMap2.put("value", "q");
        softwareList.add(softwareMap2);
        Map<String, Object> softwareMap3 = new HashMap<>();
        softwareMap3.put("name", "a");
        softwareMap3.put("value", "b");
        softwareList.add(softwareMap3);

        when(service.getInstanceSoftwareInstallDetails(anyObject(), anyObject(), anyObject(), anyObject()))
                .thenReturn(softwareList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ReflectionTestUtils.setField(controller, "qualysEnabled", true);

        ResponseEntity<Object> responseObj1 = controller.getInstalledSoftwareDetailsByInstanceId("a1", 0, 1, "pacman");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj1.getBody()).get("data") != null);

        ResponseEntity<Object> responseObj2 = controller.getInstalledSoftwareDetailsByInstanceId("a1", 1, 1, "pacman");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj2.getBody()).get("data") != null);

        ResponseEntity<Object> responseObj3 = controller.getInstalledSoftwareDetailsByInstanceId("a1", 6, 1, "pacman");
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        ResponseEntity<Object> responseObj4 = controller.getInstalledSoftwareDetailsByInstanceId("", 1, 1, "pacman");
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        ResponseEntity<Object> responseObj5 = controller.getInstalledSoftwareDetailsByInstanceId("a1", 1, 8, "pacman");
        assertTrue(responseObj5.getStatusCode() == HttpStatus.OK);

        ResponseEntity<Object> responseObj6 = controller.getInstalledSoftwareDetailsByInstanceId("a1", 0, 0, "pacman");
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);

        ResponseEntity<Object> responseObj7 = controller.getInstalledSoftwareDetailsByInstanceId("a1", null, 0,
                "pacman");
        assertTrue(responseObj7.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testgetOpenPortsByInstanceId() throws Exception {
        List<Map<String, Object>> softwareList = new ArrayList<>();

        when(service.getInstanceSoftwareInstallDetails(anyObject(), anyObject(), anyObject(), anyObject()))
                .thenReturn(softwareList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getOpenPortsByInstanceId("a1", 1, 1, "pacman");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        Map<String, Object> softwareMap1 = new HashMap<>();
        softwareMap1.put("name", "x");
        softwareMap1.put("value", "y");
        Map<String, Object> softwareMap2 = new HashMap<>();
        softwareMap2.put("name", "p");
        softwareMap2.put("value", "q");
        softwareList.add(softwareMap2);
        Map<String, Object> softwareMap3 = new HashMap<>();
        softwareMap3.put("name", "a");
        softwareMap3.put("value", "b");
        softwareList.add(softwareMap3);

        when(service.getOpenPortDetails(anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(softwareList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ReflectionTestUtils.setField(controller, "qualysEnabled", true);

        ResponseEntity<Object> responseObj1 = controller.getOpenPortsByInstanceId("a1", 0, 1, "pacman");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj1.getBody()).get("data") != null);

        ResponseEntity<Object> responseObj2 = controller.getOpenPortsByInstanceId("a1", 1, 1, "pacman");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj2.getBody()).get("data") != null);

        ResponseEntity<Object> responseObj3 = controller.getOpenPortsByInstanceId("a1", 6, 1, "pacman");
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        ResponseEntity<Object> responseObj4 = controller.getOpenPortsByInstanceId("", 1, 1, "pacman");
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        ResponseEntity<Object> responseObj5 = controller.getOpenPortsByInstanceId("a1", 1, 8, "pacman");
        assertTrue(responseObj5.getStatusCode() == HttpStatus.OK);

        ResponseEntity<Object> responseObj6 = controller.getOpenPortsByInstanceId("a1", 0, 0, "pacman");
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);

        ResponseEntity<Object> responseObj7 = controller.getOpenPortsByInstanceId("a1", null, 0, "pacman");
        assertTrue(responseObj7.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testgetAwsNotificationSummary() throws Exception {
        List<Map<String, Object>> notiList = new ArrayList<>();
        Map<String, Object> notiMap = new HashMap<>();
        notiMap.put("name", "a");
        notiMap.put("vaule", "b");
        notiList.add(notiMap);
        when(service.getNotificationSummary(anyString())).thenReturn(notiList);
        when(service.getNotificationSummaryTotal(anyObject())).thenReturn("total");

        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.getAwsNotificationSummary("a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj1.getBody()).get("data") != null);

        ResponseEntity<Object> responseObj2 = controller.getAwsNotificationSummary("");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        doThrow(new NullPointerException()).when(service).getNotificationSummary(anyString());
        ResponseEntity<Object> responseObj3 = controller.getAwsNotificationSummary("a1");
        assertTrue(((Map<String, Object>) responseObj3.getBody()).get("data") != null);

    }

    @Test
    public void testgetAwsNotificationDetails() throws Exception {
        List<Map<String, Object>> notiList = new ArrayList<>();
        PageFilterRequest pageFilterRequest = new PageFilterRequest();
        pageFilterRequest.setFrom(1);
        pageFilterRequest.setSize(1);
        pageFilterRequest.setSearchText("pacman");
        pageFilterRequest.setFilter(null);

        pageFilterRequest.getFilter();
        pageFilterRequest.getFrom();
        pageFilterRequest.getSize();
        pageFilterRequest.getSearchText();

        when(service.getNotificationDetails(anyString(), anyObject(), anyString())).thenReturn(notiList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj0 = controller.getAwsNotificationDetails(pageFilterRequest, "a1");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);

        Map<String, Object> notiMap1 = new HashMap<>();
        notiMap1.put("name", "x");
        notiMap1.put("value", "y");
        notiList.add(notiMap1);
        Map<String, Object> notiMap2 = new HashMap<>();
        notiMap2.put("name", "p");
        notiMap2.put("value", "q");
        notiList.add(notiMap2);
        Map<String, Object> notiMap3 = new HashMap<>();
        notiMap3.put("name", "a");
        notiMap3.put("value", "b");
        notiList.add(notiMap3);

        when(service.getNotificationDetails(anyString(), anyObject(), anyString())).thenReturn(notiList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.getAwsNotificationDetails(pageFilterRequest, "a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);

        ResponseEntity<Object> responseObj2 = controller.getAwsNotificationDetails(pageFilterRequest, "");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        pageFilterRequest.setFrom(-1);
        ResponseEntity<Object> responseObj3 = controller.getAwsNotificationDetails(pageFilterRequest, "a1");
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        pageFilterRequest.setFrom(18);
        ResponseEntity<Object> responseObj4 = controller.getAwsNotificationDetails(pageFilterRequest, "a1");
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        pageFilterRequest.setFrom(1);
        pageFilterRequest.setSize(8);
        ResponseEntity<Object> responseObj5 = controller.getAwsNotificationDetails(pageFilterRequest, "a1");
        assertTrue(responseObj5.getStatusCode() == HttpStatus.OK);

        pageFilterRequest.setFrom(1);
        pageFilterRequest.setSize(-1);
        ResponseEntity<Object> responseObj6 = controller.getAwsNotificationDetails(pageFilterRequest, "a1");
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);

    }

    @Test
    public void testgetEc2CreatorDetail() throws Exception {
        Map<String, Object> creatorMap = new HashMap<>();
        creatorMap.put("creationDate", "x");
        creatorMap.put("userid", "y");
        when(service.getEc2CreatorDetail(anyString())).thenReturn(creatorMap);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.getEc2CreatorDetail("ag", "ec2", "a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);

        doThrow(new NullPointerException()).when(service).getEc2CreatorDetail("a1");
        ResponseEntity<Object> responseObj2 = controller.getEc2CreatorDetail("ag", "ec2", "a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testgetAdGroupsDetail() throws Exception {
        List<Map<String, String>> adList = new ArrayList<>();
        Map<String, String> adMap = new HashMap<>();
        adMap.put("group", "x");
        adMap.put("admin", "y");
        adList.add(adMap);
        when(service.getAdGroupsDetail(anyString(), anyString())).thenReturn(adList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.getAdGroupsDetail("ag", "a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);

        doThrow(new NullPointerException()).when(service).getAdGroupsDetail(anyString(), anyString());
        ResponseEntity<Object> responseObj2 = controller.getAdGroupsDetail("ag", "a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testgetEc2ResourceSummary() throws Exception {
        List<Map<String, Object>> ec2List = new ArrayList<>();
        Map<String, Object> ec2Map = new HashMap<>();
        ec2Map.put("resourceid", "x");
        ec2Map.put("ipaddress", "y");
        ec2List.add(ec2Map);
        when(service.getEC2AvgAndTotalCost(anyString())).thenReturn(ec2Map);

        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.getEc2ResourceSummary("a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj1.getBody()).get("data") != null);

        doThrow(new NullPointerException()).when(service).getEC2AvgAndTotalCost(anyString());
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj2 = controller.getEc2ResourceSummary("a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);

    }

    @Test
    public void testgetEc2ResourceDetail() throws Exception {
        List<Map<String, Object>> ec2List = new ArrayList<>();
        Map<String, Object> ec2Map = new HashMap<>();
        ec2Map.put("resourceid", "x");
        ec2Map.put("ipaddress", "y");
        ec2List.add(ec2Map);
        when(service.getEc2ResourceDetail(anyString(), anyString())).thenReturn(ec2Map);
        when(service.getGenericResourceDetail(anyString(), anyString(), anyString())).thenReturn(ec2Map);

        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj1 = controller.getEc2ResourceDetail("ag", "ec2", "a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj1.getBody()).get("data") != null);

        doThrow(new NullPointerException()).when(service).getEc2ResourceDetail(anyString(), anyString());
        ResponseEntity<Object> responseObj2 = controller.getEc2ResourceDetail("ag", "ec2", "a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);

        ResponseEntity<Object> responseObj3 = controller.getEc2ResourceDetail("ag", "s3", "a1");
        assertTrue(responseObj3.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj3.getBody()).get("data") != null);

    }

    @Test
    public void testgetEc2ResourceSummary1() throws Exception {

        PolicyViolationApiData violationData = new PolicyViolationApiData();
        violationData.setCompliance("75");
        String compliance = violationData.getCompliance();

        PolicyViolationApi violationSummary = new PolicyViolationApi();
        violationSummary.setData(violationData);
        violationSummary.getData();
        violationSummary.setMessage("Compliance Data");
        violationSummary.getMessage();
        violationSummary.toString();

        List<Map<String, Object>> utilList = new ArrayList<>();
        Map<String, Object> utilMap = new HashMap<>();
        utilMap.put("date", "d1");
        utilMap.put("cpu-utilization", 50);
        utilList.add(utilMap);

        when(complianceServiceClient.getPolicyViolationSummary(anyString(), anyString(), anyString()))
                .thenReturn(violationSummary);
        when(service.getEc2StateDetail(anyString(), anyString())).thenReturn("Started");
        when(service.getInstanceCPUUtilization(anyString())).thenReturn(utilList);

        ReflectionTestUtils.setField(controller, "complianceServiceClient", complianceServiceClient);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj1 = controller.getEc2ResourceSummary("ag", "ec2", "a1");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.OK);
        assertTrue(((Map<String, Object>) responseObj1.getBody()).get("data") != null);

        doThrow(new NullPointerException()).when(service).getInstanceCPUUtilization(anyString());
        ResponseEntity<Object> responseObj2 = controller.getEc2ResourceSummary("ag", "ec2", "a1");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.OK);

        doThrow(new NullPointerException()).when(service).getEc2StateDetail(anyString(),anyString());
        ResponseEntity<Object> responseObj3 = controller.getEc2ResourceSummary("ag", "ec2", "a1");
        assertTrue(responseObj3.getStatusCode() == HttpStatus.OK);
    }

}
