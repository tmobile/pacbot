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
import org.springframework.transaction.annotation.Transactional;

import com.tmobile.pacman.api.admin.domain.AssetGroupExceptionDetailsRequest;
import com.tmobile.pacman.api.admin.domain.AssetGroupExceptionProjections;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupExceptionRequest;
import com.tmobile.pacman.api.admin.domain.StickyExceptionResponse;
import com.tmobile.pacman.api.admin.exceptions.PacManException;

/**
 * AssetGroup Exception Service Functionalities
 */
@Transactional
public interface AssetGroupExceptionService {

	/**
     * Service to get all asset group exception details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return All AssetGroupException Details
     */
	public Page<AssetGroupExceptionProjections> getAllAssetGroupExceptions(final String searchTerm, final int page, final int size);

	/**
     * Service to create asset group
     *
     * @author Nidhish
     * @param assetGroupExceptionDetailsRequest - assetGroup details for configuring
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String createAssetGroupExceptions(final AssetGroupExceptionDetailsRequest assetGroupExceptionDetailsRequest, final String userId) throws PacManException;

	/**
     * Service to update asset group
     *
     * @author Nidhish
     * @param assetGroupExceptionDetailsRequest - assetGroup details for configuring
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateAssetGroupExceptions(final AssetGroupExceptionDetailsRequest assetGroupExceptionDetailsRequest, final String userId) throws PacManException;

	/**
     * Service to get all asset group exception details by exception name and dataSource
     *
     * @author Nidhish
     * @param exceptionName - exception name of the target type
     * @param dataSource - dataSource name of the target type
     * @return All AssetGroup Exception Details
     * @throws PacManException
     */
	public StickyExceptionResponse getAllTargetTypesByExceptionNameAndDataSource(final String exceptionName, final String dataSource) throws PacManException;

	/**
     * Service to delete asset group
     *
     * @author Nidhish
     * @param assetGroupExceptionRequest - assetGroup details for deleting
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String deleteAssetGroupExceptions(final DeleteAssetGroupExceptionRequest assetGroupExceptionRequest, String userId) throws PacManException;

	/**
     * Service to get all exception names
     *
     * @author Nidhish
     * @return All exception names list
     */
	public Collection<String> getAllExceptionNames();

}
