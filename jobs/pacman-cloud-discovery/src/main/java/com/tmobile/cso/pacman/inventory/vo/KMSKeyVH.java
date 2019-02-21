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
package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.Tag;


/**
 * The Class KMSKeyVH.
 */
public class KMSKeyVH {

	/** The key. */
	KeyMetadata key;
	
	/** The tags. */
	List<Tag> tags;
	
	/** The rotation status. */
	boolean rotationStatus;
	
	/** The alias. */
	AliasListEntry alias;

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public KeyMetadata getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(KeyMetadata key) {
		this.key = key;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * Sets the tags.
	 *
	 * @param tags the new tags
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * Checks if is rotation status.
	 *
	 * @return true, if is rotation status
	 */
	public boolean isRotationStatus() {
		return rotationStatus;
	}

	/**
	 * Sets the rotation status.
	 *
	 * @param rotationStatus the new rotation status
	 */
	public void setRotationStatus(boolean rotationStatus) {
		this.rotationStatus = rotationStatus;
	}

	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public AliasListEntry getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 *
	 * @param alias the new alias
	 */
	public void setAlias(AliasListEntry alias) {
		this.alias = alias;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KMSKey [key=" + key + ", tags=" + tags + ", rotationStatus="
				+ rotationStatus + ", alias=" + alias + "]";
	}

}
