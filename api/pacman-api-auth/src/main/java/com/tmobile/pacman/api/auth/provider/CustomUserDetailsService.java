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
  Author :Nidhish
  Modified Date: Nov 27, 2017
**/
package com.tmobile.pacman.api.auth.provider;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.auth.repo.AuthRepository;
import com.tmobile.pacman.api.auth.util.AuthUtils;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private CustomUserService userService;

  @Autowired
  private AuthRepository authRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username == null || username.isEmpty()) {
      throw new UsernameNotFoundException("Username is empty");
    }
    com.tmobile.pacman.api.auth.repo.model.User authUser = userService.findByUserId(username);
    if (authUser != null) {
    	Set<String> userRoles = authRepository.getUserRoleDetails(username.toLowerCase(), AuthUtils.getClientId());
        return new User(authUser.getUserId(), StringUtils.EMPTY, AuthUtils.getUserAuthorities(userRoles));
    }
    throw new UsernameNotFoundException("Unauthorized client_id or username not found: " + username);
  }
}
