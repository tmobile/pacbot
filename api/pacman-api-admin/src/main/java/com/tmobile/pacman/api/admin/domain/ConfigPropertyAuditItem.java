package com.tmobile.pacman.api.admin.domain;

import java.util.List;

/**
 * The Class ConfigPropertyAuditItem.
 */
public class ConfigPropertyAuditItem {

	/** The audit time stamp. */
	private String auditTimeStamp;

	/** The config property change list. */
	List<ConfigPropertyDataChange> configPropertyChangeList;

	/** The modified by. */
	private String modifiedBy;
	
	/** The user message. */
	private String userMessage;
	
	/** The system message. */
	private String systemMessage;



	/**
	 * Gets the user message.
	 *
	 * @return the user message
	 */
	public String getUserMessage() {
		return userMessage;
	}

	/**
	 * Sets the user message.
	 *
	 * @param userMessage the new user message
	 */
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	/**
	 * Gets the system message.
	 *
	 * @return the system message
	 */
	public String getSystemMessage() {
		return systemMessage;
	}

	/**
	 * Sets the system message.
	 *
	 * @param systemMessage the new system message
	 */
	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	/**
	 * Gets the modified by.
	 *
	 * @return the modified by
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Sets the modified by.
	 *
	 * @param modifiedBy the new modified by
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * Gets the config property change list.
	 *
	 * @return the config property change list
	 */
	public List<ConfigPropertyDataChange> getConfigPropertyChangeList() {
		return configPropertyChangeList;
	}

	/**
	 * Sets the config property change list.
	 *
	 * @param configPropertyChangeList the new config property change list
	 */
	public void setConfigPropertyChangeList(List<ConfigPropertyDataChange> configPropertyChangeList) {
		this.configPropertyChangeList = configPropertyChangeList;
	}

	/**
	 * Gets the audit time stamp.
	 *
	 * @return the audit time stamp
	 */
	public String getAuditTimeStamp() {
		return auditTimeStamp;
	}

	/**
	 * Sets the audit time stamp.
	 *
	 * @param auditTimeStamp the new audit time stamp
	 */
	public void setAuditTimeStamp(String auditTimeStamp) {
		this.auditTimeStamp = auditTimeStamp;
	}

	
	
}
