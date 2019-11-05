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
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.NetworkSecurityRule;
import com.microsoft.azure.management.network.Subnet;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.NSGSecurityRule;
import com.tmobile.pacbot.azure.inventory.vo.NSGSubnet;
import com.tmobile.pacbot.azure.inventory.vo.SecurityGroupVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class NSGInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(NSGInventoryCollector.class);
	
	public List<SecurityGroupVH> fetchNetworkSecurityGroupDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {
		List<SecurityGroupVH> securityGroupsList = new ArrayList<>();

		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<NetworkSecurityGroup> securityGroups = azure.networkSecurityGroups().list();
		for (NetworkSecurityGroup securityGroup : securityGroups) {
			SecurityGroupVH securityGroupVH = new SecurityGroupVH();
			securityGroupVH.setId(securityGroup.id());
			securityGroupVH.setKey(securityGroup.key());
			securityGroupVH.setName(securityGroup.name());
			securityGroupVH.setRegion(securityGroup.regionName());
			securityGroupVH.setResourceGroupName(securityGroup.resourceGroupName());
			securityGroupVH.setTags(Util.tagsList(tagMap, securityGroup.resourceGroupName(), securityGroup.tags()));
			securityGroupVH.setSubnetList(getNetworkSecuritySubnetDetails(securityGroup.listAssociatedSubnets()));
			securityGroupVH.setNetworkInterfaceIds(securityGroup.networkInterfaceIds());
			securityGroupVH.setSubscription(subscription.getSubscriptionId());
			securityGroupVH.setSubscriptionName(subscription.getSubscriptionName());
			setSecurityRules(securityGroup, securityGroupVH);
			securityGroupsList.add(securityGroupVH);

		}
		log.info("Target Type : {}  Total: {} ","Nsg",securityGroupsList.size());
		return securityGroupsList;
	}

	private void setSecurityRules(NetworkSecurityGroup securityGroup, SecurityGroupVH securityGroupVH) {
		List<NSGSecurityRule> inBoundSecurityList = new ArrayList<NSGSecurityRule>();
		List<NSGSecurityRule> outBoundSecurityList = new ArrayList<NSGSecurityRule>();

		for (Map.Entry<String, NetworkSecurityRule> entry : securityGroup.securityRules().entrySet()) {
			populateRuleInfo(inBoundSecurityList, outBoundSecurityList, entry.getValue(), false);
		}
		for (Map.Entry<String, NetworkSecurityRule> entry : securityGroup.defaultSecurityRules().entrySet()) {
			populateRuleInfo(inBoundSecurityList, outBoundSecurityList, entry.getValue(), true);
		}
		securityGroupVH.setOutBoundSecurityRules(outBoundSecurityList);
		securityGroupVH.setInBoundSecurityRules(inBoundSecurityList);

	}

	private void populateRuleInfo(List<NSGSecurityRule> inBoundSecurityList, List<NSGSecurityRule> outBoundSecurityList,
			NetworkSecurityRule securityRule, boolean isDefault) {
		NSGSecurityRule securityListVH = new NSGSecurityRule();
		securityListVH.setName(securityRule.name());
		securityListVH.setDescription(securityRule.description());
		securityListVH.setAccess(securityRule.access().toString());
		securityListVH.setPriority(securityRule.priority());
		securityListVH.setProtocol(securityRule.protocol().toString());
		securityListVH.listValue(securityRule);
		securityListVH.setDestinationApplicationSecurityGroupIds(securityRule.destinationApplicationSecurityGroupIds());
		securityListVH.setSourceApplicationSecurityGroupIds(securityRule.sourceApplicationSecurityGroupIds());
		securityListVH.setDefault(isDefault);
		if (securityRule.direction().toString().equals("Inbound")) {
			inBoundSecurityList.add(securityListVH);
		} else if (securityRule.direction().toString().equals("Outbound")) {
			outBoundSecurityList.add(securityListVH);
		}
	}

	private List<NSGSubnet> getNetworkSecuritySubnetDetails(List<Subnet> subnetList) {
		List<NSGSubnet> subnetVHlist = new ArrayList<>();
		for (Subnet subnet : subnetList) {
			NSGSubnet subnetVH = new NSGSubnet();
			subnetVH.setAddressPrefix(subnet.addressPrefix());
			subnetVH.setName(subnet.name());
			subnetVH.setVnet(subnet.parent().id());
			subnetVHlist.add(subnetVH);

		}
		return subnetVHlist;

	}
}
