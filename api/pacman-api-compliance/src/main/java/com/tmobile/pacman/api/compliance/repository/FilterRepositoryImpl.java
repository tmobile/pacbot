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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.compliance.client.AssetServiceClient;
import com.tmobile.pacman.api.compliance.domain.AssetApi;
import com.tmobile.pacman.api.compliance.domain.AssetApiData;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;

/**
 * The Class FilterRepositoryImpl.
 */
@Repository
public class FilterRepositoryImpl implements FilterRepository, Constants {

    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /** The asset service client. */
    @Autowired
    private AssetServiceClient assetServiceClient;
    
    protected final Log logger = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getFiltersFromDb(int)
     */
    @Override
    public List<Map<String, Object>> getFiltersFromDb(int filterId)
            throws DataException {
        String ruleIdWithTargetTypeQuery = "SELECT opt.optionName as optionName,opt.optionValue as optionValue,opt.optionURL as optionURL FROM pac_v2_ui_filters filters LEFT JOIN pac_v2_ui_options opt ON filters.filterId = opt.filterId WHERE opt.filterId = "
                + filterId + "";
        return rdsepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getPoliciesFromDB(java.lang.String)
     */
    public List<Map<String, Object>> getPoliciesFromDB(String targetTypes)
            throws DataException {
        String ruleIdQuery = "SELECT rule.policyId,policy.policyName FROM cf_RuleInstance rule LEFT JOIN cf_Policy policy ON rule.policyId = policy.policyId WHERE rule.status = 'ENABLED' AND targetType IN ("
                + targetTypes + ") GROUP BY rule.policyId";
        return rdsepository.getDataFromPacman(ruleIdQuery);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getPoliciesFromES(java.lang.String)
     */
    public Map<String, Long> getPoliciesFromES(String assetGroup)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        String aggsFilter = CommonUtils.convertAttributetoKeyword("policyId");
        try{
        return elasticSearchRepository.getTotalDistributionForIndexAndType(
                assetGroup, null, mustFilter, mustNotFilter, null, aggsFilter,
                1000, null);
        }catch(Exception e){
        	logger.error("error in getPoliciesFromES",e);
            throw new DataException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getAccountsFromES(java.lang.String)
     */
    public List<Map<String, Object>> getAccountsFromES(String assetGroup)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("latest", "true");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("accountname");
        fields.add("accountid");
        try{
        return elasticSearchRepository.getSortedDataFromES(assetGroup,
                "account", mustFilter, null, null, fields, null, null);
        }catch(Exception e){
            throw new DataException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getRegionsFromES(java.lang.String)
     */
    public Map<String, Long> getRegionsFromES(String assetGroup)
            throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        String aggsFilter = CommonUtils.convertAttributetoKeyword("region");
       try{
        return elasticSearchRepository.getTotalDistributionForIndexAndType(
                assetGroup, null, mustFilter, mustNotFilter, null, aggsFilter,
                THOUSAND, null);
       }catch(Exception e){
           throw new DataException(e);
       }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getRulesFromES(java.lang.String)
     */
    public Map<String, Long> getRulesFromES(String assetGroup) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        String aggsFilter = CommonUtils.convertAttributetoKeyword(RULEID);
       try{
        return elasticSearchRepository.getTotalDistributionForIndexAndType(
                assetGroup, null, mustFilter, mustNotFilter, null, aggsFilter,
                THOUSAND, null);
       }catch(Exception e){
           throw new DataException(e);
       }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getListOfApplications(java.lang.String, java.lang.String)
     */
    public AssetCountDTO[] getListOfApplications(String assetGroup,
            String domain) throws DataException {
        AssetApi asstApi = assetServiceClient.getApplicationsList(assetGroup,
                domain);
        AssetApiData data = asstApi.getData();
        return data.getApplications();
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getListOfEnvironments(java.lang.String, java.lang.String, java.lang.String)
     */
    public AssetCountDTO[] getListOfEnvironments(String assetGroup,
            String application, String domain) throws DataException {
        AssetApi asstApi = assetServiceClient.getEnvironmentList(assetGroup,
                application, domain);
        AssetApiData data = asstApi.getData();
        return data.getEnvironments();
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FilterRepository#getListOfTargetTypes(java.lang.String, java.lang.String)
     */
    public AssetCountDTO[] getListOfTargetTypes(String assetGroup, String domain)
            throws DataException {
        AssetApi asstApi = assetServiceClient.getTargetTypeList(assetGroup,
                domain);
        AssetApiData data = asstApi.getData();
        return data.getTargettypes();
    }

}
