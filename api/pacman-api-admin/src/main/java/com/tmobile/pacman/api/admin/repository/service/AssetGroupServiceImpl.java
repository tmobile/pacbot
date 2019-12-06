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

import static com.tmobile.pacman.api.admin.common.AdminConstants.ASSET_GROUP_ALIAS_DELETION_FAILED;
import static com.tmobile.pacman.api.admin.common.AdminConstants.ASSET_GROUP_CREATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.ASSET_GROUP_DELETE_FAILED;
import static com.tmobile.pacman.api.admin.common.AdminConstants.ASSET_GROUP_DELETE_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.ASSET_GROUP_NOT_EXITS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.ASSET_GROUP_UPDATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.DATE_FORMAT;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tmobile.pacman.api.admin.domain.AssetGroupView;
import com.tmobile.pacman.api.admin.domain.AttributeDetails;
import com.tmobile.pacman.api.admin.domain.CreateUpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupRequest;
import com.tmobile.pacman.api.admin.domain.TargetTypesDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypesProjection;
import com.tmobile.pacman.api.admin.domain.UpdateAssetGroupDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.AssetGroupRepository;
import com.tmobile.pacman.api.admin.repository.AssetGroupTargetDetailsRepository;
import com.tmobile.pacman.api.admin.repository.TargetTypesRepository;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupDetails;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupTargetDetails;
import com.tmobile.pacman.api.admin.service.CommonService;
import com.tmobile.pacman.api.admin.util.AdminUtils;

/**
 * AssetGroup Service Implementations
 */
@Service
public class AssetGroupServiceImpl implements AssetGroupService {

	private static final Logger log = LoggerFactory.getLogger(AssetGroupServiceImpl.class);
	
	private static final String ALIASES = "/_aliases";
		
	@Autowired
	private AssetGroupRepository assetGroupRepository;
	
	@Autowired
	private TargetTypesRepository targetTypesRepository;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private AssetGroupTargetDetailsRepository assetGroupTargetDetailsRepository;

	@Override
	public Collection<String> getAllAssetGroupNames() {
		return assetGroupRepository.getAllAssetGroupNames();
	}

	@Override
	public Page<AssetGroupView> getAllAssetGroupDetails(final String searchTerm, final int page, final int size) {
		return buildAssetGroupView(assetGroupRepository.findAll(searchTerm.toLowerCase(), PageRequest.of(page, size)));
	}

	private Page<AssetGroupView> buildAssetGroupView(final Page<AssetGroupDetails> allAssetGroups) {
		List<AssetGroupView> allAssetGroupList = Lists.newArrayList();
		allAssetGroups.getContent().forEach(assetGroup -> {
			AssetGroupView assetGroupView = new AssetGroupView();
			assetGroupView.setGroupId(assetGroup.getGroupId());
			assetGroupView.setGroupName(assetGroup.getGroupName());
			assetGroupView.setTargetTypes(assetGroup.getTargetTypes());
			allAssetGroupList.add(assetGroupView);
		});
		return new PageImpl<AssetGroupView>(allAssetGroupList, PageRequest.of(allAssetGroups.getNumber(), allAssetGroups.getSize()),allAssetGroups.getTotalElements());
	}

