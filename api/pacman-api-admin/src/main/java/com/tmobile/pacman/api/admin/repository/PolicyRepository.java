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
package com.tmobile.pacman.api.admin.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.admin.repository.model.Policy;

/**
 * Policy Repository Interface
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {

	@Query("SELECT policyId FROM Policy")
	public Collection<String> getAllPolicyIds();


	/**
     * Policy Repository function for to get all domain details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param pageable - pagination information
     * @return All Policy Details
     */
	@Query(value = "SELECT p.createdDate AS createdDate, p.modifiedDate AS modifiedDate, resolution,  policyDesc, p.policyId AS policyId, policyUrl, policyVersion, policyName, COUNT(r.policyId) AS numberOfRules FROM cf_Policy p LEFT JOIN cf_RuleInstance r ON r.policyId = p.policyId WHERE "
			+ "LOWER(p.policyId) LIKE %:searchTerm% OR "
			+ "LOWER(p.resolution) LIKE %:searchTerm% OR "
			+ "LOWER(p.createdDate) LIKE %:searchTerm% OR "
			+ "LOWER(p.modifiedDate) LIKE %:searchTerm% OR "
			+ "LOWER(p.policyDesc) LIKE %:searchTerm% OR "
			+ "LOWER(p.policyVersion) LIKE %:searchTerm% OR "
			+ "LOWER(p.policyName) LIKE %:searchTerm% GROUP BY p.policyId",

			countQuery = "SELECT COUNT(*) FROM cf_Policy p WHERE "
					+ "LOWER(p.policyId) LIKE %:searchTerm% OR "
					+ "LOWER(p.resolution) LIKE %:searchTerm% OR "
					+ "LOWER(p.createdDate) LIKE %:searchTerm% OR "
					+ "LOWER(p.modifiedDate) LIKE %:searchTerm% OR "
					+ "LOWER(p.policyDesc) LIKE %:searchTerm% OR "
					+ "LOWER(p.policyVersion) LIKE %:searchTerm% OR "
					+ "LOWER(p.policyName) LIKE %:searchTerm% GROUP BY p.policyId", nativeQuery=true)
	public Page<Object[]> getAllPolicyDetails(@Param("searchTerm") String searchTerm, Pageable pageable);


	/**
     * Policy Repository function for to get policy by policyId
     *
     * @author Nidhish
     * @param policyId - valid policyId
     * @return Policy Details
     */
	public Policy findByPolicyIdIgnoreCase(final String policyId);
}
