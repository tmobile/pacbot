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
/**
 * Utility functions for ASGC Rules
 */
package com.tmobile.pacman.commons.utils;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeFlowLogsRequest;
import com.amazonaws.services.ec2.model.DescribeFlowLogsResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.FlowLog;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;

// TODO: Auto-generated Javadoc
/**
 * The Class Ec2Utils.
 */
public class Ec2Utils {

	
	 /**
 	 * Gets the instances.
 	 *
 	 * @param ec2ServiceClient the ec 2 service client
 	 * @param request the request
 	 * @return the instances
 	 */
	public static List<Instance> getInstances(AmazonEC2 ec2ServiceClient,DescribeInstancesRequest request) {
		
		List<Reservation> reservations = new ArrayList<Reservation>();
		String nextToken;
		if(request==null) request=new DescribeInstancesRequest();
		do{
		    DescribeInstancesResult result = ec2ServiceClient.describeInstances(request);
		    nextToken = result.getNextToken();
		    reservations.addAll(result.getReservations());
		    request.setNextToken(nextToken);
		}while(null!=nextToken);
		
		List<Instance> instances = getAllInstancesFromReservations(reservations);
		return instances;
	}
	
	/**
	 * Collect all volumes.
	 *
	 * @param ec2ServiceClient the ec 2 service client
	 * @param region the region
	 * @return the list
	 */
	public static List<Volume> collectAllVolumes(AmazonEC2 ec2ServiceClient,Region region){
		DescribeVolumesRequest request = new DescribeVolumesRequest();
		DescribeVolumesResult result;
		String nextToken;
		List<Volume> volumes=new ArrayList<Volume>();
		do{
			result = ec2ServiceClient.describeVolumes(request);
			volumes.addAll(result.getVolumes());
			nextToken = result.getNextToken();
		    request.setNextToken(nextToken);
		}while(null!=nextToken);
		return volumes;
	}
	
	
	 /**
 	 * return all the instances in list of reservations.
 	 *
 	 * @param reservations the reservations
 	 * @return the all instances from reservations
 	 */
    private static List<Instance> getAllInstancesFromReservations(
			List<Reservation> reservations) {
    	List<Instance>  instances = new ArrayList<Instance>();
    	for(Reservation reservation : reservations){
    		instances.addAll(reservation.getInstances());
    	}
		return instances;
	}
    
    /**
     * Gets the vpcs for region.
     *
     * @param ec2ServiceClient the ec 2 service client
     * @param region the region
     * @param request the request
     * @return the vpcs for region
     */
	public static List<Vpc> getVpcsForRegion(AmazonEC2 ec2ServiceClient,
			Region region, DescribeVpcsRequest request) {
		ec2ServiceClient.setRegion(region);
		DescribeVpcsResult describeVpcsResult =  ec2ServiceClient.describeVpcs(request);
		return  describeVpcsResult.getVpcs();
		
	}
	
	/**
	 * Gets the flow logs.
	 *
	 * @param ec2ServiceClient the ec 2 service client
	 * @param region the region
	 * @param describeFlowLogsRequest the describe flow logs request
	 * @return the flow logs
	 */
	public static List<FlowLog> getFlowLogs(AmazonEC2 ec2ServiceClient,
			Region region, DescribeFlowLogsRequest describeFlowLogsRequest) {
		List<FlowLog> flowLogs = new ArrayList<FlowLog>();
		DescribeFlowLogsResult flowLogsResult;
		String nextToken;
		do{
			try{
					flowLogsResult  = ec2ServiceClient.describeFlowLogs(describeFlowLogsRequest);
			}catch(AmazonServiceException exception){
				flowLogsResult=null;
				nextToken=null;
				continue;
			}
			flowLogs.addAll(flowLogsResult.getFlowLogs());
			nextToken = flowLogsResult.getNextToken();
			describeFlowLogsRequest.setNextToken(nextToken);
			
		}while(null!=nextToken);
		
		return flowLogs;
	}

	/**
	 * Gets the subnets for region.
	 *
	 * @param ec2ServiceClient the ec 2 service client
	 * @param region the region
	 * @param describeSubnetsRequest the describe subnets request
	 * @return the subnets for region
	 */
	public static List<Subnet> getSubnetsForRegion(AmazonEC2 ec2ServiceClient,
			Region region, DescribeSubnetsRequest describeSubnetsRequest) {
		ec2ServiceClient.setRegion(region);
		DescribeSubnetsResult describeSubnetsResult = ec2ServiceClient.describeSubnets(describeSubnetsRequest);
		return describeSubnetsResult.getSubnets();
	}
	
	/**
	 * Gets the all elbs desc.
	 *
	 * @param elbClient the elb client
	 * @param region the region
	 * @param balancersRequest the balancers request
	 * @return the all elbs desc
	 */
	public static List<LoadBalancerDescription> getAllElbsDesc(AmazonElasticLoadBalancing elbClient,
			Region region, DescribeLoadBalancersRequest balancersRequest) {

		List<LoadBalancerDescription> loadDescriptionList = new ArrayList<LoadBalancerDescription>();
		DescribeLoadBalancersResult balancersResult;
		String marker;
		do {
			balancersResult = elbClient.describeLoadBalancers(balancersRequest);
			
			marker = balancersResult.getNextMarker();
			balancersRequest.setMarker(marker);
			loadDescriptionList.addAll(balancersResult.getLoadBalancerDescriptions());

		} while (null != marker);
		
		return loadDescriptionList;
	}	
	
	
}
