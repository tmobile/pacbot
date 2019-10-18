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
package com.tmobile.pacbot.azure.inventory.file;

import java.io.File;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.tmobile.pacbot.azure.inventory.ErrorManageUtil;
import com.tmobile.pacbot.azure.inventory.auth.AWSCredentialProvider;

/**
 * The Class S3Uploader.
 */
@Component
public class S3Uploader {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(S3Uploader.class);
	
	
	/** The account. */
	@Value("${base.account}")
	private String account;
	
	/** The account. */
	@Value("${s3.role}")
	private String s3Role;
	
	@Value("${base.region}")
	private String region ;
	
	/** The cred provider. */
	@Autowired
	AWSCredentialProvider credProvider;
	
	/**
	 * Upload files.
	 *
	 * @param s3Bucket the s 3 bucket
	 * @param dataFolder the data folder
	 * @param s3Region the s 3 region
	 * @param filePath the file path
	 */
	public void uploadFiles(String s3Bucket,String dataFolder, String s3Region,String filePath){
		BasicSessionCredentials credentials = credProvider.getCredentials(account,region,s3Role);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(s3Region).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		uploadAllFiles(s3client,s3Bucket,dataFolder,filePath);
	}
	
	/**
	 * Back up files.
	 *
	 * @param s3Bucket the s 3 bucket
	 * @param s3Region the s 3 region
	 * @param from the from
	 * @param to the to
	 */
	public void backUpFiles(String s3Bucket,String s3Region,String from,String to){
		BasicSessionCredentials credentials = credProvider.getCredentials(account,region,s3Role);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(s3Region).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		log.info("Backing up  files from  : {} to : {} in bucket : {}",from,to,s3Bucket);
		copytoBackUp(s3client,s3Bucket,from,to);
		deleteFiles(s3client,s3Bucket,from);
	}
	
	/**
	 * Upload all files.
	 *
	 * @param s3client the s 3 client
	 * @param s3Bucket the s 3 bucket
	 * @param dataFolderS3 the data folder S 3
	 * @param filePath the file path
	 */
	private void uploadAllFiles(AmazonS3 s3client,String s3Bucket,String dataFolderS3, String filePath){
		log.info("Uploading files to bucket: {} folder: {}",s3Bucket,dataFolderS3);
		TransferManager xferMgr = TransferManagerBuilder.standard().withS3Client(s3client).build();
		try {
		    MultipleFileUpload xfer = xferMgr.uploadDirectory(s3Bucket,
		    		dataFolderS3, new File(filePath), false);
		   
		    while(!xfer.isDone()){
		    	try{
		    		Thread.sleep(3000);
		    	}catch(InterruptedException e){
		    	    log.error("Error in uploadAllFiles",e);
		    	    ErrorManageUtil.uploadError("all", "all", "all", e.getMessage());
		    	    Thread.currentThread().interrupt();
		    	}
		    	log.debug("    Transfer % Completed :" +xfer.getProgress().getPercentTransferred());
		    }
		    xfer.waitForCompletion();
		   
		   log.info("Transfer completed");
		} catch (Exception e) {
			log.error("{\"errcode\": \"S3_UPLOAD_ERR\" ,\"account\": \"ANY\",\"Message\": \"Exception in loading files to S3\", \"cause\":\"" +e.getMessage()+"\"}") ;
			ErrorManageUtil.uploadError("all", "all", "all", e.getMessage());
		}
		 xferMgr.shutdownNow();
	}
	
	/**
	 * Copyto back up.
	 *
	 * @param s3client the s 3 client
	 * @param s3Bucket the s 3 bucket
	 * @param from the from
	 * @param to the to
	 */
	private void copytoBackUp(AmazonS3 s3client,String s3Bucket,String from, String to){
		String[] keys = listKeys(s3client,s3Bucket,from);
		String fileName ="";
		for(String key:keys){
			try{
				fileName = key.substring(key.lastIndexOf('/')+1);
				s3client.copyObject(s3Bucket,key,s3Bucket,to+"/"+fileName);
				log.debug("    Copy "+fileName + " to backup folder");
			}catch(Exception e){
				log.info("    Copy "+fileName + "failed",e);
				ErrorManageUtil.uploadError("all", "all", "all", e.getMessage());
			}
		}
	}
	
	/**
	 * Delete files.
	 *
	 * @param s3client the s 3 client
	 * @param s3Bucket the s 3 bucket
	 * @param folder the folder
	 */
	private void deleteFiles(AmazonS3 s3client,String s3Bucket,String folder){
		
		String[] keys = listKeys(s3client,s3Bucket,folder);
		DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(s3Bucket).withKeys((keys));
		
		try{
			DeleteObjectsResult result = s3client.deleteObjects(multiObjectDeleteRequest);
			log.debug("Files Deleted " +result.getDeletedObjects().stream().map(obj->obj.getKey()).collect(Collectors.toList()));
		}catch(Exception e){
			log.error("Delete Failed",e);
			ErrorManageUtil.uploadError("all", "all", "all", e.getMessage());
		}
	}
	
	/**
	 * List keys.
	 *
	 * @param s3client the s 3 client
	 * @param s3Bucket the s 3 bucket
	 * @param folder the folder
	 * @return the string[]
	 */
	private String[] listKeys(AmazonS3 s3client,String s3Bucket,String folder){
		try{
			return s3client.listObjectsV2(new ListObjectsV2Request().withBucketName(s3Bucket).withPrefix(folder)).getObjectSummaries().stream().map(S3ObjectSummary::getKey).toArray(String[]::new);
		}catch(Exception e){
		    log.error("Error in listKeys",e);
		    ErrorManageUtil.uploadError("all", "all", "all", e.getMessage());
		}
		return new String[0];
	}

}
