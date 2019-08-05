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
package com.tmobile.cloud.awsrules.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.ec2.AmazonEC2;
import com.tmobile.pacman.commons.rule.Annotation;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.apache.http.conn.ssl.*")
@PrepareForTest({RulesElasticSearchRepositoryUtil.class, Annotation.class,SSLContext.class, HttpClientBuilder.class, EntityUtils.class, HttpClient.class, HttpResponse.class, CloseableHttpResponse.class, CloseableHttpClient.class, StatusLine.class})
public class PacmanUtilsTest {

    @InjectMocks
    PacmanUtils pacmanUtils;
    
    
    @Mock
    AmazonEC2 ec2ServiceClient;
    
    @Mock
    DefaultArtifactVersion minVersion;
    
    Annotation annotation;
    
    URL url;
    
    XmlRpcClientConfigImpl config;
    
    CloseableHttpClient closeableHttpClient;
    
    CloseableHttpResponse httpResponse;
    
    @Before
    public void setUp() throws Exception{
        ec2ServiceClient = PowerMockito.mock(AmazonEC2.class); 
        minVersion = PowerMockito.mock(DefaultArtifactVersion.class); 
        url = PowerMockito.mock(URL.class); 
        config = PowerMockito.mock(XmlRpcClientConfigImpl.class); 
        annotation = PowerMockito.mock(Annotation.class);
        mockStatic(HttpClientBuilder.class);
        mockStatic(HttpClient.class);
        mockStatic(CloseableHttpClient.class);
        mockStatic(HttpResponse.class);
        mockStatic(CloseableHttpResponse.class);
        
        closeableHttpClient = PowerMockito.mock(CloseableHttpClient.class);
        HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        PowerMockito.when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        PowerMockito.when(HttpClientBuilder.create().setConnectionTimeToLive(anyLong(), anyObject())).thenReturn(httpClientBuilder);
        PowerMockito.when(HttpClientBuilder.create().setConnectionTimeToLive(anyLong(), anyObject()).build()).thenReturn(closeableHttpClient);
        HttpGet httpGet = PowerMockito.mock(HttpGet.class); 
        PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(httpGet);
        httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
        HttpEntity entity = PowerMockito.mock(HttpEntity.class);
        InputStream input = new ByteArrayInputStream("2001".getBytes() );
        PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        PowerMockito.when(entity.getContent()).thenReturn(input);
        PowerMockito.when(httpResponse.getEntity()).thenReturn(entity);
    }
 
