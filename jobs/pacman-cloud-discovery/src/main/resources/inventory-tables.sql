
create table if not exists pd_app_elb_instance (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  loadbalancername varchar(250) ,
  instanceid varchar(50) 
) ;


create table if not exists pd_appelb (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  loadbalancerarn varchar(500) ,
  dnsname varchar(250) ,
  canonicalhostedzoneid varchar(250) ,
  createdtime timestamptz ,
  loadbalancername varchar(250) ,
  scheme varchar(100) ,
  vpcid varchar(50) ,
  availabilityzones varchar(50),
  type varchar(20)
) ;


create table if not exists pd_asg (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  autoscalinggrouparn varchar(500) ,
  autoscalinggroupname varchar(250) ,
  availabilityzones varchar(250) ,
  createdtime timestamptz ,
  defaultcooldown int8 ,
  desiredcapacity int8 ,
  healthcheckgraceperiod int8 ,
  healthchecktype varchar(50) ,
  launchconfigurationname varchar(250) ,
  maxsize int8 ,
  minsize int8 ,
  newinstancesprotectedfromscalein varchar(5) ,
  placementgroup varchar(100) ,
  status varchar(100) ,
  suspendedprocesses varchar(1000) ,
  targetgrouparns varchar(1000) ,
  terminationpolicies varchar(500) ,
  vpczoneidentifier varchar(500) 
) ;


create table if not exists pd_asg_elb (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  autoscalinggrouparn varchar(500) ,
  loadbalancernames varchar(100) 
) ;


create table if not exists pd_asg_instances (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  autoscalinggrouparn varchar(500) ,
  instancesinstanceid varchar(50) 
) ;


create table if not exists pd_asg_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  autoscalinggrouparn varchar(500) ,
  key varchar(250) ,
  value varchar(1000) 
) ;


create table if not exists pd_checks (
  discoverydate timestamptz,
  accountid varchar(50),
  checkid varchar(100) ,
  checkcategory varchar(50) ,
  status varchar(50) ,
  checkname varchar(100) ,
  checkdescription varchar(max)
) ;



create table if not exists pd_checks_resources (
  discoverydate timestamptz,
  accountid varchar(50),
  checkid varchar(100) ,
  id varchar(250) ,
  status varchar(50),
  resourceinfo varchar(max)  
) ;


create table if not exists pd_checks_resources_attributes (
  discoverydate timestamptz,
  accountid varchar(50),
  checkid varchar(100) ,
  resourceid varchar(250) ,
  key varchar(50) ,
  value varchar(500) 
) ;


create table if not exists pd_classicelb (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dnsname varchar(250) ,
  availabilityzones varchar(50) ,
  canonicalhostedzonename varchar(250) ,
  canonicalhostedzonenameid varchar(250) ,
  createdtime timestamptz ,
  loadbalancername varchar(250) ,
  scheme varchar(100) ,
  vpcid varchar(50) 
) ;


create table if not exists pd_classicelb_instances (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  loadbalancername varchar(250) ,
  instanceid varchar(50) 
) ;



create table if not exists pd_cloudfrmnstack (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  stackid varchar(500) ,
  stackname varchar(250) ,
  changesetid varchar(500) ,
  creationtime timestamptz ,
  description varchar(4000) ,
  disablerollback varchar(5) ,
  lastupdatedtime timestamptz ,
  rolearn varchar(500) ,
  status varchar(50) ,
  statusreason varchar(1000) ,
  timeoutinminutes int8 
) ;


create table if not exists pd_cloudfrmnstack_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  stackid varchar(500) ,
  key varchar(50) ,
  value varchar(500) 
) ;



create table if not exists pd_dynamodb_tables (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  tablearn varchar(500) ,
  tablename varchar(250) ,
  creationdatetime timestamptz ,
  itemcount int8 ,
  lateststreamarn varchar(500) ,
  lateststreamlabel varchar(100) ,
  tablesizebytes int8 ,
  tablestatus varchar(50) ,
  readcapacityunits int8 ,
  writecapacityunits int8 ,
  streamenabled varchar(5) ,
  streamviewtype varchar(50) 
) ;

/*table structure for table pd_dynamodb_tables_tags */



create table if not exists pd_dynamodb_tables_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  tablearn varchar(500) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_efs */



create table if not exists pd_efs (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  filesystemid varchar(50) ,
  name varchar(100) ,
  creationtime timestamptz ,
  creationtoken varchar(50) ,
  lifecyclestate varchar(50) ,
  noofmounttargets int8 ,
  ownerid varchar(50) ,
  performancemode varchar(50) 
) ;

/*table structure for table pd_efs_tags */


create table if not exists pd_efs_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  filesystemid varchar(50) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_emr */



create table if not exists pd_emr (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  id varchar(50) ,
  autoscalingrole varchar(50) ,
  autoterminate varchar(5) ,
  instancecollectiontype varchar(50) ,
  loguri varchar(500) ,
  masterpubdnsname varchar(100) ,
  name varchar(100) ,
  norminstancehours int8 ,
  releaselabel varchar(50) ,
  reqamiversion varchar(10) ,
  runningamiversion varchar(10) ,
  scaledownbehavior varchar(50) ,
  securityconfig varchar(50) ,
  servicerole varchar(50) ,
  terminationprotected varchar(5) ,
  visibletoallusers varchar(5) 
) ;

/*table structure for table pd_emr_tags */



create table if not exists pd_emr_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  id varchar(50) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_instance */