	@Override
	public AssetGroupDetails findByGroupName(String groupName) {
		return assetGroupRepository.findByGroupName(groupName);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String updateAssetGroupDetails(final CreateUpdateAssetGroupDetails updateAssetGroupDetails, final String userId) throws PacManException {
		AssetGroupDetails isAssetGroupExits = assetGroupRepository.findByGroupName(updateAssetGroupDetails.getGroupName());
		if (isAssetGroupExits != null) {
			boolean isDeletedSuccess = deleteAssetGroupAliasFromUpdation(updateAssetGroupDetails);
			if(isDeletedSuccess) {
				try {
					Map<String, Object> assetGroupAlias = createAliasForAssetGroup(updateAssetGroupDetails);
					List<Object> actions = (List<Object>) assetGroupAlias.get("actions");
					if(!actions.isEmpty()) {
						Response response = commonService.invokeAPI("POST", ALIASES, mapper.writeValueAsString(assetGroupAlias));
						if(response != null && response.getStatusLine().getStatusCode() == 200) {
							return processUpdateAssetGroupDetails(updateAssetGroupDetails, assetGroupAlias, userId);
						} else {
							throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
						}
					} else {
						return processUpdateAssetGroupDetails(updateAssetGroupDetails, assetGroupAlias, userId);
					}
				} catch (Exception exception) {
					log.error(UNEXPECTED_ERROR_OCCURRED, exception);
					throw new PacManException(UNEXPECTED_ERROR_OCCURRED.concat(": ").concat(exception.getMessage()));
				}
			} else {
				throw new PacManException(ASSET_GROUP_ALIAS_DELETION_FAILED);
			}
		} else {
			throw new PacManException(ASSET_GROUP_NOT_EXITS);
		}			
	}

	@Override
	public String createAssetGroupDetails(final CreateUpdateAssetGroupDetails createAssetGroupDetails, final String userId) throws PacManException {
		try {
			Map<String, Object> assetGroupAlias = createAliasForAssetGroup(createAssetGroupDetails);
			Response response = commonService.invokeAPI("POST", ALIASES, mapper.writeValueAsString(assetGroupAlias));
			if(response != null) {
				if (response.getStatusLine().getStatusCode() == 200) {
					return processCreateAssetGroupDetails(createAssetGroupDetails, assetGroupAlias, userId);
				} else {
					throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
				}
			} else {
				throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
			}
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			throw new PacManException(UNEXPECTED_ERROR_OCCURRED.concat(": ").concat(exception.getMessage()));
		}
	}

	@Override
	public UpdateAssetGroupDetails getAssetGroupDetailsByIdAndDataSource(final String assetGroupId, final String dataSource) throws PacManException {
		if(assetGroupRepository.existsById(assetGroupId)) {
			AssetGroupDetails existingAssetGroupDetails = assetGroupRepository.findById(assetGroupId).get();	
			return buildAssetGroupDetails(existingAssetGroupDetails);
		} else {
			throw new PacManException(ASSET_GROUP_NOT_EXITS);
		}
	}

	@Override
	public String deleteAssetGroup(final DeleteAssetGroupRequest assetGroupDetails, final String userId) throws PacManException {
		return deleteAssetGroupDetails(assetGroupDetails);
	}

	private String processUpdateAssetGroupDetails(final CreateUpdateAssetGroupDetails updateAssetGroupDetails, final Map<String, Object> assetGroupAlias, String userId) throws PacManException {
		try {
			AssetGroupDetails existingAssetGroupDetails = assetGroupRepository.findByGroupName(updateAssetGroupDetails.getGroupName());
			existingAssetGroupDetails.setDisplayName(updateAssetGroupDetails.getDisplayName());
			existingAssetGroupDetails.setGroupType(updateAssetGroupDetails.getType());
			existingAssetGroupDetails.setDescription(updateAssetGroupDetails.getDescription());
			existingAssetGroupDetails.setCreatedBy(updateAssetGroupDetails.getCreatedBy());
			existingAssetGroupDetails.setModifiedUser(userId);
			existingAssetGroupDetails.setModifiedDate(AdminUtils.getFormatedStringDate(DATE_FORMAT, new Date()));
			existingAssetGroupDetails.setAliasQuery(mapper.writeValueAsString(assetGroupAlias));
			existingAssetGroupDetails.setIsVisible(updateAssetGroupDetails.isVisible());
			List<TargetTypesDetails> targetTypesDetails = updateAssetGroupDetails.getTargetTypes();
			Set<AssetGroupTargetDetails> allTargetTypesDetails = buildTargetTypes(targetTypesDetails, existingAssetGroupDetails.getGroupId());
			existingAssetGroupDetails.setTargetTypes(allTargetTypesDetails);
			assetGroupRepository.saveAndFlush(existingAssetGroupDetails);
			return ASSET_GROUP_UPDATION_SUCCESS;
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			throw new PacManException(UNEXPECTED_ERROR_OCCURRED.concat(": ").concat(exception.getMessage()));
		}
	}

	private boolean deleteAssetGroupAliasFromUpdation(CreateUpdateAssetGroupDetails updateAssetGroupDetails) throws PacManException {
		AssetGroupDetails assetGroupDetails = assetGroupRepository.findByGroupName(updateAssetGroupDetails.getGroupName());
		boolean isDeleted = deleteAssetGroupAlias(assetGroupDetails);
		if(isDeleted) {
			Set<AssetGroupTargetDetails> allDeletedTargets = assetGroupDetails.getTargetTypes();
			assetGroupTargetDetailsRepository.deleteInBatch(allDeletedTargets);
			return true;
		}
		return false;
	}

	private String processCreateAssetGroupDetails(final CreateUpdateAssetGroupDetails createAssetGroupDetails, final Map<String, Object> assetGroupAlias, final String userId) throws PacManException {
		AssetGroupDetails assetGroupDetails = new AssetGroupDetails();
		try {
			String dataSource = createAssetGroupDetails.getDataSourceName();
			String assetGroupId = UUID.randomUUID().toString();
			assetGroupDetails.setGroupId(assetGroupId);
			assetGroupDetails.setGroupName(createAssetGroupDetails.getGroupName().toLowerCase().trim().replaceAll(" ", "-"));
			assetGroupDetails.setDisplayName(createAssetGroupDetails.getDisplayName());
			assetGroupDetails.setGroupType(createAssetGroupDetails.getType());
			assetGroupDetails.setCreatedBy(createAssetGroupDetails.getCreatedBy());
			assetGroupDetails.setDescription(createAssetGroupDetails.getDescription());
			assetGroupDetails.setCreatedDate(AdminUtils.getFormatedStringDate(DATE_FORMAT, new Date()));
			assetGroupDetails.setCreatedUser(userId);
			assetGroupDetails.setDataSource(dataSource);
			assetGroupDetails.setAliasQuery(mapper.writeValueAsString(assetGroupAlias));
			assetGroupDetails.setIsVisible(createAssetGroupDetails.isVisible());
			List<TargetTypesDetails> targetTypesDetails = createAssetGroupDetails.getTargetTypes();
			Set<AssetGroupTargetDetails> allTargetTypesDetails = buildTargetTypes(targetTypesDetails, assetGroupId);
			assetGroupDetails.setTargetTypes(allTargetTypesDetails);
			assetGroupRepository.save(assetGroupDetails);
			return ASSET_GROUP_CREATION_SUCCESS;
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			deleteAssetGroupAlias(assetGroupDetails);
			throw new PacManException(UNEXPECTED_ERROR_OCCURRED.concat(": ").concat(exception.getMessage()));
		}
	}

	private Set<AssetGroupTargetDetails> buildTargetTypes(List<TargetTypesDetails> targetTypesDetails, String assetGroupId) {
		Set<AssetGroupTargetDetails> allTargetTypesDetails = Sets.newHashSet();
		for (int index = 0; index < targetTypesDetails.size(); index++) {
			TargetTypesDetails targetTypes = targetTypesDetails.get(index);
			if (targetTypesDetails.get(index).isIncludeAll()) {
				AssetGroupTargetDetails assetGroupTargetDetails = new AssetGroupTargetDetails();
				assetGroupTargetDetails.setId(UUID.randomUUID().toString());
				assetGroupTargetDetails.setGroupId(assetGroupId);
				assetGroupTargetDetails.setTargetType(targetTypes.getTargetName());
				assetGroupTargetDetails.setAttributeName("all");
				assetGroupTargetDetails.setAttributeValue("all");
				allTargetTypesDetails.add(assetGroupTargetDetails);
			} else {
				List<AttributeDetails> attributes = targetTypes.getAttributes();
				for (int attributeIndex = 0; attributeIndex < attributes.size(); attributeIndex++) {
					AttributeDetails attribute = attributes.get(attributeIndex);
					AssetGroupTargetDetails assetGroupTargetDetails = new AssetGroupTargetDetails();
					assetGroupTargetDetails.setId(UUID.randomUUID().toString());
					assetGroupTargetDetails.setGroupId(assetGroupId);
					assetGroupTargetDetails.setTargetType(targetTypes.getTargetName());
					assetGroupTargetDetails.setAttributeName(attribute.getName());
					assetGroupTargetDetails.setAttributeValue(attribute.getValue());
					allTargetTypesDetails.add(assetGroupTargetDetails);
				}
			}
		}
		return allTargetTypesDetails;
	}

	private UpdateAssetGroupDetails buildAssetGroupDetails(final AssetGroupDetails existingAssetGroupDetails) {
		List<TargetTypesDetails> assetGroupTargetTypes = Lists.newArrayList();
		UpdateAssetGroupDetails assetGroupDetails = new UpdateAssetGroupDetails();
		assetGroupDetails.setGroupId(existingAssetGroupDetails.getGroupId());
		assetGroupDetails.setCreatedBy(existingAssetGroupDetails.getCreatedBy());
		assetGroupDetails.setDataSourceName(existingAssetGroupDetails.getDataSource());
		assetGroupDetails.setDescription(existingAssetGroupDetails.getDescription());
		assetGroupDetails.setDisplayName(existingAssetGroupDetails.getDisplayName());
		assetGroupDetails.setGroupName(existingAssetGroupDetails.getGroupName());
		assetGroupDetails.setType(existingAssetGroupDetails.getGroupType());
		assetGroupDetails.setVisible(existingAssetGroupDetails.getIsVisible());
		
		Set<AssetGroupTargetDetails> allTargetTypeDetails = existingAssetGroupDetails.getTargetTypes();
		Map<String, Integer> targetTypesIndex = Maps.newHashMap();
		Set<String> selectedTargetTypes = Sets.newHashSet();
		final int[] idx = { -1 };
		allTargetTypeDetails.forEach(targetTypeDetails -> {
			TargetTypesDetails targetTypes = new TargetTypesDetails();
			selectedTargetTypes.add(targetTypeDetails.getTargetType());
			if(targetTypesIndex.get(targetTypeDetails.getTargetType()) != null) {
				int index = targetTypesIndex.get(targetTypeDetails.getTargetType());
				targetTypes = assetGroupTargetTypes.get(index);
				List<AttributeDetails> attributes = targetTypes.getAttributes();
				AttributeDetails attributeDetails = new AttributeDetails();
				attributeDetails.setName(targetTypeDetails.getAttributeName());
				attributeDetails.setValue(targetTypeDetails.getAttributeValue());
				attributes.add(attributeDetails);
				targetTypes.setAttributes(attributes);
			} else {
				idx[0]++;
				targetTypesIndex.put(targetTypeDetails.getTargetType(), idx[0]);
				targetTypes.setAdded(true);
				targetTypes.setTargetName(targetTypeDetails.getTargetType());
				targetTypes.setAllAttributesName(commonService.getFieldNames(targetTypesRepository.findDataSourceByTargetType(targetTypeDetails.getTargetType()) + "_" + targetTypeDetails.getTargetType(), targetTypeDetails.getTargetType()));
				if(targetTypeDetails.getAttributeName().equalsIgnoreCase("all") && targetTypeDetails.getAttributeValue().equalsIgnoreCase("all")) {
					targetTypes.setIncludeAll(true);
					targetTypes.setAttributes(Lists.newArrayList());
				} else {
					targetTypes.setIncludeAll(false);
					AttributeDetails attributeDetails = new AttributeDetails();
					attributeDetails.setName(targetTypeDetails.getAttributeName());
					attributeDetails.setValue(targetTypeDetails.getAttributeValue());
					List<AttributeDetails> attributes = Lists.newArrayList();
					attributes.add(attributeDetails);
					targetTypes.setAttributes(attributes);
				}
				assetGroupTargetTypes.add(targetTypes);
			}
		});
		List<TargetTypesDetails> attributes = Lists.newArrayList(); 
		List<TargetTypesProjection> remainingTargetTypes = Lists.newArrayList(); 
		assetGroupDetails.setTargetTypes(assetGroupTargetTypes);
		if(!selectedTargetTypes.isEmpty()) {
			remainingTargetTypes = targetTypesRepository.findByTargetTypeNotIn(Lists.newArrayList(selectedTargetTypes));
		} else {
			remainingTargetTypes = targetTypesRepository.getAllTargetTypes();
		}
		
		
		for(TargetTypesProjection remainingTargetType : remainingTargetTypes) {
			String targetName = remainingTargetType.getText().trim();
			TargetTypesDetails targetTypeAttribute = new TargetTypesDetails();
			targetTypeAttribute.setAttributes(Lists.newArrayList());
			targetTypeAttribute.setTargetName(targetName.trim());
			targetTypeAttribute.setAllAttributesName(commonService.getFieldNames(targetTypesRepository.findDataSourceByTargetType(targetName) + "_" + targetName, targetName));
			targetTypeAttribute.setIncludeAll(false);
			attributes.add(targetTypeAttribute);
		}
		assetGroupDetails.setRemainingTargetTypes(remainingTargetTypes);
		assetGroupDetails.setRemainingTargetTypesFullDetails(attributes);
		return assetGroupDetails;
	}

	private boolean deleteAssetGroupAlias(final AssetGroupDetails assetGroupDetails) throws PacManException {
		try {
			Map<String, Object> alias = Maps.newHashMap();
			List<Object> action = Lists.newArrayList();
			Set<AssetGroupTargetDetails> targetTypes = assetGroupDetails.getTargetTypes();
			final String aliasName = assetGroupDetails.getGroupName().toLowerCase().trim().replaceAll(" ", "-");
			
			if(!targetTypes.isEmpty()) {
				targetTypes.forEach(targetType -> {
					String targetName = targetType.getTargetType().toLowerCase().trim().replaceAll(" ", "-");
					Map<String, Object> addObj = Maps.newHashMap();
					addObj.put("index", targetTypesRepository.findDataSourceByTargetType(targetName).toLowerCase().trim().replaceAll(" ", "-")+"_"+targetName);
					addObj.put("alias", aliasName);
					Map<String, Object> add = Maps.newHashMap();
					add.put("remove", addObj);
					action.add(add);
				});
				alias.put("actions", action);
				Response response = commonService.invokeAPI("POST", ALIASES, mapper.writeValueAsString(alias));
				if(response != null && response.getStatusLine().getStatusCode() == 200) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			throw new PacManException(UNEXPECTED_ERROR_OCCURRED.concat(": ").concat(exception.getMessage()));
		}
	}

	private Map<String, Object> createAliasForAssetGroup(final CreateUpdateAssetGroupDetails assetGroupDetailsJson) {
		try {
			Map<String, Object> alias = Maps.newHashMap();
			List<Object> action = Lists.newArrayList();
			List<TargetTypesDetails> targetTypes = assetGroupDetailsJson.getTargetTypes();
			final String aliasName = assetGroupDetailsJson.getGroupName().toLowerCase().trim().replaceAll(" ", "-");
			for (int targetIndex = 0; targetIndex < targetTypes.size(); targetIndex++) {
				Map<String, Object> addObj = Maps.newHashMap();
				String targetType = targetTypes.get(targetIndex).getTargetName().toLowerCase().trim().replaceAll(" ", "-");
				addObj.put("index", targetTypesRepository.findDataSourceByTargetType(targetType).toLowerCase().trim().replaceAll(" ", "-") + "_" + targetType);
				addObj.put("alias", aliasName);
				List<AttributeDetails> attributes = Lists.newArrayList();
				if (!targetTypes.get(targetIndex).isIncludeAll()) {
					attributes = targetTypes.get(targetIndex).getAttributes();
				}

				Map<String, Object> parentObj = Maps.newHashMap();

				Map<String, Map<String, String>> typeDetails = Maps.newHashMap();
				Map<String, String> typeValueDetails = Maps.newHashMap();
				typeValueDetails.put("value", targetTypes.get(targetIndex).getTargetName());
				typeDetails.put("_type", typeValueDetails);
				parentObj.put("term", typeDetails);
				
				List<Object> mustArray = buildMustArray(attributes);
				String tempMustArray = mapper.writeValueAsString(mustArray);
				List<Object> shouldArray = Lists.newArrayList();
				Map<String, Object> hasParent = Maps.newHashMap();
				hasParent.put("parent_type", targetTypes.get(targetIndex).getTargetName());
				if (mustArray.isEmpty()) {
					Map<String, Object> matchAll = Maps.newHashMap();
					Map<String, Object> matchAllDetails = Maps.newHashMap();
					matchAll.put("match_all", matchAllDetails);
					hasParent.put("query", matchAll);
				} else {
					Map<String, Object> mustObj = Maps.newHashMap();
					mustObj.put("must", mustArray);
					Map<String, Object> boolMust = Maps.newHashMap();
					boolMust.put("bool", mustObj);
					hasParent.put("query", boolMust);
				}
				Map<String, Object> hasParentDetails = Maps.newHashMap();
				hasParentDetails.put("has_parent", hasParent);
				List<Object> mustObjects = mapper.readValue(tempMustArray,new TypeReference<List<Object>>() {});
				mustObjects.add(parentObj);
				Map<String, Object> mustDetails = Maps.newHashMap();
				Map<String, Object> mustValueDetails = Maps.newHashMap();
				mustValueDetails.put("must", mustObjects);
				mustDetails.put("bool", mustValueDetails);

				shouldArray.add(hasParentDetails);
				shouldArray.add(mustDetails);
				Map<String, Object> filterDetails = Maps.newHashMap();
				Map<String, Object> shouldDetails = Maps.newHashMap();
				shouldDetails.put("should", shouldArray);
				filterDetails.put("bool", shouldDetails);
				addObj.put("filter", filterDetails);

				Map<String, Object> add = Maps.newHashMap();
				add.put("add", addObj);
				action.add(add);
			}
			alias.put("actions", action);
			return alias;
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return Maps.newHashMap();
		}
	}

	private List<Object> buildMustArray(List<AttributeDetails> attributes) {
		List<Object> mustArray = Lists.newArrayList();
		for (Entry<String, String> attribute : createAttrMap(attributes).entrySet()) {
			String[] values = attribute.getValue().split(",");
			if (values.length > 1) {
				List<Object> shouldArray = Lists.newArrayList();
				for (String value : values) {
					Map<String, Object> attributeObj = Maps.newHashMap();
					attributeObj.put(attribute.getKey() + ".keyword", value);
					Map<String, Object> match = Maps.newHashMap();
					match.put("match", attributeObj);
					shouldArray.add(match);
				}
				Map<String, Object> shouldObj = Maps.newHashMap();
				shouldObj.put("should", shouldArray);
				shouldObj.put("minimum_should_match", 1);
				Map<String, Object> innerboolObj = Maps.newHashMap();
				innerboolObj.put("bool", shouldObj);
				mustArray.add(innerboolObj);
			} else {
				Map<String, Object> attributeObj = Maps.newHashMap();
				attributeObj.put(attribute.getKey() + ".keyword", attribute.getValue());
				Map<String, Object> match = Maps.newHashMap();
				match.put("match", attributeObj);
				mustArray.add(match);
			}
		}
		return mustArray;
	}

	private static Map<String, String> createAttrMap(List<AttributeDetails> attributes) {
		Map<String, String> attrMap = Maps.newHashMap();
		for (int index = 0; index < attributes.size(); index++) {
			AttributeDetails attribute = attributes.get(index);
			if (attrMap.isEmpty()) {
				attrMap.put(attribute.getName(), attribute.getValue());
			} else {
				if (attrMap.containsKey(attribute.getName())) {
					attrMap.put(attribute.getName(), attrMap.get(attribute.getName()) + "," + attribute.getValue());
				} else {
					attrMap.put(attribute.getName(), attribute.getValue());
				}
			}
		}
		return attrMap;
	}

	private String deleteAssetGroupDetails(final DeleteAssetGroupRequest deleteAssetGroupRequest) throws PacManException {
		if(assetGroupRepository.existsById(deleteAssetGroupRequest.getGroupId())) {
			AssetGroupDetails assetGroupDetails = assetGroupRepository.findById(deleteAssetGroupRequest.getGroupId()).get();
			boolean isDeleted = deleteAssetGroupAlias(assetGroupDetails);
			if(isDeleted) {
				try {
					assetGroupRepository.delete(assetGroupDetails);
					return ASSET_GROUP_DELETE_SUCCESS;
				} catch(Exception exception) {
					log.error(UNEXPECTED_ERROR_OCCURRED, exception);
					commonService.invokeAPI("POST", ALIASES, assetGroupDetails.getAliasQuery());
					throw new PacManException(UNEXPECTED_ERROR_OCCURRED.concat(": ").concat(exception.getMessage()));
				}
			} else {
				return ASSET_GROUP_DELETE_FAILED;
			}
		} else {
			throw new PacManException(ASSET_GROUP_NOT_EXITS);
		}
	}
}
