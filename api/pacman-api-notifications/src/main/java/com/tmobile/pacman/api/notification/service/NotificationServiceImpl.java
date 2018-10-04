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
package com.tmobile.pacman.api.notification.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;

@Service
public class NotificationServiceImpl implements NotificationService,Constants {
	
	
	@Autowired
	private ElasticSearchRepository	elasticSearchRepository;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getApiRoles(String serviceProject, String environment) {
		String query = "SELECT roles, urls FROM ApiRbac WHERE serviceProject='"+ serviceProject +"' AND environment='"+ environment +"'";
		return jdbcTemplate.queryForList(query); 
	}
	/**
	 * @throws Exception 
	 * 
	 */
	public Map<String,Object> getDeviceDetails(String deviceId) throws Exception{
		 Map<String, Object> mustFilter= new HashMap<>();
		 mustFilter.put("_id", deviceId);
		
		 List<Map<String,Object>> response =  elasticSearchRepository.getSortedDataFromES("pac_cache", "element", mustFilter, null, null, null,null, null);
		 if(null!=response) return response.get(0);
		 else
			  throw new Exception("no data found");
	}

	@Override
	public List<Map<String,Object>> getAllAssetGroupOwnerEmailDetails() {
		String query = "SELECT displayName AS ownerName, groupName AS assetGroup, ownerEmailId AS ownerEmail FROM cf_AssetGroupDetails LEFT JOIN cf_AssetGroupOwnerDetails ON (groupName = assetGroupName) WHERE groupType = 'Director' AND ownerEmailId NOT IN (SELECT emailId FROM PacmanSubscriptions WHERE subscriptionValue=0) GROUP BY groupName";
		return jdbcTemplate.queryForList(query); 
	}
	
	@Override
	public Map<String, Object> unsubscribeDigestMail(final String mailId) {
		String sql = "SELECT subscriptionValue AS subscriptionValue FROM PacmanSubscriptions WHERE emailId = ?";
		String updateOrInsertQuery ;
		Object[] parameters;
		Date date = new Date();
		Boolean isUnsubscriptedAlready =  jdbcTemplate.query(sql, new Object[]{mailId}, new ResultSetExtractor<Boolean>() {
		    @Override
		    public Boolean extractData(ResultSet rs) {
		        try {
					return rs.next() ? rs.getBoolean("subscriptionValue") : null;
				} catch (SQLException e) {
					Log.error(e.getMessage());
					return null;
				}
		    }
		});
		
		if(isUnsubscriptedAlready != null) {
			updateOrInsertQuery="UPDATE PacmanSubscriptions SET emailId=?, subscriptionValue=?, modifiedDate=? WHERE emailId=?";
			parameters = new Object[] {mailId, false, date, mailId};
		} else {
			updateOrInsertQuery="INSERT INTO PacmanSubscriptions (emailId, subscriptionValue, createdDate, modifiedDate) VALUES (?,?,?,?)";
			
			parameters = new Object[] {mailId, false, date, date};
		}
		int status = jdbcTemplate.update(updateOrInsertQuery, parameters);
		Map<String, Object> response = Maps.newHashMap();
		if(status==1) {
			response.put(STATUS, true);
			response.put(MESSAGE_KEY, "You have successfully unsubscribed");
		} else {
			response.put(STATUS, false);
			response.put(MESSAGE_KEY, "Unsubscribing Failed!!!");
		}
		return response;
	}
	@Override
	public Map<String, Object> subscribeDigestMail(String mailId) {
		String sql = "SELECT subscriptionValue AS subscriptionValue FROM PacmanSubscriptions WHERE emailId = ?";
		String updateOrInsertQuery ;
		Object[] parameters;
		Date date = new Date();
		Boolean isUnsubscriptedAlready =  jdbcTemplate.query(sql, new Object[]{mailId}, new ResultSetExtractor<Boolean>() {
		    @Override
		    public Boolean extractData(ResultSet rs) {
		        try {
					return rs.next() ? rs.getBoolean("subscriptionValue") : null;
				} catch (SQLException e) {
					Log.error(e.getMessage());
					return null;
				}
		    }
		});
		
		if(isUnsubscriptedAlready != null) {
			updateOrInsertQuery="UPDATE PacmanSubscriptions SET emailId=?, subscriptionValue=?, modifiedDate=? WHERE emailId=?";
			parameters = new Object[] {mailId, true, date, mailId};
		} else {
			updateOrInsertQuery="INSERT INTO PacmanSubscriptions (emailId, subscriptionValue, createdDate, modifiedDate) VALUES (?,?,?,?)";
			
			parameters = new Object[] {mailId, true, date, date};
		}
		int status = jdbcTemplate.update(updateOrInsertQuery, parameters);
		Map<String, Object> response = Maps.newHashMap();
		if(status==1) {
			response.put(STATUS, true);
			response.put(MESSAGE_KEY, "You have successfully subscribed");
		} else {
			response.put(STATUS, false);
			response.put(MESSAGE_KEY, "Subscription Failed!!!");
		}
		return response;
	}
}
