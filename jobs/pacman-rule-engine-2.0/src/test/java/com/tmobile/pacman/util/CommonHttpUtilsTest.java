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
package com.tmobile.pacman.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.util.CommonHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClients.class,SSLContext.class, HttpClientBuilder.class, EntityUtils.class, HttpResponse.class, CloseableHttpResponse.class, CloseableHttpClient.class})
public class CommonHttpUtilsTest {

    @InjectMocks
    CommonHttpUtils configurationDataProvider;
    
    CloseableHttpClient closeableHttpClient;
    
    CloseableHttpResponse httpResponse;
    
    @Before
    public void setUp() throws Exception{
       
        mockStatic(HttpClientBuilder.class);
        mockStatic(HttpClient.class);
        mockStatic(CloseableHttpClient.class);
        mockStatic(HttpResponse.class);
        mockStatic(CloseableHttpResponse.class);
        mockStatic(HttpClients.class);
        
        closeableHttpClient = PowerMockito.mock(CloseableHttpClient.class);
        HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        PowerMockito.when(HttpClients.custom()).thenReturn(httpClientBuilder);
        PowerMockito.when(HttpClients.custom().setSSLHostnameVerifier(anyObject())).thenReturn(httpClientBuilder);
        PowerMockito.when(HttpClients.custom().setSSLHostnameVerifier(anyObject()).setSSLContext(anyObject())).thenReturn(httpClientBuilder);
        PowerMockito.when(HttpClients.custom().setSSLHostnameVerifier(anyObject()).setSSLContext(anyObject()).build()).thenReturn(closeableHttpClient);
        HttpGet httpGet = PowerMockito.mock(HttpGet.class); 
        PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(httpGet);
        httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
        HttpEntity entity = PowerMockito.mock(HttpEntity.class);
        InputStream input = new ByteArrayInputStream("{\"data\":{\"puliclyaccessble\":false},\"input\":{\"endpoint\":\"http://123\"}}".getBytes() );
        PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        PowerMockito.when(entity.getContent()).thenReturn(input);
        PowerMockito.when(httpResponse.getEntity()).thenReturn(entity);
    }
   
    @Test
    public void getHeaderTest() throws Exception {
    	
    	
        assertThat(configurationDataProvider.getHeader("123"), is(notNullValue()));
        
    }
    
    @Test
    public void getConfigurationsFromConfigApiTest() throws Exception {
    	 when(closeableHttpClient.execute((HttpGet) any())).thenReturn(httpResponse);
    	
        assertThat(configurationDataProvider.getConfigurationsFromConfigApi("123",new HashMap<String, Object>()), is(nullValue()));
        
    }
    
}
