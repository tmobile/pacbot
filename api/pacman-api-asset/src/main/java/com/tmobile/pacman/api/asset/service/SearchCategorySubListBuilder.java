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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchFilterAttribute;
import com.tmobile.pacman.api.asset.domain.SearchFilterAttributeGroup;
import com.tmobile.pacman.api.asset.domain.SearchFilterItem;
import com.tmobile.pacman.api.asset.repository.SearchRepository;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * This class builds the different layers of filter with lists and sublists following a tree structure
 */
public class SearchCategorySubListBuilder implements Callable<SearchFilterAttribute> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCategorySubListBuilder.class);

    SearchRepository searchRepository;
    
    SearchCriteria criteria;
    
    String searchCategory;
    
    boolean makeDefaultSelected;

    /**
     * Instantiates a new search category sub list builder.
     *
     * @param searchRepository the search repository
     * @param criteria the criteria
     * @param searchCategory the search category
     * @param makeDefaultSelected the make default selected
     */
    public SearchCategorySubListBuilder(SearchRepository searchRepository, SearchCriteria criteria,
            String searchCategory, boolean makeDefaultSelected) {
        super();
        this.searchRepository = searchRepository;
        this.criteria = criteria;
        this.searchCategory = searchCategory;
        this.makeDefaultSelected = makeDefaultSelected;
    }

     /* This is the thread runner call which is invoked by each instantiated thread
     */
    @Override
    public SearchFilterAttribute call() throws DataException {

        SearchFilterAttribute menuItem = new SearchFilterAttribute();

        menuItem.setName(searchCategory);
        if (makeDefaultSelected) {
            menuItem.setApplied(true);
        }

        SearchFilterAttributeGroup menuGroup = new SearchFilterAttributeGroup();
        menuItem.setGroupBy(menuGroup);

        menuGroup.setName(searchCategory.toUpperCase());
        List<SearchFilterAttribute> targetTypeMenuItemsList = new ArrayList<>();
        menuGroup.setValues(targetTypeMenuItemsList);
        List<Map<String, Object>> targetTypeListFromES;
        try {
            targetTypeListFromES = searchRepository.fetchTargetTypes(criteria.getAg(),
                    criteria.getSearchText(), searchCategory, criteria.getDomain(), criteria.isIncludeAllAssets());
        } catch (DataException e) {
            throw new DataException(e);
        }

        populateTargetTypeMenuItemsInList(targetTypeMenuItemsList, targetTypeListFromES, criteria, searchCategory);
        menuItem.setCount(rollUpTotals(menuItem.getGroupBy()));
        return menuItem;
    }

    /**
     * Populates the first level target type menu items such as ec2, s3 etc.
     *
     * @param targetTypeMenuItemsList the target type menu items list
     * @param targetTypeListFromES the target type list from ES
     * @param criteria the criteria
     * @param searchCategory the search category
     */
    private void populateTargetTypeMenuItemsInList(List<SearchFilterAttribute> targetTypeMenuItemsList,
            List<Map<String, Object>> targetTypeListFromES, SearchCriteria criteria, String searchCategory) {
        if (targetTypeListFromES.isEmpty()) {
            return;
        }

        int threadCount = targetTypeListFromES.size() > AssetConstants.THIRTY ? AssetConstants.THIRTY
                : targetTypeListFromES.size();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Map<String, Future<Map<String, List<Map<String, Object>>>>> submissionResults = new LinkedHashMap<>();

        targetTypeListFromES.forEach(targetTypeEntityMapFromES -> {
            String targetTypeName = targetTypeEntityMapFromES.get(AssetConstants.FIELDNAME).toString();

            Callable<Map<String, List<Map<String, Object>>>> worker = new RefineByBuilder(searchRepository, criteria,
                    targetTypeName, searchCategory);

            Future<Map<String, List<Map<String, Object>>>> submission = executor.submit(worker);

            submissionResults.put(targetTypeName, submission);

        });

        LOGGER.info("The refine by threads for all targetypes in {} have been submitted",searchCategory);
        long submissionTime = System.currentTimeMillis();
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        LOGGER.info("The refine by threads for all targettypes in {} have completed. It took: ",searchCategory
                , (System.currentTimeMillis() - submissionTime));

        targetTypeListFromES.forEach(targetTypeEntityMapFromES -> {
            String targetTypeName = targetTypeEntityMapFromES.get(AssetConstants.FIELDNAME).toString();
            Long count = (Long) targetTypeEntityMapFromES.get("count");
            SearchFilterAttribute targetTypeMenuItem = new SearchFilterAttribute();
            targetTypeMenuItem.setName(targetTypeName);
            targetTypeMenuItem.setCount(count);
            targetTypeMenuItemsList.add(targetTypeMenuItem);

            SearchFilterAttributeGroup refinementMenuGroup = new SearchFilterAttributeGroup();
            targetTypeMenuItem.setGroupBy(refinementMenuGroup);

            refinementMenuGroup.setName("REFINE BY");
            List<SearchFilterAttributeGroup> refinementMenuSubSections = new ArrayList<>();
            refinementMenuGroup.setValues(refinementMenuSubSections);

            try {
                populateRefinementGroupSubSections(refinementMenuSubSections, submissionResults.get(targetTypeName)
                        .get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error in populateTargetTypeMenuItemsInList" , e);
            }

        });

    }

    /**
     * Populate second level filter which is a further refinement on top of targetTypes
     *
     * @param refinementMenuSubSections the refinement menu sub sections
     * @param refinementDataFromES the refinement data from ES
     * @param criteria the criteria
     */
    private void populateRefinementGroupSubSections(List<SearchFilterAttributeGroup> refinementMenuSubSections,
            Map<String, List<Map<String, Object>>> refinementDataFromES) {
        refinementDataFromES.forEach((subSectionHeading, subSectionAttributeList) -> {
            SearchFilterAttributeGroup subSectionOfRefinementMenu = new SearchFilterAttributeGroup();
            subSectionOfRefinementMenu.setName(subSectionHeading);

            List<SearchFilterAttribute> leafAttributeList = new ArrayList<>();
            subSectionOfRefinementMenu.setValues(leafAttributeList);

            subSectionOfRefinementMenu.setValues(leafAttributeList);

            subSectionAttributeList.forEach(subSectionAttribute -> {
                SearchFilterAttribute leafAttribute = new SearchFilterAttribute();
                subSectionAttribute.forEach((key, value) -> {
                    if (key.equals(AssetConstants.FIELDNAME)) {
                        leafAttribute.setName(value.toString());
                    }
                    if (key.equals(Constants.COUNT)) {
                        leafAttribute.setCount((Long) value);
                    }

                });
                leafAttributeList.add(leafAttribute);
            });

            refinementMenuSubSections.add(subSectionOfRefinementMenu);
        });
    }

    /**
     * Roll up total counts from each sublist to show in the parent list
     *
     * @param groupBy the group by
     * @return the long
     */
    private long rollUpTotals(SearchFilterAttributeGroup groupBy) {
        long totalCount = 0;
        for (SearchFilterItem searchFilterItem : groupBy.getValues()) {
            if (searchFilterItem instanceof SearchFilterAttribute) {
                totalCount = totalCount + ((SearchFilterAttribute) searchFilterItem).getCount();
            }

        }
        return totalCount;
    }
}
