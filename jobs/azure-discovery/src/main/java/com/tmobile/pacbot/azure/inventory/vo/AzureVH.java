package com.tmobile.pacbot.azure.inventory.vo;

import com.tmobile.pacbot.azure.inventory.collector.Util;

public class AzureVH {

	private String discoverydate;
	private String _cloudType = "Azure";
	private String subscription;
	private String region;
	private String subscriptionName;
	private String resourceGroupName;
	private String id;

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSubscriptionName() {
		return subscriptionName;
	}

	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = Util.removeFirstSlash(id);
	}

	public String getResourceGroupName() {
		return resourceGroupName;
	}

	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}

	public String getDiscoverydate() {
		return discoverydate;
	}

	public void setDiscoverydate(String discoverydate) {
		this.discoverydate = discoverydate;
	}

	public String get_cloudType() {
		return _cloudType;
	}

	public void set_cloudType(String _cloudType) {
		this._cloudType = _cloudType;
	}

}
