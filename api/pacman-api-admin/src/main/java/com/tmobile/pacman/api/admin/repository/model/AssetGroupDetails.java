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
package com.tmobile.pacman.api.admin.repository.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * AssetGroupDetails Model Class
 */
@Entity
@Table(name = "cf_AssetGroupDetails", uniqueConstraints = @UniqueConstraint(columnNames = "groupId"))
public class AssetGroupDetails {

	@Id
	@Column(name = "groupId", unique = true, nullable = false)
	private String groupId;
	private String groupName;
	private String dataSource;
	private String displayName;
	private String groupType;
	private String createdBy;
	private String createdUser;
	private String createdDate;
	private String modifiedUser;
	private String modifiedDate;
	private String description;
	private String aliasQuery;
	private Boolean isVisible;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "assetGroup", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.EXTRA)
	private Set<AssetGroupTargetDetails> targetTypes = new HashSet<AssetGroupTargetDetails>();

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getDataSource() {
		return dataSource;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getGroupType() {
		return groupType;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getModifiedUser() {
		return modifiedUser;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getDescription() {
		return description;
	}

	public String getAliasQuery() {
		return aliasQuery;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAliasQuery(String aliasQuery) {
		this.aliasQuery = aliasQuery;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Set<AssetGroupTargetDetails> getTargetTypes() {
		return targetTypes;
	}

	public void setTargetTypes(Set<AssetGroupTargetDetails> targetTypes) {
		this.targetTypes = targetTypes;
	}
}
