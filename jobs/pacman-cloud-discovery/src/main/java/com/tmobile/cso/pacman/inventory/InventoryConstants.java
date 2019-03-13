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
package com.tmobile.cso.pacman.inventory;


/**
 * The Class InventoryConstants.
 */
public final class InventoryConstants {

    /**
     * Instantiates a new inventory constants.
     */
    private InventoryConstants() {
        
    }
    
    /** The Constant ERROR_PREFIX_CODE. */
    public static final String ERROR_PREFIX_CODE = "{\"errcode\": \"NO_RES_REG\" ,\"account\": \"";
    
    /** The Constant ERROR_PREFIX_EC2. */
    public static final String ERROR_PREFIX_EC2 = "\",\"Message\": \"Exception in fetching info for resource in specific region\" "
            + ",\"type\": \"EC2\" , \"region\":\"";
    
    /** The Constant ACCOUNT. */
    public static final String ACCOUNT = "Account : ";
    
    /** The Constant ERROR_CAUSE. */
    public static final String ERROR_CAUSE = "\", \"cause\":\"";
    
    /** The Constant ERROR_EXECUTEQUERY. */
    public static final String ERROR_EXECUTEQUERY = "Error in executeQuery ";
    
    /** The Constant ERROR_EXECUTEUPDATE. */
    public static final String ERROR_EXECUTEUPDATE = "Error in executeUpdate ";
    
    /** The Constant REGION_US_WEST_2. */
    public static final String REGION_US_WEST_2 = "us-west-2";
    
    /** The Constant SOURCE. */
    public static final String SOURCE = "source";
    
    /** The Constant NAME. */
    public static final String NAME = "name";
    
    /** The Constant APPLICATION. */
    public static final String APPLICATION = "application";
    
    /** The Constant BATCH. */
    public static final String BATCH = "batch";
    
    /** The Constant INVENTORY. */
    public static final String INVENTORY = "inventory";
    
    public static final String OPEN_ARRAY = "[";
    
    public static final String CLOSE_ARRAY = "]";
    
    
    public static final String ACCOUNT_ID = "accountId";
    
    public static final String ACCOUNT_NAME = "accountName";
    
    public static final String RDS_DB_URL = "spring.datasource.url";

    /** The rds user. */
    public static final String RDS_USER = "spring.datasource.username";

    /** The rds pwd. */
    public static final String RDS_PWD = "spring.datasource.password";
}
