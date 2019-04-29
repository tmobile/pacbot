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
 ******************************************************************************//*

package com.tmobile.pacman.commons.autofix.manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.util.CommonUtils;

// TODO: Auto-generated Javadoc
*//**
 * The Class NextStepManagerTest.
 *
 * @author kkumar
 *//*
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({CommonUtils.class,ConfigManager.class})
public class NextStepManagerTest {

    *//** The nextstep manager. *//*
    private NextStepManager nextstepManager=null;

    *//** The tagging manager. *//*
    @Mock
    ResourceTaggingManager taggingManager;

    
     *
     * {\"lastActions\":[],\"message\":\"Last action not found!!!\",\"responseCode\":0}
     *
     

    *//**
     * Setup.
     *//*
    @Before
    public void setup(){
    	mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
        PowerMockito.spy(CommonUtils.class);
    }

    *//**
     * Test get next step with response code 0.
     *//*
    @Test
    public void testGetNextStepWithResponseCode0(){

        this.nextstepManager = new NextStepManager();
        String ruleId="test"; //$NON-NLS-1$
        String resourceId="test"; //$NON-NLS-1$
        Map<String, Object> clientMap=new HashMap<>();
        AWSService serviceType= AWSService.S3;
        Mockito.when(taggingManager.getPacmanTagValue(anyString(), anyMap(), any())).thenReturn("");
        PowerMockito.when(CommonUtils.doHttpGet(anyString())).thenReturn("{\"lastActions\":[],\"message\":\"Last action not found!!!\",\"responseCode\":0}");
        PowerMockito.when(CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_GET_LASTACTION)).thenReturn("http://localhost");

        nextstepManager.setTaggingManager(taggingManager);
        assertEquals(AutoFixAction.AUTOFIX_ACTION_EMAIL,this.nextstepManager.getNextStep(ruleId, resourceId, clientMap, serviceType));


    }

    *//**
     * Test get next step with response code 1.
     *//*
    @Test
    public void testGetNextStepWithResponseCode1(){

        this.nextstepManager = new NextStepManager();
        String ruleId="test"; //$NON-NLS-1$
        String resourceId="test"; //$NON-NLS-1$
        Map<String, Object> clientMap=new HashMap<>();
        AWSService serviceType= AWSService.S3;
        Mockito.when(taggingManager.getPacmanTagValue(anyString(), anyMap(), any())).thenReturn("");
        PowerMockito.when(CommonUtils.doHttpGet(anyString())).thenReturn("{\"lastActions\":[1529704215000,1529704226000,1529704226000],\"message\":\"Last action  found!!!\",\"responseCode\":1}");
        PowerMockito.when(CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_GET_LASTACTION)).thenReturn("http://localhost");

        nextstepManager.setTaggingManager(taggingManager);
        assertEquals(AutoFixAction.AUTOFIX_ACTION_FIX,this.nextstepManager.getNextStep(ruleId, resourceId, clientMap, serviceType));


    }

}
*/