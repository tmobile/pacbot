package com.tmobile.pacman.api.compliance.domain;

import java.util.List;
import java.util.Map;

public class ExemptedAssetByPolicyData {

	private String totalExempted;
	
	private List<Map<String,Object>> exempted;

	public String getTotalExempted() {
		return totalExempted;
	}

	public void setTotalExempted(String totalExempted) {
		this.totalExempted = totalExempted;
	}

	public List<Map<String, Object>> getExempted() {
		return exempted;
	}

	public void setExempted(List<Map<String, Object>> exempted) {
		this.exempted = exempted;
	}

	@Override
	public String toString() {
		return "ExemptedAssetByPolicyData [totalExempted=" + totalExempted
				+ ", exempted=" + exempted + "]";
	}
}
