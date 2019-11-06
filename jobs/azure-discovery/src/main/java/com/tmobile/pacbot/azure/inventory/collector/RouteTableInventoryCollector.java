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
import com.microsoft.azure.management.network.Route;
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.Subnet;
import com.tmobile.pacbot.azure.inventory.auth.AzureCredentialProvider;
import com.tmobile.pacbot.azure.inventory.vo.RouteTableSubnet;
import com.tmobile.pacbot.azure.inventory.vo.RouteTableVH;
import com.tmobile.pacbot.azure.inventory.vo.RouteVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;

@Component
public class RouteTableInventoryCollector {
	
	@Autowired
	AzureCredentialProvider azureCredentialProvider;
	
	private static Logger log = LoggerFactory.getLogger(RouteTableInventoryCollector.class);

	public List<RouteTableVH> fetchRouteTableDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {
		List<RouteTableVH> routeTableDetailsList = new ArrayList<RouteTableVH>();

		Azure azure = azureCredentialProvider.getClient(subscription.getTenant(),subscription.getSubscriptionId());
		PagedList<RouteTable> routTableList = azure.routeTables().list();
		for (RouteTable routTable : routTableList) {
			RouteTableVH routeTableVH = new RouteTableVH();
			routeTableVH.setHashCode(routTable.hashCode());
			routeTableVH.setId(routTable.id());
			routeTableVH.setKey(routTable.key());
			routeTableVH.setName(routTable.name());
			routeTableVH.setRegionName(routTable.regionName());
			routeTableVH.setResourceGroupName(routTable.resourceGroupName());
			routeTableVH.setTags(Util.tagsList(tagMap, routTable.resourceGroupName(), routTable.tags()));
			routeTableVH.setSubnetList(getNetworkSecuritySubnetDetails(routTable.listAssociatedSubnets()));
			routeTableVH.setType(routTable.type());
			getRouteDetails(routTable.routes(), routeTableVH);
			routeTableVH.setSubscription(subscription.getSubscriptionId());
			routeTableVH.setSubscriptionName(subscription.getSubscriptionName());
			routeTableDetailsList.add(routeTableVH);

		}
		log.info("Target Type : {}  Total: {} ","Route Table",routeTableDetailsList.size());
		return routeTableDetailsList;
	}

	private void getRouteDetails(Map<String, Route> routeDetails, RouteTableVH routeTableVH) {
		List<RouteVH> routeVHlist = new ArrayList<>();
		for (Map.Entry<String, Route> entry : routeDetails.entrySet()) {
			RouteVH routeVH = new RouteVH();
			routeVH.setAddressPrefix(entry.getValue().destinationAddressPrefix());
			routeVH.setName(entry.getValue().name());
			routeVH.setNextHop(entry.getValue().nextHopType().toString());
			routeVHlist.add(routeVH);
		}

		routeTableVH.setRouteVHlist(routeVHlist);

	}

	private List<RouteTableSubnet> getNetworkSecuritySubnetDetails(List<Subnet> subnetList) {
		List<RouteTableSubnet> subnetVHlist = new ArrayList<>();
		for (Subnet subnet : subnetList) {
			RouteTableSubnet routeTableSubnet = new RouteTableSubnet();
			routeTableSubnet.setAddressPrefix(subnet.addressPrefix());
			routeTableSubnet.setName(subnet.name());
			routeTableSubnet.setVnet(subnet.parent().id());
			subnetVHlist.add(routeTableSubnet);

		}
		return subnetVHlist;

	}

}
