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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ HttpServletResponse.class, ServletOutputStream.class, EntityUtils.class, Response.class, RestClient.class })
public class DownloadFileServiceTest {

    @InjectMocks
    private DownloadFileService downloadFileService;

    @Mock
    private HttpServletResponse httpServletResponse;
    
    @Mock
    private ServletOutputStream servletOutputStream;
    
    @Mock
    private PacmanRdsRepository repository;
    
    @Mock
    OutputStream outputStream;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void downloadDataTest() throws Exception {
        Gson gson = new Gson();
        JsonArray array = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("column1", gson.fromJson("column1", JsonElement.class));
        jsonObject.add("column2", gson.fromJson("column2", JsonElement.class));
        jsonObject.add("column3", gson.fromJson("column3", JsonElement.class));
        jsonObject.add("column4", gson.fromJson("column4", JsonElement.class));
        array.add(jsonObject);
        httpServletResponse = mock(HttpServletResponse.class);
        servletOutputStream = mock(ServletOutputStream.class);
        when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
        downloadFileService.downloadData(httpServletResponse, array,"csv","xyz");
        
        downloadFileService.downloadData(httpServletResponse, array,"excel","xyz1");
    }

    @Test
    public void getAllAssetGroupApisTest() throws Exception {
        when(repository.getDataFromPacman(anyString())).thenReturn(CommonTestUtil.getListMapObject());
        ReflectionTestUtils.setField(downloadFileService, "assetGroupQuery", "dummyEsURL");
        assertThat(downloadFileService.getAllAssetGroupApis(), is(notNullValue()));
    }
}
