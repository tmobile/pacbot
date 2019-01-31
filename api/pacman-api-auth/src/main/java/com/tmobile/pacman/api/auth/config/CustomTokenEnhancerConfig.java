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
package com.tmobile.pacman.api.auth.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.auth.model.User;
import com.tmobile.pacman.api.auth.repository.AuthRepository;
import com.tmobile.pacman.api.auth.services.CustomUserService;

/**
 * @author 	NidhishKrishnan
 * @purpose Custom implementation of TokenEnhancer
 * @since	November 10, 2018
 * @version	1.0 
**/
public class CustomTokenEnhancerConfig implements TokenEnhancer {
	
	@Autowired
	private CustomUserService userService;
	
	@Autowired
	private AuthRepository authRepository;
	
	@Value("${auth.active}")
	private String activeAuth;
	
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		String clientId = authentication.getOAuth2Request().getClientId();
		User user = userService.findByUserId(authentication.getName().toLowerCase());
		if(user!=null) {
			Map<String, Object> userInfo = Maps.newHashMap();
		    userInfo.put("userInfo", buildUserDetails(user, clientId));
		    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(userInfo);
		}
        return accessToken;
    }

	private Map<String, Object> buildUserDetails(final User user, final String clientId) {
		Map<String, Object> userDetails = Maps.newHashMap();
		userDetails.put("userId", user.getUserId());
		userDetails.put("userName", user.getUserName());
		userDetails.put("firstName", user.getFirstName());
		userDetails.put("lastName", user.getLastName());
		userDetails.put("email", user.getEmail());
		if(activeAuth.equals("azuread") || activeAuth.equals("ldap") || activeAuth.equals("db")) {
			userDetails.put("userRoles", userService.getUserRoles(user.getUserId(), clientId));
			userDetails.put("defaultAssetGroup", authRepository.getUserDefaultAssetGroup(user.getUserId()));
		}
		return userDetails;
	}
}
