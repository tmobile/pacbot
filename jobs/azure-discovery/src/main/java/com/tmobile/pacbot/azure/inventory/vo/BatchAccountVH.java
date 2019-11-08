package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class BatchAccountVH extends AzureVH {

	private String name;
	private String type;
	private String location;
	private Map<String, Object> tags;
	private String provisioningState;
	private String accountEndpoint;
	private String poolQuota;
	private String dedicatedCoreQuotaPerVMFamily;
	private String poolAllocationMode;
	private String dedicatedCoreQuota;
	private String lowPriorityCoreQuota;
	private String activeJobAndJobScheduleQuota;
	private boolean dedicatedCoreQuotaPerVMFamilyEnforced;
	private Map<String, Object> autoStorage;

	

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getLocation() {
		return location;
	}

	public Map<String, Object> getTags() {
		return tags;
	}

	

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}

	public String getProvisioningState() {
		return provisioningState;
	}

	public String getAccountEndpoint() {
		return accountEndpoint;
	}

	public String getPoolQuota() {
		return poolQuota;
	}

	public String getDedicatedCoreQuotaPerVMFamily() {
		return dedicatedCoreQuotaPerVMFamily;
	}

	public String getPoolAllocationMode() {
		return poolAllocationMode;
	}

	public String getDedicatedCoreQuota() {
		return dedicatedCoreQuota;
	}

	public String getLowPriorityCoreQuota() {
		return lowPriorityCoreQuota;
	}

	public String getActiveJobAndJobScheduleQuota() {
		return activeJobAndJobScheduleQuota;
	}

	public boolean isDedicatedCoreQuotaPerVMFamilyEnforced() {
		return dedicatedCoreQuotaPerVMFamilyEnforced;
	}

	public Map<String, Object> getAutoStorage() {
		return autoStorage;
	}

	public void setProvisioningState(String provisioningState) {
		this.provisioningState = provisioningState;
	}

	public void setAccountEndpoint(String accountEndpoint) {
		this.accountEndpoint = accountEndpoint;
	}

	public void setPoolQuota(String poolQuota) {
		this.poolQuota = poolQuota;
	}

	public void setDedicatedCoreQuotaPerVMFamily(String dedicatedCoreQuotaPerVMFamily) {
		this.dedicatedCoreQuotaPerVMFamily = dedicatedCoreQuotaPerVMFamily;
	}

	public void setPoolAllocationMode(String poolAllocationMode) {
		this.poolAllocationMode = poolAllocationMode;
	}

	public void setDedicatedCoreQuota(String dedicatedCoreQuota) {
		this.dedicatedCoreQuota = dedicatedCoreQuota;
	}

	public void setLowPriorityCoreQuota(String lowPriorityCoreQuota) {
		this.lowPriorityCoreQuota = lowPriorityCoreQuota;
	}

	public void setActiveJobAndJobScheduleQuota(String activeJobAndJobScheduleQuota) {
		this.activeJobAndJobScheduleQuota = activeJobAndJobScheduleQuota;
	}

	public void setDedicatedCoreQuotaPerVMFamilyEnforced(boolean dedicatedCoreQuotaPerVMFamilyEnforced) {
		this.dedicatedCoreQuotaPerVMFamilyEnforced = dedicatedCoreQuotaPerVMFamilyEnforced;
	}

	public void setAutoStorage(Map<String, Object> autoStorage) {
		this.autoStorage = autoStorage;
	}

}
