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

import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;

/**
 * The Class AccessKeyMetadataVH.
 */
public class AccessKeyMetadataVH extends AccessKeyMetadata {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5844918070449531830L;
	
	/** The last used date. */
	private java.util.Date lastUsedDate;

	/**
	 * Instantiates a new access key metadata VH.
	 *
	 * @param access the access
	 */
	public AccessKeyMetadataVH(AccessKeyMetadata access){
		this.setAccessKeyId(access.getAccessKeyId());
		this.setCreateDate(access.getCreateDate());
		this.setUserName(access.getUserName());
		this.setStatus(access.getStatus());
	}
	
	/**
	 * Gets the last used date.
	 *
	 * @return the last used date
	 */
	public java.util.Date getLastUsedDate() {
		return lastUsedDate;
	}

	/**
	 * Sets the last used date.
	 *
	 * @param lastUsedDate the new last used date
	 */
	public void setLastUsedDate(java.util.Date lastUsedDate) {
		this.lastUsedDate = lastUsedDate;
	}
	

}
