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

package com.tmobile.pacman.common.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerlessRuleFailedException.
 */
public class ServerlessRuleFailedException extends Exception {

    /** The annotation. */
    String annotation;

    /** The responsecode. */
    Integer responsecode;

    /**
     * Instantiates a new serverless rule failed exception.
     *
     * @param annotation the annotation
     * @param responsecode the responsecode
     */
    public ServerlessRuleFailedException(String annotation, int responsecode) {
        this.annotation = annotation;
        this.responsecode = responsecode;
    }

    /**
     * Gets the annotation.
     *
     * @return the annotation
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Sets the annotation.
     *
     * @param annotation the new annotation
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Gets the responsecode.
     *
     * @return the responsecode
     */
    public Integer getResponsecode() {
        return responsecode;
    }

    /**
     * Sets the responsecode.
     *
     * @param responsecode the new responsecode
     */
    public void setResponsecode(Integer responsecode) {
        this.responsecode = responsecode;
    }

}
