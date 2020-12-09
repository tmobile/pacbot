package com.tmobile.cloud.azurerules.publicaccess;

public class FirewallRuleDTO {
	String startIP;
	String endIP;
	
	public FirewallRuleDTO(String startIP, String endIP) {
		this.startIP = startIP;
		this.endIP = endIP; 
	}

	public String getStartIP() {
		return startIP;
	}

	public void setStartIP(String startIP) {
		this.startIP = startIP;
	}

	public String getEndIP() {
		return endIP;
	}

	public void setEndIP(String endIP) {
		this.endIP = endIP;
	}

}