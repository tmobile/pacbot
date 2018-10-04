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

import com.tmobile.pacman.api.admin.domain.CreateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UpdateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesResponse;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.UserRoles;

/**
 * User Roles Service Functionalities
 */
public interface UserRolesService {

	/**
     * Service to create roles details
     *
     * @author Nidhish
     * @param roleDetailsRequest - details for creating new role
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String createUserRole(final CreateRoleDetailsRequest roleDetailsRequest, final String userId) throws PacManException;

	/**
     * Service to update roles details
     *
     * @author Nidhish
     * @param roleDetailsRequest - details for updating existing role
     * @param userId - userId who performs the action
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateUserRole(final UpdateRoleDetailsRequest roleDetailsRequest, final String userId) throws PacManException;

	/**
     * Service to get all user roles details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return User Role details
     */
	public Page<UserRolesResponse> getAllUserRoles(String searchTerm, int page, int size);

	/**
     * Service to get user role by id
     *
     * @author Nidhish
     * @param roleId - valid role Id
     * @return User Role details
     * @throws PacManException
     */
	public UserRoles getUserRoleById(final String roleId) throws PacManException;
}
