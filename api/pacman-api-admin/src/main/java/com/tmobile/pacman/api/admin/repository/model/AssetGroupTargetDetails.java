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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * AssetGroupTargetDetails Model Class
 */
@Entity
@Table(name = "cf_AssetGroupTargetDetails", uniqueConstraints = @UniqueConstraint(columnNames = "id_"))
public class AssetGroupTargetDetails {

	@Id
	@Column(name = "id_", unique = true, nullable = false)
	private String id;

	@Column(name = "groupId")
	private String groupId;

	private String targetType;
	private String attributeName;
	private String attributeValue;

	@JsonBackReference
	@JoinColumn(name = "groupId", insertable=false, updatable=false)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private AssetGroupDetails assetGroup;

	public String getId() {
		return id;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getTargetType() {
		return targetType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public AssetGroupDetails getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(AssetGroupDetails assetGroup) {
		this.assetGroup = assetGroup;
	}
}
