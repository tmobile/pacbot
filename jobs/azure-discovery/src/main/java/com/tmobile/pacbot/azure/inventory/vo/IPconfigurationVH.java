package com.tmobile.pacbot.azure.inventory.vo;

public class IPconfigurationVH {

	private String networkSecurityGroup;
	private boolean isPrimary;
	private String key;
	private String name;
	private String networkId;
	private String privateIPAddress;
	private String version;
	private String publicIPAddressId;
	private String type;

	public String getNetworkSecurityGroup() {
		return networkSecurityGroup;
	}

	public void setNetworkSecurityGroup(String networkSecurityGroup) {
		this.networkSecurityGroup = networkSecurityGroup;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

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

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getPrivateIPAddress() {
		return privateIPAddress;
	}

	public void setPrivateIPAddress(String privateIPAddress) {
		this.privateIPAddress = privateIPAddress;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPublicIPAddressId() {
		return publicIPAddressId;
	}

	public void setPublicIPAddressId(String publicIPAddressId) {
		this.publicIPAddressId = publicIPAddressId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
