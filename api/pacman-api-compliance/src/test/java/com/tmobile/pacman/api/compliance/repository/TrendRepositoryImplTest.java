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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :santoshi
  Modified Date: Jul 5, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class TrendRepositoryImplTest {
    @Mock
    ElasticSearchRepository elasticSearchRepository;
    @InjectMocks
    private TrendRepositoryImpl trendRepositoryImpl;

    @Test
    public void getComplianceTrendProgressTest() throws Exception {
        List<Map<String, Object>> compliantTrendProgress = new ArrayList<>();
        Map<String, Object> complianceTrend = new HashMap<>();
        complianceTrend.put("date", 01 - 10 - 2018);
        complianceTrend.put("compliance", 10);
        compliantTrendProgress.add(complianceTrend);
        complianceTrend.put("date", 02 - 10 - 2018);
        complianceTrend.put("compliance", 35);
        compliantTrendProgress.add(complianceTrend);
        Set<String> ruleCat = new HashSet<>();
        ruleCat.add("security");
        ruleCat.add("governance");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(compliantTrendProgress);
        trendRepositoryImpl.getComplianceTrendProgress("aws-all", LocalDate.now(), "test", ruleCat);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> trendRepositoryImpl.getComplianceTrendProgress("aws-all", LocalDate.now(), "test", ruleCat))
                .isInstanceOf(DataException.class);

    }

    @Test
    public void getTrendProgressTest() throws Exception {
        List<Map<String, Object>> issueTrendProgress = new ArrayList<>();
        Map<String, Object> issueTrend = new HashMap<>();
        issueTrend.put("date", 01 - 10 - 2018);
        issueTrend.put("total", 1000);
        issueTrend.put("compliant", 700);
        issueTrend.put("non-compliant", 300);
        issueTrend.put("compliance_percent", 70);
        issueTrendProgress.add(issueTrend);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueTrendProgress);
        trendRepositoryImpl.getTrendProgress("aws-all", "tagging-rule", LocalDate.now(), LocalDate.now(),
                "issuecompliance");
        trendRepositoryImpl.getTrendProgress("aws-all", "tagging-rule", LocalDate.now(), LocalDate.now(),
                "othercategory");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> trendRepositoryImpl.getTrendProgress("aws-all", "tagging-rule", LocalDate.now(), LocalDate.now(),
                        "issuecompliance")).isInstanceOf(DataException.class);

    }

    @Test
    public void getTrendIssuesTest() throws Exception {
        List<Map<String, Object>> issueTrend = new ArrayList<>();
        Map<String, Object> issueTrendSev = new HashMap<>();
        issueTrendSev.put("date", 01 - 10 - 2018);
        issueTrendSev.put("total", 1000);
        issueTrendSev.put("Critical", 50);
        issueTrendSev.put("High", 300);
        issueTrendSev.put("Medium", 200);
        issueTrendSev.put("Low", 450);
        issueTrend.add(issueTrendSev);
        Set<String> ruleSeverity = new HashSet<>();
        ruleSeverity.add("Critical");
        ruleSeverity.add("High");
        ruleSeverity.add("Medium");
        ruleSeverity.add("Low");
        Map<String, String> filter = new HashMap<>();
        filter.put("domain", "Infra & Platform");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueTrend);
        trendRepositoryImpl.getTrendIssues("aws-all", LocalDate.now(), LocalDate.now(), filter, ruleSeverity);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> trendRepositoryImpl.getTrendIssues("aws-all", LocalDate.now(), LocalDate.now(), filter,
                        ruleSeverity)).isInstanceOf(DataException.class);
    }
}
