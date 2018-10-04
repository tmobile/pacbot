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
package com.tmobile.pacman.api.admin.controller;

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.CreatePolicyDetails;
import com.tmobile.pacman.api.admin.domain.UpdatePolicyDetails;
import com.tmobile.pacman.api.admin.repository.model.Policy;
import com.tmobile.pacman.api.admin.repository.service.PolicyService;

@RunWith(MockitoJUnitRunner.class)
public class PolicyControllerTest
{
	private MockMvc mockMvc;

	@Mock
	private PolicyService policyService;

	@InjectMocks
	private PolicyController policyController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(policyController)
				/* .addFilters(new CORSFilter()) */
				.build();
	}
	
	@Test
	public void getPoliciesTest() throws Exception {
		Object[] policy = {"PolicyName", "PolicyDesc"};
		List<Object[]> policies = Lists.newArrayList();
		policies.add(policy);
		Page<Object[]> allPolicies = new PageImpl<Object[]>(policies, new PageRequest(0, 1), policies.size());
		
		when(policyService.getPolicies(anyInt(), anyInt(), anyString())).thenReturn(allPolicies);
		mockMvc.perform(get("/policy/list")
				.param("page", "0")
				.param("size", "1")
				.param("searchTerm", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getPoliciesExceptionTest() throws Exception {
		when(policyService.getPolicies(anyInt(), anyInt(), anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/policy/list")
				.param("page", "0")
				.param("size", "1")
				.param("searchTerm", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getByPolicyIdTest() throws Exception {
		when(policyService.getByPolicyId(anyString())).thenReturn(getPolicyDetails());
		mockMvc.perform(get("/policy/details-by-id")
				.param("policyId", "policyId123"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data.policyId", is("policyId123")))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getByPolicyIdExceptionTest() throws Exception {
		when(policyService.getByPolicyId(anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/policy/details-by-id")
				.param("policyId", "policyId123"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getAllPolicyIdsTest() throws Exception {
		when(policyService.getAllPolicyIds()).thenReturn(getAllPolicyIdsResponse());
		mockMvc.perform(get("/policy/list-ids"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data[0]", is("policyId123")))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllPolicyIdsExceptionTest() throws Exception {
		when(policyService.getAllPolicyIds()).thenThrow(Exception.class);
		mockMvc.perform(get("/policy/list-ids"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void updatePoliciesTest() throws Exception {
		byte[] updatePolicyDetailsContent = toJson(getUpdatePolicyDetails());
		when(policyService.updatePolicies(any())).thenReturn("Updated Successfully");
		mockMvc.perform(post("/policy/update")
				.content(updatePolicyDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is("Updated Successfully")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updatePoliciesExceptionTest() throws Exception {
		byte[] targetTypesDetailsContent = toJson(getUpdatePolicyDetails());
		when(policyService.updatePolicies(any())).thenThrow(Exception.class);
		mockMvc.perform(post("/policy/update")
				.content(targetTypesDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void createPoliciesTest() throws Exception {
		byte[] updatePolicyDetailsContent = toJson(getCreatePolicyDetails());
		when(policyService.createPolicies(any())).thenReturn("Created Successfully");
		mockMvc.perform(post("/policy/create")
				.content(updatePolicyDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is("Created Successfully")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createPoliciesExceptionTest() throws Exception {
		byte[] targetTypesDetailsContent = toJson(getCreatePolicyDetails());
		when(policyService.createPolicies(any())).thenThrow(Exception.class);
		mockMvc.perform(post("/policy/create")
				.content(targetTypesDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	private Policy getPolicyDetails() {
		Policy policy = new Policy();
		policy.setCreatedDate(new Date());
		policy.setModifiedDate(new Date());
		policy.setPolicyDesc("policyDesc123");
		policy.setPolicyId("policyId123");
		policy.setPolicyName("policyName123");
		policy.setPolicyUrl("policyUrl123");
		policy.setPolicyVersion("policyVersion123");
		policy.setResolution("resolution123");
		policy.setStatus("status123");
		policy.setUserId(123);
		return policy;
	}

	private Collection<String> getAllPolicyIdsResponse() {
		Collection<String> allPolicyIds = Lists.newArrayList();
		allPolicyIds.add("policyId123");
		allPolicyIds.add("policyId234");
		return allPolicyIds;
	}

	private UpdatePolicyDetails getUpdatePolicyDetails() {
		UpdatePolicyDetails updatePolicyDetails = new UpdatePolicyDetails();
		updatePolicyDetails.setPolicyDesc("policyDesc123");
		updatePolicyDetails.setPolicyId("policyId123");
		updatePolicyDetails.setPolicyUrl("policyUrl123");
		updatePolicyDetails.setPolicyVersion("policyVersion123");
		updatePolicyDetails.setResolution("resolution123");
		return updatePolicyDetails;
	}
	
	private CreatePolicyDetails getCreatePolicyDetails() {
		CreatePolicyDetails createPolicyDetails = new CreatePolicyDetails();
		createPolicyDetails.setPolicyDesc("policyDesc123");
		createPolicyDetails.setPolicyId("policyId123");
		createPolicyDetails.setPolicyUrl("policyUrl123");
		createPolicyDetails.setPolicyVersion("policyVersion123");
		createPolicyDetails.setResolution("resolution123");
		createPolicyDetails.setPolicyName("policyName123");
		createPolicyDetails.setStatus("status123");
		return createPolicyDetails;
	}
	
	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}

