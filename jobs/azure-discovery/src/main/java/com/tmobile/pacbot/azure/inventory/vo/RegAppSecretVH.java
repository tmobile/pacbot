package com.tmobile.pacbot.azure.inventory.vo;

public class RegAppSecretVH {
	private String customKeyIdentifier;
	private String endDateTime;
	private String keyId;
	private String startDateTime;
	private String secretText;
	private String hint;
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

	public String getSecretText() {
		return secretText;
	}

	public void setSecretText(String secretText) {
		this.secretText = secretText;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
