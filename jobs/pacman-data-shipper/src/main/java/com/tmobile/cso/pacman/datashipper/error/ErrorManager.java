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
package com.tmobile.cso.pacman.datashipper.error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.datashipper.config.CredentialProvider;
import com.tmobile.cso.pacman.datashipper.util.Constants;

/**
 * The Class AWSErrorManager.
 */
public abstract class ErrorManager implements Constants {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorManager.class);
	
	/** The s 3 account. */
	private String s3Account = System.getProperty("base.account");
	
	/** The s 3 region. */
	private String s3Region = System.getProperty("base.region");
	
	/** The s 3 role. */
	private String s3Role =  System.getProperty("s3.role");
	
	/** The bucket name. */
	private String bucketName =  System.getProperty("s3");
	
	/** The data path. */
	private String dataPath =  System.getProperty("s3.data");
	
	/** The error info. */
	private Map<String,List<Map<String,String>>> errorInfo ;
	
	protected String dataSource;
	
	/** The error manager. */
	private static ErrorManager errorManager ;
	
	/**
	 * Instantiates a new AWS error manager.
	 */
	
	
	/**
	 * Gets the single instance of AWSErrorManager.
	 *
	 * @return single instance of AWSErrorManager
	 */
	public static ErrorManager getInstance(String dataSource){
		if(errorManager==null){
			switch(dataSource) {
				case "aws":
					errorManager = new AwsErrorManager(); 
					errorManager.dataSource ="aws";
					break;
				case "azure":
					errorManager = new AzureErrorManager(); 
					errorManager.dataSource ="azure";
					break;
				default:
			}
			
		}
		return errorManager;
	}
	
	/**
	 * Fetch error info.
	 *
	 * @param datasource the datasource
	 * @param errorList the error list
	 */
	private void fetchErrorInfo(List<Map<String,String>> errorList){
		if(errorInfo==null){
			ObjectMapper objectMapper = new ObjectMapper();
	    	List<Map<String, String>> inventoryErrors = new ArrayList<>();
	    	AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                .withCredentials(new AWSStaticCredentialsProvider(new CredentialProvider().getCredentials(s3Account,s3Role))).withRegion(s3Region).build();
	    	try {
		    	S3Object inventoryErrorData = s3Client.getObject(new GetObjectRequest(bucketName,dataPath+"/"+dataSource+"-loaderror.data"));
		    	try (BufferedReader reader = new BufferedReader(new InputStreamReader(inventoryErrorData.getObjectContent()))) {
					inventoryErrors = objectMapper.readValue(reader.lines().collect(Collectors.joining("\n")),new TypeReference<List<Map<String, String>>>() {});
		        }
	    	} catch (IOException e) {
	    		LOGGER.error("Exception in collecting inventory error data",e);
	            Map<String,String> errorMap = new HashMap<>();
	            errorMap.put(ERROR, "Exception in collecting inventory error data");
	            errorMap.put(ERROR_TYPE, WARN);
	            errorMap.put(EXCEPTION, e.getMessage());
	            errorList.add(errorMap);
			}
	    	errorInfo = inventoryErrors.parallelStream().collect(Collectors.groupingBy(obj -> obj.get("type")));
		}
	}
	
	/**
	 * Gets the error info.
	 *
	 * @param datasource the datasource
	 * @param errorList the error list
	 * @return the error info
	 */
	public Map<String,List<Map<String,String>>> getErrorInfo(List<Map<String,String>> errorList){
		if(errorInfo==null){
			fetchErrorInfo(errorList);
		}
		
		return errorInfo;
		
	}
	
	public abstract Map<String, Long> handleError(String index, String type, String loaddate,List<Map<String,String>> errorList,boolean checkLatest) ;
		

	

}
