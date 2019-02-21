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


/**
 * The Class Resource.
 */
public class Resource {
	
	/** The check id. */
	String checkId;
	
	/** The id. */
	String id;
	
	/** The status. */
	String status;
	
	/** The data. */
	String data;
	
	/**
	 * Instantiates a new resource.
	 *
	 * @param checkId the check id
	 * @param id the id
	 * @param status the status
	 * @param data the data
	 */
	public Resource(String checkId, String id,String status,String data){
		this.checkId = checkId;
		this.id = id;
		this.status = status;
		this.data = data;
	}
	
}
