/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
<<<<<<< HEAD
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
=======
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.cso.pacman.inventory.vo;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.identitymanagement.model.Group;


/**
 * The Class GroupVH.
 */
public class GroupVH {
<<<<<<< HEAD
	
	/** The user. */
	private Group group;
	
	/** The policies. */
	private List<String> policies;
	
=======

	/** The user. */
	private Group group;

	/** The policies. */
	private List<String> policies;

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
	/**
	 * Instantiates a new user VH.
	 *
	 * @param usr the usr
	 */
	public GroupVH(Group grp){
		this.group = grp;
	}
<<<<<<< HEAD
	
=======

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
	/**
	 * Gets the policies.
	 *
	 * @return the policies
	 */
	public List<String> getPolicies() {
		return policies;
	}
<<<<<<< HEAD
	
=======

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
	/**
	 * Sets the groups.
	 *
	 * @param groups the new groups
	 */
	public void setPolicies(List<String> policies) {
		this.policies = policies;
<<<<<<< HEAD
	}		
=======
	}
>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
}
