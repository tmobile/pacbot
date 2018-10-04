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

package com.tmobile.pacman.commons.autofix;

import java.util.Map;

import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;

// TODO: Auto-generated Javadoc
/**
 * The Class FixResult.
 */
public class FixResult {

    /**  operation status*. */
    Integer status;

    /** The annotation. */
    Annotation annotation;

    /** The desc. */
    String desc;

    /** The resource. */
    private Map<String, String> resource;

    /**
     * Gets the resource.
     *
     * @return the resource
     */
    public Map<String, String> getResource() {
        return resource;
    }

    /**
     * Sets the resource.
     *
     * @param resource the resource
     */
    public void setResource(Map<String, String> resource) {
        this.resource = resource;
    }

    /**
     * Instantiates a new fix result.
     *
     * @param status the status
     * @param desc the desc
     */
    public FixResult(Integer status, String desc) {
        if (0!=status) {
            throw new RuntimeException("annotation expecetd in case of success, please use the other constructor");
        }
        this.status = status;
        this.desc = desc;
    }

    /**
     * Instantiates a new fix result.
     *
     * @param status the status
     * @param desc the desc
     * @param annotation the annotation
     */
    public FixResult(Integer status, String desc, Annotation annotation) {
        if (0==status) {
            throw new RuntimeException("annotation not expecetd in case of success");
        }
        this.status = status;
        this.desc = desc;
        this.annotation = annotation;
    }

    /**
     * Gets the annotation.
     *
     * @return the annotation
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * Sets the annotation.
     *
     * @param annotation the new annotation
     */
    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Gets the desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the desc.
     *
     * @param desc the new desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return this.desc;
    }
}
