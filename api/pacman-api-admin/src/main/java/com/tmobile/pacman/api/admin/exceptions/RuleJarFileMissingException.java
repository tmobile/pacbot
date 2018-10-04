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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.tmobile.pacman.api.admin.common.AdminConstants;

/**
 * RuleJarFileMissingException Custom Runtime Exception Class
 */
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class RuleJarFileMissingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuleJarFileMissingException(String message) {
		super(message);
	}
	
	public RuleJarFileMissingException() {
		super(AdminConstants.FAILED);
	}
}
