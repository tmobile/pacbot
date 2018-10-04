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
package com.tmobile.pacman.api.admin.domain;

/**
 * AssetGroupTargetDetailsDomain Domain Class
 */
public class AssetGroupTargetDetailsDomain {

	private String id;

	private String groupId;

	private String targetType;
	private String attributeName;
	private String attributeValue;

	

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
}
