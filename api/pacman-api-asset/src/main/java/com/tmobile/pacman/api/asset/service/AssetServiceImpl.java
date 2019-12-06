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
package com.tmobile.pacman.api.asset.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.ResponseWithFieldsByTargetType;
import com.tmobile.pacman.api.asset.model.DefaultUserAssetGroup;
import com.tmobile.pacman.api.asset.repository.AssetRepository;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.NoDataFoundException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * Implemented class for AssetService and all its method
 */
@Service
public class AssetServiceImpl implements AssetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetServiceImpl.class);

    @Autowired
    private AssetRepository repository;
    
    @Value("${cloudinsights.tokenurl}")
    String insightsTokenUrl;

    @Value("${cloudinsights.costurl}")
    String insightsCostUrl;

    @Value("${cloudinsights.corp-user-id}")
    String svcCorpUserId;

    @Value("${cloudinsights.corp-password}")
    String svcCorpPassword;

    @Override
	public List<Map<String, Object>> getAssetCountByAssetGroup(String assetGroup, String type, String domain,
			String application, String provider) {
    	
		LOGGER.debug("Fetch counts from elastic search");

		// ES query may possibly return other types as well.
		Map<String, Long> countMap = repository.getAssetCountByAssetGroup(assetGroup, type, application);
		List<String> validTypes = Lists.newArrayList();
		if (AssetConstants.ALL.equals(type)) {
			LOGGER.debug("Remove the entries which are not valid types");
			List<Map<String, Object>> targetTypes = getTargetTypesForAssetGroup(assetGroup, domain, provider);
			validTypes = targetTypes.stream().map(obj -> obj.get(Constants.TYPE).toString())
					.collect(Collectors.toList());
			List<String> countTypes = new ArrayList<>(countMap.keySet());
			for (String _type : validTypes) {
				if (!countMap.containsKey(_type)) {
					countMap.put(_type, 0L);
				}
			}
			for (String _type : countTypes) {
				if (!validTypes.contains(_type)) {
					countMap.remove(_type);
				}
			}
		}else {
			validTypes.add(type);
		}

		List<Map<String, Object>> datasourceForAssettypes = repository.getDataSourceForTargetTypes(validTypes);

		LOGGER.debug("Creating response objects ");
		List<Map<String, Object>> countList = new ArrayList<>();
		countMap.entrySet().stream().forEach(entry -> {
			if (!Integer.valueOf(entry.getValue().toString()).equals(0)) {
				Map<String, Object> typeMap = new HashMap<>();

				String providerInfo = datasourceForAssettypes.stream()
						.filter(data -> data.get(Constants.TYPE).equals(entry.getKey())).findFirst().get()
						.get(Constants.PROVIDER).toString();

				typeMap.put(Constants.TYPE, entry.getKey());
				typeMap.put(Constants.COUNT, entry.getValue());
				typeMap.put(Constants.PROVIDER, providerInfo);
				countList.add(typeMap);
			}
		});

		return countList;
	}

    @Override
	@Cacheable(cacheNames = "assets", unless = "#result == null")
	public List<Map<String, Object>> getTargetTypesForAssetGroup(String assetGroup, String domain, String provider) {
		if (Constants.AWS.equals(assetGroup) || Constants.AZURE.equals(assetGroup) ) {
			return repository.getAllTargetTypes(assetGroup);
		} else if (Constants.MASTER_ALIAS.equals(assetGroup) || Constants.ROOT_ALIAS.equals(assetGroup)) {
			return repository.getAllTargetTypes(null);
		}else {
			return repository.getTargetTypesByAssetGroup(assetGroup, domain, provider);
		}
	}

    @Override
    public List<Map<String, Object>> getApplicationsByAssetGroup(String assetGroup, String domain) throws DataException {

        List<String> applications ;
        if (StringUtils.isEmpty(domain)) {
            applications = repository.getApplicationByAssetGroup(assetGroup);
        } else {
            applications = repository.getApplicationByAssetGroup(assetGroup, domain);
        }

        List<Map<String, Object>> applicationList = new ArrayList<>();
        applications.forEach(app -> {
            Map<String, Object> appMap = new HashMap<>();
            appMap.put(Constants.NAME, app);
            applicationList.add(appMap);
        });
        return applicationList;
    }

    @Override
    public List<Map<String, Object>> getEnvironmentsByAssetGroup(String assetGroup, String application, String domain) {
        List<String> environments = repository.getEnvironmentsByAssetGroup(assetGroup, application, domain);
        List<Map<String, Object>> envList = new ArrayList<>();
        environments.forEach(env -> {
            Map<String, Object> envMapp = new HashMap<>();
            envMapp.put(Constants.NAME, env);
            envList.add(envMapp);
        });
        return envList;
    }

    @Override
    public List<Map<String, Object>> getAllAssetGroups() {
        List<Map<String, Object>> assetGroups = repository.getAllAssetGroups();
        List<Map<String, Object>> assetGroupDomains = repository.getAssetGroupAndDomains();
        Map<String, List<String>> agDomainMap = new ConcurrentHashMap<>();
        assetGroupDomains.parallelStream().forEach(obj -> {
            String groupName = obj.get(Constants.NAME).toString();
            String domain = obj.get(Constants.DOMAIN).toString();
            List<String> domains = agDomainMap.get(groupName);
            if (domains == null) {
                domains = new ArrayList<>();
                agDomainMap.put(groupName, domains);
            }
            domains.add(domain);
        });
        assetGroups.parallelStream().forEach(
                obj -> obj.put("domains", agDomainMap.get(obj.get(Constants.NAME).toString())));
        return assetGroups;
    }

    @Override
	public Map<String, Object> getAssetGroupInfo(String assetGroup) {
		Map<String, Object> assetGroupInfoMap = repository.getAssetGroupInfo(assetGroup);
		if (!assetGroupInfoMap.isEmpty()) {
			List<String> applications = new ArrayList<>();
			try {
				applications = repository.getApplicationByAssetGroup(assetGroup, null);
			} catch (Exception e) {
				LOGGER.error("Error in getAssetGroupInfo ", e);
			}
			assetGroupInfoMap.put("appcount", applications.size());
			List<Map<String, Object>> countMap = getAssetCountByAssetGroup(assetGroup, AssetConstants.ALL, null, null, null);
			assetGroupInfoMap.put("assetcount",
					countMap.stream().mapToLong(obj -> Long.valueOf(obj.get(Constants.COUNT).toString())).sum());
			assetGroupInfoMap.put("domains", getDomains(assetGroup));
			assetGroupInfoMap.put(Constants.PROVIDERS, getProviderWithTypeCount(assetGroup,countMap));
		}
		return assetGroupInfoMap;
	}
    
    /**
	 * Function for getting the provider details along with the target type count
	 * 
	 * @param countMap
	 * @return
	 */
	private List<Map<String, Object>> getProviderWithTypeCount (String assetGroup,List<Map<String, Object>> countMap) {
		List<Map<String, Object>> providersData = new ArrayList<>();
		
		Map<String, Long> providerMap = countMap.stream().collect(Collectors.groupingBy(countObj-> countObj.get(Constants.PROVIDER).toString(), Collectors.counting()));
		
		if(providerMap.isEmpty()) {
			List<Map<String, Object>> targetTypes = repository.getTargetTypesByAssetGroup(assetGroup, "Infra & Platforms", null);
			List<String> validTypes = targetTypes.stream().map(obj -> obj.get(Constants.TYPE).toString())
					.collect(Collectors.toList());
			List<Map<String, Object>> datasourceForAssettypes = repository.getDataSourceForTargetTypes(validTypes);
			Set<String>	mappedProviders =	datasourceForAssettypes.stream().map(obj->obj.get(Constants.PROVIDER).toString()).collect(Collectors.toSet());
			mappedProviders.forEach(provider->providerMap.put(provider,0L));
		}
		
		providerMap.forEach((k,v)-> {
			Map<String, Object> newProvider = new HashMap<String, Object>();
			newProvider.put(Constants.PROVIDER,k);
			newProvider.put(Constants.TYPE_COUNT, v);
			providersData.add(newProvider);
		});
		return providersData;
	}

    @Override
    public List<Map<String, Object>> getAssetCountByApplication(String assetGroup, String type) throws DataException {
        Map<String, Long> countMap = repository.getAssetCountByApplication(assetGroup, type);
        List<Map<String, Object>> countList = new ArrayList<>();
        countMap.entrySet().stream().forEach(entry -> {
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("application", entry.getKey());
            typeMap.put(Constants.COUNT, entry.getValue());
            countList.add(typeMap);
        });
        return countList;

    }

    @Override
    public List<Map<String, Object>> getAssetMinMax(String assetGroup, String type, Date from, Date to) {
        return repository.getAssetMinMax(assetGroup, type, from, to);
    }

    @Override
    public Boolean saveOrUpdateAssetGroup(final DefaultUserAssetGroup defaultUserAssetGroup) {
        int response = repository.saveOrUpdateAssetGroup(defaultUserAssetGroup);
        return response > 0 ? true : false;
    }

    @Override
    public String getUserDefaultAssetGroup(final String userId) {
        return repository.getUserDefaultAssetGroup(userId);
    }

    @Override
    public List<Map<String, Object>> getAssetCountByEnvironment(String assetGroup, String application, String type) {
        return repository.getAssetCountByEnvironment(assetGroup, application, type);
    }

    @Override
    public List<Map<String, Object>> saveAndAppendToRecentlyViewedAG(String userId, String assetGroup) throws DataException {
        return repository.saveAndAppendAssetGroup(userId, assetGroup);
    }

    @Override
    public List<Map<String, Object>> getListAssets(String assetGroup, Map<String, String> filter, int from, int size,
            String searchText) {
        return repository.getListAssets(assetGroup, filter, from, size, searchText);
    }

    @Override
    public long getAssetCount(String assetGroup, Map<String, String> filter, String searchText) {
        return repository.getAssetCount(assetGroup, filter, searchText);
    }

    /**
     * 
     * @author Kanchana
     * @param assetGroup
     * @param instanceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getInstanceCPUUtilization(String instanceId) throws DataException {
        return repository.getCpuUtilizationByAssetGroupAndInstanceId(instanceId);
    }

    /**
     * 
     * @author Kanchana
     * @param assetGroup
     * @param instanceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getInstanceDiskUtilization(String instanceId) throws DataException {
        return repository.getDiskUtilizationByAssetGroupAndInstanceId(instanceId);
    }

    /**
     * 
     * @author Kanchana
     * @param assetGroup
     * @param instanceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getInstanceSoftwareInstallDetails(String instanceId, Integer from, Integer size,
            String searchText) throws DataException {
        return repository.getSoftwareInstalledDetailsByAssetGroupAndInstanceId(instanceId, from, size, searchText);
    }

    private List<Map<String, Object>> createAttributes(Map<String, ?> data, String[] fields, String category) {
        List<Map<String, Object>> attributes = new ArrayList<>();

        for (String field : fields) {
            Map<String, Object> attribute = new LinkedHashMap<>();
            attribute.put(Constants.NAME, field);
            String strValue = data.get(field).toString();
            attribute.put(Constants.VALUE, new String[] { strValue });
            attribute.put(Constants.CATEGORY, category);
            if (StringUtils.isNotEmpty(strValue) && StringUtils.isNotBlank(strValue)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    @Override
    public List<Map<String, Object>> getListAssetsPatchable(String assetGroup, Map<String, String> filter) {
        return repository.getListAssetsPatchable(assetGroup, filter);
    }

    @Override
    public List<Map<String, Object>> getListAssetsTaggable(String assetGroup, Map<String, String> filter) {
        return repository.getListAssetsTaggable(assetGroup, filter);
    }

    public Map<String, Object> getEc2ResourceDetail(String ag, String resourceId) throws DataException, ServiceException {

        List<Map<String, Object>> rhnDataList = repository.getEc2ResourceDetailFromRhn(resourceId);
        Map<String, Object> rhnData = null;
        if (rhnDataList != null && !rhnDataList.isEmpty()) {
            rhnData = repository.getEc2ResourceDetailFromRhn(resourceId).get(0);
        }

        List<Map<String, Object>> ec2DataList = repository.getEc2ResourceDetail(ag, resourceId);
        Map<String, Object> ec2DetailParentMap = new LinkedHashMap<>();

        if (null == ec2DataList || ec2DataList.isEmpty()) {
            return ec2DetailParentMap;
        }

        Map<String, Object> ec2Data = ec2DataList.get(0);

        List<Map<String, Object>> attributesList = new ArrayList<>();

        String[] fields1 = { "imageid", "subnetid", "instancetype", "accountname", "vpcid", "availabilityzone" };
        attributesList.addAll(createAttributes(ec2Data, fields1, "AWS Metadata"));
        String[] fields2 = { AssetConstants.PUBLIC_IP_ADDRESS, AssetConstants.PRIVATE_IP_ADDRESS };
        attributesList.addAll(createAttributes(ec2Data, fields2, "IP Address"));
        String[] fields3 = { Constants.STATE_NAME, "monitoringstate", "hostid", "statereasoncode",
                "virtualizationtype", "rootdevicename", "keyname", "kernelid", Constants.STATE_NAME, "hypervisor",
                "architecture", "tenancy" };
        attributesList.addAll(createAttributes(ec2Data, fields3, "AWS Attributes"));

        Map<String, String> ipAddressKvPairs = new LinkedHashMap<>();
        ipAddressKvPairs
                .put(AssetConstants.PUBLIC_IP_ADDRESS, ec2Data.get(AssetConstants.PUBLIC_IP_ADDRESS).toString());
        ipAddressKvPairs.put(AssetConstants.PRIVATE_IP_ADDRESS, ec2Data.get(AssetConstants.PRIVATE_IP_ADDRESS)
                .toString());

        Map<String, String> tagsKvPairs = new LinkedHashMap<>();
        ec2Data.forEach((key, value) -> {
            String tagsPrefix = "tags.";
            if (key.startsWith(tagsPrefix)) {
                tagsKvPairs.put(key.substring(tagsPrefix.length(), key.length()), value.toString());
            }
        });

        List<String> listOfSecurityGroupIds = new ArrayList<>();
        List<Map<String, Object>> securityGroupDataList = repository.getEc2ResourceSecurityGroupDetail(resourceId);
        securityGroupDataList.forEach(securityGroupData -> {
            listOfSecurityGroupIds.add(securityGroupData.get("securitygroupid").toString());
        });

        List<String> listOfVolumeIds = new ArrayList<>();
        List<Map<String, Object>> volumeIdDataList = repository.getEc2ResourceBlockDevicesDetail(resourceId);
        volumeIdDataList.forEach(volumeIdData -> {
            listOfVolumeIds.add(volumeIdData.get("volumeid").toString());
        });

        if (rhnData != null) {
            Map<String, Object> attributeForLastBoot = new LinkedHashMap<>();
            attributeForLastBoot.put(Constants.NAME, "Last System Boot");
            attributeForLastBoot.put(Constants.VALUE, Arrays.asList(rhnData.get("last_boot")));
            attributeForLastBoot.put(Constants.CATEGORY, "RHN INFO");
            addAttributeIfNotEmpty(attributeForLastBoot, attributesList);

            Map<String, Object> attributeForLastCheckedIn = new LinkedHashMap<>();
            attributeForLastCheckedIn.put(Constants.NAME, "Last Checked In");
            attributeForLastCheckedIn.put(Constants.VALUE, Arrays.asList(rhnData.get("last_checkin")));
            attributeForLastCheckedIn.put(Constants.CATEGORY, "RHN INFO");
            addAttributeIfNotEmpty(attributeForLastCheckedIn, attributesList);
        }

        Map<String, Object> attributeForVolumes = new LinkedHashMap<>();
        attributeForVolumes.put(Constants.NAME, "EBS Volumes");
        attributeForVolumes.put(Constants.VALUE, listOfVolumeIds);
        attributeForVolumes.put(Constants.CATEGORY, AssetConstants.RELATED_ASSETS);
        addAttributeIfNotEmpty(attributeForVolumes, attributesList);

        Map<String, Object> attributeForSG = new LinkedHashMap<>();
        attributeForSG.put(Constants.NAME, "Security Groups");
        attributeForSG.put(Constants.VALUE, listOfSecurityGroupIds);
        attributeForSG.put(Constants.CATEGORY, AssetConstants.RELATED_ASSETS);
        addAttributeIfNotEmpty(attributeForSG, attributesList);

        Object publicIp = ec2Data.get(AssetConstants.PUBLIC_IP_ADDRESS);
        if (publicIp != null && StringUtils.isNotEmpty(publicIp.toString())) {
            Map<String, Object> attributeForPublicIP = new LinkedHashMap<>();
            List<String> listOfPublicIPs = new ArrayList<>();
            listOfPublicIPs.add(publicIp.toString());
            attributeForPublicIP.put(Constants.NAME, "Public IPs");
            attributeForPublicIP.put(Constants.VALUE, listOfPublicIPs);
            attributeForPublicIP.put(Constants.CATEGORY, AssetConstants.RELATED_ASSETS);
            addAttributeIfNotEmpty(attributeForPublicIP, attributesList);
        }

        Object instanceProfile = ec2Data.get("iaminstanceprofilearn");
        if (instanceProfile != null && StringUtils.isNotEmpty(instanceProfile.toString())) {
            Map<String, Object> attributeForInstancesRole = new LinkedHashMap<>();
            attributeForInstancesRole.put(Constants.NAME, "Instance Roles");
            attributeForInstancesRole.put(Constants.VALUE, instanceProfile);
            attributeForInstancesRole.put(Constants.CATEGORY, AssetConstants.RELATED_ASSETS);
            addAttributeIfNotEmpty(attributeForInstancesRole, attributesList);
        }

        try {
            Map<String, Object> createInfo = repository.getResourceCreateInfo(resourceId);
            if (createInfo != null) {
                String[] attrFields = { AssetConstants.CREATED_BY, AssetConstants.CREATION_DATE, AssetConstants.EMAIL };
                attributesList.addAll(createAttributes(createInfo, attrFields, "Creators"));
            }
        } catch (Exception e) {
            LOGGER.error("Error Fetching created info for resource " + resourceId , e);
            throw new ServiceException(e);
        }

        // Qualys was earlier a separate api. Lets add it to the generic EC2 API
        // just like RHN INFO - A separate category
        try {
            attributesList.addAll(getResourceQualysDetail(resourceId));
        } catch (Exception e) {
            LOGGER.error("Exception in getEc2ResourceDetail ",e);
            throw new ServiceException(e);
        }

        ec2DetailParentMap.put("resourceId", resourceId);
        ec2DetailParentMap.put("tags", tagsKvPairs);
        ec2DetailParentMap.put("attributes", attributesList);

        return ec2DetailParentMap;

    }

    private void addAttributeIfNotEmpty(Map<String, Object> attribute, List<Map<String, Object>> attributesList) {
        Object value = attribute.get(Constants.VALUE);

        if (value instanceof List) {
            if (!((List<?>) value).isEmpty()) {
                attributesList.add(attribute);
            }

        } else {
            if (StringUtils.isNotEmpty(value.toString()) && StringUtils.isNotBlank(value.toString())) {

                attributesList.add(attribute);
            }
        }
    }

    @Override
    public Map<String, Object> getGenericResourceDetail(String ag, String resourceType, String resourceId)
            throws DataException {

        Map<String, Object> assetDetailMap = new LinkedHashMap<>();

        List<Map<String, Object>> resourceDataList = repository.getResourceDetail(ag, resourceType, resourceId);

        if (null == resourceDataList || resourceDataList.isEmpty()) {
            return assetDetailMap;
        }

        Map<String, Object> resourceData = resourceDataList.get(0);

        List<Map<String, Object>> attributesList = new ArrayList<>();
        Map<String, String> tagsKvPairs = new LinkedHashMap<>();

        List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID,
                AssetConstants.UNDERSCORE_DISCOVERY_DATE, AssetConstants.DISCOVERY_DATE,
                AssetConstants.FIRST_DISCOVEREDON, Constants._ID, AssetConstants.UNDERSCORE_ENTITY,
                AssetConstants.UNDERSCORE_ENTITY_TYPE, Constants.LATEST, AssetConstants.UNDERSCORE_LOADDATE);

        resourceData.forEach((key, value) -> {

            if (!fieldsToBeSkipped.contains(key) && StringUtils.isNotBlank(value.toString())) {

                String tagsPrefix = "tags.";
                if (key.startsWith(tagsPrefix)) {
                    tagsKvPairs.put(key.substring(tagsPrefix.length(), key.length()), value.toString());
                } else {

                    Map<String, Object> attribute = new LinkedHashMap<>();
                    attribute.put(Constants.NAME, key);
                    attribute.put(Constants.VALUE, new String[] { value.toString() });
                    attribute.put(Constants.CATEGORY, "");
                    attributesList.add(attribute);
                }
            }
        });

        try {
            Map<String, Object> createInfo = repository.getResourceCreateInfo(resourceId);
            if (createInfo != null) {
                String[] attrFields = { AssetConstants.CREATED_BY, AssetConstants.CREATION_DATE, AssetConstants.EMAIL };
                attributesList.addAll(createAttributes(createInfo, attrFields, "Creators"));
            }
        } catch (Exception e) {
            LOGGER.error("Error Fetching created info for resrouce " + resourceId , e);
        }
        assetDetailMap.put("tags", tagsKvPairs);
        assetDetailMap.put("attributes", attributesList);

        return assetDetailMap;
    }

    @Override
    public List<Map<String, Object>> getListAssetsVulnerable(String assetGroup, Map<String, String> filter) {
        return repository.getListAssetsVulnerable(assetGroup, filter);
    }

    public List<Map<String, Object>> getOpenPortDetails(String instanceId, Integer from, Integer size, String searchText)
            throws DataException {
        return repository.getOpenPortDetailsByInstanceId(instanceId, from, size, searchText);
    }

    @Override
    public List<Map<String, Object>> getListAssetsScanned(String assetGroup, Map<String, String> filter) {
        return repository.getListAssetsScanned(assetGroup, filter);
    }

    @Override
    public String getEc2StateDetail(String ag, String resourceId) throws DataException {
        List<Map<String, Object>> ec2Details = repository.getEc2ResourceDetail(ag, resourceId);
        if (ec2Details != null && !ec2Details.isEmpty()) {
            return ec2Details.get(0).get(Constants.STATE_NAME).toString();
        }
        return "";

    }

    @Override
    public List<Map<String, Object>> getNotificationSummary(String instanceId) throws DataException {
        Map<String, Long> summaryAggregationMap = repository.getNotificationSummary(instanceId);

        List<Map<String, Object>> sevList = new ArrayList<>();

        addStatusCountsForStatus(sevList, summaryAggregationMap, "open");
        addStatusCountsForStatus(sevList, summaryAggregationMap, "closed");
        addStatusCountsForStatus(sevList, summaryAggregationMap, "upcoming");

        return sevList;
    }

    private void addStatusCountsForStatus(List<Map<String, Object>> sevList, Map<String, Long> summaryAggregationMap,
            String status) {
        Map<String, Object> sevInfo = new LinkedHashMap<>();

        sevInfo.put("status", status);
        if (null != summaryAggregationMap.get(status)
                && StringUtils.isNotBlank(summaryAggregationMap.get(status).toString())) {
            sevInfo.put(Constants.COUNT, summaryAggregationMap.get(status).toString());
        } else {
            sevInfo.put(Constants.COUNT, "0");
        }
        sevList.add(sevInfo);

    }

    @Override
    public String getNotificationSummaryTotal(List<Map<String, Object>> sevList) throws DataException {

        List<Integer> numList = new ArrayList<>();
        sevList.forEach(sevInfo -> numList.add(Integer.parseInt(sevInfo.get(Constants.COUNT).toString())));

        int total = 0;
        for (int num : numList) {
            total += num;
        }
        return Integer.toString(total);
    }

    @Override
    public Integer saveAssetConfig(String resourceId, String configType, String config) {
        return repository.saveAssetConfig(resourceId, configType, config);
    }

    @Override
    public String retrieveAssetConfig(String resourceId, String configType) {
        return repository.retrieveAssetConfig(resourceId, configType);
    }

    @Override
    public List<Map<String, Object>> getResourceQualysDetail(String resourceId) throws DataException {

        List<Map<String, Object>> qualysDataList = repository.getQualysDetail(resourceId);

        List<Map<String, Object>> attributesList = new ArrayList<>();

        if (null == qualysDataList || qualysDataList.isEmpty()) {
            return attributesList;
        }

        Map<String, Object> qualysData = qualysDataList.get(0);

        List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID,
                AssetConstants.UNDERSCORE_DISCOVERY_DATE, AssetConstants.DISCOVERY_DATE,
                AssetConstants.FIRST_DISCOVEREDON, Constants._ID, AssetConstants.UNDERSCORE_ENTITY,
                AssetConstants.UNDERSCORE_ENTITY_TYPE, Constants.LATEST, Constants.ES_DOC_ROUTING_KEY,
                Constants.ES_DOC_PARENT_KEY);

        qualysData.forEach((key, value) -> {
            if (!fieldsToBeSkipped.contains(key) && StringUtils.isNotBlank(value.toString())) {

                Map<String, Object> attribute = new LinkedHashMap<>();
                attribute.put(Constants.NAME, key);
                attribute.put(Constants.VALUE, new String[] { value.toString() });
                attribute.put(Constants.CATEGORY, "QUALYS INFO");

                if ("list".equals(key) && value.toString().contains(AssetConstants.USERNAME)) {
                    attribute = createUserListAttributeFromQualysData(value.toString());
                }

                attributesList.add(attribute);
            }
        });

        return attributesList;

    }

    private Map<String, Object> createUserListAttributeFromQualysData(String usernameJsonString) {
        List<String> userNameList = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonArray usernameArray = (JsonArray) jsonParser.parse(usernameJsonString);
        for (JsonElement usernameElement : usernameArray) {

            JsonObject userNameJsonObj = usernameElement.getAsJsonObject();

            String username = userNameJsonObj.get(AssetConstants.USERNAME).toString();
            if (!(username.contains("ec2-user") || (username.contains("root")))) {
                userNameList.add(username);
            }
        }

        Map<String, Object> attribute = new LinkedHashMap<>();
        attribute.put(Constants.NAME, AssetConstants.USERNAME);
        attribute.put(Constants.VALUE, userNameList);
        attribute.put(Constants.CATEGORY, "QUALYS INFO");

        return attribute;
    }

    @Override
    public List<Map<String, String>> getAdGroupsDetail(String ag, String resourceId) throws DataException {

        List<Map<String, String>> matchingAdGroups = new ArrayList<>();

        List<Map<String, Object>> ec2DataList = repository.getEc2ResourceDetail(ag, resourceId);

        if (null == ec2DataList || ec2DataList.isEmpty()) {
            return matchingAdGroups;
        }

        Map<String, Object> ec2Data = ec2DataList.get(0);
        Object nameTagValueObj = ec2Data.get("tags.Name");
        String nameTagValue = "";
        if (null != nameTagValueObj) {
            nameTagValue = nameTagValueObj.toString();
        }

        LOGGER.info("EC2 Tag value is: {}" , nameTagValue);

        String platform = ec2Data.get("platform").toString();

        LOGGER.info("EC2 Tag value is: {}" , nameTagValue);

        StringTokenizer stTknzr = new StringTokenizer(nameTagValue, "-");
        String envSectionOfTag = "";
        String ouSectionOfTag = "";

        String ouAndEnvOfTag = "";
        try {
            // First token is env. Second is ou.
            envSectionOfTag = stTknzr.nextToken();
            ouSectionOfTag = stTknzr.nextToken();
            ouAndEnvOfTag = ouSectionOfTag + "_" + envSectionOfTag + "_";
        } catch (NoSuchElementException e) {
            LOGGER.error("Error in getAdGroupsDetail" , e);
            ouAndEnvOfTag = "";
        }

        LOGGER.info("Resource id : {}",resourceId);
        LOGGER.info("OU and ENV section of Name Tag of EC2 instance is: {}" , ouAndEnvOfTag);

        String tagValue = ouAndEnvOfTag;

        List<Map<String, Object>> adDataList = repository.getAdGroupDetails();

        if (null == adDataList || adDataList.isEmpty()) {
            return matchingAdGroups;
        }
        
        adDataList.forEach(adMap -> {

            String osString = "";

            // For windows instances, consider only group names which
            // have 'r_win' in them
            // if platform is blank, it denotes linux. So look for group
            // names which have
            // 'r_rhel'
                if ("windows".equalsIgnoreCase(platform)) {
                    osString = "r_win";
                } else {
                    osString = "r_rhel";
                }
                Map<String, String> adOutputMap = new LinkedHashMap<>();

                String groupNameStr = adMap.get(Constants.NAME) == null ? "" : adMap.get(Constants.NAME).toString();
                String ownerNameStr = adMap.get(AssetConstants.MANAGED_BY) == null ? "" : adMap.get(
                        AssetConstants.MANAGED_BY).toString();

                LOGGER.debug("Comparing the groupNameStr: {}  with the value from tag:{}",groupNameStr,tagValue);
                if (!StringUtils.isEmpty(tagValue) && groupNameStr.contains(tagValue)
                        && groupNameStr.contains(osString)) {
                    adOutputMap.put(Constants.NAME, groupNameStr);
                    adOutputMap.put(AssetConstants.MANAGED_BY, ownerNameStr);

                    matchingAdGroups.add(adOutputMap);
                }

            });

        return matchingAdGroups;
    }

    @Override
    public List<Map<String, Object>> getNotificationDetails(String instanceId, Map<String, String> filters,
            String searchText) throws DataException {

        List<Map<String, Object>> processedNotificationList = new ArrayList<>();

        List<Map<String, Object>> notificationDataList = repository.getNotificationDetails(instanceId, filters,
                searchText);

        List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID,
                AssetConstants.UNDERSCORE_DISCOVERY_DATE, AssetConstants.DISCOVERY_DATE,
                AssetConstants.FIRST_DISCOVEREDON, Constants._ID, AssetConstants.UNDERSCORE_ENTITY,
                AssetConstants.UNDERSCORE_ENTITY_TYPE, Constants.LATEST);
        notificationDataList.forEach(notificationDataMap -> {
            Map<String, Object> processedNotificationMap = new LinkedHashMap<>();

            notificationDataMap.forEach((key, value) -> {

                if (!fieldsToBeSkipped.contains(key) && StringUtils.isNotBlank(value.toString())) {
                    processedNotificationMap.put(key, value);
                }
            });

            processedNotificationList.add(processedNotificationMap);

        });

        return processedNotificationList;
    }

    @Override
    public Map<String, Object> getEc2CreatorDetail(String resourceId) throws DataException {

        Map<String, Object> creatorReturnObj = new LinkedHashMap<>();

        try {
            Map<String, Object> createInfo = repository.getResourceCreateInfo(resourceId);

            if (null != createInfo) {
                creatorReturnObj.put(AssetConstants.CREATION_DATE, createInfo.get(AssetConstants.CREATION_DATE));
                creatorReturnObj.put(AssetConstants.CREATED_BY, createInfo.get(AssetConstants.CREATED_BY));
                creatorReturnObj.put(AssetConstants.EMAIL, createInfo.get(AssetConstants.EMAIL));

                creatorReturnObj.put("eventInfo", createInfo);
            }
            return creatorReturnObj;

        } catch (Exception e) {
            LOGGER.error("Error Fetching created info for resource " + resourceId , e);
        }

        return null;

    }

    @Override
    public Map<String, Object> getEC2AvgAndTotalCost(String resourceId) throws DataException {

        String corpUserId = svcCorpUserId;
        String corpPassword = svcCorpPassword;
        String jwtTokenGeneratorUrl = insightsTokenUrl;
        String cloudInsightsBaseUrl = insightsCostUrl;

        String startDate = "2014-01-01";
        LocalDate endDateObj = LocalDate.now();
        String endDate = endDateObj.format(DateTimeFormatter.ISO_DATE);

        String requestBodyStr = "{\"username\": \"" + corpUserId + "\",\"password\": \"" + corpPassword + "\"}";
        LOGGER.info("Invoking JWT Token Generator URL: {} with requestBody as: {}",jwtTokenGeneratorUrl, requestBodyStr);
        String jwtTokenJsonString;
        try {
            jwtTokenJsonString = PacHttpUtils.doHttpPost(jwtTokenGeneratorUrl, requestBodyStr);
        } catch (Exception e) {
            LOGGER.error("Exception in getEC2AvgAndTotalCost ",e);
            throw new DataException(e);
        }
        Gson serializer = new GsonBuilder().create();
        Map<String, String> tokenMap = (Map<String, String>) serializer.fromJson(jwtTokenJsonString, Object.class);
        String jwtToken = tokenMap.get("token");
        LOGGER.info("Received JWT Token back successfully: {}" , jwtToken);

        String cloudInsightsCostMonthlyUrl = cloudInsightsBaseUrl + resourceId + "/cost?" + "startDate=" + startDate
                + "&endDate=" + endDate;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        LOGGER.info("Invoking Insights URL for monthly data: {}" , cloudInsightsCostMonthlyUrl);
        String costResponseMonthlyStr;
        try {
            costResponseMonthlyStr = PacHttpUtils.getHttpGet(cloudInsightsCostMonthlyUrl, headers);
        } catch (ParseException | IOException e) {
            LOGGER.error(AssetConstants.ERROR_GETAPPSBYAG,e);
            throw new DataException();
        }

        LOGGER.info("Received cost response monthly as: {}" , costResponseMonthlyStr);
        Map<String, Object> costResponseMonthlyMap = (Map<String, Object>) serializer.fromJson(costResponseMonthlyStr,
                Object.class);

        // We'll have to make the API call againto get the last week (or rather
        // last 10 days) average.

        // We will ask for only last 10 days data. Ideally, it should be 7, but
        // there
        // might be a 1 or 2 days delay, do current and previous data might not
        // have
        // data in some cases
        startDate = endDateObj.minusDays(Constants.TEN).format(DateTimeFormatter.ISO_DATE);

        String cloudInsightsCostDailyUrl = cloudInsightsBaseUrl + resourceId + "/cost?" + "startDate=" + startDate
                + "&endDate=" + endDate;
        LOGGER.info("Invoking Insights URL for daily data: {}" , cloudInsightsCostDailyUrl);
        String costResponseDailyStr;
        try {
            costResponseDailyStr = PacHttpUtils.getHttpGet(cloudInsightsCostDailyUrl, headers);
        } catch (ParseException | IOException e) {
            throw new DataException(e);
        }
        LOGGER.info("Received cost response daily as: {}" , costResponseDailyStr);

        Map<String, Object> costResponseDailyMap = (Map<String, Object>) serializer.fromJson(costResponseDailyStr,
                Object.class);
        Map<String, Object> avgAndTotalCostMap = new LinkedHashMap<>();
        avgAndTotalCostMap.put(AssetConstants.TOTAL_COST, costResponseMonthlyMap.get(AssetConstants.TOTAL_COST));
        avgAndTotalCostMap.put("lastWeekCost", costResponseDailyMap.get(AssetConstants.TOTAL_COST));

        return avgAndTotalCostMap;
    }

    @Override
    public int updateAsset(String assetGroup, String targettype, Map<String, Object> resources, String updatedBy,
            List<Map<String, Object>> updates) throws DataException {
        try {
            return repository.updateAsset(assetGroup, targettype, resources, updatedBy, updates);
        } catch (NoDataFoundException e) {
            LOGGER.error(AssetConstants.ERROR_GETAPPSBYAG,e);
            throw new DataException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getAssetLists(String assetGroup, Map<String, String> filter, int from, int size,
            String searchText) {
        return repository.getAssetLists(assetGroup, filter, from, size, searchText);
    }

    @Override
    public ResponseWithFieldsByTargetType getEditFieldsByTargetType(String resourceType) {

        List<String> editableFieldsList = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        String field = null;

        String dataTypeInfo = repository.getDataTypeInfoByTargetType(resourceType);

        if (dataTypeInfo != null) {
            JsonObject datatypeInfoJson = (JsonObject) jsonParser.parse(dataTypeInfo);
            JsonObject dataTypes = datatypeInfoJson.get("dataTypes_info").getAsJsonObject();
            Iterator<String> it = dataTypes.keySet().iterator();
            while (it.hasNext()) {
                field = it.next();
                editableFieldsList.add(field);
            }
        }

        return new ResponseWithFieldsByTargetType(resourceType, editableFieldsList);
    }

    public long getTotalCountForListingAsset(String index, String type) {
        return repository.getTotalCountForListingAsset(index, type);
    }

    @Override
    public String getResourceCreatedDate(String resourceId, String resourceType) {
        return repository.getResourceCreatedDate(resourceId, resourceType);
    }

    private List<String> getDomains(String assetGroup) {
        List<Map<String, Object>> domains = repository.getDomainsByAssetGroup(assetGroup);
        List<String> domainsList = new ArrayList<>();
        if (!domains.isEmpty()) {
            domainsList = domains.stream().map(obj -> obj.get(Constants.DOMAIN).toString())
                    .collect(Collectors.toList());
        }
        return domainsList;
    }

    @Override
    public List<Map<String, Object>> getDataTypeInfoByTargetType(String resourceType) throws ServiceException {
        JsonParser jsonParser = new JsonParser();
        String field = null;
        String datatype = null;
        Map<String, Object> editablefieldsAndValues = new HashMap<>();
        List<Map<String, Object>> dataTypeList = new ArrayList<>();

        String dataTypeInfo = repository.getDataTypeInfoByTargetType(resourceType);

        if (dataTypeInfo != null) {
            JsonObject datatypeInfoJson = (JsonObject) jsonParser.parse(dataTypeInfo);
            JsonObject dataTypes = datatypeInfoJson.get("dataTypes_info").getAsJsonObject();
            Iterator<String> it = dataTypes.keySet().iterator();
            while (it.hasNext()) {
                field = it.next();
                if (!dataTypes.get(field).isJsonNull()) {
                    datatype = dataTypes.get(field).getAsString();
                    editablefieldsAndValues.put(field, datatype);
                } else {
                    throw new ServiceException("datatype not maintained in RDS");
                }
            }
            if (!editablefieldsAndValues.isEmpty()) {
                dataTypeList.add(editablefieldsAndValues);
            }
        }
        return dataTypeList;
    }
    
    @Override
	public List<Map<String, Object>> getAssetCountAndEnvDistributionByAssetGroup(String assetGroup, String type,
			String domain, String application, String provider) {

		LOGGER.debug("Fetch counts from elastic search");

		// ES query may possibly return other types as well.
		Map<String, Object> distribution = repository.getAssetCountAndEnvDistributionByAssetGroup(assetGroup, type, application);
		
		Map<String, Long> countMap = (Map<String, Long>) distribution.get(Constants.ASSET_COUNT);
		Map<String, Object> envMap = (Map<String, Object>) distribution.get(Constants.ENV_COUNT);
		
		List<String> validTypes = Lists.newArrayList();
		if (AssetConstants.ALL.equals(type)) {
			LOGGER.debug("Remove the entries which are not valid types");
			List<Map<String, Object>> targetTypes = getTargetTypesForAssetGroup(assetGroup, domain, provider);
			validTypes = targetTypes.stream().map(obj -> obj.get(Constants.TYPE).toString())
					.collect(Collectors.toList());
			List<String> countTypes = new ArrayList<>(countMap.keySet());
			for (String _type : validTypes) {
				if (!countMap.containsKey(_type)) {
					countMap.put(_type, 0L);
				}
			}
			for (String _type : countTypes) {
				if (!validTypes.contains(_type)) {
					countMap.remove(_type);
				}
			}
		}else {
			validTypes.add(type);
		}

		List<Map<String, Object>> datasourceForAssettypes = repository.getDataSourceForTargetTypes(validTypes);

		LOGGER.debug("Creating response objects ");
		List<Map<String, Object>> countList = new ArrayList<>();
		countMap.entrySet().stream().forEach(entry -> {
			if (!Integer.valueOf(entry.getValue().toString()).equals(0)) {
				Map<String, Object> typeMap = new HashMap<>();

				String providerInfo = datasourceForAssettypes.stream()
						.filter(data -> data.get(Constants.TYPE).equals(entry.getKey())).findFirst().get()
						.get(Constants.PROVIDER).toString();
				
				Long totalCount = entry.getValue();

				typeMap.put(Constants.TYPE, entry.getKey());
				typeMap.put(Constants.COUNT, totalCount);
				typeMap.put(Constants.PROVIDER, providerInfo);
				
				List<Map<String, String>> envDistribution = calculateEnvironmentDistribution((Map<String, Long>) envMap.get(entry.getKey()), totalCount);
				
				typeMap.put(Constants.ENVIRONMENTS, envDistribution);
												
				countList.add(typeMap);
			}
		});

		return countList;
	}
    
    /*
	 * categorise the environment tags to different env like dev, stg, prod and calculate the percentage for each env
	 * 
	 * assets for which the tag is not present will be categoried under Nil category
	 * 
	 * asset types for which tag is not applicable will return empty list
	 * 
	 */
	private List<Map<String, String>> calculateEnvironmentDistribution(Map<String, Long> envDetails, Long totalCount){
		List<Map<String, String>> envDistribution = new ArrayList<>();
		
		if (!envDetails.isEmpty()) {
			//categorise env based on env tag
			Map<String, Long> envCategories = new HashMap<>();
			envDetails.entrySet().stream().forEach(environment -> {
				String env = CommonUtils.getEnvironmentForTag(environment.getKey());
				Long count = environment.getValue();
				if (envCategories.containsKey(env)) {
					count = count + envCategories.get(env);
				}
				envCategories.put(env, count);
			});
			//calculate % for each env
			envCategories.entrySet().stream().forEach(environment -> {
				Map<String, String> map = new HashMap<>();
				map.put(Constants.ENV, environment.getKey());
				String percentage = String.format("%2.1f%%", ((float) environment.getValue() / totalCount * 100));
				map.put(Constants.PERCENTAGE, percentage);
				envDistribution.add(map);
			});
			
			//get untagged asset count
			Long bucketTotal = envDetails.entrySet().stream()
					.collect(Collectors.summarizingLong(map -> (Long) map.getValue())).getSum();

			if ((totalCount - bucketTotal) > 0) {
				Map<String, String> map = new HashMap<>();
				map.put(Constants.ENV, Constants.UNTAGGED_ENV);
				String percentage = String.format("%2.1f%%", ((float) (totalCount - bucketTotal) / totalCount * 100));
				map.put(Constants.PERCENTAGE, percentage);
				envDistribution.add(map);
			}
			
			Map<String, Integer> envOrder = getEnvDistributionOrder();
			
			envDistribution.sort(Comparator.comparing((Map<String, String> env) -> envOrder.get(env.get(Constants.ENV))));
		}
		return envDistribution;
	}

	private Map<String, Integer> getEnvDistributionOrder() {
		Map<String,Integer> envOrder = new HashMap<>();
		envOrder.put(Constants.PRODUCTION_ENV, 1);
		envOrder.put(Constants.STAGE_ENV, 2);
		envOrder.put(Constants.DEV_ENV, 3);
		envOrder.put(Constants.NPE_ENV, 4);
		envOrder.put(Constants.OTHER_ENV, 5);
		envOrder.put(Constants.UNTAGGED_ENV, 6);
		return envOrder;
	}

	@Override
	public List<String> getProvidersForAssetGroup(String assetGroup) throws DataException {
		try {
			return repository.getProvidersForAssetGroup(assetGroup);
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
}
