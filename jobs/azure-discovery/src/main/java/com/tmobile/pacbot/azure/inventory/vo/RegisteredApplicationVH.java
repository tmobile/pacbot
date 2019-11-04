package com.tmobile.pacbot.azure.inventory.vo;

import java.util.List;

public class RegisteredApplicationVH extends AzureVH {
	private String objectId;
	private String appId;
	private String createdDateTime;
	private String displayName;
	private String publisherDomain;
	private List<RegAppCertificateVH> certificateList;
	private List<RegAppSecretVH> secretList;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPublisherDomain() {
		return publisherDomain;
	}

	public void setPublisherDomain(String publisherDomain) {
		this.publisherDomain = publisherDomain;
	}

	public List<RegAppCertificateVH> getCertificateList() {
		return certificateList;
	}

	public void setCertificateList(List<RegAppCertificateVH> certificateList) {
		this.certificateList = certificateList;
	}

	public List<RegAppSecretVH> getSecretList() {
		return secretList;
	}

	public void setSecretList(List<RegAppSecretVH> secretList) {
		this.secretList = secretList;
	}

}
