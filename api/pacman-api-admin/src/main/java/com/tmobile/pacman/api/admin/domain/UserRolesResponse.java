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

import java.util.Date;

/**
 * UserRolesResponse Domain Class
 */
public class UserRolesResponse {

	private String roleId;
	private String createdBy;
	private String description;
	private String roleName;
	private Date modifiedDate;
	private Date createdDate;
	private Object users;

	public String getRoleId() {
		return roleId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getRoleName() {
		return roleName;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Object getUsers() {
		return users;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setUsers(Object users) {
		this.users = users;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
