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
package com.tmobile.pacman.api.admin.repository.service;

import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.UserPreferences;

/**
 * User Preferences Service Functionalities
 */
public interface UserPreferencesService {

	/**
     * Service to get user preferences by NT Id
     *
     * @author Nidhish
     * @param ntId - valid user NT Id
     * @return UserPreferences details
     * @throws PacManExceptio
     */
	public UserPreferences getUserPreferencesByNtId(final String ntId) throws PacManException;
}
