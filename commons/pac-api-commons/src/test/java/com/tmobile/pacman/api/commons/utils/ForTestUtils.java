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
  Author :Nidhish
  Modified Date: June 27, 2018

**/
package com.tmobile.pacman.api.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.powermock.api.mockito.PowerMockito;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;

public class ForTestUtils {

	public boolean convertAttributetoKeyword() {
		String name = "ABC";
		String value =  CommonUtils.convertAttributetoKeyword(name);
		return value.contains("keyword");
	}

	public boolean buildErrorResponse() {
		Exception exception = new Exception();
		String value =  CommonUtils.buildErrorResponse(exception);
		return value.contains("status");
	}

	public int flatNestedLinkedHashMap() {
		String notation = ".";
		ArrayList<String> list = Lists.newArrayList();
		list.add("AVX");
		Map<String, Object> map = Maps.newHashMap();
		map.put("name", "ABC");
		Map<String, Object> nestedParentMap = Maps.newHashMap();
		nestedParentMap.put("childString", "name");
		nestedParentMap.put("childList", list);
		nestedParentMap.put("childMap", map);
		LinkedHashMap<String, Object> value =  CommonUtils.flatNestedLinkedHashMap(notation, nestedParentMap);
		CommonUtils.flatNestedMap(notation, nestedParentMap);
		return value.size();
	}

	@SuppressWarnings("unchecked")
	public boolean filterMatchingCollectionElements() {
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> response1 = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		CommonUtils.filterMatchingCollectionElements(response1, "\\", false);
		String jsonArray = "[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]";
		List<Object> response2 = (List<Object>) gson.fromJson(jsonArray, Object.class);
		Object value2 =  CommonUtils.filterMatchingCollectionElements(response2, "\\", false);
		return value2.toString().isEmpty();
	}

	public String createNoSSLContext() {
		SSLContext sslContext =  CommonUtils.createNoSSLContext();
		return sslContext.getProtocol();
	}

	public boolean buildSucessResponse() {
		ResponseEntity<Object> response = ResponseUtils.buildSucessResponse("test");
		return response.getBody().toString().isEmpty();
	}

	public boolean buildFailureResponse() {
		Exception exception = new Exception("Error");
		ResponseEntity<Object> response = ResponseUtils.buildFailureResponse(exception);
		return response.getBody().toString().isEmpty();
	}

	public boolean buildFailureResponse2() {
		Exception exception = new Exception("Error");
		ResponseEntity<Object> response = ResponseUtils.buildFailureResponse(exception, "mockData");
		return response.getBody().toString().isEmpty();
	}

	@SuppressWarnings("rawtypes")
	public boolean getAllDatesBetweenDates() throws ParseException {
		String currentDate = "01-March-2016";
	    SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
	    Date begin = f.parse(currentDate);
		List response = DateUtils.getAllDatesBetweenDates(begin, new Date(), "dd/MM/yyyy");
		return response.isEmpty();
	}

	public Object buildQuery() {
		test3();
		test2();
		return test1();
	}

	@SuppressWarnings("unchecked")
	private Object test2() {
		ElasticSearchRepository elasticSearchRepository = PowerMockito.spy(new ElasticSearchRepository());
		Map<String, Object> mustFilter = Maps.newHashMap();
		Map<String, Object> mustFilterDetails = Maps.newHashMap();
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		mustFilterDetails.put("has_child", "has_child123");
		mustFilter.put("has_child", mustFilterDetails);
		mustFilter.put("has_parent", mustFilterDetails);
		mustFilter.put("test", json);
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		shouldFilter.put("has_child", mustFilterDetails);
		String searchText = "\"searchText\"";
		Map<String, List<String>> matchPhrasePrefix = (Map<String, List<String>>) gson.fromJson("{\"count\":[\"ABC\",\"BCD\",\"OPD\"]}", Object.class);
		Map<String, Object> response = elasticSearchRepository.buildQuery(mustFilter, mustFilter, shouldFilter, searchText, mustFilter, matchPhrasePrefix);
		return response.get("name");
	}

	@SuppressWarnings("unchecked")
	private Object test3() {
		ElasticSearchRepository elasticSearchRepository = PowerMockito.spy(new ElasticSearchRepository());
		Map<String, Object> mustFilter = Maps.newHashMap();
		Map<String, Object> mustFilterDetails = Maps.newHashMap();
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		mustFilterDetails.put("has_child", "has_child123");
		mustFilter.put("has_child", mustFilterDetails);
		mustFilter.put("has_parent", mustFilterDetails);
		mustFilter.put("test", json);
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		shouldFilter.put("has_child", mustFilterDetails);
		String searchText = "searchText";
		Map<String, List<String>> matchPhrasePrefix = (Map<String, List<String>>) gson.fromJson("{\"count\":[\"ABC\",\"BCD\",\"OPD\"]}", Object.class);
		Map<String, Object> response = elasticSearchRepository.buildQuery(mustFilter, mustFilter, shouldFilter, searchText, mustFilter, matchPhrasePrefix);
		return response.get("name");
	}

	@SuppressWarnings("unchecked")
	private Object test1() {
		ElasticSearchRepository elasticSearchRepository = PowerMockito.spy(new ElasticSearchRepository());
		Map<String, Object> mustFilter = Maps.newHashMap();
		Map<String, Object> mustFilterDetails = Maps.newHashMap();
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		mustFilterDetails.put("has_child", "has_child123");
		mustFilter.put("has_child", mustFilterDetails);
		mustFilter.put("has_parent", mustFilterDetails);
		mustFilter.put("test", json);
		mustFilter.put("range", json);
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		shouldFilter.put("has_child", mustFilterDetails);
		String searchText = null;
		Map<String, List<String>> matchPhrasePrefix = (Map<String, List<String>>) gson.fromJson("{\"count\":[\"ABC\",\"BCD\",\"OPD\"]}", Object.class);
		Map<String, Object> response = elasticSearchRepository.buildQuery(mustFilter, mustFilter, shouldFilter, searchText, mustFilter, matchPhrasePrefix);
		return response.get("name");
	}

	public Object getAllDatesBetweenDates1() throws ParseException {
		String currentDate = "01-March-2016";
	    SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
	    Date begin = f.parse(currentDate);
		List response = DateUtils.getAllDatesBetweenDates(begin, null, null);
		return response.isEmpty();
	}
}
