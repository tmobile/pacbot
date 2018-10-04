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

import org.springframework.data.domain.Page;

import com.tmobile.pacman.api.admin.domain.CreatePolicyDetails;
import com.tmobile.pacman.api.admin.domain.UpdatePolicyDetails;
import com.tmobile.pacman.api.admin.repository.model.Policy;

/**
 * Policy Service Functionalities
 */
public interface PolicyService {

	/**
     * Service to get all policies
     *
     * @author Nidhish
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @param searchTerm - searchTerm to be searched.
     * @return All Policies details
     */
	public Page<Object[]> getPolicies(final int page, final int size, final String searchTerm);

	/**
     * Service to get all policy Id's
     *
     * @author Nidhish
     * @return Policy Id's
     */
	public Collection<String> getAllPolicyIds();

	/**
     * Service to update existing policy
     *
     * @author Nidhish
     * @param policyDetails - details for updating existing policy
     * @return Success or Failure response
     * @throws Exception
     */
	public String updatePolicies(final UpdatePolicyDetails policyDetails) throws Exception;

	/**
     * Service to create new policy
     *
     * @author Nidhish
     * @param policyDetails - details for creating new policy
     * @return Success or Failure response
     * @throws Exception
     */
	public String createPolicies(final CreatePolicyDetails policyDetails) throws Exception;

	/**
     * Service to get policy by id
     *
     * @author Nidhish
     * @param policyId - valid policy id
     * @return Policy details
     */
	public Policy getByPolicyId(String policyId);
}
