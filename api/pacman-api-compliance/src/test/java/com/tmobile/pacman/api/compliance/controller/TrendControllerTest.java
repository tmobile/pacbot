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
import com.tmobile.pacman.api.compliance.service.ComplianceServiceImpl;
import com.tmobile.pacman.api.compliance.service.IssueTrendService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TrendControllerTest {

    @InjectMocks
    TrendController trendController;
    
    @Mock
    IssueTrendService issueTrendService;
    
    @Mock
    ComplianceServiceImpl complianceService;
    
    @Test
    public void getTrendForIssuesTest() throws Exception {
        when(issueTrendService.getTrendForIssues(anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtil.getMapLong());
        assertThat(trendController.getTrendForIssues("ag","fromDate","toDate","severity","ruleId","policyId","app","env"), is(notNullValue()));
        assertThat(trendController.getTrendForIssues("", "", "", "", "", "", "", ""), is(notNullValue()));
        
        when(issueTrendService.getTrendForIssues(anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString())).thenThrow(new ServiceException());
       // when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = trendController.getTrendForIssues("ag","fromDate","toDate","severity","ruleId","policyId","app","env");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getTrendFromCacheTest() throws Exception {
        assertThat(trendController.getTrendFromCache(), is(notNullValue()));
    }
    
    @Test
    public void getTrendFromCache_WithParamTest() throws Exception {
        ResponseEntity<Object> responseObj = trendController.getTrendFromCache("ag","fromDate","toDate","severity","ruleId");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCompliantTrendTest() throws Exception {
        when(issueTrendService.getComplianceTrendProgress(anyString(),anyObject(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(trendController.getCompliantTrend(CommonTestUtil.getCompliantTrendRequest()), is(notNullValue()));
        assertThat(trendController.getCompliantTrend(CommonTestUtil.getCompliantTrendEmptyRequest()), is(notNullValue()));
        
        when(issueTrendService.getComplianceTrendProgress(anyString(),anyObject(),anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = trendController.getCompliantTrend(CommonTestUtil.getCompliantTrendRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getRuleTrendTest() throws Exception {
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(trendController.getRuleTrend(CommonTestUtil.getRuleTrendRequest()), is(notNullValue()));
        assertThat(trendController.getRuleTrend(CommonTestUtil.getRuleTrendRequestEmpty()), is(notNullValue()));
        
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = trendController.getRuleTrend(CommonTestUtil.getRuleTrendRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getVulnTrendTest() throws Exception {
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(trendController.getVulnTrend(CommonTestUtil.getRuleTrendRequest()), is(notNullValue()));
        assertThat(trendController.getVulnTrend(CommonTestUtil.getRuleTrendRequestEmpty()), is(notNullValue()));
        
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = trendController.getVulnTrend(CommonTestUtil.getRuleTrendRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCertTrendTest() throws Exception {
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(trendController.getCertTrend(CommonTestUtil.getRuleTrendRequest()), is(notNullValue()));
        assertThat(trendController.getCertTrend(CommonTestUtil.getRuleTrendRequestEmpty()), is(notNullValue()));
        
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = trendController.getCertTrend(CommonTestUtil.getRuleTrendRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    
    @Test
    public void getTagTrendTest() throws Exception {
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(trendController.getTagTrend(CommonTestUtil.getRuleTrendRequest()), is(notNullValue()));
        assertThat(trendController.getTagTrend(CommonTestUtil.getRuleTrendRequestEmpty()), is(notNullValue()));
        
        when(issueTrendService.getTrendProgress(anyString(),anyString(),anyObject(),anyObject(),anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = trendController.getTagTrend(CommonTestUtil.getRuleTrendRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getTrendIssuesTest() throws Exception {
        when(issueTrendService.getTrendIssues(anyString(),anyObject(),anyObject(),anyObject(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(trendController.getTrendIssues(CommonTestUtil.getRuleTrendRequest()), is(notNullValue()));
        assertThat(trendController.getTrendIssues(CommonTestUtil.getRuleTrendRequestEmpty()), is(notNullValue()));
        
        when(issueTrendService.getTrendIssues(anyString(),anyObject(),anyObject(),anyObject(),anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = trendController.getTrendIssues(CommonTestUtil.getRuleTrendRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
}
