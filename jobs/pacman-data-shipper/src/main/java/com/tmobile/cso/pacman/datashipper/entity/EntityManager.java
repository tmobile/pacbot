package com.tmobile.cso.pacman.datashipper.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.config.CredentialProvider;
import com.tmobile.cso.pacman.datashipper.dao.RDSDBManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.Util;


/**
 * The Class EntityManager.
 */
public class EntityManager implements Constants {

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManager.class);
    private static final String FIRST_DISCOVERED = "firstdiscoveredon";
    private static final String DISCOVERY_DATE = "discoverydate";
    private static final String PAC_OVERRIDE = "pac_override_";
	private String s3Account = System.getProperty("base.account");
	private String s3Region = System.getProperty("base.region");
	private String s3Role =  System.getProperty("s3.role");
	private String bucketName =  System.getProperty("s3");
	private String dataPath =  System.getProperty("s3.data");
    
    /**
     * Upload entity data.
     *
     * @param datasource
     *            the datasource
     */
    public List<Map<String, String>> uploadEntityData(String datasource) {
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	List<Map<String,String>> errorList = new ArrayList<>();
    	
    	AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new CredentialProvider().getCredentials(s3Account,s3Role))).withRegion(s3Region).build();
    	
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
                S3Object entitiesData = null ;
                S3Object tagsData = null;
                List<Map<String, String>> entities = new ArrayList<>();
                List<Map<String, String>> tags = new ArrayList<>();
                try {
                	entitiesData = s3Client.getObject(new GetObjectRequest(bucketName, dataPath+"/"+datasource + "-" + type+".data"));
                	try (BufferedReader reader = new BufferedReader(new InputStreamReader(entitiesData.getObjectContent()))) {
                    	entities = objectMapper.readValue(reader.lines().collect(Collectors.joining("\n")),new TypeReference<List<Map<String, String>>>() {});
                    }
                } catch (Exception e) {
                	 LOGGER.error("Exception in collecting data for {}" ,type,e);
                     Map<String,String> errorMap = new HashMap<>();
                     errorMap.put(ERROR, "Exception in collecting data for "+type);
                     errorMap.put(ERROR_TYPE, WARN);
                     errorMap.put(EXCEPTION, e.getMessage());
                     errorList.add(errorMap);
                }
                
                try {
                	tagsData = s3Client.getObject(new GetObjectRequest(bucketName, dataPath+"/"+datasource + "-" + type+"-tags.data"));
                	try (BufferedReader reader = new BufferedReader(new InputStreamReader(tagsData.getObjectContent()))) {
                    	tags = objectMapper.readValue(reader.lines().collect(Collectors.joining("\n")),new TypeReference<List<Map<String, String>>>() {});
                    }
                } catch (Exception e) {
                	 // Do Nothing as there may not a tag file.
                }
                LOGGER.info("Fetched from S3");
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
                
                AWSErrorManager.getInstance().handleError(datasource,indexName,type,loaddate,errorList,true);
                prepareDocs(currentInfo, entities, tags, overridableInfo, overridesMap, idColumn, keysArray, type);
                stats.put("total_docs", entities.size());
              
                Map<String, Object> uploadInfo = ESManager.uploadData(indexName, type, entities, loaddate);
                stats.putAll(uploadInfo);
                
                errorList.addAll(childTypeManager.uploadAssociationInfo(datasource, type)) ;
                stats.put("end_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date()));

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
    private  void prepareDocs(Map<String, Map<String, String>> currentInfo, List<Map<String, String>> entities,
            List<Map<String, String>> tags, List<Map<String, String>> overridableInfo,
            Map<String, List<Map<String, String>>> overridesMap, String idColumn, String[] _keys, String _type) {
        entities.parallelStream().forEach(entityInfo -> {
            String id = entityInfo.get(idColumn);
            String docId = Util.concatenate(entityInfo, _keys, "_");
            entityInfo.put("_resourceid", id);
            entityInfo.put("_docid", docId);
            entityInfo.put("_entity", "true");
            entityInfo.put("_entitytype", _type);
            if (currentInfo != null && !currentInfo.isEmpty()) {
                Map<String, String> _currInfo = currentInfo.get(docId);
                if (_currInfo != null) {
                    if (_currInfo.get(FIRST_DISCOVERED) == null) {
                        _currInfo.put(FIRST_DISCOVERED, entityInfo.get(DISCOVERY_DATE));
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
    private static void updateOnPremData(Map<String, String> entity) {
        entity.put("tags.Application", entity.get("u_business_service").toLowerCase());
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
    private static void override(Map<String, String> entity, List<Map<String, String>> overrideList,
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

                String value = entity.get(_strOverrideField);
                if (_strOverrideField.startsWith(PAC_OVERRIDE)) {
                    String originalField = _strOverrideField.replace(PAC_OVERRIDE, "");
                    String finalField = _strOverrideField.replace(PAC_OVERRIDE, "final_");
                    if (entity.containsKey(originalField)) { // Only if the
                                                             // field exists in
                                                             // source, we need
                                                             // to add
                        String originalValue = entity.get(originalField);
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