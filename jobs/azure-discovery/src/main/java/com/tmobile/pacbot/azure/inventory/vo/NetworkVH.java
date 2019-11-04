package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.azure.management.network.Subnet;

@JsonSerialize
public class NetworkVH extends AzureVH {

	private String ddosProtectionPlanId;
	private int hashCode;
	private boolean isDdosProtectionEnabled;
	private boolean isVmProtectionEnabled;
	private String key;
	private String name;
	private List<String> addressSpaces;
	private List<String> dnsServerIPs;
	private Map<String, Subnet> subnets;
	private Map<String, String> tags;

	public String getDdosProtectionPlanId() {
		return ddosProtectionPlanId;
	}

	public void setDdosProtectionPlanId(String ddosProtectionPlanId) {
		this.ddosProtectionPlanId = ddosProtectionPlanId;
	}

	public List<String> getAddressSpaces() {
		return addressSpaces;
	}

	public void setAddressSpaces(List<String> addressSpaces) {
		this.addressSpaces = addressSpaces;
	}

	public List<String> getDnsServerIPs() {
		return dnsServerIPs;
	}

	public void setDnsServerIPs(List<String> dnsServerIPs) {
		this.dnsServerIPs = dnsServerIPs;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public boolean isDdosProtectionEnabled() {
		return isDdosProtectionEnabled;
	}

	public void setDdosProtectionEnabled(boolean isDdosProtectionEnabled) {
		this.isDdosProtectionEnabled = isDdosProtectionEnabled;
	}

	public boolean isVmProtectionEnabled() {
		return isVmProtectionEnabled;
	}

	public void setVmProtectionEnabled(boolean isVmProtectionEnabled) {
		this.isVmProtectionEnabled = isVmProtectionEnabled;
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


	public Map<String, Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(Map<String, Subnet> subnets) {
		this.subnets = subnets;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

}
