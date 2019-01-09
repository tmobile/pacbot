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

import static org.hamcrest.Matchers.hasSize;
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
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.CreateUpdateDomain;
import com.tmobile.pacman.api.admin.repository.model.Domain;
import com.tmobile.pacman.api.admin.repository.service.DomainService;

@RunWith(MockitoJUnitRunner.class)
public class DomainControllerTest {
	
	private MockMvc mockMvc;
	
	private Principal principal;

	@Mock
	private DomainService domainService;

	@InjectMocks
	private DomainController domainController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(domainController)
				.build();
		principal = Mockito.mock(Principal.class);
	}
	
	@Test
	public void getAllTargetTypesDetailsTest() throws Exception {
		List<Domain> allDomains = getAllDomainRespone();
		when(domainService.getAllDomains()).thenReturn(allDomains);
		mockMvc.perform(get("/domains/list"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].domainName", is("domainName123")))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllTargetTypesDetailsExceptionTest() throws Exception {
		when(domainService.getAllDomains()).thenThrow(Exception.class);
		mockMvc.perform(get("/domains/list"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void getAllDomainDetailsTest() throws Exception {
		Object[] domain = {"DomainName123", "DomainDesc123"};
		List<Object[]> domains = Lists.newArrayList();
		domains.add(domain);
		Page<Object[]> allDomains = new PageImpl<Object[]>(domains, new PageRequest(0, 1), domains.size());
		
		when(domainService.getAllDomainDetails(anyString(), anyInt(), anyInt())).thenReturn(allDomains);
		mockMvc.perform(get("/domains/list-details")
				.param("page", "0")
				.param("size", "1")
				.param("searchTerm", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data.content[0][0]", is("DomainName123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllDomainDetailsExceptionTest() throws Exception {
		when(domainService.getAllDomainDetails(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/domains/list-details")
				.param("page", "0")
				.param("size", "1")
				.param("searchTerm", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void getDomainByNameTest() throws Exception {
		when(domainService.getDomainByName(anyString())).thenReturn(getDomainDetails());
		mockMvc.perform(get("/domains/list-by-domain-name")
				.param("domainName", "domainName123"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data.domainName", is("domainName123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getDomainByNameExceptionTest() throws Exception {
		when(domainService.getDomainByName(anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/domains/list-by-domain-name")
				.param("domainName", "domainName123"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void createDomainTest() throws Exception {
		byte[] domainDetailsContent = toJson(getDomainDetailsRequest());
		when(domainService.createDomain(any(), any())).thenReturn(AdminConstants.DOMAIN_CREATION_SUCCESS);
		mockMvc.perform(post("/domains/create").principal(principal)
				.content(domainDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.DOMAIN_CREATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createDomainExceptionTest() throws Exception {
		byte[] domainDetailsContent = toJson(getDomainDetailsRequest());
		when(domainService.createDomain(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/domains/create")
				.content(domainDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void updateDomainTest() throws Exception {
		byte[] domainDetailsContent = toJson(getDomainDetailsRequest());
		when(domainService.updateDomain(any(), any())).thenReturn(AdminConstants.DOMAIN_UPDATION_SUCCESS);
		mockMvc.perform(post("/domains/update").principal(principal)
				.content(domainDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.DOMAIN_UPDATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateDomainExceptionTest() throws Exception {
		byte[] domainDetailsContent = toJson(getDomainDetailsRequest());
		when(domainService.updateDomain(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/domains/update")
				.content(domainDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}
	
	private CreateUpdateDomain getDomainDetailsRequest() {
		CreateUpdateDomain domainDetails = new CreateUpdateDomain();
		domainDetails.setName("domainName123");
		domainDetails.setConfig("domainConfig123");
		domainDetails.setDesc("domainDesc123");
		return domainDetails;
	}
	
	private List<Domain> getAllDomainRespone() {
		List<Domain> allDomains = Lists.newArrayList();
		Domain domain = getDomainDetails();
		allDomains.add(domain);
		return allDomains;
	}

	private Domain getDomainDetails() {
		Domain domain = new Domain();
		domain.setConfig("config123");
		domain.setCreatedDate(new Date());
		domain.setDomainDesc("domainDesc123");
		domain.setDomainName("domainName123");
		domain.setModifiedDate(new Date());
		domain.setUserId("userId123");
		return domain;
	}
	
	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}
