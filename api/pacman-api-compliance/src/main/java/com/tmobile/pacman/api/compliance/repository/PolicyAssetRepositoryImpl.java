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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;

/**
 * The Class PolicyAssetRepositoryImpl.
 */
@Repository
public class PolicyAssetRepositoryImpl implements PolicyAssetRepository,
        Constants {

    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;
    
    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;
    
    /** The Constant PROTOCOL. */
    static final String PROTOCOL = "http";
    
    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;
    
    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAssetRepositoryImpl.class);

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PolicyAssetRepository#fetchRuleDetails(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> fetchRuleDetails(String targetType) {

        String query = "SELECT  ruleId ,displayName,ruleFrequency,ruleParams,policyId  FROM cf_RuleInstance where status ='ENABLED' and  targetType ='"
                + targetType + "' order by ruleId";
        return rdsepository.getDataFromPacman(query);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PolicyAssetRepository#fetchOpenIssues(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<Map<String, Object>> fetchOpenIssues(String ag,
            String resourceType, String resourceId, boolean includeExempted) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("type", "issue");
        mustFilter.put("targetType.keyword", resourceType);
        mustFilter.put("_resourceid.keyword", resourceId);
        
        Map<String, Object> mustTermsFilter = new HashMap<>();
        List<Object> issueStatusList = new ArrayList<>();
        issueStatusList.add("open");
        
        if(includeExempted) {
        	issueStatusList.add("exempted");
        }
        
        mustTermsFilter.put("issueStatus", issueStatusList);
        
        List<String> fields = Arrays.asList("_resourceid", DOCID, "ruleId",
                "createdDate", "modifiedDate", "issueStatus");
        try {
            return elasticSearchRepository.getSortedDataFromES(ag, "issue_"
                    + resourceType, mustFilter, null, null, fields, mustTermsFilter, null);
        } catch (Exception e) {
            LOGGER.error("Error fetching issue from ES for ", resourceId);
            LOGGER.error("Error fetching issue from ES", e);
            throw new DataException("Error fetching issue from ES for "+resourceId,e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.PolicyAssetRepository#fetchDocId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String fetchDocId(String ag, String resourceType, String resourceId) {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("_resourceid", "resourceId");
        List<String> fields = Arrays.asList(DOCID);
        try {
            List<Map<String, Object>> docs = elasticSearchRepository
                    .getSortedDataFromES(ag, resourceType, mustFilter, null,
                            null, fields, null, null);
            if (!docs.isEmpty()) {
                return docs.get(0).get(DOCID).toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching issue from ES " , resourceId);
            LOGGER.error("Error fetching issue from ES" , e);
            return null;
        }
    }

}
