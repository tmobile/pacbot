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

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.admin.domain.CreatePolicyDetails;
import com.tmobile.pacman.api.admin.domain.UpdatePolicyDetails;
import com.tmobile.pacman.api.admin.repository.PolicyRepository;
import com.tmobile.pacman.api.admin.repository.model.Policy;
import com.tmobile.pacman.api.commons.Constants;

/**
 * Policy Service Implementations
 */
@Service
public class PolicyServiceImpl implements PolicyService, Constants {

	@Autowired
	private PolicyRepository policyRepository;

	@Override
	public Page<Object[]> getPolicies(final int page, final int size, final String searchTerm) {
		return policyRepository.getAllPolicyDetails(searchTerm.toLowerCase(), PageRequest.of(page, size));
	}

	@Override
	public Collection<String> getAllPolicyIds() {
		return policyRepository.getAllPolicyIds();
	}

	@Override
	public String updatePolicies(final UpdatePolicyDetails policyDetails) throws Exception {
		boolean isPolicyExits = policyRepository.existsById(policyDetails.getPolicyId());
		if(isPolicyExits) {
			Policy policyToUpdate = policyRepository.findById(policyDetails.getPolicyId()).get();
			policyToUpdate.setPolicyDesc(policyDetails.getPolicyDesc());
			policyToUpdate.setPolicyUrl(policyDetails.getPolicyUrl());
			policyToUpdate.setPolicyVersion(policyDetails.getPolicyVersion());
			policyToUpdate.setResolution(policyDetails.getResolution());
			policyToUpdate.setModifiedDate(new Date());
			policyRepository.save(policyToUpdate);
			return "Updated Successfully";
		} else {
			return "Policy does not exits!!";
		}
	}

	@Override
	public String createPolicies(final CreatePolicyDetails policyDetails) throws Exception {
		boolean isPolicyExits = policyRepository.existsById(policyDetails.getPolicyId());
		if(!isPolicyExits) {
			Date date = new Date();
			Policy newPolicy = new Policy();
			newPolicy.setPolicyId(policyDetails.getPolicyId());
			newPolicy.setPolicyName(policyDetails.getPolicyName());
			newPolicy.setPolicyDesc(policyDetails.getPolicyDesc());
			newPolicy.setPolicyUrl(policyDetails.getPolicyUrl());
			newPolicy.setPolicyVersion(policyDetails.getPolicyVersion());
			newPolicy.setResolution(policyDetails.getResolution());
			newPolicy.setCreatedDate(date);
			newPolicy.setModifiedDate(date);
			policyRepository.save(newPolicy);
			return "Created Successfully";
		} else {
			return "Policy already exits!!";
		}
	}

	@Override
	public Policy getByPolicyId(String policyId) {
		return policyRepository.findByPolicyIdIgnoreCase(policyId);
	}
}
