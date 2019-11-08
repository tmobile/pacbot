package com.tmobile.pacbot.azure.inventory.vo;

public class PolicyStatesVH extends AzureVH {
	private String timestamp;
	private String resourceId;
	private String resourceIdLower;
	private String policyAssignmentId;
	private String policyDefinitionId;
	private String effectiveParameters;
	private Boolean isCompliant;
	private String subscriptionId;
	private String resourceType;
	private String resourceLocation;
	private String resourceGroup;
	private String resourceTags;
	private String policyAssignmentName;
	private String policyAssignmentOwner;
	private String policyAssignmentParameters;
	private String policyAssignmentScope;
	private String policyDefinitionName;
	private String policyDefinitionAction;
	private String policyDefinitionCategory;
	private String policySetDefinitionId;
	private String policySetDefinitionName;
	private String policySetDefinitionOwner;
	private String policySetDefinitionCategory;
	private String policySetDefinitionParameters;
	private String managementGroupIds;
	private String policyDefinitionReferenceId;
	private String policyDescription;
	private String policyName;
	private String policyType;
	private String policyRule;

	public String getTimestamp() {
		return timestamp;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getPolicyAssignmentId() {
		return policyAssignmentId;
	}

	public String getPolicyDefinitionId() {
		return policyDefinitionId;
	}

	public String getEffectiveParameters() {
		return effectiveParameters;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public String getResourceType() {
		return resourceType;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public String getResourceGroup() {
		return resourceGroup;
	}

	public String getResourceTags() {
		return resourceTags;
	}

	public String getPolicyAssignmentName() {
		return policyAssignmentName;
	}

	public String getPolicyAssignmentOwner() {
		return policyAssignmentOwner;
	}

	public String getPolicyAssignmentParameters() {
		return policyAssignmentParameters;
	}

	public String getPolicyAssignmentScope() {
		return policyAssignmentScope;
	}

	public String getPolicyDefinitionName() {
		return policyDefinitionName;
	}

	public String getPolicyDefinitionAction() {
		return policyDefinitionAction;
	}

	public String getPolicyDefinitionCategory() {
		return policyDefinitionCategory;
	}

	public String getPolicySetDefinitionId() {
		return policySetDefinitionId;
	}

	public String getPolicySetDefinitionName() {
		return policySetDefinitionName;
	}

	public String getPolicySetDefinitionOwner() {
		return policySetDefinitionOwner;
	}

	public String getPolicySetDefinitionCategory() {
		return policySetDefinitionCategory;
	}

	public String getPolicySetDefinitionParameters() {
		return policySetDefinitionParameters;
	}

	public String getManagementGroupIds() {
		return managementGroupIds;
	}

	public String getPolicyDefinitionReferenceId() {
		return policyDefinitionReferenceId;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public void setPolicyAssignmentId(String policyAssignmentId) {
		this.policyAssignmentId = policyAssignmentId;
	}

	public void setPolicyDefinitionId(String policyDefinitionId) {
		this.policyDefinitionId = policyDefinitionId;
	}

	public void setEffectiveParameters(String effectiveParameters) {
		this.effectiveParameters = effectiveParameters;
	}

	public Boolean getIsCompliant() {
		return isCompliant;
	}

	public void setIsCompliant(Boolean isCompliant) {
		this.isCompliant = isCompliant;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public void setResourceGroup(String resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public void setResourceTags(String resourceTags) {
		this.resourceTags = resourceTags;
	}

	public void setPolicyAssignmentName(String policyAssignmentName) {
		this.policyAssignmentName = policyAssignmentName;
	}

	public void setPolicyAssignmentOwner(String policyAssignmentOwner) {
		this.policyAssignmentOwner = policyAssignmentOwner;
	}

	public void setPolicyAssignmentParameters(String policyAssignmentParameters) {
		this.policyAssignmentParameters = policyAssignmentParameters;
	}

	public void setPolicyAssignmentScope(String policyAssignmentScope) {
		this.policyAssignmentScope = policyAssignmentScope;
	}

	public void setPolicyDefinitionName(String policyDefinitionName) {
		this.policyDefinitionName = policyDefinitionName;
	}

	public void setPolicyDefinitionAction(String policyDefinitionAction) {
		this.policyDefinitionAction = policyDefinitionAction;
	}

	public void setPolicyDefinitionCategory(String policyDefinitionCategory) {
		this.policyDefinitionCategory = policyDefinitionCategory;
	}

	public void setPolicySetDefinitionId(String policySetDefinitionId) {
		this.policySetDefinitionId = policySetDefinitionId;
	}

	public void setPolicySetDefinitionName(String policySetDefinitionName) {
		this.policySetDefinitionName = policySetDefinitionName;
	}

	public void setPolicySetDefinitionOwner(String policySetDefinitionOwner) {
		this.policySetDefinitionOwner = policySetDefinitionOwner;
	}

	public void setPolicySetDefinitionCategory(String policySetDefinitionCategory) {
		this.policySetDefinitionCategory = policySetDefinitionCategory;
	}

	public void setPolicySetDefinitionParameters(String policySetDefinitionParameters) {
		this.policySetDefinitionParameters = policySetDefinitionParameters;
	}

	public void setManagementGroupIds(String managementGroupIds) {
		this.managementGroupIds = managementGroupIds;
	}

	public void setPolicyDefinitionReferenceId(String policyDefinitionReferenceId) {
		this.policyDefinitionReferenceId = policyDefinitionReferenceId;
	}

	public String getPolicyType() {
		return policyType;
	}

	public String getPolicyRule() {
		return policyRule;
	}

	public String getPolicyDescription() {
		return policyDescription;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyDescription(String policyDescription) {
		this.policyDescription = policyDescription;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public void setPolicyRule(String policyRule) {
		this.policyRule = policyRule;
	}

	public String getResourceIdLower() {
		return resourceIdLower;
	}

	public void setResourceIdLower(String resourceIdLower) {
		this.resourceIdLower = resourceIdLower;
	}

}
