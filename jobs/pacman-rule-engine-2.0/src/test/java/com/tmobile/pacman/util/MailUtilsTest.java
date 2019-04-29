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
*//**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar
  Modified Date: Jul 16, 2018

**//*

 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 
package com.tmobile.pacman.util;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.dto.ResourceOwner;
import com.tmobile.pacman.publisher.impl.AnnotationPublisher;

// TODO: Auto-generated Javadoc
*//**
 * The Class MailUtilsTest.
 *
 * @author kkumar
 *//*
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ESUtils.class, CommonUtils.class,ConfigManager.class})
public class MailUtilsTest {

	@Mock
	private HttpResponse response;
	
	@Mock
	private StatusLine sl;
	
	

    *//**
     * Setup.
     *//*
    @Before
    public void setup(){
            mockStatic(ConfigManager.class);
            ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
    		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }
	*//**
 * Send auto fix notification.
 *
 * @throws Exception the exception
 *//*
@SuppressWarnings("unchecked")
	@Test
	public void sendAutoFixNotification() throws Exception {
	  mockStatic(ConfigManager.class);
      ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
		PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.when(ESUtils.publishMetrics(anyMap(),anyString())).thenReturn(Boolean.TRUE);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString(),anyMap())).thenReturn("");
        PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString(),anyMap())).thenReturn("");
        PowerMockito.when(CommonUtils.getTemplateContent(anyString())).thenReturn("");
        PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("test@gmail.com;test@gmail.com");
        Map<String, String> params = new HashMap<>();
        params.put(PacmanSdkConstants.RULE_ID, "ruleId123");
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setEmailId("test@gmail.com");
        resourceOwner.setName("name123");
		boolean response = MailUtils.sendAutoFixNotification(params, resourceOwner, "targetType123", "resourceid123", "31/05/1999", AutoFixAction.AUTOFIX_ACTION_EMAIL,new ArrayList<AutoFixTransaction>(),new HashMap<String, String>());
		assertTrue(response);
	}
    
}
*/