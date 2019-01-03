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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.auth.common.Constants;
import com.tmobile.pacman.api.auth.domain.UserClientCredentials;
import com.tmobile.pacman.api.auth.domain.UserLoginCredentials;

/**
 * @author 	NidhishKrishnan
 * @purpose ApiService Service
 * @since	November 10, 2018
 * @version	1.0 
**/
@Service
public class ApiService implements Constants {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
    private DataSource dataSource;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Value("${pacman.api.oauth2.client-id}")
	private String oauth2ClientId;
	
	@Value("${pacman.api.oauth2.client-secret}")
	private String oauth2ClientSecret;

	@Value("${auth.active}")
	private String activeAuth;

	@Bean
	public JdbcTokenStore tokenStore() {
	    return new JdbcTokenStore(dataSource);
	}

	public Map<String, Object> loginProxy(final UserClientCredentials credentials) {
		String requestBodyUrl = StringUtils.EMPTY;
		try {
			requestBodyUrl = "grant_type=password&username=".concat(URLEncoder.encode(credentials.getUsername(), "UTF-8")).concat("&password=".concat(URLEncoder.encode(credentials.getPassword(), "UTF-8")));
			return generateAccessToken(requestBodyUrl, credentials.getClientId());
		} catch (UnsupportedEncodingException exception) {
			log.error("Exception in loginProxy: " + exception.getMessage());
			return response(false, "Unexpected Error Occured!!!");
		}
	}

	public Map<String, Object> refreshToken(final String refreshToken) {
		String requestBodyUrl = StringUtils.EMPTY;
		try {
        	requestBodyUrl = "grant_type=refresh_token&refresh_token=".concat(URLEncoder.encode(refreshToken, "UTF-8"));
			return generateAccessToken(requestBodyUrl, oauth2ClientId);
		} catch (UnsupportedEncodingException exception) {
			log.error("Exception in loginProxy: " + exception.getMessage());
			return response(false, "Unexpected Error Occured!!!");
		}
	}
	
	private Map<String, Object> generateAccessToken(String requestBodyUrl, String clientId) {
		Map<String, Object> accessTokenDetails = Maps.newHashMap();
		try {
			String url = System.getenv(DOMAIN_URL) + "/oauth/token";
			Map<String, String> headers = Maps.newHashMap();
			headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			String clientCredentials = null;
			try {
				String authString = clientId.concat(":").concat(oauth2ClientSecret);
				byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
				clientCredentials = "Basic " + new String(authEncBytes);
			} catch (Exception exception) {
				log.error("Exception in getClientAuthorization: " + exception.getMessage());
				return response(false, "Client Validation Failed!!!");
			}

			headers.put(HttpHeaders.AUTHORIZATION, clientCredentials);
			String accessToken = doHttpPost(url, requestBodyUrl, headers);
			accessTokenDetails = mapper.readValue(accessToken, new TypeReference<HashMap<String, Object>>() {});
			
			if (accessTokenDetails.containsKey("error_description")) {
				return response(false, accessTokenDetails.get("error_description").toString());
			} else {
				accessTokenDetails.put("success", true);
				accessTokenDetails.put("message", "Authentication Successfull");
			}
		} catch (Exception exception) {
			log.error("Exception in loginProxy: " + exception.getMessage());
			return response(false, "Unexpected Error Occured!!!");
		}
		return accessTokenDetails;
	}
	
	/**
	 * 
	 * @param url
	 * @param requestBody
	 * @param headers
	 * @return
	 */
	public String doHttpPost(final String url, final String requestBody, final Map<String, String> headers)
	{
	     try {
			 HttpClient client = HttpClientBuilder.create().build();
		     HttpPost httppost = new HttpPost(url);
			 for (Map.Entry<String, String> entry : headers.entrySet()) {
				 httppost.addHeader(entry.getKey(), entry.getValue());
			 }
		     StringEntity jsonEntity = new StringEntity(requestBody);
		     httppost.setEntity(jsonEntity);
		     HttpResponse httpresponse = client.execute(httppost);
			 return EntityUtils.toString(httpresponse.getEntity());
		} catch (org.apache.http.ParseException parseException) {
			log.error("ParseException : "+parseException.getMessage());
		} catch (IOException ioException) {
			log.error("IOException : "+ioException.getMessage());
		}
		return null;
	}
	
	public String doHttpGet(String url, Map<String, String> headers) {
	  try {
			 HttpClient client = HttpClientBuilder.create().build();
		     HttpGet httpget = new HttpGet(url);
			 for (Map.Entry<String, String> entry : headers.entrySet()) {
				 httpget.addHeader(entry.getKey(), entry.getValue());
			 }
		     HttpResponse httpresponse = client.execute(httpget);
		     if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		    	 return EntityUtils.toString(httpresponse.getEntity());
		     }
		} catch (org.apache.http.ParseException parseException) {
			log.error("ParseException : "+parseException.getMessage());
		} catch (IOException ioException) {
			log.error("IOException : "+ioException.getMessage());
		}
		return null;
	}

	public Map<String, Object> response(final boolean success, final String message) {
		Map<String, Object> response = Maps.newHashMap();
		response.put("success", success);
		response.put("message", message);
		return response;
	}

	public Map<String, Object> login(UserLoginCredentials credentials) {
		if(!activeAuth.equalsIgnoreCase("azuread")) {
			UserClientCredentials userClientCredentials = new UserClientCredentials();
			userClientCredentials.setClientId(oauth2ClientId);
			userClientCredentials.setUsername(credentials.getUsername());
			userClientCredentials.setPassword(credentials.getPassword());
			return loginProxy(userClientCredentials);
		} else {
			return response(false, "This Api is disabled since azuread is the active authentication mode");
		}
	}

	public void logout(Principal principal) {
		 JdbcTokenStore jdbcTokenStore = tokenStore();
		 OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
		 OAuth2AccessToken accessToken = jdbcTokenStore.getAccessToken(oAuth2Authentication);
		 jdbcTokenStore.removeAccessToken(accessToken.getValue());
		 jdbcTokenStore.removeRefreshToken(accessToken.getRefreshToken());
	}
}
