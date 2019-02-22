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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionImpl;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.DistributionList;
import com.amazonaws.services.cloudfront.model.DistributionSummary;
import com.amazonaws.services.cloudfront.model.ListDistributionsResult;
import com.amazonaws.services.cloudfront.model.Tags;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ListTagsOfResourceResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeNatGatewaysResult;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSnapshotsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DescribeVpcEndpointsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.NatGateway;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.VpcEndpoint;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentResourcesResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsResult;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentResourceDescription;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystem;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystemClientBuilder;
import com.amazonaws.services.elasticfilesystem.model.DescribeFileSystemsResult;
import com.amazonaws.services.elasticfilesystem.model.DescribeTagsResult;
import com.amazonaws.services.elasticfilesystem.model.FileSystemDescription;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.TagDescription;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthResult;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.Cluster;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterResult;
import com.amazonaws.services.elasticmapreduce.model.ListClustersResult;
import com.amazonaws.services.health.AWSHealth;
import com.amazonaws.services.health.AWSHealthClientBuilder;
import com.amazonaws.services.health.model.AffectedEntity;
import com.amazonaws.services.health.model.DescribeAffectedEntitiesResult;
import com.amazonaws.services.health.model.DescribeEventDetailsResult;
import com.amazonaws.services.health.model.DescribeEventsResult;
import com.amazonaws.services.health.model.Event;
import com.amazonaws.services.health.model.EventDetails;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AccessKeyLastUsed;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedResult;
import com.amazonaws.services.identitymanagement.model.GetLoginProfileResult;
import com.amazonaws.services.identitymanagement.model.Group;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;
import com.amazonaws.services.identitymanagement.model.ListGroupsForUserResult;
import com.amazonaws.services.identitymanagement.model.ListMFADevicesResult;
import com.amazonaws.services.identitymanagement.model.ListRolesResult;
import com.amazonaws.services.identitymanagement.model.ListUsersResult;
import com.amazonaws.services.identitymanagement.model.LoginProfile;
import com.amazonaws.services.identitymanagement.model.MFADevice;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.GetKeyRotationStatusResult;
import com.amazonaws.services.kms.model.KeyListEntry;
import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.kms.model.ListKeysResult;
import com.amazonaws.services.kms.model.ListResourceTagsResult;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.ListTagsResult;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.DescribeDBClustersResult;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.amazonaws.services.rds.model.ListTagsForResourceResult;
import com.amazonaws.services.redshift.AmazonRedshift;
import com.amazonaws.services.redshift.AmazonRedshiftClientBuilder;
import com.amazonaws.services.redshift.model.DescribeClustersResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.TagSet;
import com.amazonaws.services.support.AWSSupport;
import com.amazonaws.services.support.AWSSupportClientBuilder;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorCheckResultResult;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorChecksResult;
import com.amazonaws.services.support.model.RefreshTrustedAdvisorCheckResult;
import com.amazonaws.services.support.model.TrustedAdvisorCheckDescription;
import com.amazonaws.services.support.model.TrustedAdvisorCheckResult;
import com.amazonaws.services.support.model.TrustedAdvisorResourceDetail;


