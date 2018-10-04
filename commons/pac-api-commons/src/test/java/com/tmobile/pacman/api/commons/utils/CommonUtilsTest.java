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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.HeimdallElasticSearchRepository;

@PowerMockIgnore("org.apache.http.conn.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SSLContext.class })
public class CommonUtilsTest {

	private ForTestUtils forTest;

	@Before
    public void setUp() throws Exception{
		forTest = PowerMockito.spy(new ForTestUtils());
    }

	@Test
	public void testProductOrderProcessing() {
		assertEquals(forTest.convertAttributetoKeyword(), true);
	}

	@Test
	public void buildErrorResponse() {
		assertEquals(forTest.buildErrorResponse(), true);
	}

	@Test
	public void flatNestedLinkedHashMap() {
		assertEquals(forTest.flatNestedLinkedHashMap(), 3);
	}

	@Test
	public void filterMatchingCollectionElements() {
		assertEquals(forTest.filterMatchingCollectionElements(), false);
	}

	@Test
	public void createNoSSLContext() throws NoSuchAlgorithmException {
		final SSLContext ssl_ctx = PowerMockito.mock(SSLContext.class);
        mockStatic(SSLContext.class);
        when(SSLContext.getInstance(anyString())).thenReturn(ssl_ctx);
		assertEquals(forTest.createNoSSLContext(), null);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void createNoSSLContext1() throws NoSuchAlgorithmException {
        mockStatic(SSLContext.class);
        when(SSLContext.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);
		assertThatThrownBy(() -> forTest.createNoSSLContext()).isInstanceOf(NullPointerException.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void createNoSSLContext2() throws Exception {
		final SSLContext ssl_ctx = PowerMockito.mock(SSLContext.class);
        mockStatic(SSLContext.class);
        when(SSLContext.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);
        PowerMockito.when(ssl_ctx, "init", anyObject(), anyObject(), anyObject()).thenThrow(KeyManagementException.class);
		assertThatThrownBy(() -> forTest.createNoSSLContext()).isInstanceOf(NullPointerException.class);
	}

	@Test
    public void forTest() throws Exception{
		assertEquals(forTest.buildQuery(), null);
    }

	@SuppressWarnings("unchecked")
	@Test
    public void getFilterWithWildCard() throws Exception{
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
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		List<Map<String, Object>> result = Whitebox.invokeMethod(classUnderTest, "getFilterWithWildCard", mustFilter, mustFilter, mustFilter);
		assertEquals(result.size() > 0, true);
    }

	@SuppressWarnings("unchecked")
	@Test
    public void buildQueryForMustTermsFilter() throws Exception{
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
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		String searchText = "searchText";
		Map<String, Object> result = Whitebox.invokeMethod(classUnderTest, "buildQueryForMustTermsFilter", mustFilter, mustFilter, shouldFilter, searchText, mustFilter, mustFilter, mustFilter);
		assertEquals(result.keySet().size()>0,  true);
	}

	@Test
    public void buildAggs() throws Exception{
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		Map<String, Object> result = Whitebox.invokeMethod(classUnderTest, "buildAggs", "distributionName", 12);
		assertEquals(result.keySet().size()>0,  true);
	}

	@Test
    public void initHeimdallElasticSearchRepository() throws Exception{
		final HeimdallElasticSearchRepository classUnderTest = PowerMockito.spy(new HeimdallElasticSearchRepository());
		Whitebox.invokeMethod(classUnderTest, "init");
	}

	@Test
    public void initElasticSearchRepository() throws Exception{
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		Whitebox.invokeMethod(classUnderTest, "init");
	}
}
