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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyMap;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.config.ConfigManager;

// TODO: Auto-generated Javadoc
/**
 * The Class ESUtilsTest.
 */
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({CommonUtils.class, StringBuilder.class, Strings.class,ConfigManager.class})
public class ESUtilsTest {
    
    
	/** The Constant DUMMY_ES_HOST. */
    private static final String DUMMY_ES_HOST = "http://localhost";

    /**
     * Setup.
     */
    @Before
    public void setup(){
    	mockStatic(ConfigManager.class);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getEnvVariableValue(anyString())).thenReturn(DUMMY_ES_HOST);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }
    
    
    /**
     * Post audit trail.
     *
     * @throws ParseException the parse exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
	public void postAuditTrail() throws ParseException, UnsupportedEncodingException {
		List<Annotation> annotations = Lists.newArrayList();
		Annotation annotation = new Annotation();
        annotation.put(PacmanSdkConstants.RULE_ID, "ruleId123");
        annotation.put(PacmanSdkConstants.DOC_ID, "docId123");
		annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "sKey123");
		annotation.put(PacmanSdkConstants.TARGET_TYPE, "target123");
		annotations.add(annotation);
		assertNotNull(ESUtils.buildIndexNameFromAnnotation(annotation));
	}
    
    /**
     * Publish metrics test 2.
     *
     * @throws Exception the exception
     */
    @Test
  	public void publishMetricsTest2() throws Exception {
    	Map<String, Object> evalResults = Maps.newHashMap();
    	evalResults.put(PacmanSdkConstants.EXECUTION_ID,"test");
    	PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("fre-stats");
        PowerMockito.when(CommonUtils.getEnvVariableValue(anyString())).thenReturn("fre-stats");
        PowerMockito.when(CommonUtils.isValidResource(anyString())).thenReturn(true);
        PowerMockito.when(CommonUtils.doHttpPut(anyString(), anyString())).thenReturn("{}");
        PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString(),anyMap())).thenReturn("{\"count\":\"10\"}");

  		assertNotNull(ESUtils.publishMetrics(evalResults,""));
  	}
	
    /**
     * Publish metrics test 3.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
	@Test
  	public void publishMetricsTest3() throws Exception {
    	Map<String, Object> evalResults = Maps.newHashMap();
    	evalResults.put(PacmanSdkConstants.EXECUTION_ID,"test");
    	PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("fre-stats");
        PowerMockito.when(CommonUtils.getEnvVariableValue(anyString())).thenReturn("fre-stats");
        PowerMockito.when(CommonUtils.isValidResource(anyString())).thenReturn(false);
        PowerMockito.when(CommonUtils.doHttpPut(anyString(), anyString())).thenReturn("{}");
        PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString(),anyMap())).thenThrow(Exception.class);

  		assertFalse(ESUtils.publishMetrics(evalResults,""));
  	}
    
    /**
     * Publish metrics test.
     *
     * @throws Exception the exception
     */
    @Test
  	public void publishMetricsTest() throws Exception {
    	Map<String, Object> evalResults = Maps.newHashMap();
    	evalResults.put(PacmanSdkConstants.EXECUTION_ID,"test");
    	PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("fre-stats");
        PowerMockito.when(CommonUtils.getEnvVariableValue(anyString())).thenReturn("fre-stats");
        PowerMockito.when(CommonUtils.isValidResource(anyString())).thenReturn(false);
        PowerMockito.when(CommonUtils.doHttpPut(anyString(), anyString())).thenReturn("{}");
        PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString(),anyMap())).thenReturn("{\"count\":\"10\"}");

  		assertNotNull(ESUtils.publishMetrics(evalResults,""));
  	}
    
    /**
     * Gets the ES port.
     *
     * @throws Exception the exception
     */
    @Test
  	public void getESPort() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("1");
  		assertNotNull(ESUtils.getESPort());
  	}
    
    /**
     * Gets the ES host.
     *
     * @throws Exception the exception
     */
    @Test
  	public void getESHost() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("123");
  		assertNotNull(ESUtils.getESHost());
    }
    
    /**
     * Gets the document for id test.
     *
     * @throws Exception the exception
     */
    @Test
  	public void getDocumentForIdTest() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
    	PowerMockito.mockStatic(StringBuilder.class);
    	//StringBuilder stringBuilder = PowerMockito.mock(StringBuilder.class);

    	PowerMockito.when(CommonUtils.getEnvVariableValue(anyString())).thenReturn("123");
    	final StringBuilder stringBuilder = PowerMockito.spy(new StringBuilder());
		PowerMockito.whenNew(StringBuilder.class).withAnyArguments().thenReturn(stringBuilder); 
		
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("123");
    	PowerMockito.mockStatic(Strings.class);
    	PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);

		String jsonObject = "{\"ruleUUID\":\"qqqq123\",\"hits\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString(),anyMap())).thenReturn(jsonObject);
	        
  		assertNotNull(ESUtils.getDocumentForId("index", "targetType", "_id"));
    }
    
    /**
     * Gets the document for id test 1.
     *
     * @throws Exception the exception
     */
    @Test
  	public void getDocumentForIdTest1() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
    	PowerMockito.mockStatic(StringBuilder.class);
    	//StringBuilder stringBuilder = PowerMockito.mock(StringBuilder.class);

    	PowerMockito.when(CommonUtils.getEnvVariableValue(anyString())).thenReturn(null);
    	assertThatThrownBy(() -> ESUtils.getDocumentForId("index", "targetType", "_id")).isInstanceOf(Exception.class);

    }
    
    /**
     * Convert attributeto keyword.
     *
     * @throws Exception the exception
     */
    @Test
  	public void convertAttributetoKeyword() throws Exception {
    	assertEquals(ESUtils.convertAttributetoKeyword("attributeName"), "attributeName.keyword");
    }
    
    
    	   
   
