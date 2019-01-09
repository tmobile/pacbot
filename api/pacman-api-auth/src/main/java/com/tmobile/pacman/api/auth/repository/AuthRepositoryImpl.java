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
package com.tmobile.pacman.api.auth.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Sets;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;

/**
 * @author 	NidhishKrishnan
 * @purpose AuthRepositoryImpl Repository Implementation
 * @since	November 10, 2018
 * @version	1.0 
**/
@Repository
public class AuthRepositoryImpl implements AuthRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private PacmanRdsRepository rdsRepository;
	
	@Override
	public Set<String> getUserRoleDetails(final String userId, final String clientId) {
		String query = "SELECT role.roleId AS roleId, role.roleName AS roleName FROM oauth_user_roles role INNER JOIN oauth_user_role_mapping userRole ON role.roleId = userRole.roleId WHERE userId =? AND clientId =?";
		Object parameters[] = new Object[] {userId.toLowerCase(), clientId};
		List<Map<String,Object>> userRoles = jdbcTemplate.queryForList(query, parameters); 
		Set<String> allUserRoles = Sets.newHashSet();
		for(Map<String,Object> userRole: userRoles) {
			allUserRoles.add(userRole.get("roleName").toString());
		}
		allUserRoles.add("ROLE_USER");
		return allUserRoles;
	}
	
	@Override
	public String getUserDefaultAssetGroup(String userId) {
		String query = "SELECT defaultAssetGroup from pac_v2_userpreferences WHERE userId=\""+userId.toLowerCase()+"\"";
		try {
			String defaultAssetGroup = rdsRepository.queryForString(query);
			if(!StringUtils.isBlank(defaultAssetGroup)) {
				return defaultAssetGroup;
			} else {
				return StringUtils.EMPTY;
			}
		} catch (Exception exception) {
			return StringUtils.EMPTY;
		}
	}
}

