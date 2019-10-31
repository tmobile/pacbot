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
package com.tmobile.pacman.api.compliance.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.compliance.domain.IssueExceptionResponse;
import com.tmobile.pacman.api.compliance.domain.IssueResponse;
import com.tmobile.pacman.api.compliance.domain.IssuesException;
import com.tmobile.pacman.api.compliance.domain.KernelVersion;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithOrder;
import com.tmobile.pacman.api.compliance.domain.RuleDetails;

// TODO: Auto-generated Javadoc
/**
 * The Interface ComplianceRepository.
 */
@Repository
public interface ComplianceRepository {

    /**
     * asssetGroup and domain are mandatory & ruleId is optional. If method
     * receives assetGroup and domain as request parameter, it gives issues
     * count of all open issues for all the rules associated to that domain. If
     * method receives assetGroup,domain and ruleId as request parameter,it
     * gives issues count of all open issues for that rule associated to that
     * domain.
     *
     * @param assetGroup
     *            the asset group
     * @param ruleId the rule id
     * @param domain
     *            the domain
     * @return long
     * @throws DataException
     *             the data exception
     */
    public long getIssuesCount(String assetGroup, String ruleId, String domain)
            throws DataException;

    /**
     * This request expects asssetGroup and domain as mandatory, ruleId as
     * optional. If method receives assetGroup and domain as request parameter,
     * it gives details of all open issues for all the rules associated to that
     * domain. If method receives assetGroup, domain and ruleId as request
     * parameter,it gives only open issues of that rule associated to that
     * domain. SearchText is used to match any text you are looking for. from
     * and size are for the pagination.
     *
     * @param request
     *            the request
     * @return ResponseWithOrder
     * @throws DataException
     *             the data exception
     */
    public ResponseWithOrder getIssuesFromES(Request request)
            throws DataException;



    /**
     * asssetGroup is mandatory and targetType is optional.If method receives
     * assetGroup as request parameter, method returns tagged count of all the
     * target types for that asset group. If method receives both assetGroup and
     * targetType as request parameter,method returns tagged count of specified
     * target type
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return Map<String, Long>
     * @throws DataException the data exception
     */
    public Map<String, Long> getTagging(String assetGroup, String targetType)

    throws DataException;

    /**
     * Gets count of expiredCertificates with in 60days and totalCertificates
     * for given assetGroup.
     *
     * @param assetGroup            the asset group
     * @return Map<String, Long>
     * @throws DataException             the data exception
     */
    public Map<String, Long> getCertificates(String assetGroup)
            throws DataException;

    /**
     * Format unpatched must filter.
     *
     * @param targetType
     *            the target type
     * @param ruleId
     *            the rule id
     * @return the map
     */
    public Map<String, Object> formatUnpatchedMustFilter(String targetType,
            String ruleId);

    /**
     * asssetGroup is mandatory and targetType is optional. If method receives
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
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRecommendations(String assetGroup,
            String targetType) throws DataException;

    /**
     * Gets the resource details from ES.
     *
     * @param assetGroup
     *            the asset group
     * @param resourceId
     *            the resource id
     * @return List<Map<String, Object>>
     * @throws DataException
     *             the data exception
     */
    public List<Map<String, Object>> getResourceDetailsFromES(
            String assetGroup, String resourceId) throws DataException;

    /**
     * Gets the issue audit log details.
     *
     * @param annotationId the annotation id
     * @param targetType the target type
     * @param from the from
     * @param size the size
     * @param searchText the search text
     * @return List<LinkedHashMap<String, Object>>
     * @throws DataException the data exception
     */
    public List<LinkedHashMap<String, Object>> getIssueAuditLog(
            String annotationId, String targetType, int from, int size,
            String searchText) throws DataException;

    /**
     * Gets the issue audit log count.
     *
     * @param annotationId the annotation id
     * @param targetType the target type
     * @return Long
     * @throws DataException the data exception
     */
    public Long getIssueAuditLogCount(String annotationId, String targetType)
            throws DataException;

