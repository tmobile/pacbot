package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NicIPConfiguration;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.NIIPConfigVH;
import com.tmobile.pacbot.azure.inventory.vo.NetworkInterfaceVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class NetworkInterfaceInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(NetworkInterfaceInventoryCollector.class);
	
	public List<NetworkInterfaceVH> fetchNetworkInterfaceDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {
		List<NetworkInterfaceVH> networkInterfaceList = new ArrayList<NetworkInterfaceVH>();

		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<NetworkInterface> networkInterfaces = azure.networkInterfaces().list();

		for (NetworkInterface networkInterface : networkInterfaces) {
			NetworkInterfaceVH networkInterfaceVH = new NetworkInterfaceVH();
			networkInterfaceVH.setAppliedDnsServers(networkInterface.appliedDnsServers());
			networkInterfaceVH.setDnsServers(networkInterface.dnsServers());
			networkInterfaceVH.setId(networkInterface.id());
			networkInterfaceVH.setInternalDnsNameLabel(networkInterface.internalDnsNameLabel());
			networkInterfaceVH.setInternalDomainNameSuffix(networkInterface.internalDomainNameSuffix());
			networkInterfaceVH.setInternalFqdn(networkInterface.internalFqdn());
			networkInterfaceVH.setAcceleratedNetworkingEnabled(networkInterface.isAcceleratedNetworkingEnabled());
			networkInterfaceVH.setKey(networkInterface.key());
			networkInterfaceVH.setMacAddress(networkInterface.macAddress());
			networkInterfaceVH.setName(networkInterface.name());
			networkInterfaceVH.setNetworkSecurityGroupId(networkInterface.networkSecurityGroupId());
			networkInterfaceVH.setPrimaryPrivateIP(networkInterface.primaryPrivateIP());
			networkInterfaceVH
					.setTags(Util.tagsList(tagMap, networkInterface.resourceGroupName(), networkInterface.tags()));
			networkInterfaceVH.setVirtualMachineId(networkInterface.virtualMachineId());
			networkInterfaceVH.setSubscription(subscription.getSubscriptionId());
			networkInterfaceVH.setSubscriptionName(subscription.getSubscriptionName());
			networkInterfaceVH.setIPForwardingEnabled(networkInterface.isIPForwardingEnabled());
			setipConfigurations(networkInterface.ipConfigurations(), networkInterfaceVH);
			networkInterfaceList.add(networkInterfaceVH);

		}
		log.info("Target Type : {}  Total: {} ","Networkinterface",networkInterfaceList.size());
		return networkInterfaceList;
	}

	private void setipConfigurations(Map<String, NicIPConfiguration> ipConfigurations,
			NetworkInterfaceVH networkInterfaceVH) {
		List<NIIPConfigVH> ipConfigurationList = new ArrayList<>();
		for (Map.Entry<String, NicIPConfiguration> entry : ipConfigurations.entrySet()) {
			NIIPConfigVH niipConfigVH = new NIIPConfigVH();
			niipConfigVH.setName(entry.getValue().name());
			niipConfigVH.setPrivateIPAddress(entry.getValue().privateIPAddress());
			niipConfigVH.setPrivateIPAddressVersion(entry.getValue().privateIPAddressVersion() != null
					? entry.getValue().privateIPAddressVersion().toString()
					: "");
			niipConfigVH.setNetworkName(entry.getValue().getNetwork().name());
			niipConfigVH.setSubnetName(entry.getValue().subnetName());
			niipConfigVH.setPrimary(entry.getValue().isPrimary());
			niipConfigVH.setPublicIPAddress(
					entry.getValue().getPublicIPAddress() != null ? entry.getValue().getPublicIPAddress().ipAddress()
							: "");
			ipConfigurationList.add(niipConfigVH);
		}
		networkInterfaceVH.setIpConfigurationList(ipConfigurationList);
	}
}
