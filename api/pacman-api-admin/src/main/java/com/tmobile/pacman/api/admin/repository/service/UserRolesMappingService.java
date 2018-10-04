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

import org.springframework.data.domain.Page;

import com.tmobile.pacman.api.admin.domain.UserRoleConfigRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesMappingResponse;
import com.tmobile.pacman.api.admin.exceptions.PacManException;

/**
 * User Roles Mapping Service Functionalities
 */
public interface UserRolesMappingService {

	/**
     * Service to get all user roles mappings details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return All UserRolesMapping Details
     */
	public Page<UserRolesMappingResponse> getAllUserRolesMapping(final String searchTerm, final int page, final int size);

	/**
     * Service to allocate user roles
     *
     * @author Nidhish
     * @param userRoleConfigRequest - details request for role allocation
     * @param allocator - userId who performs the action
     * @return Success or Failure response
     * @throws PacManExceptio
     */
	public String allocateUserRole(final UserRoleConfigRequest userRoleConfigRequest, final String allocator) throws PacManException;
}
