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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class SearchRepositoryTest {

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
    AssetService assetService;

    SearchRepositoryImpl repository = new SearchRepositoryImpl();

    @Test
    public void testgetAssetCountByAssetGroup() throws Exception {

        List<Map<String, Object>> returnFieldsData = new ArrayList<>();
        Map<String, Object> firstRow = new HashMap<>();
        firstRow.put("SEARCH_CATEGORY", "Assets");
        firstRow.put("RESOURCE_TYPE", "All");
        firstRow.put("REFINE_BY_FIELDS", "accountname,region,tags.Application,tags.Environment,tags.Stack,tags.Role");
        firstRow.put("RETURN_FIELDS", "_resourceid,searchcategory,tags,accountname,_entitytype");

        Map<String, Object> secondRow = new HashMap<>();
        secondRow.put("SEARCH_CATEGORY", "Policy Violations");
        secondRow.put("RESOURCE_TYPE", "All");
        secondRow.put("REFINE_BY_FIELDS", "severity,policyId");
        secondRow.put("RETURN_FIELDS", "_id,issueid,resourceid,severity,_entitytype,_resourceid");

        Map<String, Object> thirdRow = new HashMap<>();
        thirdRow.put("SEARCH_CATEGORY", "Vulnerabilities");
        thirdRow.put("RESOURCE_TYPE", "All");
        thirdRow.put("REFINE_BY_FIELDS", "severity,category,vulntype");
        thirdRow.put("RETURN_FIELDS", "qid,vulntype,category,_entitytype,_resourceid");

        returnFieldsData.add(firstRow);
        returnFieldsData.add(secondRow);
        returnFieldsData.add(thirdRow);

        Map<String, Object> tTypeMap1 = new HashMap<>();
        tTypeMap1.put("type", "ec2");
        Map<String, Object> tTypeMap2 = new HashMap<>();
        tTypeMap1.put("type", "s3");
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        tTypeList.add(tTypeMap1);
        tTypeList.add(tTypeMap2);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("a", "1");

        String responseJson = "{\"aggregations\":{\"qids\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[]}}}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(responseJson);

        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(returnFieldsData);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);
        when(assetService.getTargetTypesForAssetGroup(anyString(), anyString(), anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "assetService", assetService);
        when(elasticSearchRepository.buildQuery(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                anyObject())).thenReturn(queryMap);
        when(elasticSearchRepository.processResponseAndSendTheScrollBack(anyString(), anyObject()))
                .thenCallRealMethod();
        ReflectionTestUtils.setField(repository, "esRepository", elasticSearchRepository);
        mockStatic(EntityUtils.class);
        String json = "{\"hits\":{\"total\":10,\"hits\":[{\"_source\":{\"_resourceid\":\"a1\"},\"sort\":[\"s1\"]}]}}";

        when(EntityUtils.toString(anyObject())).thenReturn(json);
        when(response.getEntity()).thenReturn(null);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
                Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "restClient", restClient);

        SearchResult sr = new SearchResult();
        sr = repository.fetchSearchResultsAndSetTotal("aws-all", null, true, null, "pacman", new HashMap<>(), 0, 1, sr,
                "Vulnerabilities");
        assertTrue(sr.getTotal() == 0);

        sr = repository.fetchSearchResultsAndSetTotal("aws-all", null, true, null, "pacman", new HashMap<>(), 0, 1, sr,
                "Assets");
        assertTrue(sr.getTotal() == 10);

    }

    @Test
    public void testfetchTargetTypes() throws Exception {

        List<Map<String, Object>> returnFieldsData = new ArrayList<>();
        Map<String, Object> firstRow = new HashMap<>();
        firstRow.put("SEARCH_CATEGORY", "Assets");
        firstRow.put("RESOURCE_TYPE", "All");
        firstRow.put("REFINE_BY_FIELDS", "accountname,region,tags.Application,tags.Environment,tags.Stack,tags.Role");
        firstRow.put("RETURN_FIELDS", "_resourceid,searchcategory,tags,accountname,_entitytype");

        Map<String, Object> secondRow = new HashMap<>();
        secondRow.put("SEARCH_CATEGORY", "Policy Violations");
        secondRow.put("RESOURCE_TYPE", "All");
        secondRow.put("REFINE_BY_FIELDS", "severity,policyId");
        secondRow.put("RETURN_FIELDS", "_id,issueid,resourceid,severity,_entitytype,_resourceid");

        Map<String, Object> thirdRow = new HashMap<>();
        thirdRow.put("SEARCH_CATEGORY", "Vulnerabilities");
        thirdRow.put("RESOURCE_TYPE", "All");
        thirdRow.put("REFINE_BY_FIELDS", "severity,category,vulntype");
        thirdRow.put("RETURN_FIELDS", "qid,vulntype,category,_entitytype,_resourceid");

        returnFieldsData.add(firstRow);
        returnFieldsData.add(secondRow);
        returnFieldsData.add(thirdRow);

        Map<String, Object> tTypeMap1 = new HashMap<>();
        tTypeMap1.put("type", "ec2");
        Map<String, Object> tTypeMap2 = new HashMap<>();
        tTypeMap2.put("type", "s3");
        List<Map<String, Object>> tTypeList = new ArrayList<>();
        tTypeList.add(tTypeMap1);
        tTypeList.add(tTypeMap2);

        String responseJson = "{\"aggregations\":{\"targetTypes\":{\"buckets\":[{\"key\":\"ec2\",\"doc_count\":2774},{\"key\":\"volume\",\"doc_count\":1880}]}}}";

        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(responseJson);
        when(pacmanRdsRepository.getDataFromPacman(anyString())).thenReturn(returnFieldsData);
        ReflectionTestUtils.setField(repository, "rdsRepository", pacmanRdsRepository);
        
        String json = "{\"aggregations\":{\"severity\":{\"buckets\":[{\"key\":\"low\",\"doc_count\":2158},{\"key\":\"high\",\"doc_count\":1998}]}}}";
        mockStatic(EntityUtils.class);

        when(EntityUtils.toString(anyObject())).thenReturn(json);
        when(response.getEntity()).thenReturn(null);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
                Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(repository, "restClient", restClient);
        
        when(assetService.getTargetTypesForAssetGroup(anyString(), anyString(), anyString())).thenReturn(tTypeList);
        ReflectionTestUtils.setField(repository, "assetService", assetService);
        
        List<Map<String, Object>> x = repository.fetchTargetTypes("aws-all", "pacman", "Assets", "", true);

        Map<String, List<Map<String, Object>>> a = repository.fetchDistributionForTargetType("aws-all", "ec2", "pacman",
                "Assets", true);
        assertTrue(x.size() > 0);

    }

}
