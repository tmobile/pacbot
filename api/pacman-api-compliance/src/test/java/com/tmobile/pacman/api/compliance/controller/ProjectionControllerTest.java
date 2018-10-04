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
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.service.ComplianceServiceImpl;
import com.tmobile.pacman.api.compliance.service.ProjectionService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectionControllerTest {

    @InjectMocks
    ProjectionController projectionController;
    
    @Mock
    ProjectionService projectionService;
    
    @Mock
    ComplianceServiceImpl complianceService;
    
    @Test
    public void getProjectionDataTest() throws Exception {
        when(projectionService.getProjection(anyString(),anyInt(),anyInt())).thenReturn(CommonTestUtil.getProjectionResponse());
        assertThat(projectionController.getProjectionData("onpremserver",2018,2), is(notNullValue()));
        assertThat(projectionController.getProjectionData("",0,5), is(notNullValue()));
        
        when(projectionService.getProjection(anyString(),anyInt(),anyInt())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = projectionController.getProjectionData("onpremserver",2018,2);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getProjectionDataWithMoreQuarterNumberTest() throws Exception {
        when(projectionService.getProjection(anyString(),anyInt(),anyInt())).thenReturn(CommonTestUtil.getProjectionResponse());
        assertThat(projectionController.getProjectionData("onpremserver",2018,5), is(notNullValue()));
    }
    
    @Test
    public void updateProjectionDataTest() throws Exception {
        ProjectionRequest projectionRequest1 = new ProjectionRequest();
        ProjectionRequest projectionRequest = new ProjectionRequest();
        projectionRequest.setQuarter(3);
        projectionRequest.setYear(2018);
        projectionRequest.setResourceType("onpremserver");
        when(projectionService.updateProjection(anyObject())).thenReturn(true);
        assertThat(projectionController.updateProjectionData(projectionRequest), is(notNullValue()));
        assertThat(projectionController.updateProjectionData(projectionRequest1), is(notNullValue()));
        
        when(projectionService.updateProjection(anyObject())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = projectionController.updateProjectionData(projectionRequest);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void updateProjectionDataMoreQuarterNumberTest() throws Exception {
        ProjectionRequest projectionRequest = new ProjectionRequest();
        projectionRequest.setQuarter(5);
        projectionRequest.setYear(2018);
        projectionRequest.setResourceType("onpremserver");
        when(projectionService.updateProjection(anyObject())).thenReturn(false);
        assertThat(projectionController.updateProjectionData(projectionRequest), is(notNullValue()));
    }
    
    @Test
    public void updateProjectionDataFalseTest() throws Exception {
        ProjectionRequest projectionRequest = new ProjectionRequest();
        projectionRequest.setYear(2018);
        projectionRequest.setResourceType("onpremserver");
        when(projectionService.updateProjection(anyObject())).thenReturn(false);
        assertThat(projectionController.updateProjectionData(projectionRequest), is(notNullValue()));
    }
    
    @Test
    public void getPatchingAndProjectionProgressTest() throws Exception {
        when(projectionService.getPatchingAndProjectionByWeek(anyString())).thenReturn(CommonTestUtil.getProjectionResponse());
        assertThat(projectionController.getPatchingAndProjectionProgress("onpremserver"), is(notNullValue()));
        assertThat(projectionController.getPatchingAndProjectionProgress(""), is(notNullValue()));
        
        when(projectionService.getPatchingAndProjectionByWeek(anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = projectionController.getPatchingAndProjectionProgress("onpremserver");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void patchingProgressByDirectorTest() throws Exception {
        Request request = new Request();
        Request request1 = new Request();
        request.setAg("aws-all");
        when(projectionService.getPatchingProgressByDirector(anyString())).thenReturn(CommonTestUtil.getPatchingProgressResponse());
        assertThat(projectionController.patchingProgressByDirector(request), is(notNullValue()));
        assertThat(projectionController.patchingProgressByDirector(request1), is(notNullValue()));
        
        when(projectionService.getPatchingProgressByDirector(anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = projectionController.patchingProgressByDirector(request);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void patchingProgressByExecutiveSponserTest() throws Exception {
        Request request = new Request();
        Request request1 = new Request();
        request.setAg("aws-all");
        when(projectionService.patchProgByExSponsor(anyString())).thenReturn(CommonTestUtil.getPatchingProgressResponse());
        assertThat(projectionController.patchingProgressByExecutiveSponser(request), is(notNullValue()));
        assertThat(projectionController.patchingProgressByExecutiveSponser(request1), is(notNullValue()));
        
        when(projectionService.patchProgByExSponsor(anyString())).thenThrow(new ServiceException());
        ResponseEntity<Object> responseObj = projectionController.patchingProgressByExecutiveSponser(request);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
}
