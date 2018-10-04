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

import java.util.List;

/**
 * TargetTypesDetails Domain Class
 */
public class TargetTypesDetails {
	
	private boolean includeAll;
	private String targetName;
	private List<AttributeDetails> attributes;
	private List<String> allAttributesName;
	private boolean added;

	public boolean isIncludeAll() {
		return includeAll;
	}

	public void setIncludeAll(boolean includeAll) {
		this.includeAll = includeAll;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public List<AttributeDetails> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeDetails> attributes) {
		this.attributes = attributes;
	}

	public List<String> getAllAttributesName() {
		return allAttributesName;
	}

	public void setAllAttributesName(List<String> allAttributesName) {
		this.allAttributesName = allAttributesName;
	}

	public boolean isAdded() {
		return added;
	}

	public void setAdded(boolean added) {
		this.added = added;
	}
}
