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
package com.tmobile.cloud.awsrules.ec2;

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
import com.tmobile.cloud.awsrules.utils.ConfigUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PacmanUtils.class,ConfigUtils.class})
public class TaggingRuleTest {

    @InjectMocks
    TaggingRule taggingRule;
    
    @Test
    public void executeTest() throws Exception {
    	
    	mockStatic(PacmanUtils.class);
    	when(PacmanUtils.getEnvironmentVariable(anyString())).thenReturn("env");
        when(PacmanUtils.getHeader(anyString())).thenReturn(CommonTestUtils.getMapString("id"));
        when(PacmanUtils.getConfigurationsFromConfigApi(anyString(), anyObject())).thenReturn(CommonTestUtils.getJsonObject());
    	mockStatic(ConfigUtils.class);
    	when(ConfigUtils.getPropValue(anyString())).thenReturn("tag");
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                true);
        when(PacmanUtils.splitStringToAList(anyString(),anyString())).thenReturn(CommonTestUtils.getListString());
        assertThat(taggingRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.getMissingTagsfromResourceAttribute(anyObject(),anyObject())).thenReturn(CommonTestUtils.getSetString("123"));
        assertThat(taggingRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.createAnnotaion(anyObject(),anyString(),anyObject(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtils.getAnnotation("123"));
        assertThat(taggingRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        assertThat(taggingRule.execute(CommonTestUtils.getOneMoreMapString("r_123 "),CommonTestUtils.getMapString("r_123 ")), is(notNullValue()));
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> taggingRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
    }
    
    @Test
    public void getHelpTextTest(){
        assertThat(taggingRule.getHelpText(), is(notNullValue()));
    }
}
