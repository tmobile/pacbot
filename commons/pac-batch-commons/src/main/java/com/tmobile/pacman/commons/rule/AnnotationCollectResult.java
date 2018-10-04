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
package com.tmobile.pacman.commons.rule;

// TODO: Auto-generated Javadoc
/**
 * The Class AnnotationCollectResult.
 */
public class AnnotationCollectResult {
	
	/** The status. */
	Boolean status;
	
	/** The desc. */
	String desc;

	/**
	 * Instantiates a new annotation collect result.
	 *
	 * @param status the status
	 * @param desc the desc
	 */
	public AnnotationCollectResult(Boolean status, String desc) {
		super();
		this.status = status;
		this.desc = desc;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}

	/**
	 * Gets the desc.
	 *
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Sets the desc.
	 *
	 * @param desc the new desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
