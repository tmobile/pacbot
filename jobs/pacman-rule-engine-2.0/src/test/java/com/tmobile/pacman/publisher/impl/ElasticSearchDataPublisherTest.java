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

package com.tmobile.pacman.publisher.impl;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ProgramExitUtils;
import com.tmobile.pacman.util.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ElasticSearchDataPublisherTest.
 *
 * @author kkumar
 */
@PowerMockIgnore({"org.apache.http.conn.ssl.*", "javax.net.ssl.*" , "javax.crypto.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class,ESUtils.class,CommonUtils.class,ConfigManager.class})
public class ElasticSearchDataPublisherTest {
    
	/**
     * Setup.
     */
    @Before
    public void setup(){
        mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }
    /** The elastic search data publisher. */
    private ElasticSearchDataPublisher elasticSearchDataPublisher=null;
    
    /**
     * Test publish with no annotations.
     *
     * @throws Exception the exception
     */
    @Test
    public void testPublishWithNoAnnotations() throws Exception{
    //	RestHighLevelClient restHighLevelClient = PowerMockito.mock(RestHighLevelClient.class);
       // RestClient restClient = PowerMockito.mock(RestClient.class);
       // RestClientBuilder restClientBuilder = PowerMockito.mock(RestClientBuilder.class);
       // PowerMockito.when(RestClient.builder(any())).thenReturn(restClientBuilder);
      //  HttpHost httpHost = PowerMockito.mock(HttpHost.class);
    //    PowerMockito.whenNew(HttpHost.class).withAnyArguments().thenReturn(httpHost);  
        elasticSearchDataPublisher = new ElasticSearchDataPublisher(true);
        
        List<AutoFixTransaction> autoFixTrans = Lists.newArrayList();
    	AutoFixTransaction autoFixTransaction = new AutoFixTransaction();
    	autoFixTransaction.setDesc("desc");
    	autoFixTransaction.setAction(AutoFixAction.AUTOFIX_ACTION_BACKUP);
    	autoFixTransaction.setExecutionId("executionId");
    	autoFixTransaction.setResourceId("resourceId");
    	autoFixTransaction.setRuleId("ruleId");
    	autoFixTransaction.setTransactionId("transactionId");
    	autoFixTransaction.setTransationTime("transationTime");
    	autoFixTrans.add(autoFixTransaction);
    	
    	AutoFixTransaction autoFixTransaction1 = new AutoFixTransaction(AutoFixAction.AUTOFIX_ACTION_BACKUP, "resourceId", "ruleId", "executionId", "transactionId", "desc","type","targetType","annotationId");
    	assertTrue(autoFixTransaction1.equals(autoFixTransaction1));
    	assertNotNull(elasticSearchDataPublisher.publishAutoFixTransactions(autoFixTrans,new HashMap<>()));
    } 
    
}
