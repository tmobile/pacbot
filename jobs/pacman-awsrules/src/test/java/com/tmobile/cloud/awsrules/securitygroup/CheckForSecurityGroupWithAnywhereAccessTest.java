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
package com.tmobile.cloud.awsrules.securitygroup;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class})
public class CheckForSecurityGroupWithAnywhereAccessTest {

    @InjectMocks
    CheckForSecurityGroupWithAnywhereAccess checkForSecurityGroupWithAnywhereAccess;
 
    @Test
    public void executeTest() throws Exception {
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                true);
        when(PacmanUtils.formatUrl(anyObject(),anyString())).thenReturn("host");
        when(PacmanUtils.checkAccessibleToAll(anyObject(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getMapBoolean("r_123 "));
        assertThat(checkForSecurityGroupWithAnywhereAccess.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.checkAccessibleToAll(anyObject(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getEmptyMapBoolean("r_123 "));
        assertThat(checkForSecurityGroupWithAnywhereAccess.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.checkAccessibleToAll(anyObject(),anyString(),anyString(),anyString(),anyString(),anyString())).thenThrow(new Exception());
        assertThatThrownBy( 
                () -> checkForSecurityGroupWithAnywhereAccess.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(RuleExecutionFailedExeption.class);
        
        assertThatThrownBy( 
                () -> checkForSecurityGroupWithAnywhereAccess.execute(CommonTestUtils.getOneMoreMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(RuleExecutionFailedExeption.class);
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> checkForSecurityGroupWithAnywhereAccess.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
        
    }
    
    @Test
    public void getHelpTextTest(){
        assertThat(checkForSecurityGroupWithAnywhereAccess.getHelpText(), is(notNullValue()));
    }
}
