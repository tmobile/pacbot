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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.directconnect.model.Connection;
import com.amazonaws.services.directconnect.model.VirtualInterface;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.CustomerGateway;
import com.amazonaws.services.ec2.model.DhcpOptions;
import com.amazonaws.services.ec2.model.EgressOnlyInternetGateway;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.NatGateway;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.ec2.model.ReservedInstances;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VpcPeeringConnection;
import com.amazonaws.services.ec2.model.VpnConnection;
import com.amazonaws.services.ec2.model.VpnGateway;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;
import com.amazonaws.services.elasticmapreduce.model.Cluster;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.simplesystemsmanagement.model.InstanceInformation;
import com.amazonaws.services.sns.model.Subscription;
import com.tmobile.cso.pacman.inventory.vo.BucketVH;
import com.tmobile.cso.pacman.inventory.vo.CheckVH;
import com.tmobile.cso.pacman.inventory.vo.ClassicELBVH;
import com.tmobile.cso.pacman.inventory.vo.CloudFrontVH;
import com.tmobile.cso.pacman.inventory.vo.DBClusterVH;
import com.tmobile.cso.pacman.inventory.vo.DBInstanceVH;
import com.tmobile.cso.pacman.inventory.vo.DynamoVH;
import com.tmobile.cso.pacman.inventory.vo.EbsVH;
import com.tmobile.cso.pacman.inventory.vo.EfsVH;
import com.tmobile.cso.pacman.inventory.vo.ElastiCacheVH;
import com.tmobile.cso.pacman.inventory.vo.ElasticsearchDomainVH;
import com.tmobile.cso.pacman.inventory.vo.ErrorVH;
import com.tmobile.cso.pacman.inventory.vo.KMSKeyVH;
import com.tmobile.cso.pacman.inventory.vo.LambdaVH;
import com.tmobile.cso.pacman.inventory.vo.LoadBalancerVH;
import com.tmobile.cso.pacman.inventory.vo.PhdVH;
import com.tmobile.cso.pacman.inventory.vo.Resource;
import com.tmobile.cso.pacman.inventory.vo.SGRuleVH;
import com.tmobile.cso.pacman.inventory.vo.TargetGroupVH;
import com.tmobile.cso.pacman.inventory.vo.UserVH;
import com.tmobile.cso.pacman.inventory.vo.VpcVH;

/**
 * The Class FileManager.
 */
public class FileManager {
    
    /**
     * Instantiates a new file manager.
     */
    private FileManager() {
        
    }
	
