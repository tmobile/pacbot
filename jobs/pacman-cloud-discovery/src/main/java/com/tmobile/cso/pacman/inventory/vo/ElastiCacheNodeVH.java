package com.tmobile.cso.pacman.inventory.vo;

import com.amazonaws.services.elasticache.model.CacheNode;

public class ElastiCacheNodeVH {

	/** The node. */
    CacheNode node;
    
    /** The tags. */
    String tags;
    
    
    String nodeName ;

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public CacheNode getNode() {
		return node;
	}

	public void setNode(CacheNode node) {
		this.node = node;
	}

	

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
}
