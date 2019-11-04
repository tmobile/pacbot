package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.azure.management.compute.StorageAccountTypes;
import com.microsoft.azure.management.resources.fluentcore.arm.AvailabilityZoneId;

@JsonSerialize
public class VirtualMachineVH extends AzureVH {

	private String computerName;
	private String vmSize;
	private Map<String, String> tags;
	private List<String> networkInterfaceIds;
	private StorageAccountTypes osDiskStorageAccountType;
	private Set<AvailabilityZoneId> availabilityZones;
	
	
	private boolean isManagedDiskEnabled;
	private String availabilitySetId;
	private String provisioningState;
	private String licenseType;

	private List<VMDiskVH> disks;

	private String vmId;
	private boolean isBootDiagnosticsEnabled;
	private String bootDiagnosticsStorageUri;
	private boolean isManagedServiceIdentityEnabled;
	private String systemAssignedManagedServiceIdentityTenantId;
	private String systemAssignedManagedServiceIdentityPrincipalId;
	private Set<String> userAssignedManagedServiceIdentityIds;
	private String name;

	private String os;
	private String osVersion;

	private String privateIpAddress;
	private String publicIpAddress;

	private List<Map<String, String>> networkSecurityGroups;

	private String vnet;
	private String subnet;
	private String vnetName;
	private String primaryNCIMacAddress;
	private String osType;
	
	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getPrimaryNCIMacAddress() {
		return primaryNCIMacAddress;
	}

	public void setPrimaryNCIMacAddress(String primaryNCIMacAddress) {
		this.primaryNCIMacAddress = primaryNCIMacAddress;
	}

	
	public List<Map<String, String>> getSecondaryNetworks() {
		return secondaryNetworks;
	}

	public void setSecondaryNetworks(List<Map<String, String>> secondaryNetworks) {
		this.secondaryNetworks = secondaryNetworks;
	}

	private String primaryNetworkIntefaceId;

	List<Map<String, String>> secondaryNetworks;
	
	public String getVnet() {
		return vnet;
	}

	public void setVnet(String vnet) {
		this.vnet = vnet;
	}

	public String getSubnet() {
		return subnet;
	}

	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}

	public String getVnetName() {
		return vnetName;
	}

	public void setVnetName(String vnetName) {
		this.vnetName = vnetName;
	}

	public String getPrimaryNetworkIntefaceId() {
		return primaryNetworkIntefaceId;
	}

	public void setPrimaryNetworkIntefaceId(String primaryNetworkIntefaceId) {
		this.primaryNetworkIntefaceId = primaryNetworkIntefaceId;
	}

	public List<Map<String, String>> getNetworkSecurityGroups() {
		return networkSecurityGroups;
	}

	public void setNetworkSecurityGroups(List<Map<String, String>> networkSecurityGroups) {
		this.networkSecurityGroups = networkSecurityGroups;
	}

	public String getPrivateIpAddress() {
		return privateIpAddress;
	}

	public void setPrivateIpAddress(String privateIpAddress) {
		this.privateIpAddress = privateIpAddress;
	}

	public String getPublicIpAddress() {
		return publicIpAddress;
	}

	public void setPublicIpAddress(String publicIpAddress) {
		this.publicIpAddress = publicIpAddress;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getStatus() {
		return status;
	}

	private String status;

	public List<VMDiskVH> getDisks() {
		return disks;
	}

	public void setDisks(List<VMDiskVH> disks) {
		this.disks = disks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComputerName() {
		return computerName;
	}

	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}

	public boolean isManagedDiskEnabled() {
		return isManagedDiskEnabled;
	}

	public void setManagedDiskEnabled(boolean isManagedDiskEnabled) {
		this.isManagedDiskEnabled = isManagedDiskEnabled;
	}

	

	public String getAvailabilitySetId() {
		return availabilitySetId;
	}

	public void setAvailabilitySetId(String availabilitySetId) {
		this.availabilitySetId = availabilitySetId;
	}

	public String getProvisioningState() {
		return provisioningState;
	}

	public void setProvisioningState(String provisioningState) {
		this.provisioningState = provisioningState;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public String getVmId() {
		return vmId;
	}

	public void setVmId(String vmId) {
		this.vmId = vmId;
	}

	public boolean isBootDiagnosticsEnabled() {
		return isBootDiagnosticsEnabled;
	}

	public void setBootDiagnosticsEnabled(boolean isBootDiagnosticsEnabled) {
		this.isBootDiagnosticsEnabled = isBootDiagnosticsEnabled;
	}

	public String getBootDiagnosticsStorageUri() {
		return bootDiagnosticsStorageUri;
	}

	public void setBootDiagnosticsStorageUri(String bootDiagnosticsStorageUri) {
		this.bootDiagnosticsStorageUri = bootDiagnosticsStorageUri;
	}

	public boolean isManagedServiceIdentityEnabled() {
		return isManagedServiceIdentityEnabled;
	}

	public void setManagedServiceIdentityEnabled(boolean isManagedServiceIdentityEnabled) {
		this.isManagedServiceIdentityEnabled = isManagedServiceIdentityEnabled;
	}

	public String getSystemAssignedManagedServiceIdentityTenantId() {
		return systemAssignedManagedServiceIdentityTenantId;
	}

	public void setSystemAssignedManagedServiceIdentityTenantId(String systemAssignedManagedServiceIdentityTenantId) {
		this.systemAssignedManagedServiceIdentityTenantId = systemAssignedManagedServiceIdentityTenantId;
	}

	public String getSystemAssignedManagedServiceIdentityPrincipalId() {
		return systemAssignedManagedServiceIdentityPrincipalId;
	}

	public void setSystemAssignedManagedServiceIdentityPrincipalId(
			String systemAssignedManagedServiceIdentityPrincipalId) {
		this.systemAssignedManagedServiceIdentityPrincipalId = systemAssignedManagedServiceIdentityPrincipalId;
	}

	public Set<String> getUserAssignedManagedServiceIdentityIds() {
		return userAssignedManagedServiceIdentityIds;
	}

	public void setUserAssignedManagedServiceIdentityIds(Set<String> userAssignedManagedServiceIdentityIds) {
		this.userAssignedManagedServiceIdentityIds = userAssignedManagedServiceIdentityIds;
	}



	public String getVmSize() {
		return vmSize;
	}

	public void setVmSize(String vmSize) {
		this.vmSize = vmSize;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public List<String> getNetworkInterfaceIds() {
		return networkInterfaceIds;
	}

	public void setNetworkInterfaceIds(List<String> networkInterfaceIds) {
		this.networkInterfaceIds = networkInterfaceIds;
	}

	public StorageAccountTypes getOsDiskStorageAccountType() {
		return osDiskStorageAccountType;
	}

	public void setOsDiskStorageAccountType(StorageAccountTypes osDiskStorageAccountType) {
		this.osDiskStorageAccountType = osDiskStorageAccountType;
	}

	public Set<AvailabilityZoneId> getAvailabilityZones() {
		return availabilityZones;
	}

	public void setAvailabilityZones(Set<AvailabilityZoneId> availabilityZones) {
		this.availabilityZones = availabilityZones;
	}

	public void setStatus(String status) {
		this.status = status;

	}

	

}
