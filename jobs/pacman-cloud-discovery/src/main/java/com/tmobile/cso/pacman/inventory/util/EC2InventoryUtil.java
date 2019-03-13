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
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.CustomerGateway;
import com.amazonaws.services.ec2.model.DescribeEgressOnlyInternetGatewaysRequest;
import com.amazonaws.services.ec2.model.DhcpOptions;
import com.amazonaws.services.ec2.model.EgressOnlyInternetGateway;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.ReservedInstances;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.VpcPeeringConnection;
import com.amazonaws.services.ec2.model.VpnConnection;
import com.amazonaws.services.ec2.model.VpnGateway;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeInstanceInformationRequest;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeInstanceInformationResult;
import com.amazonaws.services.simplesystemsmanagement.model.InstanceInformation;
import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;

/**
 * The Class EC2InventoryUtil.
 */
public class EC2InventoryUtil {
	
	/**
	 * Instantiates a new EC 2 inventory util.
	 */
	private EC2InventoryUtil(){
	}
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(EC2InventoryUtil.class);
	
	/** The delimiter. */
	private static String delimiter = FileGenerator.DELIMITER;
	
	/**
	 * Fetch route tables.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<RouteTable>> fetchRouteTables(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<RouteTable>> routeTableMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + InventoryConstants.ERROR_PREFIX_EC2 ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<RouteTable> routeTableList = ec2Client.describeRouteTables().getRouteTables();
					
					if(!routeTableList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Route table "+ region.getName()+" >> " + routeTableList.size());
						routeTableMap.put(accountId+delimiter+accountName+delimiter+region.getName(), routeTableList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"routetable",e.getMessage());
		   	}
		}
		return routeTableMap;
	}
	
	/**
	 * Fetch network ACL.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<NetworkAcl>> fetchNetworkACL(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<NetworkAcl>> networkAclMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + InventoryConstants.ERROR_PREFIX_EC2 ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<NetworkAcl> networkAclList = ec2Client.describeNetworkAcls().getNetworkAcls();
					
					if(!networkAclList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Network Acl "+ region.getName()+" >> " + networkAclList.size());
						networkAclMap.put(accountId+delimiter+accountName+delimiter+region.getName(), networkAclList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"networkacl",e.getMessage());
		   	}
		}
		return networkAclMap;
	}
	
	/**
	 * Fetch elastic IP addresses.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<Address>> fetchElasticIPAddresses(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<Address>> elasticIPMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + InventoryConstants.ERROR_PREFIX_EC2 ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<Address> elasticIPList = ec2Client.describeAddresses().getAddresses();
					
					if(!elasticIPList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Elastic IP "+ region.getName()+" >> " + elasticIPList.size());
						elasticIPMap.put(accountId+delimiter+accountName+delimiter+region.getName(), elasticIPList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"elasticip",e.getMessage());
		   	}
		}
		return elasticIPMap;
	}
	
	/**
	 * Fetch internet gateway.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<InternetGateway>> fetchInternetGateway(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<InternetGateway>> internetGatewayMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"internetgateway\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<InternetGateway> internetGatewayList = ec2Client.describeInternetGateways().getInternetGateways();
					
					if(!internetGatewayList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Internet Gateway "+ region.getName()+" >> " + internetGatewayList.size());
						internetGatewayMap.put(accountId+delimiter+accountName+delimiter+region.getName(), internetGatewayList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"internetgateway",e.getMessage());
		   	}
		}
		return internetGatewayMap;
	}
	
	/**
	 * Fetch VPN gateway.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<VpnGateway>> fetchVPNGateway(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<VpnGateway>> vpnGatewayMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"vpngateway\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<VpnGateway> vpnGatewayList = ec2Client.describeVpnGateways().getVpnGateways();
					
					if(!vpnGatewayList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 VPN Gateway "+ region.getName()+" >> " + vpnGatewayList.size());
						vpnGatewayMap.put(accountId+delimiter+accountName+delimiter+region.getName(), vpnGatewayList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"vpngateway",e.getMessage());
		   	}
		}
		return vpnGatewayMap;
	}
	
	/**
	 * Fetch egress gateway.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<EgressOnlyInternetGateway>> fetchEgressGateway(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<EgressOnlyInternetGateway>> egressGatewayMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"egressgateway\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<EgressOnlyInternetGateway> egressGatewayList = ec2Client.describeEgressOnlyInternetGateways(new DescribeEgressOnlyInternetGatewaysRequest()).getEgressOnlyInternetGateways();
					
					if(!egressGatewayList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Egress Gateway "+ region.getName()+" >> " + egressGatewayList.size());
						egressGatewayMap.put(accountId+delimiter+accountName+delimiter+region.getName(), egressGatewayList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"egressgateway",e.getMessage());
		   	}
		}
		return egressGatewayMap;
	}
	
	/**
	 * Fetch DHCP options.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<DhcpOptions>> fetchDHCPOptions(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<DhcpOptions>> dhcpOptionsMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"dhcpoption\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<DhcpOptions> dhcpOptionsList = ec2Client.describeDhcpOptions().getDhcpOptions();
					
					if(!dhcpOptionsList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 DHCP Options "+ region.getName()+" >> " + dhcpOptionsList.size());
						dhcpOptionsMap.put(accountId+delimiter+accountName+delimiter+region.getName(), dhcpOptionsList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"dhcpoption",e.getMessage());
		   	}
		}
		return dhcpOptionsMap;
	}
	
	/**
	 * Fetch peering connections.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<VpcPeeringConnection>> fetchPeeringConnections(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<VpcPeeringConnection>> peeringConnectionMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"peeringconnection\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<VpcPeeringConnection> peeringConnectionList = ec2Client.describeVpcPeeringConnections().getVpcPeeringConnections();
					
					if(!peeringConnectionList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Peering Connections "+ region.getName()+" >> " + peeringConnectionList.size());
						peeringConnectionMap.put(accountId+delimiter+accountName+delimiter+region.getName(), peeringConnectionList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"peeringconnection",e.getMessage());
		   	}
		}
		return peeringConnectionMap;
	}
	
	/**
	 * Fetch customer gateway.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<CustomerGateway>> fetchCustomerGateway(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<CustomerGateway>> customerGatewayMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"customergateway\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<CustomerGateway> customerGatewayList = ec2Client.describeCustomerGateways().getCustomerGateways();
					
					if(!customerGatewayList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 Customer Gateway "+ region.getName()+" >> " + customerGatewayList.size());
						customerGatewayMap.put(accountId+delimiter+accountName+delimiter+region.getName(), customerGatewayList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"customergateway",e.getMessage());
		   	}
		}
		return customerGatewayMap;
	}
	
	/**
	 * Fetch VPN connections.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<VpnConnection>> fetchVPNConnections(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<VpnConnection>> vpnConnectionMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"vpnconnection\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<VpnConnection> vpnConnectionsList = ec2Client.describeVpnConnections().getVpnConnections();
					if(!vpnConnectionsList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : EC2 VPN Connections"+ region.getName()+" >> " + vpnConnectionsList.size());
						vpnConnectionMap.put(accountId+delimiter+accountName+delimiter+region.getName(), vpnConnectionsList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"vpnconnection",e.getMessage());
		   	}
		}
		return vpnConnectionMap;
	}
	
	/**
	 * Fetch reserved instances.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<ReservedInstances>> fetchReservedInstances(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<ReservedInstances>> reservedInstancesMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + InventoryConstants.ERROR_PREFIX_EC2 ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<ReservedInstances> reservedInstancesList = ec2Client.describeReservedInstances().getReservedInstances();
					if(!reservedInstancesList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : reservedinstance"+ region.getName()+" >> " + reservedInstancesList.size());
						reservedInstancesMap.put(accountId+delimiter+accountName+delimiter+region.getName(), reservedInstancesList);
					}
			   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
		   		ErrorManageUtil.uploadError(accountId,region.getName(),"reservedinstance",e.getMessage());
		   	}
		}
		return reservedInstancesMap;
	}
	
	/**
	 * Fetch SSM info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<InstanceInformation>> fetchSSMInfo(BasicSessionCredentials temporaryCredentials, String skipRegions, String accountId,String accountName) {

		Map<String,List<InstanceInformation>> ssmInstanceList = new LinkedHashMap<>();

		AWSSimpleSystemsManagement ssmClient;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE + accountId
				+ "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"SSM\" , \"region\":\"";

		List<InstanceInformation> ssmInstanceListTemp ;
		
		for (Region region : RegionUtils.getRegions()) {
			try {
				if (!skipRegions.contains(region.getName())) {
					ssmInstanceListTemp = new ArrayList<>();
					ssmClient = AWSSimpleSystemsManagementClientBuilder.standard()
							.withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials))
							.withRegion(region.getName()).build();
					String nextToken = null;
					DescribeInstanceInformationResult describeInstanceInfoRslt;
					do {
						describeInstanceInfoRslt = ssmClient.describeInstanceInformation(
								new DescribeInstanceInformationRequest().withNextToken(nextToken));
						nextToken = describeInstanceInfoRslt.getNextToken();
						ssmInstanceListTemp.addAll(describeInstanceInfoRslt
								.getInstanceInformationList());
					} while (nextToken != null);
					if(! ssmInstanceListTemp.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + accountId + " Type : SSM "+region.getName() + " >> "+ssmInstanceListTemp.size());
						ssmInstanceList.put(accountId+delimiter+accountName+delimiter+region.getName(), ssmInstanceListTemp);
					}
				}

			} catch (Exception e) {
				log.warn(expPrefix + region.getName() + InventoryConstants.ERROR_CAUSE + e.getMessage() + "\"}");
				ErrorManageUtil.uploadError(accountId, region.getName(), "SSM", e.getMessage());
			}
		}
		return ssmInstanceList;
	}
}
