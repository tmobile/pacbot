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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.admin.repository.model.UserRoles;

/**
 * UserRoles Repository Interface
 */
@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, String> {

	@Query(value = "SELECT role FROM UserRoles role WHERE "
			+ "LOWER(role.roleName) LIKE %:searchTerm% OR "
			+ "LOWER(role.owner) LIKE %:searchTerm% OR "
			+ "LOWER(role.createdDate) LIKE %:searchTerm% OR "
			+ "LOWER(role.modifiedDate) LIKE %:searchTerm% GROUP BY role.roleName", 
			
			countQuery = "SELECT COUNT(*) FROM UserRoles role WHERE "
					+ "LOWER(role.roleName) LIKE %:searchTerm% OR "
					+ "LOWER(role.owner) LIKE %:searchTerm% OR "
					+ "LOWER(role.createdDate) LIKE %:searchTerm% OR "
					+ "LOWER(role.modifiedDate) LIKE %:searchTerm% GROUP BY role.roleName")

	public Page<UserRoles> findAllUserRolesDetails(@Param("searchTerm") String searchTerm, Pageable pageable);
	
	public boolean existsByRoleNameIgnoreCase(String roleName);

	public List<String[]> findAllUserDetailsByRoleId(String roleId);
}
