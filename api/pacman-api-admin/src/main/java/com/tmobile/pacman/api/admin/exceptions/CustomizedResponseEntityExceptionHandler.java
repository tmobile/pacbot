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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tmobile.pacman.api.admin.common.AdminConstants;

/**
 * CustomizedResponseEntityExceptionHandler Custom Exception Class
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(RuleJarFileMissingException.class)
	public final ResponseEntity<Object> handlerRuleJarFileMissingException(RuleJarFileMissingException exception, WebRequest webRequest) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), AdminConstants.JAR_FILE_MISSING);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.EXPECTATION_FAILED);
	}
	
	@ExceptionHandler(PacManException.class)
	public final ResponseEntity<Object> handlerRuleJarFileMissingException(PacManException exception, WebRequest webRequest) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), exception.getMessage());
		return new ResponseEntity<>(exceptionResponse, HttpStatus.EXPECTATION_FAILED);
	}
}
