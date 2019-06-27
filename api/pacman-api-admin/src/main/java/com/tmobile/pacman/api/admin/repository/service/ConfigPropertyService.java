package com.tmobile.pacman.api.admin.repository.service;

import java.util.List;

import com.tmobile.pacman.api.admin.domain.ConfigPropertyAuditTrail;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyItem;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRollbackPreview;
import com.tmobile.pacman.api.admin.domain.ConfigTreeNode;
import com.tmobile.pacman.api.admin.repository.model.ConfigProperty;

/**
 * The Interface ConfigPropertyService.
 */
public interface ConfigPropertyService {

	/**
	 * List properties.
	 *
	 * @return the config tree node
	 * @throws Exception the exception
	 */
	public ConfigTreeNode listProperties() throws Exception;

	/**
	 * Checks if is property existing.
	 *
	 * @param configPropertyItem the config property item
	 * @return true, if is property existing
	 * @throws Exception the exception
	 */
	public boolean isPropertyExisting(ConfigPropertyItem configPropertyItem) throws Exception;

	/**
	 * Checks if is cfkey existing.
	 *
	 * @param cfkey the cfkey
	 * @return true, if is cfkey existing
	 * @throws Exception the exception
	 */
	public boolean isCfkeyExisting(String cfkey) throws Exception;

	/**
	 * Checks if is application existing.
	 *
	 * @param application the application
	 * @return true, if is application existing
	 * @throws Exception the exception
	 */
	public boolean isApplicationExisting(String application) throws Exception;

	/**
	 * List all keys.
	 *
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<String> listAllKeys() throws Exception;

	/**
	 * Checks if is all properties existing.
	 *
	 * @param configPropertyRequest the config property request
	 * @return true, if is all properties existing
	 * @throws Exception the exception
	 */
	boolean isAllPropertiesExisting(ConfigPropertyRequest configPropertyRequest) throws Exception;

	/**
	 * Checks if is all applications existing.
	 *
	 * @param configPropertyRequest the config property request
	 * @return true, if is all applications existing
	 * @throws Exception the exception
	 */
	boolean isAllApplicationsExisting(ConfigPropertyRequest configPropertyRequest) throws Exception;

	/**
	 * Checks if is all cfkeys existing.
	 *
	 * @param configPropertyRequest the config property request
	 * @return true, if is all cfkeys existing
	 * @throws Exception the exception
	 */
	boolean isAllCfkeysExisting(ConfigPropertyRequest configPropertyRequest) throws Exception;

	/**
	 * Checks if is any property existing.
	 *
	 * @param configPropertyRequest the config property request
	 * @return true, if is any property existing
	 * @throws Exception the exception
	 */
	public boolean isAnyPropertyExisting(ConfigPropertyRequest configPropertyRequest) throws Exception;

	/**
	 * Gets the rollback preview.
	 *
	 * @param timestamp the timestamp
	 * @return the rollback preview
	 * @throws Exception the exception
	 */
	public ConfigPropertyRollbackPreview getRollbackPreview(String timestamp) throws Exception;

	/**
	 * Do config property rollback to timestamp.
	 *
	 * @param timestamp the timestamp
	 * @param user the user
	 * @param userMessage the user message
	 * @return the string
	 * @throws Exception the exception
	 */
	public String doConfigPropertyRollbackToTimestamp(String timestamp, String user, String userMessage)
			throws Exception;

	/**
	 * List all config property audits.
	 *
	 * @param timestamp the timestamp
	 * @return the config property audit trail
	 * @throws Exception the exception
	 */
	public ConfigPropertyAuditTrail listAllConfigPropertyAudits(String timestamp) throws Exception;



	/**
	 * Adds the update properties.
	 *
	 * @param configPropertyRequest the config property request
	 * @param user the user
	 * @param userMessage the user message
	 * @param timeNow the time now
	 * @param partOfRollbackOperation the part of rollback operation
	 * @return the list
	 */
	List<ConfigProperty> addUpdateProperties(ConfigPropertyRequest configPropertyRequest, String user,
			String userMessage, String timeNow, boolean partOfRollbackOperation);

	/**
	 * Delete property.
	 *
	 * @param configPropertyItem the config property item
	 * @param user the user
	 * @param userMessage the user message
	 * @param timeNow the time now
	 * @param partOfRollbackOperation the part of rollback operation
	 * @return the string
	 */
	String deleteProperty(ConfigPropertyItem configPropertyItem, String user, String userMessage, String timeNow,
			boolean partOfRollbackOperation);

    /**
     * List property.
     *
     * @param cfkey the cfkey
     * @param application the application
     * @return the list
     */
    public List<ConfigProperty> listProperty(String cfkey, String application);

}
