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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserRepository;
import com.tmobile.pacman.api.admin.repository.UserRolesMappingRepository;
import com.tmobile.pacman.api.admin.repository.model.User;
import com.tmobile.pacman.api.admin.repository.model.UserPreferences;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserPreferencesService userPreferencesService;
	
	@Mock
	private UserRolesMappingRepository userRolesMappingRepository;

	@Test
	public void getUserByEmailIdTest() throws PacManException {
		User user = getUserDetails();
		List<String[]> userRoles = getUserRoles();
		UserPreferences userPreferences = getUserPreferences();
		when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(user);
		when(userRolesMappingRepository.findAllUserRoleDetailsByUserIdIgnoreCase(user.getUserId())).thenReturn(userRoles);
		when(userPreferencesService.getUserPreferencesByNtId(user.getUserId())).thenReturn(userPreferences);
		assertThat(userService.getUserByEmailId(anyString()).getEmail(), is("email123"));
	}
	
	@Test
	public void getUserByEmailIdEmailIdNotFoundTest() throws PacManException {
		User user = getUserDetails();
		when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(user);
		assertThatThrownBy(() -> userService.getUserByEmailId(null)).isInstanceOf(PacManException.class);
	}
	
	@Test
	public void getUserByEmailIdUserNotFoundTest() throws PacManException {
		when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(null);
		assertThat(userService.getUserByEmailId(anyString()).getEmail(), is(""));
	}
	
	@Test
	public void getUserByEmailIdEmailIdFoundTest() throws PacManException {
		when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(null);
		assertThat(userService.getUserByEmailId("emailId").getEmail(), is("emailId"));
	}
	
	private List<String[]> getUserRoles() {
		List<String[]> roles = Lists.newArrayList();
		String[] role = {"ROLE_ADMIN", "ROLE_USER"};
		roles.add(role);
		return roles;
	}

	private UserPreferences getUserPreferences() {
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setDefaultAssetGroup("defaultAssetGroup123");
		userPreferences.setId(123l);
		userPreferences.setRecentlyViewedAG("recentlyViewedAG123");
		userPreferences.setUserId("userId123");
		return userPreferences;
	}

	private User getUserDetails() {
		User user = new User();
		user.setCreatedDate(new Date());
		user.setEmail("email123");
		user.setFirstName("firstName123");
		user.setId(123l);
		user.setLastName("lastName123");
		user.setModifiedDate(new Date());
		user.setUserId("userId123");
		user.setUserName("userName123");
		return user;
	}
}
