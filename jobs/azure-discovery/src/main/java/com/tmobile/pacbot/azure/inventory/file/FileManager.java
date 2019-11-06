/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.pacbot.azure.inventory.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tmobile.pacbot.azure.inventory.vo.BatchAccountVH;
import com.tmobile.pacbot.azure.inventory.vo.BlobContainerVH;
import com.tmobile.pacbot.azure.inventory.vo.CosmosDBVH;
import com.tmobile.pacbot.azure.inventory.vo.DataDiskVH;
import com.tmobile.pacbot.azure.inventory.vo.DatabricksVH;
import com.tmobile.pacbot.azure.inventory.vo.LoadBalancerVH;
import com.tmobile.pacbot.azure.inventory.vo.MariaDBVH;
import com.tmobile.pacbot.azure.inventory.vo.MySQLServerVH;
import com.tmobile.pacbot.azure.inventory.vo.NamespaceVH;
import com.tmobile.pacbot.azure.inventory.vo.NetworkInterfaceVH;
import com.tmobile.pacbot.azure.inventory.vo.NetworkVH;
import com.tmobile.pacbot.azure.inventory.vo.PolicyDefinitionVH;
import com.tmobile.pacbot.azure.inventory.vo.PolicyStatesVH;
import com.tmobile.pacbot.azure.inventory.vo.PostgreSQLServerVH;
import com.tmobile.pacbot.azure.inventory.vo.PublicIpAddressVH;
import com.tmobile.pacbot.azure.inventory.vo.RecommendationVH;
import com.tmobile.pacbot.azure.inventory.vo.RegisteredApplicationVH;
import com.tmobile.pacbot.azure.inventory.vo.ResourceGroupVH;
import com.tmobile.pacbot.azure.inventory.vo.RouteTableVH;
import com.tmobile.pacbot.azure.inventory.vo.SQLDatabaseVH;
import com.tmobile.pacbot.azure.inventory.vo.SQLServerVH;
import com.tmobile.pacbot.azure.inventory.vo.SearchServiceVH;
import com.tmobile.pacbot.azure.inventory.vo.SecurityAlertsVH;
import com.tmobile.pacbot.azure.inventory.vo.SecurityGroupVH;
import com.tmobile.pacbot.azure.inventory.vo.SitesVH;
import com.tmobile.pacbot.azure.inventory.vo.SnapshotVH;
import com.tmobile.pacbot.azure.inventory.vo.StorageAccountVH;
import com.tmobile.pacbot.azure.inventory.vo.SubnetVH;
import com.tmobile.pacbot.azure.inventory.vo.VaultVH;
import com.tmobile.pacbot.azure.inventory.vo.VirtualMachineVH;
import com.tmobile.pacbot.azure.inventory.vo.WorkflowVH;;

/**
 * The Class FileManager.
 */
public class FileManager {

	/**
	 * Instantiates a new file manager.
	 */
	private FileManager() {

	}

