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
package com.tmobile.pacman.api.admin.exceptions;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * ExceptionResponse Custom Exception Response Class
 */
public class ExceptionResponse {
	
	private Map<String, String> data = Maps.newHashMap();
	private String message;
	
	public ExceptionResponse(String message) {
		super();
		this.message = message;
	}

	public ExceptionResponse(String message, String details) {
		super();
		this.message = message;
		this.data.put("errorDetails", details);
	}

	public Map<String, String> getData() {
		return data;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
