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

import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchException;
import com.tmobile.pacman.api.asset.domain.SearchResult;

/**
 * This is the main interface for omni search which contains methods to do the search and to return search categories
 */
public interface SearchService {
    
    /**
     * This is the main search method which looks at the incoming filter criteria and search category and queries ElasticSearch accordingly to get the results
     *
     * @param criteria the criteria
     * @return the search result
     * @throws SearchException the search exception
     */
    SearchResult search(SearchCriteria criteria) throws SearchException;

    /**
     * Gets the search categories such as Assets, etc. which apply to the particular domain.
     *
     * @param domain the domain
     * @return the search categories
     */
    List<String> getSearchCategories(String domain);

}
