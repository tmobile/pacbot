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

import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_ALLOCATION_SUCCESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tmobile.pacman.api.admin.domain.UserRoleConfigRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesMappingResponse;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserRolesMappingRepository;
import com.tmobile.pacman.api.admin.repository.UserRolesRepository;
import com.tmobile.pacman.api.admin.repository.model.User;
import com.tmobile.pacman.api.admin.repository.model.UserRoles;
import com.tmobile.pacman.api.admin.repository.model.UserRolesMapping;

@RunWith(MockitoJUnitRunner.class)
public class UserRolesMappingServiceImplTest {

	@InjectMocks
	private UserRolesMappingServiceImpl userRolesMappingService;
	
	@Mock
	private UserRolesMappingRepository userRolesMappingRepository;
	
	@Mock
	private UserRolesRepository userRolesRepository;

	@Test
	public void getAllUserRolesMappingTest() {
		List<UserRolesMappingResponse> userRolesDetails = new ArrayList<UserRolesMappingResponse>();
		userRolesDetails.add(getUserRolesMappingDetails());
	
		Page<UserRolesMapping> userRoleMappings = getUserRolesMapping();
		when(userRolesMappingRepository.findAllUserRolesMappingDetails(StringUtils.EMPTY, new PageRequest(0, 1))).thenReturn(userRoleMappings);
		assertThat(userRolesMappingService.getAllUserRolesMapping(StringUtils.EMPTY, 0, 1).getContent().size(), is(1));
	}
	
	@Test
	public void allocateUserRoleTest() throws PacManException {
		UserRoleConfigRequest roleAllocateDetailsRequest = getUserRoleConfigRequest();
		List<UserRolesMapping> userRolesMappingDetails = getUserRolesMappingDetailsRequest();
		when(userRolesMappingRepository.deleteByRoleId(anyString())).thenReturn(userRolesMappingDetails);
		when(userRolesRepository.existsById(anyString())).thenReturn(true);
		assertThat(userRolesMappingService.allocateUserRole(roleAllocateDetailsRequest, "userId"), is(USER_ROLE_ALLOCATION_SUCCESS));
	}

	@Test
	public void allocateUserRoleExceptionTest() throws PacManException {
		UserRoleConfigRequest roleAllocateDetailsRequest = getUserRoleConfigRequest();
		when(userRolesMappingRepository.deleteByRoleId(anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> userRolesMappingService.allocateUserRole(roleAllocateDetailsRequest, "userId")).isInstanceOf(PacManException.class);
	}
	
	private UserRoleConfigRequest getUserRoleConfigRequest() {
		UserRoleConfigRequest userRoleConfigRequest = new UserRoleConfigRequest();
		userRoleConfigRequest.setRoleId("roleId");
		List<User> userDetails = Lists.newArrayList();
		User userDetailsRequest = new User();
		userDetailsRequest.setUserId("userId");
		userDetails.add(userDetailsRequest);
		userRoleConfigRequest.setUserDetails(userDetails);
		return userRoleConfigRequest;
	}

	private Page<UserRolesMapping> getUserRolesMapping() {
		List<UserRolesMapping> allUserRolesMapping = getUserRolesMappingDetailsRequest();
		Page<UserRolesMapping> allUserRolesMappingDetails = new PageImpl<UserRolesMapping>(allUserRolesMapping, new PageRequest(0, 1), allUserRolesMapping.size());
		return allUserRolesMappingDetails;
	}

	private List<UserRolesMapping> getUserRolesMappingDetailsRequest() {
		List<UserRolesMapping> allUserRolesMapping = Lists.newArrayList();
		UserRoles userRoles = getUserRoleDetails();
		UserRolesMapping userRolesMapping = new UserRolesMapping();
		userRolesMapping.setAllocator("allocator123");
		userRolesMapping.setClientId("clientId123");
		userRolesMapping.setCreatedDate(new Date());
		userRolesMapping.setModifiedDate(new Date());
		userRolesMapping.setRoleId("123");
		userRolesMapping.setUserId("userId");
		userRolesMapping.setUserRoleId("userRoleId");
		userRolesMapping.setUserRoles(userRoles);
		allUserRolesMapping.add(userRolesMapping);
		return allUserRolesMapping;
	}

	private UserRoles getUserRoleDetails() {
		UserRoles userRoleDetails = new UserRoles();
		userRoleDetails.setClient("clientId123");
		userRoleDetails.setCreatedDate(new Date());
		userRoleDetails.setModifiedDate(new Date());
		userRoleDetails.setOwner("owner123");
		userRoleDetails.setRoleId("123");
		userRoleDetails.setRoleName("roleName123");
		userRoleDetails.setUsers(Sets.newHashSet());
		userRoleDetails.setWritePermission(false);
		return userRoleDetails;
	}
	private UserRolesMappingResponse getUserRolesMappingDetails() {
		UserRolesMappingResponse userRolesMappingResponse = new UserRolesMappingResponse();
		userRolesMappingResponse.setUserRoleId("userRoleId123");
		userRolesMappingResponse.setUserId("userId123");
		userRolesMappingResponse.setRoles(Lists.newArrayList());
		return userRolesMappingResponse;
	}
}
