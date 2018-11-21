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
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.admin.domain.RuleProjection;
import com.tmobile.pacman.api.admin.repository.model.Rule;

/**
 * Rule Repository Interface
 */
@Repository
public interface RuleRepository extends JpaRepository<Rule, String> {

	public Rule findByRuleId(final String ruleId); 
	
	public List<Rule> findByTargetTypeIgnoreCase(final String targetType); 
	
	@Query("SELECT r.ruleId AS id, r.policyId AS policyId, r.ruleType AS type, r.status AS status, r.ruleName AS text FROM Rule r WHERE LOWER(r.targetType) LIKE %:targetType% GROUP BY r.ruleId")
	public List<RuleProjection> findByTargetType(@Param("targetType") final String targetType); 
	
	@Query("SELECT r.ruleId AS id, r.policyId AS policyId, r.ruleType AS type, r.status AS status, r.ruleName AS text FROM Rule r WHERE LOWER(r.targetType) LIKE %:targetType% AND r.ruleId NOT IN (:ruleIdList) GROUP BY r.ruleId")
	public List<RuleProjection> findByTargetTypeAndRuleIdNotIn(@Param("targetType") String targetType, @Param("ruleIdList") List<String> ruleIdList); 
	
	@Query("SELECT r.ruleId AS id, r.policyId AS policyId, r.ruleType AS type, r.status AS status, r.ruleName AS text FROM Rule r WHERE LOWER(r.targetType) LIKE %:targetType% AND r.ruleId IN (:ruleIdList) GROUP BY r.ruleId")
	public List<RuleProjection> findByTargetTypeAndRuleIdIn(@Param("targetType") String targetType, @Param("ruleIdList")  List<String> ruleIdList);

	@Query(value = "SELECT r FROM Rule r WHERE "
			+ "LOWER(r.ruleId) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleUUID) LIKE %:searchTerm% OR "
			+ "LOWER(r.policyId) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleName) LIKE %:searchTerm% OR "
			+ "LOWER(r.targetType) LIKE %:searchTerm% OR "
			+ "LOWER(r.assetGroup) LIKE %:searchTerm% OR "
			+ "LOWER(r.alexaKeyword) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleParams) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleFrequency) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleExecutable) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleRestUrl) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleType) LIKE %:searchTerm% OR "
			+ "LOWER(r.ruleArn) LIKE %:searchTerm% OR "
			+ "LOWER(r.status) LIKE %:searchTerm% OR "
			+ "LOWER(r.displayName) LIKE %:searchTerm% OR "
			+ "LOWER(r.createdDate) LIKE %:searchTerm% OR "
			+ "LOWER(r.modifiedDate) LIKE %:searchTerm% GROUP BY r.ruleId", 
			
			countQuery = "SELECT COUNT(*) FROM Rule r WHERE "
					+ "LOWER(r.ruleId) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleUUID) LIKE %:searchTerm% OR "
					+ "LOWER(r.policyId) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleName) LIKE %:searchTerm% OR "
					+ "LOWER(r.targetType) LIKE %:searchTerm% OR "
					+ "LOWER(r.assetGroup) LIKE %:searchTerm% OR "
					+ "LOWER(r.alexaKeyword) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleParams) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleFrequency) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleExecutable) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleRestUrl) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleType) LIKE %:searchTerm% OR "
					+ "LOWER(r.ruleArn) LIKE %:searchTerm% OR "
					+ "LOWER(r.status) LIKE %:searchTerm% OR "
					+ "LOWER(r.displayName) LIKE %:searchTerm% OR "
					+ "LOWER(r.createdDate) LIKE %:searchTerm% OR "
					+ "LOWER(r.modifiedDate) LIKE %:searchTerm% GROUP BY r.ruleId")
	public Page<Rule> findAll(@Param("searchTerm") String searchTerm, Pageable pageable);
	
	@Query("SELECT alexaKeyword FROM Rule WHERE alexaKeyword != '' AND alexaKeyword != null GROUP BY alexaKeyword")
	public Collection<String> getAllAlexaKeywords();

	@Query("SELECT ruleId FROM Rule WHERE ruleId != '' AND ruleId != null GROUP BY ruleId")
	public Collection<String> getAllRuleIds();
}
