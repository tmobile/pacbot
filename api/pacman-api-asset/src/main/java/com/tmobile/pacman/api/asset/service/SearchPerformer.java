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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchFilter;
import com.tmobile.pacman.api.asset.domain.SearchFilterAttribute;
import com.tmobile.pacman.api.asset.domain.SearchFilterAttributeGroup;
import com.tmobile.pacman.api.asset.domain.SearchFilterItem;
import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.repository.SearchRepository;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;

/**
 * This class performs the omni search results retrieval. This can be invoked in parallel in a multithreaded way.
 */
public class SearchPerformer implements Callable<SearchResult> {
    
    SearchRepository searchRepository;
    
    SearchCriteria criteria;
    
    SearchResult searchResult = new SearchResult();
    
    String highLevelMenuSelection;

    /**
     * Instantiates a new search performer.
     *
     * @param searchRepository the search repository
     * @param criteria the criteria
     * @param highLevelMenuSelection the high level menu selection
     */
    public SearchPerformer(SearchRepository searchRepository, SearchCriteria criteria, String highLevelMenuSelection) {
        super();
        this.searchRepository = searchRepository;
        this.criteria = criteria;
        this.highLevelMenuSelection = highLevelMenuSelection;
    }

    /** The thread runner method which is invoked for each thread instantiated
     */
    @Override
    public SearchResult call() throws DataException, ServiceException {
        String targetTypeMenuSelection = null;
        Map<String, List<String>> lowLevelFilters = new LinkedHashMap<>();

        if (null != criteria.getFilter()) {
                targetTypeMenuSelection = extractTargetTypeMenuSelectionAndLowLevelFilters(criteria.getFilter(),
                        lowLevelFilters);
        }

        return searchRepository.fetchSearchResultsAndSetTotal(criteria.getAg(), criteria.getDomain(),
                criteria.isIncludeAllAssets(), targetTypeMenuSelection, criteria.getSearchText(), lowLevelFilters,
                (int) criteria.getFrom(), (int) criteria.getSize(), searchResult, highLevelMenuSelection);
    }

    /**
     * Checks the incoming filter and extracts the required target types menu selections from it
     *
     * @param incomingFilter the incoming filter
     * @param lowLevelFilters the low level filters
     * @return the string
     * @throws ServiceException the service exception
     */
    private String extractTargetTypeMenuSelectionAndLowLevelFilters(SearchFilter incomingFilter,
            Map<String, List<String>> lowLevelFilters) throws ServiceException {

        List<String> targetTypeMenuSelection = new ArrayList<>();

        List<? extends SearchFilterItem> highLevelMenuGroupList = incomingFilter.getGroupBy().getValues();

        highLevelMenuGroupList
                .forEach(item -> {
                    SearchFilterAttributeGroup midLevelGroupBy = ((SearchFilterAttribute) item).getGroupBy();

                    List<? extends SearchFilterItem> midLevelMenuGroupList = new ArrayList<>();

                    if (null != midLevelGroupBy) {
                        midLevelMenuGroupList = ((SearchFilterAttribute) item).getGroupBy().getValues();
                    }

                    midLevelMenuGroupList
                            .forEach(targetTypeItem -> {
                                SearchFilterAttribute targetTypeAttr = (SearchFilterAttribute) targetTypeItem;
                                if (targetTypeAttr.isApplied()) {
                                    targetTypeMenuSelection.add(targetTypeAttr.getName());
                                    List<? extends SearchFilterItem> lowLevelMenuList = targetTypeAttr.getGroupBy()
                                            .getValues();
                                    lowLevelMenuList.forEach(lowLevelMenuHeading -> {
                                        if (lowLevelMenuHeading instanceof SearchFilterAttributeGroup) {

                                            String headingName = ((SearchFilterAttributeGroup) lowLevelMenuHeading)
                                                    .getName();

                                            List<? extends SearchFilterItem> filtersUnderTheHeading = ((SearchFilterAttributeGroup) lowLevelMenuHeading)
                                                    .getValues();

                                            filtersUnderTheHeading.forEach(filterItem -> {
                                                if (filterItem instanceof SearchFilterAttribute) {
                                                    String filterStr = ((SearchFilterAttribute) filterItem).getName();
                                                    if (((SearchFilterAttribute) filterItem).isApplied()) {
                                                        List<String> values = lowLevelFilters.get(headingName);
                                                        if (null == values) {
                                                            values = new ArrayList<>();
                                                        }
                                                        values.add(filterStr);
                                                        lowLevelFilters.put(headingName, values);
                                                    }
                                                }
                                            });

                                        }
                                    });

                                }
                            });

                });

        if (targetTypeMenuSelection.size() != 1) {
            return null;
        }
        return targetTypeMenuSelection.get(0);
    }
}
