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
package com.tmobile.pacman.publisher.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;

// TODO: Auto-generated Javadoc
/**
 * Annotation publisher.
 *
 * @author kkumar
 */
public class AnnotationPublisher {

    /** The Constant BULK_INDEX_REQUEST_TEMPLATE. */
    private static final String BULK_INDEX_REQUEST_TEMPLATE = "{ \"index\" : { \"_index\" : \"%s\", \"parent\" : \"%s\",  \"_type\" : \"%s\", \"_id\" : \"%s\" } }%n";

    /** The Constant BULK_WITH_REFRESH_TRUE. */
    public static final String BULK_WITH_REFRESH_TRUE = "/_bulk?refresh=true";

    /** The Constant ID. */
    private static final String ID = "_id";

    /** The Constant PARENT. */
    private static final String PARENT = "_parent";

    /** The Constant ROUTING. */
    private static final String ROUTING = "_routing";

    /** The Constant ERRORS. */
    private static final String ERRORS = "errors";

    /** The Constant TARGET_TYPE. */
    private static final String TARGET_TYPE = "targetType";

    /** The Constant TYPE. */
    private static final String TYPE = "type";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AnnotationPublisher.class);
    
    /** The bulk upload bucket. */
    private List<Annotation> bulkUploadBucket;
    
    /** The clouser bucket. */
    private List<Annotation> clouserBucket;
    
    /** The existing issues map with annotation id as key. */
    private Map<String, Map<String, String>> existingIssuesMapWithAnnotationIdAsKey;
    
    /** The resources. */
    private List<Map<String, String>> resources;
    
    /** The rule param. */
    private ImmutableMap<String, String> ruleParam;

    /**
     * Instantiates a new annotation publisher.
     */
    public AnnotationPublisher() {
        bulkUploadBucket = new ArrayList<Annotation>();
        clouserBucket = new ArrayList<Annotation>();
        existingIssuesMapWithAnnotationIdAsKey = new HashMap<>();
        setResources(new ArrayList<Map<String, String>>());
    }

    /**
     * Submit to publish.
     *
     * @param annotation the annotation
     */
    public void submitToPublish(Annotation annotation) {
        getBulkUploadBucket().add(annotation);
    }

    /**
     * Submit to close.
     *
     * @param annotation the annotation
     */
    public void submitToClose(Annotation annotation) {
        getClouserBucket().add(annotation);
    }

    /**
     * Gets the bulk upload bucket.
     *
     * @return the bulk upload bucket
     */
    public List<Annotation> getBulkUploadBucket() {
        return bulkUploadBucket;
    }

    /**
     * Sets the bulk upload bucket.
     *
     * @param bulkUploadBucket the new bulk upload bucket
     */
    public void setBulkUploadBucket(List<Annotation> bulkUploadBucket) {
        this.bulkUploadBucket = bulkUploadBucket;
    }

    /**
     * Populate existing issues for type.
     *
     * @param ruleParam the rule param
     * @throws Exception the exception
     */
    public void populateExistingIssuesForType(Map<String, String> ruleParam) throws Exception {

        String esUrl = ESUtils.getEsUrl();
        String ruleId = ruleParam.get(PacmanSdkConstants.RULE_ID);
        String indexName = CommonUtils.getIndexNameFromRuleParam(ruleParam);
        Map<String, Object> mustFilter = new HashMap<>();
        String attributeToQuery = ESUtils.convertAttributetoKeyword(PacmanSdkConstants.RULE_ID); //actual attribute will be  tokenized hence querying on keyword
        mustFilter.put(attributeToQuery, ruleId);
        List<String> fields = new ArrayList<String>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        mustNotFilter.put("issueStatus.keyword", "closed");
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        shouldFilter.put("type.keyword", "recommendation");
        shouldFilter.put("type.keyword", "issue");
        Long totalDocs = ESUtils.getTotalDocumentCountForIndexAndType(esUrl, indexName, null, mustFilter, mustNotFilter,
                shouldFilter);
        // get all the issues for this ruleId
        List<Map<String, String>> existingIssues = ESUtils.getDataFromES(esUrl, indexName.toLowerCase(), null,
                mustFilter, mustNotFilter, shouldFilter, fields, 0, totalDocs);
        existingIssues.stream().forEach(obj -> {
            existingIssuesMapWithAnnotationIdAsKey.put(obj.get(PacmanSdkConstants.ES_DOC_ID_KEY), obj);
        });
    }

    /**
     * Publish.
     *
     * @throws Exception the exception
     */
    public void publish() throws Exception {

        List<Annotation> annotations = getBulkUploadBucket();
        if (annotations.size() == 0) {
            logger.info("nothing to publish, exiting");
            return;
        }
        String esUrl = ESUtils.getEsUrl();
        Annotation sampleAnnotation = annotations.get(0);
        // this is called from rule executor now
        // populateExistingIssuesForType(sampleAnnotation);
        String indexName = ESUtils.buildIndexNameFromAnnotation(sampleAnnotation);
        String typeIssue = ESUtils.getIssueTypeFromAnnotation(sampleAnnotation);
        sampleAnnotation = null;
        Gson serializer = new GsonBuilder().create();

        StringBuffer bulkRequestBody = new StringBuffer();
        String bulkPostUrl = esUrl + BULK_WITH_REFRESH_TRUE;
        String response = "";
        String annotationId = "";
        Map<String, String> issueAttributes;
        String actualCreatedDate = "";
        String currentIssueStatus;
        List<Map<String, Map>> responseList = new ArrayList<>();
        for (Annotation _annotation : annotations) {
            annotationId = CommonUtils.getUniqueAnnotationId(_annotation);
            _annotation.put(PacmanSdkConstants.ANNOTATION_PK, annotationId);
            issueAttributes = getExistingIssuesMapWithAnnotationIdAsKey().get(annotationId);
            if (null != issueAttributes) {
                // now we are using this to modify and post hence remove all ES
                // specific fields
                issueAttributes.remove(ROUTING);
                issueAttributes.remove(PARENT);
                issueAttributes.remove(ID);
                actualCreatedDate = issueAttributes.get(PacmanSdkConstants.CREATED_DATE);
                currentIssueStatus = issueAttributes.get(PacmanSdkConstants.ISSUE_STATUS_KEY);
                issueAttributes.putAll(_annotation);
                issueAttributes.put(PacmanSdkConstants.CREATED_DATE, actualCreatedDate);
                issueAttributes.put(PacmanSdkConstants.MODIFIED_DATE, CommonUtils.getCurrentDateStringWithFormat(
                        PacmanSdkConstants.PAC_TIME_ZONE, PacmanSdkConstants.DATE_FORMAT));
                // no need to copy status as RuleExecutor already adjusting the
                // status
                // if(isAnnotationExempted(currentIssueStatus)){
                // issueAttributes.put(PacmanSdkConstants.ISSUE_STATUS_KEY,
                // currentIssueStatus);
                // }
            } else {
                issueAttributes = _annotation;
            }
            bulkRequestBody.append(String.format(BULK_INDEX_REQUEST_TEMPLATE, indexName,
                    _annotation.get(PacmanSdkConstants.DOC_ID), getTypeFromAnnotation(_annotation), annotationId));
            bulkRequestBody.append(serializer.toJson(issueAttributes));
            bulkRequestBody.append("\n");
            if (bulkRequestBody.toString().getBytes().length
                    / (1024 * 1024) >= PacmanSdkConstants.ES_MAX_BULK_POST_SIZE) {
                response = CommonUtils.doHttpPost(bulkPostUrl, bulkRequestBody.toString(),new HashMap<>());
                responseList.add(serializer.fromJson(response, Map.class));
                bulkRequestBody.setLength(0);
            }
        }
        // post the remaining data if available
        if (bulkRequestBody.length() > 0) {
            response = CommonUtils.doHttpPost(bulkPostUrl, bulkRequestBody.toString(),new HashMap<>());
        }
        responseList.add(serializer.fromJson(response, Map.class));
        if (responsesHasError(responseList)) {
            processErrors(responseList);
        }

    }

    /**
     * Gets the type from annotation.
     *
     * @param _annotation the annotation
     * @return the type from annotation
     */
    private String getTypeFromAnnotation(Annotation _annotation) {
        if (null != _annotation)
            return _annotation.get(TYPE) + "_" + _annotation.get(TARGET_TYPE);
        else
            return "";
    }

    /**
     * Checks if is annotation exempted.
     *
     * @param status the status
     * @return true, if is annotation exempted
     */
    private boolean isAnnotationExempted(String status) {
        return PacmanSdkConstants.ISSUE_STATUS_EXEMPTED_VALUE.equals(status);
    }

    /**
     * Responses has error.
     *
     * @param responseList the response list
     * @return true, if successful
     */
    private boolean responsesHasError(List<Map<String, Map>> responseList) {
        for (Map<String, Map> response : responseList) {

            if (response.containsKey(ERRORS) && Boolean.TRUE.toString().equals(response.get(ERRORS))) {
                return Boolean.TRUE;
            }
        }
        return false;
    }

    /**
     * Process closure ex.
     *
     * @return the list
     * @throws Exception the exception
     */
    public List<Annotation> processClosureEx() throws Exception {
        Integer totalClosed = 0;
        List<Annotation> closedIssues = new ArrayList<>();
        String esUrl = ESUtils.getEsUrl();
        Map<String, String> issue = null;
        String _id, _ds;
        String _type = null;
        String _index = null;
        StringBuffer bulkRequestBody = new StringBuffer();
        String bulkIndexRequestTemplate = BULK_INDEX_REQUEST_TEMPLATE;
        String bulkPostUrl = esUrl + BULK_WITH_REFRESH_TRUE;
        Gson serializer = new GsonBuilder().create();
        String response = "";
        for (Annotation annotation : clouserBucket) {
            _id = CommonUtils.getUniqueAnnotationId(annotation);
            issue = getExistingIssuesMapWithAnnotationIdAsKey().get(_id);
            if (!getExistingIssuesMapWithAnnotationIdAsKey().containsKey(_id)
                    || PacmanSdkConstants.STATUS_CLOSE.equals(issue.get(PacmanSdkConstants.ISSUE_STATUS_KEY))) {
                continue;
            } else {

                _index = ESUtils.buildIndexNameFromAnnotation(annotation);
                _type = ESUtils.getIssueTypeFromAnnotation(annotation);

                // removing _routing as this is a ES internal attribute , cannot
                // be specified while indexing
                issue.remove(ROUTING);
                issue.remove(PARENT);
                issue.remove(ID);
                issue.put(PacmanSdkConstants.ISSUE_STATUS_KEY, PacmanSdkConstants.STATUS_CLOSE);
                issue.put(PacmanSdkConstants.ISSUE_CLOSED_DATE, CommonUtils.getCurrentDateStringWithFormat(
                        PacmanSdkConstants.PAC_TIME_ZONE, PacmanSdkConstants.DATE_FORMAT));
                issue.put(PacmanSdkConstants.REASON_TO_CLOSE_KEY,
                        annotation.get(PacmanSdkConstants.REASON_TO_CLOSE_KEY));// copy
                                                                                // reason
                                                                                // to
                                                                                // close
                                                                                // from
                                                                                // annotation
                bulkRequestBody.append(String.format(bulkIndexRequestTemplate, _index,
                        issue.get(PacmanSdkConstants.DOC_ID), _type, _id));
                bulkRequestBody.append(serializer.toJson(issue));
                bulkRequestBody.append("\n");
                totalClosed++;
                annotation.putAll(issue); // copy all the attributes to
                                          // annotation
                closedIssues.add(annotation);
                if (bulkRequestBody.toString().getBytes().length
                        / (1024 * 1024) >= PacmanSdkConstants.ES_MAX_BULK_POST_SIZE) {
                    CommonUtils.doHttpPost(bulkPostUrl, bulkRequestBody.toString(),new HashMap<>());
                    bulkRequestBody.setLength(0);
                }
            }
        }
        if (bulkRequestBody.length() > 0) {
            response = CommonUtils.doHttpPost(bulkPostUrl, bulkRequestBody.toString(),new HashMap<>());
        }
        return closedIssues;
    }

    /**
     * Close dangling issues.
     *
     * @param sampleAnnotation the sample annotation
     * @return the int
     * @throws Exception the exception
     */
    public int closeDanglingIssues(Annotation sampleAnnotation) throws Exception {
        String indexName = ESUtils.buildIndexNameFromAnnotation(sampleAnnotation);
        String typeIssue = ESUtils.getIssueTypeFromAnnotation(sampleAnnotation);
        return closeDanglingIssues(indexName, typeIssue);

    }

    /**
     * Close dangling issues.
     *
     * @param _index the index
     * @param _type the type
     * @return the int
     * @throws Exception the exception
     */
    private int closeDanglingIssues(String _index, String _type) throws Exception {
        String esUrl = ESUtils.getEsUrl();
        StringBuffer bulkRequestBody = new StringBuffer();
        String bulkIndexRequestTemplate = BULK_INDEX_REQUEST_TEMPLATE;
        String bulkPostUrl = esUrl + BULK_WITH_REFRESH_TRUE;
        Gson serializer = new GsonBuilder().create();
        Integer totalClosed = 0;
        Map<String, String> issue;
        String _id, issueKey;

        for (Map<String, String> resource : getResources()) {
            issueKey = buildIssueKey(resource);
            getExistingIssuesMapWithAnnotationIdAsKey().remove(issueKey);
        }
        for (Map.Entry<String, Map<String, String>> issueWithId : getExistingIssuesMapWithAnnotationIdAsKey()
                .entrySet()) {
            issue = issueWithId.getValue();
            if (PacmanSdkConstants.STATUS_CLOSE.equals(issue.get(PacmanSdkConstants.ISSUE_STATUS_KEY))) {
                continue;
            }
            issue.remove(ROUTING);
            issue.remove(PARENT);
            issue.remove(ID);
            issue.put(PacmanSdkConstants.ISSUE_STATUS_KEY, PacmanSdkConstants.STATUS_CLOSE);
            issue.put(PacmanSdkConstants.ISSUE_CLOSED_DATE, CommonUtils
                    .getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE, PacmanSdkConstants.DATE_FORMAT));
            issue.put(PacmanSdkConstants.MODIFIED_DATE, CommonUtils
                    .getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE, PacmanSdkConstants.DATE_FORMAT));
            issue.put(PacmanSdkConstants.REASON_TO_CLOSE_KEY, PacmanSdkConstants.REASON_TO_CLOSE_VALUE);
            bulkRequestBody.append(String.format(bulkIndexRequestTemplate, _index, issue.get(PacmanSdkConstants.DOC_ID),
                    _type, issueWithId.getKey()));
            bulkRequestBody.append(serializer.toJson(issue));
            bulkRequestBody.append("\n");
            totalClosed++;
            if (bulkRequestBody.toString().getBytes().length
                    / (1024 * 1024) >= PacmanSdkConstants.ES_MAX_BULK_POST_SIZE) {
                CommonUtils.doHttpPost(bulkPostUrl, bulkRequestBody.toString(),new HashMap<>());
                bulkRequestBody.setLength(0);
            }
        }
        if (bulkRequestBody.length() > 0) {
            CommonUtils.doHttpPost(bulkPostUrl, bulkRequestBody.toString(),new HashMap<>());
        }
        return totalClosed;
    }

    /**
     * builds issue key using resource attributes.
     *
     * @param resource the resource
     * @return the string
     */
    private String buildIssueKey(Map<String, String> resource) {
        String parentId = resource.get(ID);
        String ruleId = getRuleParam().get(PacmanSdkConstants.RULE_ID);
        return CommonUtils.getUniqueAnnotationId(parentId, ruleId);
    }

    /**
     * Process errors.
     *
     * @param responseMapList the response map list
     */
    private void processErrors(List<Map<String, Map>> responseMapList) {
        logger.error("some errors occured while publishing the anotation, but no error handler found to handle it",
                responseMapList);
        // need to implement the error handling here
    }

    /**
     * Gets the clouser bucket.
     *
     * @return the clouser bucket
     */
    public List<Annotation> getClouserBucket() {
        return clouserBucket;
    }

    /**
     * Sets the clouser bucket.
     *
     * @param clouserBucket the new clouser bucket
     */
    public void setClouserBucket(List<Annotation> clouserBucket) {
        this.clouserBucket = clouserBucket;
    }

    /**
     * Gets the existing issues map with annotation id as key.
     *
     * @return the existing issues map with annotation id as key
     */
    public Map<String, Map<String, String>> getExistingIssuesMapWithAnnotationIdAsKey() {
        return existingIssuesMapWithAnnotationIdAsKey;
    }

    /**
     * Sets the existing resources.
     *
     * @param resources the resources
     */
    public void setExistingResources(List<Map<String, String>> resources) {
        this.setResources(resources);
    }

    /**
     * Gets the resources.
     *
     * @return the resources
     */
    public List<Map<String, String>> getResources() {
        return resources;
    }

    /**
     * Sets the resources.
     *
     * @param resources            the resources to set
     */
    private void setResources(List<Map<String, String>> resources) {
        this.resources = resources;
    }

    /**
     * Sets the rule param.
     *
     * @param ruleParam the rule param
     */
    public void setRuleParam(ImmutableMap<String, String> ruleParam) {
        this.ruleParam = ruleParam;

    }

    /**
     * Gets the rule param.
     *
     * @return the rule param
     */
    public ImmutableMap<String, String> getRuleParam() {
        return ruleParam;
    }

}
