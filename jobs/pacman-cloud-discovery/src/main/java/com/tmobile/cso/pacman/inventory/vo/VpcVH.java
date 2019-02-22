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

import com.amazonaws.services.ec2.model.Vpc;


/**
 * The Class VpcVH.
 */
public class VpcVH {

	/** The vpc. */
	private Vpc vpc;
	
	/** The vpc end points. */
	private List<VpcEndPointVH> vpcEndPoints;
	
	/**
	 * Gets the vpc.
	 *
	 * @return the vpc
	 */
	public Vpc getVpc() {
		return vpc;
	}
	
	/**
	 * Sets the vpc.
	 *
	 * @param vpc the new vpc
	 */
	public void setVpc(Vpc vpc) {
		this.vpc = vpc;
	}
	
	/**
	 * Gets the vpc end points.
	 *
	 * @return the vpc end points
	 */
	public List<VpcEndPointVH> getVpcEndPoints() {
		return vpcEndPoints;
	}
	
	/**
	 * Sets the vpc end points.
	 *
	 * @param vpcEndPoints the new vpc end points
	 */
	public void setVpcEndPoints(List<VpcEndPointVH> vpcEndPoints) {
		this.vpcEndPoints = vpcEndPoints;
	}
	
}
