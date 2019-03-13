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

import com.amazonaws.services.support.model.TrustedAdvisorCheckDescription;


/**
 * The Class CheckVH.
 */
public class CheckVH {
	
	/** The check. */
	private TrustedAdvisorCheckDescription check;
	
	/** The status. */
	private String status;
	
	/** The resources. */
	private List<Resource> resources;
	
	/**
	 * Instantiates a new check VH.
	 *
	 * @param check the check
	 * @param status the status
	 */
	public CheckVH(TrustedAdvisorCheckDescription check,String status){
		this.check = check;
		this.status = status;
	}
	
	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}
	
	/**
	 * Sets the resources.
	 *
	 * @param resources the new resources
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	/**
	 * Gets the check.
	 *
	 * @return the check
	 */
	public TrustedAdvisorCheckDescription getCheck() {
		return check;
	}
	
	/**
	 * Sets the check.
	 *
	 * @param check the new check
	 */
	public void setCheck(TrustedAdvisorCheckDescription check) {
		this.check = check;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}



