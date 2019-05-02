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
package com.tmobile.cloud.awsrules.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionResult;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.GetUserPolicyResult;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListPolicyVersionsResult;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.PolicyVersion;

@RunWith(PowerMockRunner.class)
@PrepareForTest({URLDecoder.class, PacmanUtils.class})
public class IAMUtilsTest {

    @InjectMocks
    IAMUtils iamUtils;
    
    @Mock
    AmazonIdentityManagementClient iamClient;
    
    @Before
    public void setUp() throws Exception{
        iamClient = PowerMockito.mock(AmazonIdentityManagementClient.class); 
    }
 
    @SuppressWarnings("static-access")
    @Test
    public void getAccessKeyInformationForUserTest() throws Exception {
        
        ListAccessKeysResult keysResult = new ListAccessKeysResult();
        keysResult.setIsTruncated(false);
        
        when(iamClient.listAccessKeys(anyObject())).thenReturn(keysResult);
        assertThat(iamUtils.getAccessKeyInformationForUser("user",iamClient),is(notNullValue()));
        
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getAttachedPolicyOfIAMUserTest() throws Exception {
        
    	ListAttachedUserPoliciesResult policiesResult = new ListAttachedUserPoliciesResult();
        
        when(iamClient.listAttachedUserPolicies(anyObject())).thenReturn(policiesResult);
        assertThat(iamUtils.getAttachedPolicyOfIAMUser("user",iamClient),is(notNullValue()));
        
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getActionListByPolicyTest() throws Exception {
    	 AttachedPolicy attachedPolicies = new AttachedPolicy();
         attachedPolicies.setPolicyName("IAMFullAccess");
         List<AttachedPolicy> policies = new ArrayList<>();
         policies.add(attachedPolicies);
         
         PolicyVersion versions = new PolicyVersion();
         versions.setIsDefaultVersion(true);
         versions.setVersionId("123");
         versions.setDocument("{\"ag\":\"aws-all\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"iam:*\"],\"Resource\":[\"iam:*\"]}],\"from\":0,\"searchtext\":\"\",\"size\":25}");
        ListPolicyVersionsResult policyVersions = new ListPolicyVersionsResult();
        policyVersions.setVersions(Arrays.asList(versions));
        
        
        ListAttachedUserPoliciesResult attachedUserPoliciesResult = new ListAttachedUserPoliciesResult();
        attachedUserPoliciesResult.setAttachedPolicies(policies);
        attachedUserPoliciesResult.setIsTruncated(false);
        
        ListUserPoliciesResult listUserPoliciesResult = new ListUserPoliciesResult();
        listUserPoliciesResult.setPolicyNames(Arrays.asList("123"));
        listUserPoliciesResult.setIsTruncated(false);
        
        GetUserPolicyResult policyResult = new GetUserPolicyResult();
        
        policyResult.setPolicyName("123");
        policyResult.setPolicyDocument("{\"ag\":\"aws-all\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"iam:*\"],\"Resource\":[\"iam:*\"]}],\"from\":0,\"searchtext\":\"\",\"size\":25}");
        policyResult.setUserName("123");
         
        GetPolicyVersionResult versionResult = new GetPolicyVersionResult();
        versionResult.setPolicyVersion(versions);
        when(iamClient.listAttachedUserPolicies(anyObject())).thenReturn(attachedUserPoliciesResult);
        when(iamClient.listUserPolicies(anyObject())).thenReturn(listUserPoliciesResult);
        when(iamClient.getUserPolicy(anyObject())).thenReturn(policyResult);
        when(iamClient.listPolicyVersions(anyObject())).thenReturn(policyVersions);
        when(iamClient.getPolicyVersion(anyObject())).thenReturn(versionResult);
        mockStatic(URLDecoder.class);
        when(URLDecoder.decode(anyString(),anyString())).thenReturn("qeqwehgj");
        assertThat(iamUtils.getAllowedActionsByUserPolicy(iamClient,"133"),is(notNullValue()));
        
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void getActionsByRolePolicyTest() throws Exception {
    	 AttachedPolicy attachedPolicies = new AttachedPolicy();
         attachedPolicies.setPolicyName("IAMFullAccess");
         List<AttachedPolicy> policies = new ArrayList<>();
         policies.add(attachedPolicies);
         
         PolicyVersion versions = new PolicyVersion();
         versions.setIsDefaultVersion(true);
         versions.setVersionId("123");
         versions.setDocument("{\"ag\":\"aws-all\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"iam:*\"],\"Resource\":[\"iam:*\"]}],\"from\":0,\"searchtext\":\"\",\"size\":25}");
        ListPolicyVersionsResult policyVersions = new ListPolicyVersionsResult();
        policyVersions.setVersions(Arrays.asList(versions));
        
        
        ListAttachedRolePoliciesResult attachedRolePoliciesResult = new ListAttachedRolePoliciesResult();
        attachedRolePoliciesResult.setAttachedPolicies(policies);
        attachedRolePoliciesResult.setIsTruncated(false);
        
        ListRolePoliciesResult rolePoliciesResult = new ListRolePoliciesResult();
        rolePoliciesResult.setPolicyNames(Arrays.asList("123"));
        rolePoliciesResult.setIsTruncated(false);
        
        GetRolePolicyResult policyResult = new GetRolePolicyResult();
        
        policyResult.setPolicyName("123");
        policyResult.setPolicyDocument("{\"ag\":\"aws-all\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"iam:*\"],\"Resource\":[\"iam:*\"]}],\"from\":0,\"searchtext\":\"\",\"size\":25}");
        policyResult.setRoleName("123");
         
        GetPolicyVersionResult versionResult = new GetPolicyVersionResult();
        versionResult.setPolicyVersion(versions);
        when(iamClient.listAttachedRolePolicies(anyObject())).thenReturn(attachedRolePoliciesResult);
        when(iamClient.listRolePolicies(anyObject())).thenReturn(rolePoliciesResult);
        when(iamClient.getRolePolicy(anyObject())).thenReturn(policyResult);
        when(iamClient.listPolicyVersions(anyObject())).thenReturn(policyVersions);
        when(iamClient.getPolicyVersion(anyObject())).thenReturn(versionResult);
        mockStatic(URLDecoder.class);
        when(URLDecoder.decode(anyString(),anyString())).thenReturn("qeqwehgj");
        assertThat(iamUtils.getAllowedActionsByRolePolicy(iamClient,"133"),is(notNullValue()));
        
    }
    
}
