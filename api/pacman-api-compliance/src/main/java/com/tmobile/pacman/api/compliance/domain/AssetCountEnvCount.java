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
package com.tmobile.pacman.api.compliance.domain;

/**
 * The Class AssetCountEnvCount.
 */
public class AssetCountEnvCount {
    
    /** The environment. */
    private String environment;
    
    /** The count. */
    private String count;

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
     * Gets the count.
     *
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * Sets the count.
     *
     * @param count the new count
     */
    public void setCount(String count) {
        this.count = count;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClassPojo [count = " + count + ",environment = " + environment
                + "]";
    }

}
