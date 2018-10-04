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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose: For checking kernel compliance
  Author :u26405
  Modified Date: Aug 10, 2017
  
 **/
package com.tmobile.cloud.awsrules.compliance;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-kernel-compliance", desc = "checks whether kernel version of instance is compliant or not", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class KernelComplianceRule extends BaseRule {
    private static final Logger logger = LoggerFactory.getLogger(KernelComplianceRule.class);
    Map<String, Boolean> kernelMap = new HashMap<>();
    String kernelversion;

    /**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 *            ************* Following are the Rule Parameters********* <br>
	 * <br>
	 * 
	 *            ruleKey : check-kernel-compliance <br>
	 * <br>
	 * 
	 *            severity : Enter the value of severity <br>
	 * <br>
	 * 
	 *            ruleCategory : Enter the value of category <br>
	 * <br>
	 * 
	 *            accountNames : Enter the comma separated account names <br>
	 * <br>
	 * 
	 *            splitterChar : Enter the delimiter such as comma<br>
	 * <br>
	 * 
	 *            esLdapUrl : Enter the LDAP URL <br>
	 * <br>
	 * 
	 *            esSatAndSpacewalkUrl : Enter the spacewalk and satellite URL <br>
	 * <br>
	 * 
	 *            kernelInfoApi : Enter the Kernel info URL <br>
	 * <br>
	 * 
	 * 
	 *            kernelVersionByInstanceIdAPI : Enter the API which gets the KV
	 *            by instanceId<br>
	 * <br>
	 * 
	 *            defaultKernelCriteriaUrl : Enter the API which gets the
	 *            default kernel criteria <br>
	 * <br>
	 * 
	 *            threadsafe : if true , rule will be executed on multiple
	 *            threads <br>
	 * <br>
	 * 
	 *            sourceType : Enter the source type from where you get the KV <br>
	 * <br>
	 * 
	 * @param resourceAttributes
	 *            this is a resource in context which needs to be scanned this
	 *            is provided y execution engine
	 *
	 */

    @Override
    public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
        logger.debug("==KernelComplianceRule started===");
        String userName = "username";
        String sshPd = "pwd";
        
        String instanceId = null;
        Annotation annotation = null;
        String ldapApi = null;
        String satAndSpacewalkApi = null;
        String kernelVersionByInstanceIdUrl = null;
        String kernelInfoApi = null;
        String ipAddress = null;
        int connectionPort = 22;
        boolean isComplianceCheckPassed = false;
        boolean isEligibleForAnnotation = true;

        List<String> listOfServersWhereComplianceCheckFailed = new ArrayList<>();
        Gson gson = new Gson();
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        
        String defaultKernelCriteriaUrl = ruleParam.get(PacmanRuleConstants.DEFAULT_KERNEL_CRITERIA_URL);
        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String accNames = ruleParam.get(PacmanRuleConstants.ACCOUNT_NAMES);
        String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);
        String sourceType = ruleParam.get(PacmanRuleConstants.SOURCE_TYPE);
        
        String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI); 
        String kernelVersionByInstanceIdAPI = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.KERNEL_VERSION_BY_INSTANCEID_API,PacmanRuleConstants.PACMAN_API_URI);
        logger.debug("==pacmanHost {}  ===",pacmanHost);
        if(!StringUtils.isNullOrEmpty(pacmanHost)){
            ldapApi = ruleParam.get(PacmanRuleConstants.ES_LDAP_URL);
            satAndSpacewalkApi = ruleParam
                    .get(PacmanRuleConstants.ES_SATLLITE_AND_SPACEWALK_URL);
            kernelInfoApi = ruleParam.get(PacmanRuleConstants.KERNEL_INFO_API);
            ldapApi = pacmanHost+ldapApi;
            satAndSpacewalkApi = pacmanHost+satAndSpacewalkApi;
            kernelInfoApi = pacmanHost+kernelInfoApi;
        }
        logger.debug("==kernelInfoApi URL after concatination param {}  ===",kernelInfoApi);
        logger.debug("==ldapApi URL after concatination param {}  ===",ldapApi);
        logger.debug("==satAndSpacewalkApi URL after concatination param {}  ===",satAndSpacewalkApi);
        
        logger.debug("==kernelVersionByInstanceIdAPI {}==",kernelVersionByInstanceIdAPI);
		if (!StringUtils.isNullOrEmpty(kernelVersionByInstanceIdAPI)) {
			kernelVersionByInstanceIdUrl = kernelVersionByInstanceIdAPI;
		}
		logger.debug("==kernelVersionByInstanceIdUrl URL {}==", kernelVersionByInstanceIdUrl);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
        StringBuilder description = new StringBuilder();
        List<String> sourcesverified = new ArrayList<>();
        LinkedHashMap<String, Object> kernelVersionFromSource = new LinkedHashMap<>();
        if (!PacmanUtils.doesAllHaveValue(sourceType, userName, sshPd,severity, category, ldapApi, satAndSpacewalkApi, kernelInfoApi,accNames, tagsSplitter, kernelVersionByInstanceIdUrl,defaultKernelCriteriaUrl)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (resourceAttributes != null) {
            String accountName = resourceAttributes.get(PacmanRuleConstants.ACCOUNT_NAME);
            if ("running".equalsIgnoreCase(resourceAttributes.get("statename"))&& (!resourceAttributes.get(PacmanRuleConstants.PLATFORM).equals(PacmanRuleConstants.PLATFORM_VAL))) {
                instanceId = resourceAttributes.get(PacmanRuleConstants.INSTANCEID);
                ipAddress = resourceAttributes.get(PacmanRuleConstants.PRIVATE_IP_ADDRESS);
                List<String> accountNames = PacmanUtils.splitStringToAList(accNames, tagsSplitter);
                List<String> sourceTypesList = PacmanUtils.splitStringToAList(sourceType, tagsSplitter);
                MDC.put("_resourceid", instanceId);
                MDC.put("applicationname",resourceAttributes.get("tags.Application"));
                MDC.put("accountname", accountName);
                boolean isAccountExists = PacmanUtils.isAccountExists(accountNames, accountName);

                // Taking the Quaterly versions of the kernel versions using the
                // PACMAN API
                logger.info("calling PacMan API to get Kernel criteria");
                JsonObject kernelVersionFromPacmanTable = DefaultTargetCriteriaDataProvider.getInstance(defaultKernelCriteriaUrl).getTargetCriterianData();

                if (kernelVersionFromPacmanTable != null && !kernelVersionFromPacmanTable.entrySet().isEmpty()) {
                    Map<String, String> complianceMap = new HashMap<>();
                    HashMap<String, String> mapOfQuaterlyVersions = gson.fromJson(kernelVersionFromPacmanTable.toString(),HashMap.class);
                    Set<String> keySet = mapOfQuaterlyVersions.keySet();

                    for (String source : sourceTypesList) {
                        try {
                            isComplianceCheckPassed = checkCompliance(source,mapOfQuaterlyVersions, keySet, userName,sshPd, ipAddress, connectionPort,instanceId, isAccountExists, ldapApi,
                                    satAndSpacewalkApi, kernelInfoApi,
                                    kernelVersionByInstanceIdUrl, complianceMap);
                        } catch (Exception e) {
                            logger.error("error"+e.getMessage());
                            throw new RuleExecutionFailedExeption(e.getMessage());
                        }
                        if (!isComplianceCheckPassed) {
                            sourcesverified.add(source);
                            if (null == kernelversion)
                                kernelversion = "Kernel not found";
                            kernelVersionFromSource.put(source, kernelversion);
                            listOfServersWhereComplianceCheckFailed.add(source);
                            
                            if (description.length() > 0) {
                                description.append("/").append(source);
                            } else {
                                description.append(source);
                            }

                        } else {
                            isEligibleForAnnotation = false;
                            break;
                        }
                    }

                    if (!listOfServersWhereComplianceCheckFailed.isEmpty() && isEligibleForAnnotation) {
                        annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
                        if (kernelMap.size() == sourceTypesList.size()) {
                            kernelMap = new HashMap<>();
                            annotation.put(PacmanSdkConstants.DESCRIPTION, "Kernel version not found." + "validated using:" + description);

                        } else {
                            annotation.put(PacmanSdkConstants.DESCRIPTION, "Kernel version not compliant." + "validated using:" + description);
                        }
                        annotation.put(PacmanRuleConstants.SEVERITY, severity);
                        annotation.put(PacmanRuleConstants.CATEGORY, category);
                        annotation.put(PacmanRuleConstants.FAILED_TYPES,listOfServersWhereComplianceCheckFailed.toString());
                        // Issue Details
                        issue.put(PacmanRuleConstants.VIOLATION_REASON, "ResourceId " + instanceId + " is not patched with latest update");

                        issue.put(PacmanRuleConstants.SOURCE_VERIFIED, String.join(",", sourcesverified));
                        issue.put("kernel_version_from_sources", gson.toJson(kernelVersionFromSource));
                        issueList.add(issue);
                        annotation.put("issueDetails", issueList.toString());
                        logger.debug("==KernelComplianceRule ended with annotation : {}===",annotation);
                        return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                    } else {
                        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, complianceMap.toString());
                    }
                } else {
                    logger.info("target kernel criteria not maintained");

                    // If Target Kernel Version not maintained create an issue
                    annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
                    annotation.put(PacmanSdkConstants.DESCRIPTION,"Target Kernerl Criteria not maintained");
                    annotation.put(PacmanRuleConstants.SEVERITY, severity);
                    annotation.put(PacmanRuleConstants.CATEGORY, category);
                    issue.put(PacmanRuleConstants.VIOLATION_REASON,PacmanRuleConstants.NO_DEFAULT_TARGET);
                    issueList.add(issue);

                    annotation.put("issueDetails", issueList.toString());
                    logger.debug("==KernelComplianceRule ended with annotation : {}===",annotation);
                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                }
            } else {
                logger.info(resourceAttributes.get(PacmanRuleConstants.INSTANCEID)+ " stopped/windows instance");
            }

        }
        logger.debug("==KernelComplianceRule ended===");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "Check for kernel compliance";
    }

    /**
     * This is a configurable method for checking the compliance in different
     * targets.
     * 
     * @param name
     * @param mapOfQuaterlyVersions
     * @param keys
     * @param userName
     * @param password
     * @param ipAddress
     * @param connectionPort
     * @param instanceId
     * @return true, if compliant else false
     * @throws Exception 
     */
    private boolean checkCompliance(String name,
            HashMap<String, String> mapOfQuaterlyVersions, Set<String> keys,
            String userName, String password, String ipAddress,
            int connectionPort, String instanceId, boolean isAccountExists,
            String ldapApi, String satAndSpacewalkApi, String kernelInfoApi,
            String kernelVersionByInstanceIdUrl,
            Map<String, String> complianceMap) throws Exception {
        boolean isCompliant = false; // default, we assume that all machines are
                                     // non-compliant

        switch (name) {
        
        case PacmanRuleConstants.LDAP:
            kernelversion = LDAPManager.getQueryfromLdapElasticSearch(instanceId, ldapApi);
            if (!StringUtils.isNullOrEmpty(kernelversion)) {
                logger.info(instanceId, "===>", kernelVersionByInstanceIdUrl,"===>", "checking kernel version complaint at LDAP");
                isCompliant = PacmanUtils.checkIsCompliant(kernelversion, keys,mapOfQuaterlyVersions);
                if (isCompliant) {
                    logger.info(instanceId, "====>","kernel version is complaint in LDAP");
                    setComplianceMap(complianceMap, kernelversion, name);
                }
            } else {
                logger.info(instanceId, " kernel version not found in ldap");
                kernelMap.put(PacmanRuleConstants.LDAP, false);
            }
            break;

        case PacmanRuleConstants.SSH:
            if (isAccountExists) {
                kernelversion = SSHManager.getkernelDetailsViaSSH(userName,password, ipAddress, connectionPort);

                if (!StringUtils.isNullOrEmpty(kernelversion)) {
                    logger.info(instanceId, "====>","kernel version found in SSH", kernelversion);
                    isCompliant = PacmanUtils.checkIsCompliant(kernelversion,keys, mapOfQuaterlyVersions);
                    if (isCompliant) {
                        setComplianceMap(complianceMap, kernelversion, name);
                    }
                } else {
                    kernelMap.put(PacmanRuleConstants.SSH, false);
                }
            } else {
                kernelMap.put(PacmanRuleConstants.SSH, false);
            }
            break;

        case PacmanRuleConstants.SPACEWALK_SAT:
            kernelversion = SpacewalkAndSatelliteManager.getQueryfromRhnElasticSearch(instanceId,satAndSpacewalkApi);
            if (!StringUtils.isNullOrEmpty(kernelversion)) {
                isCompliant = PacmanUtils.checkIsCompliant(kernelversion, keys,mapOfQuaterlyVersions);
                if (isCompliant) {
                    setComplianceMap(complianceMap, kernelversion, name);
                }
            } else {
                kernelMap.put(PacmanRuleConstants.SPACEWALK_SAT, false);
            }
            break;

        case PacmanRuleConstants.QUALYS:
            kernelversion = PacmanUtils.getKernelInfoFromElasticSearchBySource(instanceId,kernelInfoApi,PacmanRuleConstants.QUALYS);
            if (!StringUtils.isNullOrEmpty(kernelversion)) {
                isCompliant = PacmanUtils.checkIsCompliant(kernelversion, keys,mapOfQuaterlyVersions);
                if (isCompliant) {
                    setComplianceMap(complianceMap, kernelversion, name);
                }
            } else {
                kernelMap.put(PacmanRuleConstants.QUALYS, false);
            }
            break;

        case PacmanRuleConstants.WEB_SERVICE:
            kernelversion = PacmanTableAPI.getKernelVersionFromRHNSystemDetails(instanceId,kernelVersionByInstanceIdUrl);
            if (!StringUtils.isNullOrEmpty(kernelversion)) {
                isCompliant = PacmanUtils.checkIsCompliant(kernelversion, keys,mapOfQuaterlyVersions);
                if (isCompliant) {
                    setComplianceMap(complianceMap, kernelversion, name);
                }
            } else {
                kernelMap.put(PacmanRuleConstants.WEB_SERVICE, false);
            }
            break;
        default: // default clause should be the last one
            logger.error("Configure proper source type");
            break;
        }
        return isCompliant;
    }

    private void setComplianceMap(Map<String, String> complianceMap,String kernelVersion, String sourceType) {
        complianceMap.put(PacmanRuleConstants.KERNEL_VERSION, kernelVersion);
        complianceMap.put(PacmanRuleConstants.SOURCE_TYPE, sourceType);
        complianceMap.put(PacmanRuleConstants.DESCRIPTION,"Kernel version is compliant");
    }
        
}