create table if not exists pd_instance (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  instanceid varchar(50) ,
  amilaunchindex int8 ,
  architecture varchar(100) ,
  clienttoken varchar(500) ,
  ebsoptimized varchar(5) ,
  enasupport varchar(5) ,
  hypervisor varchar(50) ,
  imageid varchar(500) ,
  instancelifecycle varchar(50) ,
  instancetype varchar(50) ,
  kernelid varchar(50) ,
  keyname varchar(500) ,
  launchtime timestamptz ,
  platform varchar(50) ,
  privatednsname varchar(1000) ,
  privateipaddress varchar(50) ,
  publicdnsname varchar(1000) ,
  publicipaddress varchar(100) ,
  ramdiskid varchar(100) ,
  rootdevicename varchar(100) ,
  rootdevicetype varchar(100) ,
  sourcedestcheck varchar(5) ,
  spotinstancerequestid varchar(100) ,
  sriovnetsupport varchar(500) ,
  statetransitionreason varchar(500) ,
  subnetid varchar(100) ,
  virtualizationtype varchar(100) ,
  vpcid varchar(100) ,
  iaminstanceprofilearn varchar(500) ,
  iaminstanceprofileid varchar(100) ,
  monitoringstate varchar(50) ,
  affinity varchar(50) ,
  availabilityzone varchar(500) ,
  groupname varchar(500) ,
  hostid varchar(100) ,
  tenancy varchar(100) ,
  statename varchar(50) ,
  statecode int8 ,
  statereasonmessage varchar(500) ,
  statereasoncode varchar(100) 
) ;

/*table structure for table pd_instance_blockdevices */



create table if not exists pd_instance_blockdevices (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  instanceid varchar(50) ,
  devicename varchar(100) ,
  volumeid varchar(50) ,
  attachtime timestamptz ,
  delontermination varchar(5) ,
  status varchar(50) 
) ;

/*table structure for table pd_instance_nwinterfaces */



create table if not exists pd_instance_nwinterfaces (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  instanceid varchar(50) ,
  networkinterfaceid varchar(50) ,
  networkinterfacedescription varchar(100) 
) ;

/*table structure for table pd_instance_productcodes */



create table if not exists pd_instance_productcodes (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  instanceid varchar(50) ,
  productcodeid varchar(50) ,
  productcodetype varchar(20) 
) ;

/*table structure for table pd_instance_secgroups */



create table if not exists pd_instance_secgroups (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  instanceid varchar(50) ,
  securitygroupid varchar(50) ,
  securitygroupname varchar(500) 
) ;

/*table structure for table pd_instance_tags */



create table if not exists pd_instance_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  instanceid varchar(50) ,
  key varchar(100) ,
  value varchar(1000) 
) ;

/*table structure for table pd_lamda */



create table if not exists pd_lamda (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  functionarn varchar(500) ,
  codesha256 varchar(500) ,
  codesize int8 ,
  description varchar(250) ,
  functionname varchar(250) ,
  handler varchar(250) ,
  kmskeyarn varchar(500) ,
  lastmodified varchar(50) ,
  memorysize int8 ,
  role varchar(500) ,
  runtime varchar(50) ,
  timeout int8 ,
  version varchar(20) ,
  vpcconfigid varchar(500) ,
  vpcconfigsubnetids varchar(500) ,
  vpcconfigsecuritygroupids varchar(500) 
) ;

/*table structure for table pd_natgateway */



create table if not exists pd_natgateway (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  natgatewayid varchar(50) ,
  vpcid varchar(50) ,
  subnetid varchar(50) ,
  state varchar(20) ,
  createtime timestamptz ,
  deletetime timestamptz ,
  failurecode varchar(20) ,
  failuremessage varchar(50) 
) ;

/*table structure for table pd_natgateway_addresses */



create table if not exists pd_natgateway_addresses (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  natgatewayid varchar(50) ,
  networkinterfaceid varchar(50) ,
  privateip varchar(20) ,
  publicip varchar(20) ,
  allocationid varchar(50) 
) ;

/*table structure for table pd_nwinterface */



create table if not exists pd_nwinterface (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  networkinterfaceid varchar(50) ,
  description varchar(1000) ,
  macaddress varchar(50) ,
  ownerid varchar(50) ,
  privatednsname varchar(250) ,
  privateipaddress varchar(20) ,
  sourcedestcheck varchar(5) ,
  status varchar(50) ,
  subnetid varchar(100) ,
  vpcid varchar(100) ,
  associationipownerid varchar(100) ,
  associationpubdnsname varchar(500) ,
  associationpubip varchar(50) ,
  attachmentid varchar(100) ,
  attachmentattachtime timestamptz ,
  attachmentdelontermination varchar(5) ,
  attachmentdeviceindex varchar(25) ,
  attachmentstatus varchar(50) 
) ;


/*table structure for table pd_nwinterface_ipv */



create table if not exists pd_nwinterface_ipv (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  networkinterfaceid varchar(50) ,
  ipv6address varchar(50) 
) ;

/*table structure for table pd_nwinterface_privateipaddr */



create table if not exists pd_nwinterface_privateipaddr (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  networkinterfaceid varchar(50) ,
  privateipaddrprimary varchar(50) ,
  privatednsname varchar(250) ,
  privateipaddress varchar(20) ,
  associpownerid varchar(250) ,
  assocpubdnsname varchar(250) ,
  assocpublicip varchar(20) 
) ;

/*table structure for table pd_nwinterface_secgroups */



create table if not exists pd_nwinterface_secgroups (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  networkinterfaceid varchar(50) ,
  groupid varchar(20) ,
  groupname varchar(250) 
) ;

