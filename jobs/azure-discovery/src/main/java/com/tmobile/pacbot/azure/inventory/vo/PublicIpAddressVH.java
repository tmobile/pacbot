package com.tmobile.pacbot.azure.inventory.vo;

import java.util.Map;

public class PublicIpAddressVH extends AzureVH {
	private String name;
	private String fqdn;
	private String reverseFqdn;
	private String ipAddress;
	private String key;
	private String regionName;
	private String version;
	private String type;
	private String kind;
	private int idleTimeoutInMinutes;
	private Map<String, String> tags;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getKind() {
		return kind;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getFqdn() {
		return fqdn;
	}

	public String getReverseFqdn() {
		return reverseFqdn;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getKey() {
		return key;
	}

	public String getRegionName() {
		return regionName;
	}

	public String getVersion() {
		return version;
	}

	public int getIdleTimeoutInMinutes() {
		return idleTimeoutInMinutes;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public void setReverseFqdn(String reverseFqdn) {
		this.reverseFqdn = reverseFqdn;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setIdleTimeoutInMinutes(int idleTimeoutInMinutes) {
		this.idleTimeoutInMinutes = idleTimeoutInMinutes;
	}

}
