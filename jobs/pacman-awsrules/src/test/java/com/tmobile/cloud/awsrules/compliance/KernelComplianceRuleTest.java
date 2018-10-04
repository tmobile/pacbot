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
package com.tmobile.cloud.awsrules.compliance;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({PacmanTableAPI.class,SpacewalkAndSatelliteManager.class,SSHManager.class,PacmanUtils.class, LDAPManager.class,PacmanUtils.class,DefaultTargetCriteriaDataProvider.class})
public class KernelComplianceRuleTest {

    @InjectMocks
    KernelComplianceRule kernelComplianceRule;
    
    DefaultTargetCriteriaDataProvider defaultTargetCriteriaDataProvider; 
    @Before
    public void setUp() throws Exception{
        mockStatic(DefaultTargetCriteriaDataProvider.class);
        defaultTargetCriteriaDataProvider = PowerMockito.mock(DefaultTargetCriteriaDataProvider.class);
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.isAccountExists(anyObject(),anyString())).thenReturn(true);
        ReflectionTestUtils.setField(kernelComplianceRule, "kernelversion",
                "kernelversion");
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString())).thenReturn(defaultTargetCriteriaDataProvider);
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString()).getTargetCriterianData()).thenReturn(CommonTestUtils.getJsonObject());
    }
 
    @Test
    public void executeTest() throws Exception {
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(), anyString(),anyString(),anyString(),anyString(), anyString(),anyString(),anyString(),anyString(), anyString())).thenReturn(
                true);
        when(PacmanUtils.splitStringToAList(anyString(),anyString())).thenReturn(CommonTestUtils.getListString());
        
        //LDAP
        mockStatic(LDAPManager.class);
        when(LDAPManager.getQueryfromLdapElasticSearch(anyString(),anyString())).thenReturn("123");
        
       
        when(PacmanUtils.getPacmanHost(anyString())).thenReturn("host");
        when(PacmanUtils.formatUrl(anyObject(),anyString(),anyString())).thenReturn("host");
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(),anyObject())).thenReturn(true);
        
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));

        
        when(LDAPManager.getQueryfromLdapElasticSearch(anyString(),anyString())).thenReturn("");
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        //SSH
        when(PacmanUtils.isAccountExists(anyObject(),anyString())).thenReturn(true);
        mockStatic(SSHManager.class);
        when(SSHManager.getkernelDetailsViaSSH(anyString(),anyString(),anyString(),anyInt())).thenReturn("123");
        
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(),anyObject())).thenReturn(true);
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(SSHManager.getkernelDetailsViaSSH(anyString(),anyString(),anyString(),anyInt())).thenReturn("");
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        //SpaceAndSat
        mockStatic(SpacewalkAndSatelliteManager.class);
        when(SpacewalkAndSatelliteManager.getQueryfromRhnElasticSearch(anyString(),anyString())).thenReturn("123");
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(),anyObject())).thenReturn(true);
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(SpacewalkAndSatelliteManager.getQueryfromRhnElasticSearch(anyString(),anyString())).thenReturn("");
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        //Qualys
        when(PacmanUtils.getKernelInfoFromElasticSearchBySource(anyString(),anyString(),anyString())).thenReturn("kv");
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(),anyObject())).thenReturn(true);
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.getKernelInfoFromElasticSearchBySource(anyString(),anyString(),anyString())).thenReturn("");
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
      //Webservice
        mockStatic(PacmanTableAPI.class);
        when(PacmanTableAPI.getKernelVersionFromRHNSystemDetails(anyString(),anyString())).thenReturn("123");
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(),anyObject())).thenReturn(true);
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanTableAPI.getKernelVersionFromRHNSystemDetails(anyString(),anyString())).thenReturn("");
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString())).thenReturn(defaultTargetCriteriaDataProvider);
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString()).getTargetCriterianData()).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        assertThat(kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getEmptyMapString()), is(notNullValue()));
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(), anyString(),anyString(),anyString(),anyString(), anyString(),anyString(),anyString(),anyString(), anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> kernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
    }
    
    @Test
    public void getHelpTextTest(){
        assertThat(kernelComplianceRule.getHelpText(), is(notNullValue()));
    }
}
