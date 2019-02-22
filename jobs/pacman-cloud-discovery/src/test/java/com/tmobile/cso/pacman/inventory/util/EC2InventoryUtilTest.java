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
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.CustomerGateway;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeCustomerGatewaysResult;
import com.amazonaws.services.ec2.model.DescribeDhcpOptionsResult;
import com.amazonaws.services.ec2.model.DescribeEgressOnlyInternetGatewaysResult;
import com.amazonaws.services.ec2.model.DescribeInternetGatewaysResult;
import com.amazonaws.services.ec2.model.DescribeNetworkAclsResult;
import com.amazonaws.services.ec2.model.DescribeReservedInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRouteTablesResult;
import com.amazonaws.services.ec2.model.DescribeVpcPeeringConnectionsResult;
import com.amazonaws.services.ec2.model.DescribeVpnConnectionsResult;
import com.amazonaws.services.ec2.model.DescribeVpnGatewaysResult;
import com.amazonaws.services.ec2.model.DhcpOptions;
import com.amazonaws.services.ec2.model.EgressOnlyInternetGateway;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.ReservedInstances;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.VpcPeeringConnection;
import com.amazonaws.services.ec2.model.VpnConnection;
import com.amazonaws.services.ec2.model.VpnGateway;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeInstanceInformationResult;
import com.amazonaws.services.simplesystemsmanagement.model.InstanceInformation;


