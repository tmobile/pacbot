package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Snapshot;
import com.tmobile.pacbot.azure.inventory.vo.SnapshotVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class SnapshotInventoryCollector {

	public List<SnapshotVH> fetchSnapshotDetails(SubscriptionVH subscription, Map<String, Map<String, String>> tagMap) {
		List<SnapshotVH> snapshotList = new ArrayList<SnapshotVH>();

		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<Snapshot> snapshots = azure.snapshots().list();
		System.out.println(snapshots.size());
		for (Snapshot snapshot : snapshots) {
			SnapshotVH snapshotVH = new SnapshotVH();
			snapshotVH.setId(snapshot.id());
			snapshotVH.setName(snapshot.name());
			snapshotVH.setResourceGroupName(snapshot.resourceGroupName());
			snapshotVH.setType(snapshot.type());
			snapshotVH.setTags(Util.tagsList(tagMap, snapshot.resourceGroupName(), snapshot.tags()));
			snapshotVH.setSubscription(subscription.getSubscriptionId());
			snapshotVH.setSubscriptionName(subscription.getSubscriptionName());
			snapshotVH.setKey(snapshot.key());
			snapshotVH.setRegionName(snapshot.regionName());
			snapshotVH.setSizeInGB(snapshot.sizeInGB());
			snapshotList.add(snapshotVH);

		}

		return snapshotList;
	}

}
