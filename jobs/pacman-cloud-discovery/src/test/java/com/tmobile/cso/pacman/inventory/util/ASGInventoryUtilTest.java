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
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsResult;
import com.amazonaws.services.autoscaling.model.DescribePoliciesResult;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;


/**
 * The Class ASGInventoryUtilTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegionUtils.class,AmazonAutoScalingClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class ASGInventoryUtilTest {

    /** The asg inventory util. */
    @InjectMocks
    ASGInventoryUtil asgInventoryUtil;
    
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
     * Fetch launch configurations test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchLaunchConfigurationsTest() throws Exception {
        
        mockStatic(AmazonAutoScalingClientBuilder.class);
        AmazonAutoScaling asgClient = PowerMockito.mock(AmazonAutoScaling.class);
        AmazonAutoScalingClientBuilder amazonAutoScalingClientBuilder = PowerMockito.mock(AmazonAutoScalingClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonAutoScalingClientBuilder.standard()).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.withCredentials(anyObject())).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.withRegion(anyString())).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.build()).thenReturn(asgClient);
        
        DescribeAutoScalingGroupsResult autoScalingGroupsResult = new DescribeAutoScalingGroupsResult();
        List<AutoScalingGroup> asgList = new ArrayList<>();
        AutoScalingGroup autoScalingGroup = new AutoScalingGroup();
        autoScalingGroup.setLaunchConfigurationName("launchConfigurationName");
        asgList.add(autoScalingGroup);
        autoScalingGroupsResult.setAutoScalingGroups(asgList);
        when(asgClient.describeAutoScalingGroups(anyObject())).thenReturn(autoScalingGroupsResult);
        
        DescribeLaunchConfigurationsResult launchConfigurationsResult = new DescribeLaunchConfigurationsResult();
        List<LaunchConfiguration> launchConfigurations = new ArrayList<>();
        launchConfigurations.add(new LaunchConfiguration());
        
        launchConfigurationsResult.setLaunchConfigurations(launchConfigurations);
        when(asgClient.describeLaunchConfigurations(anyObject())).thenReturn(launchConfigurationsResult);
        assertThat(asgInventoryUtil.fetchLaunchConfigurations(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch launch configurations test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchLaunchConfigurationsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(asgInventoryUtil.fetchLaunchConfigurations(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch scaling policies test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchScalingPoliciesTest() throws Exception {
        
        mockStatic(AmazonAutoScalingClientBuilder.class);
        AmazonAutoScaling asgClient = PowerMockito.mock(AmazonAutoScaling.class);
        AmazonAutoScalingClientBuilder amazonAutoScalingClientBuilder = PowerMockito.mock(AmazonAutoScalingClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonAutoScalingClientBuilder.standard()).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.withCredentials(anyObject())).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.withRegion(anyString())).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.build()).thenReturn(asgClient);
        
        DescribeAutoScalingGroupsResult autoScalingGroupsResult = new DescribeAutoScalingGroupsResult();
        List<AutoScalingGroup> asgList = new ArrayList<>();
        AutoScalingGroup autoScalingGroup = new AutoScalingGroup();
        autoScalingGroup.setAutoScalingGroupName("autoScalingGrpName");
        asgList.add(autoScalingGroup);
        autoScalingGroupsResult.setAutoScalingGroups(asgList);
        
        when(asgClient.describeAutoScalingGroups(anyObject())).thenReturn(autoScalingGroupsResult);
        
        DescribePoliciesResult policiesResult = new DescribePoliciesResult();
        List<ScalingPolicy> scalingPolicies = new ArrayList<>();
        scalingPolicies.add(new ScalingPolicy());
        policiesResult.setScalingPolicies(scalingPolicies);
        when(asgClient.describePolicies(anyObject())).thenReturn(policiesResult);
        assertThat(asgInventoryUtil.fetchScalingPolicies(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch scaling policies test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchScalingPoliciesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(asgInventoryUtil.fetchScalingPolicies(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
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
