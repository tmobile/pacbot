/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.Tag;


/**
 * The Class ElastiCacheVH.
 */
public class ElastiCacheVH {

    /** The cluster. */
    CacheCluster cluster;
    
    /** The tags. */
    List<Tag> tags;
    
    /** The cluster name. */
    String clusterName;
    
    /** The arn. */
    String arn;
    
    /** The no of nodes. */
    int noOfNodes;
    
    /** The primary or config endpoint. */
    String primaryOrConfigEndpoint;
    
    /** The availability zones. */
    String availabilityZones;
    
    /** The description. */
    String description ;
    
    /** The security groups. */
    String securityGroups;
    
    /** The parameter group. */
    String parameterGroup;
    
    /** Subenets associated with the Cache Subnet Group **/
    
    List <String> subnets;
    /** vpc associaged with the cache subnet Group **/
    
    String vpc ;
    
    List<ElastiCacheNodeVH> nodes;
    
    List<String> nodeNames;
    
    /**
     * Gets the arn.
     *
     * @return the arn
     */
    public String getArn() {
        return arn;
    }
    
    /**
     * Sets the arn.
     *
     * @param arn the new arn
     */
    public void setArn(String arn) {
        this.arn = arn;
    }
    
    /**
     * Gets the parameter group.
     *
     * @return the parameter group
     */
    public String getParameterGroup() {
        return parameterGroup;
    }
    
    /**
     * Sets the parameter group.
     *
     * @param parameterGroup the new parameter group
     */
    public void setParameterGroup(String parameterGroup) {
        this.parameterGroup = parameterGroup;
    }
    
    /**
     * Gets the security groups.
     *
     * @return the security groups
     */
    public String getSecurityGroups() {
        return securityGroups;
    }
    
    /**
     * Sets the security groups.
     *
     * @param securityGroups the new security groups
     */
    public void setSecurityGroups(String securityGroups) {
        this.securityGroups = securityGroups;
    }
    
    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the cluster.
     *
     * @return the cluster
     */
    public CacheCluster getCluster() {
        return cluster;
    }
    
    /**
     * Sets the cluster.
     *
     * @param cluster the new cluster
     */
    public void setCluster(CacheCluster cluster) {
        this.cluster = cluster;
    }
    
    /**
     * Gets the cluster name.
     *
     * @return the cluster name
     */
    public String getClusterName() {
        return clusterName;
    }
    
    /**
     * Sets the cluster name.
     *
     * @param clusterName the new cluster name
     */
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
    
    /**
     * Gets the no of nodes.
     *
     * @return the no of nodes
     */
    public int getNoOfNodes() {
        return noOfNodes;
    }
    
    /**
     * Sets the no of nodes.
     *
     * @param noOfNodes the new no of nodes
     */
    public void setNoOfNodes(int noOfNodes) {
        this.noOfNodes = noOfNodes;
    }
    
    /**
     * Gets the primary or config endpoint.
     *
     * @return the primary or config endpoint
     */
    public String getPrimaryOrConfigEndpoint() {
        return primaryOrConfigEndpoint;
    }
    
    /**
     * Sets the primary or config endpoint.
     *
     * @param primaryOrConfigEndpoint the new primary or config endpoint
     */
    public void setPrimaryOrConfigEndpoint(String primaryOrConfigEndpoint) {
        this.primaryOrConfigEndpoint = primaryOrConfigEndpoint;
    }
    
    /**
     * Gets the availability zones.
     *
     * @return the availability zones
     */
    public String getAvailabilityZones() {
        return availabilityZones;
    }
    
    /**
     * Sets the availability zones.
     *
     * @param availabilityZones the new availability zones
     */
    public void setAvailabilityZones(String availabilityZones) {
        this.availabilityZones = availabilityZones;
    }
    
    /**
     * Gets the tags.
     *
     * @return the tags
     */
    public List<Tag> getTags() {
        return tags;
    }
    
    /**
     * Sets the tags.
     *
     * @param tags the new tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<String> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<String> subnets) {
        this.subnets = subnets;
    }

    public String getVpc() {
        return vpc;
    }

    public void setVpc(String vpc) {
        this.vpc = vpc;
    }
    
	public List<ElastiCacheNodeVH> getNodes() {
		return nodes;
	}

	public void setNodes(List<ElastiCacheNodeVH> nodes) {
		this.nodes = nodes;
	}

	public List<String> getNodeNames() {
		return nodeNames;
	}

	public void setNodeNames(List<String> nodeNames) {
		this.nodeNames = nodeNames;
	}
    
}
