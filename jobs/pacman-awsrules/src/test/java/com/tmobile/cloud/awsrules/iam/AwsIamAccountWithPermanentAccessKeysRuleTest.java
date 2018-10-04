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
package com.tmobile.cloud.awsrules.iam;

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

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.BaseRule;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class,IAMUtils.class})
public class AwsIamAccountWithPermanentAccessKeysRuleTest {

    @InjectMocks
    AwsIamAccountWithPermanentAccessKeysRule awsIamAccountWithPermanentAccessKeysRule;
    
    
    @Mock
    AmazonIdentityManagementClient identityManagementClient;

    @Before
    public void setUp() throws Exception{
        identityManagementClient = PowerMockito.mock(AmazonIdentityManagementClient.class); 
    }
    @Test
    public void test()throws Exception{
        AccessKeyMetadata accessKeyMetadata = new AccessKeyMetadata();
        accessKeyMetadata.setAccessKeyId("123");
       
        List<AccessKeyMetadata> accessKeyMetadatas  = new ArrayList<>();
        accessKeyMetadatas.add(accessKeyMetadata);
        
        ListAccessKeysResult keysResult  = new ListAccessKeysResult();
        keysResult.setAccessKeyMetadata(accessKeyMetadatas);
        
        

        
        List<AccessKeyMetadata> emptyAccessKeyMetadatas  = new ArrayList<>();
        ListAccessKeysResult emptyKeysResult  = new ListAccessKeysResult();
        
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                true);
        
        
       
        
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("client", identityManagementClient);
        AwsIamAccountWithPermanentAccessKeysRule spy = Mockito.spy(new AwsIamAccountWithPermanentAccessKeysRule());
        
        Mockito.doReturn(map).when((BaseRule)spy).getClientFor(anyObject(), anyString(), anyObject());
        
        mockStatic(IAMUtils.class);
        when(IAMUtils.getAccessKeyInformationForUser(anyString(),anyObject())).thenReturn(accessKeyMetadatas);
        when(identityManagementClient.listAccessKeys(anyObject())).thenReturn(keysResult);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        
        when(IAMUtils.getAccessKeyInformationForUser(anyString(),anyObject())).thenReturn(emptyAccessKeyMetadatas);
        when(identityManagementClient.listAccessKeys(anyObject())).thenReturn(emptyKeysResult);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        
        
        spy.execute(CommonTestUtils.getMapString("svc_123 "),CommonTestUtils.getMapString("svc_123 "));
        when(identityManagementClient.listAccessKeys(anyObject())).thenThrow(new RuleExecutionFailedExeption());
        assertThatThrownBy( 
                () -> awsIamAccountWithPermanentAccessKeysRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> awsIamAccountWithPermanentAccessKeysRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
    }
  
    
    @Test
    public void getHelpTextTest(){
        assertThat(awsIamAccountWithPermanentAccessKeysRule.getHelpText(), is(notNullValue()));
    }

}
