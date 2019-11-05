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
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.TrendRepository;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class IssueTrendServiceImplTest {

    @InjectMocks
    private IssueTrendServiceImpl issueTrendServiceImpl;

    @Mock
    private TrendRepository repository;

    @Mock
    private ComplianceRepository complainceRepository;
    
    @Mock
    private ComplianceService complianceService;
    
    Request request = new Request();
    
    UntaggedTargetTypeRequest untaggedTargetTypeRequest = new UntaggedTargetTypeRequest();

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTrendProgressTest() throws Exception {
        Map<String,Long> taggingInfoMap = new HashMap<>();
        taggingInfoMap.put("tagged", 100l);
        taggingInfoMap.put("untagged", 2114l);
        taggingInfoMap.put("assets", 2123l);
        taggingInfoMap.put("compliance", 21l);
        taggingInfoMap.put("certificates", 21l);
        
        
        taggingInfoMap.put("patched_instances", 100l);
        taggingInfoMap.put("unpatched_instances", 2114l);
        taggingInfoMap.put("total_instances", 2123l);
        taggingInfoMap.put("patching_percentage", 21l);
        
        taggingInfoMap.put("certificates_expiring", 1000l);
        Map<String, Object> vulnInfoMap = new HashMap<>();
        vulnInfoMap.put("hosts", "200");
        vulnInfoMap.put("totalVulnerableAssets", "2000");
        when(repository.getTrendProgress(anyString(), anyString(),anyObject(),anyObject(),anyString()))
                .thenReturn(CommonTestUtil.getListMapObject());
        
        when(complianceService.getTagging(anyString(), anyString()))
        .thenReturn(taggingInfoMap);
        
        when(complianceService.getCertificates(anyString()))
        .thenReturn(taggingInfoMap);
        
        when(complianceService.getRulecompliance(anyObject()))
        .thenReturn(CommonTestUtil.getResponseWithOrder());
        
        when(complianceService.getPatching(anyString(),anyString(),anyString()))
        .thenReturn(taggingInfoMap);

        when(complianceService.getOverallComplianceByDomain(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getMapObject());
        
        when(complianceService.getDistribution(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getMapObject());
        
        assertThat(issueTrendServiceImpl.getTrendProgress("ansString", "s3",LocalDate.now(),LocalDate.now(),"tagcompliance"),
                is(notNullValue()));

        assertThat(issueTrendServiceImpl.getTrendProgress("", "",LocalDate.now(),LocalDate.now(),"certcompliance"),
                is(notNullValue()));
        
        assertThat(issueTrendServiceImpl.getTrendProgress("", "",LocalDate.now(),LocalDate.now(),"vulncompliance"),
                is(notNullValue()));
        
        assertThat(issueTrendServiceImpl.getTrendProgress("", "",LocalDate.now(),LocalDate.now(),"issuecompliance"),
                is(notNullValue()));
        
        assertThat(issueTrendServiceImpl.getTrendProgress("", "",LocalDate.now(),LocalDate.now(),"patching"),
                is(notNullValue()));
        assertThat(issueTrendServiceImpl.getTrendProgress("", "",LocalDate.now(),LocalDate.now(),"compliance"),
                is(notNullValue()));
        
        assertThat(issueTrendServiceImpl.getTrendProgress("", "",LocalDate.now(),LocalDate.now(),"issues"),
                is(notNullValue()));
    }
   
    
    @Test
    public void getTrendIssuesTest() throws Exception {
        String targetTypes = "'ec2','s3','volume'";
        when(complainceRepository.getTargetTypeForAG(anyString(), anyString()))
        .thenReturn(targetTypes);
        
        when(complainceRepository.getRuleIdWithDisplayNameQuery(anyString()))
        .thenReturn(CommonTestUtil.getListMapObject());
        
        when(complianceService.getRuleSevCatDetails(anyObject()))
        .thenReturn(CommonTestUtil.getListMapObject());
        
        when(repository.getTrendIssues(anyString(),anyObject(),anyObject(),anyObject(),anyObject()))
        .thenReturn(CommonTestUtil.getListMapObject());
        
        when(complianceService.getDistribution(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getMapObject());
        
        assertThat(issueTrendServiceImpl.getTrendIssues("ansString", LocalDate.now(),LocalDate.now(),new HashMap<>(),"issues"),
                is(notNullValue()));

    }
    
    @Test
    public void getComplianceTrendProgressTest() throws Exception {
        String targetTypes = "'ec2','s3','volume'";
        when(complainceRepository.getTargetTypeForAG(anyString(), anyString()))
        .thenReturn(targetTypes);
        
        when(complainceRepository.getRuleIdWithDisplayNameQuery(anyString()))
        .thenReturn(CommonTestUtil.getListMapObject());
        
        when(complianceService.getRuleSevCatDetails(anyObject()))
        .thenReturn(CommonTestUtil.getListMapObject());
        
        when(repository.getComplianceTrendProgress(anyString(),anyObject(),anyString(),anyObject()))
        .thenReturn(CommonTestUtil.getListMapObject());
        
        when(complianceService.getOverallComplianceByDomain(anyString(),anyString()))
        .thenReturn(CommonTestUtil.getMapObject());
        
        assertThat(issueTrendServiceImpl.getComplianceTrendProgress("ansString", LocalDate.now(),"compliance"),
                is(notNullValue()));

    }
    
}
