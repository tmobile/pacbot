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
package com.tmobile.cso.pacman.inventory.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionImpl;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.AWSElasticsearchClientBuilder;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainsResult;
import com.amazonaws.services.elasticsearch.model.DomainInfo;
import com.amazonaws.services.elasticsearch.model.ElasticsearchDomainStatus;
import com.amazonaws.services.elasticsearch.model.ListDomainNamesResult;
import com.amazonaws.services.elasticsearch.model.ListTagsResult;
import com.amazonaws.services.elasticsearch.model.Tag;


/**
 * The Class ESInventoryUtilTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegionUtils.class,AWSElasticsearchClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class ESInventoryUtilTest {

    /** The es inventory util. */
    @InjectMocks
    ESInventoryUtil esInventoryUtil;
    
    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        mockStatic(RegionUtils.class);
        when(RegionUtils.getRegions()).thenReturn(getRegions());
    }
    
    /**
     * Fetch ES info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchESInfoTest() throws Exception {
        
        mockStatic(AWSElasticsearchClientBuilder.class);
        AWSElasticsearch awsEsClient = PowerMockito.mock(AWSElasticsearch.class);
        AWSElasticsearchClientBuilder awsElasticsearchClientBuilder = PowerMockito.mock(AWSElasticsearchClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(awsElasticsearchClientBuilder.standard()).thenReturn(awsElasticsearchClientBuilder);
        when(awsElasticsearchClientBuilder.withCredentials(anyObject())).thenReturn(awsElasticsearchClientBuilder);
        when(awsElasticsearchClientBuilder.withRegion(anyString())).thenReturn(awsElasticsearchClientBuilder);
        when(awsElasticsearchClientBuilder.build()).thenReturn(awsEsClient);
        
        ListDomainNamesResult listDomainResult = new ListDomainNamesResult();
        List<DomainInfo> domainNames = new ArrayList<>();
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setDomainName("domain");
        domainNames.add(domainInfo);
        listDomainResult.setDomainNames(domainNames);
        when(awsEsClient.listDomainNames(anyObject())).thenReturn(listDomainResult);
        
        DescribeElasticsearchDomainsResult  describeResult = new DescribeElasticsearchDomainsResult() ;
        List<ElasticsearchDomainStatus> domainStatusList = new ArrayList<>();
        ElasticsearchDomainStatus domainStatus = new ElasticsearchDomainStatus();
        domainStatus.setARN("arn");
        domainStatusList.add(domainStatus);
        describeResult.setDomainStatusList(domainStatusList);
        when(awsEsClient.describeElasticsearchDomains(anyObject())).thenReturn(describeResult);
        ListTagsResult listTagsResult = new ListTagsResult();
        listTagsResult.setTagList(new ArrayList<Tag>());
        when(awsEsClient.listTags(anyObject())).thenReturn(listTagsResult);
        
        assertThat(esInventoryUtil.fetchESInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch ES info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchESInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(esInventoryUtil.fetchESInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Gets the regions.
     *
     * @return the regions
     */
    private List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        Region region = new Region(new RegionImpl() {
            
            @Override
            public boolean isServiceSupported(String serviceName) {
                return false;
            }
            
            @Override
            public boolean hasHttpsEndpoint(String serviceName) {
                return false;
            }
            
            @Override
            public boolean hasHttpEndpoint(String serviceName) {
                return false;
            }
            
            @Override
            public String getServiceEndpoint(String serviceName) {
                return null;
            }
            
            @Override
            public String getPartition() {
                return null;
            }
            
            @Override
            public String getName() {
                return "north";
            }
            
            @Override
            public String getDomain() {
                return null;
            }
            
            @Override
            public Collection<String> getAvailableEndpoints() {
                return null;
            }
        });
        regions.add(region);
        return regions;
    }
}
