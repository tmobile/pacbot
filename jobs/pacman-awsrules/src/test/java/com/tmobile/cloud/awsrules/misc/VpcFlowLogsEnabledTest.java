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
package com.tmobile.cloud.awsrules.misc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.FlowLog;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.PacmanEc2Utils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.BaseRule;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class,BaseRule.class,PacmanEc2Utils.class})
public class VpcFlowLogsEnabledTest {

    @InjectMocks
    VpcFlowLogsEnabled flowLogsEnabled;
    
    
    @Mock
    AmazonEC2 ec2Client;

    @Before
    public void setUp() throws Exception{
        ec2Client = PowerMockito.mock(AmazonEC2.class); 
    }
    @Test
    public void test()throws Exception{
        FlowLog flow = new FlowLog();
        flow.setResourceId("123");
        List<FlowLog> flowLogs = new ArrayList<>();
        flowLogs.add(flow);
        
        List<FlowLog> emptyList = new ArrayList<>();
        
        
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                true);
        
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("client", ec2Client);
        VpcFlowLogsEnabled spy = Mockito.spy(new VpcFlowLogsEnabled());
        
        Mockito.doReturn(map).when((BaseRule)spy).getClientFor(anyObject(), anyString(), anyObject());
        
        mockStatic(PacmanEc2Utils.class);
        when(PacmanEc2Utils.getFlowLogs(anyObject(),anyObject())).thenReturn(flowLogs);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(PacmanEc2Utils.getFlowLogs(anyObject(),anyObject())).thenReturn(emptyList);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(PacmanEc2Utils.getFlowLogs(anyObject(),anyObject())).thenThrow(new InvalidInputException());
        assertThatThrownBy( 
                () -> flowLogsEnabled.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> flowLogsEnabled.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
    }
  
    
    @Test
    public void getHelpTextTest(){
        assertThat(flowLogsEnabled.getHelpText(), is(notNullValue()));
    }

}
