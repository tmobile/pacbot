package com.tmobile.pacbot.azure.inventory.vo;

public class NSGSubnet {

	private String addressPrefix;
	private String name;
	private String vnet;

	public String getVnet() {
		return vnet;
	}

	public void setVnet(String vnet) {
		this.vnet = vnet;
	}

	public String getAddressPrefix() {
		return addressPrefix;
	}

	public void setAddressPrefix(String addressPrefix) {
		this.addressPrefix = addressPrefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
