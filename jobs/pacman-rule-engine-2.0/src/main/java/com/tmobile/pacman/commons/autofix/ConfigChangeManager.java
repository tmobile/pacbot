/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Jan 14, 2019
  
**/
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
package com.tmobile.pacman.commons.autofix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.util.CommonUtils;

/**
 * @author kkumar28
 *
 */
public class ConfigChangeManager {
    
    
    
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigChangeManager.class);
    
    /**
     * Backup old config.
     *
     * @param resourceId the resource id
     * @param configType the config type
     * @param oldConfig the old config
     * @return true, if successful
     * @throws AutoFixException the auto fix exception
     */
    public boolean backupOldConfig(String resourceId, String configType, String oldConfig) throws AutoFixException {
        String url = CommonUtils.getPropValue(com.tmobile.pacman.common.PacmanSdkConstants.BACKUP_ASSET_CONFIG);
        url = url.concat("?resourceId=").concat(resourceId).concat("&configType=").concat(configType);
        try {
                String resp = CommonUtils.doHttpPost(url, oldConfig, Maps.newHashMap());
                if(!Strings.isNullOrEmpty(resp)){
                    return true;
                }else{
                    throw new AutoFixException();
                }
        } catch (Exception exception) {
            LOGGER.error(String.format("Exception in backuping Old Config: %s" , exception.getMessage()));
            throw new AutoFixException(exception);
        }
    }

}
