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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tmobile.cso.pacman.inventory.dao.DBLoader;
import com.tmobile.cso.pacman.inventory.file.AssetFileGenerator;
import com.tmobile.cso.pacman.inventory.file.S3Uploader;

/**
 * The Class InventoryFetchOrchestrator.
 */
@Component
public class InventoryFetchOrchestrator {

	/** The accounts. */
	private Set<String> accounts;
	
	/** The skip regions. */
	@Value("${region-ignore}")
	private String skipRegions;
	
	
	/** The s 3 bucket. */
	@Value("${s3}")
	private String s3Bucket;
	
	/** The s 3 data. */
	@Value("${s3-data}")
	private String s3Data;
	
	/** The s 3 processed. */
	@Value("${s3-processed}")
	private String s3Processed;
	
	/** The s 3 region. */
	@Value("${s3-region}")
	private String s3Region;
	
	/** The file path. */
	@Value("${file-path}")
	private String filePath;
	
	/** The file generator. */
	@Autowired
	AssetFileGenerator fileGenerator;
	
	/** The db loader. */
	@Autowired
	DBLoader dbLoader;

	/** The s 3 uploader. */
	@Autowired
	S3Uploader s3Uploader;
	
	/** The log. */
	private static Logger log = LogManager.getLogger(InventoryFetchOrchestrator.class);
	
	/**
	 * Instantiates a new inventory fetch orchestrator.
	 *
	 * @param accountInfo the account info
	 */
	@Autowired
	public InventoryFetchOrchestrator(@Value("${accountinfo}") String accountInfo){
		String[] accntNames = accountInfo.split(",");
		accounts = new HashSet<>();
		for(String accnt : accntNames){
			accounts.add(accnt);
		}
	}

	/**
	 * Orchestrate.
	 */
	public void orchestrate(){
		try{
		    log.info("Start : Create all missing tables in RedShift");
            dbLoader.runScriptFromFile("inventory-tables.sql");
            log.info("End : Create all missing tables in RedShift");
            
            log.info("Start : Create all missing views in RedShift");
            dbLoader.runScriptFromFile("inventory-views.sql");
            log.info("End : Create all missing views in RedShift");
            
			log.info("Start : Asset Discovery and File Creation");
			fileGenerator.generateFiles(accounts,skipRegions,filePath);
			log.info("End : Asset Discovery and File Creation");
	
			log.info("Start : Backup Current Files");
			s3Uploader.backUpFiles(s3Bucket, s3Region, s3Data, s3Processed+ File.separator+ new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
			log.info("End : Backup Current Files");
		
			log.info("Start : Upload Files to S3");
			s3Uploader.uploadFiles(s3Bucket,s3Data,s3Region,filePath);
			log.info("End : Upload Files to S3");
		
			log.info("Start : Load Redshift Tables");
			dbLoader.loadFilesToRedshift(s3Bucket,s3Data);
			log.info("End : Load Redshift Tables");
			
			
		}catch(Exception e){
			log.fatal("Asset Discovery Failed" + e);
		}
	}
	
	
	
}
