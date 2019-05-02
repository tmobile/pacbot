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
package com.tmobile.pacman.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.config.ConfigManager;

// TODO: Auto-generated Javadoc
/**
 * The Class AuditUtilsTest.
 */
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({CommonUtils.class, ESUtils.class,ConfigManager.class})
public class AuditUtilsTest {

	/**
     * Setup.
     */
    @Before
    public void setup(){
        
        mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }

	
	
	/**
	 * Post audit trail.
	 *
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 *//*
	@Test
	public void postAuditTrail() throws UnsupportedEncodingException {
		List<Annotation> annotations = Lists.newArrayList();
		Annotation annotation = new Annotation();
        annotation.put(PacmanSdkConstants.RULE_ID, "ruleId123");
        annotation.put(PacmanSdkConstants.DOC_ID, "docId123");
		annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "sKey123");
		annotation.put(PacmanSdkConstants.TARGET_TYPE, "target123");
		annotations.add(annotation);
		String status = "testStatus";
		AuditUtils.postAuditTrail(annotations, status);
		assertTrue(true);
	}*/

	/**
	 * Gets the resources from es test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getResourcesFromEsTest() throws Exception {
		//PowerMockito.mockStatic(CommonUtils.class);
		//PowerMockito.mockStatic(ESUtils.class);
		PowerMockito.mockStatic(ESUtils.class);
		CloseableHttpResponse mockResponse = PowerMockito.mock(CloseableHttpResponse.class);
		String responseBody = "{\"count\":\"123\", \"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"12\",\"lastname\":\"12\",\"age\":29,\"gender\":\"F\",\"address\":\"123\",\"employer\":\"123\",\"email\":\"123\",\"city\":\"123\",\"state\":\"CO\"}}]}}";
		//PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString())).thenReturn(responseBody);
		PowerMockito.when(ESUtils.getEsUrl()).thenReturn("Test");
		PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString(),anyMap())).thenReturn(responseBody);
        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString())).thenReturn(responseBody);


    	String index = "index";
		String targetType = "targetType";
		Map<String, String> filter = Maps.newHashMap();
		filter.put("testKey123", "testValue123");
		List<String> fields = Lists.newArrayList();
		fields.add("field123");
		fields.add("field234");
		fields.add("field345");
		List<Map<String, String>> response = ESUtils.getResourcesFromEs(index, targetType, filter, fields);
		assertEquals(response.size()>0, false);
	}
}
