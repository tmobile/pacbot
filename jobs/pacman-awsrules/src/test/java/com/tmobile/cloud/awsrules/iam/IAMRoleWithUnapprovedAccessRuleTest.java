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
 ******************************************************************************//*
package com.tmobile.cloud.awsrules.iam;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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

import com.amazonaws.auth.policy.Action;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.EC2Actions;
import com.amazonaws.auth.policy.actions.IdentityManagementActions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionRequest;
import com.amazonaws.services.identitymanagement.model.GetPolicyVersionResult;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListPolicyVersionsRequest;
import com.amazonaws.services.identitymanagement.model.ListPolicyVersionsResult;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.PolicyVersion;
import com.amazonaws.services.identitymanagement.model.Role;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cloud.awsrules.utils.CommonTestUtils;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PowerMockIgnore({ "javax.net.ssl.*", "javax.management.*", "org.slf4j.*", "org.apache.commons.logging.*", "ch.qos.*",
		"javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*" })

@RunWith(PowerMockRunner.class)
@PrepareForTest({ URLDecoder.class, PacmanUtils.class, IAMUtils.class })
public class IAMRoleWithUnapprovedAccessRuleTest {

	@InjectMocks
	IAMRoleWithUnapprovedAccessRule unapprovedAccessRule;

	@Mock
	AmazonIdentityManagementClient identityManagementClient;

	@Before
	public void setUp() throws Exception {
		identityManagementClient = PowerMockito.mock(AmazonIdentityManagementClient.class);
	}

