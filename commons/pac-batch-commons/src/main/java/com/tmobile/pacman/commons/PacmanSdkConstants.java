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
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar
  Modified Date: Jun 14, 2017

**/

package com.tmobile.pacman.commons;

// TODO: Auto-generated Javadoc
/**
 * The Interface PacmanSdkConstants.
 */
public interface PacmanSdkConstants {

	/** The client. */
	String CLIENT = "client";

	/** The temporary creds valid seconds. */
	Integer TEMPORARY_CREDS_VALID_SECONDS = 3600;

	/** The default session name. */
	String DEFAULT_SESSION_NAME = "PAC_GET_DATA_SESSION";

	/** The pacman dev profile name. */
	String PACMAN_DEV_PROFILE_NAME = "pacman-dev";

	/** The pacman dev env variable. */
	String PACMAN_DEV_ENV_VARIABLE = "PACMAN_DEV";

	/** The default pacman resource srv url. */
	String DEFAULT_PACMAN_RESOURCE_SRV_URL = "https://tmobile.atlassian.net/someservice";

	/** The run time argument name. */
	String RUN_TIME_ARGUMENT_NAME="params";

	/** The pacman resource srv url env var name. */
	String PACMAN_RESOURCE_SRV_URL_ENV_VAR_NAME="pacman_resource_srv_url";

	/** The account id. */
	String ACCOUNT_ID="accountid";

	/** The region. */
	String REGION = "region";

	/** The role arn prefix. */
	String ROLE_ARN_PREFIX="arn:aws:iam::";

	/** The resource id. */
	String RESOURCE_ID = "_resourceid";

	/** The rule id. */
	String RULE_ID="ruleId";

	/** The policy id. */
	String POLICY_ID="policyId";

	/** The policy version. */
	String POLICY_VERSION="policyVersion";

	/** The Role IDENTIFYIN G STRING. */
	String Role_IDENTIFYING_STRING="roleIdentifyingString";

	/** The mendetory tags key. */
	String MANDATORY_TAGS_KEY="mandatoryTags";

	/** The splitter char. */
	String SPLITTER_CHAR="splitterChar";

	/** The description. */
	String DESCRIPTION = "desc";

	/** The exception. */
	String EXCEPTION = "Exception";

	/** The target type. */
	String TARGET_TYPE = "targetType";

	/** The annotation pk. */
	String ANNOTATION_PK = "annotationid";

	/** The x api key. */
	String X_API_KEY = "x-api-key";

	/** The env variable name for environment. */
	String ENV_VARIABLE_NAME_FOR_ENVIRONMENT="PAC_ENV";

	/** The staging env prefix. */
	String STAGING_ENV_PREFIX = "stg";

	/** The type. */
	String TYPE="type";

	/** The tz utc. */
	String TZ_UTC="UTC";

	/** The created date. */
	String CREATED_DATE="createdDate";

	/** The modified date. */
	String MODIFIED_DATE="modifiedDate";

	/** The sev high. */
	String SEV_HIGH = "high";

	/** The sev medium. */
	String SEV_MEDIUM="medium";

	/** The sev low. */
	String SEV_LOW="low";

	/** The financial. */
	String FINANCIAL="financial";

	/** The security. */
	String SECURITY="security";

	/** The governance. */
	String GOVERNANCE="governance";

	/** The pacman. */
	String PACMAN="pacman";

	/** The pac time zone. */
	String PAC_TIME_ZONE="UTC";

	/** The issue status key. */
	String ISSUE_STATUS_KEY="issueStatus";

	/** The rule category. */
	String RULE_CATEGORY = "ruleCategory";

	/** The rule severity. */
	String RULE_SEVERITY="severity";

	/** The updated success. */
	String UPDATED_SUCCESS = "Successfully Updated";

	/** The updated failure. */
	String UPDATED_FAILURE = "Updation Failed";

	/** The creation failure. */
	String CREATION_FAILURE = "Failure In Adding New Item";

	/** The creation success. */
	String CREATION_SUCCESS = "Successfully Added New Item";

