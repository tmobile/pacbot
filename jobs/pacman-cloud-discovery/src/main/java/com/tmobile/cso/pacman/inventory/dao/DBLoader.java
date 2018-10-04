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
package com.tmobile.cso.pacman.inventory.dao;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicSessionCredentials;
import com.tmobile.cso.pacman.inventory.InventoryFetchOrchestrator;
import com.tmobile.cso.pacman.inventory.auth.CredentialProvider;


/**
 * The Class DBLoader.
 */
@Component
public class DBLoader {
    
    /** The resource loader. */
    @Autowired
    private ResourceLoader resourceLoader;
	
	/** The file tableinfo. */
	@Value("${file-tableinfo}")
	private String fileTableinfo;
	
	/** The account. */
	@Value("${base-account}")
	private String account;
	
	/** The thread count. */
	@Value("${file-load-threads}")
	private int threadCount;
	
	/** The role name. */
	@Value("${s3-role}")
	private String roleName;
	
	/** The cred provider. */
	@Autowired
	CredentialProvider credProvider;
	
	/** The log. */
	private static Logger log = LogManager.getLogger(InventoryFetchOrchestrator.class);
	
	/**
	 * Load files to redshift.
	 *
	 * @param s3Bucket the s 3 bucket
	 * @param folder the folder
	 */
	public void loadFilesToRedshift(String s3Bucket, String folder){
		BasicSessionCredentials credentials = credProvider.getCredentials(account,roleName);
		String accessKey = credentials.getAWSAccessKeyId();
		String secretKey = credentials.getAWSSecretKey();
		String sessionToken = credentials.getSessionToken();
		
		String[] fileTableInfo = fileTableinfo.split(",");
		String[] fileTable ;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		for(String fileTableStr :fileTableInfo){
			fileTable = fileTableStr.split(":");
			String file = fileTable[0].trim();
			String table = fileTable[1].trim();
			executor.execute(()->{
				
				try{
					log.debug("    Loading "+file +" >> "+table);
					String query = createCopyQuery(table,s3Bucket,folder,file,accessKey,secretKey,sessionToken);
					DBManager.executeUpdate(query);
					log.debug("    Finished Loading "+file +" >> "+table);
				}catch(Exception e) {
					log.fatal("{\"errcode\": \"REDSHIFT_LOAD_ERR\" ,\"account\": \"ANY\",\"Message\": \"Exception in loading redshfit table :"+table+"\", \"cause\":\"" +e.getMessage()+"\"}") ;
				}
			});
		}
		executor.shutdown();
		while(!executor.isTerminated()){
			
		}
		
	}
	
	/**
	 * Creates the copy query.
	 *
	 * @param tableName the table name
	 * @param s3Bucket the s 3 bucket
	 * @param folder the folder
	 * @param fileName the file name
	 * @param accessKey the access key
	 * @param secretKey the secret key
	 * @param sessionToken the session token
	 * @return the string
	 */
	private String createCopyQuery(String tableName,String s3Bucket,String folder, String fileName,String accessKey, String secretKey,String sessionToken){
		String queryTemplate = "copy %s from 's3://%s/%s/%s' IGNOREHEADER AS 1 DELIMITER '`'  ACCESS_KEY_ID '%s' SECRET_ACCESS_KEY '%s' SESSION_TOKEN '%s'";
		return String.format(queryTemplate, tableName,s3Bucket,folder,fileName,accessKey,secretKey,sessionToken);		
	}

    /**
     * Run script from file.
     *
     * @param fileName the file name
     */
    public void runScriptFromFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            final Resource fileResource = resourceLoader.getResource("classpath:"+fileName);

            DataInputStream in = new DataInputStream(fileResource.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = null;

            while ((strLine = br.readLine()) != null) {
                stringBuilder.append(strLine);
            }
            in.close();
        } catch (IOException e) {
           log.debug("Unable to create required tables!");
        }
        DBManager.executeUpdate(stringBuilder.toString());
        
    }
	
}
