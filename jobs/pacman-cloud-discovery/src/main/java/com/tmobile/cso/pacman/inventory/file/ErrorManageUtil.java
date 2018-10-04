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
package com.tmobile.cso.pacman.inventory.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tmobile.cso.pacman.inventory.vo.ErrorVH;


/**
 * The Class ErrorManageUtil.
 */
public class ErrorManageUtil {

    /** The log. */
    private static Logger log = LogManager.getLogger(ErrorManageUtil.class);
	
	/** The error map. */
	private static Map<String,List<ErrorVH>> errorMap = new HashMap<>();
	
	/**
	 * Instantiates a new error manage util.
	 */
	private ErrorManageUtil() {
		
	}
	
	/**
	 * Initialise.
	 */
	public static void initialise(){
		String fieldNames = "loaddate`accountid`type`region`exception\n";
		try {
			FileGenerator.writeToFile("load-error.data", fieldNames, false);
		} catch (IOException e) {
			log.error("Error in Initialise",e);
		}
	}
	
	/**
	 * Upload error.
	 *
	 * @param account the account
	 * @param region the region
	 * @param type the type
	 * @param exception the exception
	 */
	public static synchronized void  uploadError(String account, String region, String type, String exception) {
		try{
			List<ErrorVH> errorList = errorMap.get(account);
			if(errorList==null){
				errorList =  new ArrayList<>();
				errorMap.put(account, errorList);
			}
			ErrorVH error = new ErrorVH();
			error.setException(exception);
			error.setRegion(region);
			error.setType(type);
			errorList.add(error);
		}catch(Exception e){
		    log.error("Error in uploadError",e);
		}
	}
	
	/**
	 * Write error file.
	 */
	public static void writeErrorFile(){
		try{
			FileManager.generateErrorFile(errorMap);
		}catch(Exception e){
		    log.error("Error in writeErrorFile",e);
		}
	}
}
