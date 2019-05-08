package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

public class AccountVH {


	/** The subsARN. */
	String securityTopicARN;

	/** The endpoint. */
	String securityTopicEndpoint;

	List<String> cloudTrailName;

	public List<String> getCloudTrailName() {
		return cloudTrailName;
	}

	public void setCloudTrailName(List<String> cloudTrailName) {
		this.cloudTrailName = cloudTrailName;
	}

	public String getSecurityTopicARN() {
		return securityTopicARN;
	}

	public void setSecurityTopicARN(String securityTopicARN) {
		this.securityTopicARN = securityTopicARN;
	}

	public String getSecurityTopicEndpoint() {
		return securityTopicEndpoint;
	}

	public void setSecurityTopicEndpoint(String securityTopicEndpoint) {
		this.securityTopicEndpoint = securityTopicEndpoint;
	}



}
