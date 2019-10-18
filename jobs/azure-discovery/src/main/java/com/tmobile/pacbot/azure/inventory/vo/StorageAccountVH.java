package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class StorageAccountVH extends AzureVH {

	private String resourceGroupName;
	private boolean canAccessFromAzureServices;
	private boolean isAccessAllowedFromAllNetworks;
	private boolean isAzureFilesAadIntegrationEnabled;
	private boolean isHnsEnabled;
	private String name;
	private String regionName;
	private String systemAssignedManagedServiceIdentityPrincipalId;
	private String systemAssignedManagedServiceIdentityTenantId;
	private List<String> endPoints;
	private List<String> ipAddressesWithAccess;
	private List<String> ipAddressRangesWithAccess;
	private List<String> networkSubnetsWithAccess;
	private Map<String, String> tags;
	private String kind;
	private Map<String, String> endpointsMap;

	/**
	 * @return the resourceGroupName
	 */
	public String getResourceGroupName() {
		return resourceGroupName;
	}

	/**
	 * @param resourceGroupName
	 *            the resourceGroupName to set
	 */
	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}

	/**
	 * @return the canAccessFromAzureServices
	 */
	public boolean isCanAccessFromAzureServices() {
		return canAccessFromAzureServices;
	}

	/**
	 * @param canAccessFromAzureServices
	 *            the canAccessFromAzureServices to set
	 */
	public void setCanAccessFromAzureServices(boolean canAccessFromAzureServices) {
		this.canAccessFromAzureServices = canAccessFromAzureServices;
	}

	/**
	 * @return the creationTime
	 */

	/**
	 * @return the endPoints
	 */
	public List<String> getEndPoints() {
		return endPoints;
	}

	/**
	 * @param endPoints
	 *            the endPoints to set
	 */
	public void setEndPoints(List<String> endPoints) {
		this.endPoints = endPoints;
	}

	/**
	 * @return the ipAddressRangesWithAccess
	 */
	public List<String> getIpAddressRangesWithAccess() {
		return ipAddressRangesWithAccess;
	}

	/**
	 * @param ipAddressRangesWithAccess
	 *            the ipAddressRangesWithAccess to set
	 */
	public void setIpAddressRangesWithAccess(List<String> ipAddressRangesWithAccess) {
		this.ipAddressRangesWithAccess = ipAddressRangesWithAccess;
	}

	/**
	 * @return the isAccessAllowedFromAllNetworks
	 */
	public boolean isAccessAllowedFromAllNetworks() {
		return isAccessAllowedFromAllNetworks;
	}

	/**
	 * @param isAccessAllowedFromAllNetworks
	 *            the isAccessAllowedFromAllNetworks to set
	 */
	public void setAccessAllowedFromAllNetworks(boolean isAccessAllowedFromAllNetworks) {
		this.isAccessAllowedFromAllNetworks = isAccessAllowedFromAllNetworks;
	}

	/**
	 * @return the isAzureFilesAadIntegrationEnabled
	 */
	public boolean isAzureFilesAadIntegrationEnabled() {
		return isAzureFilesAadIntegrationEnabled;
	}

	/**
	 * @param isAzureFilesAadIntegrationEnabled
	 *            the isAzureFilesAadIntegrationEnabled to set
	 */
	public void setAzureFilesAadIntegrationEnabled(boolean isAzureFilesAadIntegrationEnabled) {
		this.isAzureFilesAadIntegrationEnabled = isAzureFilesAadIntegrationEnabled;
	}

	/**
	 * @return the isHnsEnabled
	 */
	public boolean isHnsEnabled() {
		return isHnsEnabled;
	}

	/**
	 * @param isHnsEnabled
	 *            the isHnsEnabled to set
	 */
	public void setHnsEnabled(boolean isHnsEnabled) {
		this.isHnsEnabled = isHnsEnabled;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the regionName
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * @param regionName
	 *            the regionName to set
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * @return the networkSubnetsWithAccess
	 */
	public List<String> getNetworkSubnetsWithAccess() {
		return networkSubnetsWithAccess;
	}

	/**
	 * @param networkSubnetsWithAccess
	 *            the networkSubnetsWithAccess to set
	 */
	public void setNetworkSubnetsWithAccess(List<String> networkSubnetsWithAccess) {
		this.networkSubnetsWithAccess = networkSubnetsWithAccess;
	}

	/**
	 * @return the systemAssignedManagedServiceIdentityPrincipalId
	 */
	public String getSystemAssignedManagedServiceIdentityPrincipalId() {
		return systemAssignedManagedServiceIdentityPrincipalId;
	}

	/**
	 * @param systemAssignedManagedServiceIdentityPrincipalId
	 *            the systemAssignedManagedServiceIdentityPrincipalId to set
	 */
	public void setSystemAssignedManagedServiceIdentityPrincipalId(
			String systemAssignedManagedServiceIdentityPrincipalId) {
		this.systemAssignedManagedServiceIdentityPrincipalId = systemAssignedManagedServiceIdentityPrincipalId;
	}

	/**
	 * @return the systemAssignedManagedServiceIdentityTenantId
	 */
	public String getSystemAssignedManagedServiceIdentityTenantId() {
		return systemAssignedManagedServiceIdentityTenantId;
	}

	/**
	 * @param systemAssignedManagedServiceIdentityTenantId
	 *            the systemAssignedManagedServiceIdentityTenantId to set
	 */
	public void setSystemAssignedManagedServiceIdentityTenantId(String systemAssignedManagedServiceIdentityTenantId) {
		this.systemAssignedManagedServiceIdentityTenantId = systemAssignedManagedServiceIdentityTenantId;
	}

	/**
	 * @return the tags
	 */
	public Map<String, String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the ipAddressesWithAccess
	 */
	public List<String> getIpAddressesWithAccess() {
		return ipAddressesWithAccess;
	}

	/**
	 * @param ipAddressesWithAccess
	 *            the ipAddressesWithAccess to set
	 */
	public void setIpAddressesWithAccess(List<String> ipAddressesWithAccess) {
		this.ipAddressesWithAccess = ipAddressesWithAccess;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Map<String, String> getEndpointsMap() {
		return endpointsMap;
	}

	public void setEndpointsMap(Map<String, String> endpointsMap) {
		this.endpointsMap = endpointsMap;
	}

}
