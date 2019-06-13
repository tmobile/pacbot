package com.tmobile.pacman.api.admin.domain;

import java.util.List;

/**
 * The Class ConfigTreeNode.
 */
public class ConfigTreeNode {

	/** The name. */
	private String name;
	
	/** The properties. */
	private List<ConfigPropertyNode> properties;
	
	/** The children. */
	private List<ConfigTreeNode> children;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public List<ConfigPropertyNode> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the new properties
	 */
	public void setProperties(List<ConfigPropertyNode> properties) {
		this.properties = properties;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<ConfigTreeNode> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(List<ConfigTreeNode> children) {
		this.children = children;
	}

}
