package com.tmobile.pacbot.azure.inventory.vo;

public class VirtualNetworkRuleVH {
	private String id;
	private boolean ignoreMissingVNetServiceEndpoint;

	public String getId() {
		return id;
	}

	public boolean isIgnoreMissingVNetServiceEndpoint() {
		return ignoreMissingVNetServiceEndpoint;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIgnoreMissingVNetServiceEndpoint(boolean ignoreMissingVNetServiceEndpoint) {
		this.ignoreMissingVNetServiceEndpoint = ignoreMissingVNetServiceEndpoint;
	}

}
