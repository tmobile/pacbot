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


/**
 * The Class SGRuleVH.
 */
public class SGRuleVH {
	
	/** The group id. */
	private String groupId;
	
	/** The from port. */
	private String fromPort;
	
	/** The to port. */
	private String toPort;
	
	/** The ip protocol. */
	private String ipProtocol;
	
	/** The cidr ipv 6. */
	private String cidrIpv6;
	
	/** The cidr ip. */
	private String cidrIp;
	
	/** The type. */
	private String type;
	
	/**
	 * Instantiates a new SG rule VH.
	 *
	 * @param groupId the group id
	 * @param type the type
	 * @param fromPort the from port
	 * @param toPort the to port
	 * @param cidrIpv6 the cidr ipv 6
	 * @param cidrIp the cidr ip
	 * @param ipProtocol the ip protocol
	 */
	public SGRuleVH(String groupId,String type,String fromPort,String toPort,String cidrIpv6,String cidrIp,String ipProtocol){
		this.groupId = groupId;
		this.fromPort= fromPort;
		this.toPort = toPort;
		this.ipProtocol = ipProtocol;
		this.cidrIp = cidrIp;
		this.cidrIpv6 = cidrIpv6;
		this.setType(type);
	}
	
	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public String getGroupId() {
		return groupId;
	}
	
	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * Gets the from port.
	 *
	 * @return the from port
	 */
	public String getFromPort() {
		return fromPort;
	}
	
	/**
	 * Sets the from port.
	 *
	 * @param fromPort the new from port
	 */
	public void setFromPort(String fromPort) {
		this.fromPort = fromPort;
	}
	
	/**
	 * Gets the to port.
	 *
	 * @return the to port
	 */
	public String getToPort() {
		return toPort;
	}
	
	/**
	 * Sets the to port.
	 *
	 * @param toPort the new to port
	 */
	public void setToPort(String toPort) {
		this.toPort = toPort;
	}
	
	/**
	 * Gets the ip protocol.
	 *
	 * @return the ip protocol
	 */
	public String getIpProtocol() {
		return ipProtocol;
	}
	
	/**
	 * Sets the ip protocol.
	 *
	 * @param ipProtocol the new ip protocol
	 */
	public void setIpProtocol(String ipProtocol) {
		this.ipProtocol = ipProtocol;
	}
	
	/**
	 * Gets the cidr ipv 6.
	 *
	 * @return the cidr ipv 6
	 */
	public String getCidrIpv6() {
		return cidrIpv6;
	}
	
	/**
	 * Sets the cidr ipv 6.
	 *
	 * @param cidrIpv6 the new cidr ipv 6
	 */
	public void setCidrIpv6(String cidrIpv6) {
		this.cidrIpv6 = cidrIpv6;
	}
	
	/**
	 * Gets the cidr ip.
	 *
	 * @return the cidr ip
	 */
	public String getCidrIp() {
		return cidrIp;
	}
	
	/**
	 * Sets the cidr ip.
	 *
	 * @param cidrIp the new cidr ip
	 */
	public void setCidrIp(String cidrIp) {
		this.cidrIp = cidrIp;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	 
}
