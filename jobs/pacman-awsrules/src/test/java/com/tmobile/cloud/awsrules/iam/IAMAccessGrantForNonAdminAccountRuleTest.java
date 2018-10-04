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
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.GetPolicyResult;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.Policy;
import com.amazonaws.services.identitymanagement.model.PolicyVersion;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.BaseRule;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({URLDecoder.class, PacmanUtils.class,IAMUtils.class})
public class IAMAccessGrantForNonAdminAccountRuleTest {

    @InjectMocks
    IAMAccessGrantForNonAdminAccountRule iamAccessGrantForNonAdminAccountRule;
    
    
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
        ListAttachedRolePoliciesResult result  = new ListAttachedRolePoliciesResult();
        result.setAttachedPolicies(policies);
        result.setIsTruncated(false);
        
        
        AttachedPolicy attachedPolicies1 = new AttachedPolicy();
        attachedPolicies1.setPolicyName("AdministratorAccess");
        List<AttachedPolicy> policies1 = new ArrayList<>();
        policies1.add(attachedPolicies1);
        ListAttachedRolePoliciesResult result1  = new ListAttachedRolePoliciesResult();
        result1.setAttachedPolicies(policies1);
        result1.setIsTruncated(false);
        
        AttachedPolicy attachedPolicies2 = new AttachedPolicy();
        attachedPolicies2.setPolicyArn("123");
        List<AttachedPolicy> policies2 = new ArrayList<>();
        policies2.add(attachedPolicies2);
        ListAttachedRolePoliciesResult result2  = new ListAttachedRolePoliciesResult();
        result2.setAttachedPolicies(policies2);
        result2.setIsTruncated(false);
        
        
        List<AttachedPolicy> policies3 = new ArrayList<>();
        ListAttachedRolePoliciesResult result3  = new ListAttachedRolePoliciesResult();
        result3.setAttachedPolicies(policies3);
        result3.setIsTruncated(false);
        
        Policy policy = new Policy();
        policy.setPolicyId("policyId");
        
        
        
        GetPolicyResult policyResult = new GetPolicyResult();
        policyResult.setPolicy(policy);
        
        
        PolicyVersion policyVersion = new PolicyVersion();
        policyVersion.setVersionId("versionId");
        policyVersion.setIsDefaultVersion(true);
        policyVersion.setDocument("{\"ag\":\"aws-all\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"iam:*\"]}],\"from\":0,\"searchtext\":\"\",\"size\":25}");
        
        GetPolicyVersionResult versionResult = new GetPolicyVersionResult();
        versionResult.setPolicyVersion(policyVersion);
        
        
        GetPolicyResult policyResult1 = new GetPolicyResult();
        policyResult1.setPolicy(policy);
        
        
        PolicyVersion policyVersion1 = new PolicyVersion();
        policyVersion1.setVersionId("versionId");
        policyVersion1.setIsDefaultVersion(true);
        policyVersion1.setDocument("{\"ag\":\"aws-all\",\"Statement\":{\"Effect\":\"Allow\",\"Action\":[\"iam:*\"]},\"from\":0,\"searchtext\":\"\",\"size\":25}");
        
        GetPolicyVersionResult versionResult1 = new GetPolicyVersionResult();
        versionResult1.setPolicyVersion(policyVersion1);
        
        mockStatic(PacmanUtils.class);
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                true);
        
        
        when(PacmanUtils.splitStringToAList(anyString(),anyString())).thenReturn(CommonTestUtils.getListString());
        
        Map<String,Object>map=new HashMap<String, Object>();
        map.put("client", identityManagementClient);
        IAMAccessGrantForNonAdminAccountRule spy = Mockito.spy(new IAMAccessGrantForNonAdminAccountRule());
        
        Mockito.doReturn(map).when((BaseRule)spy).getClientFor(anyObject(), anyString(), anyObject());
        
        when(identityManagementClient.getPolicy(anyObject())).thenReturn(policyResult);
        when(identityManagementClient.listAttachedRolePolicies(anyObject())).thenReturn(result);
        when(identityManagementClient.getPolicyVersion(anyObject())).thenReturn(versionResult1);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(identityManagementClient.getPolicy(anyObject())).thenReturn(policyResult);
        when(identityManagementClient.listAttachedRolePolicies(anyObject())).thenReturn(result);
        when(identityManagementClient.getPolicyVersion(anyObject())).thenReturn(versionResult);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(identityManagementClient.listAttachedRolePolicies(anyObject())).thenReturn(result1);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(identityManagementClient.listAttachedRolePolicies(anyObject())).thenReturn(result2);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(identityManagementClient.listAttachedRolePolicies(anyObject())).thenReturn(result3);
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        spy.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getAnotherMapString("r_123 "));
        
        mockStatic(URLDecoder.class);
        when(URLDecoder.decode(anyString(),anyString())).thenThrow(new UnsupportedEncodingException());
        assertThatThrownBy( 
                () -> iamAccessGrantForNonAdminAccountRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
        
        
        when(PacmanUtils.doesAllHaveValue(anyString(),anyString(),anyString())).thenReturn(
                false);
        assertThatThrownBy(
                () -> iamAccessGrantForNonAdminAccountRule.execute(CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
    }
  
    
    @Test
    public void getHelpTextTest(){
        assertThat(iamAccessGrantForNonAdminAccountRule.getHelpText(), is(notNullValue()));
    }

}
