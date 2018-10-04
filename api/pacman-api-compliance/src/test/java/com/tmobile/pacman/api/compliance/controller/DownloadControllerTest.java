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
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.compliance.repository.DownloadRepository;
import com.tmobile.pacman.api.compliance.service.ComplianceServiceImpl;
import com.tmobile.pacman.api.compliance.service.DownloadFileService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;
import com.tmobile.pacman.api.compliance.util.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, HttpServletResponse.class })
public class DownloadControllerTest {

    @InjectMocks
    DownloadController downloadController;

    @Mock
    DownloadFileService downloadFileService;

    @Mock
    DownloadRepository downloadRepository;

    @Mock
    ComplianceServiceImpl complianceService;

    @Mock
    HttpServletResponse servletResponse;
    
    @Mock
    HttpServletRequest servletRequest;

    @Test
    public void getIssuesDownloadTest() throws Exception {
        
        when(downloadRepository.getFiltersFromDb(anyInt())).thenReturn(
                CommonTestUtil.getListMapObject());
        ReflectionTestUtils.setField(downloadController, "serviceDnsName", "serviceDnsName");
		String response = "{\"data\":{\"response\":[{\"PolicyName\":"
				+ "\"EC2 instance should not have guard duty findings\",\"IssueId\":\"IssueId\","
				+ "\"ResourceId\":\"ResourceId\",\"Severity\":\"high\",\"RuleCategory\":\"security\","
				+ "\"AccountName\":\"AccountName\",\"AccountId\":\"AccountId\",\"Region\":\"Region\","
				+ "\"Application\":\"Application\",\"Environment\":\"Environment\",\"CreatedOn\":"
				+ "\"2018-07-06T18:23:15.486Z\",\"ModifiedOn\":\"2018-07-10T06:23:33.911Z\","
				+ "\"Status\":\"open\",\"resourcetType\":\"ec2\",\"description\":\"Guard Duty findings exists!!\","
				+ "\"nonDisplayableAttributes\":{\"RuleId\":\"RuleId\","
				+ "\"PolicyId\":\"PolicyId\"}}],\"total\":106762},\"message\":\"success\"}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.getBase64AuthorizationHeader(anyObject())).thenReturn("124");
        when(PacHttpUtils.doHttpsPost(anyString(), anyString(),anyObject())).thenReturn(
                response);
        servletResponse = mock(HttpServletResponse.class);
        servletRequest = mock(HttpServletRequest.class);
        assertThat(downloadController.getIssuesDownload(servletRequest,servletResponse, "csv",
                1, CommonTestUtil.getRequest()), is(notNullValue()));
        assertThat(downloadController.getIssuesDownload(servletRequest,servletResponse, "", 1,
                CommonTestUtil.getRequestEmpty()), is(notNullValue()));
        
        assertThat(downloadController.getIssuesDownload(servletRequest,servletResponse, "", 1,
                CommonTestUtil.getWithoutSizeRequest()), is(notNullValue()));
        
        when(downloadRepository.getFiltersFromDb(anyInt())).thenReturn(new ArrayList<Map<String,Object>>());
        assertThat(downloadController.getIssuesDownload(servletRequest,servletResponse, "csv", 1,
                CommonTestUtil.getRequest()), is(notNullValue()));
        
        when(downloadRepository.getFiltersFromDb(anyInt())).thenThrow(
                new DataException());
        ResponseEntity<Object> responseObj = downloadController
                .getIssuesDownload(servletRequest,servletResponse, "csv", 1,
                        CommonTestUtil.getRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    public void getIssuesDownloadEmptyServiceEndPointTest() throws Exception {
        List<Map<String, Object>> emptyList = new ArrayList();
        emptyList.add(new HashMap<String, Object>());
        when(downloadRepository.getFiltersFromDb(anyInt())).thenReturn(
                emptyList);
        servletResponse = mock(HttpServletResponse.class);
        servletRequest = mock(HttpServletRequest.class);
        assertThat(downloadController.getIssuesDownload(servletRequest,servletResponse, "csv",
                1, CommonTestUtil.getRequest()), is(notNullValue()));
    }

    @Test
    public void getIssuesDownloadEmptyServiceNameTest() throws Exception {
        List<Map<String, Object>> emptyList = new ArrayList();
        when(downloadRepository.getFiltersFromDb(anyInt())).thenReturn(
                emptyList);
        assertThat(downloadController.getIssuesDownload(servletRequest,servletResponse, "csv",
                1, CommonTestUtil.getRequest()), is(notNullValue()));
    }
}
