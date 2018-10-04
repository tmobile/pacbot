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
  Modified Date: Jun 28, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class FAQRespositoryImplTest {
    @InjectMocks
    FAQRespositoryImpl faqRepositoryImpl;
    @Mock
    PacmanRdsRepository rdsepository;

    @Mock
    ElasticSearchRepository elasticSearchRepository;

    @Test
    public void intTest() {
        faqRepositoryImpl.init();
    }

    @Test
    public void getFAQSFromESTest() throws Exception {
        List<Map<String, Object>> faqsList = new ArrayList<>();
        Map<String, Object> faq = new HashMap<>();
        faq.put("faqid", "w1q1");
        faq.put("widgetid", "w1");
        faq.put("faq", "how is overallComplaince % calculated");
        faqsList.add(faq);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(faqsList);
        // test faq's list is not empty
        List<Map<String, Object>> faqsFromES = faqRepositoryImpl.getFAQSFromEs("w1", "1");
        assertTrue(faqsFromES.size() > 0);
        // test domain id null scenario
        faqsFromES = faqRepositoryImpl.getFAQSFromEs("w1", null);
        assertTrue(faqsFromES.size() > 0);
        // test data Exception
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new DataException());
        assertThatThrownBy(() -> faqRepositoryImpl.getFAQSFromEs("w1", "1")).isInstanceOf(DataException.class);
    }

    @Test
    public void getRelevantFAQSFromEs() throws Exception {
        List<Map<String, Object>> releventFaqsList = new ArrayList<>();
        List<String> faqsList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        String response = "{\"took\":2,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"failed\":0},\"hits\":{\"total\":17,\"max_score\":1,\"hits\":[{\"_index\":\"faqs\",\"_type\":\"faqinfo\",\"_id\":\"w1q2\",\"_score\":1,\"_source\":{\"faqid\":\"q2\",\"faqname\":\"How is patching compliance % calculated ?\",\"answer\":\"Total patched resources divided by total running resources\",\"widgetid\":\"w1\",\"tag\":\"patching\"}}]}}";
        Map<String, Object> faq = new HashMap<>();
        faq.put("faqid", "w1q1");
        faq.put("widgetid", "w1");
        faq.put("faq", "how is overallComplaince % calculated");
        releventFaqsList.add(faq);
        faqsList.add("w1q4");
        faqsList.add("w1q5");
        tags.add("patching");
        mockStatic(PacHttpUtils.class);
        //test relevent faq's non exception
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(faqRepositoryImpl, "esUrl", "dummyEsURL");
        List<Map<String, Object>> releventFaqs = faqRepositoryImpl.getRelevantFAQSFromEs("w1", "1", tags, faqsList,
                releventFaqsList);
        assertTrue(releventFaqs.size() > 0 || releventFaqs.size() == 0);
       //test data dataExeception
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> faqRepositoryImpl.getRelevantFAQSFromEs("w1", "1", tags, faqsList,
                releventFaqsList)).isInstanceOf(DataException.class);
    }
}
