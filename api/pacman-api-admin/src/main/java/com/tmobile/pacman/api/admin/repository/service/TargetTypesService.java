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
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.tmobile.pacman.api.admin.domain.AssetGroupTargetTypes;
import com.tmobile.pacman.api.admin.domain.AttributeValuesRequest;
import com.tmobile.pacman.api.admin.domain.CreateUpdateTargetTypeDetailsRequest;
import com.tmobile.pacman.api.admin.domain.TargetTypeAttribute;
import com.tmobile.pacman.api.admin.domain.TargetTypesDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypesProjections;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.TargetTypes;

/**
 * TargetTypes Service Functionalities
 */
public interface TargetTypesService {

	/**
     * Service to get all target type names by dataSource name
     *
     * @author Nidhish
     * @param dataSourceName - valid dataSourceName
     * @return TargetTypes names list
     */
	public Collection<String> getTargetTypesNamesByDataSourceName(final String dataSourceName);

	/**
     * Service to get all target type names by dataSource name list
     *
     * @author Nidhish
     * @param domains - valid dataSource name list
     * @return TargetTypes details list
     */
	public List<TargetTypes> getAllTargetTypesByDomainList(final List<String> domains);

	/**
     * Service to get all target type attributes
     *
     * @author Nidhish
     * @param targetTypes - list of target type details
     * @return TargetType Attributes list
     */
	public List<TargetTypeAttribute> getTargetTypeAttributes(final List<TargetTypes> targetTypes);

	/**
     * Service to get attribute values
     *
     * @author Nidhish
     * @param attributeValuesRequest - valid attribute value request details
     * @return TargetType Attribute Values
     */
	public Map<String, Object> getAttributeValues(AttributeValuesRequest attributeValuesRequest);

	/**
     * Service to get all target type details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return All TargetType details
     */
	public Page<TargetTypesProjections> getAllTargetTypeDetails(final String searchTerm, final int page, final int size);

	/**
     * Service to get all targetTypes categories
     *
     * @author Nidhish
     * @return All TargetType Categories list
     */
	public List<String> getAllTargetTypesCategories();

	/**
     * Service to add new target type
     * 
     * @author Nidhish
     * @param targetTypeDetailsRequest - details for creating new targetType
     * @param userId - valid user id
     * @return Success or Failure response
     * @throws PacManException
     */
	public String addTargetTypeDetails(final CreateUpdateTargetTypeDetailsRequest targetTypeDetailsRequest, final String userId) throws PacManException;

	/**
     * Service to add new target type
     *
     * @author Nidhish
     * @param targetTypeIndex - valid targetType index
     */
	public void deleteIndex(String targetTypeIndex);

	/**
     * API to get target types details by name
     *
     * @author Nidhish
     * @param targetTypeName - valid targetType name
     * @return TargetTypes details
     * @throws PacManException
     */
	public TargetTypes getTargetTypesByName(final String targetTypeName) throws PacManException;


	/**
     * Service to update existing target type
     * 
     * @author Nidhish
     * @param targetTypesDetails - details for updating existing targetType
     * @param userId - valid user id
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateTargetTypeDetails(CreateUpdateTargetTypeDetailsRequest targetTypesDetails, final String userId) throws PacManException;

	/**
     * Service to get all target types details by selected asset group targetTypes
     *
     * @author Nidhish
     * @param selectedTargetTypes - list of selected TargetTypes
     * @return All TargetTypes details
     */
	public List<TargetTypesDetails> getAllTargetTypes(List<AssetGroupTargetTypes> selectedTargetTypes);
}
