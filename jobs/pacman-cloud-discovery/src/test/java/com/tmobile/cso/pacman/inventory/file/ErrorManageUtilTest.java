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
package com.tmobile.cso.pacman.inventory.file;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * The Class ErrorManageUtilTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FileManager.class,FileGenerator.class})
@PowerMockIgnore("javax.management.*")
public class ErrorManageUtilTest {

    /** The error manage util. */
    @InjectMocks
    ErrorManageUtil errorManageUtil;
    
    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * Initialise test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void initialiseTest() throws Exception{
        
        mockStatic(FileGenerator.class);
        PowerMockito.doNothing().when(FileGenerator.class,"writeToFile",anyString(),anyString(),anyBoolean());
        errorManageUtil.initialise();
        
        PowerMockito.doThrow(new IOException()).when(FileGenerator.class,"writeToFile",anyString(),anyString(),anyBoolean());
        errorManageUtil.initialise();
    }
    
    /**
     * Upload error test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void uploadErrorTest() throws Exception{
        
        errorManageUtil.uploadError("account", "region", "type", "exception");
    }
    
    /**
     * Write error file test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void writeErrorFileTest() throws Exception{
        
        mockStatic(FileManager.class);
        PowerMockito.doNothing().when(FileManager.class,"generateErrorFile",anyMap());
        errorManageUtil.writeErrorFile();
        
        PowerMockito.doThrow(new IOException()).when(FileManager.class,"generateErrorFile",anyMap());
        errorManageUtil.writeErrorFile();
    }
}
