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
  Modified Date: Oct 18, 2017

**/
package com.tmobile.pacman.api.commons.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmobile.pacman.api.commons.Constants;

public class CommonUtils {



	/**
	 *
	 */
	private CommonUtils() {
		// hide the implicit public constructor
	}

	/**
	 *
	 * @param notation
	 * @return nestedMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> flatNestedMap(String notation, Map<String, Object> nestedMap) {
		Map<String, Object> flatNestedMap = new HashMap<String, Object>();
		String prefixKey = notation != null ? notation + "." : "";
		for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
			if (entry.getValue() instanceof String || entry.getValue() instanceof Long || entry.getValue() instanceof Integer  || entry.getValue() instanceof Float || entry.getValue() instanceof Double) {
				flatNestedMap.put(prefixKey + entry.getKey(), entry.getValue());
			}
			if (entry.getValue() instanceof Map) {
				flatNestedMap.putAll(flatNestedMap(prefixKey + entry.getKey(), (Map<String, Object>) entry.getValue()));
			}
			if (entry.getValue() instanceof ArrayList) {
				Gson gson = new Gson();
				flatNestedMap.put("list",gson.toJson(entry.getValue()));

			}
		}
		return flatNestedMap;
	}

	/**
	 *
	 * @param e
	 * @return
	 */
	public static String buildErrorResponse(Exception e){
		return "{\"status\" : \"failed\"}";
	}
	/**
	 *
	 * @param attributeName
	 * @return
	 */
	public static String convertAttributetoKeyword(String attributeName){
		return attributeName + ".keyword";
	}

	/**
	 *
	 * @param notation
	 * @param nestedMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> flatNestedLinkedHashMap(String notation, Map<String, Object> nestedMap) {
		LinkedHashMap<String, Object> flatNestedMap = new LinkedHashMap<String, Object>();
		String prefixKey = notation != null ? notation + "." : "";
		for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
			if (entry.getValue() instanceof String || entry.getValue() instanceof Long || entry.getValue() instanceof Integer  || entry.getValue() instanceof Float || entry.getValue() instanceof Double) {
				flatNestedMap.put(prefixKey + entry.getKey(), entry.getValue());
			}
			if (entry.getValue() instanceof Map) {
				flatNestedMap.putAll(flatNestedMap(prefixKey + entry.getKey(), (Map<String, Object>) entry.getValue()));
			}
			if (entry.getValue() instanceof ArrayList) {
				Gson gson = new Gson();
				flatNestedMap.put("list",gson.toJson(entry.getValue()));

			}
		}
		return flatNestedMap;
	}

	/*
	 * This function can remove/retain elements from collections if it matches
	 * certain criteria. The collections could be deeply nested within the
	 * incoming java object. The removal/retaining is done only if the json
	 * object, that matched the criteria, is a part of a collection. Other json
	 * objects will be left alone if the match is true.
	 *
	 *
	 * The first arg is obvious, it is the java obj itself. This will be
	 * converted to json , processed, and then converted back to a java object
	 * and the incoming object gets replaced with this.
	 *
	 * The second arg is a colon separate key:value. For e.g. if you want to
	 * remove/retain all elements of a particular key provided it matches with a
	 * particular value, the matchTerm would be "issue:open". While it parses
	 * the json tree, if it encounters any key with the value "issue" and if its
	 * value is "open", then this particular element will be removed/retained.
	 *
	 * If you simply give the value alone without the key and the colon, it will
	 * match ALL elements whose value matches this term, irrespective of the key
	 * of the element. Use this keyless approach, only if you are sure of what
	 * you are doing.
	 *
	 * By default, this function will 'remove' matched elements. But if you pass
	 * the third arg as true, this behaviour will flip. We will then 'retain'
	 * only the matched elements and will remove everything else.
	 *
	 *
	 */
	/**
	 * @param obj
	 * @param pattern
	 * @param retainInsteadOfFilter
	 * @return
	 */
	public static Object filterMatchingCollectionElements(Object obj, String pattern, boolean retainInsteadOfFilter) {
		JsonElement jsonElement = new GsonBuilder().create().toJsonTree(obj);

		boolean exact=false;

		if (!Strings.isNullOrEmpty(pattern) && pattern.startsWith("\"")&&pattern.endsWith("\"")) {
			//If the user enters search term enclosed in double quotes,
			//we take that as an 'exact' search rather than a 'like' search
				exact=true;
				//Strip off the quotes now. We will use the search term within.
				pattern = pattern.substring(1,Math.max(1,pattern.length()-1));
		}

		doRecursiveJsonMatch(jsonElement, pattern, retainInsteadOfFilter,exact);

		obj = new GsonBuilder().create().fromJson(jsonElement, Object.class);

		return obj;

	}

