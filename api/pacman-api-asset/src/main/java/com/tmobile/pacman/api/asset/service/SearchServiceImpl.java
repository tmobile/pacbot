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
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchException;
import com.tmobile.pacman.api.asset.domain.SearchFilter;
import com.tmobile.pacman.api.asset.domain.SearchFilterAttribute;
import com.tmobile.pacman.api.asset.domain.SearchFilterAttributeGroup;
import com.tmobile.pacman.api.asset.domain.SearchFilterItem;
import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.repository.SearchRepository;
import com.tmobile.pacman.api.commons.Constants;

/**
 * Implemented class for SearchService and all its method
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    SearchRepository searchRepository;

    @Override
    public SearchResult search(SearchCriteria criteria) throws SearchException {

        SearchFilter incomingFilter = criteria.getFilter();

        String highLevelMenuSelection;

        if (null != incomingFilter) {
            highLevelMenuSelection = extractHighLevelMenuSelection(incomingFilter);
        } else {
            // Just to start with
            highLevelMenuSelection = AssetConstants.ASSETS;
        }

        if (!getSearchCategories(criteria.getDomain()).contains(highLevelMenuSelection)) {
            throw new SearchException("Invalid high level menu selection. Valid ones are:"
                    + getSearchCategories(criteria.getDomain()).toString());
        }

        ExecutorService executors = Executors.newFixedThreadPool(Constants.TWO);
        List<SearchResult> candidateResult = new ArrayList<>();
        List<String> candidateSearchCategory = new ArrayList<>();

        executors.execute(() -> {
            long start = System.currentTimeMillis();
            LOGGER.debug("Start Getting Search Results...........");

            int threadSize = 1;
            List<String> listOfSearchCategories = Arrays.asList(highLevelMenuSelection);

            if (null == incomingFilter) {
                threadSize = getSearchCategories(criteria.getDomain()).size();
                listOfSearchCategories = getSearchCategories(criteria.getDomain());
            }

            ExecutorService parallelSearchCategoryExecutor = Executors.newFixedThreadPool(threadSize);
            Map<String, Future<SearchResult>> submissionResults = new LinkedHashMap<>();

            listOfSearchCategories.forEach(searchCategory -> {
                Callable<SearchResult> worker = new SearchPerformer(searchRepository, criteria, searchCategory);
                Future<SearchResult> submission = parallelSearchCategoryExecutor.submit(worker);
                submissionResults.put(searchCategory, submission);
            });
            LOGGER.info("All search results threads have been submitted");
            long submissionTime = System.currentTimeMillis();

            parallelSearchCategoryExecutor.shutdown();
            while (!parallelSearchCategoryExecutor.isTerminated()) {

            }

            LOGGER.info("All search results threads have completed. It took: {}"
                    , (System.currentTimeMillis() - submissionTime));

            submissionResults.forEach((searchCategory, future) -> {
                SearchResult nextInLineSearchResult = null;
                try {
                    nextInLineSearchResult = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error(AssetConstants.ERROR_SEARCH , e);
                }
                if (candidateResult.isEmpty() && !nextInLineSearchResult.getResults().isEmpty()) {
                    candidateResult.add(nextInLineSearchResult);
                    candidateSearchCategory.add(searchCategory);
                }
            });

            long end = System.currentTimeMillis();
            LOGGER.debug(".....End. Search Results took(in ms): {}" , (end - start));
        });

        boolean doNotReturnFilter = criteria.isDoNotReturnFilter();

        SearchFilter outgoingFilter = new SearchFilter();
        if (!doNotReturnFilter) {
            executors.execute(() -> {
                long start = System.currentTimeMillis();
                LOGGER.debug("Start Filter population...........");
                try {
                    populateOutgoingFilter(criteria, outgoingFilter);
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_SEARCH , e);
                }
                long end = System.currentTimeMillis();
                LOGGER.debug(".....End. Filter population took(in ms): {}" , (end - start));
                if (null != incomingFilter) {
                    copyAppliedFiltersToOutgoing(incomingFilter.getGroupBy(), outgoingFilter.getGroupBy());
                }
            });
        }
        LOGGER.info("The two main threads -search and filter - have been submitted");
        long submissionTime = System.currentTimeMillis();

        executors.shutdown();
        while (!executors.isTerminated()) {

        }
        LOGGER.info("Both the main threads - search and filter - have completed. It took: {}"
                , (System.currentTimeMillis() - submissionTime));
        if (candidateResult.isEmpty()) {
            // None of the search categories yielded results.
            return null;
        }

        candidateResult.get(0).setFilter(outgoingFilter);

        return candidateResult.get(0);
    }

    private void copyAppliedFiltersToOutgoing(SearchFilterAttributeGroup incomingGroupBy,
            SearchFilterAttributeGroup outgoingGroupBy) {
        if (null == incomingGroupBy || null == outgoingGroupBy) {
            return;
        }

        List<? extends SearchFilterItem> incomingAttList = incomingGroupBy.getValues();
        List<? extends SearchFilterItem> outgoingAttList = outgoingGroupBy.getValues();

        incomingAttList.forEach(incomingAtt -> {
            if (incomingAtt instanceof SearchFilterAttributeGroup) {
                SearchFilterAttributeGroup incomingSearchGroup = (SearchFilterAttributeGroup) incomingAtt;
                String incomingSearchGroupName = incomingSearchGroup.getName();
                outgoingAttList.forEach(outgoingAtt -> {
                    SearchFilterAttributeGroup outgoingSearchGroup = (SearchFilterAttributeGroup) outgoingAtt;
                    if (((SearchFilterAttributeGroup) outgoingAtt).getName().equals(incomingSearchGroupName)) {
                        copyAppliedFiltersToOutgoing(incomingSearchGroup, outgoingSearchGroup);
                    }
                });

            }
            if (incomingAtt instanceof SearchFilterAttribute) {
                boolean incomingApplied = ((SearchFilterAttribute) incomingAtt).isApplied();
                String incomingName = ((SearchFilterAttribute) incomingAtt).getName();
                SearchFilterAttributeGroup innerIncomingGroupBy = ((SearchFilterAttribute) incomingAtt).getGroupBy();

                if (incomingApplied) {
                    outgoingAttList.forEach(outgoingAtt -> {
                        if (outgoingAtt instanceof SearchFilterAttribute && ((SearchFilterAttribute) outgoingAtt).getName().equals(incomingName)) {
                                ((SearchFilterAttribute) outgoingAtt).setApplied(true);
                                SearchFilterAttributeGroup innerOutgoingGroupBy = ((SearchFilterAttribute) outgoingAtt)
                                        .getGroupBy();
                                // Recursive call
                            if (null != innerIncomingGroupBy && null != innerOutgoingGroupBy) {
                                copyAppliedFiltersToOutgoing(innerIncomingGroupBy, innerOutgoingGroupBy);
                            }
                        }
                })  ;
                }
            }
        });
    }

    private SearchFilter populateOutgoingFilter(SearchCriteria criteria, SearchFilter outgoingFilter) throws SearchException {
        SearchFilterAttributeGroup rootGroup = new SearchFilterAttributeGroup();
        outgoingFilter.setGroupBy(rootGroup);

        List<SearchFilterAttribute> highLevelAttributesList = new ArrayList<>();
        rootGroup.setName("Group");
        rootGroup.setValues(highLevelAttributesList);

        ExecutorService executor = Executors.newFixedThreadPool(Constants.TEN);
        Map<String, Future<SearchFilterAttribute>> submissionResults = new LinkedHashMap<>();

        if (null == criteria.getFilter()) {

            Callable<SearchFilterAttribute> worker1 = new SearchCategorySubListBuilder(searchRepository, criteria,
                    AssetConstants.ASSETS, false);
            Future<SearchFilterAttribute> submission1 = executor.submit(worker1);
            submissionResults.put(AssetConstants.ASSETS, submission1);

            Callable<SearchFilterAttribute> worker2 = new SearchCategorySubListBuilder(searchRepository, criteria,
                    AssetConstants.POLICY_VIOLATIONS, false);
            Future<SearchFilterAttribute> submission2 = executor.submit(worker2);
            submissionResults.put(AssetConstants.POLICY_VIOLATIONS, submission2);

            Callable<SearchFilterAttribute> worker3 = new SearchCategorySubListBuilder(searchRepository, criteria,
                    AssetConstants.VULNERABILITIES, false);
            Future<SearchFilterAttribute> submission3 = executor.submit(worker3);
            submissionResults.put(AssetConstants.VULNERABILITIES, submission3);

        } else {
            String highLevelMenuSelection = extractHighLevelMenuSelection(criteria.getFilter());
            Callable<SearchFilterAttribute> worker = new SearchCategorySubListBuilder(searchRepository, criteria,
                    highLevelMenuSelection, true);
            Future<SearchFilterAttribute> submission = executor.submit(worker);
            submissionResults.put(highLevelMenuSelection, submission);
        }

        LOGGER.info("The filter threads have been submitted");
        long submissionTime = System.currentTimeMillis();
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        LOGGER.info("All the filter threads have completed. It took: {}" , (System.currentTimeMillis() - submissionTime));

        submissionResults.forEach((searchCategory, future) -> {
            try {
                highLevelAttributesList.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(AssetConstants.ERROR_SEARCH , e);
            }
        });
        boolean menuDecisionMade = false;
        for (SearchFilterAttribute menuItem : highLevelAttributesList) {
            if (menuItem.getCount() > 0 && !menuDecisionMade) {
                menuItem.setApplied(true);
                menuDecisionMade = true;
            }
        }
        return outgoingFilter;
    }

    private String extractHighLevelMenuSelection(SearchFilter incomingFilter) throws SearchException {
        List<String> highLevelMenuSelection = new ArrayList<>();

        List<? extends SearchFilterItem> highLevelMenuGroupList = incomingFilter.getGroupBy().getValues();

        highLevelMenuGroupList.forEach(item -> {

            if (item instanceof SearchFilterAttribute) {
                SearchFilterAttribute attr = (SearchFilterAttribute) item;
                if (attr.isApplied()) {
                    highLevelMenuSelection.add(attr.getName());
                }
            }

        });

        if (highLevelMenuSelection.size() != 1) {
            throw new SearchException("One and only one high level menu selection is permitted.");
        }
        return highLevelMenuSelection.get(0);
    }

    @Override
    public List<String> getSearchCategories(String domain) {
        if (null != domain && domain.contains("Infra")) {
            return Arrays.asList(AssetConstants.ASSETS, AssetConstants.POLICY_VIOLATIONS,
                    AssetConstants.VULNERABILITIES);
        }
        return Arrays.asList(AssetConstants.ASSETS, AssetConstants.POLICY_VIOLATIONS);
    }

}
