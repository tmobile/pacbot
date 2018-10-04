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
package com.tmobile.cso.pacman.inventory.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ListSubscriptionsRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.Subscription;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;

/**
 * The Class SNSInventoryUtil.
 */
public class SNSInventoryUtil {

	/**
	 * Instantiates a new SNS inventory util.
	 */
	private SNSInventoryUtil() {
	}
	
	/** The log. */
	private static Logger log = LogManager.getLogger(SNSInventoryUtil.class);
	
	/** The delimiter. */
	private static String delimiter = FileGenerator.DELIMITER;
	
	/**
	 * Fetch SNS topics.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Subscription>> fetchSNSTopics(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		
		Map<String,List<Subscription>> subscriptionMap = new LinkedHashMap<>();
		AmazonSNSClient snsClient ;
		String expPrefix = "{\"errcode\": \"NO_RES_REG\" ,\"account\": \""+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"snstopic\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					List<Subscription> subscriptionList = new ArrayList<>();
					snsClient = (AmazonSNSClient) AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextToken = null;
					ListSubscriptionsResult listSubscriptionDefinitionsResult;
					do {
						listSubscriptionDefinitionsResult = snsClient.listSubscriptions(new ListSubscriptionsRequest().withNextToken(nextToken));
						subscriptionList.addAll(listSubscriptionDefinitionsResult.getSubscriptions());
						nextToken = listSubscriptionDefinitionsResult.getNextToken();
					}while(nextToken!=null);
					
					if(!subscriptionList.isEmpty() ) {
						log.debug("Account : " + account + " Type : SNS Topics "+ region.getName()+" >> " + subscriptionList.size());
						subscriptionMap.put(account+delimiter+region.getName(), subscriptionList);
					}
			   	}
				
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+"\", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"snstopic",e.getMessage());
		   	}
		}
		return subscriptionMap;
	}
}
