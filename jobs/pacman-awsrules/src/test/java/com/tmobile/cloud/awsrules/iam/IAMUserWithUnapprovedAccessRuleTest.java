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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

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
import com.amazonaws.services.identitymanagement.model.GetUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetUserPolicyResult;
import com.amazonaws.services.identitymanagement.model.GetUserRequest;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListPolicyVersionsRequest;
import com.amazonaws.services.identitymanagement.model.ListPolicyVersionsResult;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.PolicyVersion;
import com.amazonaws.services.identitymanagement.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cloud.awsrules.utils.IAMUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PowerMockIgnore({ "javax.net.ssl.*", "javax.management.*", "org.slf4j.*", "org.apache.commons.logging.*", "ch.qos.*",
		"javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*" })

@RunWith(PowerMockRunner.class)
@PrepareForTest({ URLDecoder.class, PacmanUtils.class, IAMUtils.class })
public class IAMUserWithUnapprovedAccessRuleTest {

	@InjectMocks
	IAMUserWithUnapprovedAccessRule unapprovedAccessRule;

	@Mock
	AmazonIdentityManagementClient identityManagementClient;

	@Before
	public void setUp() throws Exception {
		identityManagementClient = PowerMockito.mock(AmazonIdentityManagementClient.class);
	}

	@Mock
	MockAmazonIdentityManagementClient mockAmazonIdentityManagementClient = new MockAmazonIdentityManagementClient();

