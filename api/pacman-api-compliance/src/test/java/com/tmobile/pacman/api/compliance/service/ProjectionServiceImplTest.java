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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.PatchingRepository;
import com.tmobile.pacman.api.compliance.repository.ProjectionRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProjectionServiceImplTest {

    @InjectMocks
    private ProjectionServiceImpl projectionServiceImpl;

    @Mock
    private ElasticSearchRepository elasticSearchRepository;
    
    @Mock
    private ProjectionRepository repository;
    
    @Mock
    ProjectionRequest projectionRequest;
    
    @Mock
    private ComplianceRepository complainceRepository;
    
    @Mock
    private ComplianceService complianceService;
    
    @Mock
    private PatchingRepository patchingRepository;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void getProjectionTest() throws Exception {
        Map<String, Object> targetTypesMap = new HashMap<>();
        targetTypesMap.put("faqid", "q5");
        targetTypesMap.put("tag", "vulnerabilities");
        List<Map<String, Object>> maintargetTypesList = new ArrayList<>();
        maintargetTypesList.add(targetTypesMap);
        ReflectionTestUtils.setField(projectionServiceImpl, "projectionTargetTypes",
                "onpremserver");
        when(repository.getProjectionDetailsFromDb(anyString(), anyInt(),anyInt()))
                .thenReturn(maintargetTypesList);
        when(repository.getTotalAssetCountByTargetType(
                                anyString()))
                .thenReturn(5000l);

        assertThat(projectionServiceImpl.getProjection("onpremserver", 1,2),
                is(notNullValue()));

    }
    
    @Test
    public void getTotalAsseCountByTargetTypeTest() throws Exception {
        when(repository.getTotalAssetCountByTargetType(anyString()))
                .thenReturn(5000l);

        assertThat(projectionServiceImpl.getTotalAsseCountByTargetType("onpremserver"),
                is(notNullValue()));

    }

    @Test
    public void getPatchingAndProjectionByWeekTest() throws Exception {
        Map<String, Object> targetTypesMap = new HashMap<>();
        targetTypesMap.put("week", "1");
        targetTypesMap.put("projection", "500");
        List<Map<String, Object>> maintargetTypesList = new ArrayList<>();
        maintargetTypesList.add(targetTypesMap);
        
        List<LocalDate> lastDayOfEachWeek =new ArrayList<>();
        lastDayOfEachWeek.add(LocalDate.now());
        
        Map<Integer, Long> patchingSnapshotMap = new HashMap<>();
        patchingSnapshotMap.put(1, 0l);
        
        Map<String, Long> patchingMap = new HashMap<>();
        patchingMap.put("total_instances", 100l);
        ReflectionTestUtils.setField(projectionServiceImpl, "projectionAssetgroups",
                "onprem-vm");
        String targetTypes = "'onpremserver','ec2','s3','volume'";
        when(complainceRepository.getTargetTypeForAG(anyString(), anyString()))
                .thenReturn(targetTypes);
        
        when(complianceService.getPatching(anyString(),anyString(),anyString()))
                .thenReturn(patchingMap);
        
        when(repository.getProjectionDetailsFromDb(anyString(), anyInt(),anyInt()))
        .thenReturn(maintargetTypesList);
        
        when(repository.getPatchingSnapshot(anyString()))
        .thenReturn(patchingSnapshotMap);
        
        when(repository.getListOfLastWeekDateOfQuarter())
        .thenReturn(lastDayOfEachWeek);
        
        when(repository.getWeekNoByDate(anyObject()))
        .thenReturn(1);

        assertThat(projectionServiceImpl.getPatchingAndProjectionByWeek("onprem-vm"),
                is(notNullValue()));
        
        assertThatThrownBy(() -> projectionServiceImpl.getPatchingAndProjectionByWeek("dummy")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void updateProjectionTest() throws Exception {
        projectionRequest.setQuarter(2);
        projectionRequest.setResourceType("onpremserver");
        projectionRequest.setYear(2018);
        when(repository.updateProjectionByTargetType(anyObject()))
                .thenReturn(true);

        assertThat(projectionServiceImpl.updateProjection(projectionRequest),
                is(true));

    }
    
    @Test
    public void getPatchingProgressByDirectorTest() throws Exception {
        Map<String, Object> targetTypesMap = new HashMap<>();
        targetTypesMap.put("week", "1");
        targetTypesMap.put("projection", "500");
        targetTypesMap.put("appTag", "500");
        targetTypesMap.put("director", "abc");
        List<Map<String, Object>> maintargetTypesList = new ArrayList<>();
        maintargetTypesList.add(targetTypesMap);
        
        List<LocalDate> lastDayOfEachWeek =new ArrayList<>();
        lastDayOfEachWeek.add(LocalDate.now());
        
        Map<Integer, Long> patchingSnapshotMap = new HashMap<>();
        patchingSnapshotMap.put(1, 0l);
        
        Map<String, Long> assetMap = new HashMap<>();
        assetMap.put("total_instances", 100l);
        ReflectionTestUtils.setField(projectionServiceImpl, "projectionAssetgroups",
                "onprem-vm");
        String targetTypes = "'onpremserver','ec2','s3','volume'";
        when(complainceRepository.getTargetTypeForAG(anyString(), anyString()))
                .thenReturn(targetTypes);
        
        when(repository.getAssetDetailsByApplication(anyString(),anyString()))
                .thenReturn(assetMap);
        
        when(patchingRepository.getNonCompliantNumberForAgAndResourceType(anyString(), anyString()))
        .thenReturn(assetMap);
        
        when(repository.getAppsDetails(anyString()))
        .thenReturn(maintargetTypesList);
        
        when(complianceService.getPatching(anyString(),anyString(),anyString()))
        .thenReturn(assetMap);
        
        assertThat(projectionServiceImpl.getPatchingProgressByDirector("onprem-vm"),
                is(notNullValue()));
        
       assertThatThrownBy(() -> projectionServiceImpl.getPatchingProgressByDirector("dummy")).isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void patchProgByExSponsorTest() throws Exception {
        Map<String, Object> targetTypesMap = new HashMap<>();
        targetTypesMap.put("week", "1");
        targetTypesMap.put("projection", "500");
        targetTypesMap.put("appTag", "500");
        targetTypesMap.put("executiveSponsor", "abc");
        
        List<Map<String, Object>> maintargetTypesList = new ArrayList<>();
        maintargetTypesList.add(targetTypesMap);
        
        List<LocalDate> lastDayOfEachWeek =new ArrayList<>();
        lastDayOfEachWeek.add(LocalDate.now());
        
        Map<Integer, Long> patchingSnapshotMap = new HashMap<>();
        patchingSnapshotMap.put(1, 0l);
        
        Map<String, Long> assetMap = new HashMap<>();
        assetMap.put("total_instances", 100l);
        ReflectionTestUtils.setField(projectionServiceImpl, "projectionAssetgroups",
                "onprem-vm");
        String targetTypes = "'onpremserver','ec2','s3','volume'";
        when(complainceRepository.getTargetTypeForAG(anyString(), anyString()))
                .thenReturn(targetTypes);
        
        when(repository.getAssetDetailsByApplication(anyString(),anyString()))
                .thenReturn(assetMap);
        
        when(patchingRepository.getNonCompliantNumberForAgAndResourceType(anyString(), anyString()))
        .thenReturn(assetMap);
        
        when(repository.getAppsDetails(anyString()))
        .thenReturn(maintargetTypesList);
        
        when(complianceService.getPatching(anyString(),anyString(),anyString()))
        .thenReturn(assetMap);
        
        assertThat(projectionServiceImpl.patchProgByExSponsor("onprem-vm"),
                is(notNullValue()));
        
       assertThatThrownBy(() -> projectionServiceImpl.patchProgByExSponsor("dummy")).isInstanceOf(ServiceException.class);
    }
}
