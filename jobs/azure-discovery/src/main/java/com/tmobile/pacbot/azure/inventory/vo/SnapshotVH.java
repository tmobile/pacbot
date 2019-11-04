package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class SnapshotVH extends AzureVH {
	private String name;
	private String type;
	private String key;
	private String regionName;
	private int sizeInGB;
	private Map<String, String> tags;

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getRegionName() {
		return regionName;
	}

	public int getSizeInGB() {
		return sizeInGB;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public void setSizeInGB(int sizeInGB) {
		this.sizeInGB = sizeInGB;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

}
