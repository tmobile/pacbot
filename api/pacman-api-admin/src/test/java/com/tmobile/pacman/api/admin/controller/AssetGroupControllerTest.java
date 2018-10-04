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
import static org.junit.Assert.assertEquals;
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
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Sets;
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
import com.tmobile.pacman.api.admin.domain.AssetGroupView;
import com.tmobile.pacman.api.admin.domain.CreateUpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupDetails;
import com.tmobile.pacman.api.admin.repository.service.AssetGroupService;


@RunWith(MockitoJUnitRunner.class)
public class AssetGroupControllerTest {

	private MockMvc mockMvc;
    
	private Principal principal;
	
	@Mock
	private AssetGroupService assetGroupService;

	@InjectMocks
    private AssetGroupController assetGroupController;
	
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(assetGroupController)
                .build();
        principal = Mockito.mock(Principal.class);
    }
	
	@Test
	public void getAllAssetGroupNamesTest() throws Exception {
		Collection<String> allAssetGroupNames = new ArrayList<String>();
		allAssetGroupNames.add("aws-all");
		allAssetGroupNames.add("security");
		allAssetGroupNames.add("sandbox");
		when(assetGroupService.getAllAssetGroupNames()).thenReturn(allAssetGroupNames);
		mockMvc.perform(get("/asset-group/list-names")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data", hasSize(3)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllAssetGroupNamesExceptionTest() throws Exception {
		when(assetGroupService.getAllAssetGroupNames()).thenThrow(Exception.class);
		mockMvc.perform(get("/asset-group/list-names").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void getAllAssetGroupDetailsTest() throws Exception {
		List<AssetGroupView> assetGroupDetail = new ArrayList<AssetGroupView>();
		assetGroupDetail.add(getAssetGroupViewDetails());
		Page<AssetGroupView> allAssetGroupDetails = new PageImpl<AssetGroupView>(assetGroupDetail,new PageRequest(0, 1), assetGroupDetail.size());
		when(assetGroupService.getAllAssetGroupDetails(anyString(), anyInt(), anyInt())).thenReturn(allAssetGroupDetails);
		mockMvc.perform(get("/asset-group/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data.content[0].groupId", is("groupId123")))
				.andExpect(jsonPath("$.message", is("success")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getAllAssetGroupDetailsExceptionTest() throws Exception {
		when(assetGroupService.getAllAssetGroupDetails(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);
		mockMvc.perform(get("/asset-group/list")
				.param("searchTerm", StringUtils.EMPTY)
				.param("page", "0")
				.param("size", "1"))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void updateAssetGroupDetailsTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getCreateUpdateAssetGroupDetailsRequest());
		when(assetGroupService.updateAssetGroupDetails(any(), any())).thenReturn(AdminConstants.ASSET_GROUP_UPDATION_SUCCESS);
		mockMvc.perform(post("/asset-group/update").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.ASSET_GROUP_UPDATION_SUCCESS)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void updateAssetGroupDetailsExceptionTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getCreateUpdateAssetGroupDetailsRequest());
		when(assetGroupService.updateAssetGroupDetails(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/asset-group/update").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void createAssetGroupDetailsTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getCreateUpdateAssetGroupDetailsRequest());
		when(assetGroupService.createAssetGroupDetails(any(), any())).thenReturn(AdminConstants.ASSET_GROUP_CREATION_SUCCESS);
		mockMvc.perform(post("/asset-group/create").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.ASSET_GROUP_CREATION_SUCCESS)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void createAssetGroupDetailsExceptionTest() throws Exception {
		byte[] assetGroupDetailsContent = toJson(getCreateUpdateAssetGroupDetailsRequest());
		when(assetGroupService.createAssetGroupDetails(any(), any())).thenThrow(Exception.class);
		mockMvc.perform(post("/asset-group/create").principal(principal)
				.content(assetGroupDetailsContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}

	@Test
	public void getAssetGroupDetailsByNameAndDataSourceTest() throws Exception {
		when(assetGroupService.getAssetGroupDetailsByIdAndDataSource(anyString(), anyString())).thenReturn(null);
		mockMvc.perform(get("/asset-group/list-by-id-and-datasource")
				.param("assetGroupId", StringUtils.EMPTY)
				.param("dataSource", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getAssetGroupDetailsByNameAndDataSourceExceptionTest() throws Exception {
		when(assetGroupService.getAssetGroupDetailsByIdAndDataSource(anyString(), anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/asset-group/list-by-id-and-datasource")
				.param("assetGroupId", StringUtils.EMPTY)
				.param("dataSource", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void findByGroupNameTest() throws Exception {
		AssetGroupDetails assetGroupDetails = getAssetGroupDetails();
		when(assetGroupService.findByGroupName(anyString())).thenReturn(assetGroupDetails);
		mockMvc.perform(get("/asset-group/list-by-name")
				.param("defaultValue", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findByGroupNameExceptionTest() throws Exception {
		when(assetGroupService.findByGroupName(anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/asset-group/list-by-name")
				.param("defaultValue", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	
	private AssetGroupDetails getAssetGroupDetails() {
		AssetGroupDetails assetGroupDetails = new AssetGroupDetails();
		assetGroupDetails.setGroupId("groupId123");
		assetGroupDetails.setGroupName("groupName123");
		assetGroupDetails.setDataSource("dataSource123");
		assetGroupDetails.setDisplayName("displayName123");
		assetGroupDetails.setGroupType("groupType123");
		assetGroupDetails.setCreatedBy("createdBy123");
		assetGroupDetails.setCreatedUser("createdUser123");
		assetGroupDetails.setCreatedDate("createdDate123");
		assetGroupDetails.setModifiedUser("modifiedUser123");
		assetGroupDetails.setModifiedDate("modifiedDate123");
		assetGroupDetails.setDescription("description123");
		assetGroupDetails.setAliasQuery("aliasQuery123");
		assetGroupDetails.setIsVisible(true);
		assetGroupDetails.setTargetTypes(Sets.newHashSet());
		return assetGroupDetails;
	}

	private Object getCreateUpdateAssetGroupDetailsRequest() {
		CreateUpdateAssetGroupDetails createUpdateAssetGroupDetails = new CreateUpdateAssetGroupDetails();
		createUpdateAssetGroupDetails.setCreatedBy("createdBy");
		createUpdateAssetGroupDetails.setDataSourceName("dataSourceName");
		createUpdateAssetGroupDetails.setDescription("description");
		createUpdateAssetGroupDetails.setDisplayName("displayName");
		createUpdateAssetGroupDetails.setGroupName("groupName");
		createUpdateAssetGroupDetails.setTargetTypes(Lists.newArrayList());
		createUpdateAssetGroupDetails.setType("type");
		createUpdateAssetGroupDetails.setVisible(false);
		return createUpdateAssetGroupDetails;
	}

	private AssetGroupView getAssetGroupViewDetails() {
		
		AssetGroupDetails assetGroupDetails = new AssetGroupDetails();
		assetGroupDetails.setGroupId("groupId123");
		assetGroupDetails.setGroupName("groupName123");
		assetGroupDetails.setDataSource("dataSource123");
		assetGroupDetails.setDisplayName("displayName123");
		assetGroupDetails.setGroupType("groupType123");
		assetGroupDetails.setCreatedBy("createdBy123");
		assetGroupDetails.setCreatedUser("createdUser123");
		assetGroupDetails.setCreatedDate("createdDate123");
		assetGroupDetails.setModifiedUser("modifiedUser123");
		assetGroupDetails.setModifiedDate("modifiedDate123");
		assetGroupDetails.setDescription("description123");
		assetGroupDetails.setAliasQuery("aliasQuery123");
		assetGroupDetails.setIsVisible(true);
		assetGroupDetails.setTargetTypes(Sets.newHashSet());
		assertEquals(assetGroupDetails.getDataSource(), "dataSource123");
		assertEquals(assetGroupDetails.getDisplayName(), "displayName123");
		assertEquals(assetGroupDetails.getGroupType(), "groupType123");
		assertEquals(assetGroupDetails.getCreatedBy(), "createdBy123");
		assertEquals(assetGroupDetails.getCreatedUser(), "createdUser123");
		assertEquals(assetGroupDetails.getCreatedDate(), "createdDate123");
		assertEquals(assetGroupDetails.getModifiedUser(), "modifiedUser123");
		assertEquals(assetGroupDetails.getModifiedDate(), "modifiedDate123");
		assertEquals(assetGroupDetails.getDescription(), "description123");
		assertEquals(assetGroupDetails.getAliasQuery(), "aliasQuery123");
		assertEquals(assetGroupDetails.getIsVisible(), true);
		assertEquals(assetGroupDetails.getTargetTypes().size(), 0);
		
		AssetGroupView assetGroupView = new AssetGroupView();
		assetGroupView.setGroupId(assetGroupDetails.getGroupId());
		assetGroupView.setGroupName(assetGroupDetails.getGroupName());
		assetGroupView.setTargetTypes(Sets.newHashSet());
		return new AssetGroupView(assetGroupDetails);
	}
	
	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}
