package com.tmobile.pacman.cloud.es;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.pacman.cloud.exception.DataException;
import com.tmobile.pacman.cloud.util.Constants;
import com.tmobile.pacman.cloud.util.HttpUtil;
import com.tmobile.pacman.cloud.util.Util;

public class ElasticSearchRepository {

	/** The es host key name. */
	private static final String ES_HOST = System.getProperty(Constants.ELASTIC_SEARCH_HOST);

	/** The es http port. */
	private static final Integer ES_HTTP_PORT = getESPort(System.getProperty(Constants.ELASTIC_SEARCH_PORT));

	/** The heimdall es host key name. */
	private static final String HEIMDAL_ES_HOS = System.getProperty(Constants.ELASTCIC_SEARCH_HOST_HEIMDALL);

	/** The rest client. */
	private static RestClient restClient;

	/** The Constant PROTOCOL. */
	static final String PROTOCOL = "http";

	/** The es url. */
	private static String esUrl = PROTOCOL + "://" + ES_HOST + ":" + ES_HTTP_PORT;;
	
	/** The es type. */
	private static String ESTYPE = "cloud_notification";

	/** The log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchRepository.class);

	/**
	 * Gets the ES port.
	 *
	 * @return the ES port
	 */
	public static int getESPort(String port) {
		try {
			return Integer.parseInt(port);
		} catch (Exception e) {
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
			restClient = RestClient.builder(new HttpHost(ES_HOST, ES_HTTP_PORT)).build();
		return restClient;

	}
	
	/**
	 * Gets the phd events by targetType
	 *
	 * @param targetType name of the asset group
	 * @return the phd events by targetType
	 * @throws DataException when there is error while fetching data from ES
	 */
	public static List<Map<String, Object>> getPhdEvents(String targetType) throws DataException {
		LOGGER.info("****In getPhdEvents*****" + targetType);
		String responseDetails = null;
		System.out.println(esUrl);
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(Constants.AWS_PHD).append("/").append(Constants.PHD).append("/")
				.append(Constants._SEARCH);
		StringBuilder requestBody = null;
		{
			String body  = "{\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"eventservice.keyword\":\""
					+ targetType+ "\"}}]}}}";
			
			requestBody = new StringBuilder(body);
			try {
				responseDetails = HttpUtil.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
			} catch (Exception e) {
				LOGGER.error("failed to fetch phd events in getPhdEvents", Util.getStackTrace(e));
				throw new DataException(e);
			}
			List<Map<String, Object>> arnList = new ArrayList<Map<String, Object>>();
			String HITS = Constants.HITS;
			Gson serializer = new GsonBuilder().create();
			List<Map<String, Object>> iamDetails = null;
			Map<String, Object> responseMap = (Map<String, Object>) serializer.fromJson(responseDetails, Object.class);
			if (responseMap.containsKey(HITS)) {
				Map<String, Object> hits = (Map<String, Object>) responseMap.get(HITS);
				if (hits.containsKey(HITS)) {
					iamDetails = (List<Map<String, Object>>) hits.get(HITS);
					for (Map<String, Object> iamDetail : iamDetails) {
						Map<String, Object> sourceMap = (Map<String, Object>) iamDetail.get(Constants._SOURCE);
						arnList.add(sourceMap);
					}
				}
			}
			return arnList;
		}
	}

	/**
	 * Gets the phd entities by phd event Arn
	 *
	 * @param phdEventArn name of the asset group
	 * @return the phd entities by phd event
	 * @throws DataException when there is error while fetching data from ES
	 */
	public static String getPhdEnityByArn(String phdEventArn) throws DataException {
		LOGGER.info("****In getPhdEnityByArn*****" + phdEventArn);
		String responseDetails = null;
		System.out.println(esUrl);
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(Constants.AWS_PHD).append("/").append(Constants.PHD_ENTITES).append("/")
				.append(Constants._SEARCH);
		StringBuilder requestBody = null;
		{
			String body  = "{\"size\":10000,\"_source\":[\"entityvalue\"],\"query\":{\"match\":{\"eventarn.keyword\":\""
					+ phdEventArn+ "\"}}}";
			
			requestBody = new StringBuilder(body);
			try {
				responseDetails = HttpUtil.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
			} catch (Exception e) {
				LOGGER.error("failed to fetch phdEntity in getPhdEnityByArn", Util.getStackTrace(e));
				throw new DataException(e);
			}
			String entity = "";
			String HITS = Constants.HITS;
			Gson serializer = new GsonBuilder().create();
			List<Map<String, Object>> iamDetails = null;
			Map<String, Object> responseMap = (Map<String, Object>) serializer.fromJson(responseDetails, Object.class);
			if (responseMap.containsKey(HITS)) {
				Map<String, Object> hits = (Map<String, Object>) responseMap.get(HITS);
				if (hits.containsKey(HITS)) {
					iamDetails = (List<Map<String, Object>>) hits.get(HITS);
					for (Map<String, Object> iamDetail : iamDetails) {
						Map<String, Object> sourceMap = (Map<String, Object>) iamDetail.get(Constants._SOURCE);
						entity = sourceMap.get("entityvalue").toString();
					}
				}
			}
			return entity;
		}
	}
	
	/**
	 * Gets the Pacbot Resource Details by Phd entity events by targetType
	 *
	 * @param index name of the index
	 * @param type type of the index
	 * @param resourceKey the keyname of the resource
	 * @param phdEntity name of the asset group
	 * @return the resource details by phdEntity
	 * @throws DataException when there is error while fetching data from ES
	 */
	public static List<Map<String, Object>> getPacResourceDet(String index, String key, String value, String phdEntity) throws DataException {
		LOGGER.info("****In getPacResourceDet*****" + phdEntity);
		String responseDetails = null;
		System.out.println(esUrl);
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append("aws_"+index).append("/").append(index).append("/")
				.append(Constants._SEARCH);
		StringBuilder requestBody = null;
		{
			//String body  = "{\"size\":1,\"_source\":[\""+value+"\",\"_docid\"],\"query\":{\"bool\":{\"must\":[{\"match\":{\""+key+"\":\""
				//	+ phdEntity+ "\"}}]}}}";
			String body = "{\"size\":1,\"_source\":[\""+value+"\",\"_docid\"],\"query\":{\"bool\":{\"must\":[{\"match\":{\""+key+"\":\""
					+phdEntity+"\"}},{\"match\":{\"latest\":true}}]}}}";
			
			requestBody = new StringBuilder(body);
			try {
				responseDetails = HttpUtil.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
			} catch (Exception e) {
				LOGGER.error("failed to fetch resources in getPacResourceDet", Util.getStackTrace(e));
				throw new DataException(e);
			}
			List<Map<String, Object>> arnList = new ArrayList<Map<String, Object>>();
			String HITS = Constants.HITS;
			Gson serializer = new GsonBuilder().create();
			List<Map<String, Object>> iamDetails = null;
			Map<String, Object> responseMap = (Map<String, Object>) serializer.fromJson(responseDetails, Object.class);
			if (responseMap.containsKey(HITS)) {
				Map<String, Object> hits = (Map<String, Object>) responseMap.get(HITS);
				if (hits.containsKey(HITS)) {
					iamDetails = (List<Map<String, Object>>) hits.get(HITS);
					for (Map<String, Object> iamDetail : iamDetails) {
						Map<String, Object> sourceMap = (Map<String, Object>) iamDetail.get(Constants._SOURCE);
						arnList.add(sourceMap);
					}
				}
			}
			return arnList;
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
    
    
    public static void bulkUpload(List<String> errors, String bulkRequest) {
        try {
            System.out.println("********"+bulkRequest);
        	Response resp = invokeAPI("POST", "/_bulk?refresh=true", bulkRequest);
            
            String responseStr = EntityUtils.toString(resp.getEntity());
            if (responseStr.contains("\"errors\":true")) {
            	LOGGER.error(responseStr);
                errors.add(responseStr);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	LOGGER.error("Bulk upload failed",e);
            errors.add(e.getMessage());
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
    public static void uploadData(List<Map<String, String>> docs, String[] parentKey) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_parent\" : \"%s\" } }%n"; // added
                                                                                                                       // _parent
                                                                                                                       // node
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, String> doc : docs) {
            	String type = doc.get("type").toString();
            	String index = "aws_" + type;
            	LOGGER.info("*********UPLOADING*** {}", type);
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
     * added for uploading Child docs where parent id could be dervied from
     * child.
     *
     * @param index the index
     * @param type the type
     * @param docs the docs
     * @param parentKey the parent key
     */
    public static void uploadDataWithParent(List<Map<String, Object>> docs) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\", \"_parent\" : \"%s\" } }%n"; // added
                                                                                                                       // _parent
                                                                                                                       // node
        String loaddate = new SimpleDateFormat("yyyy-MM-dd HH:mm:00Z").format(new java.util.Date());
        List<Map<String, Object>> updateStatus = new  ArrayList<Map<String, Object>>(); 
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            LOGGER.info("*********UPLOADING*** {}");
            for (Map<String, Object> doc : docs) {
            	String type = doc.get(Constants.TYPE).toString();
            	String index = "aws_" + type;
            	Map<String, Object> indexMap = new HashMap<String, Object>();
            	String awsType = "aws_"+type;
        		indexMap.put(Constants.TYPE, ESTYPE);
        		indexMap.put(Constants.INDEX, awsType);
        		indexMap.put(Constants.LOADDATE, loaddate);
        		updateStatus.add(indexMap);
        	    StringBuilder _doc = new StringBuilder(new Gson().toJson(doc));
                createType("aws_" + type, ESTYPE, type);
                _doc.deleteCharAt(_doc.length() - 1); 
    			_doc.append(",\"_loaddate\":\"" + loaddate + "\" }");
                String parent = doc.get(Constants._DOCID).toString();
                String idkey = Util.getUniqueID(doc.get(Constants._DOCID).toString() + doc.get(Constants.EVENTARN));
                bulkRequest.append(String.format(actionTemplate, index, ESTYPE, idkey, parent));
                bulkRequest.append(_doc + "\n");
                i++;
                if (i % 100 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
                	LOGGER.info("Uploaded {}" + i);
                    bulkUpload(bulkRequest);
                    bulkRequest = new StringBuilder();
                }
            }
            if (bulkRequest.length() > 0) {
            	LOGGER.info("Uploaded {}" + i);
                bulkUpload(bulkRequest);
            }
            updateStatus = updateStatus.stream().distinct().collect(Collectors.toList());
            updateStatus.parallelStream().forEach(updateMap -> {
            	LOGGER.info("Updating status");
    			refresh(updateMap.get(Constants.INDEX).toString());
    			updateLatestStatus(updateMap.get(Constants.INDEX).toString(), updateMap.get(Constants.TYPE).toString(), updateMap.get(Constants.LOADDATE).toString());
    		});
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
	 * Method not used by the entity upload.But to append data to speific index
	 *
	 * @param index   the index
	 * @param type    the type
	 * @param docs    the docs
	 * @param idKey   the id key
	 * @param refresh the refresh
	 */
	public static void uploadData(String index, String type, List<Map<String, Object>> docs, String idKey,
			boolean refresh) {
		try {
			String loaddate = new SimpleDateFormat("yyyy-MM-dd HH:mm:00Z").format(new java.util.Date());
			String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\"} }%n";
			String endpoint = "/_bulk";
			if (refresh) {
				endpoint = endpoint + "?refresh=true";
			}
			LOGGER.info("*********UPLOADING*** {}" + type);
			if (null != docs && !docs.isEmpty()) {
				StringBuilder bulkRequest = new StringBuilder();
				int i = 0;
				for (Map<String, Object> doc : docs) {
					
					String id = doc.get(idKey).toString();
					StringBuilder _doc = new StringBuilder(createESDoc(doc));
					_doc.deleteCharAt(_doc.length() - 1); 
					 _doc.append(",\"latest\":true,\"_loaddate\":\"" + loaddate + "\" }");

					if (_doc != null) {
						bulkRequest.append(String.format(actionTemplate, index, type, id));
						bulkRequest.append(_doc + "\n");
					}
					i++;
					if (i % 1000 == 0 || bulkRequest.toString().getBytes().length / (1024 * 1024) > 5) {
						LOGGER.info("Uploaded {}" + i);
						bulkUpload(endpoint, bulkRequest);
						bulkRequest = new StringBuilder();
					}
				}
				if (bulkRequest.length() > 0) {
					LOGGER.info("Uploaded {}" + i);
					bulkUpload(endpoint, bulkRequest);
				}
				 LOGGER.info("Updating status");
		            refresh(index);
		            updateLatestStatus(index, type, loaddate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(" Some thing failed during uploading the data", Util.getStackTrace(e));
		}

	}

	public static String createESDoc(Map<String, ?> doc) {
		ObjectMapper objMapper = new ObjectMapper();
		String docJson = "{}";
		try {
			docJson = objMapper.writeValueAsString(doc);
		} catch (JsonProcessingException e) {
			LOGGER.error(" Some thing got failed in createESDc", Util.getStackTrace(e));
		}
		return docJson;
	}

	/**
	 * Bulk upload.
	 *
	 * @param endpoint    the endpoint
	 * @param bulkRequest the bulk request
	 */
	private static void bulkUpload(String endpoint, StringBuilder bulkRequest) {
		try {
			Response resp = invokeAPI("POST", endpoint, bulkRequest.toString());
			String responseStr = EntityUtils.toString(resp.getEntity());
			if (responseStr.contains("\"errors\":true")) {
				LOGGER.info(responseStr);
			}
		} catch (Exception e) {
			LOGGER.error(" Some thing got failed in bulkUpload", Util.getStackTrace(e));
		}
	}

	/**
	 * Invoke API.
	 *
	 * @param method   the method
	 * @param endpoint the endpoint
	 * @param payLoad  the pay load
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
     * Update latest status.
     *
     * @param index
     *            the index
     * @param type
     *            the type
     * @param loaddate
     *            the loaddate
     */

	public static void updateLatestStatus(String index, String type, String loaddate) {
		  String updateJson = "{\"script\":{\"inline\": \"ctx._source.latest=false\"},\"query\": {\"bool\": {\"must\": [{ \"match\": {\"latest\":true}}], \"must_not\": [{\"match\": {\"_loaddate.keyword\":\""
	                + loaddate + "\"}}]}}}";
	        try {
	            invokeAPI("POST", index + "/" + type + "/" + "_update_by_query", updateJson);
	        } catch (IOException e) {
	            LOGGER.error("Error in updateLatestStatus",e);
	        }
		
	}
}
