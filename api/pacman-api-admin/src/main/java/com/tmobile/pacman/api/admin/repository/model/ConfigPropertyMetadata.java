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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ConfigProperty Metadata Model Class.
 */
@Entity
@Table(name = "pac_config_key_metadata")
public class ConfigPropertyMetadata {

	/** The cfkey. */
	@Id
	private String cfkey;

	/** The description. */
	private String description;

	/**
	 * Gets the cfkey.
	 *
	 * @return the cfkey
	 */
	public String getCfkey() {
		return cfkey;
	}

	/**
	 * Sets the cfkey.
	 *
	 * @param cfkey the new cfkey
	 */
	public void setCfkey(String cfkey) {
		this.cfkey = cfkey;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
