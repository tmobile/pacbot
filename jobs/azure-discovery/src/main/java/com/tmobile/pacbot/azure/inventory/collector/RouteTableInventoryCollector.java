package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Route;
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.Subnet;
import com.tmobile.pacbot.azure.inventory.vo.RouteTableSubnet;
import com.tmobile.pacbot.azure.inventory.vo.RouteTableVH;
import com.tmobile.pacbot.azure.inventory.vo.RouteVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class RouteTableInventoryCollector {

	public List<RouteTableVH> fetchRouteTableDetails(SubscriptionVH subscription,
			Map<String, Map<String, String>> tagMap) {
		List<RouteTableVH> routeTableDetailsList = new ArrayList<RouteTableVH>();

		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<RouteTable> routTableList = azure.routeTables().list();
		System.out.println(routTableList.size());
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
