package com.tmobile.cso.pacman.datashipper.entity;

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
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;

public class AWSErrorManager implements Constants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSErrorManager.class);
	private String s3Account = System.getProperty("base.account");
	private String s3Region = System.getProperty("base.region");
	private String s3Role =  System.getProperty("s3.role");
	private String bucketName =  System.getProperty("s3");
	private String dataPath =  System.getProperty("s3.data");
	
	private Map<String,List<Map<String,String>>> errorInfo ;
	
	private static AWSErrorManager errorManager ;
	
	private AWSErrorManager(){
	}
	public static AWSErrorManager getInstance(){
		if(errorManager==null){
			errorManager = new AWSErrorManager(); 
		}
		return errorManager;
	}
	
	private void fetchErrorInfo(String datasource, List<Map<String,String>> errorList){
		if(errorInfo==null){
			ObjectMapper objectMapper = new ObjectMapper();
	    	List<Map<String, String>> inventoryErrors = new ArrayList<>();
	    	AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                .withCredentials(new AWSStaticCredentialsProvider(new CredentialProvider().getCredentials(s3Account,s3Role))).withRegion(s3Region).build();
	    	try {
		    	S3Object inventoryErrorData = s3Client.getObject(new GetObjectRequest(bucketName,dataPath+"/"+datasource+"-loaderror.data"));
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
	
	public Map<String,List<Map<String,String>>> getErrorInfo(String datasource, List<Map<String,String>> errorList){
		if(errorInfo==null){
			fetchErrorInfo( datasource, errorList);
		}
		
		return errorInfo;
		
	}
	public void handleError(String dataSource,String index, String type, String loaddate,List<Map<String,String>> errorList,boolean checkLatest) {
		fetchErrorInfo(dataSource,errorList);
		String parentType = index.replace(dataSource+"_", "");
		if(errorInfo.containsKey(parentType) || errorInfo.containsKey("all")) {
			List<Map<String,String>> errorByType = errorInfo.get(parentType);
			if(errorByType==null){
				errorByType = errorInfo.get("all");
			}
			errorByType.forEach(errorData -> 
	    		ESManager.updateLoadDate(index, type, errorData.get("accountid"), errorData.get("region"), loaddate,checkLatest)
	    	);
	     }
	}

}
