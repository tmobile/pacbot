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

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.UserDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.UserRepository;
import com.tmobile.pacman.api.admin.repository.UserRolesMappingRepository;
import com.tmobile.pacman.api.admin.repository.model.User;
import com.tmobile.pacman.api.admin.repository.model.UserPreferences;

/**
 * User Service Implementations
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserPreferencesService userPreferencesService;
	
	@Autowired
	private UserRolesMappingRepository userRolesMappingRepository;

	@Override
	public List<User> getAllLoginUsers() throws PacManException {
		return userRepository.findAll();
	}
	
	@Override
	public UserDetails getUserByEmailId(final String emailId) throws PacManException {
		User user;
		if (emailId != null) {
			user = userRepository.findByEmailIgnoreCase(emailId);
			return fetchUserDetails(user, emailId);
		} else {
			throw new PacManException("Email Id cannot be empty");
		}
	}

	private UserDetails fetchUserDetails(User user, String emailId) throws PacManException {
		UserDetails userDetails = new UserDetails();
		try {
			if (user == null) {
				List<String> roles = Lists.newArrayList();
				roles.add("ROLE_USER");
				userDetails.setUserRoles(roles);
				userDetails.setUserName(StringUtils.EMPTY);
				userDetails.setUserId(StringUtils.EMPTY);
				if (emailId != null) {
					userDetails.setEmail(emailId);
				} else {
					userDetails.setEmail(StringUtils.EMPTY);
				}
				userDetails.setLastName(StringUtils.EMPTY);
				userDetails.setFirstName(StringUtils.EMPTY);
				userDetails.setDefaultAssetGroup("");
			} else {
				List<String[]> userRoles = userRolesMappingRepository.findAllUserRoleDetailsByUserIdIgnoreCase(user.getUserId());
				UserPreferences userPreferences = userPreferencesService.getUserPreferencesByNtId(user.getUserId());
				userDetails.setUserRoles(userRoles);
				userDetails.setUserName(user.getUserName());
				userDetails.setUserId(user.getUserId());
				userDetails.setLastName(user.getLastName());
				userDetails.setFirstName(user.getFirstName());
				userDetails.setEmail(user.getEmail());
				userDetails.setDefaultAssetGroup(userPreferences.getDefaultAssetGroup());
			}
		} catch (Exception eception) {
			throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
		}
		return userDetails;
	}
}
