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
package com.tmobile.pacman.api.admin.repository.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.CreatePolicyDetails;
import com.tmobile.pacman.api.admin.domain.UpdatePolicyDetails;
import com.tmobile.pacman.api.admin.repository.PolicyRepository;
import com.tmobile.pacman.api.admin.repository.model.Policy;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceImplTest {

	@InjectMocks
	private PolicyServiceImpl policyService;

	@Mock
	private PacmanConfiguration config ;
	
	@Mock
	private PolicyRepository policyRepository;

	@Test
	public void getPoliciesTest() {
		List<Object[]> policiesDetails = new ArrayList<Object[]>();
		Object[] policiesDetail = {"PolicyName123", "policyDesc123"};
		policiesDetails.add(policiesDetail);
		Page<Object[]> allPoliciesDetails = new PageImpl<Object[]>(policiesDetails,new PageRequest(0, 1), policiesDetails.size());
		when(policyService.getPolicies(0,  1, StringUtils.EMPTY)).thenReturn(allPoliciesDetails);
		assertThat(policyRepository.getAllPolicyDetails(StringUtils.EMPTY, new PageRequest(0, 1)).getContent().get(0).length, is(2));
	}
	
	@Test
	public void getAllPolicyIdsTest() {
		Collection<String> policyIdDetails = Lists.newArrayList();
		policyIdDetails.add("PolicyID123");
		policyIdDetails.add("PolicyID234");
		policyIdDetails.add("PolicyID345");
		when(policyService.getAllPolicyIds()).thenReturn(policyIdDetails);
		assertThat(policyRepository.getAllPolicyIds().size(), is(3));
	}
	
	@Test
	public void createPoliciesTest() throws Exception {
		CreatePolicyDetails policyDetails = getCreatePolicyDetails();
		Policy newPolicy = getPolicy();
		when(policyRepository.existsById(policyDetails.getPolicyId())).thenReturn(false);
		when(policyRepository.save(newPolicy)).thenReturn(newPolicy);
		assertThat(policyService.createPolicies(policyDetails), is("Created Successfully"));
	}
	
	@Test
	public void createPoliciesNotFoundTest() throws Exception {
		CreatePolicyDetails policyDetails = getCreatePolicyDetails();
		when(policyRepository.existsById(policyDetails.getPolicyId())).thenReturn(true);
		assertThat(policyService.createPolicies(policyDetails), is("Policy already exits!!"));
	}

	@Test
	public void updatePoliciesTest() throws Exception {
		UpdatePolicyDetails policiesDetails = getUpdatePolicyDetails();
		Optional<Policy> policyToUpdate = Optional.of(getPolicy());
		when(policyRepository.existsById(policiesDetails.getPolicyId())).thenReturn(true);
		when(policyRepository.findById(policiesDetails.getPolicyId())).thenReturn(policyToUpdate);
		when(policyRepository.save(policyToUpdate.get())).thenReturn(policyToUpdate.get());
		assertThat(policyService.updatePolicies(policiesDetails), is("Updated Successfully"));
	}
	
	@Test
	public void updatePoliciesNotFoundTest() throws Exception {
		UpdatePolicyDetails policiesDetails = getUpdatePolicyDetails();
		when(policyRepository.existsById(policiesDetails.getPolicyId())).thenReturn(false);
		assertThat(policyService.updatePolicies(policiesDetails), is("Policy does not exits!!"));
	}

	@Test
	public void getByPolicyIdTest() {
		Policy policy = getPolicy();
		when(policyRepository.findByPolicyIdIgnoreCase("policyId123")).thenReturn(policy);
		assertThat(policyService.getByPolicyId("policyId123"), is(policy));
	}
	
	private CreatePolicyDetails getCreatePolicyDetails() {
		CreatePolicyDetails createPolicyDetails = new CreatePolicyDetails();
		createPolicyDetails.setPolicyId("policyId123");
		createPolicyDetails.setPolicyName("policyName123");
		createPolicyDetails.setPolicyDesc("policyDesc123");
		createPolicyDetails.setPolicyUrl("policyUrl123");
		createPolicyDetails.setPolicyVersion("policyVersion123");		
		return createPolicyDetails;
	}

	private Policy getPolicy() {
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

	private UpdatePolicyDetails getUpdatePolicyDetails() {
		UpdatePolicyDetails updatePolicyDetails = new UpdatePolicyDetails();
		updatePolicyDetails.setPolicyDesc("policyDesc123");
		updatePolicyDetails.setPolicyId("policyId123");
		updatePolicyDetails.setPolicyUrl("policyUrl123");
		updatePolicyDetails.setPolicyVersion("policyVersion123");
		updatePolicyDetails.setResolution("resolution123");
		return updatePolicyDetails;
	}
}
