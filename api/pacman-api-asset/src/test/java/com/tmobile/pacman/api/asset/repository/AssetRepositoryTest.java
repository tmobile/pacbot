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
package com.tmobile.pacman.api.asset.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Date;
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

import com.tmobile.pacman.api.asset.model.DefaultUserAssetGroup;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class AssetRepositoryTest {

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

    AssetRepositoryImpl repository = new AssetRepositoryImpl();

    @Test
    public void testGetAssetCountByAssetGroupForTypeAll() throws Exception {
        repository.init();
        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("ec2", (long) 300);
        mockMap.put("s3", (long) 655);
        mockMap.put("stack", (long) 655);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        Map<String, Long> countMap = new HashMap<>();
        countMap = repository.getAssetCountByAssetGroup("aws-all", "all", null);
        assertTrue(countMap.size() > 2);
    }

    @Test
    public void testGetAssetCountByAssetGroupForTypeOtherThanAll() throws Exception {

        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject())).thenReturn((long) 23);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        Map<String, Long> countMap = new HashMap<>();
        countMap = repository.getAssetCountByAssetGroup("aws-all", "s3", null);
        assertEquals(1, countMap.size());
    }

    @Test
    public void testGetAssetCountByAssetGroupForInvalidAG() throws Exception {
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject())).thenReturn((long) 0);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        Map<String, Long> countMap = new HashMap<>();
        countMap = repository.getAssetCountByAssetGroup("invalid-ag", "s3", null);
        assertEquals(1, countMap.size());
        assertEquals(0, countMap.get("s3").longValue());
    }

    @Test
    public void testGetAssetCountByAssetGroupForInvalidType() throws Exception {
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject())).thenReturn((long) 0);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        Map<String, Long> countMap = new HashMap<>();
        countMap = repository.getAssetCountByAssetGroup("aws-all", "invalid-type", null);
        assertEquals("0", countMap.get("invalid-type").toString());
    }

    @Test
    public void testGetTargetTypesByAssetGroupForInvalidDomain() {

        List<Map<String, Object>> tTypeList = new ArrayList<>();

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> targetTypesList = repository.getTargetTypesByAssetGroup("aws-all", "invalid-domain", null);

        assertEquals(0, targetTypesList.size());

    }

    @Test
    public void testGetTargetTypesByAssetGroupForInfraDomain() {

        Map<String, Object> tTypeMap1 = new HashMap<>();
        tTypeMap1.put("type", "ec2");

        Map<String, Object> tTypeMap2 = new HashMap<>();
        tTypeMap1.put("type", "s3");

        List<Map<String, Object>> tTypeList = new ArrayList<>();
        tTypeList.add(tTypeMap1);
        tTypeList.add(tTypeMap2);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> targetTypesList = repository.getTargetTypesByAssetGroup("aws-all",
                "Infra & Platforms", null);

        assertTrue(targetTypesList.size() > 1);
    }

    @Test
    public void testGetAllTargetTypes() {
        Map<String, Object> tTypeMap1 = new HashMap<>();
        tTypeMap1.put("type", "ec2");

        Map<String, Object> tTypeMap2 = new HashMap<>();
        tTypeMap1.put("type", "s3");

        List<Map<String, Object>> tTypeList = new ArrayList<>();
        tTypeList.add(tTypeMap1);
        tTypeList.add(tTypeMap2);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> allTypes = repository.getAllTargetTypes("*");
        assertTrue(allTypes.size() > 1);

    }

    @Test
    public void testGetApplicationByAssetGroup() throws Exception {

        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("app1", (long) 300);
        mockMap.put("app2", (long) 655);
        mockMap.put("app3", (long) 655);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        List<String> allApps = repository.getApplicationByAssetGroup("aws-all");
        assertTrue(allApps.size() == 3);
    }

    @Test
    public void testGetApplicationByAssetGroupAndDomain() throws Exception {

        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("app1", (long) 300);
        mockMap.put("app2", (long) 655);
        mockMap.put("app3", (long) 655);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<String> allApps = repository.getApplicationByAssetGroup("aws-all", "Infra");
        assertTrue(allApps.size() == 3);
    }

    @Test
    public void testGetEnvironmentsByAssetGroup() throws Exception {

        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("Non-Production::dev", (long) 300);
        mockMap.put("Non-Production::uat", (long) 655);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<String> allApps = repository.getEnvironmentsByAssetGroup("aws-all", "ec2", "Infra");
        assertTrue(allApps.size() == 2);
    }

    @Test
    public void testGetAllAssetGroups() throws Exception {

        Map<String, Object> agMap1 = new HashMap<>();
        agMap1.put("name", "applicationsecurity");

        Map<String, Object> agMap2 = new HashMap<>();
        agMap1.put("name", "aws-all");

        List<Map<String, Object>> agList = new ArrayList<>();
        agList.add(agMap1);
        agList.add(agMap2);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(agList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> allAGs = repository.getAllAssetGroups();
        assertTrue(allAGs.size() == 2);
    }

    @Test
    public void testGetAssetGroupInfo() throws Exception {

        Map<String, Object> agMap1 = new HashMap<>();
        agMap1.put("name", "testAG");

        List<Map<String, Object>> agList = new ArrayList<>();
        agList.add(agMap1);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(agList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        Map<String, Object> agInfo = repository.getAssetGroupInfo("dummyString");
        assertTrue(agInfo.size() == 1);

    }

    @Test
    public void testGetAssetCountByApplication() throws Exception {

        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("testAG", (long) 400);
        mockMap.put("pacman", (long) 600);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        Map<String, Long> appInfo = repository.getAssetCountByApplication("ag", "ec2");
        assertTrue(appInfo.size() == 2);
        assertTrue((appInfo.get("testAG").longValue() + appInfo.get("pacman").longValue()) == 1000);

    }

    @Test
    public void testGetAssetMinMax() throws Exception {

        String response = "{\"hits\":{\"total\":31,\"max_score\":7.7013307,\"hits\":[{\"_source\":{\"date\":\"2018-05-16\",\"min\":5866,\"max\":6125}},{\"_source\":{\"date\":\"2018-05-19\",\"min\":5917,\"max\":6073}}]}}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        repository.getAssetMinMax("ag", "type", new Date(), new Date());
    }

    @Test
    public void testsaveOrUpdateAssetGroup() throws Exception {

        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("testAG", (long) 400);
        mockMap.put("pacman", (long) 600);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        DefaultUserAssetGroup defaultUserAssetGroup = new DefaultUserAssetGroup();
        defaultUserAssetGroup.setUserId("userId");
        defaultUserAssetGroup.setDefaultAssetGroup("defaultAg");

        when(pacmanRdsRepository.count(anyString())).thenReturn(1);
        when(pacmanRdsRepository.update(anyString(), anyString(), anyString())).thenReturn(1);

        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        Integer status = repository.saveOrUpdateAssetGroup(defaultUserAssetGroup);
        assertTrue(status == 1);

    }

    @Test
    public void testgetUserDefaultAssetGroup() throws Exception {
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("testAG");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        String ag = repository.getUserDefaultAssetGroup("userId");
        assertEquals("testAG", ag);

        doThrow(new NullPointerException()).when(pacmanRdsRepository).queryForString(anyString());
        ag = repository.getUserDefaultAssetGroup("userId");
        assertEquals("", ag);
    }

    @Test
    public void testretrieveAssetConfig() throws Exception {
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("configType");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        String ag = repository.retrieveAssetConfig("resourceId", "configType");
        assertEquals("configType", ag);

        doThrow(new NullPointerException()).when(pacmanRdsRepository).queryForString(anyString());
        ag = repository.retrieveAssetConfig("resourceId", "configType");
        assertEquals("", ag);
    }

    @Test
    public void testsaveAssetConfig() throws Exception {
        when(pacmanRdsRepository.update(anyString(), anyString(), anyString(), anyString(), anyObject())).thenReturn(1);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        Integer ag = repository.saveAssetConfig("resourceId", "configType", "config");
        assertTrue(ag == 1);

        doThrow(new NullPointerException()).when(pacmanRdsRepository).update(anyString(), anyString(), anyString(),
                anyString(), anyObject());
        ag = repository.saveAssetConfig("resourceId", "configType", "config");
        assertTrue(ag == -1);
    }

    @Test
    public void testgetAssetCountByEnvironment() throws Exception {

        String response = "{\"aggregations\":{\"apps\":{\"buckets\":[{\"key\":\"test1\",\"doc_count\":314,\"envs\":{\"buckets\":[{\"key\":\"PROD\",\"doc_count\":309},{\"key\":\"test\",\"doc_count\":4},{\"key\":\"DEV\",\"doc_count\":1}]}}]}}}";

        mockStatic(PacHttpUtils.class);

        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        List<Map<String, Object>> countList = repository.getAssetCountByEnvironment("ag", "app", "ec2");

        assertTrue(((List) countList.get(0).get("environments")).size() == 3);

    }

    @Test
    public void testsaveAndAppendAssetGroup() throws Exception {

        Map<String, Object> agMap1 = new HashMap<>();
        agMap1.put("name", "applicationsecurity");

        Map<String, Object> agMap2 = new HashMap<>();
        agMap1.put("name", "aws-all");

        Map<String, Object> agMap3 = new HashMap<>();
        agMap3.put("recentlyViewedAg", "aws-all");

        List<Map<String, Object>> agList = new ArrayList<>();
        agList.add(agMap1);
        agList.add(agMap2);
        agList.add(agMap3);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(agList);
        when(pacmanRdsRepository.count(anyString())).thenReturn(1);

        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> a = repository.saveAndAppendAssetGroup("userId", "aws-all");

        assertTrue(a.size() > 0);

        boolean invalidAgExceptionCaught = false;
        try {
            a = repository.saveAndAppendAssetGroup("userId", "aws-allxxx");

        } catch (DataException e) {
            invalidAgExceptionCaught = true;
        }
        assertTrue(invalidAgExceptionCaught);
    }

    @Test
    public void testgetListAssets() throws Exception {
        Map<String, String> filter = new HashMap<>();

        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("type", "ec2");

        Map<String, Object> ttypeMap2 = new HashMap<>();
        ttypeMap2.put("type", "s3");

        List<Map<String, Object>> ttypeList = new ArrayList<>();
        ttypeList.add(ttypeMap1);
        ttypeList.add(ttypeMap2);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(ttypeList);
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");

        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        Map<String, Object> assetMap1 = new HashMap<>();
        assetMap1.put("_resourceid", "a1");

        Map<String, Object> assetMap2 = new HashMap<>();
        assetMap2.put("_resourceid", "b2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(assetMap1);
        assetList.add(assetMap2);

        when(elasticSearchRepository.getDataFromESBySize(anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyInt(), anyInt(), anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        List<Map<String, Object>> aList1 = repository.getListAssets("aws-all", filter, 0, 2, null);
        assertTrue(aList1.get(0).get("_resourceid") != null);
        assertTrue(aList1.size() == 2);

        when(pacmanRdsRepository.queryForString(anyString())).thenReturn(null);
        List<Map<String, Object>> aList2 = repository.getListAssets("aws-all", filter, 0, 2, null);
        assertTrue(aList2.get(0).get("_resourceid") != null);
        assertTrue(aList2.size() == 2);
        filter.put("resourceType", "ec2");
        filter.put("environment", "dev");
        filter.put("application", "pacman");

        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");
        List<Map<String, Object>> aList3 = repository.getListAssets("aws-all", filter, 0, 2, null);
        assertTrue(aList3.get(0).get("_resourceid") != null);
        assertTrue(aList3.size() == 2);
    }

    @Test
    public void testgetAssetCount() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("application", "pacman");
        filter.put("environment", "prd");

        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("type", "ec2");
        Map<String, Object> ttypeMap2 = new HashMap<>();
        ttypeMap2.put("type", "s3");
        List<Map<String, Object>> ttypeList = new ArrayList<>();
        ttypeList.add(ttypeMap1);
        ttypeList.add(ttypeMap2);
        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(ttypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject())).thenReturn((long) 100);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        long count1 = repository.getAssetCount("aws-all", filter, "");
        assertTrue(count1 == 100);

        filter.put("resourceType", "s3");
        long count2 = repository.getAssetCount("aws-all", filter, "");
        assertTrue(count2 == 100);

    }

    @Test
    public void testgetCpuUtilizationByAssetGroupAndInstanceId() throws Exception {
        String response = "{\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"2018-05-24\",\"Avg-CPU-Utilization\":{\"value\":0.8414895795285702}},{\"key_as_string\":\"2018-05-23\",\"Avg-CPU-Utilization\":{\"value\":0.8000763853391012}}]}}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        List<Map<String, Object>> a = repository.getCpuUtilizationByAssetGroupAndInstanceId("a1");
        assertTrue(a.size() == 2);
        assertTrue(a.get(0).size() == 2);
    }

    @Test
    public void testgetDiskUtilizationByAssetGroupAndInstanceId() throws Exception {
        String response = "{\"hits\":{\"hits\":[{\"_source\":{\"volume\":{\"list\":{\"hostAssetVolume\":[{\"name\":\"dummy\",\"size\":\"dummy\",\"free\":\"dummy\"},{\"name\":\"dummy\",\"size\":\"dummy\",\"free\":\"dummy\"},{\"name\":\"dummy\",\"size\":\"dummy\",\"free\":\"dummy\"}]}}}}]}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        List<Map<String, Object>> a = repository.getDiskUtilizationByAssetGroupAndInstanceId("a1");
        assertTrue(a.size() == 3);
    }

    @Test
    public void testgetSoftwareInstalledDetailsByAssetGroupAndInstanceId() throws Exception {
        String response = "{\"hits\":{\"hits\":[{\"_source\":{\"software\":{\"list\":{\"hostAssetSoftware\":[{\"name\":\"dummy\",\"version\":\"dummy\"},{\"name\":\"dummy\",\"version\":\"dummy\"},{\"name\":\"dummy\",\"version\":\"dummy\"}]}}}}]}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        List<Map<String, Object>> a = repository.getSoftwareInstalledDetailsByAssetGroupAndInstanceId("a1", 0, 2, "");
        assertTrue(a.size() == 3);

    }

    @Test
    public void testgetOpenPortDetailsByInstanceId() throws Exception {
        String response = "{\"hits\":{\"hits\":[{\"_source\":{\"openPort\":{\"list\":{\"hostAssetOpenPort\":[{\"protocol\":\"dummy\",\"port\":\"dummy\",\"serviceId\":\"dummy\",\"serviceName\":\"dummy\"},{\"protocol\":\"dummy\",\"port\":\"dummy\",\"serviceId\":\"dummy\",\"serviceName\":\"dummy\"},{\"protocol\":\"dummy\",\"port\":\"dummy\",\"serviceId\":\"dummy\",\"serviceName\":\"dummy\"}]}}}}]}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        List<Map<String, Object>> a = repository.getOpenPortDetailsByInstanceId("a1", 0, 2, "");
        assertTrue(a.size() == 3);

    }

    @Test
    public void testgetListAssetsPatchable() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("resourceType", "ec2");
        filter.put("application", "pacman");
        filter.put("environment", "dev");
        filter.put("patched", "true");
        filter.put("executiveSponsor", "John Doe");
        filter.put("director", "John Doe");

        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "b2");
        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);
        Map<String, Object> asset3 = new HashMap<>();
        asset3.put("_resourceid", "a1");
        Map<String, Object> asset4 = new HashMap<>();
        asset4.put("_resourceid", "c3");
        List<Map<String, Object>> assetList2 = new ArrayList<>();
        assetList2.add(asset3);
        assetList2.add(asset4);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList, assetList2);
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        String ag = "ag";
        List<Map<String, Object>> result = repository.getListAssetsPatchable(ag, filter);
        assertTrue(result != null);
        filter.put("resourceType", "onpremserver");
        result = repository.getListAssetsPatchable(ag, filter);
        assertTrue(result != null);
        filter.remove("resourceType");
        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("type", "ec2");
        Map<String, Object> ttypeMap2 = new HashMap<>();
        ttypeMap2.put("type", "s3");
        List<Map<String, Object>> ttypeList = new ArrayList<>();
        ttypeList.add(ttypeMap1);
        ttypeList.add(ttypeMap2);
        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(ttypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        result = repository.getListAssetsPatchable(ag, filter);
        assertTrue(result != null);

    }

    @Test
    public void testgetListAssetsTaggable() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("resourceType", "ec2");
        filter.put("application", "value");
        filter.put("environment", "value");
        filter.put("tagName", "value");
        filter.put("tagged", "value");

        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("targetType", "ec2");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "b2");
        asset2.put("targetType", "ec2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);
        Map<String, Object> asset3 = new HashMap<>();
        asset3.put("_resourceid", "a1");
        asset3.put("targetType", "ec2");

        Map<String, Object> asset4 = new HashMap<>();
        asset4.put("_resourceid", "c3");
        asset4.put("targetType", "ec2");

        List<Map<String, Object>> assetList2 = new ArrayList<>();
        assetList2.add(asset3);
        assetList2.add(asset4);
        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("targetType", "ec2");
        Map<String, Object> ttypeMap2 = new HashMap<>();
        ttypeMap2.put("targetType", "onpremserver");
        List<Map<String, Object>> ttypeList = new ArrayList<>();
        ttypeList.add(ttypeMap1);
        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(ttypeList);
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");
        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList, assetList2);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        ReflectionTestUtils.setField(repository, "mandatoryTags", "Application,Environment");

        List<Map<String, Object>> result = repository.getListAssetsTaggable("ag", filter);
        filter.remove("resourceType");

        result = repository.getListAssetsTaggable("ag", filter);
        assertTrue(result != null);
    }

    @Test
    public void testgetListAssetsVulnerable() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("resourceType", "ec2");
        filter.put("application", "value");
        filter.put("environment", "value");

        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "b2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("type", "ec2");
        Map<String, Object> ttypeMap2 = new HashMap<>();
        ttypeMap2.put("type", "onpremserver");
        List<Map<String, Object>> ttypeList = new ArrayList<>();
        ttypeList.add(ttypeMap1);
        // ttypeList.add(ttypeMap2);
        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(ttypeList);
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        ReflectionTestUtils.setField(repository, "vulnTypes", "ec2,onpremserver");
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        String response = "{\"count\":\"2\"}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        List<Map<String, Object>> a = repository.getListAssetsVulnerable("aws-all", filter);
        assertTrue(a.size() == 0);

        filter.put("resourceType", "ec2");
        List<Map<String, Object>> a1 = repository.getListAssetsVulnerable("aws-all", filter);
        assertTrue(a1.size() == 0);

        filter.put("resourceType", "onpremserver");
        List<Map<String, Object>> a2 = repository.getListAssetsVulnerable("aws-all", filter);
        assertTrue(a2.size() == 0);

    }

    @Test
    public void testgetListAssetsScanned() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("resourceType", "ec2");
        filter.put("compliant", "true");
        filter.put("application", "value");
        filter.put("environment", "value");

        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("policyId", "PacMan_TaggingRule_version-1");
        asset1.put("Environment", "Not Found");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "b2");
        asset2.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);
        ReflectionTestUtils.setField(repository, "mandatoryTags", "Application,Environment");

        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("targetType", "ec2");
        ttypeMap1.put("type", "ec2");
        ttypeMap1.put("ruleId", "r1");
        Map<String, Object> ttypeMap2 = new HashMap<>();
        ttypeMap2.put("type", "onpremserver");
        List<Map<String, Object>> ttypeList = new ArrayList<>();
        ttypeList.add(ttypeMap1);
        // ttypeList.add(ttypeMap2);
        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(ttypeList);
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        ReflectionTestUtils.setField(repository, "vulnTypes", "ec2,onpremserver");
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        String response = "{\"count\":\"2\"}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        List<Map<String, Object>> a = repository.getListAssetsScanned("aws-all", filter);
        assertTrue(a != null);
        filter.remove("compliant");
        filter.remove("resourceType");
        a = repository.getListAssetsScanned("aws-all", filter);
        assertTrue(a != null);

    }

    @Test
    public void testgetResourceDetail() throws Exception {
        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "b2");
        asset1.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);
        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> a = repository.getResourceDetail("ag", "ec2", "a1");
        assert (a.size() == 2);
    }

    @Test
    public void testgetAssetLists() throws Exception {
        String response = "{\"hits\":{\"hits\":[{\"_source\":{\"_resourceId\":\"a1\"}},{\"_source\":{\"_resourceId\":\"b2\"}}]}}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        Map<String, String> filter = new HashMap<>();
        filter.put("resourceType", "ec2");
        filter.put("application", "value");
        filter.put("environment", "value");
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("_resourceid");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);

        List<Map<String, Object>> a = repository.getAssetLists("ag", filter, 0, 2, "");
        assert (a.size() == 2);

        filter.put("resourceType", "onpremserver");
        List<Map<String, Object>> b = repository.getAssetLists("ag", filter, 0, 2, "");
        assert (b.size() == 2);

    }

    @Test
    public void testgetResourceCreateInfo() throws Exception {
        String response = "{\"hits\":{\"hits\":[{\"_source\":{\"_resourceId\":\"a1\",\"user\":\"u1\",\"time\":\"t1\",\"email\":\"a@b\"}},{\"_source\":{\"_resourceId\":\"b2\",\"user\":\"u1\",\"time\":\"t1\",\"email\":\"a@b\"}}]}}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        Map<String, Object> a = repository.getResourceCreateInfo("a1");
        assertTrue(a.get("createdBy") != null);
    }

    @Test
    public void testgetNotificationSummary() throws Exception {
        Map<String, Long> mockMap = new HashMap<>();
        mockMap.put("open", (long) 3);
        mockMap.put("closed", (long) 7);
        mockMap.put("upcoming", (long) 10);

        when(elasticSearchRepository.getTotalDistributionForIndexAndType(anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(mockMap);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        Map<String, Long> a = repository.getNotificationSummary("a1");
        assertTrue((a.get("open") + a.get("closed") + a.get("upcoming")) == 20);
    }

    @Test
    public void testgetNotificationDetails() throws Exception {
        Map<String, Object> phd1 = new HashMap<>();
        phd1.put("_resourceid", "a1");
        phd1.put("open", "p1");

        Map<String, Object> phd2 = new HashMap<>();
        phd2.put("_resourceid", "b2");
        phd1.put("closed", "p2");

        List<Map<String, Object>> phdList = new ArrayList<>();
        phdList.add(phd1);
        phdList.add(phd2);
        when(elasticSearchRepository.getDataFromESBySize(anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject(), anyInt(), anyInt(), anyObject(), anyObject())).thenReturn(phdList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        List<Map<String, Object>> a = repository.getNotificationDetails("a1", null, "");
        assertTrue(a.size() == 2);
    }

    @Test
    public void testupdateAsset() throws Exception {

        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        when(response.getEntity()).thenReturn(null);
        mockStatic(EntityUtils.class);
        when(EntityUtils.toString(anyObject())).thenReturn("");
        when(restClient.performRequest(anyObject(), anyObject(), anyObject(), anyObject(), anyObject()))
                .thenReturn(response);
        ReflectionTestUtils.setField(repository, "restClient", restClient);

        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("_docid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "c3");
        asset1.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        Map<String, Object> resources = new HashMap<>();
        List<String> resourceIdStrList = new ArrayList<String>();
        resourceIdStrList.add("a1");
        resourceIdStrList.add("b2");
        resources.put("values", resourceIdStrList);

        Map<String, Object> firstUpdateMap = new HashMap<>();
        firstUpdateMap.put("key", "policyId");
        firstUpdateMap.put("value", "p5");
        List<Map<String, Object>> updateList = new ArrayList<>();
        updateList.add(firstUpdateMap);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        when(redshiftRepository.batchUpdate(anyObject())).thenReturn(new int[] { 1, 1 });
        ReflectionTestUtils.setField(repository, "redshiftRepository", redshiftRepository);

        int a = repository.updateAsset("aws-all", "ec2", resources, "testuser", updateList);
        assertTrue(a > 0);
    }

    @Test
    public void testgetEc2ResourceDetailFromRhn() throws Exception {
        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("_docid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "c3");
        asset1.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        List<Map<String, Object>> a = repository.getEc2ResourceDetailFromRhn("a1");
        assertTrue(a.size() == 2);

        doThrow(new NullPointerException()).when(elasticSearchRepository).getDataFromES(anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject(), anyObject());
        a = repository.getEc2ResourceDetailFromRhn("a1");
        assertTrue(a==null);

    }

    @Test
    public void testgetEc2ResourceDetail() throws Exception {
        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("_docid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "c3");
        asset1.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        List<Map<String, Object>> a = repository.getEc2ResourceDetail("ag", "a1");
        assertTrue(a.size() == 2);

        doThrow(new NullPointerException()).when(elasticSearchRepository).getDataFromES(anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject(), anyObject());
        boolean noDataExceptionCaught = false;
        try {
            a = repository.getEc2ResourceDetail("ag", "a1");
        } catch (DataException e) {
            noDataExceptionCaught = true;
        }
        assertTrue(noDataExceptionCaught);

    }

    @Test
    public void testgetEc2ResourceSecurityGroupDetail() throws Exception {
        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("_docid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "c3");
        asset1.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        List<Map<String, Object>> a = repository.getEc2ResourceSecurityGroupDetail("a1");
        assertTrue(a.size() == 2);

        doThrow(new NullPointerException()).when(elasticSearchRepository).getDataFromES(anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject(), anyObject());
        boolean noDataExceptionCaught = false;
        try {
            a = repository.getEc2ResourceSecurityGroupDetail("a1");
        } catch (DataException e) {
            noDataExceptionCaught = true;
        }
        assertTrue(noDataExceptionCaught);

    }

    @Test
    public void testgetEc2ResourceBlockDevicesDetail() throws Exception {
        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("_docid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "c3");
        asset1.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        List<Map<String, Object>> a = repository.getEc2ResourceBlockDevicesDetail("a1");
        assertTrue(a.size() == 2);

        doThrow(new NullPointerException()).when(elasticSearchRepository).getDataFromES(anyObject(), anyObject(),
                anyObject(), anyObject(), anyObject(), anyObject(), anyObject());
        boolean noDataExceptionCaught = false;
        try {
            a = repository.getEc2ResourceBlockDevicesDetail("a1");
        } catch (DataException e) {
            noDataExceptionCaught = true;
        }
        assertTrue(noDataExceptionCaught);

    }

    @Test
    public void testGetQualysDetail() throws Exception {
        Map<String, Object> asset1 = new HashMap<>();
        asset1.put("_resourceid", "a1");
        asset1.put("_docid", "a1");
        asset1.put("policyId", "p1");

        Map<String, Object> asset2 = new HashMap<>();
        asset2.put("_resourceid", "c3");
        asset2.put("policyId", "p2");

        List<Map<String, Object>> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);

        boolean dataExceptionCaught = false;
        try {
            repository.getQualysDetail("a1");
        } catch (DataException e) {
            dataExceptionCaught = true;
        }
        assertTrue(dataExceptionCaught);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(assetList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        List<Map<String, Object>> a = repository.getQualysDetail("a1");
        assert (a.size() == 2);

    }

    @Test
    public void testgetTotalCountForListingAsset() throws Exception {
        String response = "{\"count\":\"2\"}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");

        repository.getTotalCountForListingAsset("ag", "ec2");
    }

    @Test
    public void testgetResourceCreatedDate() throws Exception {
        Map<String, String> events = new HashMap<>();
        events.put("ec2", "event1");
        repository.setEvents(events);
        repository.getEvents();
        ReflectionTestUtils.setField(repository, "esUrl", "dummyEsURL");
        ReflectionTestUtils.setField(repository, "heimdallEsesUrl", "dummyEsURL");

        String response = "{\"hits\":{\"total\":\"2\",\"hits\":[{\"_source\":{\"_resourceId\":\"a1\"}},{\"_source\":{\"_resourceId\":\"b2\"}}]}}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        String result = repository.getResourceCreatedDate("a1", "ec2");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testgetDomainsByAssetGroup() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("targetType", "ec2");
        ttypeMap1.put("type", "ec2");
        ttypeMap1.put("ruleId", "r1");
        tTypeList.add(ttypeMap1);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);
        List<Map<String, Object>> result = repository.getDomainsByAssetGroup("ag");
        assertTrue(result.size() == 1);
    }

    @Test
    public void testgetAssetGroupAndDomains() throws Exception {
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> ttypeMap1 = new HashMap<>();
        ttypeMap1.put("targetType", "ec2");
        ttypeMap1.put("type", "ec2");
        ttypeMap1.put("ruleId", "r1");
        tTypeList.add(ttypeMap1);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);
        List<Map<String, Object>> result = repository.getAssetGroupAndDomains();
        assertTrue(result.size() == 1);
    }

    @Test
    public void testgetAdGroupDetails() throws Exception {
        Map<String, Object> adMap1 = new HashMap<>();
        adMap1.put("admin", "a1");

        Map<String, Object> adMap2 = new HashMap<>();
        adMap2.put("admin", "b2");

        List<Map<String, Object>> adList = new ArrayList<>();
        adList.add(adMap1);
        adList.add(adMap2);

        boolean isDataExceptionThrown = false;
        try {
            repository.getAdGroupDetails();
        } catch (DataException e) {
            isDataExceptionThrown = true;
        }
        assertTrue(isDataExceptionThrown);

        when(elasticSearchRepository.getDataFromES(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject(), anyObject())).thenReturn(adList);
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);

        List<Map<String, Object>> result = repository.getAdGroupDetails();
        assertTrue(result.size() > 0);
    }

    @Test
    public void testgetDataTypeInfoByTargetType() throws Exception {
        when(pacmanRdsRepository.queryForString(anyString())).thenReturn("resourceid");
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);
        String dataType = repository.getDataTypeInfoByTargetType("ec2");
        assertTrue(dataType.equals("resourceid"));
    }
}