	/**
	 * Initialise.
	 *
	 * @param folderName the folder name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void initialise(String folderName) throws IOException{
		FileGenerator.folderName = folderName;
		new File(folderName).mkdirs();
		
		String fieldNames ="";
		
		fieldNames = "loaddate`accountid`instanceId`amiLaunchIndex`architecture`clientToken`ebsOptimized`EnaSupport`Hypervisor`ImageId`InstanceLifecycle`InstanceType`KernelId`KeyName`LaunchTime`Platform`PrivateDnsName`PrivateIpAddress`PublicDnsName`PublicIpAddress`RamdiskId`RootDeviceName`RootDeviceType`SourceDestCheck`SpotInstanceRequestId`SriovNetSupport`StateTransitionReason`SubnetId`VirtualizationType`VpcId`IamInstanceProfileArn`IamInstanceProfileId`Monitoring.State`Placement.Affinity`Placement.AvailabilityZone`Placement.GroupName`Placement.HostId`Placement.Tenancy`State.Name`State.Code`StateReason.Message`StateReason.Code\n";
		FileGenerator.writeToFile("instance-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`instanceId`tags.key`tags.value\n";
		FileGenerator.writeToFile("instance-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`instanceId`SecurityGroups.groupId`SecurityGroups.groupName\n";
		FileGenerator.writeToFile("instance-secgroups.data", fieldNames, false);
	
		fieldNames = "loaddate`accountid`instanceId`ProductCodes.ProductCodeId`ProductCodes.ProductCodeType\n";
		FileGenerator.writeToFile("instance-productcodes.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`instanceId`deviceName`ebs.VolumeId`ebs.AttachTime`ebs.DeleteOnTermination`ebs.status\n";
		FileGenerator.writeToFile("instance-blockdevices.data", fieldNames, false);
	
		fieldNames = "loaddate`accountid`instanceId`NetworkInterfaceId`NetworkInterfaceDescription\n";
		FileGenerator.writeToFile("instance-nwinterfaces.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`NetworkInterfaceId`Description`MacAddress`OwnerId`PrivateDnsName`PrivateIpAddress`SourceDestCheck`Status`SubnetId`VpcId`association.IpOwnerId`association.PublicDnsName`association.PublicIp`attachment.AttachmentId`attachment.AttachTime`attachment.DeleteOnTermination`attachment.DeviceIndex`attachment.status\n";
		FileGenerator.writeToFile("nwinterface-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`NetworkInterfaceId`groups.GroupId`groups.GroupName\n";
		FileGenerator.writeToFile("nwinterface-secgroups.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`NetworkInterfaceId`Ipv6Addresses.Ipv6Address\n";
		FileGenerator.writeToFile("nwinterface-ipv6.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`NetworkInterfaceId`PrivateIpAddresses.Primary`PrivateIpAddresses.PrivateDnsName`PrivateIpAddresses.PrivateIpAddress`PrivateIpAddresses.association.IpOwnerId`PrivateIpAddresses.association.PublicDnsName`PrivateIpAddresses.association.PublicIp\n";
		FileGenerator.writeToFile("nwinterface-privateIpAddr.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`AutoScalingGroupARN`AutoScalingGroupName`AvailabilityZones`CreatedTime`DefaultCooldown`DesiredCapacity`HealthCheckGracePeriod`HealthCheckType`LaunchConfigurationName`MaxSize`MinSize`NewInstancesProtectedFromScaleIn`PlacementGroup`Status`SuspendedProcesses`TargetGroupARNs`TerminationPolicies`VPCZoneIdentifier\n";
		FileGenerator.writeToFile("asg-info.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`AutoScalingGroupARN`instances.instanceid\n";
		FileGenerator.writeToFile("asg-instances.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`AutoScalingGroupARN`LoadBalancerNames\n";
		FileGenerator.writeToFile("asg-elb.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`AutoScalingGroupARN`tags.key`tags.value\n";
		FileGenerator.writeToFile("asg-tags.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`StackId`StackName`ChangeSetId`CreationTime`Description`DisableRollback`LastUpdatedTime`RoleARN`StackStatus`StackStatusReason`TimeoutInMinutes\n";
		FileGenerator.writeToFile("cloudfrmnstack-info.data", fieldNames, false);
		fieldNames ="loaddate`accountid`StackId`tags.key`tags.value\n";
		FileGenerator.writeToFile("cloudfrmnstack-tags.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`table.TableArn`table.TableName`table.CreationDateTime`table.ItemCount`table.LatestStreamArn`table.LatestStreamLabel`table.TableSizeBytes`table.TableStatus`table.ProvisionedThroughput.ReadCapacityUnits`table.ProvisionedThroughput.WriteCapacityUnits`table.StreamSpecification.StreamEnabled`table.StreamSpecification.StreamViewType\n";
		FileGenerator.writeToFile("dynamodb-tables.data", fieldNames, false);
		fieldNames ="loaddate`accountid`table.TableArn`tags.key`tags.value\n";
		FileGenerator.writeToFile("dynamodb-tables-tags.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`efs.FileSystemId`efs.Name`efs.CreationTime`efs.CreationToken`efs.LifeCycleState`efs.NumberOfMountTargets`efs.OwnerId`efs.PerformanceMode\n";
		FileGenerator.writeToFile("efs-info.data", fieldNames, false);
		fieldNames ="loaddate`accountid`efs.FileSystemId`tags.key`tags.value\n";
		FileGenerator.writeToFile("efs-tags.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`Id`AutoScalingRole`AutoTerminate`InstanceCollectionType`LogUri`MasterPublicDnsName`Name`NormalizedInstanceHours`ReleaseLabel`RequestedAmiVersion`RunningAmiVersion`ScaleDownBehavior`SecurityConfiguration`ServiceRole`TerminationProtected`VisibleToAllUsers\n";
		FileGenerator.writeToFile("emr-info.data", fieldNames, false);
		fieldNames ="loaddate`accountid`Id`tags.key`tags.value\n";
		FileGenerator.writeToFile("emr-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`FunctionArn`CodeSha256`CodeSize`Description`FunctionName`Handler`KMSKeyArn`LastModified`MemorySize`Role`Runtime`Timeout`Version`VpcConfig.VpcId`VpcConfig.SubnetIds`VpcConfig.SecurityGroupIds\n";
		FileGenerator.writeToFile("lambda-info.data", fieldNames, false);
		fieldNames ="loaddate`accountid`FunctionArn`tags.key`tags.value\n";
		FileGenerator.writeToFile("lambda-tags.data", fieldNames, false);
		fieldNames ="loaddate`FunctionArn`SecurityGroups\n";
		FileGenerator.writeToFile("lambda-secgroups.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`DNSName`AvailabilityZones`CanonicalHostedZoneName`CanonicalHostedZoneNameID`CreatedTime`LoadBalancerName`Scheme`VPCId\n";
		FileGenerator.writeToFile("classicelb-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`LoadBalancerName`Instances.InstanceId\n";
		FileGenerator.writeToFile("classicelb-instances.data", fieldNames, false);	
		fieldNames ="loaddate`accountid`LoadBalancerName`tags.key`tags.value\n";
		FileGenerator.writeToFile("classicelb-tags.data", fieldNames, false);
		fieldNames ="loaddate`LoadBalancerName`SecurityGroups\n";
		FileGenerator.writeToFile("classicelb-secgroups.data",fieldNames,false);
		
		fieldNames = "loaddate`accountid`appelb.LoadBalancerArn`appelb.DNSName`appelb.CanonicalHostedZoneID`appelb.CreatedTime`appelb.LoadBalancerName`appelb.Scheme`appelb.VPCId`AvailabilityZones`type\n";
		FileGenerator.writeToFile("appelb-info.data", fieldNames, false);
		fieldNames ="loaddate`accountid`LoadBalancerName`tags.key`tags.value\n";
		FileGenerator.writeToFile("appelb-tags.data", fieldNames, false);
		fieldNames = "loaddate`accountid`appElb.LoadBalancerArn`Instances.InstanceId\n";
		FileGenerator.writeToFile("appelb-instances.data", fieldNames, false);
		fieldNames ="loaddate`LoadBalancerName`SecurityGroups\n";
		FileGenerator.writeToFile("appelb-secgroups.data",fieldNames,false);
		
		fieldNames = "loaddate`accountid`trgtGrp.TargetGroupArn`trgtGrp.TargetGroupName`trgtGrp.vpcid`trgtGrp.protocol`trgtGrp.port`trgtGrp.HealthyThresholdCount`trgtGrp.UnhealthyThresholdCount`trgtGrp.HealthCheckIntervalSeconds`trgtGrp.HealthCheckTimeoutSeconds`trgtGrp.LoadBalancerArns\n";
		FileGenerator.writeToFile("targetGroup-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`trgtGrp.TargetGroupArn`targets.target.id\n";
		FileGenerator.writeToFile("targetGroup-instances.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`NatGatewayId`VpcId`SubnetId`State`CreateTime`DeleteTime`FailureCode`FailureMessage\n";
		FileGenerator.writeToFile("natgateway-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`NatGatewayId`NatGatewayAddresses.NetworkInterfaceId`NatGatewayAddresses.PrivateIp`NatGatewayAddresses.PublicIp`NatGatewayAddresses.AllocationId\n";
		FileGenerator.writeToFile("natgateway-addresses.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`DBClusterArn`AllocatedStorage`AvailabilityZones`BackupRetentionPeriod`CharacterSetName`ClusterCreateTime`DatabaseName`DBClusterIdentifier`DBClusterParameterGroup"
				+ "`DbClusterResourceId`DBSubnetGroup`EarliestRestorableTime`Endpoint`Engine`EngineVersion`HostedZoneId`IAMDatabaseAuthenticationEnabled"
				+ "`KmsKeyId`LatestRestorableTime`MasterUsername`MultiAZ`PercentProgress`Port`PreferredBackupWindow`PreferredMaintenanceWindow`ReaderEndpoint"
				+ "`ReadReplicaIdentifiers`ReplicationSourceIdentifier`Status`StorageEncrypted\n";
		FileGenerator.writeToFile("rdscluster-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`DBClusterArn`VpcSecurityGroups.VpcSecurityGroupId`VpcSecurityGroups.status\n";
		FileGenerator.writeToFile("rdscluster-vpcsecgroup.data", fieldNames, false);
		

		fieldNames = "loaddate`accountid`DBClusterArn`tags.key`tags.value\n";
		FileGenerator.writeToFile("rdscluster-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`DBInstanceArn`AllocatedStorage`AutoMinorVersionUpgrade`AvailabilityZone`BackupRetentionPeriod`CACertificateIdentifier`CharacterSetName`CopyTagsToSnapshot"
				+ "`DBClusterIdentifier`DBInstanceClass`DBInstanceIdentifier`DbInstancePort`DBInstanceStatus`DbiResourceId`DBName`Endpoint.Address`Endpoint.Port`Endpoint.HostedZoneID"
				+ "`Engine`EngineVersion`EnhancedMonitoringResourceArn`IAMDatabaseAuthenticationEnabled`InstanceCreateTime`Iops`KmsKeyId`LatestRestorableTime`LicenseModel`MasterUsername`MonitoringInterval"
				+ "`MonitoringRoleArn`MultiAZ`PreferredBackupWindow`PreferredMaintenanceWindow`PromotionTier`PubliclyAccessible`SecondaryAvailabilityZone`StorageEncrypted`StorageType`TdeCredentialArn`Timezone`ReadReplicaDBClusterIdentifiers`ReadReplicaDBInstanceIdentifiers`ReadReplicaSourceDBInstanceIdentifier\n";
		FileGenerator.writeToFile("rdsinstance-info.data", fieldNames, false);
	
		fieldNames = "loaddate`accountid`DBInstanceArn`VpcSecurityGroups.VpcSecurityGroupId`VpcSecurityGroups.status\n";
		FileGenerator.writeToFile("rdsinstance-vpcsecgroup.data", fieldNames, false);
	
		fieldNames = "loaddate`accountid`DBInstanceArn`tags.key`tags.value\n";
		FileGenerator.writeToFile("rdsinstance-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`bucket.Name`bucket.CreationDate`bucket.owner.displayname`bucket.owner.id`versionStatus`mfaDelete`location\n";
		FileGenerator.writeToFile("s3-info.data", fieldNames, false);
		fieldNames = "region`Name`tags.key`tags.value\n";
		FileGenerator.writeToFile("s3-tags.data", fieldNames, false);
		
	
		fieldNames = "loaddate`accountid`GroupId`Description`GroupName`OwnerId`vpcid\n";
		FileGenerator.writeToFile("secgroup-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`GroupId`tags.key`tags.value\n";
		FileGenerator.writeToFile("secgroup-tags.data", fieldNames, false);
		fieldNames = "loaddate`accountid`region`groupId`type`ipProtocol`fromPort`toPort`cidrIp`cidrIpv6\n";
		FileGenerator.writeToFile("secgroup-rules.data", fieldNames, false);
		

		fieldNames = "loaddate`accountid`SubnetId`AssignIpv6AddressOnCreation`AvailabilityZone`AvailableIpAddressCount`CidrBlock`DefaultForAz`MapPublicIpOnLaunch`State`VpcId\n";
		FileGenerator.writeToFile("subnet-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`SubnetId`tags.key`tags.value\n";
		FileGenerator.writeToFile("subnet-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`check.Id`check.Category`status`check.name`check.Description\n";
		FileGenerator.writeToFile("checks-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`checkid`id`status`data\n";
		FileGenerator.writeToFile("checks-resources-info.data", fieldNames, false);
		
		
		fieldNames = "loaddate`accountid`ClusterIdentifier`AllowVersionUpgrade`AutomatedSnapshotRetentionPeriod`AvailabilityZone`ClusterCreateTime`ClusterIdentifier`ClusterPublicKey`ClusterRevisionNumber`ClusterStatus`ClusterSubnetGroupName`ClusterVersion`DBName`ElasticIpStatus`Encrypted`Endpoint.Address`Endpoint.Port`EnhancedVpcRouting`KmsKeyId`MasterUsername`ModifyStatus`NodeType`NumberOfNodes`PreferredMaintenanceWindow`PubliclyAccessible`VpcId\n";
		FileGenerator.writeToFile("redshift-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`ClusterIdentifier`VpcSecurityGroups.VpcSecurityGroupId`VpcSecurityGroups.status\n";
		FileGenerator.writeToFile("redshfit-secgroup.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`ClusterIdentifier`tags.key`tags.value\n";
		FileGenerator.writeToFile("redshfit-tags.data", fieldNames, false);

		fieldNames = "loaddate`accountid`VolumeId`VolumeType`AvailabilityZone`CreateTime`Encrypted`Iops`KmsKeyId`Size`SnapshotId`State\n";
		FileGenerator.writeToFile("volume-info.data", fieldNames, false);

		fieldNames = "loaddate`accountid`VolumeId`attachments.InstanceId`attachments.AttachTime`attachments.DeleteOnTermination`attachments.Device`attachments.State\n";
		FileGenerator.writeToFile("volume-attachment.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`VolumeId`tags.key`tags.value\n";
		FileGenerator.writeToFile("volume-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`SnapshotId`Description`VolumeId`VolumeSize`Encrypted`DataEncryptionKeyId`KmsKeyId`OwnerAlias`OwnerId`Progress`StartTime`State`StateMessage`CreateVolumePublicAccess\n";
		FileGenerator.writeToFile("snapshot-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`SnapshotId`tags.key`tags.value\n";
		FileGenerator.writeToFile("snapshot-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`VpcId`CidrBlock`DhcpOptionsId`InstanceTenancy`IsDefault`State`cidrBlockAssociationSet.cidrBlock`cidrBlockAssociationSet.cidrBlockState.state`cidrBlockAssociationSet.cidrBlockState.statusMessage`cidrBlockAssociationSet.associationId"
				+ "`ipv6CidrBlockAssociationSet.ipv6CidrBlock`ipv6CidrBlockAssociationSet.ipv6CidrBlockState.state`ipv6CidrBlockAssociationSet.ipv6CidrBlockState.statusMessage`ipv6CidrBlockAssociationSet.associationId\n";
		FileGenerator.writeToFile("vpc-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`VpcId`tags.key`tags.value\n";
		FileGenerator.writeToFile("vpc-tags.data", fieldNames, false);
		fieldNames = "loaddate`accountid`VpcId`VpcEndpointId`serviceName`state`creationTimestamp`publicAccess`policyDocument`routeTableIds\n";
		FileGenerator.writeToFile("vpc-endpoints.data", fieldNames, false);
		
		
		fieldNames = "loaddate`accountid`user.username`user.userid`user.arn`user.CreateDate`user.path`passwordCreationDate`user.PasswordLastUsed`passwordResetRequired`mfa`groups\n";
		FileGenerator.writeToFile( "iamuser-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`user.username`accessKeys.AccessKeyId`accessKeys.CreateDate`accessKeys.status`accessKeys.lastUsedDate\n";
		FileGenerator.writeToFile( "iamuser-accesskeys.data", fieldNames, false);
		
		
		fieldNames = "loaddate`accountid`region`DBSnapshotIdentifier`DBSnapshotArn`DBInstanceIdentifier`Status`snapshotCreateTime`snapshotType`encrypted`engine`allocatedStorage`port`availabilityZone`vpcId`instanceCreateTime`masterUsername`engineVersion`licenseModel`iops`optionGroupName`percentProgress`sourceRegion`sourceDBSnapshotIdentifier`storageType`tdeCredentialArn`kmsKeyId`timezone`iAMDatabaseAuthenticationEnabled`publicAccess\n";
		FileGenerator.writeToFile( "rdssnapshot-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`roleName`roleId`arn`description`path`createDate`assumeRolePolicyDocument\n";
		FileGenerator.writeToFile( "iamrole-info.data", fieldNames, false);
		
		
		
		fieldNames = "loaddate`key.keyId`key.arn`key.creationDate`key.aWSAccountId`key.description`key.keyState`key.enabled`key.keyUsage`key.deletionDate`key.validTo`"
				+"rotationStatus`alias.aliasName`alias.aliasArn\n";
		FileGenerator.writeToFile("kms-info.data", fieldNames, false);
		fieldNames = "loaddate`key.keyId`tags.tagKey`tags.tagValue\n";
		FileGenerator.writeToFile("kms-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`distSummary.id`distSummary.aRN`distSummary.status`distSummary.lastModifiedTime`distSummary.domainName`distSummary.enabled"
				+"`distSummary.comment`distSummary.priceClass`distSummary.webACLId`distSummary.httpVersion`distSummary.isIPV6Enabled`viewerCertificate.iAMCertificateId"
				+"`viewerCertificate.aCMCertificateArn`viewerCertificate.cloudFrontDefaultCertificate`viewerCertificate.sSLSupportMethod`viewerCertificate.minimumProtocolVersion`aliases\n";
		FileGenerator.writeToFile("cloudfront-info.data", fieldNames, false);
		fieldNames = "loaddate`distSummary.id`tags.key`tags.value\n";
		FileGenerator.writeToFile("cloudfront-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`app.applicationName`app.description`app.dateCreated`app.dateUpdated`env.environmentName`env.environmentId`env.versionLabel`env.solutionStackName"
				+"`env.platformArn`env.templateName`env.description`env.endpointURL`env.cNAME`env.dateCreated`env.dateUpdated`env.status`env.abortableOperationInProgress"
				+"`env.health`env.healthStatus\n";
		FileGenerator.writeToFile("ebs-info.data", fieldNames, false);
		fieldNames = "loaddate`app.applicationName`env.environmentId`envResource.instances.id\n";
		FileGenerator.writeToFile("ebs-instances.data",fieldNames,false);
		fieldNames = "loaddate`app.applicationName`env.environmentId`envResource.autoScalingGroups.name\n";
		FileGenerator.writeToFile("ebs-asg.data",fieldNames,false);
		fieldNames = "loaddate`app.applicationName`env.environmentId`envResource.loadBalancers.name\n";
		FileGenerator.writeToFile("ebs-elb.data",fieldNames,false);
		
		
		fieldNames = "loaddate`event.arn`event.service`eventTypeCode`eventTypeCategory`event.region`event.availabilityZone`event.startTime`event.endTime`"
				+"event.lastUpdatedTime`event.statusCode`eventDescription.latestDescription`eventMetadata\n";
		FileGenerator.writeToFile("phd-info.data", fieldNames, false);
		fieldNames = "loaddate`affectedEntities.eventArn`affectedEntities.entityArn`affectedEntities.awsAccountId`affectedEntities.entityValue`"
				+"affectedEntities.lastUpdatedTime`affectedEntities.statusCode`affectedEntities.tags\n";
		FileGenerator.writeToFile("phd-affectedentities.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`type`region`exception\n";
		FileGenerator.writeToFile("load-error.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`routeTableId`vpcId\n";
		FileGenerator.writeToFile("routetable.data", fieldNames, false);
		fieldNames = "loaddate`accountid`routeTableId`routes.destinationCidrBlock`routes.destinationPrefixListId`routes.gatewayId`routes.instanceId`routes.instanceOwnerId`routes.networkInterfaceId`routes.vpcPeeringConnectionId`routes.natGatewayId"
				+"`routes.state`routes.origin`routes.destinationIpv6CidrBlock`routes.egressOnlyInternetGatewayId\n";
		FileGenerator.writeToFile("routetable-routes.data", fieldNames, false);
		fieldNames = "loaddate`accountid`routeTableId`associations.routeTableAssociationId`associations.subnetId`associations.main\n";
		FileGenerator.writeToFile("routetable-associations.data", fieldNames, false);
		fieldNames = "loaddate`accountid`routeTableId`propagatingVgws.gatewayId\n";
		FileGenerator.writeToFile("routetable-propagatingVgws.data", fieldNames, false);
		fieldNames = "loaddate`accountid`routeTableId`vpcId`tags.key`tags.value\n";
		FileGenerator.writeToFile("routetable-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`networkAclId`vpcId`isDefault\n";
		FileGenerator.writeToFile("networkacl.data", fieldNames, false);
		fieldNames = "loaddate`accountid`networkAclId`entries.ruleNumber`entries.protocol`entries.ruleAction`entries.egress`entries.cidrBlock`entries.ipv6CidrBlock`entries.icmpTypeCode.type`entries.icmpTypeCode.code"
				+"`entries.portRange.from`entries.portRange.to\n";
		FileGenerator.writeToFile("networkacl-entries.data", fieldNames, false);
		fieldNames = "loaddate`accountid`networkAclId`associations.networkAclAssociationId`associations.networkAclId`associations.subnetId\n";
		FileGenerator.writeToFile("networkacl-associations.data", fieldNames, false);
		fieldNames = "loaddate`accountid`networkAclId`vpcId`tags.key`tags.value\n";
		FileGenerator.writeToFile("networkacl-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`instanceId`publicIp`allocationId`associationId`domain`networkInterfaceId`networkInterfaceOwnerId`privateIpAddress\n";
		FileGenerator.writeToFile("elasticip.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`launchConfigurationName`imageId`keyName`classicLinkVPCId`userData`instanceType`kernelId`ramdiskId`spotPrice`iamInstanceProfile`createdTime`ebsOptimized`associatePublicIpAddress`placementTenancy"
				+"`securityGroups`classicLinkVPCSecurityGroups`instanceMonitoring.enabled\n";
		FileGenerator.writeToFile("asg-launchconfig.data", fieldNames, false);
		fieldNames = "loaddate`accountid`launchConfigurationName`blockDeviceMappings.virtualName`blockDeviceMappings.deviceName`blockDeviceMappings.ebs.snapshotId`blockDeviceMappings.ebs.volumeSize"
				+"`blockDeviceMappings.ebs.volumeType`blockDeviceMappings.ebs.deleteOnTermination`blockDeviceMappings.ebs.iops`blockDeviceMappings.ebs.encrypted`blockDeviceMappings.noDevice\n";
		FileGenerator.writeToFile("asg-launchconfig-blockDeviceMappings.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`internetGatewayId`attachments.vpcId`attachments.state\n";
		FileGenerator.writeToFile("internetgateway-attachments.data", fieldNames, false);
		fieldNames = "loaddate`accountid`internetGatewayId\n";
		FileGenerator.writeToFile("internetgateway.data", fieldNames, false);
		fieldNames = "loaddate`accountid`internetGatewayId`tags.key`tags.value\n";
		FileGenerator.writeToFile("internetgateway-tags.data", fieldNames, false);

		fieldNames = "loaddate`accountid`vpnGatewayId`state`type`availabilityZone\n";
		FileGenerator.writeToFile("vpngateway.data", fieldNames, false);
		fieldNames = "loaddate`accountid`vpnGatewayId`vpcAttachments.vpcId`vpcAttachments.state\n";
		FileGenerator.writeToFile("vpngateway-vpcAttachments.data", fieldNames, false);
		fieldNames = "loaddate`accountid`vpnGatewayId`tags.key`tags.value\n";
		FileGenerator.writeToFile("vpngateway-tags.data", fieldNames, false );
		
		fieldNames = "loaddate`accountid`autoScalingGroupName`policyName`policyARN`policyType`adjustmentType`minAdjustmentStep`minAdjustmentMagnitude`scalingAdjustment`cooldown`metricAggregationType`estimatedInstanceWarmup\n";
		FileGenerator.writeToFile("asg-scalingpolicy.data", fieldNames, false);
		fieldNames = "loaddate`accountid`policyName`stepAdjustments.metricIntervalLowerBound`stepAdjustments.metricIntervalUpperBound`stepAdjustments.scalingAdjustment\n";
		FileGenerator.writeToFile("asg-scalingpolicy-stepAdjustments.data", fieldNames, false);
		fieldNames = "loaddate`accountid`policyName`alarms.alarmName`alarms.alarmARN\n";
		FileGenerator.writeToFile("asg-scalingpolicy-alarms.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`topicArn`subscriptionArn`owner`protocol`endpoint\n";
		FileGenerator.writeToFile("sns-topic.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`egressOnlyInternetGatewayId`attachments.vpcId`attachments.state\n";
		FileGenerator.writeToFile("egress-internetgateway.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`dhcpOptionsId`dhcpConfigurations.key`dhcpConfigurations.values\n";
		FileGenerator.writeToFile("dhcp-options.data", fieldNames, false);
		fieldNames = "loaddate`accountid`dhcpOptionsId`tags.key`tags.value\n";
		FileGenerator.writeToFile("dhcp-options-tags.data", fieldNames, false);
		
		fieldNames = "vpcPeeringConnectionId`status.code`expirationTime`requesterVpcInfo.ownerId`accepterVpcInfo.ownerId`requesterVpcInfo.vpcId`accepterVpcInfo.vpcId`requesterVpcInfo.cidrBlock`accepterVpcInfo.cidrBlock"+
					"`requesterVpcInfo.peeringOptions.allowDnsResolutionFromRemoteVpc`requesterVpcInfo.peeringOptions.allowEgressFromLocalClassicLinkToRemoteVpc`requesterVpcInfo.peeringOptions.allowEgressFromLocalVpcToRemoteClassicLink"+
					"`accepterVpcInfo.peeringOptions.allowDnsResolutionFromRemoteVpc`accepterVpcInfo.peeringOptions.allowEgressFromLocalClassicLinkToRemoteVpc`accepterVpcInfo.peeringOptions.allowEgressFromLocalVpcToRemoteClassicLink\n";
		FileGenerator.writeToFile("peering-connection-info.data", fieldNames, false);
		fieldNames = "loaddate`accountid`vpcPeeringConnectionId`tags.key`tags.value\n";
		FileGenerator.writeToFile("peering-connection-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`customerGatewayId`bgpAsn`ipAddress`state`type\n";
		FileGenerator.writeToFile("customer-gateway.data", fieldNames, false);
		fieldNames = "loaddate`accountid`customerGatewayId`tags.key`tags.value\n";
		FileGenerator.writeToFile("customer-gateway-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`vpnConnectionId`vpnGatewayId`customerGatewayId`state`category`type`customerGatewayConfiguration`options.staticRoutesOnly\n";
		FileGenerator.writeToFile("vpn-connection.data", fieldNames, false);
		fieldNames = "loaddate`accountid`vpnConnectionId`routes.source`routes.state`routes.destinationCidrBlock\n";
		FileGenerator.writeToFile("vpn-connection-routes.data", fieldNames, false);
		fieldNames = "loaddate`accountid`vpnConnectionId`vgwTelemetry.acceptedRouteCount`vgwTelemetry.outsideIpAddress`vgwTelemetry.lastStatusChange`vgwTelemetry.status`vgwTelemetry.statusMessage\n";
		FileGenerator.writeToFile("vpn-connection-telemetry.data", fieldNames, false);
		fieldNames = "loaddate`accountid`vpnConnectionId`tags.key`tags.value\n";
		FileGenerator.writeToFile("vpn-connection-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`connectionId`connectionName`ownerAccount`connectionState`region`location`bandwidth`vlan"
				+ "`partnerName`loaIssueTime`lagId`awsDevice\n";
		FileGenerator.writeToFile("direct-connection.data", fieldNames, false);
		fieldNames = "loaddate`accountid`virtualInterfaceId`ownerAccount`connectionId`location`virtualInterfaceType`virtualInterfaceName"
				+ "`vlan`asn`amazonSideAsn`authKey`amazonAddress`customerAddress`addressFamily`virtualInterfaceState"
				+ "`customerRouterConfig`virtualGatewayId`directConnectGatewayId`routeFilterPrefixes.cidr"
				+ "`bgpPeers.asn`bgpPeers.authKey`bgpPeers.addressFamily`bgpPeers.amazonAddress`bgpPeers.customerAddress`bgpPeers.bgpPeerState`bgpPeers.bgpStatus\n";
		FileGenerator.writeToFile("direct-connection-virtual-interfaces.data", fieldNames, false);
		
		fieldNames = "domainId`domainName`aRN`created`deleted`endpoint`processing`elasticsearchVersion`accessPolicies`endpoints"
				+ "`elasticsearchClusterConfig.instanceType`elasticsearchClusterConfig.instanceCount`elasticsearchClusterConfig.dedicatedMasterEnabled`elasticsearchClusterConfig.zoneAwarenessEnabled"
				+ "`elasticsearchClusterConfig.dedicatedMasterType`elasticsearchClusterConfig.dedicatedMasterCount`vPCOptions.vPCId`vPCOptions.subnetIds`vPCOptions.availabilityZones"
				+ "`vPCOptions.securityGroupIds`advancedOptions";
		FileGenerator.writeToFile("es-domain-info.data", fieldNames, false);
		fieldNames = "domainId`tags.key`tags.value";
		FileGenerator.writeToFile("es-domain-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`reservedInstancesId`instanceType`availabilityZone`duration`start`end`fixedPrice`instanceCount`productDescription`state`usagePrice`currencyCode"
				+ "`instanceTenancy`offeringClass`offeringType`scope`recurringCharges.frequency`recurringCharges.amount\n";
		FileGenerator.writeToFile("reserved-Instances-info.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`reservedInstancesId`tags.key`tags.value\n";
		FileGenerator.writeToFile("reserved-Instances-tags.data", fieldNames, false);
		
		fieldNames = "loaddate`accountid`region`instanceId`pingStatus`lastPingDateTime`agentVersion`isLatestVersion`platformType`platformName`platformVersion`activationId`iamRole`registrationDate`resourceType`name`iPAddress`computerName`associationStatus`lastAssociationExecutionDate`lastSuccessfulAssociationExecutionDate\n";
		FileGenerator.writeToFile("ssm-info.data", fieldNames, false);
		
		fieldNames ="loaddate`accountid`region`arn`clusterName`description`noOfNodes`primaryOrConfigEndpoint`availabilityZones`cacheNodeType`engine`engineVersion`cacheClusterStatus"
                + "`cacheClusterCreateTime`preferredMaintenanceWindow`cacheSubnetGroupName`autoMinorVersionUpgrade`replicationGroupId`snapshotRetentionLimit`snapshotWindow`authTokenEnabled"
                + "`transitEncryptionEnabled`atRestEncryptionEnabled`notificationConfiguration.topicArn`notificationConfiguration.topicStatus"
                + "`securityGroups`parameterGroup\n";
        FileGenerator.writeToFile("elastiCache-info.data",fieldNames, false);
        
        fieldNames = "loaddate`accountid`region`clusterName`tags.key`tags.value\n";
        FileGenerator.writeToFile("elastiCache-tags.data",fieldNames, false);
        
        fieldNames = "loaddate`accountid`region`Id`Name`Description`CreatedDate`Version";
        FileGenerator.writeToFile("api-info.data", fieldNames, false);     
	}
	
	/**
	 * Generate instance files.
	 *
	 * @param instanceMap the instance map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateInstanceFiles(Map<String,List<Instance>> instanceMap) throws IOException {
		
		String fieldNames ="";
	
		fieldNames = "instanceId`amiLaunchIndex`architecture`clientToken`ebsOptimized`EnaSupport`Hypervisor`ImageId`InstanceLifecycle`InstanceType`KernelId`KeyName`LaunchTime`Platform`PrivateDnsName`PrivateIpAddress`PublicDnsName`PublicIpAddress`RamdiskId`RootDeviceName`RootDeviceType`SourceDestCheck`SpotInstanceRequestId`SriovNetSupport`StateTransitionReason`SubnetId`VirtualizationType`VpcId`IamInstanceProfile.Arn`IamInstanceProfile.Id`Monitoring.State`Placement.Affinity`Placement.AvailabilityZone`Placement.GroupName`Placement.HostId`Placement.Tenancy`State.Name`State.Code`StateReason.Message`StateReason.Code";
		FileGenerator.generateFile(instanceMap, fieldNames, "instance-info.data");
		
		fieldNames = "instanceId`tags.key`tags.value";
		FileGenerator.generateFile(instanceMap, fieldNames, "instance-tags.data");
		
		fieldNames = "instanceId`SecurityGroups.groupId`SecurityGroups.groupName";
		FileGenerator.generateFile(instanceMap, fieldNames, "instance-secgroups.data");
	
		fieldNames = "instanceId`ProductCodes.ProductCodeId`ProductCodes.ProductCodeType";
		FileGenerator.generateFile(instanceMap, fieldNames, "instance-productcodes.data");
		
		fieldNames = "instanceId`BlockDeviceMappings.deviceName`BlockDeviceMappings.ebs.VolumeId`BlockDeviceMappings.ebs.AttachTime`BlockDeviceMappings.ebs.DeleteOnTermination`BlockDeviceMappings.ebs.status";
		FileGenerator.generateFile(instanceMap, fieldNames, "instance-blockdevices.data");
	
		fieldNames = "instanceId`NetworkInterfaces.NetworkInterfaceId`NetworkInterfaces.Description";
		FileGenerator.generateFile(instanceMap, fieldNames, "instance-nwinterfaces.data");
		
	}
	
	/**
	 * Generate nw interface files.
	 *
	 * @param nwIntfcMap the nw intfc map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateNwInterfaceFiles(Map<String,List<NetworkInterface>> nwIntfcMap) throws IOException {
		String fieldNames ="";
		fieldNames = "NetworkInterfaceId`Description`MacAddress`OwnerId`PrivateDnsName`PrivateIpAddress`SourceDestCheck`Status`SubnetId`VpcId`association.IpOwnerId`association.PublicDnsName`association.PublicIp`attachment.AttachmentId`attachment.AttachTime`attachment.DeleteOnTermination`attachment.DeviceIndex`attachment.status";
		FileGenerator.generateFile(nwIntfcMap, fieldNames, "nwinterface-info.data");
		
		fieldNames = "NetworkInterfaceId`groups.GroupId`groups.GroupName";
		FileGenerator.generateFile(nwIntfcMap, fieldNames, "nwinterface-secgroups.data");
		
		fieldNames = "NetworkInterfaceId`Ipv6Addresses.Ipv6Address";
		FileGenerator.generateFile(nwIntfcMap, fieldNames, "nwinterface-ipv6.data");
		
		fieldNames = "NetworkInterfaceId`PrivateIpAddresses.Primary`PrivateIpAddresses.PrivateDnsName`PrivateIpAddresses.PrivateIpAddress`PrivateIpAddresses.association.IpOwnerId`PrivateIpAddresses.association.PublicDnsName`PrivateIpAddresses.association.PublicIp";
		FileGenerator.generateFile(nwIntfcMap, fieldNames, "nwinterface-privateIpAddr.data");
		
	}
	
	/**
	 * Generate asg files.
	 *
	 * @param instanceMap the instance map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateAsgFiles(Map<String,List<AutoScalingGroup>> instanceMap) throws IOException {
		
		String fieldNames;
		
		fieldNames ="AutoScalingGroupARN`AutoScalingGroupName`AvailabilityZones`CreatedTime`DefaultCooldown`DesiredCapacity`HealthCheckGracePeriod`HealthCheckType`LaunchConfigurationName`MaxSize`MinSize`NewInstancesProtectedFromScaleIn`PlacementGroup`Status`SuspendedProcesses`TargetGroupARNs`TerminationPolicies`VPCZoneIdentifier";
		FileGenerator.generateFile(instanceMap, fieldNames, "asg-info.data");
		
		fieldNames ="AutoScalingGroupARN`instances.instanceid";
		FileGenerator.generateFile(instanceMap, fieldNames, "asg-instances.data");
		
		fieldNames ="AutoScalingGroupARN`LoadBalancerNames";
		FileGenerator.generateFile(instanceMap, fieldNames, "asg-elb.data");
		
		fieldNames ="AutoScalingGroupARN`tags.key`tags.value";
		FileGenerator.generateFile(instanceMap, fieldNames, "asg-tags.data");
		
	}
	
	/**
	 * Generate cloud formation stack files.
	 *
	 * @param fileInofMap the file inof map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateCloudFormationStackFiles(Map<String,List<Stack>> fileInofMap) throws IOException {
		String fieldNames;
		fieldNames ="StackId`StackName`ChangeSetId`CreationTime`Description`DisableRollback`LastUpdatedTime`RoleARN`StackStatus`StackStatusReason`TimeoutInMinutes";
		FileGenerator.generateFile(fileInofMap, fieldNames, "cloudfrmnstack-info.data");
		fieldNames ="StackId`tags.key`tags.value";
		FileGenerator.generateFile(fileInofMap, fieldNames, "cloudfrmnstack-tags.data");

	}
	
	/**
	 * Generate dynamo db files.
	 *
	 * @param dynamoMap the dynamo map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateDynamoDbFiles(Map<String,List<DynamoVH>> dynamoMap) throws IOException {
		String fieldNames;
		fieldNames ="table.TableArn`table.TableName`table.CreationDateTime`table.ItemCount`table.LatestStreamArn`table.LatestStreamLabel`table.TableSizeBytes`table.TableStatus`table.ProvisionedThroughput.ReadCapacityUnits`table.ProvisionedThroughput.WriteCapacityUnits`table.StreamSpecification.StreamEnabled`table.StreamSpecification.StreamViewType";
		FileGenerator.generateFile(dynamoMap, fieldNames, "dynamodb-tables.data");
		fieldNames ="table.TableArn`tags.key`tags.value";
		FileGenerator.generateFile(dynamoMap, fieldNames, "dynamodb-tables-tags.data");
	}
	
	/**
	 * Generate efs files.
	 *
	 * @param efsfMap the efsf map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateEfsFiles(Map<String,List<EfsVH>> efsfMap) throws IOException {
		String fieldNames;
		fieldNames ="efs.FileSystemId`efs.Name`efs.CreationTime`efs.CreationToken`efs.LifeCycleState`efs.NumberOfMountTargets`efs.OwnerId`efs.PerformanceMode";
		FileGenerator.generateFile(efsfMap, fieldNames, "efs-info.data");
		fieldNames ="efs.FileSystemId`tags.key`tags.value";
		FileGenerator.generateFile(efsfMap, fieldNames, "efs-tags.data");
		
	}
	
	/**
	 * Generate emr files.
	 *
	 * @param fileInofMap the file inof map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateEmrFiles(Map<String,List<Cluster>> fileInofMap) throws IOException {
		String fieldNames;
		fieldNames ="Id`AutoScalingRole`AutoTerminate`InstanceCollectionType`LogUri`MasterPublicDnsName`Name`NormalizedInstanceHours`ReleaseLabel`RequestedAmiVersion`RunningAmiVersion`ScaleDownBehavior`SecurityConfiguration`ServiceRole`TerminationProtected`VisibleToAllUsers";
		FileGenerator.generateFile(fileInofMap, fieldNames, "emr-info.data");
		fieldNames ="Id`tags.key`tags.value";
		FileGenerator.generateFile(fileInofMap, fieldNames, "emr-tags.data");

	}
	
	/**
	 * Generate lamda files.
	 *
	 * @param fileInofMap the file inof map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateLamdaFiles(Map<String,List<LambdaVH>> fileInofMap) throws IOException {
		String fieldNames;
		fieldNames = "lambda.FunctionArn`lambda.CodeSha256`lambda.CodeSize`lambda.Description`lambda.FunctionName`lambda.Handler`lambda.KMSKeyArn`lambda.LastModified`lambda.MemorySize`lambda.Role`lambda.Runtime`lambda.Timeout`lambda.Version`lambda.VpcConfig.VpcId`lambda.VpcConfig.SubnetIds`lambda.VpcConfig.SecurityGroupIds";
		FileGenerator.generateFile(fileInofMap, fieldNames, "lambda-info.data");
		fieldNames ="lambda.FunctionArn`tags.key`tags.value";
		FileGenerator.generateFile(fileInofMap, fieldNames, "lambda-tags.data");
		fieldNames ="lambda.FunctionArn`lambda.vpcConfig.securityGroupIds";
		FileGenerator.generateFile(fileInofMap, fieldNames, "lambda-secgroups.data");
	}

	/**
	 * Generate classic elb files.
	 *
	 * @param elbMap the elb map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateClassicElbFiles(Map<String, List<ClassicELBVH>> elbMap) throws IOException {
		String fieldNames;
		fieldNames = "elb.DNSName`elb.AvailabilityZones`elb.CanonicalHostedZoneName`elb.CanonicalHostedZoneNameID`elb.CreatedTime`elb.LoadBalancerName`elb.Scheme`elb.VPCId";
		FileGenerator.generateFile(elbMap, fieldNames, "classicelb-info.data");
		fieldNames = "elb.LoadBalancerName`elb.Instances.InstanceId";
		FileGenerator.generateFile(elbMap, fieldNames, "classicelb-instances.data");	
		fieldNames ="elb.LoadBalancerName`tags.key`tags.value";
		FileGenerator.generateFile(elbMap, fieldNames, "classicelb-tags.data");
		fieldNames ="elb.LoadBalancerName`elb.securityGroups";
		FileGenerator.generateFile(elbMap, fieldNames, "classicelb-secgroups.data");
	}
	
	/**
	 * Generate application elb files.
	 *
	 * @param elbMap the elb map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateApplicationElbFiles(Map<String, List<LoadBalancerVH>> elbMap) throws IOException {
		String fieldNames;
		fieldNames = "lb.LoadBalancerArn`lb.DNSName`lb.CanonicalHostedZoneID`lb.CreatedTime`lb.LoadBalancerName`lb.Scheme`lb.VPCId`AvailabilityZones`lb.type";
		FileGenerator.generateFile(elbMap, fieldNames, "appelb-info.data");
		fieldNames ="lb.LoadBalancerName`tags.key`tags.value";
		FileGenerator.generateFile(elbMap, fieldNames, "appelb-tags.data");
		fieldNames ="lb.LoadBalancerName`lb.securityGroups";
		FileGenerator.generateFile(elbMap, fieldNames, "appelb-secgroups.data");
	}
	
	/**
	 * Generate target group files.
	 *
	 * @param targetGrpMap the target grp map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateTargetGroupFiles(Map<String, List<TargetGroupVH>> targetGrpMap) throws IOException {
		String fieldNames;
		fieldNames = "trgtGrp.TargetGroupArn`trgtGrp.TargetGroupName`trgtGrp.vpcid`trgtGrp.protocol`trgtGrp.port`trgtGrp.HealthyThresholdCount`trgtGrp.UnhealthyThresholdCount`trgtGrp.HealthCheckIntervalSeconds`trgtGrp.HealthCheckTimeoutSeconds`trgtGrp.LoadBalancerArns";
		FileGenerator.generateFile(targetGrpMap, fieldNames, "targetGroup-info.data");
		
		fieldNames = "trgtGrp.TargetGroupName`targets.target.id";
		FileGenerator.generateFile(targetGrpMap, fieldNames, "targetGroup-instances.data");
	
		Map<String, List<LoadBalancerVH>> appElbInstanceMap = new HashMap<>();
		Iterator<Entry<String, List<TargetGroupVH>>> it=  targetGrpMap.entrySet().iterator();
		
		while(it.hasNext()){
			Entry<String, List<TargetGroupVH>> entry = it.next();
			String accntId= entry.getKey();
			List<TargetGroupVH> trgtList = entry.getValue();
			appElbInstanceMap.putIfAbsent(accntId,new ArrayList<LoadBalancerVH>());
			for(TargetGroupVH trgtGrp : trgtList){
				List<String> elbList = trgtGrp.getTrgtGrp().getLoadBalancerArns();
				for(String elbarn : elbList){
					LoadBalancer elb = new LoadBalancer();
					elb.setLoadBalancerArn(elbarn);
					Matcher appMatcher = Pattern.compile("(?<=loadbalancer/(app|net)/)(.*)(?=/)").matcher(elbarn);
					if(appMatcher.find()){
						elb.setLoadBalancerName(appMatcher.group());
						LoadBalancerVH elbVH = new LoadBalancerVH(elb);
						List<com.amazonaws.services.elasticloadbalancing.model.Instance> instances = new ArrayList<>();
						elbVH.setInstances(instances);
						trgtGrp.getTargets().forEach(trgtHealth -> {
							instances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(trgtHealth.getTarget().getId()));
						});
						appElbInstanceMap.get(accntId).add(elbVH);
					}
				}
			}
		}
		fieldNames = "lb.LoadBalancerName`Instances.InstanceId";
		FileGenerator.generateFile(appElbInstanceMap, fieldNames, "appelb-instances.data");
	}
	
	/**
	 * Generate nat gateway files.
	 *
	 * @param gateWayMap the gate way map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateNatGatewayFiles(Map<String, List<NatGateway>> gateWayMap) throws IOException {
		String fieldNames;
		fieldNames = "NatGatewayId`VpcId`SubnetId`State`CreateTime`DeleteTime`FailureCode`FailureMessage";
		FileGenerator.generateFile(gateWayMap, fieldNames, "natgateway-info.data");
		
		fieldNames = "NatGatewayId`NatGatewayAddresses.NetworkInterfaceId`NatGatewayAddresses.PrivateIp`NatGatewayAddresses.PublicIp`NatGatewayAddresses.AllocationId";
		FileGenerator.generateFile(gateWayMap, fieldNames, "natgateway-addresses.data");
	}
	
	/**
	 * Generate RDS cluster files.
	 *
	 * @param rdsclusterMap the rdscluster map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateRDSClusterFiles(Map<String, List<DBClusterVH>> rdsclusterMap) throws IOException {
		String fieldNames;
		fieldNames = "cluster.DBClusterArn`cluster.AllocatedStorage`cluster.AvailabilityZones`cluster.BackupRetentionPeriod`cluster.CharacterSetName`cluster.ClusterCreateTime`cluster.DatabaseName`cluster.DBClusterIdentifier`cluster.DBClusterParameterGroup"
				+ "`cluster.DbClusterResourceId`cluster.DBSubnetGroup`cluster.EarliestRestorableTime`cluster.Endpoint`cluster.Engine`cluster.EngineVersion`cluster.HostedZoneId`cluster.IAMDatabaseAuthenticationEnabled"
				+ "`cluster.KmsKeyId`cluster.LatestRestorableTime`cluster.MasterUsername`cluster.MultiAZ`cluster.PercentProgress`cluster.Port`cluster.PreferredBackupWindow`cluster.PreferredMaintenanceWindow`cluster.ReaderEndpoint"
				+ "`cluster.ReadReplicaIdentifiers`cluster.ReplicationSourceIdentifier`cluster.Status`cluster.StorageEncrypted";
		FileGenerator.generateFile(rdsclusterMap, fieldNames, "rdscluster-info.data");
		
		fieldNames = "cluster.DBClusterArn`cluster.VpcSecurityGroups.VpcSecurityGroupId`cluster.VpcSecurityGroups.status";
		FileGenerator.generateFile(rdsclusterMap, fieldNames, "rdscluster-vpcsecgroup.data");
		
		fieldNames = "cluster.DBClusterArn`tags.key`tags.value";
		FileGenerator.generateFile(rdsclusterMap, fieldNames, "rdscluster-tags.data");
	}
	
	/**
	 * Generate RDS instance files.
	 *
	 * @param rdsIntncMap the rds intnc map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateRDSInstanceFiles(Map<String, List<DBInstanceVH>> rdsIntncMap) throws IOException {
		String fieldNames;
		fieldNames = "dbinst.DBInstanceArn`dbinst.AllocatedStorage`dbinst.AutoMinorVersionUpgrade`dbinst.AvailabilityZone`dbinst.BackupRetentionPeriod`dbinst.CACertificateIdentifier`dbinst.CharacterSetName`dbinst.CopyTagsToSnapshot"
					+ "`dbinst.DBClusterIdentifier`dbinst.DBInstanceClass`dbinst.DBInstanceIdentifier`dbinst.DbInstancePort`dbinst.DBInstanceStatus`dbinst.DbiResourceId`dbinst.DBName`dbinst.Endpoint.Address`dbinst.Endpoint.Port`dbinst.Endpoint.HostedZoneID"
					+ "`dbinst.Engine`dbinst.EngineVersion`dbinst.EnhancedMonitoringResourceArn`dbinst.IAMDatabaseAuthenticationEnabled`dbinst.InstanceCreateTime`dbinst.Iops`dbinst.KmsKeyId`dbinst.LatestRestorableTime`dbinst.LicenseModel`dbinst.MasterUsername`dbinst.MonitoringInterval"
					+ "`dbinst.MonitoringRoleArn`dbinst.MultiAZ`dbinst.PreferredBackupWindow`dbinst.PreferredMaintenanceWindow`dbinst.PromotionTier`dbinst.PubliclyAccessible`dbinst.SecondaryAvailabilityZone`dbinst.StorageEncrypted`dbinst.StorageType`dbinst.TdeCredentialArn`dbinst.Timezone`dbinst.ReadReplicaDBClusterIdentifiers`dbinst.ReadReplicaDBInstanceIdentifiers`dbinst.ReadReplicaSourceDBInstanceIdentifier";
		FileGenerator.generateFile(rdsIntncMap, fieldNames, "rdsinstance-info.data");
		
		fieldNames = "dbinst.DBInstanceArn`dbinst.VpcSecurityGroups.VpcSecurityGroupId`dbinst.VpcSecurityGroups.status";
		FileGenerator.generateFile(rdsIntncMap, fieldNames, "rdsinstance-vpcsecgroup.data");
		
		fieldNames = "dbinst.DBInstanceArn`tags.key`tags.value";
		FileGenerator.generateFile(rdsIntncMap, fieldNames, "rdsinstance-tags.data");
		
	}
	
	/**
	 * Generate S 3 files.
	 *
	 * @param bucketMap the bucket map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateS3Files(Map<String, List<BucketVH>> bucketMap) throws IOException {
		String fieldNames;
		fieldNames = "bucket.Name`bucket.CreationDate`bucket.owner.displayname`bucket.owner.id`versionStatus`mfaDelete`location";
		FileGenerator.generateFile(bucketMap, fieldNames, "s3-info.data");
		fieldNames = "location`bucket.Name`tags.key`tags.value";
		FileGenerator.generateFile(bucketMap, fieldNames, "s3-tags.data");
		
	}

	/**
	 * Generate sec group file.
	 *
	 * @param secGrpMap the sec grp map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateSecGroupFile(Map<String, List<SecurityGroup>> secGrpMap) throws IOException {
		String fieldNames;
		fieldNames = "GroupId`Description`GroupName`OwnerId`vpcid";
		FileGenerator.generateFile(secGrpMap, fieldNames, "secgroup-info.data");
		fieldNames = "GroupId`tags.key`tags.value";
		FileGenerator.generateFile(secGrpMap, fieldNames, "secgroup-tags.data");
		
		Map<String, List<SGRuleVH>> secGrp = new HashMap<>();
		secGrpMap.forEach((k,v)-> {
				List<SGRuleVH> sgruleList = new ArrayList<>();
				v.forEach(sg -> {
					String groupId = sg.getGroupId();
					sgruleList.addAll(getRuleInfo(groupId,"inbound",sg.getIpPermissions()));
					sgruleList.addAll(getRuleInfo(groupId,"outbound",sg.getIpPermissionsEgress()));
				});
				secGrp.put(k,sgruleList);
			}
		);
		fieldNames = "groupId`type`ipProtocol`fromPort`toPort`cidrIp`cidrIpv6";
		FileGenerator.generateFile(secGrp, fieldNames, "secgroup-rules.data");
	
	}

	/**
	 * Gets the rule info.
	 *
	 * @param groupId the group id
	 * @param type the type
	 * @param permissions the permissions
	 * @return the rule info
	 */
	private static List<SGRuleVH> getRuleInfo(String groupId,String type,List<IpPermission> permissions){
		List<SGRuleVH> sgruleList = new ArrayList<>();
		permissions.forEach(obj-> {
			String ipProtocol = obj.getIpProtocol();
			Integer fromPort = obj.getFromPort();
			Integer toPort = obj.getToPort();
			String fromPortStr ;
			String toPortStr ;
			fromPortStr = fromPort==null?"":fromPort==-1?"All":fromPort.toString();
			toPortStr = toPort==null?"":toPort==-1?"All":toPort.toString();
			obj.getIpv4Ranges().forEach(iprange-> {
				String cidrIp = iprange.getCidrIp();
				SGRuleVH rule = new SGRuleVH(groupId,type, fromPortStr, toPortStr,"", cidrIp, "-1".equals(ipProtocol)?"All":ipProtocol);
				sgruleList.add(rule);
			});
			obj.getIpv6Ranges().forEach(iprange-> {
				String cidrIpv6 = iprange.getCidrIpv6();
				SGRuleVH rule = new SGRuleVH(groupId,type, fromPortStr, toPortStr,cidrIpv6, "", "-1".equals(ipProtocol)?"All":ipProtocol);
				sgruleList.add(rule);
			});
		});
		return sgruleList;
		
	}
	
