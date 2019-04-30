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

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;
import com.amazonaws.services.elasticloadbalancingv2.model.Tag;


/**
 * The Class LoadBalancerVH.
 */
public class LoadBalancerVH {

	/** The availability zones. */
	private List<String> availabilityZones;

	/** The lb. */
	private LoadBalancer lb;

	/** The instances. */
	private List<Instance> instances;

	/** The tags. */
	private List<Tag> tags;

	/** The availability zones. */
    private List<String> subnets;

    /** The accessLogBucketName. */
	 String accessLogBucketName;

	 /** The accessLog. */
	 boolean accessLog;


	/**
	 * Instantiates a new load balancer VH.
	 *
	 * @param elb the elb
	 */
	public LoadBalancerVH(LoadBalancer elb){
		lb = elb;
		availabilityZones = new ArrayList<>();
		this.instances = new ArrayList<>();
		if(lb.getAvailabilityZones()!=null){
		    lb.getAvailabilityZones().forEach(e-> { availabilityZones.add(e.getZoneName());
            subnets.add(e.getSubnetId());});
		}
	}

	/**
	 * Instantiates a new load balancer VH.
	 *
	 * @param elb the elb
	 * @param tags the tags
	 */
	public LoadBalancerVH(LoadBalancer elb,List<Tag> tags, String accessLogBucketName, boolean accessLog){
		lb = elb;
		this.tags = tags;
		this.accessLog = accessLog;
		this.accessLogBucketName = accessLogBucketName;
		availabilityZones = new ArrayList<>();
		subnets = new ArrayList<>();
		this.instances = new ArrayList<>();
		if(lb.getAvailabilityZones()!=null){
		    lb.getAvailabilityZones().forEach(e-> { availabilityZones.add(e.getZoneName());
			                                        subnets.add(e.getSubnetId());});
		}

	}

	/**
	 * Sets the instances.
	 *
	 * @param instances the new instances
	 */
	public void setInstances( List<Instance> instances){
		this.instances = instances;
	}

}

