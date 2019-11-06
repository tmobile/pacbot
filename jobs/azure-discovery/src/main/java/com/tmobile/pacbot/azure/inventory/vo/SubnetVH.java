package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

public class SubnetVH extends AzureVH {
	private String etag;
	private String name;
	private String type;
	private List<Map<String, Object>> ipConfigurations;
	private String addressPrefix;
	private String privateLinkServiceNetworkPolicies;
	private String provisioningState;
	private String privateEndpointNetworkPolicies;

	public List<Map<String, Object>> getIpConfigurations() {
		return ipConfigurations;
	}

	public String getAddressPrefix() {
		return addressPrefix;
	}

	public String getPrivateLinkServiceNetworkPolicies() {
		return privateLinkServiceNetworkPolicies;
	}

	public String getProvisioningState() {
		return provisioningState;
	}

	public String getPrivateEndpointNetworkPolicies() {
		return privateEndpointNetworkPolicies;
	}

	public void setIpConfigurations(List<Map<String, Object>> ipConfigurations) {
		this.ipConfigurations = ipConfigurations;
	}

	public void setAddressPrefix(String addressPrefix) {
		this.addressPrefix = addressPrefix;
	}

	public void setPrivateLinkServiceNetworkPolicies(String privateLinkServiceNetworkPolicies) {
		this.privateLinkServiceNetworkPolicies = privateLinkServiceNetworkPolicies;
	}

	public void setProvisioningState(String provisioningState) {
		this.provisioningState = provisioningState;
	}

	public void setPrivateEndpointNetworkPolicies(String privateEndpointNetworkPolicies) {
		this.privateEndpointNetworkPolicies = privateEndpointNetworkPolicies;
	}

	public String getEtag() {
		return etag;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

}
