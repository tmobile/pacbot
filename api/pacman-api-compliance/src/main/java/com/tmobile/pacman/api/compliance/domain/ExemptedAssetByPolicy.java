package com.tmobile.pacman.api.compliance.domain;

public class ExemptedAssetByPolicy {

	 private String message;
	 
	 private ExemptedAssetByPolicyData data;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ExemptedAssetByPolicyData getData() {
		return data;
	}

	public void setData(ExemptedAssetByPolicyData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ExemptedAssetByPolicy [message=" + message + ", data=" + data
				+ "]";
	}
}
