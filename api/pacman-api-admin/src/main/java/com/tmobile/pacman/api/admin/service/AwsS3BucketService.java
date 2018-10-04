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
package com.tmobile.pacman.api.admin.service;

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tmobile.pacman.api.admin.util.AdminUtils;

/**
 * AwsS3Bucket Service Implementations
 */
@Service
public class AwsS3BucketService {
	
	private static final Logger log = LoggerFactory.getLogger(AwsS3BucketService.class);

	public boolean uploadFile(final AmazonS3 amazonS3, MultipartFile fileToUpload, String s3BucketName, String key) {
		try {
			File file = AdminUtils.convert(fileToUpload);
			long size = fileToUpload.getSize();
			String contentType = fileToUpload.getContentType();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			metadata.setContentLength(size);
			PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, key, file).withCannedAcl(CannedAccessControlList.PublicRead);
			amazonS3.putObject(putObjectRequest);
			return Boolean.TRUE;
		} catch (IOException exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		} 
		return Boolean.FALSE;
	}
}
