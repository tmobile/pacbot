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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Sets;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.AssetGroupView;
import com.tmobile.pacman.api.admin.domain.AttributeDetails;
import com.tmobile.pacman.api.admin.domain.CreateUpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypesDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.AssetGroupRepository;
import com.tmobile.pacman.api.admin.repository.AssetGroupTargetDetailsRepository;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupDetails;
import com.tmobile.pacman.api.admin.service.CommonService;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class AssetGroupServiceImplTest {

	@InjectMocks
	private AssetGroupServiceImpl assetGroupService;
	 
	@Mock
	private AssetGroupRepository assetGroupRepository ;
	
	@Mock
	private AssetGroupTargetDetailsRepository assetGroupTargetDetailsRepository;

	@Mock
	private RestClient restClient;
	
	@Mock
	private CommonService commonService;
	
	@Mock
	private Response response;
	
	@Mock
	private StatusLine sl;
	 
	@Mock
	private PacmanConfiguration config;
	
	@Mock
	private ObjectMapper mapper;
	
	@Before
	public void setUp() throws Exception {
		PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapper);
		//MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAllAssetGroupNamesTest() throws Exception {
		Collection<String> allAssetGroupNames = Lists.newArrayList();
		allAssetGroupNames.add("GroupName123");
		when(assetGroupRepository.getAllAssetGroupNames()).thenReturn(allAssetGroupNames);
		assertThat(assetGroupService.getAllAssetGroupNames(), is(notNullValue()));
	}
	
	@Test
	public void getAllAssetGroupDetailsTest() {
		List<AssetGroupDetails> assetGroupDetail = new ArrayList<AssetGroupDetails>();
		assetGroupDetail.add(getAssetGroupDetails());
		Page<AssetGroupDetails> allAssetGroupDetails = new PageImpl<AssetGroupDetails>(assetGroupDetail,new PageRequest(0, 1), assetGroupDetail.size());
		when(assetGroupRepository.findAll(anyString(), any(PageRequest.class))).thenReturn(allAssetGroupDetails);
		assertThat(assetGroupService.getAllAssetGroupDetails(StringUtils.EMPTY, 0, 1), is(notNullValue()));
	}
	
	@Test
	public void findByGroupNameTest() {
		when(assetGroupRepository.findByGroupName(anyString())).thenReturn(getAssetGroupDetails());
		assertThat(assetGroupService.findByGroupName(StringUtils.EMPTY), is(notNullValue()));
	}

	@Test
	public void createAssetGroupDetailsIncludeTest() throws PacManException, IOException {
		CreateUpdateAssetGroupDetails createUpdateAssetGroupDetails = getCreateUpdateAssetGroupDetailsRequest(true, 1);
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(commonService.invokeAPI(anyString(), anyString(), anyString())).thenReturn(response);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
		assertThat(assetGroupService.createAssetGroupDetails(createUpdateAssetGroupDetails, StringUtils.EMPTY), is(notNullValue()));
	} 

	@Test
	public void createAssetGroupDetailsNotIncludeTest() throws PacManException, IOException {
		CreateUpdateAssetGroupDetails createUpdateAssetGroupDetails = getCreateUpdateAssetGroupDetailsRequest(false, 2);
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
		when(response.getEntity()).thenReturn(jsonEntity);
        when(commonService.invokeAPI(anyString(), anyString(), anyString())).thenReturn(response);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
		assertThat(assetGroupService.createAssetGroupDetails(createUpdateAssetGroupDetails, StringUtils.EMPTY), is(notNullValue()));
	}
	
	private CreateUpdateAssetGroupDetails getCreateUpdateAssetGroupDetailsRequest(boolean include, int attributeSize) { 
		CreateUpdateAssetGroupDetails createUpdateAssetGroupDetails = new CreateUpdateAssetGroupDetails();
		createUpdateAssetGroupDetails.setCreatedBy("createdBy");
		createUpdateAssetGroupDetails.setDataSourceName("dataSourceName");
		createUpdateAssetGroupDetails.setDescription("description");
		createUpdateAssetGroupDetails.setDisplayName("displayName");
		createUpdateAssetGroupDetails.setGroupName("groupName");
		List<String> allAttributesName = Lists.newArrayList();
		allAttributesName.add("ABC");
		List<AttributeDetails> attributes = Lists.newArrayList();
		AttributeDetails attributeDetails = new AttributeDetails();
		attributeDetails.setName("name123");
		attributeDetails.setValue("value123");
		attributes.add(attributeDetails);
		if (attributeSize > 1) {
			attributeDetails = new AttributeDetails();
			attributeDetails.setName("name234");
			attributeDetails.setValue("value123, value234");
			attributes.add(attributeDetails);
		}
		TargetTypesDetails targetTypesDetails = new TargetTypesDetails();
		targetTypesDetails.setAdded(false);
		targetTypesDetails.setIncludeAll(include);
		targetTypesDetails.setTargetName("targetName");
		targetTypesDetails.setAllAttributesName(allAttributesName);
		targetTypesDetails.setAttributes(attributes);
		assertEquals(targetTypesDetails.getAllAttributesName(), allAttributesName);
		assertEquals(targetTypesDetails.isAdded(), false);
		
		List<TargetTypesDetails> allTargetTypesDetails = Lists.newArrayList();
		allTargetTypesDetails.add(targetTypesDetails);
		createUpdateAssetGroupDetails.setType("type");
		createUpdateAssetGroupDetails.setVisible(false);
		createUpdateAssetGroupDetails.setTargetTypes(allTargetTypesDetails);
		return createUpdateAssetGroupDetails;
	}

	private AssetGroupView getAssetGroupViewDetails() {
		AssetGroupDetails assetGroupDetails = getAssetGroupDetails();
		AssetGroupView assetGroupView = new AssetGroupView();
		assetGroupView.setGroupId(assetGroupDetails.getGroupId());
		assetGroupView.setGroupName(assetGroupDetails.getGroupName());
		assetGroupView.setTargetTypes(Sets.newHashSet());
		return new AssetGroupView(assetGroupDetails);
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
	
	
}