   @SuppressWarnings("static-access")
    @Test
    public void getMissingTagsfromResourceAttributeTest() throws Exception {
        assertThat(pacmanUtils.getMissingTagsfromResourceAttribute(CommonTestUtils.getListString(),CommonTestUtils.getMapString("123")),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getMissingTagsTest() throws Exception {
        assertThat(pacmanUtils.getMissingTags(CommonTestUtils.getListString(), CommonTestUtils.getListString()),is(notNullValue()));
    }
    
    
    @SuppressWarnings("static-access")
    @Test
    public void checkIsCompliantTest() throws Exception {
        assertThat(pacmanUtils.checkIsCompliant("kernelversionForComparision.x86_64",CommonTestUtils.getSetString("123"), CommonTestUtils.getMapString("123")),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getBodyTest() throws Exception {
        assertThat(pacmanUtils.getBody(CommonTestUtils.getMapString("123")),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void isAccountExistsTest() throws Exception {
        assertThat(pacmanUtils.isAccountExists(CommonTestUtils.getListString(),"123"),is(notNullValue()));
    }
  
    @SuppressWarnings("static-access")
    @Test
    public void checkResourceIdFromElasticSearchTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.checkResourceIdFromElasticSearch("test","123","test","123"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.checkResourceIdFromElasticSearch("test","123","test","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getSecurityGroupsByInstanceIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getSecurityGroupsByInstanceId("test","123"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getSecurityGroupsByInstanceId("test","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getRouteTableIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getRouteTableId("test","123","test","123"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getRouteTableId("subnetid","123","test","123"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getRouteTableId("test","123","test","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getRouteTableRoutesIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getRouteTableRoutesId(CommonTestUtils.getListString(),CommonTestUtils.getSetString("123"),"123","test","igw"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getRouteTableRoutesId(CommonTestUtils.getListString(),CommonTestUtils.getSetString("123"),"123","test","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void checkInstanceIdForPortRuleInESTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.checkInstanceIdForPortRuleInES("test","123","test"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.checkInstanceIdForPortRuleInES("test","123","test"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getSeverityVulnerabilitiesByInstanceIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getSeverityVulnerabilitiesByInstanceId("test","123","test"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getSeverityVulnerabilitiesByInstanceId("test","123","test"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void checkAccessibleToAllTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.checkAccessibleToAll(CommonTestUtils.getSetGroupIdentifier("123"),"80","test","test","test","123"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getAllJsonObject());
        assertThat(pacmanUtils.checkAccessibleToAll(CommonTestUtils.getEmptySetGroupIdentifier("123"),"123","123","test","test","123"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.checkAccessibleToAll(CommonTestUtils.getEmptySetGroupIdentifier("123"),"123","123","test","test","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void isAccessbleToAllTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.isAccessbleToAll(CommonTestUtils.getSetGroupIdentifier("123"),80,"123","test"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getAllJsonObject());
        assertThat(pacmanUtils.isAccessbleToAll(CommonTestUtils.getSetGroupIdentifier("123"),80,"123","test"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.isAccessbleToAll(CommonTestUtils.getEmptySetGroupIdentifier("123"),80,"123","test"),is(notNullValue()));
    }
    
    
    @SuppressWarnings("static-access")
    @Test
    public void checkResourceIdForRuleInESTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.checkResourceIdForRuleInES("123","123","123","test"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.checkResourceIdForRuleInES("123","123","123","test"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getIdleLoadBalancerDetailsTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getIdleLoadBalancerDetails("123","rbl","123","test","test"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getIdleLoadBalancerDetails("123","123","123","test","test"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getUnownedAdGroupTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getUnownedAdGroup("123","rbl"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getUnownedAdGroup("123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getNestedRolesTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getNestedRoles("123","rbl","nested"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getAllJsonObject());
        assertThat(pacmanUtils.getNestedRoles("123","rbl","nested"),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getNestedRoles("123","123","nested"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getMemberOfTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getMemberOf("123","rbl"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getMemberOf("123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void checkInstanceIdFromElasticSearchForQualysTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.checkInstanceIdFromElasticSearchForQualys("123","rbl","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.checkInstanceIdFromElasticSearchForQualys("123","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getLowUtilizationEc2DetailsTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getLowUtilizationEc2Details("123","i-09","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getLowUtilizationEc2Details("123","i-09","123","123","123"),is(notNullValue()));
    }
    
    
    @SuppressWarnings("static-access")
    @Test
    public void getDetailsForCheckIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getDetailsForCheckId("123","redShift","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getDetailsForCheckId("123","redShift","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getRDSDetailsForCheckIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getRDSDetailsForCheckId("123","prd","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getRDSDetailsForCheckId("123","prd","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getEBSVolumeWithCheckIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getEBSVolumeWithCheckId("123","prd","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getEBSVolumeWithCheckId("123","prd","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getEBSSnapshotWithCheckIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getEBSSnapshotWithCheckId("123","snap","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getEBSSnapshotWithCheckId("123","snap","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getRDSSnapshotWithCheckIdTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getRDSSnapshotWithCheckId("123","snap","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getRDSSnapshotWithCheckId("123","snap","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void checkSSMAgentTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.checkSSMAgent("123","snap","123","123","123","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.checkSSMAgent("123","snap","123","123","123","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getResourceCreatedDetailsTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getResourceCreatedDetails("123","snap","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getResourceCreatedDetails("123","snap","123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getAmazonEC2ReservedInstanceLeaseExpirationTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getAmazonEC2ReservedInstanceLeaseExpiration("123","24300dd4","123","snap","123"),is(notNullValue()));
        
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getAmazonEC2ReservedInstanceLeaseExpiration("123","24300dd4","123","snap","123"),is(notNullValue()));
    }
    
    
    @SuppressWarnings("static-access")
    @Test
    public void getResponseTest() throws Exception {
        when(closeableHttpClient.execute((HttpGet) any())).thenReturn(httpResponse);
        assertThat(pacmanUtils.getResponse(CommonTestUtils.getMapString("123"),"123"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getValueFromElasticSearchSetTest() throws Exception {
        mockStatic(RulesElasticSearchRepositoryUtil.class);
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getJsonObject());
        assertThat(pacmanUtils.getValueFromElasticSearchAsSet("test",CommonTestUtils.getMapObject("123"),CommonTestUtils.getMulHashMapObject("123"),CommonTestUtils.getMapObject("123"),"test",CommonTestUtils.getMapStringList("123")),is(notNullValue()));
        
        when(RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyInt(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(pacmanUtils.getValueFromElasticSearchAsSet("test",CommonTestUtils.getMapObject("123"),CommonTestUtils.getMulHashMapObject("123"),CommonTestUtils.getMapObject("123"),"test",CommonTestUtils.getMapStringList("123")),is(nullValue()));
    }

}
