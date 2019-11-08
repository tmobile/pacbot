package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class WorkflowVH extends AzureVH{
	
	private String name;
	private String type;
	private String location;
	private Map<String, Object> tags;
	private Map<String, Object> properties;
	
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getLocation() {
		return location;
	}
	public Map<String, Object> getTags() {
		return tags;
	}
	public Map<String, Object> getProperties() {
		return properties;
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
	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	
}
