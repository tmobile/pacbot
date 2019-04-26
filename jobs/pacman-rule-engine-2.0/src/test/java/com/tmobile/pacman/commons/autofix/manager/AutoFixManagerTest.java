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

package com.tmobile.pacman.commons.autofix.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.reflect.Method;
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
import org.powermock.reflect.Whitebox;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.dto.ExceptionType;
import com.tmobile.pacman.dto.IssueException;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AutoFixManagerTest.
 *
 * @author kkumar
 */
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class,ESUtils.class, CommonUtils.class, Strings.class,ConfigManager.class})
public class AutoFixManagerTest {


    /** The auto fix manager. */
    AutoFixManager autoFixManager;

    /**
     * Setup.
     */
    @Before
   public void setup(){
    	mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
        autoFixManager = new AutoFixManager();
        PowerMockito.mockStatic(ReflectionUtils.class);
        PowerMockito.mockStatic(ESUtils.class);
        Class cl = String.class;
        Method method = null;
        try {
            PowerMockito.when(ReflectionUtils.findFixClass(anyString())).thenReturn(cl);
            PowerMockito.when(ReflectionUtils.findAssociatedMethod(anyObject(), anyString())).thenReturn(method);
            PowerMockito.when(ESUtils.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyString(), anyMap(), anyMap(), any(HashMultimap.class))).thenReturn(10L);
            PowerMockito.when(ESUtils.getDataFromES(anyString(), anyString(), anyString(), anyMap(), anyMap(), any(HashMultimap.class), anyList(), anyLong(), anyLong())).thenReturn(new ArrayList());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

    /**
     * Checks if is A fix candidate.
     *
     * @throws Exception the exception
     */
    @Test
    public void isAFixCandidate() throws Exception{
		Method isFixCandidateMethod = null;;
		Object fixObject = null;
		String resourceId = "resourceId";
		String targetType= "targetType";
		Map<String, Object> clientMap = Maps.newHashMap();
		clientMap.put("field123", "field123");
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");
		final AutoFixManager classUnderTest = PowerMockito.spy(new AutoFixManager());
		Whitebox.invokeMethod(classUnderTest, "isAFixCandidate", isFixCandidateMethod, fixObject, resourceId, targetType, clientMap, clientMap, clientMap);
	}

    /**
     * Checks if is resource type exempted from cut of date criteria.
     *
     * @throws Exception the exception
     */
    @Test
	public void isResourceTypeExemptedFromCutOfDateCriteria() throws Exception {
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString(),anyMap())).thenReturn("");
        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString())).thenReturn("");
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenCallRealMethod();
        final AutoFixManager classUnderTest = PowerMockito.spy(new AutoFixManager());
		Whitebox.invokeMethod(classUnderTest, "isResourceTypeExemptedFromCutOfDateCriteria", "targetType123");
	}

    /**
     * Creates the pac tag.
     *
     * @throws Exception the exception
     */
    @Test
   	public void createPacTag() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
    	PowerMockito.when(CommonUtils.getPropValue(anyString())).thenCallRealMethod();
    	final AutoFixManager classUnderTest = PowerMockito.spy(new AutoFixManager());
   		Whitebox.invokeMethod(classUnderTest, "createPacTag", "exceptionDetails123");
   	}

    /**
     * Checks if is account white listed for auto fix.
     *
     * @throws Exception the exception
     */
    @Test
   	public void isAccountWhiteListedForAutoFix() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
       	PowerMockito.when(CommonUtils.getPropValue(anyString())).thenCallRealMethod();
       	final AutoFixManager classUnderTest = PowerMockito.spy(new AutoFixManager());
   		Whitebox.invokeMethod(classUnderTest, "isAccountWhiteListedForAutoFix", "account", "ruleId");
   	}



    /**
     * Resource created before cutoff data.
     *
     * @throws Exception the exception
     */
    @Test
   	public void resourceCreatedBeforeCutoffData() throws Exception {
    	PowerMockito.mockStatic(CommonUtils.class);
       	PowerMockito.when(CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_CREATIONDATE)).thenReturn("12/12/2017");
       	PowerMockito.when(CommonUtils.doHttpGet(anyString())).thenReturn("{\"data\":\"12-12-2017\"}");

       	final AutoFixManager classUnderTest = PowerMockito.spy(new AutoFixManager());
   		Whitebox.invokeMethod(classUnderTest, "resourceCreatedBeforeCutoffData", "resourceid", "resourceType");
   	}


