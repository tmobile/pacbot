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
package com.tmobile.pacman.api.compliance.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
/**
 * The Class RhnSystemDetails.
 */
@Entity
@Table(name = "cf_RhnSystemDetails", uniqueConstraints = @UniqueConstraint(columnNames = "instanceId"))
public class RhnSystemDetails {

    /** The rhn system details id. */
    private long rhnSystemDetailsId;
    
    /** The group id. */
    private Long groupId;
    
    /** The company id. */
    private Long companyId;
    
    /** The user id. */
    private Long userId;
    
    /** The user name. */
    private String userName;
    
    /** The create date. */
    private Date createDate;
    
    /** The modified date. */
    private Date modifiedDate;
    
    /** The system id. */
    private Integer systemId;
    
    /** The kernel version. */
    private String kernelVersion;
    
    /** The instance id. */
    private String instanceId;
    
    /** The last checked in. */
    private Date lastCheckedIn;
    
    /** The os. */
    private String os;
    
    /** The is kernel compliant. */
    private Boolean isKernelCompliant;

    /**
     * Instantiates a new rhn system details.
     */
    public RhnSystemDetails() {
    }

    /**
     * Instantiates a new rhn system details.
     *
     * @param rhnSystemDetailsId the rhn system details id
     * @param groupId the group id
     * @param companyId the company id
     * @param userId the user id
     * @param userName the user name
     * @param createDate the create date
     * @param modifiedDate the modified date
     * @param systemId the system id
     * @param kernelVersion the kernel version
     * @param instanceId the instance id
     * @param lastCheckedIn the last checked in
     * @param os the os
     * @param isKernelCompliant the is kernel compliant
     */
    public RhnSystemDetails(long rhnSystemDetailsId, Long groupId,
            Long companyId, Long userId, String userName, Date createDate,
            Date modifiedDate, Integer systemId, String kernelVersion,
            String instanceId, Date lastCheckedIn, String os,
            Boolean isKernelCompliant) {
        this.rhnSystemDetailsId = rhnSystemDetailsId;
        this.groupId = groupId;
        this.companyId = companyId;
        this.userId = userId;
        this.userName = userName;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.systemId = systemId;
        this.kernelVersion = kernelVersion;
        this.instanceId = instanceId;
        this.lastCheckedIn = lastCheckedIn;
        this.os = os;
        this.isKernelCompliant = isKernelCompliant;
    }

    /**
     * Gets the rhn system details id.
     *
     * @return the rhn system details id
     */
    @Id
    @GeneratedValue
    @Column(name = "rhnSystemDetailsId", unique = true, nullable = false)
    public long getRhnSystemDetailsId() {
        return this.rhnSystemDetailsId;
    }

    /**
     * Sets the rhn system details id.
     *
     * @param rhnSystemDetailsId the new rhn system details id
     */
    public void setRhnSystemDetailsId(long rhnSystemDetailsId) {
        this.rhnSystemDetailsId = rhnSystemDetailsId;
    }

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    @Column(name = "groupId")
    public Long getGroupId() {
        return this.groupId;
    }

    /**
     * Sets the group id.
     *
     * @param groupId the new group id
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the company id.
     *
     * @return the company id
     */
    @Column(name = "companyId")
    public Long getCompanyId() {
        return this.companyId;
    }

    /**
     * Sets the company id.
     *
     * @param companyId the new company id
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    @Column(name = "userId")
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    @Column(name = "userName", length = 75)
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName the new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the creates the date.
     *
     * @return the creates the date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createDate", length = 19)
    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * Sets the creates the date.
     *
     * @param createDate the new creates the date
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the modified date.
     *
     * @return the modified date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modifiedDate", length = 19)
    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    /**
     * Sets the modified date.
     *
     * @param modifiedDate the new modified date
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Gets the system id.
     *
     * @return the system id
     */
    @Column(name = "systemId")
    public Integer getSystemId() {
        return this.systemId;
    }

    /**
     * Sets the system id.
     *
     * @param systemId the new system id
     */
    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    /**
     * Gets the kernel version.
     *
     * @return the kernel version
     */
    @Column(name = "kernelVersion", length = 75)
    public String getKernelVersion() {
        return this.kernelVersion;
    }

    /**
     * Sets the kernel version.
     *
     * @param kernelVersion the new kernel version
     */
    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    /**
     * Gets the instance id.
     *
     * @return the instance id
     */
    @Column(name = "instanceId", unique = true, length = 75)
    public String getInstanceId() {
        return this.instanceId;
    }

    /**
     * Sets the instance id.
     *
     * @param instanceId the new instance id
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * Gets the last checked in.
     *
     * @return the last checked in
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastCheckedIn", length = 19)
    public Date getLastCheckedIn() {
        return this.lastCheckedIn;
    }

    /**
     * Sets the last checked in.
     *
     * @param lastCheckedIn the new last checked in
     */
    public void setLastCheckedIn(Date lastCheckedIn) {
        this.lastCheckedIn = lastCheckedIn;
    }

    /**
     * Gets the os.
     *
     * @return the os
     */
    @Column(name = "os", length = 75)
    public String getOs() {
        return this.os;
    }

    /**
     * Sets the os.
     *
     * @param os the new os
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Gets the checks if is kernel compliant.
     *
     * @return the checks if is kernel compliant
     */
    @Column(name = "isKernelCompliant")
    public Boolean getIsKernelCompliant() {
        return this.isKernelCompliant;
    }

    /**
     * Sets the checks if is kernel compliant.
     *
     * @param isKernelCompliant the new checks if is kernel compliant
     */
    public void setIsKernelCompliant(Boolean isKernelCompliant) {
        this.isKernelCompliant = isKernelCompliant;
    }
}
