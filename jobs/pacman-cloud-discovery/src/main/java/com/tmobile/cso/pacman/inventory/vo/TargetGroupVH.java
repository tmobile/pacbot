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

import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetHealthDescription;


/**
 * The Class TargetGroupVH.
 */
public class TargetGroupVH {
	
	/** The trgt grp. */
	TargetGroup trgtGrp;
	
	/** The targets. */
	List<TargetHealthDescription> targets;
	
	/**
	 * Instantiates a new target group VH.
	 *
	 * @param trgtGrp the trgt grp
	 * @param targets the targets
	 */
	public TargetGroupVH(TargetGroup trgtGrp, List<TargetHealthDescription> targets){
		this.trgtGrp = trgtGrp;
		this.targets = targets;
	}
	
	/**
	 * Gets the trgt grp.
	 *
	 * @return the trgt grp
	 */
	public TargetGroup getTrgtGrp() {
		return trgtGrp;
	}
	
	/**
	 * Gets the targets.
	 *
	 * @return the targets
	 */
	public List<TargetHealthDescription> getTargets() {
		return targets;
	}
}