/*table structure for table pd_rdscluster */



create table if not exists pd_rdscluster (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dbclusterarn varchar(500) ,
  allocatedstorage int8 ,
  availabilityzones varchar(50) ,
  backupretentionperiod int8 ,
  charactersetname varchar(100) ,
  clustercreatetime timestamptz ,
  databasename varchar(100) ,
  dbclusteridentifier varchar(100) ,
  dbclusterparametergroup varchar(100) ,
  dbclusterresourceid varchar(100) ,
  dbsubnetgroup varchar(500) ,
  earliestrestorabletime timestamptz ,
  endpoint varchar(500) ,
  engine varchar(100) ,
  engineversion varchar(20) ,
  hostedzoneid varchar(100) ,
  iamdatabaseauthenticationenabled varchar(5) ,
  kmskeyid varchar(500) ,
  latestrestorabletime timestamptz ,
  masterusername varchar(100) ,
  multiaz varchar(100) ,
  percentprogress varchar(20) ,
  port int8 ,
  preferredbackupwindow varchar(100) ,
  preferredmaintenancewindow varchar(100) ,
  readerendpoint varchar(250) ,
  readreplicaidentifiers varchar(500) ,
  replicationsourceidentifier varchar(500) ,
  status varchar(50) ,
  storageencrypted varchar(5) 
) ;

/*table structure for table pd_rdscluster_tags */



create table if not exists pd_rdscluster_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dbclusterarn varchar(500) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_rdscluster_vpcsecgroup */



create table if not exists pd_rdscluster_vpcsecgroup (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dbclusterarn varchar(500) ,
  vpcsecuritygroupid varchar(20) ,
  vpcsecuritygroupstatus varchar(50) 
) ;

/*table structure for table pd_rdsinstance */



create table if not exists pd_rdsinstance (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dbclusterarn varchar(500) ,
  allocatedstorage int8 ,
  autominorversionupgrade varchar(5) ,
  availabilityzones varchar(50) ,
  backupretentionperiod int8 ,
  cacertificateidentifier varchar(50) ,
  charactersetname varchar(50) ,
  copytagstosnapshot varchar(5) ,
  dbclusteridentifier varchar(250) ,
  dbinstanceclass varchar(50) ,
  dbinstanceidentifier varchar(250) ,
  dbinstanceport int8 ,
  dbinstancestatus varchar(50) ,
  dbiresourceid varchar(50) ,
  dbname varchar(100) ,
  endpointaddress varchar(250) ,
  endpointport int8 ,
  endpointhostedzoneid varchar(20) ,
  engine varchar(50) ,
  engineversion varchar(20) ,
  enhancedmonitoringresourcearn varchar(500) ,
  iamdatabaseauthenticationenabled varchar(5) ,
  instancecreatetime timestamptz ,
  iops int8 ,
  kmskeyid varchar(250) ,
  latestrestorabletime timestamptz ,
  licensemodel varchar(50) ,
  masterusername varchar(50) ,
  monitoringinterval int8 ,
  monitoringrolearn varchar(500) ,
  multiaz varchar(5) ,
  preferredbackupwindow varchar(50) ,
  preferredmaintenancewindow varchar(50) ,
  promotiontier varchar(50) ,
  publiclyaccessible varchar(5) ,
  secondaryavailabilityzone varchar(50) ,
  storageencrypted varchar(5) ,
  storagetype varchar(50) ,
  tdecredentialarn varchar(500) ,
  timezone varchar(50) ,
  readreplicadbclusteridentifiers varchar(500) ,
  readreplicadbinstanceidentifiers varchar(500) ,
  readreplicasourcedbinstanceidentifier varchar(500) 
) ;

/*table structure for table pd_rdsinstance_tags */



create table if not exists pd_rdsinstance_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dbclusterarn varchar(500) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_rdsinstance_vpcsecgroup */



create table if not exists pd_rdsinstance_vpcsecgroup (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  dbclusterarn varchar(500) ,
  vpcsecuritygroupid varchar(20) ,
  vpcsecuritygroupstatus varchar(50) 
) ;

/*table structure for table pd_redshfit_secgroup */



create table if not exists pd_redshfit_secgroup (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  clusteridentifier varchar(50) ,
  vpcsecuritygroupid varchar(20) ,
  vpcsecuritygroupstatus varchar(50) 
) ;

/*table structure for table pd_redshfit_tags */



create table if not exists pd_redshfit_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  clusteridentifier varchar(50) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_redshift */

create table if not exists pd_redshift (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  clusteridentifier varchar(50) ,
  allowversionupgrade varchar(5)	 ,
  automatedsnapshotretentionperiod int8 ,
  availabilityzone varchar(50) ,
  clustercreatetime timestamptz ,
  clusterpublickey varchar(1000) ,
  clusterrevisionnumber varchar(20) ,
  clusterstatus varchar(20) ,
  clustersubnetgroupname varchar(150) ,
  clusterversion varchar(50) ,
  dbname varchar(50) ,
  elasticipstatus varchar(500) ,
  encrypted varchar(5) ,
  endpointaddress varchar(500) ,
  endpointport int8 ,
  enhancedvpcrouting varchar(100) ,
  kmskeyid varchar(250) ,
  masterusername varchar(100) ,
  modifystatus varchar(50) ,
  nodetype varchar(50) ,
  numberofnodes int8 ,
  preferredmaintenancewindow varchar(50) ,
  publiclyaccessible varchar(5) ,
  vpcid varchar(50) 
) ;
/*table structure for table pd_s3_tags */



