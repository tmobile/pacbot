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

import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.Tag;


/**
 * The Class DBClusterVH.
 */
public class DBClusterVH {
	
	/** The cluster. */
	private DBCluster cluster;
	
	/** The tags. */
	private List<Tag> tags;
	
	/**
	 * Instantiates a new DB cluster VH.
	 *
	 * @param cluster the cluster
	 * @param tags the tags
	 */
	public DBClusterVH(DBCluster cluster, List<Tag> tags){
		this.cluster = cluster;
		this.tags = tags;
	}
	
	/**
	 * Gets the cluster.
	 *
	 * @return the cluster
	 */
	public DBCluster getCluster() {
		return cluster;
	}
	
	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}

}
