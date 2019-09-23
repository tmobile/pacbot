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
package com.tmobile.cso.pacman.qualys.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.qualys.Constants;

/**
 * The Class ConfigUtil.
 */
public class ConfigUtil {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);
    
    /**
     * Sets the config properties.
     *
     * @param configCreds the new config properties
     * @throws Exception the exception
     */
    public static void setConfigProperties(String configCreds) throws Exception{
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        properties.putAll(fetchConfigProperties(configCreds));
        System.setProperties(properties);
    }
    
    /**
     * Fetch config properties.
     *
     * @param configCreds the config creds
     * @return the map
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String,String> fetchConfigProperties(String configCreds) throws Exception {
        
        Map<String,String> properties = new HashMap<>();
        
        String configUrl = System.getenv("CONFIG_URL");        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String,String> appProps = new HashMap<>();
            Map<String,String> batchProps = new HashMap<>();
            Map<String,String> invProps = new HashMap<>();
            Map<String,Object> response = objectMapper.readValue(HttpUtil.httpGetMethodWithHeaders(configUrl, Util.getHeader(configCreds)), new TypeReference<Map<String,Object>>(){});
            List<Map<String,Object>> propertySources = (List<Map<String,Object>>)response.get("propertySources");
            for(Map<String,Object> propertySource : propertySources) {
                if(propertySource.get(Constants.NAME).toString().contains("application")) {
                    appProps.putAll((Map<String,String>)propertySource.get(Constants.SOURCE));
                }
                if(propertySource.get(Constants.NAME).toString().contains("batch")) {
                    batchProps.putAll((Map<String,String>)propertySource.get(Constants.SOURCE));
                }
                if(propertySource.get(Constants.NAME).toString().contains("qualys-enricher")) {
                    invProps.putAll((Map<String,String>)propertySource.get(Constants.SOURCE));
                }
                properties.putAll(appProps);
                properties.putAll(batchProps);
                properties.putAll(invProps);
            }
        } catch (Exception e) {
            LOGGER.error("Error in fetchConfigProperties",e);
            throw e;
        }
        if(properties.isEmpty()){
        	throw new Exception("No config properties fetched from "+configUrl);
        }
        LOGGER.info("Config are feteched from {}",configUrl);
        properties.forEach((k,v)-> LOGGER.debug("{} : {} ",k,v));
        return properties;
    }
}
