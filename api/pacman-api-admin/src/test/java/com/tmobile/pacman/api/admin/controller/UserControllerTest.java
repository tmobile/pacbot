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
package com.tmobile.pacman.api.admin.controller;

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tmobile.pacman.api.admin.domain.UserDetails;
import com.tmobile.pacman.api.admin.repository.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
	private MockMvc mockMvc;
	
	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
				.build();
	}
	
	@Test
	public void getUserByEmailIdTest() throws Exception {
		UserDetails userDetails = getUserDetails();
		when(userService.getUserByEmailId(anyString())).thenReturn(userDetails);
		mockMvc.perform(get("/users/list")
				.param("emailId", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")));
	}

	
	@Test
	@SuppressWarnings("unchecked")
	public void getUserByEmailIdExceptionTest() throws Exception {
		when(userService.getUserByEmailId(anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/users/list")
				.param("emailId", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	private UserDetails getUserDetails() {
		UserDetails userDetails = new UserDetails();
		userDetails.setDefaultAssetGroup("defaultAssetGroup123");
		userDetails.setEmail("email123");
		userDetails.setFirstName("firstName123");
		userDetails.setLastName("lastName123");
		userDetails.setUserId("userId123");
		userDetails.setUserName("userName123");
		userDetails.setUserRoles("userRoles123");
		return userDetails;
	}
}

