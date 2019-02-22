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

import com.amazonaws.services.elasticsearch.model.ElasticsearchDomainStatus;
import com.amazonaws.services.elasticsearch.model.Tag;


/**
 * The Class ElasticsearchDomainVH.
 */
public class ElasticsearchDomainVH {

	/** The domain. */
	private ElasticsearchDomainStatus domain;
	
	/** The tags. */
	private List<Tag> tags;
	
	/**
	 * Gets the elasticsearch domain status.
	 *
	 * @return the elasticsearch domain status
	 */
	public ElasticsearchDomainStatus getElasticsearchDomainStatus() {
		return domain;
	}
	
	/**
	 * Sets the elasticsearch domain status.
	 *
	 * @param domain the new elasticsearch domain status
	 */
	public void setElasticsearchDomainStatus(
			ElasticsearchDomainStatus domain) {
		this.domain = domain;
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
