package com.tmobile.pacbot.azure.inventory.vo;

public class FirewallRules {
	private String name;
	private String startIPAddress;
	private String endIPAddress;
/*	private String virtualNetworkName;
	private String virtualNetworkSubnetId;
	private String virtualNetworkResourceGroupName;
	private String virtualNetworkState;*/
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartIPAddress() {
		return startIPAddress;
	}

	public void setStartIPAddress(String startIPAddress) {
		this.startIPAddress = startIPAddress;
	}

	public String getEndIPAddress() {
		return endIPAddress;
	}

	public void setEndIPAddress(String endIPAddress) {
		this.endIPAddress = endIPAddress;
	}

/*	public String getVirtualNetworkName() {
		return virtualNetworkName;
	}

	public void setVirtualNetworkName(String virtualNetworkName) {
		this.virtualNetworkName = virtualNetworkName;
	}

	public String getVirtualNetworkSubnetId() {
		return virtualNetworkSubnetId;
	}

	public void setVirtualNetworkSubnetId(String virtualNetworkSubnetId) {
		this.virtualNetworkSubnetId = virtualNetworkSubnetId;
	}

	public String getVirtualNetworkResourceGroupName() {
		return virtualNetworkResourceGroupName;
	}

	public void setVirtualNetworkResourceGroupName(String virtualNetworkResourceGroupName) {
		this.virtualNetworkResourceGroupName = virtualNetworkResourceGroupName;
	}

	public String getVirtualNetworkState() {
		return virtualNetworkState;
	}

	public void setVirtualNetworkState(String virtualNetworkState) {
		this.virtualNetworkState = virtualNetworkState;
	}*/

}
