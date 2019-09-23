package com.tmobile.cso.pacman.qualys.util;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


/**
 * The Class ElasticSearchManager.
 */
public class ElasticSearchManager {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchManager.class);

    /** The Constant ES_HOST_KEY_NAME. */
    private static final String ES_HOST_KEY_NAME = System.getProperty("elastic-search.host");
    
    /** The Constant ES_HTTP_PORT. */
    private static final Integer ES_HTTP_PORT = Integer.parseInt(System.getProperty("elastic-search.port"));
    
    /** The rest client. */
    private static RestClient restClient;

    /**
     * Instantiates a new elastic search manager.
     */
    private ElasticSearchManager() {

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
     * Creates the index.
     *
     * @param indexName the index name
     */
    public static void createIndex(String index) {
    	String indexName = "/"+index;
        if (!indexExists(indexName)) {
            String payLoad = "{\"settings\": { \"index.mapping.ignore_malformed\": true }}";
            try {
                invokeAPI("PUT", indexName, payLoad);
            } catch (IOException e) {
                LOGGER.error("Error createIndex ", e);
            }
        }
    }

    /**
     * Creates the type.
     *
     * @param indexName the index name
     * @param typename the typename
     */
    public static void createType(String index, String typename) {
    	String indexName = "/"+index;
        if (!typeExists(indexName, typename)) {
            String endPoint = indexName + "/_mapping/" + typename;
            try {
                invokeAPI("PUT", endPoint, "{ \"properties\":{}}");
            } catch (IOException e) {
                LOGGER.error("Error in method createType", e);
                ;
            }
        }
    }

    /**
     * Creates the type as parent.
     *
     * @param indexName the index name
     * @param typename the typename
     */
    public static void createTypeAsParent(String indexName, String typename) {
        if (!typeExists(indexName, typename)) {
            String endPoint = indexName + "/_mapping/" + typename;
            try {
                invokeAPI("PUT", endPoint, "{ \"properties\":{}, \"issue_" + typename
                        + "\":{ \"_parent\": { \"type\": \"" + typename + "\" }}	}");
            } catch (IOException e) {
                LOGGER.error("Error at createTypeAsParent", e);
            }
        }
    }

    /**
     * Creates the alias.
     *
     * @param indexName the index name
     * @param aliasName the alias name
     */
    public static void createAlias(String indexName, String aliasName) {
        try {
            invokeAPI("PUT", "/" + indexName + "/_alias/" + aliasName, null);
        } catch (IOException e) {
            LOGGER.error("Error in createAlias ", e);
        }
    }

    /**
     * Bulk upload.
     *
     * @param bulkRequest the bulk request
     */
    private static void bulkUpload(StringBuilder bulkRequest) {
        try {
            Response resp = invokeAPI("POST", "/_bulk", bulkRequest.toString());
            String responseStr = EntityUtils.toString(resp.getEntity());
            if (responseStr.contains("\"errors\":true")) {
                LOGGER.error(responseStr);
            }
        } catch (ParseException | IOException e) {
            LOGGER.error("Error in uploading data", e);
        }
    }

    /**
     * Upload data.
     *
     * @param index the index
     * @param type the type
     * @param docs the docs
     * @param idKey the id key
     */
    public static void uploadData(String index, String type, List<Map<String, Object>> docs, String idKey) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\"} }%n";

        LOGGER.info("*********UPLOADING*** {}", type);
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, Object> doc : docs) {
                String id = doc.get(idKey).toString();
                StringBuilder _doc = new StringBuilder(createESDoc(doc));
                bulkRequest.append(String.format(actionTemplate, index, type, id));
                bulkRequest.append(_doc + "\n");
                i++;
                if (i % 1000 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                    LOGGER.info("Uploaded {}", i);
                    bulkUpload(bulkRequest);
                    bulkRequest = new StringBuilder();
                }
            }
            if (bulkRequest.length() > 0) {
                LOGGER.info("Uploaded {}", i);
                bulkUpload(bulkRequest);
            }
            refresh(index);
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
    public static void uploadData(String index, String type, List<Map<String, Object>> docs, String parentKey,String idKey,boolean removeIdKey) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\" , \"_parent\" : \"%s\"} }%n";

        LOGGER.info("*********UPLOADING*** {}", type);
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, Object> doc : docs) {

                
                String parent = doc.get(parentKey).toString();
                String id =  doc.get(idKey).toString();
                if(removeIdKey){
                    doc.remove(idKey);
                }
                StringBuilder _doc = new StringBuilder(new Gson().toJson(doc));
                bulkRequest.append(String.format(actionTemplate, index, type,id, parent));
                bulkRequest.append(_doc + "\n");
                i++;
                if (i % 1000 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                    LOGGER.info("Uploading {}", i);
                    bulkUpload(bulkRequest);
                    bulkRequest = new StringBuilder();
                }
            }
            if (bulkRequest.length() > 0) {
                LOGGER.info("Uploaded {}", i);
                bulkUpload(bulkRequest);
            }
            refresh(index);
        }
    }

    /**
     * Refresh.
     *
     * @param index the index
     */
    public static void refresh(String index) {
    	String indexName = "/"+index;
        try {
            Response refrehsResponse = invokeAPI("POST", indexName + "/" + "_refresh", null);
            if (refrehsResponse != null && HttpStatus.SC_OK != refrehsResponse.getStatusLine().getStatusCode()) {
                LOGGER.error("Refreshing index {} failed", index, refrehsResponse);
            }
        } catch (IOException e) {
            LOGGER.error("Error refresh ", e);
        }

    }

    /**
     * Creates the ES doc.
     *
     * @param doc the doc
     * @return the string
     */
    public static String createESDoc(Map<String, Object> doc) {
        return new Gson().toJson(doc);
    }

    /**
     * Invoke API.
     *
     * @param method the method
     * @param endpoint the endpoint
     * @param payLoad the pay load
     * @return the response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Response invokeAPI(String method, String endpoint, String payLoad) throws IOException {
        HttpEntity entity = null;
        if (payLoad != null)
            entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);
        return getRestClient().performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
    }

    /**
     * Index exists.
     *
     * @param indexName the index name
     * @return true, if successful
     */
    private static boolean indexExists(String indexName) {

        try {
            Response response = invokeAPI("HEAD", indexName, null);
            if (response != null) {
                return response.getStatusLine().getStatusCode() == 200 ? true : false;
            }
        } catch (IOException e) {
            LOGGER.error("Error indexExists ", e);
        }

        return false;
    }

    /**
     * Type exists.
     *
     * @param indexName the index name
     * @param type the type
     * @return true, if successful
     */
    private static boolean typeExists(String indexName, String type) {

        try {
            Response response = invokeAPI("HEAD", indexName + "/_mapping/" + type, null);
            if (response != null) {
                return response.getStatusLine().getStatusCode() == 200 ? true : false;
            }
        } catch (IOException e) {
            LOGGER.error("Error typeExists ", e);
        }

        return false;
    }

    /**
     * Gets the type count.
     *
     * @param indexName the index name
     * @param type the type
     * @return the type count
     */
    private static int getTypeCount(String indexName, String type) {

        try {
            Response response = invokeAPI("GET", indexName + "/" + type + "/_count?filter_path=count", null);
            String rspJson = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readTree(rspJson).at("/count").asInt();
        } catch (IOException e) {
            LOGGER.error("Error getTypeCount ", e);
        }
        return 0;
    }

    /**
     * Creates the type.
     *
     * @param index the index
     * @param type the type
     * @param parent the parent
     */
    public static void createType(String index, String type, String parent) {
    	String indexName = "/"+index;
        if (!typeExists(indexName, type)) {
            String endPoint = indexName + "/_mapping/" + type;
            String payLoad = "{\"_parent\": { \"type\": \"" + parent + "\" } }";
            try {
                invokeAPI("PUT", endPoint, payLoad);
            } catch (IOException e) {
                LOGGER.error("Error createType ", e);
            }
        }
    }

    /**
     * Gets the existing info.
     *
     * @param indexName the index name
     * @param type the type
     * @param filters the filters
     * @param latest the latest
     * @return the existing info
     */
    public static Map<String, Map<String, String>> getExistingInfo(String index, String type, List<String> filters,
            boolean latest) {
    	String indexName = "/"+index;
        int count = getTypeCount(indexName, type);
        int _count = count;
        boolean scroll = false;
        int SCROLL_SIZE = 10000;
        if (count > SCROLL_SIZE) {
            _count = SCROLL_SIZE;
            scroll = true;
        }

        String keyField = filters.get(0);
        String filter_path = "&filter_path=hits.hits._source,_scroll_id";

        StringBuilder payLoad = new StringBuilder("{ \"_source\": [");
        for (String _filter : filters) {
            payLoad.append("\"" + _filter + "\",");
        }
        payLoad.deleteCharAt(payLoad.length() - 1);
        if (latest)
            payLoad.append("],\"query\": { \"match\": {\"latest\": true}}}");
        else
            payLoad.append("]}");

        String endPoint = indexName + "/" + type + "/_search?scroll=1m" + filter_path + "&size=" + _count;

        Map<String, Map<String, String>> _data = new HashMap<>();
        String scrollId = fetchDataAndScrollId(endPoint, _data, keyField, payLoad.toString());

        if (scroll) {
            count -= SCROLL_SIZE;
            do {
                endPoint = "/_search/scroll?scroll=1m&scroll_id=" + scrollId + filter_path;
                scrollId = fetchDataAndScrollId(endPoint, _data, keyField, null);
                count -= SCROLL_SIZE;
                if (count < 0)
                    scroll = false;
            } while (scroll);
        }
        // invokeAPI("DELETE", "/_search/scroll?scroll_id="+scrollId, null);
        return _data;
    }

    /**
     * Fetch data and scroll id.
     *
     * @param endPoint the end point
     * @param _data the data
     * @param keyField the key field
     * @param payLoad the pay load
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
            while (it.hasNext()) {
                String doc = it.next().fields().next().getValue().toString();
                Map<String, String> docMap = new ObjectMapper().readValue(doc,
                        new TypeReference<Map<String, String>>() {
                        });
                _data.put(docMap.get(keyField), docMap);
                docMap.remove(keyField);
            }
            return scrollId;
        } catch (ParseException | IOException e) {
            LOGGER.error("Error fetchDataAndScrollId ", e);
        }
        return "";

    }
    
    /**
     * Update latest status.
     *
     * @param index the index
     * @param type the type
     * @param discoveryDate the discovery date
     */
    public static void updateLatestStatus(String index, String type, String discoveryDate) {
    	String indexName = "/"+index;
        String updateJson = "{\"script\":{\"inline\": \"ctx._source.latest=false\"},\"query\": {\"bool\": {\"must\": [{ \"match\": {\"latest\":true}}], \"must_not\": [{\"match\": {\"discoverydate.keyword\":\""
                + discoveryDate + "\"}}]}}}";
        try {
            invokeAPI("POST", indexName + "/" + type + "/" + "_update_by_query", updateJson);
        } catch (IOException e) {
            LOGGER.error("Error updateLatestStatus ", e);
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
    	String indexName = "/"+index;
        String deleteJson = "{\"query\": {\"bool\": {\"must_not\": [{ \"match\": {\"" + field + "\":\"" + value
                + "\"}}]}}}";
        try {
            invokeAPI("POST", indexName + "/" + type + "/" + "_delete_by_query", deleteJson);
        } catch (IOException e) {
            LOGGER.error("Error deleteOldDocuments ", e);
        }
    }
}
