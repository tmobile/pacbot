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

import com.tmobile.pacman.api.admin.domain.TargetTypesProjection;
import com.tmobile.pacman.api.admin.domain.TargetTypesProjections;
import com.tmobile.pacman.api.admin.repository.model.TargetTypes;

/**
 * TargetTypes Repository Interface
 */
@Repository
public interface TargetTypesRepository extends JpaRepository<TargetTypes, String> {

	@Query("SELECT targetName FROM TargetTypes WHERE dataSourceName = ?#{[0]}  GROUP BY targetName")
	public Collection<String> findByDataSourceName(final String dataSourceName);

	@Query("SELECT target.targetName AS id, target.targetName AS text FROM TargetTypes target WHERE target.targetName NOT IN (:targetIdList) GROUP BY target.targetName")
	public List<TargetTypesProjection> findByTargetTypeNotIn(@Param("targetIdList") List<String> targetIdList); 
	
	public List<TargetTypes> findByDomainIn(List<String> domains);
	
	@Query(value = "SELECT t.targetName AS targetName,t.targetDesc AS targetDesc,t.category AS category,t.dataSourceName AS dataSourceName,t.targetConfig AS targetConfig,t.endpoint AS endpoint, t.domain AS domain FROM TargetTypes t WHERE "
			+ "LOWER(t.targetName) LIKE %:searchTerm% OR "
			+ "LOWER(t.targetDesc) LIKE %:searchTerm% OR "
			+ "LOWER(t.category) LIKE %:searchTerm% OR "
			+ "LOWER(t.dataSourceName) LIKE %:searchTerm% OR "
			+ "LOWER(t.targetConfig) LIKE %:searchTerm% OR "
			+ "LOWER(t.endpoint) LIKE %:searchTerm% OR "
			+ "LOWER(t.domain) LIKE %:searchTerm% GROUP BY t.targetName", 
			
			countQuery = "SELECT COUNT(*) FROM TargetTypes t WHERE "
					+ "LOWER(t.targetName) LIKE %:searchTerm% OR "
					+ "LOWER(t.targetDesc) LIKE %:searchTerm% OR "
					+ "LOWER(t.category) LIKE %:searchTerm% OR "
					+ "LOWER(t.dataSourceName) LIKE %:searchTerm% OR "
					+ "LOWER(t.targetConfig) LIKE %:searchTerm% OR "
					+ "LOWER(t.endpoint) LIKE %:searchTerm% OR "
					+ "LOWER(t.domain) LIKE %:searchTerm% GROUP BY t.targetName")
	public Page<TargetTypesProjections> findAllTargetTypeDetails(@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("SELECT target.targetName AS id, target.targetName AS text FROM TargetTypes target GROUP BY target.targetName")
	public List<TargetTypesProjection> getAllTargetTypes(); 
	
	@Query("SELECT dataSourceName FROM TargetTypes WHERE targetName = (:targetType) ")
	public String findDataSourceByTargetType(@Param("targetType") String targetType);

}
