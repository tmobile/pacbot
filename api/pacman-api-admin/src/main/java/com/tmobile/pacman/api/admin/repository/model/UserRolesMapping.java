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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * UserRolesMapping Model Class
 */
@Entity
@Table(name = "oauth_user_role_mapping", uniqueConstraints = @UniqueConstraint(columnNames = "userRoleId"))
public class UserRolesMapping {

	@Id
	@Column(name = "userRoleId", unique = true, nullable = false)
	private String userRoleId;
	private String userId;
	private String roleId;

	@JsonBackReference
	@JoinColumn(name = "roleId", nullable = false, insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@ManyToOne(fetch = FetchType.LAZY)
	private UserRoles userRoles;

	private String clientId;
	private String allocator;
	private Date createdDate;
	private Date modifiedDate;

	public String getUserRoleId() {
		return userRoleId;
	}

	public String getUserId() {
		return userId;
	}

	public String getClientId() {
		return clientId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public UserRoles getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(UserRoles userRoles) {
		this.userRoles = userRoles;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getAllocator() {
		return allocator;
	}

	public void setAllocator(String allocator) {
		this.allocator = allocator;
	}
}
