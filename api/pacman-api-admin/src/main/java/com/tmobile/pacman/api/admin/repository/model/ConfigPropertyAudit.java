package com.tmobile.pacman.api.admin.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The Class ConfigPropertyAudit.
 */
@Entity
@Table(name = "pac_config_audit", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class ConfigPropertyAudit {

	/** The id. */
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	/** The cfkey. */
	private String cfkey;
	
	/** The application. */
	private String application;
	
	/** The profile. */
	private String profile;
	
	/** The label. */
	private String label;
	
	/** The oldvalue. */
	private String oldvalue;
	
	/** The newvalue. */
	private String newvalue;
	
	/** The modified by. */
	private String modifiedBy;
	
	/** The modified date. */
	private String modifiedDate;
	
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the cfkey.
	 *
	 * @return the cfkey
	 */
	public String getCfkey() {
		return cfkey;
	}

	/**
	 * Sets the cfkey.
	 *
	 * @param cfkey the new cfkey
	 */
	public void setCfkey(String cfkey) {
		this.cfkey = cfkey;
	}

	/**
	 * Gets the application.
	 *
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 *
	 * @param application the new application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * Sets the profile.
	 *
	 * @param profile the new profile
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the oldvalue.
	 *
	 * @return the oldvalue
	 */
	public String getOldvalue() {
		return oldvalue;
	}

	/**
	 * Sets the oldvalue.
	 *
	 * @param oldvalue the new oldvalue
	 */
	public void setOldvalue(String oldvalue) {
		this.oldvalue = oldvalue;
	}

	/**
	 * Gets the newvalue.
	 *
	 * @return the newvalue
	 */
	public String getNewvalue() {
		return newvalue;
	}

	/**
	 * Sets the newvalue.
	 *
	 * @param newvalue the new newvalue
	 */
	public void setNewvalue(String newvalue) {
		this.newvalue = newvalue;
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
	 * Gets the modified date.
	 *
	 * @return the modified date
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the new modified date
	 */
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
