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

import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_CREATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_UPDATION_SUCCESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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
import com.tmobile.pacman.api.admin.domain.CreateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UpdateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserRolesRepository;
import com.tmobile.pacman.api.admin.repository.model.UserRoles;

@RunWith(MockitoJUnitRunner.class)
public class UserRolesServiceImplTest {

	@InjectMocks
	private UserRolesServiceImpl userRolesService;

	@Mock
	private UserRolesRepository userRolesRepository;

	@Test
	public void getAllUserRolesTest() {
		List<UserRoles> userRolesDetails = Lists.newArrayList();
		UserRoles userRolesDetail = getUserRoleDetails();
		userRolesDetails.add(userRolesDetail);
		Page<UserRoles> allUserRolesDetails = new PageImpl<UserRoles>(userRolesDetails, new PageRequest(0, 1), userRolesDetails.size());
		when(userRolesRepository.findAllUserRolesDetails(StringUtils.EMPTY, new PageRequest(0, 1))).thenReturn(allUserRolesDetails);
		assertThat(userRolesService.getAllUserRoles(StringUtils.EMPTY, 0, 1).getContent().size(), is(1));
	}

	@Test
	public void createUserRoleTest() throws PacManException {
		CreateRoleDetailsRequest roleDetailsRequest = getUserRoleDetailsRequest();
		UserRoles userRolesDetail = getUserRoleDetails();
		when(userRolesRepository.existsByRoleNameIgnoreCase(roleDetailsRequest.getRoleName())).thenReturn(false);
		when(userRolesRepository.save(userRolesDetail)).thenReturn(userRolesDetail);
		assertThat(userRolesService.createUserRole(roleDetailsRequest, anyString()), is(USER_ROLE_CREATION_SUCCESS));
	}

	@Test
	public void createUserRoleNotFoundTest() throws PacManException {
		CreateRoleDetailsRequest roleDetailsRequest = getUserRoleDetailsRequest();
		when(userRolesRepository.existsByRoleNameIgnoreCase(roleDetailsRequest.getRoleName())).thenReturn(true);
		assertThatThrownBy(() -> userRolesService.createUserRole(roleDetailsRequest, anyString())).isInstanceOf(PacManException.class);
	}


	@Test
	public void updateUserRoleTest() throws PacManException {
		UpdateRoleDetailsRequest roleDetailsRequest = getUpdateRoleDetailsRequest();
		UserRoles userRolesDetail = getUserRoleDetails();
		when(userRolesRepository.findOne(anyString())).thenReturn(userRolesDetail);
		when(userRolesRepository.exists(anyString())).thenReturn(true);

		when(userRolesRepository.save(userRolesDetail)).thenReturn(userRolesDetail);
		assertThat(userRolesService.updateUserRole(roleDetailsRequest, anyString()), is(USER_ROLE_UPDATION_SUCCESS));
	}

	@Test
	public void updateUserRoleUserNotFoundTest() throws PacManException {
		UpdateRoleDetailsRequest roleDetailsRequest = getUpdateRoleDetailsRequest();
		UserRoles userRolesDetail = getUserRoleDetails();
		when(userRolesRepository.findOne(anyString())).thenReturn(userRolesDetail);
		when(userRolesRepository.exists(anyString())).thenReturn(false);

		when(userRolesRepository.save(userRolesDetail)).thenReturn(userRolesDetail);
		assertThatThrownBy(() -> userRolesService.updateUserRole(roleDetailsRequest, anyString())).isInstanceOf(PacManException.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateUserRoleUserNotFoundExceptionTest() throws PacManException {
		UpdateRoleDetailsRequest roleDetailsRequest = getUpdateRoleDetailsRequest();
		UserRoles userRolesDetail = getUserRoleDetails();
		when(userRolesRepository.findOne(anyString())).thenThrow(Exception.class);
		when(userRolesRepository.exists(anyString())).thenReturn(true);

		when(userRolesRepository.save(userRolesDetail)).thenReturn(userRolesDetail);
		assertThatThrownBy(() -> userRolesService.updateUserRole(roleDetailsRequest, anyString())).isInstanceOf(PacManException.class);
	}

	@Test
	public void getUserRoleByIdTest() throws PacManException {
		UserRoles userRolesDetail = getUserRoleDetails();
		when(userRolesRepository.exists(anyString())).thenReturn(true);
		when(userRolesRepository.findOne(anyString())).thenReturn(userRolesDetail);
		assertThat(userRolesService.getUserRoleById(anyString()).getOwner(), is("owner123"));
	}

	@Test
	public void getUserRoleByIdRoleIdNotExitsTest() throws PacManException {
		UserRoles userRolesDetail = getUserRoleDetails();
		when(userRolesRepository.exists(anyString())).thenReturn(false);
		when(userRolesRepository.getOne(anyString())).thenReturn(userRolesDetail);
		assertThatThrownBy(() -> userRolesService.getUserRoleById(anyString())).isInstanceOf(PacManException.class);
	}
/*
 * try {
				Date currentDate = new Date();
				UserRoles userRole = userRolesRepository.findOne(roleDetailsRequest.getRoleId());
				userRole.setModifiedDate(currentDate);
				userRole.setOwner("Nidhish");
				userRole.setRoleName(roleDetailsRequest.getRoleName());
				userRole.setWritePermission(roleDetailsRequest.getWritePermission());
				userRolesRepository.save(userRole);
				return USER_ROLE_UPDATION_SUCCESS;
			} catch (Exception exception) {
				exception.printStackTrace();
				throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
			}
 * @Override
	public UserRoles getUserRoleById(final Long roleId) throws PacManException {
		boolean isRoleIdExits = userRolesRepository.exists(roleId);
		if(isRoleIdExits) {

		} else {
			throw new PacManException(USER_ROLE_NOT_EXITS);
		}
		return userRolesRepository.getOne(roleId);
	}
	*/
	private UpdateRoleDetailsRequest getUpdateRoleDetailsRequest() {
		UpdateRoleDetailsRequest updateRoleDetailsRequest = new UpdateRoleDetailsRequest();
		updateRoleDetailsRequest.setDescription("description123");
		updateRoleDetailsRequest.setRoleId("123");
		updateRoleDetailsRequest.setRoleName("roleName123");
		updateRoleDetailsRequest.setWritePermission(false);
		return updateRoleDetailsRequest;
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

	private CreateRoleDetailsRequest getUserRoleDetailsRequest() {
		CreateRoleDetailsRequest roleDetailsRequest = new CreateRoleDetailsRequest();
		roleDetailsRequest.setRoleName("roleName123");
		roleDetailsRequest.setDescription("description123");
		roleDetailsRequest.setWritePermission(false);
		return roleDetailsRequest;
	}
}
