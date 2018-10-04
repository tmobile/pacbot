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
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_CREATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.USER_ROLE_UPDATION_SUCCESS;
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
import java.util.Date;
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
import com.google.common.collect.Sets;
import com.tmobile.pacman.api.admin.domain.CreateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UpdateRoleDetailsRequest;
import com.tmobile.pacman.api.admin.domain.UserRolesResponse;
import com.tmobile.pacman.api.admin.repository.model.UserRoles;
import com.tmobile.pacman.api.admin.repository.service.UserRolesService;

@RunWith(MockitoJUnitRunner.class)
public class UserRolesControllerTest {
	private MockMvc mockMvc;
	
	private Principal principal;

	@Mock
	private UserRolesService userRolesService;

	@InjectMocks
	private UserRolesController userRolesController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userRolesController)
				.build();
		principal = Mockito.mock(Principal.class);
	}
	
	@Test
	public void getAllUserRolesTest() throws Exception {
		List<UserRolesResponse> userRolesDetails = new ArrayList<UserRolesResponse>();
		userRolesDetails.add(getUserRolesResponseDetails());
		Page<UserRolesResponse> allUserRolesDetails = new PageImpl<UserRolesResponse>(userRolesDetails,new PageRequest(0, 1), userRolesDetails.size());
		when(userRolesService.getAllUserRoles(anyString(), anyInt(), anyInt())).thenReturn(allUserRolesDetails);
		mockMvc.perform(get("/roles/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllUserRolesExceptionTest() throws Exception {
		when(userRolesService.getAllUserRoles(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/roles/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	
	@Test
	public void createUserRoleTest() throws Exception {
		byte[] createUserRoleDetailsContent = toJson(getUserRoleDetailsRequest());
		when(userRolesService.createUserRole(any(), any())).thenReturn(USER_ROLE_CREATION_SUCCESS);
		mockMvc.perform(post("/roles/create").principal(principal)
				.content(createUserRoleDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(USER_ROLE_CREATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createUserRoleExceptionTest() throws Exception {
		byte[] createUserRoleDetailsContent = toJson(getUserRoleDetailsRequest());
		when(userRolesService.createUserRole(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/roles/create").principal(principal)
				.content(createUserRoleDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void updateUserRoleTest() throws Exception {
		byte[] udateUserRoleDetailsContent = toJson(getUpdateRoleDetailsRequest());
		when(userRolesService.updateUserRole(any(), any())).thenReturn(USER_ROLE_UPDATION_SUCCESS);
		mockMvc.perform(post("/roles/update").principal(principal)
				.content(udateUserRoleDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(USER_ROLE_UPDATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateUserRoleExceptionTest() throws Exception {
		byte[] udateUserRoleDetailsContent = toJson(getUpdateRoleDetailsRequest());
		when(userRolesService.updateUserRole(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/roles/update").principal(principal)
				.content(udateUserRoleDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
/*	@ApiOperation(httpMethod = "GET", value = "API to get user role by id", response = Response.class,  produces = MediaType.APPLICATION_JSON_VALUE)
	//@PreAuthorize("@securityService.hasPermission(authentication)")
	@RequestMapping(path = "/details-by-id", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getUserRoleById(
		@ApiParam(value = "provide valid role id", required = true) @RequestParam(defaultValue="", name = "roleId", required = true) Long roleId) {
		try {
			return ResponseUtils.buildSucessResponse(userRolesService.getUserRoleById(roleId));
		} catch (Exception exception) {
			return ResponseUtils.buildFailureResponse(new Exception("failure"), exception.getMessage());
		}
	}
	*/
	@Test
	public void getUserRoleByIdTest() throws Exception {
		UserRoles userRoleDetails = getUserRoleDetails();
		when(userRolesService.getUserRoleById(anyString())).thenReturn(userRoleDetails);
		mockMvc.perform(get("/roles/details-by-id")
				.param("roleId", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
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

	@Test
	@SuppressWarnings("unchecked")
	public void getUserRoleByIdExceptionTest() throws Exception {
		when(userRolesService.getUserRoleById(anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/roles/details-by-id")
				.param("roleId", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	private UpdateRoleDetailsRequest getUpdateRoleDetailsRequest() {
		UpdateRoleDetailsRequest updateRoleDetailsRequest = new UpdateRoleDetailsRequest();
		updateRoleDetailsRequest.setRoleId("123");
		updateRoleDetailsRequest.setRoleName("roleName123");
		updateRoleDetailsRequest.setDescription("description123");
		updateRoleDetailsRequest.setWritePermission(false);
		return updateRoleDetailsRequest;
	}

	private CreateRoleDetailsRequest getUserRoleDetailsRequest() {
		CreateRoleDetailsRequest roleDetailsRequest = new CreateRoleDetailsRequest();
		roleDetailsRequest.setRoleName("roleName123");
		roleDetailsRequest.setDescription("description123");
		roleDetailsRequest.setWritePermission(false);
		return roleDetailsRequest;
	}

	private UserRolesResponse getUserRolesResponseDetails() {
		UserRolesResponse userRolesResponse = new UserRolesResponse();
		userRolesResponse.setCreatedBy("createdBy123");
		userRolesResponse.setCreatedDate(new Date());
		userRolesResponse.setModifiedDate(new Date());
		userRolesResponse.setRoleId("123");
		userRolesResponse.setRoleName("roleName123");
		userRolesResponse.setUsers(Lists.newArrayList());
		return userRolesResponse;
	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}

