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
package com.tmobile.pacman.cloud.util;

/**
 * The Interface Constants.
 */
public interface Constants {

	/** The rds db url. */
	String RDS_DB_URL = "spring.datasource.url";

	/** The rds user. */
	String RDS_USER = "spring.datasource.username";

	/** The rds pwd. */
	String RDS_PWD = "spring.datasource.password";

	/** The target type info. */
	String TARGET_TYPE_INFO = "targetTypes";

	/** The config creds. */
	String CONFIG_CREDS = "config_creds";

	/** The config query. */
	String CONFIG_QUERY = "configquery";

	/** The config url. */
	String CONFIG_URL = "CONFIG_URL";

	/** The failed. */
	String FAILED = "failed";

	/** The error. */
	String ERROR = "error";

	/** The exception. */
	String EXCEPTION = "exception";

	/** The error type. */
	String ERROR_TYPE = "type";

	/** The warn. */
	String WARN = "warn";

	/** The fatal. */
	String FATAL = "fatal";

	/** The source. */
	String SOURCE = "source";

	/** The name. */
	String NAME = "name";

	/** The JDBC Driver. */
	String DBDRIVER = "com.mysql.jdbc.Driver";

	/** The elastic search host */
	String ELASTIC_SEARCH_HOST = "elastic-search.host";

	/** The elastic search host */
	String ELASTIC_SEARCH_PORT = "elastic-search.port";

	/** The heimdall elastic search host */
	String ELASTCIC_SEARCH_HOST_HEIMDALL = "elastic-search.host-heimdall";

	/** The heimdall elastic search host */
	String HEIMDALL_ELASTIC_SEARCH_PORT = "elastic-search.port-heimdall";
	
	 String TARGET_TYPE_OUTSCOPE = "typesNotInScope";

	String _SEARCH = "_search";
	String AGGREGATIONS = "aggregations";
	String BUCKETS = "buckets";
	String KEY = "key";
	String CIS_COMPLIANCE = "cis-compliance";
	String RAW_DATA = "raw-data";
	String IAMUSER = "iamuser";
	String _SOURCE = "_source";
	String HITS = "hits";
	String AWS = "aws";
	String KMS = "kms";
	
	String GROUPNAME = "groupName";
	String SECURITYHUB = "SecurityHub";
	String RULESOURCE = "ruleSource";
	String SECURITYhUB = "securityhub";
	String RULEID = "ruleId";
	String ENTITYTYPE = "entityType";
	String _ENTITYTPE = "_entitytype";
	String AWSACCOUNTID = "AwsAccountId";
	String REGION = "Region";
	String _REGION = "region";
	String RULETITLE = "ruleTitle";
	String UPDATEDAT = "UpdatedAt";
	String MODIFIEDDATE = "modifiedDate";
	String LOADDATE = "loaddate";
	String AWSACCOUNT = "AwsAccount";
	String ID = "Id";
	String ASSETTYPE = "assetType";
	String AWSEC2SECGRP = "AwsEc2SecurityGroup";
	String AWSS3BUCKET = "AwsS3Bucket";
	String AWSKMSKEY = "AwsKmsKey";
	String AWSEC2VPC = "AwsEc2Vpc";
	String ACCOUNT = "account";
	String CLOUDTRAIL = "cloudtrail";
	String VPC = "vpc";
	String S3 = "s3";
	String SG = "sg";
	String SCOREPERCENTAGE = "scorePercentage";
	String _VIOLATEDASSETS = "_violatedAssets";
	String TOTALASSETS = "total_assets";
	String VIOLATEDASSETS = "violated_assets";
	String AG = "ag";
	String _LOADDATE = "_loaddate";
	String SCOREDATA = "scoredata";
	String RESPONSBILITY = "responsbility";
	String GROUPTYPE = "groupType";
	String POLICYID = "policyId";
	
	
	
	String EVENTTYPE = "eventType";
	String AWS_PHD = "aws_phd";
	String PHD = "phd";
	String PHD_ENTITES = "phd_entities";
	String EVENTARN = "eventarn";
	String ESINDEX = "esIndex";
	String RESOURCEIDKEY = "resourceIdKey";
	String RESOURCEIDVAL = "resourceIdVal";
	String _RESOURCEID = "_resourceid";
	String EVENTSERVICE = "eventservice";
	String _DOCID = "_docid";
	String ACCOUNTID = "accountid";
	String TYPE = "type";
	String INDEX = "index";
	
	
	
}
