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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.PolicyScanInfo;
import com.tmobile.pacman.api.compliance.domain.PolicyVialationSummary;
import com.tmobile.pacman.api.compliance.domain.SevInfo;
import com.tmobile.pacman.api.compliance.repository.PolicyAssetRepository;
import com.tmobile.pacman.api.compliance.util.CommonUtil;
/**
 * The Class PolicyAssetServiceImpl.
 */
@Service
public class PolicyAssetServiceImpl implements PolicyAssetService, Constants {

    /** The repository. */
    @Autowired
    PolicyAssetRepository repository;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.PolicyAssetService#getPolicyExecutionDetails(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<PolicyScanInfo> getPolicyExecutionDetails(String ag,
            String resourceType, String resourceId) throws ServiceException {
        List<Map<String, Object>> rules = repository
                .fetchRuleDetails(resourceType);
        List<Map<String, Object>> issueList;
		try {
			issueList = repository.fetchOpenIssues(ag,
			        resourceType, resourceId, true);
		} catch (DataException e) {
			throw new ServiceException("Error fetching issues from ES",e);
		}
        List<PolicyScanInfo> scanList = new ArrayList<>();
        if (issueList != null) {

            for (Map<String, Object> rule : rules) {
                PolicyScanInfo scanInfo = new PolicyScanInfo();
				Optional<Map<String, Object>> issueOpenOptonal = issueList.stream()
						.filter(obj -> (rule.get(RULEID).equals(obj.get(RULEID)) && obj.get("issueStatus").equals("open"))).findFirst();
				Map<String, Object> openIssue = null;
                if (issueOpenOptonal.isPresent()) {
                    openIssue = issueOpenOptonal.get();
                }
                
            	Optional<Map<String, Object>> issueExemptOptonal = issueList.stream()
						.filter(obj -> (rule.get(RULEID).equals(obj.get(RULEID)) && obj.get("issueStatus").equals("exempted"))).findFirst();
				Map<String, Object> exemptIssue = null;
                if (issueExemptOptonal.isPresent()) {
                	exemptIssue = issueExemptOptonal.get();
                }
                
                scanInfo.setPolicyName(rule.get("displayName").toString());
                scanInfo.setRuleId(rule.get(RULEID).toString());
                scanInfo.setPolicyId(rule.get("policyId").toString());
                if (openIssue != null && !openIssue.isEmpty()) {
                    scanInfo.setLastScan("Fail");
                    scanInfo.setIssueId(openIssue.get("_id").toString());
                    scanList.add(0, scanInfo);
                }else if(exemptIssue != null && !exemptIssue.isEmpty()) {
                	 scanInfo.setLastScan("Exempted");
                     scanInfo.setIssueId(exemptIssue.get("_id").toString());
                     scanList.add(scanInfo);
                } else {
                    scanInfo.setLastScan("Pass");
                    scanInfo.setIssueId("");
                    scanList.add(scanInfo);
                }
                scanInfo.setFrequency(CommonUtil.decodeAwsCronExp(rule.get(
                        "ruleFrequency").toString()));
                scanInfo.setSeverity(CommonUtil.getRuleSeverityFromParms(rule
                        .get("ruleParams").toString()));
                scanInfo.setScanHistory(new ArrayList<>());
                // Need additional data source to find if the asset is scanned.
                // As we only capture issue aduti which is not truley scan
                // history
            }
        } 
        return scanList;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.PolicyAssetService#getPolicyViolationSummary(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PolicyVialationSummary getPolicyViolationSummary(String ag,
            String resourceType, String resourceId) throws ServiceException {
        List<Map<String, Object>> rules = repository
                .fetchRuleDetails(resourceType);
        Map<String, String> ruleSevMap = new HashMap<>();
		List<Map<String, Object>> issueList;
		try {
			issueList = repository.fetchOpenIssues(ag, resourceType, resourceId, false);
		} catch (DataException e) {
			throw new ServiceException("Error fetching issues from ES",e);
		}

        if (!issueList.isEmpty()) {
            rules.forEach(rule -> {
                ruleSevMap.put(
                        rule.get(RULEID).toString(),
                        CommonUtil.getRuleSeverityFromParms(rule.get(
                                "ruleParams").toString()));
            });

            issueList = issueList
                    .stream()
                    .filter(issue -> ruleSevMap.get(issue.get(RULEID)
                            .toString()) != null).collect(Collectors.toList());
            int criticalCnt = 0;
            int highCnt = 0;
            int mediumCnt = 0;
            int lowCnt = 0;
            for (Map<String, Object> issue : issueList) {
                String severity = ruleSevMap.get(issue.get(RULEID).toString());
                if (severity != null) {
                    switch (severity) {
                    case CRITICAL:
                        criticalCnt++;
                        break;
                    case HIGH:
                        highCnt++;
                        break;
                    case MEDIUM:
                        mediumCnt++;
                        break;
                    case LOW:
                        lowCnt++;
                        break;
                        default:
                            
                    }
                }
            }
            List<SevInfo> sevList = new ArrayList<>();
            SevInfo critical = new SevInfo(CRITICAL, criticalCnt);
            SevInfo high = new SevInfo(HIGH, highCnt);
            SevInfo medium = new SevInfo(MEDIUM, mediumCnt);
            SevInfo low = new SevInfo(LOW, lowCnt);
            sevList.add(critical);
            sevList.add(high);
            sevList.add(medium);
            sevList.add(low);

            long denominator = ruleSevMap.values().stream()
                    .mapToLong(severity -> {
                        long weigtage = 0;
                        switch (severity) {
                        case CRITICAL:
                            weigtage = TEN;
                            break;
                        case HIGH:
                            weigtage = FIVE;
                            break;
                        case MEDIUM:
                            weigtage = THREE;
                            break;
                        case LOW:
                            weigtage = ONE;
                            break;
                        default:
                        }
                        return weigtage;
                    }).sum();
            long numerator = criticalCnt * TEN + highCnt * FIVE + mediumCnt
                    * THREE + Long.valueOf(lowCnt) * ONE;
            double compliance;
            if (numerator > 0 && denominator > 0) {
                compliance = Math.floor(INT_HUNDRED
                        - (numerator * HUNDRED / denominator));
            } else {
                compliance = INT_HUNDRED;
            }

            PolicyVialationSummary summary = new PolicyVialationSummary();
            summary.setTotal(issueList.size());
            summary.setSeverityInfo(sevList);
            summary.setCompliance(compliance);
            return summary;

        }
        // Default
        PolicyVialationSummary summary = new PolicyVialationSummary();
        summary.setTotal(0);
        summary.setSeverityInfo(new ArrayList<>());
        summary.setCompliance(INT_HUNDRED);
        return summary;
    }

}
