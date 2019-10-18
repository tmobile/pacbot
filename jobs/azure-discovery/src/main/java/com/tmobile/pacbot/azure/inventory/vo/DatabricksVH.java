package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class DatabricksVH extends AzureVH {


	private String name;
	private String type;
	private String location;
	private Map<String, Object> propertiesMap;
	private Map<String, Object> skuMap;


	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getLocation() {
		return location;
	}

	public Map<String, Object> getPropertiesMap() {
		return propertiesMap;
	}

	public Map<String, Object> getSkuMap() {
		return skuMap;
	}



	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setPropertiesMap(Map<String, Object> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

	public void setSkuMap(Map<String, Object> skuMap) {
		this.skuMap = skuMap;
	}

}
