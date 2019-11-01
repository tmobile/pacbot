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
package com.tmobile.pacman.api.asset.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.domain.ResponseWithFieldsByTargetType;
import com.tmobile.pacman.api.asset.repository.AssetRepository;
import com.tmobile.pacman.api.asset.repository.PacmanRedshiftRepository;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class AssetServiceTest {

    @Mock
    ElasticSearchRepository elasticSearchRepository;

    @Mock
    PacmanRedshiftRepository redshiftRepository;

    @Mock
    RestClient restClient;

    @Mock
    StatusLine sl;

    @Mock
    Response response;

    @Mock
    PacmanRdsRepository pacmanRdsRepository;

    @Mock
    AssetRepository assetRepository;

    AssetServiceImpl service = new AssetServiceImpl();

    @Test
    public void testgetAssetCountByAssetGroup() throws Exception {

        Map<String, Object> tTypeMap1 = new HashMap<>();
        tTypeMap1.put("type", "ec2");

        Map<String, Object> tTypeMap2 = new HashMap<>();
        tTypeMap2.put("type", "s3");

        List<Map<String, Object>> tTypeList = new ArrayList<>();
        tTypeList.add(tTypeMap1);
        tTypeList.add(tTypeMap2);

        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("ec2", (long) 300);
        mockMap.put("s3", (long) 655);
        mockMap.put("stack", (long) 655);

        List<Map<String,Object>> typeDataSource = new ArrayList<>();
		Map<String,Object> dataSource = new HashMap<>();
		dataSource.put(Constants.TYPE, "ec2");
		dataSource.put(Constants.PROVIDER, "aws");
		typeDataSource.add(dataSource);
		dataSource = new HashMap<>();
		dataSource.put(Constants.TYPE, "s3");
		dataSource.put(Constants.PROVIDER, "aws");
		typeDataSource.add(dataSource);
        
        when(assetRepository.getAllTargetTypes(anyString())).thenReturn(tTypeList);
        when(assetRepository.getTargetTypesByAssetGroup(anyObject(), anyObject(), anyObject())).thenReturn(tTypeList);
        when(assetRepository.getAssetCountByAssetGroup(anyObject(), anyObject(),  anyObject())).thenReturn(mockMap);
        when(assetRepository.getDataSourceForTargetTypes(anyObject())).thenReturn(typeDataSource);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        List<Map<String, Object>> listOfCountMaps = new ArrayList<>();
        listOfCountMaps = service.getAssetCountByAssetGroup("aws-all", "all", "Infra & Platforms", null, null);
        assertTrue(listOfCountMaps.size() == 2);
    }

    @Test
    public void testgetApplicationsByAssetGroup() throws Exception {
        List<String> appList = Arrays.asList("pacman", "monitor");
        when(assetRepository.getApplicationByAssetGroup(anyObject(), anyObject())).thenReturn(appList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        List<Map<String, Object>> a = service.getApplicationsByAssetGroup("testAg", "testDomain");
        assertTrue(a.size() == 2);
    }

    @Test
    public void testgetEnvironmentsByAssetGroup() throws Exception {
        List<String> appList = Arrays.asList("dev", "prd");
        when(assetRepository.getEnvironmentsByAssetGroup(anyObject(), anyObject(), anyObject())).thenReturn(appList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        List<Map<String, Object>> a = service.getEnvironmentsByAssetGroup("testAg", "pacman", "testDomain");
        assertTrue(a.size() == 2);
    }

    @Test
    public void testgetAllAssetGroups() throws Exception {
        Map<String, Object> agAndDomain1 = new HashMap<>();
        Map<String, Object> agAndDomain2 = new HashMap<>();
        agAndDomain1.put("name", "testDomain");
        agAndDomain1.put("domain", "domain1");
        agAndDomain2.put("name", "name");
        agAndDomain2.put("domain", "domain2");
        List<Map<String, Object>> agAndDomList = new ArrayList<>();
        agAndDomList.add(agAndDomain1);
        agAndDomList.add(agAndDomain2);

        when(assetRepository.getAssetGroupAndDomains()).thenReturn(agAndDomList);
        when(assetRepository.getAllAssetGroups()).thenReturn(agAndDomList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        List<Map<String, Object>> a = service.getAllAssetGroups();
        assertTrue(a.size() == 2);

    }

    @Test
    public void testgetAssetGroupInfo() throws Exception {

        Map<String, Object> agMap1 = new HashMap<>();
        agMap1.put("name", "testDomain");

        List<Map<String, Object>> agList = new ArrayList<>();
        agList.add(agMap1);

        when(assetRepository.getAssetGroupInfo(anyString())).thenReturn(agMap1);
        when(assetRepository.getApplicationByAssetGroup(anyString())).thenReturn(Arrays.asList("pacman", "monitor"));
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        Map<String, Object> a = service.getAssetGroupInfo("testAg");
        System.out.println(a);
        assertTrue(a.size() == 4);

    }

    @Test
    public void testgetAssetCountByApplication() throws Exception {
        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("app1", (long) 300);
        mockMap.put("app2", (long) 655);
        mockMap.put("app3", (long) 655);

        when(assetRepository.getAssetCountByApplication(anyString(), anyString())).thenReturn(mockMap);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        List<Map<String, Object>> a = service.getAssetCountByApplication("testAg", "ec2");
        assertTrue(a.size() == 3);
    }

    @Test
    public void testgetAssetMinMax() throws Exception {
        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("app1", (long) 300);
        mockMap.put("app2", (long) 655);
        mockMap.put("app3", (long) 655);

        when(assetRepository.getAssetCountByApplication(anyString(), anyString())).thenReturn(mockMap);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        List<Map<String, Object>> a = service.getAssetCountByApplication("testAg", "ec2");
        assertTrue(a.size() == 3);
    }

    @Test
    public void testgetEc2ResourceDetail() throws Exception {

        List<Map<String, Object>> rhnResourceList = new ArrayList<>();
        List<Map<String, Object>> ec2ResourceList = new ArrayList<>();

        Map<String, Object> resource = new HashMap<>();
        resource.put("last_boot", "value");
        resource.put("instanceid", "value");
        resource.put("ip", "value");
        resource.put("_id", "value");
        resource.put("creationdate", "value");
        resource.put("last_checkin", "value");
        resource.put("imageid", "value");
        resource.put("subnetid", "value");
        resource.put("instancetype", "value");
        resource.put("accountname", "value");
        resource.put("vpcid", "value");
        resource.put("availabilityzone", "value");
        resource.put("publicipaddress", "value");
        resource.put("privateipaddress", "value");
        resource.put("statename", "value");
        resource.put("monitoringstate", "value");
        resource.put("hostid", "value");
        resource.put("statereasoncode", "value");
        resource.put("virtualizationtype", "value");
        resource.put("rootdevicename", "value");
        resource.put("keyname", "value");
        resource.put("kernelid", "value");
        resource.put("statename", "value");
        resource.put("hypervisor", "value");
        resource.put("architecture", "value");
        resource.put("tenancy", "value");
        resource.put("createdBy", "value");
        resource.put("creationDate", "value");
        resource.put("email", "value");

        rhnResourceList.add(resource);
        ec2ResourceList.add(resource);

        when(assetRepository.getEc2ResourceDetailFromRhn(anyObject())).thenReturn(rhnResourceList);
        when(assetRepository.getEc2ResourceDetail(anyObject(), anyObject())).thenReturn(ec2ResourceList);
        when(assetRepository.getResourceCreateInfo(anyObject())).thenReturn(resource);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        Map<String, Object> a = service.getEc2ResourceDetail("ag", "resourceId");

        assertTrue(a.size() > 0);
    }

    @Test
    public void testgetGenericResourceDetail() throws Exception {
        List<Map<String, Object>> ec2ResourceList = new ArrayList<>();

        Map<String, Object> ec2Resource = new HashMap<>();
        ec2Resource.put("last_boot", "value");
        ec2Resource.put("instanceid", "value");
        ec2Resource.put("ip", "value");
        ec2Resource.put("creationdate", "value");
        ec2Resource.put("_id", "value");
        ec2Resource.put("last_checkin", "value");
        ec2Resource.put("imageid", "value");
        ec2Resource.put("subnetid", "value");
        ec2Resource.put("instancetype", "value");
        ec2Resource.put("accountname", "value");
        ec2Resource.put("vpcid", "value");
        ec2Resource.put("availabilityzone", "value");
        ec2Resource.put("publicipaddress", "value");
        ec2Resource.put("privateipaddress", "value");
        ec2Resource.put("statename", "value");
        ec2Resource.put("monitoringstate", "value");
        ec2Resource.put("hostid", "value");
        ec2Resource.put("statereasoncode", "value");
        ec2Resource.put("virtualizationtype", "value");
        ec2Resource.put("rootdevicename", "value");
        ec2Resource.put("keyname", "value");
        ec2Resource.put("kernelid", "value");
        ec2Resource.put("statename", "value");
        ec2Resource.put("hypervisor", "value");
        ec2Resource.put("architecture", "value");
        ec2Resource.put("tenancy", "value");
        ec2Resource.put("createdBy", "value");
        ec2Resource.put("creationDate", "value");
        ec2Resource.put("email", "value");

        ec2ResourceList.add(ec2Resource);

        when(assetRepository.getResourceDetail(anyObject(), anyObject(), anyObject())).thenReturn(ec2ResourceList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        Map<String, Object> a = service.getGenericResourceDetail("ag", "resourceType", "resourceId");

        assertTrue(a.size() > 0);
    }

    @Test
    public void testgetEc2StateDetail() throws Exception {
        List<Map<String, Object>> ec2ResourceList = new ArrayList<>();

        Map<String, Object> ec2Resource = new HashMap<>();
        ec2Resource.put("statename", "state1");
        ec2Resource.put("createdBy", "value");
        ec2Resource.put("creationDate", "value");
        ec2Resource.put("email", "value");

        ec2ResourceList.add(ec2Resource);

        when(assetRepository.getEc2ResourceDetail(anyObject(), anyObject())).thenReturn(ec2ResourceList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        String a = service.getEc2StateDetail("ag", "a1");
        assertTrue(a.equals("state1"));
    }

    @Test
    public void testgetNotificationSummary() throws Exception {
        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("open", (long) 3);
        mockMap.put("closed", (long) 7);
        mockMap.put("upcoming", (long) 10);

        when(assetRepository.getNotificationSummary(anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        List<Map<String, Object>> a = service.getNotificationSummary("instanceId");
        assertTrue(a.size() == 3);

    }

    @Test
    public void testgetNotificationSummaryTotal() throws Exception {
        Map<String, Object> countMap1 = new HashMap<>();
        countMap1.put("count", 3);
        Map<String, Object> countMap2 = new HashMap<>();
        countMap2.put("count", 4);
        Map<String, Object> countMap3 = new HashMap<>();
        countMap3.put("count", 5);

        List<Map<String, Object>> countList = new ArrayList<>();
        countList.add(countMap1);
        countList.add(countMap2);
        countList.add(countMap3);

        String a = service.getNotificationSummaryTotal(countList);

        assertTrue(a.equals("12"));

    }

    @Test
    public void testgetResourceQualysDetail() throws Exception {

        List<Map<String, Object>> ec2ResourceList = new ArrayList<>();

        Map<String, Object> ec2Resource = new HashMap<>();
        ec2Resource.put("statename", "state1");
        ec2Resource.put("createdBy", "value");
        ec2Resource.put("creationDate", "value");
        ec2Resource.put("email", "value");
        ec2Resource.put("list", "[{\"username\":\"user\"}]");

        ec2ResourceList.add(ec2Resource);
        when(assetRepository.getQualysDetail(anyString())).thenReturn(ec2ResourceList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        List<Map<String, Object>> a = service.getResourceQualysDetail("a1");

        assertTrue(a.size() > 0);

    }

    @Test
    public void testgetAdGroupsDetail() throws Exception {

        List<Map<String, Object>> ec2ResourceList = new ArrayList<>();

        Map<String, Object> ec2Resource = new HashMap<>();
        ec2Resource.put("tags.Name", "dev-rpl");
        ec2Resource.put("platform", "value");

        ec2ResourceList.add(ec2Resource);

        when(assetRepository.getEc2ResourceDetail(anyString(), anyString())).thenReturn(ec2ResourceList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);

        List<Map<String, Object>> adList = new ArrayList<>();
        Map<String, Object> adMap = new HashMap<>();
        adMap.put("name", "r_rhel_rpl_dev_");
        adMap.put("managedBy", "admin");
        adList.add(adMap);

        when(assetRepository.getAdGroupDetails()).thenReturn(adList);

        List<Map<String, String>> a = service.getAdGroupsDetail("testAg", "a1");

        assertTrue(a.size() > 0);

    }

    @Test
    public void testgetNotificationDetails() throws Exception {
        List<Map<String, Object>> notiList = new ArrayList<>();

        Map<String, Object> notiMap = new HashMap<>();
        notiMap.put("_resourceid", "a1");
        notiMap.put("open", "p1");
        notiMap.put("closed", "P2");
        notiList.add(notiMap);
        
        when(assetRepository.getNotificationDetails(anyObject(),anyObject(),anyObject())).thenReturn(notiList);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        
        List<Map<String, Object>> a = service.getNotificationDetails("a1", null, "");
        
        assertTrue(a.size() == 1);

    }
    
    @Test
    public void testgetEc2CreatorDetail() throws Exception{
        Map<String, Object> ec2Resource = new HashMap<>();
        ec2Resource.put("statename", "state1");
        ec2Resource.put("createdBy", "value");
        ec2Resource.put("creationDate", "value");
        ec2Resource.put("email", "value");

        
        when(assetRepository.getResourceCreateInfo(anyObject())).thenReturn(ec2Resource);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
        
        
        Map<String, Object> a = service.getEc2CreatorDetail("a1");
        assertTrue(a.size()==4);
    }
    
    @Test
    public void testgetEC2AvgAndTotalCost() throws Exception {
        
        ReflectionTestUtils.setField(service, "svcCorpUserId", "");
        ReflectionTestUtils.setField(service, "svcCorpPassword", "");
        ReflectionTestUtils.setField(service, "insightsTokenUrl", "");
        ReflectionTestUtils.setField(service, "insightsCostUrl", "");

        String costResponse = "{\"totalCost\":500}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.getHttpGet(anyString(), anyObject())).thenReturn(costResponse);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn("{\"token\":\"123\"}");

        
        
        Map<String, Object> a = service.getEC2AvgAndTotalCost("a1");
        assertTrue(a.get("totalCost").toString().equals("500.0"));
    }
    
    @Test
    public void testgetEditFieldsByTargetType() throws Exception{
        String responseJson = "{\"dataTypes_info\":{\"key1\":\"value1\",\"key2\":\"value2\"}}";
        when(assetRepository.getDataTypeInfoByTargetType(anyObject())).thenReturn(responseJson);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
      
        ResponseWithFieldsByTargetType resp = service.getEditFieldsByTargetType("ec2");
        assertTrue(resp.getEditableFields().size()==2);
    }
    
    @Test
    public void testgetDataTypeInfoByTargetType() throws Exception{
        String responseJson = "{\"dataTypes_info\":{\"key1\":\"value1\",\"key2\":\"value2\"}}";
        when(assetRepository.getDataTypeInfoByTargetType(anyObject())).thenReturn(responseJson);
        ReflectionTestUtils.setField(service, "repository", assetRepository);
      
        List<Map<String, Object>> resp = service.getDataTypeInfoByTargetType("ec2");
        assertTrue(resp.size()==1);
    }
}
