package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class SearchServiceVH extends AzureVH {

	private String name;
	private String type;
	private String location;
	private Map<String, Object> properties;
	private Map<String, Object> sku;


	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getLocation() {
		return location;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public Map<String, Object> getSku() {
		return sku;
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

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public void setSku(Map<String, Object> sku) {
		this.sku = sku;
	}

}
