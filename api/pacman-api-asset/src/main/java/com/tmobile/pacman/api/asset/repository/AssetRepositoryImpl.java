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
package com.tmobile.pacman.api.asset.repository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.ResourceResponse;
import com.tmobile.pacman.api.asset.domain.ResourceResponse.Source;
import com.tmobile.pacman.api.asset.model.DefaultUserAssetGroup;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.NoDataFoundException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * Implemented class for AssetRepository and all its method
 */
@Repository
@ConfigurationProperties(prefix = "resource")
public class AssetRepositoryImpl implements AssetRepository {

    private Map<String, String> events;

    @Value("${tagging.mandatoryTags}")
    private String mandatoryTags;

    @Value("${elastic-search.host}")
    private String esHost;
    @Value("${elastic-search.port}")
    private int esPort;
    @Value("${elastic-search.update-host}")
    private String updateESHost;
    @Value("${elastic-search.update-port}")
    private int updateESPort;
    @Value("${elastic-search.host-heimdall}")
    private String heimdallEsHost;
    @Value("${elastic-search.port-heimdall}")
    private int heimdallEsPort;

    private static final String PROTOCOL = "http";
    private String esUrl;
    private String heimdallEsesUrl;

    @Value("${vulnerability.types}")
    private String vulnTypes;
    
    @Value("${patching.types:ec2,virtualmachine,onpremserver}")
	private String patchingTypes;
    
    @Value("${features.vulnerability.enabled:false}")
    private boolean qualysEnabled;

    private static final Log LOGGER = LogFactory.getLog(AssetRepositoryImpl.class);
    private RestClient restClient;

