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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.client.AuthServiceClient;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;
import com.tmobile.pacman.api.compliance.domain.Compare;
import com.tmobile.pacman.api.compliance.domain.IssueExceptionResponse;
import com.tmobile.pacman.api.compliance.domain.IssueResponse;
import com.tmobile.pacman.api.compliance.domain.IssuesException;
import com.tmobile.pacman.api.compliance.domain.KernelVersion;
import com.tmobile.pacman.api.compliance.domain.PolicyViolationDetails;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseData;
import com.tmobile.pacman.api.compliance.domain.ResponseWithOrder;
import com.tmobile.pacman.api.compliance.domain.RuleDetails;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.FilterRepository;
import com.tmobile.pacman.api.compliance.util.CommonUtil;

/**
 * The Class ComplianceServiceImpl.
 */
@Service
public class ComplianceServiceImpl implements ComplianceService, Constants {

    /** The mandatory tags. */
    @Value("${tagging.mandatoryTags}")
    private String mandatoryTags;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The statistics client. */
    // @Autowired

    /** The auth client. */
    @Autowired
    private AuthServiceClient authClient;

    /** The repository. */
    @Autowired
    private ComplianceRepository repository;

    /** The proj eligibletypes. */
    @Autowired
    @Value("${projections.targetTypes}")
    private String projEligibletypes;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /** The filter repository. */
    @Autowired
    private FilterRepository filterRepository;
    
    /** The system configuration service. */
    @Autowired
    private SystemConfigurationService systemConfigurationService;
    
