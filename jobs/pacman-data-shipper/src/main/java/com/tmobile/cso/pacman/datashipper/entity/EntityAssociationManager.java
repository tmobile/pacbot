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
package com.tmobile.cso.pacman.datashipper.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.config.CredentialProvider;
import com.tmobile.cso.pacman.datashipper.error.ErrorManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;

/**
 * The Class ChildTableDataCollector.
 */
public class EntityAssociationManager implements Constants {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityAssociationManager.class);
    
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

    /**
     * Execute.
     *
     * @param dataSource the data source
     * @param type the type
     * @return the list
     */
    public List<Map<String, String>> uploadAssociationInfo(String dataSource,String type) {
        LOGGER.info("Started EntityAssociationDataCollector for {}",type);
        List<Map<String,String>> errorList = new ArrayList<>();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(
                        new AWSStaticCredentialsProvider(new CredentialProvider().getCredentials(s3Account,s3Role))).withRegion(s3Region).build();
        ObjectMapper objectMapper = new ObjectMapper();
      
        try {
            String indexName = dataSource + "_" + type;
            String filePrefix = dataSource+"-"+type+"-";
            List<String> childTypes = new ArrayList<>();
            for (S3ObjectSummary objectSummary : s3Client.listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName).withPrefix(dataPath+"/"+filePrefix)).getObjectSummaries()) {
                String fileName = objectSummary.getKey().replace(dataPath+"/", "").replace(".data", "");
            	if(fileName.chars().filter(ch -> ch == '-').count() == 2) {
            		childTypes.add(fileName.replace(filePrefix,""));
                }
            }
            String key = ConfigManager.getKeyForType(dataSource, type);
            if (!childTypes.isEmpty()) {
                for (String childType : childTypes) {
                    String childTypeES = type+"_"+childType;
                    if (!childType.equalsIgnoreCase("tags")) {
                        ESManager.createType(indexName, childTypeES, type);
                        LOGGER.info("Fetching data for {}", childTypeES);
                        List<Map<String, Object>> entities = new ArrayList<>();
                        S3Object entitiesData = s3Client.getObject(new GetObjectRequest(bucketName, dataPath+"/"+filePrefix+childType+".data"));
						try (BufferedReader reader = new BufferedReader(new InputStreamReader(entitiesData.getObjectContent()))) {
                        	entities = objectMapper.readValue(reader.lines().collect(Collectors.joining("\n")),new TypeReference<List<Map<String, Object>>>() {});
                        }
                        String loaddate = new SimpleDateFormat("yyyy-MM-dd HH:mm:00Z").format(new java.util.Date());
                        entities.parallelStream().forEach(obj -> obj.put("_loaddate", loaddate));
                        LOGGER.info("Collected :  {}", entities.size());
                        if (!entities.isEmpty()) {
                        	ErrorManager.getInstance(dataSource).handleError(indexName, childTypeES, loaddate, errorList,false);
                            ESManager.uploadData(indexName, childTypeES, entities, key.split(","));
                            ESManager.deleteOldDocuments(indexName, childTypeES, "_loaddate.keyword",
                                    loaddate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in populating child tables", e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error in populating child tables");
            errorMap.put(ERROR_TYPE, WARN);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }
        LOGGER.info("Completed EntityAssociationDataCollector for {}",type);
        return errorList;
    }
}