    @Autowired
    ElasticSearchRepository esRepository;
    @Autowired
    PacmanRdsRepository rdsRepository;
    @Autowired
    PacmanRedshiftRepository redshiftRepository;

    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
        heimdallEsesUrl = PROTOCOL + "://" + heimdallEsHost + ":" + heimdallEsPort;
    }
    
    private static final String SIZE = "size";
	private static final String AGGS = "aggs";
	private static final String QUERY = "query";
	private static final String ERROR_RETRIEVING_INVENTORY_FROM_ES = "error retrieving inventory from ES";

    @Override
	public Map<String, Long> getAssetCountByAssetGroup(String aseetGroupName, String type, String application) {

		Map<String, Object> filter = new HashMap<>();
		filter.put(Constants.LATEST, Constants.TRUE);
		filter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
		if (application != null) {
			filter.put(Constants.TAGS_APPS, application);
		}
		
		Map<String, Long> countMap = new HashMap<>();
		try {
			if (AssetConstants.ALL.equals(type)) {
				try {
					countMap = esRepository.getTotalDistributionForIndexAndType(aseetGroupName, null, filter, null,
							null, AssetConstants.UNDERSCORE_TYPE, Constants.THOUSAND, null);
				} catch (Exception e) {
					LOGGER.error("Exception in getAssetCountByAssetGroup :", e);
				}
			} else {
				long count = esRepository.getTotalDocumentCountForIndexAndType(aseetGroupName, type, filter, null,
						null, null, null);
				countMap.put(type, count);
			}
		} catch (Exception e) {
			LOGGER.error("Exception in getAssetCountByAssetGroup :", e);
		}

		return countMap;
	}

    @Override
	public List<Map<String, Object>> getTargetTypesByAssetGroup(String aseetGroupName, String domain, String provider) {

		String query = "select distinct targetType as type ,c.category as category,c.domain as domain, dataSourceName as " + Constants.PROVIDER + " from cf_AssetGroupTargetDetails a , cf_AssetGroupDetails b ,cf_Target c where a.groupId = b.groupId and a.targetType = c.targetName and b.groupName ='"
				+ aseetGroupName.trim() + "'";
		if (!StringUtils.isEmpty(domain)) {
			query = query + " and lower(c.domain) = '" + domain.toLowerCase().trim() + "'";
		}
		if (!StringUtils.isEmpty(provider)) {
			query = query + " and lower(c.dataSourceName) = '" + provider.toLowerCase().trim() + "'";
		}
		return rdsRepository.getDataFromPacman(query);
	}

	@Override
	public List<Map<String, Object>> getAllTargetTypes(String datasource) {

		String query = "select distinct targetName as type, category, dataSourceName as " + Constants.PROVIDER + " from cf_Target ";
		if(datasource!=null) {
			query = query + "where lower(dataSourceName) = '"+datasource.toLowerCase()+"'";
		}
		return rdsRepository.getDataFromPacman(query);

	}

    @Override
    public List<String> getApplicationByAssetGroup(String aseetGroupName) throws DataException {

        Map<String, Object> filter = new HashMap<>();
        filter.put(Constants.LATEST, Constants.TRUE);
        filter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
        Map<String, Long> applicationMap ;
        try {
            applicationMap = esRepository.getTotalDistributionForIndexAndType(aseetGroupName, null, filter, null, null,
                    Constants.TAGS_APPS, Constants.THOUSAND, null);
        } catch (Exception e) {
            LOGGER.error(AssetConstants.ERROR_GETAPPSBYAG, e);
            throw new DataException(e);
        }
        return new ArrayList<>(applicationMap.keySet());
    }

    @Override
	public List<String> getApplicationByAssetGroup(String assetGroupName, String domain) throws DataException {
		Map<String, Long> applicationMap = getApplicationAssetCountByAssetGroup(assetGroupName, domain, null);
		return new ArrayList<>(applicationMap.keySet());
	}

	@Override
	public Map<String, Long> getApplicationAssetCountByAssetGroup(String assetGroupName, String domain, String provider)
			throws DataException {

		Map<String, Long> applicationMap = new HashMap<>();
		try {
			Map<String, Object> applicationAssetData = getApplicationAssetCountByAssetGroupWithProvider(assetGroupName, domain, provider);
			applicationMap = (Map<String, Long>) applicationAssetData.get(Constants.APPLICATION_COUNT);
		} catch (Exception e) {
			LOGGER.error(AssetConstants.ERROR_GETAPPSBYAG, e);
			throw new DataException(e);
		}

		return applicationMap;
	}
	
	@Override
	public Map<String, Object> getApplicationAssetCountByAssetGroupWithProvider (String assetGroupName, String domain, String provider)
			throws DataException {

		List<String> targetTypes = getTargetTypesByAssetGroup(assetGroupName, domain, provider).stream()
				.map(obj -> obj.get(Constants.TYPE).toString()).collect(Collectors.toList());
		Map<String, Object> filter = new HashMap<>();
		filter.put(Constants.LATEST, Constants.TRUE);
		filter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
		Map<String, Object> mustTermsFilter = new HashMap<>();
		mustTermsFilter.put(AssetConstants.UNDERSCORE_TYPE, targetTypes);
		Map<String, Object> applicationMap = new HashMap<>();

		try {
			applicationMap = getTotalDistributionForIndexAndTypeWithProviders(assetGroupName, null, filter, null, null,
					Constants.TAGS_APPS, Constants.TEN_THOUSAND, mustTermsFilter);
		} catch (Exception e) {
			LOGGER.error(AssetConstants.ERROR_GETAPPSBYAG, e);
			throw new DataException(e);
		}

		return applicationMap;
	}
	
	/**
	 * 
	 * @param index
	 * @param type
	 * @param mustFilter
	 * @param mustNotFilter
	 * @param shouldFilter
	 * @param aggsFilter
	 * @param size
	 * @param mustTermsFilter
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getTotalDistributionForIndexAndTypeWithProviders(String index, String type,
			Map<String, Object> mustFilter, Map<String, Object> mustNotFilter,
			HashMultimap<String, Object> shouldFilter, String aggsFilter, int size, Map<String, Object> mustTermsFilter)
			throws Exception {
		Map<String, Object> distributionDataList = new HashMap<String, Object>();
		Map<String, Long> distributionCount = new HashMap<String, Long>();
		Map<String, List<Map<String, Object>>> distributionProviders = new HashMap<String, List<Map<String, Object>>>();
		try {
			Map<String, Object> nestedaggs = esRepository.buildAggs(Constants.CLOUD_TYPE_KEYWORD, size, Constants.AGGS_NAME_PROVIDERS, null);
			Map<String, Object> response = getDistributionDataFromES (index, type, mustFilter, mustNotFilter, shouldFilter, aggsFilter, size, null, nestedaggs, mustTermsFilter);
			Map<String, Object> aggregations = (Map<String, Object>) response.get(Constants.AGGREGATIONS);
			Map<String, Object> name = (Map<String, Object>) aggregations.get(Constants.NAME);
			List<Map<String, Object>> buckets = (List<Map<String, Object>>) name.get(Constants.BUCKETS);

			for (int i = 0; i < buckets.size(); i++) {
				Map<String, Object> bucket = buckets.get(i);
				distributionCount.put(bucket.get("key").toString(), ((Double) bucket.get("doc_count")).longValue());
				Map<String, Object> esProviders = (Map<String, Object>) bucket.get(Constants.AGGS_NAME_PROVIDERS);
				List<Map<String, Object>> providerbuckets = (List<Map<String, Object>>) esProviders.get(Constants.BUCKETS);
				List<Map<String, Object>> providers = new ArrayList<Map<String, Object>>();
				
				for (Map<String, Object> esProvider : providerbuckets) {
					Map<String, Object> provider = new HashMap<String, Object>();
					provider.put(Constants.PROVIDER, esProvider.get("key").toString());
					provider.put(Constants.TYPE_COUNT, ((Double) esProvider.get("doc_count")).longValue());
					providers.add(provider);
				}
				distributionProviders.put(bucket.get("key").toString(), providers);
			}
			
			distributionDataList.put(Constants.APPLICATION_COUNT, distributionCount);
			distributionDataList.put(Constants.APPLICATION_PROVIDERS, distributionProviders);

		} catch (Exception e) {
			LOGGER.error(ERROR_RETRIEVING_INVENTORY_FROM_ES, e);
			throw e;
		}
		return distributionDataList;
	}
	
	/**
	 * Function for getting the distribution data from ES
	 * @param index
	 * @param type
	 * @param mustFilter
	 * @param mustNotFilter
	 * @param shouldFilter
	 * @param aggsFilter
	 * @param size
	 * @param mustTermsFilter
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getDistributionDataFromES(String index, String type, Map<String, Object> mustFilter,
			Map<String, Object> mustNotFilter, HashMultimap<String, Object> shouldFilter, String aggsFilter, int size,
			String aggsName, Map<String, Object> nestedaggs, Map<String, Object> mustTermsFilter) throws Exception {
		
		String urlToQuery = esRepository.buildAggsURL(esUrl, index, type);
		Map<String, Object> requestBody = new HashMap<String, Object>();
		Map<String, Object> matchFilters = Maps.newHashMap();
		Map<String, Object> distributionData = new HashMap<String, Object>();
		if (mustFilter == null) {
			matchFilters.put("match_all", new HashMap<String, String>());
		} else {
			matchFilters.putAll(mustFilter);
		}
		if (null != mustFilter) {
			requestBody.put(QUERY, esRepository.buildQuery(matchFilters, mustNotFilter, shouldFilter, null, mustTermsFilter,null));
			requestBody.put(AGGS, esRepository.buildAggs(aggsFilter, size, aggsName, nestedaggs));

			if (!Strings.isNullOrEmpty(aggsFilter)) {
				requestBody.put(SIZE, "0");
			}

		} else {
			requestBody.put(QUERY, matchFilters);
		}
		String responseDetails = null;
		Gson gson = new GsonBuilder().create();

		try {
			String requestJson = gson.toJson(requestBody, Object.class);
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery, requestJson);
			distributionData = (Map<String, Object>) gson.fromJson(responseDetails, Map.class);
		} catch (Exception e) {
			LOGGER.error(ERROR_RETRIEVING_INVENTORY_FROM_ES, e);
			throw e;
		}
		return distributionData;
	}

    @Override
    public List<String> getEnvironmentsByAssetGroup(String assetGroup, String application, String domain) {

        Map<String, Object> filter = new HashMap<>();
        filter.put(Constants.LATEST, Constants.TRUE);
        filter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
        if (application != null) {
            filter.put(Constants.TAGS_APPS, application);
        }
        Map<String, Object> mustTermsFilter;
        if (!StringUtils.isEmpty(domain)) {
            List<String> targetTypes = getTargetTypesByAssetGroup(assetGroup, domain, null).stream()
                    .map(obj -> obj.get(Constants.TYPE).toString()).collect(Collectors.toList());
            mustTermsFilter = new HashMap<>();
            mustTermsFilter.put(AssetConstants.UNDERSCORE_TYPE, targetTypes);
        } else {
            mustTermsFilter = null;
        }

        Map<String, Long> envnMap = new HashMap<>();
        try {
            envnMap = esRepository.getTotalDistributionForIndexAndType(assetGroup, null, filter, null, null,
                    Constants.TAGS_ENV, Constants.THOUSAND, mustTermsFilter);
        } catch (Exception e) {
            LOGGER.error("Exception in getEnvironmentsByAssetGroup :" , e);
        }
        return new ArrayList<>(envnMap.keySet());
    }

    @Override
    public List<Map<String, Object>> getAllAssetGroups() {

        String query = "select distinct groupName as name, displayName as displayname ,description, groupType as type ,createdBy as createdby from cf_AssetGroupDetails where isVisible = true order by groupName asc ";
        return rdsRepository.getDataFromPacman(query);
    }

    @Override
    public Map<String, Object> getAssetGroupInfo(String assetGroup) {

        String query = "select distinct groupName as name, displayName as displayname ,description, groupType as type ,createdBy as createdby from cf_AssetGroupDetails where groupName = '"
                + assetGroup + "'";
        List<Map<String, Object>> results = rdsRepository.getDataFromPacman(query);
        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Long> getAssetCountByApplication(String assetGroup, String type) throws DataException {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Constants.LATEST, Constants.TRUE);
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        if (Constants.EC2.equals(type)) {
            shouldFilter.put(Constants.STATE_NAME, Constants.RUNNING);
            shouldFilter.put(Constants.STATE_NAME, AssetConstants.STOPPED);
            shouldFilter.put(Constants.STATE_NAME, AssetConstants.STOPPING);
        }
        try {
            return esRepository.getTotalDistributionForIndexAndType(assetGroup, type, filter, null, shouldFilter,
                    Constants.TAGS_APPS, Constants.THOUSAND, null);
        } catch (Exception e) {
            LOGGER.error("Exception in getAssetCountByApplication ", e);
            throw new DataException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getAssetMinMax(String assetGroup, String type, Date from, Date to) {

        List<Map<String, Object>> minMaxList = new ArrayList<>();
        try {

            StringBuilder request = new StringBuilder(
                    "{\"size\": 10000, \"_source\": [\"min\",\"max\",\"date\"],  \"query\": { \"bool\": { \"must\": [ { \"match\": {\"ag.keyword\": ");
            request.append("\"" + assetGroup + "\"}} ,{ \"match\": {\"type.keyword\": " + "\"" + type + "\"}}");
            String gte = null;
            String lte = null;

            if (from != null) {
                gte = "\"gte\": \"" + new SimpleDateFormat("yyyy-MM-dd").format(from) + "\"";
            }
            if (to != null) {
                lte = "\"lte\": \"" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "\"";
            }

            if (gte == null && lte == null) {
                request.append("]}}}");
            } else if (gte != null && lte != null) {
                request.append(AssetConstants.ESQUERY_RANGE + gte + "," + lte + AssetConstants.ESQUERY_RANGE_CLOSE);
            } else if (gte != null) {
                request.append(AssetConstants.ESQUERY_RANGE + gte + AssetConstants.ESQUERY_RANGE_CLOSE);
            } else {
                request.append(AssetConstants.ESQUERY_RANGE + lte + AssetConstants.ESQUERY_RANGE_CLOSE);
            }
            minMaxList = getAssetStats(request.toString());

        } catch (Exception e) {
            LOGGER.error("Exception in getAssetMinMax " , e);
        }
        return minMaxList;
    }

    List<Map<String, Object>> getAssetStats(String rqstBody) {

        List<Map<String, Object>> docs = new ArrayList<>();
        String responseJson = "";
        try {
            responseJson = PacHttpUtils.doHttpPost("http://" + esHost + ":" + esPort
                    + "/assetgroup_stats/count_type/_search", rqstBody);
        } catch (Exception e) {
            LOGGER.error("Exception in getAssetStats " , e);
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get("hits").toString());
        JsonArray jsonArray = hitsJson.getAsJsonObject().get("hits").getAsJsonArray();
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = (JsonObject) jsonArray.get(i);
                JsonObject sourceJson = (JsonObject) obj.get(AssetConstants.UNDERSCORE_SOURCE);
                if (sourceJson != null) {
                    Map<String, Object> doc = new Gson().fromJson(sourceJson, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    docs.add(doc);
                }
            }
        }
        return docs;

    }

    @Override
    public Integer saveOrUpdateAssetGroup(final DefaultUserAssetGroup defaultAssetGroup) {

        String userId = defaultAssetGroup.getUserId().toLowerCase();
        String defaultAssetGroupDetails = defaultAssetGroup.getDefaultAssetGroup();
        String userCountQuery = "SELECT COUNT(userId) FROM pac_v2_userpreferences WHERE userId=\"" + userId + "\"";
        String assetGroupUpdateQuery = "UPDATE pac_v2_userpreferences SET defaultAssetGroup=? WHERE userId=?";
        String assetGroupInserteQuery = "INSERT INTO pac_v2_userpreferences (userId, defaultAssetGroup) VALUES (?, ?)";
        int userCount = rdsRepository.count(userCountQuery);
        if (userCount > 0) {
            return rdsRepository.update(assetGroupUpdateQuery, defaultAssetGroupDetails, userId);
        } else {
            return rdsRepository.update(assetGroupInserteQuery, userId, defaultAssetGroupDetails);
        }
    }

    @Override
    public String getUserDefaultAssetGroup(String userId) {
        String query = "SELECT defaultAssetGroup from pac_v2_userpreferences WHERE userId=\"" + userId.toLowerCase()
                + "\"";
        try {
            return rdsRepository.queryForString(query);
        } catch (Exception exception) {
            LOGGER.error("Error in getUserDefaultAssetGroup " , exception);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String retrieveAssetConfig(String resourceId, String configType) {
        String query = "SELECT config FROM Pacman_Asset_Config WHERE resourceId=\"" + resourceId.toLowerCase()
                + "\" AND configType=\"" + configType.toLowerCase() + "\"";
        try {
            return rdsRepository.queryForString(query);
        } catch (Exception exception) {
            LOGGER.error("Exception in retrieveAssetConfig : " , exception);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public Integer saveAssetConfig(String resourceId, String configType, String config) {
        String assetGroupInserteQuery = "INSERT INTO Pacman_Asset_Config (resourceId, configType, config, createdDate) VALUES (?, ?, ?, ?)";
        try {
            return rdsRepository.update(assetGroupInserteQuery, resourceId, configType, config, new Date());
        } catch (Exception exception) {
            LOGGER.error("Exception in saveAssetConfig :" , exception);
            return -1;
        }
    }

    @Override
    public List<Map<String, Object>> getAssetCountByEnvironment(String assetGroup, String application, String type) {
        StringBuilder request = new StringBuilder(
                "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}}"); // Common
                                                                                                 // part
                                                                                                 // where
                                                                                                 // we
                                                                                                 // will
                                                                                                 // check
                                                                                                 // for
                                                                                                 // latest=true
        if (application != null) {
            request.append(",{\"match\":{\"tags.Application.keyword\":\"" + application + "\"}}"); // Add
                                                                                                   // Application
                                                                                                   // filter
        }
        if (Constants.EC2.equals(type)) {
            request.append(",{\"terms\":{\"statename\":[\"running\",\"stopped\",\"stopping\"]}}"); // EC2
                                                                                                   // special
                                                                                                   // handling
                                                                                                   // to
                                                                                                   // check
                                                                                                   // for
                                                                                                   // only
                                                                                                   // relevant
                                                                                                   // assets
            // Ending must/bool/query
            request.append("]}}");
            request.append(",\"aggs\":{\"apps\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":1000},\"aggs\":{\"envs\":{\"terms\":{\"field\":\"tags.Environment.keyword\",\"size\":1000}}}}}}"); // Aggs
        } // part

        String responseJson = "";
        try {
            responseJson = PacHttpUtils.doHttpPost("http://" + esHost + ":" + esPort + "/" + assetGroup + "/" + type
                    + "/_search", request.toString());
        } catch (Exception e) {
            LOGGER.error("Error in getAssetCountByEnvironment " , e);
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = jsonParser.parse(responseJson).getAsJsonObject();
        JsonArray apps = resultJson.get("aggregations").getAsJsonObject().get("apps").getAsJsonObject().get(Constants.BUCKETS)
                .getAsJsonArray();
        List<Map<String, Object>> appList = new ArrayList<>();
        for (JsonElement app : apps) {

            JsonObject appObj = app.getAsJsonObject();
            String appName = appObj.get("key").getAsString();
            JsonArray envs = appObj.get("envs").getAsJsonObject().get(Constants.BUCKETS).getAsJsonArray();
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("application", appName);
            List<Map<String, Object>> envList = new ArrayList<>();
            appMap.put("environments", envList);
            for (JsonElement env : envs) {

                JsonObject envObj = env.getAsJsonObject();
                String envName = envObj.get("key").getAsString();
                long count = envObj.get("doc_count").getAsLong();
                Map<String, Object> envMap = new HashMap<>();
                envMap.put("environment", envName);
                envMap.put("count", count);
                envList.add(envMap);
            }
            appList.add(appMap);
        }
        return appList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> saveAndAppendAssetGroup(String userId, String assetGroup) throws DataException {
		String lowerCaseUserId = userId.toLowerCase();
		String recentView = null;
		boolean isDuplicate = false;
		boolean isGreaterThanTen = false;
		boolean isValidAssetGroup = false;
		String assetGroupUpdateWithListQuery = null;
		List<String> recentViewList = new ArrayList<>();
		List<Map<String, Object>> assets = getAllAssetGroups();
		List<Map<String, Object>> recentlyViewed = new ArrayList<>();
		Map<String, Object> recentViewMap = new HashMap<>();
		for (Map<String, Object> ag : assets) {
			if (assetGroup.equals(ag.get("name"))) {
				isValidAssetGroup = true;
				String userCountQuery = "SELECT COUNT(userId) FROM pac_v2_userpreferences WHERE userId=\""
						+ lowerCaseUserId + "\"";
				String recentlyViewedAgQuery = "SELECT recentlyViewedAG FROM pac_v2_userpreferences WHERE userId=\""
						+ lowerCaseUserId + "\"";
				String assetGroupUpdateQuery = "UPDATE pac_v2_userpreferences SET recentlyViewedAG='" + assetGroup
						+ "' WHERE userId='" + lowerCaseUserId + "'";
				String assetGroupUpdateAndAppendQuery = "UPDATE pac_v2_userpreferences SET recentlyViewedAG = concat(recentlyViewedAG,'"
						+ "," + assetGroup + "') WHERE userId='" + lowerCaseUserId + "'";
				String assetGroupInsertQuery = "INSERT INTO pac_v2_userpreferences (userId, recentlyViewedAG) VALUES (?, ?)";
				int userCount = rdsRepository.count(userCountQuery);
				List<Map<String, Object>> recentlyViewedAgMap = rdsRepository.getDataFromPacman(recentlyViewedAgQuery);
				for (Map<String, Object> recentlyViewedAg : recentlyViewedAgMap) {
					if (recentlyViewedAg.get(AssetConstants.RECENTLY_VIEWED_AG) != null) {
						recentView = recentlyViewedAg.get(AssetConstants.RECENTLY_VIEWED_AG).toString();
						recentViewList = new CopyOnWriteArrayList(Arrays.asList(recentView.split(",")));
					}
				}

				if (userCount > 0) {
					if (!StringUtils.isEmpty(recentView)) {
						if (recentViewList.size() <= AssetConstants.NINE) {
							if (recentViewList.contains(assetGroup)) {
								recentViewList.remove(assetGroup);
								isDuplicate = true;
							}
						} else {
							if (recentViewList.contains(assetGroup)) {
								recentViewList.remove(assetGroup);
								isDuplicate = true;
							} else {
								recentViewList.remove(0);
								isGreaterThanTen = true;
							}
						}
						if (isDuplicate || isGreaterThanTen) {
							recentViewList.add(assetGroup);
							String assetGroups = String.join(",", recentViewList);
							assetGroupUpdateWithListQuery = "UPDATE pac_v2_userpreferences SET recentlyViewedAG='"
									+ assetGroups + "' WHERE userId='" + lowerCaseUserId + "'";
							rdsRepository.update(assetGroupUpdateWithListQuery);
							recentViewMap.put(AssetConstants.RECENTLY_VIEWED_AG,
									buildRecentViewDetails(recentViewList));
							recentlyViewed.add(recentViewMap);
							return recentlyViewed;

						} else {
							rdsRepository.update(assetGroupUpdateAndAppendQuery);
							recentViewList.add(assetGroup);
							recentViewMap.put(AssetConstants.RECENTLY_VIEWED_AG,
									buildRecentViewDetails(recentViewList));
							recentlyViewed.add(recentViewMap);
							return recentlyViewed;
						}
					} else {
						rdsRepository.update(assetGroupUpdateQuery);
						recentViewList.add(assetGroup);
						recentViewMap.put(AssetConstants.RECENTLY_VIEWED_AG, buildRecentViewDetails(recentViewList));
						recentlyViewed.add(recentViewMap);
						return recentlyViewed;
					}
				} else {
					rdsRepository.update(assetGroupInsertQuery, lowerCaseUserId, assetGroup);
					recentViewList.add(assetGroup);
					recentViewMap.put(AssetConstants.RECENTLY_VIEWED_AG, buildRecentViewDetails(recentViewList));
					recentlyViewed.add(recentViewMap);
					return recentlyViewed;
				}
			}
		}
		if (!isValidAssetGroup) {
			throw new DataException("Not A Valid Asset Group");
		}
		return recentlyViewed;
	}

	private List<Map<String, Object>> buildRecentViewDetails(List<String> recentViewList) {
		List<Map<String, Object>> recentlyViewedAgMapList = Lists.newArrayList();
		if (recentViewList.size() > 0) {
			ListIterator<String> iterator = recentViewList.listIterator(recentViewList.size());
			while (iterator.hasPrevious()) {
				String ag = iterator.previous();
				String query = "SELECT displayName FROM cf_AssetGroupDetails WHERE groupName = '" + ag + "'";
				String displayName = rdsRepository.queryForString(query);
				if (displayName != null) {
					Map<String, Object> details = Maps.newHashMap();
					details.put("ag", ag);
					details.put("displayName", displayName);
					details.put(Constants.PROVIDERS, providersDetailForAssetGroup(ag));
					recentlyViewedAgMapList.add(details);
				}
			}
		}
		return recentlyViewedAgMapList;
	}

	/**
	 * To get the provider details for an asset group
	 * @param assetGroup
	 * @return
	 */
	private List<Map<String, Object>> providersDetailForAssetGroup ( String assetGroup) {
		
		Map<String, Long> countMap =  getAssetCountByAssetGroup(assetGroup, "all", null);
		List<Map<String, Object>> targetTypes = getTargetTypesByAssetGroup(assetGroup, "Infra & Platforms", null);
		List<String> validTypes = targetTypes.stream().map(obj -> obj.get(Constants.TYPE).toString())
				.collect(Collectors.toList());
		List<String> countTypes = new ArrayList<>(countMap.keySet());
		for (String _type : countTypes) {
			if (!validTypes.contains(_type)) {
				countMap.remove(_type);
			}
		}
		List<Map<String, Object>> datasourceForAssettypes = getDataSourceForTargetTypes(validTypes);
		Map<String,Long> providerMap = datasourceForAssettypes.stream().filter(typeInfo-> countTypes.contains(typeInfo.get(Constants.TYPE))).collect(Collectors.groupingBy(typeInfo->typeInfo.get(Constants.PROVIDER).toString(),Collectors.counting()));
					
		List<Map<String, Object>> providersDetails = new ArrayList<Map<String, Object>>();
		providerMap.forEach((k,v)-> {
			Map<String, Object> newProvider = new HashMap<String, Object>();
			newProvider.put(Constants.PROVIDER,k);
			newProvider.put(Constants.TYPE_COUNT, v);
			providersDetails.add(newProvider);
		});
		return providersDetails;
	}

    @SuppressWarnings("rawtypes")
    @Override
    public List<Map<String, Object>> getListAssets(String assetGroup, Map<String, String> filter, int from, int size,
            String searchText) {
        LOGGER.info("Inside getListAssets");
        List<Map<String, Object>> assetDetails = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();
        String targetType = "";
        String domain = filter.get(Constants.DOMAIN);
        Map<String, Object> mustFilter = new HashMap<>();

        Iterator it = filter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey().equals(AssetConstants.FILTER_APPLICATION)) {
                mustFilter.put(Constants.TAGS_APPS, entry.getValue());
            }
            if (entry.getKey().equals(AssetConstants.FILTER_ENVIRONMENT)) {
                mustFilter.put(Constants.TAGS_ENV, entry.getValue());
            }
            if (entry.getKey().equals(AssetConstants.FILTER_RES_TYPE)) {
                targetType = entry.getValue().toString();
            }
        }

        try {
            if (StringUtils.isEmpty(targetType)) {
                List<String> validTypes = getTargetTypesByAssetGroup(assetGroup, domain, null).stream()
                        .map(obj -> obj.get(Constants.TYPE).toString()).collect(Collectors.toList());
                if (validTypes.size() > 1) {
                    try {
                        fieldNames = getDisplayFieldsForTargetType("all_list");
                    } catch (Exception e) {
                        LOGGER.error("Error while fetching field names for all targetType in getListAssets" , e);
                    }
                } else {
                    try {
                        fieldNames = getDisplayFieldsForTargetType(validTypes.get(0));
                    } catch (Exception e) {
                        LOGGER.error("Error while fetching field names for " + validTypes.get(0) + " in getListAssets"
                                , e);
                    }
                }
                assetDetails = getAssetsByAssetGroupBySize(assetGroup, AssetConstants.ALL, mustFilter, validTypes,
                        fieldNames, from, size, searchText);
            } else {
                try {
                    fieldNames = getDisplayFieldsForTargetType(targetType);
                } catch (Exception e) {
                    LOGGER.error("Error while fetching field names for " + targetType + " in getListAssets" , e);
                }
                assetDetails = getAssetsByAssetGroupBySize(assetGroup, targetType, mustFilter, null, fieldNames, from,
                        size, searchText);
            }

        } catch (Exception e) {
            LOGGER.error("Error in getListAssets", e);
        }
        
        List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID, AssetConstants.UNDERSCORE_ENTITY,
                Constants._ID, AssetConstants.UNDERSCORE_LOADDATE, Constants.ES_DOC_PARENT_KEY, Constants.ES_DOC_ROUTING_KEY, AssetConstants.CREATE_TIME,
                AssetConstants.FIRST_DISCOVEREDON, AssetConstants.DISCOVERY_DATE, Constants.LATEST, AssetConstants.CREATION_DATE);
        LOGGER.info("Exiting getListAssets");
        return formGetListResponse(fieldNames,assetDetails,fieldsToBeSkipped);
    }

    @Override
    public long getAssetCount(String assetGroup, Map<String, String> filter, String searchText) {

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(AssetConstants.UNDERSCORE_ENTITY, true);
        mustFilter.put(Constants.LATEST, true);
        String domain = filter.get(Constants.DOMAIN);
        String targetType = "";
        if (filter != null) {
            Iterator<Entry<String, String>> it = filter.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                if (entry.getKey().equals(AssetConstants.FILTER_APPLICATION)) {
                    mustFilter.put(Constants.TAGS_APPS, entry.getValue());
                }
                if (entry.getKey().equals(AssetConstants.FILTER_ENVIRONMENT)) {
                    mustFilter.put(Constants.TAGS_ENV, entry.getValue());
                }
                if (entry.getKey().equals(AssetConstants.FILTER_RES_TYPE)) {
                    targetType = entry.getValue();
                }
            }
        }

        try {
            Map<String, Object> mustTermFilter = null;
            if (StringUtils.isEmpty(targetType)) {
                mustTermFilter = new HashMap<>();
                List<String> validTypes = getTargetTypesByAssetGroup(assetGroup, domain, null).stream()
                        .map(obj -> obj.get(Constants.TYPE).toString()).collect(Collectors.toList());
                mustTermFilter.put(AssetConstants.UNDERSCORE_ENTITY_TYPE_KEYWORD, validTypes);
            } else {
                mustFilter.put(AssetConstants.UNDERSCORE_ENTITY_TYPE_KEYWORD, targetType);
            }
            return esRepository.getTotalDocumentCountForIndexAndType(assetGroup, null, mustFilter, null, null,
                    searchText, mustTermFilter);
        } catch (Exception e) {
            LOGGER.error("Error retrieving inventory from ES in getAssetCount ", e);
        }
        return 0;
    }

    public List<Map<String, Object>> getCpuUtilizationByAssetGroupAndInstanceId(String instanceId) throws DataException {

        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(AssetConstants.AWS_EC2)
                .append("/").append("ec2_utilization").append("/").append(Constants.SEARCH);
        Map<String, Object> utilization = null;
        List<Map<String, Object>> utilizationList = new ArrayList<>();
        try {
            StringBuilder requestBodyCpu = new StringBuilder(
                    "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":{\"#Datetime-CPU-Utilization\":{\"gte\":\"now-30d\",\"lte\":\"now\",\"format\":\"yyyy-MM-dd HH:mm:ss\"}}},{\"match\":{\"Instance_Id.keyword\":\""
                            + instanceId
                            + "\"}}]}},\"aggs\":{\"avg-values-per-day\":{\"date_histogram\":{\"field\":\"#Datetime-CPU-Utilization\",\"interval\":\"day\",\"format\":\"yyyy-MM-dd HH:mm:ss\",\"order\":{\"_key\":\"desc\"}},\"aggs\":{\"Avg-CPU-Utilization\":{\"avg\":{\"field\":\"Avg-CPU-Utilization\"}}}}}}");
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBodyCpu.toString());

            JsonParser parser = new JsonParser();
            JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
            JsonObject aggregations = responseDetailsjson.get("aggregations").getAsJsonObject();
            JsonObject avgvalues = aggregations.get("avg-values-per-day").getAsJsonObject();
            JsonArray buckets = avgvalues.get(Constants.BUCKETS).getAsJsonArray();

            for (JsonElement jsonElement : buckets) {

                JsonObject bucketdetails = jsonElement.getAsJsonObject();
                JsonObject cpuUtilizationObj = bucketdetails.get("Avg-CPU-Utilization").getAsJsonObject();
                if (!cpuUtilizationObj.get(Constants.VALUE).isJsonNull()) {
                    utilization = new HashMap<>();
                    utilization.put("date", bucketdetails.get("key_as_string").getAsString());
                    utilization.put("cpu-utilization", cpuUtilizationObj.get(Constants.VALUE).getAsDouble());
                    utilizationList.add(utilization);
                }
            }
            return utilizationList;

        } catch (Exception e) {
            LOGGER.error("Error retrieving inventory from ES in getCpuUtilizationByAssetGroupAndInstanceId", e);
            throw new DataException(e);
        }
    }

    public List<Map<String, Object>> getDiskUtilizationByAssetGroupAndInstanceId(String instanceId) throws DataException {
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(AssetConstants.AWS_EC2)
                .append("/").append(Constants.QUALYS_INFO).append("/").append(Constants.SEARCH);
        Map<String, Object> utilization = null;
        List<Map<String, Object>> utilizationList = new ArrayList<>();
        try {
            StringBuilder requestBodyCpu = new StringBuilder(
                    "{\"_source\":[\"volume\",\"instanceid\"],\"query\":{\"bool\":{\"must\":[{\"match\":{\"instanceid.keyword\":\""
                            + instanceId + AssetConstants.ESQUERY_CLOSE);
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBodyCpu.toString());

            JsonParser parser = new JsonParser();
            JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
            JsonObject firstHits = responseDetailsjson.get("hits").getAsJsonObject();
            JsonArray secHitsArray = firstHits.get("hits").getAsJsonArray();
            for (JsonElement hits : secHitsArray) {
                JsonObject hitsJson = hits.getAsJsonObject();
                JsonObject source = hitsJson.get(AssetConstants.UNDERSCORE_SOURCE).getAsJsonObject();
                if (!source.get("volume").isJsonNull()) {
                    JsonObject volume = source.get("volume").getAsJsonObject();

                    JsonObject list = volume.get("list").getAsJsonObject();
                    JsonArray hostAssetVolume = list.get("hostAssetVolume").getAsJsonArray();

                    for (JsonElement jsonElement : hostAssetVolume) {
                        utilization = new HashMap<>();
                        JsonObject voldetails = jsonElement.getAsJsonObject();
                        utilization.put("size", voldetails.get("size").getAsString());
                        utilization.put("name", voldetails.get("name").getAsString());
                        utilization.put("free", voldetails.get("free").getAsString());
                        utilizationList.add(utilization);
                    }
                }
            }
            return utilizationList;
        } catch (Exception e) {
            LOGGER.error("Error retrieving inventory from ES in getDiskUtilizationByAssetGroupAndInstanceId", e);
            throw new DataException(e);
        }
    }

    public List<Map<String, Object>> getSoftwareInstalledDetailsByAssetGroupAndInstanceId(String instanceId,
            Integer from, Integer size, String searchText) throws DataException {
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(AssetConstants.AWS_EC2)
                .append("/").append(Constants.QUALYS_INFO).append("/").append(Constants.SEARCH).append("?")
                .append("size").append("=").append(Constants.ES_PAGE_SIZE);
        Map<String, Object> softwareDet = null;
        List<Map<String, Object>> softwareList = new ArrayList<>();
        StringBuilder requestBodyCpu = null;
        try {
            if (StringUtils.isEmpty(searchText)) {
                requestBodyCpu = new StringBuilder(
                        "{\"_source\":[\"software\",\"instanceid\"],\"query\":{\"bool\":{\"must\":[{\"match\":{\"instanceid.keyword\":\""
                                + instanceId + AssetConstants.ESQUERY_CLOSE);
            } else {
                requestBodyCpu = new StringBuilder(
                        "{\"_source\":[\"software\",\"instanceid\"],\"query\":{\"bool\":{\"must\":[{\"term\":{\"instanceid.keyword\":{\"value\":\""
                                + instanceId + "\"}}},{\"match_phrase_prefix\":{\"_all\":\"" + searchText + AssetConstants.ESQUERY_CLOSE);
            }

            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBodyCpu.toString());
            JsonParser parser = new JsonParser();
            JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
            JsonObject firstHits = responseDetailsjson.get("hits").getAsJsonObject();
            JsonArray secHitsArray = firstHits.get("hits").getAsJsonArray();
            for (JsonElement hits : secHitsArray) {
                JsonObject hitsJson = hits.getAsJsonObject();
                JsonObject source = hitsJson.get(AssetConstants.UNDERSCORE_SOURCE).getAsJsonObject();
                if (!source.get("software").isJsonNull()) {
                    JsonObject software = source.get("software").getAsJsonObject();
                    JsonObject list = software.get("list").getAsJsonObject();
                    JsonArray hostAssetSoftware = list.get("hostAssetSoftware").getAsJsonArray();

                    for (JsonElement jsonElement : hostAssetSoftware) {
                        softwareDet = new HashMap<>();
                        JsonObject softwareDetails = jsonElement.getAsJsonObject();
                        softwareDet.put("version", softwareDetails.get("version").getAsString());
                        softwareDet.put("name", softwareDetails.get("name").getAsString());
                        softwareList.add(softwareDet);
                    }
                }
            }
            return softwareList;
        } catch (Exception e) {
            LOGGER.error("Error retrieving inventory from ES in getSoftwareInstalledDetailsByAssetGroupAndInstanceId",
                    e);
            throw new DataException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getEc2ResourceDetailFromRhn(String resourceId) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(AssetConstants.INSTANCEID_KEYWORD, resourceId);
        try {
            return esRepository.getDataFromES(AssetConstants.AWS_EC2, "rhn-info", mustFilter, null, null,
                    Arrays.asList(Constants.INSTANCE_ID, "last_checkin", "ip", "last_boot"), null);
        } catch (Exception e) {
            LOGGER.error("Exception in getEc2ResourceDetailFromRhn ",e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getEc2ResourceDetail(String ag, String resourceId) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(AssetConstants.INSTANCEID_KEYWORD, resourceId);
        try {
            return esRepository.getDataFromES(ag, Constants.EC2, mustFilter, null, null, Arrays.asList(
                    Constants.INSTANCE_ID, "imageid", "publicipaddress", "privateipaddress", "vpcid", "availabilityzone",
                    "subnetid", "instancetype", "accountid", "tags", "accountname", "iaminstanceprofilearn",
                    Constants.STATE_NAME, "monitoringstate", "hostid", "statereasoncode", "virtualizationtype",
                    "rootdevicename", "keyname", "kernelid", Constants.STATE_NAME, "hypervisor", "architecture", "tenancy",
                    "launchtime", "platform"), null);
        } catch (Exception e) {
            LOGGER.error("Exception in getEc2ResourceDetail ",e);
            throw new DataException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getEc2ResourceSecurityGroupDetail(String resourceId) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(AssetConstants.INSTANCEID_KEYWORD, resourceId);
        try {
            return esRepository.getDataFromES(AssetConstants.AWS_EC2, "ec2_secgroups", mustFilter, null, null,
                    Arrays.asList(Constants.RESOURCEID, "securitygroupid", "securitygroupname", "tmonpe"), null);
        } catch (Exception e) {
            LOGGER.error("Exception in getEc2ResourceSecurityGroupDetail ",e);
            throw new DataException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getEc2ResourceBlockDevicesDetail(String resourceId) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(AssetConstants.INSTANCEID_KEYWORD, resourceId);
        try {
            return esRepository.getDataFromES(AssetConstants.AWS_EC2, "ec2_blockdevices", mustFilter, null, null,
                    Arrays.asList(Constants.RESOURCEID, "volumeid"), null);
        } catch (Exception e) {
            LOGGER.error("Exception in getEc2ResourceBlockDevicesDetail ",e);
            throw new DataException(e);
        }
    }

    @Override
	public List<Map<String, Object>> getListAssetsPatchable(String assetGroup, Map<String, String> filter) {

		LOGGER.info("Inside getListAssetsPatchable");
		List<Map<String, Object>> assetList = new ArrayList<>();
		List<Map<String, Object>> assetDetails = new ArrayList<>();
		List<String> sourceFields = null;
		String resourceType = null;
		if (filter.containsKey(AssetConstants.FILTER_RES_TYPE)) {
			resourceType = filter.get(AssetConstants.FILTER_RES_TYPE);
			if (Constants.ONPREMSERVER.equals(resourceType)) {
				sourceFields = getDisplayFieldsForTargetType(resourceType);
				assetDetails.addAll(getListAssetsPathachableForOnPrem(assetGroup, filter, sourceFields));
			} else if (Constants.EC2.equals(resourceType) || Constants.VIRTUALMACHINE.equals(resourceType)) {
				assetDetails.addAll(getListAssetsPathachableForEC2(assetGroup, filter, sourceFields));
				
			}
		} else {
			
			List<String>  validPatchingTypes= Arrays.asList(patchingTypes.split(","));
			List<String> validTargetTypes  = new ArrayList<>(getAssetCountByAssetGroup(assetGroup,AssetConstants.ALL,filter.get(AssetConstants.FILTER_APPLICATION)).keySet());
			List<String>  agPatchingTypes =validTargetTypes.stream().filter(validPatchingTypes::contains).collect(Collectors.toList());
			
			if (agPatchingTypes.size()>1) {
				// source common fields for onprem & cloud
				sourceFields = getDisplayFieldsForTargetType("all_patchable");
			}
			for(String type: agPatchingTypes) {
				if(type.equals(Constants.ONPREMSERVER)) {
					if(agPatchingTypes.size()==1) {
						sourceFields = getDisplayFieldsForTargetType(Constants.ONPREMSERVER);
					}
					assetDetails.addAll(getListAssetsPathachableForOnPrem(assetGroup, filter, sourceFields));
				}else {
					filter.put(AssetConstants.FILTER_RES_TYPE,type);
					assetDetails.addAll(getListAssetsPathachableForEC2(assetGroup, filter, sourceFields));
				}
			} 
		}

		try {
			final List<String> executiveapps;
			if (filter.containsKey(AssetConstants.FILTER_EXEC_SPONSOR)) {
				executiveapps = fetchExecDirectorApps(filter.get(AssetConstants.FILTER_EXEC_SPONSOR),
						"executiveSponsor");
			}else if (filter.containsKey(AssetConstants.FILTER_DIRECTOR)) {
				executiveapps = fetchExecDirectorApps(filter.get(AssetConstants.FILTER_DIRECTOR), "director");
			}else {
				executiveapps = new ArrayList<>();
			}
			List<String> fieldsToBeSkipped = Arrays.asList(Constants.DOCID,
					AssetConstants.UNDERSCORE_ENTITY, Constants._ID, AssetConstants.UNDERSCORE_LOADDATE,
					Constants.ES_DOC_PARENT_KEY, Constants.ES_DOC_ROUTING_KEY, AssetConstants.CREATE_TIME,
					AssetConstants.FIRST_DISCOVEREDON, AssetConstants.DISCOVERY_DATE, Constants.LATEST,
					AssetConstants.CREATION_DATE);
			assetList.addAll(formGetListResponse(sourceFields, assetDetails, fieldsToBeSkipped));
			if (filter.containsKey(AssetConstants.FILTER_EXEC_SPONSOR)
					|| filter.containsKey(AssetConstants.FILTER_DIRECTOR)) {
				
				return assetList.parallelStream().filter(asset-> executiveapps.contains(asset.get(Constants.TAGS_APPLICATION))).collect(Collectors.toList());
				
			}else {
				return assetList;
			}
			
		} catch (Exception e) {
			LOGGER.error("Error in getListAssetsPatchable", e);
		}

		LOGGER.info("Exiting getListAssetsPatchable");
		return assetList;
	}

    @Override
    public List<Map<String, Object>> getListAssetsTaggable(String assetGroup, Map<String, String> filter) {

        LOGGER.info("Inside getListAssetsTaggable");
        List<Map<String, Object>> assetList = new ArrayList<>();
        List<Map<String, Object>> assetDetails = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();

        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustFilterAsset = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();

        String[] tags = mandatoryTags.split(",");
        for (String tag : tags) {
            shouldFilter.put(CommonUtils.convertAttributetoKeyword(tag.trim()), AssetConstants.TAG_NOT_FOUND);
        }

        List<Map<String, Object>> untaggedAssets;
        List<Map<String, Object>> totalAssets;
        StringBuilder sb;
        String type;
        String targetType = "";
        if (filter.containsKey(AssetConstants.FILTER_RES_TYPE)) {
            targetType = filter.get(AssetConstants.FILTER_RES_TYPE);
        }

        List<Map<String, Object>> ruleIdwithTargetType ;
        String ruleIdWithTargetTypeQuery = null;

        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TYPE), Constants.ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.POLICYID), Constants.TAGGIG_POLICY);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.ISSUE_STATUS), Constants.OPEN);

        filter.entrySet()
                .stream()
                .forEach(
                        entry -> {
                            String filterKey = entry.getKey();
                            if (!(filterKey.equals(AssetConstants.FILTER_TAGGED)
                                    || filterKey.equals(AssetConstants.FILTER_RES_TYPE) || filterKey
                                    .equals(AssetConstants.FILTER_TAGNAME))) {
                                if (filterKey.equals(AssetConstants.FILTER_APPLICATION)) {
                                    mustFilter.put(Constants.TAGS_APPS, entry.getValue());
                                    mustFilterAsset.put(Constants.TAGS_APPS, entry.getValue());
                                }
                                if (filterKey.equals(AssetConstants.FILTER_ENVIRONMENT)) {
                                    mustFilter.put(Constants.TAGS_ENV, entry.getValue());
                                    mustFilterAsset.put(Constants.TAGS_ENV, entry.getValue());
                                }
                            }
                        });
        if (!Strings.isNullOrEmpty(targetType)) {
            sb = new StringBuilder();
            type = sb.append("'").append(targetType).append("'").toString();
            ruleIdWithTargetTypeQuery = "SELECT  A.targetType FROM cf_RuleInstance A, cf_Policy B WHERE A.policyId = B.policyId AND A.status = 'ENABLED' AND B.policyId = 'PacMan_TaggingRule_version-1' AND A.targetType = "
                    + type;
            ruleIdwithTargetType = rdsRepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
            try {
                fieldNames = getDisplayFieldsForTargetType(targetType);
            } catch (Exception e) {
                LOGGER.error(AssetConstants.ERROR_FETCHING_FIELDNAMES , e);
            }
            if (!ruleIdwithTargetType.isEmpty()) {
                mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TARGET_TYPE), targetType);
                try {
                    if (filter.containsKey(AssetConstants.FILTER_TAGNAME)) {
                        mustFilter.put(
                                CommonUtils.convertAttributetoKeyword(filter.get(AssetConstants.FILTER_TAGNAME)),
                                "Not Found");
                        shouldFilter = null;
                    }

                    if (filter.containsKey(AssetConstants.FILTER_TAGGED)) {
                        untaggedAssets = esRepository.getDataFromES(assetGroup, null, mustFilter, null, shouldFilter,
                                null, null);
                        List<String> untaggedResourceIds = untaggedAssets.parallelStream()
                                .map(obj -> obj.get(Constants.RESOURCEID).toString()).collect(Collectors.toList());
                        totalAssets = getAssetsByAssetGroup(assetGroup, targetType, mustFilterAsset, null, fieldNames);
                        if (filter.get(AssetConstants.FILTER_TAGGED).equals(AssetConstants.FALSE)) {
                            assetDetails = totalAssets.parallelStream()
                                    .filter(asset -> untaggedResourceIds.contains(asset.get(Constants.RESOURCEID)))
                                    .collect(Collectors.toList());
                        } else {
                            assetDetails = totalAssets.parallelStream()
                                    .filter(asset -> !untaggedResourceIds.contains(asset.get(Constants.RESOURCEID)))
                                    .collect(Collectors.toList());
                        }
                    } else {
                        assetDetails = getAssetsByAssetGroup(assetGroup, targetType, mustFilterAsset, null, fieldNames);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error in getListAssetsTaggable", e);
                }
            }else{
            	 assetDetails = getAssetsByAssetGroup(assetGroup, targetType, new HashMap(), null, fieldNames);
            }
        } else {
            ruleIdWithTargetTypeQuery = "SELECT  A.targetType FROM cf_RuleInstance A, cf_Policy B WHERE A.policyId = B.policyId AND A.status = 'ENABLED' AND B.policyId = 'PacMan_TaggingRule_version-1'";
            ruleIdwithTargetType = rdsRepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
            List<String> validTypes = ruleIdwithTargetType.stream()
                    .map(obj -> obj.get(Constants.TARGET_TYPE).toString()).collect(Collectors.toList());
            if (validTypes.size() > 1) {
                try {
                    fieldNames = getDisplayFieldsForTargetType("all_taggable");
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_FETCHING_FIELDNAMES , e);
                }
            } else {
                try {
                    fieldNames = getDisplayFieldsForTargetType(validTypes.get(0));
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_FETCHING_FIELDNAMES , e);
                }
            }
            try {
                if (filter.containsKey(AssetConstants.FILTER_TAGNAME)) {
                    mustFilter.put(CommonUtils.convertAttributetoKeyword(filter.get(AssetConstants.FILTER_TAGNAME)),
                            "Not Found");
                    shouldFilter = null;
                }
                if (filter.containsKey(AssetConstants.FILTER_TAGGED)) {
                    untaggedAssets = esRepository.getDataFromES(assetGroup, null, mustFilter, null, shouldFilter,
                            fieldNames, null);
                    List<String> untaggedResourceIds = untaggedAssets
                            .parallelStream()
                            .map(obj -> obj.get(Constants.RESOURCEID).toString()
                                    + obj.get(Constants.TARGET_TYPE).toString()).collect(Collectors.toList());
                    totalAssets = getAssetsByAssetGroup(assetGroup, AssetConstants.ALL, mustFilterAsset, validTypes,
                            fieldNames);
                    if (filter.get(AssetConstants.FILTER_TAGGED).equals(AssetConstants.FALSE)) {
                        assetDetails = totalAssets
                                .parallelStream()
                                .filter(asset -> untaggedResourceIds.contains(asset.get(Constants.RESOURCEID)
                                        .toString() + asset.get(AssetConstants.UNDERSCORE_ENTITY_TYPE).toString()))
                                .collect(Collectors.toList());
                    } else {
                        assetDetails = totalAssets
                                .parallelStream()
                                .filter(asset -> !untaggedResourceIds.contains(asset.get(Constants.RESOURCEID)
                                        .toString() + asset.get(AssetConstants.UNDERSCORE_ENTITY_TYPE).toString()))
                                .collect(Collectors.toList());
                    }
                } else {
                    assetDetails = getAssetsByAssetGroup(assetGroup, AssetConstants.ALL, mustFilterAsset, validTypes,
                            fieldNames);
                }

            } catch (Exception e) {
                LOGGER.error("Error in getListAssetsTaggable", e);
            }
        }

        if (!CollectionUtils.isEmpty(fieldNames)) {
            final List<String> fieldNamesCopy = fieldNames;
            assetDetails.parallelStream().forEach(assetDetail -> {
                Map<String, Object> asset = new LinkedHashMap<>();
                for (String fieldName : fieldNamesCopy) {
                    if (!Constants.TARGET_TYPE.equals(fieldName)) {
                        if (!assetDetail.containsKey(fieldName)) {
                            asset.put(fieldName, "");
                        } else {
                            asset.put(fieldName, assetDetail.get(fieldName));
                        }
                    }
                }
                synchronized (assetList) {
                    assetList.add(asset);
                }
            });
            LOGGER.info("Exiting getListAssetsTaggable");
            return assetList;
        } else {
            assetDetails.parallelStream().forEach(
                    assetDetail -> {
                        Map<String, Object> asset = new LinkedHashMap<>();
                        asset.put(Constants.RESOURCEID, assetDetail.get(Constants.RESOURCEID));
                        assetDetail.forEach((key, value) -> {
                            if (!Arrays.asList(Constants.RESOURCEID, Constants.DOCID, AssetConstants.UNDERSCORE_ENTITY,
                                    Constants._ID, AssetConstants.UNDERSCORE_LOADDATE, Constants.ES_DOC_PARENT_KEY,
                                    Constants.ES_DOC_ROUTING_KEY, AssetConstants.CREATE_TIME,
                                    AssetConstants.FIRST_DISCOVEREDON, AssetConstants.DISCOVERY_DATE, Constants.LATEST,
                                    AssetConstants.CREATION_DATE).contains(key)) {
                                asset.put(key, value);
                            }
                        });
                        synchronized (assetList) {
                            assetList.add(asset);
                        }
                    });
            LOGGER.info("Exiting getListAssetsTaggable");
            return assetList;
        }
    }

    @Override
	public List<Map<String, Object>> getListAssetsVulnerable(String assetGroup, Map<String, String> filter) {

		LOGGER.info("Inside getListAssetsVulnerable");
		List<Map<String, Object>> assetList = new ArrayList<>();
		List<Map<String, Object>> assetDetails = new ArrayList<>();

		List<String> validTargetTypes  = new ArrayList<>(getAssetCountByAssetGroup(assetGroup,AssetConstants.ALL,filter.get(AssetConstants.FILTER_APPLICATION)).keySet());
		String[] vulnTypesArray = vulnTypes.split(",");
		List<String> vulnTargetTypes = new ArrayList<>();

		for (String vulnType : vulnTypesArray) {
			if (validTargetTypes.contains(vulnType.trim())) {
				vulnTargetTypes.add(vulnType);
			}
		}
		
		List<String> fieldNames = null;
		try {
			if (vulnTargetTypes.size()>1) {
				fieldNames = getDisplayFieldsForTargetType("all_vulnerable");
			}
		} catch (Exception e) {
			LOGGER.error(AssetConstants.ERROR_FETCHING_FIELDNAMES, e);
		}
		if (!vulnTargetTypes.isEmpty()) {
			for (String parentType : vulnTargetTypes) {
				StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup);
				urlToQueryBuffer.append("/").append(parentType);
				urlToQueryBuffer.append("/").append(Constants.SEARCH).append("?scroll=")
						.append(Constants.ES_PAGE_SCROLL_TTL);

				String urlToQuery = urlToQueryBuffer.toString();
				String urlToScroll = new StringBuilder(esUrl).append("/").append(Constants.SEARCH).append("/scroll")
						.toString();

				StringBuilder requestBody = new StringBuilder(
						"{\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"has_child\":{\"type\":\"vulninfo\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"term\":{\"qid\":");
				requestBody.append(filter.get("qid"));
				requestBody.append("}}]}}}},{\"term\":{\"latest\":true}}");
				if (filter.containsKey(AssetConstants.FILTER_APPLICATION)) {
					requestBody.append(",{\"term\":{\"tags.Application.keyword\":\"");
					requestBody.append(filter.get(AssetConstants.FILTER_APPLICATION));
					requestBody.append("\"}}");
				}
				if (filter.containsKey(AssetConstants.FILTER_ENVIRONMENT)) {
					requestBody.append(",{\"term\":{\"tags.Environment.keyword\":\"");
					requestBody.append(filter.get(AssetConstants.FILTER_ENVIRONMENT));
					requestBody.append("\"}}");
				}
				if (filter.containsKey(AssetConstants.FILTER_RES_TYPE)) {
					requestBody.append(",{\"term\":{\"_entitytype.keyword\":\"");
					requestBody.append(filter.get(AssetConstants.FILTER_RES_TYPE));
					requestBody.append("\"}}");
				}
				requestBody.append("]}}}");
				Long totalDocs = getTotalDocCount(assetGroup, parentType, "{" + requestBody.toString().substring(14));
				String request = requestBody.toString();
				String scrollId = null;
				if (totalDocs > 0) {
					for (int index = 0; index <= (totalDocs / Constants.ES_PAGE_SIZE); index++) {
						String responseDetails = null;
						try {
							if (!Strings.isNullOrEmpty(scrollId)) {
								request = esRepository.buildScrollRequest(scrollId, Constants.ES_PAGE_SCROLL_TTL);
								urlToQuery = urlToScroll;
							}
							responseDetails = PacHttpUtils.doHttpPost(urlToQuery, request);
							scrollId = esRepository.processResponseAndSendTheScrollBack(responseDetails, assetDetails);
						} catch (Exception e) {
							LOGGER.error("Error in getListAssetsVulnerable", e);
						}
					}
				}
			}

			List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID,
					AssetConstants.UNDERSCORE_ENTITY, Constants._ID, AssetConstants.UNDERSCORE_LOADDATE,
					Constants.ES_DOC_PARENT_KEY, Constants.ES_DOC_ROUTING_KEY, AssetConstants.CREATE_TIME,
					AssetConstants.FIRST_DISCOVEREDON, AssetConstants.DISCOVERY_DATE, Constants.LATEST,
					AssetConstants.CREATION_DATE);
			LOGGER.info("Exiting getListAssetsVulnerable");
			assetList.addAll(formGetListResponse(fieldNames, assetDetails, fieldsToBeSkipped));
		}
		return assetList;
	}

    @Override
    public List<Map<String, Object>> getListAssetsScanned(String assetGroup, Map<String, String> filter) {

        LOGGER.info("Inside getListAssetsScanned");
        List<Map<String, Object>> assetDetails = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();

        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustFilterAsset = new HashMap<>();

        String targetType = "";
        if (filter.containsKey(AssetConstants.FILTER_RES_TYPE)) {
            targetType = filter.get(AssetConstants.FILTER_RES_TYPE);
        }

        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TYPE), Constants.ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.ISSUE_STATUS), Constants.OPEN);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.RULEID),
                filter.get(AssetConstants.FILTER_RULEID));
        mustFilterAsset.put(Constants.LATEST, Constants.TRUE);
        mustFilterAsset.put(Constants.RULEID,
                filter.get(AssetConstants.FILTER_RULEID));

        filter.entrySet()
                .stream()
                .forEach(
                        entry -> {
                            if (!(entry.getKey().equals(AssetConstants.FILTER_RULEID)
                                    || entry.getKey().equals(AssetConstants.FILTER_RES_TYPE) || entry.getKey().equals(
                                    AssetConstants.FILTER_COMPLIANT))) {
                                if (entry.getKey().equals(AssetConstants.FILTER_APPLICATION)) {
                                    mustFilter.put(Constants.TAGS_APPS, entry.getValue());
                                    mustFilterAsset.put(Constants.TAGS_APPS, entry.getValue());
                                }
                                if (entry.getKey().equals(AssetConstants.FILTER_ENVIRONMENT)) {
                                    mustFilter.put(Constants.TAGS_ENV, entry.getValue());
                                    mustFilterAsset.put(Constants.TAGS_ENV, entry.getValue());
                                }
                            }
                        });

        try {
            List<Map<String, Object>> nonCompliantAssets = esRepository.getDataFromES(assetGroup, null, mustFilter,
                    null, null, null, null);
            if (!nonCompliantAssets.isEmpty()) {
                String policy = nonCompliantAssets.get(0).get("policyId").toString();
                if ("PacMan_TaggingRule_version-1".equals(policy)) {
                    String[] tags = mandatoryTags.split(",");
                    nonCompliantAssets = nonCompliantAssets.stream().filter(issue -> {
                        boolean compliant = true;
                        for (String tag : tags) {
                            if (AssetConstants.TAG_NOT_FOUND.equals(issue.get(tag))) {
                                compliant = false;
                                break;
                            }
                        }
                        return !compliant;
                    }).collect(Collectors.toList());
                }
            }
            if (filter.containsKey(AssetConstants.FILTER_COMPLIANT)) {
                List<String> nonCompliantresourceIds = nonCompliantAssets.parallelStream()
                        .map(obj -> obj.get(Constants.RESOURCEID).toString()).collect(Collectors.toList());
                if (StringUtils.isEmpty(targetType)) {
                    targetType = getTargetTypeByRuleId(assetGroup, filter.get(AssetConstants.FILTER_RULEID));
                }
                try {
                    fieldNames = getDisplayFieldsForTargetType(targetType);
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_FETCHING_FIELDNAMES , e);
                }
                List<Map<String, Object>> totalAssets = getAssetsByAssetGroup(assetGroup, targetType, mustFilterAsset,
                        null, fieldNames);

                if (filter.get(AssetConstants.FILTER_COMPLIANT).equals(AssetConstants.FALSE)) {
                    assetDetails = totalAssets.parallelStream()
                            .filter(asset -> nonCompliantresourceIds.contains(asset.get(Constants.RESOURCEID)))
                            .collect(Collectors.toList());
                } else {
                    assetDetails = totalAssets.parallelStream()
                            .filter(asset -> !nonCompliantresourceIds.contains(asset.get(Constants.RESOURCEID)))
                            .collect(Collectors.toList());
                }
            } else {
                if (StringUtils.isEmpty(targetType)) {
                    targetType = getTargetTypeByRuleId(assetGroup, filter.get(AssetConstants.FILTER_RULEID));
                }
                try {
                    fieldNames = getDisplayFieldsForTargetType(targetType);
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_FETCHING_FIELDNAMES , e);
                }
                assetDetails = getAssetsByAssetGroup(assetGroup, targetType, mustFilterAsset, null, fieldNames);
            }
        } catch (Exception e) {
            LOGGER.error("Error in getListAssetsScanned", e);
        }
        
        List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID, AssetConstants.UNDERSCORE_ENTITY,
                Constants._ID, AssetConstants.UNDERSCORE_LOADDATE, Constants.ES_DOC_PARENT_KEY, Constants.ES_DOC_ROUTING_KEY, AssetConstants.CREATE_TIME,
                AssetConstants.FIRST_DISCOVEREDON, AssetConstants.DISCOVERY_DATE, Constants.LATEST, AssetConstants.CREATION_DATE);
        LOGGER.info("Exiting getListAssetsScanned");
        return formGetListResponse(fieldNames, assetDetails, fieldsToBeSkipped);

    }

    @Override
    public List<Map<String, Object>> getResourceDetail(String ag, String resourceType, String resourceId)
            throws DataException {

        String indexName = ag;
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("_resourceid.keyword", resourceId);
        try {
            return esRepository.getDataFromES(indexName, resourceType, mustFilter, null, null, null, null);
        } catch (Exception e) {
            LOGGER.error("Exception in getResourceDetail ",e);
            throw new DataException(e);
        }
    }

    private List<Map<String, Object>> getAssetsByAssetGroup(String assetGroupName, String type,
            Map<String, Object> mustFilter, List<String> targetTypes, List<String> fieldNames) {
        
        mustFilter.put(Constants.LATEST, Constants.TRUE);
        mustFilter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
        
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        if (Constants.EC2.equals(type) || AssetConstants.ALL.equals(type)) {
            if(mustFilter.containsKey(AssetConstants.FILTER_RULEID) && 
                    ((mustFilter.get(AssetConstants.FILTER_RULEID).toString().equalsIgnoreCase(Constants.CLOUD_QUALYS_RULE) && qualysEnabled) || mustFilter.get(AssetConstants.FILTER_RULEID).toString().equalsIgnoreCase(Constants.SSM_AGENT_RULE))) {
                return getLongRunningInstances(assetGroupName, type, fieldNames);
            } else {
                shouldFilter.put(Constants.STATE_NAME, Constants.RUNNING);
                shouldFilter.put(Constants.STATE_NAME, AssetConstants.STOPPED);
                shouldFilter.put(Constants.STATE_NAME, AssetConstants.STOPPING);
            }
        }
        mustFilter.remove(AssetConstants.FILTER_RULEID);
        
        List<Map<String, Object>> assets = new ArrayList<>();
        try {
            if (AssetConstants.ALL.equals(type)) {
                try {
                    boolean ec2Exists = false;
                    Map<String, Object> mustTermsFilter = new HashMap<>();
                    if (targetTypes.contains(Constants.EC2)) {
                        targetTypes.remove(Constants.EC2);
                        ec2Exists = true;
                    }
                    mustTermsFilter.put(AssetConstants.UNDERSCORE_ENTITY_TYPE_KEYWORD, targetTypes);

                    assets = esRepository.getDataFromES(assetGroupName, null, mustFilter, null, null, fieldNames,
                            mustTermsFilter);
                    if (ec2Exists) {
                        assets.addAll(esRepository.getDataFromES(assetGroupName, Constants.EC2, mustFilter, null,
                                shouldFilter, fieldNames, null));
                    }
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_GETASSETSBYAG, e);
                }

            } else {
                if (Constants.ONPREMSERVER.equalsIgnoreCase(type)) {
                    fieldNames = getDisplayFieldsForTargetType(type);
                }
                assets = esRepository.getDataFromES(assetGroupName, type, mustFilter, null, shouldFilter, fieldNames,
                        null);
            }
        } catch (Exception e) {
            LOGGER.error(AssetConstants.ERROR_GETASSETSBYAG, e);
        }

        return assets;
    }
    
    private List<Map<String,Object>> getLongRunningInstances(String assetGroup, String type, List<String> fieldNames) {
        
        List<Map<String,Object>> assetDetails = new ArrayList<>();
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(assetGroup);
        urlToQueryBuffer.append("/").append(type);
        urlToQueryBuffer.append("/").append(Constants.SEARCH).append("?scroll=")
                .append(Constants.ES_PAGE_SCROLL_TTL);

        String urlToQuery = urlToQueryBuffer.toString();
        String urlToScroll = new StringBuilder(esUrl).append("/").append(Constants.SEARCH).append("/scroll")
                .toString();
        StringBuilder requestCount = new StringBuilder("{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"statename\":\"running\"}}],"
                + "\"should\":[{\"script\":{\"script\":\"LocalDate.parse(doc['firstdiscoveredon.keyword'].value.substring(0,10))"
                + ".isBefore(LocalDate.from(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault())).minusDays(7))\"}},"
                + "{\"has_child\":{\"type\":\"qualysinfo\",\"query\":{\"match\":{\"latest\":\"true\"}}}}],\"minimum_should_match\":1}}}");
        
        Long totalDocs = getTotalDocCount(assetGroup, type, requestCount.toString());
        StringBuilder requestBody = new StringBuilder("{\"_source\":").append(new Gson().toJson(fieldNames)).append(",").append("\"size\":10000,").
                append(requestCount.toString().substring(1, requestCount.length()));
        String request = requestBody.toString();
        String scrollId = null;
        if(totalDocs>0){
            for (int index = 0; index <= (totalDocs / Constants.ES_PAGE_SIZE); index++) {
                String responseDetails = null;
                try {
                    if (!Strings.isNullOrEmpty(scrollId)) {
                        request = esRepository.buildScrollRequest(scrollId, Constants.ES_PAGE_SCROLL_TTL);
                        urlToQuery = urlToScroll;
                    }
                    responseDetails = PacHttpUtils.doHttpPost(urlToQuery, request);
                    scrollId = esRepository.processResponseAndSendTheScrollBack(responseDetails, assetDetails);
                } catch (Exception e) {
                    LOGGER.error("Error in getListAssetsVulnerable", e);
                }
            }
        }
        return assetDetails;
    }

    private List<Map<String, Object>> getAssetsByAssetGroupBySize(String assetGroupName, String type,
            Map<String, Object> mustFilter, List<String> targetTypes, List<String> fieldNames, int from, int size,
            String searchText) {
        mustFilter.put(Constants.LATEST, Constants.TRUE);
        mustFilter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        if (Constants.EC2.equals(type)) {
            shouldFilter.put(Constants.STATE_NAME, Constants.RUNNING);
            shouldFilter.put(Constants.STATE_NAME, AssetConstants.STOPPED);
            shouldFilter.put(Constants.STATE_NAME, AssetConstants.STOPPING);
        }
        
        List<Map<String, Object>> assets = new ArrayList<>();
        try {
            if (AssetConstants.ALL.equals(type)) {
                try {
                    Map<String, Object> mustTermsFilter = new HashMap<>();
                    mustTermsFilter.put(AssetConstants.UNDERSCORE_ENTITY_TYPE_KEYWORD, targetTypes);
                    assets = esRepository.getDataFromESBySize(assetGroupName, null, mustFilter, null, null, fieldNames,
                            from, size, searchText, mustTermsFilter);
                } catch (Exception e) {
                    LOGGER.error(AssetConstants.ERROR_GETASSETSBYAG, e);
                }

            } else {
                if (Constants.ONPREMSERVER.equalsIgnoreCase(type)) {
                    fieldNames = getDisplayFieldsForTargetType(type);
                }
                assets = esRepository.getDataFromESBySize(assetGroupName, type, mustFilter, null, shouldFilter,
                        fieldNames, from, size, searchText, null);
            }
        } catch (Exception e) {
            LOGGER.error(AssetConstants.ERROR_GETASSETSBYAG, e);
        }

        return assets;
    }
    
    @SuppressWarnings("unchecked")
    private long getTotalDocCount(String index, String type, String requestBody) {
        StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(index).append("/").append(type)
                .append("/").append("_count");
        String responseDetails = null;
        Gson gson = new GsonBuilder().create();
        try {
            responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody);
            Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
            return (long) (Double.parseDouble(response.get("count").toString()));
        } catch (Exception e) {
            LOGGER.error("Error in getTotalDocCount", e);
            return 0;
        }
    }

    private List<String> fetchExecDirectorApps(String name, String exeOrDirec) {
        List<String> executiveApps = new ArrayList<>();
        List<Map<String, Object>> execAndDirectorInfo;
        try {
            execAndDirectorInfo = esRepository.getDataFromES("aws_apps", "apps", null, null, null,
                    Arrays.asList("appTag", "director", "executiveSponsor"), null);
            for (Map<String, Object> execMap : execAndDirectorInfo) {
                if (name.equals(execMap.get(exeOrDirec))) {
                    executiveApps.add(execMap.get("appTag").toString());
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in fetchExecDirectorApps", e);
        }
        return executiveApps;
    }

    public List<Map<String, Object>> getOpenPortDetailsByInstanceId(String instanceId, Integer from, Integer size,
            String searchText) throws DataException {
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(AssetConstants.AWS_EC2)
                .append("/").append(Constants.QUALYS_INFO).append("/").append(Constants.SEARCH).append("?")
                .append("size").append("=").append(Constants.ES_PAGE_SIZE);
        Map<String, Object> openPortDet = null;
        List<Map<String, Object>> openPortList = new ArrayList<>();
        StringBuilder requestBodyCpu = null;
        try {
            if (StringUtils.isEmpty(searchText)) {
                requestBodyCpu = new StringBuilder(
                        "{\"_source\":[\"openPort\",\"instanceid\"],\"query\":{\"bool\":{\"must\":[{\"match\":{\"instanceid.keyword\":\""
                                + instanceId + AssetConstants.ESQUERY_CLOSE);
            } else {
                requestBodyCpu = new StringBuilder(
                        "{\"_source\":[\"openPort\",\"instanceid\"],\"query\":{\"bool\":{\"must\":[{\"term\":{\"instanceid.keyword\":{\"value\":\""
                                + instanceId + "\"}}},{\"match_phrase_prefix\":{\"_all\":\"" + searchText + AssetConstants.ESQUERY_CLOSE);
            }
            String responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBodyCpu.toString());
            JsonParser parser = new JsonParser();
            JsonObject responseDetailsjson = parser.parse(responseDetails).getAsJsonObject();
            JsonObject firstHits = responseDetailsjson.get("hits").getAsJsonObject();
            JsonArray secHitsArray = firstHits.get("hits").getAsJsonArray();
            for (JsonElement hits : secHitsArray) {
                JsonObject hitsJson = hits.getAsJsonObject();
                JsonObject source = hitsJson.get(AssetConstants.UNDERSCORE_SOURCE).getAsJsonObject();
                if (!source.get("openPort").isJsonNull()) {
                    JsonObject openPort = source.get("openPort").getAsJsonObject();
                    JsonObject list = openPort.get("list").getAsJsonObject();
                    JsonArray hostAssetOpenPort = list.get("hostAssetOpenPort").getAsJsonArray();

                    for (JsonElement jsonElement : hostAssetOpenPort) {
                        openPortDet = new HashMap<>();
                        JsonObject openPortDetails = jsonElement.getAsJsonObject();
                        openPortDet.put("protocol", openPortDetails.get("protocol").getAsString());
                        openPortDet.put("port", openPortDetails.get("port").getAsString());
                        openPortDet.put("serviceId", openPortDetails.get("serviceId").getAsString());
                        if (!openPortDetails.get(AssetConstants.SERVICE_NAME).isJsonNull()) {
                            openPortDet.put(AssetConstants.SERVICE_NAME,
                                    openPortDetails.get(AssetConstants.SERVICE_NAME).getAsString());
                        } else {
                            openPortDet.put(AssetConstants.SERVICE_NAME, "");
                        }
                        openPortList.add(openPortDet);
                    }
                }
            }
            return openPortList;

        } catch (Exception e) {
            LOGGER.error("Error retrieving inventory from ES in getOpenPortDetailsByInstanceId", e);
            throw new DataException(e);
        }
    }

    private String getTargetTypeByRuleId(String assetGroup, String ruleId) {

        LOGGER.info("Getting Target type for Rule id : " + ruleId);
        List<String> targetTypes = getTargetTypesByAssetGroup(assetGroup, null, null).stream()
                .map(obj -> obj.get(Constants.TYPE).toString()).collect(Collectors.toList());
        String ttypesTemp;
        String ttypes = null;
        for (String name : targetTypes) {
            ttypesTemp = new StringBuilder().append('\'').append(name).append('\'').toString();
            if (Strings.isNullOrEmpty(ttypes)) {
                ttypes = ttypesTemp;
            } else {
                ttypes = new StringBuilder().append(ttypes).append(",").append(ttypesTemp).toString();
            }
        }
        String ruleIdWithTargetTypeQuery = "SELECT ruleId, targetType FROM cf_RuleInstance WHERE STATUS = 'ENABLED'AND targetType IN ("
                + ttypes + ")";
        List<Map<String, Object>> ruleIdwithTargetType = rdsRepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
        Map<String, String> ruleIdwithruleTargetTypeMap = ruleIdwithTargetType.stream().collect(
                Collectors.toMap(s -> (String) s.get(Constants.RULEID), s -> (String) s.get(Constants.TARGET_TYPE)));

        return ruleIdwithruleTargetTypeMap.get(ruleId);
    }

    @Override
	public Map<String, Object> getResourceCreateInfo(String resourceId) throws DataException {
		Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
				Pattern.CASE_INSENSITIVE);

		String url = heimdallEsesUrl + "/pacman-resource-claim/_search";
		String request = "{\"query\": {\"match\": {\"resourceid.keyword\": \"" + resourceId + "\"}}}";
		String responseDetails;
		try {
			responseDetails = PacHttpUtils.doHttpPost(url, request);
		} catch (Exception e) {
			LOGGER.error("Exception in getResourceCreateInfo ", e);
			throw new DataException(e);
		}

		JsonObject responseDetailsjson = new JsonParser().parse(responseDetails).getAsJsonObject();
		JsonArray hits = responseDetailsjson.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
		Map<String, Object> map = new HashMap<>();

		if (hits.size() > 0) {
			JsonObject createInfoObj = hits.get(0).getAsJsonObject().get(AssetConstants.UNDERSCORE_SOURCE)
					.getAsJsonObject(); // Exp
			Gson gson = new Gson();
			map = (Map<String, Object>) gson.fromJson(createInfoObj, map.getClass());

			// User better key names for createdBy and creationDate
			Object obj = map.remove("user");
			map.put("createdBy", obj);

			obj = map.remove("time");
			map.put(AssetConstants.CREATION_DATE, obj);

			convertNullToBlankStr(map);

			if (map.get("createdBy").toString().indexOf("/") != -1) {
				String userIdStr = map.get("createdBy").toString()
						.substring(map.get("createdBy").toString().indexOf("/") + 1);
				try {
					Map<String, Object> slashUserNameMustFilter = new HashMap<>();
					slashUserNameMustFilter.put("_resourceid.keyword", userIdStr);
					List<Map<String, Object>> adUserReturn = esRepository.getDataFromES("aws_aduser", null,
							slashUserNameMustFilter, null, null, Arrays.asList("mail"), null);
					map.put("email", getValueFromList(adUserReturn, "mail"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			convertNullToBlankStr(map);

			String userName = retrieveOu(map);
			String appId = "";
			if (userName != null && userName.length() >= 4 && userName.substring(0, 4).endsWith("_")) {
				appId = userName.substring(0, 3);
			} else if (userName != null) {
				try {
					Map<String, Object> userNameMustFilter = new HashMap<>();
					userNameMustFilter.put("_resourceid.keyword", userName);
					List<Map<String, Object>> adUserReturn = esRepository.getDataFromES("aws_aduser", null,
							userNameMustFilter, null, null, Arrays.asList("mail"), null);
					if (!adUserReturn.isEmpty()) {
						map.put("email", getValueFromList(adUserReturn, "mail"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Map<String, Object> plOuMustFilter = new HashMap<>();
			plOuMustFilter.put(CommonUtils.convertAttributetoKeyword("appID"), appId);
			try {
				List<Map<String, Object>> plOuEmail = esRepository.getDataFromES("aws_apps", null, plOuMustFilter, null,
						null, Arrays.asList("projectLead"), null);
				if (!plOuEmail.isEmpty()) {
					map.put("projectLead", getValueFromList(plOuEmail, "projectLead"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			convertNullToBlankStr(map);

		}

		try {
			Map<String, Object> plResMustFilter = new HashMap<>();
			plResMustFilter.put(CommonUtils.convertAttributetoKeyword("_resourceid"), resourceId);
			List<Map<String, Object>> tagsInput = esRepository.getDataFromES(Constants.MASTER_ALIAS, null, plResMustFilter, null, null,
					Arrays.asList("tags.Owner", "tags.Application"), null);

			map.put("ownerEmail", getValueFromList(tagsInput, "tags.Owner"));

			Map<String, Object> appTagMustFilter = new HashMap<>();
			appTagMustFilter.put(CommonUtils.convertAttributetoKeyword("appTag"),
					getValueFromList(tagsInput, "tags.Application"));
			List<Map<String, Object>> plFromAppTag = esRepository.getDataFromES("aws_apps", null, appTagMustFilter,
					null, null, Arrays.asList("projectLead"), null);
			if (!plFromAppTag.isEmpty() && (map.get("projectLead") == null || map.get("projectLead").equals(""))) {
				map.put("projectLead", getValueFromList(plFromAppTag, "projectLead"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		convertNullToBlankStr(map);

		return map;
	}
    
    private void convertNullToBlankStr(Map<String, Object> map) {
		if (map.get("email") == null || "null".equals(map.get("email").toString())) {
			map.put("email", "");
		}
		if (map.get("projectLead") == null || "null".equals(map.get("projectLead").toString())) {
			map.put("projectLead", "");
		}
		if (map.get(AssetConstants.CREATION_DATE) == null
				|| "null".equals(map.get(AssetConstants.CREATION_DATE).toString())) {
			map.put(AssetConstants.CREATION_DATE, "");
		}
		if (map.get("createdBy") == null || "null".equals(map.get("createdBy").toString())) {
			map.put("createdBy", "");
		}
	}

	private String retrieveOu(Map<String, Object> map) {
		map = (Map<String, Object>) ((Map<String, Object>) map.get("detail")).get("userIdentity");

		String userName = null;
		if ("Root".equalsIgnoreCase(map.get("type").toString())
				|| "IAMUser".equalsIgnoreCase(map.get("type").toString())) {
			userName = map.get("userName").toString();
		} else if ("AssumedRole".equalsIgnoreCase(map.get("type").toString())) {
			Map<String, Object> sessionContext = (Map<String, Object>) (map.get("sessionContext"));
			Map<String, Object> sessionIssuer = (Map<String, Object>) sessionContext.get("sessionIssuer");
			userName = sessionIssuer.get("userName").toString();
		}
		return userName;
	}

	private Object getValueFromList(List<Map<String, Object>> input, String str) {
		Iterator<Map<String, Object>> inputListIterator = input.iterator();

		while (inputListIterator.hasNext()) {

			Map<String, Object> inputMap = inputListIterator.next();
			if (inputMap.get(str) != null) {
				return inputMap.get(str).toString();
			}

		}
		return "";
	}

    @Override
    public Map<String, Long> getNotificationSummary(String instanceId) throws DataException {

        Map<String, Object> matchEntryMap = new HashMap<>();
        matchEntryMap.put("entityvalue.keyword", instanceId);

        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put(Constants.MATCH, matchEntryMap);

        Map<String, Object> childEntryMap = new LinkedHashMap<>();
        childEntryMap.put(Constants.TYPE, "phd_entities");
        childEntryMap.put(AssetConstants.QUERY, matchMap);

        // Create must map
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(Constants.LATEST, Constants.TRUE);
        mustFilter.put("has_child", childEntryMap);

        String aggsFilterFieldName = CommonUtils.convertAttributetoKeyword("statuscode");

        try {
            return esRepository.getTotalDistributionForIndexAndType("aws_phd", "phd", mustFilter, null, null,
                    aggsFilterFieldName, Constants.TEN, null);
        } catch (Exception e) {
            LOGGER.error("Exception in getNotificationSummary ",e);
            throw new DataException(e);
        }

    }

    @Override
    public List<Map<String, Object>> getNotificationDetails(String instanceId, Map<String, String> filters,
            String searchText) throws DataException {

        Map<String, Object> matchEntryMap = new HashMap<>();
        matchEntryMap.put("entityvalue.keyword", instanceId);

        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put(Constants.MATCH, matchEntryMap);

        Map<String, Object> childEntryMap = new LinkedHashMap<>();
        childEntryMap.put(Constants.TYPE, "phd_entities");
        childEntryMap.put(AssetConstants.QUERY, matchMap);

        // Create must map
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(Constants.LATEST, Constants.TRUE);
        mustFilter.put("has_child", childEntryMap);

        if (null != filters && filters.size() > 0) {
            filters.forEach((key, value) -> mustFilter.put(key, value));
        }

        try {
            return esRepository.getDataFromESBySize("aws_phd", "phd", mustFilter, null, null, null, 0,
                    Constants.ES_PAGE_SIZE, searchText, null);
        } catch (Exception e) {
            LOGGER.error("Exception in getNotificationDetails ",e);
            throw new DataException(e);
        }

    }

    @Override
    public List<Map<String, Object>> getQualysDetail(String resourceId) throws DataException {

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(AssetConstants.INSTANCEID_KEYWORD, resourceId);
        try {
            return esRepository.getDataFromES(AssetConstants.AWS_EC2, Constants.QUALYS_INFO, mustFilter, null, null,
                    Arrays.asList("lastVulnScan", "totalMemory", "account.list.hostAssetAccount.username"), null);
        } catch (Exception e) {
            LOGGER.error("Exception in getQualysDetail ",e);
            throw new DataException();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public int updateAsset(String assetGroup, String targettype, Map<String, Object> resources, String updatedBy,
            List<Map<String, Object>> updates) throws DataException, NoDataFoundException {

        int totalrows = 0;
        List<String> queriesToExecute = new ArrayList<>();
        List<Map<String, Object>> assetDetails = new ArrayList<>();
        List<String> insertValuesList = new ArrayList<>();
        List<String> deleteValuesList = new ArrayList<>();
        String[] insertValuesListFinal;
        String[] deleteValuesListFinal;
        List<String> inptutResources = (List<String>) resources.get("values");
        LOGGER.info("Total Resouces passed in " + inptutResources.size());
        Set<String> resourceids = new HashSet<>(inptutResources);
        LOGGER.info("Dups Resouces passed in " + (resourceids.size() - inptutResources.size()));
        HashMap<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(Constants.LATEST, true);
        List<Map<String, Object>> assetDetailsFromES;
        try {
            assetDetailsFromES = esRepository.getDataFromES(assetGroup, targettype, mustFilter,
                    null, null, null, null);
        } catch (Exception e) {
            LOGGER.error("Exception in updateAsset ",e);
            throw new DataException(e);
        }
        LOGGER.info("Total records in ES " + assetDetailsFromES.size());

        Map<String, Map<String, Object>> assetsTobeUpdated = assetDetailsFromES.parallelStream()
                .filter(asset -> resourceids.contains(asset.get(Constants.RESOURCEID).toString()))
                .collect(Collectors.toMap(asset -> asset.get(Constants.RESOURCEID).toString(), asset -> asset));
        assetDetailsFromES.clear();

        LOGGER.info("Total records to be updated  " + assetsTobeUpdated.size());
        resourceids.parallelStream().forEach(
                resourceValue -> {
                    Map<String, Object> assetDetailsMap = assetsTobeUpdated.get(resourceValue);
                    if (null != assetDetailsMap) {
                        assetDetailsMap.remove(Constants._ID);
                        assetDetailsMap.remove(Constants.ES_DOC_PARENT_KEY);
                        assetDetailsMap.remove(Constants.ES_DOC_ROUTING_KEY);
                        assetDetailsMap.put(Constants.LATEST, true);
                        for (Map<String, Object> updateMap : updates) {
                            String type = targettype;
                            String updatedByLocal = updatedBy;
                            StringBuilder deleteValue = new StringBuilder("(").append("'" + resourceValue + "'")
                                    .append("," + "'" + updateMap.get("key") + "'").append(")");
                            if (deleteValue != null) {
                                synchronized (deleteValuesList) {
                                    deleteValuesList.add(deleteValue.toString());
                                }
                            }
                            StringBuilder insertValue = new StringBuilder("(")
                                    .append("'" + type + "'")
                                    .append("," + "'" + resourceValue + "'")
                                    .append("," + "'" + updateMap.get("key") + "'")
                                    .append("," + "'" + updateMap.get("value") + "'")
                                    .append("," + "'" + updatedByLocal + "'")
                                    .append("," + "'"
                                            + new SimpleDateFormat("yyyy-MM-dd H:00:00Z").format(new java.util.Date())
                                            + "')");
                            if (insertValue != null) {
                                synchronized (insertValuesList) {
                                    insertValuesList.add(insertValue.toString());
                                }
                            }
                            assetDetailsMap.put(updateMap.get("key").toString(), updateMap.get("value"));
                        }
                        synchronized (assetDetails) {
                            assetDetails.add(assetDetailsMap);
                        }
                    } else {
                        LOGGER.info("resource not found" + resourceValue);
                    }
                });

        LOGGER.info("Total records to be updated back in ES  " + assetDetails.size());
        assetsTobeUpdated.clear();
        if (!assetDetails.isEmpty()) {
            insertValuesList.removeAll(Collections.singleton(null));
            insertValuesListFinal = insertValuesList.toArray(new String[insertValuesList.size()]);
            deleteValuesList.removeAll(Collections.singleton(null));
            deleteValuesListFinal = deleteValuesList.toArray(new String[deleteValuesList.size()]);

            StringBuilder insertQuery = new StringBuilder(
                    "INSERT INTO pacman_field_override ( resourcetype,_resourceid,fieldName,fieldValue,updatedBy,updatedOn) VALUES ");
            insertQuery.append(String.join(",", insertValuesListFinal));
            StringBuilder deleteQuery = new StringBuilder("DELETE FROM pacman_field_override WHERE resourceType = '"
                    + targettype + "' AND " + "( _resourceid,fieldName) IN");

            deleteQuery.append("(").append(String.join(",", deleteValuesListFinal)).append(")");

            queriesToExecute.add(deleteQuery.toString());
            queriesToExecute.add(insertQuery.toString());
            int[] updateCnt = redshiftRepository.batchUpdate(queriesToExecute);
            if (updateCnt != null && updateCnt.length == Constants.TWO && updateCnt[1] > 0) {
                String index = "aws_" + targettype;
                uploadData(index, targettype, assetDetails, Constants.DOCID);
                totalrows = updateCnt[1];
            } else {
                LOGGER.error("Update failed");
            }
            return totalrows;
        } else {
            throw new NoDataFoundException("Updation Failed,No matching records found in ES");
        }
    }

    private boolean uploadData(String index, String type, List<Map<String, Object>> docs, String idKey) {
        String actionTemplate = "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\"} }%n";

        LOGGER.info("*********UPLOADING*** " + type);
        if (null != docs && !docs.isEmpty()) {
            StringBuilder bulkRequest = new StringBuilder();
            int i = 0;
            for (Map<String, Object> doc : docs) {
                if (doc != null) {
                    String id = doc.get(idKey).toString();
                    StringBuilder docStrBuilder = new StringBuilder(createESDoc(doc));

                    if (docStrBuilder != null) {
                        bulkRequest.append(String.format(actionTemplate, index, type, id));
                        bulkRequest.append(docStrBuilder + "\n");
                    }
                    i++;
                    if (i % Constants.THOUSAND == AssetConstants.ZERO
                            || bulkRequest.toString().getBytes().length
                                    / (Constants.THOUSAND_TWENTY_FOUR * Constants.THOUSAND_TWENTY_FOUR) > Constants.FIVE) {
                        LOGGER.info("Uploaded" + i);
                        Response resp = invokeAPI("POST", AssetConstants.ESQUERY_BULK, bulkRequest.toString());
                        try {
                            String responseStr = "";
                            if(null != resp) {
                                responseStr = EntityUtils.toString(resp.getEntity());
                            }
                            if (responseStr.contains(AssetConstants.RESPONSE_ERROR)) {
                                Response retryResp = invokeAPI("POST", AssetConstants.ESQUERY_BULK,
                                        bulkRequest.toString());
                                String retryResponse = "";
                                if(null != retryResp) {
                                    retryResponse = EntityUtils.toString(retryResp.getEntity());
                                }
                                if (retryResponse.contains(AssetConstants.RESPONSE_ERROR)) {
                                    LOGGER.error(retryResponse);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Bulk upload failed",e);
                            return false;
                        }
                        bulkRequest = new StringBuilder();
                    }
                }
            }
            if (bulkRequest.length() > 0) {
                LOGGER.info("Uploaded" + i);
                Response resp = invokeAPI("POST", AssetConstants.ESQUERY_BULK, bulkRequest.toString());
                try {
                    String responseStr = "";
                    if(null != resp) {
                        responseStr = EntityUtils.toString(resp.getEntity());
                    }
                    if (responseStr.contains(AssetConstants.RESPONSE_ERROR)) {
                        Response retryResp = invokeAPI("POST", AssetConstants.ESQUERY_BULK,
                                bulkRequest.toString());
                        String retryResponse = "";
                        if(null != retryResp) {
                            retryResponse = EntityUtils.toString(retryResp.getEntity());
                        }
                        
                        if (retryResponse.contains(AssetConstants.RESPONSE_ERROR)) {
                            LOGGER.error(retryResponse);
                        }
                    }
                    return resp.getStatusLine().getStatusCode() == 200 ? true : false;
                } catch (Exception e) {
                    LOGGER.error("Bulk upload failed",e);
                    return false;
                }
            }
        }
        return true;
    }

    private String createESDoc(Map<String, ?> doc) {
        ObjectMapper objMapper = new ObjectMapper();
        String docJson = "{}";
        try {
            docJson = objMapper.writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error in createESDoc" , e);
        }
        return docJson;
    }

    private Response invokeAPI(String method, String endpoint, String payLoad) {
        HttpEntity entity = null;
        try {
            if (payLoad != null) {
                entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);
            }
            return getRestClient().performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
        } catch (IOException e) {
            LOGGER.error("Error in invokeAPI" , e);
        }
        return null;
    }

    private RestClient getRestClient() {
        if (restClient == null) {
            restClient = RestClient.builder(new HttpHost(updateESHost, updateESPort)).build();
        }
        return restClient;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Map<String, Object>> getAssetLists(String assetGroup, Map<String, String> filter, int from, int size,
            String searchText) {
        LOGGER.info("Inside getAssetLists");
        List<Map<String, Object>> assetDetails = new ArrayList<>();
        String targetType = "";

        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(Constants.LATEST, Constants.TRUE);
        Iterator it = filter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey().equals(AssetConstants.FILTER_APPLICATION)) {
                mustFilter.put(Constants.TAGS_APPS, entry.getValue());
            }
            if (entry.getKey().equals(AssetConstants.FILTER_ENVIRONMENT)) {
                mustFilter.put(Constants.TAGS_ENV, entry.getValue());
            }
            if (entry.getKey().equals(AssetConstants.FILTER_RES_TYPE)) {
                targetType = entry.getValue().toString();
            }
        }

        List<String> fieldNames = Arrays.asList(Constants.RESOURCEID, Constants.ACCOUNT_NAME, Constants.REGION,
                Constants.TAGS_APPLICATION, Constants.TAGS_ENVIRONMENT, Constants.ACCOUNT_ID,
                AssetConstants.UNDERSCORE_ENTITY_TYPE);
        if (Constants.ONPREMSERVER.equalsIgnoreCase(targetType)) {
            fieldNames = getDisplayFieldsForTargetType(targetType);
        }
        try {
            String url = esUrl + "/" + assetGroup + "/" + targetType + "/_search?size=" + size + "&from=" + from;
            Map<String, Object> source = new HashMap<>();
            source.put(AssetConstants.UNDERSCORE_SOURCE, fieldNames);
            String soruceJson = new Gson().toJson(source).replaceFirst("}", "");
            String query = soruceJson
                    + ",\"query\":{\"match\":{\"latest\":\"true\"}},\"sort\":[{\"_resourceid.keyword\":{\"order\":\"asc\"}}]}";
            String responseJson = "";
            try {
                responseJson = PacHttpUtils.doHttpPost(url, query);
            } catch (Exception e) {
                LOGGER.error("Error in getAssetLists" , e);
            }
            JsonParser jsonParser = new JsonParser();
            JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get("hits").toString());
            JsonArray jsonArray = hitsJson.getAsJsonObject().get("hits").getAsJsonArray();
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject obj = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) obj.get(AssetConstants.UNDERSCORE_SOURCE);
                    if (sourceJson != null) {
                        Map<String, Object> doc = new Gson().fromJson(sourceJson, new TypeToken<Map<String, Object>>() {
                        }.getType());
                        assetDetails.add(doc);
                    }
                }
            }

            if (targetType.equals(Constants.ONPREMSERVER)) {
                assetDetails.parallelStream().forEach(asset -> {
                    if (!asset.containsKey("u_kernel_release")) {
                        asset.put("u_kernel_release", "");
                    }
                    if (!asset.containsKey("u_projection_week")) {
                        asset.put("u_projection_week", "");
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error in getAssetLists", e);
        }

        LOGGER.info("Exiting getAssetLists");
        return assetDetails;
    }

    private List<Map<String, Object>> getListAssetsPathachableForEC2(String assetGroup, Map<String, String> filter,
			List<String> source) {

		String resourceType = filter.get(AssetConstants.FILTER_RES_TYPE);
		
		List<Map<String, Object>> assetDetails = new ArrayList<>();

		Map<String, Object> mustFilter = new HashMap<>();
		Map<String, Object> mustFilterAsset = new HashMap<>();
		Map<String, Object> mustNotFilterAsset = new HashMap<>();

		List<Map<String, Object>> unpatchedInstances;
		List<Map<String, Object>> totalInstances;

		String targetAssetGroup;
		mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TYPE), Constants.ISSUE);
		mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.ISSUE_STATUS), Constants.OPEN);

		Map<String, Object> parentBool = new HashMap<>();
		List<Map<String, Object>> mustList = new ArrayList<>();
		Map<String, Object> matchMap = new HashMap<>();
		Map<String, String> match = new HashMap<>();

		mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.POLICYID), Constants.CLOUD_KERNEL_COMPLIANCE_POLICY);

		// Changes to include only latest resources

		match.put(Constants.LATEST, Constants.TRUE);

		matchMap.put(Constants.MATCH, match);
		mustList.add(matchMap);

		match = new HashMap<>();
		if(Constants.EC2.equals(resourceType)) {
			match.put(Constants.STATE_NAME, Constants.RUNNING);
		}
		if(Constants.VIRTUALMACHINE.equals(resourceType)) {
			match.put(Constants.STATUS,  Constants.RUNNING);
		}
		
		matchMap = new HashMap<>();
		matchMap.put(Constants.MATCH, match);
		mustList.add(matchMap);

		parentBool.put("must", mustList);

		match = new HashMap<>();
		if(Constants.EC2.equals(resourceType)) {
			match.put("platform", Constants.WINDOWS);
		}
		if(Constants.VIRTUALMACHINE.equals(resourceType)) {
			match.put("osType",Constants.AZURE_WINDOWS);
		}
		matchMap = new HashMap<>();
		matchMap.put(Constants.MATCH, match);

		parentBool.put("must_not", matchMap);

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("bool", parentBool);

		Map<String, Object> parentEntryMap = new LinkedHashMap<>();
		parentEntryMap.put(Constants.TYPE, resourceType);
		parentEntryMap.put(AssetConstants.QUERY, queryMap);
		mustFilter.put("has_parent", parentEntryMap);

		filter.entrySet().stream().forEach(entry -> {
			if (!(entry.getKey().equals(AssetConstants.FILTER_PATCHED)
					|| entry.getKey().equals(AssetConstants.FILTER_RES_TYPE)
					|| entry.getKey().equals(AssetConstants.FILTER_EXEC_SPONSOR)
					|| entry.getKey().equals(AssetConstants.FILTER_DIRECTOR))) {
				if (entry.getKey().equals(AssetConstants.FILTER_APPLICATION)) {
					mustFilter.put(Constants.TAGS_APPS, entry.getValue());
					mustFilterAsset.put(Constants.TAGS_APPS, entry.getValue());
				}
				if (entry.getKey().equals(AssetConstants.FILTER_ENVIRONMENT)) {
					mustFilter.put(Constants.TAGS_ENV, entry.getValue());
					mustFilterAsset.put(Constants.TAGS_ENV, entry.getValue());
				}
			}
		});

		mustFilterAsset.put(Constants.LATEST, true);
		if(Constants.EC2.equals(resourceType)) {
			mustFilterAsset.put(CommonUtils.convertAttributetoKeyword(Constants.STATE_NAME), Constants.RUNNING);
			mustNotFilterAsset.put(CommonUtils.convertAttributetoKeyword(Constants.PLATFORM), Constants.WINDOWS);
		}
		if(Constants.VIRTUALMACHINE.equals(resourceType)) {
			mustFilterAsset.put(CommonUtils.convertAttributetoKeyword(Constants.STATUS), Constants.RUNNING);
			mustNotFilterAsset.put(CommonUtils.convertAttributetoKeyword("osType"), Constants.AZURE_WINDOWS); 
		}
			targetAssetGroup = assetGroup + "/" + resourceType;

		try {
			if (filter.containsKey(AssetConstants.FILTER_PATCHED)) {

				unpatchedInstances = esRepository.getDataFromES(assetGroup, null, mustFilter, null, null, null, null);
				List<String> unPatchedResourceIds = unpatchedInstances.parallelStream()
						.map(obj -> obj.get(Constants.RESOURCEID).toString()).collect(Collectors.toList());
				totalInstances = esRepository.getDataFromES(targetAssetGroup, null, mustFilterAsset, mustNotFilterAsset,
						null, null, null);
				if (filter.get(AssetConstants.FILTER_PATCHED).equals(AssetConstants.FALSE)) {
					assetDetails = totalInstances.parallelStream()
							.filter(asset -> unPatchedResourceIds.contains(asset.get(Constants.RESOURCEID)))
							.collect(Collectors.toList());
				} else if (filter.get(AssetConstants.FILTER_PATCHED).equals(Constants.TRUE)) {
					assetDetails = totalInstances.parallelStream()
							.filter(asset -> !unPatchedResourceIds.contains(asset.get(Constants.RESOURCEID)))
							.collect(Collectors.toList());
				}
			} else {
				assetDetails = esRepository.getDataFromES(targetAssetGroup, null, mustFilterAsset, mustNotFilterAsset,
						null, source, null);
			}
		} catch (Exception e) {
			LOGGER.error("Error in getListAssetsPatchable", e);
		}
		return assetDetails;
	}

    private List<Map<String, Object>> getListAssetsPathachableForOnPrem(String assetGroup, Map<String, String> filter,
            List<String> source) {

        List<Map<String, Object>> assetDetails = new ArrayList<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustFilterAsset = new HashMap<>();

        List<Map<String, Object>> unpatchedInstances;
        List<Map<String, Object>> totalInstances;

        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.TYPE), Constants.ISSUE);
        mustFilter.put(CommonUtils.convertAttributetoKeyword(Constants.ISSUE_STATUS), Constants.OPEN);
        
        mustFilter
                .put(CommonUtils.convertAttributetoKeyword(Constants.RULEID), Constants.ONPREM_KERNEL_COMPLIANCE_RULE);
        
        // Has Parent Query Start
        Map<String,Object> match = new HashMap<>();
        match.put(Constants.LATEST, Constants.TRUE);

        Map<String,Object> matchMap = new HashMap<>(); 
        matchMap.put(Constants.MATCH, match);
       
        Map<String, Object> parentEntryMap = new LinkedHashMap<>();
        parentEntryMap.put(Constants.TYPE, Constants.ONPREMSERVER);
        parentEntryMap.put(AssetConstants.QUERY, matchMap);
        mustFilter.put("has_parent", parentEntryMap);
        
        // Has Parent Query End
        
        filter.entrySet()
                .stream()
                .forEach(
                        entry -> {
                            if (!(entry.getKey().equals(AssetConstants.FILTER_PATCHED)
                                    || entry.getKey().equals(AssetConstants.FILTER_RES_TYPE)
                                    || entry.getKey().equals(AssetConstants.FILTER_EXEC_SPONSOR) || entry
                                    .getKey().equals(AssetConstants.FILTER_DIRECTOR))) {
                                if (entry.getKey().equals(AssetConstants.FILTER_APPLICATION)) {
                                    mustFilter.put(Constants.TAGS_APPS, entry.getValue());
                                    mustFilterAsset.put(Constants.TAGS_APPS, entry.getValue());
                                }
                                if (entry.getKey().equals(AssetConstants.FILTER_ENVIRONMENT)) {
                                    mustFilter.put(Constants.TAGS_ENV, entry.getValue());
                                    mustFilterAsset.put(Constants.TAGS_ENV, entry.getValue());
                                }
                            }
                        });

        mustFilterAsset.put(Constants.LATEST, true);
        mustFilterAsset.put(Constants.INSCOPE, true);
        String targetAssetGroup = assetGroup + "/" + Constants.ONPREMSERVER;

        try {
            Map<String, Object> mustNotFilterAsset = new HashMap<>();
            if (filter.containsKey(AssetConstants.FILTER_PATCHED)) {
                unpatchedInstances = esRepository.getDataFromES(assetGroup, null, mustFilter, null, null, null, null);
                List<String> unPatchedResourceIds = unpatchedInstances.parallelStream()
                        .map(obj -> obj.get(Constants.RESOURCEID).toString()).collect(Collectors.toList());
                totalInstances = esRepository.getDataFromES(targetAssetGroup, null, mustFilterAsset,
                        mustNotFilterAsset, null, null, null);
                if (AssetConstants.FALSE.equals(filter.get(AssetConstants.FILTER_PATCHED))) {
                    assetDetails = totalInstances.parallelStream()
                            .filter(asset -> unPatchedResourceIds.contains(asset.get(Constants.RESOURCEID)))
                            .collect(Collectors.toList());
                } else if (Constants.TRUE.equals(filter.get(AssetConstants.FILTER_PATCHED))) {
                    assetDetails = totalInstances.parallelStream()
                            .filter(asset -> !unPatchedResourceIds.contains(asset.get(Constants.RESOURCEID)))
                            .collect(Collectors.toList());
                }
            } else {
                assetDetails = esRepository.getDataFromES(targetAssetGroup, null, mustFilterAsset, mustNotFilterAsset,
                        null, source, null);
            }
        } catch (Exception e) {
            LOGGER.error("Error in getListAssetsPatchableForOnPrem", e);
        }

        return assetDetails;
    }

    private List<String> getDisplayFieldsForTargetType(String targetType) {

        String query = "select displayfields from cf_pac_updatable_fields where resourceType = '" + targetType.trim()
                + "'";
        return Arrays.asList(rdsRepository.queryForString(query).split("\\s*,\\s*"));
    }

    public long getTotalCountForListingAsset(String index, String type) {
        StringBuilder requestBody = new StringBuilder(
                "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}}]}}}");
        return getTotalDocCount(index, type, requestBody.toString());
    }

    @Override
    public String getResourceCreatedDate(final String resourceId, final String resourceType) {
        try {
            String eventName = events.get(resourceType.toLowerCase());
            String searchResourceQuery = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"detail.eventName.keyword\":{\"value\":\""
                    + eventName + "\"}}},{\"term\":{\"resourceid.keyword\":{\"value\":\"" + resourceId + "\"}}}]}}}";
            StringBuilder urlToQueryResource = new StringBuilder(heimdallEsesUrl).append("/")
                    .append("pacman-resource-claim").append("/").append(Constants.SEARCH);
            String resourceDetails;
            Gson gson = new Gson();
            resourceDetails = PacHttpUtils.doHttpPost(urlToQueryResource.toString(), searchResourceQuery);
            Type resourceTypeToken = new TypeToken<ResourceResponse>() {
            }.getType();
            ResourceResponse resourceResponse = gson.fromJson(resourceDetails, resourceTypeToken);
            int total = resourceResponse.getHits().getTotal();
            if (total > 0) {
                Source source = resourceResponse.getHits().getHits().get(0).getSource();
                String time = null;
                String creationDate = null;
                String eventTime = null;
                try {
                    time = source.getTime();
                } catch (Exception exception) {
                    LOGGER.error(exception);
                }
                try {
                    creationDate = source.getDetail().getUserIdentity().getSessionContext().getAttributes()
                            .getCreationDate();
                } catch (Exception exception) {
                    LOGGER.error(exception);
                }
                try {
                    eventTime = source.getDetail().getEventTime();
                } catch (Exception exception) {
                    LOGGER.error(exception);
                }

                if (creationDate != null) {
                    return creationDate;
                } else if (eventTime != null) {
                    return eventTime;
                } else if (time != null) {
                    return time;
                } else {
                    return StringUtils.EMPTY;
                }
            }
        } catch (Exception exception) {
            LOGGER.error(exception);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getDomainsByAssetGroup(String aseetGroupName) {
        String query = "select distinct c.domain from cf_AssetGroupTargetDetails a , cf_AssetGroupDetails b, cf_Target c where a.groupId = b.groupId  and a.targetType = c.targetName and b.groupName ='"
                + aseetGroupName.trim() + "'";
        return rdsRepository.getDataFromPacman(query);
    }

    @Override
    public List<Map<String, Object>> getAssetGroupAndDomains() {
        String query = "select distinct b.groupName as name, c.domain from cf_AssetGroupTargetDetails a , cf_AssetGroupDetails b, cf_Target c where a.groupId = b.groupId  and a.targetType = c.targetName";
        return rdsRepository.getDataFromPacman(query);
    }

    public Map<String, String> getEvents() {
        return events;
    }

    public void setEvents(Map<String, String> events) {
        this.events = events;
    }

    @Override
    public String getDataTypeInfoByTargetType(String targettype) {
        String query = "select updatableFields from cf_pac_updatable_fields where resourceType = '" + targettype.trim()
                + "'";
        return rdsRepository.queryForString(query);
    }

    @Override
    public List<Map<String, Object>> getAdGroupDetails() throws DataException {
        try {
            return esRepository.getDataFromES("adinfo", null, null, null, null, Arrays.asList("managedBy", "name"), null);
        } catch (Exception e) {
            LOGGER.error("Exception in getAdGroupDetails ",e);
            throw new DataException();
        }
    }
    
    private List<Map<String, Object>> formGetListResponse(List<String> fieldNames, List<Map<String, Object>> assetDetails, List<String> fieldsToBeSkipped) {
        
        List<Map<String, Object>> assetList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(fieldNames)) {
            final List<String> fieldNamesCopy = fieldNames;
            assetDetails.parallelStream().forEach(assetDetail -> {
                Map<String, Object> asset = new LinkedHashMap<>();
                for (String fieldName : fieldNamesCopy) {
                    if (!assetDetail.containsKey(fieldName)) {
                        asset.put(fieldName, "");
                    } else {
                        asset.put(fieldName, assetDetail.get(fieldName));
                    }
                }
                synchronized (assetList) {
                    assetList.add(asset);
                }
            });
            return assetList;
        } else {
            assetDetails.parallelStream().forEach(
                    assetDetail -> {
                        Map<String, Object> asset = new LinkedHashMap<>();
                        asset.put(Constants.RESOURCEID, assetDetail.get(Constants.RESOURCEID));
                        assetDetail.forEach((key, value) -> {
                            if (!fieldsToBeSkipped.contains(key)) {
                                asset.put(key, value);
                            }
                        });
                        synchronized (assetList) {
                            assetList.add(asset);
                        }
                    });
            return assetList;
        }
    }
    
    @Override
    public List<Map<String, Object>> getDataSourceForTargetTypes(List<String> targetTypes) {
        String targetTypeQuery = targetTypes.stream().map(targettype -> "\"" + targettype.trim() + "\"")
                .collect(Collectors.joining(","));
        String query = "SELECT dataSourceName as " + Constants.PROVIDER + ", targetName as " + Constants.TYPE
                + " FROM cf_Target";
        if (!CollectionUtils.isEmpty(targetTypes)) {
            query += " WHERE targetName IN (" + targetTypeQuery + ")";
        }
                
        return rdsRepository.getDataFromPacman(query);
    }
    
    @Override
	public Map<String, Object> getAssetCountAndEnvDistributionByAssetGroup(String aseetGroupName, String type, String application) {

		Map<String, Object> filter = new HashMap<>();
		filter.put(Constants.LATEST, Constants.TRUE);
		filter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
		if (application != null) {
			filter.put(Constants.TAGS_APPS, application);
		}
		
		Map<String, Object> countMap = new HashMap<>();
		try {
			if (AssetConstants.ALL.equals(type)) {
				try {
					Map<String, Object> nestedaggs = esRepository.buildAggs(Constants.TAGS_ENV, Constants.THOUSAND, Constants.ENVIRONMENTS, null);
					
					countMap = esRepository.getEnvAndTotalDistributionForIndexAndType(aseetGroupName, null, filter, null,
							null, AssetConstants.UNDERSCORE_TYPE, nestedaggs, Constants.THOUSAND, null);
				} catch (Exception e) {
					LOGGER.error("Exception in getAssetCountByAssetGroup :", e);
				}
			} 
			else {
				long count = esRepository.getTotalDocumentCountForIndexAndType(aseetGroupName, type, filter, null, null,
						null, null);
				Map<String, Long> envMap = esRepository.getTotalDistributionForIndexAndType(aseetGroupName, type, filter, null, null,
							Constants.TAGS_ENV, Constants.THOUSAND, null);
				
				Map<String, Object> countDetails = new HashMap<>();
				countDetails.put(type, count);
				Map<String, Object> envDetails = new HashMap<>(); 
				envDetails.put(type, envMap);
				countMap.put(Constants.ASSET_COUNT, countDetails);
				countMap.put(Constants.ENV_COUNT, envDetails);
				
			}
				 
		} catch (Exception e) {
			LOGGER.error("Exception in getAssetCountByAssetGroup :", e);
		}

		return countMap;
	}

	@Override
	public List<String> getProvidersForAssetGroup(String assetGroup) throws DataException {
		List<String> providerList = new ArrayList<String>();
		String query = "select distinct dataSourceName as " + Constants.PROVIDER + " from cf_AssetGroupTargetDetails a , cf_AssetGroupDetails b ,cf_Target c where a.groupId = b.groupId and a.targetType = c.targetName and b.groupName ='"
				+ assetGroup.trim() + "'";
		
		List<Map<String, Object>> providers= rdsRepository.getDataFromPacman(query);
		providers.forEach(providerMap -> {
			providerList.add(providerMap.get(Constants.PROVIDER).toString());
			});
		return providerList;
	}

}