	/**
	 *
	 * @param jsonElement
	 * @param pattern
	 * @param retainInsteadOfFilter
	 * @param exact
	 * @return
	 */
	protected static boolean doRecursiveJsonMatch(JsonElement jsonElement, String pattern, boolean retainInsteadOfFilter,boolean exact) {

		String jsonKeyMatchTerm = "";
		String jsonValueMatchTerm = "";

		boolean skipKeyMatch = false;

		if (!Strings.isNullOrEmpty(pattern)) {
			int colonPosition = pattern.indexOf(":");
			int length = pattern.length();

			if (colonPosition == -1) {
				// If colon is not there, then we assume that the incoming
				// string is the value term.
				// The keys will not be matched, any key would qualify as a
				// candidate. Only the values will be matched.
				jsonValueMatchTerm = pattern;
				skipKeyMatch = true;
			} else {
				// key will be to the left of colon in the incoming pattern
				jsonKeyMatchTerm = pattern.substring(0, colonPosition);

				// value will be to the right of colon in the incoming pattern
				jsonValueMatchTerm = pattern.substring(colonPosition + 1, length);
			}
		} else {// We definitely need a pattern. Pattern is the sine qua non of
				// this function.
			return false;
		}

		// Dead end. Can't parse this branch further from here. Return.
		if (jsonElement.isJsonNull()) {
			return false;
		}

		// If array, then pick each element from the array, treat it as a
		// separate json object and make a recursive call
		if (jsonElement.isJsonArray()) {
			JsonArray jsonArray = (JsonArray) jsonElement;
			Iterator<JsonElement> jsonArrayItr = jsonArray.iterator();

			while (jsonArrayItr.hasNext()) {
				JsonElement jsonArrayElement = jsonArrayItr.next();
				boolean remove = doRecursiveJsonMatch(jsonArrayElement, pattern, retainInsteadOfFilter,exact);
				// If the remove flag comes is true, then this means that the
				// current
				// element in the collection needs to be removed
				if (remove) {
					jsonArrayItr.remove();
				}
			}

		}

		// This means that we are not dealing with an array, and we are dealing
		// with a json k-v pair. The key will be a string, but the value need
		// not be a primitive type. The value could be another inner JSON
		// object or even an array.
		if (jsonElement.isJsonObject()) {

			boolean removeThisObjectEntirely = false;

			JsonObject jsonObj = (JsonObject) jsonElement;

			Iterator<String> jsonKeyItr = jsonObj.keySet().iterator();

			while (jsonKeyItr.hasNext()) {
				String key = jsonKeyItr.next();

				Object valueObj = jsonObj.get(key);
				// If the value is not a primitive value, and is another inner
				// JSON object or JSON array, then make a recursive call
				if ((valueObj instanceof JsonObject) || (valueObj instanceof JsonArray)) {
					doRecursiveJsonMatch((JsonElement) valueObj, pattern, retainInsteadOfFilter,exact);
				} else {

					// if the skipKeyMatch is true, they keyMatched will always
					// be true. Why bother about matching keys?
					boolean keyMatched = skipKeyMatch || jsonKeyMatchTerm.equals(key);
					boolean valueMatched = false;

					//Exact match versus Like match
					if(exact) {
						 valueMatched = jsonValueMatchTerm.equalsIgnoreCase(jsonObj.get(key).getAsString());
					}else {
						String lowercasedvalue = jsonObj.get(key).getAsString().toLowerCase();
						String jsonValueMatchTermLowerCased = jsonValueMatchTerm.toLowerCase();
						valueMatched = lowercasedvalue.contains(jsonValueMatchTermLowerCased);
					}

					boolean match = keyMatched && valueMatched;

					// If there is a match, then the decision can be taken
					// immediately if we need to remove/retain. This is the
					// reason why both two out of the three if conditions below
					// with 'match' as true,have a 'break' statement.

					// But if we have a negative match, then we should iterate
					// through all k-v pairs of the json object. Only then can
					// we take the remove/retain decision. This is the reason,
					// the if condition below for negative match does not have a
					// 'break'
					if (retainInsteadOfFilter && !match) {
						removeThisObjectEntirely = true;

					}

					// Tf any of the k-v pairs match, and the option is
					// 'retain', then flip the switch 'removeThisObjectEntirely'
					// back to false
					if (retainInsteadOfFilter && match) {
						removeThisObjectEntirely = false;
						break;
					}

					// If we are in 'filter' mode, and we have a match, remove!
					if (!retainInsteadOfFilter && match) {
						removeThisObjectEntirely = true;
						break;
					}

				}

			}
			// If we have reached here and the flag 'removeThisObjectEntirely'
			// is true, this means that this object needs to be removed for
			// sure. But only if this object is part of a collection. So if the
			// call to this function came from inside a collection iteration,
			// then the caller should ideally watch out for the return of this
			// function,and remove this object from the collection, if the
			// return value is true
			if (removeThisObjectEntirely) {
				return true;
			}
		}
		return false;

	}

