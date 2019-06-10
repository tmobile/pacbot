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
package com.tmobile.pacman.api.admin.common;

/**
 * Admin Constants
 */
public final class AdminConstants {
	
	private AdminConstants() {
	}
	
	public static final String SERVERLESS_RULE_TYPE					= 	"Serverless";
	public static final String MANAGED_RULE_TYPE					= 	"ManageRule";
	public static final String JAR_EXTENSION						= 	".jar";
	public static final String JAR_FILE_MISSING						=	"Jar file is missing";
	public static final String RESOURCE_ACCESS_DENIED				=	"You don't have sufficient privileges to access this resource";
	public static final String FAILED								=	"failed";
	public static final String ENABLED_CAPS							=	"ENABLED";
	public static final String RULE									=	"rule";
	public static final String JOB									=	"job";
	public static final String ENABLE								=	"enable";
	
	public static final String DATE_FORMAT 							= 	"MM/dd/yyyy HH:mm";
	
	public static final String RULE_CREATION_SUCCESS				=	"Rule has been successfully created";
	public static final String RULE_DISABLE_ENABLE_SUCCESS			=	"Rule has been successfully %s !!";
	public static final String RULE_ID_EXITS 						= 	"Rule id %s already exists!!";
	public static final String RULE_ID_NOT_EXITS 					= 	"Rule id %s does not exists!!";
	public static final String JOB_DISABLE_ENABLE_SUCCESS           =   "Job has been successfully %s !!";

	public static final String JOB_CREATION_SUCCESS					=	"Job has been successfully created";
	public static final String JOB_UPDATION_SUCCESS					=	"Job has been successfully updated";
	public static final String JOB_ID_ALREADY_EXITS					=	"Job %s already exists!!";
	public static final String JOB_ID_NOT_EXITS						=	"Job %s does not exists!!";
	public static final String INVALID_JOB_FREQUENCY				=	"Invalid Job Frequency or Cron Expression!";
	public static final String UNEXPECTED_ERROR_OCCURRED 			= 	"Unexpected error occurred!!";
	public static final String LAMBDA_LINKING_EXCEPTION             =   "Failed in linking the lambda function to the rule";
	public static final String CLOUDWATCH_RULE_DELETION_FAILURE     =   "Failed in deleting the cloudwatch rule while disabling the rule";
	public static final String CLOUDWATCH_RULE_DISABLE_FAILURE     	=   "Failed in disabling the cloudwatch rule";
	public static final String CLOUDWATCH_RULE_ENABLE_FAILURE     	=   "Failed in enabling the cloudwatch rule";

	public static final String DOMAIN_CREATION_SUCCESS				=	"Domain has been successfully created";
	public static final String DOMAIN_NAME_EXITS					=	"Domain name already exits!!!";
	public static final String DOMAIN_UPDATION_SUCCESS 				=	"Domain has been successfully updated";
	
	public static final String TARGET_TYPE_NAME_NOT_EXITS			=	"Target Type name does not exits!!!";
	public static final String TARGET_TYPE_NAME_EXITS				=	"Target Type name already exits!!!";
	public static final String TARGET_TYPE_CREATION_SUCCESS			=	"Target Type has been successfully created";
	public static final String TARGET_TYPE_UPDATION_SUCCESS 		=	"Target Type has been successfully updated";
	public static final String TARGET_TYPE_CREATION_FAILURE 		=	"Failed in creating Target Type";
	public static final String TARGET_TYPE_UPDATION_FAILURE 		=	"Failed in updating Target Type";
	public static final String TARGET_TYPE_INDEX_EXITS				=	"Target Type index already exits!!!";
	
	public static final String ASSET_GROUP_CREATION_SUCCESS			=	"Asset Group has been successfully created";
	public static final String ASSET_GROUP_UPDATION_SUCCESS 		=	"Asset Group has been successfully updated";
	public static final String ASSET_GROUP_DELETE_SUCCESS 			=	"Asset Group has been successfully deleted";
	public static final String ASSET_GROUP_DELETE_FAILED 			=	"Failed in deleting the Asset Group";
	public static final String ASSET_GROUP_NOT_EXITS				=	"Asset Group does not exits!!!";
	public static final String ASSET_GROUP_ALIAS_DELETION_FAILED	= 	"Failed in deleting the Asset Group Alias";
	
	public static final String EXCEPTION_DELETEION_SUCCESS 			= 	"Asset Group Exception has been successfully deleted";
	public static final String EXCEPTION_DELETEION_FAILURE 			= 	"Failed in deleting Asset Group Exception";
	public static final String CONFIG_STICKY_EXCEPTION_SUCCESS 		= 	"Successfully Configured Sticky Exceptions";
	public static final String CONFIG_STICKY_EXCEPTION_FAILED 		= 	"Failed in ConfigurING Sticky Exceptions";
	
	public static final String USER_ROLE_CREATION_SUCCESS			=	"User Role has been successfully created";
	public static final String USER_ROLE_UPDATION_SUCCESS 			=	"User Role has been successfully updated";
	public static final String USER_ROLE_NOT_EXITS					=	"User Role does not exits!!!";
	public static final String USER_ROLE_ALREADY_EXITS				=	"User Role already exits!!!";
	public static final String USER_ROLE_ALLOCATION_FAILURE 		=	"Failed in user role allocation";
	public static final String USER_ROLE_ALLOCATION_SUCCESS 		=	"User Roles has been successfully allocated";
	
	public static final String QUERY								= 	"query";
	
	public static final String PLUGIN_DETAILS                       =   "pluginDetails";
	
	public static final String ACCOUNT_CREATION_SUCCESS             =   "Account has been successfully created";
	public static final String ACCOUNT_ID_EXITS                     =   "Account Id already exits!!!";
	public static final String ACCOUNT_ID_NOT_EXITS                 =   "Account Id does not exits!!!";
	public static final String ACCOUNT_UPDATION_SUCCESS             =   "Account has been successfully updated";
	public static final String ACCOUNT_DELETION_SUCCESS             =   "Account has been successfully deleted";
	public static final String ACCOUNT_DELETE_FAILED                =   "Failed in deleting the Account";
	public static final Integer TEMPORARY_CREDS_VALID_SECONDS 		=   3600;
	public static final String DEFAULT_SESSION_NAME 				=   "PAC_GET_ADMIN_DATA_SESSION";
	
	public static final String JOBID_OR_RULEID_NOT_EMPTY            =   "Both Job Id or Rule Id cannot be blank";
    public static final String DELETE_RULE_TARGET_FAILED            =   "Failed in deleting the lambda target from rule";
    
	public static final String ES_EXCEPTION_INDEX					= 	"/exceptions";
	public static final String INIT_ES_CREATE_INDEX					= 	"INIT_ES_CREATE_INDEX";
	
	public static final String ERROR_CONFIG_MANDATORY				=	"Config key, Config value and application are mandatory";
    public static final String LATEST								=	"latest";
    public static final String CONFIG_ROLLBACK_MSG					=	"Rollback to an older timestamp through API invocation";
}

