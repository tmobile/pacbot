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
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_ALLOCATION_SUCCESS;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.UserRoleConfigRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesMappingResponse;
import com.tmobile.pacman.api.admin.repository.model.User;
import com.tmobile.pacman.api.admin.repository.service.UserRolesMappingService;


@RunWith(MockitoJUnitRunner.class)
public class UserRolesMappingControllerTest {
	private MockMvc mockMvc;
	
	private Principal principal;

	@Mock
	private UserRolesMappingService userRolesMappingService;

	@InjectMocks
	private UserRolesMappingController userRolesMappingController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userRolesMappingController)
				.build();
		principal = Mockito.mock(Principal.class);
	}

	@Test
	public void getAllUserRolesMappingTest() throws Exception {
		List<UserRolesMappingResponse> userRolesDetails = new ArrayList<UserRolesMappingResponse>();
		userRolesDetails.add(getUserRolesMappingDetails());
		Page<UserRolesMappingResponse> allUserRolesDetails = new PageImpl<UserRolesMappingResponse>(userRolesDetails,new PageRequest(0, 1), userRolesDetails.size());
		when(userRolesMappingService.getAllUserRolesMapping(anyString(), anyInt(), anyInt())).thenReturn(allUserRolesDetails);
		mockMvc.perform(get("/users-roles/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getAllUserRolesMappingExceptionTest() throws Exception {
		when(userRolesMappingService.getAllUserRolesMapping(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/users-roles/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void allocateUserRoleTest() throws Exception {
		byte[] roleAllocateDetailsContent = toJson(getRoleAllocateDetailsRequest());
		when(userRolesMappingService.allocateUserRole(any(), any())).thenReturn(USER_ROLE_ALLOCATION_SUCCESS);
		mockMvc.perform(post("/users-roles/allocate").principal(principal)
				.content(roleAllocateDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(USER_ROLE_ALLOCATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void allocateUserRoleExceptionTest() throws Exception {
		byte[] roleAllocateDetailsContent = toJson(getRoleAllocateDetailsRequest());
		when(userRolesMappingService.allocateUserRole(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/users-roles/allocate").principal(principal)
				.content(roleAllocateDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	private UserRoleConfigRequest getRoleAllocateDetailsRequest() {
		UserRoleConfigRequest userRoleConfigRequest = new UserRoleConfigRequest();
		userRoleConfigRequest.setRoleId("roleId");
		List<User> userDetails = Lists.newArrayList();
		User userDetailsRequest = new User();
		userDetailsRequest.setUserId("userId");
		userDetails.add(userDetailsRequest);
		userRoleConfigRequest.setUserDetails(userDetails);
		return userRoleConfigRequest;
	}

	private UserRolesMappingResponse getUserRolesMappingDetails() {
		UserRolesMappingResponse userRolesMappingResponse = new UserRolesMappingResponse();
		userRolesMappingResponse.setUserRoleId("userRoleId123");
		userRolesMappingResponse.setUserId("userId123");
		userRolesMappingResponse.setRoles(Lists.newArrayList());
		return userRolesMappingResponse;
	}
	
	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}

