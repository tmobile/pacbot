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

package com.tmobile.pacman.service;

import java.util.List;
import java.util.Map;

import com.tmobile.pacman.dto.IssueException;

// TODO: Auto-generated Javadoc
/**
 * The Interface ExceptionManager.
 *
 * @author kkumar
 */
public interface ExceptionManager {

    /**
     * Gets the sticky exceptions.
     *
     * @param ruleId the rule id
     * @param resourceType the resource type
     * @return the sticky exceptions
     * @throws Exception the exception
     */
    Map<String, List<IssueException>> getStickyExceptions(String ruleId, String resourceType) throws Exception;

    /**
     * Gets the individual exceptions.
     *
     * @param resourceType the resource type
     * @return the individual exceptions
     * @throws Exception the exception
     */
    Map<String, IssueException> getIndividualExceptions(String resourceType) throws Exception;

}
