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

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ProgramExitUtils;
import com.tmobile.pacman.util.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AnnotationPublisherTest.
 *
 * @author kkumar
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class,ESUtils.class,CommonUtils.class,ConfigManager.class})
public class AnnotationPublisherTest {

    /** The annotation publisher. */
    AnnotationPublisher annotationPublisher;

    /**
     * Setup.
     */
    @Before
    public void setup(){
        annotationPublisher = new AnnotationPublisher();
            mockStatic(ConfigManager.class);
            ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
    		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }


    /**
     * Test publish with no annotations.
     */
    @Test
    public void testPublishWithNoAnnotations(){

        try {
                annotationPublisher.setBulkUploadBucket(new ArrayList<>());
                annotationPublisher.publish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test publish with annotations.
     */
    @Test
    public void testPublishWithAnnotations(){

        PowerMockito.mockStatic(ESUtils.class);
        PowerMockito.when(ESUtils.getEsUrl()).thenReturn("");
        PowerMockito.mockStatic(CommonUtils.class);
        try {
                PowerMockito.when(CommonUtils.doHttpPost(anyString(),anyString())).thenReturn("");
        } catch (Exception e1) {
        }

        annotationPublisher.setBulkUploadBucket(buildSomeAnnotations());
        try {
                annotationPublisher.publish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Builds the some annotations.
     *
     * @return the list
     */
    private List<Annotation> buildSomeAnnotations() {

        List<Annotation> annotations = new ArrayList<>();
        Annotation annotation = new Annotation();
        annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "");
        annotation.put(PacmanSdkConstants.TARGET_TYPE, "");
        annotations.add(annotation);
        return annotations;
    }




}
