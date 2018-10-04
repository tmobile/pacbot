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
import com.tmobile.pacman.api.compliance.repository.FAQRepository;

@RunWith(MockitoJUnitRunner.class)
public class FAQServiceImplTest {

    @InjectMocks
    private FAQServiceImpl faqServiceImpl;

    @Mock
    private ElasticSearchRepository elasticSearchRepository;
    
    @Mock
    private FAQRepository repository;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void getFAQSByWidgetTest() throws Exception {
        Map<String, Object> targetTypesMap = new HashMap<>();
        targetTypesMap.put("faqid", "q5");
        targetTypesMap.put("tag", "vulnerabilities");
        List<Map<String, Object>> maintargetTypesList = new ArrayList<>();
        maintargetTypesList.add(targetTypesMap);
        when(repository.getFAQSFromEs(anyString(), anyString()))
                .thenReturn(maintargetTypesList);
        when(repository.getRelevantFAQSFromEs(
                                anyString(), anyString(), anyObject(),
                                anyObject(), anyObject()))
                .thenReturn(maintargetTypesList);

        assertThat(faqServiceImpl.getFAQSByWidget("aws-all", ""),
                is(notNullValue()));

    }


}
