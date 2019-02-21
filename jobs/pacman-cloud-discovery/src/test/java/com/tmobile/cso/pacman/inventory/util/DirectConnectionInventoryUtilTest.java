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
import com.amazonaws.services.directconnect.AmazonDirectConnectClient;
import com.amazonaws.services.directconnect.AmazonDirectConnectClientBuilder;
import com.amazonaws.services.directconnect.model.Connection;
import com.amazonaws.services.directconnect.model.DescribeConnectionsResult;
import com.amazonaws.services.directconnect.model.DescribeVirtualInterfacesResult;
import com.amazonaws.services.directconnect.model.VirtualInterface;


/**
 * The Class DirectConnectionInventoryUtilTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegionUtils.class,AmazonDirectConnectClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class DirectConnectionInventoryUtilTest {

    /** The direct connection inventory util. */
    @InjectMocks
    DirectConnectionInventoryUtil directConnectionInventoryUtil;
    
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
     * Fetch direct connections test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDirectConnectionsTest() throws Exception {
        
        mockStatic(AmazonDirectConnectClientBuilder.class);
        AmazonDirectConnectClient amazonDirectConnectClient = PowerMockito.mock(AmazonDirectConnectClient.class);
        AmazonDirectConnectClientBuilder amazonDirectConnectClientBuilder = PowerMockito.mock(AmazonDirectConnectClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonDirectConnectClientBuilder.standard()).thenReturn(amazonDirectConnectClientBuilder);
        when(amazonDirectConnectClientBuilder.withCredentials(anyObject())).thenReturn(amazonDirectConnectClientBuilder);
        when(amazonDirectConnectClientBuilder.withRegion(anyString())).thenReturn(amazonDirectConnectClientBuilder);
        when(amazonDirectConnectClientBuilder.build()).thenReturn(amazonDirectConnectClient);
        
        DescribeConnectionsResult describeConnectionsResult = new DescribeConnectionsResult();
        List<Connection> connections = new ArrayList<>();
        connections.add(new Connection());
        describeConnectionsResult.setConnections(connections);
        when(amazonDirectConnectClient.describeConnections()).thenReturn(describeConnectionsResult);
        assertThat(directConnectionInventoryUtil.fetchDirectConnections(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch direct connections test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDirectConnectionsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(directConnectionInventoryUtil.fetchDirectConnections(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch direct connections virtual interfaces test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDirectConnectionsVirtualInterfacesTest() throws Exception {
        
        mockStatic(AmazonDirectConnectClientBuilder.class);
        AmazonDirectConnectClient amazonDirectConnectClient = PowerMockito.mock(AmazonDirectConnectClient.class);
        AmazonDirectConnectClientBuilder amazonDirectConnectClientBuilder = PowerMockito.mock(AmazonDirectConnectClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonDirectConnectClientBuilder.standard()).thenReturn(amazonDirectConnectClientBuilder);
        when(amazonDirectConnectClientBuilder.withCredentials(anyObject())).thenReturn(amazonDirectConnectClientBuilder);
        when(amazonDirectConnectClientBuilder.withRegion(anyString())).thenReturn(amazonDirectConnectClientBuilder);
        when(amazonDirectConnectClientBuilder.build()).thenReturn(amazonDirectConnectClient);
        
        DescribeVirtualInterfacesResult  describeVirtualInterfacesResult  = new DescribeVirtualInterfacesResult ();
        List<VirtualInterface> virtualInterfaces = new ArrayList<>();
        virtualInterfaces.add(new VirtualInterface());
        describeVirtualInterfacesResult.setVirtualInterfaces(virtualInterfaces);
        when(amazonDirectConnectClient.describeVirtualInterfaces()).thenReturn(describeVirtualInterfacesResult);
        assertThat(directConnectionInventoryUtil.fetchDirectConnectionsVirtualInterfaces(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch direct connections virtual interfaces test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDirectConnectionsVirtualInterfacesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(directConnectionInventoryUtil.fetchDirectConnectionsVirtualInterfaces(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
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
