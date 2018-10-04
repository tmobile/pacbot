package com.tmobile.cso.pacman.datashipper.entity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.dao.DBManager;
import com.tmobile.cso.pacman.datashipper.dao.RDSDBManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Util;


/**
 * The Class EntityManager.
 */
public class EntityManager {

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManager.class);
    private static final String FIRST_DISCOVERED = "firstdiscoveredon";
    private static final String DISCOVERY_DATE = "discoverydate";
    private static final String PAC_OVERRIDE = "pac_override_";
    
    /**
     * Upload entity data.
     *
     * @param datasource
     *            the datasource
     */
    public void uploadEntityData(String datasource) {

        Set<String> types = ConfigManager.getTypes(datasource);
        Iterator<String> itr = types.iterator();
        String type = "";
        LOGGER.info("*** Start Colleting Entity Info ***");
        List<String> filters = Arrays.asList("_docid", FIRST_DISCOVERED);
        while (itr.hasNext()) {

            try {
                type = itr.next();
                Map<String, Object> stats = new LinkedHashMap<>();
                stats.put("datasource", datasource);
                stats.put("type", type);
                stats.put("start_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date()));

                //if(type.equals("s3")){

                LOGGER.info("Fetching {}" , type);
                String indexName = datasource + "_" + type;

                Map<String, Map<String, String>> currentInfo = ESManager.getExistingInfo(indexName, type, filters);
                LOGGER.info("Existing no of docs : {}" , currentInfo.size());

                List<Map<String, String>> entities = DBManager.executeQuery("select * from " + datasource + "_" + type);
                List<Map<String, String>> tags = DBManager
                        .executeQuery("select * from " + datasource + "_" + type + "_tags");
                List<Map<String, String>> overridableInfo = RDSDBManager.executeQuery(
                        "select updatableFields  from cf_pac_updatable_fields where resourceType ='" + type + "'");
                List<Map<String, String>> overrides = DBManager.executeQuery(
                        "select _resourceid,fieldname,fieldvalue from pacman_field_override where resourcetype = '"
                                + type + "'");
                Map<String, List<Map<String, String>>> overridesMap = overrides.parallelStream()
                        .collect(Collectors.groupingBy(obj -> obj.get("_resourceid")));

                String keys = ConfigManager.getKeyForType(datasource, type);
                String idColumn = ConfigManager.getIdForType(datasource, type);
                String[] _keys = keys.split(",");
                LOGGER.info("Fetched from Redshift");
                String _type = type;
                prepareDocs(currentInfo, entities, tags, overridableInfo, overridesMap, idColumn, _keys, _type);
                LOGGER.info("Docs are prepared");
                stats.put("total_docs", entities.size());
                Map<String, Object> uploadInfo = ESManager.uploadData(indexName, type, entities);
                stats.putAll(uploadInfo);
                //}
                stats.put("end_time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date()));

                String statsJson = ESManager.createESDoc(stats);
                ESManager.invokeAPI("POST", "/datashipper/stats", statsJson);
            } catch (Exception e) {
                LOGGER.error("Exception in collecting/uploading data for {}" ,type,e);
            }

        }
        LOGGER.info("*** End Colleting Entity Info ***");
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
                        String oringalValue = entity.get(originalField);
                        if ("".equals(value)) {
                            entity.put(finalField, oringalValue);
                        } else {
                            entity.put(finalField, value);
                        }
                    }

                }
            }
        }

    }

}