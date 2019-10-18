package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class SecurityGroupVH extends AzureVH {
	
	private String key;
	private String name;
	private Map<String, String> tags;
	private Set<String> networkInterfaceIds;
	private List<NSGSubnet> subnetList;
	private List<NSGSecurityRule> inBoundSecurityRules;
	private List<NSGSecurityRule> outBoundSecurityRules;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
	public Set<String> getNetworkInterfaceIds() {
		return networkInterfaceIds;
	}

	public void setNetworkInterfaceIds(Set<String> networkInterfaceIds) {
		this.networkInterfaceIds = networkInterfaceIds;
	}
	
	public List<NSGSubnet> getSubnetList() {
		return subnetList;
	}

	public void setSubnetList(List<NSGSubnet> subnetList) {
		this.subnetList = subnetList;
	}

	public List<NSGSecurityRule> getInBoundSecurityRules() {
		return inBoundSecurityRules;
	}

	public void setInBoundSecurityRules(List<NSGSecurityRule> inBoundSecurityRules) {
		this.inBoundSecurityRules = inBoundSecurityRules;
	}

	public List<NSGSecurityRule> getOutBoundSecurityRules() {
		return outBoundSecurityRules;
	}

	public void setOutBoundSecurityRules(List<NSGSecurityRule> outBoundSecurityRules) {
		this.outBoundSecurityRules = outBoundSecurityRules;
	}


	
}
