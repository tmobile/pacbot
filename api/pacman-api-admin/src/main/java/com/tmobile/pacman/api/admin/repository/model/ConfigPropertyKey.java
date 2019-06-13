package com.tmobile.pacman.api.admin.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Class ConfigPropertyKey.
 */
@Embeddable
public class ConfigPropertyKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cfkey. */
	@Column(name = "cfkey")
	protected String cfkey;

	/** The application. */
	@Column(name = "application")
	protected String application;

	/** The profile. */
	@Column(name = "profile")
	protected String profile;

	/** The label. */
	@Column(name = "label")
	protected String label;

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

	
}
