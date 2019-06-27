package com.tmobile.pacman.api.admin.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ConfigPropertyRollbackPreview.
 */
public class ConfigPropertyRollbackPreview {

	/** The restore to timestamp. */
	private String restoreToTimestamp;

	/** The rollback change set. */
	private List<ConfigPropertyRollBackItem> rollbackChangeSet = new ArrayList<>();

	/**
	 * Gets the restore to timestamp.
	 *
	 * @return the restore to timestamp
	 */
	public String getRestoreToTimestamp() {
		return restoreToTimestamp;
	}

	/**
	 * Sets the restore to timestamp.
	 *
	 * @param restoreToTimestamp the new restore to timestamp
	 */
	public void setRestoreToTimestamp(String restoreToTimestamp) {
		this.restoreToTimestamp = restoreToTimestamp;
	}

	/**
	 * Gets the rollback change set.
	 *
	 * @return the rollback change set
	 */
	public List<ConfigPropertyRollBackItem> getRollbackChangeSet() {
		return rollbackChangeSet;
	}

	/**
	 * Sets the rollback change set.
	 *
	 * @param rollbackChangeSet the new rollback change set
	 */
	public void setRollbackChangeSet(List<ConfigPropertyRollBackItem> rollbackChangeSet) {
		this.rollbackChangeSet = rollbackChangeSet;
	}

}
