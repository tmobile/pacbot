package com.tmobile.pacman.api.admin.domain;

import java.util.List;


/**
 * The Class ConfigPropertyAuditTrail.
 */
public class ConfigPropertyAuditTrail {

	/** The config property audit. */
	private List<ConfigPropertyAuditItem> configPropertyAudit;

	/**
	 * Gets the config property audit.
	 *
	 * @return the config property audit
	 */
	public List<ConfigPropertyAuditItem> getConfigPropertyAudit() {
		return configPropertyAudit;
	}

	/**
	 * Sets the config property audit.
	 *
	 * @param configPropertyAudit the new config property audit
	 */
	public void setConfigPropertyAudit(List<ConfigPropertyAuditItem> configPropertyAudit) {
		this.configPropertyAudit = configPropertyAudit;
	}
	
}
