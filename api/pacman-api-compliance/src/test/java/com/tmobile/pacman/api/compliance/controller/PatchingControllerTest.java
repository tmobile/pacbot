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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.tmobile.pacman.api.compliance.service.PatchingService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class PatchingControllerTest {

	@Mock
	private PatchingService mockPatchingService;

	@Mock
	private PatchingService patchingService;

	@InjectMocks
	private PatchingController patchingController;
	
	@Mock
    ComplianceServiceImpl complianceService;

	@Test
	public void getTopNonCompliantAppsTest() throws Exception {

	    when(patchingService.getNonCompliantNumberForAG(anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(patchingController.getTopNonCompliantApps("ag"), is(notNullValue()));
        assertThat(patchingController.getTopNonCompliantApps(""), is(notNullValue()));
        
        when(patchingService.getNonCompliantNumberForAG(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = patchingController.getTopNonCompliantApps("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
	}

	@Test
	public void getTopNonCompliantExecsTest() throws Exception {
	    when(patchingService.getNonCompliantExecsForAG(anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(patchingController.getTopNonCompliantExecs("ag"), is(notNullValue()));
        assertThat(patchingController.getTopNonCompliantExecs(""), is(notNullValue()));
        
        when(patchingService.getNonCompliantExecsForAG(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = patchingController.getTopNonCompliantExecs("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

	}
	
	@Test
    public void getPatchingDetailsTest() throws Exception {
	    when(patchingService.getPatchingDetails(anyString(),anyObject())).thenReturn(CommonTestUtil.getListMapObject());
	    when(patchingService.filterMatchingCollectionElements(anyObject(),anyString(),anyBoolean())).thenReturn(CommonTestUtil.getListMapLong());
        assertThat(patchingController.getPatchingDetails(CommonTestUtil.getRequest()), is(notNullValue()));
        assertThat(patchingController.getPatchingDetails(CommonTestUtil.getRequestFromLessThanZero()), is(notNullValue()));
        assertThat(patchingController.getPatchingDetails(CommonTestUtil.getRequestFromgreaterThanMasterList()), is(notNullValue()));
        assertThat(patchingController.getPatchingDetails(CommonTestUtil.getRequestEmpty()), is(notNullValue()));
        
        when(patchingService.getPatchingDetails(anyString(),anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = patchingController.getPatchingDetails(CommonTestUtil.getRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
	
	@Test
    public void getPatchingDetailsEmptyListTest() throws Exception {
        when(patchingService.getPatchingDetails(anyString(),anyObject())).thenReturn(CommonTestUtil.getListMapObject());
        when(patchingService.filterMatchingCollectionElements(anyObject(),anyString(),anyBoolean())).thenReturn(new ArrayList<HashMap<String,Object>>());
        assertThat(patchingController.getPatchingDetails(CommonTestUtil.getRequest()), is(notNullValue()));
        assertThat(patchingController.getPatchingDetails(CommonTestUtil.getRequestEmpty()), is(notNullValue()));
        
        when(patchingService.getPatchingDetails(anyString(),anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = patchingController.getPatchingDetails(CommonTestUtil.getRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
	
	@Test
    public void getPatchingProgressTest() throws Exception {
        when(patchingService.getPatchingProgress(anyString(),anyInt(),anyInt())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(patchingController.getPatchingProgress(CommonTestUtil.getPatchingRequest()), is(notNullValue()));
        assertThat(patchingController.getPatchingProgress(CommonTestUtil.getPatchingRequestEmpty()), is(notNullValue()));
        assertThat(patchingController.getPatchingProgress(CommonTestUtil.getPatchingRequestYearAndQuarterEmpty()), is(notNullValue()));
        assertThat(patchingController.getPatchingProgress(CommonTestUtil.getPatchingRequestMoreYear()), is(notNullValue()));
        
        when(patchingService.getPatchingProgress(anyString(),anyInt(),anyInt())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = patchingController.getPatchingProgress(CommonTestUtil.getPatchingRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
	
	@Test
    public void getQuartersWithPatchingDataTest() throws Exception {
        when(patchingService.getQuartersWithPatchingData(anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(patchingController.getQuartersWithPatchingData("ag"), is(notNullValue()));
        assertThat(patchingController.getQuartersWithPatchingData(""), is(notNullValue()));
        
        when(patchingService.getQuartersWithPatchingData(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = patchingController.getQuartersWithPatchingData(anyString());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    

}
