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
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.service.SearchService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class, Util.class })
public class SearchControllerTest {

    @Mock
    SearchService service;

    SearchController controller = new SearchController();

    @Test
    public void testSearch() throws Exception {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAg("ag");
        criteria.setSize(1);
        criteria.setFrom(0);
        criteria.setSearchText("pacman");

        SearchResult sr = new SearchResult();
        when(service.search(any(SearchCriteria.class))).thenReturn(sr);
        ReflectionTestUtils.setField(controller, "searchService", service);

        ResponseEntity<Object> responseObj = controller.search(criteria);
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);

        criteria.setSize(-1);
        responseObj = controller.search(criteria);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        criteria.setSize(1);
        criteria.setFrom(-1);
        responseObj = controller.search(criteria);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        criteria.setFrom(1);
        criteria.setSearchText(null);
        responseObj = controller.search(criteria);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        criteria.setSearchText("pacman");
        criteria.setAg(null);
        responseObj = controller.search(criteria);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

        criteria.setAg("aws-all");
        doThrow(new NullPointerException()).when(service).search(any(SearchCriteria.class));
        ReflectionTestUtils.setField(controller, "searchService", service);
        responseObj = controller.search(criteria);
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);

    }

    @Test
    public void testgetSearchCategories() throws Exception {
        when(service.getSearchCategories("domain")).thenReturn(Arrays.asList("cat1", "cat2"));
        ReflectionTestUtils.setField(controller, "searchService", service);
        ResponseEntity<Object> responseObj = controller.getSearchCategories("domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
        assertTrue(((List<String>) (((Map<String, Object>) responseObj.getBody()).get("data"))).size() == 2);
    }

}
