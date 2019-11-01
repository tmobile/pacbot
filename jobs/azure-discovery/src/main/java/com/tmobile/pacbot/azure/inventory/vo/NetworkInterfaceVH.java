package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.azure.management.network.NicIPConfiguration;

@JsonSerialize
public class NetworkInterfaceVH extends AzureVH {

	private String internalDnsNameLabel;
	private String internalDomainNameSuffix;
	private String internalFqdn;
	private boolean isAcceleratedNetworkingEnabled;
	private boolean isIPForwardingEnabled;
	private String key;
	private String macAddress;
	private String name;
	private String networkSecurityGroupId;
	private String primaryPrivateIP;
	private String type;
	private String virtualMachineId;
	private List<String> appliedDnsServers;
	private List<String> dnsServers;
	private List<NIIPConfigVH> ipConfigurationList;
	private Map<String, String> tags;

	public List<String> getAppliedDnsServers() {
		return appliedDnsServers;
	}

	public void setAppliedDnsServers(List<String> appliedDnsServers) {
		this.appliedDnsServers = appliedDnsServers;
	}

	public List<String> getDnsServers() {
		return dnsServers;
	}

	public void setDnsServers(List<String> dnsServers) {
		this.dnsServers = dnsServers;
	}

	public String getInternalDnsNameLabel() {
		return internalDnsNameLabel;
	}

	public void setInternalDnsNameLabel(String internalDnsNameLabel) {
		this.internalDnsNameLabel = internalDnsNameLabel;
	}

	public String getInternalDomainNameSuffix() {
		return internalDomainNameSuffix;
	}

	public void setInternalDomainNameSuffix(String internalDomainNameSuffix) {
		this.internalDomainNameSuffix = internalDomainNameSuffix;
	}

	public String getInternalFqdn() {
		return internalFqdn;
	}

	public void setInternalFqdn(String internalFqdn) {
		this.internalFqdn = internalFqdn;
	}

	public boolean isAcceleratedNetworkingEnabled() {
		return isAcceleratedNetworkingEnabled;
	}

	public void setAcceleratedNetworkingEnabled(boolean isAcceleratedNetworkingEnabled) {
		this.isAcceleratedNetworkingEnabled = isAcceleratedNetworkingEnabled;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNetworkSecurityGroupId() {
		return networkSecurityGroupId;
	}

	public void setNetworkSecurityGroupId(String networkSecurityGroupId) {
		this.networkSecurityGroupId = networkSecurityGroupId;
	}

	public String getPrimaryPrivateIP() {
		return primaryPrivateIP;
	}

	public void setPrimaryPrivateIP(String primaryPrivateIP) {
		this.primaryPrivateIP = primaryPrivateIP;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVirtualMachineId() {
		return virtualMachineId;
	}

	public void setVirtualMachineId(String virtualMachineId) {
		this.virtualMachineId = virtualMachineId;
	}

	public List<NIIPConfigVH> getIpConfigurationList() {
		return ipConfigurationList;
	}

	public void setIpConfigurationList(List<NIIPConfigVH> ipConfigurationList) {
		this.ipConfigurationList = ipConfigurationList;
	}

	public boolean isIPForwardingEnabled() {
		return isIPForwardingEnabled;
	}

	public void setIPForwardingEnabled(boolean isIPForwardingEnabled) {
		this.isIPForwardingEnabled = isIPForwardingEnabled;
	}

}
