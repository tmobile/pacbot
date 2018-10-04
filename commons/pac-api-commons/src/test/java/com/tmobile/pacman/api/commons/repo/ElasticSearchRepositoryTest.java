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
package com.tmobile.pacman.api.commons.repo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * @author Nidhish
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ByteBuffer.class, StringBuilder.class, PacHttpUtils.class, Strings.class, String.class })
public class ElasticSearchRepositoryTest implements Constants {

	@SuppressWarnings("deprecation")
	@Test
	public void getDataFromESTest() throws Exception {
		esUrl = "esUrl";
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		String response = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		final StringBuilder mock = PowerMockito.spy(new StringBuilder());
		PowerMockito.whenNew(StringBuilder.class).withAnyArguments().thenReturn(mock);
		assertThat(classUnderTest.getDataFromES(anyString(), eq("targetType"), anyObject(), anyObject(), anyObject(),
				anyObject(), anyObject()).size(), is(1));
	}

	@Test
	public void getSortedDataFromESTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		String response = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

		assertThat(classUnderTest.getSortedDataFromES(eq("dataSource"), eq("targetType"), anyObject(), anyObject(),
				anyObject(), anyObject(), anyObject(), anyObject()).size(), is(1));
	}

	@Test
	public void buildScrollRequestTest() {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		assertThat(classUnderTest.buildScrollRequest(eq("123"), eq("esPageScrollTtl")), is("{\"scroll\":\"2m\"}"));
	}

	@Test
	public void getTotalDistributionForIndexAndTypeTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		mockStatic(Strings.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"name\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getTotalDistributionForIndexAndType(eq("index"), eq("type"), anyObject(), anyObject(),
				anyObject(), anyString(), eq(1), anyObject()).get("TX"), is(27l));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTotalDistributionForIndexAndTypeTest1() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		mockStatic(Strings.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"name\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getTotalDistributionForIndexAndType(eq("index"), eq("type"), anyMap(), anyMap(),
				anyObject(), anyObject(), eq(1), anyObject()).get("TX"), is(27l));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTotalDistributionForIndexAndTypeTest2() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		mockStatic(Strings.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> classUnderTest.getTotalDistributionForIndexAndType(eq("index"), eq("type"), anyObject(), anyObject(),
				anyObject(), anyString(), eq(1), anyObject())).isInstanceOf(Exception.class);
	}

	@Test
	public void getUtilizationByAssetGroupTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(PacHttpUtils.class);
		String response = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

		assertThat(classUnderTest.getUtilizationByAssetGroup(eq("asseGroup")).size(), is(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUtilizationByAssetGroupTest2() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> classUnderTest.getUtilizationByAssetGroup(eq("asseGroup"))).isInstanceOf(Exception.class);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getDataFromESBySizeTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		String response = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"name\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

		assertThat(classUnderTest.getDataFromESBySize(eq("dataSource"), eq("targetType"), anyObject(), anyObject(),
				anyObject(), anyObject(), eq(1), eq(1), anyObject(), anyObject()).size(), is(1));

	}

	@Test
	public void getSortedDataFromESBySizeTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		String response = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"name\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

		assertThat(classUnderTest.getSortedDataFromESBySize(eq("dataSource"), eq("targetType"), anyObject(),
				anyObject(), anyObject(), anyObject(), eq(1), eq(1), anyObject(), anyObject(), anyObject()).size(),
				is(1));

	}

	@Test
	public void updatePartialDataToESTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.updatePartialDataToES(anyString(), anyString(), anyObject(), anyObject(), anyObject(),
				anyObject()), is(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updatePartialDataToESExceptionTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenThrow(Exception.class);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.updatePartialDataToES(anyString(), anyString(), anyObject(), anyObject(), anyObject(),
				anyObject()), is(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void saveExceptionDataToESTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.saveExceptionDataToES(eq("datasource"), anyString(), anyMap()), is(false));
	}

	@Test
	public void getTotalDistributionForIndexAndTypeBySizeTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"name\":{\"buckets\":[{\"key\":\"ID\",\"doc_count\":27},{\"key\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getTotalDistributionForIndexAndTypeBySize(eq("index"), eq("type"), anyObject(), anyObject(),
				anyObject(), anyObject(), eq(1), eq(1), anyString()).get("TX"), is(27l));
	}


	@SuppressWarnings("unchecked")
	@Test
	public void getTotalDistributionForIndexAndTypeBySizeTest2() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		   assertThatThrownBy(() -> classUnderTest.getTotalDistributionForIndexAndTypeBySize(eq("index"), eq("type"), anyObject(), anyObject(),
					anyObject(), anyObject(), eq(1), eq(1), anyString())).isInstanceOf(Exception.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getDateHistogramForIndexAndTypeByIntervalTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"name\":{\"buckets\":[{\"key_as_string\":\"ID\",\"doc_count\":27},{\"key_as_string\":\"TX\",\"doc_count\":27}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getDateHistogramForIndexAndTypeByInterval(eq("index"), eq("type"), anyMap(), anyMap(),
				anyObject(), anyString(), anyString()).get("TX"), is(27l));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getDateHistogramForIndexAndTypeByIntervalTest2() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> classUnderTest.getDateHistogramForIndexAndTypeByInterval(eq("index"), eq("type"), anyMap(), anyMap(),
					anyObject(), anyString(), anyString())).isInstanceOf(Exception.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAccountsByMultiAggsTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"accountid\":{\"buckets\":[{\"accountname\":{\"buckets\" :[{\"key\":\"key678\"}]},\"key\":\"key123\"},{\"accountname\":{\"buckets\" :[{\"key\":\"key566\"}]},\"key\":\"key455\"}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getAccountsByMultiAggs(eq("index"), eq("type"), anyMap(), anyMap(),
				anyObject(), anyMap(), eq(1)).get("key455"), is("key566"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAccountsByMultiAggsTest2() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> classUnderTest.getAccountsByMultiAggs(eq("index"), eq("type"), anyMap(), anyMap(),
				anyObject(), anyMap(), eq(1)).get("key455")).isInstanceOf(Exception.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getDetailsFromESBySizeTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"result\":\"updated\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"accountid\":{\"buckets\":[{\"accountname\":{\"buckets\" :[{\"key\":\"key678\"}]},\"key\":\"key123\"},{\"accountname\":{\"buckets\" :[{\"key\":\"key566\"}]},\"key\":\"key455\"}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getDetailsFromESBySize(eq("dataSource"), eq("targetType"), anyMap(), anyMap(),
				anyObject(), anyList(), eq(1), eq(1), anyString(), anyMap()).size(), is(1));
	}


	@Test
	@SuppressWarnings("unchecked")
	public void getDetailsFromESBySizeTest1() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> classUnderTest.getDetailsFromESBySize(eq("dataSource"), eq("targetType"), anyMap(), anyMap(),
				anyObject(), anyList(), eq(1), eq(1), anyString(), anyMap())).isInstanceOf(Exception.class);
	}

	@Test
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void getDataFromESWithMustNotTermsFilterTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"count\":\"1234\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"accountid\":{\"buckets\":[{\"accountname\":{\"buckets\" :[{\"key\":\"key678\"}]},\"key\":\"key123\"},{\"accountname\":{\"buckets\" :[{\"key\":\"key566\"}]},\"key\":\"key455\"}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getDataFromESWithMustNotTermsFilter(eq("dataSource"), eq("targetType"), anyMap(), anyMap(),
				anyObject(), anyList(), anyMap(), anyMap(), anyMap()).size(), is(1));
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void getSortedDataFromESWithMustNotTermsFilterTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"count\":\"1234\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"accountid\":{\"buckets\":[{\"accountname\":{\"buckets\" :[{\"key\":\"key678\"}]},\"key\":\"key123\"},{\"accountname\":{\"buckets\" :[{\"key\":\"key566\"}]},\"key\":\"key455\"}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getSortedDataFromESWithMustNotTermsFilter(eq("dataSource"), eq("targetType"), anyMap(), anyMap(),
				anyObject(), anyList(), anyMap(), anyMap(), anyMap(), anyList()).size(), is(1));
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void getTotalDistributionForIndexAndTypeWithMatchPhraseTest() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		String response = "{\"count\":\"1234\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"accountid\":{\"buckets\":[{\"accountname\":{\"buckets\" :[{\"key\":\"key678\"}]},\"key\":\"key123\"},{\"accountname\":{\"buckets\" :[{\"key\":\"key566\"}]},\"key\":\"key455\"}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		assertThat(classUnderTest.getTotalDistributionForIndexAndTypeWithMatchPhrase(eq("index"), eq("type"), anyMap(), anyMap(),
				anyObject(), anyString(), anyMap(), anyMap()), is(1234L));
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void getTotalDistributionForIndexAndTypeWithMatchPhraseTest1() throws Exception {
		final ElasticSearchRepository classUnderTest = PowerMockito.spy(new ElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(Strings.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> classUnderTest.getTotalDistributionForIndexAndTypeWithMatchPhrase(eq("index"), eq("type"), anyMap(), anyMap(),
				anyObject(), anyString(), anyMap(), anyMap())).isInstanceOf(Exception.class);
	}

	private String esHost = "elastic-search.host";

	private int esPort = 9090;

	final static String protocol = "http";

	@SuppressWarnings("unused")
	private String esUrl = protocol + "://" + esHost + ":" + esPort;
}
