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

import com.amazonaws.services.cloudfront.model.DistributionSummary;
import com.amazonaws.services.cloudfront.model.Tag;


/**
 * The Class CloudFrontVH.
 */
public class CloudFrontVH {

	/** The dist summary. */
	DistributionSummary distSummary;

	/** The tags. */
	List<Tag> tags;

	/** The bucketName. */
	String bucketName;

	/** The accessLogEnabled. */
	boolean accessLogEnabled;
	
	/**  Default Root Object for the distribution *. */
	String defaultRootObject;

	public String getDefaultRootObject() {
		return defaultRootObject;
	}



	public void setDefaultRootObject(String defaultRootObject) {
		this.defaultRootObject = defaultRootObject;
	}



	/**
	 * Gets the dist summary.
	 *
	 * @return the dist summary
	 */
	public DistributionSummary getDistSummary() {
		return distSummary;
	}
	
	

	/**
	 * Sets the dist summary.
	 *
	 * @param distSummary the new dist summary
	 */
	public void setDistSummary(DistributionSummary distSummary) {
		this.distSummary = distSummary;
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

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public boolean isAccessLogEnabled() {
		return accessLogEnabled;
	}

	public void setAccessLogEnabled(boolean accessLogEnabled) {
		this.accessLogEnabled = accessLogEnabled;
	}


}
