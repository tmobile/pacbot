package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class SitesVH extends AzureVH  {

	private String etag;
	private String location;
	private String name;
	private String type;
	private Map<String, Object> tags;
	private Map<String, Object> properties;
	
	public String getEtag() {
		return etag;
	}
	public String getLocation() {
		return location;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}

	
	public void setEtag(String etag) {
		this.etag = etag;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	public Map<String, Object> getTags() {
		return tags;
	}
	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}
	

}