    /**
     * Gets the open issue details.
     *
     * @author NidhishKrishnan (Nidhish)
     * @param issueId
     *            the issue id
     * @return List<Map<String, Object>>
     * @throws DataException
     *             the data exception
     * @requestParam issueId - String
     */
    public List<Map<String, Object>> getOpenIssueDetails(String issueId)
            throws DataException;

    /**
     * Gets the exempted issue details.
     *
     * @author NidhishKrishnan (Nidhish)
     * @param issueId
     *            the issue id
     * @return List<Map<String, Object>>
     * @throws DataException
     *             the data exception
     * @requestParam issueId - String
     */
    public List<Map<String, Object>> getExemptedIssueDetails(String issueId)
            throws DataException;

    /**
     * Exempt and update issue details.
     *
     * @author NidhishKrishnan (Nidhish)
     * @param issueException
     *            the issue exception
     * @return Boolean
     * @throws DataException
     *             the data exception
     * @requestBody IssueException
     */
    public Boolean exemptAndUpdateIssueDetails(
            final IssueResponse issueException) throws DataException;

    /**
     * Revoke and update issue details.
     *
     * @author NidhishKrishnan (Nidhish)
     * @param issueId
     *            the issue id
     * @return Boolean
     * @throws DataException
     *             the data exception
     * @requestParam issueId - String
     */
    public Boolean revokeAndUpdateIssueDetails(final String issueId)
            throws DataException;

    /**
     * Returns true if its successfully closes all issues in ES.
     *
     * @param ruleDetails            the rule details
     * @return Boolean
     */
    public Boolean closeIssuesByRule(RuleDetails ruleDetails);

    /**
     * Gets the total asset count for any target type.
     *
     * @param assetGroup
     *            the asset group
     * @param targetType
     *            the target type
     * @return Long
     */
    public Long getTotalAssetCountForAnytargetType(String assetGroup,
            String targetType);

    /**
     * Gets the rule id with display name.
     *
     * @param targetTypes the target types
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleIdWithDisplayNameQuery(
            String targetTypes) throws DataException;

    /**
     * Gets the rule Id's for target type.
     *
     * @param targetType
     *            the target type
     * @return List<Map<String, Object>>
     * @throws DataException
     *             the data exception
     */
    public List<Map<String, Object>> getRuleIDsForTargetType(String targetType)
            throws DataException;

    /**
     * Gets the non compliance policy by ES with asset group.
     *
     * @param assetGroup
     *            the asset group
     * @param searchText
     *            the search text
     * @param filter
     *            the filter
     * @param from
     *            the from
     * @param size
     *            the size
     * @param targetTypes
     *            the target types
     * @return Map<String, Long>
     * @throws DataException
     *             the data exception
     */
    public Map<String, Long> getNonCompliancePolicyByEsWithAssetGroup(
            String assetGroup, String searchText, Map<String, String> filter,
            int from, int size, String targetTypes) throws DataException;

    /**
     * Gets the comma separated target type for AG.
     *
     * @param assetGroup
     *            the asset group
     * @param domain
     *            the domain
     * @return String
     */
    public String getTargetTypeForAG(String assetGroup, String domain);

    /**
     * Gets the list of all rules and its modified date.
     *
     * @return List<Map<String, Object>>
     * @throws DataException             the data exception
     */
    public List<Map<String, Object>> getRulesLastScanDate()
            throws DataException;

    /**
     * Gets the modified date as string for the given ruleId.
     *
     * @param ruleId            the rule id
     * @param rulidwithScanDate            the rulidwith scan date
     * @return String
     */
    public String getScanDate(String ruleId,
            Map<String, String> rulidwithScanDate);

    /**
     * Gets the rule category.
     *
     * @param ruleId the rule id
     * @param ruleIdwithruleParamsMap the rule idwithrule params map
     * @return String
     */
    public String getRuleCategory(Object ruleId,
            Map<String, String> ruleIdwithruleParamsMap);

    /**
     * Gets the rule id details which are in enabled status from the DB.
     *
     * @param ruleId the rule id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleIdDetails(String ruleId)
            throws DataException;

    /**
     * Gets array of application tags and its issue count of the ruleId for
     * given assetGroup. SearchText is used to match any text you are looking
     * for.
     *
     * @param assetGroup the asset group
     * @param ruleId the rule id
     * @param searchText the search text
     * @return JsonArray
     * @throws DataException the data exception
     */
    public JsonArray getRuleDetailsByApplicationFromES(String assetGroup,
            String ruleId, String searchText) throws DataException;

