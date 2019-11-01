package com.tmobile.pacbot.azure.inventory.vo;

public class RouteVH {
	private String name;
	private String addressPrefix;
	private String nextHop;

	public String getName() {
		return name;
	}

	public String getAddressPrefix() {
		return addressPrefix;
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddressPrefix(String addressPrefix) {
		this.addressPrefix = addressPrefix;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

}