	/**
	 * Generate subnet files.
	 *
	 * @param subNetMap the sub net map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateSubnetFiles(Map<String, List<Subnet>> subNetMap) throws IOException{
		String fieldNames;
		fieldNames = "SubnetId`AssignIpv6AddressOnCreation`AvailabilityZone`AvailableIpAddressCount`CidrBlock`DefaultForAz`MapPublicIpOnLaunch`State`VpcId";
		FileGenerator.generateFile(subNetMap, fieldNames, "subnet-info.data");
		fieldNames = "SubnetId`tags.key`tags.value";
		FileGenerator.generateFile(subNetMap, fieldNames, "subnet-tags.data");
	}

	/**
	 * Generate trusted advisor files.
	 *
	 * @param checksMap the checks map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateTrustedAdvisorFiles(Map<String, List<CheckVH>> checksMap) throws IOException {
		String fieldNames;
		fieldNames = "check.Id`check.Category`status`check.name`check.Description";
		FileGenerator.generateFile(checksMap, fieldNames, "checks-info.data");
		
		Iterator<Entry<String, List<CheckVH>>> it = checksMap.entrySet().iterator();
		Map<String, List<Resource>> resourceMap = new HashMap<>();
		while(it.hasNext()){
			Entry<String, List<CheckVH>> entry = it.next();
			String account = entry.getKey();
			List<CheckVH> checksValue = entry.getValue();
			List<Resource> resources = new ArrayList<>();
			checksValue.forEach(obj -> {
					resources.addAll(obj.getResources());
				}
			);
			resourceMap.put(account, resources);
		}
		
		fieldNames = "checkid`id`status`data";
		FileGenerator.generateFile(resourceMap, fieldNames, "checks-resources-info.data");
		
	}

	/**
	 * Generate redshift files.
	 *
	 * @param redShiftMap the red shift map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateRedshiftFiles(Map<String, List<com.amazonaws.services.redshift.model.Cluster>> redShiftMap) throws IOException {
		String fieldNames;
		fieldNames = "ClusterIdentifier`AllowVersionUpgrade`AutomatedSnapshotRetentionPeriod`AvailabilityZone`ClusterCreateTime`ClusterPublicKey`ClusterRevisionNumber`ClusterStatus`ClusterSubnetGroupName`ClusterVersion`DBName`ElasticIpStatus`Encrypted`Endpoint.Address`Endpoint.Port`EnhancedVpcRouting`KmsKeyId`MasterUsername`ModifyStatus`NodeType`NumberOfNodes`PreferredMaintenanceWindow`PubliclyAccessible`VpcId";
		FileGenerator.generateFile(redShiftMap, fieldNames, "redshift-info.data");
		
		fieldNames = "ClusterIdentifier`VpcSecurityGroups.VpcSecurityGroupId`VpcSecurityGroups.status";
		FileGenerator.generateFile(redShiftMap, fieldNames, "redshfit-secgroup.data");
		
		fieldNames = "ClusterIdentifier`tags.key`tags.value";
		FileGenerator.generateFile(redShiftMap, fieldNames, "redshfit-tags.data");
		
	}

	/**
	 * Generatefetch volume files.
	 *
	 * @param volumeMap the volume map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generatefetchVolumeFiles(Map<String, List<Volume>> volumeMap) throws IOException {
		String fieldNames;
		fieldNames = "VolumeId`VolumeType`AvailabilityZone`CreateTime`Encrypted`Iops`KmsKeyId`Size`SnapshotId`State";
		FileGenerator.generateFile(volumeMap, fieldNames, "volume-info.data");

		fieldNames = "VolumeId`attachments.InstanceId`attachments.AttachTime`attachments.DeleteOnTermination`attachments.Device`attachments.State";
		FileGenerator.generateFile(volumeMap, fieldNames, "volume-attachment.data");
		
		fieldNames = "VolumeId`tags.key`tags.value";
		FileGenerator.generateFile(volumeMap, fieldNames, "volume-tags.data");
	}

	/**
	 * Generate snapshot files.
	 *
	 * @param snapshotMap the snapshot map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateSnapshotFiles(Map<String, List<Snapshot>> snapshotMap) throws IOException {
		String fieldNames;
		fieldNames = "SnapshotId`Description`VolumeId`VolumeSize`Encrypted`DataEncryptionKeyId"
				+ "`KmsKeyId`OwnerAlias`OwnerId`Progress`StartTime`State`StateMessage";
		FileGenerator.generateFile(snapshotMap, fieldNames, "snapshot-info.data");
		fieldNames = "SnapshotId`tags.key`tags.value";
		FileGenerator.generateFile(snapshotMap, fieldNames, "snapshot-tags.data");
	}

	/**
	 * Generate vpc files.
	 *
	 * @param vpcMap the vpc map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateVpcFiles(Map<String, List<VpcVH>> vpcMap) throws IOException {
		
		String fieldNames;
		fieldNames = "vpc.vpcId`vpc.cidrBlock`vpc.dhcpOptionsId`vpc.instanceTenancy`vpc.isDefault`vpc.state`vpc.cidrBlockAssociationSet.cidrBlock`vpc.cidrBlockAssociationSet.cidrBlockState.state`vpc.cidrBlockAssociationSet.cidrBlockState.statusMessage`vpc.cidrBlockAssociationSet.associationId";
		FileGenerator.generateFile(vpcMap, fieldNames, "vpc-info.data");
		fieldNames = "vpc.vpcId`vpc.tags.key`vpc.tags.value";
		FileGenerator.generateFile(vpcMap, fieldNames, "vpc-tags.data");
		fieldNames = "vpcEndPoints.vpcId`vpcEndPoints.vpcEndpointId`vpcEndPoints.serviceName`vpcEndPoints.state`vpcEndPoints.creationTimestamp`vpcEndPoints.publicAccess`vpcEndPoints.policyDocument`vpcEndPoints.routeTableIds";
		FileGenerator.generateFile(vpcMap, fieldNames, "vpc-endpoints.data");
	}
	
	/**
	 * Generate api gateway files.
	 *
	 * @param apiGatewayMap the api gateway map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateApiGatewayFiles(Map<String, List<RestApi>> apiGatewayMap) throws IOException {
		String fieldNames;
		fieldNames = "Id`Name`Description`CreatedDate`Version";
		FileGenerator.generateFile(apiGatewayMap, fieldNames, "api-info.data");		
	}
	
	/**
	 * Generate iam user files.
	 *
	 * @param userMap the user map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateIamUserFiles(Map<String,List<UserVH>> userMap) throws IOException {
		String fieldNames;
		fieldNames = "user.username`user.userid`user.arn`user.CreateDate`user.path`passwordCreationDate`user.PasswordLastUsed`passwordResetRequired`mfa`groups";
		FileGenerator.generateFile(userMap, fieldNames, "iamuser-info.data");	
		fieldNames = "user.username`accessKeys.AccessKeyId`accessKeys.CreateDate`accessKeys.status`accessKeys.lastUsedDate";
		FileGenerator.generateFile(userMap, fieldNames, "iamuser-accesskeys.data");
		
	}
	
	/**
	 * Generate RDS snapshot files.
	 *
	 * @param dbSnapShots the db snap shots
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateRDSSnapshotFiles(Map<String,List<DBSnapshot>> dbSnapShots) throws IOException {
		
		String fieldNames;
		fieldNames = "DBSnapshotIdentifier`DBSnapshotArn`DBInstanceIdentifier`Status`snapshotCreateTime`snapshotType"
				+ "`encrypted`engine`allocatedStorage`port`availabilityZone`vpcId`instanceCreateTime`masterUsername"
				+ "`engineVersion`licenseModel`iops`optionGroupName`percentProgress`sourceRegion`sourceDBSnapshotIdentifier"
				+ "`storageType`tdeCredentialArn`kmsKeyId`timezone`iAMDatabaseAuthenticationEnabled";
		FileGenerator.generateFile(dbSnapShots, fieldNames, "rdssnapshot-info.data");	
	}

	/**
	 * Generate iam role files.
	 *
	 * @param iamRoleMap the iam role map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateIamRoleFiles(Map<String, List<Role>> iamRoleMap) throws IOException {
		String fieldNames;
		fieldNames = "roleName`roleId`arn`description`path`createDate`assumeRolePolicyDocument";
		FileGenerator.generateFile(iamRoleMap, fieldNames, "iamrole-info.data");	
	}
	
	/* Changes by John Start */
	
