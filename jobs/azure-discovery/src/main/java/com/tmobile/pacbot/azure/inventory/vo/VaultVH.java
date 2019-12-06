package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class VaultVH extends AzureVH {
	private String name;
	private String type;
	private String location;
	private Map<String, Object> tags;
	private Map<String, Object> sku;
	private boolean enabledForDeployment;
	private boolean enabledForDiskEncryption;
	private boolean enabledForTemplateDeployment;
	private String tenantId;
	private String provisioningState;
	private String vaultUri;

	public Map<String, Object> getSku() {
		return sku;
	}

	public boolean isEnabledForDeployment() {
		return enabledForDeployment;
	}

	public boolean isEnabledForDiskEncryption() {
		return enabledForDiskEncryption;
	}

	public boolean isEnabledForTemplateDeployment() {
		return enabledForTemplateDeployment;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getProvisioningState() {
		return provisioningState;
	}

	public String getVaultUri() {
		return vaultUri;
	}

	public void setSku(Map<String, Object> sku) {
		this.sku = sku;
	}

	public void setEnabledForDeployment(boolean enabledForDeployment) {
		this.enabledForDeployment = enabledForDeployment;
	}

	public void setEnabledForDiskEncryption(boolean enabledForDiskEncryption) {
		this.enabledForDiskEncryption = enabledForDiskEncryption;
	}

	public void setEnabledForTemplateDeployment(boolean enabledForTemplateDeployment) {
		this.enabledForTemplateDeployment = enabledForTemplateDeployment;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setProvisioningState(String provisioningState) {
		this.provisioningState = provisioningState;
	}

	public void setVaultUri(String vaultUri) {
		this.vaultUri = vaultUri;
	}

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

}
