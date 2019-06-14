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
package com.tmobile.cloud.awsrules.redshift;

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
import com.tmobile.cloud.awsrules.utils.PacmanEc2Utils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class,PacmanEc2Utils.class})
public class RedShiftPublicAccessRuleTest {

    @InjectMocks
    RedShiftPublicAccessRule redShiftPublicAccessRule;
 
    @Test
    public void executeTest() throws Exception {
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                true);
        
        when(PacmanUtils.getPacmanHost(anyString())).thenReturn("host");
        when(PacmanUtils.getRouteTableId(anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getSetString("123"));
        
        when(PacmanUtils.isIgwFound(anyString(),anyString(),anyString(),anyObject(),anyObject(),anyString(),anyString(),anyString())).thenReturn(true);
        when(PacmanUtils.getSecurityGroupsByResourceId(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getListSecurityGroupId());
        when(PacmanUtils.checkAccessibleToAll(anyObject(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getMapBoolean("123"));
        when(PacmanUtils.setAnnotation(anyObject(),anyObject(),anyString(),anyString(),anyObject())).thenReturn(CommonTestUtils.getAnnotation("123"));
        assertThat(redShiftPublicAccessRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.getRouteTableId(anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getEmptySetString());
        when(PacmanUtils.isIgwFound(anyString(),anyString(),anyString(),anyObject(),anyObject(),anyString(),anyString(),anyString())).thenReturn(false);
        
        assertThat(redShiftPublicAccessRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.getRouteTableId(anyString(),anyString(),anyString(),anyString())).thenThrow(new Exception());
        assertThatThrownBy( 
                () -> redShiftPublicAccessRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(RuleExecutionFailedExeption.class);
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> redShiftPublicAccessRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
    }
    
    @Test
    public void getHelpTextTest(){
        assertThat(redShiftPublicAccessRule.getHelpText(), is(notNullValue()));
    }
}
