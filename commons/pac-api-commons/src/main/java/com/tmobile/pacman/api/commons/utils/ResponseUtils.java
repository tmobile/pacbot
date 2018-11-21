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
  Purpose:
  Author :kkumar
  Modified Date: Oct 19, 2017
  
**/
package com.tmobile.pacman.api.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmobile.pacman.api.commons.Constants;

public class ResponseUtils {

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static ResponseEntity<Object> buildSucessResponse(Object data) {
		Map<String, Object> response = new HashMap();
		response.put(Constants.DATA_KEY, data);
		response.put(Constants.STATUS_KEY, Constants.STATUS_SUCCESS);
		return ResponseEntity.ok().body((Object) response);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static ResponseEntity<Object> buildFailureResponse(Exception exception) {
		Map<String, Object> errorDetails = new HashMap();
		errorDetails.put(Constants.MESSAGE_KEY, exception.getMessage());
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body((Object) errorDetails);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static ResponseEntity<Object> buildFailureResponse(Exception exception, Object mockData) {
		Map<String, Object> errorDetails = new HashMap();
		errorDetails.put(Constants.MESSAGE_KEY, exception.getMessage());
		errorDetails.put(Constants.DATA_KEY, mockData);
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body((Object) errorDetails);
	}
	
	
	
	/**
     * 
     * @param obj
     * @return
     */
    public static ResponseEntity<Object> buildFailureResponse(Exception exception,Object errorInfo,HttpStatus status ) {
        Map<String, Object> errorDetails = new HashMap();
        errorDetails.put(Constants.STATUS, Constants.STATUS_FAILURE);
        errorDetails.put(Constants.ERROR_MESSAGE, exception.getMessage());
        if(errorInfo!=null)
            errorDetails.put(Constants.ERROR_DETAILS, errorInfo);
      
        return ResponseEntity.status(status==null?HttpStatus.EXPECTATION_FAILED:status).body((Object) errorDetails);
    }
    

}
