package com.tmobile.pacbot.azure.inventory.collector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.sql.SqlDatabase;
import com.microsoft.azure.management.sql.SqlFirewallRule;
import com.microsoft.azure.management.sql.SqlServer;
import com.microsoft.azure.management.sql.SqlVirtualNetworkRule;
import com.tmobile.pacbot.azure.inventory.vo.FirewallRules;
import com.tmobile.pacbot.azure.inventory.vo.SQLDatabaseVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacbot.azure.inventory.vo.VirtualNetworkRuleVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class SQLDatabaseInventoryCollector {

	public List<SQLDatabaseVH> fetchSQLDatabaseDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {

		List<SQLDatabaseVH> sqlDatabaseList = new ArrayList<SQLDatabaseVH>();

		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<SqlServer> sqlServers = azure.sqlServers().list();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		for (SqlServer sqlServer : sqlServers) {
			List<SqlDatabase> sqlDatabases = azure.sqlServers().databases().listBySqlServer(sqlServer);
			for (SqlDatabase sqlDatabase : sqlDatabases) {
				if (!sqlDatabase.name().contentEquals("master")) {
					SQLDatabaseVH sqlDatabaseVH = new SQLDatabaseVH();
					if (sqlDatabase.creationDate() != null) {

						sqlDatabaseVH.setCreationDate(sdf.format(sqlDatabase.creationDate().toDate()));
					}
					sqlDatabaseVH.setCurrentServiceObjectiveId(sqlDatabase.currentServiceObjectiveId());
					sqlDatabaseVH.setCollation(sqlDatabase.collation());
					sqlDatabaseVH.setDatabaseId(sqlDatabase.databaseId());
					sqlDatabaseVH.setDefaultSecondaryLocation(sqlDatabase.defaultSecondaryLocation());
					if (sqlDatabase.earliestRestoreDate() != null) {

						sqlDatabaseVH.setEarliestRestoreDate(sdf.format(sqlDatabase.earliestRestoreDate().toDate()));
					}
					sqlDatabaseVH.setEdition(sqlDatabase.edition().toString());
					sqlDatabaseVH.setElasticPoolName(sqlDatabase.elasticPoolName());
					sqlDatabaseVH.setId(sqlDatabase.id());
					sqlDatabaseVH.setDataWarehouse(sqlDatabase.isDataWarehouse());
					sqlDatabaseVH.setName(sqlDatabase.name());
					sqlDatabaseVH.setStatus(sqlDatabase.status());
					sqlDatabaseVH.setSubscription(subscription.getSubscriptionId());
					sqlDatabaseVH.setSubscriptionName(subscription.getSubscriptionName());
					sqlDatabaseVH.setServerName(sqlDatabase.sqlServerName());
					sqlDatabaseVH.setResourceGroupName(sqlDatabase.resourceGroupName());

					for (Map.Entry<String, Map<String, String>> resourceGroupTag : tagMap.entrySet()) {

						if (resourceGroupTag.getKey().equalsIgnoreCase(sqlDatabase.resourceGroupName())) {
							sqlDatabaseVH.setTags(resourceGroupTag.getValue());
							break;
						}

					}

					firewallRule(sqlServer, sqlDatabaseVH);
					// sqlDatabaseVH.setFirewallRuleDetails(getFirewallRuleDetails(sqlServer.firewallRules().list()));
					// sqlDatabaseVH.setVirtualNetworkRuleDetails(
					// getVirtualNetworkRuleDetails(sqlServer.virtualNetworkRules().list()));
					sqlDatabaseList.add(sqlDatabaseVH);
				}

			}

		}
		return sqlDatabaseList;

	}

	/*
	 * private List<FirewallRules> getFirewallRuleDetails(List<SqlFirewallRule>
	 * sqlFirewallRuleList) { List<FirewallRules> firewallRulesList = new
	 * ArrayList<>(); for (SqlFirewallRule sqlFirewallRule : sqlFirewallRuleList) {
	 * FirewallRules firewallRuleVH = new FirewallRules();
	 * firewallRuleVH.setName(sqlFirewallRule.name());
	 * firewallRuleVH.setStartIPAddress(sqlFirewallRule.startIPAddress());
	 * firewallRuleVH.setEndIPAddress(sqlFirewallRule.endIPAddress());
	 * firewallRulesList.add(firewallRuleVH); } return firewallRulesList;
	 * 
	 * }
	 * 
	 * private List<VirtualNetworkRule> getVirtualNetworkRuleDetails(
	 * List<SqlVirtualNetworkRule> sqlVirtualNetworkRuleList) {
	 * List<VirtualNetworkRule> virtualNetworkRuleList = new ArrayList<>(); for
	 * (SqlVirtualNetworkRule sqlVirtualNetworkRule : sqlVirtualNetworkRuleList) {
	 * VirtualNetworkRule virtualNetworkRuleVH = new VirtualNetworkRule();
	 * virtualNetworkRuleVH.setName(sqlVirtualNetworkRule.name());
	 * virtualNetworkRuleVH.setSubnetId(sqlVirtualNetworkRule.subnetId());
	 * virtualNetworkRuleVH.setResourceGroupName(sqlVirtualNetworkRule.
	 * resourceGroupName());
	 * virtualNetworkRuleVH.setState(sqlVirtualNetworkRule.state());
	 * virtualNetworkRuleList.add(virtualNetworkRuleVH); } return
	 * virtualNetworkRuleList;
	 * 
	 * }
	 */

	private void firewallRule(SqlServer sqlServer, SQLDatabaseVH sqlDatabaseVH) {
		List<Map<String, String>> firewallRuleList = new ArrayList<>();
		Map<String, String> firewallMap;
		for (SqlFirewallRule sqlFirewallRule : sqlServer.firewallRules().list()) {
			firewallMap = new HashMap<>();
			firewallMap.put("name", sqlFirewallRule.name());
			firewallMap.put("startIPAddress", sqlFirewallRule.startIPAddress());
			firewallMap.put("endIPAddress", sqlFirewallRule.endIPAddress());
			firewallRuleList.add(firewallMap);

		}
		for (SqlVirtualNetworkRule sqlVirtualNetworkRule : sqlServer.virtualNetworkRules().list()) {
			firewallMap = new HashMap<>();

			firewallMap.put("virtualNetworkRuleName",
					sqlVirtualNetworkRule.name() != null ? sqlVirtualNetworkRule.name() : "");
			firewallMap.put("virtualNetworkSubnetId",
					sqlVirtualNetworkRule.subnetId() != null ? sqlVirtualNetworkRule.subnetId() : "");
			firewallMap.put("virtualNetworkResourceGroupName",
					sqlVirtualNetworkRule.resourceGroupName() != null ? sqlVirtualNetworkRule.resourceGroupName() : "");
			firewallMap.put("virtualNetworkState",
					sqlVirtualNetworkRule.state() != null ? sqlVirtualNetworkRule.state() : "");

			firewallRuleList.add(firewallMap);
		}
		sqlDatabaseVH.setFirewallRuleDetails(firewallRuleList);
	}
}