create table if not exists pd_s3_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region varchar(15),
  name varchar(150) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_s3bucket */



create table if not exists pd_s3bucket (
  discoverydate timestamptz,
  accountid varchar(50),
  name varchar(150) ,
  creationdate timestamptz ,
  ownerdisplayname varchar(50) ,
  ownerid varchar(100) ,
  versionstatus varchar(20) ,
  mfadelete varchar(10) ,
  location varchar(50) 
) ;

/*table structure for table pd_secgroup */



create table if not exists pd_secgroup (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  groupid varchar(100) ,
  description varchar(500) ,
  groupname varchar(250) ,
  ownerid varchar(100) ,
  vpcid varchar(50) 
) ;

/*table structure for table pd_secgroup_tags */



create table if not exists pd_secgroup_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  groupid varchar(50) ,
  key varchar(100) ,
  value varchar(1000) 
) ;

/*table structure for table pd_snapshot */



create table if not exists pd_snapshot (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  snapshotid varchar(100) ,
  description varchar(500) ,
  volumeid varchar(50) ,
  volumesize varchar(20) ,
  encrypted varchar(5) ,
  dataencryptionkeyid varchar(250) ,
  kmskeyid varchar(250) ,
  owneralias varchar(50) ,
  ownerid varchar(100) ,
  progress varchar(20) ,
  starttime timestamptz ,
  state varchar(20) ,
  statemessage varchar(500) 
) ;

/*table structure for table pd_snapshot_tags */



create table if not exists pd_snapshot_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  snapshotid varchar(100) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_subnet */



create table if not exists pd_subnet (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  subnetid varchar(100) ,
  assignipv6addressoncreation varchar(5) ,
  availabilityzone varchar(150) ,
  availableipaddresscount varchar(20) ,
  cidrblock varchar(100) ,
  defaultforaz varchar(5) ,
  mappubliciponlaunch varchar(5) ,
  state varchar(50) ,
  vpcid varchar(50) 
) ;

/*table structure for table pd_subnet_tags */



create table if not exists pd_subnet_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  subnetid varchar(100) ,
  key varchar(500) ,
  value varchar(1000) 
) ;

/*table structure for table pd_targetgroup */



create table if not exists pd_targetgroup (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  targetgrouparn varchar(500) ,
  targetgroupname varchar(100) ,
  vpcid varchar(150) ,
  protocol varchar(20) ,
  port int8 ,
  healthythresholdcount int8 ,
  unhealthythresholdcount int8 ,
  healthcheckintervalseconds int8 ,
  healthchecktimeoutseconds int8 ,
  loadbalancerarns varchar(500) 
) ;

/*table structure for table pd_targetgroup_instances */



create table if not exists pd_targetgroup_instances (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  targetgrouparn varchar(150) ,
  targetgroupid varchar(50) 
) ;

/*table structure for table pd_volume */



create table if not exists pd_volume (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  volumeid varchar(150) ,
  volumetype varchar(50) ,
  availabilityzone varchar(100) ,
  createtime timestamptz ,
  encrypted varchar(5) ,
  iops int8 ,
  kmskeyid varchar(250) ,
  size varchar(20) ,
  snapshotid varchar(100) ,
  state varchar(20) 
) ;

/*table structure for table pd_volume_attachment */


create table if not exists pd_volume_attachment (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  volumeid varchar(150) ,
  instanceid varchar(100) ,
  attachtime timestamptz ,
  deleteontermination varchar(5) ,
  device varchar(50) ,
  state varchar(20) 
) ;

/*table structure for table pd_volume_tags */



create table if not exists pd_volume_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  volumeid varchar(150) ,
  key varchar(50) ,
  value varchar(500) 
) ;

/*table structure for table pd_vpc */



create table if not exists pd_vpc
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpcid VARCHAR(150),
   cidrblock VARCHAR(50),
   dhcpoptionsid VARCHAR(50),
   instanceTenancy VARCHAR(50),
   isdefault VARCHAR(5),
   state VARCHAR(20),
   cidrblockset VARCHAR(50),
   CidrBlockState VARCHAR(10),
   cidrBlockStatusMessage VARCHAR(50),
   cidrblockAssociationId VARCHAR(50)
)
;

/*table structure for table pd_vpc_tags */


create table if not exists pd_vpc_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  vpcid varchar(150) ,
  key varchar(50) ,
  value varchar(500) 
) ;




create table if not exists pd_api (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  id varchar(50) ,
  name varchar(100) ,
  description varchar(500) ,
  createddate timestamptz,
  version varchar(10)
) ;

/** 8/28/2017 Changes **/



create table if not exists pd_iamuser (
  discoverydate timestamptz,
  accountid varchar(50),
  username varchar(100) ,
  userid varchar(100) ,
  arn varchar(500) ,
  createdate timestamptz,
  path varchar(100),
  passwordCreationDate timestamptz,
  PasswordLastUsed timestamptz,
  passwordResetRequired varchar(5),
  mfaenabled varchar(5),
  groups varchar(500) 
) ;



create table if not exists pd_iamuserkeys (
  discoverydate timestamptz,
  accountid varchar(50),
  username varchar(100) ,
  accesskey varchar(100) ,
  createdate timestamptz,
  status varchar(20),
  lastuseddate timestamptz
) ;




create table if not exists pd_iamrole (
  discoverydate timestamptz,
  accountid varchar(50),
  rolename varchar(100) ,
  roleid varchar(100) ,
  rolearn varchar(500) ,
  description varchar(500) ,
  path varchar(100),
  createdate timestamptz,
  assumedpolicydoc varchar(4000)
);



