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

import java.util.Objects;

import javax.persistence.Column;
/**
 * The Class SystemConfigurationIdentity.
 */
public class SystemConfigurationIdentity implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -809121547140958731L;

    /** The environment. */
    @Column(name = "environment", nullable = false, length = 75)
    private String environment;

    /** The keyname. */
    @Column(name = "keyname", nullable = false, length = 75)
    private String keyname;

    /**
     * Instantiates a new system configuration identity.
     */
    public SystemConfigurationIdentity() {
    }

    /**
     * Instantiates a new system configuration identity.
     *
     * @param environment the environment
     * @param keyname the keyname
     */
    public SystemConfigurationIdentity(String environment, String keyname) {
        this.environment = environment;
        this.keyname = keyname;
    }

    /**
     * Gets the environment.
     *
     * @return the environment
     */
    public String getEnvironment() {
        return this.environment;
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
        return this.keyname;
    }

    /**
     * Sets the keyname.
     *
     * @param keyname the new keyname
     */
    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(getEnvironment(), getKeyname());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SystemConfigurationIdentity)) {
            return false;
        }
        SystemConfigurationIdentity that = (SystemConfigurationIdentity) obj;
        return Objects.equals(getEnvironment(), that.getEnvironment())
                && Objects.equals(getKeyname(), that.getKeyname());
    }
}
