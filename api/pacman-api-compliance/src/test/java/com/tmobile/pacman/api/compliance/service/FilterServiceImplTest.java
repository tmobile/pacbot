
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
package com.tmobile.pacman.api.compliance.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.FilterRepository;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class FilterServiceImplTest {

    @InjectMocks
    private FilterServiceImpl filterServiceImpl;

    @Mock
    private FilterRepository repository;

    @Mock
    private ComplianceRepository complainceRepository;

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getFiltersTest() throws Exception {
        Map<String, Object> filterMap = new HashMap<>();
        List<Map<String, Object>> filters = new ArrayList<>();
        filterMap.put("optionName", "Policy");
        filterMap.put("optionValue", "policyId.keyword");
        filterMap.put("optionURL", "optionURL");
        filters.add(filterMap);
        filterMap = new HashMap<>();
        filterMap.put("optionName", "Region");
        filterMap.put("optionValue", "region.keyword");
        filterMap.put("optionURL", "optionURL");
        filters.add(filterMap);
        filterMap = new HashMap<>();
        filterMap.put("optionName", "accountname");
        filterMap.put("optionValue", "accountid.keyword");
        filterMap.put("optionURL", "optionURL");
        filters.add(filterMap);
        when(repository.getFiltersFromDb(anyInt()))
                .thenReturn(filters);
        
        assertThat(filterServiceImpl.getFilters(1, "test"),
                is(notNullValue()));
        
        when(repository.getFiltersFromDb(anyInt())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getFilters(1,"qwe")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getPoliciesTest() throws Exception {
        String targetTypes = "'ec2','s3','volume'";
        Map<String, Object> policyMap = new HashMap<>();
        List<Map<String, Object>> policyList = new ArrayList<>();
        policyMap.put("policyId", "Policy");
        policyMap.put("PolicyName", "policyId.keyword");
        policyList.add(policyMap);
        when(complainceRepository.getTargetTypeForAG(anyString(),anyString()))
                .thenReturn(targetTypes);
        when(repository.getPoliciesFromDB(anyString()))
        .thenReturn(policyList);
        assertThat(filterServiceImpl.getPolicies("dummyString", "test"),
                is(notNullValue()));
        
        when(repository.getPoliciesFromDB(anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getPolicies("www","qwe")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getRegionsTest() throws Exception {
        Map<String, Long> regionMap = new HashMap<>();
        regionMap.put("us-east-1", 1l);
        regionMap.put("us-west-2", 2l);
        when(repository.getRegionsFromES(anyString()))
                .thenReturn(regionMap);
        assertThat(filterServiceImpl.getRegions("dummyString"),
                is(notNullValue()));
        when(filterServiceImpl.getRegions(anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getRegions("qwe")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getAccountsTest() throws Exception {
        Map<String, Object> accountMap = new HashMap<>();
        List<Map<String, Object>> accountList = new ArrayList<>();
        accountMap.put("accountid", "12345678");
        accountMap.put("accountname", "JON DOE");
        accountMap.put("_id", "12345678");
        accountList.add(accountMap);
        when(repository.getAccountsFromES(anyString()))
                .thenReturn(accountList);
        assertThat(filterServiceImpl.getAccounts("dummyString"),
                is(notNullValue()));
        
        when(repository.getAccountsFromES(anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getAccounts("dummyString")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getRulesTest() throws Exception {
        String targetTypes = "'ec2','s3','volume'";
        Map<String, Object> ruleMap = new HashMap<>();
        List<Map<String, Object>> ruleList = new ArrayList<>();
        ruleMap.put("ruleId", "ruleId");
        ruleMap.put("displayName", "Amazon RDS DB instances should not be idle");
        ruleList.add(ruleMap);
        when(complainceRepository.getTargetTypeForAG(anyString(),anyString()))
        .thenReturn(targetTypes);
        when(complainceRepository.getRuleIdWithDisplayNameQuery(anyString()))
                .thenReturn(ruleList);
        assertThat(filterServiceImpl.getRules("dummyString","testString"),
                is(notNullValue()));
        
        when(complainceRepository.getRuleIdWithDisplayNameQuery(anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getRules("dummyString","qwe")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getApplicationsTest() throws Exception {
        
        when(repository.getListOfApplications(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getAssetCountByApps());
        assertThat(filterServiceImpl.getApplications("dummyString","testString"),
                is(notNullValue()));
        
        when(repository.getListOfApplications(anyString(),anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getApplications("dummyString","qwe")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getEnvironmentsByAssetGroupTest() throws Exception {
        when(repository.getListOfEnvironments(anyString(),anyString(),anyString()))
        .thenReturn(CommonTestUtil.getAssetCountByApps());
        assertThat(filterServiceImpl.getEnvironmentsByAssetGroup("dummyString","testString","testString"),
                is(notNullValue()));
        
        when(repository.getListOfEnvironments(anyString(),anyString(),anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getEnvironmentsByAssetGroup("dummyString","qwe","qwe")).isInstanceOf(ServiceException.class);
        
    }

    @Test
    public void getTargetTypesForAssetGroupTest() throws Exception {
        when(repository.getListOfTargetTypes(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getAssetCountByApps());
        assertThat(filterServiceImpl.getTargetTypesForAssetGroup("dummyString","testString"),
                is(notNullValue()));
        
        when(repository.getListOfTargetTypes(anyString(),anyString())).thenThrow(new DataException());
        assertThatThrownBy( 
                () -> filterServiceImpl.getTargetTypesForAssetGroup("dummyString","qwe")).isInstanceOf(ServiceException.class);
    }
    
}
