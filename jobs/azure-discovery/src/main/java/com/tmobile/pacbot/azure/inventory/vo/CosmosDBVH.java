package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

public class CosmosDBVH extends AzureVH {
	private String key;
	private String name;
	private String type;
	private Map<String, String> tags;
	private String ipRangeFilter;
	private boolean multipleWriteLocationsEnabled;
	private List<VirtualNetworkRuleVH> virtualNetworkRuleList;

	

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Map<String, String> getTags() {
		return tags;
	}


	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getIpRangeFilter() {
		return ipRangeFilter;
	}

	public boolean isMultipleWriteLocationsEnabled() {
		return multipleWriteLocationsEnabled;
	}

	public void setIpRangeFilter(String ipRangeFilter) {
		this.ipRangeFilter = ipRangeFilter;
	}

	public void setMultipleWriteLocationsEnabled(boolean multipleWriteLocationsEnabled) {
		this.multipleWriteLocationsEnabled = multipleWriteLocationsEnabled;
	}

	public List<VirtualNetworkRuleVH> getVirtualNetworkRuleList() {
		return virtualNetworkRuleList;
	}

	public void setVirtualNetworkRuleList(List<VirtualNetworkRuleVH> virtualNetworkRuleList) {
		this.virtualNetworkRuleList = virtualNetworkRuleList;
	}

}
