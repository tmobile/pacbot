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


/**
 * The Class Attribute.
 */
public class Attribute {
	
	/** The key. */
	private String key;
	
	/** The value. */
	private String value;
	
	/**
	 * Instantiates a new attribute.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public Attribute(String key, String value){
		this.key= key;
		this.value= value;
	}
	
	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