	/** The data source key. */
	String DATA_SOURCE_KEY = "pac_ds";

	/** The base aws account env var name. */
	String BASE_AWS_ACCOUNT_ENV_VAR_NAME="BASE_AWS_ACCOUNT";

	/** The es doc id key. */
	String ES_DOC_ID_KEY = "_id";

	/** The date format. */
	String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/** The es uri env var name. */
	String ES_URI_ENV_VAR_NAME = "ES_URI";

	/** The parent id. */
	String PARENT_ID = "_docid";

	/** The resource id col name from es. */
	Object RESOURCE_ID_COL_NAME_FROM_ES = "_resourceid";

	/** The data source attr. */
	String DATA_SOURCE_ATTR = "datasource";

	/** The audit date. */
	String AUDIT_DATE = "auditdate";

	/** The  audit date. */
	String _AUDIT_DATE = "_auditdate";

	/** The audit index. */
	String AUDIT_INDEX = "issueaudit";

	/** The audit type. */
	String AUDIT_TYPE = "audittrail";

	/** The execution id. */
	String EXECUTION_ID = "executionId";

	/** The rule type serverless. */
	String RULE_TYPE_SERVERLESS = "Serverless";

	/** The rule type classic. */
	String RULE_TYPE_CLASSIC = "classic";

	/** The rule type. */
	String RULE_TYPE = "ruletype";

	/** The rule url key. */
	String RULE_URL_KEY = "ruleRestUrl";

	/** The es page size. */
	Integer ES_PAGE_SIZE = 1000;

	/** The es page scroll ttl. */
	String ES_PAGE_SCROLL_TTL="5m";

	/** The es source fields key. */
	String ES_SOURCE_FIELDS_KEY = "es_source_fields";

	/** The account name. */
	String ACCOUNT_NAME = "accountname";

	/** The run on multi thread key. */
	String RUN_ON_MULTI_THREAD_KEY="threadsafe";

	/** The scan time out. */
	Long SCAN_TIME_OUT = 10L;

	/** The es doc parent key. */
	String ES_DOC_PARENT_KEY="_parent";

	/** The es doc routing key. */
	String ES_DOC_ROUTING_KEY="_routing";

	/** The es max bulk post size. */
	Long ES_MAX_BULK_POST_SIZE=5L;

	/** The status key. */
	String STATUS_KEY="status";

	/** The status running. */
	String STATUS_RUNNING="running";

	/** The status finished. */
	String STATUS_FINISHED="finished";

	/** The status open. */
	String STATUS_OPEN = "open";

	/** The status close. */
	String STATUS_CLOSE = "closed";

	/** The status success. */
	String STATUS_SUCCESS = "success";

	/** The status failure. */
	String STATUS_FAILURE = "fail";

	/** The status unknown. */
	String STATUS_UNKNOWN="unknown";

	/** The error desc key. */
	String ERROR_DESC_KEY = "errorDesc";

	/** The current app tag key. */
	String CURRENT_APP_TAG_KEY = "current_application_tag";

	/** The correct app tag key. */
	String CORRECT_APP_TAG_KEY= "correct_application_tag";
	
	/** The config credentials. */
    String CONFIG_CREDENTIALS = "CONFIG_CREDENTIALS";
    
    /** The config service url. */
    String CONFIG_SERVICE_URL = "CONFIG_SERVICE_URL";
    
    /** The missing configuration. */
    String MISSING_CONFIGURATION = "Missing value in the env configuration";
    
    /** The missing db configuration. */
    String MISSING_DB_CONFIGURATION = "Missing db configurations";
    
    /** The name. */
    String NAME = "name";
    
    /** The source. */
    String SOURCE = "source";
    
    String TAGGING_MANDATORY_TAGS = "tagging.mandatoryTags";
    
    String CLOUD_INSIGHT_SQL_SERVER = "CLOUD_INSIGHT_SQL_SERVER";
	
	String CLOUD_INSIGHT_USER = "CLOUD_INSIGHT_USER";
	
	String CLOUD_INSIGHT_PASSWORD = "CLOUD_INSIGHT_PASSWORD";
}
