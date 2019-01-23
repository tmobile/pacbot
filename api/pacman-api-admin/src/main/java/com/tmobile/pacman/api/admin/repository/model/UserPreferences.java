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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * UserPreferences Model Class
 */
@Entity
@Table(name = "pac_v2_userpreferences", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class UserPreferences {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	private String userId;
	private String defaultAssetGroup;
	private String recentlyViewedAG;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDefaultAssetGroup() {
		return defaultAssetGroup;
	}

	public void setDefaultAssetGroup(String defaultAssetGroup) {
		this.defaultAssetGroup = defaultAssetGroup;
	}

	public String getRecentlyViewedAG() {
		return recentlyViewedAG;
	}

	public void setRecentlyViewedAG(String recentlyViewedAG) {
		this.recentlyViewedAG = recentlyViewedAG;
	}
}
