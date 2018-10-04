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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.client.AuthServiceClient;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.FilterRepository;

/**
 * The Class FilterServiceImpl.
 */
@Service
public class FilterServiceImpl implements FilterService, Constants {

    /** The statistics client. */
  //  @Autowired

    /** The auth client. */
    @Autowired
    private AuthServiceClient authClient;

    /** The repository. */
    @Autowired
    private FilterRepository repository;

    /** The compliance repository. */
    @Autowired
    private ComplianceRepository complianceRepository;
    
    /** The empty list. */
    List<Map<String, Object>> emptyList = null;
    
    /** The empty asset count. */
    AssetCountDTO[] emptyAssetCount;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getFilters(int, java.lang.String)
     */
    public List<Map<String, Object>> getFilters(int filterId, String domain)
            throws ServiceException {
        List<Map<String, Object>> filters;
        try{
            filters = repository
                .getFiltersFromDb(filterId);
    }catch(DataException e){
        throw new ServiceException(e);
    }
        if (null != domain && !INFRA_AND_PLATFORMS.equalsIgnoreCase(domain))
            filters.removeIf(f -> (f.get("optionName").equals(
                    REGION_DISPALY_NAME) || f.get("optionName").equals(
                    ACCOUNT_NAME)));
       if(filters.isEmpty()){
           throw new ServiceException(NO_DATA_FOUND);
       }
        return filters;

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getPolicies(java.lang.String, java.lang.String)
     */
    public List<Map<String, Object>> getPolicies(String assetGroup,
            String domain) throws ServiceException {
        List<Map<String, Object>> policyDetList = new ArrayList<>();

        String ttypes = complianceRepository.getTargetTypeForAG(assetGroup,
                domain);

        List<Map<String, Object>> policyIdsFromDb;
      try{
        policyIdsFromDb= repository
                .getPoliciesFromDB(ttypes);
      }catch(DataException e){
          throw new ServiceException(e);
      }
        noDataFoundCheck(emptyAssetCount, policyIdsFromDb);

        policyIdsFromDb.parallelStream().forEach(policy -> {
            Map<String, Object> policyMap = new HashMap<>();
            String policyId = policy.get(Constants.POLICYID).toString();
            String policyName = policy.get(POLICY_DISPLAY_NAME).toString();
            policyMap.put(NAME, policyName);
            policyMap.put(ID, policyId);
            synchronized (policyDetList) {
                policyDetList.add(policyMap);
            }
        });
        if(policyDetList.isEmpty()){
            throw new ServiceException(NO_DATA_FOUND);
        }
        return policyDetList;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getRegions(java.lang.String)
     */
    public List<Map<String, Object>> getRegions(String assetGroup)
            throws ServiceException {
        Map<String, Long> regionsMap;

        List<Map<String, Object>> regions = new ArrayList<>();
       try{ regionsMap = repository.getRegionsFromES(assetGroup);
       }catch(DataException e){
           throw new ServiceException(e);
       }
        if (regionsMap.isEmpty()) {
            throw new ServiceException(NO_DATA_FOUND);
        }
        regionsMap.entrySet().parallelStream().forEach(region -> {
            Map<String, Object> regMap = new HashMap<>();
            if (StringUtils.isNotBlank(region.getKey())) {
                regMap.put(NAME, region.getKey());
                regMap.put(ID, region.getKey());
                synchronized (regions) {
                    regions.add(regMap);
                }
            }
        });
        if(regions.isEmpty()){
            throw new ServiceException(NO_DATA_FOUND);
        }
        return regions;

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getAccounts(java.lang.String)
     */
    public List<Map<String, Object>> getAccounts(String assetGroup)
            throws ServiceException {
        List<Map<String, Object>> accounts;
       try{ accounts = repository.getAccountsFromES(assetGroup);
       }catch(DataException e){
           throw new ServiceException(e);
       }
        List<Map<String, Object>> accountDetails = new ArrayList<>();
        noDataFoundCheck(emptyAssetCount, accounts);
        accounts.parallelStream().forEach(account -> {
            Map<String, Object> accMap = new HashMap<>();

            accMap.put(NAME, account.get(ACCOUNT_NAME));
            accMap.put(ID, account.get(ACCOUNT_ID));
            synchronized (accountDetails) {
                accountDetails.add(accMap);
            }

        });
        if(accountDetails.isEmpty()){
            throw new ServiceException(NO_DATA_FOUND);
        }
        return accountDetails;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getRules(java.lang.String, java.lang.String)
     */
    public List<Map<String, Object>> getRules(String assetGroup, String domain)
            throws ServiceException {
        List<Map<String, Object>> ruleDetList = new ArrayList<>();
        String ttypes = complianceRepository.getTargetTypeForAG(assetGroup,
                domain);
        List<Map<String, Object>> ruleIdsFromDb;
        try{
        ruleIdsFromDb = complianceRepository
                .getRuleIdWithDisplayNameQuery(ttypes);
      }catch(DataException e){
          throw new ServiceException(e);
      }
        noDataFoundCheck(emptyAssetCount, ruleIdsFromDb);
        ruleIdsFromDb.parallelStream().forEach(policy -> {
            Map<String, Object> ruleMap = new HashMap<>();
            String ruleId = policy.get(RULEID).toString();
            String ruleName = policy.get(RULE_DISPAY_NAME).toString();

            ruleMap.put(NAME, ruleName);
            ruleMap.put(ID, ruleId);
            synchronized (ruleDetList) {
                ruleDetList.add(ruleMap);
            }

        });
        if(ruleDetList.isEmpty()){
            throw new ServiceException(NO_DATA_FOUND);
        }
        return ruleDetList;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getApplications(java.lang.String, java.lang.String)
     */
    public List<Map<String, Object>> getApplications(String assetGroup,
            String domain) throws ServiceException {
        AssetCountDTO[] assetCountByApps;
       
        try{assetCountByApps = repository.getListOfApplications(assetGroup, domain);
        }catch(DataException e){
            throw new ServiceException(e);
        }        
        noDataFoundCheck(assetCountByApps, emptyList);
     
        return getAssetCountByAppOrEnv(assetCountByApps);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getEnvironmentsByAssetGroup(java.lang.String, java.lang.String, java.lang.String)
     */
    public List<Map<String, Object>> getEnvironmentsByAssetGroup(
            String assetGroup, String application, String domain)
            throws ServiceException {
        AssetCountDTO[] assetCountByEnvs;
      try{
        assetCountByEnvs = repository.getListOfEnvironments(assetGroup,
                application, domain);
      }catch(DataException e){
          throw new ServiceException(e);
      }
        noDataFoundCheck(assetCountByEnvs, null);
        return getAssetCountByAppOrEnv(assetCountByEnvs);
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FilterService#getTargetTypesForAssetGroup(java.lang.String, java.lang.String)
     */
    public List<Map<String, Object>> getTargetTypesForAssetGroup(
            String assetGroup, String domain) throws ServiceException {
        AssetCountDTO[] assetCountByResourceTypes;
        try{
            assetCountByResourceTypes= repository
                .getListOfTargetTypes(assetGroup, domain);
        noDataFoundCheck(assetCountByResourceTypes, emptyList);
        }catch(DataException e){
            throw new ServiceException(e);
        }
        return getAssetCountByType(assetCountByResourceTypes);
    }

    /**
     * Gets the asset count by type.
     *
     * @param assetCountsByType the asset counts by type
     * @return the asset count by type
     */
    private List<Map<String, Object>> getAssetCountByType(
            AssetCountDTO[] assetCountsByType) {
        List<Map<String, Object>> assetList = new ArrayList<>();
        for (AssetCountDTO assetCount : assetCountsByType) {

            Map<String, Object> assetMap = new HashMap<>();
            if (StringUtils.isNotBlank(assetCount.getType())) {
                assetMap.put(NAME, assetCount.getType());
                assetMap.put(ID, assetCount.getType());
                assetList.add(assetMap);
            }
        } 
        return assetList;
    }

    /**
     * Gets the asset count by app or env.
     *
     * @param assetCountsByAppOrEnv the asset counts by app or env
     * @return the asset count by app or env
     */
    private List<Map<String, Object>> getAssetCountByAppOrEnv(
            AssetCountDTO[] assetCountsByAppOrEnv) {
        List<Map<String, Object>> assetList = new ArrayList<>();
        for (AssetCountDTO assetCount : assetCountsByAppOrEnv) {

            Map<String, Object> assetMap = new HashMap<>();
            if (StringUtils.isNotBlank(assetCount.getName())) {
                assetMap.put(NAME, assetCount.getName());
                assetMap.put(ID, assetCount.getName());
                assetList.add(assetMap);
            }
        }
        return assetList;
    }

    /**
     * No data found check.
     *
     * @param emptyAssetCount the empty asset count
     * @param emptyList the empty list
     * @throws ServiceException the service exception
     */
    private void noDataFoundCheck(AssetCountDTO[] emptyAssetCount,
            List<Map<String, Object>> emptyList) throws ServiceException {
        if (null == emptyAssetCount && null == emptyList) {
            throw new ServiceException(NO_DATA_FOUND);
        }

    }

}
