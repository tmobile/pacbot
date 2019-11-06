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
package com.tmobile.cso.pacman.datashipper.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.Util;

/**
 * The Class ESManager.
 */
public class ESManager implements Constants {

    /** The es host key name. */
    private static final String ES_HOST_KEY_NAME = System.getProperty("elastic-search.host");

    /** The es http port. */
    private static final Integer ES_HTTP_PORT = getESPort();
	
    /** The rest client. */
    private static RestClient restClient;
    
    /** The log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ESManager.class);
    
    /**
     * Gets the ES port.
     *
     * @return the ES port
     */
    private static int getESPort(){
        try{
            return Integer.parseInt(System.getProperty("elastic-search.port"));
        }catch(Exception e){
            return 0;
        }
    }
    /**
     * Gets the rest client.
     *
     * @return the rest client
     */
    private static RestClient getRestClient() {
        if (restClient == null)
            restClient = RestClient.builder(new HttpHost(ES_HOST_KEY_NAME, ES_HTTP_PORT)).build();
        return restClient;

    }

    /**
     * Upload data.
     *
     * @param index            the index
     * @param type            the type
     * @param docs            the docs
     * @param loaddate the loaddate
     * @return the map
     */
    public static Map<String, Object> uploadData(String index, String type, List<Map<String, Object>> docs, String loaddate) {

        Map<String, Object> status = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\" } }%n";

        LOGGER.info("*********UPLOADING*** {}" ,type);

        String keys = ConfigManager.getKeyForType(index, type);
        String[] _keys = keys.split(",");
        if (null != docs && !docs.isEmpty()) {
            LOGGER.info("*********# of docs *** {}" , docs.size());
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, Object> doc : docs) {

                String id = Util.concatenate(doc, _keys, "_");
                StringBuilder _doc = new StringBuilder(createESDoc(doc));
                _doc.deleteCharAt(_doc.length() - 1); 
                _doc.append(",\"latest\":true,\"_loaddate\":\"" + loaddate + "\" }");
                bulkRequest.append(String.format(actionTemplate, index, type, id));
                bulkRequest.append(_doc + "\n");
                i++;
                if (i % 1000 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                    bulkUpload(errors, bulkRequest);
                    bulkRequest = new StringBuilder();
                }
            }
            if (bulkRequest.length() > 0) {
                bulkUpload(errors, bulkRequest);
            }
            LOGGER.info("Updating status");
            refresh(index);
            updateLatestStatus(index, type, loaddate);
            status.put("uploaded_docs", i);
            if (!errors.isEmpty())
                status.put("errors", errors);
        }
        return status;

    }

    /**
     * Bulk upload.
     *
     * @param errors the errors
     * @param bulkRequest the bulk request
     */
    private static void bulkUpload(List<String> errors, StringBuilder bulkRequest) {
        try {
            Response resp = invokeAPI("POST", "/_bulk", bulkRequest.toString());
            String responseStr = EntityUtils.toString(resp.getEntity());
            if (responseStr.contains("\"errors\":true")) {
                List<String> errRecords = Util.retrieveErrorRecords(responseStr);
                LOGGER.error("Upload failed for {}",errRecords);
                errors.addAll(errRecords);
            }
        } catch (Exception e) {
            LOGGER.error("Bulk upload failed",e);
            errors.add(e.getMessage());
        }
    }

    /**
     * Bulk upload.
     *
     * @param bulkRequest the bulk request
     */
    private static void bulkUpload(StringBuilder bulkRequest) {
        try {
            Response resp = invokeAPI("POST", "/_bulk?refresh=true", bulkRequest.toString());
            String responseStr = EntityUtils.toString(resp.getEntity());
            if (responseStr.contains("\"errors\":true")) {
                LOGGER.error(responseStr);
            }
        } catch (ParseException | IOException e) {
            LOGGER.error("Error in uploading data", e);
        }
    }
    
    /**
     * Refresh.
     *
     * @param index
     *            the index
     */
    public static void refresh(String index) {
        try {
            Response refrehsResponse = invokeAPI("POST", index + "/" + "_refresh", null);
            if (refrehsResponse != null && HttpStatus.SC_OK != refrehsResponse.getStatusLine().getStatusCode()) {
                    LOGGER.error("Refreshing index %s failed", index, refrehsResponse);
            }
        } catch (IOException e) {
            LOGGER.error("Error in refresh ",e); 
        }
        
    }

    /**
     * Method not used by the entity upload.But to append data to speific index
     *
     * @param index
     *            the index
     * @param type
     *            the type
     * @param docs
     *            the docs
     * @param idKey
     *            the id key
     * @param refresh
     *            the refresh
     */
    public static void uploadData(String index, String type, List<Map<String, Object>> docs, String idKey,
            boolean refresh) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\"} }%n";
        String endpoint = "/_bulk";
        if (refresh) {
            endpoint = endpoint + "?refresh=true";
        }
        LOGGER.info("*********UPLOADING*** {}" , type);
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, Object> doc : docs) {
                String id = doc.get(idKey).toString();
                StringBuilder _doc = new StringBuilder(createESDoc(doc));

                if (_doc != null) {
                    bulkRequest.append(String.format(actionTemplate, index, type, id));
                    bulkRequest.append(_doc + "\n");
                }
                i++;
                if (i % 1000 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                    LOGGER.info("Uploaded {}" , i);
                    bulkUpload(endpoint, bulkRequest);
                    bulkRequest = new StringBuilder();
                }
            }
            if (bulkRequest.length() > 0) {
                LOGGER.info("Uploaded {}" , i);
                bulkUpload(endpoint, bulkRequest);
            }
        }

    }

    /**
     * Bulk upload.
     *
     * @param endpoint the endpoint
     * @param bulkRequest the bulk request
     */
    private static void bulkUpload(String endpoint, StringBuilder bulkRequest) {
        try { 
            Response resp = invokeAPI("POST", endpoint, bulkRequest.toString());
            String responseStr = EntityUtils.toString(resp.getEntity());
            if (responseStr.contains("\"errors\":true")) {
                LOGGER.error(responseStr);
            }
        } catch (Exception e) {
            LOGGER.error("Bulk upload failed",e);
           
        }
    }

    /**
     * Update latest status.
     *
     * @param index
     *            the index
     * @param type
     *            the type
     * @param loaddate
     *            the loaddate
     */
    private static void updateLatestStatus(String index, String type, String loaddate) {
        String updateJson = "{\"script\":{\"inline\": \"ctx._source.latest=false\"},\"query\": {\"bool\": {\"must\": [{ \"match\": {\"latest\":true}}], \"must_not\": [{\"match\": {\"_loaddate.keyword\":\""
                + loaddate + "\"}}]}}}";
        try {
            invokeAPI("POST", index + "/" + type + "/" + "_update_by_query", updateJson);
        } catch (IOException e) {
            LOGGER.error("Error in updateLatestStatus",e);
        }
    }

    /**
     * Creates the ES doc.
     *
     * @param doc
     *            the doc
     * @return the string
     */
    public static String createESDoc(Map<String, ?> doc) {
        ObjectMapper objMapper = new ObjectMapper();
        String docJson = "{}";
        try {
            docJson = objMapper.writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error createESDoc",e);
        }
        return docJson;
    }

    /**
     * Invoke API.
     *
     * @param method            the method
     * @param endpoint            the endpoint
     * @param payLoad            the pay load
     * @return the response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Response invokeAPI(String method, String endpoint, String payLoad) throws IOException {
        String uri = endpoint;
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        HttpEntity entity = null;
        if (payLoad != null)
            entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);
        
        return getRestClient().performRequest(method, uri, Collections.<String, String>emptyMap(), entity);
    }

    /**
     * Index exists.
     *
     * @param indexName
     *            the index name
     * @return true, if successful
     */
    private static boolean indexExists(String indexName) {
       
        try {
            Response response = invokeAPI("HEAD", indexName, null);
            if (response != null) {
                return response.getStatusLine().getStatusCode() == 200;
            }
        } catch (IOException e) {
            LOGGER.error("Error indexExists",e);
        }
        return false;
    }

    /**
     * Type exists.
     *
     * @param indexName
     *            the index name
     * @param type
     *            the type
     * @return true, if successful
     */
    private static boolean typeExists(String indexName, String type) {
        try {
            Response response = invokeAPI("HEAD", indexName + "/_mapping/" + type, null);
            if (response != null) {
                return response.getStatusLine().getStatusCode() == 200 ? true : false;
            }
        } catch (IOException e) {
            LOGGER.error("Error in typeExists",e);
        }
        
        return false;
    }

    /**
     * Gets the type count.
     *
     * @param indexName
     *            the index name
     * @param type
     *            the type
     * @return the type count
     */
    private static int getTypeCount(String indexName, String type) {
        try {
            Response response = invokeAPI("GET", indexName + "/" + type + "/_count?filter_path=count",
                "{\"query\":{ \"match\":{\"latest\":true}}}");
            String rspJson = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readTree(rspJson).at("/count").asInt();
        } catch (IOException e) {
            LOGGER.error("Error in getTypeCount",e);
        }
        return 0;
    }
    
    /**
     * Configure index and types.
     *
     * @param ds            the ds
     * @param errorList the error list
     */
    public static void configureIndexAndTypes(String ds, List<Map<String, String>> errorList) {

        String _payLoad = "{\"settings\" : { \"number_of_shards\" : 3,\"number_of_replicas\" : 1 },\"mappings\": {";

        Set<String> types = ConfigManager.getTypes(ds);
        Iterator<String> it = types.iterator();
        while (it.hasNext()) {
            String _type = it.next();
            String indexName = ds+ "_" + _type;
            if (!indexExists(indexName)) {
                StringBuilder payLoad = new StringBuilder(_payLoad);
                payLoad.append("\"" + _type + "\":{},\"issue_" + _type + "\": { \"_parent\": {\"type\": \"" + _type
                        + "\"}},\"issue_" + _type + "_audit\": { \"_parent\": {\"type\": \"issue_" + _type
                        + "\"}},\"issue_" + _type + "_comment\": { \"_parent\": {\"type\": \"issue_" + _type
                        + "\"}},\"issue_" + _type + "_exception\": { \"_parent\": {\"type\": \"issue_" + _type
                        + "\"}}");
                payLoad.append("}}");
                try {
                    invokeAPI("PUT", indexName, payLoad.toString());
                } catch (IOException e) {
                    LOGGER.error("Error in configureIndexAndTypes",e);
                    Map<String,String> errorMap = new HashMap<>();
                    errorMap.put(ERROR, "Error in configureIndexAndTypes");
                    errorMap.put(ERROR_TYPE, WARN);
                    errorMap.put(EXCEPTION, e.getMessage());
                    errorList.add(errorMap);
                }
            }
            try {
				invokeAPI("PUT", "/" + indexName + "/_alias/" + ds, null);
				invokeAPI("PUT", "/" + indexName + "/_alias/" + "ds-all", null);
			} catch (IOException e) {
				
			}
        }
     }

    /**
     * Gets the existing info.
     *
     * @param indexName
     *            the index name
     * @param type
     *            the type
     * @param filters
     *            the filters
     * @return the existing info
     */
    public static Map<String, Map<String, String>> getExistingInfo(String indexName, String type,
            List<String> filters) {
        int count = getTypeCount(indexName, type);
        int _count = count;
        boolean scroll = false;
        if (count > 10000) {
            _count = 10000;
            scroll = true;
        }

        String keyField = filters.get(0);
        StringBuilder filter_path = new StringBuilder("&filter_path=_scroll_id,");
        for (String _filter : filters) {
            filter_path.append("hits.hits._source." + _filter + ",");
        }
        filter_path.deleteCharAt(filter_path.length() - 1);

        String endPoint = indexName + "/" + type + "/_search?scroll=1m" + filter_path.toString() + "&size=" + _count;
        String payLoad = "{ \"query\": { \"match\": {\"latest\": true}}}";
        Map<String, Map<String, String>> _data = new HashMap<>();
        String scrollId = fetchDataAndScrollId(endPoint, _data, keyField, payLoad);

        if (scroll) {
            count -= 10000;
            do {
                endPoint = "/_search/scroll?scroll=1m&scroll_id=" + scrollId + filter_path.toString();
                scrollId = fetchDataAndScrollId(endPoint, _data, keyField, null);
                count -= 10000;
                if (count <= 0)
                    scroll = false;
            } while (scroll);
        }
        return _data;
    }

    /**
     * Fetch data and scroll id.
     *
     * @param endPoint
     *            the end point
     * @param _data
     *            the data
     * @param keyField
     *            the key field
     * @param payLoad
     *            the pay load
     * @return the string
     */
    private static String fetchDataAndScrollId(String endPoint, Map<String, Map<String, String>> _data, String keyField,
            String payLoad) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            Response response = invokeAPI("GET", endPoint, payLoad);
            String responseJson = EntityUtils.toString(response.getEntity());
            JsonNode _info = objMapper.readTree(responseJson).at("/hits/hits");
            String scrollId = objMapper.readTree(responseJson).at("/_scroll_id").textValue();
            Iterator<JsonNode> it = _info.elements();
            String doc;
            Map<String, String> docMap;
            while (it.hasNext()) {
                doc = it.next().fields().next().getValue().toString();
                docMap = objMapper.readValue(doc, new TypeReference<Map<String, String>>() {
                });
                _data.put(docMap.get(keyField), docMap);
                docMap.remove(keyField);
            }
            return scrollId;
        } catch (ParseException | IOException e) {
            LOGGER.error("Error in fetchDataAndScrollId" ,e );
        }
        return "";
    }

    /**
     * Fetch current count stats for asset groups.
     *
     * @param date
     *            the date
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Map<String, Object>>> fetchCurrentCountStatsForAssetGroups(String date) {

        Map<String, Map<String, Map<String, Object>>> asgInfoList = new HashMap<>();
        try {
            ObjectMapper objMapper = new ObjectMapper();
            String payLoad = "{\"query\": { \"match\": { \"date\": \"" + date + "\"} }}";
            Response response = invokeAPI("GET", "assetgroup_stats/count_type/_search?size=10000", payLoad);
            String responseJson = EntityUtils.toString(response.getEntity());

            Map<String, Object> docMap = objMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {
            });
            List<Map<String, Object>> docs = (List<Map<String, Object>>) ((Map<String, Object>) docMap.get("hits"))
                    .get("hits");

            for (Map<String, Object> doc : docs) {
                Map<String, Object> _doc = (Map<String, Object>) doc.get("_source");

                Map<String, Map<String, Object>> typeInfo = asgInfoList.get(_doc.get("ag").toString());
                if (typeInfo == null) {
                    typeInfo = new HashMap<>();
                    asgInfoList.put(_doc.get("ag").toString(), typeInfo);
                }

                typeInfo.put(_doc.get("type").toString(), _doc);
                _doc.remove("ag");
                _doc.remove("type");

            }
        } catch (ParseException | IOException e) {
           LOGGER.error("Error in fetchCurrentCountStatsForAssetGroups" ,e );
        }
        return asgInfoList;
    }

    /**
     * Creates the index.
     *
     * @param indexName            the index name
     * @param errorList the error list
     */
    public static void createIndex(String indexName, List<Map<String, String>> errorList) {
        if (!indexExists(indexName)) {
            String payLoad = "{\"settings\": { \"index.mapping.ignore_malformed\": true }}";
            try {
                invokeAPI("PUT", indexName, payLoad);
            } catch (IOException e) {
                LOGGER.error("Error in createIndex" ,e );
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Error in createIndex "+indexName);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                errorList.add(errorMap);
            }
        }
    }

    /**
     * Creates the type.
     *
     * @param indexName            the index name
     * @param typename            the typename
     * @param errorList the error list
     */
    public static void createType(String indexName, String typename, List<Map<String, String>> errorList) {
        if (!typeExists(indexName, typename)) {
            String endPoint = indexName + "/_mapping/" + typename;
            try {
                invokeAPI("PUT", endPoint, "{ \"properties\":{}}");
            } catch (IOException e) {
                LOGGER.error("Error in createType",e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Error in createType "+typename);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                errorList.add(errorMap);
            }
        }
    }
    
    /**
     * Creates the type.
     *
     * @param index the index
     * @param type the type
     * @param parent the parent
     */
    public static void createType(String index, String type, String parent) {
        if (!typeExists(index, type)) {
            String endPoint = index + "/_mapping/" + type;
            String payLoad = "{\"_parent\": { \"type\": \"" + parent + "\" } }";
            try {
                invokeAPI("PUT", endPoint, payLoad);
            } catch (IOException e) {
                LOGGER.error("Error createType ", e);
            }
        }
    }
    
    
    /**
     * added for uploading Child docs where parent id could be dervied from
     * child.
     *
     * @param index the index
     * @param type the type
     * @param docs the docs
     * @param parentKey the parent key
     */
    public static void uploadData(String index, String type, List<Map<String, Object>> docs, String[] parentKey) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_parent\" : \"%s\" } }%n"; // added
                                                                                                                       // _parent
                                                                                                                       // node

        LOGGER.info("*********UPLOADING*** {}", type);
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, Object> doc : docs) {

                StringBuilder _doc = new StringBuilder(new Gson().toJson(doc));
                String parent = Util.concatenate(doc, parentKey, "_");
                bulkRequest.append(String.format(actionTemplate, index, type, parent));
                bulkRequest.append(_doc + "\n");
                i++;
                if (i % 1000 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                    bulkUpload(bulkRequest);
                    bulkRequest = new StringBuilder();
                }
            }
            if (bulkRequest.length() > 0) {
                bulkUpload(bulkRequest);
            }
        }
    }
    
    /**
     * Delete old documents.
     *
     * @param index the index
     * @param type the type
     * @param field the field
     * @param value the value
     */
    public static void deleteOldDocuments(String index, String type, String field, String value) {
        String deleteJson = "{\"query\": {\"bool\": {\"must_not\": [{ \"match\": {\"" + field + "\":\"" + value
                + "\"}}]}}}";
        try {
            invokeAPI("POST", index + "/" + type + "/" + "_delete_by_query", deleteJson);
        } catch (IOException e) {
            LOGGER.error("Error deleteOldDocuments ", e);
        }
    }
    
    /**
     * Update load date.
     *
     * @param index the index
     * @param type the type
     * @param accountId the account id
     * @param region the region
     * @param loaddate the loaddate
     * @param checkLatest the check latest
     */
    public static long updateLoadDate(String index, String type, String accountId, String region, String loaddate,boolean checkLatest) {
    	LOGGER.info("Error records are handled for Account : {} Type : {} Region: {} ",accountId,type,region );
    	StringBuilder updateJson = new StringBuilder("{\"script\":{\"inline\":\"ctx._source._loaddate= '");
    	updateJson.append(loaddate).append("'\"},\"query\":{\"bool\":{\"must\":[");
    	updateJson.append("{\"match\":{\"accountid\":\"");
    	updateJson.append(accountId);
    	updateJson.append("\"}}");
    	if(!Strings.isNullOrEmpty(region)) {
    		updateJson.append(",{\"match\":{\"region.keyword\":\"");
    		updateJson.append(region);
    		updateJson.append("\"}}");
    	}
    	if(checkLatest){
    		updateJson.append(",{\"match\":{\"latest\":true }}");
   
    	}
    	updateJson.append("]}}}");
        try {
        	Response updateInfo = invokeAPI("POST", index + "/" + type + "/" + "_update_by_query", updateJson.toString());
        	String updateInfoJson = EntityUtils.toString(updateInfo.getEntity());
        	return new JsonParser().parse(updateInfoJson).getAsJsonObject().get("updated").getAsLong();
        } catch (IOException e) {
            LOGGER.error("Error in updateLoadDate",e);
        }
        return 0l;
    }
}