    @Value("${features.vulnerability.enabled:false}")
    private boolean qualysEnabled;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIssuesCount(String assetGroup, String ruleId, String domain) throws ServiceException {
        Assert.notNull(assetGroup, "asset group cannot be empty or blank");
        // transform the data here
        try {
            return repository.getIssuesCount(assetGroup, ruleId, domain);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseWithOrder getIssues(Request request) throws ServiceException {
        try {
            return repository.getIssuesFromES(request);
        } catch (DataException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getDistribution(String assetGroup, String domain) throws ServiceException {
        try {
            Map<String, Object> distribution = new HashMap<>();
            // get Rules mapped to targetType
            String targetTypes = repository.getTargetTypeForAG(assetGroup, domain);
            List<Object> rules = repository.getRuleIds(targetTypes);
            // get issue count
            Long totalIssues = getIssuesCount(assetGroup, null, domain);
            // get severity distribution
            Map<String, Long> ruleSeverityDistribution = repository.getRulesDistribution(assetGroup, domain, rules,
                    SEVERITY);
            // get category distribution
            Map<String, Long> ruleCategoryDistribution = repository.getRulesDistribution(assetGroup, domain, rules,
                    RULE_CATEGORY);
            // get rule category distribution
            Map<String, Object> ruleCategoryPercentage = repository.getRuleCategoryPercentage(ruleCategoryDistribution,
                    totalIssues);
            distribution.put("distribution_by_severity", ruleSeverityDistribution);
            distribution.put("distribution_ruleCategory", ruleCategoryDistribution);
            distribution.put("ruleCategory_percentage", ruleCategoryPercentage);
            distribution.put("total_issues", totalIssues);
            return distribution;
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getTagging(String assetGroup, String targetType) throws ServiceException {
        try {
            return repository.getTagging(assetGroup, targetType);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getCertificates(String assetGroup) throws ServiceException {
        try {
            return repository.getCertificates(assetGroup);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getPatching(String assetGroup, String targetType) throws ServiceException {
        Long totalPatched;
        Long totalUnpatched = 0l;
        Long totalAssets = 0l;
        double patchingPercentage;
        Map<String, Long> patching = new HashMap<>();
        AssetCountDTO[] targetTypes;
        try {
            if (StringUtils.isEmpty(targetType)) {
                targetTypes = filterRepository.getListOfTargetTypes(assetGroup, null);
            } else {
                AssetCountDTO apiName = new AssetCountDTO();
                apiName.setType(targetType);
                targetTypes = new AssetCountDTO[] { apiName };
            }
            for (AssetCountDTO targettype : targetTypes) {
                String type = targettype.getType();
                if (EC2.equalsIgnoreCase(type)) {
                    totalAssets += repository.getPatchabeAssetsCount(assetGroup, targettype.getType());
                    totalUnpatched += repository.getUnpatchedAssetsCount(assetGroup, targettype.getType());
                }
            }
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        totalPatched = totalAssets - totalUnpatched;
        if (totalAssets > 0) {
            patchingPercentage = (totalPatched * HUNDRED) / totalAssets;
            patchingPercentage = Math.floor(patchingPercentage);
        } else {
            patchingPercentage = HUNDRED;
        }
        patching.put("unpatched_instances", totalUnpatched);
        patching.put("total_instances", totalAssets);
        patching.put("patched_instances", totalPatched);
        patching.put("patching_percentage", (long) patchingPercentage);
        if (patching.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return patching;
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, Object>> getRecommendations(String assetGroup, String targetType) throws ServiceException {
        try {
            return repository.getRecommendations(assetGroup, targetType);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ResponseWithOrder getIssueAuditLog(String annotationId, String targetType, int from, int size,
            String searchText) throws ServiceException {

        List<LinkedHashMap<String, Object>> issueAuditLogList;
        try {
            long issueAuditCount = repository.getIssueAuditLogCount(annotationId, targetType);
            issueAuditLogList = repository.getIssueAuditLog(annotationId, targetType, from, size, searchText);

            if (issueAuditLogList.isEmpty()) {
                throw new ServiceException(NO_DATA_FOUND);
            }
            return new ResponseWithOrder(issueAuditLogList, issueAuditCount);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, Object>> getResourceDetails(String assetGroup, String resourceId) throws ServiceException {
        try {
            return repository.getResourceDetailsFromES(assetGroup, resourceId);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean addIssueException(final IssueResponse issueException) throws ServiceException {
        try {
            return repository.exemptAndUpdateIssueDetails(issueException);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.tmobile.pacman.api.compliance.service.ComplianceService#getRulecompliance
     * (com.tmobile.pacman.api.compliance.domain.Request)
     */
    @SuppressWarnings("rawtypes")
    public ResponseWithOrder getRulecompliance(Request request) throws ServiceException {
        // Ignoring input as we need to return all.
        int size = 0;
        int from = 0;
        String assetGroup = request.getAg();
        String searchText = request.getSearchtext();
        Map<String, String> filters = request.getFilter();
        String ruleCategory = "";
        if (null != filters.get(CommonUtils.convertAttributetoKeyword(RULE_CATEGORY))) {
            ruleCategory = filters.get(CommonUtils.convertAttributetoKeyword(RULE_CATEGORY));
        }
        List<LinkedHashMap<String, Object>> openIssuesByRuleList = new ArrayList<>();
        List<LinkedHashMap<String, Object>> openIssuesByRuleListFinal;
        ResponseWithOrder response = null;
        String rule = null;
        String ttypes = repository.getTargetTypeForAG(assetGroup, filters.get(DOMAIN));
        
        if (!Strings.isNullOrEmpty(ttypes)) {
            try {
                List<Map<String, Object>> ruleIdwithName = repository.getRuleIdWithDisplayNameWithRuleCategoryQuery(
                        ttypes, ruleCategory);
                List<Map<String, Object>> ruleIdwithsScanDate = repository.getRulesLastScanDate();
                Map<String, Long> totalassetCount = repository.getTotalAssetCount(assetGroup, filters.get(DOMAIN));

                /*--For filters we need to take rule Id's which match the filter condition--*/
                if (!Strings.isNullOrEmpty(filters.get(RULEID_KEYWORD))) {

                    rule = rule + "," + "'" + filters.get(RULEID_KEYWORD) + "'";
                    ruleIdwithName = repository.getRuleIdDetails(rule);
                } else {
                    if (!Strings.isNullOrEmpty(filters.get(CommonUtils.convertAttributetoKeyword(TARGET_TYPE)))) {
                        ruleIdwithName = repository.getRuleIDsForTargetType(filters.get(CommonUtils
                                .convertAttributetoKeyword(TARGET_TYPE)));
                    }
                }

                if (!ruleIdwithName.isEmpty()) {
                    // Make map of rule severity,category

                    List<Map<String, Object>> ruleSevCatDetails = getRuleSevCatDetails(ruleIdwithName);
                    Map<String, Object> ruleCatDetails = ruleSevCatDetails.parallelStream().collect(
                            Collectors.toMap(c -> c.get(RULEID).toString(), c -> c.get(RULE_CATEGORY), (oldvalue,
                                    newValue) -> newValue));
                    Map<String, Object> ruleSevDetails = ruleSevCatDetails.parallelStream().collect(
                            Collectors.toMap(c -> c.get(RULEID).toString(), c -> c.get(SEVERITY),
                                    (oldvalue, newValue) -> newValue));

                    Map<String, Object> untagMap = repository.getTaggingByAG(assetGroup,ttypes);
                    Map<String, Object> totalAwsUnTagMap = repository.getTaggingByAG(AWS,ttypes);
                    final Map<String, Long> openIssuesByRuleByAG = repository.getNonCompliancePolicyByEsWithAssetGroup(
                            assetGroup, null, filters, from, size, ttypes);
                    final Map<String, Long> openIssuesByRuleByAGAWS = repository
                            .getNonCompliancePolicyByEsWithAssetGroup(AWS, null, filters, from, size, ttypes);
                    ruleIdwithName
                            .forEach(ruleIdDetails -> {
                                Map<String, String> ruleIdwithsScanDateMap = new HashMap<>();
                                LinkedHashMap<String, Object> openIssuesByRule = new LinkedHashMap<>();
                                Long assetCount = 0l;
                                Long issuecountPerRuleAG = 0l;
                                Long issuecountPerRuleAGAWS = 0l;
                                double compliancePercentage;
                                double contributionPercentage;
                                String resourceType = null;
                                String ruleId = null;
                                long totaluntagged = 0l;
                                long totalTagged = 0;
                                double compliance = 0;
                                long totalAwsUntagged = 0l;

                                if (!ruleIdwithsScanDate.isEmpty()) {
                                    ruleIdwithsScanDateMap = ruleIdwithsScanDate.stream().collect(
                                            Collectors.toMap(s -> (String) s.get(RULEID),
                                                    s -> (String) s.get(MODIFIED_DATE)));
                                }

                                ruleId = ruleIdDetails.get(RULEID).toString();
                                resourceType = ruleIdDetails.get(TARGET_TYPE).toString();
                                assetCount = (null != totalassetCount.get(resourceType)) ? totalassetCount
                                        .get(resourceType) : 0l;
                                if (ruleId.equalsIgnoreCase(EC2_KERNEL_COMPLIANCE_RULE)) {
                                    Map<String, Long> cloudPatching = null;
                                    try {
                                        cloudPatching = getPatching(assetGroup, EC2);
                                    } catch (ServiceException e) {
                                        logger.error(e.getMessage());
                                    }
                                    Long awsUnpatchedInstances = cloudPatching.get(UNPATCHED_INSTANCES);
                                    compliancePercentage = cloudPatching.get(PATCHING_PERCENTAGE);
                                    contributionPercentage = Math.floor(cloudPatching.get(UNPATCHED_INSTANCES)
                                            * HUNDRED / awsUnpatchedInstances);
                                    assetCount = cloudPatching.get(TOTAL_INSTANCES);
                                    issuecountPerRuleAG = cloudPatching.get(UNPATCHED_INSTANCES);
                                } else if (ruleId.equalsIgnoreCase(ONPREM_KERNEL_COMPLIANCE_RULE)) {
                                    Map<String, Long> onpremPatching = null;
                                    try {
                                        onpremPatching = getPatching(assetGroup, ONPREMSERVER);
                                    } catch (ServiceException e) {
                                        logger.error(e.getMessage());
                                    }

                                    Long onpremUnpatchedInstances = onpremPatching.get(UNPATCHED_INSTANCE);
                                    compliancePercentage = onpremPatching.get(PATCHING_PERCENTAGE);
                                    contributionPercentage = Math.floor(onpremPatching.get(UNPATCHED_INSTANCE)
                                            * HUNDRED / onpremUnpatchedInstances);
                                    assetCount = onpremPatching.get(TOTAL_INSTANCES);
                                    issuecountPerRuleAG = onpremPatching.get(UNPATCHED_INSTANCES);
                                } else if (ruleId.contains(TAGGIG_POLICY)) {

                                    if (untagMap.get(resourceType) != null) {
                                        String totaluntaggedStr = untagMap.get(resourceType).toString()
                                                .substring(0, untagMap.get(resourceType).toString().length() - TWO);
                                        totaluntagged = Long.parseLong(totaluntaggedStr);
                                    }
                                    if (totalAwsUnTagMap.get(resourceType) != null) {
                                        String totalAWSuntaggedStr = totalAwsUnTagMap
                                                .get(resourceType)
                                                .toString()
                                                .substring(0,
                                                        totalAwsUnTagMap.get(resourceType).toString().length() - TWO);
                                        totalAwsUntagged = Long.parseLong(totalAWSuntaggedStr);
                                    }

                                    if (totaluntagged > assetCount) {
                                        totaluntagged = assetCount;
                                    }
                                    totalTagged = assetCount - totaluntagged;
                                    if (assetCount > 0) {
                                        compliance = (totalTagged * HUNDRED / assetCount);
                                        compliance = Math.floor(compliance);
                                    } else {
                                        compliance = HUNDRED;
                                    }
                                    if (compliance > HUNDRED) {
                                        compliance = HUNDRED;
                                    }

                                    compliancePercentage = compliance;
                                    if (totalAwsUntagged > 0) {
                                        contributionPercentage = Math.floor(totaluntagged * HUNDRED / totalAwsUntagged);
                                    } else {
                                        contributionPercentage = 0;
                                    }
                                    issuecountPerRuleAG = totaluntagged;
                                } else {
                                    if((ruleId.equalsIgnoreCase(CLOUD_QUALYS_RULE) && qualysEnabled) || ruleId.equalsIgnoreCase(SSM_AGENT_RULE)){
                                        //qualys coverage require only running instances
                                        try {
                                            assetCount = repository.getInstanceCountForQualys(assetGroup,"noncompliancepolicy","", "");
                                        } catch (DataException e) {
                                            logger.error("error",e.getMessage());
                                        }
                                    }else{
                                    assetCount = (null != totalassetCount.get(resourceType)) ? totalassetCount
                                            .get(resourceType) : 0l;
                                    }

                                    if (null != openIssuesByRuleByAG.get(ruleId)) {
                                        issuecountPerRuleAG = (null != openIssuesByRuleByAG.get(ruleId)) ? openIssuesByRuleByAG
                                                .get(ruleId) : 0l;
                                        issuecountPerRuleAGAWS = (null != openIssuesByRuleByAGAWS.get(ruleId)) ? openIssuesByRuleByAGAWS
                                                .get(ruleId) : 0l;
                                    }
                                    if (issuecountPerRuleAGAWS <= 0) {
                                        compliancePercentage = HUNDRED;
                                        contributionPercentage = 0;

                                    } else {

                                        if (issuecountPerRuleAG > assetCount) {
                                            issuecountPerRuleAG = assetCount;
                                        }
                                        compliancePercentage = Math
                                                .floor(((assetCount - issuecountPerRuleAG) * HUNDRED) / assetCount);
                                        contributionPercentage = Math.floor(issuecountPerRuleAG * HUNDRED
                                                / issuecountPerRuleAGAWS);
                                    }
                                }
                                openIssuesByRule.put(SEVERITY, ruleSevDetails.get(ruleId));
                                openIssuesByRule.put(NAME, ruleIdDetails.get(DISPLAY_NAME).toString());
                                openIssuesByRule.put(COMPLIANCE_PERCENT, compliancePercentage);
                                openIssuesByRule.put(LAST_SCAN, repository.getScanDate(ruleId, ruleIdwithsScanDateMap));
                                openIssuesByRule.put(RULE_CATEGORY, ruleCatDetails.get(ruleId));
                                openIssuesByRule.put(RESOURCE_TYPE, resourceType);
                                openIssuesByRule.put(RULEID, ruleId);
                                openIssuesByRule.put(ASSETS_SCANNED, assetCount);
                                openIssuesByRule.put(PASSED, assetCount - issuecountPerRuleAG);
                                openIssuesByRule.put(FAILED, issuecountPerRuleAG);
                                openIssuesByRule.put("contribution_percent", contributionPercentage);
                                if (!Strings.isNullOrEmpty(searchText)) {
                                    if (openIssuesByRule.containsValue(searchText)) {
                                        openIssuesByRuleList.add(openIssuesByRule);
                                    }
                                } else {
                                    openIssuesByRuleList.add(openIssuesByRule);

                                }

                            });
                }
                openIssuesByRuleListFinal = openIssuesByRuleList;
                // sorting by #Violation in desencing order

                Collections.sort(openIssuesByRuleListFinal, Collections.reverseOrder(new Compare()));
                if (openIssuesByRuleList.isEmpty()) {
                    throw new DataException(NO_DATA_FOUND);
                } else {
                    response = new ResponseWithOrder(openIssuesByRuleListFinal, openIssuesByRuleListFinal.size());
                }
            } catch (DataException e) {
                throw new ServiceException(e);
            }
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> closeIssuesByRule(final RuleDetails ruleDetails) {
        Map<String, Object> response = Maps.newHashMap();
        Boolean isAllClosed = repository.closeIssuesByRule(ruleDetails);
        if (isAllClosed) {
            response.put(STATUS, TWO_HUNDRED);
            response.put("message", "Successfully Closed all Issues!!!");
            return response;
        } else {
            response.put(STATUS, FOUR_NOT_THREE);
            response.put("message", "Failed in Issues Closure!!!");
        }
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.ComplianceService#
     * getRuleDetailsbyApplication(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRuleDetailsbyApplication(String assetGroup, String ruleId, String searchText)
            throws ServiceException {
        Map<String, Long> assetcountbyAplications;
        List<Map<String, Object>> applicationList = new ArrayList<>();
        String targetType = null;
        JsonArray buckets;
        try {
            buckets = repository.getRuleDetailsByApplicationFromES(assetGroup, ruleId, searchText);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        Gson googleJson = new Gson();
        List<Map<String, Object>> issuesByApplcationList = googleJson.fromJson(buckets, ArrayList.class);
        Map<String, Long> issuesByApplcationListMap = issuesByApplcationList.parallelStream().collect(
                Collectors.toMap(issue -> issue.get(KEY).toString(),
                        issue -> (long) Double.parseDouble(issue.get(DOC_COUNT).toString())));
        targetType = getTargetTypeByRuleId(ruleId);
        if (!Strings.isNullOrEmpty(targetType)) {
            // Get AssetCount By application for Rule TargetType

            if (ruleId.equalsIgnoreCase(EC2_KERNEL_COMPLIANCE_RULE)) {
                try {
                    assetcountbyAplications = repository.getPatchableAssetsByApplication(assetGroup, searchText,
                            targetType);
                } catch (DataException e) {
                    throw new ServiceException(e);
                }
            } else if ((ruleId.equalsIgnoreCase(ONPREM_KERNEL_COMPLIANCE_RULE))) {
                try {
                    assetcountbyAplications = repository.getPatchableAssetsByApplication(assetGroup, searchText,
                            ONPREMSERVER);
                } catch (DataException e) {
                    throw new ServiceException(e);
                }
            } else if ((ruleId.equalsIgnoreCase(CLOUD_QUALYS_RULE) && qualysEnabled) || ruleId.equalsIgnoreCase(SSM_AGENT_RULE)) {
                try{
                assetcountbyAplications = repository.getInstanceCountForQualysByAppsOrEnv(assetGroup, "policydetailsbyapplication","","");
                } catch (DataException e) {
                    throw new ServiceException(e);
                }
            } else {
                assetcountbyAplications = repository.getAllApplicationsAssetCountForTargetType(assetGroup, targetType);
            }
            // Form Compliance Details by Application
           formComplianceDetailsByApplication(applicationList, assetcountbyAplications,
                    issuesByApplcationListMap);
        } else {
            throw new ServiceException("No Target Type associated");
        }
        return applicationList;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.ComplianceService#
     * getRuleDetailsbyEnvironment(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public List<Map<String, Object>> getRuleDetailsbyEnvironment(String assetGroup, String ruleId, String application,
            String searchText) throws ServiceException {
        Map<String, Object> environmentDetails;
        String environment;
        List<Map<String, Object>> environmentList = new ArrayList<>();
        JsonElement docCount = null;
        Long assetCount = 0l;
        String targetType = null;
        JsonArray buckets;
        try {
            buckets = repository.getRuleDetailsByEnvironmentFromES(assetGroup, ruleId, application, searchText);

        } catch (DataException e) {
            throw new ServiceException(e);
        }
        targetType = getTargetTypeByRuleId(ruleId);
        for (int i = 0; i < buckets.size(); i++) {
            environmentDetails = new HashMap<>();
            environment = buckets.get(i).getAsJsonObject().get(KEY).getAsString();
            if (!StringUtils.isEmpty(targetType)) {

                assetCount = repository.getTotalAssetCountForEnvironment(assetGroup, application, environment,
                        targetType);
                if ((ruleId.equalsIgnoreCase(CLOUD_QUALYS_RULE) && qualysEnabled) || ruleId.equalsIgnoreCase(SSM_AGENT_RULE)) {
                    try {
                        assetCount = repository.getInstanceCountForQualys(assetGroup, "policydetailsbyenvironment", application, environment);
                    }catch (DataException e) {
                        throw new ServiceException(e);
                    }
                }

            }
            if (assetCount > 0) {
                docCount = buckets.get(i).getAsJsonObject().get(DOC_COUNT);

                if (docCount != null && assetCount <= Long.parseLong(docCount.toString())) {

                    assetCount = Long.parseLong(docCount.toString());

                    environmentDetails.put(TOTAL, assetCount);
                    environmentDetails.put(ENV, buckets.get(i).getAsJsonObject().get(KEY).getAsString());
                    environmentDetails.put(NON_COMPLIANT, Long.parseLong(docCount.toString()));
                    environmentDetails.put("compliant", (assetCount - Long.parseLong(docCount.toString())));
                    environmentDetails
                            .put(COMPLIANTPERCENTAGE,
                                    Math.floor(((assetCount - Double.parseDouble(docCount.toString())) / assetCount)
                                            * HUNDRED));
                    environmentList.add(environmentDetails);
                }

            }
        }
        return environmentList;

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getRuleDescription(String ruleId) throws ServiceException {
        Map<String, Object> ruledetails = new HashMap<>();
        ruledetails.put(RULEID, ruleId);
        try {
            List<Map<String, Object>> description = repository.getRuleDescriptionFromDb(ruleId);
            if (!description.isEmpty()) {
                ruledetails = getRuleDescriptionDetails(description, ruledetails);
            } else {
                throw new DataException(NO_DATA_FOUND);
            }
        } catch (DataException e) {
            throw new ServiceException(e);
        }

        return ruledetails;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean revokeIssueException(final String issueId) throws ServiceException {
        try {
            return repository.revokeAndUpdateIssueDetails(issueId);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.ComplianceService#
     * getKernelComplianceByInstanceIdFromDb(java.lang.String)
     */
    @Override
    public Map<String, Object> getKernelComplianceByInstanceIdFromDb(String instanceId) throws ServiceException {

        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> kernelMap;
        try {
            kernelMap = repository.getKernelComplianceByInstanceIdFromDb(instanceId);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        for (Map<String, Object> kv : kernelMap) {
            if (null != kv.get(KERNEL_VERSION)) {
                map.put(KERNEL_VERSION, kv.get(KERNEL_VERSION));
            }
        }
        if (map.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.ComplianceService#
     * updateKernelVersion
     * (com.tmobile.pacman.api.compliance.domain.KernelVersion)
     */
    @Override
    public Map<String, Object> updateKernelVersion(final KernelVersion kernelVersion) {
        return repository.updateKernelVersion(kernelVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.api.compliance.service.ComplianceService#
     * getOverallComplianceByDomain(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Object> getOverallComplianceByDomain(String assetGroup, String domain) throws ServiceException {
        double numerator = 0;
        double denominator = 0;
        double overallcompliance = 0;
        // get all the targettypes mapped to domain
        // get all rules mapped to these targetTypes
        List<Object> rules = getRules(repository.getTargetTypeForAG(assetGroup, domain));
        List<LinkedHashMap<String, Object>> complainceByRules = getComplainceByRules(domain, assetGroup, rules);
        Map<String, Map<String, Double>> rulesComplianceByCategory = getRulesComplianceByCategory(complainceByRules,
                assetGroup);
        int totalCategories = rulesComplianceByCategory.entrySet().size();
        LinkedHashMap<String, Object> ruleCatWeightage = getRuleCategoryBWeightage(domain, totalCategories,
                rulesComplianceByCategory);

        int ruleCategoryWeightage = 1;
        int totalWeightage = 0;
        LinkedHashMap<String, Object> ruleCatDistributionWithOverall = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : ruleCatWeightage.entrySet()) {
            for (Map.Entry<String, Map<String, Double>> categoryDistribution : rulesComplianceByCategory.entrySet()) {
                // calculate compliance By Category
                if (entry.getKey().equals(categoryDistribution.getKey())) {

                    ruleCategoryWeightage = (null != ruleCatWeightage.get(categoryDistribution.getKey())) ? Integer
                            .valueOf(ruleCatWeightage.get(categoryDistribution.getKey()).toString()) : 1;
                    totalWeightage += ruleCategoryWeightage;

                    denominator = (categoryDistribution.getValue().get(DENOMINATOR));
                    numerator = (categoryDistribution.getValue().get(NUMERATOR));
                    double issueCompliance = calculateIssueCompliance(numerator, denominator);

                    ruleCatDistributionWithOverall.put(categoryDistribution.getKey(), issueCompliance);
                    overallcompliance += (ruleCategoryWeightage * issueCompliance);
                    if (totalCategories == 1) {
                        overallcompliance = overallcompliance / totalWeightage;
                        overallcompliance = Math.floor(overallcompliance);
                        ruleCatDistributionWithOverall.put("overall", overallcompliance);
                    }
                    // Calculate Overall Compliance
                    totalCategories -= 1;

                }
            }
        }
        if (ruleCatDistributionWithOverall.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        return ruleCatDistributionWithOverall;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getResourceType(String assetgroup, String domain) throws ServiceException {
        List<String> targetTypes = new ArrayList<>();

        if (!StringUtils.isEmpty(projEligibletypes)) {
            String[] projectionTargetTypes = projEligibletypes.split(",");
            String ttypes = repository.getTargetTypeForAG(assetgroup, domain);
            for (String projTargetType : projectionTargetTypes) {
                if (ttypes.contains(projTargetType)) {
                    targetTypes.add(projTargetType);
                }
            }
        } else {
            throw new ServiceException("Please configure the projection targettypes");
        }
        return targetTypes;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("serial")
    @Override
    public List<Map<String, Object>> getRuleSevCatDetails(List<Map<String, Object>> ruleDetails)
            throws ServiceException {
        List<Map<String, Object>> ruleSevCatDetails = new ArrayList<>();
        for (Map<String, Object> ruleDetail : ruleDetails) {
            JsonParser parser = new JsonParser();
            List<Map<String, String>> paramsList;
            JsonObject ruleParamsJson;
            Map<String, Object> ruleSevCatDetail = new HashMap<>();
            ruleParamsJson = (JsonObject) parser.parse(ruleDetail.get(RULE_PARAMS).toString());
            paramsList = new Gson().fromJson(ruleParamsJson.get(PARAMS), new TypeToken<List<Object>>() {
            }.getType());

            for (Map<String, String> param : paramsList) {
                ruleSevCatDetail.put(RULEID, ruleDetail.get(RULEID));
                if (param.get(KEY).equalsIgnoreCase(RULE_CATEGORY)) {

                    ruleSevCatDetail.put(RULE_CATEGORY, param.get(VALUE));

                } else if (param.get(KEY).equalsIgnoreCase(SEVERITY)) {
                    ruleSevCatDetail.put(SEVERITY, param.get(VALUE));

                }
                ruleSevCatDetails.add(ruleSevCatDetail);
            }

        }
        return ruleSevCatDetails;
    }

    /**
     * {@inheritDoc}
     */
    public PolicyViolationDetails getPolicyViolationDetailsByIssueId(String assetGroup, String issueId)
            throws ServiceException {

        String policyViolated = null;
        String policyDescription = null;
        String resourceId = null;
        String ruleId = null;
        String issueDetails = null;
        List<Map<String, Object>> violationList = new ArrayList<>();
        Map<String, Object> violation = null;
        Map<String, Object> policyViolationByIssueId;
        try {
            policyViolationByIssueId = repository.getPolicyViolationDetailsByIssueId(assetGroup, issueId);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        if (!policyViolationByIssueId.isEmpty()) {
            ruleId = policyViolationByIssueId.get(RULEID).toString();
            resourceId = policyViolationByIssueId.get(RESOURCEID).toString();
            // get policy description from DB
            policyDescription = (null != getRuleDescription(ruleId).get(RULE_DESC)) ? getRuleDescription(ruleId).get(
                    RULE_DESC).toString() : "";
            // get policy title from DB
            policyViolated = (null != getRuleDescription(ruleId).get(DISPLAY_NAME)) ? getRuleDescription(ruleId).get(
                    DISPLAY_NAME).toString() : "";
            issueDetails = (null != policyViolationByIssueId.get(ISSUE_DETAILS)) ? policyViolationByIssueId.get(
                    ISSUE_DETAILS).toString() : null;
            if (null != issueDetails) {
                issueDetails = issueDetails.substring(TWO, issueDetails.length() - TWO);
                violation = Arrays.stream(issueDetails.trim().split(", ")).map(s -> s.split("="))
                        .collect(Collectors.toMap(a -> a[0], // key
                                a -> a[1] // value
                                ));
                violation.remove("violationReason");
                violationList.add(violation);
            }
            return new PolicyViolationDetails(policyViolationByIssueId.get(TARGET_TYPE).toString(),
                    policyViolationByIssueId.get(ISSUE_STATUS).toString(), policyViolationByIssueId.get(SEVERITY)
                            .toString(), policyViolationByIssueId.get(RULE_CATEGORY).toString(), resourceId,
                    policyViolated, policyDescription, policyViolationByIssueId.get(ISSUE_REASON).toString(),
                    policyViolationByIssueId.get(CREATED_DATE).toString(), policyViolationByIssueId.get(MODIFIED_DATE)
                            .toString(), policyViolationByIssueId.get(POLICYID).toString(), ruleId, violationList);

        } else {
            throw new ServiceException(NO_DATA_FOUND);
        }

    }

    /**
     * {@inheritDoc}
     */
    public ResponseEntity<Object> formatException(ServiceException e) {
        if (e.getMessage().contains(NO_DATA_FOUND)) {
            List<Map<String, Object>> emptylist = new ArrayList<>();
            ResponseData res = new ResponseData(emptylist);
            return ResponseUtils.buildSucessResponse(res);
        } else {
            return ResponseUtils.buildFailureResponse(e);
        }
    }

    private Map<String, Object> getRuleDescriptionDetails(List<Map<String, Object>> description,
            Map<String, Object> ruledetails) {
        String ruleParams = null;
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = null;
        JsonObject firstObject;
        JsonObject resultJson;
        String value = null;
        String key = null;
        List<String> resolution = new ArrayList<>();
        for (Map<String, Object> rule : description) {
            ruledetails.put(RULE_DESC, rule.get(POLICY_DESC));
            ruledetails.put(DISPLAY_NAME, rule.get(DISPLAY_NAME));
            ruledetails.put(POLICY_VERSION, rule.get(POLICY_VERSION));

            if (null != rule.get(RESOLUTION)) {
                resolution = Arrays.asList(rule.get(RESOLUTION).toString().split(","));
                ruledetails.put(RESOLUTION, resolution);
            } else {
                ruledetails.put(RESOLUTION, resolution);
            }

            ruleParams = rule.get(RULE_PARAMS).toString();

            resultJson = (JsonObject) jsonParser.parse(ruleParams);
            jsonArray = resultJson.getAsJsonObject().get(PARAMS).getAsJsonArray();
            if (jsonArray.size() > 0) {

                for (int i = 0; i < jsonArray.size(); i++) {
                    firstObject = (JsonObject) jsonArray.get(i);

                    value = firstObject.get(VALUE).getAsString();
                    key = firstObject.get(KEY).getAsString();
                    if (key.equals(RULE_CATEGORY) || key.equals(SEVERITY)) {
                        ruledetails.put(key, value);
                    }
                }
            }
        }
        return ruledetails;
    }

    private List<Map<String, Object>> formComplianceDetailsByApplication(List<Map<String, Object>> applicationList,
            Map<String, Long> assetcountbyAplications, Map<String, Long> issuesByApplcationListMap) {
        Map<String, Object> application;
        Long assetCount;
        long issueCount = 0;
        long complaintAssets;
        String applicationFromAsset;
        double compliancePercentage;
        // Form Compliance Details by Application
        for (Map.Entry<String, Long> assetcountbyAplication : assetcountbyAplications.entrySet()) {
            application = new HashMap<>();
            assetCount = assetcountbyAplication.getValue();
            applicationFromAsset = assetcountbyAplication.getKey();

            issueCount = (null != issuesByApplcationListMap.get(applicationFromAsset)) ? issuesByApplcationListMap
                    .get(applicationFromAsset) : 0l;
            if (issueCount > 0) {
                if (issueCount > assetCount) {
                    issueCount = assetCount;
                }
                complaintAssets = assetCount - issueCount;
                compliancePercentage = (complaintAssets * HUNDRED / assetCount);
                compliancePercentage = Math.floor(compliancePercentage);
            } else {
                complaintAssets = assetCount;
                compliancePercentage = HUNDRED;
            }

            application.put(TOTAL, assetCount);
            application.put("application", assetcountbyAplication.getKey());
            application.put("compliant", complaintAssets);
            application.put("non-compliant", issueCount);
            application.put(COMPLIANTPERCENTAGE, compliancePercentage);
            applicationList.add(application);
        }
        return applicationList;
    }

    private String getTargetTypeByRuleId(String ruleId) throws ServiceException {
        List<Map<String, Object>> targetTypeByRuleId;
        try {
            targetTypeByRuleId = repository.getTargetTypeByRuleId(ruleId);
        } catch (DataException e) {
            throw new ServiceException(e);
        }

        // Get targetType By Application

        for (Map<String, Object> rule : targetTypeByRuleId) {
            if (rule.get(TARGET_TYPE) != null) {
                return rule.get(TARGET_TYPE).toString();
            }
        }
        return null;
    }

    private int getSeverityWeightage(String severity) {
        int severityWeightage = 1;
        switch (severity) {
        case "critical":
            severityWeightage = TEN;
            break;
        case "high":
            severityWeightage = FIVE;
            break;
        case "medium":
            severityWeightage = THREE;
            break;
        case "low":
            severityWeightage = ONE;
            break;
        default:
        }
        return severityWeightage;
    }

    private LinkedHashMap<String, Object> getRuleCategoryBWeightage(String domain, int totalCategories,
            Map<String, Map<String, Double>> rulesComplianceByCategory) throws ServiceException {
        int defaultWeightage = 0;
        Map<String, Object> ruleCatWeightageUnsortedMap;

        // get asset count by Target Type
        try {
            ruleCatWeightageUnsortedMap = repository.getRuleCategoryWeightagefromDB(domain);
        } catch (DataException e) {
            throw new ServiceException(e);
        }

        LinkedHashMap<String, Object> ruleCatWeightage = new LinkedHashMap<>();
        List<Entry<String, Object>> list = null;
        if (null != ruleCatWeightageUnsortedMap && !ruleCatWeightageUnsortedMap.isEmpty()) {
            Set<Entry<String, Object>> set = ruleCatWeightageUnsortedMap.entrySet();

            list = new ArrayList<>(set);
            Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o2.getValue().toString()).compareTo(o1.getValue().toString());
                }
            });

            for (Map.Entry<String, Object> entry : list) {
                ruleCatWeightage.put(entry.getKey(), entry.getValue());
            }
        }

        if (ruleCatWeightage.isEmpty()) {
            defaultWeightage = INT_HUNDRED / totalCategories;
            for (Map.Entry<String, Map<String, Double>> categoryDistribution : rulesComplianceByCategory.entrySet()) {
                ruleCatWeightage.put(categoryDistribution.getKey(), defaultWeightage);
            }
        }

        return ruleCatWeightage;
    }

    private Map<String, Map<String, Double>> getRulesComplianceByCategory(
            List<LinkedHashMap<String, Object>> complainceByRules, String assetGroup) throws ServiceException {
        boolean isTaggingPresent = false;
        int severityWeightage = 1;
        String severity;
        double numerator = 0;
        double denominator = 0;
        double compliance = 0;
        Map<String, Map<String, Double>> rulesComplianceByCategory = new HashMap<>();
        for (Map<String, Object> complainceByRule : complainceByRules) {
            if ("tagging".equals(complainceByRule.get(RULE_CATEGORY).toString())) {
                isTaggingPresent = true;
                continue;
            }
            Map<String, Double> compliancePercentageByrule = new HashMap<>();
            compliance = Double.valueOf(complainceByRule.get(COMPLIANCE_PERCENT).toString());
            severity = complainceByRule.get(SEVERITY).toString();
            severityWeightage = getSeverityWeightage(severity);

            denominator = severityWeightage;
            numerator = (compliance * severityWeightage);
            if (!rulesComplianceByCategory.isEmpty()
                    && (null != rulesComplianceByCategory.get(complainceByRule.get(RULE_CATEGORY).toString()))) {
                Map<String, Double> exisitngCompliancePercentageByrule = rulesComplianceByCategory.get(complainceByRule
                        .get(RULE_CATEGORY));
                denominator += (exisitngCompliancePercentageByrule.get(DENOMINATOR));
                numerator += (exisitngCompliancePercentageByrule.get(NUMERATOR));
            }
            compliancePercentageByrule.put(DENOMINATOR, denominator);
            compliancePercentageByrule.put(NUMERATOR, numerator);
            rulesComplianceByCategory.put(complainceByRule.get(RULE_CATEGORY).toString(), compliancePercentageByrule);
        }

        if (isTaggingPresent) {
            Map<String, Long> taggingInfo = getTagging(assetGroup, null);
            Map<String, Double> compliancePercentageByrule = new HashMap<>();
            compliancePercentageByrule.put(DENOMINATOR, taggingInfo.get("assets").doubleValue());
            compliancePercentageByrule.put(NUMERATOR, taggingInfo.get("tagged").doubleValue() * HUNDRED);
            rulesComplianceByCategory.put("tagging", compliancePercentageByrule);
        }
        return rulesComplianceByCategory;
    }

    private double calculateIssueCompliance(double numerator, double denominator) {
        if (denominator > 0) {
            return Math.floor(numerator * 1.0 / denominator);
        } else {
            return HUNDRED;
        }
    }

    private List<Object> getRules(String ttypes) throws ServiceException {
        List<Object> rules;
        try {
            rules = repository.getRuleIds(ttypes);
            // get asset count by Target Type
        } catch (DataException e) {
            throw new ServiceException(e);
        }
        return rules;
    }

    private List<LinkedHashMap<String, Object>> getComplainceByRules(String domain, String assetGroup,
            List<Object> rules) throws ServiceException {
        List<LinkedHashMap<String, Object>> complainceByRules = null;
        Map<String, String> filter = new HashMap<>();
        filter.put(DOMAIN, domain);
        Request request = new Request("", 0, rules.size(), filter, assetGroup);
        ResponseWithOrder response = getRulecompliance(request);

        if (null != response) {
            complainceByRules = response.getResponse();
        }
        return complainceByRules;
    }

	@Override
	public Map<String, String> getCurrentKernelVersions() {
		return buildCriteriaMap(CommonUtil.getCurrentQuarterCriteriaKey());
	}
	
	private Map<String, String> buildCriteriaMap(String compCriteriaMap) {
		Map<String, String> kernelCriteriaMap = new TreeMap<>();
		try {
			String kernelSriteriaString = systemConfigurationService.getConfigValue(compCriteriaMap);
			StringTokenizer st = new StringTokenizer(kernelSriteriaString, "|");
			StringTokenizer keyValue;
			logger.debug("criteria string {}" , kernelSriteriaString);
			while (st.hasMoreTokens()) {
				keyValue = new StringTokenizer(st.nextToken(), "#");
				kernelCriteriaMap.put(keyValue.nextToken(),	keyValue.nextToken());
			}
			logger.debug("criteria map {} " , kernelCriteriaMap);
			return kernelCriteriaMap;
		} catch (Exception e) {
			// create a empty map
			logger.error("error parsing pacman.kernel.compliance.map from system configuration", e.getMessage());
			return new TreeMap<>();
		}
	}

    @Override
    public IssueExceptionResponse addMultipleIssueException(IssuesException issuesException) throws ServiceException {
        try {
            return repository.exemptAndUpdateMultipleIssueDetails(issuesException);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public IssueExceptionResponse revokeMultipleIssueException(List<String> issueIds) throws ServiceException {
        try {
            return repository.revokeAndUpdateMultipleIssueDetails(issueIds);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }
}
