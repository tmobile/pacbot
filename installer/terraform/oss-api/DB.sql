/*
SQLyog Ultimate v12.09 (32 bit)
MySQL - 5.6.27-log : Database - pacmandata
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`pacmandata` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

USE `pacmandata`;

/*Table structure for table `ASGC_Issues` */

SET @region='$region';
SET @account='$account';
SET @eshost='$eshost';
SET @esport='$esport';

DROP TABLE IF EXISTS `OmniSearch_Config`;

CREATE TABLE `OmniSearch_Config` (
  `SEARCH_CATEGORY` varchar(100) COLLATE utf8_bin NOT NULL,
  `RESOURCE_TYPE` varchar(100) COLLATE utf8_bin NOT NULL,
  `REFINE_BY_FIELDS` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `RETURN_FIELDS` varchar(100) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `PacmanSubscriptions` */

DROP TABLE IF EXISTS `PacmanSubscriptions`;

CREATE TABLE `PacmanSubscriptions` (
  `subscriptionId` bigint(75) NOT NULL AUTO_INCREMENT,
  `emailId` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `subscriptionValue` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`subscriptionId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `Pacman_Asset_Config` */

DROP TABLE IF EXISTS `Pacman_Asset_Config`;

CREATE TABLE `Pacman_Asset_Config` (
  `resourceId` varchar(75) COLLATE utf8_bin NOT NULL,
  `configType` varchar(75) COLLATE utf8_bin NOT NULL,
  `config` text COLLATE utf8_bin,
  `createdDate` datetime NOT NULL,
  PRIMARY KEY (`resourceId`,`configType`,`createdDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `Roles` */

DROP TABLE IF EXISTS `Roles`;

CREATE TABLE `Roles` (
  `roleId` bigint(25) NOT NULL,
  `roleName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `roleDesc` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `writePermission` int(15) DEFAULT '0',
  `owner` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `client` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `UserRoleMapping` */

DROP TABLE IF EXISTS `UserRoleMapping`;

CREATE TABLE `UserRoleMapping` (
  `userRoleId` varchar(75) COLLATE utf8_bin NOT NULL,
  `userId` varchar(75) COLLATE utf8_bin NOT NULL,
  `roleId` int(75) NOT NULL,
  `clientId` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `allocator` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`userRoleId`,`userId`,`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `cf_AssetGroupDetails` */

DROP TABLE IF EXISTS `cf_AssetGroupDetails`;

CREATE TABLE `cf_AssetGroupDetails` (
  `groupId` varchar(75) COLLATE utf8_bin NOT NULL DEFAULT '',
  `groupName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `dataSource` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `displayName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `groupType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createdBy` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createdUser` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `modifiedUser` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `modifiedDate` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `description` text COLLATE utf8_bin,
  `aliasQuery` text COLLATE utf8_bin,
  `isVisible` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_AssetGroupException` */

DROP TABLE IF EXISTS `cf_AssetGroupException`;

CREATE TABLE `cf_AssetGroupException` (
  `id_` bigint(20) NOT NULL,
  `groupName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `targetType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `ruleName` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `ruleId` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `expiryDate` date DEFAULT NULL,
  `exceptionName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `exceptionReason` varchar(2000) COLLATE utf8_bin DEFAULT NULL,
  `dataSource` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_AssetGroupOwnerDetails` */

DROP TABLE IF EXISTS `cf_AssetGroupOwnerDetails`;

CREATE TABLE `cf_AssetGroupOwnerDetails` (
  `ownerId` varchar(100) COLLATE utf8_bin NOT NULL,
  `ownnerName` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `assetGroupName` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `ownerEmailId` text COLLATE utf8_bin,
  PRIMARY KEY (`ownerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_AssetGroupTargetDetails` */

DROP TABLE IF EXISTS `cf_AssetGroupTargetDetails`;

CREATE TABLE `cf_AssetGroupTargetDetails` (
  `id_` varchar(75) COLLATE utf8_bin NOT NULL DEFAULT '',
  `groupId` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `targetType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `attributeName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `attributeValue` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_AssetGroupUserRoles` */

DROP TABLE IF EXISTS `cf_AssetGroupUserRoles`;

CREATE TABLE `cf_AssetGroupUserRoles` (
  `agUserRoleId` varchar(75) COLLATE utf8_bin NOT NULL,
  `assetGroupName` varchar(75) COLLATE utf8_bin NOT NULL,
  `assetGroupRole` int(75) NOT NULL,
  PRIMARY KEY (`agUserRoleId`,`assetGroupName`,`assetGroupRole`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `cf_Certificate`;

CREATE TABLE `cf_Certificate` (
  `id_` bigint(20) NOT NULL,
  `domainName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `certType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `validFrom` datetime DEFAULT NULL,
  `validTo` datetime DEFAULT NULL,
  `application` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `environment` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `appContact` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `updatedBy` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `certStatus` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



/*Table structure for table `cf_Datasource` */

DROP TABLE IF EXISTS `cf_Datasource`;

CREATE TABLE `cf_Datasource` (
  `dataSourceId` bigint(20) NOT NULL,
  `dataSourceName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `dataSourceDesc` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `config` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` date DEFAULT NULL,
  `modifiedDate` date DEFAULT NULL,
  PRIMARY KEY (`dataSourceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_Domain` */

DROP TABLE IF EXISTS `cf_Domain`;

CREATE TABLE `cf_Domain` (
  `domainName` varchar(75) COLLATE utf8_bin NOT NULL,
  `domainDesc` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `config` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` date DEFAULT NULL,
  `modifiedDate` date DEFAULT NULL,
  `userId` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`domainName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `cf_JobScheduler` */

DROP TABLE IF EXISTS `cf_JobScheduler`;

CREATE TABLE `cf_JobScheduler` (
  `jobId` varchar(75) COLLATE utf8_bin NOT NULL,
  `jobUUID` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `jobName` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  `jobType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `jobParams` text COLLATE utf8_bin,
  `jobFrequency` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `jobExecutable` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `jobArn` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`jobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_Jobs` */

DROP TABLE IF EXISTS `cf_Jobs`;

CREATE TABLE `cf_Jobs` (
  `jobId` bigint(20) NOT NULL,
  `rulesetId` bigint(20) DEFAULT NULL,
  `cronExpression` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`jobId`),
  KEY `IX_6A2145F9` (`rulesetId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



/*Table structure for table `cf_OwnerDetails` */

DROP TABLE IF EXISTS `cf_OwnerDetails`;

CREATE TABLE `cf_OwnerDetails` (
  `contactId` varchar(100) COLLATE utf8_bin NOT NULL,
  `ownerName` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `ownerEmail` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`contactId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `cf_PatchStats_Kernel` */

DROP TABLE IF EXISTS `cf_PatchStats_Kernel`;

CREATE TABLE `cf_PatchStats_Kernel` (
  `awsaccount` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `instanceid` varchar(75) COLLATE utf8_bin NOT NULL,
  `rectype` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `ipaddressaws` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `nametag` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `vpcid` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `ipaddressrhs` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `rhshostname` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `systemid` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `gid` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `group_` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `kernel` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `nopendingerratas` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `erratadetails` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `iscompliant` tinyint(4) DEFAULT NULL,
  `isregistered` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`instanceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_Policy` */

DROP TABLE IF EXISTS `cf_Policy`;

CREATE TABLE `cf_Policy` (
  `policyId` varchar(75) COLLATE utf8_bin NOT NULL,
  `policyName` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `policyDesc` text COLLATE utf8_bin,
  `resolution` longtext COLLATE utf8_bin,
  `policyUrl` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `policyVersion` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `createdDate` date DEFAULT NULL,
  `modifiedDate` date DEFAULT NULL,
  PRIMARY KEY (`policyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `cf_Rbac` */

DROP TABLE IF EXISTS `cf_Rbac`;

CREATE TABLE `cf_Rbac` (
  `rbacId` bigint(20) NOT NULL,
  `rbacType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `userOrGroupId` bigint(20) DEFAULT NULL,
  `applicationName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `environmentName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `stackName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `roleName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`rbacId`),
  KEY `IX_18DB1388` (`rbacType`,`userOrGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `cf_RemediationCriteria` */

DROP TABLE IF EXISTS `cf_RemediationCriteria`;

CREATE TABLE `cf_RemediationCriteria` (
  `action` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `matchingString` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `subAction` varchar(200) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;




/*Table structure for table `cf_RuleInstance` */

DROP TABLE IF EXISTS `cf_RuleInstance`;

CREATE TABLE `cf_RuleInstance` (
  `ruleId` varchar(200) COLLATE utf8_bin NOT NULL,
  `ruleUUID` varchar(100) COLLATE utf8_bin NOT NULL,
  `policyId` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `ruleName` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `targetType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `assetGroup` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `alexaKeyword` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `ruleParams` longtext COLLATE utf8_bin,
  `ruleFrequency` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `ruleExecutable` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `ruleRestUrl` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `ruleType` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `ruleArn` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `userId` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `displayName` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` date DEFAULT NULL,
  `modifiedDate` date DEFAULT NULL,
  PRIMARY KEY (`ruleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `cf_SystemConfiguration` */

DROP TABLE IF EXISTS `cf_SystemConfiguration`;

CREATE TABLE `cf_SystemConfiguration` (
  `id_` int(11) DEFAULT NULL,
  `environment` varchar(75) COLLATE utf8_bin NOT NULL,
  `keyname` varchar(75) COLLATE utf8_bin NOT NULL,
  `value` varchar(2000) COLLATE utf8_bin DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`environment`,`keyname`),
  KEY `IX_7196BB48` (`environment`,`keyname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_Target` */

DROP TABLE IF EXISTS `cf_Target`;

CREATE TABLE `cf_Target` (
  `targetName` varchar(75) COLLATE utf8_bin NOT NULL,
  `targetDesc` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `category` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `dataSourceName` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `targetConfig` text COLLATE utf8_bin,
  `status` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `endpoint` text COLLATE utf8_bin,
  `createdDate` date DEFAULT NULL,
  `modifiedDate` date DEFAULT NULL,
  `domain` varchar(75) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`targetName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `cf_pac_updatable_fields` */

DROP TABLE IF EXISTS `cf_pac_updatable_fields`;

CREATE TABLE `cf_pac_updatable_fields` (
  `resourceType` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `displayFields` text COLLATE utf8_bin,
  `updatableFields` longtext COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*Table structure for table `oauth_access_token` */

DROP TABLE IF EXISTS `oauth_access_token`;

CREATE TABLE `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` mediumblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `oauth_approvals` */

DROP TABLE IF EXISTS `oauth_approvals`;

CREATE TABLE `oauth_approvals` (
  `userId` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastModifiedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `oauth_client_details` */

DROP TABLE IF EXISTS `oauth_client_details`;

CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `oauth_client_owner` */

DROP TABLE IF EXISTS `oauth_client_owner`;

CREATE TABLE `oauth_client_owner` (
  `clientId` varchar(75) COLLATE utf8_bin NOT NULL,
  `user` varchar(75) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`clientId`,`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `oauth_client_token` */

DROP TABLE IF EXISTS `oauth_client_token`;

CREATE TABLE `oauth_client_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `oauth_code` */

DROP TABLE IF EXISTS `oauth_code`;

CREATE TABLE `oauth_code` (
  `code` varchar(255) DEFAULT NULL,
  `authentication` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `oauth_refresh_token` */

DROP TABLE IF EXISTS `oauth_refresh_token`;

CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `oauth_user` */

DROP TABLE IF EXISTS `oauth_user`;

CREATE TABLE `oauth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(75) DEFAULT NULL,
  `user_name` varchar(75) DEFAULT NULL,
  `first_name` varchar(75) DEFAULT NULL,
  `last_name` varchar(75) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=340 DEFAULT CHARSET=latin1;

/*Table structure for table `pac_rule_engine_autofix_actions` */

DROP TABLE IF EXISTS `pac_ _engine_autofix_actions`;

CREATE TABLE `pac_rule_engine_autofix_actions` (
  `resourceId` varchar(100) COLLATE utf8_bin NOT NULL,
  `lastActionTime` datetime NOT NULL,
  `action` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`resourceId`,`lastActionTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_projections` */

DROP TABLE IF EXISTS `pac_v2_projections`;

CREATE TABLE `pac_v2_projections` (
  `resourceType` varchar(100) COLLATE utf8_bin NOT NULL,
  `year` decimal(65,0) NOT NULL,
  `quarter` decimal(65,0) NOT NULL,
  `week` decimal(65,0) NOT NULL,
  `projection` bigint(65) DEFAULT NULL,
  PRIMARY KEY (`resourceType`,`year`,`quarter`,`week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_ruleCategory_weightage` */

DROP TABLE IF EXISTS `pac_v2_ruleCategory_weightage`;

CREATE TABLE `pac_v2_ruleCategory_weightage` (
  `ruleCategory` varchar(50) COLLATE utf8_bin NOT NULL,
  `domain` varchar(50) COLLATE utf8_bin NOT NULL,
  `weightage` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ruleCategory`,`domain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_ui_download_filters` */

DROP TABLE IF EXISTS `pac_v2_ui_download_filters`;

CREATE TABLE `pac_v2_ui_download_filters` (
  `serviceId` int(100) NOT NULL AUTO_INCREMENT,
  `serviceName` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `serviceEndpoint` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`serviceId`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_ui_filters` */

DROP TABLE IF EXISTS `pac_v2_ui_filters`;

CREATE TABLE `pac_v2_ui_filters` (
  `filterId` int(25) NOT NULL AUTO_INCREMENT,
  `filterName` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`filterId`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_ui_options` */

DROP TABLE IF EXISTS `pac_v2_ui_options`;

CREATE TABLE `pac_v2_ui_options` (
  `optionId` int(25) NOT NULL AUTO_INCREMENT,
  `filterId` int(25) NOT NULL,
  `optionName` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `optionValue` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `optionURL` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`optionId`),
  KEY `filterId` (`filterId`),
  CONSTRAINT `filterId` FOREIGN KEY (`filterId`) REFERENCES `pac_v2_ui_filters` (`filterId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_ui_widget_faqs` */

DROP TABLE IF EXISTS `pac_v2_ui_widget_faqs`;

CREATE TABLE `pac_v2_ui_widget_faqs` (
  `faqId` int(11) NOT NULL AUTO_INCREMENT,
  `widgetId` int(11) NOT NULL,
  `widgetName` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `faqName` text COLLATE utf8_bin,
  `faqAnswer` text COLLATE utf8_bin,
  PRIMARY KEY (`faqId`),
  KEY `widgetId` (`widgetId`),
  CONSTRAINT `pac_v2_ui_widget_faqs_ibfk_1` FOREIGN KEY (`widgetId`) REFERENCES `pac_v2_ui_widgets` (`widgetId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_ui_widgets` */

DROP TABLE IF EXISTS `pac_v2_ui_widgets`;

CREATE TABLE `pac_v2_ui_widgets` (
  `widgetId` int(11) NOT NULL AUTO_INCREMENT,
  `pageName` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `widgetName` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`widgetId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `pac_v2_userpreferences` */

DROP TABLE IF EXISTS `pac_v2_userpreferences`;

CREATE TABLE `pac_v2_userpreferences` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `defaultAssetGroup` text COLLATE utf8_bin,
  `recentlyViewedAG` text COLLATE utf8_bin,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=336 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `qartz_BLOB_TRIGGERS` */

DROP TABLE IF EXISTS `qartz_BLOB_TRIGGERS`;

CREATE TABLE `qartz_BLOB_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_CALENDARS` */

DROP TABLE IF EXISTS `qartz_CALENDARS`;

CREATE TABLE `qartz_CALENDARS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_CRON_TRIGGERS` */

DROP TABLE IF EXISTS `qartz_CRON_TRIGGERS`;

CREATE TABLE `qartz_CRON_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_FIRED_TRIGGERS` */

DROP TABLE IF EXISTS `qartz_FIRED_TRIGGERS`;

CREATE TABLE `qartz_FIRED_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QARTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QARTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QARTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QARTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QARTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QARTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_JOB_DETAILS` */

DROP TABLE IF EXISTS `qartz_JOB_DETAILS`;

CREATE TABLE `qartz_JOB_DETAILS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QARTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QARTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_LOCKS` */

DROP TABLE IF EXISTS `qartz_LOCKS`;

CREATE TABLE `qartz_LOCKS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_PAUSED_TRIGGER_GRPS` */

DROP TABLE IF EXISTS `qartz_PAUSED_TRIGGER_GRPS`;

CREATE TABLE `qartz_PAUSED_TRIGGER_GRPS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_SCHEDULER_STATE` */

DROP TABLE IF EXISTS `qartz_SCHEDULER_STATE`;

CREATE TABLE `qartz_SCHEDULER_STATE` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_SIMPLE_TRIGGERS` */

DROP TABLE IF EXISTS `qartz_SIMPLE_TRIGGERS`;

CREATE TABLE `qartz_SIMPLE_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_SIMPROP_TRIGGERS` */

DROP TABLE IF EXISTS `qartz_SIMPROP_TRIGGERS`;

CREATE TABLE `qartz_SIMPROP_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_TRIGGERS` */

DROP TABLE IF EXISTS `qartz_TRIGGERS`;

CREATE TABLE `qartz_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QARTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QARTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QARTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QARTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QARTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QARTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QARTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QARTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QARTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QARTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QARTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QARTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `qartz_TRIGGERS` */

DROP TABLE IF EXISTS `oauth_user_role_mapping`;
CREATE TABLE `oauth_user_role_mapping` (
  `userRoleId` varchar(225) DEFAULT NULL,
  `userId` varchar(225) DEFAULT NULL,
  `roleId` varchar(225) DEFAULT NULL,
  `clientId` varchar(300) DEFAULT NULL,
  `allocator` varchar(300) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `oauth_user_credentials`;
CREATE TABLE `oauth_user_credentials` (
    `id` bigint (75),
    `password` varchar (225),
    `type` varchar (225)
);

DROP TABLE IF EXISTS `oauth_user_roles`;
CREATE TABLE `oauth_user_roles` (
  `roleId` varchar(225) DEFAULT NULL,
  `roleName` varchar(225) DEFAULT NULL,
  `roleDesc` varchar(225) DEFAULT NULL,
  `writePermission` int(15) DEFAULT NULL,
  `owner` varchar(225) DEFAULT NULL,
  `client` varchar(225) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/*Insert Data Source to necessary tables*/

INSERT  INTO `cf_Datasource`(`dataSourceId`,`dataSourceName`,`dataSourceDesc`,`config`,`createdDate`,`modifiedDate`) VALUES (1,'aws','Amazon WebService','N/A','2017-08-01','2018-03-09');

/*Insert Data Asset Group to necessary tables*/

INSERT INTO cf_AssetGroupDetails (groupId,groupName,dataSource,displayName,groupType,createdBy,createdUser,createdDate,modifiedUser,modifiedDate,description,aliasQuery,isVisible) VALUES ('201','aws','aws','aws all','admin','Cloud Security','','','pacman','03/26/2018 23:00','Asset Group to segregate all data related to aws.','',true);
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11501','201','ec2','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11502','201','s3','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11503','201','appelb','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11504','201','asg','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11505','201','classicelb','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11506','201','stack','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11507','201','dynamodb','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11508','201','efs','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11509','201','emr','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11510','201','lambda','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11511','201','nat','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11512','201','eni','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11513','201','rdscluster','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11514','201','rdsdb','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11515','201','redshift','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11516','201','sg','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11517','201','snapshot','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11518','201','subnet','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11519','201','targetgroup','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11520','201','volume','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11521','201','vpc','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11522','201','api','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11523','201','iamuser','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11526','201','iamrole','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11527','201','rdssnapshot','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11528','201','account','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11529','201','checks','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11530','201','kms','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11531','201','phd','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11532','201','cloudfront','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11533','201','cert','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11534','201','wafdomain','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11535','201','corpdomain','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11536','201','elasticip','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('11537','201','routetable','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67701','201','internetgateway','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67702','201','launchconfig','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67703','201','networkacl','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67704','201','vpngateway','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67705','201','asgpolicy','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67706','201','snstopic','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67707','201','dhcpoption','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67708','201','peeringconnection','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67709','201','customergateway','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67710','201','vpnconnection','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67711','201','directconnect','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67712','201','virtualinterface','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67713','201','elasticsearch','all','all');
INSERT INTO cf_AssetGroupTargetDetails (id_,groupId,targetType,attributeName,attributeValue) VALUES ('67714','201','elasticache','all','all');

/*Insert Domain in required table*/

INSERT INTO cf_Domain (domainName,domainDesc,config,createdDate,modifiedDate,userId) VALUES ('Infra & Platforms','Domain for Infra & Platforms','{}',{d '2018-04-09'},{d '2018-08-03'},'123');

/*Insert Target data in required table*/
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('account','Aws Accounts','Other','aws','{"key":"accountid","id":"accountid"}','enabled',null,concat(@eshost,':',@esport,'/aws_account/account'),{d '2017-09-07'},{d '2017-09-07'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('api','api','Application Service','aws','{"key":"accountid,region,id","id":"id"}','enabled',null,concat(@eshost,':',@esport,'/aws_api/api'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('appelb','appelb','Compute','aws','{"key":"accountid,region,loadbalancername","id":"loadbalancername"}','enabled',null,concat(@eshost,':',@esport,'/aws_appelb/appelb'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('asg','asg','Compute','aws','{"key":"accountid,region,autoscalinggrouparn","id":"autoscalinggrouparn"}','enabled',null,concat(@eshost,':',@esport,'/aws_asg/asg'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('asgpolicy','ASG Scaling policy','Compute','aws','{"key":"accountid,region,policyname","id":"policyname"}','active',920825,concat(@eshost,':',@esport,'/aws_asgpolicy/asgpolicy'),{d '2017-11-29'},{d '2017-11-29'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('cert','Certificates','Other','aws','{"key":"","id":""}','enabled',null,concat(@eshost,':',@esport,'/aws_cert/cert'),{d '2017-10-24'},{d '2017-10-24'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('checks','Trusted Advisor Checks','Other','aws','{"key":"accountid,checkid","id":"checkid"}','enabled',null,concat(@eshost,':',@esport,'/aws_checks/checks'),{d '2017-09-27'},{d '2017-09-27'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('classicelb','classicelb','Compute','aws','{"key":"accountid,region,loadbalancername","id":"loadbalancername"}','enabled',null,concat(@eshost,':',@esport,'/aws_classicelb/classicelb'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('cloudfront','Cloud Front','Networking & Content Delivery','aws','{"key":"accountid,id","id":"id"}','enabled',null,concat(@eshost,':',@esport,'/aws_cloudfront/cloudfront'),{d '2017-10-24'},{d '2017-10-24'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('corpdomain','Internal CORP Domains','Other','aws','{"key":"","id":""}','enabled',null,concat(@eshost,':',@esport,'/aws_corpdomain/corpdomain'),{d '2017-11-13'},{d '2017-11-13'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('customergateway','Customer Gateway','Networking & Content Delivery','aws','{"key":"accountid,region,customergatewayid","id":"customergatewayid"}','active',20433,concat(@eshost,':',@esport,'/aws_customergateway/customergateway'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('dhcpoption','DHCP Option Sets','Networking & Content Delivery','aws','{"key":"accountid,region,dhcpoptionsid","id":"dhcpoptionsid"}','active',20433,concat(@eshost,':',@esport,'/aws_dhcpoption/dhcpoption'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('directconnect','Direct Connect','Networking & Content Delivery','aws','{"key":"accountid,region,connectionid","id":"connectionid"}','active',20433,concat(@eshost,':',@esport,'/aws_directconnect/directconnect'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('dynamodb','dynamodb','Database','aws','{"key":"accountid,region,tablearn","id":"tablearn"}','enabled',null,concat(@eshost,':',@esport,'/aws_dynamodb/dynamodb'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('ec2','ec2','Compute','aws','{"key":"accountid,region,instanceid","id":"instanceid"}','enabled',null,concat(@eshost,':',@esport,'/aws_ec2/ec2'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('efs','efs','Storage','aws','{"key":"accountid,region,filesystemid","id":"filesystemid"}','enabled',null,concat(@eshost,':',@esport,'/aws_efs/efs'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('elasticip','Elastic IP','Networking & Content Delivery','aws','{"key":"accountid,region,publicip","id":"publicip"}','active',920825,concat(@eshost,':',@esport,'/aws_elasticip/elasticip'),{d '2017-11-29'},{d '2017-11-29'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('elasticsearch','Elasticsearch Service','Analytics','aws','{"key":"accountid,region,domainid","id":"domainid"}','active',20433,concat(@eshost,':',@esport,'/aws_elasticsearch/elasticsearch'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('emr','emr','Analytics','aws','{"key":"accountid,region,id","id":"id"}','enabled',null,concat(@eshost,':',@esport,'/aws_emr/emr'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('eni','eni','Compute','aws','{"key":"accountid,region,networkinterfaceid","id":"networkinterfaceid"}','enabled',null,concat(@eshost,':',@esport,'/aws_eni/eni'),{d '2017-07-13'},{d '2017-07-13'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('iamrole','IAM Role','Identity','aws','{"key":"rolearn","id":"rolearn"}','enabled',null,concat(@eshost,':',@esport,'/aws_iamrole/iamrole'),{d '2017-08-28'},{d '2017-08-28'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('iamuser','IAM User','Identity','aws','{"key":"accountid,username","id":"username"}','enabled',null,concat(@eshost,':',@esport,'/aws_iamuser/iamuser'),{d '2017-08-08'},{d '2017-08-08'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('internetgateway','Internet gate way','Networking & Content Delivery','aws','{"key":"accountid,region,internetgatewayid","id":"internetgatewayid"}','active',920825,concat(@eshost,':',@esport,'/aws_internetgateway/internetgateway'),{d '2017-11-29'},{d '2017-11-29'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('kms','KMS','Identity','aws','{"key":"accountid,region,keyid","id":"keyid"}','enabled',null,concat(@eshost,':',@esport,'/aws_kms/kms'),{d '2017-10-24'},{d '2017-10-24'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('lambda','lambda','Compute','aws','{"key":"accountid,region,functionarn","id":"functionarn"}','enabled',null,concat(@eshost,':',@esport,'/aws_lambda/lambda'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('launchconfig','ASG Launch Configurations','Compute','aws','{"key":"accountid,region,launchconfigurationname","id":"launchconfigurationname"}','active',920825,concat(@eshost,':',@esport,'/aws_launchconfig/launchconfig'),{d '2017-11-29'},{d '2017-11-29'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('nat','nat','Compute','aws','{"key":"accountid,region,natgatewayid","id":"natgatewayid"}','enabled',null,concat(@eshost,':',@esport,'/aws_nat/nat'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('networkacl','Network ACL','Networking & Content Delivery','aws','{"key":"accountid,region,networkaclid","id":"networkaclid"}','active',920825,concat(@eshost,':',@esport,'/aws_networkacl/networkacl'),{d '2017-11-28'},{d '2017-11-28'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('onpremserver','On Premise Linux Servers','Compute','aws','{"key":"name","id":"name"}','active',20433,concat(@eshost,':',@esport,'/aws_onpremserver/onpremserver'),{d '2018-02-23'},{d '2018-02-23'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('peeringconnection','Peering Connection','Networking & Content Delivery','aws','{"key":"accountid,region,vpcpeeringconnectionid","id":"vpcpeeringconnectionid"}','active',20433,concat(@eshost,':',@esport,'/aws_peeringconnection/peeringconnection'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('phd','Personal Dashboard Info','Other','aws','{"key":"accountid,eventarn","id":"eventarn"}','enabled',null,concat(@eshost,':',@esport,'/aws_phd/phd'),{d '2017-10-24'},{d '2017-10-24'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('rdscluster','rdscluster','Database','aws','{"key":"accountid,region,dbclusterarn","id":"dbclusterarn"}','enabled',123,concat(@eshost,':',@esport,'/aws_rdscluster/rdscluster'),{d '2017-07-17'},{d '2018-08-03'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('rdsdb','rdsdb','Database','aws','{"key":"accountid,region,dbclusterarn","id":"dbclusterarn"}','enabled',null,concat(@eshost,':',@esport,'/aws_rdsdb/rdsdb'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('rdssnapshot','RDS Snapshot','Database','aws','{"key":"accountid,region,dbsnapshotidentifier","id":"dbsnapshotidentifier"}','enabled',null,concat(@eshost,':',@esport,'/aws_rdssnapshot/rdssnapshot'),{d '2017-08-28'},{d '2017-08-28'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('redshift','redshift','Database','aws','{"key":"accountid,region,clusteridentifier","id":"clusteridentifier"}','enabled',20433,concat(@eshost,':',@esport,'/aws_redshift/redshift'),{d '2017-07-17'},{d '2017-09-06'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('routetable','Route Table','Networking & Content Delivery','aws','{"key":"accountid,region,routetableid","id":"routetableid"}','active',920825,concat(@eshost,':',@esport,'/aws_routetable/routetable'),{d '2017-11-28'},{d '2017-11-28'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('s3','s3','Storage','aws','{"key":"accountid,region,name","id":"name"}','enabled',null,concat(@eshost,':',@esport,'/aws_s3/s3'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('sg','sg','Compute','aws','{"key":"accountid,region,groupid","id":"groupid"}','enabled',null,concat(@eshost,':',@esport,'/aws_sg/sg'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('snapshot','snapshot','Compute','aws','{"key":"accountid,region,snapshotid","id":"snapshotid"}','enabled',null,concat(@eshost,':',@esport,'/aws_snapshot/snapshot'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('snstopic','Simple Notification Service topics','Application Services','aws','{"key":"accountid,region,topicarn","id":"topicarn"}','active',20433,concat(@eshost,':',@esport,'/aws_snstopic/snstopic'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('stack','stack','Management Tools','aws','{"key":"accountid,region,stackid","id":"stackid"}','enabled',null,concat(@eshost,':',@esport,'/aws_stack/stack'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('subnet','subnet','Compute','aws','{"key":"accountid,region,subnetid","id":"subnetid"}','enabled',null,concat(@eshost,':',@esport,'/aws_subnet/subnet'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('targetgroup','targetgroup','Compute','aws','{"key":"accountid,region,targetgroupname","id":"targetgroupname"}','enabled',null,concat(@eshost,':',@esport,'/aws_targetgroup/targetgroup'),{d '2017-07-17'},{d '2017-07-17'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('virtualinterface','Virtual Interface','Networking & Content Delivery','aws','{"key":"accountid,region,virtualinterfaceid","id":"virtualinterfaceid"}','active',20433,concat(@eshost,':',@esport,'/aws_virtualinterface/virtualinterface'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('volume','volume','Storage','aws','{"key":"accountid,region,volumeid","id":"volumeid"}','enabled',20433,concat(@eshost,':',@esport,'/aws_volume/volume'),{d '2017-07-17'},{d '2017-11-03'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('vpc','vpc','Compute','aws','{"key":"accountid,region,vpcid","id":"vpcid"}','enabled',20433,concat(@eshost,':',@esport,'/aws_vpc/vpc'),{d '2017-07-17'},{d '2017-11-28'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('vpnconnection','VPN Connection','Networking & Content Delivery','aws','{"key":"accountid,region,vpnconnectionid","id":"vpnconnectionid"}','active',20433,concat(@eshost,':',@esport,'/aws_vpnconnection/vpnconnection'),{d '2018-03-26'},{d '2018-03-26'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('vpngateway','VPN Gateway','Networking & Content Delivery','aws','{"key":"accountid,region,vpngatewayid","id":"vpngatewayid"}','active',920825,concat(@eshost,':',@esport,'/aws_vpngateway/vpngateway'),{d '2017-11-29'},{d '2017-11-29'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('wafdomain','WAF Domains','Other','aws','{"key":"","id":""}','enabled',null,concat(@eshost,':',@esport,'/aws_wafdomain/wafdomain'),{d '2017-11-13'},{d '2017-11-13'},'Infra & Platforms');
INSERT INTO cf_Target (targetName,targetDesc,category,dataSourceName,targetConfig,status,userId,endpoint,createdDate,modifiedDate,domain) VALUES ('elasticache','ElastiCache','Database','aws','{"key":"account,region,clustername","id":"arn"}','enabled',null,concat(@eshost,':',@esport,'/aws_elasticache/elasticache'),{d '2017-11-13'},{d '2017-11-13'},'Infra & Platforms');



/* Auth Related data */
insert into `oauth_client_details`(`client_id`,`resource_ids`,`client_secret`,`scope`,`authorized_grant_types`,`web_server_redirect_uri`,`authorities`,`access_token_validity`,`refresh_token_validity`,`additional_information`,`autoapprove`) values ('22e14922-87d7-4ee4-a470-da0bb10d45d3',NULL,'csrWpc5p7JFF4vEZBkwGCAh67kGQGwXv46qug7v5ZwtKg','resource-access','implicit,authorization_code,refresh_token,password,client_credentials',NULL,'ROLE_CLIENT,ROLE_USER',NULL,NULL,NULL,'');
insert into `oauth_user`(`id`,`user_id`,`user_name`,`first_name`,`last_name`,`email`,`created_date`,`modified_date`) values (1,'user@pacbot.org','user','user','','user@pacbot.org','2018-06-26 18:21:56','2018-06-26 18:21:56'),(2,'admin@pacbot.org','admin','admin','','admin@pacbot.org','2018-06-26 18:21:56','2018-06-26 18:21:56');
insert into `oauth_user_credentials` (`id`, `password`, `type`) values('1','$2a$10$IKXbqqHbMBMa/1Cs3VhjGeye4EKVBen4dPwhTYB24cHgDouravEMa','db');
insert into `oauth_user_credentials` (`id`, `password`, `type`) values('2','$2a$10$G02s.dXgFAV7oKvYzvL5luq9FaBuzwNHeBLdbpncBazk5APkiVjUq','db');
insert into `oauth_user_roles`(`roleId`,`roleName`,`roleDesc`,`writePermission`,`owner`,`client`,`createdDate`,`modifiedDate`) values ('1','ROLE_USER','ROLE_USER',0,'asgc','22e14922-87d7-4ee4-a470-da0bb10d45d3','2018-01-23 00:00:00','2018-01-23 00:00:00'),('703','ROLE_ADMIN','ROLE_ADMIN',1,'asgc','22e14922-87d7-4ee4-a470-da0bb10d45d3','2018-03-13 17:26:58','2018-03-13 17:26:58');
insert into `oauth_user_role_mapping`(`userRoleId`,`userId`,`roleId`,`clientId`,`allocator`,`createdDate`,`modifiedDate`) values ('4747c0cf-63cc-4829-a1e8-f1e957ec5dd6','user@pacbot.org','1','22e14922-87d7-4ee4-a470-da0bb10d45d3','user123','2018-01-09 16:11:47','2018-01-09 16:11:47'),('4747c0cf-63cc-4829-a1e8-f1e957ec5dd7','admin@pacbot.org','1','22e14922-87d7-4ee4-a470-da0bb10d45d3','user123','2018-01-09 16:11:47','2018-01-09 16:11:47'),('f5b2a689-c185-11e8-9c73-12d01119b604','admin@pacbot.org','703','22e14922-87d7-4ee4-a470-da0bb10d45d3','user123','2018-01-09 16:11:47','2018-01-09 16:11:47');

/* Display and Update Fields */
INSERT INTO cf_pac_updatable_fields  (resourceType,displayFields,updatableFields) VALUES
 ('all_list','_resourceid,tags.Application,tags.Environment,_entitytype',null),
 ('all_taggable','_resourceid,tags.Application,tags.Environment,_entitytype,targetType,accountid,accountname,region',null),
 ('all_vulnerable','_resourceid,tags.Application,tags.Environment,_entitytype,accountid,accountname,region',null),
 ('all_patchable','_resourceid,tags.Application,tags.Environment,_entitytype',null);


/* Rule and Policy Initialisation */


INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_AWSCloudTrailConfig_version-1','AWSCloudTrailConfig','Cloudtrail logs provide the audit trail of who did what and when. Cloudtrail is enabled by default on all AWS accounts, this should not be turned off any time','Enable cloudtrail for all regions','','version-1','',710383,{d '2017-08-18'},{d '2017-08-18'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_AWSConfigEnabled_version-1','AWSConfigEnabled','AWS Config records all supported resources that it discovers in the region and maintain a timeline for each of the resource. AWS Config should always be in ''enabled'' stated','Enable AWS Config for each region','','version-1','',710383,{d '2017-08-18'},{d '2017-08-18'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Amazon-EC2-Reserved-Instance-Lease-Expiration_version-1','Amazon-EC2-Reserved-Instance-Lease-Expiration','Checks for Amazon EC2 Reserved Instances that are scheduled to expire within the next 30 days or have expired in the preceding 30 days. \nReserved Instances do not renew automatically; you can continue using an EC2 instance covered by the reservation without interruption, \nbut you will be charged On-Demand rates. New Reserved Instances can have the same parameters as the expired ones, or you can purchase \nReserved Instances with different parameters.The estimated monthly savings we show is the difference between the On-Demand and\nReserved Instance rates for the same instance type.\n\nAlert Criteria :\nYellow: The Reserved Instance lease expires in less than 30 days.\nYellow: The Reserved Instance lease expired in the preceding 30 days.','Consider purchasing a new Reserved Instance to replace the one that is nearing the end of its term, For more information \nsee <a href="https://aws.amazon.com/ec2/purchasing-options/reserved-instances/buyer/" target="_blank">How to Purchase Reserved Instances</a>\n<a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ri-market-concepts-buying.html" target="_blank">Buying Reserved Instances</a>,\nAdditional Resources : <a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts-on-demand-reserved-instances.html" target="_blank">Reserved Instances</a>\n<a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html" target="_blank">Instance Types</a>','','version-1','',710383,{d '2018-05-28'},{d '2018-05-28'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_AmazonRDSIdleDBInstancesRule_version-1','AmazonRDSIdleDBInstancesRule','Checks the configuration of your Amazon Relational Database Service (Amazon RDS) for any DB instances that appear to be idle. If a DB instance has not had a connection for a prolonged period of time, you can delete the instance to reduce costs. If persistent storage is needed for data on the instance, you can use lower-cost options such as taking and retaining a DB snapshot. Manually created DB snapshots are retained until you delete them.','Consider taking a snapshot of the idle DB instance and then deleting it,See Deleting a DB Instance with a Final Snapshot','','version-1','',710383,{d '2018-03-15'},{d '2018-03-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ApiGatewayProtected_version-1','ApiGatewayProtected','AWS API gateway resources are by default publicly accessible, all of the API resources should be protected by a Authorizer or a API key. Unprotected API''s can lead to data leaks and security breaches.','Protect the API gateway with an API key OR Use a custom authorizers at the gateway level','','version-1','',710383,{d '2017-08-16'},{d '2017-08-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ApiGatewayProtected_version-2','ApiGatewayProtected','AWS API gateway resources are by default publicly accessible, all of the API resources should be protected by a Authorizer or a API key. Unprotected API''s can lead to data leaks and security breaches.','Protect the API gateway with an API key OR Use a custom authorizers at the gateway level','','version-2','',710383,{d '2017-08-24'},{d '2017-08-24'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ApiGatewayProtected_version-3','ApiGatewayProtected','AWS API gateway resources are by default publicly accessible, all of the API resources should be protected by a Authorizer or a API key. Unprotected API''s can lead to data leaks and security breaches.','Protect the API gateway with an API key OR Use a custom authorizers at the gateway level','','version-3','',333523,{d '2017-09-19'},{d '2017-09-19'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ApiGatewayProtected_version-4','ApiGatewayProtected','AWS API gateway resources are by default publicly accessible, all of the API resources should be protected by a Authorizer or a API key. Unprotected API''s can lead to data leaks and security breaches.','','','version-4','',76355,{d '2018-04-23'},{d '2018-04-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ApplicationTagsShouldBeValid_version-1','ApplicationTagsShouldBeValid','The value of ''Application'' tag  of the asset should be one of the  application names approved by the cloud intake team. Assets with wrong value for Application tag would generally get orphaned from monitoring, patching, centralized access control, etc. Lot of auomations rely on correct application tag and care should be taken to make sure all assets are tagged with correct value for this tag.','Add correct value for the Application tag.','','version-1','',710383,{d '2017-12-07'},{d '2017-12-07'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_AwsRoute53DNSForAccountsRule_version-1','AwsRoute53DNSForAccountsRule','Route 53 service is allowed to be used only in approved accounts. No other accounts should be using Route 53 service. Since Route 53 service is critical service for every application, a controlled environment is required for smooth operations. Also in order stop domain proliferation and enforce best practices, this service  is limited only to these two accounts.','Please work with pacbot@t-mobile.com for migration or exceptions','','version-1','',710383,{d '2018-02-16'},{d '2018-02-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_CheckAwsActivityInBlacklistedRegion_version-1','CheckAwsActivityInBlacklistedRegion','At T-Mobile we primarily use US regions. Any activity in regions outside of US regions is a violation of policy. It generally indicates malicious and un authorized activity','Reach out to pacbot@t-mobile.com for addressing malicious activity,In case of legitimate workloads please reach out to the same team for exception.','','version-1','',710383,{d '2018-02-16'},{d '2018-02-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_CheckEdpRepositoryRule_version-1','CheckEdpRepositoryRule','Every repository should have master and dev branches','','','version-1','',710383,{d '2018-02-28'},{d '2018-02-28'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_CheckGuardDutyIsEnabledForAllAccount_version-1','CheckGuardDutyIsEnabledForAllAccount','All the AWS accounts should have guard duty enabled. Amazon GuardDuty is a managed threat detection service that continuously monitors for malicious or unauthorized behavior to help you protect your AWS accounts and workloads. It monitors for activity such as unusual API calls or potentially unauthorized deployments that indicate a possible account compromise. GuardDuty also detects potentially compromised instances or reconnaissance by attackers','','','version-1','',710383,{d '2018-01-19'},{d '2018-01-19'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_CheckInactiveIamUser_version-1','CheckInactiveIamUser','IAM users who have not logged into AWS and have no API activity for 90 days will be considered inactive IAM users and their accounts will be terminated.','Reach out to pacbot@t-mobile.com for exceptions','','version-1','',710383,{d '2018-02-13'},{d '2018-02-13'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_CheckMongoDBPublicAccess_version-1','CheckMongoDBPublicAccess','To prevent data theft and data loss all Mongo DBs should be protected with access control mechanism. ','Disable anonymous access to MongoDB','','version-1','',2689645,{d '2017-09-01'},{d '2017-09-01'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_CloudWatchEventsForAllAccounts_version-1','CloudWatchEventsForAllAccounts','Events from all AWS account should be routed to a central event bus so that the events and be processed and analyzed centrally.','','','version-1','',710383,{d '2018-01-18'},{d '2018-01-18'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessForPort27017_version-1','EC2WithPublicAccessForPort27017','Global permission to access the well known services like TCP on port 27017 (Mongo DB) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',2689645,{d '2017-08-24'},{d '2017-08-24'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessForPort9600_version-1','EC2WithPublicAccessForPort9600','This rule checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0 for port 9600,if it find any then its an issue.',null,'','version-1','',2689645,{d '2017-08-23'},{d '2017-08-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessForPort9600_version-2','EC2WithPublicAccessForPort9600','This rule checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0 for port 9600,if it find any then its an issue.',null,'','version-2','',333523,{d '2017-08-30'},{d '2017-08-30'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessSSHPort22_version-1','EC2WithPublicAccessSSHPort22','This rule checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0 for SSH port,if it find any then its an issue.',null,'','version-1','',710383,{d '2017-08-18'},{d '2017-08-18'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessSSHPort22_version-2','EC2WithPublicAccessSSHPort22','SSH port 22 should not be accessible from internet. Port 22 should be open only to the internal 10.*.*.* network. Further reducing the permitted IP addresses or ranges allowed to communicate to destination hosts on TCP port 22 is recommended. An exposed SSH port 22 pose a great security risk. Dedicated bastion hosts can have port open to internet with appropriate SSH config.','Remove the rule from the security groups that allows inbound access from 0.0.0.0/0.','','version-2','',2689645,{d '2017-08-23'},{d '2017-08-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessSSHPort9600_version-1','EC2WithPublicAccessSSHPort9600','This rule checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0 for port 9600,if it find any then its an issue.',null,'','version-1','',2689645,{d '2017-08-23'},{d '2017-08-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicIPAccess_version-1','EC2WithPublicIPAccess','EC2 instances should not be directly accessible from internet (Except for the servers in DMZ zone). Ideally these instances should be behind firewall (AWS WAF or any other firewall)','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-08-18'},{d '2017-08-18'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EbsSnapShot_version-1','EbsSnapShot','Depending on the purpose for which the EBS was used, the snapshot might carry sensistive information about our cloud ecosystem or might carry customer PII or CPNI or it could be anything. The cases where we need to make a snpashot public is very rare, those cases have to go through an exception process','Make the snapshot private','','version-1','',710383,{d '2017-08-16'},{d '2017-08-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2InstanceScannedByQualys_version-1','Ec2InstanceScannedByQualys','All assets in Cloud should be scanned by Qualys vulnerability assessment tool atleast once a month. It would be ideal to have the Qulays Cloud Agent installed on all the assets. This would eliminate the need to have manual external scans','Install Qualys Cloud Agent on the server or get the asset scanned manually by VMAS team every month','','version-1','',710383,{d '2017-11-14'},{d '2017-11-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2PublicAccessPortWithS5Vulnerability_version-1','Ec2PublicAccessPortWithS5Vulnerability','An Ec2 instance with remotely exploitable vulnerability (S5) should not be open to internet, this instance can be easily compromised from a remote location','Immediately remove the internet access,Apply the vulnerability fix','','version-1','',710383,{d '2018-01-11'},{d '2018-01-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2PublicAccessPortWithTarget_version-1','Ec2PublicAccessPortWithTarget','Global permission to access the well known services running on privileged ports should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-12-22'},{d '2017-12-22'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2SoppedInstanceForLong_version-2','Ec2SoppedInstanceForLong','EC2 Stopped Instances rule look for the stopped instances which are stopped  for more than 60 days.',null,'','version-2','',1205352,{d '2017-11-06'},{d '2017-11-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2SoppedInstanceForLong_version-3','Ec2SoppedInstanceForLong','EC2 Stopped Instances rule check for the stopped instances which are stopped  for more than 60 days.',null,'','version-3','',1205352,{d '2017-11-06'},{d '2017-11-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2SoppedInstanceForLong_version-4','Ec2SoppedInstanceForLong','EC2 Stopped Instances rule check for the stopped instances for more than 60 days.',null,'','version-4','',1205352,{d '2017-11-06'},{d '2017-11-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2SoppedInstanceForLong_version-5','Ec2SoppedInstanceForLong','EC2 Stopped Instances rule check for instances stopped for more than 60 days.',null,'','version-5','',1205352,{d '2017-11-13'},{d '2017-11-13'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2StoppedInstanceForLong_version-1','Ec2StoppedInstanceForLong','Stopped EC2 instances still incur cost for the volumes,elastic IP associated with it, potential AWS marketplace license costs as well.','Terminate the EC2 instance if it is no longer required.','','version-1','',710383,{d '2017-08-29'},{d '2017-08-29'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccFTP-DataPort20_version-1','Ec2WithPubAccFTP-DataPort20','Global permission to access the well known services like TCP on port 20  should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-10-20'},{d '2017-10-20'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccPort1434_version-1','Ec2WithPubAccPort1434','Global permission to access the well known services like TCP on port 1434 (SQL Browser) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-07'},{d '2017-09-07'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccSMTPPort25_version-1','Ec2WithPubAccSMTPPort25','Global permission to access the well known services like SMTP on port 25 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-10-20'},{d '2017-10-20'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccUDP-Port-53_version-1','Ec2WithPubAccUDP-Port-53','Global permission to access the well known services like UDP on port 53 (Nameservers) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-10-20'},{d '2017-10-20'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessCifsPort445_version-1','Ec2WithPublicAccessCifsPort445','Global permission to access the well known services like TCP on port 445 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-15'},{d '2017-09-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessFtp21port_version-1','Ec2WithPublicAccessFtp21port','Global permission to access the well known services like TCP on port 21 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-10-20'},{d '2017-10-20'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessMySql4333_version-1','Ec2WithPublicAccessMySql4333','Global permission to access the well known services like TCP on port 4333 (MINISQL Server) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-14'},{d '2017-09-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessMySqlPort3306_version-1','Ec2WithPublicAccessMySqlPort3306','Global permission to access the well known services like TCP on port 3306 (MySQL) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNetBIOSPort137_version-1','Ec2WithPublicAccessNetBIOSPort137','Global permission to access the well known services like TCP on port 137 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-15'},{d '2017-09-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNetBIOSPort138_version-1','Ec2WithPublicAccessNetBIOSPort138','Global permission to access the well known services like TCP on port 138 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-15'},{d '2017-09-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNonWebPorts443_version-1','Ec2WithPublicAccessNonWebPorts443','Global permission to access the well known services like TCP on port 443 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNonWebPorts80_version-1','Ec2WithPublicAccessNonWebPorts80','Global permission to access the well known services like HTTP on port 80 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessPort8080_version-1','Ec2WithPublicAccessPort8080','This rule creates an issue, if the port 8080 is open to internet.',null,null,'version-1',null,null,{d '2018-09-07'},{d '2018-09-07'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessPostgreSqlPort5432_version-1','Ec2WithPublicAccessPostgreSqlPort5432','Global permission to access the well known services like TCP on port 5432 (POSTGRESQL) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessRdpPort3389_version-1','Ec2WithPublicAccessRdpPort3389','RDP port 3389 should not be accessible from internet. Port 3389 should be open only to the internal 10.*.*.* network. Further reducing the permitted IP addresses or ranges allowed to communicate to destination hosts on RDP port 3389 is recommended. An exposed RDP port 3389 pose a great security risk.','Remove the rule from the security groups that allows inbound access from 0.0.0.0/0.','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessRpcPort135_version-1','Ec2WithPublicAccessRpcPort135','Global permission to access the well known services like TCP on port 135 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessSmbPort445_version-1','Ec2WithPublicAccessSmbPort445','Global permission to access the well known services like TCP on port 445 should not be allowed','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessSql1433_version-1','Ec2WithPublicAccessSql1433','Global permission to access the well known services like TCP on port 1433 (SQL Server) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessTelnetPort23_version-1','Ec2WithPublicAccessTelnetPort23','Global permission to access the well known services like TCP/UDP on port 23 should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessVncPort5500_version-1','Ec2WithPublicAccessVncPort5500','Global permission to access the well known services like TCP on port 5500 (VNC) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-14'},{d '2017-09-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessVncServerPort5900_version-1','Ec2WithPublicAccessVncServerPort5900','Global permission to access the well known services like TCP on port 5900 (VNC) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-09-14'},{d '2017-09-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithSeverityVulnerability_version-1','Ec2WithSeverityVulnerability','If an EC2 Instance having S5, S4 and S3 vulnerability report it as an issue with severity high, medium and low respectively','','','version-1','',710383,{d '2018-03-08'},{d '2018-03-08'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EveryProjectShouldHaveMasterBranch-Asif_version-1','EveryProjectShouldHaveMasterBranch-Asif','EveryProjectShouldHaveMasterBranch-Asif','','','version-1','',1205352,{d '2018-05-10'},{d '2018-05-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_EveryProjectShouldHaveRepository_version-1','EveryProjectShouldHaveRepository','Every project should have a repository','','','version-1','',710383,{d '2018-03-27'},{d '2018-03-27'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_GuardDutyFindingsExists_version-1','GuardDutyFindingsExists','Amazon GuardDuty is a managed threat detection service that continuously monitors your VPC flow logs, CloudTrail event logs and DNS logs for malicious or unauthorized behavior. When GuardDuty detects a suspicious or unexpected behavior in your AWS account, it generates a finding. A finding is a notification that contains information about a potential security threat identified by the GuardDuty service. The finding details includes data about the finding actor, the AWS resource(s) involved in the suspicious activity, the time when the activity occurred and so on.','Follow the step by step guide line provided for each finding from the Guard Duty console,Please reach out to pacbot@t-mobile.com in case of any queries about how to fix a finding','','version-1','',710383,{d '2018-02-12'},{d '2018-02-12'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_IamAccessKeyRotatedInEvery90Days_version-1','IamAccessKeyRotatedInEvery90Days','Access keys of IAM accounts should be rotated every 90 days in order to decrease the likelihood of accidental exposures and protect  AWS resources against unauthorized access','Rotate the access keys every 90 days','','version-1','',710383,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_IamIdentityProviderWithADFS_version-1','IamIdentityProviderWithADFS','All the AWS accounts should use CORP ADFS identity provider.','','','version-1','',710383,{d '2018-01-15'},{d '2018-01-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_IamPasswordPolicy_version-1','IamPasswordPolicy','Enforce a strong password policy on IAM console authentications. By default AWS does not configure the maximal strength password complexity policy on your behalf.','Log into your AWS console,Go to the IAM service,On the left menu select Password Policy which should be the bottom option,Set the Minimum Password Length form field to 12 (or higher) and Select each of the checkboxes so that all four required  complexity options are selected,Depending on your corporate policy you may wish to allow users to change their own passwords,We recommend that you permit users to do so,Apply your new password policy and you have satisfied this security remediation','','version-1','',710383,{d '2018-01-08'},{d '2018-01-08'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_IamWithPermanentAccessKeysExceptServiceAccount_version-1','IamWithPermanentAccessKeysExceptServiceAccount','Every AWS account is configured one IAM Identity provider. This identity provider  is required for logging into AWS with CORP AD account','Add the CORP AD ADFS provider configuration back to the AWS account','','version-1','',710383,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_IdleLoadBalancerRule_version-1','IdleLoadBalancerRule','Checks your Elastic Load Balancing configuration for load balancers that are not actively used. Any load balancer that is configured accrues charges. If a load balancer has no associated back-end instances or if network traffic is severely limited, the load balancer is not being used effectively.','If your load balancer has no active back-end instance then consider registering instances or deleting your load balancer, SeeÂ Registering Your Amazon EC2 Instances with Your Load BalancerÂ orÂ Delete Your Load Balancer,If your load balancer has had a low request count then consider deleting your load balancer.Â ','','version-1','',710383,{d '2018-02-25'},{d '2018-02-25'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LambdaFunMemorySize_version-1','LambdaFunMemorySize','This rule checks, If the given lambda function''s memory size exceeds more than 512 Mb, then its an issue.',null,'','version-1','',710383,{d '2017-09-05'},{d '2017-09-05'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LambdaFunThrottleInvocationsRule_version-1','LambdaFunThrottleInvocationsRule','Lambda function throttle invocations should not exceed the threshold','Review the code and design and inspect if there is any problem with the logic. If it known and expected behaviour please request for an exception.','','version-1','',710383,{d '2017-10-13'},{d '2017-10-13'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LambdaFunWithAdmin-OrIamPrivileges_version-1','LambdaFunWithAdmin-OrIamPrivileges','Lambda functions should not have administrative permissions (Managed Policy : AdministratorAccess). Least privileges should be granted to lambda functions. Also IAM privileges should never be granted to lambda functions. (Exceptional cases has to be reviewed and prior whitelisting would be required.)','Remove AdministratorAccess policy associated with lambda functions,Remove IAM privileges associated with the lambda function','','version-1','',710383,{d '2018-02-15'},{d '2018-02-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LambdaFunWithAdmin-OrIamPrivileges_version-2','LambdaFunWithAdmin-OrIamPrivileges','Lambda functions should not have administrative permissions (Managed Policy : AdministratorAccess). Least privileges should be granted to lambda functions. Also IAM privileges should never be granted to lambda functions. (Exceptional cases has to be reviewed and prior whitelisting would be required.)','','','version-2','',76355,{d '2018-04-23'},{d '2018-04-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LambdaInvocationRule_version-1','LambdaInvocationRule','AWS Lambda is cheap but is pay per use. An errant lambda function calling itself, cyclic lambda function calls bentween functions can result is huge bills. Any lambda functions that is going to exceed 1 million executions a day should be reviewed.','Review the code and design and inspect if there is any problem with the logic. If it known and expected behaviour please request for an exception.','','version-1','',1205352,{d '2017-09-15'},{d '2017-09-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LambdaWithVPC_version-1','LambdaWithVPC','This rule checks for lambda which are associated with any VPC, if so then creates an issue.',null,'','version-1','',710383,{d '2017-10-26'},{d '2017-10-26'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Lambdacost_version-1','Lambdacost','AWS Lambda is cheap but is pay per use. An errant lambda function calling itself, cyclic lambda function calls bentween functions can result is huge bills. Any lambda functions that is going to exceed 25 dollars should be reviewed.','Review the code and design and inspect if there is any problem with the logic. If it known and expected behaviour please request for an exception.','','version-1','',1205352,{d '2017-09-15'},{d '2017-09-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_LowUtilizationAmazonEC2InstancesRule_version-1','LowUtilizationAmazonEC2InstancesRule','Checks the Amazon Elastic Compute Cloud (Amazon EC2) instances that were running at any time during the last 14 days and alerts you if the daily CPU utilization was 10% or less and network I/O was 5 MB or less on 4 or more days. Running instances generate hourly usage charges. Although some scenarios can result in low utilization by design, you can often lower your costs by managing the number and size of your instances. \nAn instance had 10% or less daily average CPU utilization and 5 MB or less network I/O on at least 4 of the previous 14 days','Consider stopping or terminating instances that have low utilization, or scale the number of instances by using Auto Scaling.','','version-1','',710383,{d '2018-03-12'},{d '2018-03-12'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Non-role-task-members_version-1','Non-role-task-members','Only roles can be a member of a task\nAny task group (t_*) that contains a memberof which is not a role (r_*)','','','version-1','',710383,{d '2018-05-23'},{d '2018-05-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_NonAdminAccountsWithIAMFullAccess_version-1','NonAdminAccountsWithIAMFullAccess','As per AWS policy management standards, only the role named ''Admin'' have access to IAM. No other AWS role is supposed have IAM access.','Remove the IAM privilleges from that role.','','version-1','',710383,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_NonStandardRegionRule_version-1','NonStandardRegionRule','T-Mobile using resources some standard region (us-est/west). As part of this rule if the resource finds non-standard region it should report as violation.',null,null,'version-1',null,null,{d '2018-08-30'},{d '2018-08-30'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Onprem-asset-scanned-by-qualys-API_version-1','Onprem-asset-scanned-by-qualys-API','Onprem assets should be scanned by Qualys vulnerability assessment tool atleast once a month. It would be ideal to have the Qulays Cloud Agent installed on all the assets. This would eliminate the need to have manual external scans','','','version-1','',710383,{d '2018-05-14'},{d '2018-05-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_RdsSnapshotWithPublicAccess_version-1','RdsSnapshotWithPublicAccess','A RDS snapshot may contain sensitive or customer information. No RDS snapshot should be made public from our accounts. There are very rare cases where this might be required. Those cases have to go through exception process. ','Make the snapshot private','','version-1','',710383,{d '2017-08-16'},{d '2017-08-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_RedShiftPublicAccess_version-1','RedShiftPublicAccess','A Redshift snapshot may contain sensitive or customer information. No RDS snapshot should be made public from our accounts. There are very rare cases where this might be required. Those cases have to go through exception process. ','Make the snapshot private','','version-1','',710383,{d '2017-10-09'},{d '2017-10-09'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Repository-complies-with-a-branching-strategy_version-1','Repository-complies-with-a-branching-strategy','This policy checks that repository in Bitbucket follows a matured branching strategy - \n1.Repository should either follow git flow workflow\n2.Or repository should follow trunck based workflow\n3. Or repository follws feature branch based workflow','Follow gitflow workflow branching strategy (https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow), Follow trunk or forking based  branching strategy (https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow), Follow feature branch based workflow (https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow)','','version-1','',710383,{d '2018-04-05'},{d '2018-04-05'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Repository-complies-with-a-branching-strategy_version-2','Repository-complies-with-a-branching-strategy','This policy checks that repository in Bit-bucket follows a mature branching strategy \n1. Repository follows git flow workflow\n2. Or repository follows trunk based workflow \n3. Or repository follows feature branch based workflow','','','version-2','',1205352,{d '2018-06-14'},{d '2018-06-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Repository-complies-with-trunk-based-strategy_version-1','Repository-complies-with-trunk-based-strategy','This policy checks that repository in Bitbucket follows trunk branching strategy\n\n1. Repository should only have a master branch\n2. Repository should only have Feature branches other than Master branch','If there are branches other than Feature, Merge and delete the branches, In future only create feature branches.','','version-1','',710383,{d '2018-04-05'},{d '2018-04-05'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Repository-should-not-have-stale-branch_version-1','Repository-should-not-have-stale-branch','This policy checks if branches in a repository, other than master/develop/release are not active for more than two weeks','','','version-1','',1205352,{d '2018-04-06'},{d '2018-04-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Repository-should-not-have-stale-branch_version-2','Repository-should-not-have-stale-branch','This policy checks if branches, other than master/develop/release had a commit in the last two weeks','','','version-2','',1205352,{d '2018-05-23'},{d '2018-05-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_RepositoryCompliesWithTruckBasedStrategy_version-1','RepositoryCompliesWithTruckBasedStrategy','Repository should only have a master branch','','','version-1','',710383,{d '2018-03-27'},{d '2018-03-27'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_RepositoryShouldNotHaveMoreBranches_version-1','RepositoryShouldNotHaveMoreBranches','Every Repository should not have more than <X> branches at a time','','','version-1','',710383,{d '2018-03-27'},{d '2018-03-27'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Respository-Policy_version-1','Respository-Policy','This policy checks that repository in Bitbucket follows git flow branching strategy - \n1.Repo should have exactly 1 master branch\n2.Repo should have exactly 1 develop branch\n3.Repo should have branches prefixed with /hotfix, /release ,/feature, /bugfix ','Follow gitflow workflow branching strategy (https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)','','version-1','',1205352,{d '2018-03-27'},{d '2018-03-27'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_S3-apptag-policy-by-Asif_version-1','S3-apptag-policy-by-Asif','S3-apptag-policy-by-Asif','','','version-1','',1205352,{d '2018-06-18'},{d '2018-06-18'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_S3BucketWithGlobalReadPermission_version-1','S3BucketWithGlobalReadPermission','Unprotected S3 buckets are one of the major causes for data theft and intrusions. Except for the S3 buckets used for hosting static website, none of the S3 buckets should be globally accessible for unauthenticated users or for Any AWS Authenticate Users.','S3 buckets should be protected by using the bucket ACL and bucket policies,If you want to share data via S3 buckets to other users,you could create pre-signed URLs which will be valid only for short duration.For all automation related work use the bucket policy and grant access to the required roles.','','version-1','',2689645,{d '2017-08-17'},{d '2017-08-17'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_S3BucketWithGlobalWritePermission_version-1','S3BucketWithGlobalWritePermission','AWS S3 buckets cannot be publicly accessed for WRITE actions in order to protect S3 data from unauthorized users. An S3 bucket that allows WRITE (UPLOAD/DELETE) access to everyone (i.e. anonymous users) can provide attackers the capability to add, delete and replace objects within the bucket, which can lead to S3 data loss or unintended changes to applications using that bucket or possibly a huge bill.','Make the S3 bucket private by applying ACLs or bucket policies','','version-1','',2689645,{d '2017-08-17'},{d '2017-08-17'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_SGWithAnywhereAccess_version-1','SGWithAnywhereAccess','It is best practice to allows required ip ranges and specific port in the security groups that will be used for securing EC2 instances in private subnets.','Edit the security groups and allow only specific IP ranges and ports','','version-1','',710383,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_SSMAgentCheckRule_version-1','SSMAgentCheckRule','This rule checks if EC2 instance has SSM agent with pingstatus as Online, if not its an issue','','','version-1','',710383,{d '2018-05-26'},{d '2018-05-26'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ServiceLimitRule_version-1','ServiceLimitRule','All AWS service limits should be extended from time to time based on the growing needs. Cloudformation execution, Auotscalling or A,B deplymnet for production workloads may fail if the service limit is reached  causing downtime. Proactively service limits should be extended when limit thresholds reach 75% or above','Open a case with AWS and increase the service limits','','version-1','',710383,{d '2017-10-17'},{d '2017-10-17'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_SgWithRdpPortHasGlobalAccess_version-1','SgWithRdpPortHasGlobalAccess','Global permission to access the well known services like RDP on port 3389 (Windows RDP) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',710383,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_TaggingRule_version-1','TaggingRule','All AWS assets should be tagged with following mandatory tags. Application,  Environment, Role and Stack. Assets without these mandatory tags will be marked as non-complaint. Below is an example for the tag value pairs.\n\nTag name: Application\nExample value: Rebellion\n\nNotes\nThis value for the application tag should be the approved application name give for the project during the cloud on-boarding process. Unknown applications will be marked for review and possible termination.\n\nTag name: Environment\nExample value: Production or Non Production or Non Production::qat1 or Non Production::dit1 (Refer Naming guide)\n\nNotes\nThe value for environment should distinguish the asset as a Production or Non Production class. You can further qualify Non Production assets using the :: separator. Look at the examples 3 and 4.\n\nTag name: Stack\nExample Value: Apache Httpd\n\nTag name: Role\nExample value: Webserver\n\n \nEach asset should at least have these 4 mandatory tags. You can have additional tags as well.','Add the mandatory tags to the assets,Follow the Cloud Asset Tagging guidelines.','','version-1','',710383,{d '2017-11-02'},{d '2017-11-02'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Underutilized-Amazon-EBS-Volumes_version-1','Underutilized-Amazon-EBS-Volumes','Checks Amazon Elastic Block Store (Amazon EBS) volume configurations and warns when volumes appear to be underused. Charges begin when a volume is created. If a volume remains unattached or has very low write activity (excluding boot volumes) for a period of time, the volume is probably not being used.\n\nAlert Criteria\nYellow: A volume is unattached or had less than 1 IOPS per day for the past 7 days.','Consider creating a snapshot and deleting the volume to reduce costs','','version-1','',710383,{d '2018-05-14'},{d '2018-05-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_UnderutilizedAmazonRedshiftClustersRule_version-1','UnderutilizedAmazonRedshiftClustersRule','Checks your Amazon Redshift configuration for clusters that appear to be underutilized. If an Amazon Redshift cluster has not had a connection for a prolonged period of time or is using a low amount of CPU, you can use lower-cost options such as downsizing the cluster or shutting down the cluster and taking a final snapshot. Final snapshots are retained even after you delete your cluster\nAlert Criteria\nYellow: A running cluster has not had a connection in the last 7 days.\nYellow: A running cluster had less than 5% cluster-wide average CPU utilization for 99% of the last 7 days.','Consider shutting down the cluster and taking a final snapshot, or downsizing the cluster\n','','version-1','',710383,{d '2018-03-14'},{d '2018-03-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_UntaggedOrUnusedEbsRule_version-1','UntaggedOrUnusedEbsRule','This rule checks the untagged or unused Ebs volume to avoid the cost',null,null,'version-1',null,null,{d '2018-08-21'},{d '2018-08-21'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_Unused-Security-group_version-1','Unused-Security-group','Cleaning up un-used security groups is best practice to keep the security groups upto date and relevant.','Delete the unused security groups','','version-1','',710383,{d '2017-10-09'},{d '2017-10-09'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_UnusedApplicationElbRule_version-1','UnusedApplicationElbRule','Un-used assets should be terminated promptly for obvious cost saving reasons','Terminate the ELB if it is no longer required','','version-1','',710383,{d '2017-09-28'},{d '2017-09-28'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_UnusedClassicElbRule_version-1','UnusedClassicElbRule','Un-used assets should be terminated promptly for obvious cost saving reasons','Terminate the ELB if it is no longer required','','version-1','',710383,{d '2017-09-28'},{d '2017-09-28'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_UnusedEBSRule_version-1','UnusedEBSRule','Un-used assets should be terminated promptly for obvious cost saving reasons','Delete the volume if it is no longer required','','version-1','',710383,{d '2017-10-13'},{d '2017-10-13'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_UnusedElasticIpRule_version-1','UnusedElasticIpRule','Checks for Elastic IP addresses (EIPs) that are not associated with a running Amazon Elastic Compute Cloud (Amazon EC2) instance. EIPs are static IP addresses designed for dynamic cloud computing. Unlike traditional static IP addresses, EIPs can mask the failure of an instance or Availability Zone by remapping a public IP address to another instance in your account. A nominal charge is imposed for an EIP that is not associated with a running instance.\n','Associate the EIP with a running active instance, or release the unassociated EIP','','version-1','',710383,{d '2018-02-01'},{d '2018-02-01'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_VpcFlowLogsEnabled_version-1','VpcFlowLogsEnabled','VPC flow logs provide vital information for debugging and forensic exercise in case of any incidents. These should be always enabled','Enable VPC flow logs','','version-1','',710383,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_VpcFlowLogsEnabled_version-2','VpcFlowLogsEnabled','This rule checks  the VPC flow log enabled for a given VPC id,account & region else its an issue',null,'','version-2','',20433,{d '2017-08-21'},{d '2017-08-21'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_apacheserver-public-access_version-1','apacheserver-public-access','This rule check EC2 private IP can be accessed with port 80 to the public',null,'','version-1','',1205352,{d '2017-08-15'},{d '2017-08-15'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-1','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-10','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-10','',333523,{d '2017-09-28'},{d '2017-09-28'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-11','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-11','',333523,{d '2017-09-29'},{d '2017-09-29'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-12','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-12','',333523,{d '2017-10-10'},{d '2017-10-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-13','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-13','',333523,{d '2017-10-13'},{d '2017-10-13'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-14','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-14','',333523,{d '2017-11-02'},{d '2017-11-02'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-2','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-2','',333523,{d '2017-08-16'},{d '2017-08-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-3','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-3','',333523,{d '2017-08-17'},{d '2017-08-17'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-4','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-4','',333523,{d '2017-08-23'},{d '2017-08-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-5','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-5','',333523,{d '2017-08-24'},{d '2017-08-24'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-6','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-6','',333523,{d '2017-08-28'},{d '2017-08-28'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-7','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-7','',333523,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-8','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-8','',333523,{d '2017-09-08'},{d '2017-09-08'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_applicationelb_version-9','applicationelb','This rule checks for Application ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-9','',333523,{d '2017-09-12'},{d '2017-09-12'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_certificate-expiry-policy_version-1','certificate-expiry-policy','All SSL certificates must be renewed before specified days of the expiry and installed in the corresponding system','Renew and install the certficate before the specified threshold','','version-1','',1205352,{d '2017-10-26'},{d '2017-10-26'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_classicelbmandatory_version-1','classicelbmandatory','This rule checks for Classic ELB mandatory tags maintained for given LB in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_cloud-kernel-compliance_version-1','cloud-kernel-compliance','All Linux servers in AWS cloud should be patched as per the quarterly patching criteria published for the entire organization','Patch the operating system as per the criteria defined for the current quarter','','version-1','',1205352,{d '2017-09-14'},{d '2017-09-14'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_dryrundemo_version-1','dryrundemo','Simple policy creation demo','','','version-1','',70245,{d '2018-01-12'},{d '2018-01-12'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ebsmandatorytags_version-1','ebsmandatorytags','This rule checks for EBS mandatory tags maintained for given volume in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ec2deperecatedinstancetype_version-1','ec2deperecatedinstancetype','Deprecated Ec2 instance types (Old generation instance types) should not be used. Using old generation instance types have cost implication, they are not covered in our RI purchase as well','Stop the instance and change the instance type to a newer generation one and start it','','version-1','',1205352,{d '2017-08-11'},{d '2017-08-11'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ec2mandatorytags_version-1','ec2mandatorytags','This rule checks for EC2 mandatory tags maintained for given instance in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_ec2publicaccesstoport9200_version-1','ec2publicaccesstoport9200','Global permission to access the well known services like TCP on Port 9200 (Elastic Search) should not be allowed.','Do not allow global access to well known ports of an EC2 instance directly (except for 80 and 443)','','version-1','',1205352,{d '2017-08-23'},{d '2017-08-23'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_es-access_version-1','es-access','This Rule check for EC2 private IP address is accessible internally.',null,'','version-1','',1205352,{d '2017-08-22'},{d '2017-08-22'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_esearch_version-1','esearch','This rule checks for elastic search accessible internally.',null,'','version-1','',1205352,{d '2017-08-22'},{d '2017-08-22'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_lambdamandatorytags_version-1','lambdamandatorytags','This rule checks for Lambda mandatory tags maintained for given function in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_onpremisekernelversion_version-1','onpremisekernelversion','This rule checks for the on-premise servers kernel version is compliant. If it is not compliant it will create an issue.',null,'','version-1','',1205352,{d '2017-08-16'},{d '2017-08-16'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_rdsdb_version-1','rdsdb','A publicly accessible database end-point would be vulnerable to bruteforce login attempts and subsequent data loss. Unauthorised access should be restircted to minimize security risks.','To restrict access to any publicly accessible RDS database instance you must disable the database Publicly Accessible flag and update the VPC security group associated with the instance.','','version-1','',1205352,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_s3mandatorytags_version-1','s3mandatorytags','This rule checks for S3 mandatory tags maintained for given bucket in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_serverless-rule_version-1','serverless-rule','Serverless rule',null,'','version-1','',20433,{d '2017-09-06'},{d '2017-09-06'});
INSERT INTO cf_Policy (policyId,policyName,policyDesc,resolution,policyUrl,policyVersion,status,userId,createdDate,modifiedDate) VALUES ('PacMan_sgmandatorytags_version-1','sgmandatorytags','This rule checks for Security Group mandatory tags maintained for given SG in AWS account. If any of the mandatory tags are missing it will create an issue.',null,'','version-1','',1205352,{d '2017-08-10'},{d '2017-08-10'});

/* Rule  Initialisation */
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_AmazonRDSIdleDBInstancesRule_version-1_AmazonRDSIdleDBInstancesRule_rdsdb','086273db-c864-46e0-9108-9630f9c4c008','PacMan_AmazonRDSIdleDBInstancesRule_version-1','AmazonRDSIdleDBInstancesRule','rdsdb','aws-all','AmazonRDSIdleDBInstancesRule','{"params":[{"encrypt":false,"value":"Ti39halfu8","key":"checkId"},{"encrypt":false,"value":"check-for-amazon-RDS-idle-DB-instances","key":"ruleKey"},{"encrypt":false,"value":"low","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"costOptimization","key":"ruleCategory"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_AmazonRDSIdleDBInstancesRule_version-1_AmazonRDSIdleDBInstancesRule_rdsdb","autofix":false,"alexaKeyword":"AmazonRDSIdleDBInstancesRule","ruleRestUrl":"","targetType":"rdsdb","pac_ds":"aws","policyId":"PacMan_AmazonRDSIdleDBInstancesRule_version-1","assetGroup":"aws-all","ruleUUID":"086273db-c864-46e0-9108-9630f9c4c008","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/086273db-c864-46e0-9108-9630f9c4c008'),'ENABLED','ASGC','Amazon RDS DB instances should not be idle',{d '2018-03-15'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_ApiGatewayProtected_version-2_APiGatewayProtected_api','849bbeb7-55e9-43be-873f-7c9faa4b7ade','PacMan_ApiGatewayProtected_version-2','APiGatewayProtected','api','aws','APiGatewayProtected','{"params":[{"encrypt":false,"value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":false,"value":"AWS_IAM","key":"authType"},{"encrypt":false,"value":"check-for-api-gateway-protected","key":"ruleKey"},{"encrypt":false,"value":",","key":"splitterChar"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"}],"environmentVariables":[],"ruleId":"PacMan_ApiGatewayProtected_version-2_APiGatewayProtected_api","autofix":false,"alexaKeyword":"APiGatewayProtected","ruleRestUrl":"","targetType":"api","pac_ds":"aws","policyId":"PacMan_ApiGatewayProtected_version-2","assetGroup":"aws","ruleUUID":"849bbeb7-55e9-43be-873f-7c9faa4b7ade","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/849bbeb7-55e9-43be-873f-7c9faa4b7ade'),'ENABLED','ASGC','All publicly accessible API''s behind API gateway should be protected atleast one custom authorizer',{d '2017-08-24'},{d '2018-08-31'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_CheckInactiveIamUser_version-1_CheckInactiveIamUser_iamuser','beca18cd-1fdd-43ce-9171-1af54e398da5','PacMan_CheckInactiveIamUser_version-1','CheckInactiveIamUser','iamuser','aws-all','CheckInactiveIamUser','{"assetGroup":"aws-all","policyId":"PacMan_CheckInactiveIamUser_version-1","environmentVariables":[],"ruleUUID":"beca18cd-1fdd-43ce-9171-1af54e398da5","ruleType":"ManageRule","pac_ds":"aws","targetType":"iamuser","params":[{"encrypt":false,"value":"90","key":"pwdInactiveDuration"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"check-for-inactive-iam-users","key":"ruleKey"},{"encrypt":false,"value":"true","key":"threadsafe"}],"ruleId":"PacMan_CheckInactiveIamUser_version-1_CheckInactiveIamUser_iamuser","autofix":false,"alexaKeyword":"CheckInactiveIamUser","ruleRestUrl":""}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/beca18cd-1fdd-43ce-9171-1af54e398da5'),'ENABLED','710383','IAM users should not be inactive for more than 90 days',{d '2018-02-13'},{d '2018-02-13'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessForPort27017_version-1_EC2WithPublicAccessForConfiguredPort27017_ec2','5e93205d-a394-4fff-8002-97fa9edb6e17','PacMan_EC2WithPublicAccessForPort27017_version-1','EC2WithPublicAccessForConfiguredPort27017','ec2','aws','EC2WithPublicAccessForConfiguredPort27017','{"params":[{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"27017","key":"portToCheck"},{"encrypt":false,"value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_EC2WithPublicAccessForPort27017_version-1_EC2WithPublicAccessForConfiguredPort27017_ec2","autofix":false,"alexaKeyword":"EC2WithPublicAccessForConfiguredPort27017","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_EC2WithPublicAccessForPort27017_version-1","assetGroup":"aws","ruleUUID":"5e93205d-a394-4fff-8002-97fa9edb6e17","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/5e93205d-a394-4fff-8002-97fa9edb6e17'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default MONGO DB port 27017',{d '2017-08-24'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicAccessSSHPort22_version-2_EC2WithPublicAccessForConfiguredPort22_ec2','5e5fb39c-27c4-46f2-823e-4cbfb1c57c65','PacMan_EC2WithPublicAccessSSHPort22_version-2','EC2WithPublicAccessForConfiguredPort22','ec2','aws','EC2WithPublicAccessForConfiguredPort22','{"params":[{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"22","key":"portToCheck"},{"encrypt":false,"value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_EC2WithPublicAccessSSHPort22_version-2_EC2WithPublicAccessForConfiguredPort22_ec2","autofix":false,"alexaKeyword":"EC2WithPublicAccessForConfiguredPort22","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_EC2WithPublicAccessSSHPort22_version-2","assetGroup":"aws","ruleUUID":"5e5fb39c-27c4-46f2-823e-4cbfb1c57c65","ruleType":"ManageRule"}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/5e5fb39c-27c4-46f2-823e-4cbfb1c57c65'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on SSH port 22',{d '2017-08-23'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_EC2WithPublicIPAccess_version-1_Ec2WithPublicAccess_ec2','bbf30a5d-9dfe-463b-a5a2-e872fe201e5a','PacMan_EC2WithPublicIPAccess_version-1','Ec2WithPublicAccess','ec2','aws','Ec2WithPublicAccess','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"check-for-ec2-public-access","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_EC2WithPublicIPAccess_version-1_Ec2WithPublicAccess_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccess","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_EC2WithPublicIPAccess_version-1","assetGroup":"aws","ruleUUID":"bbf30a5d-9dfe-463b-a5a2-e872fe201e5a","ruleType":"ManageRule"}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/bbf30a5d-9dfe-463b-a5a2-e872fe201e5a'),'ENABLED','ASGC','EC2 instances should not have any publicly accessible ports',{d '2017-08-18'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2PublicAccessPortWithTarget_version-1_Ec2PublicAccessPortWithTarget_ec2','2d444634-b39c-4352-865f-445770bd06e9','PacMan_Ec2PublicAccessPortWithTarget_version-1','Ec2PublicAccessPortWithTarget','ec2','aws-all','Ec2PublicAccessPortWithTarget','{"params":[{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"check-for-ec2-with-public-access-port-with-target","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"1024","key":"target"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Ec2PublicAccessPortWithTarget_version-1_Ec2PublicAccessPortWithTarget_ec2","autofix":false,"alexaKeyword":"Ec2PublicAccessPortWithTarget","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2PublicAccessPortWithTarget_version-1","assetGroup":"aws-all","ruleUUID":"2d444634-b39c-4352-865f-445770bd06e9","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/2d444634-b39c-4352-865f-445770bd06e9'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on ports which are < 1024',{d '2017-12-22'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2StoppedInstanceForLong_version-1_Ec2StoppedInstanceForLong_ec2','116f7195-6f7c-4728-a945-42fff3111424','PacMan_Ec2StoppedInstanceForLong_version-1','Ec2StoppedInstanceForLong','ec2','aws','Ec2StoppedInstanceForLong','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"true","key":"threadsafe"},{"encrypt":"false","value":"check-for-stopped-instance-for-long","key":"ruleKey"},{"encrypt":false,"value":"90","key":"targetstoppedDuration"},{"encrypt":false,"value":"governance","key":"ruleCategory"},{"encrypt":false,"value":"low","key":"severity"},{"encrypt":false,"value":"costUrlValue","key":"costUrl"}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2StoppedInstanceForLong_version-1_Ec2StoppedInstanceForLong_ec2","autofix":false,"alexaKeyword":"Ec2StoppedInstanceForLong","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2StoppedInstanceForLong_version-1","assetGroup":"aws","ruleUUID":"116f7195-6f7c-4728-a945-42fff3111424","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/116f7195-6f7c-4728-a945-42fff3111424'),'ENABLED','ASGC','EC2 instances should not be in stopped state for more than 60 days',{d '2017-08-29'},{d '2018-08-31'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccFTP-DataPort20_version-1_Ec2WithPubAccessFTP-DataPort20_ec2','ac8b5056-20cf-4692-95f2-efcadc3bea50','PacMan_Ec2WithPubAccFTP-DataPort20_version-1','Ec2WithPubAccessFTP-DataPort20','ec2','aws','Ec2WithPubAccessFTP-DataPort20','{"params":[{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"20","key":"portToCheck"},{"encrypt":false,"value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Ec2WithPubAccFTP-DataPort20_version-1_Ec2WithPubAccessFTP-DataPort20_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccessFTP-DataPort20","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPubAccFTP-DataPort20_version-1","assetGroup":"aws","ruleUUID":"ac8b5056-20cf-4692-95f2-efcadc3bea50","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/ac8b5056-20cf-4692-95f2-efcadc3bea50'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default port 20',{d '2017-10-20'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccPort1434_version-1_Ec2WithPubAccPort1434_ec2','8b15f4c5-119d-48e4-a85b-4a8288740279','PacMan_Ec2WithPubAccPort1434_version-1','Ec2WithPubAccPort1434','ec2','aws','Ec2WithPubAccPort1434','{"params":[{"encrypt":"false","value":"1434","key":"portToCheck"},{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPubAccPort1434_version-1_Ec2WithPubAccPort1434_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccPort1434","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPubAccPort1434_version-1","assetGroup":"aws","ruleUUID":"8b15f4c5-119d-48e4-a85b-4a8288740279","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/8b15f4c5-119d-48e4-a85b-4a8288740279'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default SQL Browser port 1434',{d '2017-09-11'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccSMTPPort25_version-1_Ec2WithPubAccessSmtpPort25_ec2','eeaa37b3-3c88-48f6-82f6-2822e2e9900d','PacMan_Ec2WithPubAccSMTPPort25_version-1','Ec2WithPubAccessSmtpPort25','ec2','aws','Ec2WithPubAccessSmtpPort25','{"params":[{"encrypt":false,"value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"25","key":"portToCheck"},{"encrypt":false,"value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Ec2WithPubAccSMTPPort25_version-1_Ec2WithPubAccessSmtpPort25_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccessSmtpPort25","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPubAccSMTPPort25_version-1","assetGroup":"aws","ruleUUID":"eeaa37b3-3c88-48f6-82f6-2822e2e9900d","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/eeaa37b3-3c88-48f6-82f6-2822e2e9900d'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on SMTP port 25 ',{d '2017-10-20'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPubAccUDP-Port-53_version-1_Ec2WithPubAccUdpPort53_ec2','351f96cc-e7c3-4930-b619-35b5265a6d42','PacMan_Ec2WithPubAccUDP-Port-53_version-1','Ec2WithPubAccUdpPort53','ec2','aws','Ec2WithPubAccUdpPort53','{"params":[{"encrypt":false,"value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"53","key":"portToCheck"},{"encrypt":false,"value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Ec2WithPubAccUDP-Port-53_version-1_Ec2WithPubAccUdpPort53_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccUdpPort53","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPubAccUDP-Port-53_version-1","assetGroup":"aws","ruleUUID":"351f96cc-e7c3-4930-b619-35b5265a6d42","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/351f96cc-e7c3-4930-b619-35b5265a6d42'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on UDP port 53 ',{d '2017-10-20'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessCifsPort445_version-1_Ec2WithPubAccCIFS445_ec2','e4e793c0-166d-4590-95aa-ab251043962f','PacMan_Ec2WithPublicAccessCifsPort445_version-1','Ec2WithPubAccCIFS445','ec2','aws','Ec2WithPubAccCIFS445','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"445","key":"portToCheck"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessCifsPort445_version-1_Ec2WithPubAccCIFS445_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccCIFS445","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessCifsPort445_version-1","assetGroup":"aws","ruleUUID":"e4e793c0-166d-4590-95aa-ab251043962f","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/e4e793c0-166d-4590-95aa-ab251043962f'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on CIFS port 445 ',{d '2017-09-15'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessFtp21port_version-1_Ec2WithPublicAccessFtp21_ec2','6b948eb4-c900-49c4-ab35-3be12f63aefc','PacMan_Ec2WithPublicAccessFtp21port_version-1','Ec2WithPublicAccessFtp21','ec2','aws','Ec2WithPublicAccessFtp21','{"params":[{"encrypt":false,"value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":false,"value":"igw","key":"internetGateWay"},{"encrypt":false,"value":"21","key":"portToCheck"},{"encrypt":false,"value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"isValueNew":true,"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Ec2WithPublicAccessFtp21port_version-1_Ec2WithPublicAccessFtp21_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessFtp21","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessFtp21port_version-1","assetGroup":"aws","ruleUUID":"6b948eb4-c900-49c4-ab35-3be12f63aefc","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/6b948eb4-c900-49c4-ab35-3be12f63aefc'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 21 ',{d '2017-10-20'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessMySql4333_version-1_Ec2WithPubAcc4333_ec2','33db700b-a7ff-4bcc-8acc-a1194a3dfbd9','PacMan_Ec2WithPublicAccessMySql4333_version-1','Ec2WithPubAcc4333','ec2','aws','Ec2WithPubAcc4333','{"params":[{"encrypt":"false","value":"4333","key":"portToCheck"},{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessMySql4333_version-1_Ec2WithPubAcc4333_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAcc4333","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessMySql4333_version-1","assetGroup":"aws","ruleUUID":"33db700b-a7ff-4bcc-8acc-a1194a3dfbd9","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/33db700b-a7ff-4bcc-8acc-a1194a3dfbd9'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default Mini SQL Server 4333',{d '2017-09-14'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessMySqlPort3306_version-1_Ec2WithPubAccMySqlPort3306_ec2','9bc2345f-7679-4917-9a7c-efc3fd8c6fa3','PacMan_Ec2WithPublicAccessMySqlPort3306_version-1','Ec2WithPubAccMySqlPort3306','ec2','aws','Ec2WithPubAccMySqlPort3306','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"3306","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessMySqlPort3306_version-1_Ec2WithPubAccMySqlPort3306_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccMySqlPort3306","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessMySqlPort3306_version-1","assetGroup":"aws","ruleUUID":"9bc2345f-7679-4917-9a7c-efc3fd8c6fa3","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/9bc2345f-7679-4917-9a7c-efc3fd8c6fa3'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default MySQL port 3306',{d '2017-09-07'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNetBIOSPort137_version-1_Ec2WithPubAccNetBIOS137_ec2','749ba9eb-c59d-4501-8bef-4407bcdc5584','PacMan_Ec2WithPublicAccessNetBIOSPort137_version-1','Ec2WithPubAccNetBIOS137','ec2','aws','Ec2WithPubAccNetBIOS137','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"137","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessNetBIOSPort137_version-1_Ec2WithPubAccNetBIOS137_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccNetBIOS137","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessNetBIOSPort137_version-1","assetGroup":"aws","ruleUUID":"749ba9eb-c59d-4501-8bef-4407bcdc5584","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/749ba9eb-c59d-4501-8bef-4407bcdc5584'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 137',{d '2017-09-15'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNetBIOSPort138_version-1_Ec2WithPubAccNetBIOS138_ec2','461c1e21-5482-435f-88d4-be129e61562b','PacMan_Ec2WithPublicAccessNetBIOSPort138_version-1','Ec2WithPubAccNetBIOS138','ec2','aws','Ec2WithPubAccNetBIOS138','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"138","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessNetBIOSPort138_version-1_Ec2WithPubAccNetBIOS138_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccNetBIOS138","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessNetBIOSPort138_version-1","assetGroup":"aws","ruleUUID":"461c1e21-5482-435f-88d4-be129e61562b","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/461c1e21-5482-435f-88d4-be129e61562b'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 138 ',{d '2017-09-15'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNonWebPorts443_version-1_Ec2WithPublicAccessNonWebPort443_ec2','293f587b-0320-4d18-a717-ac3c7784baf5','PacMan_Ec2WithPublicAccessNonWebPorts443_version-1','Ec2WithPublicAccessNonWebPort443','ec2','aws','Ec2WithPublicAccessNonWebPort443','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"443","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessNonWebPorts443_version-1_Ec2WithPublicAccessNonWebPort443_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessNonWebPort443","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessNonWebPorts443_version-1","assetGroup":"aws","ruleUUID":"293f587b-0320-4d18-a717-ac3c7784baf5","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/293f587b-0320-4d18-a717-ac3c7784baf5'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 443 ',{d '2017-09-06'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessNonWebPorts80_version-1_Ec2WithPublicAccessNonWebPort80_ec2','208c0af9-5eeb-4745-b49b-4fd78a4c66e9','PacMan_Ec2WithPublicAccessNonWebPorts80_version-1','Ec2WithPublicAccessNonWebPort80','ec2','aws','Ec2WithPublicAccessNonWebPort80','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"80","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessNonWebPorts80_version-1_Ec2WithPublicAccessNonWebPort80_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessNonWebPort80","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessNonWebPorts80_version-1","assetGroup":"aws","ruleUUID":"208c0af9-5eeb-4745-b49b-4fd78a4c66e9","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/208c0af9-5eeb-4745-b49b-4fd78a4c66e9'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 80 ',{d '2017-09-06'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessPort8080_version-1_Ec2WithPublicAccessPort8080_ec2','c8a555f2-65c5-40b6-9ab4-4816a89002c8','PacMan_Ec2WithPublicAccessPort8080_version-1','Ec2WithPublicAccessPort8080','ec2','aws-all','Ec2WithPublicAccessPort8080','{"params":[{"key":"internetGateWay","value":"igw","encrypt":false},{"key":"portToCheck","value":"8080","encrypt":false},{"key":"ruleKey","value":"check-for-ec2-with-public-access-for-configured-port","encrypt":false},{"key":"ruleCategory","value":"security","encrypt":false},{"key":"severity","value":"critical","encrypt":false},{"key":"cidrIp","value":"0.0.0.0/0","encrypt":false},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Ec2WithPublicAccessPort8080_version-1_Ec2WithPublicAccessPort8080_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessPort8080","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessPort8080_version-1","assetGroup":"aws-all","ruleUUID":"c8a555f2-65c5-40b6-9ab4-4816a89002c8","ruleType":"ManageRule"}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/c8a555f2-65c5-40b6-9ab4-4816a89002c8'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 8080',{d '2018-09-07'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessPostgreSqlPort5432_version-1_Ec2WithPubAcc5432_ec2','d8726734-387c-4cbd-ba57-cbd327183e20','PacMan_Ec2WithPublicAccessPostgreSqlPort5432_version-1','Ec2WithPubAcc5432','ec2','aws','Ec2WithPubAcc5432','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"5432","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessPostgreSqlPort5432_version-1_Ec2WithPubAcc5432_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAcc5432","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessPostgreSqlPort5432_version-1","assetGroup":"aws","ruleUUID":"d8726734-387c-4cbd-ba57-cbd327183e20","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/d8726734-387c-4cbd-ba57-cbd327183e20'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default POSTGRESQL port 5432',{d '2017-09-14'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessRdpPort3389_version-1_Ec2WithPublicAccessRdpPort3389_ec2','b84fa980-318b-4d6c-83c1-bbf803c2d968','PacMan_Ec2WithPublicAccessRdpPort3389_version-1','Ec2WithPublicAccessRdpPort3389','ec2','aws','Ec2WithPublicAccessRdpPort3389','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"3389","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessRdpPort3389_version-1_Ec2WithPublicAccessRdpPort3389_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessRdpPort3389","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessRdpPort3389_version-1","assetGroup":"aws","ruleUUID":"b84fa980-318b-4d6c-83c1-bbf803c2d968","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/b84fa980-318b-4d6c-83c1-bbf803c2d968'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default WINDOWS RDP port 3389',{d '2017-09-06'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessRpcPort135_version-1_Ec2WithPublicAccessRpcPort135_ec2','685652ca-5a3e-475b-b8d5-243691d3ec8f','PacMan_Ec2WithPublicAccessRpcPort135_version-1','Ec2WithPublicAccessRpcPort135','ec2','aws','Ec2WithPublicAccessRpcPort135','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"135","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"isValueNew":true,"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessRpcPort135_version-1_Ec2WithPublicAccessRpcPort135_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessRpcPort135","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessRpcPort135_version-1","assetGroup":"aws","ruleUUID":"685652ca-5a3e-475b-b8d5-243691d3ec8f","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/685652ca-5a3e-475b-b8d5-243691d3ec8f'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on port 135 ',{d '2017-09-06'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessSql1433_version-1_Ec2WithPubAccSql1433_ec2','af30c037-d958-410e-b84b-3d2b46631592','PacMan_Ec2WithPublicAccessSql1433_version-1','Ec2WithPubAccSql1433','ec2','aws','Ec2WithPubAccSql1433','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"1433","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessSql1433_version-1_Ec2WithPubAccSql1433_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccSql1433","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessSql1433_version-1","assetGroup":"aws","ruleUUID":"af30c037-d958-410e-b84b-3d2b46631592","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/af30c037-d958-410e-b84b-3d2b46631592'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on SQL Server port 1433',{d '2017-09-07'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessTelnetPort23_version-1_Ec2WithPublicAccessTelnetPort23_ec2','7d20c86f-737e-4f27-b093-4905c21d214a','PacMan_Ec2WithPublicAccessTelnetPort23_version-1','Ec2WithPublicAccessTelnetPort23','ec2','aws','Ec2WithPublicAccessTelnetPort23','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"23","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessTelnetPort23_version-1_Ec2WithPublicAccessTelnetPort23_ec2","autofix":false,"alexaKeyword":"Ec2WithPublicAccessTelnetPort23","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessTelnetPort23_version-1","assetGroup":"aws","ruleUUID":"7d20c86f-737e-4f27-b093-4905c21d214a","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/7d20c86f-737e-4f27-b093-4905c21d214a'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on Telnet port 23 ',{d '2017-09-06'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessVncPort5500_version-1_Ec2WithPubAccVNC5500_ec2','0844b81c-9f29-4d84-afde-d663b1c38921','PacMan_Ec2WithPublicAccessVncPort5500_version-1','Ec2WithPubAccVNC5500','ec2','aws','Ec2WithPubAccVNC5500','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"5500","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"isValueNew":true,"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessVncPort5500_version-1_Ec2WithPubAccVNC5500_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccVNC5500","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessVncPort5500_version-1","assetGroup":"aws","ruleUUID":"0844b81c-9f29-4d84-afde-d663b1c38921","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/0844b81c-9f29-4d84-afde-d663b1c38921'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on VNC port 5500',{d '2017-09-14'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Ec2WithPublicAccessVncServerPort5900_version-1_Ec2WithPubAccVncServerPOrt5900_ec2','a6d197e2-f650-4de1-a215-b333688b0b25','PacMan_Ec2WithPublicAccessVncServerPort5900_version-1','Ec2WithPubAccVncServerPOrt5900','ec2','aws','Ec2WithPubAccVncServerPOrt5900','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"5900","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_Ec2WithPublicAccessVncServerPort5900_version-1_Ec2WithPubAccVncServerPOrt5900_ec2","autofix":false,"alexaKeyword":"Ec2WithPubAccVncServerPOrt5900","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_Ec2WithPublicAccessVncServerPort5900_version-1","assetGroup":"aws","ruleUUID":"a6d197e2-f650-4de1-a215-b333688b0b25","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/a6d197e2-f650-4de1-a215-b333688b0b25'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on VNC port 5900',{d '2017-09-15'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_IamAccessKeyRotatedInEvery90Days_version-1_IamAccessKeyRotatedInEvery90Days_iamuser','ccf26226-eb53-4761-8ecb-d54901ec3533','PacMan_IamAccessKeyRotatedInEvery90Days_version-1','IamAccessKeyRotatedInEvery90Days','iamuser','aws','IamAccessKeyRotatedInEvery90Days','{"assetGroup":"aws","policyId":"PacMan_IamAccessKeyRotatedInEvery90Days_version-1","environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleUUID":"ccf26226-eb53-4761-8ecb-d54901ec3533","ruleType":"ManageRule","pac_ds":"aws","targetType":"iamuser","params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"check-for-accesskeys-rotated-in-every-90-days","key":"ruleKey"},{"encrypt":false,"value":"high","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"security","key":"ruleCategory"}],"ruleId":"PacMan_IamAccessKeyRotatedInEvery90Days_version-1_IamAccessKeyRotatedInEvery90Days_iamuser","autofix":false,"alexaKeyword":"IamAccessKeyRotatedInEvery90Days","ruleRestUrl":""}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/ccf26226-eb53-4761-8ecb-d54901ec3533'),'ENABLED','1205352','IAM accesskey must be rotated every 90 days',{d '2017-08-30'},{d '2018-01-05'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_IamPasswordPolicy_version-1_IamPasswordPolicy_account','99403bda-8f25-448b-a1f3-16450c7c611f','PacMan_IamPasswordPolicy_version-1','IamPasswordPolicy','account','aws-all','IamPasswordPolicy','{"assetGroup":"aws-all","policyId":"PacMan_IamPasswordPolicy_version-1","environmentVariables":[],"ruleUUID":"99403bda-8f25-448b-a1f3-16450c7c611f","ruleType":"ManageRule","pac_ds":"aws","targetType":"account","params":[{"encrypt":false,"value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":false,"value":"check-iam-password-policy","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"90","key":"maxPasswordAge"},{"encrypt":false,"value":"true","key":"requireSymbols"},{"encrypt":false,"value":"true","key":"requireNumbers"},{"encrypt":false,"value":"true","key":"requireUppercaseCharacters"},{"encrypt":false,"value":"true","key":"requireLowercaseCharacters"},{"encrypt":false,"value":"true","key":"allowUsersToChangePassword"},{"encrypt":false,"value":"true","key":"expirePasswords"},{"encrypt":false,"value":"false","key":"hardExpiry"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"14","key":"minPasswordLength"},{"encrypt":false,"value":"24","key":"lastPasswordsToRemember"},{"encrypt":false,"value":"iam-password-policy-fix","key":"fixKey"}],"ruleId":"PacMan_IamPasswordPolicy_version-1_IamPasswordPolicy_account","autofix":true,"alexaKeyword":"IamPasswordPolicy","ruleRestUrl":""}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/99403bda-8f25-448b-a1f3-16450c7c611f'),'ENABLED','1205352','All AWS accounts should follow the IAM password policy',{d '2018-01-08'},{d '2018-06-29'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_IdleLoadBalancerRule_version-1_IdleLoadbalancerRule_classicelb','8b212299-7386-4638-b4a3-321fe4ff0daa','PacMan_IdleLoadBalancerRule_version-1','IdleLoadbalancerRule','classicelb','aws-all','IdleLoadBalancer','{"params":[{"encrypt":false,"value":"check-for-idle-load-balancers","key":"ruleKey"},{"encrypt":false,"value":"hjLMh88uM8","key":"checkId"},{"encrypt":false,"value":"low","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"costOptimization","key":"ruleCategory"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_IdleLoadBalancerRule_version-1_IdleLoadbalancerRule_classicelb","autofix":false,"alexaKeyword":"IdleLoadBalancer","ruleRestUrl":"","targetType":"classicelb","pac_ds":"aws","policyId":"PacMan_IdleLoadBalancerRule_version-1","assetGroup":"aws-all","ruleUUID":"8b212299-7386-4638-b4a3-321fe4ff0daa","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/8b212299-7386-4638-b4a3-321fe4ff0daa'),'ENABLED','ASGC','Loadbalncer''s should not be idle ',{d '2018-02-25'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_LowUtilizationAmazonEC2InstancesRule_version-1_LowUtilizationAmazonEC2InstancesRule_ec2','0b72ce81-1b93-4ef6-abf2-8843459b04a3','PacMan_LowUtilizationAmazonEC2InstancesRule_version-1','LowUtilizationAmazonEC2InstancesRule','ec2','aws-all','LowUtilizationAmazonEC2InstancesRule','{"params":[{"encrypt":false,"value":"check-for-low-utilization-amazon-ec2-instance","key":"ruleKey"},{"encrypt":false,"value":"Qch7DwouX1","key":"checkId"},{"encrypt":false,"value":"low","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"costOptimization","key":"ruleCategory"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_LowUtilizationAmazonEC2InstancesRule_version-1_LowUtilizationAmazonEC2InstancesRule_ec2","autofix":false,"alexaKeyword":"LowUtilizationAmazonEC2InstancesRule","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_LowUtilizationAmazonEC2InstancesRule_version-1","assetGroup":"aws-all","ruleUUID":"0b72ce81-1b93-4ef6-abf2-8843459b04a3","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/0b72ce81-1b93-4ef6-abf2-8843459b04a3'),'ENABLED','ASGC','Amazon EC2 instances should not have low utilization',{d '2018-03-12'},{d '2018-09-05'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_NonAdminAccountsWithIAMFullAccess_version-1_IAMAccessGrantForNonAdminAccountRule_iamrole','f0d1e104-d930-4e52-88ca-90ff2148311f','PacMan_NonAdminAccountsWithIAMFullAccess_version-1','IAMAccessGrantForNonAdminAccountRule','iamrole','aws','IAMAccessGrantForNonAdminAccountRule','{"assetGroup":"aws","policyId":"PacMan_NonAdminAccountsWithIAMFullAccess_version-1","environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleUUID":"f0d1e104-d930-4e52-88ca-90ff2148311f","ruleType":"ManageRule","pac_ds":"aws","targetType":"iamrole","params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"Admin","key":"adminRolesToCompare"},{"encrypt":"false","value":"check-non-admin-accounts-for-iamfullccess","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"high","key":"severity"}],"ruleId":"PacMan_NonAdminAccountsWithIAMFullAccess_version-1_IAMAccessGrantForNonAdminAccountRule_iamrole","autofix":false,"alexaKeyword":"IAMAccessGrantForNonAdminAccountRule","ruleRestUrl":""}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/f0d1e104-d930-4e52-88ca-90ff2148311f'),'ENABLED','710383','Non Admin IAM roles should not have full IAM access',{d '2017-08-31'},{d '2018-02-09'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_RdsSnapshotWithPublicAccess_version-1_RdsSnapshotWithPublicAccess_rdssnapshot','577c5a2b-328f-45f7-b22f-39ecd77eb114','PacMan_RdsSnapshotWithPublicAccess_version-1','RdsSnapshotWithPublicAccess','rdssnapshot','aws','RdsSnapshotWithPublicAccess','{"params":[{"encrypt":"false","value":"check-for-rds-snapshot-with-public-access","key":"ruleKey"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"rSs93HQwa1","key":"checkId"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_RdsSnapshotWithPublicAccess_version-1_RdsSnapshotWithPublicAccess_rdssnapshot","autofix":false,"alexaKeyword":"RdsSnapshotWithPublicAccess","ruleRestUrl":"","targetType":"rdssnapshot","pac_ds":"aws","policyId":"PacMan_RdsSnapshotWithPublicAccess_version-1","assetGroup":"aws","ruleUUID":"577c5a2b-328f-45f7-b22f-39ecd77eb114","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/577c5a2b-328f-45f7-b22f-39ecd77eb114'),'ENABLED','ASGC','RDS snapshot should not be publicly accessible',{d '2017-08-31'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_S3BucketWithGlobalReadPermission_version-1_s3globalread_s3','1665f681-1b24-4d89-b948-15d85e3c4c13','PacMan_S3BucketWithGlobalReadPermission_version-1','s3globalread','s3','aws','s3globalread','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"apikey","key":"apiKeyValue"},{"encrypt":"false","value":"x-api-key","key":"apiKeyName"},{"encrypt":"false","value":"check-for-s3-global-read-access","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"Pfx0RwqBli","key":"checkId"},{"encrypt":false,"value":"/aws_checks/checks_resources/_search","key":"esServiceURL"},{"encrypt":false,"value":"https://apiurl/test/%s","key":"apiGWURL"},{"encrypt":false,"value":"s3-global-access-fix","key":"fixKey"}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_S3BucketWithGlobalReadPermission_version-1_s3globalread_s3","autofix":true,"alexaKeyword":"s3globalread","ruleRestUrl":"","targetType":"s3","pac_ds":"aws","policyId":"PacMan_S3BucketWithGlobalReadPermission_version-1","assetGroup":"aws","ruleUUID":"1665f681-1b24-4d89-b948-15d85e3c4c13","ruleType":"ManageRule"}','0 0/1 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/1665f681-1b24-4d89-b948-15d85e3c4c13'),'ENABLED','ASGC','Non whitelisted S3 buckets should not be publicly accessible for read',{d '2017-08-18'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_S3BucketWithGlobalWritePermission_version-1_s3globalwrite_s3','986b0ba3-6af8-40aa-830b-2885b2ae1973','PacMan_S3BucketWithGlobalWritePermission_version-1','s3globalwrite','s3','aws','s3globalwrite','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"apikey","key":"apiKeyValue"},{"encrypt":"false","value":"x-api-key","key":"apiKeyName"},{"encrypt":"false","value":"check-for-s3-global-write-access","key":"ruleKey"},{"encrypt":"false","value":"url","key":"apiGWURL"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"Pfx0RwqBli","key":"checkId"},{"isValueNew":true,"encrypt":false,"value":"s3-global-access-fix","key":"fixKey"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_S3BucketWithGlobalWritePermission_version-1_s3globalwrite_s3","autofix":false,"alexaKeyword":"s3globalwrite","ruleRestUrl":"","targetType":"s3","pac_ds":"aws","policyId":"PacMan_S3BucketWithGlobalWritePermission_version-1","assetGroup":"aws","ruleUUID":"986b0ba3-6af8-40aa-830b-2885b2ae1973","ruleType":"ManageRule"}','0 0/1 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/986b0ba3-6af8-40aa-830b-2885b2ae1973'),'ENABLED','ASGC','Non whitelisted S3 buckets should not be publicly accessible for write',{d '2017-08-18'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_SGWithAnywhereAccess_version-1_SgWithAnywhereAccess_sg','bf7a4928-7c82-4bc7-83a8-2dac63e75678','PacMan_SGWithAnywhereAccess_version-1','SgWithAnywhereAccess','sg','aws','SgWithAnywhereAccess','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"22","key":"portToCheck"},{"encrypt":"false","value":"check-for-security-group-global-access","key":"ruleKey"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_SGWithAnywhereAccess_version-1_SgWithAnywhereAccess_sg","autofix":false,"alexaKeyword":"SgWithAnywhereAccess","ruleRestUrl":"","targetType":"sg","pac_ds":"aws","policyId":"PacMan_SGWithAnywhereAccess_version-1","assetGroup":"aws","ruleUUID":"bf7a4928-7c82-4bc7-83a8-2dac63e75678","ruleType":"ManageRule"}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/bf7a4928-7c82-4bc7-83a8-2dac63e75678'),'ENABLED','ASGC','Security groups should not have inbound rule allowing 0.0.0.0/0 for non DMZ resources',{d '2017-08-11'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_SgWithRdpPortHasGlobalAccess_version-1_SgWithRdpPortHasOpenAccess_sg','733ef891-ee1e-4e2c-b2bf-bdf8fe86a470','PacMan_SgWithRdpPortHasGlobalAccess_version-1','SgWithRdpPortHasOpenAccess','sg','aws','SgWithRdpPortHasOpenAccess','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"3389","key":"portToCheck"},{"encrypt":"false","value":"check-for-security-group-rdp-port-global-access","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_SgWithRdpPortHasGlobalAccess_version-1_SgWithRdpPortHasOpenAccess_sg","autofix":false,"alexaKeyword":"SgWithRdpPortHasOpenAccess","ruleRestUrl":"","targetType":"sg","pac_ds":"aws","policyId":"PacMan_SgWithRdpPortHasGlobalAccess_version-1","assetGroup":"aws","ruleUUID":"733ef891-ee1e-4e2c-b2bf-bdf8fe86a470","ruleType":"ManageRule"}','0 0/6 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/733ef891-ee1e-4e2c-b2bf-bdf8fe86a470'),'ENABLED','ASGC','Security group with RDP port 3389 should not be open to internet',{d '2017-08-11'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Underutilized-Amazon-EBS-Volumes_version-1_Underutilized-EBS-Volumes_volume','58f422d4-8b26-4571-b3b3-1becf7420495','PacMan_Underutilized-Amazon-EBS-Volumes_version-1','Underutilized EBS Volumes','volume','aws-all','Underutilized Amazon EBS Volumes','{"params":[{"encrypt":false,"value":"check-for-underutilized-EBS-Volumes","key":"ruleKey"},{"encrypt":false,"value":"DAvU99Dc4C","key":"checkId"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"costOptimization","key":"ruleCategory"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Underutilized-Amazon-EBS-Volumes_version-1_Underutilized-EBS-Volumes_volume","autofix":false,"alexaKeyword":"Underutilized Amazon EBS Volumes","ruleRestUrl":"","targetType":"volume","pac_ds":"aws","policyId":"PacMan_Underutilized-Amazon-EBS-Volumes_version-1","assetGroup":"aws-all","ruleUUID":"58f422d4-8b26-4571-b3b3-1becf7420495","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/58f422d4-8b26-4571-b3b3-1becf7420495'),'ENABLED','ASGC','Amazon EBS volumes should not be underutilized ',{d '2018-05-14'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_UnderutilizedAmazonRedshiftClustersRule_version-1_UnderutilizedAmazonRedshiftClustersRule_redshift','da5618be-4e58-41a3-b2fb-657cd820231c','PacMan_UnderutilizedAmazonRedshiftClustersRule_version-1','UnderutilizedAmazonRedshiftClustersRule','redshift','aws-all','UnderutilizedAmazonRedshiftClustersRule','{"params":[{"encrypt":false,"value":"check-for-under-utilized-amazon-redshift-clusters","key":"ruleKey"},{"encrypt":false,"value":"low","key":"severity"},{"encrypt":false,"value":"G31sQ1E9U","key":"checkId"},{"isValueNew":true,"encrypt":false,"value":"costOptimization","key":"ruleCategory"},{"key":"esServiceURL","value":"/aws_checks/checks_resources/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_UnderutilizedAmazonRedshiftClustersRule_version-1_UnderutilizedAmazonRedshiftClustersRule_redshift","autofix":false,"alexaKeyword":"UnderutilizedAmazonRedshiftClustersRule","ruleRestUrl":"","targetType":"redshift","pac_ds":"aws","policyId":"PacMan_UnderutilizedAmazonRedshiftClustersRule_version-1","assetGroup":"aws-all","ruleUUID":"da5618be-4e58-41a3-b2fb-657cd820231c","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/da5618be-4e58-41a3-b2fb-657cd820231c'),'ENABLED','ASGC','Amazon Redshift clusters should not be underutilized',{d '2018-03-14'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_Unused-Security-group_version-1_UnusedSecurityGroup_sg','420fd3b3-716c-4b01-b27b-8c731b9d2986','PacMan_Unused-Security-group_version-1','UnusedSecurityGroup','sg','aws','UnusedSecurityGroup','{"params":[{"encrypt":false,"value":"check-for-unused-security-group","key":"ruleKey"},{"encrypt":false,"value":"governance","key":"ruleCategory"},{"encrypt":false,"value":"low","key":"severity"},{"encrypt":false,"value":",","key":"splitterChar"},{"key":"esServiceWithSgUrl","value":"/aws/ec2_secgroups/_search,/aws/rdsdb_secgroups/_search,/aws/rdscluster_secgroups/_search,/aws/redshift_secgroups/_search,/aws_lambda/lambda_secgroups/_search,/aws_appelb/appelb_secgroups/_search,/aws_classicelb/classicelb_secgroups/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_Unused-Security-group_version-1_UnusedSecurityGroup_sg","autofix":false,"alexaKeyword":"UnusedSecurityGroup","ruleRestUrl":"","targetType":"sg","pac_ds":"aws","policyId":"PacMan_Unused-Security-group_version-1","assetGroup":"aws","ruleUUID":"420fd3b3-716c-4b01-b27b-8c731b9d2986","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/420fd3b3-716c-4b01-b27b-8c731b9d2986'),'ENABLED','ASGC','Security groups should not be in unused state',{d '2017-10-16'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_UnusedApplicationElbRule_version-1_UnusedApplicationElbRule_appelb','f6a49af1-caaf-4d32-8dee-df239ef06248','PacMan_UnusedApplicationElbRule_version-1','UnusedApplicationElbRule','appelb','aws','UnusedApplicationElbRule','{"params":[{"encrypt":"false","value":"check-for-unused-application-elb","key":"ruleKey"},{"encrypt":false,"value":"governance","key":"ruleCategory"},{"encrypt":false,"value":"low","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"costUrlValue","key":"costUrl"},{"key":"esAppElbWithInstanceUrl","value":"/aws/appelb_instances/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_UnusedApplicationElbRule_version-1_UnusedApplicationElbRule_appelb","autofix":false,"alexaKeyword":"UnusedApplicationElbRule","ruleRestUrl":"","targetType":"appelb","pac_ds":"aws","policyId":"PacMan_UnusedApplicationElbRule_version-1","assetGroup":"aws","ruleUUID":"f6a49af1-caaf-4d32-8dee-df239ef06248","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/f6a49af1-caaf-4d32-8dee-df239ef06248'),'ENABLED','ASGC','Application ELB should not be in unused state',{d '2017-09-28'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_UnusedClassicElbRule_version-1_UnusedClassicElbRule_classicelb','83b71ecd-ae53-46f1-b684-4dc203028fb7','PacMan_UnusedClassicElbRule_version-1','UnusedClassicElbRule','classicelb','aws','UnusedClassicElbRule','{"params":[{"encrypt":false,"value":"check-for-unused-classic-elb","key":"ruleKey"},{"encrypt":false,"value":"true","key":"threadsafe"},{"encrypt":false,"value":"governance","key":"ruleCategory"},{"encrypt":false,"value":"low","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"costUrlValue","key":"costUrl"},{"key":"esClassicElbWithInstanceUrl","value":"/aws/classicelb_instances/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"sdfsd","key":"sdf"}],"ruleId":"PacMan_UnusedClassicElbRule_version-1_UnusedClassicElbRule_classicelb","autofix":false,"alexaKeyword":"UnusedClassicElbRule","ruleRestUrl":"","targetType":"classicelb","pac_ds":"aws","policyId":"PacMan_UnusedClassicElbRule_version-1","assetGroup":"aws","ruleUUID":"83b71ecd-ae53-46f1-b684-4dc203028fb7","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/83b71ecd-ae53-46f1-b684-4dc203028fb7'),'ENABLED','ASGC','Classic ELB should not be in unused state',{d '2017-09-28'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_UnusedEBSRule_version-1_UnusedEbsRule_volume','a14127e5-0454-49f8-9337-0cb38862e20c','PacMan_UnusedEBSRule_version-1','UnusedEbsRule','volume','aws','UnusedEBSRule','{"params":[{"encrypt":false,"value":"check-for-unused-ebs-rule","key":"ruleKey"},{"encrypt":false,"value":"governance","key":"ruleCategory"},{"encrypt":false,"value":"costUrlValue","key":"costUrl"},{"encrypt":false,"value":"low","key":"severity"},{"key":"esEbsWithInstanceUrl","value":"/aws/volume_attachments/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_UnusedEBSRule_version-1_UnusedEbsRule_volume","autofix":false,"alexaKeyword":"UnusedEBSRule","ruleRestUrl":"","targetType":"volume","pac_ds":"aws","policyId":"PacMan_UnusedEBSRule_version-1","assetGroup":"aws","ruleUUID":"a14127e5-0454-49f8-9337-0cb38862e20c","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/a14127e5-0454-49f8-9337-0cb38862e20c'),'ENABLED','ASGC','EBS volumes should not be in unused state',{d '2017-10-13'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_UnusedElasticIpRule_version-1_UnusedElasticIpRule_elasticip','09159bf1-a452-4746-bccf-6f9b162824ab','PacMan_UnusedElasticIpRule_version-1','UnusedElasticIpRule','elasticip','aws-all','UnusedElasticIpRule','{"params":[{"encrypt":false,"value":"check-for-unused-elastic-ip","key":"ruleKey"},{"encrypt":false,"value":"high","key":"severity"},{"encrypt":false,"value":"governance","key":"ruleCategory"},{"key":"esElasticIpUrl","value":"/aws_elasticip/elasticip/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[],"ruleId":"PacMan_UnusedElasticIpRule_version-1_UnusedElasticIpRule_elasticip","autofix":false,"alexaKeyword":"UnusedElasticIpRule","ruleRestUrl":"","targetType":"elasticip","pac_ds":"aws","policyId":"PacMan_UnusedElasticIpRule_version-1","assetGroup":"aws-all","ruleUUID":"09159bf1-a452-4746-bccf-6f9b162824ab","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/09159bf1-a452-4746-bccf-6f9b162824ab'),'ENABLED','ASGC','Elastic Ip''s should not be in unused state',{d '2018-02-01'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_VpcFlowLogsEnabled_version-1_VpcFlowLogsEnabled_vpc','ac546315-625a-43e1-aca6-50f5c2bcd450','PacMan_VpcFlowLogsEnabled_version-1','VpcFlowLogsEnabled','vpc','aws','VpcFlowLogsEnabled','{"params":[{"encrypt":"false","value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":"false","value":"check-for-vpc-flowlog-enabled","key":"ruleKey"},{"encrypt":false,"value":"high","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"security","key":"ruleCategory"}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_VpcFlowLogsEnabled_version-1_VpcFlowLogsEnabled_vpc","autofix":false,"alexaKeyword":"VpcFlowLogsEnabled","ruleRestUrl":"","targetType":"vpc","pac_ds":"aws","policyId":"PacMan_VpcFlowLogsEnabled_version-1","assetGroup":"aws","ruleUUID":"ac546315-625a-43e1-aca6-50f5c2bcd450","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/ac546315-625a-43e1-aca6-50f5c2bcd450'),'ENABLED','ASGC','VPC flowlogs should be enabled for all VPCs',{d '2017-08-11'},{d '2018-08-31'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_ec2deperecatedinstancetype_version-1_ec2deprecatedinstancetype_ec2','86bd22f7-e3b1-413b-a651-03bc7cddad2f','PacMan_ec2deperecatedinstancetype_version-1','ec2deprecatedinstancetype','ec2','aws','ec2deprecatedinstancetype','{"params":[{"encrypt":false,"value":"role/pac_ro","key":"roleIdentifyingString"},{"encrypt":false,"value":"m1,m2,t1,c1,c2","key":"deprecatedInstanceType"},{"encrypt":false,"value":"true","key":"threadsafe"},{"encrypt":false,"value":"check-for-deprecated-instance-type","key":"ruleKey"},{"encrypt":false,"value":",","key":"splitterChar"},{"encrypt":false,"value":"medium","key":"severity"},{"isValueNew":true,"encrypt":false,"value":"governance","key":"ruleCategory"}],"environmentVariables":[],"ruleId":"PacMan_ec2deperecatedinstancetype_version-1_ec2deprecatedinstancetype_ec2","autofix":false,"alexaKeyword":"ec2deprecatedinstancetype","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_ec2deperecatedinstancetype_version-1","assetGroup":"aws","ruleUUID":"86bd22f7-e3b1-413b-a651-03bc7cddad2f","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/86bd22f7-e3b1-413b-a651-03bc7cddad2f'),'ENABLED','ASGC','Deprecated EC2 instances types should not be used to launch instances',{d '2017-08-11'},{d '2018-08-31'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_ec2publicaccesstoport9200_version-1_ec2publicaccesswithport9200_ec2','16f98a60-90d4-44be-923a-a8b02549eec8','PacMan_ec2publicaccesstoport9200_version-1','ec2publicaccesswithport9200','ec2','aws','ec2publicaccesswithport9200','{"params":[{"encrypt":"false","value":"igw","key":"internetGateWay"},{"encrypt":"false","value":"9200","key":"portToCheck"},{"encrypt":"false","value":"check-for-ec2-with-public-access-for-configured-port","key":"ruleKey"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"},{"encrypt":false,"value":"0.0.0.0/0","key":"cidrIp"},{"key":"esEc2SgURL","value":"/aws/ec2_secgroups/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableAssociationsURL","value":"/aws_routetable/routetable_associations/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableRoutesURL","value":"/aws_routetable/routetable_routes/_search","isValueNew":true,"encrypt":false},{"key":"esRoutetableURL","value":"/aws_routetable/routetable/_search","isValueNew":true,"encrypt":false},{"key":"esSgRulesUrl","value":"/aws_sg/sg_rules/_search","isValueNew":true,"encrypt":false}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_ec2publicaccesstoport9200_version-1_ec2publicaccesswithport9200_ec2","autofix":false,"alexaKeyword":"ec2publicaccesswithport9200","ruleRestUrl":"","targetType":"ec2","pac_ds":"aws","policyId":"PacMan_ec2publicaccesstoport9200_version-1","assetGroup":"aws","ruleUUID":"16f98a60-90d4-44be-923a-a8b02549eec8","ruleType":"ManageRule"}','0 0/23 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/16f98a60-90d4-44be-923a-a8b02549eec8'),'ENABLED','ASGC','EC2 instances should not be publicly accessible on default ELASTIC SEARCH port 9200',{d '2017-08-23'},{d '2018-09-19'});
INSERT INTO cf_RuleInstance (ruleId,ruleUUID,policyId,ruleName,targetType,assetGroup,alexaKeyword,ruleParams,ruleFrequency,ruleExecutable,ruleRestUrl,ruleType,ruleArn,status,userId,displayName,createdDate,modifiedDate) VALUES ('PacMan_rdsdb_version-1_RdsDbPublicAccess_rdsdb','e409a096-74aa-4eb2-882f-e3e9d82e0b8c','PacMan_rdsdb_version-1','RdsDbPublicAccess','rdsdb','aws','rdsdb','{"params":[{"encrypt":false,"value":"check-for-rds-db-public-access","key":"ruleKey"},{"encrypt":false,"value":"url","key":"apiGWURL"},{"encrypt":false,"value":"security","key":"ruleCategory"},{"encrypt":false,"value":"critical","key":"severity"}],"environmentVariables":[{"encrypt":false,"value":"123","key":"abc"}],"ruleId":"PacMan_rdsdb_version-1_RdsDbPublicAccess_rdsdb","autofix":false,"alexaKeyword":"rdsdb","ruleRestUrl":"","targetType":"rdsdb","pac_ds":"aws","policyId":"PacMan_rdsdb_version-1","assetGroup":"aws","ruleUUID":"e409a096-74aa-4eb2-882f-e3e9d82e0b8c","ruleType":"ManageRule"}','0 0/12 * * ? *','','','ManageRule',concat('arn:aws:events:',@region,':',@account,':rule/e409a096-74aa-4eb2-882f-e3e9d82e0b8c'),'ENABLED','ASGC','RDS database endpoints should not be publicly accessible',{d '2017-10-09'},{d '2018-09-19'});


/* Omni Seach Configuration */


INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','All','accountname,region,tags.Application,tags.Environment,tags.Stack,tags.Role','_resourceid,searchcategory,tags[],accountname,_entitytype');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','api','','region,name');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','appelb','scheme,vpcid,type','region,scheme,vpcid,type');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','asg','healthchecktype','region,healthchecktype');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','asgpolicy','policytype,adjustmenttype','region,autoscalinggroupname,policytype');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','cert','','');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','checks','','');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','classicelb','scheme,vpcid','region,scheme,vpcid');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','cloudfront','status,enabled,priceclass,httpversion,ipv6enabled','domainname,status,httpversion,aliases');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','corpdomain','','');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','dynamodb','tablestatus','region,tablestatus');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','ec2 ','availabilityzone,statename,instancetype,imageid,platform,subnetid','availabilityzone,privateipaddress,statename,instancetype');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','efs','performancemode,lifecyclestate','region,performancemode,lifecyclestate');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','elasticip','','networkinterfaceid,privateipaddress,region');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','emr','instancecollectiontype,releaselabel','region,instancecollectiontype,releaselabel');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','eni','status,sourcedestcheck,vpcid,subnetid','region,privateipaddress,status,vpcid,subnetid');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','iamrole','','description');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','iamuser','passwordresetrequired,mfaenabled','');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','internetgateway','','region');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','kms','keystate,keyenabled,keyusage,rotationstatus','region,keystate');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','lambda','memorysize,runtime,timeout','region,runtime');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','launchconfig','instancetype,ebsoptimized,instancemonitoringenabled','instancetype,region');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','nat','vpcid,subnetid,state','region,vpcid,subnetid,state');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','networkacl','vpcid,isdefault','vpcid,region');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','onpremserver','os,used_for,u_business_service,location,company,firewall_status,u_patching_director,install_staus','ip_address,os,os_version,comapny');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','phd','','');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','rdscluster','multiaz,engine,engineversion','region,engine,engineversion');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','rdsdb','dbinstanceclass,dbinstancestatus,engine,engineversion,licensemodel,multiaz,publiclyaccessible','region,engine,engineversion');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','rdssnapshot','snapshottype,encrypted,engine,engineversion,storagetype','vpcid,availabilityzone,engine,engineversion');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','redshift','nodetype,publiclyaccessible','region,nodetype,vpcid');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','routetable','vpcid','vpcid,region');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','s3','versionstatus','region,creationdate,versionstatus');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','sg','vpcid','region,vpcid');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','snapshot','encrypted,state','region,volumeid,state');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','stack','disablerollback,status','region,status');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','subnet','vpcid,availabilityzone,defaultforaz,state','availabilityzone,cidrblock,state');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','targetgroup','','region,vpcid,protocol,port');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','volume','volumetype,availabilityzone,encrypted,state','volumetype,availabilityzone,state');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','vpc','','region,cidrblock,state');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','vpngateway','state,type','region,state,type');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','elasticache','engine,nodetype,engineversion','region,engine');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Assets','wafdomain','','');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Policy Violations','All','severity,policyId','_id,issueid,resourceid,severity,_entitytype,_resourceid');
INSERT INTO OmniSearch_Config (SEARCH_CATEGORY,RESOURCE_TYPE,REFINE_BY_FIELDS,RETURN_FIELDS) VALUES ('Vulnerabilities','All','severity,category,vulntype','qid,vulntype,category,_entitytype,_resourceid');


/* RUle Category Weightage */
INSERT INTO pac_v2_ruleCategory_weightage (ruleCategory,domain,weightage) VALUES ('costOptimization','Infra & Platforms',20);
INSERT INTO pac_v2_ruleCategory_weightage (ruleCategory,domain,weightage) VALUES ('governance','Infra & Platforms',20);
INSERT INTO pac_v2_ruleCategory_weightage (ruleCategory,domain,weightage) VALUES ('security','Infra & Platforms',50);
INSERT INTO pac_v2_ruleCategory_weightage (ruleCategory,domain,weightage) VALUES ('tagging','Infra & Platforms',10);


/* UI FIlter */

INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (1,'Issue');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (2,'vulnerbility');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (3,'asset');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (4,'compliance');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (5,'tagging');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (6,'certificates');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (7,'patching');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (8,'AssetListing');
INSERT INTO pac_v2_ui_filters (filterId,filterName) VALUES (9,'digitaldev');

/* UI Filter Options */

INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (1,1,'Policy','policyId.keyword','/compliance/v1/filters/policies?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (2,1,'Rule','ruleId.keyword','/compliance/v1/filters/rules?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (3,1,'Region','region.keyword','/compliance/v1/filters/regions?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (4,1,'AccountName','accountid.keyword','/compliance/v1/filters/accounts?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (5,1,'Application','tags.Application.keyword','/compliance/v1/filters/application?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (6,1,'Environment','tags.Environment.keyword','/compliance/v1/filters/environment?ag=aws&application=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (7,2,'Application','tags.Application.keyword','/compliance/v1/filters/application?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (8,2,'Environment','tags.Environment.keyword','/compliance/v1/filters/environment?ag=aws&application=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (9,3,'Application','tags.Application.keyword','/compliance/v1/filters/application?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (12,4,'Resource Type','targetType.keyword','/compliance/v1/filters/targettype?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (13,8,'Application ','application ','/compliance/v1/filters/application?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (14,8,'Environment  ','environment ','/compliance/v1/filters/environment?ag=aws&application=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (15,8,'Resource Type','resourceType ','/compliance/v1/filters/targettype?ag=aws');
INSERT INTO pac_v2_ui_options (optionId,filterId,optionName,optionValue,optionURL) VALUES (16,9,'Application','tags.Application.keyword','/compliance/v1/filters/application?ag=aws');

/* UI Widgets */
INSERT INTO pac_v2_ui_widgets (widgetId,pageName,widgetName) VALUES (1,'Tagging','TaggingSummary');
INSERT INTO pac_v2_ui_widgets (widgetId,pageName,widgetName) VALUES (2,'Tagging','Total Tag Compliance');
INSERT INTO pac_v2_ui_widgets (widgetId,pageName,widgetName) VALUES (3,'Tagging','Tagging Compliance Trend');
INSERT INTO pac_v2_ui_widgets (widgetId,pageName,widgetName) VALUES (4,'ComplianceOverview','OverallCompliance,tagging,patching,vulnerabilites');

/* UI Widgets faqs */
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (1,1,'Tagging Summary','How overall Compliance% calculated ?','Total assets which has Application and Environment tag devided by total taggable Assets.');
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (2,1,'Tagging Summary','How an AssetGroup Un-tagged count calculted ?','Total assets which is missing application,Environment tag.');
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (3,4,'OverallCompliance,tagging,patching,vulnerabilites','How overall % calculated ?','It''s average of patching,certificates,tagging,vulnerbilites and other policies percentage');
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (7,4,'OverallCompliance,tagging,patching,vulnerabilites','How patching % calculated ?','total patched running ec2 instances /total running ec2 instances');
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (8,4,'OverallCompliance,tagging,patching,vulnerabilites','How tagging % calculated ?','total tagged assets /total assets');
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (9,4,'OverallCompliance,tagging,patching,vulnerabilites','How vulnerabilities % calculated ?','total vulnerable ec2 assets/total ec2 assets.  ');
INSERT INTO pac_v2_ui_widget_faqs (faqId,widgetId,widgetName,faqName,faqAnswer) VALUES (10,4,'OverallCompliance,tagging,patching,vulnerabilites','How other policies % calculated',null);


INSERT INTO pac_v2_ui_download_filters (serviceId,serviceName,serviceEndpoint) VALUES
 (1,'Violations','/api/compliance/v1/issues'),
 (2,'NonComplaincePolicies','/api/compliance/v1/noncompliancepolicy'),
 (3,'PatchingDetails','/api/compliance/v1/patching/detail'),
 (4,'TaggingDetailsByApplication','/api/compliance/v1/tagging/summarybyapplication'),
 (5,'CertificateDetails','/api/compliance/v1/certificates/detail'),
 (6,'VulnerabilitiesDetails','/api/compliance/v1/vulnerabilities/detail'),
 (7,'Assets','/api/asset/v1//list/assets'),
 (8,'PatchableAssets','/api/asset/v1/list/assets/patchable'),
 (9,'ScannedAssets','/api/asset/v1/list/assets/scanned'),
 (10,'TaggableAssets','/api/asset/v1/list/assets/taggable'),
 (11,'VulnerableAssets','/api/asset/v1/list/assets/vulnerable'),
 (12,'PullRequestAssetsByState','/api/devstandards/v1/pullrequests/asset/bystates'),
 (13,'PullRequestAsstesByAge','/api/devstandards/v1/pullrequests/assets/openstate'),
 (14,'ApplicationOrRepositoryDistribution','/api/devstandards/v1/repositories/assets/repositoryorapplicationdistribution');
