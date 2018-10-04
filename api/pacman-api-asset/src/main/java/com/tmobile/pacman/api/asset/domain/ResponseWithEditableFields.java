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
package com.tmobile.pacman.api.asset.domain;

import java.util.List;
import java.util.Map;

/**
 * The Class ResponseWithEditableFields.
 */
public class ResponseWithEditableFields {

    List<Map<String, Object>> response;
    
    long total;
    
    List<Map<String, Object>> editableFields;
    
    Map<String, Object> identifier;

    /**
     * Instantiates a new response with editable fields.
     */
    public ResponseWithEditableFields() {
        super();
    }

    /**
     * Instantiates a new response with editable fields.
     *
     * @param response the response
     * @param total the total
     * @param editableFields the editable fields
     * @param identifier the identifier
     */
    public ResponseWithEditableFields(List<Map<String, Object>> response, long total,
            List<Map<String, Object>> editableFields, Map<String, Object> identifier) {
        super();
        this.response = response;
        this.total = total;
        this.editableFields = editableFields;
        this.identifier = identifier;
    }

    /**
     * Gets the identifier.
     *
     * @return the identifier
     */
    public Map<String, Object> getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     *
     * @param identifier the identifier
     */
    public void setIdentifier(Map<String, Object> identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    public List<Map<String, Object>> getResponse() {
        return response;
    }

    /**
     * Sets the response.
     *
     * @param response the response
     */
    public void setResponse(List<Map<String, Object>> response) {
        this.response = response;
    }

    /**
     * Gets the total.
     *
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total the new total
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * Gets the editable fields.
     *
     * @return the editable fields
     */
    public List<Map<String, Object>> getEditableFields() {
        return editableFields;
    }

    /**
     * Sets the editable fields.
     *
     * @param editableFields the editable fields
     */
    public void setEditableFields(List<Map<String, Object>> editableFields) {
        this.editableFields = editableFields;
    }

}
