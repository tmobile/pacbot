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
  Modified Date: Jul 16, 2018

**/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.dto.ExemptedResource;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonUtilsTest.
 *
 * @author kkumar
 */
@PowerMockIgnore("org.apache.http.conn.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigManager.class,SSLContext.class, HttpClientBuilder.class, EntityUtils.class, HttpClient.class, HttpResponse.class, CloseableHttpResponse.class, CloseableHttpClient.class, StatusLine.class})
public class CommonUtilsTest {

/*	@Mock
	private HttpResponse response;

	@Mock
	private StatusLine sl;*/

	/** The http client. */
private CloseableHttpClient httpClient;

	/** The http response. */
	private CloseableHttpResponse httpResponse;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
    public void setUp() throws Exception{
		mockStatic(ConfigManager.class);
		mockStatic(HttpClientBuilder.class);
		mockStatic(HttpClient.class);
		mockStatic(CloseableHttpClient.class);
		mockStatic(HttpResponse.class);
		mockStatic(CloseableHttpResponse.class);
		httpClient = PowerMockito.mock(CloseableHttpClient.class);
		HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
		PowerMockito.when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
		PowerMockito.when(HttpClientBuilder.create().build()).thenReturn(httpClient);

		HttpGet httpGet = PowerMockito.mock(HttpGet.class);
		HttpPost httpPost = PowerMockito.mock(HttpPost.class);
		HttpHead httpHead = PowerMockito.mock(HttpHead.class);
		HttpPut httpPut = PowerMockito.mock(HttpPut.class);
		PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(httpGet);
		PowerMockito.whenNew(HttpHead.class).withAnyArguments().thenReturn(httpHead);
		PowerMockito.whenNew(HttpPost.class).withAnyArguments().thenReturn(httpPost);
    	PowerMockito.whenNew(HttpPut.class).withAnyArguments().thenReturn(httpPut);

    	httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
    	HttpEntity entity = PowerMockito.mock(HttpEntity.class);
    	InputStream input = new ByteArrayInputStream("success".getBytes() );
    	PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
    	PowerMockito.when(entity.getContent()).thenReturn(input);
    	PowerMockito.when(httpResponse.getEntity()).thenReturn(entity);
    	
    	
    	ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }





	/**
	 * Post audit trail.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void postAuditTrail() throws Exception {
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"qwe\",\"age\":29,\"gender\":\"F\",\"address\":\"2133\",\"employer\":\"12\",\"email\":\"bradshawqwe@123.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		Map<String, Object> mustFilter = Maps.newHashMap();
		mustFilter.put("test", json);
		mustFilter.put("range", json);
		Object response = CommonUtils.buildQueryForExistingIssues(mustFilter);
		assertNotNull(response);
	}

	/**
	 * Flat nested map.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void flatNestedMap() throws Exception {
		Gson gson = new Gson();
		String jsonObject = "{\"ruleUUID\":\"qqqq123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"qwe\",\"age\":29,\"gender\":\"F\",\"address\":\"2133\",\"employer\":\"123\",\"email\":\"bradshawqwe@123.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		Map<String, String> response = CommonUtils.flatNestedMap(".", json);
		assertNotNull(response);
	}

	/**
	 * Gets the unique annotation id.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getUniqueAnnotationId() throws Exception {
		Annotation annotation = new Annotation();
        annotation.put(PacmanSdkConstants.RULE_ID, "ruleId123");
        annotation.put(PacmanSdkConstants.DOC_ID, "docId123");
		annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "sKey123");
		annotation.put(PacmanSdkConstants.TARGET_TYPE, "target123");
		String response = CommonUtils.getUniqueAnnotationId(annotation);
		assertNotNull(response);
	}

	/**
	 * Hash.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void hash() throws Exception {
		String response = CommonUtils.hash("test");
		assertNotNull(response);
	}






	/**
	 * Gets the index name from rule param.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getIndexNameFromRuleParam() throws Exception {
		Map<String, String> ruleParam = Maps.newHashMap();
		ruleParam.put(PacmanSdkConstants.DATA_SOURCE_KEY, "DATA_SOURCE_KEY");
		ruleParam.put(PacmanSdkConstants.TARGET_TYPE, "TARGET_TYPE");
		String response = CommonUtils.getIndexNameFromRuleParam(ruleParam);
		assertEquals(response, "DATA_SOURCE_KEY_TARGET_TYPE");
		ruleParam.put(PacmanSdkConstants.ASSET_GROUP_KEY, "ASSET_GROUP_KEY");
		response = CommonUtils.getIndexNameFromRuleParam(ruleParam);
		assertEquals(response, "ASSET_GROUP_KEY");
	}

	/**
	 * Inits the heimdall elastic search repository.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void initHeimdallElasticSearchRepository() throws Exception{
		String jsonObject = "{\"ruleUUID\": \"asss1111\", \"name\": [{\"key\": \"asss1111\", \"value\": \"manu\", \"encrypt\": true}, {\"key\": \"asss1111\", \"value\": \"manu\", \"encrypt\": false}]}";
		final CommonUtils classUnderTest = PowerMockito.spy(new CommonUtils());
		Whitebox.invokeMethod(classUnderTest, "buildMapFromJson", jsonObject);
	}

	/**
	 * Gets the current date string with format.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getCurrentDateStringWithFormat() throws Exception {
		String response = CommonUtils.getCurrentDateStringWithFormat(DateTimeZone.UTC.toString(), "dd/MM/yyyy");
		assertNotNull(response);
		response = CommonUtils.getCurrentDateStringWithFormat(null, "dd/MM/yyyy");
		assertNotNull(response);
	}

	/**
	 * Gets the date from string.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getDateFromString() throws Exception {
		Date response = CommonUtils.getDateFromString("31/05/1988", "dd/MM/yyyy");
		assertNotNull(response);
		response = CommonUtils.getDateFromString("31/05/1988", null);
		assertNotNull(response);
	}

	/**
	 * Date format.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void dateFormat() throws Exception {
		Date response = CommonUtils.dateFormat("31/05/1988", null, "MM/dd/yyyy");
		assertNotNull(response);
		response = CommonUtils.dateFormat("31/05/1988", "MM/dd/yyyy", null);
		assertNotNull(response);
	}

	/**
	 * Compare date.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void compareDate() throws Exception {
		int response = CommonUtils.compareDate(new Date(), CommonUtils.dateFormat("31/05/1988", null, "MM/dd/yyyy"));
		assertEquals(response, 1);
	}

	/**
	 * Resource created before cutoff data.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void resourceCreatedBeforeCutoffData() throws Exception {
		boolean response = CommonUtils.resourceCreatedBeforeCutoffData(CommonUtils.dateFormat("31/05/1988", null, "MM/dd/yyyy"));
		assertEquals(response, false);
		/*response = CommonUtils.resourceCreatedBeforeCutoffData(new Date());
		assertEquals(response, true);*/
	}

	/**
	 * Gets the json string.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getJsonString() throws Exception {
		List<String> names = Lists.newArrayList();
		names.add("ABCD");
		String response = CommonUtils.getJsonString(names);
		assertEquals(response.contains("ABCD"), true);
	}

	/**
	 * Encrypt B 64.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void encryptB64() throws Exception {
		String response = CommonUtils.encryptB64("ABCD");
		assertNotNull(response);
	}

	/**
	 * Decrypt B 64.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void decryptB64() throws Exception {
		String response = CommonUtils.decryptB64(CommonUtils.encryptB64("ABCD"));
		assertNotNull(response);
	}

	/**
	 * Serialize to string.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void serializeToString() throws Exception {
		String response = CommonUtils.serializeToString("ABCDADASDASD");
		assertEquals(CommonUtils.deSerializeToObject(response), "ABCDADASDASD");
	}

	/**
	 * Gets the filter.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getFilter() throws Exception {
		Map<String, Object> mustFilterDetails = Maps.newHashMap();
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"qwe\",\"age\":29,\"gender\":\"F\",\"address\":\"2133\",\"employer\":\"123\",\"email\":\"bradshawqwe@123.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		mustFilterDetails.put("has_child", "has_child123");
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		shouldFilter.put("has_child", mustFilterDetails);
		final CommonUtils classUnderTest = PowerMockito.spy(new CommonUtils());
		Whitebox.invokeMethod(classUnderTest, "getFilter", shouldFilter);
	}

	/**
	 * Builds the query.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void buildQuery() throws Exception {
		Map<String, Object> mustFilterDetails = Maps.newHashMap();
		Gson gson = new Gson();
		String jsonObject = "{\"count\":\"123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"qwe\",\"age\":29,\"gender\":\"F\",\"address\":\"2133\",\"employer\":\"123\",\"email\":\"tt@123.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		Map<String, Object> json = (Map<String, Object>) gson.fromJson(jsonObject, Object.class);
		mustFilterDetails.put("has_child", "has_child123");
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		shouldFilter.put("has_child", mustFilterDetails);
		final CommonUtils classUnderTest = PowerMockito.spy(new CommonUtils());
		Whitebox.invokeMethod(classUnderTest, "buildQuery", json, json, shouldFilter);
	}

	/**
	 * Builds the query 1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void buildQuery1() throws Exception {
		Map<String, Object> json = Maps.newHashMap();
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		final CommonUtils classUnderTest = PowerMockito.spy(new CommonUtils());
		Whitebox.invokeMethod(classUnderTest, "buildQuery", json, json, shouldFilter);
	}

	/**
	 * Creates the param map.
	 *
	 * @throws Exception the exception
	 *//*
	@Test
	public void createParamMap() throws Exception {
		Map<String, String> response = CommonUtils.createParamMap("test=122*name=908");
		assertNotNull(response);
	}*/

    /**
     * Test is env variable exists.
     */
    @Test
    public void testIsEnvVariableExists(){
        assertFalse(CommonUtils.isEnvVariableExists("TEST"));
    }

    /**
     * Do http put test.
     *
     * @throws Exception the exception
     */
    @Test
    public void doHttpPutTest() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPut) any())).thenReturn(httpResponse);
    	assertNotNull(CommonUtils.doHttpPut("url", "{}"));
    }

	/**
	 * Do http put exception test.
	 *
	 * @throws Exception the exception
	 *//*
	@Test
    public void doHttpPutExceptionTest() throws Exception{
    	PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_FORBIDDEN, "NOT FINE!"));
    	PowerMockito.when(httpClient.execute((HttpPut) any())).thenReturn(httpResponse);
    	assertThatThrownBy(() -> CommonUtils.doHttpPut("url", null)).isInstanceOf(Exception.class);
    }*/

	/**
	 * Do http put exception test 2.
	 *
	 * @throws Exception the exception
	 *//*
	@SuppressWarnings("unchecked")
	@Test
    public void doHttpPutExceptionTest2() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPut) any())).thenThrow(IOException.class);
    	assertNull(CommonUtils.doHttpPut("url", null));
    }*/

	/**
	 * Checks if is valid resource test.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void isValidResourceTest() throws Exception{
		PowerMockito.when(httpClient.execute((HttpHead) any())).thenReturn(httpResponse);
    	assertTrue(CommonUtils.isValidResource("url"));
    }

	/**
	 * Checks if is valid resource test 2.
	 *
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
    public void isValidResourceTest2() throws ClientProtocolException, IOException {
		PowerMockito.when(httpClient.execute((HttpHead) any())).thenThrow(ClientProtocolException.class);
    	assertFalse(CommonUtils.isValidResource("url"));
    }

	/**
	 * Checks if is valid resource test 3.
	 *
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
    public void isValidResourceTest3() throws ClientProtocolException, IOException {
		PowerMockito.when(httpClient.execute((HttpHead) any())).thenThrow(IOException.class);
    	assertFalse(CommonUtils.isValidResource("url"));
    }

	/**
	 * Checks if is valid resource test 4.
	 *
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
    public void isValidResourceTest4() throws ClientProtocolException, IOException {
		PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_FORBIDDEN, "NOT FINE!"));
		PowerMockito.when(httpClient.execute((HttpHead) any())).thenReturn(httpResponse);
    	assertFalse(CommonUtils.isValidResource("https://sample.com"));
    }

	/**
	 * Do http post test.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void doHttpPostTest() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPost) any())).thenReturn(httpResponse);
    	assertNotNull(CommonUtils.doHttpPost("http://sample.com", "{}"));
    }

	/**
	 * Do http post exception test.
	 *
	 * @throws Exception the exception
	 *//*
	@Test
    public void doHttpPostExceptionTest() throws Exception{
    	PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_FORBIDDEN, "NOT FINE!"));
    	PowerMockito.when(httpClient.execute((HttpPost) any())).thenReturn(httpResponse);
    	assertThatThrownBy(() -> CommonUtils.doHttpPost("http://sample.com", null)).isInstanceOf(Exception.class);
    }*/

	/**
	 * Do http post test 2.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void doHttpPostTest2() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPost) any())).thenReturn(httpResponse);
        final Map<String, String> headers = Maps.newHashMap();
        headers.put("key1", "value1");
    	assertNotNull(CommonUtils.doHttpPost("http://sample.com", "{}", headers));
    }

	/**
	 * Do http post test 3.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void doHttpPostTest3() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPost) any())).thenReturn(httpResponse);
        final Map<String, String> headers = Maps.newHashMap();
        headers.put("key1", "value1");
    	assertNotNull(CommonUtils.doHttpPost(null, "{}", headers));
    }

	/**
	 * Do http post exception test 2.
	 *
	 * @throws Exception the exception
	 *//*
	@SuppressWarnings("unchecked")
	@Test
    public void doHttpPostExceptionTest2() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPost) any())).thenThrow(IOException.class);
        final Map<String, String> headers = Maps.newHashMap();
        headers.put("key1", "value1");
    	assertNull(CommonUtils.doHttpPost("http://sample.com", "{}", headers));
    }*/


	/**
	 * Do http get test.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void doHttpGetTest() throws Exception{
        PowerMockito.when(httpClient.execute((HttpGet) any())).thenReturn(httpResponse);
        assertNotNull(CommonUtils.doHttpGet("http://sample.com"));
    }

	/**
	 * Do http get exception test.
	 *
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
    public void doHttpGetExceptionTest() throws ClientProtocolException, IOException {
		PowerMockito.when(httpClient.execute((HttpGet) any())).thenThrow(Exception.class);
    	assertNull(CommonUtils.doHttpGet("http://sample.com"));
    }

	/**
	 * Gets the exempted resource.
	 *
	 */
	@Test
	public void getExemptedResource() {
		Map<String, String> resourceAttributes = Maps.newHashMap();
		ExemptedResource exemptedResource2 = new ExemptedResource("resourceId", "exemptionExpiryDate", "exemptionReason");
		ExemptedResource exemptedResource3 = new ExemptedResource(resourceAttributes);
		exemptedResource3.setExemptionExpiryDate("exemptionExpiryDate");
		exemptedResource3.setExemptionReason("exemptionReason");
		exemptedResource3.setResourceId("resourceId");
		assertFalse(exemptedResource2.equals(exemptedResource3));
		assertEquals(exemptedResource3.getExemptionExpiryDate(), "exemptionExpiryDate");
		assertEquals(exemptedResource3.getExemptionReason(), "exemptionReason");
		assertEquals(exemptedResource3.getResourceId(), "resourceId");
	}



	/*@Test
    public void doHttpPostExceptionTest2() throws Exception{
    	PowerMockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_FORBIDDEN, "NOT FINE!"));
    	PowerMockito.when(httpClient.execute((HttpPost) any())).thenReturn(httpResponse);
    	final Map<String, String> headers = Maps.newHashMap();
        headers.put("key1", "value1");
    	assertThatThrownBy(() -> CommonUtils.doHttpPost("http://sample.com", null, headers)).isInstanceOf(Exception.class);
    }*/

	/*@SuppressWarnings("unchecked")
	@Test
    public void doHttpPostExceptionTest2() throws Exception{
        PowerMockito.when(httpClient.execute((HttpPost) any())).thenThrow(IOException.class);
    	assertNull(CommonUtils.doHttpPost("https://sample.com", null));
    }*/
   /*
    @Test(expected=Exception.class)
    public void testDoHttpPost() throws Exception{

        CommonUtils.doHttpPost("http://www.google.com", "");

    }

    @Test(expected=Exception.class)
    public void testDoHttpPut() throws Exception{

        CommonUtils.doHttpPut("http://www.google.com", "");

    }*/




}
