package com.tmobile.pacbot.azure.inventory.vo;

public class VMDiskVH {
	String storageAccountType;
	String name;
	Integer sizeInGB;
	String type;
	String cachingType;
	
	public String getType() {
		return type;
	}
	public String getCachingType() {
		return cachingType;
	}
	public void setCachingType(String cachingType) {
		this.cachingType = cachingType;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStorageAccountType() {
		return storageAccountType;
	}
	public void setStorageAccountType(String type) {
		this.storageAccountType = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSizeInGB() {
		return sizeInGB;
	}
	public void setSizeInGB(Integer sizeInGB) {
		this.sizeInGB = sizeInGB;
	}
	
}
