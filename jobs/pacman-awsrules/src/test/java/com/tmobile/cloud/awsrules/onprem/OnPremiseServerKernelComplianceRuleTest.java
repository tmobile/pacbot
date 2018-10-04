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
package com.tmobile.cloud.awsrules.onprem;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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

import com.tmobile.cloud.awsrules.compliance.DefaultTargetCriteriaDataProvider;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
@PowerMockIgnore("org.apache.http.conn.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({PacmanUtils.class,DefaultTargetCriteriaDataProvider.class,PacmanUtils.class})
public class OnPremiseServerKernelComplianceRuleTest {

    @InjectMocks
    OnPremiseServerKernelComplianceRule premiseServerKernelComplianceRule;
    
    DefaultTargetCriteriaDataProvider defaultTargetCriteriaDataProvider; 
    @Before
    public void setUp() throws Exception{
        mockStatic(DefaultTargetCriteriaDataProvider.class);
        defaultTargetCriteriaDataProvider = PowerMockito.mock(DefaultTargetCriteriaDataProvider.class);
        
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString())).thenReturn(defaultTargetCriteriaDataProvider);
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString()).getTargetCriterianData()).thenReturn(CommonTestUtils.getJsonObject());
        
    }
 
    @Test
    public void executeTest() throws Exception {
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                true);
        when(PacmanUtils.splitStringToAList(anyString(),anyString())).thenReturn(CommonTestUtils.getListString());
        
        assertThat(premiseServerKernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        assertThat(premiseServerKernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getEmptyMapString()), is(notNullValue()));
        
        assertThat(premiseServerKernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getAnotherMapString("123")), is(notNullValue()));
        
        
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString())).thenReturn(defaultTargetCriteriaDataProvider);
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString()).getTargetCriterianData()).thenReturn(CommonTestUtils.getEmptyJsonObject());
        assertThat(premiseServerKernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString())).thenReturn(defaultTargetCriteriaDataProvider);
        PowerMockito.when(DefaultTargetCriteriaDataProvider.getInstance(anyString()).getTargetCriterianData()).thenReturn(CommonTestUtils.getJsonObject());
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(), anyObject())).thenReturn(
                true);
        assertThat(premiseServerKernelComplianceRule.execute(CommonTestUtils.getFinalKernelReleaseAnotherMapString("r_123 "),CommonTestUtils.getFinalKernelReleaseAnotherMapString("123")), is(notNullValue()));
        
        
        when(PacmanUtils.checkIsCompliant(anyString(),anyObject(), anyObject())).thenReturn(
                true);
        assertThat(premiseServerKernelComplianceRule.execute(CommonTestUtils.getLastPatchedMapString("123 "),CommonTestUtils.getLastPatchedMapString("123")), is(notNullValue()));
        
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(), anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> premiseServerKernelComplianceRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
    }
    
    @Test
    public void getHelpTextTest(){
        assertThat(premiseServerKernelComplianceRule.getHelpText(), is(notNullValue()));
    }
}
