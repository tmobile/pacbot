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

import com.tmobile.pacman.api.auth.domain.UserClientCredentials;
import com.tmobile.pacman.api.auth.domain.UserLoginCredentials;

/**
 * @author 	NidhishKrishnan
 * @purpose AuthService Service
 * @since	November 10, 2018
 * @version	1.0 
**/
public interface AuthService {
	
    /**
     * authenticate and load user authorities 
     * @param credentials userName and password
     * @return user authorities and auth token
     */
	public Map<String, Object> doLogin(final UserLoginCredentials credentials);
	/**
	 * 
	 * @param credentials
	 * @return
	 */
	public Map<String, Object> authorizeUser(final String idToken);
	/**
	 * 
	 * @param principal
	 */
	public void logout(Principal principal);

	/**
	 * 
	 * @param credentials
	 * @return
	 */
	public Map<String, Object> loginProxy(UserClientCredentials credentials);
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserDefaultAssetGroup(final String userId);
}
