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
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.auth.domain.UserClientCredentials;
import com.tmobile.pacman.api.auth.domain.UserLoginCredentials;
import com.tmobile.pacman.api.auth.model.User;
import com.tmobile.pacman.api.auth.repository.AuthRepository;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SimpleBindRequest;

/**
 * @author 	NidhishKrishnan
 * @purpose LdapAuthServiceImpl Service Implementation
 * @since	November 10, 2018
 * @version	1.0 
**/
@Service
@ConditionalOnProperty(
        name = "auth.active", 
        havingValue = "ldap")
public class LdapAuthServiceImpl implements AuthService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CustomUserService userService;
	
	@Value("${ldap.domain}")
    private String ntDomain;
	
	@Value("${ldap.baseDn}")
	private String baseDn;
	
	@Value("${ldap.port}")
	private int ntPort;
	
	@Value("${ldap.responseTimeout}")
	private long responseTimeout;
	
	@Value("${ldap.connectionTimeout}")
    private int connectTimeout;
    
	// this is if DNS ntDomain is not able to resolve to IP
	@Value("#{'${ldap.hostList}'.split(',')}")
    private List<String> hostIps;
	
	
	@Autowired
	private AuthRepository authRepository;

	@Autowired
    private DataSource dataSource;
	
	@Bean
	public JdbcTokenStore tokenStore() {
	    return new JdbcTokenStore(dataSource);
	}
	
	@Override
	public Map<String, Object> doLogin(final UserLoginCredentials credentials) {
		log.debug("in doGsmLogin");
		Map<String, Object> userDetails = authenticateAndGetUserDetails(credentials.getPassword(), credentials.getUsername());
		log.debug("in doGsmLogin : Post service call");
		return userDetails;
	}

	@Override
	public Map<String, Object> authorizeUser(String idToken) {
		return response(false, "This Api is disabled since azuread is not the active authentication mode");
	}
	
	@Override
	public void logout(Principal principal) {
		 JdbcTokenStore jdbcTokenStore = tokenStore();
		 OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
		 OAuth2AccessToken accessToken = jdbcTokenStore.getAccessToken(oAuth2Authentication);
		 jdbcTokenStore.removeAccessToken(accessToken.getValue());
		 jdbcTokenStore.removeRefreshToken(accessToken.getRefreshToken());
	}

	@Override
	public Map<String, Object> loginProxy(final UserClientCredentials credentials) {
		/*return apiService.loginProxy(credentials);*/
		return null;
	}

	@Override
	public String getUserDefaultAssetGroup(String userId) {
		return authRepository.getUserDefaultAssetGroup(userId);
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public Map<String, Object> authenticateAndGetUserDetails(String username, String password) {
		Map<String, Object> returnUser = Maps.newHashMap();
		LDAPConnection ldapConnection=null;
		try {
				log.debug("trying to get LDAP Connection");
				// reverse the seq for demo only
				try{
						//ldapConnection = getConnection();
						ldapConnection = tryGettingConnectionWithDirectIP();
				}catch(LDAPException ldapException)
				{
					ldapConnection = getConnection();		
					//ldapConnection = tryGettingConnectionWithDirectIP();
				}
				log.debug("got connection");
				
				//String bindDn = username.concat("@").concat(ntDomain);
				BindRequest bindRequest = new SimpleBindRequest(username, password);
				bindRequest.setResponseTimeoutMillis(responseTimeout);
				LDAPResult bindResult=ldapConnection.bind(bindRequest);
				log.debug("bind completed...");
				
			if (bindResult.getResultCode().equals(ResultCode.SUCCESS)) {
				/**
	        	 * If this is the first time user logs in, we can create an account for user in oauth_user table
	        	 * although user records exist in LDAP, we need a user created in our database as well ..for obvious reasons
	        	 */
				User user = userService.findByUserId(username.toLowerCase());
				if (user == null) {
					SearchRequest searchRequest = new SearchRequest(baseDn, SearchScope.SUB, "cn="+username);
					searchRequest.setResponseTimeoutMillis(responseTimeout);
					searchRequest.setSizeLimit(1);
					SearchResult result = ldapConnection.search(searchRequest);
					log.debug("user search completed...");
					if (result.getEntryCount() != 1) {
						closeConnection(ldapConnection);
						returnUser.put("message", "Authentication Failed!!");
						returnUser.put("success", false);
						return returnUser;
					}
					//String userDN = getDN(ldapConnection,username);
					Map<String, Object> userDetails = Maps.newHashMap();
					SearchResultEntry entry = result.getSearchEntries().get(0);
					for (Attribute attribute : entry.getAttributes()) {
						String name = attribute.getName();
						switch (name) {
							case "sn":
								userDetails.put("lastName", attribute.getValue());
								break;
							case "givenName":
								userDetails.put("firstName", attribute.getValue());
								break;
							case "displayName":
								userDetails.put("userName", attribute.getValue());
								break;
							case "mail":
								userDetails.put("email", attribute.getValue());
								break;
							case "sAMAccountName":
								userDetails.put("userId", attribute.getValue().toLowerCase());
								break;
						}
					}
					userService.registerNewUser(userDetails);
				}
				returnUser.put("message", "Authentication Success!!");
				returnUser.put("success", true);
			}
		} catch (LDAPException ldapException) {
			ldapException.printStackTrace();
			log.error("error connecting to gsm",ldapException);
			closeConnection(ldapConnection);
			return unAuthorizeResponse();
		} finally {
			closeConnection(ldapConnection);
		}
		return returnUser;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
	private LDAPConnection getConnection() throws LDAPException{
		LDAPConnection ldapConnection=null;
		
		LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
		connectionOptions.setConnectTimeoutMillis(connectTimeout);
		connectionOptions.setCaptureConnectStackTrace(Boolean.TRUE);
		connectionOptions.setUseSynchronousMode(Boolean.TRUE);
		int numberOfTries=0;
		while(true){
			try{	
					numberOfTries++;
					log.debug(String.format("trial number -> %s" , numberOfTries));
					ldapConnection = new LDAPConnection(connectionOptions);
					ldapConnection.connect(ntDomain, ntPort,connectTimeout);
					break;
			}catch(LDAPException exception) {
				log.debug("connection timeout , retrying " + numberOfTries + " time");
				if(numberOfTries>2){
					log.debug("tried too hard now giving up");
					throw exception;
				}
			}
			
		}
		return ldapConnection;
	}
	
	
	/**
	 * fall back to connect using IP itself
	 * @return
	 */
	private LDAPConnection tryGettingConnectionWithDirectIP() throws LDAPException {
		LDAPConnection ldapConnection = new LDAPConnection();
		try{
		hostIps.forEach(
					ip->{
						try{
								ldapConnection.connect(ip, ntPort,connectTimeout);
								throw new BreakLoopException();
						}catch(LDAPException exception){
							log.debug("unable to connect using" + ip + ", trying next ip");
						}
					}
				);
		}catch(BreakLoopException br){
			log.info("got connected using IP");
		}
		if(ldapConnection.isConnected())
			return ldapConnection;
		else{
			throw new LDAPException(ResultCode.CONNECT_ERROR,"unable to connect using IP");
		}
	}

	/**
	 * 
	 * @param ldapConnection
	 */
	private void closeConnection(LDAPConnection ldapConnection) {
		if (ldapConnection != null) {
			ldapConnection.close();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private Map<String, Object> unAuthorizeResponse() {
		Map<String, Object> unauthroizeRespone = Maps.newHashMap();
		unauthroizeRespone.put("message", "Authentication Failed!!");
		unauthroizeRespone.put("success", false);
		return unauthroizeRespone;
	}
	
	private Map<String, Object> response(final boolean success, final String message) {
		Map<String, Object> response = Maps.newHashMap();
		response.put("success", success);
		response.put("message", message);
		return response;
	}
	
    @PreDestroy
    private void tearDown(){
        
    }
	
	private static class BreakLoopException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
