package com.tmobile.pacbot.azure.inventory.vo;

public class PolicyDefinitionVH extends AzureVH {

	public String name;
	public String description;
	public String displayName;
	public String policyType;
	public String policyRule;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPolicyType() {
		return policyType;
	}

	public String getPolicyRule() {
		return policyRule;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public void setPolicyRule(String policyRule) {
		this.policyRule = policyRule;
	}

}
