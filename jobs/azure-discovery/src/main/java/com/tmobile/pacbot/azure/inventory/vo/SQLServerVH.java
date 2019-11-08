package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class SQLServerVH extends AzureVH {

	private String kind;
	private String name;
	private String regionName;
	private String state;
	private String systemAssignedManagedServiceIdentityPrincipalId;
	private String systemAssignedManagedServiceIdentityTenantId;
	private Map<String, String> tags;
	private String version;
	private String administratorLogin;
	List<ElasticPoolVH> elasticPoolList;
	List<FailoverGroupVH> failoverGroupList;
	private List<Map<String, String>> firewallRuleDetails;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSystemAssignedManagedServiceIdentityPrincipalId() {
		return systemAssignedManagedServiceIdentityPrincipalId;
	}

	public void setSystemAssignedManagedServiceIdentityPrincipalId(
			String systemAssignedManagedServiceIdentityPrincipalId) {
		this.systemAssignedManagedServiceIdentityPrincipalId = systemAssignedManagedServiceIdentityPrincipalId;
	}

	public String getSystemAssignedManagedServiceIdentityTenantId() {
		return systemAssignedManagedServiceIdentityTenantId;
	}

	public void setSystemAssignedManagedServiceIdentityTenantId(String systemAssignedManagedServiceIdentityTenantId) {
		this.systemAssignedManagedServiceIdentityTenantId = systemAssignedManagedServiceIdentityTenantId;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAdministratorLogin() {
		return administratorLogin;
	}

	public void setAdministratorLogin(String administratorLogin) {
		this.administratorLogin = administratorLogin;
	}

	public List<Map<String, String>> getFirewallRuleDetails() {
		return firewallRuleDetails;
	}

	public void setFirewallRuleDetails(List<Map<String, String>> firewallRuleDetails) {
		this.firewallRuleDetails = firewallRuleDetails;
	}

	public List<ElasticPoolVH> getElasticPoolList() {
		return elasticPoolList;
	}

	public void setElasticPoolList(List<ElasticPoolVH> elasticPoolList) {
		this.elasticPoolList = elasticPoolList;
	}

	public List<FailoverGroupVH> getFailoverGroupList() {
		return failoverGroupList;
	}

	public void setFailoverGroupList(List<FailoverGroupVH> failoverGroupList) {
		this.failoverGroupList = failoverGroupList;
	}

}
