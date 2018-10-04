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
 * RoleBase Domain Class
 */
public class RoleBase {

	private String roleName;
	private String description;
	private Boolean writePermission = false;

	public String getRoleName() {
		return roleName;
	}

	public Boolean getWritePermission() {
		return writePermission;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setWritePermission(Boolean writePermission) {
		this.writePermission = writePermission;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
