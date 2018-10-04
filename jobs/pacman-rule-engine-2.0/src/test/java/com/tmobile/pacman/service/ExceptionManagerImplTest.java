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

package com.tmobile.pacman.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.HashMultimap;
import com.tmobile.pacman.util.ESUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ExceptionManagerImplTest.
 *
 * @author kkumar
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ESUtils.class})
public class ExceptionManagerImplTest {


    /** The ex manager. */
    private ExceptionManager exManager;

    /**
     * Setup.
     */
    @Before
    public void setup(){
        exManager = new ExceptionManagerImpl();
    }

    /**
     * Test get individual exceptions.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetIndividualExceptions() throws Exception{
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        try {

                PowerMockito.when(ESUtils.getDataFromES(anyString(), anyString(), anyString(), anyMap(), anyMap(), any(HashMultimap.class), anyList(), anyLong(), anyLong())).thenReturn(new ArrayList());
        } catch (Exception e) {
        }

                exManager.getIndividualExceptions("ec2");
    }

    /**
     * Test get sticky exceptions.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetStickyExceptions() throws Exception{
        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        try {
                PowerMockito.when(ESUtils.getDataFromES(anyString(), anyString(), anyString(), anyMap(), anyMap(), any(HashMultimap.class), anyList(), anyLong(), anyLong())).thenReturn(new ArrayList());
        } catch (Exception e) {
        }

                exManager.getStickyExceptions("r1", "ec2");
    }


}
