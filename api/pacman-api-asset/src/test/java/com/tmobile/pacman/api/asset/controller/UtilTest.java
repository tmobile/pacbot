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
package com.tmobile.pacman.api.asset.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class UtilTest {

    @Mock
    AssetService service;

    @Test
    public void testisValidTargetType() throws Exception {

        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("type", "ec2");
        tTypeMap.put("category", "aws-all");
        tTypeMap.put("domain", "Infra & Platforms");
        tTypeList.add(tTypeMap);

        when(service.getTargetTypesForAssetGroup(anyObject(), anyObject(), anyString())).thenReturn(tTypeList);
        
        ReflectionTestUtils.setField(Util.class, "assetService", service);
        boolean valid = Util.isValidTargetType("aws-all", "ec2");

        assertTrue(valid);

        doThrow(new NullPointerException()).when(service).getTargetTypesForAssetGroup(anyObject(), anyObject(), anyString());
        valid = Util.isValidTargetType("aws-all", "ec2");

        assertTrue(!valid);
    }

    @Test
    public void testisValidAssetGroup() throws Exception {

        List<Map<String, Object>> tTypeList = new ArrayList<>();
        Map<String, Object> tTypeMap = new HashMap<>();
        tTypeMap.put("name", "aws-all");
        tTypeList.add(tTypeMap);
       
        when(service.getAllAssetGroups()).thenReturn(tTypeList);
        
        new Util().setassetService(service);
        boolean valid = Util.isValidAssetGroup("aws-all");

        assertTrue(valid);

    }

    @Test
    public void testgetUtilisationScore() throws Exception {

        int score = Util.getUtilisationScore(75);
        assertTrue(score == 10);

        score = Util.getUtilisationScore(55);
        assertTrue(score == 9);

        score = Util.getUtilisationScore(45);
        assertTrue(score == 8);

        score = Util.getUtilisationScore(35);
        assertTrue(score == 7);

        score = Util.getUtilisationScore(26);
        assertTrue(score == 6);

        score = Util.getUtilisationScore(13);
        assertTrue(score == 3);

        score = Util.getUtilisationScore(7);
        assertTrue(score == 2);

        score = Util.getUtilisationScore(3);
        assertTrue(score == 1);

    }
    
    @Test
    public void encodeUrl() throws Exception{
        String encoded = Util.encodeUrl("http://wwww.google.com");
        assertTrue(encoded.equals("http%3A%2F%2Fwwww.google.com"));
        
        
    }

}