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

package com.tmobile.pacman.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceOwner.
 *
 * @author kkumar
 */
public class ResourceOwner {

    /** The name. */
    String name;

    /** The email id. */
    String emailId;

    /**
     * Instantiates a new resource owner.
     *
     * @param name the name
     * @param emailId the email id
     */
    public ResourceOwner(String name, String emailId) {
        super();
        this.name = name;
        this.emailId = emailId;
    }

    /**
     * Instantiates a new resource owner.
     */
    public ResourceOwner() {
        // TODO Auto-generated constructor stub
    }

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
     * Gets the email id.
     *
     * @return the email id
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets the email id.
     *
     * @param emailId the new email id
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return name + " ---> " + emailId;
    }

}
