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

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.auth.domain.UserClientCredentials;
import com.tmobile.pacman.api.auth.domain.UserLoginCredentials;
import com.tmobile.pacman.api.auth.model.User;
import com.tmobile.pacman.api.auth.model.UserCredentials;
import com.tmobile.pacman.api.auth.repository.UserCredentialsRepository;
import com.tmobile.pacman.api.auth.repository.UserRepository;

/**
 * @author 	NidhishKrishnan
 * @purpose DataBaseAuthServiceImpl Service Implementation
 * @since	November 10, 2018
 * @version	1.0 
**/
@Service
@ConditionalOnProperty(
        name = "auth.active",
        havingValue = "db")
public class DataBaseAuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserCredentialsRepository userCredentialsRepository;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.auth.service.AuthService#doLogin(com.tmobile.pacman.api.auth.domain.UserCredentials)
     */
    @Override
    public Map<String, Object> doLogin(UserLoginCredentials credentials) {
    	Map<String, Object> userDetails = Maps.newHashMap();
    	User user = userRepository.findByUserId(credentials.getUsername());
		Optional<UserCredentials> userCredentials = userCredentialsRepository.findById(user.getId());
		if(userCredentials.isPresent()) {
			if(encoder.matches(credentials.getPassword(), userCredentials.get().getPassword())) {
	    		userDetails.put("success", true);
	    		userDetails.put("message", "Authentication Success!!!");
			} else {
				userDetails.put("success", false);
				userDetails.put("message", "Authentication Failed!!!");
			}
		} else {
			userDetails.put("success", false);
			userDetails.put("message", "Authentication Failed!!!");
		}
        return userDetails;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.auth.service.AuthService#authorizeUser(com.tmobile.pacman.api.auth.domain.UserClientCredentials)
     */
    @Override
    public Map<String, Object> authorizeUser(String idToken) {
    	return response(false, "This Api is disabled since azuread is not the active authentication mode");
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.auth.service.AuthService#logout(java.security.Principal)
     */
    @Override
    public void logout(Principal principal) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.auth.service.AuthService#loginProxy(com.tmobile.pacman.api.auth.domain.UserClientCredentials)
     */
    @Override
    public Map<String, Object> loginProxy(UserClientCredentials credentials) {
    	//return apiService.loginProxy(credentials);
    	return null;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.auth.service.AuthService#getUserDefaultAssetGroup(java.lang.String)
     */
    @Override
    public String getUserDefaultAssetGroup(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    private Map<String, Object> response(final boolean success, final String message) {
		Map<String, Object> response = Maps.newHashMap();
		response.put("success", success);
		response.put("message", message);
		return response;
	}
}
