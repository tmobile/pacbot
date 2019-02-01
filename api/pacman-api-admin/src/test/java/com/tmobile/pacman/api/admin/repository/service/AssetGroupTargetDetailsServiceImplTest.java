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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.AssetGroupTargetDetailsRepository;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupDetails;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupTargetDetails;

@RunWith(MockitoJUnitRunner.class)
public class AssetGroupTargetDetailsServiceImplTest {

	@InjectMocks
	private AssetGroupTargetDetailsServiceImpl assetGroupTargetDetailsService;
	
	@Mock
	private AssetGroupTargetDetailsRepository assetGroupTargetDetailsRepository;

	@Mock
	private AssetGroupService assetGroupService;

	@Mock
	private RuleService ruleService;

	@Test
	public void getTargetTypesByAssetGroupNameTest() throws PacManException {
		AssetGroupDetails assetGroupDetails = getAssetGroupDetails();
		List<AssetGroupTargetDetails> allAssetGroupTargetDetails =  getAllAssetGroupTargetDetails();
		when(assetGroupService.findByGroupName("assetGroupName123")).thenReturn(assetGroupDetails);
		when(assetGroupTargetDetailsRepository.findByGroupId(assetGroupDetails.getGroupId())).thenReturn(allAssetGroupTargetDetails);
		assertThat(assetGroupTargetDetailsService.getTargetTypesByAssetGroupName("assetGroupName123").size(), is(1));
	}

	/*@Override
	public List<TargetTypeRuleViewDetails> getTargetTypesByAssetGroupIdAndTargetTypeNotIn(String assetGroupName, List<String> targetTypeNames) throws PacManException {
		AssetGroupDetails assetGroupDetails = assetGroupService.findByGroupName(assetGroupName);
		if (assetGroupDetails != null) {
			List<AssetGroupTargetDetails> allAssetGroupTargetDetails = assetGroupTargetDetailsRepository.findByGroupIdAndTargetTypeNotIn(assetGroupDetails.getGroupId(), targetTypeNames);
			List<TargetTypeRuleViewDetails> allStudentsDetails = allAssetGroupTargetDetails.parallelStream().map(fetchStickyExceptionTargetTypeRuleDetails).collect(Collectors.toList());
			return allStudentsDetails;
		} else {
			throw new PacManException("Asset Group does not exits");
		}
	}
	
	Function<AssetGroupTargetDetails, TargetTypeRuleViewDetails> fetchStickyExceptionTargetTypeRuleDetails = assetGroupTargetDetail -> {
		TargetTypeRuleViewDetails targetTypeRuleDetails = new TargetTypeRuleViewDetails();
		targetTypeRuleDetails.setTargetName(assetGroupTargetDetail.getTargetType());
		List<RuleProjection> allRules = ruleService.getAllRulesByTargetTypeName(assetGroupTargetDetail.getTargetType());
		targetTypeRuleDetails.setAllRules(allRules);
		targetTypeRuleDetails.setRules(Lists.newArrayList());
		return targetTypeRuleDetails;
	};*/

	@Test
	public void getTargetTypesByAssetGroupIdAndTargetTypeNotInTest() throws PacManException {
		AssetGroupDetails assetGroupDetails = getAssetGroupDetails();
		List<AssetGroupTargetDetails> allAssetGroupTargetDetails =  getAllAssetGroupTargetDetails();
		when(assetGroupService.findByGroupName("assetGroupName123")).thenReturn(assetGroupDetails);
		Set<String> targetTypeNames = Sets.newHashSet();
		when(assetGroupTargetDetailsRepository.findByGroupIdAndTargetTypeNotIn(assetGroupDetails.getGroupId(), targetTypeNames)).thenReturn(allAssetGroupTargetDetails);
		assertThat(assetGroupTargetDetailsService.getTargetTypesByAssetGroupIdAndTargetTypeNotIn("assetGroupName123", targetTypeNames).size(), is(1));
	}
	
	@Test
	public void getTargetTypesByAssetGroupIdAndTargetTypeNotInExceptionTest() throws PacManException {
		AssetGroupDetails assetGroupDetails = getAssetGroupDetails();
		List<AssetGroupTargetDetails> allAssetGroupTargetDetails =  getAllAssetGroupTargetDetails();
		when(assetGroupService.findByGroupName("assetGroupName123")).thenReturn(null);
		Set<String> targetTypeNames = Sets.newHashSet();
		when(assetGroupTargetDetailsRepository.findByGroupIdAndTargetTypeNotIn(assetGroupDetails.getGroupId(), targetTypeNames)).thenReturn(allAssetGroupTargetDetails);
		assertThatThrownBy(() -> assetGroupTargetDetailsService.getTargetTypesByAssetGroupIdAndTargetTypeNotIn("assetGroupName123", targetTypeNames)).isInstanceOf(PacManException.class);
	}

	
	private List<AssetGroupTargetDetails> getAllAssetGroupTargetDetails() {
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
		
		AssetGroupTargetDetails assetGroupTargetDetails = new AssetGroupTargetDetails();
		assetGroupTargetDetails.setAttributeName("attributeName123");
		assetGroupTargetDetails.setAttributeValue("attributeValue123");
		assetGroupTargetDetails.setGroupId("groupId123");
		assetGroupTargetDetails.setId("id123");
		assetGroupTargetDetails.setTargetType("targetType123");
		assetGroupTargetDetails.setAssetGroup(assetGroupDetails);
		assertEquals(assetGroupTargetDetails.getId(), "id123");
		assertEquals(assetGroupTargetDetails.getAttributeName(), "attributeName123");
		assertEquals(assetGroupTargetDetails.getAttributeValue(), "attributeValue123");
		assertEquals(assetGroupTargetDetails.getGroupId(), "groupId123");
		assertEquals(assetGroupTargetDetails.getAssetGroup().getDataSource(), "dataSource123");
		
		List<AssetGroupTargetDetails> allAssetGroupTargetDetails = Lists.newArrayList();
		allAssetGroupTargetDetails.add(assetGroupTargetDetails);
		return allAssetGroupTargetDetails;
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
		AssetGroupTargetDetails assetGroupTargetDetails = new AssetGroupTargetDetails();
		assetGroupTargetDetails.setAttributeName("attributeName123");
		assetGroupTargetDetails.setAttributeValue("attributeValue123");
		assetGroupTargetDetails.setGroupId("groupId123");
		assetGroupTargetDetails.setId("id123");
		assetGroupTargetDetails.setTargetType("targetType123");
		Set<AssetGroupTargetDetails> allAssetGroupTargetDetails = Sets.newHashSet();
		allAssetGroupTargetDetails.add(assetGroupTargetDetails);
		assetGroupDetails.setTargetTypes(allAssetGroupTargetDetails);
		return assetGroupDetails;
	}
}
