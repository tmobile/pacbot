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

package com.tmobile.pacman.executor;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.google.gson.JsonObject;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.executor.rules.TestPacRule;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ProgramExitUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class RuleExecutorTest.
 *
 * @author kkumar
 */

@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ESUtils.class,ProgramExitUtils.class,ConfigManager.class})
public class RuleExecutorTest {

    
    /** The re. */
    //RuleExecutor re = new RuleExecutor();
    
    final static String TEST_KEY = "test_key";

    /**
     * Setup.
     */
    @Before
    public void setup(){
        PowerMockito.mockStatic(ProgramExitUtils.class);
        
        mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }
    
    
    /**
     * Test run single thread.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRunSingleThread() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        List<Map<String, String>> resources = new ArrayList<>();
        
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.when(CommonUtils.createParamMap(anyString())).thenCallRealMethod();
//        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString() ,anyMap())).thenReturn("");
//        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString())).thenReturn("");
        final Class<?> ruleClass = TestPacRule.class;
//        PowerMockito.mockStatic(ReflectionUtils.class);
//        PowerMockito.when(ReflectionUtils.findAssociateClass(anyString())).thenAnswer(new Answer<Object>() {
//            @Override
//            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//                return ruleClass;
//            }
//        });
        
        Map<String, String> resource = new HashMap<>();
        resource.put(PacmanSdkConstants.DOC_ID, "testId");
        resource.put(PacmanSdkConstants.RESOURCE_ID,"testResId");
        resources.add(resource);
        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(resources);
        
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.doNothing().when(CommonUtils.class);
        
        JsonObject input = new JsonObject();
        PowerMockito.mockStatic(CommonUtils.class);
        input.addProperty("ruleName", "test");
        input.addProperty(PacmanSdkConstants.RULE_KEY, TEST_KEY);
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "aws");
        String[] args = {input.toString()};
        RuleExecutor.main(args);   
    }
    
    
   /* *//**
     * Test run.
     *
     * @throws Exception the exception
     *//*
    @Test
    public void testRunWithNullRuleKey() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        List<Map<String, String>> resources = new ArrayList<>();
        Map<String, String> issue = new HashMap<>();
        issue.put(PacmanSdkConstants.DOC_ID, "testId");
        resources.add(issue);
        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(resources);
        
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.doNothing().when(CommonUtils.class);
        JsonObject input = new JsonObject();
        
        input.addProperty("ruleName", "test");
        input.addProperty(PacmanSdkConstants.RULE_KEY, TEST_KEY);
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "aws");
        String[] args = {input.toString()};
        PowerMockito.mockStatic(RuleExecutor.class);
        RuleExecutor.main(args);   
    }*/
    
    
    /**
     * Test run.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRunWithNoResourcesFound() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(new ArrayList<>());
        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.doNothing().when(CommonUtils.class);
        
        JsonObject input = new JsonObject();
        
        input.addProperty("ruleName", "test");
        
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "aws");
        String[] args = {input.toString()};
        PowerMockito.mockStatic(RuleExecutor.class);
        RuleExecutor.main(args);    
    }
    
    
    /**
     * Test run.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRunWithEmptyDS() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        
        
        JsonObject input = new JsonObject();
        PowerMockito.mockStatic(CommonUtils.class);
        input.addProperty("ruleName", "test");
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "");
        String[] args = {input.toString()};
        PowerMockito.mockStatic(RuleExecutor.class);
        RuleExecutor.main(args);    
    }
    
    
//    @Test
//    public void testDSNull() throws Exception{
//        
//        PowerMockito.mockStatic(ESUtils.class);
//        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
//        PowerMockito.when(ESUtils.publishMetrics(anyMap())).thenReturn(Boolean.TRUE);
//        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(new ArrayList<>());
//        
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.doNothing().when(CommonUtils.class);
//        
//        JsonObject input = new JsonObject();
//        
//        input.addProperty("ruleName", "test");
//        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "");
//        String[] args = {input.toString()};
//        re.main(args);   
//    }
    
    
    /**
 * Test run multi thread with rule passing.
 *
 * @throws Exception the exception
 */
@Test
    public void testRunMultiThreadWithRulePassing() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        
        final Class<?> ruleClass = TestPacRule.class;
        List<Map<String, String>> resources = new ArrayList<>();

        Map<String, String> resource = new HashMap<>();
        resource.put(PacmanSdkConstants.DOC_ID, "testId");
        resource.put(PacmanSdkConstants.RESOURCE_ID,"testResId");
        resources.add(resource);
        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(resources);
        
        JsonObject input = new JsonObject();
        input.addProperty("ruleName", "test");
        input.addProperty(PacmanSdkConstants.RUN_ON_MULTI_THREAD_KEY, "true");
        input.addProperty(PacmanSdkConstants.RULE_KEY, TEST_KEY);
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "aws");
        String[] args = {input.toString()};
        RuleExecutor.main(args);   
    }
    
   /* *//**
     * Test run multi thread with rule failing.
     *
     * @throws Exception the exception
     *//*
    @Test
    public void testRunMultiThreadWithRuleFailing() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        
        final Class<?> ruleClass = TestPacRule.class;
        List<Map<String, String>> resources = new ArrayList<>();

        Map<String, String> resource = new HashMap<>();
        resource.put(PacmanSdkConstants.DOC_ID, "testId");
        resource.put(PacmanSdkConstants.RESOURCE_ID,"testResId");
        resources.add(resource);
        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(resources);
        
        JsonObject input = new JsonObject();
        input.addProperty("ruleName", "test");
        input.addProperty(PacmanSdkConstants.RUN_ON_MULTI_THREAD_KEY, "true");
        input.addProperty(PacmanSdkConstants.RULE_KEY, TEST_KEY+"_fail");
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "aws");
        String[] args = {input.toString()};
        RuleExecutor.main(args);   
    }*/
    
   /* *//**
     * Test run serverless rule.
     *
     * @throws Exception the exception
     *//*
    @Test
    public void testRunServerlessRule() throws Exception{
        
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        
        final Class<?> ruleClass = TestPacRule.class;
        List<Map<String, String>> resources = new ArrayList<>();

//        PowerMockito.mockStatic(ReflectionUtils.class);
//        PowerMockito.when(ReflectionUtils.findAssociateClass(anyString())).thenAnswer(new Answer<Object>() {
//            @Override
//            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//                return ruleClass;
//            }
//        });
        
        Map<String, String> resource = new HashMap<>();
        resource.put(PacmanSdkConstants.DOC_ID, "testId");
        resource.put(PacmanSdkConstants.RESOURCE_ID,"testResId");
        resources.add(resource);
        PowerMockito.when(ESUtils.getResourcesFromEs(anyString(), anyString(), anyMap(), anyList())).thenReturn(resources);
        
//        PowerMockito.mockStatic(CommonUtils.class);
//        PowerMockito.doNothing().when(CommonUtils.class);
        
        JsonObject input = new JsonObject();
        input.addProperty(PacmanSdkConstants.RULE_TYPE,PacmanSdkConstants.RULE_TYPE_SERVERLESS);
        input.addProperty("ruleName", "test");
//        input.addProperty(PacmanSdkConstants.RUN_ON_MULTI_THREAD_KEY, "true");
        input.addProperty(PacmanSdkConstants.RULE_KEY, TEST_KEY);
        input.addProperty(PacmanSdkConstants.DATA_SOURCE_KEY, "aws");
        String[] args = {input.toString()};
        RuleExecutor.main(args);   
    }*/
    
    
}