create table if not exists pd_classicelb_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  loadbalancername varchar(250) ,
  key varchar(100) ,
  value varchar(1000)  
) ;



create table if not exists pd_appelb_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  loadbalancername varchar(250) ,
  key varchar(50) ,
  value varchar(500)  
) ;



create table if not exists pd_lambda_tags (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  functionarn varchar(500) ,
  key varchar(50) ,
  value varchar(500)  
) ;



create table if not exists pd_rdssnapshot (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  DBSnapshotIdentifier varchar(100),
  DBSnapshotArn varchar(500),
  DBInstanceIdentifier varchar(100),
  Status varchar(50),
  snapshotCreateTime timestamptz,
  snapshotType varchar(100),
  encrypted varchar(5),
  engine varchar(100),
  allocatedStorage varchar(100),
  port varchar(10),
  availabilityZone varchar(100),
  vpcId varchar(100),
  instanceCreateTime timestamptz,
  masterUsername varchar(100),
  engineVersion varchar(100),
  licenseModel varchar(100),
  iops varchar(100),
  optionGroupName varchar(100),
  percentProgress varchar(100),
  sourceRegion varchar(15),
  sourceDBSnapshotIdentifier varchar(100),
  storageType varchar(100),
  tdeCredentialArn varchar(500),
  kmsKeyId varchar(100),
  timezone varchar(50),
  iAMDatabaseAuthenticationEnabled varchar(5)
) ;


create table if not exists pd_phd
(
   discoverydate timestamptz,
   accountid varchar(50),
   eventarn VARCHAR(500),
   eventservice VARCHAR(200),
   eventTypeCode VARCHAR(200),
   eventTypeCategory VARCHAR(50),
   eventregion VARCHAR(50),
   availabilityZone VARCHAR(200),
   startTime timestamptz,
   endTime timestamptz,
   lastUpdatedTime timestamptz,
   statusCode VARCHAR(20),
   latestDescription VARCHAR(max),
   eventMetadata VARCHAR(500)
);

create table if not exists pd_phd_entities
(
   discoverydate timestamptz,
   accountid varchar(50),
   eventArn VARCHAR(500),
   entityArn VARCHAR(500),
   awsAccountId VARCHAR(50),
   entityValue VARCHAR(500),
   lastUpdatedTime timestamptz,
   statusCode VARCHAR(20),
   tags VARCHAR(100)
);

create table if not exists pd_kms
(
   discoverydate timestamptz,
   accountid varchar(50),
   region varchar(15),
   keyid VARCHAR(100),
   arn VARCHAR(500),
   creationdate timestamptz,
   awsaccountid VARCHAR(50),
   description VARCHAR(5000),
   keystate VARCHAR(50),
   keyenabled VARCHAR(20),
   keyusage VARCHAR(100),
   deletiondate timestamptz,
   validTo timestamptz,
   rotationStatus VARCHAR(50),
   aliasname VARCHAR(500),
   aliasarn VARCHAR(500)
)
;

create table if not exists pd_kms_tags
(
   discoverydate timestamptz,
   accountid varchar(50),
   region varchar(15),
   keyid VARCHAR(100),
   key VARCHAR(50),
   value VARCHAR(500)
)
;

create table if not exists pd_cloudfront
(
   discoverydate timestamptz,
   accountid varchar(50),
   id VARCHAR(100),
   arn VARCHAR(500),
   status VARCHAR(200),
   lastmodifiedtime timestamptz,
   domainName VARCHAR(200),
   enabled VARCHAR(20),
   comment VARCHAR(5000),
   priceclass VARCHAR(100),
   webaclid VARCHAR(100),
   httpversion VARCHAR(100),
   ipv6enabled VARCHAR(20),
   viewercertificateid VARCHAR(100),
   viewercertificatearn VARCHAR(500),
   viewercertificatedefaultcertificate VARCHAR(100),
   viewercertificatesslsupportmethod VARCHAR(100),
   viewercertificateminprotocolversion VARCHAR(100),
   aliases VARCHAR(500)
)
;

create table if not exists pd_cloudfront_tags
(
   discoverydate timestamptz,
   accountid varchar(50),
   id VARCHAR(100),
   key VARCHAR(50),
   value VARCHAR(500)
)
;

create table if not exists pd_beanstalk
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   applicationname VARCHAR(50),
   description VARCHAR(1000),
   datecreated timestamptz,
   dateupdated timestamptz,
   envname VARCHAR(100),
   envid VARCHAR(50),
   envversionlabel VARCHAR(100),
   envsolutionstackname VARCHAR(500),
   envplatformarn VARCHAR(500),
   envtemplatename VARCHAR(50),
   envdescription VARCHAR(500),
   envendpointurl VARCHAR(500),
   envcname VARCHAR(100),
   envdatecreated timestamptz,
   envdateupdated timestamptz,
   envstatus VARCHAR(20),
   envabortableoperationinprogress VARCHAR(100),
   envhealth VARCHAR(100),
   envhealthstatus VARCHAR(50)
)
;

create table if not exists pd_beanstalk_instance
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   applicationname VARCHAR(50),
   envid VARCHAR(50),
   instanceid VARCHAR(50)
);

create table if not exists pd_beanstalk_asg
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   applicationname VARCHAR(50),
   envid VARCHAR(50),
   asgname VARCHAR(200)
);

create table if not exists pd_beanstalk_elb
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   applicationname VARCHAR(50),
   envid VARCHAR(50),
   loadbalancername VARCHAR(200)
);

