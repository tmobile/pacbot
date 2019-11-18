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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.asset.model.DefaultUserAssetGroup;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.NoDataFoundException;

/**
 * This is the Asset Repository layer which makes call to RDS DB as well as ElasticSearch
 */
@Repository
public interface AssetRepository {

    /**
     * Fetches the total count of assets for the particular asset group. If no
     * type is passed, all the assets of valid target type for the asset group
     * is considered.
     *
     * @param aseetGroupName name of the asset group
     * @param type target type
     * @param application the application of asset group
     * 
     * @return list of type and its asset count.
     */
    public Map<String, Long> getAssetCountByAssetGroup(String aseetGroupName, String type, String application);

    /**
     * Fetches all the target types for the particular asset group. If asset
     * group is passed as aws then all the available target types is returned .
     *
     * @param aseetGroupName name of the asset group
     * @param domain the domain of asset group
     * 
     * @return list of target types.
     */
    public List<Map<String, Object>> getTargetTypesByAssetGroup(String aseetGroupName, String domain, String provider);

    /**
     * Fetches all the applications for the particular asset group.
     *
     * @param aseetGroupName name of the asset group
     * @param domain the domain of asset group
     * 
     * @return list of applications.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<String> getApplicationByAssetGroup(String aseetGroupName, String domain) throws DataException;

    /**
     * Fetches all the applications for the particular asset group.
     *
     * @param aseetGroupName name of the asset group
     * 
     * @return list of applications.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<String> getApplicationByAssetGroup(String aseetGroupName) throws DataException;

    /**
     * Fetches all the environments for the particular asset group. Application
     * and Domain can also be passed to filter the environments
     *
     * @param assetGroup name of the asset group
     * @param application application needed to be filtered
     * @param domain the domain of asset group
     * 
     * @return list of environments.
     */
    public List<String> getEnvironmentsByAssetGroup(String assetGroup, String application, String domain);

    /**
     * Fetches all the target type and its details
     *
     * @return list of target type details.
     */
    public List<Map<String, Object>> getAllTargetTypes(String datasource);

    /**
     * Fetches all the asset groups and its name, display name, description,
     * type, createdby and domains
     *
     * @return list of asset group details.
     */
    public List<Map<String, Object>> getAllAssetGroups();

    /**
     * Fetches all the details of the asset group - name, display name,
     * description, type, createdby, appcount, assetcount and domains.
     *
     * @param assetGroup name of the asset group
     * 
     * @return asset group info.
     */
    public Map<String, Object> getAssetGroupInfo(String assetGroup);

    /**
     * Fetches the total asset count for each application for the given target
     * type and asset group.
     *
     * @param assetGroup name of the asset group
     * @param type target type of the asset group
     * 
     * @return list of applications and its asset count.
     * @throws DataException when there is an error while fetching data from ES
     */
    public Map<String, Long> getAssetCountByApplication(String assetGroup, String type) throws DataException;

    /**
     * Fetches the asset trends(daily min/max) over the period of last 1 month
     * for the given asset group. From and to can be passed to fetch the asset
     * trends for particular days.
     *
     * @param assetGroup name of the asset group
     * @param type target type of the asset group
     * @param from from date
     * @param to to date
     * 
     * @return list of days with its min/max asset count.
     */
    public List<Map<String, Object>> getAssetMinMax(String assetGroup, String type, Date from, Date to);

    /**
     * Save/update asset group details in DB.Saves default asset group for the
     * user id.
     *
     * @param defaultUserAssetGroup This request expects userid and assetgroup name as mandatory.
     * 
     * @return boolean as updated status.
     */
    public Integer saveOrUpdateAssetGroup(DefaultUserAssetGroup defaultUserAssetGroup);

    /**
     * Fetches the default asset group the user has saved for the given asset
     * group.
     *
     * @param userId id of the user
     * 
     * @return asset group name.
     */
    public String getUserDefaultAssetGroup(String userId);

