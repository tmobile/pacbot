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
  Author :kkumar
  Modified Date: Jun 27, 2017

**/
package com.tmobile.pacman.commons.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonUtils.
 */
public class CommonUtils {

	 /** The Constant logger. */
 	static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	/**
	 * Checks if is env variable exists.
	 *
	 * @param envVariableName the env variable name
	 * @return the boolean
	 */
	public static Boolean isEnvVariableExists(String envVariableName){
		return !Strings.isNullOrEmpty(System.getenv(envVariableName));
	}

	/**
	 * Gets the env variable value.
	 *
	 * @param envVariableName the env variable name
	 * @return the env variable value
	 */
	public static String getEnvVariableValue(String envVariableName){
		return System.getenv(envVariableName);
	}

	/**
	 * Do http post.
	 *
	 * @param url the url
	 * @param requestBody the request body
	 * @return String
	 * @throws Exception the exception
	 */
	public static String doHttpPost(final String url, final String requestBody) throws Exception {
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httppost = new HttpPost(url);
			//httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
			StringEntity jsonEntity = new StringEntity(requestBody);
			httppost.setEntity(jsonEntity);
			HttpResponse httpresponse = client.execute(httppost);
			int statusCode = httpresponse.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK || statusCode==HttpStatus.SC_CREATED)
			{
				return EntityUtils.toString(httpresponse.getEntity());
			}else{
				logger.error(httpresponse.getStatusLine().getStatusCode() + "---" + httpresponse.getStatusLine().getReasonPhrase());
				throw new Exception("unable to execute post request because " + httpresponse.getStatusLine().getReasonPhrase());
			}
		} catch (ParseException parseException) {
			logger.error("ParseException in getHttpPost :"+parseException.getMessage());
			throw parseException;
		} catch (Exception exception) {
			logger.error("Exception in getHttpPost :"+exception.getMessage());
			throw exception;
		 }
	}

	/**
	 * Do http put.
	 *
	 * @param url the url
	 * @param requestBody the request body
	 * @return String
	 * @throws Exception the exception
	 */
	public static String doHttpPut(final String url, final String requestBody) throws Exception {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(url);
			httpPut.setHeader("Content-Type", "application/json");

			StringEntity jsonEntity =null;
			if(requestBody!=null){
				jsonEntity = new StringEntity(requestBody);
			}

			httpPut.setEntity(jsonEntity);
			HttpResponse httpresponse = client.execute(httpPut);
			if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
			{
				return EntityUtils.toString(httpresponse.getEntity());
			}else{
				throw new Exception("unable to execute put request caused by"+EntityUtils.toString(httpresponse.getEntity()));
			}
		} catch (ParseException parseException) {
			logger.error("ParseException in getHttpPut :"+parseException.getMessage());
		} catch (IOException ioException) {
			logger.error("IOException in getHttpPut :"+ioException.getMessage());
		 }
		return null;
	}

	/**
	 * Checks if is valid resource.
	 *
	 * @param esUrl the es url
	 * @return boolean
	 */
	public static boolean isValidResource(String esUrl) {
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpHead httpHead = new HttpHead(esUrl);
		HttpResponse response;
		try {
			response = httpclient.execute(httpHead);
			return HttpStatus.SC_OK==response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException clientProtocolException) {
			logger.error("ClientProtocolException in getHttpHead:"+ clientProtocolException.getMessage());
		} catch (IOException ioException) {
			logger.error("IOException in getHttpHead:"+ ioException.getMessage());
		}
		return false;
	}

	/**
	 * Builds the query.
	 *
	 * @param filter the filter
	 * @return elastic search query details
	 */
	static Map<String, Object> buildQuery(final Map<String, String> filter) {
		Map<String, Object> queryFilters = Maps.newHashMap();
		Map<String, Object> boolFilters = Maps.newHashMap();
		List<Map<String, Object>> should = Lists.newArrayList();
		for (Map.Entry<String, String> entry : filter.entrySet()) {
			Map<String, Object> term = Maps.newHashMap();
			Map<String, Object> termDetails = Maps.newHashMap();
			termDetails.put(entry.getKey(), entry.getValue());
			term.put("term", termDetails);
			should.add(term);
		}
		boolFilters.put("should", should);
		queryFilters.put("bool", boolFilters);
		return queryFilters;
	}

	/**
	 * Flat nested map.
	 *
	 * @param notation the notation
	 * @param nestedMap the nested map
	 * @return nestedMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> flatNestedMap(String notation, Map<String, Object> nestedMap) {
		Map<String, String> flatNestedMap = new HashMap<String, String>();
		String prefixKey = notation != null ? notation + "." : "";
		for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				flatNestedMap.put(prefixKey + entry.getKey(), (String) entry.getValue());
			}
			if (entry.getValue() instanceof Map) {
				flatNestedMap.putAll(flatNestedMap(prefixKey + entry.getKey(), (Map<String, Object>) entry.getValue()));
			}
		}
		return flatNestedMap;
	}


		/**
		 * Gets the unique annotation id.
		 *
		 * @param annotation the annotation
		 * @return the unique annotation id
		 */
		public static String getUniqueAnnotationId(Annotation annotation){
			String documentId = annotation.get(PacmanSdkConstants.PARENT_ID) +  annotation.get(PacmanSdkConstants.RULE_ID);
			return getUniqueIdForString(documentId);
		}

		 //In order to avoid collision 100%, you need a prime number that
		//is bigger than the wider difference between your characters. So for 7-bit ASCII,
		//you need something higher than 128. So instead of 31, use 131 (the next prime number after 128).
		/**
 		 * This is inspired by java hash function.
 		 *
 		 * @param inStr the in str
 		 * @return the unique id for string
 		 */
		public static String getUniqueIdForString(String inStr) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				//if algorithm does not exist, fall back and try to generate unique hash
				logger.error("unable to generate has usnig Md5",e);
				logger.error("falling back to hash generation");
				return hash(inStr);
			}
	        md.update(inStr.getBytes());
	        byte byteData[] = md.digest();
	      //convert the byte to hex format method 2
	        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	return hexString.toString();
		}

		/**
		 * Hash.
		 *
		 * @param s the s
		 * @return the string
		 */
		public static String hash(String s) {
		    long h = 0;
		    for (int i = 0; i < s.length(); i++) {
		        h = 131 * h + s.charAt(i);
		    }
		    return Long.toString(h);
		}


	/**
	 * Creates the param map.
	 *
	 * @param ruleParams the rule params
	 * @return the map
	 */
	public static Map<String, String> createParamMap(String ruleParams) {
        //return Splitter.on("#").withKeyValueSeparator("=").split(ruleParams);
		return buildMapFromString(ruleParams,"*","=");
	}

	/**
	 * Builds the map from string.
	 *
	 * @param input the input
	 * @param splitOn the split on
	 * @param keyValueSeparator the key value separator
	 * @return the map
	 */
	public static Map<String, String> buildMapFromString(String input,String splitOn,String keyValueSeparator){

		//Map<String,String> modifiableMap = new HashMap<String, String>();
//		modifiableMap.putAll(Splitter.on(splitOn)
		return Splitter.on(splitOn)
        .omitEmptyStrings()
        .trimResults()
        .withKeyValueSeparator(keyValueSeparator)
        .split(input);
		//return modifiableMap;
	}


	/**
	 * Gets the elapse time since.
	 *
	 * @param startTime the start time
	 * @return the elapse time since
	 */
	public static String getElapseTimeSince(long startTime) {
		return Long.toString(TimeUnit.MINUTES.convert(System.nanoTime()-startTime, TimeUnit.NANOSECONDS));
	}


	/**
	 * Gets the json string.
	 *
	 * @param annotation the annotation
	 * @return the json string
	 */
	public static String getJsonString(final Object annotation) {
		try {
			return new ObjectMapper().writeValueAsString(annotation);
		} catch (JsonProcessingException jsonProcessingException) {
			logger.error("JsonProcessingException : "+ jsonProcessingException.getMessage());
		}
		return null;
	}
	
	/**
	 * Do http post.
	 *
	 * @param url the url
	 * @param requestBody the request body
	 * @return String
	 * @throws Exception the exception
	 */
	public static String doHttpPost(final String url, final Map<String,String> requestBody) throws Exception {
		try {
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httppost = new HttpPost(url);
			
			List<NameValuePair> form = new ArrayList<>();
			requestBody.forEach((k,v)-> {
				form.add(new BasicNameValuePair(k,v));
			});
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
			httppost.setEntity(entity);
			
			HttpResponse httpresponse = client.execute(httppost);
			int statusCode = httpresponse.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK || statusCode==HttpStatus.SC_CREATED)
			{
				return EntityUtils.toString(httpresponse.getEntity());
			}else{
				logger.error(httpresponse.getStatusLine().getStatusCode() + "---" + httpresponse.getStatusLine().getReasonPhrase());
				throw new Exception("unable to execute post request because " + httpresponse.getStatusLine().getReasonPhrase());
			}
		} catch (ParseException parseException) {
			logger.error("ParseException in getHttpPost :"+parseException.getMessage());
			throw parseException;
		} catch (Exception exception) {
			logger.error("Exception in getHttpPost :"+exception.getMessage());
			throw exception;
		 }
	}
	
	public static String doHttpGet(String uri ,String tokeType, String token) throws Exception  {
		 
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("content-type", "application/json");
        httpGet.addHeader("cache-control", "no-cache");
        if(!Strings.isNullOrEmpty(token)){
            httpGet.addHeader("Authorization", tokeType+" "+token);
        }
    	HttpClient httpClient = HttpClientBuilder.create().build();
        if(httpClient!=null){
            HttpResponse httpResponse;
            try {
               
                httpResponse = httpClient.execute(httpGet);
                if( httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
                	return EntityUtils.toString(httpResponse.getEntity());
                }else {
                	throw new Exception("unable to execute put request caused by"+EntityUtils.toString(httpResponse.getEntity()));
                }
            } catch (Exception e) {
            	logger.error("Error getting the data " , e);
                throw e;
            }
        }
        return "{}";
    }
	
	public static String doHttpPost(String uri, String token, String accessToken) throws Exception {

		HttpPost httpPost = new HttpPost(uri);
		httpPost.addHeader("content-type", "application/json");
		httpPost.addHeader("cache-control", "no-cache");
		if (!Strings.isNullOrEmpty(token)) {
			httpPost.addHeader("Authorization", token + " " + accessToken);
		}
		HttpClient httpClient = HttpClientBuilder.create().build();
		if (httpClient != null) {
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return EntityUtils.toString(httpResponse.getEntity());
				} else {
					throw new Exception("unable to execute post request caused by"
							+ EntityUtils.toString(httpResponse.getEntity()));
				}
			} catch (Exception e) {
				logger.error("Error getting the data ", e);
				throw e;
			}
		}
		return "{}";
	}
}
