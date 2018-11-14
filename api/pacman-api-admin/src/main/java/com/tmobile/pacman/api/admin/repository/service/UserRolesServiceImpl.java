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

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_ALREADY_EXITS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_CREATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_NOT_EXITS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_UPDATION_SUCCESS;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.CreateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UpdateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesResponse;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserRolesRepository;
import com.tmobile.pacman.api.admin.repository.model.UserRoles;

/**
 * UserRoles Service Implementations
 */
@Service
public class UserRolesServiceImpl implements UserRolesService {

	private static final Logger log = LoggerFactory.getLogger(UserRolesServiceImpl.class);
	
	@Autowired
	private UserRolesRepository userRolesRepository;

	@Override
	public Page<UserRolesResponse> getAllUserRoles(String searchTerm, int page, int size) {
		Page<UserRoles> userRoles = userRolesRepository.findAllUserRolesDetails(searchTerm, PageRequest.of(page, size));
		List<UserRolesResponse> allUserRolesList = Lists.newCopyOnWriteArrayList();
		userRoles.getContent().forEach(userRoleDetail -> {
			UserRolesResponse userRolesResponse = new UserRolesResponse();
			if(userRoleDetail != null){
				userRolesResponse.setCreatedBy(userRoleDetail.getOwner());
				userRolesResponse.setCreatedDate(userRoleDetail.getCreatedDate());
				userRolesResponse.setModifiedDate(userRoleDetail.getModifiedDate());
				userRolesResponse.setRoleId(userRoleDetail.getRoleId());
				userRolesResponse.setDescription(userRoleDetail.getRoleDesc());
				userRolesResponse.setRoleName(userRoleDetail.getRoleName());
				userRolesResponse.setUsers(userRoleDetail.getUsers().parallelStream().map(user -> user != null ? user.getUserId() : StringUtils.EMPTY).collect(Collectors.toList()));
				allUserRolesList.add(userRolesResponse);
			} 
		 });
		Page<UserRolesResponse> allUserRoles = new PageImpl<UserRolesResponse>(allUserRolesList, PageRequest.of(page, size), userRoles.getTotalElements());
		return allUserRoles;
	}

	@Override
	public String createUserRole(final CreateRoleDetailsRequest roleDetailsRequest, final String userId) throws PacManException {
		boolean isRoleNameExits = userRolesRepository.existsByRoleNameIgnoreCase(roleDetailsRequest.getRoleName());
		if (!isRoleNameExits) {
			try {
				Date currentDate = new Date();
				UserRoles userRole = new UserRoles();
				userRole.setRoleId(UUID.randomUUID().toString());
				userRole.setClient("pacman2_api_client");
				userRole.setRoleDesc(roleDetailsRequest.getDescription());
				userRole.setCreatedDate(currentDate);
				userRole.setModifiedDate(currentDate);
				userRole.setOwner(userId);
				userRole.setRoleName(roleDetailsRequest.getRoleName());
				userRole.setWritePermission(roleDetailsRequest.getWritePermission());
				userRolesRepository.save(userRole);
				return USER_ROLE_CREATION_SUCCESS;
			} catch (Exception exception) {
				log.error(UNEXPECTED_ERROR_OCCURRED, exception);
				throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
			}
		} else {
			throw new PacManException(USER_ROLE_ALREADY_EXITS);
		}
	}
	
	@Override
	public String updateUserRole(final UpdateRoleDetailsRequest roleDetailsRequest, final String userId) throws PacManException {
		boolean isRoleNameExits = userRolesRepository.existsById(roleDetailsRequest.getRoleId());
		if (isRoleNameExits) {
			try {
				Date currentDate = new Date();
				UserRoles userRole = userRolesRepository.findById(roleDetailsRequest.getRoleId()).get();
				userRole.setModifiedDate(currentDate);
				userRole.setOwner(userId);
				userRole.setRoleDesc(roleDetailsRequest.getDescription());
				userRole.setRoleName(roleDetailsRequest.getRoleName());
				userRole.setWritePermission(roleDetailsRequest.getWritePermission());
				userRolesRepository.save(userRole);
				return USER_ROLE_UPDATION_SUCCESS;
			} catch (Exception exception) {
				log.error(UNEXPECTED_ERROR_OCCURRED, exception);
				throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
			}
		} else {
			throw new PacManException(USER_ROLE_NOT_EXITS);
		}
	}

	@Override
	public UserRoles getUserRoleById(final String roleId) throws PacManException {
		boolean isRoleIdExits = userRolesRepository.existsById(roleId);
		if(isRoleIdExits) {
			return userRolesRepository.findById(roleId).get();
		} else {
			throw new PacManException(USER_ROLE_NOT_EXITS);
		}
	}
}