/**
 * The Class InventoryUtilTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegionUtils.class,AmazonEC2ClientBuilder.class,AmazonAutoScalingClientBuilder.class,AmazonCloudFormationClientBuilder.class,
    AmazonDynamoDBClientBuilder.class,AmazonElasticFileSystemClientBuilder.class,AmazonElasticMapReduceClientBuilder.class,AWSLambdaClientBuilder.class,AmazonElasticLoadBalancingClientBuilder.class,
    com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.class,AmazonRDSClientBuilder.class,AmazonS3ClientBuilder.class,AmazonRedshiftClientBuilder.class,
    AmazonApiGatewayClientBuilder.class,AmazonIdentityManagementClientBuilder.class,AWSKMSClientBuilder.class,AmazonCloudFrontClientBuilder.class,AWSElasticBeanstalkClientBuilder.class,
    AWSHealthClientBuilder.class,AWSSupportClientBuilder.class,com.amazonaws.services.s3.model.Region.class})
@PowerMockIgnore("javax.management.*")
public class InventoryUtilTest {

    /** The inventory util. */
    @InjectMocks
    InventoryUtil inventoryUtil;
    
    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        mockStatic(RegionUtils.class);
        when(RegionUtils.getRegions()).thenReturn(getRegions());
    }
    
    /**
     * Fetch instances test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchInstancesTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeInstancesResult describeInstancesResult = new DescribeInstancesResult();
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance());
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservation.setInstances(instanceList);
        reservations.add(reservation);
        describeInstancesResult.setReservations(reservations );
        when(ec2Client.describeInstances(anyObject())).thenReturn(describeInstancesResult);
        assertThat(inventoryUtil.fetchInstances(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName","").size(), is(1));
        
    }
    
    /**
     * Fetch instances test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchInstancesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchInstances(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName","").size(), is(0));
    }
    
    /**
     * Fetch network interfaces test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchNetworkInterfacesTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeNetworkInterfacesResult describeNetworkInterfacesResult = new DescribeNetworkInterfacesResult();
        List<NetworkInterface> niList = new ArrayList<>();
        niList.add(new NetworkInterface());
        describeNetworkInterfacesResult.setNetworkInterfaces(niList);
        when(ec2Client.describeNetworkInterfaces()).thenReturn(describeNetworkInterfacesResult);
        assertThat(inventoryUtil.fetchNetworkIntefaces(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch network intefaces test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchNetworkIntefacesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchNetworkIntefaces(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch security groups test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSecurityGroupsTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeSecurityGroupsResult describeSecurityGroupsResult = new DescribeSecurityGroupsResult();
        List<SecurityGroup> secGrpList = new ArrayList<>();
        secGrpList.add(new SecurityGroup());
        describeSecurityGroupsResult.setSecurityGroups(secGrpList);
        when(ec2Client.describeSecurityGroups()).thenReturn(describeSecurityGroupsResult);
        assertThat(inventoryUtil.fetchSecurityGroups(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch security groups test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSecurityGroupsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchSecurityGroups(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch launch configurations test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchLaunchConfigurationsTest() throws Exception {
        
        mockStatic(AmazonAutoScalingClientBuilder.class);
        AmazonAutoScaling asgClient = PowerMockito.mock(AmazonAutoScaling.class);
        AmazonAutoScalingClientBuilder amazonAutoScalingClientBuilder = PowerMockito.mock(AmazonAutoScalingClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonAutoScalingClientBuilder.standard()).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.withCredentials(anyObject())).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.withRegion(anyString())).thenReturn(amazonAutoScalingClientBuilder);
        when(amazonAutoScalingClientBuilder.build()).thenReturn(asgClient);
        
        DescribeAutoScalingGroupsResult autoScalingGroupsResult = new DescribeAutoScalingGroupsResult();
        List<AutoScalingGroup> asgList = new ArrayList<>();
        asgList.add(new AutoScalingGroup());
        autoScalingGroupsResult.setAutoScalingGroups(asgList);
        when(asgClient.describeAutoScalingGroups(anyObject())).thenReturn(autoScalingGroupsResult);
        assertThat(inventoryUtil.fetchAsg(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch launch configurations test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchLaunchConfigurationsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchAsg(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch cloud formation stack test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchCloudFormationStackTest() throws Exception {
        
        mockStatic(AmazonCloudFormationClientBuilder.class);
        AmazonCloudFormation cloudFormClient = PowerMockito.mock(AmazonCloudFormation.class);
        AmazonCloudFormationClientBuilder amazonCloudFormationClientBuilder = PowerMockito.mock(AmazonCloudFormationClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonCloudFormationClientBuilder.standard()).thenReturn(amazonCloudFormationClientBuilder);
        when(amazonCloudFormationClientBuilder.withCredentials(anyObject())).thenReturn(amazonCloudFormationClientBuilder);
        when(amazonCloudFormationClientBuilder.withRegion(anyString())).thenReturn(amazonCloudFormationClientBuilder);
        when(amazonCloudFormationClientBuilder.build()).thenReturn(cloudFormClient);
        
        DescribeStacksResult describeStacksResult = new DescribeStacksResult();
        List<Stack> stacks = new ArrayList<>();
        stacks.add(new Stack());
        describeStacksResult.setStacks(stacks);
        when(cloudFormClient.describeStacks(anyObject())).thenReturn(describeStacksResult);
        assertThat(inventoryUtil.fetchCloudFormationStack(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch cloud formation stack test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchCloudFormationStackTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchCloudFormationStack(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch dynamo DB tables test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchDynamoDBTablesTest() throws Exception {
        
        mockStatic(AmazonDynamoDBClientBuilder.class);
        AmazonDynamoDB awsClient = PowerMockito.mock(AmazonDynamoDB.class);
        AmazonDynamoDBClientBuilder amazonDynamoDBClientBuilder = PowerMockito.mock(AmazonDynamoDBClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonDynamoDBClientBuilder.standard()).thenReturn(amazonDynamoDBClientBuilder);
        when(amazonDynamoDBClientBuilder.withCredentials(anyObject())).thenReturn(amazonDynamoDBClientBuilder);
        when(amazonDynamoDBClientBuilder.withRegion(anyString())).thenReturn(amazonDynamoDBClientBuilder);
        when(amazonDynamoDBClientBuilder.build()).thenReturn(awsClient);
        
        ListTablesResult listTableResult = new ListTablesResult();
        List<String> tables = new ArrayList<>();
        tables.add(new String());
        listTableResult.setTableNames(tables);
        when(awsClient.listTables()).thenReturn(listTableResult);
        
        DescribeTableResult describeTableResult = new DescribeTableResult();
        TableDescription table = new TableDescription();
        table.setTableArn("tableArn");
        describeTableResult.setTable(table);
        when(awsClient.describeTable(anyString())).thenReturn(describeTableResult);
        
        when(awsClient.listTagsOfResource(anyObject())).thenReturn(new ListTagsOfResourceResult());
        assertThat(inventoryUtil.fetchDynamoDBTables(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch dynamo DB tables test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    public void fetchDynamoDBTablesTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchDynamoDBTables(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch EFS info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEFSInfoTest() throws Exception {
        
        mockStatic(AmazonElasticFileSystemClientBuilder.class);
        AmazonElasticFileSystem efsClient = PowerMockito.mock(AmazonElasticFileSystem.class);
        AmazonElasticFileSystemClientBuilder amazonElasticFileSystemClientBuilder = PowerMockito.mock(AmazonElasticFileSystemClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonElasticFileSystemClientBuilder.standard()).thenReturn(amazonElasticFileSystemClientBuilder);
        when(amazonElasticFileSystemClientBuilder.withCredentials(anyObject())).thenReturn(amazonElasticFileSystemClientBuilder);
        when(amazonElasticFileSystemClientBuilder.withRegion(anyString())).thenReturn(amazonElasticFileSystemClientBuilder);
        when(amazonElasticFileSystemClientBuilder.build()).thenReturn(efsClient);
        
        DescribeFileSystemsResult describeFileSystemsResult = new DescribeFileSystemsResult();
        List<FileSystemDescription> efsList = new ArrayList<>();
        FileSystemDescription fileSystemDescription = new FileSystemDescription();
        fileSystemDescription.setFileSystemId("fileSystemId");
        efsList.add(fileSystemDescription);
        describeFileSystemsResult.setFileSystems(efsList);
        when(efsClient.describeFileSystems(anyObject())).thenReturn(describeFileSystemsResult);
        when(efsClient.describeTags(anyObject())).thenReturn(new DescribeTagsResult());
        assertThat(inventoryUtil.fetchEFSInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch EFS info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEFSInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchEFSInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch EMR info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEMRInfoTest() throws Exception {
        
        mockStatic(AmazonElasticMapReduceClientBuilder.class);
        AmazonElasticMapReduce emrClient = PowerMockito.mock(AmazonElasticMapReduce.class);
        AmazonElasticMapReduceClientBuilder amazonElasticFileSystemClientBuilder = PowerMockito.mock(AmazonElasticMapReduceClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonElasticFileSystemClientBuilder.standard()).thenReturn(amazonElasticFileSystemClientBuilder);
        when(amazonElasticFileSystemClientBuilder.withCredentials(anyObject())).thenReturn(amazonElasticFileSystemClientBuilder);
        when(amazonElasticFileSystemClientBuilder.withRegion(anyString())).thenReturn(amazonElasticFileSystemClientBuilder);
        when(amazonElasticFileSystemClientBuilder.build()).thenReturn(emrClient);
        
        ListClustersResult listClustersResult = new ListClustersResult();
        List<ClusterSummary> clusters = new ArrayList<>();
        ClusterSummary clusterSummary = new ClusterSummary();
        clusterSummary.setId("id");
        clusters.add(clusterSummary);
        listClustersResult.setClusters(clusters);
        when(emrClient.listClusters(anyObject())).thenReturn(listClustersResult);
        
        DescribeClusterResult describeClusterResult = new DescribeClusterResult();
        describeClusterResult.setCluster(new Cluster());
        when(emrClient.describeCluster(anyObject())).thenReturn(describeClusterResult);
        assertThat(inventoryUtil.fetchEMRInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch EMR info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEMRInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchEMRInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch lambda info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchLambdaInfoTest() throws Exception {
        
        mockStatic(AWSLambdaClientBuilder.class);
        AWSLambda lamdaClient = PowerMockito.mock(AWSLambda.class);
        AWSLambdaClientBuilder awsLambdaClientBuilder = PowerMockito.mock(AWSLambdaClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(awsLambdaClientBuilder.standard()).thenReturn(awsLambdaClientBuilder);
        when(awsLambdaClientBuilder.withCredentials(anyObject())).thenReturn(awsLambdaClientBuilder);
        when(awsLambdaClientBuilder.withRegion(anyString())).thenReturn(awsLambdaClientBuilder);
        when(awsLambdaClientBuilder.build()).thenReturn(lamdaClient);
        
        ListFunctionsResult listFunctionsResult = new ListFunctionsResult();
        List<FunctionConfiguration> functions = new ArrayList<>();
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setFunctionArn("functionArn");
        functions.add(functionConfiguration);
        listFunctionsResult.setFunctions(functions);
        when(lamdaClient.listFunctions(anyObject())).thenReturn(listFunctionsResult);
        
        ListTagsResult listTagsResult = new ListTagsResult();
        listTagsResult.setTags(new HashMap<>());
        when(lamdaClient.listTags(anyObject())).thenReturn(listTagsResult);
        assertThat(inventoryUtil.fetchLambdaInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch lambda info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchLambdaInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchLambdaInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch classic elb info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchClassicElbInfoTest() throws Exception {
        
        mockStatic(AmazonElasticLoadBalancingClientBuilder.class);
        AmazonElasticLoadBalancing elbClient = PowerMockito.mock(AmazonElasticLoadBalancing.class);
        AmazonElasticLoadBalancingClientBuilder amazonElasticLoadBalancingClientBuilder = PowerMockito.mock(AmazonElasticLoadBalancingClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonElasticLoadBalancingClientBuilder.standard()).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.withCredentials(anyObject())).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.withRegion(anyString())).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.build()).thenReturn(elbClient);
        
        com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult elbDescResult = new com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult();
        List<LoadBalancerDescription> elbList = new ArrayList<>();
        LoadBalancerDescription loadBalancerDescription = new LoadBalancerDescription();
        loadBalancerDescription.setLoadBalancerName("loadBalancerName");
        elbList.add(loadBalancerDescription);
        elbDescResult.setLoadBalancerDescriptions(elbList);
        when(elbClient.describeLoadBalancers(anyObject())).thenReturn(elbDescResult);
        
        com.amazonaws.services.elasticloadbalancing.model.DescribeTagsResult describeTagsResult = new com.amazonaws.services.elasticloadbalancing.model.DescribeTagsResult();
        List<TagDescription> tagsList = new ArrayList<TagDescription>();
        TagDescription tagDescription = new TagDescription();
        tagDescription.setLoadBalancerName("loadBalancerName");
        tagDescription.setTags(new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Tag>());
        tagsList.add(tagDescription);
        describeTagsResult.setTagDescriptions(tagsList);
        when(elbClient.describeTags(anyObject())).thenReturn(describeTagsResult);
        assertThat(inventoryUtil.fetchClassicElbInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch classic elb info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchClassicElbInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchClassicElbInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch elb info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchElbInfoTest() throws Exception {
        
        mockStatic(com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.class);
        com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing elbClient = PowerMockito.mock(com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing.class);
        com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder amazonElasticLoadBalancingClientBuilder = PowerMockito.mock(com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonElasticLoadBalancingClientBuilder.standard()).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.withCredentials(anyObject())).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.withRegion(anyString())).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.build()).thenReturn(elbClient);
        
        DescribeLoadBalancersResult elbDescResult = new DescribeLoadBalancersResult();
        List<LoadBalancer> elbList = new ArrayList<>();
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.setLoadBalancerArn("loadBalancerArn");
        elbList.add(loadBalancer);
        elbDescResult.setLoadBalancers(elbList);
        when(elbClient.describeLoadBalancers(anyObject())).thenReturn(elbDescResult);
        
        com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsResult describeTagsResult = new com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsResult();
        List<com.amazonaws.services.elasticloadbalancingv2.model.TagDescription> tagsList = new ArrayList<>();
        com.amazonaws.services.elasticloadbalancingv2.model.TagDescription tagDescription = new com.amazonaws.services.elasticloadbalancingv2.model.TagDescription();
        tagDescription.setResourceArn("loadBalancerArn");
        tagDescription.setTags(new ArrayList<com.amazonaws.services.elasticloadbalancingv2.model.Tag>());
        tagsList.add(tagDescription);
        describeTagsResult.setTagDescriptions(tagsList);
        when(elbClient.describeTags(anyObject())).thenReturn(describeTagsResult);
        assertThat(inventoryUtil.fetchElbInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
    }
    
    /**
     * Fetch elb info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchElbInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchElbInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch target groups test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchTargetGroupsTest() throws Exception {
        
        mockStatic(com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.class);
        com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing elbClient = PowerMockito.mock(com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing.class);
        com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder amazonElasticLoadBalancingClientBuilder = PowerMockito.mock(com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonElasticLoadBalancingClientBuilder.standard()).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.withCredentials(anyObject())).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.withRegion(anyString())).thenReturn(amazonElasticLoadBalancingClientBuilder);
        when(amazonElasticLoadBalancingClientBuilder.build()).thenReturn(elbClient);
        
        DescribeTargetGroupsResult trgtGrpRslt = new DescribeTargetGroupsResult();
        List<TargetGroup> targetGrpList = new ArrayList<>();
        TargetGroup targetGroup = new TargetGroup();
        targetGroup.setTargetGroupArn("targetGroupArn");
        targetGrpList.add(targetGroup);
        trgtGrpRslt.setTargetGroups(targetGrpList);
        when(elbClient.describeTargetGroups(anyObject())).thenReturn(trgtGrpRslt);
        
        DescribeTargetHealthResult describeTargetHealthResult = new DescribeTargetHealthResult();
        describeTargetHealthResult.setTargetHealthDescriptions(new ArrayList<>());
        when(elbClient.describeTargetHealth(anyObject())).thenReturn(describeTargetHealthResult);
        assertThat(inventoryUtil.fetchTargetGroups(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch target groups test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchTargetGroupsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchTargetGroups(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch NAT gateway info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchNATGatewayInfoTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeNatGatewaysResult describeNatGatewaysResult = new DescribeNatGatewaysResult();
        List<NatGateway> natGatwayList = new ArrayList<>();
        natGatwayList.add(new NatGateway());
        describeNatGatewaysResult.setNatGateways(natGatwayList);
        when(ec2Client.describeNatGateways(anyObject())).thenReturn(describeNatGatewaysResult);
        assertThat(inventoryUtil.fetchNATGatewayInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch NAT gateway info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchNATGatewayInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchNATGatewayInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch RDS cluster info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRDSClusterInfoTest() throws Exception {
        
        mockStatic(AmazonRDSClientBuilder.class);
        AmazonRDS rdsClient = PowerMockito.mock(AmazonRDS.class);
        AmazonRDSClientBuilder amazonRDSClientBuilder = PowerMockito.mock(AmazonRDSClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonRDSClientBuilder.standard()).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withCredentials(anyObject())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withRegion(anyString())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.build()).thenReturn(rdsClient);
        
        DescribeDBClustersResult describeDBClustersResult = new DescribeDBClustersResult();
        List<DBCluster> rdsList = new ArrayList<>();
        DBCluster dBCluster = new DBCluster();
        dBCluster.setDBClusterArn("dBClusterArn");;
        rdsList.add(dBCluster);
        describeDBClustersResult.setDBClusters(rdsList);
        when(rdsClient.describeDBClusters(anyObject())).thenReturn(describeDBClustersResult);
        
        ListTagsForResourceResult listTagsForResourceResult = new ListTagsForResourceResult();
        listTagsForResourceResult.setTagList(new ArrayList<>());
        when(rdsClient.listTagsForResource(anyObject())).thenReturn(listTagsForResourceResult);
        assertThat(inventoryUtil.fetchRDSClusterInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch RDS cluster info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRDSClusterInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchRDSClusterInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch RDS instance info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRDSInstanceInfoTest() throws Exception {
        
        mockStatic(AmazonRDSClientBuilder.class);
        AmazonRDS rdsClient = PowerMockito.mock(AmazonRDS.class);
        AmazonRDSClientBuilder amazonRDSClientBuilder = PowerMockito.mock(AmazonRDSClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonRDSClientBuilder.standard()).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withCredentials(anyObject())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withRegion(anyString())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.build()).thenReturn(rdsClient);
        
        DescribeDBInstancesResult describeDBInstancesResult = new DescribeDBInstancesResult();
        List<DBInstance> rdsList = new ArrayList<>();
        DBInstance dBInstance = new DBInstance();
        dBInstance.setDBInstanceArn("dBInstanceArn");
        rdsList.add(dBInstance);
        describeDBInstancesResult.setDBInstances(rdsList);
        when(rdsClient.describeDBInstances(anyObject())).thenReturn(describeDBInstancesResult);
        
        ListTagsForResourceResult listTagsForResourceResult = new ListTagsForResourceResult();
        listTagsForResourceResult.setTagList(new ArrayList<>());
        when(rdsClient.listTagsForResource(anyObject())).thenReturn(listTagsForResourceResult);
        assertThat(inventoryUtil.fetchRDSInstanceInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch RDS instance info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRDSInstanceInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchRDSInstanceInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch S 3 info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({ "static-access"})
    @Test
    public void fetchS3InfoTest() throws Exception {
        
        mockStatic(AmazonS3ClientBuilder.class);
        AmazonS3 amazonS3Client = PowerMockito.mock(AmazonS3.class);
        AmazonS3ClientBuilder amazonRDSClientBuilder = PowerMockito.mock(AmazonS3ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonRDSClientBuilder.standard()).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withCredentials(anyObject())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withRegion(anyString())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.build()).thenReturn(amazonS3Client);
        
        List<Bucket> s3buckets = new ArrayList<>();
        Bucket bucket = new Bucket();
        bucket.setName("name");
        s3buckets.add(bucket);
        when(amazonS3Client.listBuckets()).thenReturn(s3buckets);
        when(amazonS3Client.getBucketLocation(anyString())).thenReturn("bucketLocation");
        mockStatic(com.amazonaws.services.s3.model.Region.class);
        com.amazonaws.services.s3.model.Region value = null;
        when(com.amazonaws.services.s3.model.Region.fromValue(anyString())).thenReturn(value.US_West);
        when(value.US_West.toAWSRegion()).thenReturn(getRegions().get(0));
        when(amazonS3Client.getBucketVersioningConfiguration(anyString())).thenReturn(new BucketVersioningConfiguration());
        BucketTaggingConfiguration tagConfig = new BucketTaggingConfiguration();
        List<TagSet> tagSets = new ArrayList<>();
        TagSet tagSet = new TagSet();
        tagSet.setTag("key", "value");
        tagSets.add(tagSet);
        tagSets.add(tagSet);
        tagConfig.setTagSets(tagSets);
        when(amazonS3Client.getBucketTaggingConfiguration(anyString())).thenReturn(tagConfig);
        
        assertThat(inventoryUtil.fetchS3Info(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch S 3 info test test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchS3InfoTestTest_Exception() throws Exception {
        
        mockStatic(AmazonS3ClientBuilder.class);
        AmazonS3 amazonS3Client = PowerMockito.mock(AmazonS3.class);
        AmazonS3ClientBuilder amazonRDSClientBuilder = PowerMockito.mock(AmazonS3ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonRDSClientBuilder.standard()).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withCredentials(anyObject())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withRegion(anyString())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.build()).thenReturn(amazonS3Client);
        
        List<Bucket> s3buckets = new ArrayList<>();
        Bucket bucket = new Bucket();
        bucket.setName("name");
        s3buckets.add(bucket);
        when(amazonS3Client.listBuckets()).thenReturn(s3buckets);
        
        when(amazonS3Client.getBucketLocation(anyString())).thenThrow(new AmazonServiceException("Error"));
        assertThat(inventoryUtil.fetchS3Info(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch subnets test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSubnetsTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeSubnetsResult describeSubnetsResult = new DescribeSubnetsResult();
        List<Subnet> subnets = new ArrayList<>();
        subnets.add(new Subnet());
        describeSubnetsResult.setSubnets(subnets);
        when(ec2Client.describeSubnets()).thenReturn(describeSubnetsResult);
        assertThat(inventoryUtil.fetchSubnets(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch subnets test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSubnetsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchSubnets(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch trusterd advisors checks test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchTrusterdAdvisorsChecksTest() throws Exception {
        
        mockStatic(AWSSupportClientBuilder.class);
        AWSSupport awsSupportClient = PowerMockito.mock(AWSSupport.class);
        AWSSupportClientBuilder awsSupportClientBuilder = PowerMockito.mock(AWSSupportClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(awsSupportClientBuilder.standard()).thenReturn(awsSupportClientBuilder);
        when(awsSupportClientBuilder.withCredentials(anyObject())).thenReturn(awsSupportClientBuilder);
        when(awsSupportClientBuilder.withRegion(anyString())).thenReturn(awsSupportClientBuilder);
        when(awsSupportClientBuilder.build()).thenReturn(awsSupportClient);
        
        DescribeTrustedAdvisorChecksResult describeTrustedAdvisorChecksResult = new DescribeTrustedAdvisorChecksResult();
        List<TrustedAdvisorCheckDescription> trstdAdvsrList = new ArrayList<>();
        TrustedAdvisorCheckDescription trustedAdvisorCheckDescription = new TrustedAdvisorCheckDescription();
        trustedAdvisorCheckDescription.setId("id");
        List<String> metadata = new ArrayList<>();
        metadata.add("metaData");
        trustedAdvisorCheckDescription.setMetadata(metadata);
        trstdAdvsrList.add(trustedAdvisorCheckDescription);
        describeTrustedAdvisorChecksResult.setChecks(trstdAdvsrList);
        when(awsSupportClient.describeTrustedAdvisorChecks(anyObject())).thenReturn(describeTrustedAdvisorChecksResult);
        
        DescribeTrustedAdvisorCheckResultResult result = new DescribeTrustedAdvisorCheckResultResult();
        TrustedAdvisorCheckResult trustedAdvisorCheckResult = new TrustedAdvisorCheckResult();
        trustedAdvisorCheckResult.setStatus("not ok");
        List<TrustedAdvisorResourceDetail> flaggedResources = new ArrayList<>();
        TrustedAdvisorResourceDetail trustedAdvisorResourceDetail = new TrustedAdvisorResourceDetail();
        trustedAdvisorResourceDetail.setResourceId("id");
        trustedAdvisorResourceDetail.setMetadata(metadata);
        flaggedResources.add(trustedAdvisorResourceDetail);
        trustedAdvisorCheckResult.setFlaggedResources(flaggedResources );
        result.setResult(trustedAdvisorCheckResult);
        when(awsSupportClient.describeTrustedAdvisorCheckResult(anyObject())).thenReturn(result );
        
        when(awsSupportClient.refreshTrustedAdvisorCheck(anyObject())).thenReturn(new RefreshTrustedAdvisorCheckResult());
        assertThat(inventoryUtil.fetchTrusterdAdvisorsChecks(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch redshift info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRedshiftInfoTest() throws Exception {
        
        mockStatic(AmazonRedshiftClientBuilder.class);
        AmazonRedshift redshiftClient = PowerMockito.mock(AmazonRedshift.class);
        AmazonRedshiftClientBuilder amazonRedshiftClientBuilder = PowerMockito.mock(AmazonRedshiftClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonRedshiftClientBuilder.standard()).thenReturn(amazonRedshiftClientBuilder);
        when(amazonRedshiftClientBuilder.withCredentials(anyObject())).thenReturn(amazonRedshiftClientBuilder);
        when(amazonRedshiftClientBuilder.withRegion(anyString())).thenReturn(amazonRedshiftClientBuilder);
        when(amazonRedshiftClientBuilder.build()).thenReturn(redshiftClient);
        
        DescribeClustersResult describeClustersResult = new DescribeClustersResult();
        List<com.amazonaws.services.redshift.model.Cluster> redshiftList = new ArrayList<>();
        redshiftList.add(new com.amazonaws.services.redshift.model.Cluster());
        describeClustersResult.setClusters(redshiftList);
        when(redshiftClient.describeClusters(anyObject())).thenReturn(describeClustersResult);
        assertThat(inventoryUtil.fetchRedshiftInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch redshift info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRedshiftInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchRedshiftInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch volumet info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVolumetInfoTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeVolumesResult describeVolumesResult = new DescribeVolumesResult();
        List<Volume> volumeList = new ArrayList<>();
        volumeList.add(new Volume());
        describeVolumesResult.setVolumes(volumeList);
        when(ec2Client.describeVolumes()).thenReturn(describeVolumesResult);
        assertThat(inventoryUtil.fetchVolumetInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch volumet info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVolumetInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchVolumetInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch snapshots test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSnapshotsTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeSnapshotsResult describeSnapshotsResult = new DescribeSnapshotsResult();
        List<Snapshot> snapShotsList = new ArrayList<>();
        snapShotsList.add(new Snapshot());
        describeSnapshotsResult.setSnapshots(snapShotsList);
        when(ec2Client.describeSnapshots(anyObject())).thenReturn(describeSnapshotsResult);
        assertThat(inventoryUtil.fetchSnapshots(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch snapshots test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchSnapshotsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchSnapshots(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch vpc info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVpcInfoTest() throws Exception {
        
        mockStatic(AmazonEC2ClientBuilder.class);
        AmazonEC2 ec2Client = PowerMockito.mock(AmazonEC2.class);
        AmazonEC2ClientBuilder amazonEC2ClientBuilder = PowerMockito.mock(AmazonEC2ClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonEC2ClientBuilder.standard()).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withCredentials(anyObject())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.withRegion(anyString())).thenReturn(amazonEC2ClientBuilder);
        when(amazonEC2ClientBuilder.build()).thenReturn(ec2Client);
        
        DescribeVpcsResult describeVpcsResult = new DescribeVpcsResult();
        List<Vpc> vpcList = new ArrayList<>();
        Vpc vpc = new Vpc();
        vpc.setVpcId("vpcId");
        vpcList.add(vpc);
        describeVpcsResult.setVpcs(vpcList);
        when(ec2Client.describeVpcs()).thenReturn(describeVpcsResult);
        
        DescribeVpcEndpointsResult describeVpcEndpointsResult = new DescribeVpcEndpointsResult();
        List<VpcEndpoint> vpcEndpoints = new ArrayList<VpcEndpoint>();
        VpcEndpoint vpcEndpoint = new VpcEndpoint();
        vpcEndpoint.setPolicyDocument("{\"Statement\":[{\"Effect\":\"Allow\",\"Resource\":\"*\"}]}");
        vpcEndpoints.add(vpcEndpoint);
        describeVpcEndpointsResult.setVpcEndpoints(vpcEndpoints);
        when(ec2Client.describeVpcEndpoints(anyObject())).thenReturn(describeVpcEndpointsResult);
        assertThat(inventoryUtil.fetchVpcInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
        describeVpcEndpointsResult = new DescribeVpcEndpointsResult();
        vpcEndpoints = new ArrayList<VpcEndpoint>();
        vpcEndpoint = new VpcEndpoint();
        vpcEndpoint.setPolicyDocument("{\"Statement\"[{\"Effect\":\"Allow\",\"Resource\":\"*\"}]}");
        vpcEndpoints.add(vpcEndpoint);
        describeVpcEndpointsResult.setVpcEndpoints(vpcEndpoints);
        when(ec2Client.describeVpcEndpoints(anyObject())).thenReturn(describeVpcEndpointsResult);
        assertThat(inventoryUtil.fetchVpcInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch vpc info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchVpcInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchVpcInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch api gateways test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchApiGatewaysTest() throws Exception {
        
        mockStatic(AmazonApiGatewayClientBuilder.class);
        AmazonApiGateway apiGatWayClient = PowerMockito.mock(AmazonApiGateway.class);
        AmazonApiGatewayClientBuilder amazonApiGatewayClientBuilder = PowerMockito.mock(AmazonApiGatewayClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonApiGatewayClientBuilder.standard()).thenReturn(amazonApiGatewayClientBuilder);
        when(amazonApiGatewayClientBuilder.withCredentials(anyObject())).thenReturn(amazonApiGatewayClientBuilder);
        when(amazonApiGatewayClientBuilder.withRegion(anyString())).thenReturn(amazonApiGatewayClientBuilder);
        when(amazonApiGatewayClientBuilder.build()).thenReturn(apiGatWayClient);
        
        GetRestApisResult getRestApisResult = new GetRestApisResult();
        List<RestApi> apiGateWaysList = new ArrayList<>();
        apiGateWaysList.add(new RestApi());
        getRestApisResult.setItems(apiGateWaysList);
        when(apiGatWayClient.getRestApis(anyObject())).thenReturn(getRestApisResult);
        assertThat(inventoryUtil.fetchApiGateways(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch api gateways test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchApiGatewaysTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchApiGateways(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch IAM users test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchIAMUsersTest() throws Exception {
        
        mockStatic(AmazonIdentityManagementClientBuilder.class);
        AmazonIdentityManagement iamClient = PowerMockito.mock(AmazonIdentityManagement.class);
        AmazonIdentityManagementClientBuilder amazonIdentityManagementClientBuilder = PowerMockito.mock(AmazonIdentityManagementClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonIdentityManagementClientBuilder.standard()).thenReturn(amazonIdentityManagementClientBuilder);
        when(amazonIdentityManagementClientBuilder.withCredentials(anyObject())).thenReturn(amazonIdentityManagementClientBuilder);
        when(amazonIdentityManagementClientBuilder.withRegion(anyString())).thenReturn(amazonIdentityManagementClientBuilder);
        when(amazonIdentityManagementClientBuilder.build()).thenReturn(iamClient);
        
        ListUsersResult listUsersResult = new ListUsersResult();
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUserName("name");
        users.add(user);
        listUsersResult.setUsers(users);
        when(iamClient.listUsers(anyObject())).thenReturn(listUsersResult);
        
        ListAccessKeysResult listAccessKeysResult = new ListAccessKeysResult();
        List<AccessKeyMetadata> accessKeyMetadataList = new ArrayList<>();
        AccessKeyMetadata accessKeyMetadata = new AccessKeyMetadata();
        accessKeyMetadata.setAccessKeyId("accessKeyId");
        accessKeyMetadataList.add(accessKeyMetadata);
        listAccessKeysResult.setAccessKeyMetadata(accessKeyMetadataList );
        when(iamClient.listAccessKeys(anyObject())).thenReturn(listAccessKeysResult);
        
        GetAccessKeyLastUsedResult getAccessKeyLastUsedResult = new GetAccessKeyLastUsedResult();
        AccessKeyLastUsed accessKeyLastUsed = new AccessKeyLastUsed();
        accessKeyLastUsed.setLastUsedDate(new Date());
        getAccessKeyLastUsedResult.setAccessKeyLastUsed(accessKeyLastUsed );
        when(iamClient.getAccessKeyLastUsed(anyObject())).thenReturn(getAccessKeyLastUsedResult);
        
        GetLoginProfileResult getLoginProfileResult = new GetLoginProfileResult();
        LoginProfile loginProfile = new LoginProfile();
        loginProfile.setCreateDate(new Date());
        loginProfile.setPasswordResetRequired(false);
        getLoginProfileResult.setLoginProfile(loginProfile );
        when(iamClient.getLoginProfile(anyObject())).thenReturn(getLoginProfileResult );
        
        ListGroupsForUserResult listGroupsForUserResult = new ListGroupsForUserResult();
        List<Group> groups = new ArrayList<>();
        Group group = new Group();
        group.setGroupName("groupName");
        groups.add(group);
        listGroupsForUserResult.setGroups(groups );
        when(iamClient.listGroupsForUser(anyObject())).thenReturn(listGroupsForUserResult );
        
        ListMFADevicesResult listMFADevicesResult = new ListMFADevicesResult();
        listMFADevicesResult.setMFADevices(new ArrayList<>());;
        when(iamClient.listMFADevices(anyObject())).thenReturn(listMFADevicesResult );
        
        assertThat(inventoryUtil.fetchIAMUsers(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"),
                "account","accountName").size(), is(1));
        
        listMFADevicesResult = new ListMFADevicesResult();
        List<MFADevice> mfaDevices = new ArrayList<>();
        mfaDevices.add(new MFADevice());
        listMFADevicesResult.setMFADevices(mfaDevices);
        when(iamClient.listMFADevices(anyObject())).thenReturn(listMFADevicesResult );
        
        assertThat(inventoryUtil.fetchIAMUsers(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"),
                "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch IAM roles test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchIAMRolesTest() throws Exception {
        
        mockStatic(AmazonIdentityManagementClientBuilder.class);
        AmazonIdentityManagement iamClient = PowerMockito.mock(AmazonIdentityManagement.class);
        AmazonIdentityManagementClientBuilder amazonIdentityManagementClientBuilder = PowerMockito.mock(AmazonIdentityManagementClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonIdentityManagementClientBuilder.standard()).thenReturn(amazonIdentityManagementClientBuilder);
        when(amazonIdentityManagementClientBuilder.withCredentials(anyObject())).thenReturn(amazonIdentityManagementClientBuilder);
        when(amazonIdentityManagementClientBuilder.withRegion(anyString())).thenReturn(amazonIdentityManagementClientBuilder);
        when(amazonIdentityManagementClientBuilder.build()).thenReturn(iamClient);
        
        ListRolesResult listRolesResult = new ListRolesResult();
        List<Role> roles = new ArrayList<>();
        roles.add(new Role());
        listRolesResult.setRoles(roles);
        when(iamClient.listRoles(anyObject())).thenReturn(listRolesResult);
        assertThat(inventoryUtil.fetchIAMRoles(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"),"account","accountName").size(), is(1));
    }
    
    /**
     * Fetch RDSDB snapshots test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRDSDBSnapshotsTest() throws Exception {
        
        mockStatic(AmazonRDSClientBuilder.class);
        AmazonRDS rdsClient = PowerMockito.mock(AmazonRDS.class);
        AmazonRDSClientBuilder amazonRDSClientBuilder = PowerMockito.mock(AmazonRDSClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonRDSClientBuilder.standard()).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withCredentials(anyObject())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.withRegion(anyString())).thenReturn(amazonRDSClientBuilder);
        when(amazonRDSClientBuilder.build()).thenReturn(rdsClient);
        
        DescribeDBSnapshotsResult describeDBSnapshotsResult = new DescribeDBSnapshotsResult();
        List<DBSnapshot> snapshots = new ArrayList<>();
        snapshots.add(new DBSnapshot());
        describeDBSnapshotsResult.setDBSnapshots(snapshots);
        when(rdsClient.describeDBSnapshots(anyObject())).thenReturn(describeDBSnapshotsResult);
        assertThat(inventoryUtil.fetchRDSDBSnapshots(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch RDSDB snapshots test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchRDSDBSnapshotsTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchRDSDBSnapshots(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch KMS keys test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({ "static-access", "unchecked" })
    @Test
    public void fetchKMSKeysTest() throws Exception {
        
        mockStatic(AWSKMSClientBuilder.class);
        AWSKMS awskms = PowerMockito.mock(AWSKMS.class);
        AWSKMSClientBuilder awsKMSClientBuilder = PowerMockito.mock(AWSKMSClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(awsKMSClientBuilder.standard()).thenReturn(awsKMSClientBuilder);
        when(awsKMSClientBuilder.withCredentials(anyObject())).thenReturn(awsKMSClientBuilder);
        when(awsKMSClientBuilder.withRegion(anyString())).thenReturn(awsKMSClientBuilder);
        when(awsKMSClientBuilder.build()).thenReturn(awskms);
        
        ListKeysResult listKeysResult = new ListKeysResult();
        List<KeyListEntry> regionKeys = new ArrayList<>();
        KeyListEntry keyListEntry = new KeyListEntry();
        keyListEntry.setKeyId("keyId");
        regionKeys.add(keyListEntry);
        listKeysResult.setKeys(regionKeys);
        when(awskms.listKeys()).thenReturn(listKeysResult);
        
        ListAliasesResult listAliasesResult = new ListAliasesResult();
        List<AliasListEntry> regionKeyAliases = new ArrayList<>();
        AliasListEntry aliasListEntry = new AliasListEntry();
        aliasListEntry.setTargetKeyId("keyId");
        regionKeyAliases.add(aliasListEntry);
        listAliasesResult.setAliases(regionKeyAliases);
        when(awskms.listAliases()).thenReturn(listAliasesResult);
        
        DescribeKeyResult describeKeyResult = new DescribeKeyResult();
        describeKeyResult.setKeyMetadata(new KeyMetadata());
        when(awskms.describeKey(anyObject())).thenReturn(describeKeyResult );
        
        ListResourceTagsResult listResourceTagsResult = new ListResourceTagsResult();
        listResourceTagsResult.setTags(new ArrayList<>());
        when(awskms.listResourceTags(anyObject())).thenReturn(listResourceTagsResult);
        
        GetKeyRotationStatusResult getKeyRotationStatusResult = new GetKeyRotationStatusResult();
        getKeyRotationStatusResult.setKeyRotationEnabled(true);
        when(awskms.getKeyRotationStatus(anyObject())).thenReturn(getKeyRotationStatusResult);
        assertThat(inventoryUtil.fetchKMSKeys(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
        listAliasesResult = new ListAliasesResult();
        listAliasesResult.setAliases(new ArrayList<>());
        when(awskms.listAliases()).thenReturn(listAliasesResult);
        
        when(awskms.describeKey(anyObject())).thenThrow(Exception.class );
        
        listResourceTagsResult = new ListResourceTagsResult();
        listResourceTagsResult.setTags(new ArrayList<>());
        when(awskms.listResourceTags(anyObject())).thenReturn(listResourceTagsResult);
        
        when(awskms.getKeyRotationStatus(anyObject())).thenThrow(Exception.class );
        assertThat(inventoryUtil.fetchKMSKeys(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
        listAliasesResult = new ListAliasesResult();
        regionKeyAliases = new ArrayList<>();
        aliasListEntry = new AliasListEntry();
        aliasListEntry.setTargetKeyId("id");
        regionKeyAliases.add(aliasListEntry);
        listAliasesResult.setAliases(regionKeyAliases);
        when(awskms.listAliases()).thenReturn(listAliasesResult);

        assertThat(inventoryUtil.fetchKMSKeys(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch KMS keys test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({ "static-access"})
    @Test
    public void fetchKMSKeysTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchKMSKeys(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch cloud front info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchCloudFrontInfoTest() throws Exception {
        
        mockStatic(AmazonCloudFrontClientBuilder.class);
        AmazonCloudFront amazonCloudFront = PowerMockito.mock(AmazonCloudFront.class);
        AmazonCloudFrontClientBuilder amazonCloudFrontClientBuilder = PowerMockito.mock(AmazonCloudFrontClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(amazonCloudFrontClientBuilder.standard()).thenReturn(amazonCloudFrontClientBuilder);
        when(amazonCloudFrontClientBuilder.withCredentials(anyObject())).thenReturn(amazonCloudFrontClientBuilder);
        when(amazonCloudFrontClientBuilder.withRegion(anyString())).thenReturn(amazonCloudFrontClientBuilder);
        when(amazonCloudFrontClientBuilder.build()).thenReturn(amazonCloudFront);
        
        ListDistributionsResult listDistributionsResult = new ListDistributionsResult();
        List<DistributionSummary> distributionSummaries = new ArrayList<>();
        DistributionSummary distributionSummary = new DistributionSummary();
        distributionSummary.setARN("aRN");
        distributionSummaries.add(distributionSummary);
        DistributionList distributionList = new DistributionList();
        distributionList.setItems(distributionSummaries);
        listDistributionsResult.setDistributionList(distributionList);
        when(amazonCloudFront.listDistributions(anyObject())).thenReturn(listDistributionsResult);
        
        com.amazonaws.services.cloudfront.model.ListTagsForResourceResult listTagsForResourceResult = new com.amazonaws.services.cloudfront.model.ListTagsForResourceResult();
        Tags tags = new Tags();
        tags.setItems(new ArrayList<>());
        listTagsForResourceResult.setTags(tags );
        when(amazonCloudFront.listTagsForResource(anyObject())).thenReturn(listTagsForResourceResult );
        assertThat(inventoryUtil.fetchCloudFrontInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch cloud front info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({ "static-access"})
    @Test
    public void fetchCloudFrontInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchCloudFrontInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch EBS info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchEBSInfoTest() throws Exception {
        
        mockStatic(AWSElasticBeanstalkClientBuilder.class);
        AWSElasticBeanstalk awsElasticBeanstalk = PowerMockito.mock(AWSElasticBeanstalk.class);
        AWSElasticBeanstalkClientBuilder awsElasticBeanstalkClientBuilder = PowerMockito.mock(AWSElasticBeanstalkClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(awsElasticBeanstalkClientBuilder.standard()).thenReturn(awsElasticBeanstalkClientBuilder);
        when(awsElasticBeanstalkClientBuilder.withCredentials(anyObject())).thenReturn(awsElasticBeanstalkClientBuilder);
        when(awsElasticBeanstalkClientBuilder.withRegion(anyString())).thenReturn(awsElasticBeanstalkClientBuilder);
        when(awsElasticBeanstalkClientBuilder.build()).thenReturn(awsElasticBeanstalk);
        
        DescribeApplicationsResult describeApplicationsResult = new DescribeApplicationsResult();
        List<ApplicationDescription> applicationDescriptions = new ArrayList<>();
        ApplicationDescription applicationDescription = new ApplicationDescription();
        applicationDescription.setApplicationName("applicationName");
        applicationDescriptions.add(applicationDescription);
        describeApplicationsResult.setApplications(applicationDescriptions);
        when(awsElasticBeanstalk.describeApplications()).thenReturn(describeApplicationsResult);
        
        DescribeEnvironmentsResult describeEnvironmentsResult = new DescribeEnvironmentsResult();
        List<EnvironmentDescription> environments = new ArrayList<>();
        EnvironmentDescription environmentDescription = new EnvironmentDescription();
        environmentDescription.setEnvironmentId("environmentId");
        environments.add(environmentDescription);
        describeEnvironmentsResult.setEnvironments(environments );
        when(awsElasticBeanstalk.describeEnvironments(anyObject())).thenReturn(describeEnvironmentsResult );
        
        DescribeEnvironmentResourcesResult describeEnvironmentResourcesResult = new DescribeEnvironmentResourcesResult();
        describeEnvironmentResourcesResult.setEnvironmentResources(new EnvironmentResourceDescription());
        when(awsElasticBeanstalk.describeEnvironmentResources(anyObject())).thenReturn(describeEnvironmentResourcesResult);
        assertThat(inventoryUtil.fetchEBSInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
        
        describeEnvironmentsResult = new DescribeEnvironmentsResult();
        describeEnvironmentsResult.setEnvironments(new ArrayList<>() );
        when(awsElasticBeanstalk.describeEnvironments(anyObject())).thenReturn(describeEnvironmentsResult );
        
        assertThat(inventoryUtil.fetchEBSInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch EBS info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({ "static-access"})
    @Test
    public void fetchEBSInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchEBSInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "skipRegions", "account","accountName").size(), is(0));
    }
    
    /**
     * Fetch PHD info test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void fetchPHDInfoTest() throws Exception {
        
        mockStatic(AWSHealthClientBuilder.class);
        AWSHealth awsHealth = PowerMockito.mock(AWSHealth.class);
        AWSHealthClientBuilder awsHealthClientBuilder = PowerMockito.mock(AWSHealthClientBuilder.class);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = PowerMockito.mock(AWSStaticCredentialsProvider.class);
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenReturn(awsStaticCredentialsProvider);
        when(awsHealthClientBuilder.standard()).thenReturn(awsHealthClientBuilder);
        when(awsHealthClientBuilder.withCredentials(anyObject())).thenReturn(awsHealthClientBuilder);
        when(awsHealthClientBuilder.withRegion(anyString())).thenReturn(awsHealthClientBuilder);
        when(awsHealthClientBuilder.build()).thenReturn(awsHealth);
        
        DescribeEventsResult describeEventsResult = new DescribeEventsResult();
        List<Event> resultEvents = new ArrayList<>();
        Event event = new Event();
        event.setArn("arn");
        resultEvents.add(event);
        describeEventsResult.setEvents(resultEvents);
        when(awsHealth.describeEvents(anyObject())).thenReturn(describeEventsResult);
        
        DescribeEventDetailsResult describeEventDetailsResult = new DescribeEventDetailsResult();
        List<EventDetails> successfulEventDetails = new ArrayList<>();
        EventDetails eventDetails = new EventDetails();
        eventDetails.setEvent(event);
        successfulEventDetails.add(eventDetails);
        describeEventDetailsResult.setSuccessfulSet(successfulEventDetails);
        when(awsHealth.describeEventDetails(anyObject())).thenReturn(describeEventDetailsResult );
        
        DescribeAffectedEntitiesResult affectedEntitiesResult = new DescribeAffectedEntitiesResult();
        List<AffectedEntity> affectedEntities = new ArrayList<>();
        affectedEntities.add(new AffectedEntity());
        affectedEntitiesResult.setEntities(new ArrayList<>());
        when(awsHealth.describeAffectedEntities(anyObject())).thenReturn(affectedEntitiesResult);
        assertThat(inventoryUtil.fetchPHDInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "account","accountName").size(), is(1));
    }
    
    /**
     * Fetch PHD info test exception.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings({ "static-access"})
    @Test
    public void fetchPHDInfoTest_Exception() throws Exception {
        
        PowerMockito.whenNew(AWSStaticCredentialsProvider.class).withAnyArguments().thenThrow(new Exception());
        assertThat(inventoryUtil.fetchPHDInfo(new BasicSessionCredentials("awsAccessKey", "awsSecretKey", "sessionToken"), 
                "account","accountName").size(), is(0));
    }
    
    /**
     * Gets the regions.
     *
     * @return the regions
     */
    private List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        Region region = new Region(new RegionImpl() {
            
            @Override
            public boolean isServiceSupported(String serviceName) {
                return false;
            }
            
            @Override
            public boolean hasHttpsEndpoint(String serviceName) {
                return false;
            }
            
            @Override
            public boolean hasHttpEndpoint(String serviceName) {
                return false;
            }
            
            @Override
            public String getServiceEndpoint(String serviceName) {
                return null;
            }
            
            @Override
            public String getPartition() {
                return null;
            }
            
            @Override
            public String getName() {
                return "north";
            }
            
            @Override
            public String getDomain() {
                return null;
            }
            
            @Override
            public Collection<String> getAvailableEndpoints() {
                return null;
            }
        });
        regions.add(region);
        return regions;
    }
}