create table if not exists pd_classicelb_secgroups
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   loadbalancername VARCHAR(50),
   securitygroupid varchar(500)
);

create table if not exists pd_appelb_secgroups
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   loadbalancername VARCHAR(50),
   securityGroupId varchar(500)
);

create table if not exists pd_lambda_secgroups
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   functionarn VARCHAR(500),
   securityGroupId varchar(500)
);


create table if not exists pd_routetable
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   routeTableId VARCHAR(50),
   vpcId VARCHAR(50)
)
;

create table if not exists pd_routetable_routes
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   routeTableId VARCHAR(50),
   destinationcidrblock VARCHAR(1000),
   destinationPrefixListId VARCHAR(50),
   gatewayId VARCHAR(50),
   instanceId VARCHAR(50),
   instanceOwnerId VARCHAR(50),
   networkInterfaceId VARCHAR(50),
   vpcPeeringConnectionId VARCHAR(50),
   natGatewayId VARCHAR(50),
   state VARCHAR(50),
   origin VARCHAR(50),
   destinationIpv6CidrBlock VARCHAR(1000),
   egressOnlyInternetGatewayId VARCHAR(50)
)
;

create table if not exists pd_routetable_associations
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   routeTableId VARCHAR(50),
   routeTableAssociationId VARCHAR(100),
   subnetId VARCHAR(50),
   main VARCHAR(10)
)
;

create table if not exists pd_routetable_propagatingvgws
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   routeTableId VARCHAR(50),
   gatewayId VARCHAR(50)
)
;

create table if not exists pd_routetable_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   routeTableId VARCHAR(50),
   key VARCHAR(50),
   value VARCHAR(500)
)
;

create table if not exists pd_networkacl
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   networkAclId VARCHAR(50),
   vpcId VARCHAR(50),
   isDefault VARCHAR(10)
)
;

create table if not exists pd_networkacl_entries
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   networkAclId VARCHAR(50),
   ruleNumber VARCHAR(50),
   protocol VARCHAR(50),
   ruleAction VARCHAR(50),
   egress VARCHAR(50),
   cidrBlock VARCHAR(50),
   ipv6CidrBlock VARCHAR(100),
   icmpType VARCHAR(50),
   icmpTypeCode VARCHAR(50),
   portRangeFrom VARCHAR(10),
   portRangeTo VARCHAR(10)
)
;

create table if not exists pd_networkacl_associations
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   networkAclId VARCHAR(50),
   networkAclAssociationId VARCHAR(50),
   subnetId VARCHAR(50)
)
;

create table if not exists pd_networkacl_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   networkAclId VARCHAR(50),
   key VARCHAR(50),
   value VARCHAR(500)
)
;

create table if not exists pd_elasticip
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   instanceId VARCHAR(50),
   publicIp VARCHAR(20),
   allocationId VARCHAR(50),
   associationId VARCHAR(50),
   domain VARCHAR(50),
   networkInterfaceId VARCHAR(50),
   networkInterfaceOwnerId VARCHAR(50),
   privateIpAddress VARCHAR(20)
)
;

create table if not exists pd_launchconfig
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   launchConfigurationName VARCHAR(250),
   launchConfigurationARN VARCHAR(500),
   imageId VARCHAR(100),
   keyName VARCHAR(250),
   classicLinkVPCId VARCHAR(100),
   userData VARCHAR(max),
   instanceType VARCHAR(100),
   kernelId VARCHAR(100),
   ramdiskId VARCHAR(100),
   spotPrice VARCHAR(100),
   iamInstanceProfile VARCHAR(500),
   createdTime timestamptz,
   ebsOptimized VARCHAR(100),
   associatePublicIpAddress VARCHAR(100),
   placementTenancy VARCHAR(250),
   securityGroups VARCHAR(1000),
   classicLinkVPCSecurityGroups VARCHAR(1000),
   instanceMonitoringEnabled VARCHAR(10)
)
;

create table if not exists pd_launchconfig_blockdevicemappings
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   launchConfigurationName VARCHAR(250),
   virtualName VARCHAR(500),
   deviceName VARCHAR(100),
   ebsSnapshotId VARCHAR(250),
   ebsVolumeSize VARCHAR(50),
   ebsVolumeType VARCHAR(50),
   ebsDeleteOnTermination VARCHAR(10),
   ebsiops VARCHAR(250),
   ebsEncrypted VARCHAR(20),
   noDevice VARCHAR(50)
)
;

create table if not exists pd_internetgateway
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   internetGatewayId VARCHAR(50)
);

create table if not exists pd_internetgateway_attachments
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   internetGatewayId VARCHAR(50),
   vpcId VARCHAR(50),
   state VARCHAR(50)
);

create table if not exists pd_internetgateway_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   internetGatewayId VARCHAR(50),
   key VARCHAR(50),
   value VARCHAR(500)
)
;

create table if not exists pd_vpngateway
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnGatewayId VARCHAR(50),
   state VARCHAR(50),
   type VARCHAR(50),
   availabilityZone VARCHAR(50),
   amazonSideAsn INT8
)
;

create table if not exists pd_vpngateway_vpcattachments
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnGatewayId VARCHAR(50),
   vpcId VARCHAR(50),
   state VARCHAR(50)
)
;

create table if not exists pd_vpngateway_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnGatewayId VARCHAR(50),
   key VARCHAR(50),
   value VARCHAR(500)
)
;

