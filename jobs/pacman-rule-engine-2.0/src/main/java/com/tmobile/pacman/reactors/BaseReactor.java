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

package com.tmobile.pacman.reactors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.autofix.ConfigChangeManager;

/**
 * @author kkumar28
 *
 */
public abstract class BaseReactor implements Reactor {
    
    /****/
    private static final Logger logger = LoggerFactory.getLogger(BaseReactor.class);
    
    /****/
    private String resourceId;
    
    /****/
    private JsonObject event;
    
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Reaction call() throws Exception {
        return react(event);
    }
    
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
        return new ConfigChangeManager().backupOldConfig(resourceId, configType, oldConfig);
    }

}
