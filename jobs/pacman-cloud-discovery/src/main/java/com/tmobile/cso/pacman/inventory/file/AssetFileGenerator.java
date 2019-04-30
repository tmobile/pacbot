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
package com.tmobile.cso.pacman.inventory.file;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicSessionCredentials;
import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.auth.CredentialProvider;
import com.tmobile.cso.pacman.inventory.util.ASGInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.DirectConnectionInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.EC2InventoryUtil;
import com.tmobile.cso.pacman.inventory.util.ESInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.ElastiCacheUtil;
import com.tmobile.cso.pacman.inventory.util.InventoryUtil;
import com.tmobile.cso.pacman.inventory.util.KinesisInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.SNSInventoryUtil;


/**
 * The Class AssetFileGenerator.
 */
@Component
public class AssetFileGenerator {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(AssetFileGenerator.class);

	/** The cred provider. */
	@Autowired
	CredentialProvider credProvider;

	/** The target types. */
	@Value("${target-types:}")
	private String targetTypes;

	/** The target types. */
	@Value("${discovery.role}")
	private  String roleName;

	/** The target types. */
	@Value("${ec2.statenames:running,stopped,stopping}")
	private  String ec2StatenameFilters;


	/**
	 * Generate files.
	 *
	 * @param accounts the accounts
	 * @param skipRegions the skip regions
	 * @param filePath the file path
	 */
	public void generateFiles(List<Map<String,String>> accounts,String skipRegions,String filePath){

		try {
			FileManager.initialise(filePath);
			ErrorManageUtil.initialise();
		} catch (IOException e1) {
			log.error("Error initialising File ",e1);
		}
		Iterator<Map<String, String>> it = accounts.iterator();

		while(it.hasNext()){
			Map<String, String> account = it.next();
			String accountId = account.get(InventoryConstants.ACCOUNT_ID);
			String accountName = account.get(InventoryConstants.ACCOUNT_NAME);

			log.info("Started Discovery for account {}", accountId);
			BasicSessionCredentials tempCredentials = null;
			try{
				tempCredentials = credProvider.getCredentials(accountId,roleName);
			}catch(Exception e){
				log.error("{\"errcode\":\"NO_CRED\" , \"account\":\""+accountId +"\", \"Message\":\"Error getting credentials for account "+accountId +"\" , \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId, "all", "all", e.getMessage());
				continue;
			}
			final BasicSessionCredentials temporaryCredentials = tempCredentials;
			String expPrefix = "{\"errcode\": \"NO_RES\" ,\"account\": \""+accountId + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"" ;
			String infoPrefix = "Fetching for Account : "+accountId + " Type : ";

			ExecutorService executor = Executors.newCachedThreadPool();

			executor.execute(() ->
			{
			    if(!(isTypeInScope("ec2"))) {
			        return;
			    }
				try{
					log.info(infoPrefix + "EC2");
					FileManager.generateInstanceFiles(InventoryUtil.fetchInstances(temporaryCredentials,skipRegions,accountId,accountName,ec2StatenameFilters));
				}catch(Exception e){
					log.error(expPrefix+ "EC2\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "ec2", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("asg"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "ASG");
					FileManager.generateAsgFiles(InventoryUtil.fetchAsg(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "ASG\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "asg", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("stack"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "Cloud Formation Stack");
					FileManager.generateCloudFormationStackFiles(InventoryUtil.fetchCloudFormationStack(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Stack\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "stack", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("dynamodb"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "DynamoDB");
					FileManager.generateDynamoDbFiles(InventoryUtil.fetchDynamoDBTables(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "DynamoDB\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "dynamodb", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("efs"))) {
			        return;
			    }
				try{
					log.info(infoPrefix + "EFS");
					FileManager.generateEfsFiles(InventoryUtil.fetchEFSInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EFS\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "efs", e.getMessage());
				}
			});


			executor.execute(() ->
			{
			    if(!(isTypeInScope("emr"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "EMR");
					FileManager.generateEmrFiles(InventoryUtil.fetchEMRInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EMR\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "emr", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("lambda"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "Lambda");
					FileManager.generateLamdaFiles(InventoryUtil.fetchLambdaInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Lambda\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "lambda", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("classicelb"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "Classic ELB");
					FileManager.generateClassicElbFiles( InventoryUtil.fetchClassicElbInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Classic ELB\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "classicelb", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("appelb"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "Application ELB");
					FileManager.generateApplicationElbFiles(InventoryUtil.fetchElbInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Application ELB\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "appelb", e.getMessage());
				}
			});


			executor.execute(() ->
			{
			    if(!(isTypeInScope("targetgroup"))) {
                    return;
                }
				try{
					log.info(infoPrefix + "Target Group");
					FileManager.generateTargetGroupFiles(InventoryUtil.fetchTargetGroups(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Target Group\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "targergroup", e.getMessage());
				}
			});



			executor.execute(() ->
			{
			    if(!(isTypeInScope("nat"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Nat Gateway");
					FileManager.generateNatGatewayFiles(InventoryUtil.fetchNATGatewayInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Nat Gateway\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "nat", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("rdsdb"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "RDS Instance");
					FileManager.generateRDSInstanceFiles(InventoryUtil.fetchRDSInstanceInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "RDS Instance\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "rdsdb", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("rdscluster"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "RDS Cluster");
					FileManager.generateRDSClusterFiles(InventoryUtil.fetchRDSClusterInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "RDS Cluster\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "rdscluster", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("s3"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "S3");
					FileManager.generateS3Files(InventoryUtil.fetchS3Info(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "S3\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "s3", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("eni"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Network Interface");
					FileManager.generateNwInterfaceFiles(InventoryUtil.fetchNetworkIntefaces(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Network Interface\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "eni", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("sg"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Security Group");
					FileManager.generateSecGroupFile(InventoryUtil.fetchSecurityGroups(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Security Group\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "sg", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("subnet"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Subnet");
					FileManager.generateSubnetFiles(InventoryUtil.fetchSubnets(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Subnet\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "subnet", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("checks"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Trusted Advisor Check");
					FileManager.generateTrustedAdvisorFiles(InventoryUtil.fetchTrusterdAdvisorsChecks(temporaryCredentials,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Trusted Advisor Check\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "checks", e.getMessage());
				}
			});


			executor.execute(() ->
			{
			    if(!(isTypeInScope("redshift"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Redshift");
					FileManager.generateRedshiftFiles(InventoryUtil.fetchRedshiftInfo(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Redshift\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "redshift", e.getMessage());		}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("volume"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Volume");
					FileManager.generatefetchVolumeFiles(InventoryUtil.fetchVolumetInfo(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Volume\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "volume", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("snapshot"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Snapshot");
					FileManager.generateSnapshotFiles(InventoryUtil.fetchSnapshots(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Snapshot\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "snapshot", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("vpc"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "VPC");
					FileManager.generateVpcFiles(InventoryUtil.fetchVpcInfo(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "VPC\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "vpc", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("api"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "ApiGateway");
					 FileManager.generateApiGatewayFiles(InventoryUtil.fetchApiGateways(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "API\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "api", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("iamuser"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "IAM User");
					FileManager.generateIamUserFiles(InventoryUtil.fetchIAMUsers(temporaryCredentials,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "iAM muser\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "iamuser", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("rdssnapshot"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "RDS Snapshot");
					FileManager.generateRDSSnapshotFiles(InventoryUtil.fetchRDSDBSnapshots(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "RDS Snapshot\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "rdssnapshot", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("iamrole"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "IAM Roles");
					FileManager.generateIamRoleFiles(InventoryUtil.fetchIAMRoles(temporaryCredentials,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "IAM Roles\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "iamrole", e.getMessage());
				}
			});


			executor.execute(() ->
			{
			    if(!(isTypeInScope("kms"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "KMS");
					FileManager.generateKMSFiles(InventoryUtil.fetchKMSKeys(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "KMS\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "kms", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("cloudfront"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "CloudFront");
					FileManager.generateCloudFrontFiles(InventoryUtil.fetchCloudFrontInfo(temporaryCredentials,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "CloudFront\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "cloudfront", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("beanstalk"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "beanstalk");
					FileManager.generateEBSFiles(InventoryUtil.fetchEBSInfo(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "beanstalk\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "beanstalk", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("phd"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "PHD");
					FileManager.generatePHDFiles(InventoryUtil.fetchPHDInfo(temporaryCredentials,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "PHD\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "phd", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("routetable"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "EC2 Route table");
					FileManager.generateEC2RouteTableFiles(EC2InventoryUtil.fetchRouteTables(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EC2 Route table\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "routetable", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("networkacl"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "EC2 Network Acl");
					FileManager.generateNetworkAclFiles(EC2InventoryUtil.fetchNetworkACL(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EC2 Network Acl\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "networkacl", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("elasticip"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "EC2 Elastic IP");
					FileManager.generateElasticIPFiles(EC2InventoryUtil.fetchElasticIPAddresses(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EC2 Elastic IP\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "elasticip", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("launchconfig"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "ASG Launch Configurations");
					FileManager.generateLaunchConfigurationsFiles(ASGInventoryUtil.fetchLaunchConfigurations(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "ASG Launch Configurations\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "launchconfig", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("internetgw"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "EC2 Internet Gateway");
					FileManager.generateInternetGatewayFiles(EC2InventoryUtil.fetchInternetGateway(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EC2 Internet Gateway\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "internetgw", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("vpngw"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "EC2 Vpn Gateway");
					FileManager.generateVPNGatewayFiles(EC2InventoryUtil.fetchVPNGateway(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "EC2 Vpn Gateway\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "vpngw", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("asgpolicy"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "ASG Scaling Policy");
					FileManager.generateScalingPolicies(ASGInventoryUtil.fetchScalingPolicies(temporaryCredentials,skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "ASG Scaling Policy\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "asgpolicy", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("snstopic"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "SNS Topics");
					FileManager.generateSNSTopics(SNSInventoryUtil.fetchSNSTopics(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "SNS Topics\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "snstopic", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("egressgateway"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Egress Gateway");
					FileManager.generateEgressGateway(EC2InventoryUtil.fetchEgressGateway(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Egress Gateway\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "egressgateway", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("dhcpoption"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Dhcp Options");
					FileManager.generateDhcpOptions(EC2InventoryUtil.fetchDHCPOptions(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Dhcp Options\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "dhcpoption", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("peeringconnection"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Peering Connections");
					FileManager.generatePeeringConnections(EC2InventoryUtil.fetchPeeringConnections(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Peering Connections\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "peeringconnection", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("customergateway"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Customer Gateway");
					FileManager.generateCustomerGateway(EC2InventoryUtil.fetchCustomerGateway(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Customer Gateway\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "customergateway", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("vpnconnection"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "VPN Connection");
					FileManager.generateVpnConnection(EC2InventoryUtil.fetchVPNConnections(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "VPN Connection\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "vpnconnection", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("directconnect"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Direct Connection");
					FileManager.generateDirectConnection(DirectConnectionInventoryUtil.fetchDirectConnections(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Direct Connection\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "directconnect", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("virtualinterface"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "Direct Connection Virtual Interfaces");
					FileManager.generateDirectConnectionVirtualInterfaces(DirectConnectionInventoryUtil.fetchDirectConnectionsVirtualInterfaces(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Direct Connection Virtual Interfaces\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "virtualinterface", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("elasticsearch"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "ES Domain");
					FileManager.generateESDomain(ESInventoryUtil.fetchESInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "ES Domain\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "elasticsearch", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("reservedinstance"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "reservedinstance");
					FileManager.generateReservedInstances(EC2InventoryUtil.fetchReservedInstances(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "reservedinstance\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "reservedinstance", e.getMessage());
				}
			});


			executor.execute(() ->
			{
			    if(!(isTypeInScope("ssm"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "ssm");
					FileManager.generateSsmFiles(EC2InventoryUtil.fetchSSMInfo(temporaryCredentials, skipRegions,accountId,accountName));
				}catch(Exception e){
					log.error(expPrefix+ "SSM\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "ssm", e.getMessage());
				}
			});

			executor.execute(() ->
            {
                if(!(isTypeInScope("elasticache"))) {
                    return;
                }

                try{
                    log.info(infoPrefix + "elasticache");
                    FileManager.generateElastiCacheFiles(ElastiCacheUtil.fetchElastiCacheInfo(temporaryCredentials, skipRegions,accountId,accountName));
                }catch(Exception e){
                    log.error(expPrefix+ "elasticache\", \"cause\":\"" +e.getMessage()+"\"}");
                    ErrorManageUtil.uploadError(accountId, "", "elasticache", e.getMessage());
                }
            });

			executor.execute(() ->
            {
                if(!(isTypeInScope("datastream"))) {
                    return;
                }

                try{
                    log.info(infoPrefix + "datastream");
                    FileManager.generateKinesisDataStreamFiles(KinesisInventoryUtil.fetchDataStreamInfo(temporaryCredentials,skipRegions,accountId,accountName));
                }catch(Exception e){
                    log.error(expPrefix+ "datastream\", \"cause\":\"" +e.getMessage()+"\"}");
                    ErrorManageUtil.uploadError(accountId, "", "datastream", e.getMessage());
                }
            });

			executor.execute(() ->
            {
                if(!(isTypeInScope("sqs"))) {
                    return;
                }

                try{
                    log.info(infoPrefix + "sqs");
                    FileManager.generateSQSFiles(InventoryUtil.fetchSQSInfo(temporaryCredentials,skipRegions,accountId,accountName));
                }catch(Exception e){
                    log.error(expPrefix+ "sqs\", \"cause\":\"" +e.getMessage()+"\"}");
                    ErrorManageUtil.uploadError(accountId, "", "sqs", e.getMessage());
                }
            });

			executor.execute(() ->
            {
                if(!(isTypeInScope("deliverystream"))) {
                    return;
                }

                try{
                    log.info(infoPrefix + "deliverystream");
                    FileManager.generateKinesisDeliveryStreamFiles(KinesisInventoryUtil.fetchDeliveryStreamInfo(temporaryCredentials,skipRegions,accountId,accountName));
                }catch(Exception e){
                    log.error(expPrefix+ "deliverystream\", \"cause\":\"" +e.getMessage()+"\"}");
                    ErrorManageUtil.uploadError(accountId, "", "deliverystream", e.getMessage());
                }
            });

			executor.execute(() ->
            {
                if(!(isTypeInScope("videostream"))) {
                    return;
                }

                try{
                    log.info(infoPrefix + "videostream");
                    FileManager.generateKinesisVideoStreamFiles(KinesisInventoryUtil.fetchVideoStreamInfo(temporaryCredentials,skipRegions,accountId,accountName));
                }catch(Exception e){
                    log.error(expPrefix+ "videostream\", \"cause\":\"" +e.getMessage()+"\"}");
                    ErrorManageUtil.uploadError(accountId, "", "videostream", e.getMessage());
                }
            });
			//****** Changes For Federated Rules Start ******
			executor.execute(() ->
			{
			    if(!(isTypeInScope("acmcertificate"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "acmcertificate");
					FileManager.generateACMCertificateFiles(InventoryUtil.fetchACMCertficateInfo(temporaryCredentials, skipRegions, accountId, accountName));
				}catch(Exception e){
					log.error(expPrefix+ "acmcertificate\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "acmcertificate", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("iamcertificate"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "iamcertificate");
					FileManager.generateIAMCertificateFiles(InventoryUtil.fetchIAMCertificateInfo(temporaryCredentials, skipRegions, accountId, accountName));
				}catch(Exception e){
					log.error(expPrefix+ "iamcertificate\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "iamcertificate", e.getMessage());
				}
			});

			executor.execute(() ->
            {
                if(!(isTypeInScope("account"))) {
                   return;
               }

                try{
                    log.info(infoPrefix + "Account");
                    FileManager.generateAccountFiles(InventoryUtil.fetchAccountsInfo(temporaryCredentials, skipRegions, accountId, accountName));
                }catch(Exception e){
                    log.error(expPrefix+ "AccountInfo\", \"cause\":\"" +e.getMessage()+"\"}");
                    ErrorManageUtil.uploadError(accountId, "", "AccountInfo", e.getMessage());
                }
            });

			executor.execute(() ->
			{
			    if(!(isTypeInScope("iamgroup"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "IAM Groups");
					FileManager.generateIamGroupFiles(InventoryUtil.fetchIAMGroups(temporaryCredentials, accountId, accountName));
				}catch(Exception e){
					log.error(expPrefix+ "IAM Groups\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "iamgroup", e.getMessage());
				}
			});

			executor.execute(() ->
			{
			    if(!(isTypeInScope("cloudtrail"))) {
                    return;
                }

				try{
					log.info(infoPrefix + "CloudTrail");
					FileManager.generateCloudTrailFiles(InventoryUtil.fetchCloudTrails(temporaryCredentials, skipRegions, accountId, accountName));
				}catch(Exception e){
					log.error(expPrefix+ "Cloud Trailt\", \"cause\":\"" +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(accountId, "", "cloudtrail", e.getMessage());
				}
			});
			//****** Changes For Federated Rules End ******

			executor.shutdown();
			while (!executor.isTerminated()) {

			}

			log.info("Completed Discovery for accountId "+ accountId);
		}

		ErrorManageUtil.writeErrorFile();
		try {
			FileManager.finalise();
			ErrorManageUtil.finalise();
		} catch (IOException e) {
			log.error("Error Writing File",e);
		}
	}

	/**
	 * Checks if is type in scope.
	 *
	 * @param type the type
	 * @return true, if is type in scope
	 */
	private boolean isTypeInScope(String type){
	    if("".equals(targetTypes)){
	        return true;
	    }else{
	        List<String> targetTypesList = Arrays.asList(targetTypes.split(","));
	        return targetTypesList.contains(type);
	    }
	}
}
