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
package com.tmobile.pacman.cloud.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.pacman.cloud.dao.RDSDBManager;
import com.tmobile.pacman.cloud.util.Constants;


// TODO: Auto-generated Javadoc
/**
 * The Class ConfigManager.
 */
public class ConfigManager {

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    /** The type info. */
    private static Map<String, Map<String, String>> typeInfo;

    /**
     * Gets the type config.
     *
     * @param datasoruce
     *            the datasoruce
     * @return the type config
     */
    private static Map<String, Map<String, String>> getTypeConfig(String datasoruce) {

        String commaSepTargetTypes = System.getProperty(Constants.TARGET_TYPE_INFO);
        List<String> targetTypesList = new ArrayList<>();
        if (null != commaSepTargetTypes && !"".equals(commaSepTargetTypes)) {
            targetTypesList = Arrays.asList(commaSepTargetTypes.split(","));
        }
        String outscopeTypes = System.getProperty(Constants.TARGET_TYPE_OUTSCOPE);
        List<String> targetTypesOutScopeList = new ArrayList<>();
        if (null != outscopeTypes && !"".equals(outscopeTypes)) {
        	targetTypesOutScopeList = Arrays.asList(outscopeTypes.split(","));
        }
        
        System.out.println("&&&&&"+System.getProperty(Constants.CONFIG_QUERY));
        if (typeInfo == null) {
            typeInfo = new HashMap<>();
            List<Map<String, String>> typeList = RDSDBManager.executeQuery("select targetName,targetConfig from cf_Target where domain ='Infra & Platforms'");
            try{
                for (Map<String, String> _type : typeList) {
                    String typeName =  _type.get("targetName");
                    Map<String, String> config =  new ObjectMapper().readValue(_type.get("targetConfig"),new TypeReference<Map<String,String>>() {});
                    if ( (targetTypesList.isEmpty() || targetTypesList.contains(typeName) ) && !targetTypesOutScopeList.contains(typeName)) {
                        typeInfo.put(typeName, config);
                    }
                }
            } catch (IOException e) {
                log.error("Error Fetching config Info" + e);
            }
           
        }
        return typeInfo;
    }

    /**
     * Gets the key for type.
     *
     * @param ds
     *            the ds
     * @param type
     *            the type
     * @return the key for type
     */
    public static String getKeyForType(String ds, String type) {
        return getTypeConfig(ds).get(type).get("key");

    }

    /**
     * Gets the id for type.
     *
     * @param ds
     *            the ds
     * @param type
     *            the type
     * @return the id for type
     */
    public static String getIdForType(String ds, String type) {
        return getTypeConfig(ds).get(type).get("id");

    }

    /**
     * Gets the types.
     *
     * @param ds
     *            the ds
     * @return the types
     */
    public static Set<String> getTypes(String ds) {
        return getTypeConfig(ds).keySet();
    }

}
