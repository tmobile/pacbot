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

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.RuleProjection;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleViewDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.AssetGroupTargetDetailsRepository;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupDetails;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupTargetDetails;
import com.tmobile.pacman.api.commons.Constants;

/**
 * AssetGroup Target Details Service Implementations
 */
@Service
public class AssetGroupTargetDetailsServiceImpl implements AssetGroupTargetDetailsService, Constants {

	@Autowired
	private AssetGroupTargetDetailsRepository assetGroupTargetDetailsRepository;

	@Autowired
	private AssetGroupService assetGroupService;

	@Autowired
	private RuleService ruleService;

	@Override
	public List<TargetTypeRuleDetails> getTargetTypesByAssetGroupName(String assetGroupName) {
		AssetGroupDetails assetGroupDetails = assetGroupService.findByGroupName(assetGroupName);
		List<AssetGroupTargetDetails> allAssetGroupTargetDetails = assetGroupTargetDetailsRepository.findByGroupId(assetGroupDetails.getGroupId());
		List<TargetTypeRuleDetails> allStudentsDetails = allAssetGroupTargetDetails.parallelStream().map(fetchTargetTypeRuleDetails).collect(Collectors.toList());
		return allStudentsDetails;
	}

	Function<AssetGroupTargetDetails, TargetTypeRuleDetails> fetchTargetTypeRuleDetails = assetGroupTargetDetail -> {
		TargetTypeRuleDetails targetTypeRuleDetails = new TargetTypeRuleDetails();
		targetTypeRuleDetails.setTargetName(assetGroupTargetDetail.getTargetType());
		List<RuleProjection> allRules = ruleService.getAllRulesByTargetTypeName(assetGroupTargetDetail.getTargetType());
		targetTypeRuleDetails.setAllRules(allRules);
		targetTypeRuleDetails.setRules(Lists.newArrayList());
		return targetTypeRuleDetails;
	};

	@Override
	public List<TargetTypeRuleViewDetails> getTargetTypesByAssetGroupIdAndTargetTypeNotIn(String assetGroupName, Set<String> targetTypeNames) throws PacManException {
		AssetGroupDetails assetGroupDetails = assetGroupService.findByGroupName(assetGroupName);
		List<AssetGroupTargetDetails> allAssetGroupTargetDetails = Lists.newArrayList();
		if (assetGroupDetails != null) {
			if(targetTypeNames.isEmpty()) {
				allAssetGroupTargetDetails = assetGroupTargetDetailsRepository.findByGroupId(assetGroupDetails.getGroupId());
			} else {
				allAssetGroupTargetDetails = assetGroupTargetDetailsRepository.findByGroupIdAndTargetTypeNotIn(assetGroupDetails.getGroupId(), targetTypeNames);
			}
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
	};
}