create table if not exists pd_scalingpolicy
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   policyName VARCHAR(500),
   policyARN VARCHAR(1000),
   autoScalingGroupName VARCHAR(500),
   policyType VARCHAR(50),
   adjustmentType VARCHAR(50),
   minAdjustmentStep VARCHAR(500),
   minAdjustmentMagnitude VARCHAR(500),
   scalingAdjustment VARCHAR(500),
   cooldown VARCHAR(500),
   metricAggregationType VARCHAR(500),
   estimatedInstanceWarmup VARCHAR(500)
)
;

create table if not exists pd_scalingpolicy_stepadjustments
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   policyName VARCHAR(250),
   metricIntervalLowerBound VARCHAR(50),
   metricIntervalUpperBound VARCHAR(50),
   scalingAdjustment VARCHAR(50)
)
;

create table if not exists pd_scalingpolicy_alarms
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   policyName VARCHAR(250),
   alarmName VARCHAR(250),
   alarmARN VARCHAR(500)
)
;


create table if not exists pd_loaderror
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   type varchar(50),
   message varchar(max)
);


create table if not exists pd_secgroup_rules (
  discoverydate timestamptz,
  accountid varchar(50),
  region  varchar(15),
  groupid varchar(50) ,
  type varchar(20) ,
  ipprotocol varchar(50),
  fromport varchar(10),
  toport varchar(10),
  cidrIp varchar(50),
  cidrIpv6 varchar(50)
) ;




create table if not exists pd_sns_topic
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   topicArn VARCHAR(500),
   subscriptionArn VARCHAR(500),
   owner VARCHAR(50),
   protocol VARCHAR(20),
   endpoint VARCHAR(500)
)
;

create table if not exists pd_egress_internetgateway
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   egressOnlyInternetGatewayId VARCHAR(100),
   attachmentsVpcId VARCHAR(50),
   attachmentsState VARCHAR(10)
)
;

create table if not exists pd_dhcp_options
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   dhcpOptionsId VARCHAR(50),
   dhcpConfigurations VARCHAR(max)
)
;

create table if not exists pd_dhcp_options_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   dhcpOptionsId VARCHAR(50),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;

create table if not exists pd_peering_connection
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpcPeeringConnectionId VARCHAR(50),
   status VARCHAR(50),
   expirationTime VARCHAR(50),
   requesterVpcOwnerId VARCHAR(50),
   accepterVpcOwnerId VARCHAR(50),
   requesterVpcId VARCHAR(50),
   accepterVpcId VARCHAR(50),
   requesterVpcInfoCidrBlock VARCHAR(50),
   accepterVpcInfoCidrBlock VARCHAR(50),
   requesterVpcAllowDnsResolutionFromRemoteVpc VARCHAR(10),
   requesterVpcAllowEgressFromLocalClassicLinkToRemoteVpc VARCHAR(10),
   requesterVpcAllowEgressFromLocalVpcToRemoteClassicLink VARCHAR(10),
   accepterVpcAllowDnsResolutionFromRemoteVpc VARCHAR(10),
   accepterVpcAllowEgressFromLocalClassicLinkToRemoteVpc VARCHAR(10),
   accepterVpcAllowEgressFromLocalVpcToRemoteClassicLink VARCHAR(10)
)
;

create table if not exists pd_peering_connection_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpcPeeringConnectionId VARCHAR(50),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;

create table if not exists pd_customer_gateway
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   customerGatewayId VARCHAR(50),
   bgpAsn VARCHAR(50),
   ipAddress VARCHAR(50),
   state VARCHAR(10),
   type VARCHAR(20)
)
;

create table if not exists pd_customer_gateway_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   customerGatewayId VARCHAR(50),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;

create table if not exists pd_vpn_connection
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnConnectionId VARCHAR(50),
   vpnGatewayId VARCHAR(50),
   customerGatewayId VARCHAR(50),
   state VARCHAR(10),
   category VARCHAR(50),
   type VARCHAR(20),
   optionsStaticRoutesOnly VARCHAR(50)
)
;

create table if not exists pd_vpn_connection_routes
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnConnectionId VARCHAR(50),
   routesSource VARCHAR(50),
   routesState VARCHAR(20),
   routesDestinationCidrBlock VARCHAR(100)
)
;

create table if not exists pd_vpn_connection_telemetry
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnConnectionId VARCHAR(50),
   acceptedRouteCount VARCHAR(10),
   outsideIpAddress VARCHAR(50),
   lastStatusChange VARCHAR(50),
   status VARCHAR(10),
   statusMessage VARCHAR(100)
)
;

create table if not exists pd_vpn_connection_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   vpnConnectionId VARCHAR(50),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;

create table if not exists pd_direct_connect
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   connectionId VARCHAR(50),
   connectionName VARCHAR(500),
   ownerAccount VARCHAR(50),
   connectionState VARCHAR(10),
   location VARCHAR(50),
   bandwidth VARCHAR(50),
   vlan VARCHAR(50),
   partnerName VARCHAR(50),
   loaIssueTime VARCHAR(50),
   lagId VARCHAR(50),
   awsDevice VARCHAR(50)
)
;

