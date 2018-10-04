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
package com.tmobile.pacman.api.asset.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * This is the Search Repository layer which makes call to RDS DB as well as ElasticSearch
 */
@Repository
public interface SearchRepository {

    /**
     * Get the fields that apply to the incoming resourceType
     *
     * @param incomingResourceType the incoming resource type
     * @param flipOrder the flip order
     * @param searchCategory the search category
     * @return the field mappings for search
     */
    Map<String, String> getFieldMappingsForSearch(String incomingResourceType, boolean flipOrder, String searchCategory);

    /**
     * Gets the fields which need to be shown back to the user in the results set
     *
     * @param incomingResourceType the incoming resource type
     * @param searchCategory the search category
     * @return the return fields for search
     */
    List<String> getReturnFieldsForSearch(String incomingResourceType, String searchCategory);

    /**
     * Get the distribution of assets, policy violation etc. segregated by targetType
     *
     * @param ag the ag
     * @param resourceType the resource type
     * @param searchText the search text
     * @param searchCategory the search category
     * @param includeAllAssets the include all assets
     * @return the map
     */
    Map<String, List<Map<String, Object>>> fetchDistributionForTargetType(String ag, String resourceType,
            String searchText, String searchCategory, boolean includeAllAssets);

    /**
     * Fetch all targetTypes such as ec2, s3 etc...
     *
     * @param ag the ag
     * @param searchText the search text
     * @param searchCategory the search category
     * @param domain the domain
     * @param includeAllAssets the include all assets
     * @return the list
     * @throws DataException the data exception
     */
    List<Map<String, Object>> fetchTargetTypes(String ag, String searchText, String searchCategory, String domain,
            boolean includeAllAssets) throws DataException;

    /**
     * Queries elastic search and returns the results for the particular search term and filters
     *
     * @param ag the ag
     * @param domain the domain
     * @param includeAllAssets the include all assets
     * @param targetType the target type
     * @param searchText the search text
     * @param lowLevelFilters the low level filters
     * @param from the from
     * @param size the size
     * @param result the result
     * @param searchCategory the search category
     * @return the search result
     * @throws DataException the data exception
     */
    SearchResult fetchSearchResultsAndSetTotal(String ag, String domain, boolean includeAllAssets, String targetType,
            String searchText, Map<String, List<String>> lowLevelFilters, int from, int size, SearchResult result,
            String searchCategory) throws DataException;

}
