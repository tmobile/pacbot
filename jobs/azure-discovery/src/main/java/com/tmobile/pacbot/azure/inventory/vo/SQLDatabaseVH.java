package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class SQLDatabaseVH extends AzureVH {
	private String creationDate;
	private UUID currentServiceObjectiveId;
	private String collation;
	private String databaseId;
	private String defaultSecondaryLocation;
	private String earliestRestoreDate;
	private String edition;
	private String elasticPoolName;
	private boolean isDataWarehouse;
	private String name;
	private String status;
	private String serverName;
	private List<Map<String, String>> firewallRuleDetails;
	private Map<String, String> tags;

	public UUID getCurrentServiceObjectiveId() {
		return currentServiceObjectiveId;
	}

	public void setCurrentServiceObjectiveId(UUID currentServiceObjectiveId) {
		this.currentServiceObjectiveId = currentServiceObjectiveId;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getDefaultSecondaryLocation() {
		return defaultSecondaryLocation;
	}

	public void setDefaultSecondaryLocation(String defaultSecondaryLocation) {
		this.defaultSecondaryLocation = defaultSecondaryLocation;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getElasticPoolName() {
		return elasticPoolName;
	}

	public void setElasticPoolName(String elasticPoolName) {
		this.elasticPoolName = elasticPoolName;
	}

	public boolean isDataWarehouse() {
		return isDataWarehouse;
	}

	public void setDataWarehouse(boolean isDataWarehouse) {
		this.isDataWarehouse = isDataWarehouse;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getEarliestRestoreDate() {
		return earliestRestoreDate;
	}

	public void setEarliestRestoreDate(String earliestRestoreDate) {
		this.earliestRestoreDate = earliestRestoreDate;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public List<Map<String, String>> getFirewallRuleDetails() {
		return firewallRuleDetails;
	}

	public void setFirewallRuleDetails(List<Map<String, String>> firewallRuleDetails) {
		this.firewallRuleDetails = firewallRuleDetails;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

}