create table if not exists pd_virtual_interfaces
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   virtualInterfaceId VARCHAR(50),
   ownerAccount VARCHAR(50),
   connectionId VARCHAR(50),
   location VARCHAR(20),
   virtualInterfaceType VARCHAR(20),
   virtualInterfaceName VARCHAR(50),
   vlan VARCHAR(50),
   asn VARCHAR(50),
   amazonSideAsn VARCHAR(50),
   authKey VARCHAR(200),
   amazonAddress VARCHAR(50),
   customerAddress VARCHAR(50),
   addressFamily VARCHAR(50),
   virtualInterfaceState VARCHAR(50),
   customerRouterConfig VARCHAR(max),
   virtualGatewayId VARCHAR(50),
   directConnectGatewayId VARCHAR(50),
   routeFilterPrefixesCidr VARCHAR(50),
   bgpPeersAsn VARCHAR(50),
   bgpPeersAuthkey VARCHAR(100),
   bgpPeersAddressFamily VARCHAR(50),
   bgpPeersAmazonAddress VARCHAR(50),
   bgpPeersCustomerAddress VARCHAR(50),
   bgpPeersBgpPeerState VARCHAR(50),
   bgpPeersBgpStatus VARCHAR(50)
)
;

create table if not exists pd_elasticsearch
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   domainId VARCHAR(50),
   domainName VARCHAR(500),
   aRN VARCHAR(500),
   created VARCHAR(50),
   deleted VARCHAR(50),
   endpoint VARCHAR(1000),
   processing VARCHAR(50),
   elasticsearchVersion VARCHAR(50),
   accessPolicies VARCHAR(max),
   endpoints VARCHAR(max),
   clusterInstanceType VARCHAR(100),
   clusterInstanceCount VARCHAR(10),
   clusterDedicatedMasterEnabled VARCHAR(10),
   clusterZoneAwarenessEnabled VARCHAR(10),
   clusterDedicatedMasterType VARCHAR(100),
   clusterDedicatedMasterCount VARCHAR(10),
   vPCId VARCHAR(100),
   subnetId VARCHAR(500),
   availabilityZone VARCHAR(50),
   securityGroupId VARCHAR(500),
   advancedOptions VARCHAR(max)
)
;

create table if not exists pd_elasticsearch_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   domainId VARCHAR(50),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;

create table if not exists pd_ssm_instance
(
	discoverydate 		timestamptz,
	accountid 			VARCHAR(50),
	region 				VARCHAR(15),
	instanceId 			VARCHAR(50),
	pingStatus 			VARCHAR(50),
	lastPingDateTime 	timestamptz,
	agentVersion 		VARCHAR(50),
	isLatestVersion 	VARCHAR(5),
	platformType 		VARCHAR(50),
	platformName 		VARCHAR(50),
	platformVersion 	VARCHAR(50),
	activationId 		VARCHAR(50),
	iamRole 			VARCHAR(100),
	registrationDate 	timestamptz,
	resourceType 		VARCHAR(50),
	name 				VARCHAR(100),
	iPAddress 			VARCHAR(50),
	computerName 		VARCHAR(100),
	associationStatus 	VARCHAR(50),
	lastAssociationExecutionDate timestamptz,
	lastSuccessfulAssociationExecutionDate timestamptz
);

create table if not exists pd_reserved_instance
(
	discoverydate 		timestamptz,
	accountid 			VARCHAR(50),
	region 				VARCHAR(15),
	instanceId 			VARCHAR(50),
	instanceType 		VARCHAR(20),
	availabilityZone 	VARCHAR(20),
	duration 			VARCHAR(10),
	startDate			timestamptz,
	endDate			    timestamptz,
	fixedPrice 			VARCHAR(20),
	instanceCount 		VARCHAR(10),
	productDescription 	VARCHAR(max),
	state 				VARCHAR(20),
	usagePrice 			VARCHAR(50),
	currencyCode 		VARCHAR(20),
	instanceTenancy 	VARCHAR(50),
	offeringClass 		VARCHAR(50),
	offeringType 		VARCHAR(50),
	scope 				VARCHAR(50),
	recurringChargesFrequency VARCHAR(50),
	recurringChargesAmount VARCHAR(20)
);

create table if not exists pd_reserved_instance_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   instanceId VARCHAR(50),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;

create table if not exists cf_aws_accounts
(
   accountid varchar(20),
   accountname varchar(50)
)
;

create table if not exists pacman_field_override
(
   resourcetype varchar(50),
   _resourceid varchar(1000),
   fieldname varchar(100),
   fieldvalue varchar(200),
   updatedby varchar(100),
   updatedon varchar(50)
)
;

create table if not exists pd_elasticache
(
	discoverydate 		timestamptz,
	accountid 			VARCHAR(50),
	region 				VARCHAR(15),
	arn VARCHAR(500),
	clusterName VARCHAR(200),
	description  VARCHAR(1000),
	noofnodes  int2,
	primaryOrConfigEndpoint VARCHAR(500),
	availabilityZones VARCHAR(200),
	nodeType VARCHAR(50),
	engine VARCHAR(50),
	engineVersion VARCHAR(20),
	clusterStatus VARCHAR(20),
	clusterCreateTime timestamptz,
	preferredMaintenanceWindow VARCHAR(50),
	subnetGroupName VARCHAR(100),
	autoMinorVersionUpgrade VARCHAR(20),
	replicationGroupId VARCHAR(100),
	snapshotRetentionLimit VARCHAR(50),
	snapshotWindow VARCHAR(100),
	authTokenEnabled VARCHAR(10),
	transitEncryptionEnabled VARCHAR(10),
	atRestEncryptionEnabled VARCHAR(10),
	notificationConfigTopicArn VARCHAR(500),
	notificationConfigTopicStatus VARCHAR(20),
	securityGroups VARCHAR(200),
	parameterGroup VARCHAR(200)
);

create table if not exists pd_elasticache_tags
(
   discoverydate timestamptz,
   accountid VARCHAR(50),
   region VARCHAR(15),
   clustername VARCHAR(500),
   key VARCHAR(100),
   value VARCHAR(1000)
)
;