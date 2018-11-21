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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.AttributeValuesRequest;
import com.tmobile.pacman.api.admin.domain.CreateUpdateTargetTypeDetailsRequest;
import com.tmobile.pacman.api.admin.domain.TargetTypeAttribute;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypesProjections;
import com.tmobile.pacman.api.admin.repository.model.TargetTypes;
import com.tmobile.pacman.api.admin.repository.service.AssetGroupTargetDetailsService;
import com.tmobile.pacman.api.admin.repository.service.TargetTypesService;

@RunWith(MockitoJUnitRunner.class)
public class TargetTypesControllerTest {

	private MockMvc mockMvc;
	
	private Principal principal;

	@Mock
	private TargetTypesService targetTypesService;

	@Mock
	private AssetGroupTargetDetailsService assetGroupTargetDetailsService;

	@InjectMocks
	private TargetTypesController targetTypesController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(targetTypesController)
				.build();
		principal = Mockito.mock(Principal.class);
	}

	@Test
	public void getAllTargetTypesDetailsTest() throws Exception {
		List<TargetTypesProjections> targetTypesDetails = new ArrayList<TargetTypesProjections>();
		targetTypesDetails.add(getTargetTypesProjections());
		Page<TargetTypesProjections> allRules = new PageImpl<TargetTypesProjections>(targetTypesDetails,new PageRequest(0, 1), targetTypesDetails.size());
		when(targetTypesService.getAllTargetTypeDetails(anyString(), anyInt(), anyInt())).thenReturn(allRules);
		mockMvc.perform(get("/target-types/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllTargetTypesDetailsExceptionTest() throws Exception {
		when(targetTypesService.getAllTargetTypeDetails(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/target-types/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getAllTargetTypesCategoriesTest() throws Exception {
		List<String> allCategories = new ArrayList<String>();
		allCategories.add("Category1");
		allCategories.add("Category2");
		allCategories.add("Category3");
		when(targetTypesService.getAllTargetTypesCategories()).thenReturn(allCategories);
		mockMvc.perform(get("/target-types/list-categories"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", hasSize(3)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllTargetTypesCategoriesExceptionTest() throws Exception {
		when(targetTypesService.getAllTargetTypesCategories()).thenThrow(Exception.class);
		mockMvc.perform(get("/target-types/list-categories")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getTargetTypesByAssetGroupNameTest() throws Exception {
		TargetTypeRuleDetails targetTypeRuleDetails = new TargetTypeRuleDetails();
		targetTypeRuleDetails.setTargetName("targetNameTest1");
		targetTypeRuleDetails.setRules(Lists.newArrayList());
		targetTypeRuleDetails.setAllRules(Lists.newArrayList());
		List<TargetTypeRuleDetails> allTargetTypeRuleDetails = Lists.newArrayList();
		allTargetTypeRuleDetails.add(targetTypeRuleDetails);
		when(assetGroupTargetDetailsService.getTargetTypesByAssetGroupName(eq(StringUtils.EMPTY)))
				.thenReturn(allTargetTypeRuleDetails);
		
		mockMvc.perform(get("/target-types/list-by-asset-group-name"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data[0].targetName", is("targetNameTest1")))
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getTargetTypesByAssetGroupNameExceptionTest() throws Exception {
		when(assetGroupTargetDetailsService.getTargetTypesByAssetGroupName(eq(StringUtils.EMPTY))).thenThrow(Exception.class);
		
		mockMvc.perform(get("/target-types/list-by-asset-group-name")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getTargetTypesByNameTest() throws Exception {
		TargetTypes targetTypes = new TargetTypes();
		targetTypes.setTargetName("targetTypeName123");
		targetTypes.setTargetDesc("targetTypeDesc123");
		when(targetTypesService.getTargetTypesByName(eq(StringUtils.EMPTY))).thenReturn(targetTypes);
		
		mockMvc.perform(get("/target-types/list-by-target-type-name"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data.targetName", is("targetTypeName123")))
				.andExpect(jsonPath("$.data.targetDesc", is("targetTypeDesc123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getTargetTypesByNameExceptionTest() throws Exception {
		when(targetTypesService.getTargetTypesByName(eq(StringUtils.EMPTY))).thenThrow(Exception.class);
		mockMvc.perform(get("/target-types/list-by-target-type-name")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getTargetTypesNamesByDataSourceNameTest() throws Exception {
		List<String> allDataSourceNames = new ArrayList<String>();
		allDataSourceNames.add("DataSourceName1");
		allDataSourceNames.add("DataSourceName2");
		allDataSourceNames.add("DataSourceName3");
		when(targetTypesService.getTargetTypesNamesByDataSourceName(eq(StringUtils.EMPTY)))
				.thenReturn(allDataSourceNames);
		
		mockMvc.perform(get("/target-types/list-names-by-datasource")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", hasSize(3)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getTargetTypesNamesByDataSourceNameExceptionTest() throws Exception {
		when(targetTypesService.getTargetTypesNamesByDataSourceName(eq(StringUtils.EMPTY))).thenThrow(Exception.class);
		mockMvc.perform(get("/target-types/list-names-by-datasource")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void addTargetTypeDetailsTest() throws Exception {
		byte[] targetTypeDetailsContent = toJson(getCreateUpdateTargetTypeDetailsRequest());
		when(targetTypesService.addTargetTypeDetails(any(), any())).thenReturn(AdminConstants.TARGET_TYPE_CREATION_SUCCESS);
		mockMvc.perform(post("/target-types/create").principal(principal)
				.content(targetTypeDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.TARGET_TYPE_CREATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addTargetTypeDetailsExceptionTest() throws Exception {
		byte[] targetTypeDetailsContent = toJson(getCreateUpdateTargetTypeDetailsRequest());
		when(targetTypesService.addTargetTypeDetails(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/target-types/create")
				.content(targetTypeDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getAllTargetTypesByDomainListTest() throws Exception {
		byte[] domainsContent = toJson(getDomains());
		when(targetTypesService.getAllTargetTypesByDomainList(any())).thenReturn(getTargetTypesDetails());
		mockMvc.perform(post("/target-types/list-by-domains")
				.content(domainsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data[0].targetName", is("targetTypeName123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllTargetTypesByDomainListExceptionTest() throws Exception {
		byte[] domainsContent = toJson(getDomains());
		when(targetTypesService.getAllTargetTypesByDomainList(any())).thenThrow(Exception.class);
		mockMvc.perform(post("/target-types/list-by-domains")
				.content(domainsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void updateTargetTypeDetailsTest() throws Exception {
		byte[] targetTypeDetailsContent = toJson(getCreateUpdateTargetTypeDetailsRequest());
		when(targetTypesService.updateTargetTypeDetails(any(), any())).thenReturn(AdminConstants.TARGET_TYPE_UPDATION_SUCCESS);
		mockMvc.perform(post("/target-types/update").principal(principal)
				.content(targetTypeDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.TARGET_TYPE_UPDATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateTargetTypeDetailsExceptionTest() throws Exception {
		byte[] targetTypeDetailsContent = toJson(getCreateUpdateTargetTypeDetailsRequest());
		when(targetTypesService.updateTargetTypeDetails(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/target-types/update")
				.content(targetTypeDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getTargetTypeAttributesTest() throws Exception {
		byte[] targetTypesDetailsContent = toJson(getTargetTypesDetails());
		when(targetTypesService.getTargetTypeAttributes(any())).thenReturn(getTargetTypeAttributeDetails());
		mockMvc.perform(post("/target-types/list-target-type-attributes")
				.content(targetTypesDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data[0].includeAll", is(false)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getTargetTypeAttributesExceptionTest() throws Exception {
		byte[] targetTypesDetailsContent = toJson(getTargetTypesDetails());
		when(targetTypesService.getTargetTypeAttributes(any())).thenThrow(Exception.class);
		mockMvc.perform(post("/target-types/list-target-type-attributes")
				.content(targetTypesDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getAttributeValuesTest() throws Exception {
		byte[] attributeValuesRequestContent = toJson(getAttributeValuesRequest());
		when(targetTypesService.getAttributeValues(any())).thenReturn(getTargetTypeAttributeValuesDetails());
		mockMvc.perform(post("/target-types/list-target-type-attributes-values")
				.content(attributeValuesRequestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data.name", is("name123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAttributeValuesExceptionTest() throws Exception {
		byte[] attributeValuesRequestContent = toJson(getAttributeValuesRequest());
		when(targetTypesService.getAttributeValues(any())).thenThrow(Exception.class);
		mockMvc.perform(post("/target-types/list-target-type-attributes-values")
				.content(attributeValuesRequestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	private Map<String, Object> getTargetTypeAttributeValuesDetails() {
		Map<String, Object> targetTypeAttributeValues = Maps.newHashMap();
		targetTypeAttributeValues.put("name", "name123");
		targetTypeAttributeValues.put("value", "value123");
		return targetTypeAttributeValues;
	}

	private AttributeValuesRequest getAttributeValuesRequest() {
		AttributeValuesRequest attributeValuesRequest = new AttributeValuesRequest();
		attributeValuesRequest.setIndex("endpoint123");
		attributeValuesRequest.setPayload("payload123");
		return attributeValuesRequest;
	}

	private List<TargetTypeAttribute> getTargetTypeAttributeDetails() {
		TargetTypeAttribute targetTypeAttribute = new TargetTypeAttribute();
		targetTypeAttribute.setTargetName("targetTypeName123");
		targetTypeAttribute.setAllAttributesName(Lists.newArrayList());
		targetTypeAttribute.setAttributes(Lists.newArrayList());
		targetTypeAttribute.setIncludeAll(false);
		List<TargetTypeAttribute> allTargetTypeAttributes = Lists.newArrayList();
		allTargetTypeAttributes.add(targetTypeAttribute);
		return allTargetTypeAttributes;
	}

	private List<String> getDomains() {
		List<String> domains = Lists.newArrayList();
		domains.add("Domain123");
		domains.add("Domain234");
		domains.add("Domain345");
		return domains;
	}

	private List<TargetTypes> getTargetTypesDetails() {
		TargetTypes targetTypes = new TargetTypes();
		targetTypes.setTargetName("targetTypeName123");
		targetTypes.setTargetDesc("targetTypeDesc123");
		List<TargetTypes> allTargetTypes = Lists.newArrayList();
		allTargetTypes.add(targetTypes);
		return allTargetTypes;
	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}

	private CreateUpdateTargetTypeDetailsRequest getCreateUpdateTargetTypeDetailsRequest() {
		CreateUpdateTargetTypeDetailsRequest targetTypeDetailsRequest = new CreateUpdateTargetTypeDetailsRequest();
		targetTypeDetailsRequest.setName("TargetTypeName123");
		targetTypeDetailsRequest.setDesc("TargetTypeDesc123");
		targetTypeDetailsRequest.setDomain("Domain123");
		targetTypeDetailsRequest.setCategory("Category123");
		return targetTypeDetailsRequest;
	}

	private TargetTypesProjections getTargetTypesProjections() {
		return new TargetTypesProjections() {
			@Override
			public String getTargetName() {
				return "TargetName123";
			}

			@Override
			public String getTargetDesc() {
				return "TargetDesc123";
			}

			@Override
			public String getTargetConfig() {
				return "TargetConfig123";
			}

			@Override
			public String getEndpoint() {
				return "Endpoint123";
			}

			@Override
			public String getDomain() {
				return "Domain123";
			}

			@Override
			public String getDataSourceName() {
				return "DataSourceName123";
			}

			@Override
			public String getCategory() {
				return "Category123";
			}
		};
	}
}
