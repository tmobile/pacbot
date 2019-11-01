package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

public class RouteTableVH extends AzureVH {
	private String name;
	private String key;
	private String regionName;
	private String type;
	private int hashCode;
	private Map<String, String> tags;
	private List<RouteTableSubnet> subnetList;
	private List<RouteVH> routeVHlist;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getRegionName() {
		return regionName;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public List<RouteTableSubnet> getSubnetList() {
		return subnetList;
	}

	public void setSubnetList(List<RouteTableSubnet> subnetList) {
		this.subnetList = subnetList;
	}

	public List<RouteVH> getRouteVHlist() {
		return routeVHlist;
	}

	public void setRouteVHlist(List<RouteVH> routeVHlist) {
		this.routeVHlist = routeVHlist;
	}

}
