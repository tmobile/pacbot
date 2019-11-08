package com.tmobile.pacbot.azure.inventory.vo;

public class RegAppCertificateVH {
	private String customKeyIdentifier;
	private String endDateTime;
	private String keyId;
	private String startDateTime;
	private String type;
	private String usage;
	private String key;
	private String displayName;

	public String getCustomKeyIdentifier() {
		return customKeyIdentifier;
	}

	public void setCustomKeyIdentifier(String customKeyIdentifier) {
		this.customKeyIdentifier = customKeyIdentifier;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
