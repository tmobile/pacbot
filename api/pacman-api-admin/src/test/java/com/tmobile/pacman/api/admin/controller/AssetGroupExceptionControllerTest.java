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
import com.tmobile.pacman.api.admin.domain.AssetGroupExceptionProjections;
import com.tmobile.pacman.api.admin.domain.CreateAssetGroupExceptionDetailsRequest;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupExceptionRequest;
import com.tmobile.pacman.api.admin.domain.StickyExceptionResponse;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleViewDetails;
import com.tmobile.pacman.api.admin.repository.service.AssetGroupExceptionService;

@RunWith(MockitoJUnitRunner.class)
public class AssetGroupExceptionControllerTest
{
	private Principal principal;
	
	private MockMvc mockMvc;
    
	@Mock
	private AssetGroupExceptionService assetGroupExceptionService;

	@InjectMocks
    private AssetGroupExceptionController assetGroupExceptionController;
	
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(assetGroupExceptionController)
                .build();
        principal = Mockito.mock(Principal.class);
    }

	@Test
	public void getAllAssetGroupExceptionsTest() throws Exception {
		List<AssetGroupExceptionProjections> assetGroupExceptionDetails = Lists.newArrayList();
		assetGroupExceptionDetails.add(getAssetGroupExceptionProjections());
		Page<AssetGroupExceptionProjections> allAssetGroupExceptionDetails = new PageImpl<AssetGroupExceptionProjections>(assetGroupExceptionDetails, new PageRequest(0, 1), assetGroupExceptionDetails.size());
		when(assetGroupExceptionService.getAllAssetGroupExceptions(anyString(), anyInt(), anyInt())).thenReturn(allAssetGroupExceptionDetails);
		mockMvc.perform(get("/asset-group-exception/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data.content[0].targetType", is("TargetType123")))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllAssetGroupExceptionsExceptionTest() throws Exception {
		when(assetGroupExceptionService.getAllAssetGroupExceptions(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/asset-group-exception/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void getAllTargetTypesByExceptionNameAndDataSourceTest() throws Exception {
		when(assetGroupExceptionService.getAllTargetTypesByExceptionNameAndDataSource(anyString(), anyString())).thenReturn(getStickyExceptionResponseResponse());
		mockMvc.perform(get("/asset-group-exception/list-by-name-and-datasource")
				.param("exceptionName", "exceptionName123")
				.param("dataSource", "dataSource123")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data.exceptionName", is("exceptionName123")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getAllTargetTypesByExceptionNameAndDataSourceExceptionTest() throws Exception {
		when(assetGroupExceptionService.getAllTargetTypesByExceptionNameAndDataSource(anyString(), anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/asset-group-exception/list-by-name-and-datasource")
				.param("exceptionName", "exceptionName123")
				.param("dataSource", "dataSource123"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void createAssetGroupExceptionsTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getCreateAssetGroupExceptionDetailsRequest());
		when(assetGroupExceptionService.createAssetGroupExceptions(any(), any())).thenReturn(AdminConstants.CONFIG_STICKY_EXCEPTION_SUCCESS);
		mockMvc.perform(post("/asset-group-exception/configure").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.CONFIG_STICKY_EXCEPTION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createAssetGroupExceptionsExceptionTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getCreateAssetGroupExceptionDetailsRequest());
		when(assetGroupExceptionService.createAssetGroupExceptions(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/asset-group-exception/configure").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(AdminConstants.UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void deleteAssetGroupExceptionsTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getDeleteAssetGroupExceptionRequest());
		when(assetGroupExceptionService.deleteAssetGroupExceptions(any(), any())).thenReturn(AdminConstants.CONFIG_STICKY_EXCEPTION_SUCCESS);
		mockMvc.perform(post("/asset-group-exception/delete").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.CONFIG_STICKY_EXCEPTION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void deleteAssetGroupExceptionsExceptionTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getDeleteAssetGroupExceptionRequest());
		when(assetGroupExceptionService.deleteAssetGroupExceptions(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/asset-group-exception/delete").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	

	private DeleteAssetGroupExceptionRequest getDeleteAssetGroupExceptionRequest() {
		DeleteAssetGroupExceptionRequest deleteAssetGroupExceptionRequest = new DeleteAssetGroupExceptionRequest();
		deleteAssetGroupExceptionRequest.setExceptionName("exceptionName123");
		deleteAssetGroupExceptionRequest.setGroupName("groupName123");
		return deleteAssetGroupExceptionRequest;
	}

	private StickyExceptionResponse getStickyExceptionResponseResponse() {
		List<TargetTypeRuleViewDetails> allTargetTypeRuleViewDetails = Lists.newArrayList();
		TargetTypeRuleViewDetails targetTypeRuleViewDetails = new TargetTypeRuleViewDetails();
		targetTypeRuleViewDetails.setAdded(false);
		targetTypeRuleViewDetails.setAllRules("[]");
		targetTypeRuleViewDetails.setRules("[]");
		targetTypeRuleViewDetails.setTargetName("targetName123");
		allTargetTypeRuleViewDetails.add(targetTypeRuleViewDetails);
		StickyExceptionResponse stickyExceptionResponse = new StickyExceptionResponse();
		stickyExceptionResponse.setDataSource("dataSource123");
		stickyExceptionResponse.setExceptionName("exceptionName123");
		stickyExceptionResponse.setExceptionReason("exceptionReason123");
		stickyExceptionResponse.setExpiryDate("expiryDate123");
		stickyExceptionResponse.setTargetTypes(allTargetTypeRuleViewDetails);
		return stickyExceptionResponse;
	}
	
	private CreateAssetGroupExceptionDetailsRequest getCreateAssetGroupExceptionDetailsRequest() {
		List<TargetTypeRuleDetails> allTargetTypeRuleDetails = Lists.newArrayList();
		TargetTypeRuleDetails targetTypeRuleDetails = new TargetTypeRuleDetails();
		targetTypeRuleDetails.setAllRules("[]");
		targetTypeRuleDetails.setRules(Lists.newArrayList());
		targetTypeRuleDetails.setTargetName("targetName123");
		allTargetTypeRuleDetails.add(targetTypeRuleDetails);
		
		CreateAssetGroupExceptionDetailsRequest createAssetGroupExceptionDetailsRequest = new CreateAssetGroupExceptionDetailsRequest();
		createAssetGroupExceptionDetailsRequest.setAssetGroup("assetGroup123");
		createAssetGroupExceptionDetailsRequest.setDataSource("dataSource123");
		createAssetGroupExceptionDetailsRequest.setExceptionName("exceptionName123");
		createAssetGroupExceptionDetailsRequest.setExceptionReason("exceptionReason123");
		createAssetGroupExceptionDetailsRequest.setExpiryDate("expiryDate123");
		createAssetGroupExceptionDetailsRequest.setTargetTypes(allTargetTypeRuleDetails);
		return createAssetGroupExceptionDetailsRequest;
	}

	private AssetGroupExceptionProjections getAssetGroupExceptionProjections() {
		return new AssetGroupExceptionProjections() {

			@Override
			public String getTargetType() {
				return "TargetType123";
			}

			@Override
			public String getRuleName() {
				return "RuleName123";
			}

			@Override
			public String getRuleId() {
				return "RuleId123";
			}

			@Override
			public long getId() {
				return 123;
			}

			@Override
			public String getExpiryDate() {
				return "ExpiryDate123";
			}

			@Override
			public String getExceptionReason() {
				return "ExceptionReason123";
			}

			@Override
			public String getExceptionName() {
				return "ExceptionName123";
			}

			@Override
			public String getDataSource() {
				return "DataSource123";
			}

			@Override
			public String getAssetGroup() {
				return "AssetGroup123";
			}
		};
	}
	
	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}