/**
 * The Class EC2InventoryUtilTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegionUtils.class,AmazonEC2ClientBuilder.class,AWSSimpleSystemsManagementClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class EC2InventoryUtilTest {
    
    /** The ec 2 inventory util. */
    @InjectMocks
    EC2InventoryUtil ec2InventoryUtil;
    
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
     * Fetch route tables test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRouteTablesTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeRouteTablesResult describeRouteTablesResult = new DescribeRouteTablesResult();
        List<RouteTable> routeTableList = new ArrayList<>();
        routeTableList.add(new RouteTable());
        describeRouteTablesResult.setRouteTables(routeTableList);
        when(ec2Client.describeRouteTables()).thenReturn(describeRouteTablesResult);
        assertThat(ec2InventoryUtil.fetchRouteTables(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch route tables test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRouteTablesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchRouteTables(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch network ACL test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchNetworkACLTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeNetworkAclsResult describeNetworkAclsResult = new DescribeNetworkAclsResult();
        List<NetworkAcl> networkAclList = new ArrayList<>();
        networkAclList.add(new NetworkAcl());
        describeNetworkAclsResult.setNetworkAcls(networkAclList);
        when(ec2Client.describeNetworkAcls()).thenReturn(describeNetworkAclsResult);
        assertThat(ec2InventoryUtil.fetchNetworkACL(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch network ACL test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchNetworkACLTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchNetworkACL(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch elastic IP addresses test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchElasticIPAddressesTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeAddressesResult describeAddressesResult = new DescribeAddressesResult();
        List<Address> elasticIPList = new ArrayList<>();
        elasticIPList.add(new Address());
        describeAddressesResult.setAddresses(elasticIPList);
        when(ec2Client.describeAddresses()).thenReturn(describeAddressesResult);
        assertThat(ec2InventoryUtil.fetchElasticIPAddresses(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch elastic IP addresses test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchElasticIPAddressesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchElasticIPAddresses(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch internet gateway test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchInternetGatewayTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeInternetGatewaysResult describeInternetGatewaysResult = new DescribeInternetGatewaysResult();
        List<InternetGateway> internetGatewayList = new ArrayList<>();
        internetGatewayList.add(new InternetGateway());
        describeInternetGatewaysResult.setInternetGateways(internetGatewayList);
        when(ec2Client.describeInternetGateways()).thenReturn(describeInternetGatewaysResult);
        assertThat(ec2InventoryUtil.fetchInternetGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch internet gateway test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchInternetGatewayTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchInternetGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch VPN gateway test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVPNGatewayTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeVpnGatewaysResult describeVpnGatewaysResult = new DescribeVpnGatewaysResult();
        List<VpnGateway> vpnGatewayList = new ArrayList<>();
        vpnGatewayList.add(new VpnGateway());
        describeVpnGatewaysResult.setVpnGateways(vpnGatewayList);
        when(ec2Client.describeVpnGateways()).thenReturn(describeVpnGatewaysResult);
        assertThat(ec2InventoryUtil.fetchVPNGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch VPN gateway test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVPNGatewayTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchVPNGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch egress gateway test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEgressGatewayTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeEgressOnlyInternetGatewaysResult describeEgressOnlyInternetGatewaysResult = new DescribeEgressOnlyInternetGatewaysResult();
        List<EgressOnlyInternetGateway> egressGatewayList = new ArrayList<>();
        egressGatewayList.add(new EgressOnlyInternetGateway());
        describeEgressOnlyInternetGatewaysResult.setEgressOnlyInternetGateways(egressGatewayList);
        when(ec2Client.describeEgressOnlyInternetGateways(anyObject())).thenReturn(describeEgressOnlyInternetGatewaysResult);
        assertThat(ec2InventoryUtil.fetchEgressGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch egress gateway test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEgressGatewayTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchEgressGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch DHCP options test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDHCPOptionsTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeDhcpOptionsResult describeDhcpOptionsResult = new DescribeDhcpOptionsResult();
        List<DhcpOptions> dhcpOptionsList = new ArrayList<>();
        dhcpOptionsList.add(new DhcpOptions());
        describeDhcpOptionsResult.setDhcpOptions(dhcpOptionsList);
        when(ec2Client.describeDhcpOptions()).thenReturn(describeDhcpOptionsResult);
        assertThat(ec2InventoryUtil.fetchDHCPOptions(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch DHCP options test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDHCPOptionsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchDHCPOptions(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch peering connections test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchPeeringConnectionsTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeVpcPeeringConnectionsResult describeVpcPeeringConnectionsResult = new DescribeVpcPeeringConnectionsResult();
        List<VpcPeeringConnection> peeringConnectionList = new ArrayList<>();
        peeringConnectionList.add(new VpcPeeringConnection());
        describeVpcPeeringConnectionsResult.setVpcPeeringConnections(peeringConnectionList);
        when(ec2Client.describeVpcPeeringConnections()).thenReturn(describeVpcPeeringConnectionsResult);
        assertThat(ec2InventoryUtil.fetchPeeringConnections(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch peering connections test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchPeeringConnectionsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchPeeringConnections(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch customer gateway test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchCustomerGatewayTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeCustomerGatewaysResult describeCustomerGatewaysResult = new DescribeCustomerGatewaysResult();
        List<CustomerGateway> customerGatewayList = new ArrayList<>();
        customerGatewayList.add(new CustomerGateway());
        describeCustomerGatewaysResult.setCustomerGateways(customerGatewayList);
        when(ec2Client.describeCustomerGateways()).thenReturn(describeCustomerGatewaysResult);
        assertThat(ec2InventoryUtil.fetchCustomerGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch customer gateway test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchCustomerGatewayTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchCustomerGateway(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch VPN connections test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVPNConnectionsTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeVpnConnectionsResult describeVpnConnectionsResult = new DescribeVpnConnectionsResult();
        List<VpnConnection> vpnConnectionsList = new ArrayList<>();
        vpnConnectionsList.add(new VpnConnection());
        describeVpnConnectionsResult.setVpnConnections(vpnConnectionsList);
        when(ec2Client.describeVpnConnections()).thenReturn(describeVpnConnectionsResult);
        assertThat(ec2InventoryUtil.fetchVPNConnections(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch VPN connections test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVPNConnectionsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchVPNConnections(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch reserved instances test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchReservedInstancesTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeReservedInstancesResult describeReservedInstancesResult = new DescribeReservedInstancesResult();
        List<ReservedInstances> reservedInstancesList = new ArrayList<>();
        reservedInstancesList.add(new ReservedInstances());
        describeReservedInstancesResult.setReservedInstances(reservedInstancesList);
        when(ec2Client.describeReservedInstances()).thenReturn(describeReservedInstancesResult);
        assertThat(ec2InventoryUtil.fetchReservedInstances(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch reserved instances test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchReservedInstancesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchReservedInstances(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch SSM info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSSMInfoTest() throws Exception {
        
        mockStatic(AWSSimpleSystemsManagementClientBuilder.class);
        AWSSimpleSystemsManagement ssmClient = PowerMockito.mock(AWSSimpleSystemsManagement.class);
        AWSSimpleSystemsManagementClientBuilder simpleSystemsManagementClientBuilder = PowerMockito.mock(AWSSimpleSystemsManagementClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(simpleSystemsManagementClientBuilder.standard()).thenReturn(simpleSystemsManagementClientBuilder);
        when(simpleSystemsManagementClientBuilder.withCredentials(anyObject())).thenReturn(simpleSystemsManagementClientBuilder);
        when(simpleSystemsManagementClientBuilder.withRegion(anyString())).thenReturn(simpleSystemsManagementClientBuilder);
        when(simpleSystemsManagementClientBuilder.build()).thenReturn(ssmClient);
        
        DescribeInstanceInformationResult describeInstanceInfoRslt = new DescribeInstanceInformationResult();
        List<InstanceInformation> ssmInstanceListTemp = new ArrayList<>();
        ssmInstanceListTemp.add(new InstanceInformation());
        describeInstanceInfoRslt.setInstanceInformationList(ssmInstanceListTemp);
        when(ssmClient.describeInstanceInformation(anyObject())).thenReturn(describeInstanceInfoRslt);
        assertThat(ec2InventoryUtil.fetchSSMInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch SSM info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSSMInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(ec2InventoryUtil.fetchSSMInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
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
