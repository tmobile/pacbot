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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserPreferencesRepository;
import com.tmobile.pacman.api.admin.repository.model.UserPreferences;

/**
 * UserPreferences Service Implementations
 */
@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

	@Autowired
	private UserPreferencesRepository userPreferencesRepository;

	@Override
	public UserPreferences getUserPreferencesByNtId(String userNtId) throws PacManException {
		return userPreferencesRepository.findByUserIdIgnoreCase(userNtId);
	}
}
