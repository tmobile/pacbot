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

package com.tmobile.pacman.executor;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.util.ProgramExitUtils;

// TODO: Auto-generated Javadoc
*//**
 * The Class JobExecutorTest.
 *
 * @author kkumar
 *//*
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ProgramExitUtils.class,ConfigManager.class})

public class JobExecutorTest {

    *//** The job executor. *//*
    private JobExecutor jobExecutor=null;

    *//** The Constant inputDate1. *//*
    final static String inputDate1="{\"jobName\":\"aws-redshift-es-data-shipper\",\"params\":[{\"encrypt\":false,\"key\":\"package_hint\",\"value\":\"com.tmobile\"},{\"encrypt\":false,\"key\":\"datasource\",\"value\":\"aws\"},{\"encrypt\":false,\"key\":\"redshiftinfo\",\"value\":\"\"},{\"encrypt\":false,\"key\":\"rdsinfo\",\"value\":\"\"}],\"jobUuid\":\"31f1d5ab-fa12-419f-890e-b153962379be\",\"jobDesc\":\"Ship aws data periodically from redshfit to ES\",\"jobType\":\"jar\"}";
    
    
    *//**
     * Setup.
     *//*
    @Before
    public void setup(){
    	mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
        PowerMockito.spy(ProgramExitUtils.class);
    }

    *//**
     * Test main with method signature match.
     *
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     *//*
    @Test
    public void testMainWithMethodSignatureMatch() throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException{
        String[] args = new String[2];
        args[0]=inputDate1;
        PowerMockito.doNothing().when(ProgramExitUtils.class);
        jobExecutor.main(args);
    }

}
*/