	/**
	 * Initialise.
	 *
	 * @param folderName
	 *            the folder name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void initialise(String folderName) throws IOException {
		FileGenerator.folderName = folderName;
		new File(folderName).mkdirs();

		FileGenerator.writeToFile("azure-virtualmachine.data", "[", false);
		FileGenerator.writeToFile("azure-storageaccount.data", "[", false);
		FileGenerator.writeToFile("azure-sqldatabase.data", "[", false);
		FileGenerator.writeToFile("azure-nsg.data", "[", false);
		FileGenerator.writeToFile("azure-disk.data", "[", false);
		FileGenerator.writeToFile("azure-networkinterface.data", "[", false);
		FileGenerator.writeToFile("azure-vnet.data", "[", false);
		FileGenerator.writeToFile("azure-loadbalancer.data", "[", false);
		FileGenerator.writeToFile("azure-securitycenter.data", "[", false);
		FileGenerator.writeToFile("azure-sqlserver.data", "[", false);
		FileGenerator.writeToFile("azure-blobcontainer.data", "[", false);
		FileGenerator.writeToFile("azure-resourcegroup.data", "[", false);
		FileGenerator.writeToFile("azure-cosmosdb.data", "[", false);
		FileGenerator.writeToFile("azure-mysqlserver.data", "[", false);
		FileGenerator.writeToFile("azure-databricks.data", "[", false);
		FileGenerator.writeToFile("azure-mariadb.data", "[", false);
		FileGenerator.writeToFile("azure-postgresql.data", "[", false);
		FileGenerator.writeToFile("azure-registeredApplication.data", "[", false);
		FileGenerator.writeToFile("azure-snapshot.data", "[", false);
		FileGenerator.writeToFile("azure-publicipaddress.data", "[", false);
		FileGenerator.writeToFile("azure-routetable.data", "[", false);
		FileGenerator.writeToFile("azure-securityalerts.data", "[", false);
		FileGenerator.writeToFile("azure-policyevaluationresults.data", "[", false);
		FileGenerator.writeToFile("azure-policydefinitions.data", "[", false);
		FileGenerator.writeToFile("azure-sites.data", "[", false);
		FileGenerator.writeToFile("azure-vaults.data", "[", false);
		FileGenerator.writeToFile("azure-workflows.data", "[", false);
		FileGenerator.writeToFile("azure-batchaccounts.data", "[", false);
		FileGenerator.writeToFile("azure-namespaces.data", "[", false);
		FileGenerator.writeToFile("azure-searchservices.data", "[", false);
		FileGenerator.writeToFile("azure-subnets.data", "[", false);
	}

	public static void finalise() throws IOException {

		FileGenerator.writeToFile("azure-virtualmachine.data", "]", true);
		FileGenerator.writeToFile("azure-storageaccount.data", "]", true);
		FileGenerator.writeToFile("azure-sqldatabase.data", "]", true);
		FileGenerator.writeToFile("azure-nsg.data", "]", true);
		FileGenerator.writeToFile("azure-disk.data", "]", true);
		FileGenerator.writeToFile("azure-networkinterface.data", "]", true);
		FileGenerator.writeToFile("azure-vnet.data", "]", true);
		FileGenerator.writeToFile("azure-securitycenter.data", "]", true);
		FileGenerator.writeToFile("azure-loadbalancer.data", "]", true);
		FileGenerator.writeToFile("azure-sqlserver.data", "]", true);
		FileGenerator.writeToFile("azure-blobcontainer.data", "]", true);
		FileGenerator.writeToFile("azure-resourcegroup.data", "]", true);
		FileGenerator.writeToFile("azure-cosmosdb.data", "]", true);
		FileGenerator.writeToFile("azure-mysqlserver.data", "]", true);
		FileGenerator.writeToFile("azure-databricks.data", "]", true);
		FileGenerator.writeToFile("azure-mariadb.data", "]", true);
		FileGenerator.writeToFile("azure-postgresql.data", "]", true);
		FileGenerator.writeToFile("azure-registeredApplication.data", "]", true);
		FileGenerator.writeToFile("azure-snapshot.data", "]", true);
		FileGenerator.writeToFile("azure-publicipaddress.data", "]", true);
		FileGenerator.writeToFile("azure-routetable.data", "]", true);
		FileGenerator.writeToFile("azure-securityalerts.data", "]", true);
		FileGenerator.writeToFile("azure-policyevaluationresults.data", "]", true);
		FileGenerator.writeToFile("azure-policydefinitions.data", "]", true);
		FileGenerator.writeToFile("azure-sites.data", "]", true);
		FileGenerator.writeToFile("azure-vaults.data", "]", true);
		FileGenerator.writeToFile("azure-workflows.data", "]", true);
		FileGenerator.writeToFile("azure-batchaccounts.data", "]", true);
		FileGenerator.writeToFile("azure-namespaces.data", "]", true);
		FileGenerator.writeToFile("azure-searchservices.data", "]", true);
		FileGenerator.writeToFile("azure-subnets.data", "]", true);
		

	}

	public static void generateVMFiles(List<VirtualMachineVH> vmMap) throws IOException {

		FileGenerator.generateJson(vmMap, "azure-virtualmachine.data");

	}

	public static void generateStorageAccountFiles(List<StorageAccountVH> storageAccountMap) throws IOException {

		FileGenerator.generateJson(storageAccountMap, "azure-storageaccount.data");

	}

	public static void generateSQLdatabaseFiles(List<SQLDatabaseVH> sqlDatabaseMap) throws IOException {

		FileGenerator.generateJson(sqlDatabaseMap, "azure-sqldatabase.data");

	}

	public static void generateNetworkSecurityFiles(List<SecurityGroupVH> securityGroupMap) throws IOException {

		FileGenerator.generateJson(securityGroupMap, "azure-nsg.data");

	}

	public static void generateDataDiskFiles(List<DataDiskVH> dataDiskMap) throws IOException {

		FileGenerator.generateJson(dataDiskMap, "azure-disk.data");

	}

	public static void generateNetworkInterfaceFiles(List<NetworkInterfaceVH> networkInterfaceMap) throws IOException {

		FileGenerator.generateJson(networkInterfaceMap, "azure-networkinterface.data");

	}

	public static void generateNetworkFiles(List<NetworkVH> networkMap) throws IOException {

		FileGenerator.generateJson(networkMap, "azure-vnet.data");

	}

	public static void generateLoadBalancerFiles(List<LoadBalancerVH> loadBalancerMap) throws IOException {

		FileGenerator.generateJson(loadBalancerMap, "azure-loadbalancer.data");

	}

	public static void generateSecurityCenterFiles(List<RecommendationVH> recommendations) throws IOException {

		FileGenerator.generateJson(recommendations, "azure-securitycenter.data");

	}

	public static void generateSQLServerFiles(List<SQLServerVH> sqlServerList) throws IOException {
		FileGenerator.generateJson(sqlServerList, "azure-sqlserver.data");
	}

	public static void generateBlobContainerFiles(List<BlobContainerVH> blobDetailsList) throws IOException {
		FileGenerator.generateJson(blobDetailsList, "azure-blobcontainer.data");
	}

	public static void generateResourceGroupFiles(List<ResourceGroupVH> resourceGroupList) throws IOException {
		FileGenerator.generateJson(resourceGroupList, "azure-resourcegroup.data");
	}

	public static void generateCosmosDBFiles(List<CosmosDBVH> cosmosDBList) throws IOException {
		FileGenerator.generateJson(cosmosDBList, "azure-cosmosdb.data");
	}

	public static void generateRegisteredApplicationFiles(List<RegisteredApplicationVH> registeredApplicationVHList)
			throws IOException {
		FileGenerator.generateJson(registeredApplicationVHList, "azure-registeredApplication.data");
	}

	public static void generateMySqlServerFiles(List<MySQLServerVH> mySqlServerList) throws IOException {
		FileGenerator.generateJson(mySqlServerList, "azure-mysqlserver.data");
	}

	public static void generateDatabricksFiles(List<DatabricksVH> databricksList) throws IOException {
		FileGenerator.generateJson(databricksList, "azure-databricks.data");
	}

	public static void generateMariaDBFiles(List<MariaDBVH> mariaDBList) throws IOException {
		FileGenerator.generateJson(mariaDBList, "azure-mariadb.data");
	}

	public static void generatePostgreSQLServerFiles(List<PostgreSQLServerVH> postgreSQLServerList) throws IOException {
		FileGenerator.generateJson(postgreSQLServerList, "azure-postgresql.data");
	}

	public static void generateSnapshotFiles(List<SnapshotVH> snapshotList) throws IOException {
		FileGenerator.generateJson(snapshotList, "azure-snapshot.data");
	}

	public static void generatePublicIpAddressFiles(List<PublicIpAddressVH> publicIpAddressList) throws IOException {
		FileGenerator.generateJson(publicIpAddressList, "azure-publicipaddress.data");
	}

	public static void generateRouteTableFiles(List<RouteTableVH> routeTableDetailsList) throws IOException {
		FileGenerator.generateJson(routeTableDetailsList, "azure-routetable.data");
	}

	public static void generateSecurityAlertsFiles(List<SecurityAlertsVH> securityAlertsList) throws IOException {
		FileGenerator.generateJson(securityAlertsList, "azure-securityalerts.data");
	}

	public static void generatePolicyStatesFiles(List<PolicyStatesVH> policyStatesList) throws IOException {
		FileGenerator.generateJson(policyStatesList, "azure-policyevaluationresults.data");
	}

	public static void generatePolicyDefinitionFiles(List<PolicyDefinitionVH> policyDefinitionList) throws IOException {
		FileGenerator.generateJson(policyDefinitionList, "azure-policydefinitions.data");
	}

	public static void generateSiteFiles(List<SitesVH> sitesList) throws IOException {
		FileGenerator.generateJson(sitesList, "azure-sites.data");
	}

	public static void generateVaultFiles(List<VaultVH> vaultList) throws IOException {
		FileGenerator.generateJson(vaultList, "azure-vaults.data");

	}

	public static void generateWorkflowFiles(List<WorkflowVH> workflowList) throws IOException {
		FileGenerator.generateJson(workflowList, "azure-workflows.data");

	}

	public static void generateBatchAccountFiles(List<BatchAccountVH> batchAccountList) throws IOException {
		FileGenerator.generateJson(batchAccountList, "azure-batchaccounts.data");

	}
	
	public static void generateNamespaceFiles(List<NamespaceVH> namespaceList) throws IOException {
		FileGenerator.generateJson(namespaceList, "azure-namespaces.data");

	}
	
	public static void generateSearchServiceFiles(List<SearchServiceVH> searchServiceList) throws IOException {
		FileGenerator.generateJson(searchServiceList, "azure-searchservices.data");

	}
	
	public static void generateSubnetFiles(List<SubnetVH> subnetList) throws IOException {
		FileGenerator.generateJson(subnetList, "azure-subnets.data");

	}
	
}
