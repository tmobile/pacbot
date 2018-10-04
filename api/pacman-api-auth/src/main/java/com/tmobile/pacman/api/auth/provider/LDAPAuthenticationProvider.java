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
  Author :U51792
  Modified Date: Jan 1, 2018
  
**//*
package com.tmobile.pacman.api.auth.provider;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.auth.service.LdapAuthenticator;

@Service
public class LDAPAuthenticationProvider {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private LdapAuthenticator ldapAuthentication;
	
	public Map<String, Object> authenticateAndGetUserDetails(String username, String password) {
		try {
			Map<String, Object> userDetails = ldapAuthentication.authenticateAndGetUserDetails(username, password);
			if(Boolean.parseBoolean(userDetails.get("success").toString())) {
				return userDetails;
			} else {
				return unAuthorizeResponse();
			}
		} catch(Exception exception) {
			log.error("Exception occured at authenticateAndGetUserDetails: "+exception.getMessage());
			return unAuthorizeResponse();
		}
	}
	
	private Map<String, Object> unAuthorizeResponse() {
		Map<String, Object> unauthroizeRespone = Maps.newHashMap();
		unauthroizeRespone.put("message", "Authentication Failed!!");
		unauthroizeRespone.put("success", false);
		return unauthroizeRespone;
	}
}*/