	/**
	 *
	 * @return
	 */
	public static SSLContext createNoSSLContext() {
		SSLContext ssl_ctx = null;
		try {
			ssl_ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
		}
        TrustManager[] trust_mgr = new TrustManager[] {
			new X509TrustManager() {
	    		public X509Certificate[] getAcceptedIssuers() { return null; }
	    		public void checkClientTrusted(X509Certificate[] certs, String t) { }
	    		public void checkServerTrusted(X509Certificate[] certs, String t) { }
	    		}
	    	};
        try {
			ssl_ctx.init(null, trust_mgr, new SecureRandom());
		} catch (KeyManagementException e) {
		}
		return ssl_ctx;
	}
	// If previous character is space and current 
    // character is not space then it shows that 
    // current letter is the starting of the word 
	/**
	 * @param str input String
	 * @return string with capital case
	 */
	public static String capitailizeWord(String mainStr) { 
		
		String str = mainStr.replaceAll("_", " ").toLowerCase();
		StringBuffer s = new StringBuffer(); 
  
        char ch = ' '; 
        for (int i = 0; i < str.length(); i++) { 
            if (ch == ' ' && str.charAt(i) != ' ') 
                s.append(Character.toUpperCase(str.charAt(i))); 
            else
                s.append(str.charAt(i)); 
            ch = str.charAt(i); 
        } 
        return s.toString().trim(); 
    } 
	
	/**
	 * returns environment from the env tag based on the regex match -
	 * prod/stg/dev/npe/others 
	 * 
	 * prod - either starts with production, prod or prd or if it
	 * is after ":"
	 * 
	 * stg - starts with stg or stag or after ":"
	 * 
	 * dev - starts with dev or development or after ":"
	 * 
	 * npe - starts with npe or non production or after ":"
	 */
	public static String getEnvironmentForTag(String key) {
		
		if (key.toLowerCase().matches(Constants.PROD_PATTERN)) {
			return Constants.PRODUCTION_ENV;
		} else if (key.toLowerCase().matches(Constants.STG_PATTERN)) {
			return Constants.STAGE_ENV;
		} else if (key.toLowerCase().matches(Constants.DEV_PATTERN)) {
			return Constants.DEV_ENV;
		} else if (key.toLowerCase().matches(Constants.NPE_PATTERN)) {
			return Constants.NPE_ENV;
		} else {
			return Constants.OTHER_ENV;
		}
	}
}
