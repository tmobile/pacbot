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
package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.Tag;


/**
 * The Class BucketVH.
 */
public class BucketVH {

	/** The bucket. */
	Bucket bucket;

	/** The tags. */
	List<Tag> tags ;

	/** The location. */
	String location;

	/** The version status. */
	String versionStatus;

	/** The mfa delete. */
	Boolean mfaDelete;

	/** The Bucket Encryption. */
    String bucketEncryp;
    
    Boolean websiteConfiguration;

    Boolean isLoggingEnabled;
    
    /** The destination bucket name. */
    String destinationBucketName;
    
    /** The log file prefix. */
    String logFilePrefix;


	/**
	 * Instantiates a new bucket VH.
	 *
	 * @param bucket the bucket
	 * @param location the location
	 * @param versionConfig the version config
	 * @param tags the tags
	 */
	public BucketVH(Bucket bucket,String location,BucketVersioningConfiguration versionConfig, List<Tag> tags, String bucketEncryp, boolean websiteConfiguration,BucketLoggingConfiguration bucketLoggingConfiguration){
		this.bucket = bucket;
		this.location = location;
		this.versionStatus = versionConfig==null?"":versionConfig.getStatus();
		this.mfaDelete =  versionConfig==null?null:versionConfig.isMfaDeleteEnabled();
		this.tags = tags;
		this.bucketEncryp = bucketEncryp;
		this.websiteConfiguration = websiteConfiguration;
        this.isLoggingEnabled = bucketLoggingConfiguration==null?null:bucketLoggingConfiguration.isLoggingEnabled();
        this.destinationBucketName = bucketLoggingConfiguration==null?"":bucketLoggingConfiguration.getDestinationBucketName();
        this.logFilePrefix = bucketLoggingConfiguration==null?"":bucketLoggingConfiguration.getLogFilePrefix();

	}
}
