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
  Author :santoshi
  Modified Date: Jun 28, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.client.AssetServiceClient;
import com.tmobile.pacman.api.compliance.domain.AssetApi;
import com.tmobile.pacman.api.compliance.domain.AssetApiData;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class FilterRepositoryImplTest {
    @InjectMocks
    FilterRepositoryImpl filterRepositoryImpl;
    @Mock
    PacmanRdsRepository rdsepository;

    @Mock
    ElasticSearchRepository elasticSearchRepository;

    @Mock
    AssetServiceClient assetServiceClient;
    @Test
    public void getFiltersFromDbTest() throws Exception{
        List<Map<String, Object>> filters=new ArrayList<>();
        Map<String,Object>filter=new HashMap<>();
        filter.put("filterId", "1");
        filter.put("filtername", "policyViolation");
        filters.add(filter);
        when(
                rdsepository.getDataFromPacman(anyString())).thenReturn(filters);
      List<Map<String,Object>>filtersfromDb=  filterRepositoryImpl.getFiltersFromDb(1);
      assertTrue(filtersfromDb.size()>0);
    }
    @Test
    public void getPoliciesFromDBTest()throws DataException{
        List<Map<String, Object>> policies=new ArrayList<>();
        Map<String,Object>policy=new HashMap<>();
        policy.put("policyId", "Tagging_policy");
        policy.put("policyDescription", "Tagging policy compliance");
        policies.add(policy);
        when(
                rdsepository.getDataFromPacman(anyString())).thenReturn(policies);
      List<Map<String,Object>>policiesfromDb=  filterRepositoryImpl.getPoliciesFromDB("ec2");
      assertTrue(policiesfromDb.size()>0);
    }
    @Test
    public void getPoliciesFromESTest()throws Exception{
        Map<String,Long>policy=new HashMap<>();
        policy.put("tagging_policy",19l);
        policy.put("patching_polcy",2l);
       //test method returning policy & count
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(policy);
       filterRepositoryImpl.getPoliciesFromES("aws-all");
       //test method throwing DataException
       when(
               elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenThrow(new RuntimeException());
       assertThatThrownBy(() ->  filterRepositoryImpl.getPoliciesFromES("aws-all")).isInstanceOf(DataException.class);
    }
    @Test
    public void  getAccountsFromESTest()throws Exception{
        List<Map<String, Object>>accountList= new ArrayList<>();
       Map<String,Object>accountDetails = new HashMap<>();
       accountDetails.put("accountName", "name");
       accountDetails.put("accountid", "1234");
       accountList.add(accountDetails);
       //test method returning account list
       when(
               elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(accountList);
       filterRepositoryImpl.getAccountsFromES("aws-all");
       //test method returning dataException
       when(
               elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
       assertThatThrownBy(() ->   filterRepositoryImpl.getAccountsFromES("aws-all")).isInstanceOf(DataException.class);
    }
    @Test
    public void getRegionsFromESTest()throws Exception{
        Map<String, Long>regions =new HashMap<>();
        regions.put("region", 1l);
       //test method returning regions
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(regions);
       filterRepositoryImpl.getRegionsFromES("aws-all");
       //test method returning DataExceptons
       when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() ->  filterRepositoryImpl.getRegionsFromES("aws-all")).isInstanceOf(DataException.class);
    }
    @Test
    public void  getRulesFromESTest()throws Exception{
        Map<String, Long>rules =new HashMap<>();
        rules.put("tagging_rule_1", 1l);
        rules.put("patching_rule_1", 2l);
      //test method returning rules
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(rules);
       filterRepositoryImpl.getRulesFromES("aws-all");
       //test method returning DataExceptons
       when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() ->  filterRepositoryImpl.getRulesFromES("aws-all")).isInstanceOf(DataException.class);

    }
    @Test
    public void getListOfApplicationsTest()throws DataException{
        AssetApi assetApi = new AssetApi();
        AssetApiData apidata = new AssetApiData();
        AssetCountDTO application = new AssetCountDTO();
        AssetCountDTO[] applications = new AssetCountDTO[]{application};
        application.setName("app1");
        application.setName("app2");
        apidata.setApplications(applications);
        assetApi.setData(apidata);
        //test method returnin applications
        when(
                assetServiceClient.getApplicationsList(anyString(),anyString())).thenReturn(assetApi);
       filterRepositoryImpl.getListOfApplications("aws-all", "1");
    }
    @Test
    public void getListOfEnvironmentsTest()throws Exception{
        AssetApi assetApi = new AssetApi();
        AssetApiData apidata = new AssetApiData();
        AssetCountDTO environments = new AssetCountDTO();
        AssetCountDTO[] applications = new AssetCountDTO[]{environments};
        environments.setName("dev");
        environments.setName("prod");
        apidata.setApplications(applications);
        assetApi.setData(apidata);
        //test method returnin applications
        when(
                assetServiceClient.getEnvironmentList(anyString(),anyString(),anyString())).thenReturn(assetApi);
       filterRepositoryImpl.getListOfEnvironments("aws-all", null, "1");
    }
    @Test
    public void  getListOfTargetTypesTest()throws Exception{
        AssetApi assetApi = new AssetApi();
        AssetApiData apidata = new AssetApiData();
        AssetCountDTO targetTypes = new AssetCountDTO();
        AssetCountDTO[] applications = new AssetCountDTO[]{targetTypes};
        targetTypes.setName("ec2");
        targetTypes.setName("s3");
        apidata.setApplications(applications);
        assetApi.setData(apidata);
        //test method returnin applications
        when(
                assetServiceClient.getTargetTypeList(anyString(), anyString())).thenReturn(assetApi);
       filterRepositoryImpl.getListOfTargetTypes("aws-all", "1");
    }


}
