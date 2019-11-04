package com.tmobile.pacbot.azure.inventory.vo;

public class SubscriptionVH {

	@Override
	public String toString() {
		return "[subscriptionId=" + subscriptionId + ", subscriptionName=" + subscriptionName + "]";
	}
	private String subscriptionId;
	private String subscriptionName;
	public String getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(String subscription) {
		this.subscriptionId = subscription;
	}
	public String getSubscriptionName() {
		return subscriptionName;
	}
	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}
}