	@Test
	public void test() throws Exception {
		AttachedPolicy attachedPolicies = new AttachedPolicy();
		attachedPolicies.setPolicyName("IAMFullAccess");
		List<AttachedPolicy> policies = new ArrayList<>();
		policies.add(attachedPolicies);

		mockStatic(PacmanUtils.class);
		when(PacmanUtils.doesAllHaveValue(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(true);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("client", identityManagementClient);
		IAMRoleWithUnapprovedAccessRule spy = Mockito.spy(new IAMRoleWithUnapprovedAccessRule());

		Mockito.doReturn(map).when((BaseRule) spy).getClientFor(anyObject(), anyString(), anyObject());

		mockStatic(IAMUtils.class);
		when(PacmanUtils.splitStringToAList(anyString(), anyString())).thenReturn(CommonTestUtils.getListString());
		when(IAMUtils.getAllowedActionsByRolePolicy(anyObject(), anyString()))
				.thenReturn(CommonTestUtils.getSetString("svc_123"));
		spy.execute(CommonTestUtils.getMapString("svc_123 "), CommonTestUtils.getMapString("svc_123 "));

		spy.execute(CommonTestUtils.getMapString("svec_123 "), CommonTestUtils.getMapString("svec_123 "));

		when(IAMUtils.getAllowedActionsByRolePolicy(anyObject(), anyString()))
				.thenThrow(new RuleExecutionFailedExeption());
		assertThatThrownBy(() -> unapprovedAccessRule.execute(CommonTestUtils.getMapString("r_123 "),
				CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);

		when(PacmanUtils.doesAllHaveValue(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(false);
		assertThatThrownBy(() -> unapprovedAccessRule.execute(CommonTestUtils.getMapString("r_123 "),
				CommonTestUtils.getMapString("r_123 "))).isInstanceOf(InvalidInputException.class);
	}

	@Mock
	MockAmazonIdentityManagementClient mockAmazonIdentityManagementClient = new MockAmazonIdentityManagementClient();

	@Test
	public void testIAMRoleWithoutUnapprovedActionsAccess()
			throws JsonParseException, JsonMappingException, IOException, UnableToCreateClientException {
		Policy policy = new Policy();
		List<Statement> statements = new ArrayList<Statement>();
		Statement statement = new Statement(Effect.Allow);
		List<Action> actions = new ArrayList<>();
		actions.add(IdentityManagementActions.AllIdentityManagementActions);
		actions.add(EC2Actions.RunInstances);
		statement.setActions(actions);
		statements.add(statement);
		policy.setStatements(statements);
		mockAmazonIdentityManagementClient.addAttachedRolePolicy("TestResourceID", policy);
		Policy policyOne = new Policy();
		statements = new ArrayList<Statement>();
		statement = new Statement(Effect.Allow);
		actions = new ArrayList<>();
		actions.add(EC2Actions.AllEC2Actions);
		statement.setActions(actions);
		statements.add(statement);
		policyOne.setStatements(statements);
		mockAmazonIdentityManagementClient.addInlineRolePolicy("TestResourceID", policyOne);
		Map<String, Object> clientMap = new HashMap<String, Object>();
		clientMap.put("client", mockAmazonIdentityManagementClient);
		IAMRoleWithUnapprovedAccessRule spyIAMRoleWithUnapprovedAccessRule = Mockito
				.spy(new IAMRoleWithUnapprovedAccessRule());
		Mockito.doReturn(clientMap).when((BaseRule) spyIAMRoleWithUnapprovedAccessRule).getClientFor(anyObject(),
				anyString(), anyObject());
		Map<String, String> ruleParam = new HashMap<>();
		ObjectMapper om = new ObjectMapper();
		Map<String, String> resourceAttributes = new HashMap<>();
		String resourceAttributesAsString = "{          \"_resourceid\": \"TestResourceID\",          \"discoverydate\": \"2017-10-11 14:00:00+00\",          \"_docid\": \"XXXX_TestResourceID\",          \"rolename\": \"TestResourceID\",          \"roleid\": \"TEST123456\",          \"firstdiscoveredon\": \"2017-10-11 14:00:00+00\",          \"description\": \"\",          \"createdate\": \"2017-10-11 14:11:41+00\",          \"accountid\": \"XXXXXXXX12\",          \"path\": \"/\",          \"accountname\": \"TEST\",          \"rolearn\": \"arn:aws:iam::XXXXXX12:role/TESTROLE\",          \"assumedpolicydoc\": \"\",          \"latest\": false        }";
		resourceAttributes = om.readValue(resourceAttributesAsString, Map.class);
		ruleParam.put(PacmanSdkConstants.Role_IDENTIFYING_STRING, "Test");// TODO : look into this
		ruleParam.put(PacmanSdkConstants.RESOURCE_ID, "TestResourceID");
		ruleParam.put(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS, "*");
		ruleParam.put(PacmanSdkConstants.SPLITTER_CHAR, ",");
		ruleParam.put(PacmanRuleConstants.SEVERITY, "high");
		ruleParam.put(PacmanRuleConstants.CATEGORY, "security");
		RuleResult ruleResult = spyIAMRoleWithUnapprovedAccessRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_SUCCESS));
		assertTrue(ruleResult.getDesc().equals(PacmanRuleConstants.SUCCESS_MESSAGE));
		ruleParam.put(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS, "iam:*");
		ruleResult = spyIAMRoleWithUnapprovedAccessRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_FAILURE));
		assertTrue(ruleResult.getDesc().equals(PacmanRuleConstants.FAILURE_MESSAGE));
		ruleParam.put(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS, "ec2:*");
		ruleResult = spyIAMRoleWithUnapprovedAccessRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_FAILURE));
		assertTrue(ruleResult.getDesc().equals(PacmanRuleConstants.FAILURE_MESSAGE));
	}

	class MockAmazonIdentityManagementClient extends AmazonIdentityManagementClient {
		List<Role> rolesList = new ArrayList<>();
		int attachedPolicyIDCounter = 0;
		int inlinePolicyIDCounter = 0;
		Map<String, List<Policy>> roleNameAttachedPoliciesMap = new HashMap<>();
		Map<String, List<Policy>> roleNameInlinePoliciesMap = new HashMap<>();
		Map<String, List<AttachedPolicy>> roleNameAttachedPoliciesModelMap = new HashMap<>();

		@Override
		public ListRolePoliciesResult listRolePolicies(ListRolePoliciesRequest request) {
			// TODO Auto-generated method stub
			List<String> policyNames = new ArrayList<>();
			ListRolePoliciesResult listRolePoliciesResult = new ListRolePoliciesResult();
			listRolePoliciesResult.setIsTruncated(false);
			List<Policy> policyList = roleNameInlinePoliciesMap.get(request.getRoleName());
			if (policyList != null) {
				for (Policy policy : roleNameInlinePoliciesMap.get(request.getRoleName())) {
					policyNames.add(policy.getId());
				}
			}
			listRolePoliciesResult.setPolicyNames(policyNames);
			return listRolePoliciesResult;
			// return super.listRolePolicies(request);
		}
		 @Override
		    public GetPolicyVersionResult getPolicyVersion(GetPolicyVersionRequest request) {
			 GetPolicyVersionResult getPolicyVersionResult = new GetPolicyVersionResult();
			 
			 String policyArn = request.getPolicyArn();
				String roleName = policyArn.split(":")[0];
				String policyId = policyArn.split(":")[1];
				String policyAsString = "";
				String encodedPolicy = "";
				List<Policy> policyList = roleNameAttachedPoliciesMap.get(roleName);
				for (Policy policy : policyList) {
					if (policy.getId().equals(policyId)) {
						try {
							policyAsString = policy.toJson();
							encodedPolicy = URLEncoder.encode(policyAsString, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				//List<PolicyVersion> versions = new ArrayList<>();
				PolicyVersion policyVersion = new PolicyVersion();
				policyVersion.setDocument(encodedPolicy);
				policyVersion.setCreateDate(new Date());
				policyVersion.setVersionId("1");
				policyVersion.setIsDefaultVersion(true);
				//versions.add(policyVersion);
				getPolicyVersionResult.setPolicyVersion(policyVersion);
				return getPolicyVersionResult;
			 
		 }
		void addAttachedRolePolicy(String roleName, Policy policy) {
			attachedPolicyIDCounter++;
			int id = attachedPolicyIDCounter;
			policy.setId(id + "");
			AttachedPolicy attachedPolicy = new AttachedPolicy();
			attachedPolicy.setPolicyArn(roleName + ":" + policy.getId());
			attachedPolicy.setPolicyName(policy.getId());
			List<AttachedPolicy> attachedPolicyList = roleNameAttachedPoliciesModelMap.get(roleName);
			if (attachedPolicyList == null) {
				attachedPolicyList = new ArrayList<>();
				roleNameAttachedPoliciesModelMap.put(roleName, attachedPolicyList);
			}
			attachedPolicyList.add(attachedPolicy);
			List<Policy> policyList = roleNameAttachedPoliciesMap.get(roleName);
			if (policyList == null) {
				policyList = new ArrayList<>();
				roleNameAttachedPoliciesMap.put(roleName, policyList);
			}
			policyList.add(policy);
		}

		void addInlineRolePolicy(String roleName, Policy policy) {
			inlinePolicyIDCounter++;
			int id = inlinePolicyIDCounter;
			policy.setId(id + "");
			List<Policy> policyList = roleNameInlinePoliciesMap.get(roleName);
			if (policyList == null) {
				policyList = new ArrayList<>();
				roleNameInlinePoliciesMap.put(roleName, policyList);
			}
			policyList.add(policy);
		}

		void setRoles(List<Role> rolesList) {
			this.rolesList = rolesList;
		}

		@Override
		public GetRoleResult getRole(GetRoleRequest request) {
			GetRoleResult getRoleResult = new GetRoleResult();
			for (Role role : rolesList) {
				if (request.getRoleName().equals(role.getRoleName())) {
					getRoleResult.setRole(role);
					break;
				}
			}
			return getRoleResult;
		}

		@Override
		public ListPolicyVersionsResult listPolicyVersions(ListPolicyVersionsRequest request) {
			ListPolicyVersionsResult listPolicyVersionsResult = new ListPolicyVersionsResult();
			String policyArn = request.getPolicyArn();
			String roleName = policyArn.split(":")[0];
			String policyId = policyArn.split(":")[1];
			String policyAsString = "";
			String encodedPolicy = "";
			List<Policy> policyList = roleNameAttachedPoliciesMap.get(roleName);
			for (Policy policy : policyList) {
				if (policy.getId().equals(policyId)) {
					try {
						policyAsString = policy.toJson();
						encodedPolicy = URLEncoder.encode(policyAsString, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			listPolicyVersionsResult.setIsTruncated(false);
			List<PolicyVersion> versions = new ArrayList<>();
			PolicyVersion policyVersion = new PolicyVersion();
			policyVersion.setDocument(encodedPolicy);
			policyVersion.setCreateDate(new Date());
			policyVersion.setVersionId("1");
			policyVersion.setIsDefaultVersion(true);
			versions.add(policyVersion);
			listPolicyVersionsResult.setVersions(versions);
			// return super.listPolicyVersions(request);
			return listPolicyVersionsResult;
		}

		@Override
		public GetRolePolicyResult getRolePolicy(GetRolePolicyRequest request) {
			GetRolePolicyResult getRolePolicyResult = new GetRolePolicyResult();
			List<Policy> policyList = roleNameInlinePoliciesMap.get(request.getRoleName());
			if (policyList != null) {
				for (Policy policy : policyList) {
					if (policy.getId().equals(request.getPolicyName())) {
						String policyAsString = "";
						String encodedPolicy = "";
						try {
							policyAsString = policy.toJson();
							encodedPolicy = URLEncoder.encode(policyAsString, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						getRolePolicyResult.setPolicyDocument(encodedPolicy);
						getRolePolicyResult.setPolicyName(request.getPolicyName());
						getRolePolicyResult.setRoleName(request.getRoleName());
					}
				}
			}
			return getRolePolicyResult;
		}

		@Override
		public ListAttachedRolePoliciesResult listAttachedRolePolicies(ListAttachedRolePoliciesRequest request) {
			ListAttachedRolePoliciesResult listAttachedRolePoliciesResult = new ListAttachedRolePoliciesResult();
			listAttachedRolePoliciesResult
					.setAttachedPolicies(roleNameAttachedPoliciesModelMap.get(request.getRoleName()));
			return listAttachedRolePoliciesResult;
		}
	}

	@Test
	public void getHelpTextTest() {
		assertThat(unapprovedAccessRule.getHelpText(), is(notNullValue()));
	}

}
*/