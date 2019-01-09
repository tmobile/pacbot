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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.CreateUpdateRuleDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.Rule;
import com.tmobile.pacman.api.admin.repository.service.RuleService;

@RunWith(MockitoJUnitRunner.class)
public class RuleControllerTest {
	private MockMvc mockMvc;

	private Principal principal;

	@Mock
	private RuleService ruleService;

	@InjectMocks
	private RuleController ruleController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(ruleController)
				/* .addFilters(new CORSFilter()) */
				.build();
		principal = Mockito.mock(Principal.class);
	}

	@Test
	public void getRulesTest() throws Exception {
		List<Rule> ruleDetails = new ArrayList<Rule>();
		Rule ruleDetail = getRuleDetailsResponse();
		ruleDetails.add(ruleDetail);
		Page<Rule> allRules = new PageImpl<Rule>(ruleDetails, new PageRequest(0, 1), ruleDetails.size());

		when(ruleService.getRules(anyString(), anyInt(), anyInt())).thenReturn(allRules);
		mockMvc.perform(get("/rule/list").param("searchTerm", StringUtils.EMPTY).param("page", "0").param("size", "1"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getRulesExceptionTest() throws Exception {
		when(ruleService.getRules(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/rule/list").param("searchTerm", StringUtils.EMPTY).param("page", "0").param("size", "1"))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getRulesByIdTest() throws Exception {
		when(ruleService.getByRuleId(eq(StringUtils.EMPTY))).thenReturn(getRuleDetailsResponse());
		mockMvc.perform(get("/rule/details-by-id")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success"))).andExpect(jsonPath("$.data.ruleId", is("ruleId123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getRulesByIdExceptionTest() throws Exception {
		when(ruleService.getByRuleId(eq(StringUtils.EMPTY))).thenThrow(Exception.class);
		mockMvc.perform(get("/rule/details-by-id").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getAllAlexaKeywordsTest() throws Exception {
		when(ruleService.getAllAlexaKeywords()).thenReturn(getAllAlexaKeywordsResponse());
		mockMvc.perform(get("/rule/alexa-keywords")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success"))).andExpect(jsonPath("$.data[0]", is("alexaKeywords1")))
				.andExpect(jsonPath("$.data", hasSize(3)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllAlexaKeywordsExceptionTest() throws Exception {
		when(ruleService.getAllAlexaKeywords()).thenThrow(Exception.class);
		mockMvc.perform(get("/rule/alexa-keywords").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void invokeRuleTest() throws Exception {
		String invocationId = "123";
		byte[] ruleOptionalParamsContent = toJson(Lists.newArrayList());
		when(ruleService.invokeRule(any(), any())).thenReturn(invocationId);
		mockMvc.perform(post("/rule/invoke").param("ruleId", "ruleId123").content(ruleOptionalParamsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success"))).andExpect(jsonPath("$.data", is("123")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void invokeRuleExceptionTest() throws Exception {
		byte[] ruleOptionalParamsContent = toJson(Lists.newArrayList());
		when(ruleService.invokeRule(anyString(), anyList())).thenThrow(Exception.class);
		mockMvc.perform(post("/rule/invoke").param("ruleId", "ruleId123").content(ruleOptionalParamsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void enableDisableRuleTest() throws Exception {
		when(ruleService.enableDisableRule(anyString(), anyString(), anyString()))
				.thenReturn(AdminConstants.RULE_DISABLE_ENABLE_SUCCESS);
		mockMvc.perform(post("/rule/enable-disable").principal(principal).param("ruleId", "ruleId123")
				.param("action", "action123").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success"))).andDo(print())
				.andExpect(jsonPath("$.data", is(AdminConstants.RULE_DISABLE_ENABLE_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void enableDisableRuleExceptionTest() throws Exception {
		when(ruleService.enableDisableRule(any(), any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/rule/enable-disable").principal(principal).param("ruleId", "ruleId123")
				.param("action", "action123").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isExpectationFailed()).andDo(print())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void createRuleTest() throws Exception {
		byte[] ruleDetailsContent = toJson(getCreateUpdateRuleDetailsRequest());
		MultipartFile firstFile = getMockMultipartFile();
		when(ruleService.createRule(any(), any(), any())).thenReturn(AdminConstants.RULE_CREATION_SUCCESS);
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rule/create").file("file", firstFile.getBytes())
				.principal(principal).content(ruleDetailsContent).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isOk()).andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.RULE_CREATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createRuleExceptionTest() throws Exception {
		byte[] ruleDetailsContent = toJson(getCreateUpdateRuleDetailsRequest());
		MultipartFile firstFile = getMockMultipartFile();
		when(ruleService.createRule(any(), any(), any())).thenThrow(Exception.class);
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rule/create").file("file", firstFile.getBytes())
				.principal(principal).content(ruleDetailsContent).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void updateRuleTest() throws Exception {
		byte[] ruleDetailsContent = toJson(getCreateUpdateRuleDetailsRequest());
		MultipartFile firstFile = getMockMultipartFile();
		when(ruleService.updateRule(any(), any(), any())).thenReturn(AdminConstants.RULE_CREATION_SUCCESS);
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rule/update").file("file", firstFile.getBytes())
				.principal(principal).content(ruleDetailsContent).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isOk()).andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.RULE_CREATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateRuleExceptionTest() throws Exception {
		byte[] ruleDetailsContent = toJson(getCreateUpdateRuleDetailsRequest());
		MultipartFile firstFile = getMockMultipartFile();
		when(ruleService.updateRule(any(), any(), any())).thenThrow(Exception.class);
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rule/update").file("file", firstFile.getBytes())
				.principal(principal).content(ruleDetailsContent).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void getAllRuleCategoryTest() throws PacManException {
		
		when(ruleService.getAllRuleCategories()).thenReturn(new ArrayList<>());
        assertThat(ruleController.getAllRuleCategory(), is(notNullValue()));
        
        when(ruleService.getAllRuleCategories()).thenThrow(new PacManException("error"));
        assertTrue(ruleController.getAllRuleCategory().getStatusCode() == HttpStatus.EXPECTATION_FAILED);
	}

	private MultipartFile getMockMultipartFile() {
		return new MockMultipartFile("data", "rule.jar", "multipart/form-data", "rule content".getBytes());
	}

	private CreateUpdateRuleDetails getCreateUpdateRuleDetailsRequest() {
		CreateUpdateRuleDetails ruleDetails = new CreateUpdateRuleDetails();
		ruleDetails.setRuleId("ruleId123");
		ruleDetails.setPolicyId("policyId123");
		ruleDetails.setRuleName("ruleName123");
		ruleDetails.setTargetType("targetType123");
		ruleDetails.setAssetGroup("assetGroup123");
		ruleDetails.setAlexaKeyword("alexaKeyword123");
		ruleDetails.setRuleParams("ruleParams123");
		ruleDetails.setRuleFrequency("ruleFrequency123");
		ruleDetails.setRuleExecutable("ruleExecutable123");
		ruleDetails.setRuleRestUrl("ruleRestUrl123");
		ruleDetails.setRuleType("ruleType123");
		ruleDetails.setStatus("status123");
		ruleDetails.setDisplayName("displayName123");
		ruleDetails.setDataSource("dataSource123");
		ruleDetails.setIsAutofixEnabled(false);
		ruleDetails.setIsFileChanged(false);
		return ruleDetails;
	}

	private Collection<String> getAllAlexaKeywordsResponse() {
		Collection<String> allAlexaKeywords = new ArrayList<String>();
		allAlexaKeywords.add("alexaKeywords1");
		allAlexaKeywords.add("alexaKeywords2");
		allAlexaKeywords.add("alexaKeywords3");
		return allAlexaKeywords;
	}

	private Rule getRuleDetailsResponse() {
		Rule rule = new Rule();
		rule.setRuleId("ruleId123");
		rule.setRuleUUID("ruleUUID123");
		rule.setPolicyId("policyId123");
		rule.setRuleName("ruleName123");
		rule.setTargetType("targetType123");
		rule.setAssetGroup("assetGroup123");
		rule.setAlexaKeyword("alexaKeyword123");
		rule.setRuleParams("ruleParams123");
		rule.setRuleFrequency("ruleFrequency123");
		rule.setRuleExecutable("ruleExecutable123");
		rule.setRuleRestUrl("ruleRestUrl123");
		rule.setRuleType("ruleType123");
		rule.setRuleArn("ruleArn123");
		rule.setStatus("status123");
		rule.setUserId("userId123");
		rule.setDisplayName("displayName123");
		rule.setCreatedDate(new Date());
		rule.setModifiedDate(new Date());
		return rule;
	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}