    /**
     * Gets the target type by rule id which is in enabled status from the DB.
     *
     * @param ruleId the rule id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getTargetTypeByRuleId(String ruleId)
            throws DataException;

    /**
     * Gets the total asset count for environment.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param environment the environment
     * @param targetType the target type
     * @return Long
     */
    public Long getTotalAssetCountForEnvironment(String assetGroup,
            String application, String environment, String targetType);

    /**
     * Gets the array of environment tags and its issue count of the ruleId for
     * given assetGroup. SearchText is used to match any text you are looking
     * for.
     *
     * @param assetGroup            the asset group
     * @param ruleId            the rule id
     * @param application            the application
     * @param searchText            the search text
     * @param targetType the target type
     * @return JsonArray
     * @throws DataException the data exception
     */

    public JsonArray getRuleDetailsByEnvironmentFromES(String assetGroup,
            String ruleId, String application, String searchText,String targetType)
            throws DataException;

    /**
     * Gets the rule description from db.
     *
     * @param ruleId the rule id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleDescriptionFromDb(String ruleId)
            throws DataException;

    /**
     * Gets the kernel compliance by instance id from DB which is updated from
     * web service.
     *
     * @param instanceId the instance id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getKernelComplianceByInstanceIdFromDb(
            String instanceId) throws DataException;

    /**
     * Gets all applications asset count for target type.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return Map<String, Long>
     */
    public Map<String, Long> getAllApplicationsAssetCountForTargetType(
            String assetGroup, String targetType);

    /**
     * Gets the target type and its asset count.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @param application the application
     * @param type the type
     * @return Map<String, Long>
     */
    public Map<String, Long> getTotalAssetCount(String assetGroup, String domain, String application,String type);

    /**
     * Gets true if it updates the kernel version for the given instanceId
     * successfully if it is from webservice .
     *
     * @param kernelVersion the kernel version
     * @return Map<String, Object>
     */
    public Map<String, Object> updateKernelVersion(
            final KernelVersion kernelVersion);

    /**
     * If method receives comma separated target types, it gives list of
     * ruleId's for those given target types which are in enabled status from
     * the DB.
     *
     * @param targetType the target type
     * @return List<Object>
     * @throws DataException the data exception
     */
    public List<Object> getRuleIds(String targetType) throws DataException;

    /**
     * Gets the rule category weightage from DB.
     *
     * @param domain the domain
     * @return Map<String, Object>
     * @throws DataException the data exception
     */
    public Map<String, Object> getRuleCategoryWeightagefromDB(String domain)
            throws DataException;

    /**
     * Gets the tagging rules related target type and its issue count for the
     * missed tag.
     *
     * @param assetGroup the asset group
     * @param ttypes the ttypes
     * @return Map<String, Object>
     * @throws DataException the data exception
     */
    public Map<String, Object> getTaggingByAG(String assetGroup,String ttypes,String application)
            throws DataException;

    /**
     * Gets the policy violation details by issue id.
     *
     * @param assetGroup the asset group
     * @param issueId the issue id
     * @return Map<String, Object>
     * @throws DataException the data exception
     */
    public Map<String, Object> getPolicyViolationDetailsByIssueId(
            String assetGroup, String issueId) throws DataException;

    /**
     * This method is applicable only for ec2 and onpremserver target types. If
     * method receives assetGroup,application and
     * resourceType(ec2/onpremserver), it gives the single map of asset count
     * for that given application from the ES. If method receives assetGroup and
     * resourceType(ec2/onpremserver), it gives the map of all asset count for
     * that given resourceType from the ES.
     *
     * @param assetGroup the asset group
     * @param application the application
     * @param resourceType the resource type
     * @return Map<String, Long>
     * @throws DataException the data exception
     */

    public Map<String, Long> getPatchableAssetsByApplication(String assetGroup,
            String application, String resourceType) throws DataException;

