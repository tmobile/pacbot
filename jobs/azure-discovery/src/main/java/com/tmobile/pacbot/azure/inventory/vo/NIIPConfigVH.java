package com.tmobile.pacbot.azure.inventory.vo;

public class NIIPConfigVH {
	private String name;
	private String privateIPAddress;
	private String privateIPAddressVersion;
	private String networkName;
	private String subnetName;
	private boolean isPrimary;
	private String publicIPAddress;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrivateIPAddress() {
		return privateIPAddress;
	}
	public void setPrivateIPAddress(String privateIPAddress) {
		this.privateIPAddress = privateIPAddress;
	}
	public String getPrivateIPAddressVersion() {
		return privateIPAddressVersion;
	}
	public void setPrivateIPAddressVersion(String privateIPAddressVersion) {
		this.privateIPAddressVersion = privateIPAddressVersion;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getSubnetName() {
		return subnetName;
	}
	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}
	public boolean isPrimary() {
		return isPrimary;
	}
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getPublicIPAddress() {
		return publicIPAddress;
	}
	public void setPublicIPAddress(String publicIPAddress) {
		this.publicIPAddress = publicIPAddress;
	}
	
	

}
