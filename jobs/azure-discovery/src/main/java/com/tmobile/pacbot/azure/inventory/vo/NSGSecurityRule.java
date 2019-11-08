package com.tmobile.pacbot.azure.inventory.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.microsoft.azure.management.network.NetworkSecurityRule;

public class NSGSecurityRule {

	private String description;
	private String access;
	private int priority;
	private String name;
	private String protocol;
	private List<String> destinationAddressPrefixes = new ArrayList<String>();;
	private Set<String> destinationApplicationSecurityGroupIds;
	private List<String> destinationPortRanges = new ArrayList<String>();;
	private List<String> sourceAddressPrefixes = new ArrayList<String>();
	private Set<String> sourceApplicationSecurityGroupIds;
	private List<String> sourcePortRanges = new ArrayList<String>();;
	private boolean isDefault;

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<String> getDestinationAddressPrefixes() {
		return destinationAddressPrefixes;
	}

	public void setDestinationAddressPrefixes(List<String> destinationAddressPrefixes) {
		this.destinationAddressPrefixes = destinationAddressPrefixes;
	}

	public Set<String> getDestinationApplicationSecurityGroupIds() {
		return destinationApplicationSecurityGroupIds;
	}

	public void setDestinationApplicationSecurityGroupIds(Set<String> destinationApplicationSecurityGroupIds) {
		this.destinationApplicationSecurityGroupIds = destinationApplicationSecurityGroupIds;
	}

	public List<String> getDestinationPortRanges() {
		return destinationPortRanges;
	}

	public void setDestinationPortRanges(List<String> destinationPortRanges) {
		this.destinationPortRanges = destinationPortRanges;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSourceAddressPrefixes() {
		return sourceAddressPrefixes;
	}

	public void setSourceAddressPrefixes(List<String> sourceAddressPrefixes) {
		this.sourceAddressPrefixes = sourceAddressPrefixes;
	}

	public Set<String> getSourceApplicationSecurityGroupIds() {
		return sourceApplicationSecurityGroupIds;
	}

	public void setSourceApplicationSecurityGroupIds(Set<String> sourceApplicationSecurityGroupIds) {
		this.sourceApplicationSecurityGroupIds = sourceApplicationSecurityGroupIds;
	}

	public List<String> getSourcePortRanges() {
		return sourcePortRanges;
	}

	public void setSourcePortRanges(List<String> sourcePortRanges) {
		this.sourcePortRanges = sourcePortRanges;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void listValue(NetworkSecurityRule networkSecurityRule) {
		sourceAddressPrefixes.addAll(networkSecurityRule.sourceAddressPrefixes());
		destinationAddressPrefixes.addAll(networkSecurityRule.destinationAddressPrefixes());
		sourcePortRanges.addAll(networkSecurityRule.sourcePortRanges());
		destinationPortRanges.addAll(networkSecurityRule.destinationPortRanges());
		if (networkSecurityRule.sourceAddressPrefix() != null && !networkSecurityRule.sourceAddressPrefix().isEmpty()) {
			sourceAddressPrefixes.add(networkSecurityRule.sourceAddressPrefix());
		}
		if (networkSecurityRule.destinationAddressPrefix() != null
				&& !networkSecurityRule.destinationAddressPrefix().isEmpty()) {
			destinationAddressPrefixes.add(networkSecurityRule.destinationAddressPrefix());
		}
		if (networkSecurityRule.sourcePortRange() != null && !networkSecurityRule.sourcePortRange().isEmpty()) {
			sourcePortRanges.add(networkSecurityRule.sourcePortRange());
		}
		if (networkSecurityRule.destinationPortRange() != null
				&& !networkSecurityRule.destinationPortRange().isEmpty()) {
			destinationPortRanges.add(networkSecurityRule.destinationPortRange());
		}
	}

}
