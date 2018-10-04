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
package com.tmobile.pacman.api.asset.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.tmobile.pacman.api.commons.utils.ResponseUtils;

/**
 * The Class to handle the asset service errors.
 */
@ControllerAdvice
public class ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    // TODO add MethodArgumentNotValidException handler
    // TODO remove such general handler
    
    /**
     * Logs the error
     *
     * @param e the exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void processValidationError(IllegalArgumentException e) {
        LOGGER.info("Returning HTTP 400 Bad Request", e);
    }

    /**
     * Returns the error with failure response
     *
     * @param e the exception
     * @return the response entity with failure response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> processValidationError(Exception e) {
        LOGGER.error("processValidationError " , e);
        return ResponseUtils.buildFailureResponse(e);
    }
}