/*    *//**
     * Test perform auto fixs.
     *
     * @throws Exception the exception
     *//*
    @Test
    public void testPerformAutoFixs() throws Exception{
    	List<Map<String, String>> allAnnotations = Lists.newArrayList();
    	Map<String, String> annotation = Maps.newHashMap();
    	annotation.put("_resourceid", "_resourceid123");
    	annotation.put("targetType", "account");
    	annotation.put(PacmanSdkConstants.ES_DOC_ID_KEY, "ES_DOC_ID_KEY");
    	annotation.put(PacmanSdkConstants.ACCOUNT_ID, "account");
    	annotation.put("issueStatus", "account");
    	allAnnotations.add(annotation);
		String jsonObject = "{\"count\":\"123\",\"data\":{\"total\":1000,\"max_score\":null,\"hits\":[{\"_index\":\"bank\",\"_type\":\"_doc\",\"_id\":\"0\",\"sort\":[0],\"_score\":null,\"_source\":{\"account_number\":0,\"balance\":16623,\"firstname\":\"Bradshaw\",\"lastname\":\"Mckenzie\",\"age\":29,\"gender\":\"F\",\"address\":\"244 Columbus Place\",\"employer\":\"Euron\",\"email\":\"bradshawmckenzie@euron.com\",\"city\":\"Hobucken\",\"state\":\"CO\"}}]},\"aggregations\":{\"avg-values-per-day\":{\"buckets\":[{\"key_as_string\":\"ID\",\"Avg-CPU-Utilization\":{\"value\":12},\"Avg-NetworkIn\":{\"value\":12},\"Avg-NetworkOut\":{\"value\":12},\"Avg-DiskReadinBytes\":{\"value\":12},\"Avg-DiskWriteinBytes\":{\"value\":12}}]}}}";
		PowerMockito.mockStatic(CommonUtils.class);
		PowerMockito.mockStatic(ESUtils.class);
		PowerMockito.mockStatic(Strings.class);
		PowerMockito.when(ESUtils.getEsUrl()).thenReturn("esUrl");
		PowerMockito.when(CommonUtils.getIndexNameFromRuleParam(anyMap())).thenReturn("anyIndex");

		PowerMockito.when(Strings.isNullOrEmpty(anyString())).thenReturn(false);
		PowerMockito.when(CommonUtils.doHttpPost(anyString(), anyString())).thenReturn(jsonObject);
		PowerMockito.when(CommonUtils.doHttpGet(anyString())).thenReturn(jsonObject);
		PowerMockito.when(ESUtils.getTotalDocumentCountForIndexAndType(anyString(),anyString(),anyString(),anyMap(), anyMap(), anyObject())).thenReturn(12l);
		PowerMockito.when(ESUtils.getDataFromES(anyString(),anyString(),anyString(),anyMap(), anyMap(), anyObject(), anyList(), anyLong(), anyLong())).thenReturn(allAnnotations);
		PowerMockito.when(CommonUtils.getUniqueIdForString(anyString())).thenReturn("transactionId");
		PowerMockito.when(CommonUtils.getPropValue(anyString())).thenCallRealMethod();




        Map<String, String> ruleParam = new HashMap<>();
        ruleParam.put("fix-key", "test");
        ruleParam.put(PacmanSdkConstants.RULE_ID, "ruleId123");
        IssueException issueException1 = getIssueException1();
        IssueException issueException2 = getIssueException2();
        List<IssueException> allissueException = Lists.newArrayList();
        allissueException.add(issueException1);
        allissueException.add(issueException2);
        Map<String, List<IssueException>> exemptedResourcesForRule = new HashMap<>();
        exemptedResourcesForRule.put("issueException", allissueException);
        Map<String, IssueException> individuallyExcemptedIssues = new HashMap<>();
        AutoFixManager autoFixManagerMock = PowerMockito.spy(new AutoFixManager());
       // try {
        assertNotNull(autoFixManagerMock.performAutoFixs(ruleParam, exemptedResourcesForRule, individuallyExcemptedIssues));
       // } catch (Exception e) {
       // }
    }*/

    /**
     * Gets the issue exception 2.
     *
     * @return the issue exception 2
     */
    private IssueException getIssueException2() {
    	Map<String, String> exception = Maps.newHashMap();
		exception.put("_id", "123");
		exception.put("exceptionReason", "exceptionReason");
		exception.put("exceptionName", "exceptionName");
		exception.put("assetGroup", "assetGroup");
		exception.put("expiryDate", "expiryDate");
		exception.put("issueId", "issueId");
		exception.put("exceptionEndDate", "exceptionEndDate");
		ExceptionType exceptionType = ExceptionType.INDIVIDUAL;
		IssueException issueException3 = new IssueException(exception, exceptionType);
		IssueException issueException = new IssueException(exception, exceptionType);
		IssueException issueException2 = new IssueException("exceptionName", "assetGroup", "exceptionReason", "expiryDate" , ExceptionType.INDIVIDUAL);
		issueException2.setId("id");
		issueException3.setAssetGroup("assetGroup");
		issueException3.setExceptionName("exceptionName");
		issueException3.setExceptionReason("exceptionReason");
		issueException3.setExceptionType(ExceptionType.INDIVIDUAL);
		issueException3.setExpiryDate("expiryDate");
		issueException3.setId("id");
		issueException3.setIssueId("issueId");

		assertTrue(issueException3.getId().equals("id"));
		assertTrue(issueException3.getExceptionName().equals("exceptionName"));
		assertTrue(issueException3.getExceptionReason().equals("exceptionReason"));
		assertTrue(issueException3.getExceptionType().equals(ExceptionType.INDIVIDUAL));
		assertTrue(issueException3.getExpiryDate().equals("expiryDate"));
		assertTrue(issueException3.getIssueId().equals("issueId"));


		assertTrue(issueException3.getAssetGroup().equals("assetGroup"));
		assertTrue(issueException3.equals(issueException2));
		assertTrue(issueException2.equals(issueException2));
		assertFalse(IssueException.class.equals(issueException));
		assertTrue(issueException.equals(issueException));
		assertNotNull(issueException2.toString());
		assertNotNull(issueException.toString());
		return issueException;
	}

	/**
	 * Gets the issue exception 1.
	 *
	 * @return the issue exception 1
	 */
	private IssueException getIssueException1() {
		Map<String, String> exception = Maps.newHashMap();
		exception.put("_id", "123");
		exception.put("exceptionReason", "exceptionReason");
		exception.put("exceptionName", "exceptionName");
		exception.put("assetGroup", "assetGroup");
		exception.put("expiryDate", "expiryDate");
		exception.put("issueId", "issueId");
		exception.put("exceptionEndDate", "exceptionEndDate");
		ExceptionType exceptionType = ExceptionType.STICKY;
		IssueException issueException = new IssueException(exception, exceptionType);
		return issueException;
	}

}
