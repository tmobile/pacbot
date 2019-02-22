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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.auth.BasicSessionCredentials;
import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.auth.CredentialProvider;
import com.tmobile.cso.pacman.inventory.util.ASGInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.DirectConnectionInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.EC2InventoryUtil;
import com.tmobile.cso.pacman.inventory.util.ESInventoryUtil;
import com.tmobile.cso.pacman.inventory.util.InventoryUtil;
import com.tmobile.cso.pacman.inventory.util.SNSInventoryUtil;



/**
 * The Class AssetFileGeneratorTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FileManager.class,ErrorManageUtil.class,InventoryUtil.class,EC2InventoryUtil.class,SNSInventoryUtil.class,DirectConnectionInventoryUtil.class,
    ASGInventoryUtil.class,ESInventoryUtil.class})
@PowerMockIgnore("javax.management.*")
public class AssetFileGeneratorTest {
    
    /** The asset file generator. */
    @InjectMocks
    AssetFileGenerator assetFileGenerator;
    
    /** The cred provider. */
    @Mock
    CredentialProvider credProvider;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * Generate files test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void generateFilesTest() throws Exception{
        
        mockStatic(FileManager.class);
        PowerMockito.doNothing().when(FileManager.class,"initialise",anyString());
        mockStatic(ErrorManageUtil.class);
        PowerMockito.doNothing().when(ErrorManageUtil.class,"initialise");
        
        ReflectionTestUtils.setField(assetFileGenerator, "targetTypes", "ec2,asg,stack,dynamodb,efs,emr,lambda,classicelb,appelb,targetgroup,"
                + "nat,rdsdb,rdscluster,s3,eni,sg,subnet,checks,redshift,volume,snapshot,vpc,api,iamuser,rdssnapshot,iamrole,kms,cloudfront,beanstalk,phd,"
                + "routetable,networkacl,elasticip,launchconfig,internetgw,vpngw,asgpolicy,snstopic,egressgateway,dhcpoption,peeringconnection,customergateway,"
                + "vpnconnection,directconnect,virtualinterface,elasticsearch,reserved instances,ssm");
        when(credProvider.getCredentials(anyString(), anyString())).thenReturn(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"));
        
        mockStatic(InventoryUtil.class);
        mockStatic(EC2InventoryUtil.class);
        mockStatic(ASGInventoryUtil.class);
        mockStatic(DirectConnectionInventoryUtil.class);
        mockStatic(ESInventoryUtil.class);
        mockStatic(SNSInventoryUtil.class);
        
        when(InventoryUtil.fetchInstances(anyObject(), anyString(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateInstanceFiles",new HashMap<>());
        
        when(InventoryUtil.fetchAsg(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateAsgFiles",new HashMap<>());
        
        when(InventoryUtil.fetchCloudFormationStack(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateCloudFormationStackFiles",new HashMap<>());
        
        when(InventoryUtil.fetchDynamoDBTables(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateDynamoDbFiles",new HashMap<>());
        
        when(InventoryUtil.fetchEFSInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateEfsFiles",new HashMap<>());
        
        when(InventoryUtil.fetchEMRInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateEmrFiles",new HashMap<>());
        
        when(InventoryUtil.fetchLambdaInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateLamdaFiles",new HashMap<>());
        
        when(InventoryUtil.fetchClassicElbInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateClassicElbFiles",new HashMap<>());
        
        when(InventoryUtil.fetchElbInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateApplicationElbFiles",new HashMap<>());
        
        when(InventoryUtil.fetchTargetGroups(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateTargetGroupFiles",new HashMap<>());
        
        when(InventoryUtil.fetchNATGatewayInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateNatGatewayFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRDSInstanceInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateRDSInstanceFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRDSClusterInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateRDSClusterFiles",new HashMap<>());
        
        when(InventoryUtil.fetchS3Info(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap());
        PowerMockito.doNothing().when(FileManager.class,"generateS3Files",new HashMap<>());
        
        when(InventoryUtil.fetchNetworkIntefaces(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateNwInterfaceFiles",new HashMap<>());
        
        when(InventoryUtil.fetchSecurityGroups(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateSecGroupFile",new HashMap<>());
        
        when(InventoryUtil.fetchSubnets(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateSubnetFiles",new HashMap<>());
        
        when(InventoryUtil.fetchTrusterdAdvisorsChecks(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateTrustedAdvisorFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRedshiftInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateRedshiftFiles",new HashMap<>());
        
        when(InventoryUtil.fetchVolumetInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generatefetchVolumeFiles",new HashMap<>());
        
        when(InventoryUtil.fetchSnapshots(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateSnapshotFiles",new HashMap<>());
        
        when(InventoryUtil.fetchVpcInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateVpcFiles",new HashMap<>());
        
        when(InventoryUtil.fetchApiGateways(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateApiGatewayFiles",new HashMap<>());
        
        when(InventoryUtil.fetchIAMUsers(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateIamUserFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRDSDBSnapshots(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateRDSSnapshotFiles",new HashMap<>());
        
        when(InventoryUtil.fetchIAMRoles(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateIamRoleFiles",new HashMap<>());
        
        when(InventoryUtil.fetchKMSKeys(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateKMSFiles",new HashMap<>());
        
        when(InventoryUtil.fetchCloudFrontInfo(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateCloudFrontFiles",new HashMap<>());
        
        when(InventoryUtil.fetchEBSInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateEBSFiles",new HashMap<>());
        
        when(InventoryUtil.fetchPHDInfo(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generatePHDFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchRouteTables(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateEC2RouteTableFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchNetworkACL(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateNetworkAclFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchElasticIPAddresses(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateElasticIPFiles",new HashMap<>());
        
        when(ASGInventoryUtil.fetchLaunchConfigurations(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateLaunchConfigurationsFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchInternetGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateInternetGatewayFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchVPNGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateVPNGatewayFiles",new HashMap<>());
        
        when(ASGInventoryUtil.fetchScalingPolicies(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateScalingPolicies",new HashMap<>());
        
        when(SNSInventoryUtil.fetchSNSTopics(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateSNSTopics",new HashMap<>());
        
        when(EC2InventoryUtil.fetchEgressGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateEgressGateway",new HashMap<>());
        
        when(EC2InventoryUtil.fetchDHCPOptions(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateDhcpOptions",new HashMap<>());
        
        when(EC2InventoryUtil.fetchPeeringConnections(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generatePeeringConnections",new HashMap<>());
        
        when(EC2InventoryUtil.fetchCustomerGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateCustomerGateway",new HashMap<>());
        
        when(EC2InventoryUtil.fetchVPNConnections(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateVpnConnection",new HashMap<>());
        
        when(DirectConnectionInventoryUtil.fetchDirectConnections(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateDirectConnection",new HashMap<>());
        
        when(DirectConnectionInventoryUtil.fetchDirectConnectionsVirtualInterfaces(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateDirectConnectionVirtualInterfaces",new HashMap<>());
        
        when(ESInventoryUtil.fetchESInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateESDomain",new HashMap<>());
        
        when(EC2InventoryUtil.fetchReservedInstances(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateReservedInstances",new HashMap<>());
        
        when(EC2InventoryUtil.fetchSSMInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doNothing().when(FileManager.class,"generateSsmFiles",new HashMap<>());
        
        List<Map<String,String>> accounts = new ArrayList<>();
        Map<String,String> account = new HashMap<>();
        account.put(InventoryConstants.ACCOUNT_ID, "account");
        account.put(InventoryConstants.ACCOUNT_NAME, "accountName");
        accounts.add(account);
        assetFileGenerator.generateFiles(accounts , "skipRegions", "filePath");
        
        ReflectionTestUtils.setField(assetFileGenerator, "targetTypes", "targetType");
        assetFileGenerator.generateFiles(accounts , "skipRegions", "filePath");
    }
    
    /**
     * Generate files test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void generateFilesTest_Exception() throws Exception{
        
        mockStatic(FileManager.class);
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"initialise",anyString());
        
        ReflectionTestUtils.setField(assetFileGenerator, "targetTypes", "ec2,asg,stack,dynamodb,efs,emr,lambda,classicelb,appelb,targetgroup,"
                + "nat,rdsdb,rdscluster,s3,eni,sg,subnet,checks,redshift,volume,snapshot,vpc,api,iamuser,rdssnapshot,iamrole,kms,cloudfront,beanstalk,phd,"
                + "routetable,networkacl,elasticip,launchconfig,internetgw,vpngw,asgpolicy,snstopic,egressgateway,dhcpoption,peeringconnection,customergateway,"
                + "vpnconnection,directconnect,virtualinterface,elasticsearch,reserved instances,ssm");
        when(credProvider.getCredentials(anyString(), anyString())).thenReturn(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"));
        
        mockStatic(InventoryUtil.class);
        mockStatic(EC2InventoryUtil.class);
        mockStatic(ASGInventoryUtil.class);
        mockStatic(DirectConnectionInventoryUtil.class);
        mockStatic(ESInventoryUtil.class);
        mockStatic(SNSInventoryUtil.class);
        mockStatic(ErrorManageUtil.class);
        
        PowerMockito.doNothing().when(ErrorManageUtil.class,"uploadError",anyString(),anyString(),anyString(),anyString());
        
        when(InventoryUtil.fetchInstances(anyObject(), anyString(), anyString(), anyString(),anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateInstanceFiles",new HashMap<>());
        
        when(InventoryUtil.fetchAsg(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateAsgFiles",new HashMap<>());
        
        when(InventoryUtil.fetchCloudFormationStack(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateCloudFormationStackFiles",new HashMap<>());
        
        when(InventoryUtil.fetchDynamoDBTables(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateDynamoDbFiles",new HashMap<>());
        
        when(InventoryUtil.fetchEFSInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateEfsFiles",new HashMap<>());
        
        when(InventoryUtil.fetchEMRInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateEmrFiles",new HashMap<>());
        
        when(InventoryUtil.fetchLambdaInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateLamdaFiles",new HashMap<>());
        
        when(InventoryUtil.fetchClassicElbInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateClassicElbFiles",new HashMap<>());
        
        when(InventoryUtil.fetchElbInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateApplicationElbFiles",new HashMap<>());
        
        when(InventoryUtil.fetchTargetGroups(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateTargetGroupFiles",new HashMap<>());
        
        when(InventoryUtil.fetchNATGatewayInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateNatGatewayFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRDSInstanceInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateRDSInstanceFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRDSClusterInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateRDSClusterFiles",new HashMap<>());
        
        when(InventoryUtil.fetchS3Info(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateS3Files",new HashMap<>());
        
        when(InventoryUtil.fetchNetworkIntefaces(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateNwInterfaceFiles",new HashMap<>());
        
        when(InventoryUtil.fetchSecurityGroups(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateSecGroupFile",new HashMap<>());
        
        when(InventoryUtil.fetchSubnets(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateSubnetFiles",new HashMap<>());
        
        when(InventoryUtil.fetchTrusterdAdvisorsChecks(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateTrustedAdvisorFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRedshiftInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateRedshiftFiles",new HashMap<>());
        
        when(InventoryUtil.fetchVolumetInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generatefetchVolumeFiles",new HashMap<>());
        
        when(InventoryUtil.fetchSnapshots(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateSnapshotFiles",new HashMap<>());
        
        when(InventoryUtil.fetchVpcInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateVpcFiles",new HashMap<>());
        
        when(InventoryUtil.fetchApiGateways(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateApiGatewayFiles",new HashMap<>());
        
        when(InventoryUtil.fetchIAMUsers(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateIamUserFiles",new HashMap<>());
        
        when(InventoryUtil.fetchRDSDBSnapshots(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateRDSSnapshotFiles",new HashMap<>());
        
        when(InventoryUtil.fetchIAMRoles(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateIamRoleFiles",new HashMap<>());
        
        when(InventoryUtil.fetchKMSKeys(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateKMSFiles",new HashMap<>());
        
        when(InventoryUtil.fetchCloudFrontInfo(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateCloudFrontFiles",new HashMap<>());
        
        when(InventoryUtil.fetchEBSInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateEBSFiles",new HashMap<>());
        
        when(InventoryUtil.fetchPHDInfo(anyObject(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generatePHDFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchRouteTables(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateEC2RouteTableFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchNetworkACL(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateNetworkAclFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchElasticIPAddresses(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateElasticIPFiles",new HashMap<>());
        
        when(ASGInventoryUtil.fetchLaunchConfigurations(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateLaunchConfigurationsFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchInternetGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateInternetGatewayFiles",new HashMap<>());
        
        when(EC2InventoryUtil.fetchVPNGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateVPNGatewayFiles",new HashMap<>());
        
        when(ASGInventoryUtil.fetchScalingPolicies(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateScalingPolicies",new HashMap<>());
        
        when(SNSInventoryUtil.fetchSNSTopics(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateSNSTopics",new HashMap<>());
        
        when(EC2InventoryUtil.fetchEgressGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateEgressGateway",new HashMap<>());
        
        when(EC2InventoryUtil.fetchDHCPOptions(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateDhcpOptions",new HashMap<>());
        
        when(EC2InventoryUtil.fetchPeeringConnections(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generatePeeringConnections",new HashMap<>());
        
        when(EC2InventoryUtil.fetchCustomerGateway(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateCustomerGateway",new HashMap<>());
        
        when(EC2InventoryUtil.fetchVPNConnections(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateVpnConnection",new HashMap<>());
        
        when(DirectConnectionInventoryUtil.fetchDirectConnections(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateDirectConnection",new HashMap<>());
        
        when(DirectConnectionInventoryUtil.fetchDirectConnectionsVirtualInterfaces(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateDirectConnectionVirtualInterfaces",new HashMap<>());
        
        when(ESInventoryUtil.fetchESInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateESDomain",new HashMap<>());
        
        when(EC2InventoryUtil.fetchReservedInstances(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateReservedInstances",new HashMap<>());
        
        when(EC2InventoryUtil.fetchSSMInfo(anyObject(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateSsmFiles",new HashMap<>());
        
        List<Map<String,String>> accounts = new ArrayList<>();
        Map<String,String> account = new HashMap<>();
        account.put(InventoryConstants.ACCOUNT_ID, "account");
        account.put(InventoryConstants.ACCOUNT_NAME, "accountName");
        accounts.add(account);
        assetFileGenerator.generateFiles(accounts , "skipRegions", "filePath");
        
    }
}
