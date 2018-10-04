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
package com.tmobile.pacman.api.asset.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.repository.SearchRepository;

/**
 * This class builds the second level inner filter for Omni Search
 */
public class RefineByBuilder implements Callable<Map<String, List<Map<String, Object>>>> {
    
    SearchRepository searchRepository;
    
    SearchCriteria criteria;
    
    String targetTypeName;
    
    String searchCategory;

    /**
     * Instantiates a new refine by builder.
     *
     * @param searchRepository the search repository
     * @param criteria the criteria
     * @param targetTypeName the target type name
     * @param searchCategory the search category
     */
    public RefineByBuilder(SearchRepository searchRepository, SearchCriteria criteria, String targetTypeName,
            String searchCategory) {
        super();
        this.searchRepository = searchRepository;
        this.criteria = criteria;
        this.targetTypeName = targetTypeName;
        this.searchCategory = searchCategory;
    }

    /* This is the thread runner call which is invoked by each instantiated thread
     */
    @Override
    public Map<String, List<Map<String, Object>>> call() throws Exception {
        return searchRepository.fetchDistributionForTargetType(criteria.getAg(), targetTypeName,
                criteria.getSearchText(), searchCategory, criteria.isIncludeAllAssets());
    }
}
