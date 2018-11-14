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

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.tmobile.pacman.api.auth.domain.UserLoginCredentials;
import com.tmobile.pacman.api.auth.repository.AuthRepository;
import com.tmobile.pacman.api.auth.utils.AuthUtils;

/**
 * @author 	NidhishKrishnan
 * @purpose PacmanAuthenticationProvider Service Implementation
 * @since	November 10, 2018
 * @version	1.0 
**/
@Component
public class PacmanAuthenticationProvider implements AuthenticationProvider {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${auth.active}")
	private String activeAuth;

	@Autowired
	private AuthService authService;

	@Autowired
	private AuthRepository authRepository;

	public PacmanAuthenticationProvider() {
    	super();
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    	String userName = ((String) authentication.getPrincipal()).toLowerCase();
    	String password = (String) authentication.getCredentials();
		if (userName != null && authentication.getCredentials() != null) {
	        boolean isAuthenticated = invokeAuthentication(userName, password, false);
	        if(isAuthenticated) {
				Set<String> userRoles = authRepository.getUserRoleDetails(userName.toLowerCase(), AuthUtils.getClientId());
				return new UsernamePasswordAuthenticationToken(userName, StringUtils.EMPTY, AuthUtils.getUserAuthorities(userRoles));
	        } else {
        		throw new BadCredentialsException("Authentication Failed!!!");
        	}
		 } else {
			 throw new BadCredentialsException("Username or Password cannot be empty!!!");
	     }
    }

	public boolean invokeAuthentication(String username, String password, Boolean isClientValidation) {
		try {
			if (activeAuth.equals("azuread")) {
				return true;
			} else {
				UserLoginCredentials credentials = new UserLoginCredentials();
				credentials.setPassword(password);
				credentials.setUsername(username);
				Map<String, Object> userDetails = authService.doLogin(credentials);
				if (Boolean.parseBoolean(userDetails.get("success").toString())) {
					return true;
				}
			}
		} catch (Exception exception) {
			log.error("Exception in invokeAuthentication::: " + exception.getMessage());
		}
		return false;
	}

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
