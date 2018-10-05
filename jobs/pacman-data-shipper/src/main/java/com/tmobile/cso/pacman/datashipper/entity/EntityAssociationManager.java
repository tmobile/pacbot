package com.tmobile.cso.pacman.datashipper.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.dao.DBManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;




/**
 * The Class ChildTableDataCollector.
 */
public class EntityAssociationManager implements Constants {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityAssociationManager.class);

    /**
     * Execute.
     *
     * @param dataSource the data source
     * @return 
     */
    public List<Map<String, String>> uploadAssociationInfo(String dataSource) {
        LOGGER.info("Started EntityAssociationDataCollector");
        List<Map<String,String>> errorList = new ArrayList<>();
        Set<String> types = ConfigManager.getTypes(dataSource);
        Iterator<String> itr = types.iterator();
        String type = "";

        while (itr.hasNext()) {
            try {
                type = itr.next();
                String indexName = dataSource + "_" + type;
                List<String> childTableNames = DBManager.getChildTableNames(indexName);
                String key = ConfigManager.getKeyForType(dataSource, type);
                if (!childTableNames.isEmpty()) {
                    for (String childTable : childTableNames) {
                        String childTableES = childTable.substring(childTable.indexOf('_') + 1);
                        if (!childTable.equalsIgnoreCase(indexName + "_tags")) {
                            ESManager.createType(indexName, childTableES, type);
                            LOGGER.info("Fetching data from {}", childTable);
                            List<Map<String, String>> entities = DBManager.executeQuery("select * from " + childTable);
                            String loaddate = new SimpleDateFormat("yyyy-MM-dd H:mm:00Z").format(new java.util.Date());
                            entities.parallelStream().forEach(obj -> obj.put("_loaddate", loaddate));
                            LOGGER.info("Collected :  {}", entities.size());
                            if (!entities.isEmpty()) {
                                ESManager.uploadData(indexName, childTableES, entities, key.split(","));
                                ESManager.deleteOldDocuments(indexName, childTableES, "_loaddate.keyword",
                                        loaddate);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error in populating child tables", e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Error in populating child tables");
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                errorList.add(errorMap);
            }
        }
        LOGGER.info("Completed ChildTableDataCollector");
        return errorList;
    }
}
