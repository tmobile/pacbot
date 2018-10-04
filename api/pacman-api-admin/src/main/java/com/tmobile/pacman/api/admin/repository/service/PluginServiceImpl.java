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
package com.tmobile.pacman.api.admin.repository.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.admin.domain.PluginDetails;
import com.tmobile.pacman.api.commons.exception.ServiceException;
/**
 * The Class PluginServiceImpl.
 */
@Service

public class PluginServiceImpl implements PluginService {
    
    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.admin.repository.service.PluginService#pluginList(java.lang.String)
     */
    @Override
    public PluginDetails pluginList(String pluginId)throws ServiceException{
      List<Map<String,Object>>availablePlugins = new ArrayList<>();
      Map<String,Object>plugins;
      Map<String,Object>pluginsDetals;
      List<Map<String,Object>>pluginsDetailsList = new ArrayList<>();
      plugins=new HashMap<>();
      plugins.put("pluginId", 1);
      plugins.put("pluginName", "qualys");
      plugins.put("pluginStatus", "enabled");
      pluginsDetals =new HashMap<>();
      pluginsDetals.put("key", "targetType");
      pluginsDetals.put("value", "qualys");
      pluginsDetailsList.add(pluginsDetals);
      plugins.put("pluginDetails", pluginsDetailsList);
      availablePlugins.add(plugins);
      return new PluginDetails(availablePlugins);
    }

/* (non-Javadoc)
 * @see com.tmobile.pacman.api.admin.repository.service.PluginService#updatePlugins(com.tmobile.pacman.api.admin.domain.PluginDetails)
 */
@Override
    public String updatePlugins(PluginDetails pluginDetails)throws ServiceException{
      
        return "success";
      }
}
