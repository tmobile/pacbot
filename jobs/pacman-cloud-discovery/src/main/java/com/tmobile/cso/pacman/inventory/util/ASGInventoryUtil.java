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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsRequest;
import com.amazonaws.services.autoscaling.model.DescribePoliciesRequest;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;

/**
 * The Class ASGInventoryUtil.
 */
public class ASGInventoryUtil {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(InventoryUtil.class);
	
	/** The delimiter. */
	private static String delimiter = FileGenerator.DELIMITER;
	
	/** The asg max record. */
	private static int asgMaxRecord = 100;
	
	/**
	 * Instantiates a new ASG inventory util.
	 */
	private ASGInventoryUtil(){
	}
	
	/**
	 * Fetch launch configurations.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<LaunchConfiguration>> fetchLaunchConfigurations(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		AmazonAutoScaling asgClient;
		Map<String,List<LaunchConfiguration>> launchConfigurationList = new LinkedHashMap<>();
		List<String> launchConfigurationNames = new ArrayList<>();
		
		String expPrefix = "{\"errcode\": \"NO_RES_REG\" ,\"accountId\": \""+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"ASG\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){ 
			try{
				if(!skipRegions.contains(region.getName())){ //!skipRegions
					List<LaunchConfiguration> launchConfigurationListTemp = new ArrayList<>();
					asgClient = AmazonAutoScalingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextToken = null;
					DescribeAutoScalingGroupsResult  describeResult ;
					do{
						describeResult =  asgClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withNextToken(nextToken).withMaxRecords(asgMaxRecord));
						for(AutoScalingGroup _asg : describeResult.getAutoScalingGroups()) {
							launchConfigurationNames.add(_asg.getLaunchConfigurationName());
						}
						nextToken = describeResult.getNextToken();
					}while(nextToken!=null);
					List<String> launchConfigurationNamesTemp = new ArrayList<>();
					
					for(int i =0 ; i<launchConfigurationNames.size();i++){
						launchConfigurationNamesTemp.add(launchConfigurationNames.get(i));
						if((i+1)%50==0 || i == launchConfigurationNames.size()-1){
							launchConfigurationListTemp.addAll(asgClient.describeLaunchConfigurations(new DescribeLaunchConfigurationsRequest().withLaunchConfigurationNames(launchConfigurationNamesTemp)).getLaunchConfigurations());
							launchConfigurationNamesTemp = new ArrayList<>();
						}
						
					}
					
					if(!launchConfigurationListTemp.isEmpty() ){
						log.debug("Account : " + accountId + " Type : ASG Launch Configurations "+region.getName()+" >> " + launchConfigurationListTemp.size());
						launchConfigurationList.put(accountId+delimiter+accountName+delimiter+region.getName(), launchConfigurationListTemp);
					}
			   	}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+"\", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"launchconfig",e.getMessage());
			}
		}
		return launchConfigurationList;
	}
	
/**
 * Fetch scaling policies.
 *
 * @param temporaryCredentials the temporary credentials
 * @param skipRegions the skip regions
 * @param accountId the accountId
 * @return the map
 */
public static Map<String,List<ScalingPolicy>> fetchScalingPolicies(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		AmazonAutoScaling asgClient;
		Map<String,List<ScalingPolicy>> scalingPolicyList = new LinkedHashMap<>();
		
		String expPrefix = "{\"errcode\": \"NO_RES_REG\" ,\"accountId\": \""+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"ASG\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){ 
			try{
				if(!skipRegions.contains(region.getName())){ //!skipRegions
					List<ScalingPolicy> _scalingPolicyList = new ArrayList<>();
					asgClient = AmazonAutoScalingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextToken = null;
					DescribeAutoScalingGroupsResult  describeResult ;
					do{
						describeResult =  asgClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withNextToken(nextToken).withMaxRecords(asgMaxRecord));
						for(AutoScalingGroup _asg : describeResult.getAutoScalingGroups()) {
							_scalingPolicyList.addAll(asgClient.describePolicies(new DescribePoliciesRequest().withAutoScalingGroupName(_asg.getAutoScalingGroupName())).getScalingPolicies());
						}
						nextToken = describeResult.getNextToken();
					}while(nextToken!=null);
					
					if(!_scalingPolicyList.isEmpty() ){
						log.debug("Account : " + accountId + " Type : ASG Scaling Policy "+region.getName()+" >> " + _scalingPolicyList.size());
						scalingPolicyList.put(accountId+delimiter+accountName+delimiter+region.getName(), _scalingPolicyList);
					}
			   	}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+"\", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"asgpolicy",e.getMessage());
			}
		}
		return scalingPolicyList;
	}
}
