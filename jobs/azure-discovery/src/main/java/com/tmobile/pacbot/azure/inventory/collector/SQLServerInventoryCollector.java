package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.sql.SqlElasticPool;
import com.microsoft.azure.management.sql.SqlFailoverGroup;
import com.microsoft.azure.management.sql.SqlFirewallRule;
import com.microsoft.azure.management.sql.SqlServer;
import com.microsoft.azure.management.sql.SqlVirtualNetworkRule;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.ElasticPoolVH;
import com.tmobile.pacbot.azure.inventory.vo.FailoverGroupVH;
import com.tmobile.pacbot.azure.inventory.vo.SQLServerVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class SQLServerInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(SQLServerInventoryCollector.class);
	
	public List<SQLServerVH> fetchSQLServerDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {

		List<SQLServerVH> sqlServerList = new ArrayList<>();
		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<SqlServer> sqlServers = azure.sqlServers().list();
		for (SqlServer sqlServer : sqlServers) {
			SQLServerVH sqlServerVH = new SQLServerVH();
			sqlServerVH.setSubscription(subscription.getSubscriptionId());
			sqlServerVH.setSubscriptionName(subscription.getSubscriptionName());
			sqlServerVH.setId(sqlServer.id());
			sqlServerVH.setKind(sqlServer.kind());
			sqlServerVH.setName(sqlServer.name());
			sqlServerVH.setRegionName(sqlServer.regionName());
			sqlServerVH.setState(sqlServer.state());
			sqlServerVH.setSystemAssignedManagedServiceIdentityPrincipalId(
					sqlServer.systemAssignedManagedServiceIdentityPrincipalId());
			sqlServerVH.setSystemAssignedManagedServiceIdentityTenantId(
					sqlServer.systemAssignedManagedServiceIdentityTenantId());
			sqlServerVH.setTags(Util.tagsList(tagMap, sqlServer.resourceGroupName(), sqlServer.tags()));
			sqlServerVH.setVersion(sqlServer.version());
			sqlServerVH.setAdministratorLogin(sqlServer.administratorLogin());
			firewallRule(sqlServer, sqlServerVH);
			getElasticPoolList(sqlServer.elasticPools().list(), sqlServerVH);
			getFailoverGroupList(sqlServer.failoverGroups().list(), sqlServerVH);
			sqlServerList.add(sqlServerVH);
		}
		log.info("Target Type : {}  Total: {} ","SqlServer",sqlServerList.size());
		return sqlServerList;

	}

	private void getElasticPoolList(List<SqlElasticPool> sqlElasticPoolList, SQLServerVH sqlServerVH) {
		List<ElasticPoolVH> elasticPoolList = new ArrayList<>();
		for (SqlElasticPool sqlElasticPool : sqlElasticPoolList) {
			ElasticPoolVH elasticPoolVH = new ElasticPoolVH();
			elasticPoolVH.setName(sqlElasticPool.name());
			elasticPoolVH.setSize(sqlElasticPool.listDatabases().size());
			elasticPoolVH.setStorageCapacity(sqlElasticPool.storageCapacityInMB());
			elasticPoolVH.setId(sqlElasticPool.id());
			elasticPoolVH.setStorageMB(sqlElasticPool.storageMB());
			elasticPoolVH.setDtu(sqlElasticPool.dtu());
			elasticPoolVH.setEdition(sqlElasticPool.edition().toString());
			elasticPoolList.add(elasticPoolVH);

		}
		sqlServerVH.setElasticPoolList(elasticPoolList);

	}

	private void firewallRule(SqlServer sqlServer, SQLServerVH sqlServerVH) {
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
		sqlServerVH.setFirewallRuleDetails(firewallRuleList);
	}

	private void getFailoverGroupList(List<SqlFailoverGroup> sqlFailoverGroupList, SQLServerVH sqlServerVH) {
		List<FailoverGroupVH> failoverGroupList = new ArrayList<>();
		for (SqlFailoverGroup sqlFailoverGroup : sqlFailoverGroupList) {
			FailoverGroupVH failoverGroupVH = new FailoverGroupVH();
			failoverGroupVH.setSize(sqlFailoverGroup.databases().size());
			failoverGroupVH.setId(sqlFailoverGroup.id());
			failoverGroupVH.setName(sqlFailoverGroup.name());
			failoverGroupVH.setReplicationState(sqlFailoverGroup.replicationState());
			failoverGroupVH.setReadOnlyEndpointPolicy(sqlFailoverGroup.readOnlyEndpointPolicy().toString());
			failoverGroupVH.setReadWriteEndpointPolicy(sqlFailoverGroup.readWriteEndpointPolicy().toString());
			failoverGroupVH.setGracePeriod(sqlFailoverGroup.readWriteEndpointDataLossGracePeriodMinutes());
			failoverGroupList.add(failoverGroupVH);

		}
		sqlServerVH.setFailoverGroupList(failoverGroupList);

	}

}
