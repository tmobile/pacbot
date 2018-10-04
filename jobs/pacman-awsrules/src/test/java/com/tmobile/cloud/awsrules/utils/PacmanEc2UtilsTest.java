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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeFlowLogsRequest;
import com.amazonaws.services.ec2.model.DescribeFlowLogsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.FlowLog;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Volume;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PacmanUtils.class})
public class PacmanEc2UtilsTest {

    @InjectMocks
    PacmanEc2Utils pacmanEc2Utils;
    
    
    @Mock
    AmazonEC2 ec2ServiceClient;
    
    @Mock
    DescribeVolumesRequest describeVolumesRequest;
    
    @Mock
    DescribeFlowLogsRequest describeFlowLogsRequest;
    
    @Before
    public void setUp() throws Exception{
        ec2ServiceClient = PowerMockito.mock(AmazonEC2.class); 
        describeVolumesRequest = PowerMockito.mock(DescribeVolumesRequest.class); 
        describeFlowLogsRequest = PowerMockito.mock(DescribeFlowLogsRequest.class); 
    }
 
    @SuppressWarnings("static-access")
    @Test
    public void collectAllVolumesTest() throws Exception {
        
        Volume vol = new Volume();
        vol.setVolumeId("123");
        Collection<Volume> volumes = new ArrayList<>();
        volumes.add(vol);
       
        
        DescribeVolumesResult result = new DescribeVolumesResult();
        result.setVolumes(volumes);
        
        when(ec2ServiceClient.describeVolumes(anyObject())).thenReturn(result);
        assertThat(pacmanEc2Utils.collectAllVolumes(ec2ServiceClient,describeVolumesRequest),is(notNullValue()));
    }
    
    
    @SuppressWarnings("static-access")
    @Test
    public void getFlowLogsTest() throws Exception {
        
        FlowLog flowLog = new FlowLog();
        flowLog.setFlowLogId("123");
        Collection<FlowLog> flowLogs = new ArrayList<>();
        flowLogs.add(flowLog);
       
        
        DescribeFlowLogsResult flowLogsResult = new DescribeFlowLogsResult();
        flowLogsResult.setFlowLogs(flowLogs);
        
        when(ec2ServiceClient.describeFlowLogs(anyObject())).thenReturn(flowLogsResult);
        assertThat(pacmanEc2Utils.getFlowLogs(ec2ServiceClient,describeFlowLogsRequest),is(notNullValue()));
        
        when(ec2ServiceClient.describeFlowLogs(anyObject())).thenThrow(new RuleExecutionFailedExeption());
        assertThat(pacmanEc2Utils.getFlowLogs(ec2ServiceClient,describeFlowLogsRequest),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void setFiltersTest() throws Exception {
        
        assertThat(pacmanEc2Utils.setFilters("test1", "describeFlowLogsRequest"),is(notNullValue()));
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void checkAccessibleToAllTest() throws Exception {
        GroupIdentifier identifier = new GroupIdentifier();
        identifier.setGroupId("sg-5414b52c");
        Set<GroupIdentifier> secuityGroups = new HashSet<GroupIdentifier>();
        secuityGroups.add(identifier);
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doHttpPost(anyString(),anyString())).thenReturn("{\"took\":67,\"timed_out\":false,\"_shards\":{\"total\":3,\"successful\":3,\"failed\":0},\"hits\":{\"total\":1,\"max_score\":12.365102,\"hits\":[{\"_index\":\"_index\",\"_type\":\"_type\",\"_id\":\"_id\",\"_score\":12.365102,\"_routing\":\"_routing\",\"_parent\":\"_parent\",\"_source\":{\"discoverydate\":\"2018-07-31 08:00:00+00\",\"accountid\":\"accountid\",\"region\":\"region\",\"groupid\":\"groupid\",\"type\":\"inbound\",\"ipprotocol\":\"tcp\",\"fromport\":\"80\",\"toport\":\"80\",\"cidrip\":\"0.0.0.0\0\",\"cidripv6\":\"\",\"accountname\":\"accountname\",\"_loaddate\":\"2018-07-31 9:24:00+0000\"}}]}}");
        
        assertThat(pacmanEc2Utils.checkAccessibleToAll(secuityGroups,"11","url","describeFlowLogsRequest"),is(notNullValue()));
        
        when(PacmanUtils.doHttpPost(anyString(),anyString())).thenThrow(new RuleExecutionFailedExeption());
        assertThatThrownBy( 
                () -> pacmanEc2Utils.checkAccessibleToAll(secuityGroups,"11","url","describeFlowLogsRequest")).isInstanceOf(RuleExecutionFailedExeption.class);
    }
}
