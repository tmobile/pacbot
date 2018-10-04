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
package com.tmobile.pacman.api.auth.repo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Sets;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;

@Repository
public class AuthRepositoryImpl implements AuthRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private PacmanRdsRepository rdsRepository;
	
	@Override
	public String getClientAuthorization(String clientId) {
		String query = "SELECT client_secret FROM oauth_client_details WHERE client_id= '"+ clientId +"'";
		String clientSecret = jdbcTemplate.queryForObject(query, String.class); 
		String authString = clientId + ":" + clientSecret;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		return new String(authEncBytes);		
	}
	
	@Override
	public Set<String> getUserRoleDetailsOld(String userId) {
		String query = "SELECT role.roleId AS roleId, role.roleName AS roleName FROM oauth_user_roles role INNER JOIN oauth_user_role_mapping userRole ON role.roleId = userRole.roleId WHERE userId =?";
		Object parameters[] = new Object[] {userId.toLowerCase()};
		List<Map<String,Object>> userRoles = jdbcTemplate.queryForList(query, parameters); 
		Set<String> allUserRoles = Sets.newHashSet();
		for(Map<String,Object> userRole: userRoles) {
			allUserRoles.add(userRole.get("roleName").toString());
		}
		allUserRoles.add("ROLE_USER");
		return allUserRoles;
	}
	
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
		String query = "SELECT recentlyViewedAG from pac_v2_userpreferences WHERE userId=\""+userId.toLowerCase()+"\"";
		try {
			String recentlyViewedAG = rdsRepository.queryForString(query);
			if(!StringUtils.isBlank(recentlyViewedAG)) {
				String allRecentlyViewedAG [] = recentlyViewedAG.split(",");
				return allRecentlyViewedAG.length>0 ? allRecentlyViewedAG[allRecentlyViewedAG.length-1] : StringUtils.EMPTY;
			} else {
				return StringUtils.EMPTY;
			}
		} catch (Exception exception) {
			return StringUtils.EMPTY;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<String> getRenetlyViewedAG(final String userId) {
		List<String> recentViewList = new ArrayList<String>() ;
		try {
			String recentlyViewedAgQuery = "SELECT recentlyViewedAG FROM pac_v2_userpreferences WHERE userId=\"" + userId + "\"";
			List<Map<String, Object>> recentlyViewedAgMap = rdsRepository.getDataFromPacman(recentlyViewedAgQuery);
			for (Map<String, Object> recentlyViewedAg : recentlyViewedAgMap) {
				if (recentlyViewedAg.get("recentlyViewedAG") != null) {
					String recentView = recentlyViewedAg.get("recentlyViewedAG").toString();
					recentViewList = new CopyOnWriteArrayList(Arrays.asList(recentView.split(",")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recentViewList;
	}
}
