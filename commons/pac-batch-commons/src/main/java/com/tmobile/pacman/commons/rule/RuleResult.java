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
/**
Copyright (C) 2017 T Mobile Inc - All Rights Reserve
Purpose: Execution Request for a rule, rule will be expecting key/value pair parameters in param field
Author :kkumar
Modified Date: Jun 14, 2017

**/
package com.tmobile.pacman.commons.rule;

import java.util.Map;

import com.tmobile.pacman.commons.PacmanSdkConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleResult.
 */
public class RuleResult  {

	/** The annotation. */
	Annotation annotation;

	/** The status. */
	String status;

	/** The desc. */
	String desc;

	/** The resource. */
	private Map<String, String> resource;

	/**
	 * Instantiates a new rule result.
	 */
	public RuleResult() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new rule result.
	 *
	 * @param status the status
	 * @param desc the desc
	 */
	public RuleResult(String status,String desc)  {
		if(PacmanSdkConstants.STATUS_FAILURE.equalsIgnoreCase(status)){
			throw new RuntimeException("annotation expecetd in case of success, please use the other constructor");
		}
		this.status=status;
		this.desc=desc;
	}

	/**
	 * Instantiates a new rule result.
	 *
	 * @param status the status
	 * @param desc the desc
	 * @param annotation the annotation
	 */
	public RuleResult(String status,String desc, Annotation annotation)  {
		if(PacmanSdkConstants.STATUS_SUCCESS.equalsIgnoreCase(status)){
			throw new RuntimeException("annotation not expecetd in case of success");
		}
		this.status=status;
		this.desc=desc;
		this.annotation=annotation;
	}

	/**
	 * Gets the annotation.
	 *
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return annotation;
	}

	/**
	 * Sets the annotation.
	 *
	 * @param annotation the new annotation
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the desc.
	 *
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Sets the desc.
	 *
	 * @param desc the new desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected RuleResult clone() {
		RuleResult result = new RuleResult();
		result.setStatus(this.getStatus());
		result.setDesc(this.getDesc());
		result.setAnnotation(new Annotation(this.getAnnotation()));
		return result;

	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public Map<String, String> getResource() {
		return resource;
	}

	/**
	 * Sets the resource.
	 *
	 * @param resource the resource
	 */
	public void setResource(Map<String, String> resource) {
		this.resource = resource;
	}


}