    /**
     * Fetches the total asset count for each environment for the given target
     * type and asset group.
     *
     * @param assetGroup  name of the asset group
     * @param type target type of the asset group
     * @param application application needed for the count
     * 
     * @return list of environment and its asset count.
     */
    public List<Map<String, Object>> getAssetCountByEnvironment(String assetGroup, String application, String type);

    /**
     * Saves the recently viewed asset group for the user id.
     *
     * @param assetGroup  name of the asset group
     * @param userId id of the user
     * 
     * @return updated list of asset group for the userId.
     * @throws DataException when there is an error while fetching/updating data from ES and DB
     */
    public List<Map<String, Object>> saveAndAppendAssetGroup(String userId, String assetGroup) throws DataException;

    /**
     * Fetches all the asset for the given asset group.
     *
     * @param assetGroup  name of the asset group
     * @param filter application,environment,resourceType as optional filters
     * @param searchText searchText is used to match any text you are looking for
     * @param from for pagination
     * @param size for pagination
     * 
     * @return list of assets and its some details.
     */
    public List<Map<String, Object>> getListAssets(String assetGroup, Map<String, String> filter, int from, int size,
            String searchText);

    /**
     * Fetches the total asset count for the given asset group.
     *
     * @param assetGroup  name of the asset group
     * @param filter application,environment,resourceType as optional filters
     * @param searchText searchText is used to match any text you are looking for
     * @param from for pagination
     * @param size for pagination
     * 
     * @return list of assets and its some details.
     */
    long getAssetCount(String assetGroup, Map<String, String> filter, String searchText);

    /**
     * Fetches the CPU utilization for the given instanceid.
     *
     * @param instanceId id of the instance
     * 
     * @return list of date and its CPU utilization of the instance id.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getCpuUtilizationByAssetGroupAndInstanceId(String instanceId) throws DataException;

    /**
     * Fetches the Disk utilization for the given instanceid.
     *
     * @param instanceId Id of the instance
     * 
     * @return list of disk name, size and free space of the instance id.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getDiskUtilizationByAssetGroupAndInstanceId(String instanceId) throws DataException;

    /**
     * Fetches the Softwares installed for the given instanceid.
     *
     * @param instanceId id of the instance
     * @param from for pagination
     * @param size for pagination
     * @param searchText used to match any text you are looking for
     * 
     * @return list of software name and its version installed on the instance id.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getSoftwareInstalledDetailsByAssetGroupAndInstanceId(String instanceId,
            Integer from, Integer size, String searchText) throws DataException;

    /**
     * Fetches the ec2 resource details from RHN for the given resourceId.
     *
     * @param resourceId id of the resource
     * 
     * @return all the ec2 details for resourceId.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getEc2ResourceDetailFromRhn(String resourceId) throws DataException;

    /**
     * Fetches the ec2 resource details for the given assetgroup and resourceId.
     *
     * @param ag  name of the asset group
     * @param resourceId id of the resource
     * 
     * @return all the ec2 details for assetgroup.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getEc2ResourceDetail(String ag, String resourceId) throws DataException;

    /**
     * Fetches the ec2 security details for the given resourceId.
     *
     * @param resourceId id of the resource
     * 
     * @return all the ec2 security details for resourceId.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getEc2ResourceSecurityGroupDetail(String resourceId) throws DataException;

    /**
     * Fetches the patchable assets for the given assetgroup.If patched filter is false it returns the unpatched assets
     * and if patched is true returns the patched assets.It also filters the the list based on resource type passed in filter 
     * else returns all the assets of the asset group
     *
     * @param assetGroup  name of the asset group
     * @param filter patched(true/false),application,environment,resourcetype,
     *         executivesponsor,director as optional filters
     * 
     * @return list of assets patched/unpatched.
     */
    public List<Map<String, Object>> getListAssetsPatchable(String assetGroup, Map<String, String> filter);

