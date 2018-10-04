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
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;
import com.tmobile.pacman.api.compliance.service.ComplianceServiceImpl;
import com.tmobile.pacman.api.compliance.service.TaggingService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TaggingControllerTest {

    @InjectMocks
    TaggingController taggingController;
    
    @Mock
    TaggingService taggingService;
    
    @Mock
    ComplianceServiceImpl complianceService;
    
    @Test
    public void getUntaggedAssetsByAppTest() throws Exception {
        Request request = new Request();
        request.setAg("ag");
        Request request1 = new Request();
        when(taggingService.getUnTaggedAssetsByApplication(anyObject())).thenReturn(CommonTestUtil.getResponseWithCount());
        assertThat(taggingController.getUntaggedAssetsByApp(request), is(notNullValue()));
        assertThat(taggingController.getUntaggedAssetsByApp(request1), is(notNullValue()));
        
        when(taggingService.getUnTaggedAssetsByApplication(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = taggingController.getUntaggedAssetsByApp(request);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
 
    @Test
    public void taggingSummaryTest() throws Exception {
        when(taggingService.getTaggingSummary(anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(taggingController.taggingSummary("ag"), is(notNullValue()));
        assertThat(taggingController.taggingSummary(""), is(notNullValue()));
        
        when(taggingService.getTaggingSummary(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = taggingController.taggingSummary("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void untaggingByTargetTypesTest() throws Exception {
        UntaggedTargetTypeRequest request = new UntaggedTargetTypeRequest();
        request.setAg("aws-all");
        when(taggingService.getUntaggingByTargetTypes(anyObject())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(taggingController.untaggingByTargetTypes(request), is(notNullValue()));
        assertThat(taggingController.untaggingByTargetTypes(new UntaggedTargetTypeRequest()), is(notNullValue()));
        
        when(taggingService.getUntaggingByTargetTypes(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = taggingController.untaggingByTargetTypes(request);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void taggingByApplicationTest() throws Exception {
        when(taggingService.taggingByApplication(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapLong());
        assertThat(taggingController.taggingByApplication("ag","targetType"), is(notNullValue()));
        assertThat(taggingController.taggingByApplication("",""), is(notNullValue()));
        
        when(taggingService.taggingByApplication(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = taggingController.taggingByApplication("ag","targetType");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
}
