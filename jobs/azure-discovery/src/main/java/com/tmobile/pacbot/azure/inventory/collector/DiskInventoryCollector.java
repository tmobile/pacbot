package com.tmobile.pacbot.azure.inventory.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.tmobile.pacbot.azure.inventory.vo.DataDiskVH;
import com.tmobile.pacbot.azure.inventory.vo.SubscriptionVH;
import com.tmobile.pacman.commons.azure.clients.AzureCredentialManager;

@Component
public class DiskInventoryCollector {
	public List<DataDiskVH> fetchDataDiskDetails(SubscriptionVH subscription, Map<String, Map<String, String>> tagMap) {
		List<DataDiskVH> dataDiskList = new ArrayList<DataDiskVH>();
		Azure azure = AzureCredentialManager.authenticate(subscription.getSubscriptionId());
		PagedList<Disk> dataDisks = azure.disks().list();
		System.out.println(dataDisks.size());
		for (Disk dataDisk : dataDisks) {
			DataDiskVH dataDiskVH = new DataDiskVH();
			dataDiskVH.setId(dataDisk.id());
			dataDiskVH.setIsAttachedToVirtualMachine(dataDisk.isAttachedToVirtualMachine());
			dataDiskVH.setKey(dataDisk.key());
			dataDiskVH.setName(dataDisk.name());
			dataDiskVH.setDiskInner(dataDisk.inner());
			dataDiskVH.setRegion(dataDisk.region().toString());
			dataDiskVH.setResourceGroupName(dataDisk.resourceGroupName());
			dataDiskVH.setSizeInGB(dataDisk.sizeInGB());
			dataDiskVH.setTags(Util.tagsList(tagMap, dataDisk.resourceGroupName(), dataDisk.tags()));
			dataDiskVH.setType(dataDisk.type());
			dataDiskVH.setVirtualMachineId(dataDisk.virtualMachineId());
			dataDiskVH.setSubscription(subscription.getSubscriptionId());
			dataDiskVH.setSubscriptionName(subscription.getSubscriptionName());
			dataDiskList.add(dataDiskVH);
		}
		return dataDiskList;
	}
}
