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
package com.tmobile.pacman.api.auth.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

/**
 * @author 	NidhishKrishnan
 * @purpose AuthUtils Utility
 * @since	November 10, 2018
 * @version	1.0 
**/
public class AuthUtils {
	public static String getClientId(){
	    final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	    final String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
	    final String base64AuthorizationHeader = Optional.ofNullable(authorizationHeaderValue).map(headerValue->headerValue.substring("Basic ".length())).orElse(StringUtils.EMPTY);
	    if(StringUtils.isNotEmpty(base64AuthorizationHeader)){
	        String decodedAuthorizationHeader = new String(Base64.getDecoder().decode(base64AuthorizationHeader), StandardCharsets.UTF_8);
	        return decodedAuthorizationHeader.split(":")[0];
	    }
	    return StringUtils.EMPTY;
	}
	
    /**
	 * utility method to convert the user roles to a Collection<GrantedAuthority> for spring security to deal with 
	 * @param roles the list of roles as string
	 * @return a collection of SimpleGrantedAuthority that represent user roles
	 */
	public static Collection<SimpleGrantedAuthority> getUserAuthorities(Set<String> roles) {
		Collection<SimpleGrantedAuthority> authorties = new ArrayList<SimpleGrantedAuthority>();
		for(String role : roles) {
			authorties.add(new SimpleGrantedAuthority(role));
		}
		return authorties;
	}
	
	public static Map<String, Object> convertStringToMap(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<HashMap<String, Object>>() {});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Maps.newHashMap();
	}
	
	public static String passwordEncoder(String password) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		return encodedPassword;
	}
}
