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

import com.tmobile.pacman.api.admin.domain.AssetGroupExceptionProjections;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupException;

/**
 * AssetGroupException Repository Interface
 */
@Repository
public interface AssetGroupExceptionRepository  extends JpaRepository<AssetGroupException, Long> {

	/**
     * AssetGroupException Repository function for to get all AssetGroupException details by groupName and exceptionName
     *
     * @author Nidhish
     * @param groupName - valid group name
     * @param ExceptionName - valid exception name
     * @return All Asset Group Exception Details
     */
	public List<AssetGroupException> findByGroupNameAndExceptionName(String groupName, String ExceptionName);

	/**
     * AssetGroupException Repository function for to get all AssetGroupException details by groupName
     *
     * @author Nidhish
     * @param groupName - valid group name
     * @return All Asset Group Exception Details
     */
	public List<AssetGroupException> findByGroupName(String groupName);


	/**
     * AssetGroupException Repository function for to get all assetGroup exceptions
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param pageable - pagination information
     * @return All Asset Group Exception Details
     */
	@Query(value = "SELECT exception.id AS id, exception.groupName AS assetGroup, exception.targetType AS targetType, exception.ruleName AS ruleName, exception.ruleId AS ruleId, exception.expiryDate AS expiryDate, exception.exceptionName AS exceptionName, exception.exceptionReason AS exceptionReason, exception.dataSource AS dataSource FROM AssetGroupException exception WHERE "
			+ "LOWER(exception.groupName) LIKE %:searchTerm% OR "
			+ "LOWER(exception.targetType) LIKE %:searchTerm% OR "
			+ "LOWER(exception.ruleName) LIKE %:searchTerm% OR "
			+ "LOWER(exception.ruleId) LIKE %:searchTerm% OR "
			+ "LOWER(exception.expiryDate) LIKE %:searchTerm% OR "
			+ "LOWER(exception.exceptionName) LIKE %:searchTerm% OR "
			+ "LOWER(exception.exceptionReason) LIKE %:searchTerm% OR "
			+ "LOWER(exception.dataSource) LIKE %:searchTerm% GROUP BY exception.exceptionName",

			countQuery = "SELECT COUNT(*) FROM AssetGroupException exception WHERE "
			+ "LOWER(exception.groupName) LIKE %:searchTerm% OR "
			+ "LOWER(exception.targetType) LIKE %:searchTerm% OR "
			+ "LOWER(exception.ruleName) LIKE %:searchTerm% OR "
			+ "LOWER(exception.ruleId) LIKE %:searchTerm% OR "
			+ "LOWER(exception.expiryDate) LIKE %:searchTerm% OR "
			+ "LOWER(exception.exceptionName) LIKE %:searchTerm% OR "
			+ "LOWER(exception.exceptionReason) LIKE %:searchTerm% OR "
			+ "LOWER(exception.dataSource) LIKE %:searchTerm% GROUP BY exception.exceptionName")
	public Page<AssetGroupExceptionProjections> findAllAssetGroupExceptions(@Param("searchTerm") String searchTerm, Pageable pageable);


	/**
     * AssetGroupException Repository function for to get all assetGroup exceptions
     *
     * @author Nidhish
     * @param exceptionName - valid exception name
     * @param dataSource - valid dataSource
     * @return All Asset Group Exception Details
     */
	@Query("SELECT exception.id AS id, exception.groupName AS assetGroup, exception.targetType AS targetType, exception.ruleName AS ruleName, exception.ruleId AS ruleId, exception.expiryDate AS expiryDate, exception.exceptionName AS exceptionName, exception.exceptionReason AS exceptionReason, exception.dataSource AS dataSource FROM AssetGroupException exception WHERE exception.exceptionName=:exceptionName AND exception.dataSource=:dataSource ORDER BY exception.targetType")
	public List<AssetGroupExceptionProjections> findAllAssetGroupExceptions(@Param("exceptionName") final String exceptionName, @Param("dataSource") final String dataSource);

	/**
     * AssetGroupException Repository function for to get all exception names
     *
     * @author Nidhish
     * @return All Exception Name List
     */
	@Query("SELECT exceptionName FROM AssetGroupException GROUP BY exceptionName")
	public Collection<String> getAllExceptionNames();
}
