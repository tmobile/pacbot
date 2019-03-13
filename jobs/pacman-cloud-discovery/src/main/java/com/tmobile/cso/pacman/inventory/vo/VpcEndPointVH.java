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

import com.amazonaws.services.ec2.model.VpcEndpoint;


/**
 * The Class VpcEndPointVH.
 */
public class VpcEndPointVH extends VpcEndpoint{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4770485285110362692L;
	
	/** The public access. */
	private boolean publicAccess;
	
	/**
	 * Checks if is public access.
	 *
	 * @return true, if is public access
	 */
	public boolean isPublicAccess() {
		return publicAccess;
	}

	/**
	 * Sets the public access.
	 *
	 * @param publicAccess the new public access
	 */
	public void setPublicAccess(boolean publicAccess) {
		this.publicAccess = publicAccess;
	}

	/**
	 * Instantiates a new vpc end point VH.
	 *
	 * @param vpcEndpoint the vpc endpoint
	 */
	public VpcEndPointVH(VpcEndpoint vpcEndpoint) {
		this.setCreationTimestamp(vpcEndpoint.getCreationTimestamp());
		this.setPolicyDocument(vpcEndpoint.getPolicyDocument());
		this.setRouteTableIds(vpcEndpoint.getRouteTableIds());
		this.setServiceName(vpcEndpoint.getServiceName());
		this.setState(vpcEndpoint.getState());
		this.setVpcEndpointId(vpcEndpoint.getVpcEndpointId());
		this.setVpcId(vpcEndpoint.getVpcId());
	}
}