	@Test
	public void testIAMUserWithoutUnapprovedActionsAccess()
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
		mockAmazonIdentityManagementClient.addAttachedUserPolicy("TestResourceID", policy);
		Policy policyOne = new Policy();
		statements = new ArrayList<Statement>();
		statement = new Statement(Effect.Allow);
		actions = new ArrayList<>();
		actions.add(EC2Actions.AllEC2Actions);
		statement.setActions(actions);
		statements.add(statement);
		policyOne.setStatements(statements);
		mockAmazonIdentityManagementClient.addInlineUserPolicy("TestResourceID", policyOne);
		Map<String, Object> clientMap = new HashMap<String, Object>();
		clientMap.put("client", mockAmazonIdentityManagementClient);
		IAMUserWithUnapprovedAccessRule spyIAMUserWithUnapprovedAccessRule = Mockito
				.spy(new IAMUserWithUnapprovedAccessRule());
		Mockito.doReturn(clientMap).when((BaseRule) spyIAMUserWithUnapprovedAccessRule).getClientFor(anyObject(),
				anyString(), anyObject());
		Map<String, String> ruleParam = new HashMap<>();
		ObjectMapper om = new ObjectMapper();
		Map<String, String> resourceAttributes = new HashMap<>();
		String resourceAttributesAsString = "{          \"_resourceid\": \"TestResourceID\",          \"discoverydate\": \"2017-10-11 14:00:00+00\",          \"_docid\": \"XXXX_TestResourceID\",          \"username\": \"TestResourceID\",          \"userid\": \"TEST123456\",          \"firstdiscoveredon\": \"2017-10-11 14:00:00+00\",          \"description\": \"\",          \"createdate\": \"2017-10-11 14:11:41+00\",          \"accountid\": \"XXXXXXXX12\",          \"path\": \"/\",          \"accountname\": \"TEST\",          \"userarn\": \"arn:aws:iam::XXXXXX12:user/TESTROLE\",          \"assumedpolicydoc\": \"\",          \"latest\": false        }";
		resourceAttributes = om.readValue(resourceAttributesAsString, Map.class);
		ruleParam.put(PacmanSdkConstants.Role_IDENTIFYING_STRING, "Test");// TODO : look into this
		ruleParam.put(PacmanSdkConstants.RESOURCE_ID, "TestResourceID");
		ruleParam.put(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS, "*");
		ruleParam.put(PacmanSdkConstants.SPLITTER_CHAR, ",");
		ruleParam.put(PacmanRuleConstants.SEVERITY, "high");
		ruleParam.put(PacmanRuleConstants.CATEGORY, "security");
		RuleResult ruleResult = spyIAMUserWithUnapprovedAccessRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_SUCCESS));
		assertTrue(ruleResult.getDesc().equals(PacmanRuleConstants.SUCCESS_MESSAGE));
		ruleParam.put(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS, "iam:*");
		ruleResult = spyIAMUserWithUnapprovedAccessRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_FAILURE));
		assertTrue(ruleResult.getDesc().equals(PacmanRuleConstants.FAILURE_MESSAGE));
		ruleParam.put(PacmanRuleConstants.UNAPPROVED_IAM_ACTIONS, "ec2:*");
		ruleResult = spyIAMUserWithUnapprovedAccessRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_FAILURE));
		assertTrue(ruleResult.getDesc().equals(PacmanRuleConstants.FAILURE_MESSAGE));
	}

	class MockAmazonIdentityManagementClient extends AmazonIdentityManagementClient {
		List<User> usersList = new ArrayList<>();
		int attachedPolicyIDCounter = 0;
		int inlinePolicyIDCounter = 0;
		Map<String, List<Policy>> userNameAttachedPoliciesMap = new HashMap<>();
		Map<String, List<Policy>> userNameInlinePoliciesMap = new HashMap<>();
		Map<String, List<AttachedPolicy>> userNameAttachedPoliciesModelMap = new HashMap<>();

		@Override
		public ListUserPoliciesResult listUserPolicies(ListUserPoliciesRequest request) {
			// TODO Auto-generated method stub
			List<String> policyNames = new ArrayList<>();
			ListUserPoliciesResult listUserPoliciesResult = new ListUserPoliciesResult();
			listUserPoliciesResult.setIsTruncated(false);
			List<Policy> policyList = userNameInlinePoliciesMap.get(request.getUserName());
			if (policyList != null) {
				for (Policy policy : userNameInlinePoliciesMap.get(request.getUserName())) {
					policyNames.add(policy.getId());
				}
			}
			listUserPoliciesResult.setPolicyNames(policyNames);
			return listUserPoliciesResult;
			// return super.listUserPolicies(request);
		}

		void addAttachedUserPolicy(String userName, Policy policy) {
			attachedPolicyIDCounter++;
			int id = attachedPolicyIDCounter;
			policy.setId(id + "");
			AttachedPolicy attachedPolicy = new AttachedPolicy();
			attachedPolicy.setPolicyArn(userName + ":" + policy.getId());
			attachedPolicy.setPolicyName(policy.getId());
			List<AttachedPolicy> attachedPolicyList = userNameAttachedPoliciesModelMap.get(userName);
			if (attachedPolicyList == null) {
				attachedPolicyList = new ArrayList<>();
				userNameAttachedPoliciesModelMap.put(userName, attachedPolicyList);
			}
			attachedPolicyList.add(attachedPolicy);
			List<Policy> policyList = userNameAttachedPoliciesMap.get(userName);
			if (policyList == null) {
				policyList = new ArrayList<>();
				userNameAttachedPoliciesMap.put(userName, policyList);
			}
			policyList.add(policy);
		}

		void addInlineUserPolicy(String userName, Policy policy) {
			inlinePolicyIDCounter++;
			int id = inlinePolicyIDCounter;
			policy.setId(id + "");
			List<Policy> policyList = userNameInlinePoliciesMap.get(userName);
			if (policyList == null) {
				policyList = new ArrayList<>();
				userNameInlinePoliciesMap.put(userName, policyList);
			}
			policyList.add(policy);
		}

		void setUsers(List<User> usersList) {
			this.usersList = usersList;
		}

		@Override
		public GetUserResult getUser(GetUserRequest request) {
			GetUserResult getUserResult = new GetUserResult();
			for (User user : usersList) {
				if (request.getUserName().equals(user.getUserName())) {
					getUserResult.setUser(user);
					break;
				}
			}
			return getUserResult;
		}

		@Override
		public ListPolicyVersionsResult listPolicyVersions(ListPolicyVersionsRequest request) {
			ListPolicyVersionsResult listPolicyVersionsResult = new ListPolicyVersionsResult();
			String policyArn = request.getPolicyArn();
			String userName = policyArn.split(":")[0];
			String policyId = policyArn.split(":")[1];
			String policyAsString = "";
			String encodedPolicy = "";
			List<Policy> policyList = userNameAttachedPoliciesMap.get(userName);
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
		public GetPolicyVersionResult getPolicyVersion(GetPolicyVersionRequest request) {
			GetPolicyVersionResult getPolicyVersionResult = new GetPolicyVersionResult();

			String policyArn = request.getPolicyArn();
			String userName = policyArn.split(":")[0];
			String policyId = policyArn.split(":")[1];
			String policyAsString = "";
			String encodedPolicy = "";
			List<Policy> policyList = userNameAttachedPoliciesMap.get(userName);
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
			// List<PolicyVersion> versions = new ArrayList<>();
			PolicyVersion policyVersion = new PolicyVersion();
			policyVersion.setDocument(encodedPolicy);
			policyVersion.setCreateDate(new Date());
			policyVersion.setVersionId("1");
			policyVersion.setIsDefaultVersion(true);
			// versions.add(policyVersion);
			getPolicyVersionResult.setPolicyVersion(policyVersion);
			return getPolicyVersionResult;

		}

		@Override
		public GetUserPolicyResult getUserPolicy(GetUserPolicyRequest request) {
			GetUserPolicyResult getUserPolicyResult = new GetUserPolicyResult();
			List<Policy> policyList = userNameInlinePoliciesMap.get(request.getUserName());
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
						getUserPolicyResult.setPolicyDocument(encodedPolicy);
						getUserPolicyResult.setPolicyName(request.getPolicyName());
						getUserPolicyResult.setUserName(request.getUserName());
					}
				}
			}
			return getUserPolicyResult;
		}

		@Override
		public ListAttachedUserPoliciesResult listAttachedUserPolicies(ListAttachedUserPoliciesRequest request) {
			ListAttachedUserPoliciesResult listAttachedUserPoliciesResult = new ListAttachedUserPoliciesResult();
			listAttachedUserPoliciesResult
					.setAttachedPolicies(userNameAttachedPoliciesModelMap.get(request.getUserName()));
			return listAttachedUserPoliciesResult;
		}
	}

	@Test
	public void getHelpTextTest() {
		assertThat(unapprovedAccessRule.getHelpText(), is(notNullValue()));
	}

}
*/