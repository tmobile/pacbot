package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class ResourceGroupVH extends AzureVH {

	private String key;
	private String type;
	private String provisioningState;
	private String regionName;
	private Map<String, String> tags;

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public String getProvisioningState() {
		return provisioningState;
	}

	public String getRegionName() {
		return regionName;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setProvisioningState(String provisioningState) {
		this.provisioningState = provisioningState;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

}
