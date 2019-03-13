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

import com.amazonaws.services.health.model.AffectedEntity;
import com.amazonaws.services.health.model.EventDetails;


/**
 * The Class PhdVH.
 */
public class PhdVH {

	/** The event details. */
	EventDetails eventDetails ;
	
	/** The affected entities. */
	List<AffectedEntity> affectedEntities;
	
	/**
	 * Gets the event details.
	 *
	 * @return the event details
	 */
	public EventDetails getEventDetails() {
		return eventDetails;
	}
	
	/**
	 * Sets the event details.
	 *
	 * @param eventDetails the new event details
	 */
	public void setEventDetails(EventDetails eventDetails) {
		this.eventDetails = eventDetails;
	}
	
	/**
	 * Gets the affected entities.
	 *
	 * @return the affected entities
	 */
	public List<AffectedEntity> getAffectedEntities() {
		return affectedEntities;
	}
	
	/**
	 * Sets the affected entities.
	 *
	 * @param affectedEntities the new affected entities
	 */
	public void setAffectedEntities(List<AffectedEntity> affectedEntities) {
		this.affectedEntities = affectedEntities;
	}
}
