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
package com.tmobile.pacman.api.auth.services;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.auth.model.User;
import com.tmobile.pacman.api.auth.repository.AuthRepository;
import com.tmobile.pacman.api.auth.repository.UserRepository;


/**
 * @author 	NidhishKrishnan
 * @purpose CustomUserService Service Implementation
 * @since	November 10, 2018
 * @version	1.0 
**/
@Service
public class CustomUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthRepository authRepository;

	public User save(final User user) {
	    return userRepository.save(user);
	}

	public User findByUserId(final String userId) {
	    return userRepository.findByUserId(userId);
	}

	public void deleteAll() {
	    userRepository.deleteAll();
	}

	public void registerNewUsers(final String userId) {
		User newUser = new User();
		Date now = new Date();
		newUser.setCreatedDate(now);
		newUser.setModifiedDate(now);
		newUser.setUserId(userId);
		userRepository.save(newUser);
	}

	public void registerNewUser(final Map<String, Object> userDetails) {
		User newUser = new User();
		Date now = new Date();
		newUser.setCreatedDate(now);
		newUser.setModifiedDate(now);
		newUser.setUserId(userDetails.get("userId").toString().toLowerCase());
		newUser.setUserName(userDetails.get("userName").toString());
		newUser.setFirstName(userDetails.get("firstName").toString());
		newUser.setLastName(userDetails.get("lastName").toString());
		newUser.setEmail(userDetails.get("email").toString());
		userRepository.save(newUser);
	}

	public Set<String> getUserRoles(String userId, String clientId) {
		return authRepository.getUserRoleDetails(userId, clientId);
	}
}
