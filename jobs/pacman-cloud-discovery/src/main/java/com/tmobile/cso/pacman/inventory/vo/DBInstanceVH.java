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

import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.Tag;


/**
 * The Class DBInstanceVH.
 */
public class DBInstanceVH {
	
	/** The dbinst. */
	private DBInstance dbinst;
	
	/** The tags. */
	private List<Tag> tags;
	
	/** Subnets associated with the instance */
	private String subnets;
	
	private String securityGroups;
	
	public String getSecurityGroups() {
		return securityGroups;
	}

	public void setSecurityGroups(String securityGroups) {
		this.securityGroups = securityGroups;
	}

	public String getSubnets() {
		return subnets;
	}

	public void setSubnets(String subnets) {
		this.subnets = subnets;
	}

	/**
	 * Instantiates a new DB instance VH.
	 *
	 * @param dbinstance the dbinstance
	 * @param tags the tags
	 */
	public DBInstanceVH(DBInstance  dbinstance, List<Tag> tags,String subnets,String securityGroups){
		this.setDbinst(dbinstance);
		this.setTags(tags);
		this.subnets =subnets;
		this.securityGroups = securityGroups;
	}
	
	/**
	 * Gets the dbinst.
	 *
	 * @return the dbinst
	 */
	public DBInstance getDbinst() {
		return dbinst;
	}
	
	/**
	 * Sets the dbinst.
	 *
	 * @param dbinst the new dbinst
	 */
	public void setDbinst(DBInstance dbinst) {
		this.dbinst = dbinst;
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

}
