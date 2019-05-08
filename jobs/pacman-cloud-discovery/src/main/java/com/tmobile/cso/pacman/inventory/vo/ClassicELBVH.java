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

import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.Tag;


/**
 * The Class ClassicELBVH.
 */
public class ClassicELBVH {

	/** The elb. */
	LoadBalancerDescription elb;

	/** The tags. */
	List<Tag> tags;

	/** The accessLogBucketName. */
	String accessLogBucketName;

	/** The accessLog. */
	boolean accessLog;

	/**
	 * Instantiates a new classic ELBVH.
	 *
	 * @param elb the elb
	 * @param tags the tags
	 */
	public ClassicELBVH(LoadBalancerDescription elb,List<Tag> tags, String accessLogBucketName, boolean accessLog){
		this.elb = elb;
		this.tags = tags;
		this.accessLogBucketName = accessLogBucketName;
		this.accessLog = accessLog;
	}

}
