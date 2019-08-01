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
package com.tmobile.cso.pacbot.recommendation.util;

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
    
    
    /** The target type info. */
    String TARGET_TYPE_OUTSCOPE = "typesNotInScope";
    
    /**  The API  User:Password. */
    String API_AUTH_INFO = "apiauthinfo";
    
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
    
    /** The checkid. */
    String CHECKID = "checkid";
    
    /** The accountid. */
    String ACCOUNTID = "accountid";
    
    /** The checkname. */
    String CHECKNAME = "checkname";
    
    /** The checkcategory. */
    String CHECKCATEGORY = "checkcategory";
    
    /** The resource info. */
    String RESOURCE_INFO = "resourceinfo";
    
    /** The recommendation. */
    String RECOMMENDATION = "recommendation";
    
    /** The load date. */
    String LOAD_DATE = "_loaddate";
    
    /** The global recommendations. */
    String GLOBAL_RECOMMENDATIONS = "global_recommendations";
    
    /** The monthly savings field. */
    String MONTHLY_SAVINGS_FIELD = "monthlySavingsField";
    
    /** The recommendation id. */
    String RECOMMENDATION_ID = "recommendationId";
}
