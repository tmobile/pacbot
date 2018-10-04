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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar
  Modified Date: Jul 26, 2017

**/
package com.tmobile.pacman.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;

// TODO: Auto-generated Javadoc
/**
 * The Class ESUtils.
 */
public class ESUtils {



	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ESUtils.class);

	/**
	 * Post annotation.
	 *
	 * @param indexName the index name
	 * @param typeName the type name
	 * @param docId the doc id
	 * @param annotation the annotation
	 * @return true, if successful
	 */
	public static boolean postAnnotation(String indexName, String typeName, String docId, Annotation annotation) {
		//POST data to ES using https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html
		return false;
	}

	/**
	 * Gets the total document count for index and type.
	 *
	 * @param url the url
	 * @param index name
	 * @param type name
	 * @param filter the filter
	 * @return elastic search count
	 */
	@SuppressWarnings("unchecked")
	public static long getTotalDocumentCountForIndexAndType(String url, String index, String type, Map<String, String> filter) {
		String urlToQuery = new StringBuilder(url).append("/").append(index).append("/").append(type).append("/").append("_count").toString();
		Map<String, Object> requestBody = new HashMap<String, Object>();
		Map<String, Object> matchFilters = Maps.newHashMap();
		if (filter != null) {
			matchFilters.put("match_all", filter);
		} else {
			matchFilters.put("match_all", new HashMap<String, String>());
		}
		requestBody.put("query", matchFilters);
		String responseDetails = null;
		try {
			responseDetails = CommonUtils.doHttpPost(urlToQuery,serialize(requestBody));
			Map<String, Object> response = (Map<String, Object>) deSerialize(responseDetails);
			return (long) (Double.parseDouble(response.get("count").toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Checks if is valid index.
	 *
	 * @param url the url
	 * @param index the index
	 * @return true, if is valid index
	 */
	public static boolean isValidIndex(final String url, final String index) {
		String esUrl = new StringBuilder(url).append("/").append(index).toString();
		return CommonUtils.isValidResource(esUrl);
	}

	/**
	 * Checks if is valid type.
	 *
	 * @param url the url
	 * @param index the index
	 * @param type the type
	 * @return true, if is valid type
	 */
	public static boolean isValidType(final String url, final String index, final String type) {
		String esUrl = new StringBuilder(url).append("/").append(index).append("/").append("_mapping").append("/").append(type).toString();
		return CommonUtils.isValidResource(esUrl);
	}

	/**
	 * Gets the es url.
	 *
	 * @return the es url
	 */
	public static String getEsUrl() {
		return CommonUtils.getEnvVariableValue(PacmanSdkConstants.ES_URI_ENV_VAR_NAME);
	}

	/**
	 * Creates the mapping.
	 *
	 * @param esUrl the es url
	 * @param index the index
	 * @param type the type
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String createMapping(String esUrl, String index, String type) throws Exception {
		String url = new StringBuilder(esUrl).append("/").append(index).append("/").append("_mapping").append("/").append(type).toString();
		String requestBody = "  {\"properties\": {\"text\": {\"type\": \"text\",\"analyzer\": \"whitespace\",\"search_analyzer\": \"whitespace\"}}}";
		//String requestBody = "{\"mappings\":{\"input_type\":{\"dynamic_templates\":[{\"notanalyzed\":{\"match\":\"*\",\"match_mapping_type\":\"string\",\"mapping\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}]}}}";
		requestBody = requestBody.replace("input_type", type);
		return CommonUtils.doHttpPut(url, requestBody);
	}

	/**
	 * Creates the mapping with parent.
	 *
	 * @param esUrl the es url
	 * @param index the index
	 * @param type the type
	 * @param parentType the parent type
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String createMappingWithParent(String esUrl, String index, String type,String parentType) throws Exception {
		String url = new StringBuilder(esUrl).append("/").append(index).append("/").append("_mapping").append("/").append(type).toString();
		String requestBody = "  {\"_parent\": { \"type\": \""+parentType+"\"}}";
		//String requestBody = "{\"mappings\":{\"input_type\":{\"dynamic_templates\":[{\"notanalyzed\":{\"match\":\"*\",\"match_mapping_type\":\"string\",\"mapping\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}]}}}";
		return CommonUtils.doHttpPut(url, requestBody);
	}


	/**
	 * Creates the index.
	 *
	 * @param url the url
	 * @param indexName the index name
	 * @throws Exception the exception
	 */
	public static void createIndex(String url, String indexName) throws Exception {
		String esUrl = new StringBuilder(url).append("/").append(indexName).toString();
		CommonUtils.doHttpPut(esUrl, null);
	}

	/**
	 * Ensure index and type for annotation.
	 *
	 * @param annotation the annotation
	 * @param createIndexIfNotFound the create index if not found
	 * @throws Exception the exception
	 */
	public static void ensureIndexAndTypeForAnnotation(Annotation annotation,Boolean createIndexIfNotFound) throws Exception {
		String esUrl = ESUtils.getEsUrl();
		String indexName = buildIndexNameFromAnnotation(annotation);

		if(!Strings.isNullOrEmpty(indexName)){
			indexName = indexName.toLowerCase();
		}else throw new Exception("Index/datasource/pac_ds name cannot be null or blank");

		if(!isValidIndex(esUrl, indexName)) {
			//createIndex(esUrl, indexName);
			//DO NOT CREATE INDEX, this responsibility is delegated to pacman cloud discovery,  if you will create the index, parent , child relation will be lost
			throw new Exception("Index is not yet ready to publish the data");
		}

		String parentType,type;
		if(!Strings.isNullOrEmpty(annotation.get(PacmanSdkConstants.TARGET_TYPE)) && !Strings.isNullOrEmpty(annotation.get(PacmanSdkConstants.TYPE))){
			parentType = annotation.get(PacmanSdkConstants.TARGET_TYPE);
			type = getIssueTypeFromAnnotation(annotation);
		}else throw new Exception("targetType name cannot be null or blank");

		if(!isValidType(esUrl, indexName, type)) {
			//createMappingWithParent(esUrl, indexName, type,parentType);do not create now, this responsibility is delegated to Inventory collector
			throw new Exception("Index exists but unable to find type to publish the data");
		}

	}

	/**
	 * Builds the index name from annotation.
	 *
	 * @param annotation the annotation
	 * @return the string
	 */
	public static String buildIndexNameFromAnnotation(final Annotation annotation) {
		return annotation.get(PacmanSdkConstants.DATA_SOURCE_KEY) + "_" + annotation.get(PacmanSdkConstants.TARGET_TYPE);
	}

	/**
	 * Gets the issue type from annotation.
	 *
	 * @param annotation the annotation
	 * @return the issue type from annotation
	 */
	public static String getIssueTypeFromAnnotation(Annotation annotation){
		return new StringBuilder(annotation.get(PacmanSdkConstants.TYPE)).append("_").append(annotation.get(PacmanSdkConstants.TARGET_TYPE)).toString();
	}

	/**
	 * Gets the data from ES.
	 *
	 * @param url the url
	 * @param dataSource the data source
	 * @param entityType the entity type
	 * @param filter the filter
	 * @param fields the fields
	 * @param from size
	 * @param size the size
	 * @return String
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getDataFromES(final String url, String dataSource, String entityType, Map<String,String> filter, List<String> fields, long from, long size) throws Exception {

		//if filter is not null apply filter, this can be a multi value filter
		//also if from and size are -1 -1 send all the data back and do not paginate
		String urlToQuery = new StringBuilder(url).append("/").append(dataSource).append("/").append(entityType).append("/").append("_search").append("?scroll=").append(PacmanSdkConstants.ES_PAGE_SCROLL_TTL).toString();
		String urlToScroll = new StringBuilder(url).append("/").append("_search").append("/scroll").toString();
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		//paginate for breaking the response into smaller chunks
		Map<String, Object> requestBody = new HashMap<String, Object>();
		//requestBody.put("from", from);
		requestBody.put("size", PacmanSdkConstants.ES_PAGE_SIZE);
		requestBody.put("query", CommonUtils.buildQuery(filter));
		requestBody.put("_source", fields);
		Gson serializer = new GsonBuilder().create();
		String request = serializer.toJson(requestBody);
		String _scroll_id=null;
		for(int index=0;index<=(size/PacmanSdkConstants.ES_PAGE_SIZE);index++){
			String responseDetails=null;
			try{
					if(!Strings.isNullOrEmpty(_scroll_id)){
						request=buildScrollRequest(_scroll_id,PacmanSdkConstants.ES_PAGE_SCROLL_TTL);
						urlToQuery = urlToScroll;
					}
					responseDetails = CommonUtils.doHttpPost(urlToQuery,request );
					_scroll_id = processResponseAndSendTheScrollBack(responseDetails, results);
			}catch (Exception e) {
				CommonUtils.logger.error("error retrieving inventory from ES",e);
				throw e;
			}

		}
		return results;
	}

	/**
	 * Builds the scroll request.
	 *
	 * @param _scroll_id the scroll id
	 * @param esPageScrollTtl the es page scroll ttl
	 * @return the string
	 */
	private static String buildScrollRequest(String _scroll_id, String esPageScrollTtl) {
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("scroll", PacmanSdkConstants.ES_PAGE_SCROLL_TTL);
		requestBody.put("scroll_id", _scroll_id);
		Gson serializer = new GsonBuilder().create();
		return serializer.toJson(requestBody);
	}

	/**
	 * Process response and send the scroll back.
	 *
	 * @param responseDetails the response details
	 * @param results the results
	 * @return the string
	 */
	private static String processResponseAndSendTheScrollBack(String responseDetails,List<Map<String, String>> results){
		Gson serializer = new GsonBuilder().create();
		Map<String,Object> response = (Map<String,Object>)serializer.fromJson(responseDetails,Object.class);
		if(response.containsKey("hits")){
			Map<String, Object> hits = (Map<String, Object>) response.get("hits");
			if(hits.containsKey("hits")) {
				List<Map<String, Object>> hitDetails = (List<Map<String, Object>>) hits.get("hits");
				for (Map<String, Object> hitDetail : hitDetails) {
					Map<String, Object> sources = (Map<String, Object>) hitDetail.get("_source");
					sources.put(PacmanSdkConstants.ES_DOC_ID_KEY,hitDetail.get(PacmanSdkConstants.ES_DOC_ID_KEY));
					sources.put(PacmanSdkConstants.ES_DOC_PARENT_KEY,hitDetail.get(PacmanSdkConstants.ES_DOC_PARENT_KEY));
					sources.put(PacmanSdkConstants.ES_DOC_ROUTING_KEY,hitDetail.get(PacmanSdkConstants.ES_DOC_ROUTING_KEY));
					results.add(CommonUtils.flatNestedMap(null, sources));
				}
			}
		}
		return (String)response.get("_scroll_id");
	}

	/**
	 * Convert attributeto keyword.
	 *
	 * @param attributeName the attribute name
	 * @return the string
	 */
	public static String convertAttributetoKeyword(String attributeName){
		return attributeName + ".keyword";
	}


	/**
	 * Publish metrics.
	 *
	 * @param evalResults the eval results
	 * @return the boolean
	 */
	public static Boolean publishMetrics(Map<String, String> evalResults) {
		logger.info(Joiner.on("#").withKeyValueSeparator("=").join(evalResults));
		String indexName = "fre-stats";
		String type = "execution-stats";
		String url = ESUtils.getEsUrl();
		try {
				if(!ESUtils.isValidIndex(url, indexName)){
					ESUtils.createIndex(url, indexName);
				}
				if(!ESUtils.isValidType(url, indexName, type)){
					ESUtils.createMapping(url, indexName, type);
				}
				String esUrl = new StringBuilder(url).append("/").append(indexName).append("/").append(type).append("/").append(evalResults.get(PacmanSdkConstants.EXECUTION_ID)).toString();
				Gson serializer = new GsonBuilder().create();
						CommonUtils.doHttpPost(esUrl, serializer.toJson(evalResults));
		} catch (Exception e) {
			logger.error("unable to publish execution stats");
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}



	/**
     * De serialize.
     *
     * @param responseValue the response value
     * @return the object
     */
    private  static Object deSerialize(String responseValue) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(responseValue, Object.class);
    }

    /**
     * Serialize.
     *
     * @param responseValue the response value
     * @return the string
     */
    private static String serialize(Object responseValue) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(responseValue, Object.class);
    }

}
