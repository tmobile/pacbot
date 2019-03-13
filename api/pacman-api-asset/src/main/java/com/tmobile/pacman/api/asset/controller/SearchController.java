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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchException;
import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.service.SearchService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

/**
 * The main controller layer for Omni Search feature.
 */
@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class SearchController {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    /** The search service. */
    @Autowired
    SearchService searchService;

    /**
     * This is the entry point for the Omni Search call. This takes in the filter criteria and the search term which the user has entered from the *UI and proceeds with the search.
     *
     * @param criteria the criteria
     * @return the response entity
     */
    
    @PostMapping(value = "/v1/search")
    public ResponseEntity<Object> search(@RequestBody SearchCriteria criteria) {
        long start = System.currentTimeMillis();
        LOGGER.info("Start Search API.............");

        SearchResult searchResult = new SearchResult();
        if (criteria.getSize() <= 0) {
            return ResponseUtils.buildFailureResponse(new Exception("Size should be greater than zero."));
        }
        if (criteria.getFrom() < 0) {
            return ResponseUtils.buildFailureResponse(new Exception("From should be >= zero."));
        }
        if (null == criteria.getSearchText() || StringUtils.isEmpty(criteria.getSearchText())) {
            return ResponseUtils.buildFailureResponse(new Exception("Search Text cannot be blank."));
        }
        if (null == criteria.getAg() || StringUtils.isEmpty(criteria.getAg())) {
            return ResponseUtils.buildFailureResponse(new Exception("Asset Group cannot be blank."));
        }

        try {
            searchResult = searchService.search(criteria);
        } catch (SearchException e) {
            LOGGER.error("Error in search",e);
            return ResponseUtils.buildFailureResponse(new Exception(e.getMessage()));

        } catch (Exception e) {
            LOGGER.error("Error in search",e);
            return ResponseUtils.buildSucessResponse(searchResult);

        }
        long end = System.currentTimeMillis();
        LOGGER.info("..........End Search API. Took(in ms): {}" , (end - start));
        return ResponseUtils.buildSucessResponse(searchResult);

    }

    /**
     * This will be called by the UI layer to populate the search categories dropdown
     *
     * @param domain the domain
     * @return the search categories
     */
    
    @GetMapping(value = "/v1/search/categories")
    public ResponseEntity<Object> getSearchCategories(@RequestParam(name = "domain", required = false) String domain) {

        List<String> searchCategories = searchService.getSearchCategories(domain);

        return ResponseUtils.buildSucessResponse(searchCategories);

    }
}
