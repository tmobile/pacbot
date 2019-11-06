package com.tmobile.cso.pacman.datashipper.config;

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
import com.tmobile.cso.pacman.datashipper.dao.RDSDBManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;


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
        
        if (typeInfo == null) {
            typeInfo = new HashMap<>();
            List<Map<String, String>> typeList = RDSDBManager.executeQuery(System.getProperty(Constants.CONFIG_QUERY)+" and dataSourceName ='"+datasoruce+"'");
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
