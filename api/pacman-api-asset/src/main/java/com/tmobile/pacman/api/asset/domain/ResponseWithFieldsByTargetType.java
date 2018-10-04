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
  Author :santoshi
  Modified Date: Feb 27, 2018

 **/
package com.tmobile.pacman.api.asset.domain;

/**
 Copyright (C) 2017 T Mobile Inc - All Rights Reserve
 Purpose:
 Author :santoshi
 Modified Date: Feb 27, 2018

 **/
import java.util.List;

/**
 * The Class ResponseWithFieldsByTargetType.
 */
public class ResponseWithFieldsByTargetType {

    String targetType;

    List<String> editableFields;

    /**
     * Instantiates a new response with fields by target type.
     */
    public ResponseWithFieldsByTargetType() {
        super();
    }

    /**
     * Instantiates a new response with fields by target type.
     *
     * @param targetType the target type
     * @param editableFields the editable fields
     */
    public ResponseWithFieldsByTargetType(String targetType, List<String> editableFields) {
        super();
        this.targetType = targetType;
        this.editableFields = editableFields;
    }

    /**
     * Gets the target type.
     *
     * @return the target type
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * Sets the target type.
     *
     * @param targetType the new target type
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * Gets the editable fields.
     *
     * @return the editable fields
     */
    public List<String> getEditableFields() {
        return editableFields;
    }

    /**
     * Sets the editable fields.
     *
     * @param editableFields the new editable fields
     */
    public void setEditableFields(List<String> editableFields) {
        this.editableFields = editableFields;
    }
}
