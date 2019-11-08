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
package com.tmobile.cso.pacman.datashipper.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.dao.RDSDBManager;
import com.tmobile.cso.pacman.datashipper.error.ErrorManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.Util;

/**
 * The Class EntityManager.
 */
public class EntityManager implements Constants {

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManager.class);
    
    /** The Constant FIRST_DISCOVERED. */
    private static final String FIRST_DISCOVERED = "firstdiscoveredon";
    
    /** The Constant DISCOVERY_DATE. */
    private static final String DISCOVERY_DATE = "discoverydate";
    
    /** The Constant PAC_OVERRIDE. */
    private static final String PAC_OVERRIDE = "pac_override_";
	
	/** The s 3 account. */
	private String s3Account = System.getProperty("base.account");
	
	/** The s 3 region. */
	private String s3Region = System.getProperty("base.region");
	
	/** The s 3 role. */
	private String s3Role =  System.getProperty("s3.role");
	
	/** The bucket name. */
	private String bucketName =  System.getProperty("s3");
	
	/** The data path. */
	private String dataPath =  System.getProperty("s3.data");
    
    /**
     * Upload entity data.
     *
     * @param datasource            the datasource
     * @return the list
     */
    public List<Map<String, String>> uploadEntityData(String datasource) {
    	List<Map<String,String>> errorList = new ArrayList<>();
        Set<String> types = ConfigManager.getTypes(datasource);
        Iterator<String> itr = types.iterator();
        String type = "";
        LOGGER.info("*** Start Colleting Entity Info ***");
        List<String> filters = Arrays.asList("_docid", FIRST_DISCOVERED);
        EntityAssociationManager childTypeManager = new EntityAssociationManager();
        while (itr.hasNext()) {
            try {
                type = itr.next();
                Map<String, Object> stats = new LinkedHashMap<>();
            	String loaddate = new SimpleDateFormat("yyyy-MM-dd HH:mm:00Z").format(new java.util.Date());
                stats.put("datasource", datasource);
                stats.put("type", type);
                stats.put("start_time",  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date()));
                LOGGER.info("Fetching {}" , type);
                String indexName = datasource + "_" + type;
                Map<String, Map<String, String>> currentInfo = ESManager.getExistingInfo(indexName, type, filters);
                LOGGER.info("Existing no of docs : {}" , currentInfo.size());
                
                List<Map<String, Object>> entities = fetchEntitiyInfoFromS3(datasource,type,errorList);
                List<Map<String, String>> tags = fetchTagsForEntitiesFromS3(datasource, type);
                
                LOGGER.info("Fetched from S3");
                if(!entities.isEmpty()){
	                List<Map<String, String>> overridableInfo = RDSDBManager.executeQuery(
	                        "select updatableFields  from cf_pac_updatable_fields where resourceType ='" + type + "'");
	                List<Map<String, String>> overrides = RDSDBManager.executeQuery(
	                        "select _resourceid,fieldname,fieldvalue from pacman_field_override where resourcetype = '"
	                                + type + "'");
	                Map<String, List<Map<String, String>>> overridesMap = overrides.parallelStream()
	                        .collect(Collectors.groupingBy(obj -> obj.get("_resourceid")));
	                
	                String keys = ConfigManager.getKeyForType(datasource, type); 
	                String idColumn = ConfigManager.getIdForType(datasource, type);
	                String[] keysArray = keys.split(",");
	                
	                prepareDocs(currentInfo, entities, tags, overridableInfo, overridesMap, idColumn, keysArray, type);
	                Map<String,Long> errUpdateInfo = ErrorManager.getInstance(datasource).handleError(indexName,type,loaddate,errorList,true);
	                Map<String, Object> uploadInfo = ESManager.uploadData(indexName, type, entities, loaddate);
	                stats.putAll(uploadInfo);
	                stats.put("errorUpdates", errUpdateInfo);
	                errorList.addAll(childTypeManager.uploadAssociationInfo(datasource, type)) ;
	                
                } 
                stats.put("total_docs", entities.size());
                stats.put("end_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date()));
                stats.put("newly_discovered",entities.stream().filter(entity->entity.get(DISCOVERY_DATE).equals(entity.get(FIRST_DISCOVERED))).count());
                String statsJson = ESManager.createESDoc(stats);
                ESManager.invokeAPI("POST", "/datashipper/stats", statsJson);
            } catch (Exception e) {
                LOGGER.error("Exception in collecting/uploading data for {}" ,type,e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in collecting/uploading data for "+type);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                errorList.add(errorMap);
            }
           
        }
        LOGGER.info("*** End Colleting Entity Info ***");
        return errorList;
    }

	private List<Map<String, String>> fetchTagsForEntitiesFromS3(String datasource, String type) {
		List<Map<String, String>> tags = new ArrayList<>();
		try {
			tags = Util.fetchDataFromS3(s3Account,s3Region, s3Role,bucketName,dataPath+"/"+datasource + "-" + type+"-tags.data");
		} catch (Exception e) {
			 // Do Nothing as there may not a tag file.
		}
		return tags;
	}

	private List<Map<String, Object>> fetchEntitiyInfoFromS3(String datasource,String type,List<Map<String, String>> errorList) {
		List<Map<String, Object>> entities = new ArrayList<>() ;
		try{
			entities = Util.fetchDataFromS3(s3Account,s3Region, s3Role,bucketName, dataPath+"/"+datasource + "-" + type+".data");
		} catch (Exception e) {
			 LOGGER.error("Exception in collecting data for {}" ,type,e);
		     Map<String,String> errorMap = new HashMap<>();
		     errorMap.put(ERROR, "Exception in collecting data for "+type);
		     errorMap.put(ERROR_TYPE, WARN);
		     errorMap.put(EXCEPTION, e.getMessage());
		     errorList.add(errorMap);
		}
		return entities;
	}
	

    
    /**
     * Prepare docs.
     *
     * @param currentInfo the current info
     * @param entities the entities
     * @param tags the tags
     * @param overridableInfo the overridable info
     * @param overridesMap the overrides map
     * @param idColumn the id column
     * @param _keys the keys
     * @param _type the type
     */
    private  void prepareDocs(Map<String, Map<String, String>> currentInfo, List<Map<String, Object>> entities,
            List<Map<String, String>> tags, List<Map<String, String>> overridableInfo,
            Map<String, List<Map<String, String>>> overridesMap, String idColumn, String[] _keys, String _type) {
        entities.parallelStream().forEach(entityInfo -> {
            String id = entityInfo.get(idColumn).toString();
            String docId = Util.concatenate(entityInfo, _keys, "_");
            entityInfo.put("_resourceid", id);
            entityInfo.put("_docid", docId);
            entityInfo.put("_entity", "true");
            entityInfo.put("_entitytype", _type);
            if (currentInfo != null && !currentInfo.isEmpty()) {
                Map<String, String> _currInfo = currentInfo.get(docId);
                if (_currInfo != null) {
                    if (_currInfo.get(FIRST_DISCOVERED) == null) {
                    	_currInfo.put(FIRST_DISCOVERED, entityInfo.get(DISCOVERY_DATE).toString());
                    }
                    entityInfo.putAll(_currInfo);
                } else {
                    entityInfo.put(FIRST_DISCOVERED, entityInfo.get(DISCOVERY_DATE));
                }
            } else {
                entityInfo.put(FIRST_DISCOVERED, entityInfo.get(DISCOVERY_DATE));
            }

            tags.parallelStream().filter(tag -> Util.contains(tag, entityInfo, _keys)).forEach(_tag -> {
                String key = _tag.get("key");
                if (key != null && !"".equals(key)) {
                    entityInfo.put("tags." + key, _tag.get("value"));
                }
            });
            if ("onpremserver".equals(_type)) {
                updateOnPremData(entityInfo);

                if (overridesMap.containsKey(id) || !overridableInfo.isEmpty()) {
                    override(entityInfo, overridesMap.get(id), overridableInfo);
                }
            }
        });
    }

    /**
     * Update on prem data.
     *
     * @param entity
     *            the entity
     */
    private static void updateOnPremData(Map<String, Object> entity) {
        entity.put("tags.Application", entity.get("u_business_service").toString().toLowerCase());
        entity.put("tags.Environment", entity.get("used_for"));
        entity.put("inScope", "true");
    }

    /**
     * Override.
     *
     * @param entity
     *            the entity
     * @param overrideList
     *            the override list
     * @param overrideFields
     *            the override fields
     */
    private static void override(Map<String, Object> entity, List<Map<String, String>> overrideList,
            List<Map<String, String>> overrideFields) {

        if (overrideList != null && !overrideList.isEmpty()) {
            overrideList.forEach(obj -> {
                String key = obj.get("fieldname");
                String value = obj.get("fieldvalue");
                if (null == value)
                    value = "";
                entity.put(key, value);
            });
        }

        // Add override fields if not already populated
        if (overrideFields != null && !overrideFields.isEmpty()) {
            String strOverrideFields = overrideFields.get(0).get("updatableFields");
            String[] _strOverrideFields = strOverrideFields.split(",");
            for (String _strOverrideField : _strOverrideFields) {
                if (!entity.containsKey(_strOverrideField)) {
                    entity.put(_strOverrideField, "");
                }

                String value = entity.get(_strOverrideField).toString();
                if (_strOverrideField.startsWith(PAC_OVERRIDE)) {
                    String originalField = _strOverrideField.replace(PAC_OVERRIDE, "");
                    String finalField = _strOverrideField.replace(PAC_OVERRIDE, "final_");
                    if (entity.containsKey(originalField)) { // Only if the
                                                             // field exists in
                                                             // source, we need
                                                             // to add
                        String originalValue = entity.get(originalField).toString();
                        if ("".equals(value)) {
                            entity.put(finalField, originalValue);
                        } else {
                            entity.put(finalField, value);
                        }
                    }

                }
            }
        }
    }

   
    
    
}