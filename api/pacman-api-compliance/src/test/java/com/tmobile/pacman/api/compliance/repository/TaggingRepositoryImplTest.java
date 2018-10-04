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
  Modified Date: Jun 27, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.JsonArray;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class TaggingRepositoryImplTest {
    @InjectMocks
    private TaggingRepositoryImpl taggingRepositoryImpl;
    @Mock
    private ElasticSearchRepository elasticSearchRepository;
    @Mock
    RestClient restClient;
    @Mock
    StatusLine sl;
    @Mock
    PacmanRdsRepository rdsepository;
    @Mock
    Response response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUntaggedIssuesTest() throws Exception {
        long untaggedAssets;
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndTypeWithMatchPhrase(anyString(), anyString(),
                        anyObject(), anyObject(), anyObject(), anyString(), anyObject(), anyObject()))
                .thenReturn(5000l);
        untaggedAssets = taggingRepositoryImpl.getUntaggedIssues("aws-all", "Application,Environmnet");
        assertTrue(untaggedAssets > 0);
        // check mandatory tags empty scenario
        untaggedAssets = taggingRepositoryImpl.getUntaggedIssues("aws-all", null);
        assertTrue(untaggedAssets > 0);
        // check throws DataException
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndTypeWithMatchPhrase(anyString(), anyString(),
                        anyObject(), anyObject(), anyObject(), anyString(), anyObject(), anyObject())).thenThrow(
                new DataException());
        assertThatThrownBy(() -> taggingRepositoryImpl.getUntaggedIssues("aws-all", "Application,Environmnet"))
                .isInstanceOf(DataException.class);
        taggingRepositoryImpl.init();
    }

      @Test
    public void getTaggingByApplicationTest() throws Exception {
        String response = "{\"count\":239,\"_shards\":{\"total\":176,\"successful\":176,\"skipped\":0,\"failed\":0}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(taggingRepositoryImpl, "esUrl", "dummyEsURL");
        assertThat(taggingRepositoryImpl.getTaggingByApplication("aws-all", "ec2"), is(notNullValue()));
        //targetType null scenario
        assertThat(taggingRepositoryImpl.getTaggingByApplication("aws-all", null), is(notNullValue()));
        //Throws Data Exception
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> taggingRepositoryImpl.getTaggingByApplication("aws-all", "ec2"))
        .isInstanceOf(DataException.class);
    }

    @Test
    public void getUntaggedTargetTypeIssuesTest() throws Exception {
        String response = "{\"count\":239,\"_shards\":{\"total\":176,\"successful\":176,\"skipped\":0,\"failed\":0}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(taggingRepositoryImpl, "esUrl", "dummyEsURL");
        UntaggedTargetTypeRequest request = new UntaggedTargetTypeRequest();
        request.setAg("aws-all");
        List<String> tagList = new ArrayList<>();
        tagList.add("tag1");
        tagList.add("tag2");
        assertThat(taggingRepositoryImpl.getUntaggedTargetTypeIssues(request, tagList), is(notNullValue()));
        tagList = new ArrayList<>();
        // check Tags list empty scenario
        assertThat(taggingRepositoryImpl.getUntaggedTargetTypeIssues(request, tagList), is(notNullValue()));
        // check throws DataException
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        List<String> tagListfinal = new ArrayList<>();
        assertThatThrownBy(() -> taggingRepositoryImpl.getUntaggedTargetTypeIssues(request, tagListfinal))
                .isInstanceOf(DataException.class);

    }

    @Test
    public void getUntaggedIssuesByapplicationFromESTest() throws Exception {
        JsonArray untaggedIsuueByApp;
        String response = "{\"took\":1660,\"timed_out\":false,\"_shards\":{\"total\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":58839,\"max_score\":0.0,\"hits\":[]},\"aggregations\":{\"apps\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"My-TMO\",\"doc_count\":3062,\"tags\":{\"buckets\":{\"Environment\":{\"doc_count\":154},\"Role\":{\"doc_count\":677},\"Stack\":{\"doc_count\":3052}}}},{\"key\":\"Layer3\",\"doc_count\":1897,\"tags\":{\"buckets\":{\"Environment\":{\"doc_count\":1784},\"Role\":{\"doc_count\":1896},\"Stack\":{\"doc_count\":1263}}}}]}}}";
        mockStatic(PacHttpUtils.class);
        String mandatoryTags = "Application,env";
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(taggingRepositoryImpl, "esUrl", "dummyEsURL");
        untaggedIsuueByApp = taggingRepositoryImpl.getUntaggedIssuesByapplicationFromES("aws-all", mandatoryTags,
                "rebellion", 1, 10);
        assertTrue(untaggedIsuueByApp.size() > 0);
        // check searchText null scenario
        untaggedIsuueByApp = taggingRepositoryImpl.getUntaggedIssuesByapplicationFromES("aws-all", mandatoryTags, null,
                1, 10);
        assertTrue(untaggedIsuueByApp.size() > 0);
        // check throws DataException
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(
                () -> taggingRepositoryImpl.getUntaggedIssuesByapplicationFromES("aws-all", mandatoryTags, null, 1, 10))
                .isInstanceOf(DataException.class);
    }

    @Test
    public void getRuleParamsFromDbByPolicyId() throws Exception {
        List<Map<String, Object>> ruleParams = new ArrayList<>();
        List<Map<String, Object>> response;
        Map<String, Object> ruleParamMap = new HashMap<>();
        ruleParamMap.put("assetGroup", "aws");
        ruleParamMap.put("targetType", "ec2");
        ruleParamMap.put("policyId", "PacMan_TaggingRule_version-1");
        ruleParams.add(ruleParamMap);
        when(rdsepository.getDataFromPacman(anyString())).thenReturn(ruleParams);
        response = taggingRepositoryImpl.getRuleParamsFromDbByPolicyId("PacMan_TaggingRule_version-1");
        assertTrue(response.size() > 0);
        when(rdsepository.getDataFromPacman(anyString())).thenReturn(ruleParams);
        response = taggingRepositoryImpl.getRuleTargetTypesFromDbByPolicyId("PacMan_TaggingRule_version-1");
        assertTrue(response.size() > 0);
    }

}