	/**
	 * Generate KMS files.
	 *
	 * @param kmsKeyMap the kms key map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateKMSFiles(Map<String, List<KMSKeyVH>> kmsKeyMap) throws IOException {
		String fieldNames;
		fieldNames = "key.keyId`key.arn`key.creationDate`key.aWSAccountId`key.description`key.keyState`key.enabled`key.keyUsage`key.deletionDate`key.validTo"
					+"`rotationStatus`alias.aliasName`alias.aliasArn";
		FileGenerator.generateFile(kmsKeyMap, fieldNames, "kms-info.data");
		fieldNames = "key.keyId`tags.tagKey`tags.tagValue";
		FileGenerator.generateFile(kmsKeyMap, fieldNames, "kms-tags.data");
	}
	
	/**
	 * Generate cloud front files.
	 *
	 * @param cfMap the cf map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateCloudFrontFiles(Map<String, List<CloudFrontVH>> cfMap) throws IOException {
		String fieldNames;
		fieldNames = "distSummary.id`distSummary.aRN`distSummary.status`distSummary.lastModifiedTime`distSummary.domainName`distSummary.enabled"
				+"`distSummary.comment`distSummary.priceClass`distSummary.webACLId`distSummary.httpVersion`distSummary.isIPV6Enabled`distSummary.viewerCertificate.iAMCertificateId"
				+"`distSummary.viewerCertificate.aCMCertificateArn`distSummary.viewerCertificate.cloudFrontDefaultCertificate`distSummary.viewerCertificate.sSLSupportMethod`distSummary.viewerCertificate.minimumProtocolVersion`distSummary.aliases.items";
		FileGenerator.generateFile(cfMap, fieldNames, "cloudfront-info.data");
		fieldNames = "distSummary.id`tags.key`tags.value";
		FileGenerator.generateFile(cfMap, fieldNames, "cloudfront-tags.data");
	}
	
	/**
	 * Generate EBS files.
	 *
	 * @param ebsMap the ebs map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateEBSFiles(Map<String, List<EbsVH>> ebsMap) throws IOException {
		String fieldNames;
		fieldNames = "app.applicationName`app.description`app.dateCreated`app.dateUpdated`env.environmentName`env.environmentId`env.versionLabel`env.solutionStackName"
				+"`env.platformArn`env.templateName`env.description`env.endpointURL`env.cNAME`env.dateCreated`env.dateUpdated`env.status`env.abortableOperationInProgress"
				+"`env.health`env.healthStatus";
		FileGenerator.generateFile(ebsMap, fieldNames, "ebs-info.data");
		
		fieldNames = "app.applicationName`env.environmentId`envResource.instances.id";
		FileGenerator.generateFile(ebsMap, fieldNames,"ebs-instances.data");
		
		fieldNames = "app.applicationName`env.environmentId`envResource.autoScalingGroups.name";
		FileGenerator.generateFile(ebsMap, fieldNames,"ebs-asg.data");
		
		fieldNames = "app.applicationName`env.environmentId`envResource.loadBalancers.name";
		FileGenerator.generateFile(ebsMap, fieldNames,"ebs-elb.data");
		
	}
	
	/**
	 * Generate PHD files.
	 *
	 * @param phdMap the phd map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generatePHDFiles(Map<String, List<PhdVH>> phdMap) throws IOException {
		String fieldNames;
		fieldNames = "eventDetails.event.arn`eventDetails.event.service`eventDetails.event.eventTypeCode`eventDetails.event.eventTypeCategory`eventDetails.event.region`"
				+"eventDetails.event.availabilityZone`eventDetails.event.startTime`eventDetails.event.endTime`eventDetails.event.lastUpdatedTime`eventDetails.event.statusCode"
				+"`eventDetails.eventDescription.latestDescription`eventDetails.eventMetadata";
		FileGenerator.generateFile(phdMap, fieldNames, "phd-info.data");
		fieldNames = "affectedEntities.eventArn`affectedEntities.entityArn`affectedEntities.awsAccountId`affectedEntities.entityValue`affectedEntities.lastUpdatedTime`affectedEntities.statusCode`affectedEntities.tags";
		FileGenerator.generateFile(phdMap, fieldNames, "phd-affectedentities.data");
	}
	
	/**
	 * Generate error file.
	 *
	 * @param errorMap the error map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static synchronized void generateErrorFile(Map<String, List<ErrorVH>> errorMap) throws IOException {
		String fieldNames;
		fieldNames = "region`type`exception";
		FileGenerator.generateFile(errorMap, fieldNames, "load-error.data");
	}
	
	/**
	 * Generate EC 2 route table files.
	 *
	 * @param routeTableMap the route table map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateEC2RouteTableFiles(Map<String, List<RouteTable>> routeTableMap) throws IOException {
		String fieldNames;
		fieldNames = "routeTableId`vpcId";
		FileGenerator.generateFile(routeTableMap, fieldNames, "routetable.data");
		
		fieldNames = "routeTableId`routes.destinationCidrBlock`routes.destinationPrefixListId`routes.gatewayId`routes.instanceId`routes.instanceOwnerId`routes.networkInterfaceId`routes.vpcPeeringConnectionId`routes.natGatewayId"
				+"`routes.state`routes.origin`routes.destinationIpv6CidrBlock`routes.egressOnlyInternetGatewayId";
		FileGenerator.generateFile(routeTableMap, fieldNames, "routetable-routes.data");
		
		fieldNames = "routeTableId`associations.routeTableAssociationId`associations.subnetId`associations.main";
		FileGenerator.generateFile(routeTableMap, fieldNames, "routetable-associations.data");
		
		fieldNames = "routeTableId`propagatingVgws.gatewayId";
		FileGenerator.generateFile(routeTableMap, fieldNames, "routetable-propagatingVgws.data");
		
		fieldNames = "routeTableId`tags.key`tags.value";
		FileGenerator.generateFile(routeTableMap, fieldNames, "routetable-tags.data");
	}
	
	/**
	 * Generate network acl files.
	 *
	 * @param networkAclMap the network acl map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateNetworkAclFiles(Map<String, List<NetworkAcl>> networkAclMap) throws IOException {
		String fieldNames;
		fieldNames = "networkAclId`vpcId`isDefault";
		FileGenerator.generateFile(networkAclMap, fieldNames, "networkacl.data");
		
		fieldNames = "networkAclId`entries.ruleNumber`entries.protocol`entries.ruleAction`entries.egress`entries.cidrBlock`entries.ipv6CidrBlock`entries.icmpTypeCode.type`entries.icmpTypeCode.code"
				+"`entries.portRange.from`entries.portRange.to";
		FileGenerator.generateFile(networkAclMap, fieldNames, "networkacl-entries.data");
		
		fieldNames = "networkAclId`associations.networkAclAssociationId`associations.subnetId";
		FileGenerator.generateFile(networkAclMap, fieldNames, "networkacl-associations.data");
		
		fieldNames = "networkAclId`tags.key`tags.value";
		FileGenerator.generateFile(networkAclMap, fieldNames, "networkacl-tags.data");
	}
	
	/**
	 * Generate elastic IP files.
	 *
	 * @param elasticIPMap the elastic IP map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateElasticIPFiles(Map<String, List<Address>> elasticIPMap) throws IOException {
		String fieldNames;
		fieldNames = "instanceId`publicIp`allocationId`associationId`domain`networkInterfaceId`networkInterfaceOwnerId`privateIpAddress";
		FileGenerator.generateFile(elasticIPMap, fieldNames, "elasticip.data");
	}
	
	/**
	 * Generate launch configurations files.
	 *
	 * @param launchConfigurationMap the launch configuration map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateLaunchConfigurationsFiles(Map<String, List<LaunchConfiguration>> launchConfigurationMap) throws IOException {
		String fieldNames;
		fieldNames = "launchConfigurationName`launchConfigurationARN`imageId`keyName`classicLinkVPCId`userData`instanceType`kernelId`ramdiskId`spotPrice`iamInstanceProfile`createdTime`ebsOptimized`associatePublicIpAddress`placementTenancy"
				+"`securityGroups`classicLinkVPCSecurityGroups`instanceMonitoring.enabled";
		FileGenerator.generateFile(launchConfigurationMap, fieldNames, "asg-launchconfig.data");
		
		fieldNames = "launchConfigurationName`blockDeviceMappings.virtualName`blockDeviceMappings.deviceName`blockDeviceMappings.noDevice`blockDeviceMappings.ebs.snapshotId`blockDeviceMappings.ebs.volumeSize"
				+"`blockDeviceMappings.ebs.volumeType`blockDeviceMappings.ebs.deleteOnTermination`blockDeviceMappings.ebs.iops`blockDeviceMappings.ebs.encrypted";
		FileGenerator.generateFile(launchConfigurationMap, fieldNames, "asg-launchconfig-blockDeviceMappings.data");
	}
	
	/**
	 * Generate internet gateway files.
	 *
	 * @param internetGatewayMap the internet gateway map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateInternetGatewayFiles(Map<String, List<InternetGateway>> internetGatewayMap) throws IOException {
		String fieldNames;
		
		fieldNames = "internetGatewayId";
		FileGenerator.generateFile(internetGatewayMap, fieldNames, "internetgateway.data");
		
		fieldNames = "internetGatewayId`attachments.vpcId`attachments.state";
		FileGenerator.generateFile(internetGatewayMap, fieldNames, "internetgateway-attachments.data");
		
		fieldNames = "internetGatewayId`tags.key`tags.value";
		FileGenerator.generateFile(internetGatewayMap, fieldNames, "internetgateway-tags.data");
	}
	
	/**
	 * Generate VPN gateway files.
	 *
	 * @param vpnGatewayMap the vpn gateway map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateVPNGatewayFiles(Map<String, List<VpnGateway>> vpnGatewayMap) throws IOException {
		String fieldNames;
		fieldNames = "vpnGatewayId`state`type`availabilityZone`amazonSideAsn";
		FileGenerator.generateFile(vpnGatewayMap, fieldNames, "vpngateway.data");
		
		fieldNames = "vpnGatewayId`vpcAttachments.vpcId`vpcAttachments.state";
		FileGenerator.generateFile(vpnGatewayMap, fieldNames, "vpngateway-vpcAttachments.data");
		
		fieldNames = "vpnGatewayId`tags.key`tags.value";
		FileGenerator.generateFile(vpnGatewayMap, fieldNames, "vpngateway-tags.data");
	}
	
	/**
	 * Generate scaling policies.
	 *
	 * @param scalingPolicyMap the scaling policy map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateScalingPolicies(Map<String, List<ScalingPolicy>> scalingPolicyMap) throws IOException {
		String fieldNames;
		fieldNames = "policyName`policyARN`autoScalingGroupName`policyType`adjustmentType`minAdjustmentStep`minAdjustmentMagnitude`scalingAdjustment`cooldown`metricAggregationType`estimatedInstanceWarmup";
		FileGenerator.generateFile(scalingPolicyMap, fieldNames, "asg-scalingpolicy.data");
		
		fieldNames = "policyName`stepAdjustments.metricIntervalLowerBound`stepAdjustments.metricIntervalUpperBound`stepAdjustments.scalingAdjustment";
		FileGenerator.generateFile(scalingPolicyMap, fieldNames, "asg-scalingpolicy-stepAdjustments.data");
				
		fieldNames = "policyName`alarms.alarmName`alarms.alarmARN";
		FileGenerator.generateFile(scalingPolicyMap, fieldNames, "asg-scalingpolicy-alarms.data");
	}
	
	/**
	 * Generate SNS topics.
	 *
	 * @param subscriptionMap the subscription map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateSNSTopics(Map<String, List<Subscription>> subscriptionMap) throws IOException {
		String fieldNames;
		fieldNames = "topicArn`subscriptionArn`owner`protocol`endpoint";
		FileGenerator.generateFile(subscriptionMap, fieldNames, "sns-topic.data");

	}
	
	/**
	 * Generate egress gateway.
	 *
	 * @param egressGatewayMap the egress gateway map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateEgressGateway(Map<String, List<EgressOnlyInternetGateway>> egressGatewayMap) throws IOException {
		String fieldNames;
		fieldNames = "egressOnlyInternetGatewayId`attachments.vpcId`attachments.state";
		FileGenerator.generateFile(egressGatewayMap, fieldNames, "egress-internetgateway.data");
	}
	
	/**
	 * Generate dhcp options.
	 *
	 * @param dhcpOptionsMap the dhcp options map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateDhcpOptions(Map<String, List<DhcpOptions>> dhcpOptionsMap) throws IOException {
		String fieldNames;
		fieldNames = "dhcpOptionsId`dhcpConfigurations";
		FileGenerator.generateFile(dhcpOptionsMap, fieldNames, "dhcp-options.data");
		
		fieldNames = "dhcpOptionsId`tags.key`tags.value";
		FileGenerator.generateFile(dhcpOptionsMap, fieldNames, "dhcp-options-tags.data");
	}
	
	/**
	 * Generate peering connections.
	 *
	 * @param peeringConnectionMap the peering connection map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generatePeeringConnections(Map<String, List<VpcPeeringConnection>> peeringConnectionMap) throws IOException {
		String fieldNames;
		fieldNames = "vpcPeeringConnectionId`status.code`expirationTime`requesterVpcInfo.ownerId`accepterVpcInfo.ownerId`requesterVpcInfo.vpcId`accepterVpcInfo.vpcId`requesterVpcInfo.cidrBlock`accepterVpcInfo.cidrBlock"+
					"`requesterVpcInfo.peeringOptions.allowDnsResolutionFromRemoteVpc`requesterVpcInfo.peeringOptions.allowEgressFromLocalClassicLinkToRemoteVpc`requesterVpcInfo.peeringOptions.allowEgressFromLocalVpcToRemoteClassicLink"+
					"`accepterVpcInfo.peeringOptions.allowDnsResolutionFromRemoteVpc`accepterVpcInfo.peeringOptions.allowEgressFromLocalClassicLinkToRemoteVpc`accepterVpcInfo.peeringOptions.allowEgressFromLocalVpcToRemoteClassicLink";
		FileGenerator.generateFile(peeringConnectionMap, fieldNames, "peering-connection-info.data");
		
		fieldNames = "vpcPeeringConnectionId`tags.key`tags.value";
		FileGenerator.generateFile(peeringConnectionMap, fieldNames, "peering-connection-tags.data");
	}
	
	/**
	 * Generate customer gateway.
	 *
	 * @param customerGatewayMap the customer gateway map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateCustomerGateway(Map<String, List<CustomerGateway>> customerGatewayMap) throws IOException {
		String fieldNames;
		fieldNames = "customerGatewayId`bgpAsn`ipAddress`state`type";
		FileGenerator.generateFile(customerGatewayMap, fieldNames, "customer-gateway.data");
		
		fieldNames = "customerGatewayId`tags.key`tags.value";
		FileGenerator.generateFile(customerGatewayMap, fieldNames, "customer-gateway-tags.data");
	}
	
	/**
	 * Generate vpn connection.
	 *
	 * @param vpnConnectionMap the vpn connection map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateVpnConnection(Map<String, List<VpnConnection>> vpnConnectionMap) throws IOException {
		String fieldNames;
		fieldNames = "vpnConnectionId`vpnGatewayId`customerGatewayId`state`category`type`options.staticRoutesOnly";
		FileGenerator.generateFile(vpnConnectionMap, fieldNames, "vpn-connection.data");
		
		fieldNames = "vpnConnectionId`routes.source`routes.state`routes.destinationCidrBlock";
		FileGenerator.generateFile(vpnConnectionMap, fieldNames, "vpn-connection-routes.data");
		
		fieldNames = "vpnConnectionId`vgwTelemetry.acceptedRouteCount`vgwTelemetry.outsideIpAddress`vgwTelemetry.lastStatusChange`vgwTelemetry.status`vgwTelemetry.statusMessage";
		FileGenerator.generateFile(vpnConnectionMap, fieldNames, "vpn-connection-telemetry.data");
		
		fieldNames = "vpnConnectionId`tags.key`tags.value";
		FileGenerator.generateFile(vpnConnectionMap, fieldNames, "vpn-connection-tags.data");
	}
	
	/**
	 * Generate direct connection.
	 *
	 * @param directConnectionMap the direct connection map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateDirectConnection(Map<String, List<Connection>> directConnectionMap) throws IOException{
		String fieldNames;
		fieldNames = "connectionId`connectionName`ownerAccount`connectionState`location`bandwidth`vlan`partnerName`loaIssueTime`lagId`awsDevice";
		FileGenerator.generateFile(directConnectionMap, fieldNames, "direct-connection.data");
	}
	
	/**
	 * Generate direct connection virtual interfaces.
	 *
	 * @param directConnectionVirtualInterfacesMap the direct connection virtual interfaces map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateDirectConnectionVirtualInterfaces(Map<String, List<VirtualInterface>> directConnectionVirtualInterfacesMap) throws IOException {
		String fieldNames;
		fieldNames = "virtualInterfaceId`ownerAccount`connectionId`location`virtualInterfaceType`virtualInterfaceName"
				+ "`vlan`asn`amazonSideAsn`authKey`amazonAddress`customerAddress`addressFamily`virtualInterfaceState"
				+ "`customerRouterConfig`virtualGatewayId`directConnectGatewayId`routeFilterPrefixes.cidr"
				+ "`bgpPeers.asn`bgpPeers.authKey`bgpPeers.addressFamily`bgpPeers.amazonAddress`bgpPeers.customerAddress`bgpPeers.bgpPeerState`bgpPeers.bgpStatus";
		FileGenerator.generateFile(directConnectionVirtualInterfacesMap, fieldNames, "direct-connection-virtual-interfaces.data");
	}
	
	/**
	 * Generate ES domain.
	 *
	 * @param esDomainMap the es domain map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateESDomain(Map<String, List<ElasticsearchDomainVH>> esDomainMap) throws IOException {
		String fieldNames;
		fieldNames = "domain.domainId`domain.domainName`domain.aRN`domain.created`domain.deleted`domain.endpoint`domain.processing`domain.elasticsearchVersion`domain.accessPolicies`domain.endpoints"
				+ "`domain.elasticsearchClusterConfig.instanceType`domain.elasticsearchClusterConfig.instanceCount`domain.elasticsearchClusterConfig.dedicatedMasterEnabled`domain.elasticsearchClusterConfig.zoneAwarenessEnabled"
				+ "`domain.elasticsearchClusterConfig.dedicatedMasterType`domain.elasticsearchClusterConfig.dedicatedMasterCount`domain.vPCOptions.vPCId`domain.vPCOptions.subnetIds`domain.vPCOptions.availabilityZones"
				+ "`domain.vPCOptions.securityGroupIds`domain.advancedOptions";
		FileGenerator.generateFile(esDomainMap, fieldNames, "es-domain-info.data");
		
		fieldNames = "domain.domainId`tags.key`tags.value";
		FileGenerator.generateFile(esDomainMap, fieldNames, "es-domain-tags.data");
	}
	
	/**
	 * Generate reserved instances.
	 *
	 * @param reservedInstancesMap the reserved instances map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateReservedInstances(Map<String, List<ReservedInstances>> reservedInstancesMap) throws IOException {
		String fieldNames;
		fieldNames = "reservedInstancesId`instanceType`availabilityZone`duration`start`end`fixedPrice`instanceCount`productDescription`state`usagePrice`currencyCode"
				+ "`instanceTenancy`offeringClass`offeringType`scope`recurringCharges.frequency`recurringCharges.amount";
		FileGenerator.generateFile(reservedInstancesMap, fieldNames, "reserved-Instances-info.data");
		
		fieldNames = "reservedInstancesId`tags.key`tags.value";
		FileGenerator.generateFile(reservedInstancesMap, fieldNames, "reserved-Instances-tags.data");
	}
	
	/**
	 * Generate ssm files.
	 *
	 * @param ssmMap the ssm map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateSsmFiles(Map<String,List<InstanceInformation>> ssmMap) throws IOException {
		String fieldNames;
		fieldNames ="instanceId`pingStatus`lastPingDateTime`agentVersion`isLatestVersion`platformType`platformName`platformVersion`activationId`iamRole`registrationDate`resourceType`name`iPAddress`computerName`associationStatus`lastAssociationExecutionDate`lastSuccessfulAssociationExecutionDate";
		FileGenerator.generateFile(ssmMap, fieldNames, "ssm-info.data");
	}
	
	/**
	 * Generate elasti cache files.
	 *
	 * @param elastiCacheMap the elasti cache map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateElastiCacheFiles(Map<String,List<ElastiCacheVH>> elastiCacheMap) throws IOException {
        String fieldNames;
        fieldNames ="arn`clusterName`description`noOfNodes`primaryOrConfigEndpoint`availabilityZones`cluster.cacheNodeType`cluster.engine`cluster.engineVersion`cluster.cacheClusterStatus"
                + "`cluster.cacheClusterCreateTime`cluster.preferredMaintenanceWindow`cluster.cacheSubnetGroupName`cluster.autoMinorVersionUpgrade`cluster.replicationGroupId`cluster.snapshotRetentionLimit`cluster.snapshotWindow`cluster.authTokenEnabled"
                + "`cluster.transitEncryptionEnabled`cluster.atRestEncryptionEnabled`cluster.notificationConfiguration.topicArn`cluster.notificationConfiguration.topicStatus"
                + "`securityGroups`parameterGroup";
        FileGenerator.generateFile(elastiCacheMap, fieldNames, "elastiCache-info.data");
        
        fieldNames = "clusterName`tags.key`tags.value";
        FileGenerator.generateFile(elastiCacheMap, fieldNames, "elastiCache-tags.data");
    }
}
