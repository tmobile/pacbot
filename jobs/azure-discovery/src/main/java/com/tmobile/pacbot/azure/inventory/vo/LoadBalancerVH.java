package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancerPrivateFrontend;
import com.microsoft.azure.management.network.LoadBalancerPublicFrontend;
import com.microsoft.azure.management.network.LoadBalancingRule;

@JsonSerialize
public class LoadBalancerVH extends AzureVH {

	private int hashCode;
	private String name;

	private String key;
	private LoadBalancer refresh;

	private String regionName;
	private String type;
	private List<String> publicIPAddressIds;
	private Map<String, String> tags;
	private Map<String, LoadBalancingRule> loadBalancingRules;
	private Map<String, LoadBalancerPrivateFrontend> privateFrontends;
	private Map<String, LoadBalancerPublicFrontend> publicFrontends;

	public int getHashCode() {
		return hashCode;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public Map<String, LoadBalancingRule> getLoadBalancingRules() {
		return loadBalancingRules;
	}

	public Map<String, LoadBalancerPrivateFrontend> getPrivateFrontends() {
		return privateFrontends;
	}

	public Map<String, LoadBalancerPublicFrontend> getPublicFrontends() {
		return publicFrontends;
	}

	public List<String> getPublicIPAddressIds() {
		return publicIPAddressIds;
	}

	public LoadBalancer getRefresh() {
		return refresh;
	}

	public String getRegionName() {
		return regionName;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public String getType() {
		return type;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setLoadBalancingRules(Map<String, LoadBalancingRule> loadBalancingRules) {
		this.loadBalancingRules = loadBalancingRules;
	}

	public void setPrivateFrontends(Map<String, LoadBalancerPrivateFrontend> privateFrontends) {
		this.privateFrontends = privateFrontends;
	}

	public void setPublicFrontends(Map<String, LoadBalancerPublicFrontend> publicFrontends) {
		this.publicFrontends = publicFrontends;
	}

	public void setPublicIPAddressIds(List<String> publicIPAddressIds) {
		this.publicIPAddressIds = publicIPAddressIds;
	}

	public void setRefresh(LoadBalancer refresh) {
		this.refresh = refresh;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public void setType(String type) {
		this.type = type;
	}

}
