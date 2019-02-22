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
package com.tmobile.cso.pacman.inventory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tmobile.cso.pacman.inventory.dao.RDSDBManager;
import com.tmobile.cso.pacman.inventory.file.AssetFileGenerator;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.S3Uploader;

/**
 * The Class InventoryFetchOrchestrator.
 */
@Component
public class InventoryFetchOrchestrator {
	
	
	/** The target types. */
	@Value("${accountinfo:}")
	private String accountInfo;
	
	@Value("${region.ignore}")
    private String skipRegions;
	
	@Value("${s3}")
	private String s3Bucket ;
	
	@Value("${s3.data}")
	private String s3Data ;
	
	@Value("${s3.processed}")
	private String s3Processed ;
	
	@Value("${s3.region}")
	private String s3Region ;
	
	@Value("${file.path}")
	private String filePath ;

	/** The accounts. */
	private List<Map<String,String>> accounts;
	
	/** The file generator. */
	@Autowired
	AssetFileGenerator fileGenerator;
	
	/** The s 3 uploader. */
	@Autowired
	S3Uploader s3Uploader;
	
	/** The s 3 uploader. */
	@Autowired
	RDSDBManager rdsDBManager;
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(InventoryFetchOrchestrator.class);
	
	/**
	 * Instantiates a new inventory fetch orchestrator.
	 *
	 * @param accountInfo the account info
	 */
	
	private void fetchAccountInfo() {
		String accountQuery = "SELECT accountId,accountName,STATUS FROM cf_Aws_Accounts where status = 'onboarded'";
		
		// Check DB if account information is available in DB.
		
		if( accountInfo == null || "".equals(accountInfo)){
			accounts = rdsDBManager.executeQuery(accountQuery);
		}else{
			String accountlist = Arrays.asList(accountInfo.split(",")).stream().collect(Collectors.joining("','"));
			accounts = rdsDBManager.executeQuery(accountQuery +" AND accountid IN ('"+ accountlist+ "')");	
		}
		// No info from DB. Okay lets use what parameter we get.
		if(accounts.isEmpty()){
			String[] accountArray = accountInfo.split(",");
			for(String account : accountArray){
				Map<String,String> accountMap = new HashMap<>();
				accountMap.put(InventoryConstants.ACCOUNT_ID,account);
				accountMap.put(InventoryConstants.ACCOUNT_NAME,"");
				accounts.add(accountMap);
			}
		}
	}

	/**
	 * Orchestrate.
	 * @return 
	 */
	public Map<String, Object> orchestrate(){
	    
		try{
			fetchAccountInfo();
			log.info("Inventory Fetch requested for Accounts {}",accounts);
			log.info("Start : Asset Discovery and File Creation");
			fileGenerator.generateFiles(accounts,skipRegions,filePath);
			log.info("End : Asset Discovery and File Creation");
			
			log.info("Start : Backup Current Files");
			s3Uploader.backUpFiles(s3Bucket, s3Region, s3Data, s3Processed+ "/"+ new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
			log.info("End : Backup Current Files");
		
			log.info("Start : Upload Files to S3");
			s3Uploader.uploadFiles(s3Bucket,s3Data,s3Region,filePath);
			log.info("End : Upload Files to S3");
		    
		}catch(Exception e){
			log.error("Asset Discovery Failed" , e);
		}
		
		return ErrorManageUtil.formErrorCode();
	}
	
}