    /**
     * Fetches the taggable assets for the given assetgroup.If tagged filter is false it returns the untagged assets
     * and if tagged is true returns the tagged assets.It also filters the the list based on resource type passed in filter 
     * else returns all the assets of the asset group
     *
     * @param assetGroup  name of the asset group
     * @param filter tagged(true/false),application,environment,resourcetype,
     *         tagname(must with tagged) as optional filters
     * 
     * @return list of assets tagged/untagged.
     */
    public List<Map<String, Object>> getListAssetsTaggable(String assetGroup, Map<String, String> filter);

    /**
     * Fetches the ec2 resource block devices detail for the given resourceId.
     *
     * @param resourceId id of the resource
     * 
     * @return all the ec2 resource block devices details for resourceId.
     * @throws DataException when there is an error while fetching data from ES
     */
    List<Map<String, Object>> getEc2ResourceBlockDevicesDetail(String resourceId) throws DataException;

    /**
     * Fetches the resource details for the given resourceId
     *
     * @param ag name of the asset group
     * @param resourceType type of the resource
     * @param resourceId id of the resource
     * 
     * @return name,value and category of the resourceId.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getResourceDetail(String ag, String resourceType, String resourceId)
            throws DataException;

    /**
     * Fetches the vulnerable assets for the given assetgroup. It looks for any particular resourceType passed in the filter 
     * else considers ec2 and onpremserver for targetype and fetch it vulnerable asset details.
     *
     * @param assetGroup  name of the asset group
     * @param filter qid as mandatory and application,environment as optional filters
     * 
     * @return list of vulnerable assets.
     */
    public List<Map<String, Object>> getListAssetsVulnerable(String assetGroup, Map<String, String> filter);

    /**
     * Fetches the port which are in open status for the given instanceId. 
     *
     * @param instanceId id of the instance
     * @param from for pagination
     * @param size for pagination
     * @param searchText used to match any text you are looking for
     * 
     * @return list of open ports.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getOpenPortDetailsByInstanceId(String instanceId, Integer from, Integer size,
            String searchText) throws DataException;

    /**
     * Fetches the assets with open issue status for the rule id passed in the filter.
     *
     * @param assetGroup  name of the asset group
     * @param filter  ruleid as mandatory and compliant(true/false),application,environment,resourcetype as
     *         optional filters
     * 
     * @return list of assets with open issue status.
     */
    public List<Map<String, Object>> getListAssetsScanned(String assetGroup, Map<String, String> filter);

    /**
     * Fetches the resource created info for the given resourceId
     *
     * @param resourceId id of the resource
     * 
     * @return resource create info details
     * @throws DataException when there is an error while fetching data from ES
     */
    public Map<String, Object> getResourceCreateInfo(String resourceId) throws DataException;

    /**
     * Fetches the open,closed and upcoming notification count for the given instance.
     *
     * @param instanceId id of the instance
     * 
     * @return list of assets with open,closed and upcoming count.
     * @throws DataException when there is an error while fetching data from ES
     */
    public Map<String, Long> getNotificationSummary(String instanceId) throws DataException;

    /**
     * Saves the config details for the given resourceId.
     *
     * @param resourceId id of the resource
     * @param configType type of the config
     * @param config config of the asset
     * 
     * @return 1 or any Integer.
     */
    public Integer saveAssetConfig(String resourceId, String configType, String config);

    /**
     * Fetches the config details for the given resourceId and config type.
     *
     * @param resourceId id of the resource
     * @param configType type of the config
     * 
     * @return config details as string.
     */
    public String retrieveAssetConfig(String resourceId, String configType);

    /**
     * Fetches the notification details of the instanceId.
     *
     * @param instanceId id of the instance
     * @param filters any filter
     * @param searchText searchText is used to match any text you are looking for
     * 
     * @return list of notification details.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getNotificationDetails(String instanceId, Map<String, String> filters,
            String searchText) throws DataException;

    /**
     * Fetches the qualys details of the resourceId.
     *
     * @param resourceId id of the resource
     * 
     * @return list of qualys details.
     * @throws DataException when there is an error while fetching data from ES
     */
    public List<Map<String, Object>> getQualysDetail(String resourceId) throws DataException;

