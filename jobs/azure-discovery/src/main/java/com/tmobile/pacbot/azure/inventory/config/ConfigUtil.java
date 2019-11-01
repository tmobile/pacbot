package com.tmobile.pacbot.azure.inventory.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.pacbot.azure.inventory.InventoryConstants;
import com.tmobile.pacbot.azure.inventory.collector.Util;

public class ConfigUtil {

    private static Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    
    private static String configUrl = System.getenv("CONFIG_URL");
    
    public static void setConfigProperties(String base64Creds) throws Exception {
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        properties.putAll(fetchConfigProperties(base64Creds));
        System.setProperties(properties);
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String,String> fetchConfigProperties(String base64Creds) throws Exception {
        
        Map<String,String> properties = new HashMap<>();
       
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String,String> appProps = new HashMap<>();
            Map<String,String> batchProps = new HashMap<>();
            Map<String,String> invProps = new HashMap<>();
            Map<String,Object> response = objectMapper.readValue(Util.httpGetMethodWithHeaders(configUrl, Util.getHeader(base64Creds)), new TypeReference<Map<String,Object>>(){});
            List<Map<String,Object>> propertySources = (List<Map<String,Object>>)response.get("propertySources");
            for(Map<String,Object> propertySource : propertySources) {
                if(propertySource.get(InventoryConstants.NAME).toString().contains(InventoryConstants.APPLICATION)) {
                    appProps.putAll((Map<String,String>)propertySource.get(InventoryConstants.SOURCE));
                }
                if(propertySource.get(InventoryConstants.NAME).toString().contains(InventoryConstants.BATCH)) {
                    batchProps.putAll((Map<String,String>)propertySource.get(InventoryConstants.SOURCE));
                }
                if(propertySource.get(InventoryConstants.NAME).toString().contains(InventoryConstants.INVENTORY)) {
                    invProps.putAll((Map<String,String>)propertySource.get(InventoryConstants.SOURCE));
                }
                properties.putAll(appProps);
                properties.putAll(batchProps);
                properties.putAll(invProps);
            }
        } catch (Exception e) {
            log.error("Error in fetchConfigProperties",e);
            throw e;
        }
        if(properties.isEmpty()){
        	throw new Exception("No config properties fetched from "+configUrl);
        }
        
        log.info("Config are feteched from {}",configUrl);
        properties.forEach((k,v)-> log.debug("   {} : {} ",k,v));
        return properties;
    }
}