    /**
     * Gets the rule id with display name with rule category query.
     *
     * @param targetTypes the target types
     * @param ruleCategory the rule category
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRuleIdWithDisplayNameWithRuleCategoryQuery(
            String targetTypes, String ruleCategory) throws DataException;

  
    /**
     * Gets the patchabe assets count.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @param application the application
     * @param environment the environment
     * @param searchText the search text
     * @return the patchabe assets count
     * @throws DataException the data exception
     */
    public Long getPatchabeAssetsCount(String assetGroup, String targetType,String application,String environment,String searchText)
            throws DataException;

    /**
     * This method is applicable only for ec2 and onpremserver target types. If
     * method receives assetGroup and targetType(ec2/onpremserver), it gives the
     * unpatched asset count for the given target type and asset group from the
     * ES.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @param application 
     * @return Long
     * @throws DataException the data exception
     */
    public Long getUnpatchedAssetsCount(String assetGroup, String targetType, String application)
            throws DataException;

    /**
     * If method receives keyname as input parameter, then it gives the value
     * for that key from the DB which are already configured.
     *
     * @param keyname the keyname
     * @return String
     * @throws DataException the data exception
     */
    public String fetchSystemConfiguration(final String keyname)
            throws DataException;

    /**
     * Gets the rules distribution.
     *
     * @param assetGroup the asset group
     * @param domain the domain
     * @param rules the rules
     * @param aggsFiltername the aggregation  filter name
     * @return the rules distribution
     * @throws DataException the data exception
     */
    public Map<String, Long> getRulesDistribution(String assetGroup, String domain,List<Object>rules,String aggsFiltername)throws DataException;

    /**
     * Gets the rule category percentage.
     *
     * @param ruleCategoryDistribution the rule category distribution
     * @param totalIssues the total issues
     * @return the rule category percentage
     */
    public Map<String, Object> getRuleCategoryPercentage(Map<String, Long> ruleCategoryDistribution, Long totalIssues);

    /**
     * Gets the instance count for qualys.
     *
     * @param assetGroup the asset group
     * @param apiType the api type
     * @param application the application
     * @param enivironment the enivironment
     * @return the instance count for qualys
     * @throws DataException the data exception
     */
    public Long getInstanceCountForQualys(String assetGroup,String apiType,String application,String enivironment,String resourceType)
            throws DataException;

    /**
     * Gets the instance count for qualys by apps or env.
     *
     * @param assetGroup the asset group
     * @param apiType the api type
     * @param application the application
     * @param enivironment the enivironment
     * @return the instance count for qualys by apps or env
     * @throws DataException the data exception
     */
    public Map<String, Long> getInstanceCountForQualysByAppsOrEnv(String assetGroup,String apiType,String application,String enivironment,String targetType)
            throws DataException;

    /**
     * Exempt and update multiple issue details.
     *
     * @param issuesException the issues exception
     * @return the issue exception response
     * @throws DataException the data exception
     */
    public IssueExceptionResponse exemptAndUpdateMultipleIssueDetails(IssuesException issuesException) throws DataException;

    /**
     * Revoke and update multiple issue details.
     *
     * @param issueIds the issue ids
     * @return the issue exception response
     * @throws DataException the data exception
     */
    public IssueExceptionResponse revokeAndUpdateMultipleIssueDetails(List<String> issueIds) throws DataException;
    
    /**
 	 * Gets the total asset count by environment.
 	 *
 	 * @param assetGroup the asset group
 	 * @param application the application
 	 * @param targetType the target type
 	 * @return the total asset count by environment
 	 */
 	public Map<String,Long> getTotalAssetCountByEnvironment(String assetGroup, String application,String targetType);
 	
 	/**
     * Gets the datasource for the target type.
     *
     * @param assetGroup
     *            the asset group
     * @param domain
     *            the domain
     * @param targetType
     *            the targetType
     * @return String
     */
    public List<Map<String,String>> getDataSourceForTargetTypeForAG(String assetGroup, String domain, String targetType);
    
    /**
	 * Gets the exempted assets count by rule.
	 *
	 * @param assetGroup the asset group
	 * @return the exempted assets count by rule
	 * @throws DataException the data exception
	 */
	public Map<String, Integer> getExemptedAssetsCountByRule(String assetGroup, String application,String type)
			throws DataException;


}
