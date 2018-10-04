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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The Class SystemConfiguration.
 */
@Entity
@Table(name = "cf_SystemConfiguration", uniqueConstraints = @UniqueConstraint(columnNames = {
        "environment", "keyname" }))
@IdClass(SystemConfigurationIdentity.class)
public class SystemConfiguration {

    /** The environment. */
    @Id
    private String environment;

    /** The keyname. */
    @Id
    private String keyname;

    /** The id. */
    @Column(name = "id_")
    private Integer id_;

    /** The value. */
    @Column(name = "value", length = 2000)
    private String value;

    /**
     * Instantiates a new system configuration.
     */
    public SystemConfiguration() {
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId_() {
        return id_;
    }

    /**
     * Sets the id.
     *
     * @param id_ the new id
     */
    public void setId_(Integer id_) {
        this.id_ = id_;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the environment.
     *
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment.
     *
     * @param environment the new environment
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * Gets the keyname.
     *
     * @return the keyname
     */
    public String getKeyname() {
        return keyname;
    }

    /**
     * Sets the keyname.
     *
     * @param keyname the new keyname
     */
    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }
}
