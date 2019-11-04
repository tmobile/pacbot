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
package com.tmobile.pacman.api.compliance.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.IssueExceptionResponse;
import com.tmobile.pacman.api.compliance.domain.IssueResponse;
import com.tmobile.pacman.api.compliance.domain.IssuesException;
import com.tmobile.pacman.api.compliance.domain.KernelVersion;
import com.tmobile.pacman.api.compliance.domain.PolicyViolationDetails;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithOrder;
import com.tmobile.pacman.api.compliance.domain.RuleDetails;

// TODO: Auto-generated Javadoc
/**
 * The Interface ComplianceService.
 */
public interface ComplianceService {

    /**
     * Gets the issues details based on name of the domain passed.
     *
     * @param request the request
     * @return ResponseWithOrder
     * @throws ServiceException the service exception
     */
    public ResponseWithOrder getIssues(Request request) throws ServiceException;

    /**
     * Gets Issue count based on name of the asset group/ruleId/domain passed.
     *
     * @param assetGroup the asset group
     * @param ruleId the rule id
     * @param domain the domain
     * @return long
     * @throws ServiceException the service exception
     */
    public long getIssuesCount(String assetGroup, String ruleId, String domain) throws ServiceException;

    /**
     * Gets Compliance distribution by rule category and severity.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return Map<String, Object>
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getDistribution(String assetGroup, String domain) throws ServiceException;

    /**
     * Gets Tagging compliance details based on name of name of the asset group/tagettype passed.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return Map<String, Long>
     * @throws ServiceException the service exception
     */

    public Map<String, Long> getTagging(String assetGroup, String targetType) throws ServiceException;

    /**
     * Gets the count of expiredCertificates with in 60days and
     *              totalCertificates for given assetGroup.
     *
     * @param assetGroup the asset group
     * @return Map<String, Long>
     * @throws ServiceException the service exception
     */
    public Map<String, Long> getCertificates(String assetGroup) throws ServiceException;

    /**
     * Gets the patching.
     *
     * @param assetGroup            name of the asset group
     * @param targetType            the target type
     * @param application the application
     * @return Method description: asssetGroup is mandatory. Method returns
     *         count of totalPached/toalUnpatched/TotalInstances for given
     *         assetGroup.
     * @throws ServiceException             the service exception
     */
    public Map<String, Long> getPatching(String assetGroup, String targetType, String application) throws ServiceException;

    /**
     * If method receives
     * assetGroup as request parameter, method returns list of all the issue
     * counts which are related to recommendations rules from the ES for the
     * given assetGroup with all the targetTypes.If method receives both
     * assetGroup and targetType as request parameter,method returns list of all
     * the issue counts which are related to recommendations rules from the ES
     * for the given targetType & assetGroup.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getRecommendations(String assetGroup, String targetType) throws ServiceException;

    /**
     * Gets list of issue audit log details for the size you have given.
     *
     * @param annotationId the annotation id
     * @param targetType the target type
     * @param from the from
     * @param size the size
     * @param searchText the search text
     * @return ResponseWithOrder
     * @throws ServiceException the service exception
     */
    public ResponseWithOrder getIssueAuditLog(String annotationId, String targetType, int from, int size,
            String searchText) throws ServiceException;

    /**
     * Gets the resource details.
     *
     * @param assetGroup the asset group
     * @param resourceId the resource id
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getResourceDetails(String assetGroup, String resourceId) throws ServiceException;

    /**
     *  Returns true if its successfully closes all the issues in ES
     *         for that ruleId else false.
     *
     * @param ruleDetails the rule details
     * @return Map<String, Object>
     */

    public Map<String, Object> closeIssuesByRule(RuleDetails ruleDetails);

    /**
     * Gets the list of all the rules compliance mapped to that domain.
     *
     * @param request the request
     * @return ResponseWithOrder
     * @throws ServiceException the service exception
     */
    public ResponseWithOrder getRulecompliance(Request request) throws ServiceException;

    /**
     * Gets the rule details by application.SearchText is used to match any text
     *         you are looking for.
     *
     * @param assetGroup the asset group
     * @param ruleId the rule id
     * @param searchText the search text
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getRuleDetailsbyApplication(String assetGroup, String ruleId, String searchText)
            throws ServiceException;

    /**
     * Gets the rule details by environment.SearchText is used to match any
     *         text you are looking for.
     *
     * @param assetGroup the asset group
     * @param ruleId the rule id
     * @param application the application
     * @param searchText the search text
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getRuleDetailsbyEnvironment(String assetGroup, String ruleId, String application,
            String searchText) throws ServiceException;

    /**
     * Gets the rule description and other details.
     *
     * @param ruleId the rule id
     * @return Map<String, Object>
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getRuleDescription(String ruleId) throws ServiceException;

    /**
     * Gets the kernel version of an instance id from DB where the kernel version updated by web service.
     *
     * @param instanceId the instance id
     * @return Map<String, Object>
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getKernelComplianceByInstanceIdFromDb(String instanceId) throws ServiceException;

    /**
     * Returns true if it updates the
     *         kernel version for the given instanceId successfully.
     *
     * @param kernelVersion the kernel version
     * @return Map<String, Object>
     */
    public Map<String, Object> updateKernelVersion(final KernelVersion kernelVersion);

    /**
     * Gets the overall compliance by domain.Over all compliance is calculated by its severity and rule category weightages.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @return Map<String, Object>
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getOverallComplianceByDomain(String assetGroup, String domain) throws ServiceException;

    /**
     * Gets the list of targetTypes for given asset group and domain
     *         based on project target types configurations.
     *
     * @param assetgroup the assetgroup
     * @param domain the domain
     * @return List<String>
     * @throws ServiceException the service exception
     */
    public List<String> getResourceType(String assetgroup, String domain)throws ServiceException;

    /**
     * Gets the rule severity and category details.
     *
     * @param ruleDetails the rule details
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getRuleSevCatDetails(List<Map<String, Object>> ruleDetails) throws ServiceException;

    /**
     * Gets the policy violation details by issue id.
     *
     * @param assetgroup the assetgroup
     * @param issueId the issue id
     * @return PolicyViolationDetails
     * @throws ServiceException the service exception
     */
    public PolicyViolationDetails getPolicyViolationDetailsByIssueId(String assetgroup, String issueId)
            throws ServiceException;

    /**
     * Adds the issue exception.
     *
     * @param issueException the issue exception
     * @return Boolean
     * @throws ServiceException the service exception
     */
    public Boolean addIssueException(IssueResponse issueException) throws ServiceException;

    /**
     * Revoke issue exception.
     *
     * @param issueId the issue id
     * @return boolean
     * @throws ServiceException the service exception
     */
    public Boolean revokeIssueException(String issueId) throws ServiceException;

    /**
     * Generic method to throw the service exception.
     *
     * @param e the e
     * @return ResponseEntity<Object>
     */
    public ResponseEntity<Object> formatException(ServiceException e);

    /**
     * method to get current kernel versions.
     *
     * @return Map<String, String>
     */
	public Map<String, String> getCurrentKernelVersions();

    /**
     * Adds the multiple issue exception.
     *
     * @param issuesException the issues exception
     * @return the issue exception response
     * @throws ServiceException the service exception
     */
    public IssueExceptionResponse addMultipleIssueException(IssuesException issuesException) throws ServiceException;

    /**
     * Revoke multiple issue exception.
     *
     * @param issueIds the issue ids
     * @return the issue exception response
     * @throws ServiceException the service exception
     */
    public IssueExceptionResponse revokeMultipleIssueException(List<String> issueIds) throws ServiceException;
    
}
