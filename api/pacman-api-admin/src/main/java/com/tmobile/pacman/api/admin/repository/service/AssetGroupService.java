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

import com.tmobile.pacman.api.admin.domain.AssetGroupView;
import com.tmobile.pacman.api.admin.domain.CreateUpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupRequest;
import com.tmobile.pacman.api.admin.domain.UpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupDetails;

/**
 * AssetGroup Service Functionalities
 */
public interface AssetGroupService {

	/**
     * Service to get all asset group names
     *
     * @author Nidhish
     * @return The asset group names
     */
	public Collection<String> getAllAssetGroupNames();

	/**
     * Service to get all asset group details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return All asset group details
     */
	public Page<AssetGroupView> getAllAssetGroupDetails(final String searchTerm, final int page, final int size);

	/**
     * Service to get asset group details by name
     *
     * @author Nidhish
     * @param groupName - valid assetGroup name
     * @return Asset group details
     */
	public AssetGroupDetails findByGroupName(String groupName);

	/**
     * Service to create asset group
     *
     * @author Nidhish
     * @param createAssetGroupDetails - details for creating new AssetGroup
     * @param userId - userId who performs the action
     * @return Success or failure message
     * @throws PacManException
     */
	public String createAssetGroupDetails(final CreateUpdateAssetGroupDetails createAssetGroupDetails, final String userId) throws PacManException;

	/**
     * Service to update existing asset group
     *
     * @author Nidhish
     * @param updateAssetGroupDetails - details for updating existing AssetGroup
     * @param userId - userId who performs the action
     * @return Success or failure message
     * @throws PacManException
     */
	public String updateAssetGroupDetails(final CreateUpdateAssetGroupDetails updateAssetGroupDetails, final String userId) throws PacManException;

	/**
     * Service to get all asset group details by id and dataSource
     *
     * @author Nidhish
     * @param assetGroupId - valid assetGroup Id
     * @param dataSource - valid dataSource name
     * @return All asset group details
     * @throws PacManException
     */
	public UpdateAssetGroupDetails getAssetGroupDetailsByIdAndDataSource(final String assetGroupId, final String dataSource) throws PacManException;

	/**
     * Service to delete asset group
     *
     * @author Nidhish
     * @param assetGroupDetails - details for deleting existing assetGroup
     * @param userId - userId who performs the action
     * @return Success or failure message
     * @throws PacManException
     */
	public String deleteAssetGroup(final DeleteAssetGroupRequest assetGroupDetails, final String userId) throws PacManException;

}
