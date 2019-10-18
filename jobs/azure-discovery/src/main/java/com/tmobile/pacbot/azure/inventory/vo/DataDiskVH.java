package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.azure.management.compute.implementation.DiskInner;

@JsonSerialize
public class DataDiskVH extends AzureVH {

	private Boolean isAttachedToVirtualMachine;
	private String key;
	private String name;
	private DiskInner diskInner;
	private int sizeInGB;
	private String type;
	private String virtualMachineId;
	private Map<String, String> tags;

	public Boolean getIsAttachedToVirtualMachine() {
		return isAttachedToVirtualMachine;
	}

	public void setIsAttachedToVirtualMachine(Boolean isAttachedToVirtualMachine) {
		this.isAttachedToVirtualMachine = isAttachedToVirtualMachine;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DiskInner getDiskInner() {
		return diskInner;
	}

	public void setDiskInner(DiskInner diskInner) {
		this.diskInner = diskInner;
	}

	public int getSizeInGB() {
		return sizeInGB;
	}

	public void setSizeInGB(int sizeInGB) {
		this.sizeInGB = sizeInGB;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVirtualMachineId() {
		return virtualMachineId;
	}

	public void setVirtualMachineId(String virtualMachineId) {
		this.virtualMachineId = virtualMachineId;
	}

}
