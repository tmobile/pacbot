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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.directconnect.AmazonDirectConnectClient;
import com.amazonaws.services.directconnect.AmazonDirectConnectClientBuilder;
import com.amazonaws.services.directconnect.model.Connection;
import com.amazonaws.services.directconnect.model.VirtualInterface;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;

/**
 * The Class DirectConnectionInventoryUtil.
 */
public class DirectConnectionInventoryUtil {

	/**
	 * Instantiates a new direct connection inventory util.
	 */
	private DirectConnectionInventoryUtil() {
		
	}
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(DirectConnectionInventoryUtil.class);
	
	/** The delimiter. */
	private static String delimiter = FileGenerator.DELIMITER;
	
	/**
	 * Fetch direct connections.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<Connection>> fetchDirectConnections(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName) {
		
		Map<String,List<Connection>> connectionMap = new LinkedHashMap<>();
		String expPrefix = "{\"errcode\": \"NO_RES_REG\" ,\"accountId\": \""+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Direct Connections\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonDirectConnectClient directConnectClient = (AmazonDirectConnectClient) AmazonDirectConnectClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<Connection> connectionList = directConnectClient.describeConnections().getConnections();
					
					if(!connectionList.isEmpty() ) {
						log.debug("Account : " + accountId + " Type : Direct Connections "+ region.getName()+" >> " + connectionList.size());
						connectionMap.put(accountId+delimiter+accountName+delimiter+region.getName(), connectionList);
					}
			   	}
				
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+"\", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"directconnect",e.getMessage());
		   	}
		}
		return connectionMap;
	}
	
	/**
	 * Fetch direct connections virtual interfaces.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<VirtualInterface>> fetchDirectConnectionsVirtualInterfaces(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName) {
		
		Map<String,List<VirtualInterface>> virtualInterfacesMap = new LinkedHashMap<>();
		String expPrefix = "{\"errcode\": \"NO_RES_REG\" ,\"accountId\": \""+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Direct Connections\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonDirectConnectClient directConnectClient = (AmazonDirectConnectClient) AmazonDirectConnectClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<VirtualInterface> virtualInterfacesList = directConnectClient.describeVirtualInterfaces().getVirtualInterfaces();
					if(!virtualInterfacesList.isEmpty() ) {
						log.debug("Account : " + accountId + " Type : Direct Connections "+ region.getName()+" >> " + virtualInterfacesList.size());
						virtualInterfacesMap.put(accountId+delimiter+accountName+delimiter+region.getName(), virtualInterfacesList);
					}
			   	}
				
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+"\", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"virtualinterface",e.getMessage());
		   	}
		}
		return virtualInterfacesMap;
	}
}
