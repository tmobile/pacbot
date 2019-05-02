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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.GetPolicyResult;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionResult;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.Policy;
import com.amazonaws.services.identitymanagement.model.PolicyVersion;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.BaseRule;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({URLDecoder.class, PacmanUtils.class,IAMUtils.class})
public class ServiceAccountPrivilegesRuleTest {

    @InjectMocks
    ServiceAccountPrivilegesRule serviceAccountPrivilegesRule;
    
    
    @Mock
    AmazonIdentityManagementClient identityManagementClient;

    @Before
    public void setUp() throws Exception{
        identityManagementClient = PowerMockito.mock(AmazonIdentityManagementClient.class); 
    }
    @Test
    public void test()throws Exception{
    	 AttachedPolicy attachedPolicies = new AttachedPolicy();
         attachedPolicies.setPolicyName("IAMFullAccess");
         List<AttachedPolicy> policies = new ArrayList<>();
         policies.add(attachedPolicies);
        
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                true);
        
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("client", identityManagementClient);
        ServiceAccountPrivilegesRule spy = Mockito.spy(new ServiceAccountPrivilegesRule());
        
        Mockito.doReturn(map).when((BaseRule)spy).getClientFor(anyObject(), anyString(), anyObject());
        
        mockStatic(IAMUtils.class);
        when(PacmanUtils.splitStringToAList(anyString(),anyString())).thenReturn(CommonTestUtils.getListString());
        when(IAMUtils.getAllowedActionsByUserPolicy(anyObject(),anyString())).thenReturn(CommonTestUtils.getSetString("svc_123"));
        spy.execute(CommonTestUtils.getMapString("svc_123 "),CommonTestUtils.getMapString("svc_123 "));
        
        spy.execute(CommonTestUtils.getMapString("svec_123 "),CommonTestUtils.getMapString("svec_123 "));
        
        
        when(IAMUtils.getAttachedPolicyOfIAMUser(anyString(),anyObject())).thenThrow(new RuleExecutionFailedExeption());
        assertThatThrownBy( 
                () -> serviceAccountPrivilegesRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> serviceAccountPrivilegesRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
    }
  
    
    @Test
    public void getHelpTextTest(){
        assertThat(serviceAccountPrivilegesRule.getHelpText(), is(notNullValue()));
    }

}
