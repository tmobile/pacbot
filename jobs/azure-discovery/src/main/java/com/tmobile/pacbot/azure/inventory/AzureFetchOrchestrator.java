package com.tmobile.pacbot.azure.inventory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tmobile.pacbot.azure.inventory.file.AssetFileGenerator;
import com.tmobile.pacbot.azure.inventory.file.S3Uploader;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class AzureFetchOrchestrator {
	
	@Autowired
	AssetFileGenerator fileGenerator;
	
	/** The s 3 uploader. */
	@Autowired
	S3Uploader s3Uploader;
	
	
	@Value("${file.path}")
	private String filePath ;
	/** The target types. */
	@Value("${subscriptions:}")
	private String subscriptions;
	
	@Value("${s3}")
	private String s3Bucket ;
	
	@Value("${s3.data}")
	private String s3Data ;
	
	@Value("${s3.processed}")
	private String s3Processed ;
	
	@Value("${s3.region}")
	private String s3Region ;
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(AzureFetchOrchestrator.class);
	
	public Map<String, Object> orchestrate(){
		
		try{
			List<SubscriptionVH> subscriptions = fetchSubscriptions();
			if(subscriptions.isEmpty()){
				ErrorManageUtil.uploadError("all", "all", "all", "Error fetching subscription Info ");
				return ErrorManageUtil.formErrorCode();
			}
			
			log.info("Start : FIle Generation");
			fileGenerator.generateFiles(subscriptions,filePath);
			log.info("End : FIle Generation");
			
			log.info("Start : Backup Current Files");
			s3Uploader.backUpFiles(s3Bucket, s3Region, s3Data, s3Processed+ "/"+ new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
			log.info("End : Backup Current Files");
		
			log.info("Start : Upload Files to S3");
			s3Uploader.uploadFiles(s3Bucket,s3Data,s3Region,filePath);
			log.info("End : Upload Files to S3");
		    
			
			
		}catch(Exception e){

		}
		return null;
	}
	
	private List<SubscriptionVH> fetchSubscriptions() {

		List<SubscriptionVH> subscriptionList  = new ArrayList<>();
		
		if(subscriptions != null && !"".equals(subscriptions)){
			String[] subscriptionsArray = subscriptions.split(",");
			for(String subcritpionInfo : subscriptionsArray){
				SubscriptionVH subscription= new SubscriptionVH();
				String[] subIdName = subcritpionInfo.split("~");
				subscription.setSubscriptionId(subIdName[0].trim());
				subscription.setSubscriptionName(subIdName.length>1?subIdName[1].trim():"");
				subscriptionList.add(subscription);
			}
		}
		return subscriptionList;
	}
}
