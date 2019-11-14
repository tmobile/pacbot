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

package com.tmobile.pacman.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.pacman.dto.ExceptionType;
import com.tmobile.pacman.dto.IssueException;
import com.tmobile.pacman.util.ESUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ExceptionManagerImpl.
 *
 * @author kkumar
 */
public class ExceptionManagerImpl implements ExceptionManager {

    /** The index for exceptions. */
    private static String INDEX_FOR_EXCEPTIONS = "exceptions";

    /** The type for sticky exceptions. */
    private static String TYPE_FOR_STICKY_EXCEPTIONS = "sticky_exceptions";

    /** The rule id attribute name. */
    private static String RULE_ID_ATTRIBUTE_NAME = "targetTypes.rules.ruleId.keyword";

    /** The resource type attribute name. */
    private static String RESOURCE_TYPE_ATTRIBUTE_NAME = "targetTypes.name.keyword";

    /** The resource id. */
    private static String RESOURCE_ID = "_resourceid";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ExceptionManagerImpl.class);

    /**
     * return all the valid individual exceptions.
     *
     * @param resourceType the resource type
     * @return the individual exceptions
     * @throws Exception the exception
     */
    @Override
    public Map<String, IssueException> getIndividualExceptions(String resourceType) throws Exception {
        String indexName = "*_" + resourceType;
        String type = "issue_" + resourceType + "_exception";
        Map<String, Object> mustFilter = new HashMap<>();
        //mustFilter.put("exemptedStatus", "active");

        Map<String, Object> rangeMap = new HashMap<String, Object>();
        rangeMap.put("gte", "now");
        Map<String, Object> dateRangeMap = new HashMap<String, Object>();
        dateRangeMap.put("exceptionEndDate", rangeMap);

        mustFilter.put("range", dateRangeMap);

        List<Map<String, String>> exceptions = ESUtils.getDataFromES(ESUtils.getEsUrl(), indexName, type, mustFilter,
                null, null, null, 0, 20);
        Map<String, IssueException> individualExceptions = exceptions.stream()
                .map(obj -> new IssueException(obj, ExceptionType.INDIVIDUAL))
                .collect(Collectors.toMap(IssueException::getIssueId, obj -> obj, (oldval, newval) -> {
                    logger.error(
                            "duplicate exceptions are found in the system, please fix the source,  ignoring for now --> "
                                    + oldval);
                    return newval;
                }));

        // List<IssueException> stickyExceptions =
        // exceptions.stream().map(obj->new
        // IssueException(obj,ExceptionType.INDIVIDUAL)).collect(Collectors.toList());
        if (null != individualExceptions) {
            logger.info("got " + individualExceptions.size() + " individual exceptions");
        }
        return individualExceptions;
    }

    /**
     * returns map of resourceId and corresponding exception.
     *
     * @param ruleId the rule id
     * @param resourceType the resource type
     * @return the sticky exceptions
     * @throws Exception the exception
     */
    @Override
    public Map<String, List<IssueException>> getStickyExceptions(String ruleId, String resourceType) throws Exception {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(RULE_ID_ATTRIBUTE_NAME, ruleId);
        mustFilter.put(RESOURCE_TYPE_ATTRIBUTE_NAME, resourceType);
        Map<String, Object> rangeMap = new HashMap<String, Object>();
        rangeMap.put("gte", "now");
        Map<String, Object> dateRangeMap = new HashMap<String, Object>();
        dateRangeMap.put("expiryDate", rangeMap);
        mustFilter.put("range", dateRangeMap);
        List<Map<String, String>> exceptions = ESUtils.getDataFromES(ESUtils.getEsUrl(), INDEX_FOR_EXCEPTIONS,
                TYPE_FOR_STICKY_EXCEPTIONS, mustFilter, null, null, null, 0, 20);
        List<IssueException> stickyExceptions = exceptions.stream()
                .map(obj -> new IssueException(obj, ExceptionType.STICKY)).collect(Collectors.toList());
        // clear the must filter
        mustFilter.clear();
        // get only latest resources
        mustFilter.put("latest", true);
        Map<IssueException, List<String>> exemptedResources = new HashMap<>();
        Map<String, List<IssueException>> exceptionResourceSetMap = new HashMap<>();
        stickyExceptions.forEach(obj -> {
            try {
                exemptedResources.put(obj,
                        ESUtils.getDataFromES(ESUtils.getEsUrl(), obj.getAssetGroup(), resourceType, mustFilter, null,
                                null, null, 0, 20).stream().map(resource -> resource.get(RESOURCE_ID))
                                .collect(Collectors.toList()));
            } catch (Exception e) {
            }
        });
        exemptedResources.entrySet().forEach(entry -> {
            entry.getValue().forEach(resourceid -> {
                List<IssueException> exceptionsList = exceptionResourceSetMap.get(resourceid);
                if (exceptionsList == null) {
                    exceptionsList = new ArrayList<>();
                    exceptionResourceSetMap.put(resourceid, exceptionsList);
                }
                exceptionsList.add(entry.getKey());
            });
        });
        return exceptionResourceSetMap;
    }

}
