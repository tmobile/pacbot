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

import com.tmobile.pacman.api.admin.repository.model.Domain;

/**
 * Domain Repository Interface
 */
public interface DomainRepository extends JpaRepository<Domain, String> {

	/**
     * Domain Repository function for to get all domain details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param pageable - pagination information
     * @return All Domain Details
     */
	@Query(value = "SELECT d.domainDesc AS domainDesc, d.domainName AS domainName, d.config AS config, COUNT(t.domain) AS targetTypesCount FROM cf_Domain d LEFT JOIN cf_Target t ON t.domain = d.domainName WHERE "
			+ "LOWER(d.domainDesc) LIKE %:searchTerm% OR "
			+ "LOWER(d.domainName) LIKE %:searchTerm% OR "
			+ "LOWER(d.config) LIKE %:searchTerm% GROUP BY d.domainName",

			countQuery = "SELECT COUNT(*) FROM cf_Domain d LEFT JOIN cf_Target t ON t.domain = d.domainName WHERE "
			+ "LOWER(d.domainDesc) LIKE %:searchTerm% OR "
			+ "LOWER(d.domainName) LIKE %:searchTerm% OR "
			+ "LOWER(d.config) LIKE %:searchTerm% GROUP BY d.domainName", nativeQuery=true)
	public Page<Object[]> findAllDomainDetails(@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("SELECT domainName FROM Domain WHERE domainName != '' AND domainName != null GROUP BY domainName")
	public Collection<String> getAllDomainNames();
}
