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

import java.util.Date;
import java.util.List;

import com.amazonaws.services.identitymanagement.model.User;


/**
 * The Class UserVH.
 */
public class UserVH {
	
	/** The user. */
	private User user;
	
	/** The access keys. */
	private List<AccessKeyMetadataVH> accessKeys;
	
	/** The password creation date. */
	private Date passwordCreationDate;
	
	/** The password reset required. */
	private boolean passwordResetRequired;
	
	/** The groups. */
	private List<String> groups;
	
	/** The mfa. */
	private boolean mfa;
		
	/**
	 * Checks if is mfa.
	 *
	 * @return true, if is mfa
	 */
	public boolean isMfa() {
		return mfa;
	}

	/**
	 * Sets the mfa.
	 *
	 * @param mfa the new mfa
	 */
	public void setMfa(boolean mfa) {
		this.mfa = mfa;
	}

	/**
	 * Instantiates a new user VH.
	 *
	 * @param usr the usr
	 */
	public UserVH(User usr){
		this.user = usr;
	}
	
	/**
	 * Sets the access keys.
	 *
	 * @param accessKeys the new access keys
	 */
	public void setAccessKeys(List<AccessKeyMetadataVH> accessKeys) {
		this.accessKeys = accessKeys;
	}
	
	/**
	 * Sets the password creation date.
	 *
	 * @param passwordCreationDate the new password creation date
	 */
	public void setPasswordCreationDate(Date passwordCreationDate) {
		this.passwordCreationDate = passwordCreationDate;
	}
	
	/**
	 * Sets the password reset required.
	 *
	 * @param passwordResetRequired the new password reset required
	 */
	public void setPasswordResetRequired(boolean passwordResetRequired) {
		this.passwordResetRequired = passwordResetRequired;
	}
	
	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}
	
	/**
	 * Sets the groups.
	 *
	 * @param groups the new groups
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	/**
	 * Gets the access keys.
	 *
	 * @return the access keys
	 */
	public List<AccessKeyMetadataVH> getAccessKeys() {
		return accessKeys;
	}
	
	/**
	 * Gets the password creation date.
	 *
	 * @return the password creation date
	 */
	public Date getPasswordCreationDate() {
		return passwordCreationDate;
	}
	
	/**
	 * Checks if is password reset required.
	 *
	 * @return true, if is password reset required
	 */
	public boolean isPasswordResetRequired() {
		return passwordResetRequired;
	}
}