    /**
     * Updates the asset details.
     *
     * @param assetGroup name of the asset group
     * @param targettype target type to be updated
     * @param resources resources that needs to updated
     * @param updatedBy user id of the user 
     * @param updates the values with which the resources needs to be updated.
     * 
     * @return integer count of rows updated.
     * @throws DataException when there is an error while fetching data from Es
     * @throws NoDataFoundException when there is an error while fetching data from ES
     */
    public int updateAsset(String assetGroup, String targettype, Map<String, Object> resources, String updatedBy,
            List<Map<String, Object>> updates) throws DataException,NoDataFoundException;

    /**
     * Fetches all the asset details for the given asset group.
     *
     * @param assetGroup  name of the asset group
     * @param filter application,environment,resourceType as optional filters
     * @param from for pagination
     * @param size for pagination
     * @param searchText is used to match any text you are looking for
     * 
     * @return list of complete asset details.
     */
    public List<Map<String, Object>> getAssetLists(String assetGroup, Map<String, String> filter, int from, int size,
            String searchText);

    /**
     * Fetches the total count of the documents for the index and type.
     *
     * @param index ES index
     * @param type ES type
     * 
     * @return count of docs
     */
    public long getTotalCountForListingAsset(String index, String type);

    /**
     * Fetches the created date for the give resourceId.
     *
     * @param resourceId id of the resource
     * @param resourceType type of the resource
     * 
     * @return created date as string 
     */
    public String getResourceCreatedDate(String resourceId, String resourceType);

    /**
     * Fetches the domains for the given asset group.
     *
     * @param aseetGroupName name of the asset group
     * 
     * @return created date as string 
     */
    List<Map<String, Object>> getDomainsByAssetGroup(String aseetGroupName);

    /**
     * Fetches the updatable fields info maintained in RDS for the given targettype.
     *
     * @param targettype name of the target type
     * 
     * @return  list of updatable fields
     */
    public String getDataTypeInfoByTargetType(String targettype);

    /**
     * Fetches all the asset group and its corresponding domain.
     *
     * @return  list of asset group and its corresponding domain
     */
    public List<Map<String, Object>> getAssetGroupAndDomains();

    /**
     * Fetches the AD group details
     *
     * @return list of AD group details.
     * @throws DataException
     */
    public List<Map<String, Object>> getAdGroupDetails() throws DataException;
    
    /**
     * Fetches all the applications and assetcount for each application for the particular asset group.
     *
     * @param assetGroupName name of the asset group
     * @param domain the domain of asset group
     * 
     * @return list of applications.
     * @throws DataException when there is an error while fetching data from ES
     */
    public Map<String, Long> getApplicationAssetCountByAssetGroup(String assetGroupName, String domain, String provider) throws DataException;
    
    /**
     * Fetches all the datasource and its targetName for the list of targetNames
     *
     * @return list of target type details.s
     */
    public List<Map<String, Object>> getDataSourceForTargetTypes(List<String> targetTypes);

	public Map<String, Object> getApplicationAssetCountByAssetGroupWithProvider(String assetGroupName, String domain,
			String provider) throws DataException;
	
	/**
     * Fetches the total count of assets and distribution based on environment for the particular asset group. If no
     * type is passed, all the assets of valid target type for the asset group
     * is considered.
     *
     * @param aseetGroupName name of the asset group
     * @param type target type
     * @param domain the domain of asset group
     * @param application the application of asset group
     * 
     * @return list of type and its asset count.
     */
	public Map<String, Object> getAssetCountAndEnvDistributionByAssetGroup(String aseetGroupName, String type,
			String application);

	/**
     * Fetches provider list for an asset group
     *
     * @param Asset Group
     * @return list of providers.
     * @throws DataException
     */
    public List<String> getProvidersForAssetGroup(String assetGroup) throws DataException;
    
}
