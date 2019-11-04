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
package com.tmobile.pacman.api.asset;

/**
 * Constants class where the asset service constants are stored.
 */
public final class AssetConstants {
    
    private AssetConstants() {
        
    }

    public static final String FILTER_EXEC_SPONSOR = "executiveSponsor";
    public static final String FILTER_RES_TYPE = "resourceType";
    public static final String FILTER_DIRECTOR = "director";
    public static final String FILTER_APPLICATION = "application";
    public static final String FILTER_ENVIRONMENT = "environment";
    public static final String FILTER_PATCHED = "patched";
    public static final String FILTER_TAGGED = "tagged";
    public static final String FILTER_TAGNAME = "tagName";
    public static final String FILTER_RULEID = "ruleId";
    public static final String FILTER_COMPLIANT = "compliant";
    public static final String FILTER_DOMAIN = "domain";
    public static final String TAG_NOT_FOUND = "Not Found";
    public static final String ERROR_FROM_NEGATIVE = "From should not be a negative number";
    public static final String ERROR_FILTER_ACCEPTS = "Filter accepts only ";
    public static final String ASSET_COUNT = "assetcount";
    public static final String ERROR_INSTANCEID = "Asset group/Instance Id is Mandatory";
    public static final String ERROR_FROM_EXCEEDS = "From exceeds the size of list";
    public static final String ERROR_QUALYS_NOT_ENABLED = "Qualys not enabled";
    public static final String ASSETS = "Assets";
    public static final int ZERO = 0;
    public static final int FIFTEEN = 15;
    public static final int TWENTY = 20;
    public static final int TWENTY_FIVE = 25;
    public static final int THIRTY = 30;
    public static final int FORTY = 40;
    public static final int FIFTY = 50;
    public static final int SIXTY = 60;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final String UNDERSCORE_ENTITY = "_entity";
    public static final String ALL = "all";
    public static final String STOPPED = "stopped";
    public static final String STOPPING = "stopping";
    public static final String UNDERSCORE_TYPE = "_type";
    public static final String UNDERSCORE_SOURCE = "_source";
    public static final String RECENTLY_VIEWED_AG = "recentlyViewedAg";
    public static final String UNDERSCORE_LOADDATE = "_loaddate";
    public static final String CREATE_TIME = "createtime";
    public static final String FIRST_DISCOVEREDON = "firstdiscoveredon";
    public static final String UNDERSCORE_DISCOVERY_DATE = "_discoverydate";
    public static final String DISCOVERY_DATE = "discoverydate";
    public static final String CREATION_DATE = "creationdate";
    public static final String UNDERSCORE_ENTITY_TYPE_KEYWORD = "_entitytype.keyword";
    public static final String UNDERSCORE_ENTITY_TYPE = "_entitytype";
    public static final String AWS_EC2 = "aws_ec2";
    public static final String INSTANCEID_KEYWORD = "instanceid.keyword";
    public static final String FALSE = "false";
    public static final String ERROR_FETCHING_FIELDNAMES = "Error while fetching field names ";
    public static final String SERVICE_NAME = "serviceName";
    public static final String QUERY = "query";
    public static final String POLICY_VIOLATIONS = "Policy Violations";
    public static final String VULNERABILITIES = "Vulnerabilities";
    public static final String PUBLIC_IP_ADDRESS = "publicipaddress";
    public static final String PRIVATE_IP_ADDRESS = "privateipaddress";
    public static final String RELATED_ASSETS = "RELATED ASSETS";
    public static final String CREATED_BY = "createdBy";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String TOTAL_COST = "totalCost";
    public static final String MANAGED_BY = "managedBy";
    public static final String FIELDNAME = "fieldName";
    public static final String ERROR_SEARCH = "Error in search ";
    public static final String ERROR_GETASSETSBYAG  = "Error in getAssetsByAssetGroup ";
    public static final String ERROR_COUNT  = "Error in count ";
    public static final String ERROR_EXEQUTEQUERY  = "Error in executeQuery  ";
    public static final String ERROR_BATCHUPDATE  = "Error in batchUpdate  ";
    public static final String ERROR_GETAPPSBYAG  = "Error in getApplicationByAssetGroup  ";
    public static final String DEBUG_RESPONSEJSON = "Response json is:";
    public static final String ESQUERY_RANGE = ",{ \"range\": {\"date\": {";
    public static final String ESQUERY_RANGE_CLOSE = "}}}]}}}";
    public static final String ESQUERY_CLOSE = "\"}}]}}}";
    public static final String ESQUERY_BULK = "/_bulk?refresh=true";
    public static final String RESPONSE_ERROR = "\"errors\":true";
    public static final String FILTER_CATEGORY = "category";
    public static final String FILTER_GENERAL = "general";
    public static final String FILTER_RECOMMENDATION_ID = "recommendationId";
    public static final String ASSET_TYPE = "assettype";
	public static final String TOTAL_ASSETS = "totalassets";
    
}

