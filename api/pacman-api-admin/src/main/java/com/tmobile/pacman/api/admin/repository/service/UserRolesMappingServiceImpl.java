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

import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_ALLOCATION_FAILURE;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_ALLOCATION_SUCCESS;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.UserRoleConfigRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesMappingResponse;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserRolesMappingRepository;
import com.tmobile.pacman.api.admin.repository.UserRolesRepository;
import com.tmobile.pacman.api.admin.repository.model.User;
import com.tmobile.pacman.api.admin.repository.model.UserRolesMapping;

/**
 * UserRoles Mapping Service Implementations
 */
@Service
@Transactional
public class UserRolesMappingServiceImpl implements UserRolesMappingService {

	@Autowired
	private UserRolesMappingRepository userRolesMappingRepository;
	
	@Autowired
	private UserRolesRepository userRolesRepository;

	@Value("${pacman.api.oauth2.client-id}")
	private String oauth2ClientId;
	
	@Override
	public String allocateUserRole(final UserRoleConfigRequest userRoleConfigRequest, final String allocator) throws PacManException {
		List<UserRolesMapping> allDeletedUserRoleAllocations = Lists.newArrayList();
		try {
			Date currentDate = new Date();
			List<UserRolesMapping> userRolesMappings = Lists.newArrayList();
			allDeletedUserRoleAllocations = userRolesMappingRepository.deleteByRoleId(userRoleConfigRequest.getRoleId());
			for(User user: userRoleConfigRequest.getUserDetails()) {
				if(userRolesRepository.existsById(userRoleConfigRequest.getRoleId())) {
					UserRolesMapping userRolesMapping = new UserRolesMapping();
					userRolesMapping.setAllocator(allocator);
					userRolesMapping.setClientId(oauth2ClientId);
					userRolesMapping.setCreatedDate(currentDate);
					userRolesMapping.setModifiedDate(currentDate);
					userRolesMapping.setRoleId(userRoleConfigRequest.getRoleId());
					userRolesMapping.setUserId(user.getUserId().toLowerCase());
					userRolesMapping.setUserRoleId(UUID.randomUUID().toString());
					userRolesMappings.add(userRolesMapping);
				}
			}
			if(!userRolesMappings.isEmpty()) {
				userRolesMappingRepository.saveAll(userRolesMappings);
			}
			return USER_ROLE_ALLOCATION_SUCCESS;
		} catch (Exception exception) {
			allDeletedUserRoleAllocations.parallelStream().forEach(userRolesMapping -> {
				userRolesMappingRepository.save(userRolesMapping);
			});
			throw new PacManException(USER_ROLE_ALLOCATION_FAILURE);
		}
	}
	
	@Override
	public Page<UserRolesMappingResponse> getAllUserRolesMapping(String searchTerm, int page, int size) {
		Page<UserRolesMapping> userRoleMappings = userRolesMappingRepository.findAllUserRolesMappingDetails(searchTerm, new PageRequest(page, size));
		List<UserRolesMappingResponse> allUserRoleMappingsList = userRoleMappings.getContent().parallelStream().map(fetchUserRolesMappingDetials).collect(Collectors.toList());
		Page<UserRolesMappingResponse> allUserRoleMappings = new PageImpl<UserRolesMappingResponse>(allUserRoleMappingsList, new PageRequest(page, size), userRoleMappings.getTotalElements());
		return allUserRoleMappings;
	}

	Function<UserRolesMapping, UserRolesMappingResponse> fetchUserRolesMappingDetials = userRoleDetail -> {
		List<String[]> userDetails = userRolesMappingRepository.findAllUserRoleDetailsByUserIdIgnoreCase(userRoleDetail.getUserId());
		UserRolesMappingResponse userRolesMappingResponse = new UserRolesMappingResponse();
		userRolesMappingResponse.setUserId(userRoleDetail.getUserId());
		userRolesMappingResponse.setUserRoleId(userRoleDetail.getUserRoleId());
		userRolesMappingResponse.setRoles(userDetails);
		return userRolesMappingResponse;
	};	
}

