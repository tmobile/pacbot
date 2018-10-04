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
package com.tmobile.pacman.api.compliance.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.domain.Medal;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.FilterRepository;
import com.tmobile.pacman.api.compliance.repository.PatchingRepository;
import com.tmobile.pacman.api.compliance.repository.ProjectionRepository;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class PatchingServiceImplTest {

    @InjectMocks
    private PatchingServiceImpl patchingServiceImpl;

    @Mock
    private ElasticSearchRepository elasticSearchRepository;
    
    @Mock
    private PatchingRepository repository;
    
    @Mock
    private ComplianceRepository complainceRepository;
    
    @Mock
    private ComplianceService complianceService;
    
    @Mock
    private IssueTrendService issueTrendService;
    
    @Mock
    private FilterRepository filterRepository;
    
    @Mock
    private ProjectionRepository projectionRepository;
    
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
 
    @Test
    public void getNonCompliantNumberForAGTest() throws Exception {
        Map<String, Long> nonComplaintMap = new HashMap<>();
        nonComplaintMap.put("Adapt", 500l);
        
        List<Map<String, Object>> execAndDirectorInfoList = new ArrayList<>();
        Map<String,Object> execAndDirectorInfoMap = new HashMap<>();
        execAndDirectorInfoMap.put("appTag", "Adapt");
        execAndDirectorInfoMap.put("director", "John Doel");
        execAndDirectorInfoMap.put("executiveSponsor", "John Doe");
        execAndDirectorInfoList.add(execAndDirectorInfoMap);
        List<Map<String, Long>> nonComplaintList = new ArrayList<>();
        nonComplaintList.add(nonComplaintMap);
        
        
        when(filterRepository.getListOfTargetTypes(anyString(),anyString()))
                .thenReturn(CommonTestUtil.getAssetCountByApps());
        
         when(repository.getNonCompliantNumberForAgAndResourceType(anyString(),anyString()))
                .thenReturn(nonComplaintMap);
         
         when(repository.getExecAndDirectorInfo())
         .thenReturn(execAndDirectorInfoList);
        
        assertThat(patchingServiceImpl.getNonCompliantNumberForAG("anyString"),
                is(notNullValue()));
    }
    
    @Test
    public void getNonCompliantExecsForAGTest() throws Exception {
        Map<String, Long> nonComplaintMap = new HashMap<>();
        nonComplaintMap.put("ec2", 100l);
        nonComplaintMap.put("s3", 500l);
        
        List<Map<String, Long>> nonComplaintList = new ArrayList<>();
        nonComplaintList.add(nonComplaintMap);
        
        when(filterRepository.getListOfTargetTypes(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getAssetCountByApps());
        
        when(repository.getNonCompliantNumberForAgAndResourceType(anyString(),anyString()))
        .thenReturn(nonComplaintMap);
        
        assertThat(patchingServiceImpl.getNonCompliantExecsForAG("anyString"),
                is(notNullValue()));
    }
    
    @Test
    public void getQuartersWithPatchingDataTest() throws Exception {
        Map<String, Long> nonComplaintMap = new HashMap<>();
        nonComplaintMap.put("2018-04-01T00:00:00.000Z", 100l);
        
        when(repository.getQuartersWithPatchingData(anyString()))
                .thenReturn(nonComplaintMap);
        
        assertThat(patchingServiceImpl.getQuartersWithPatchingData("anyString"),
                is(notNullValue()));
    }
    
    @Test
    public void getPatchingProgressTest() throws Exception {
        Map<String, Object> patchingProgressMap = new HashMap<>();
        List<Map<String, Object>> patchingProgressList = new ArrayList<>();
        patchingProgressMap.put("2018-04-01T00:00:00.000Z", 100l);
        patchingProgressMap.put("date", "2018-04-01");
        patchingProgressMap.put("patching_percentage", "20");
        patchingProgressMap.put("total_instances", "2000");
        patchingProgressList.add(patchingProgressMap);
        patchingProgressMap.put("2018-04-01T00:00:00.000Z", 100l);
        patchingProgressMap.put("date", "2018-04-07");
        patchingProgressMap.put("patching_percentage", "20");
        patchingProgressMap.put("appTag", "Cloud");
        patchingProgressList.add(patchingProgressMap);
        
        when(repository.getPatchingProgress(anyString(),anyObject(),anyObject()))
                .thenReturn(patchingProgressList);
        
       when(filterRepository.getListOfTargetTypes(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getAssetCountByApps());
       
       when(repository.getAmilAvailDate(anyInt(),anyInt()))
       .thenReturn("2017-10-13");
        
        assertThat(patchingServiceImpl.getPatchingProgress("anyString",2018,2),
                is(notNullValue()));
    }
    
    @Test
    public void getPatchingDetailsTest() throws Exception {
        Map<String, Object> patchingProgressMap = new HashMap<>();
        List<Map<String, Object>> patchingProgressList = new ArrayList<>();
        patchingProgressMap.put("_resourceid", "_resourceid");
        patchingProgressMap.put("issueStatus", "open");
        patchingProgressMap.put("reason-to-close", "2020");
        patchingProgressMap.put("executiveSponsor", "John Doe");
        patchingProgressMap.put("director", "John Doel");
        patchingProgressMap.put("appTag", "Cloud Core");
        patchingProgressMap.put("tags.Application", "Cloud Core");
        patchingProgressMap.put("tags.Environment", "COM");
        patchingProgressMap.put("Cloud Core", "abc");
        patchingProgressMap.put("sourceType", "webservice");
        patchingProgressMap.put("kernelVersion", "4.14.47-56.37.amzn1.x86_64");
        patchingProgressMap.put("description", "Kernel version is compliant");
        patchingProgressList.add(patchingProgressMap);
        
        Map<String, Object> closedInfoMap = new HashMap<>();
        closedInfoMap.put("sourceType", "webservice");
        closedInfoMap.put("kernelVersion", "44.14.47-56.37.amzn1.x86_64");
        closedInfoMap.put("description", "Kernel version is compliant");
        Map<String, Object> instanceInfoMap = new HashMap<>();
        List<Map<String, Object>> instanceInfoList = new ArrayList<>();
        instanceInfoMap.put("_resourceid", "_resourceid");
        instanceInfoMap.put("tags.Application", "Cloud Core");
        instanceInfoMap.put("reason-to-close", closedInfoMap);
        instanceInfoMap.put("tags.Environment", "env");
        instanceInfoMap.put("Cloud Core", "abc");
        instanceInfoMap.put("issueStatus", "open");
        instanceInfoMap.put("_resourceid", closedInfoMap);
        instanceInfoList.add(instanceInfoMap);
        
       when(filterRepository.getListOfTargetTypes(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getAssetCountByApps());
       
       when(projectionRepository.getAppsDetails(anyString()))
       .thenReturn(patchingProgressList);

       when(repository.getIssueInfo(anyString()))
       .thenReturn(patchingProgressList);
       
       
       when(repository.getInstanceInfo(anyString(),anyObject()))
       .thenReturn(instanceInfoList);
       
       when(repository.getClosedIssueInfo(anyString(),anyInt()))
       .thenReturn(instanceInfoList);
       
       when(repository.getOnpremIssueInfo(anyString()))
       .thenReturn(patchingProgressList);
       
       when(repository.getOnpremResourceInfo(anyString(),anyObject()))
       .thenReturn(patchingProgressList);
        
        assertThat(patchingServiceImpl.getPatchingDetails("anyString",new HashMap<String, String>()),
                is(notNullValue()));
    }
    
    @Test
    public void getStarRatingForAgPatchingTest() throws Exception{
    	
    	List<Map<String,Object>> patchingList1 = new ArrayList<>();
    	
    	Map<String,Object> patchingMap1 = new HashMap<String, Object>();
    	patchingMap1.put("date", "2018-03-31");
    	patchingMap1.put("ag", "aws-all");
    	patchingMap1.put("patching_percentage", "100");
    	
    	Map<String,Object> patchingMap2 = new HashMap<String, Object>();
    	patchingMap2.put("date", "2018-02-24");
    	patchingMap2.put("ag", "aws-all");
    	patchingMap2.put("patching_percentage", "100");
    	
    	Map<String,Object> patchingMap3 = new HashMap<String, Object>();
    	patchingMap3.put("date", "2018-01-28");
    	patchingMap3.put("ag", "aws-all");
    	patchingMap3.put("patching_percentage", "100");
    	
    	
    	patchingList1.add(patchingMap1);
    	patchingList1.add(patchingMap2);
    	patchingList1.add(patchingMap3);
    	
    	when(repository.getPatchingPercentForDateRange(anyString(), anyObject(), anyObject())).thenReturn(patchingList1);
        when(repository.getAmilAvailDate(anyInt(), anyInt())).thenReturn("2018-01-01");
		
    	Medal medal = patchingServiceImpl.getStarRatingForAgPatching("aws-all", 1, 2018);
    	
    	assertTrue(medal.getMedalType().equals("GOLD"));
    	
    	patchingMap3.put("patching_percentage", 80);
    	medal = patchingServiceImpl.getStarRatingForAgPatching("aws-all", 1, 2018);
    	assertTrue(medal.getMedalType().equals("SILVER"));
    	
    	patchingMap2.put("patching_percentage", 100);
    	patchingMap1.put("patching_percentage", 90);
    	medal = patchingServiceImpl.getStarRatingForAgPatching("aws-all", 1, 2018);
    	assertTrue(medal.getMedalType().equals("BRONZE"));
    	
    	patchingMap3.put("patching_percentage", 80);
    	patchingMap2.put("patching_percentage", 80);
    	patchingMap1.put("patching_percentage", 100);
    	medal = patchingServiceImpl.getStarRatingForAgPatching("aws-all", 1, 2018);
    	assertTrue(medal.getMedalType().equals("BRONZE"));
    	
    	patchingMap3.put("patching_percentage", 80);
    	patchingMap2.put("patching_percentage", 80);
    	patchingMap1.put("patching_percentage", 80);
    	medal = patchingServiceImpl.getStarRatingForAgPatching("aws-all", 1, 2018);
    	assertTrue(medal.getMedalType().equals(""));
    	assertTrue(medal.getMedalStatus().equals(""));
    
    	patchingMap3.put("patching_percentage", 100);
    	patchingMap3.put("date", "2018-08-10");
    	
    	patchingMap2.put("patching_percentage", 100);
    	patchingMap2.put("date", "2018-08-11");
    	
    	patchingMap1.put("patching_percentage", 100);
    	patchingMap1.put("date", "2018-08-12");
    	
    	medal = patchingServiceImpl.getStarRatingForAgPatching("aws-all", 3, 2018);
    	assertNotNull(medal.getMedalStatus());
    
    	
    }
   
}
