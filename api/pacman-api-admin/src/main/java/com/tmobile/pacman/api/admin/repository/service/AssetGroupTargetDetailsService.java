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

import java.util.List;
import java.util.Set;

import com.tmobile.pacman.api.admin.domain.TargetTypeRuleDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleViewDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;

/**
 * AssetGroupTargetDetails Service Functionalities
 */
public interface AssetGroupTargetDetailsService {

	/**
     * Service to get all target types by assetGroup name
     *
     * @author Nidhish
     * @param assetGroupName - valid assetGroup name.
     * @return All TargetType rule details
     */
	public List<TargetTypeRuleDetails> getTargetTypesByAssetGroupName(String assetGroupName);

	/**
     * Service to get target types by assetGroup Id and target type not in list
     *
     * @author Nidhish
     * @param assetGroupName - valid assetGroup name.
     * @param targetTypeNames - valid targetType name list.
     * @return All TargetType rule details
     * @throws PacManException
     */
	public List<TargetTypeRuleViewDetails> getTargetTypesByAssetGroupIdAndTargetTypeNotIn(String assetGroupName, Set<String> targetTypeNames) throws PacManException;
}
