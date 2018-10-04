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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :Nidhish
  Modified Date: Nov 22, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;

import javax.validation.constraints.NotNull;
/**
 * The Class KernelVersion.
 */
public class KernelVersion {

    /** The instance id. */
    @NotNull
    private String instanceId;

    /** The kernel version id. */
    @NotNull
    private String kernelVersionId;

    /**
     * Gets the kernel version id.
     *
     * @return the kernel version id
     */
    public String getKernelVersionId() {
        return kernelVersionId;
    }

    /**
     * Sets the kernel version id.
     *
     * @param kernelVersionId the new kernel version id
     */
    public void setKernelVersionId(String kernelVersionId) {
        this.kernelVersionId = kernelVersionId;
    }

    /**
     * Gets the instance id.
     *
     * @return the instance id
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Sets the instance id.
     *
     * @param instanceId the new instance id
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