/*	@Test
	public void getFilterForTypeTest() throws ParseException, UnsupportedEncodingException {
		assertEquals(forTest.getFilterForTypeTest(), true);
	}*/
	
	/**
 * Test create keyword.
 */
@Test
	public void testCreateKeyword(){
	    assertTrue(ESUtils.createKeyword("testField").contains(PacmanSdkConstants.ES_KEYWORD_KEY));
	}
	
	/**
	 * Test get resources from es with no es URL.
	 *
	 * @throws Exception the exception
	 */
	@Test(expected=Exception.class)
	public void testGetResourcesFromEsWithNoEsURL() throws Exception{
	    ESUtils.getResourcesFromEs("test", "test", null, null);
	}
	
	/**
	 * Test get resources from es.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void testGetResourcesFromEs() throws Exception{
//	    PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.when(CommonUtils.getEnvVariableValue(PacmanSdkConstants.ES_URI_ENV_VAR_NAME)).thenReturn(DUMMY_ES_HOST);
        PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString(),anyMap())).thenReturn("{\"count\":\"10\"}");
        assertNotNull(ESUtils.getResourcesFromEs("test", "test", null, null));
    }
	
	
	/**
	 * Test create mapping.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void testCreateMapping() throws Exception{
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.doHttpPut(anyString(), anyString())).thenReturn("{}");
        String toReturn = ESUtils.createMapping(DUMMY_ES_HOST, "testIndex", "testType");
        assertNotNull(toReturn);
    }
	
	/**
	 * Test create mapping with parent.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void testCreateMappingWithParent() throws Exception{
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.doHttpPut(anyString(), anyString())).thenReturn("{}");
        String toReturn = ESUtils.createMappingWithParent(DUMMY_ES_HOST, "testIndex", "testType","testParent");
        assertNotNull(toReturn);
    }
	
	/**
	 * Test create index.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void testCreateIndex() throws Exception{
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.doHttpPut(anyString(), anyString())).thenReturn("{}");
        ESUtils.createIndex(DUMMY_ES_HOST, "testIndex");
    }
	
	/**
	 * Test ensure index and type for annotation with no es URL.
	 *
	 * @throws Exception the exception
	 */
	@Test(expected=Exception.class)
    public void testEnsureIndexAndTypeForAnnotationWithNoEsURL() throws Exception{
	    Annotation annotation = new Annotation();
	    annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "test");
	    annotation.put(PacmanSdkConstants.TARGET_TYPE, "test");
	    annotation.put(PacmanSdkConstants.TYPE, "test");
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.isValidResource(anyString())).thenReturn(Boolean.TRUE);
        ESUtils.ensureIndexAndTypeForAnnotation(annotation, Boolean.FALSE);
    } 
	
//	@Test
//    public void testEnsureIndexAndTypeForAnnotation() throws Exception{
//        Annotation annotation = new Annotation();
//        annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "test");
//        annotation.put(PacmanSdkConstants.TARGET_TYPE, "test");
//        annotation.put(PacmanSdkConstants.TYPE, "test");
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.when(CommonUtils.isValidResource(anyString())).thenReturn(Boolean.TRUE);
//        ESUtils.ensureIndexAndTypeForAnnotation(annotation, Boolean.FALSE);
//    } 
	
}
