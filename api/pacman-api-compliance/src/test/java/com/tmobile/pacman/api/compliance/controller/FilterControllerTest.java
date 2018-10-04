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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.service.ComplianceServiceImpl;
import com.tmobile.pacman.api.compliance.service.FilterService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class FilterControllerTest {

    @InjectMocks
    FilterController filterController;
    
    @Mock
    FilterService filterService;
    
    @Mock
    ComplianceServiceImpl complianceService;
    
    @Test
    public void getFiltersTest() throws Exception {
        when(filterService.getFilters(anyInt(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getFilters(1,"domain"), is(notNullValue()));
        assertThat(filterController.getFilters(0,""), is(notNullValue()));
        
        when(filterService.getFilters(anyInt(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getFilters(1,"domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
 
    
    @Test
    public void getPoliciesTest() throws Exception {
        when(filterService.getPolicies(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getPolicies("ag","domain"), is(notNullValue()));
        assertThat(filterController.getPolicies("",""), is(notNullValue()));
        
        when(filterService.getPolicies(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getPolicies("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getRegionsTest() throws Exception {
        when(filterService.getRegions(anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getRegions("ag"), is(notNullValue()));
        assertThat(filterController.getRegions(""), is(notNullValue()));
        
        when(filterService.getRegions(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getRegions("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getAccountsTest() throws Exception {
        when(filterService.getAccounts(anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getAccounts("ag"), is(notNullValue()));
        assertThat(filterController.getAccounts(""), is(notNullValue()));
        
        when(filterService.getAccounts(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getAccounts("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getRulesTest() throws Exception {
        when(filterService.getRules(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getRules("ag","domain"), is(notNullValue()));
        assertThat(filterController.getRules("",""), is(notNullValue()));
        
        when(filterService.getRules(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getRules("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getListOfApplicationsTest() throws Exception {
        when(filterService.getApplications(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getListOfApplications("ag","domain"), is(notNullValue()));
        assertThat(filterController.getListOfApplications("",""), is(notNullValue()));
        
        when(filterService.getApplications(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getListOfApplications("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getListOfEnvironmentsTest() throws Exception {
        when(filterService.getEnvironmentsByAssetGroup(anyString(),anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getListOfEnvironments("ag","application","domain"), is(notNullValue()));
        assertThat(filterController.getListOfEnvironments("","",""), is(notNullValue()));
        
        when(filterService.getEnvironmentsByAssetGroup(anyString(),anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getListOfEnvironments("ag","application","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getListOfTargetTypesTest() throws Exception {
        when(filterService.getTargetTypesForAssetGroup(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(filterController.getListOfTargetTypes("ag","domain"), is(notNullValue()));
        assertThat(filterController.getListOfTargetTypes("",""), is(notNullValue()));
        
        when(filterService.getTargetTypesForAssetGroup(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = filterController.getListOfTargetTypes("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
}
