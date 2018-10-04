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
package com.tmobile.cloud.awsrules.route53;

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
import java.util.HashMap;
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

import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.BaseRule;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class,BaseRule.class})
public class CheckAwsRoute53DNSForAccountsRuleTest {

    @InjectMocks
    CheckAwsRoute53DNSForAccountsRule checkAwsRoute53DNSForAccountsRule;
    
    
    @Mock
    AmazonRoute53Client route53Client;

    @Before
    public void setUp() throws Exception{
        route53Client = PowerMockito.mock(AmazonRoute53Client.class); 
    }
    @Test
    public void test()throws Exception{
        HostedZone hostedZone = new HostedZone();
        hostedZone.setId("123");
        Collection<HostedZone> hostedZones = new ArrayList<>();
        hostedZones.add(hostedZone);
        ListHostedZonesResult result = new ListHostedZonesResult();
        result.setHostedZones(hostedZones);
        
        
        Collection<HostedZone> emptyList = new ArrayList<>();
        ListHostedZonesResult emptyDetectorsResult = new ListHostedZonesResult();
        emptyDetectorsResult.setHostedZones(emptyList);
        
        
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString())).thenReturn(
                true);
        
        when(PacmanUtils.splitStringToAList(anyString(),anyString())).thenReturn(CommonTestUtils.getListString());
        
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("client", route53Client);
        CheckAwsRoute53DNSForAccountsRule spy = Mockito.spy(new CheckAwsRoute53DNSForAccountsRule());
        
        Mockito.doReturn(map).when((BaseRule)spy).getClientFor(anyObject(), anyString(), anyObject());
        
        when(route53Client.listHostedZones()).thenReturn(result);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(route53Client.listHostedZones()).thenReturn(emptyDetectorsResult);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(route53Client.listHostedZones()).thenThrow(new InvalidInputException());
        assertThatThrownBy( 
                () -> checkAwsRoute53DNSForAccountsRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> checkAwsRoute53DNSForAccountsRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
    }
  
    
    @Test
    public void getHelpTextTest(){
        assertThat(checkAwsRoute53DNSForAccountsRule.getHelpText(), is(notNullValue()));
    }

}
